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

public final class HedSamples {
    public static Dwelling empty(String name) {
        return empty(name, Locale.ENGLISH);
    }

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

    public static Dwelling example() {
        return example(Locale.ENGLISH);
    }

    public static Dwelling example(Locale locale) {
        var home = empty("Sample home", locale);
        var ground = named(F.createStorey(), "Ground floor");
        ground.setLevelNumber(0);
        home.getStoreys().add(ground);
        var first = named(F.createStorey(), "First floor");
        first.setLevelNumber(1);
        home.getStoreys().add(first);
        var living = named(F.createRoom(), "Living room");
        var garage = named(F.createRoom(), "Garage");
        var bedroom = named(F.createRoom(), "Bedroom");
        ground.getRooms().addAll(List.of(living, garage));
        first.getRooms().add(bedroom);
        var main = named(F.createDistributionBoard(), "Main distribution board");
        main.setRoom(garage);
        home.getBoards().add(main);
        var secondary = named(F.createDistributionBoard(), "Garage distribution board");
        secondary.setRoom(garage);
        home.getBoards().add(secondary);
        var row = named(F.createDINRail(), "Row 1");
        main.getRails().add(row);
        var row2 = named(F.createDINRail(), "Row 1");
        secondary.getRails().add(row2);
        var rccb = addModularDevice(row, modularType(home, DeviceKind.RCCB, 63d, 2d, 30d, "A", null));
        rccb.setLabelText("Ground floor");
        rccb.setName("ID1");
        var lights = circuit(home, main, row, rccb, "Q1", "Ground floor lighting", CircuitUse.LIGHTING, 16d);
        var sockets = circuit(home, main, row, rccb, "Q2", "Living room outlets", CircuitUse.SOCKET_OUTLETS, 20d);
        var shutters = circuit(home, main, row, rccb, "Q3", "Roller shutters", CircuitUse.SHUTTERS, 16d);
        var upper = circuit(home, secondary, row2, null, "Q4", "Bedroom lighting", CircuitUse.LIGHTING, 10d);
        var rcbo = upper.getProtectiveDevice();
        rcbo.setDeviceType(modularType(home, DeviceKind.RCBO, 10d, 2d, 30d, "A", "C"));
        var feeder = named(F.createDistributionCircuit(), "Garage board supply");
        feeder.setSourceBoard(main);
        feeder.setTargetBoard(secondary);
        var breaker = addModularDevice(row, modularType(home, DeviceKind.MCB, 32d, 1d, null, null, "C"));
        breaker.setLabelText("Garage board supply");
        breaker.setName("Q5");
        breaker.setUpstreamRccb(rccb);
        feeder.setProtectiveDevice(breaker);
        home.getCircuits().add(feeder);
        var light = (ElectricalLoad) addEquipment(home, type(home, DeviceKind.LUMINAIRE));
        light.setName("Ceiling light");
        light.setRoom(living);
        light.setCircuit(lights);
        light.setRatedPowerW(12d);
        var outlet = (Outlet) addEquipment(home, type(home, DeviceKind.OUTLET));
        outlet.setName("Sofa outlet");
        outlet.setRoom(living);
        outlet.setCircuit(sockets);
        var shutter = (ElectricalLoad) addEquipment(home, type(home, DeviceKind.SHUTTER_MOTOR));
        shutter.setName("Living room shutter");
        shutter.setRoom(living);
        shutter.setCircuit(shutters);
        var bedroomLight = addEquipment(home, type(home, DeviceKind.LUMINAIRE));
        bedroomLight.setName("Bedroom light");
        bedroomLight.setRoom(bedroom);
        bedroomLight.setCircuit(upper);
        var doubleSwitch = (ControlDevice) addEquipment(home, type(home, DeviceKind.SWITCH));
        doubleSwitch.setName("Entrance double switch");
        doubleSwitch.setRoom(living);
        doubleSwitch.setCircuit(lights);
        doubleSwitch.getChannels().add(named(F.createControlChannel(), "Channel 2"));
        var second = (ControlDevice) addEquipment(home, type(home, DeviceKind.SWITCH));
        second.setName("Second lighting control");
        second.setRoom(living);
        second.setCircuit(lights);
        var lighting = named(F.createControlFunction(), "Living room lighting");
        lighting.getChannels().addAll(List.of(doubleSwitch.getChannels().get(0), second.getChannels().get(0)));
        lighting.getTargets().add(light);
        home.getControlFunctions().add(lighting);
        var socketControl = named(F.createControlFunction(), "Switched outlet");
        socketControl.getChannels().add(doubleSwitch.getChannels().get(1));
        socketControl.getTargets().add(outlet);
        home.getControlFunctions().add(socketControl);
        var shutterSwitch = (ControlDevice) addEquipment(home, type(home, DeviceKind.SHUTTER_CONTROL));
        shutterSwitch.setName("Shutter control");
        shutterSwitch.setRoom(living);
        shutterSwitch.setCircuit(shutters);
        var shutterFunction = named(F.createControlFunction(), "Living room shutter control");
        shutterFunction.setMode(ControlMode.SHUTTER);
        shutterFunction.getChannels().add(shutterSwitch.getChannels().get(0));
        shutterFunction.getTargets().add(shutter);
        home.getControlFunctions().add(shutterFunction);
        var connection = named(F.createServiceConnection(), "Service connection");
        connection.setPhase(SupplyPhase.SINGLE_PHASE);
        connection.setSubscribedPowerKva(9d);
        home.setServiceConnection(connection);
        var meter = F.createEnergyMeter();
        initialize(meter, type(home, DeviceKind.ENERGY_METER));
        connection.setMeter(meter);
        var serviceBreaker = F.createServiceCircuitBreaker();
        initialize(serviceBreaker, type(home, DeviceKind.SERVICE_BREAKER));
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
