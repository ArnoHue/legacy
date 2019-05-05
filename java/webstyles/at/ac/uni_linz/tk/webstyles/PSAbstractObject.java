package at.ac.uni_linz.tk.webstyles;

import java.io.*;
import java.beans.*;

public abstract class PSAbstractObject implements PropertyChangeListener, VetoableChangeListener, Serializable {
    
    private PropertyChangeSupport propSupport = new PropertyChangeSupport(this);
    private VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);
    private boolean propertyChangeSupportEnabled = true;
    private boolean vetoChangeSupportEnabled = true;
    
    public void setPropertyChangeSupportEnabled(boolean propertyChangeSupportEnabled) {
        this.propertyChangeSupportEnabled = propertyChangeSupportEnabled;
    }
      
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propSupport.addPropertyChangeListener(propertyName, listener);
    }
      
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyChangeSupportEnabled) {
            propSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
      
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propSupport.removePropertyChangeListener(propertyName, listener);
    }
      
    public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoSupport.addVetoableChangeListener(listener);
    }
      
    public void fireVetoableChange(String propertyName,
                               Object oldValue,
                               Object newValue) throws PropertyVetoException {
        vetoSupport.fireVetoableChange(propertyName, oldValue, newValue);
    }
      
    public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoSupport.removeVetoableChangeListener(listener);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
    }
    
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    }

}