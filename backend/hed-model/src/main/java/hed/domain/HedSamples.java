/*******************************************************************************
 * Copyright (c) 2026 Laurent Redor.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package hed.domain;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import hed.homeelectrical.CircuitPhase;
import hed.homeelectrical.CircuitUse;
import hed.homeelectrical.ControlDevice;
import hed.homeelectrical.ControlMode;
import hed.homeelectrical.DINRail;
import hed.homeelectrical.DeviceKind;
import hed.homeelectrical.DeviceType;
import hed.homeelectrical.DistributionBoard;
import hed.homeelectrical.Dwelling;
import hed.homeelectrical.ElectricalLoad;
import hed.homeelectrical.FinalCircuit;
import hed.homeelectrical.ModularDevice;
import hed.homeelectrical.Outlet;
import hed.homeelectrical.SupplyPhase;
import static hed.domain.HedDomain.F;
import static hed.domain.HedDomain.addEquipment;
import static hed.domain.HedDomain.addModularDevice;
import static hed.domain.HedDomain.initialize;
import static hed.domain.HedDomain.modular;
import static hed.domain.HedDomain.named;
import static hed.domain.HedDomain.type;

/** Creates editable example data in its creation language; stored names are never translated afterward. */
public final class HedSamples {
    /** @param name the dwelling name to retain verbatim
     * @return an empty dwelling with an English catalog
     */
    public static Dwelling empty(String name) {
        return empty(name, Locale.ENGLISH);
    }

    /** @param name the dwelling name to retain verbatim
     * @param locale selects initial catalog names
     * @return an empty dwelling with its own editable catalog
     */
    public static Dwelling empty(String name, Locale locale) {
        var home = named(F.createDwelling(), name);
        var catalog = named(F.createCatalog(), Messages.text(locale, "class.Catalog"));
        home.setCatalog(catalog);
        for (var kind : DeviceKind.values()) {
            var type = named(F.createDeviceType(), Messages.text(locale, "kind." + kind.getName()));
            type.setKind(kind);
            if (modular(kind)) {
                type.setWidthModules(kind == DeviceKind.RCCB || kind == DeviceKind.RCBO ? 2d : 1d);
            }
            catalog.getDeviceTypes().add(type);
        }
        return home;
    }

    /** @return a new, independent example installation with English names */
    public static Dwelling example() {
        return example(Locale.ENGLISH);
    }

