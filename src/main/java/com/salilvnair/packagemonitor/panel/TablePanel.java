package com.salilvnair.packagemonitor.panel;

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.salilvnair.packagemonitor.event.core.EventEmitter;
import com.salilvnair.packagemonitor.event.type.TableSelectionEvent;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoTableModel;
import com.salilvnair.packagemonitor.renderer.LatestVersionCellRenderer;
import com.salilvnair.packagemonitor.ui.SwingComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Salil V Nair
 */
public class TablePanel extends JBPanel implements SwingComponent {
    private JBTable table;
    private PackageInfoTableModel tableModel;
    private final JRootPane rootPane;
    private EventEmitter eventEmitter;

    public TablePanel(JRootPane rootPane) {
        this.rootPane = rootPane;
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        tableModel = new PackageInfoTableModel();
        table = new JBTable(tableModel);
        table.getColumnModel().getColumn(2).setCellRenderer(new LatestVersionCellRenderer());
    }

    @Override
    public void initStyle() {
        table.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
    }

    @Override
    public void initListeners() {
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
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TableSelectionEvent event = new TableSelectionEvent(this, true) ;
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

    public void clear() {
        tableModel.data().clear();
    }

    public void replace(PackageInfo packageInfo) {
        tableModel.data().forEach(currentPackageInfo -> {
            if(packageInfo.getPackageName().equals(currentPackageInfo.getPackageName())) {
                currentPackageInfo.setLatestVersion(packageInfo.getLatestVersion());
            }
        });
        tableModel.fireTableDataChanged();
    }

    public void refresh() {
        tableModel.fireTableDataChanged();
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public void showOnlyDiff() {
        List<PackageInfo> filteredList = tableModel
                                            .data()
                                            .stream()
                                            .filter(data -> data.getLatestVersion() != null && !data.getLatestVersion().equals(data.getYourVersion())).collect(Collectors.toList());
        tableModel.setOriginalData(tableModel.data());
        tableModel.setData(filteredList);
        tableModel.fireTableDataChanged();
    }
    public void showAll() {
        tableModel.setData(tableModel.originalData());
        tableModel.fireTableDataChanged();
    }

    public List<PackageInfo> data() {
        return this.tableModel.data();
    }

    public List<Integer> selectedRows() {
        return Arrays.stream(table.getSelectedRows()).boxed().collect(Collectors.toList());
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
