package com.salilvnair.packagemonitor.panel;

import com.intellij.ui.components.JBPanel;
import com.salilvnair.packagemonitor.emitter.EventEmitter;
import com.salilvnair.packagemonitor.event.ConfigFrameActionPanelEvent;
import com.salilvnair.packagemonitor.event.MainFrameActionPanelEvent;
import com.salilvnair.packagemonitor.ui.SwingComponent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Salil V Nair
 */
public class ConfigFrameActionPanel extends JBPanel implements SwingComponent {
    private JButton saveButton;
    private JButton showAllButton;
    private JButton cancelButton;
    private EventEmitter eventEmitter;
    private final JRootPane rootPane;

    public ConfigFrameActionPanel(JRootPane rootPane) {
        this.rootPane = rootPane;
        init();
    }

    @Override
    public void initComponents() {
        saveButton = new JButton("Save");
        showAllButton = new JButton("Show All");
        cancelButton = new JButton("Cancel");
        rootPane.setDefaultButton(saveButton);
        saveButton.setEnabled(false);
    }

    @Override
    public void initStyle() {
        showAllButton.setVisible(false);
    }

    @Override
    public void initLayout() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
    }

    @Override
    public void initChildrenLayout() {
        add(saveButton);
        add(showAllButton);
        //add(cancelButton);
    }

    @Override
    public void initListeners() {
        saveButton.addActionListener(actionEvent -> {
            ConfigFrameActionPanelEvent event = new ConfigFrameActionPanelEvent(this, true, false);
            this.eventEmitter.emit(event);
        });

        cancelButton.addActionListener(actionEvent -> {
            ConfigFrameActionPanelEvent event = new ConfigFrameActionPanelEvent(this, false, false, true);
            this.eventEmitter.emit(event);
        });

        showAllButton.addActionListener(actionEvent -> {
            ConfigFrameActionPanelEvent event = new ConfigFrameActionPanelEvent(this, false, true);
            this.eventEmitter.emit(event);
        });
    }

    public void actionPerformed(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    public void disableSaveButton() {
        this.saveButton.setEnabled(false);
    }

    public void enableSaveButton() {
        this.saveButton.setEnabled(true);
    }

    public void enableShowAllButton() {
        this.saveButton.setVisible(false);
        this.showAllButton.setVisible(true);
        rootPane.setDefaultButton(showAllButton);
    }

    public void disableShowAllButton() {
        this.saveButton.setVisible(true);
        this.showAllButton.setVisible(false);
        rootPane.setDefaultButton(saveButton);
    }

}
