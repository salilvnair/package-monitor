package com.salilvnair.packagemonitor.model;

/**
 * @author Salil V Nair
 */
public class NgLibInfo {
    private String name;
    private String version;
    private String packageJsonPath;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageJsonPath() {
        return packageJsonPath;
    }

    public void setPackageJsonPath(String packageJsonPath) {
        this.packageJsonPath = packageJsonPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
