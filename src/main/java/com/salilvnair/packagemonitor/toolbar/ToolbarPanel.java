package com.salilvnair.packagemonitor.toolbar;



import com.intellij.util.ui.UIUtil;
import com.salilvnair.packagemonitor.listener.ToolbarListener;
import com.salilvnair.packagemonitor.type.ToolbarEvent;
import com.salilvnair.packagemonitor.ui.SwingComponent;
import com.salilvnair.packagemonitor.util.IconUtils;

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
    private ToolbarListener toolbarListener;

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
        setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    @Override
    public void initComponents() {
        showAllButton = new JButton();
        showDiffOnlyButton = new JButton();
        showAllButton.setToolTipText("Show All");
        showDiffOnlyButton.setToolTipText("Show Diff Only");

        if(UIUtil.isUnderDarcula()) {
            showAllButton.setIcon(IconUtils.createIcon("/icon/view_all_white.png"));
            showDiffOnlyButton.setIcon(IconUtils.createIcon("/icon/diff_only_white.png"));
        }
        else {
            showAllButton.setIcon(IconUtils.createIcon("/icon/view_all_dark.png"));
            showDiffOnlyButton.setIcon(IconUtils.createIcon("/icon/diff_only_dark.png"));
        }
        showAllButton.setEnabled(false);
    }

    @Override
    public void initListeners() {
        showAllButton.addActionListener(this);
        showDiffOnlyButton.addActionListener(this);
    }

    @Override
    public void initChildrenLayout() {
        add(showAllButton);
        //addSeparator();
        add(showDiffOnlyButton);
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

    public void addToolbarListener(ToolbarListener toolbarListener) {
        this.toolbarListener = toolbarListener;
    }
}
