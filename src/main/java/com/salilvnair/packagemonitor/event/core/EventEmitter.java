package com.salilvnair.packagemonitor.event.core;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public interface EventEmitter {
    void emit(EventObject event);
}
