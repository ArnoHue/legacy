package at.ac.uni_linz.tk.webstyles;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import java.beans.*;
import java.util.*;

public abstract class PSComponent extends PSObject {
        
    public static final int TYPE_MANDATORY = 0;
    public static final int TYPE_OPTIONAL = 1;
    public static final int TYPE_SEQUENCE = 2;
    public static final int TYPE_FAN = 3;
    
    public static final String TYPE_MANDATORY_STRING = new String("Obligatory");
    public static final String TYPE_OPTIONAL_STRING = new String("Optional");
    public static final String TYPE_SEQUENCE_STRING = new String("Sequence");
    public static final String TYPE_FAN_STRING = new String("Fan");
    
    public static final int MARK_NONE = 0;
    public static final int MARK_FAN = 1;
    public static final int MARK_SEQUENCE = 2;
    public static final int MARK_JOIN = 3;
    public static final int MARK_DUPLICATE = 4;
    public static final int MARK_METASPACE = 5;
    
    public static final int INST_NONE = 0;
    public static final int INST_INST = 1;
    public static final int INST_IGNORE = 2;
      
    public boolean generic;
    public boolean pseudoGeneric;
    public int type;
      
    public int minInstances;
    public int maxInstances;
    public int nrInstances;
    public int indInstances;
      
    public PSComponent genericComp;
    public PSComponent firstInstanceComp, lastInstanceComp;
    public PSComponent prototype;
    
    public int mark;
    public int instMode;
    
    public boolean rejectInstantiation = false;
    public boolean isInMetaSpace = false;
    
    private static Hashtable idTable;
    protected PSGraph graph;
    
    public abstract void isLinkableWith(PSComponent comp, int dir) throws PSNotLinkableException;
    
    public abstract Enumeration getLinkedComponents(int dir);
    public abstract void link(PSComponent comp, int dir) throws PSNotLinkableException;
    public abstract boolean isLinked(PSComponent comp, int dir);
    public abstract boolean isLinked(PSComponent comp);
    public abstract void unlinkAll();
    public abstract void unlink(PSComponent comp, int dir);
    public abstract void moveLinksToMetaSpace();
    
    public abstract void instantiate() throws PSNotInstantiableException, PSNotLinkableException;
    public abstract void instantiateTrace(PSComponent prevComp, int direction, InstantiationMode mode) throws PSNotInstantiableException, PSNotLinkableException;
    public abstract void dontInstantiate() throws PSNotLinkableException;
    public abstract int getNrOfLinkedComponents(int dir);
    
    public static class InstantiationMode {
    }
    
    public static final InstantiationMode INST_MODE_DEFAULT = new InstantiationMode();
    public static final InstantiationMode INST_MODE_LAST_TRACE = new InstantiationMode();
    public static final InstantiationMode INST_MODE_NO_INSTANTIATION = new InstantiationMode();
    
    static {
        idTable = new Hashtable();
    }
    
    public static String getTypeString(int typeId) {
        switch(typeId) {
            case TYPE_MANDATORY:
                return TYPE_MANDATORY_STRING;
            case TYPE_OPTIONAL:
                return TYPE_OPTIONAL_STRING;
            case TYPE_SEQUENCE:
                return TYPE_SEQUENCE_STRING;
            case TYPE_FAN:
                return TYPE_FAN_STRING;
        }
        return null;
    }
    
    public static int getTypeId(String typeStr) {
        if (TYPE_MANDATORY_STRING.equals(typeStr)) {
            return TYPE_MANDATORY;
        }
        else if (TYPE_OPTIONAL_STRING.equals(typeStr)) {
            return TYPE_OPTIONAL;
        }
        else if (TYPE_SEQUENCE_STRING.equals(typeStr)) {
            return TYPE_SEQUENCE;
        }
        else if (TYPE_FAN_STRING.equals(typeStr)) {
            return TYPE_FAN;
        }
        return -1;
    }
    
    public PSComponent() {
        // this.graph = graph;
        id = createId();
        generic = true;
        pseudoGeneric = false;
        minInstances = 1;
        maxInstances = 1;
        mark = MARK_NONE;
    }
    
    public void setGraph(PSGraph graphParam) {
        graph = graphParam;
    }
    
    public PSGraph getGraph() {
        return graph;
    }
    
    public static void resetIds() {
        synchronized(idTable) {
            idTable.clear();
        }
    }
    
    protected int createId() {
        synchronized(idTable) {
            Integer uniqueId;
            if (idTable.containsKey(getClass().getName())) {
                uniqueId = (Integer)idTable.get(getClass().getName());
            }
            else {
                uniqueId = new Integer(0);
            }
            uniqueId = new Integer(uniqueId.intValue() + 1);
            idTable.put(getClass().getName(), uniqueId);
            return uniqueId.intValue();
        }
    }
    
