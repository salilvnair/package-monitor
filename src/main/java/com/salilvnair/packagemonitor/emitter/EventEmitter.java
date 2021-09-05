package com.salilvnair.packagemonitor.emitter;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public interface EventEmitter {
    void emit(EventObject event);
}
