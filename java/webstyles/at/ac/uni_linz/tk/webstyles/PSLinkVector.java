package at.ac.uni_linz.tk.webstyles;

import java.util.*;
import java.io.*;

public class PSLinkVector implements Serializable {
    
    private Vector vector;
    
    public class PSLinkEnumeration implements Enumeration {
        PSLinkVector vector;
        int count;

        PSLinkEnumeration(PSLinkVector v) {
	        vector = v;
	        count = 0;
        }

        public boolean hasMoreElements() {
	        return count < vector.size();
        }

        public Object nextElement() {
	        synchronized (vector) {
	            if (count < vector.size()) {
		            return vector.elementAt(count++);
	            }
	        }
	        throw new NoSuchElementException("PSLinkVectorEnumerator");
        }
        
        public PSLink nextLink() {
            return (PSLink)nextElement();
        }
    }
    
    public PSLinkVector() {
        this(100);
    }
        
    public PSLinkVector(int initialCapacity) {
        vector = new Vector(initialCapacity);
    }
        
    public void addAll(PSLinkVector vec) {
        vector.addAll(vec.vector);
    }
        
    public void addElement(PSLink link) {
        vector.addElement(link);
    }
    
    public boolean contains(PSLink link) {
        return vector.contains(link);
    }

    public PSLink elementAt(int index) {
        return (PSLink)vector.elementAt(index);
    }

    public PSLinkEnumeration elements() {
        return new PSLinkEnumeration(this);
    }
    
    public void insertElementAt(PSLink link, int index) {
        vector.insertElementAt(link, index);
    }
    
    public boolean removeElement(PSLink link) {
        return vector.removeElement(link);
    }

    public int size() {
        return vector.size();
    }
    
}