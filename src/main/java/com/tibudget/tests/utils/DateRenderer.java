package com.tibudget.tests.utils;

import java.text.SimpleDateFormat;

import javax.swing.table.DefaultTableCellRenderer;

class DateRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	SimpleDateFormat formatter;
    
	public DateRenderer() { super(); }

    public void setValue(Object value) {
        if (formatter==null) {
            formatter = new SimpleDateFormat("yyyy - MM - dd");
        }
        setText((value == null) ? "" : formatter.format(value));
    }
}