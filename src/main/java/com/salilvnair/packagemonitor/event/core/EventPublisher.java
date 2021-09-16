package com.salilvnair.packagemonitor.event.core;

/**
 * @author Salil V Nair
 */
public interface EventPublisher {
    void eventEmitter(EventEmitter eventEmitter);
    default void subscribe(EventEmitter eventEmitter) {
        eventEmitter(eventEmitter);
    }
}
