package com.salilvnair.packagemonitor.panel;

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.salilvnair.packagemonitor.emitter.EventEmitter;
import com.salilvnair.packagemonitor.event.TableSelectionEvent;
import com.salilvnair.packagemonitor.model.PackageConfigTableModel;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.ui.SwingComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class ConfigurationTablePanel extends JBPanel implements SwingComponent {
    private PackageConfigTableModel tableModel;
    private JBTable table;
    private EventEmitter eventEmitter;
    private JRootPane rootPane;
    public ConfigurationTablePanel(JRootPane rootPane) {
        this.rootPane = rootPane;
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }


    @Override
    public void initComponents() {
        tableModel = new PackageConfigTableModel();
        table = new JBTable(tableModel);
    }

    @Override
    public void initStyle() {
        table.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
    }

    @Override
    public void initListeners() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TableSelectionEvent event = new TableSelectionEvent(this, true) ;
                eventEmitter.emit(event);
            }
        });
        this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSelection");
        this.rootPane.getActionMap().put("clearSelection", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSelection();
                TableSelectionEvent event = new TableSelectionEvent(this, false) ;
                eventEmitter.emit(event);
            }
        });
    }

    @Override
    public void initChildrenLayout() {
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setData(java.util.List<PackageInfo> data) {
        tableModel.setData(data);
    }

    public void addData(PackageInfo packageInfo) {
        tableModel.data().add(packageInfo);
        tableModel.fireTableDataChanged();
    }

    public void refresh() {
        tableModel.fireTableDataChanged();
    }

    public int[] selectedRows() {
        return table.getSelectedRows();
    }

    public List<PackageInfo> data() {
        return this.tableModel.data();
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public void actionPerformed(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    public void disableRowSelection() {
        table.setRowSelectionAllowed(false);
    }

    public void enableRowSelection() {
        table.setRowSelectionAllowed(true);
    }
}
