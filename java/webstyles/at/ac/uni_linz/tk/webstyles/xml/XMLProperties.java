package at.ac.uni_linz.tk.webstyles.xml;

import java.util.*;

public class XMLProperties extends Vector {
        
    public XMLProperties(Hashtable properties) {
        Enumeration keys = properties.keys();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            addElement(new XMLProperty(key, properties.get(key)));
        }
    }

}