package com.salilvnair.packagemonitor.service.core;

import com.salilvnair.packagemonitor.event.core.EventPublisher;
import com.salilvnair.packagemonitor.service.context.DataContext;
import com.salilvnair.packagemonitor.service.context.PackageMonitorContext;
import com.salilvnair.packagemonitor.service.type.PackageMonitorTypeBase;

/**
 * @author Salil V Nair
 */
public interface PackageMonitorService extends EventPublisher {
    PackageMonitorContext monitor(PackageMonitorTypeBase monitorType, DataContext dataContext, Object... objects);
}
