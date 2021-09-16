package com.salilvnair.packagemonitor.event.type;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class PackageInfoEvent extends EventObject {
    private String name;
    private String yourVersion;
    private String latestVersion;

    public PackageInfoEvent(Object source, String yourVersion, String latestVersion) {
        super(source);
        this.yourVersion = yourVersion;
        this.latestVersion = latestVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
