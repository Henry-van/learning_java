package com.guangh.fan.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MyTable extends JTable {

    public MyTable(DefaultTableModel dm ) {
        super(dm);
    }

    public MyTable(final Object[][] rowData, final Object[] columnNames) {
        super(rowData, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
