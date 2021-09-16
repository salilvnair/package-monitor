package com.salilvnair.packagemonitor.toolbar;


import com.intellij.icons.AllIcons;
import com.salilvnair.packagemonitor.listener.ToolbarListener;
import com.salilvnair.packagemonitor.type.ToolbarEvent;
import com.salilvnair.packagemonitor.ui.SwingComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Salil V Nair
 */
public class ToolbarPanel extends JToolBar implements SwingComponent, ActionListener {

    private JButton showAllButton;
    private JButton showDiffOnlyButton;
    private JButton forceRefresh;
    private ToolbarListener toolbarListener;
    private JPanel diffPanel;
    private JPanel refreshPanel;

    public ToolbarPanel() {
        init();
    }

    @Override
    public void init() {
        setBorder(BorderFactory.createEtchedBorder());
        SwingComponent.super.init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        diffPanel = new JPanel();
        refreshPanel = new JPanel();
        showAllButton = new JButton();
        showDiffOnlyButton = new JButton();
        forceRefresh = new JButton();
        showAllButton.setToolTipText("Show All");
        showDiffOnlyButton.setToolTipText("Show Diff");
        forceRefresh.setToolTipText("Force Refresh");
        showAllButton.setIcon(AllIcons.Actions.RegexSelected);
        showDiffOnlyButton.setIcon(AllIcons.Actions.Diff);
        forceRefresh.setIcon(AllIcons.Actions.ForceRefresh);
        showAllButton.setEnabled(false);
    }

    @Override
    public void initListeners() {
        showAllButton.addActionListener(this);
        showDiffOnlyButton.addActionListener(this);
        forceRefresh.addActionListener(this);
    }

    @Override
    public void initChildrenLayout() {
        diffPanel.setLayout(new FlowLayout());
        refreshPanel.setLayout(new FlowLayout());
        refreshPanel.add(forceRefresh);
        diffPanel.add(showAllButton);
        diffPanel.add(showDiffOnlyButton);
        add(diffPanel, BorderLayout.WEST);
        add(refreshPanel, BorderLayout.EAST);
    }


    @Override
    public void initStyle() {
        this.diffPanel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton clicked = (JButton) actionEvent.getSource();
        if(toolbarListener !=null) {
            if(clicked == showAllButton) {
                //textPanel.appendText("Hello!!\n");
                showAllButton.setEnabled(false);
                showDiffOnlyButton.setEnabled(true);
                toolbarListener.emit(ToolbarEvent.SHOW_ALL);
            }
            else if(clicked == forceRefresh) {
                toolbarListener.emit(ToolbarEvent.FORCE_REFRESH);
            }
            else {
                //textPanel.appendText("Bye!!\n");
                showDiffOnlyButton.setEnabled(false);
                showAllButton.setEnabled(true);
                toolbarListener.emit(ToolbarEvent.SHOW_DIFF_ONLY);
            }
        }
    }

    public void showFilterButtons() {
        this.showDiffOnlyButton.setVisible(true);
        this.showAllButton.setVisible(true);
    }

    public void hideFilterButtons() {
        this.showDiffOnlyButton.setVisible(false);
        this.showAllButton.setVisible(false);
    }

    public boolean showDiffEnabled() {
        return this.showDiffOnlyButton.isEnabled();
    }

    public boolean showAllEnabled() {
        return this.showAllButton.isEnabled();
    }

    public void showDiffPanel() {
        this.diffPanel.setVisible(true);
    }

    public void hideDiffPanel() {
        this.diffPanel.setVisible(false);
    }

    public void enableForceRefresh() {
        this.forceRefresh.setEnabled(true);
    }

    public void disableForceRefresh() {
        this.forceRefresh.setEnabled(false);
    }

    public void addToolbarListener(ToolbarListener toolbarListener) {
        this.toolbarListener = toolbarListener;
    }
}
