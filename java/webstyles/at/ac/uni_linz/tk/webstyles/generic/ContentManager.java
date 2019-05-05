package at.ac.uni_linz.tk.webstyles.generic;

import java.util.*;
import java.util.Enumeration;

import at.ac.uni_linz.tk.webstyles.generic.*;

public class ContentManager {
    
    private static ContentManager manager;
    private Hashtable contents;
    
    static {
        manager = new ContentManager();
    }
    
    public static ContentManager getManager() {
        return manager;
    }
    
    private ContentManager() {
        contents = new Hashtable();
    }
    
    public void addContent(ContentTemplate content) {
        if (!contents.contains(content)) {
            contents.put(content.getName(), content);
        }
    }
    
    public ContentTemplate getContentTemplate(String name) {
        return (ContentTemplate)contents.get(name);
    }
    
    public ContentTemplate createContent(String name) {
        ContentTemplate content = getContentTemplate(name);
        if (content != null) {
            return (ContentTemplate)content.clone();
        }
        else {
            return null;
        }
    }
    
    public String[] getContentNames() {
        String[] names = new String[contents.size()];
        Enumeration enum = contents.elements();
        for (int i = 0; enum.hasMoreElements(); i++) {
            names[i] = ((ContentTemplate)enum.nextElement()).getName();
        }
        return names;
    }
    
}