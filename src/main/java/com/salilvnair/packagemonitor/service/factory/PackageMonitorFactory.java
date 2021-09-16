package com.salilvnair.packagemonitor.service.factory;

import com.salilvnair.packagemonitor.service.core.PackageMonitorService;
import com.salilvnair.packagemonitor.service.provider.NgLibraryPackageMonitor;
import com.salilvnair.packagemonitor.service.provider.NodeJsPackageMonitor;
import com.salilvnair.packagemonitor.service.type.NodeJsPackageMonitorType;

/**
 * @author Salil V Nair
 */
public class PackageMonitorFactory {
    private PackageMonitorFactory(){}

    public static PackageMonitorService generate(Class<?> monitorType) {
        if(NodeJsPackageMonitorType.class == monitorType) {
            return new NodeJsPackageMonitor();
        }
        else {
            return new NgLibraryPackageMonitor();
        }
    }
}
