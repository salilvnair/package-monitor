package com.salilvnair.packagemonitor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class PackageInfo implements Serializable {
    private String packageName;
    private String yourVersion;
    private String latestVersion;
    private List<String> packageVersions;

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

    public List<String> packageVersions() {
        if(packageVersions == null) {
            packageVersions = new ArrayList<>();
        }
        return packageVersions;
    }

    public List<String> getPackageVersions() {
        return packageVersions;
    }

    public void setPackageVersions(List<String> packageVersions) {
        this.packageVersions = packageVersions;
    }
}
