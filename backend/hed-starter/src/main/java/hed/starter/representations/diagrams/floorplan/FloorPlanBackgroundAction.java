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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.eclipse.sirius.components.collaborative.api.IEditingContextActionHandler;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.Failure;
import org.eclipse.sirius.components.representations.Success;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.web.application.images.services.api.IProjectImageApplicationService;
import org.eclipse.sirius.web.domain.boundedcontexts.projectsemanticdata.services.api.IProjectSemanticDataSearchService;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import hed.domain.Messages;
import hed.domain.HedFloorPlans;
import hed.homeelectrical.Storey;

/** Runs in the native editing-context event queue, so image changes persist and refresh every view. */
@Service
public class FloorPlanBackgroundAction implements IEditingContextActionHandler {
    public static final String PREFIX = "hed-plan-background:";

    private final IObjectSearchService objects;

    private final IProjectImageApplicationService images;

    private final IProjectSemanticDataSearchService links;

    private final Locale locale;

    /**
     * Creates the image-assignment handler executed by the native editing-context queue.
     * @param objects resolves the target storey within the editing context
     * @param images resolves uploaded project images and their media types
     * @param links finds the project owning the editing context's semantic data
     * @param locale selects failure messages
     */
    public FloorPlanBackgroundAction(IObjectSearchService objects, IProjectImageApplicationService images, IProjectSemanticDataSearchService links, Locale locale) {
        this.objects = objects;
        this.images = images;
        this.links = links;
        this.locale = locale;
    }

    @Override
    public boolean canHandle(IEditingContext context, String actionId) {
        return actionId.startsWith(PREFIX);
    }

    @Override
    public IStatus handle(IEditingContext context, String actionId) {
        try {
            var command = parse(actionId);
            var storey = objects.getObject(context, command.storeyId()).filter(Storey.class::isInstance).map(Storey.class::cast).orElseThrow(IllegalArgumentException::new);
            if (!command.imageId().isEmpty()) {
                var image = images.findById(UUID.fromString(command.imageId())).orElseThrow(IllegalArgumentException::new);
                var project = links.findBySemanticDataId(AggregateReference.to(UUID.fromString(context.getId()))).orElseThrow(IllegalArgumentException::new);
                if (!image.getProject().getId().equals(project.getProject().getId()) || !Set.of("image/png", "image/jpeg", "image/svg+xml").contains(image.getContentType())) {
                    throw new IllegalArgumentException();
                }
            }
            // All validation precedes mutation, so rejected imports retain the previous background.
            if (command.imageId().isEmpty()) {
                storey.setPlanImageId(null);
            } else {
                HedFloorPlans.assignImage(storey, command.imageId(), command.aspectRatio());
            }
            return new Success(ChangeKind.SEMANTIC_CHANGE, Map.of());
        } catch (IllegalArgumentException ex) {
            return new Failure(Messages.text(locale, "plan.invalidBackground"));
        }
    }

    /**
     * A validated background assignment carrying image proportions independently of the user's canvas width.
     * @param storeyId the opaque semantic identifier of the target storey
     * @param imageId the uploaded project image UUID, or an empty string to clear the background
     * @param aspectRatio the intrinsic image width divided by height, from 0.2 through 10
     */
    public record BackgroundCommand(String storeyId, String imageId, double aspectRatio) {
    }

    /**
     * Parses and validates the action payload before any semantic mutation.
     * @param actionId the action prefix, encoded storey identifier, image identifier and aspect ratio
     * @return the validated command
     * @throws IllegalArgumentException for an unknown prefix, invalid identifier or out-of-range aspect ratio
     */
    public static BackgroundCommand parse(String actionId) {
        if (!actionId.startsWith(PREFIX)) {
            throw new IllegalArgumentException();
        }
        var parts = actionId.substring(PREFIX.length()).split(":", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException();
        }
        var storeyId = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
        if (storeyId.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (!parts[1].isEmpty()) {
            UUID.fromString(parts[1]);
        }
        double ratio = Double.parseDouble(parts[2]);
        if (!Double.isFinite(ratio) || ratio < 0.2 || ratio > 10) {
            throw new IllegalArgumentException();
        }
        return new BackgroundCommand(storeyId, parts[1], ratio);
    }
}