    /** @param locale selects initial names, channels and label text throughout the sample
     * @return a new example installation whose names remain ordinary editable user data
     */
    public static Dwelling example(Locale locale) {
        var home = empty(Messages.text(locale, "sample.home"), locale);
        var ground = named(F.createStorey(), Messages.text(locale, "sample.groundFloor"));
        ground.setLevelNumber(0);
        home.getStoreys().add(ground);
        var first = named(F.createStorey(), Messages.text(locale, "sample.firstFloor"));
        first.setLevelNumber(1);
        home.getStoreys().add(first);
        var living = named(F.createRoom(), Messages.text(locale, "sample.livingRoom"));
        var garage = named(F.createRoom(), Messages.text(locale, "sample.garage"));
        var bedroom = named(F.createRoom(), Messages.text(locale, "sample.bedroom"));
        ground.getRooms().addAll(List.of(living, garage));
        first.getRooms().add(bedroom);
        var main = named(F.createDistributionBoard(), Messages.text(locale, "sample.mainBoard"));
        main.setRoom(garage);
        home.getBoards().add(main);
        var secondary = named(F.createDistributionBoard(), Messages.text(locale, "sample.garageBoard"));
        secondary.setRoom(garage);
        home.getBoards().add(secondary);
        var row = named(F.createDINRail(), Messages.text(locale, "sample.row"));
        main.getRails().add(row);
        var row2 = named(F.createDINRail(), Messages.text(locale, "sample.row"));
        secondary.getRails().add(row2);
        var rccb = addModularDevice(row, modularType(home, DeviceKind.RCCB, 63d, 2d, 30d, "A", null));
        rccb.setLabelText(Messages.text(locale, "sample.groundFloor"));
        rccb.setName("ID1");
        var lights = circuit(home, main, row, rccb, "Q1", Messages.text(locale, "sample.groundFloorLighting"), CircuitUse.LIGHTING, 16d);
        var sockets = circuit(home, main, row, rccb, "Q2", Messages.text(locale, "sample.livingRoomOutlets"), CircuitUse.SOCKET_OUTLETS, 20d);
        var shutters = circuit(home, main, row, rccb, "Q3", Messages.text(locale, "sample.rollerShutters"), CircuitUse.SHUTTERS, 16d);
        var upper = circuit(home, secondary, row2, null, "Q4", Messages.text(locale, "sample.bedroomLighting"), CircuitUse.LIGHTING, 10d);
        var rcbo = upper.getProtectiveDevice();
        rcbo.setDeviceType(modularType(home, DeviceKind.RCBO, 10d, 2d, 30d, "A", "C"));
        var feeder = named(F.createDistributionCircuit(), Messages.text(locale, "sample.garageBoardSupply"));
        feeder.setSourceBoard(main);
        feeder.setTargetBoard(secondary);
        var breaker = addModularDevice(row, modularType(home, DeviceKind.MCB, 32d, 1d, null, null, "C"));
        breaker.setLabelText(Messages.text(locale, "sample.garageBoardSupply"));
        breaker.setName("Q5");
        breaker.setUpstreamRccb(rccb);
        feeder.setProtectiveDevice(breaker);
        home.getCircuits().add(feeder);
        var light = (ElectricalLoad) addEquipment(home, type(home, DeviceKind.LUMINAIRE), locale);
        light.setName(Messages.text(locale, "sample.ceilingLight"));
        light.setRoom(living);
        light.setCircuit(lights);
        light.setRatedPowerW(12d);
        var outlet = (Outlet) addEquipment(home, type(home, DeviceKind.OUTLET), locale);
        outlet.setName(Messages.text(locale, "sample.sofaOutlet"));
        outlet.setRoom(living);
        outlet.setCircuit(sockets);
        var shutter = (ElectricalLoad) addEquipment(home, type(home, DeviceKind.SHUTTER_MOTOR), locale);
        shutter.setName(Messages.text(locale, "sample.livingRoomShutter"));
        shutter.setRoom(living);
        shutter.setCircuit(shutters);
        var bedroomLight = addEquipment(home, type(home, DeviceKind.LUMINAIRE), locale);
        bedroomLight.setName(Messages.text(locale, "sample.bedroomLight"));
        bedroomLight.setRoom(bedroom);
        bedroomLight.setCircuit(upper);
        var doubleSwitch = (ControlDevice) addEquipment(home, type(home, DeviceKind.SWITCH), locale);
        doubleSwitch.setName(Messages.text(locale, "sample.doubleSwitch"));
        doubleSwitch.setRoom(living);
        doubleSwitch.setCircuit(lights);
        doubleSwitch.getChannels().add(named(F.createControlChannel(), Messages.text(locale, "channel.defaultName", 2)));
        var second = (ControlDevice) addEquipment(home, type(home, DeviceKind.SWITCH), locale);
        second.setName(Messages.text(locale, "sample.secondControl"));
        second.setRoom(living);
        second.setCircuit(lights);
        var lighting = named(F.createControlFunction(), Messages.text(locale, "sample.livingRoomLighting"));
        lighting.getChannels().addAll(List.of(doubleSwitch.getChannels().get(0), second.getChannels().get(0)));
        lighting.getTargets().add(light);
        home.getControlFunctions().add(lighting);
        var socketControl = named(F.createControlFunction(), Messages.text(locale, "sample.switchedOutlet"));
        socketControl.getChannels().add(doubleSwitch.getChannels().get(1));
        socketControl.getTargets().add(outlet);
        home.getControlFunctions().add(socketControl);
        var shutterSwitch = (ControlDevice) addEquipment(home, type(home, DeviceKind.SHUTTER_CONTROL), locale);
        shutterSwitch.setName(Messages.text(locale, "sample.shutterControl"));
        shutterSwitch.setRoom(living);
        shutterSwitch.setCircuit(shutters);
        var shutterFunction = named(F.createControlFunction(), Messages.text(locale, "sample.shutterFunction"));
        shutterFunction.setMode(ControlMode.SHUTTER);
        shutterFunction.getChannels().add(shutterSwitch.getChannels().get(0));
        shutterFunction.getTargets().add(shutter);
        home.getControlFunctions().add(shutterFunction);
        var connection = named(F.createServiceConnection(), Messages.text(locale, "sample.serviceConnection"));
        connection.setPhase(SupplyPhase.SINGLE_PHASE);
        connection.setSubscribedPowerKva(9d);
        home.setServiceConnection(connection);
        var meter = F.createEnergyMeter();
        initialize(meter, type(home, DeviceKind.ENERGY_METER), locale);
        connection.setMeter(meter);
        var serviceBreaker = F.createServiceCircuitBreaker();
        initialize(serviceBreaker, type(home, DeviceKind.SERVICE_BREAKER), locale);
        connection.setMainBreaker(serviceBreaker);
        rccb.setUpstreamServiceBreaker(serviceBreaker);
        return home;
    }

    private static FinalCircuit circuit(Dwelling home, DistributionBoard board, DINRail rail, ModularDevice rccb, String breakerName, String name, CircuitUse use, double amps) {
        var circuit = named(F.createFinalCircuit(), name);
        circuit.setUsage(use);
        circuit.setSourceBoard(board);
        circuit.setPhase(CircuitPhase.L1);
        var breaker = addModularDevice(rail, modularType(home, DeviceKind.MCB, amps, 1d, null, null, "C"));
        breaker.setLabelText(name);
        breaker.setName(breakerName);
        breaker.setUpstreamRccb(rccb);
        circuit.setProtectiveDevice(breaker);
        home.getCircuits().add(circuit);
        return circuit;
    }

    private static DeviceType modularType(Dwelling home, DeviceKind kind, Double current, Double width, Double residual, String residualType, String curve) {
        var existing = home.getCatalog().getDeviceTypes().stream().filter(t -> t.getKind() == kind && Objects.equals(t.getRatedCurrentA(), current) && Objects.equals(t.getWidthModules(), width)
                && Objects.equals(t.getResidualCurrentMa(), residual) && Objects.equals(t.getResidualType(), residualType) && Objects.equals(t.getTripCurve(), curve)).findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }
        var specific = org.eclipse.emf.ecore.util.EcoreUtil.copy(type(home, kind));
        specific.setRatedCurrentA(current);
        specific.setWidthModules(width);
        specific.setResidualCurrentMa(residual);
        specific.setResidualType(residualType);
        specific.setTripCurve(curve);
        var suffix = " " + Objects.toString(curve, "") + java.math.BigDecimal.valueOf(current).stripTrailingZeros().toPlainString() + " A"
                + (residual == null ? "" : " / " + residual.intValue() + " mA " + residualType);
        specific.setName(specific.getName() + suffix);
        home.getCatalog().getDeviceTypes().add(specific);
        return specific;
    }
}
