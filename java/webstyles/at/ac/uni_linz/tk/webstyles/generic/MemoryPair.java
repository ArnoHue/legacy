package at.ac.uni_linz.tk.webstyles.generic;

import java.awt.*;
import java.io.*;

public class MemoryPair implements Serializable {

    public MemoryCard card1;
    public MemoryCard card2;

    public MemoryPair(MemoryCard card1, MemoryCard card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

}