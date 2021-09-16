package com.salilvnair.packagemonitor.model;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class PackageInfoTableModel extends AbstractTableModel {
    private boolean DEBUG = false;
    private List<PackageInfo> data;
    private List<PackageInfo> originalData;
    private final String[] columns = {"Package name", "Your version", "Latest version(s)"};
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
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return col == 2;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                    + " to " + value
                    + " (an instance of "
                    + value.getClass() + ")");
        }

//        data[row][col] = value;
        data.get(row).setLatestVersion((String) value);
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        System.out.println("data:"+data);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PackageInfo disputeData = data.get(rowIndex);
        switch (columnIndex) {
            case 0 : return disputeData.getPackageName();
            case 1 : return disputeData.getYourVersion();
            case 2 : return disputeData;
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

    public List<PackageInfo> originalData() {
        if(this.originalData == null) {
            this.originalData = new ArrayList<>();
        }
        return this.originalData;
    }

    public void setOriginalData(List<PackageInfo> originalData) {
        this.originalData = originalData;
    }
}
