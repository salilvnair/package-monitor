package com.salilvnair.packagemonitor.service.type;

import com.salilvnair.packagemonitor.icon.PackageMonitorIcon;

import javax.swing.*;
import java.util.Arrays;

/**
 * @author Salil V Nair
 */
public enum PackageMonitorType {
    NODE_JS(1, "Node Js (package.json)", PackageMonitorIcon.NODEJS_ICON,  "package.json", NodeJsPackageMonitorType.class, NodeJsPackageMonitorType.COMPARE_NPM_VERSIONS, NodeJsPackageMonitorType.UPDATE_NPM_VERSIONS),
    ANGULAR_LIB(2, "Angular Library (angular.json)",PackageMonitorIcon.ANGULAR_ICON,  "angular.json", NgLibraryPackageMonitorType.class, NgLibraryPackageMonitorType.COMPARE_LIBRARY_PROJECT_VERSIONS, NgLibraryPackageMonitorType.UPDATE_LIBRARY_PROJECT_VERSIONS);

    private final int typeIndex;
    private final String type;
    private final String fileName;
    private final Class<?> factory;
    private final PackageMonitorTypeBase compareVersionCommand;
    private final PackageMonitorTypeBase updateVersionCommand;
    private final Icon icon;
    PackageMonitorType(int typeIndex, String type, Icon icon, String fileName, Class<?> factory, PackageMonitorTypeBase compareVersionCommand, PackageMonitorTypeBase updateVersionCommand) {
        this.typeIndex = typeIndex;
        this.compareVersionCommand = compareVersionCommand;
        this.updateVersionCommand = updateVersionCommand;
        this.icon = icon;
        this.type = type;
        this.fileName = fileName;
        this.factory = factory;
    }

    public String type() {
        return type;
    }
    public Icon icon() {
        return icon;
    }

    public PackageMonitorTypeBase compareVersionCommand() {
        return compareVersionCommand;
    }
    public PackageMonitorTypeBase updateVersionCommand() {
        return updateVersionCommand;
    }

    public Class<?> factory() {
        return factory;
    }

    public String fileName() {
        return fileName;
    }

    public int typeIndex() {
        return typeIndex;
    }

    public static PackageMonitorType findByFileName(String fileName) {
        return Arrays.stream(PackageMonitorType.values()).filter(monitorType -> monitorType.fileName().equals(fileName)).findFirst().orElse(PackageMonitorType.NODE_JS);
    }

    @Override
    public String toString() {
        return fileName;
    }
}
