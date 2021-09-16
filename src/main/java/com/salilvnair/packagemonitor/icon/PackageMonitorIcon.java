package com.salilvnair.packagemonitor.icon;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author Salil V Nair
 */
public interface PackageMonitorIcon {
    Icon ANGULAR_ICON = IconLoader.getIcon("/icons/angular2.svg", PackageMonitorIcon.class);
    Icon NODEJS_ICON = IconLoader.getIcon("/icons/nodejs.svg", PackageMonitorIcon.class);
    Icon GEAR_ICON = IconLoader.getIcon("/icons/gear.svg", PackageMonitorIcon.class);
}
