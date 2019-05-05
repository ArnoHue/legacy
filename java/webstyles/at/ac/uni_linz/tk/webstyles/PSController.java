package at.ac.uni_linz.tk.webstyles;

import java.beans.*;
import java.io.*;

public abstract class PSController implements Serializable, PSConstants, PropertyChangeListener {
    
    protected PSObject model;
    protected PSView view;
    
    public PSController(PSObject model) {
        String[] str;
        this.model = model;
        view = PSViewFactory.createView(this, getViewId());
        
        str = getPropertyChanges();
        for (int i = 0; i < str.length; i++) {
            model.addPropertyChangeListener(str[i], this);
        }
        
        str = view.getPropertyChanges();
        for (int i = 0; i < str.length; i++) {
            model.addPropertyChangeListener(str[i], view);
        }
    }
    
        
    public void disconnect() {
        String[] str;
        str = getPropertyChanges();
        for (int i = 0; i < str.length; i++) {
            model.removePropertyChangeListener(str[i], this);
        }
        str = view.getPropertyChanges();
        for (int i = 0; i < str.length; i++) {
            model.removePropertyChangeListener(str[i], view);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
    }    
    
    protected PSObject getModelInternal() {
        return model;
    }
    
    protected PSView getViewInternal() {
        return view;
    }
    
    protected abstract int getViewId();
    
    protected abstract String[] getPropertyChanges();

}