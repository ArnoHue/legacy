package at.ac.uni_linz.tk.webstyles;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.util.*;


public class PSGraphView extends PSView implements PSConstants {
    
    public static final int MODE_TRANSLATE_SELECTED = 0;
    public static final int MODE_TRANSLATE_ALL = 1;
    
    protected Rectangle selRect;
    protected TransparentPanel transparentPanel;
    protected Vector metaSpaceViews;
    
    public MyObservable SELECTION_OBSERVABLE = new MyObservable();
    public MyObservable HIGHLIGHTING_OBSERVABLE = new MyObservable();
    public MyObservable COMPONENT_ADD_OBSERVABLE = new MyObservable();
    public MyObservable COMPONENT_REMOVE_OBSERVABLE = new MyObservable();
    public MyObservable SELECTION_RECTANGLE_OBSERVABLE = new MyObservable();
    
    private double zoomFactor = 1.0;
    private boolean debugMode = false;
    
    public class MyObservable extends Observable implements Serializable {
        
        public void setChanged() {
            super.setChanged();
        }
    }
    
    public class TransparentPanel extends JComponent {
        
        PSGraphView view;
        
        public TransparentPanel(PSGraphView view) {
            this.view = view;
            setOpaque(false);
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            if (view.selRect != null) {
                g.setColor(Color.blue);
                g.drawRect(view.selRect.x, view.selRect.y, view.selRect.width, view.selRect.height);
            }
        }
        
    }
    
