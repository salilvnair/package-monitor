package com.salilvnair.packagemonitor.event.type;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class TableSelectionEvent extends EventObject {
    private final boolean rowsSelected;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public TableSelectionEvent(Object source, boolean rowsSelected) {
        super(source);
        this.rowsSelected = rowsSelected;
    }

    public boolean isRowsSelected() {
        return rowsSelected;
    }
}
