package at.ac.uni_linz.tk.webstyles.generic;

import java.io.*;

import at.ac.uni_linz.tk.webstyles.generic.*;

public interface ContentTemplate extends Cloneable, Serializable {
    
    public String getName();
    
    public void editProperties();
    
    public Object clone();
    
}