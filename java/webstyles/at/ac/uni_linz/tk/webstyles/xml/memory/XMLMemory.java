package at.ac.uni_linz.tk.webstyles.xml.memory;

import java.util.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.generic.*;

public class XMLMemory {
    
    private Memory memory;
        
    public XMLMemory() {
        this(null);
    }
        
    public XMLMemory(Memory memory) {
        this.memory = memory;
    }
    
    public Vector getPairs() {
        Vector vec = new Vector(memory.getPairs().size());
        for (int i = 0; i < memory.getPairs().size(); i++) {
            vec.addElement(new XMLMemoryPair((MemoryPair)memory.getPairs().elementAt(i)));
        }
        return vec;
    }
    
    public String getName() {
        return memory.getName();
    }
    
    public int getWidth() {
        return memory.width;
    }
    
    public int getHeight() {
        return memory.height;
    }

}