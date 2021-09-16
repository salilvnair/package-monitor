package com.salilvnair.packagemonitor.panel;

import com.intellij.ui.components.JBPanel;
import com.salilvnair.packagemonitor.event.core.EventEmitter;
import com.salilvnair.packagemonitor.event.type.MainFrameActionPanelEvent;
import com.salilvnair.packagemonitor.ui.SwingComponent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Salil V Nair
 */
public class MainFrameActionPanel extends JBPanel implements SwingComponent {
    private JButton updateButton;
    private JButton cancelButton;
    private EventEmitter eventEmitter;
    private final JRootPane rootPane;

    public MainFrameActionPanel(JRootPane rootPane) {
        this.rootPane = rootPane;
        init();
    }

    @Override
    public void initComponents() {
        updateButton = new JButton("Update All");
        cancelButton = new JButton("Cancel");
        rootPane.setDefaultButton(updateButton);
        updateButton.setVisible(false);
    }

    @Override
    public void initLayout() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
    }

    @Override
    public void initChildrenLayout() {
        add(updateButton);
        //add(cancelButton);
    }

    @Override
    public void initListeners() {
        updateButton.addActionListener(actionEvent -> {
            MainFrameActionPanelEvent event = new MainFrameActionPanelEvent(this, true);
            this.eventEmitter.emit(event);
        });

        cancelButton.addActionListener(actionEvent -> {
            MainFrameActionPanelEvent event = new MainFrameActionPanelEvent(this, false);
            this.eventEmitter.emit(event);
        });
    }

    public void actionPerformed(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    public void showUpdateButton() {
        this.updateButton.setVisible(true);
    }

    public void hideUpdateButton() {
        this.updateButton.setVisible(false);
    }

    public void changeUpdateButtonText(boolean selected) {
        String text = selected ? "Update Selected" : "Update All";
        this.updateButton.setText(text);
    }
}
