package com.salilvnair.packagemonitor.service.context;

import com.intellij.openapi.project.Project;
import com.salilvnair.packagemonitor.frame.PackageMonitorMainFrame;
import com.salilvnair.packagemonitor.model.NgLibInfo;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoConfiguration;
import com.salilvnair.packagemonitor.service.type.PackageMonitorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Salil V Nair
 */
public class DataContext {
    private final Project project;
    private final PackageMonitorMainFrame monitorMainFrame;
    private PackageInfoConfiguration packageInfoConfiguration;
    private List<PackageInfo> packageInfos;
    private List<Integer> selectedRows;
    private PackageMonitorType packageMonitorType;
    private Map<String, NgLibInfo> ngLibInfoMap;

    public DataContext(Project project, PackageMonitorMainFrame monitorMainFrame) {
        this.project = project;
        this.monitorMainFrame = monitorMainFrame;
    }

    public DataContext(Project project, PackageMonitorMainFrame monitorMainFrame, PackageInfoConfiguration packageInfoConfiguration) {
        this.project = project;
        this.monitorMainFrame = monitorMainFrame;
        this.packageInfoConfiguration = packageInfoConfiguration;
    }

    //getters and setters

    public Project project() {
        return project;
    }

    public PackageInfoConfiguration packageInfoConfiguration() {
        return packageInfoConfiguration;
    }

    public void setPackageInfoConfiguration(PackageInfoConfiguration packageInfoConfiguration) {
        this.packageInfoConfiguration = packageInfoConfiguration;
    }

    public PackageMonitorMainFrame packageMonitorMainFrame() {
        return monitorMainFrame;
    }

    public List<PackageInfo> packageInfos() {
        return packageInfos;
    }

    public void setPackageInfos(List<PackageInfo> packageInfos) {
        this.packageInfos = packageInfos;
    }

    public List<Integer> selectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(List<Integer> selectedRows) {
        this.selectedRows = selectedRows;
    }

    public PackageMonitorType packageMonitorType() {
        if(packageMonitorType == null) {
            packageMonitorType = PackageMonitorType.NODE_JS;
        }
        return packageMonitorType;
    }

    public void setPackageMonitorType(PackageMonitorType packageMonitorType) {
        this.packageMonitorType = packageMonitorType;
    }

    public Map<String, NgLibInfo> ngLibInfoMap() {
        if(ngLibInfoMap == null) {
            ngLibInfoMap = new HashMap<>();
        }
        return ngLibInfoMap;
    }

    public void setNgLibInfoMap(Map<String, NgLibInfo> ngLibInfoMap) {
        this.ngLibInfoMap = ngLibInfoMap;
    }
}
