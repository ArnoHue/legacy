package at.ac.uni_linz.tk.webstyles.generic;

import java.awt.*;
import java.io.*;

public class MemoryCard implements Serializable {

    public String name;
    public Point pos;

    public MemoryCard(String name) {
        this(name, null);
    }

    public MemoryCard(String name, Point pos) {
        this.name = name;
        this.pos = pos;
    }

    public String toString() {
        return name;
    }

}