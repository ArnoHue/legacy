package at.ac.uni_linz.tk.webstyles;

import java.awt.*;
import java.util.*;
import java.beans.*;


public abstract class PSComponentView extends PSView {
    
    private boolean selected;
    private boolean highlighted;
    
    protected PSGraphView graphView;
        
    public abstract void editProperties();
    
    public abstract void copyBounds(PSComponentView view);
    public abstract boolean isContainedBy(Rectangle rec);
    
    public PSComponentView(PSComponentController controller) {
        super(controller);
    }
      
    public Point getCenter() {
        return new Point(getLocation().x + getSize().width / 2, getLocation().y + getSize().height / 2);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PSConstants.PROP_MARK) || 
            evt.getPropertyName().equals(PSConstants.PROP_TYPE) || 
            evt.getPropertyName().equals(PSConstants.PROP_SEQUENCE) || 
            evt.getPropertyName().equals(PSConstants.PROP_NAME) || 
            evt.getPropertyName().equals(PSConstants.PROP_JOIN)) {
            repaint();
            if (evt.getPropertyName().equals(PSConstants.PROP_TYPE) || evt.getPropertyName().equals(PSConstants.PROP_SEQUENCE)) {
                Enumeration linkedViews = getGraphView().getLinkedComponentViews((PSComponent)getModelInternal());
                while (linkedViews.hasMoreElements()) {
                    ((PSComponentView)linkedViews.nextElement()).repaint();
                }
                WebStyles.getApplication().updateActionStates();
            }
        }
    }
    
    public void copyLocation(PSComponentView view) {
        setLocation(view.getLocation());
    }
    
    public PSGraphView getGraphView() {
        return getParent() instanceof PSGraphView ? (PSGraphView)getParent() : graphView;
    }

    public void setParent(PSGraphView view) {
        graphView = view;
    }
    
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            repaint();
        }
    }
        
    public boolean isSelected() {
        return selected;
    }
      
    public void setHighlighted(boolean highlighted) {
        if (this.highlighted != highlighted) {
            this.highlighted = highlighted;
            repaint();
        }
    }
        
    public boolean isHighlighted() {
        return highlighted;
    }
    
    public String[] getPropertyChanges() {
        return new String[] { PSConstants.PROP_MARK, PSConstants.PROP_TYPE, PSConstants.PROP_NAME, PSConstants.PROP_JOIN, PSConstants.PROP_SEQUENCE };
    }
}