    public void setMark(int mark) {
        if (generic) {
            int oldMark = this.mark;
            this.mark = mark;
            firePropertyChange(PROP_MARK, new Integer(oldMark), new Integer(mark));
        }
    }
    
    public int getMark() {
        return mark;
    }
    
    public int getInverseDir(int dir) {
        if (dir == IN) {
            return OUT;
        }
        else if (dir == OUT) {
            return IN;
        }
        else {
            return UNKNOWN;
        }
    }
      
    public void setType(int type) {
        int oldType = this.type;
        this.type = type;
        firePropertyChange(PROP_TYPE, new Integer(oldType), new Integer(type));
    }
      
    public int getType() {
        return type;
    }
      
    public String getProperty(String key) {
        Object prop = properties.get(key);
        return prop != null ? prop.toString() : null;
    }
      
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
      
    public void setProperties(Properties propertiesParam) {
        properties = propertiesParam;
    }
      
    public Hashtable getProperties() {
        return properties;
    }
    
    public Object clone() {
        PSComponent obj = null;
        obj = (PSComponent)super.clone();
        obj.prototype = this;
        return obj;
    }
    
    public boolean isInstantiableForUser() {
        try {
            isInstantiable();
            return nrInstances < maxInstances;
        }
        catch (PSNotInstantiableException e) {
            return false;
        }
    }
    
    public void isInstantiable() throws PSNotInstantiableException {
        if (!generic) {
            throw new PSNotInstantiableException(PSNotInstantiableException.REASON_NOT_GENERIC, this);
        }
    }
    
    public PSComponent getGenericRoot() {
        return genericComp == null ? this : genericComp.getGenericRoot();
    }
    
    public PSComponent instantiateInternal() throws PSNotInstantiableException, PSNotLinkableException {
        return cloneInternal(CLONE_MODE_INSTANCE);
    }
    public static final int CLONE_MODE_INSTANCE = 0;
    public static final int CLONE_MODE_PSEUDOGENERIC = 1;
    public static final int CLONE_MODE_DUPLICATE = 2;
    
    public PSComponent duplicate() throws PSNotInstantiableException, PSNotLinkableException {
        return cloneInternal(CLONE_MODE_DUPLICATE);
    }
    
    public PSComponent createPseudoGeneric() throws PSNotInstantiableException, PSNotLinkableException {
        return cloneInternal(CLONE_MODE_PSEUDOGENERIC);
    }
    
    protected int nrDuplicates = 0;
    
    private PSComponent cloneInternal(int cloneMode) throws PSNotInstantiableException, PSNotLinkableException {
        PSComponent comp;
        
        isInstantiable();
        comp = (PSComponent)clone();
        comp.name = name;
        if (nrInstances == 0) {
            firstInstanceComp = comp;
        }
        comp.lastInstanceComp = null;
        comp.genericComp = this;
        comp.mark = MARK_NONE;
        comp.nrInstances = 0;
        comp.nrDuplicates = 0;
        
        comp.generic = cloneMode != CLONE_MODE_INSTANCE;
        comp.pseudoGeneric = cloneMode == CLONE_MODE_PSEUDOGENERIC;
        
        // TODO: fix this for Trace
        if (cloneMode == CLONE_MODE_DUPLICATE || cloneMode == CLONE_MODE_PSEUDOGENERIC) {
            nrDuplicates++;
            comp.name += "_dup_" + nrDuplicates;
        }
        else if (cloneMode == CLONE_MODE_INSTANCE) {
            lastInstanceComp = comp;
            nrInstances++;
            if (nrInstances > 1) {
                comp.name += "_inst_" + nrInstances;
            }
            /*
            if (nrInstances == maxInstances) {
                graph.removeComponent(this);
            }
            */
            firePropertyChange(PROP_INSTANTIATION_NOTIFY, this, comp);
        }
        return comp;
    }
    
    public void moveToMetaSpace() {
        isInMetaSpace = true;
        moveLinksToMetaSpace();
        graph.removeComponent(this);
    }
    
    public void relink(PSComponent comp, PSComponent oldComp, int dir) throws PSNotLinkableException {
        unlink(oldComp, dir);
        link(comp, dir);
    }
    
    public void relinkAll(PSComponent oldComp, int dir) throws PSNotLinkableException {
        Vector vec;
        Enumeration enum = oldComp.getLinkedComponents(dir);
        for (vec = new Vector(); enum.hasMoreElements(); vec.addElement(enum.nextElement()));
        for (int i = 0; i < vec.size(); i++) {
            relink((PSComponent)vec.elementAt(i), oldComp, dir);
        }
    }
   
    public String toString() {
        return name + ", " + getTypeString(type);
    }
  
}