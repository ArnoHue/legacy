package at.ac.uni_linz.tk.webstyles.xml.memory;

import java.awt.*;

import at.ac.uni_linz.tk.webstyles.generic.*;

public class XMLMemoryPair {
    
    private MemoryPair pair;
    
    public XMLMemoryPair() {
        this(null);
    }
    
    public XMLMemoryPair(MemoryPair pair) {
        this.pair = pair;
    }
    
    public Object getCard1() {
        return pair.card1;
    }
    
    public Object getCard2() {
        return pair.card2;
    }
    
}