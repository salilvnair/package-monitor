package com.salilvnair.packagemonitor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class PackageInfoConfiguration implements Serializable {
    List<PackageInfo> configuredPackageInfos;
    private boolean configuredPackagesInSync;

    public PackageInfoConfiguration(List<PackageInfo> configuredPackageInfos, boolean configuredPackagesInSync) {
        this.configuredPackageInfos = configuredPackageInfos;
        this.configuredPackagesInSync = configuredPackagesInSync;
    }

    public List<PackageInfo> getConfiguredPackageInfos() {
        if(configuredPackageInfos == null) {
            configuredPackageInfos = new ArrayList<>();
        }
        return configuredPackageInfos;
    }

    public void setConfiguredPackageInfos(List<PackageInfo> configuredPackageInfos) {
        this.configuredPackageInfos = configuredPackageInfos;
    }

    public boolean isConfiguredPackagesInSync() {
        return configuredPackagesInSync;
    }

    public void setConfiguredPackagesInSync(boolean configuredPackagesInSync) {
        this.configuredPackagesInSync = configuredPackagesInSync;
    }
}
