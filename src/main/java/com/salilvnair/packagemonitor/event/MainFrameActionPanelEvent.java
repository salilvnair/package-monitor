package com.salilvnair.packagemonitor.event;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class MainFrameActionPanelEvent extends EventObject {
    private final boolean updateClicked;
    public MainFrameActionPanelEvent(Object source, boolean updateClicked) {
        super(source);
        this.updateClicked = updateClicked;
    }

    public boolean updateClicked() {
        return updateClicked;
    }

}
