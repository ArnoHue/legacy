package at.ac.uni_linz.tk.webstyles;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import java.beans.*;

public abstract class PSObject extends PSAbstractObject implements PSConstants, Serializable, Cloneable {
      
    public int id;
    protected String name;
      
    public Properties properties;
    protected PSObject genericObj;

    public PSObject() {
        id = 0;
        name = new String("");
        properties = new Properties();
    }
    
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        firePropertyChange(PROP_NAME, oldName, name);
    }
    
    public String getName() {
        return name;
    }
    
    public String getProperty(String key) {
        return properties.get(key).toString();
    }
      
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
      
    public void setProperties(Properties propertiesParam) {
        properties = propertiesParam;
    }

    public Object clone() {
        PSObject obj = null;
        try {
            obj = (PSObject)super.clone();
        }
        catch (CloneNotSupportedException excpt) {
            return null;
        }
        return obj;
    }

}