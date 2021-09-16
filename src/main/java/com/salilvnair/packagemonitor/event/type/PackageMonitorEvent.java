package com.salilvnair.packagemonitor.event.type;

import com.salilvnair.packagemonitor.model.PackageInfo;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class PackageMonitorEvent extends EventObject {
    private final PackageMonitorEventType eventType;
    private PackageInfo tablePanelData;
    private boolean disableTableRowSelection;
    private boolean showLoading;
    private boolean disposeLoading;
    private boolean showUpdateButton;
    private boolean showToolbarPanel;
    private PackageInfo replacedTableData;
    public PackageMonitorEvent(Object source, PackageMonitorEventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    public PackageInfo tablePanelData() {
        return tablePanelData;
    }

    public void setTablePanelData(PackageInfo tablePanelData) {
        this.tablePanelData = tablePanelData;
    }

    public boolean disableTableRowSelection() {
        return disableTableRowSelection;
    }

    public void setDisableTableRowSelection(boolean disableTableRowSelection) {
        this.disableTableRowSelection = disableTableRowSelection;
    }

    public boolean showLoading() {
        return showLoading;
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }

    public boolean disposeLoading() {
        return disposeLoading;
    }

    public void setDisposeLoading(boolean disposeLoading) {
        this.disposeLoading = disposeLoading;
    }

    public boolean showUpdateButton() {
        return showUpdateButton;
    }

    public void setShowUpdateButton(boolean showUpdateButton) {
        this.showUpdateButton = showUpdateButton;
    }

    public boolean isShowToolbarPanel() {
        return showToolbarPanel;
    }

    public void setShowToolbarPanel(boolean showToolbarPanel) {
        this.showToolbarPanel = showToolbarPanel;
    }

    public PackageInfo replacedTableData() {
        return replacedTableData;
    }

    public void setReplacedTableData(PackageInfo replacedTableData) {
        this.replacedTableData = replacedTableData;
    }

    public PackageMonitorEventType eventType() {
        return eventType;
    }
}