    public void setDebugMode(boolean debugModeParam) {
        debugMode = debugModeParam;
        repaint();
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public PSGraphView(PSGraphController controller) {
        super(controller);
        selRect = null;
        setLayout(null);
        transparentPanel = new TransparentPanel(this);
        add(transparentPanel);
        metaSpaceViews = new Vector();
    }
    
    public void repaint(long tm, int x, int y, int width, int height) {
	    if (debugMode) {
	        super.repaint(0, 0, 0, getWidth(), getHeight());
	    }
	    else {
	        super.repaint(tm, x, y, width, height);
	    }
    }
    public void paint(Graphics g) {
	    g.setColor(Color.white);
	    g.fillRect(0, 0, getWidth(),getHeight());
	    if (debugMode) {
	        for (int i = 0; i < metaSpaceViews.size(); i++) {
	            PSComponentView view = (PSComponentView)metaSpaceViews.elementAt(i);
		        Rectangle cr = view.getBounds();
			    Graphics cg = g.create(cr.x, cr.y, cr.width, cr.height);
			    try {
	                view.setParent(this);
			        view.paint(cg);
	                view.setParent(null);
			    } 
			    finally {
			        cg.dispose();
			    }
	        }
	    }
        super.paint(g);
	    if (debugMode) {
	        // TODO: fix NxN
	        g.setColor(Color.blue);
            for (int i = 0; i < getComponentCount() - 1; i++) {
                Component comp = getComponents()[i];
                if (comp instanceof PSComponentView) {
                    PSComponentView compView = (PSComponentView)comp;
                    PSComponent model = (PSComponent)compView.getModelInternal();
                    for (int j = 0; j < getComponentCount() - 1; j++) {
                        Component metaComp = getComponents()[j];
                        if (comp instanceof PSComponentView) {
                            PSComponentView metaView = (PSComponentView)metaComp;
                            if (metaView.getModelInternal() == model.getGenericRoot()) {
                                GraphicTools.paintSimpleArrow(g, compView.getCenter(), metaView.getCenter(), false);
                                break;
                            }
                        }
                    }
                    for (int k = 0; k < metaSpaceViews.size(); k++) {
                        PSComponentView metaView = (PSComponentView)metaSpaceViews.elementAt(k);
                        if (metaView.getModelInternal() == model.getGenericRoot()) {
                            GraphicTools.paintSimpleArrow(g, compView.getCenter(), metaView.getCenter(), false);
                            break;
                        }
                    }
                }
            }
	    }
    }   
    
    public void addObserver(Observer observer) {
        SELECTION_OBSERVABLE.addObserver(observer);
        HIGHLIGHTING_OBSERVABLE.addObserver(observer);
        COMPONENT_ADD_OBSERVABLE.addObserver(observer);
        COMPONENT_REMOVE_OBSERVABLE.addObserver(observer);
        SELECTION_RECTANGLE_OBSERVABLE.addObserver(observer);
    }
    
    public void removeObserver(Observer observer) {
        SELECTION_OBSERVABLE.deleteObserver(observer);
        HIGHLIGHTING_OBSERVABLE.deleteObserver(observer);
        COMPONENT_ADD_OBSERVABLE.deleteObserver(observer);
        COMPONENT_REMOVE_OBSERVABLE.deleteObserver(observer);
        SELECTION_RECTANGLE_OBSERVABLE.deleteObserver(observer);
    }
    
    public void addMouseListener(MouseListener listener) {
        transparentPanel.addMouseListener(listener);
    }
    
    public void removeMouseListener(MouseListener listener) {
        transparentPanel.removeMouseListener(listener);
    }
    
    public void addMouseMotionListener(MouseMotionListener listener) {
        transparentPanel.addMouseMotionListener(listener);
    }
    
    public void removeMouseMotionListener(MouseMotionListener listener) {
        transparentPanel.removeMouseMotionListener(listener);
    }
    
    public void setSize(Dimension dim) {
        super.setSize(dim);
        transparentPanel.setSize(dim);
    }
    
    public void setSelectionRectangle(Rectangle selRect) {
        if (selRect == null || !selRect.equals(this.selRect)) {
            this.selRect = selRect;
            repaint();
            SELECTION_RECTANGLE_OBSERVABLE.setChanged();
            SELECTION_RECTANGLE_OBSERVABLE.notifyObservers(this);
        }
    }
    
    public Rectangle getSelectionRectangle() {
        return selRect;
    }
    
    public PSGraph getModel() {
        return (PSGraph)getModelInternal();
    }
    
    public PSComponentView getComponentView(PSComponent component) {
        // TODO: fast access (TreeMap?)
        for (int i = 0; i < getComponentCount() - 1; i++) {
            Component comp = getComponents()[i];
            if (comp instanceof PSComponentView) {
                PSComponentView compView = (PSComponentView)comp;
                if (compView.getModelInternal() == component) {
                    return compView;
                }
            }
        }
	    if (debugMode) {
            for (int i = 0; i < metaSpaceViews.size(); i++) {
                PSComponentView compView = (PSComponentView)metaSpaceViews.elementAt(i);
                if (compView.getModelInternal() == component) {
                    return compView;
                }
            }
        }
        return null;
    }
    
    public Enumeration getComponentViews(Vector models) {
        // TODO: fast access (TreeMap?)
        Vector views = new Vector(models.size());
        for (int i = 0; i < getComponentCount() - 1; i++) {
            Component comp = getComponents()[i];
            if (comp instanceof PSComponentView) {
                PSComponentView compView = (PSComponentView)comp;
                if (models.contains(compView.getModelInternal())) {
                    views.add(compView);
                }
            }
        }
        return views.elements();
    }
    
    public Enumeration getComponentViews() {
        // TODO: fast access (TreeMap?)
        Vector views = new Vector();
        for (int i = 0; i < getComponentCount() - 1; i++) {
            Component comp = getComponents()[i];
            if (comp instanceof PSComponentView) {
                views.add(comp);
            }
        }
        return views.elements();
    }
    
    public Enumeration getLinkedComponentViews(PSComponent component) {
        Vector vec = new Vector(component.getNrOfLinkedComponents(PSConstants.IN) + component.getNrOfLinkedComponents(PSConstants.OUT));
        for (int i = 0; i < getComponentCount() - 1; i++) {
            Component compView = getComponents()[i];
            if (compView instanceof PSComponentView &&
               component.isLinked((PSComponent)((PSComponentView)compView).getModelInternal())) {
                vec.add(compView);
            }
        }
        return vec.elements();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PSConstants.PROP_BOUNDS)) {
            adjustViewport();
        }
    }
    
    public void addComponentView(PSComponentView view) {
        add(view, 0);
        view.addPropertyChangeListener(PSConstants.PROP_BOUNDS, this);
        COMPONENT_ADD_OBSERVABLE.setChanged();
        COMPONENT_ADD_OBSERVABLE.notifyObservers(this);
    }
    
    public void removeComponentView(PSComponentView view) {
        remove(view);
        view.removePropertyChangeListener(PSConstants.PROP_BOUNDS, this);
        view.setSelected(false);
        if (((PSComponent)view.getModelInternal()).isInMetaSpace) {
            metaSpaceViews.add(view);
        }
        COMPONENT_REMOVE_OBSERVABLE.setChanged();
        COMPONENT_REMOVE_OBSERVABLE.notifyObservers(this);
    }
    
    public PSGraphController getController() {
        return (PSGraphController)getControllerInternal();
    }
    
    public void adjustViewport() {
        Rectangle rec = new Rectangle(0, 0, 0, 0);
        for (int i = 0; i < getComponents().length - 1; i++) {
            rec = rec.union(((PSComponentView)getComponents()[i]).getBounds());
        }
        if (rec.width != getBounds().width || rec.height != getBounds().height) {
            setPreferredSize(new Dimension(rec.width, rec.height));
            revalidate();
        }
    }

    public boolean containsSelected(Point position) {
        for (int i = 0; i < getComponentCount() - 1; i++) {
            Component comp = getComponents()[i];
            if (comp instanceof PSComponentView) {
                PSComponentView view = (PSComponentView)comp;
                if (view.isSelected() && view.contains(position)) {
                    return true;
                }
            }
        }
        return false;
    }

    public PSNodeView getNodeViewAt(Point position) {
        for (int i = 0; i < getComponentCount() - 1; i++) {
            Component comp = getComponents()[i];
            if (comp instanceof PSNodeView && comp.contains(position)) {
                return (PSNodeView)comp;
            }
        }
        return null;
    }
    
    public void setSelection(boolean selected) {
        setSelection(getBounds(), selected);
    }
    
    public void setSelection(Point pos) {
        for (int i = 0; i < getComponentCount() - 1; i++) {
            PSComponentView compView = (PSComponentView)getComponents()[i];
            if (compView.contains(pos)) {
                if (!compView.isSelected()) {
                    compView.setSelected(true);
                    SELECTION_OBSERVABLE.setChanged();
                    SELECTION_OBSERVABLE.notifyObservers(this);
                }
                return;
            }
        }
    }
    
    public void setSelection(Rectangle rec) {
        setSelection(rec, true);
    }
    
    private void setSelection(Rectangle rec, boolean selected) {
        boolean selChanged = false;
        for (int i = 0; i < getComponentCount() - 1; i++) {
            PSComponentView compView = (PSComponentView)getComponents()[i];
            boolean newSelected = compView.isContainedBy(rec) ? selected : !selected;
            if (compView.isSelected() != newSelected) {
                selChanged = true;
                compView.setSelected(compView.isContainedBy(rec) ? selected : !selected);
            }
        }
        if (selChanged) {
            SELECTION_OBSERVABLE.setChanged();
            SELECTION_OBSERVABLE.notifyObservers(this);
        }
    }
    
    public void highlightNode(Point pos) {
        boolean highlightedChanged = false;
        for (int i = 0; i < getComponentCount() - 1; i++) {
            if (getComponents()[i] instanceof PSNodeView) {
                PSNodeView nodeView = (PSNodeView)getComponents()[i];
                boolean newHighlighted = nodeView.contains(pos);
                if (newHighlighted != nodeView.isHighlighted()) {
                    highlightedChanged = true;
                    nodeView.setHighlighted(newHighlighted);
                }
            }   
        }
        if (highlightedChanged) {
            HIGHLIGHTING_OBSERVABLE.setChanged();
            HIGHLIGHTING_OBSERVABLE.notifyObservers(this);
        }
    }        
    
    public void translate(int x, int y) {
        translate(x, y, MODE_TRANSLATE_SELECTED);
    }
      
    public void translate(int x, int y, int mode) {
        for (int i = 0; i < getComponentCount() - 1; i++) {
            if (getComponents()[i] instanceof PSNodeView) {
                PSNodeView node = (PSNodeView)getComponents()[i];
                if (mode == MODE_TRANSLATE_ALL || node.isSelected()) {
                    node.translate(x, y);
                }
            }
        }
    }
    
    public void zoom(double factor) {
        zoomFactor *= factor;
        setPropertyChangeSupportEnabled(false);
        for (int i = 0; i < getComponents().length - 1; i++) {
            PSComponentView comp = (PSComponentView)getComponents()[i];
            comp.zoom(factor);
        }
        setPropertyChangeSupportEnabled(true);
        adjustViewport();
    }
    
    
    public double getZoomFactor() {
        return zoomFactor;
    }
    
    public void bringToFront(Vector vec) {
        for (int i = 0; i < vec.size(); i++) {
            Object obj = vec.elementAt(i);
            if (obj instanceof PSComponentView) {
                bringToFront((PSComponentView)obj);
            }
        }
    }
    
    public void bringToBack(Vector vec) {
        for (int i = 0; i < vec.size(); i++) {
            Object obj = vec.elementAt(i);
            if (obj instanceof PSComponentView) {
                bringToBack((PSComponentView)obj);
            }
        }
    }
   
    public void bringToBack(PSComponentView object) {
        remove(object);
        add(object, getComponentCount() - 1);
        object.repaint();
    }
    
    public void bringToFront(PSComponentView object) {
        remove(object);
        add(object,0);
        object.repaint();
    }
  
    public boolean isAnyComponentSelected() {
        return getController().getComponent(PSComponent.SELECTED) != null;
    }
    
}