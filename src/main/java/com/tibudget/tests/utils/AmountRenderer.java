package com.tibudget.tests.utils;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.table.DefaultTableCellRenderer;

class AmountRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	NumberFormat formatter;
    
	public AmountRenderer() { super(); }

    public void setValue(Object value) {
        if (formatter==null) {
        	formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        }
        if (value instanceof Double) {
	        Double amount = (Double) value;
	        setText((value == null) ? "" : formatter.format(amount));
	        setHorizontalAlignment(RIGHT);
	        if (amount < 0.0) {
	        	setForeground(Color.RED);
	        }
	        else {
	        	setForeground(new Color(0, 83, 0));
	        }
        }
        else {
        	setText(String.valueOf(value));
        }
    }
}