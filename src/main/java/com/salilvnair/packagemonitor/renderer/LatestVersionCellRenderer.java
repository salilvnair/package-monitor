package com.salilvnair.packagemonitor.renderer;

import com.salilvnair.packagemonitor.util.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Salil V Nair
 */
public class LatestVersionCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
//        if(value == null) {
//            ImageIcon icon = IconUtils.createIcon("/icon/loading.gif");
//            icon.
//            ((JLabel)cell).setIcon();
//        }
//        else {
//            ((JLabel)cell).setIcon(null);
//        }
        return this;
    }
}
