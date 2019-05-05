package at.ac.uni_linz.tk.webstyles.generic;

import at.ac.uni_linz.tk.webstyles.*;

import java.io.*;
import java.util.*;

public class Memory implements ContentTemplate, Serializable {

    public int width;
    public int height;
    public Vector pairs;

    static {
        ContentManager.getManager().addContent(new Memory());
    }

    public Memory() {
        pairs = new Vector();
    }

    public String getName() {
        return new String("Memory");
    }

    public void editProperties() {
        MemoryPropertyDialog dlg = new MemoryPropertyDialog();
        dlg.setMemory(this);
        dlg.setVisible(true);
    }

    public void removePairs() {
        pairs.removeAllElements();
    }

    public void addPair(MemoryPair pair) {
        pairs.addElement(pair);
    }

    public void setPairs(Vector pairs) {
        this.pairs = pairs;
    }

    public Vector getPairs() {
        return pairs;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException excpt) {
            return null;
        }
    }

}