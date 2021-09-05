package com.salilvnair.packagemonitor.model;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class PackageInfoTableModel extends AbstractTableModel {
    private List<PackageInfo> data;
    private List<PackageInfo> originalData;
    private final String[] columns = {"Package name", "Your version", "Latest version"};
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
        switch (columnIndex) {
            case 0 : return disputeData.getPackageName();
            case 1 : return disputeData.getYourVersion();
            case 2 : return disputeData.getLatestVersion();
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
