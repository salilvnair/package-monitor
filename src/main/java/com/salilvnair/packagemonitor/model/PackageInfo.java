package com.salilvnair.packagemonitor.model;

import java.io.Serializable;

/**
 * @author Salil V Nair
 */
public class PackageInfo implements Serializable {
    private String packageName;
    private String yourVersion;
    private String latestVersion;

    public PackageInfo() {}

    public PackageInfo(String packageName, String yourVersion, String latestVersion) {
        this.packageName = packageName;
        this.yourVersion = yourVersion;
        this.latestVersion = latestVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getYourVersion() {
        return yourVersion;
    }

    public void setYourVersion(String yourVersion) {
        this.yourVersion = yourVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }
}
