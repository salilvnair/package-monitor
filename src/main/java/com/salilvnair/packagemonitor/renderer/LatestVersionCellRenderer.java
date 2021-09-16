package com.salilvnair.packagemonitor.renderer;

import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.util.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class LatestVersionCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        JComboBox<String> comboBox = new ComboBox<>();
        comboBox.addItem("");
        if(value!=null) {
            PackageInfo packageInfo = (PackageInfo) value;
            if(!packageInfo.packageVersions().isEmpty()) {
                comboBox = new ComboBox<>();
                comboBox.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
                for (String version : packageInfo.packageVersions()) {
                    comboBox.addItem(version);
                }
                table.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(comboBox));
                comboBox.setSelectedItem(packageInfo.getLatestVersion());
                setOpaque(true);
            }
        }
        return comboBox;
    }
}
