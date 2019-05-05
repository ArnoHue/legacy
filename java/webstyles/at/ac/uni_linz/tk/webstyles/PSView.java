package at.ac.uni_linz.tk.webstyles;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import java.beans.*;

public abstract class PSView extends JComponent implements PropertyChangeListener {
    
    private PSController controller;
    
    private PropertyChangeSupport propSupport = new PropertyChangeSupport(this);
    private boolean propertyChangeSupportEnabled = true;
    /*
    protected Rectangle dirtyBounds = new Rectangle();
    
    public void reshape(int x, int y, int width, int height) {
        dirtyBounds = getBounds();
        dirtyBounds.grow(100, 100);
        super.reshape(x, y, width, height);
        if (getParent() != null) {
            getParent().repaint(dirtyBounds.x, dirtyBounds.y, dirtyBounds.width, dirtyBounds.height);
            // getParent().repaint();
        }
    }

    protected void paintChildren(Graphics g) {
        synchronized(getTreeLock()) {
            for (int i = getComponentCount() - 1 ; i >= 0 ; i--) {
                Component comp = getComponent(i);
                if (comp != null && comp.isVisible() && g.getClip().intersects(comp.getBounds())) {
                    g.translate(comp.getLocation().x, comp.getLocation().y);
                    comp.paint(g);
                    g.translate(-comp.getLocation().x, -comp.getLocation().y);
                }
            }
        }
    }
    */
    public abstract void translate(int x, int y);

    public abstract void zoom(double factor);
      
    public PSView(PSController controller) {
        this.controller = controller;
    }
    
    public PSController getControllerInternal() {
        return controller;
    }
    
    public PSObject getModelInternal() {
        return controller.getModelInternal();
    }
      
    public boolean contains(Point pos) {
        return getBounds().contains(pos);
    }
    
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
    
    public void propertyChange(PropertyChangeEvent evt) {
    }
    
    public String[] getPropertyChanges() {
        return new String[] { PSConstants.PROP_MARK, PSConstants.PROP_TYPE };
    }

}