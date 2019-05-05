package at.ac.uni_linz.tk.webstyles.generic;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class MemoryPairsTableModel extends DefaultTableModel {

    public MemoryPairsTableModel() {
        super(new Object[] { "Card 1", "Card 2" }, 0);
    }

    public Vector getMemoryPairs() {
        Vector vec = new Vector();
        for (int i = 0; i < getRowCount(); i++) {
            vec.addElement(new MemoryPair(new MemoryCard(getValueAt(i, 0).toString()), new MemoryCard(getValueAt(i, 1).toString())));
        }
        return vec;
    }

    public void setMemoryPairs(Vector pairs) {
        setNumRows(0);
        for (int i = 0; i < pairs.size(); i++) {
            MemoryPair pair = (MemoryPair)pairs.elementAt(i);
            addRow(new Object[] { pair.card1, pair.card2 });
        }
    }

}