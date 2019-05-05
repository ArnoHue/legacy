package at.ac.uni_linz.tk.webstyles.xml;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.generic.*;
import at.ac.uni_linz.tk.webstyles.generic.ContentTemplate;
import at.ac.uni_linz.tk.webstyles.xml.memory.*;

public class XMLContentFactory {
        
    public static Object getXMLContent(ContentTemplate content) {
        if (content instanceof Memory) {
            return new XMLMemory((Memory)content);
        }
        return null;
    }

}