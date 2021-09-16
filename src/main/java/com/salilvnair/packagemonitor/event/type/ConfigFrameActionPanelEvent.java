package com.salilvnair.packagemonitor.event.type;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class ConfigFrameActionPanelEvent extends EventObject {
    private final boolean saveClicked;
    private final boolean showAllClicked;
    private final boolean cancelClicked;
    public ConfigFrameActionPanelEvent(Object source, boolean saveClicked, boolean showAllClicked) {
        super(source);
        this.saveClicked = saveClicked;
        this.showAllClicked = showAllClicked;
        this.cancelClicked = false;
    }

    public ConfigFrameActionPanelEvent(Object source, boolean saveClicked, boolean showAllClicked, boolean cancelClicked) {
        super(source);
        this.saveClicked = saveClicked;
        this.cancelClicked = cancelClicked;
        this.showAllClicked = showAllClicked;
    }

    public boolean saveClicked() {
        return saveClicked;
    }

    public boolean showAllClicked() {
        return showAllClicked;
    }

    public boolean cancelClicked() {
        return cancelClicked;
    }

}
