package com.salilvnair.packagemonitor.event.type;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class MainFrameEvent extends EventObject {
    private boolean windowClosed;
    public MainFrameEvent(Object source, boolean windowClosed) {
        super(source);
        this.windowClosed = windowClosed;
    }

    public boolean windowClosed() {
        return windowClosed;
    }

    public void setWindowClosed(boolean windowClosed) {
        this.windowClosed = windowClosed;
    }
}
