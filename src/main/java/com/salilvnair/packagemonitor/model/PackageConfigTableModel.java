package com.salilvnair.packagemonitor.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class PackageConfigTableModel extends AbstractTableModel {
    private List<PackageInfo> data;
    private final String[] columns = {"Package name"};
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PackageInfo disputeData = data.get(rowIndex);
        if (columnIndex == 0) {
            return disputeData.getPackageName();
        }
        return null;
    }

    public void setData(List<PackageInfo> data) {
        this.data = data;
    }

    public List<PackageInfo> data() {
        if(this.data == null) {
            this.data = new ArrayList<>();
        }
        return this.data;
    }

}
