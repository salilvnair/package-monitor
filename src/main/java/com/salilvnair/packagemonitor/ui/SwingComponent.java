package com.salilvnair.packagemonitor.ui;

/**
 * @author Salil V Nair
 */
public interface SwingComponent {

    default void init() {
        initLayout();
        initComponents();
        initStyle();
        initListeners();
        initChildrenLayout();
    }

    default void initLayout() {}

    default void initStyle() {}

    default void initComponents() {}

    default void initChildrenLayout() {}

    default void initListeners() {}

}
