package at.ac.uni_linz.tk.webstyles.gui;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class PropertyTableModel extends DefaultTableModel {

    public PropertyTableModel() {
        super(new Object[][] { }, new Object[] { "Name", "Value" });
    }

    public Properties getProperties() {
        Properties prop = new Properties();
        for (int i = 0; i < getRowCount(); i++) {
            prop.put(getValueAt(i, 0), getValueAt(i, 1));
        }
        return prop;
    }

    public void setProperties(Properties prop) {
        setNumRows(0);
        Enumeration enum = prop.keys();
        while (enum.hasMoreElements()) {
            Object key = enum.nextElement();
            addRow(new Object[] { key, prop.get(key) });
        }
    }

}