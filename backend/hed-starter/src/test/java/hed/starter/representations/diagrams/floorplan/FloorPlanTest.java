/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.starter.representations.diagrams.floorplan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.representations.Message;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.web.application.images.services.api.IProjectImageApplicationService;
import org.eclipse.sirius.web.domain.boundedcontexts.projectimage.ProjectImage;
import org.eclipse.sirius.web.domain.boundedcontexts.projectsemanticdata.ProjectSemanticData;
import org.eclipse.sirius.web.domain.boundedcontexts.projectsemanticdata.services.api.IProjectSemanticDataSearchService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import hed.domain.HedSamples;
import hed.homeelectrical.Storey;

/** Verifies storey eligibility and atomic, project-owned image assignments without changing equipment ownership. */
class FloorPlanTest {
    @Test
    void onlyExistingEquipmentInRoomsOnTheSelectedStoreyIsEligible() {
        var home = HedSamples.example();
        var ground = home.getStoreys().get(0);
        var first = home.getStoreys().get(1);
        var service = new FloorPlanServices(new IObjectSearchService.NoOp(), new IFeedbackMessageService() {
            public void addFeedbackMessage(Message message) {
            }

            public List<Message> getFeedbackMessages() {
                return List.of();
            }
        }, Locale.ENGLISH);
        assertEquals(6, service.getHedFloorPlanEquipment(ground).size());
        assertEquals(1, service.getHedFloorPlanEquipment(first).size());
        var equipment = home.getEquipment().get(0);
        equipment.setRoom(null);
        assertFalse(service.getHedFloorPlanEquipment(ground).contains(equipment));
        equipment.setRoom(first.getRooms().get(0));
        assertFalse(service.getHedFloorPlanEquipment(ground).contains(equipment));
        assertTrue(service.getHedFloorPlanEquipment(first).contains(equipment));
        assertSame(home, equipment.eContainer());
    }

    @Test
    void existingStoreysHaveAUsableEmptyPlan() {
        var storey = HedSamples.example().getStoreys().get(0);
        assertNull(storey.getPlanImageId());
        assertEquals(1200, storey.getPlanWidth());
        assertEquals(800, storey.getPlanHeight());
    }

    @Test
    void storeyIdentifiersRemainOpaque() {
        assertEquals("external:storey", FloorPlanBackgroundAction.parse(FloorPlanBackgroundAction.PREFIX + "external%3Astorey::1.5").storeyId());
    }

    @Test
    void nativeActionSetsAndClearsAnOwnedBackgroundWithoutChangingEquipment() {
        var home = HedSamples.example();
        var storey = home.getStoreys().get(0);
        int count = home.getEquipment().size();
        var fixture = new ActionFixture(storey, "project-a", "project-a");
        assertInstanceOf(Success.class, fixture.action.handle(fixture.context, fixture.command(fixture.image.getId().toString(), 4.0 / 3.0)));
        assertEquals(fixture.image.getId().toString(), storey.getPlanImageId());
        assertEquals(900, storey.getPlanHeight());
        assertEquals(1200, storey.getPlanWidth());
        assertEquals(count, home.getEquipment().size());
        assertInstanceOf(Success.class, fixture.action.handle(fixture.context, fixture.command("", 1)));
        assertNull(storey.getPlanImageId());
        assertEquals(900, storey.getPlanHeight());
        assertEquals(count, home.getEquipment().size());
    }

    @Test
    void foreignImagesAndMalformedCommandsLeaveThePreviousBackgroundUntouched() {
        var storey = HedSamples.example().getStoreys().get(0);
        var old = UUID.randomUUID().toString();
        storey.setPlanImageId(old);
        storey.setPlanHeight(720);
        var fixture = new ActionFixture(storey, "project-a", "project-b");
        for (var command : List.of(fixture.command(fixture.image.getId().toString(), 4.0 / 3.0), fixture.command("broken", 1.5), fixture.command("", 0), fixture.command("", 10.1),
                fixture.command("", Double.NaN), fixture.command("", Double.POSITIVE_INFINITY))) {
            assertInstanceOf(Failure.class, fixture.action.handle(fixture.context, command));
            assertEquals(old, storey.getPlanImageId());
            assertEquals(720, storey.getPlanHeight());
        }
    }

    @Test
    void imageImportRetainsTheChosenWidthAndClearingRetainsTheDimensions() {
        var storey = HedSamples.example().getStoreys().getFirst();
        storey.setPlanWidth(2400);
        var fixture = new ActionFixture(storey, "project-a", "project-a");
        assertInstanceOf(Success.class, fixture.action.handle(fixture.context, fixture.command(fixture.image.getId().toString(), 1.5)));
        assertEquals(2400, storey.getPlanWidth());
        assertEquals(1600, storey.getPlanHeight());
        assertEquals(1.5, storey.getPlanAspectRatio());
        assertInstanceOf(Success.class, fixture.action.handle(fixture.context, fixture.command("", 1)));
        assertNull(storey.getPlanImageId());
        assertEquals(2400, storey.getPlanWidth());
        assertEquals(1600, storey.getPlanHeight());
        assertEquals(1.5, storey.getPlanAspectRatio());
    }

    @SuppressWarnings("unchecked")
    private static <T> T stub(Class<T> type, java.util.function.Function<String, Object> response) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> response.apply(method.getName()));
    }

    /** Supplies a single storey and project-image ownership boundary to the native action. */
    private static final class ActionFixture {
        final String storeyId = UUID.randomUUID().toString();

        final String contextId = UUID.randomUUID().toString();

        final IEditingContext context = stub(IEditingContext.class, name -> contextId);

        final ProjectImage image;

        final FloorPlanBackgroundAction action;

        ActionFixture(Storey storey, String project, String imageProject) {
            IInput cause = UUID::randomUUID;
            image = ProjectImage.newProjectImage().project(AggregateReference.to(imageProject)).label("Floor plan").contentType("image/svg+xml").content("<svg/>".getBytes()).build(cause);
            var link = ProjectSemanticData.newProjectSemanticData().project(AggregateReference.to(project)).semanticData(AggregateReference.to(UUID.fromString(contextId))).name("Model").build(cause);
            var objects = new IObjectSearchService() {
                @Override
                public Optional<Object> getObject(IEditingContext ctx, String id) {
                    return id.equals(storeyId) ? Optional.of(storey) : Optional.empty();
                }
            };
            action = new FloorPlanBackgroundAction(objects, stub(IProjectImageApplicationService.class, name -> Optional.of(image)),
                    stub(IProjectSemanticDataSearchService.class, name -> Optional.of(link)), Locale.ENGLISH);
        }

        String command(String imageId, double aspectRatio) {
            return FloorPlanBackgroundAction.PREFIX + storeyId + ":" + imageId + ":" + aspectRatio;
        }
    }
}
