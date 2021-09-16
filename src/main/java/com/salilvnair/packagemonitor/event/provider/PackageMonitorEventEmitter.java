package com.salilvnair.packagemonitor.event.provider;

import com.salilvnair.packagemonitor.event.core.EventEmitter;
import com.salilvnair.packagemonitor.event.type.PackageMonitorEvent;
import com.salilvnair.packagemonitor.event.type.PackageMonitorEventType;
import com.salilvnair.packagemonitor.model.PackageInfo;

/**
 * @author Salil V Nair
 */
public class PackageMonitorEventEmitter {
    private final EventEmitter emitter;
    public PackageMonitorEventEmitter(EventEmitter emitter) {
        this.emitter = emitter;
    }
    public void emitTablePanelData(PackageInfo disputeData) {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.TABLE_PANEL_DATA);
        event.setTablePanelData(disputeData);
        emitter.emit(event);
    }

    public void disableTableRowSelection() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.DISABLE_TABLE_ROW_SELECTION);
        event.setDisableTableRowSelection(true);
        emitter.emit(event);
    }

    public void enableTableRowSelection() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.ENABLE_TABLE_ROW_SELECTION);
        event.setDisableTableRowSelection(false);
        emitter.emit(event);
    }

    public void showLoading() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.SHOW_LOADING);
        event.setShowLoading(true);
        emitter.emit(event);
    }

    public void hideLoading() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.HIDE_LOADING);
        event.setShowLoading(false);
        emitter.emit(event);
    }

    public void disposeLoading() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.DISPOSE_LOADING);
        event.setDisposeLoading(false);
        emitter.emit(event);
    }

    public void showUpdateButton() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.SHOW_UPDATE_BTN);
        event.setShowUpdateButton(true);
        emitter.emit(event);
    }

    public void hideUpdateButton() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.HIDE_UPDATE_BTN);
        event.setShowUpdateButton(false);
        emitter.emit(event);
    }

    public void showToolbarPanel() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.SHOW_TOOLBAR_PANEL);
        event.setShowToolbarPanel(true);
        emitter.emit(event);
    }

    public void hideToolbarPanel() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.HIDE_TOOLBAR_PANEL);
        event.setShowToolbarPanel(false);
        emitter.emit(event);
    }

    public void showToolbarDiffPanel() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.SHOW_TOOLBAR_DIFF_PANEL);
        event.setShowToolbarPanel(true);
        emitter.emit(event);
    }

    public void hideToolBarDiffPanel() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.HIDE_TOOLBAR_DIFF_PANEL);
        event.setShowToolbarPanel(false);
        emitter.emit(event);
    }
    public void enableForceRefresh() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.ENABLE_FORCE_REFRESH);
        event.setShowToolbarPanel(false);
        emitter.emit(event);
    }
    public void disableForceRefresh() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.DISABLE_FORCE_REFRESH);
        event.setShowToolbarPanel(false);
        emitter.emit(event);
    }

    public void replaceTableData(PackageInfo replacedTableData) {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.REPLACE_TABLE_DATA);
        event.setReplacedTableData(replacedTableData);
        emitter.emit(event);
    }

    public void updatedNgLibVersion() {
        PackageMonitorEvent event = new PackageMonitorEvent(this, PackageMonitorEventType.NG_LIB_UPDATED_EVENT);
        emitter.emit(event);
    }
}
