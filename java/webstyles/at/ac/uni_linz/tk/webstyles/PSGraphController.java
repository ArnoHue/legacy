package at.ac.uni_linz.tk.webstyles;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.action.*;

public class PSGraphController extends PSController implements ActionListener, MouseListener, MouseMotionListener {

    public class PendingLink implements Serializable {
        public PSLinkController ctrl;
        boolean dragTarget;

        public PendingLink(PSLinkController ctrl, boolean dragTarget) {
            this.ctrl = ctrl;
            this.dragTarget = dragTarget;
            getView().setSelected(true);
        }

        public PSLink getModel() {
            return (PSLink)ctrl.getModel();
        }

        public PSLinkView getView() {
            return (PSLinkView)ctrl.getView();
        }
    }
    
    public class MyObservable extends Observable implements Serializable {
        
        public void setChanged() {
            super.setChanged();
        }
    }
    
    protected String name;
    
    private MyObservable MODE_OBSERVABLE = new MyObservable();
    private MyObservable NAME_OBSERVABLE = new MyObservable();
    
    public static final int DRAG_NONE = 0;
    public static final int DRAG_GRAPH = 1;
    public static final int DRAG_RECT = 2;
    public static final int DRAG_LINK = 3;

    public static final int MODE_EDIT = 0;
    public static final int MODE_NODE = 1;
    public static final int MODE_LINK = 2;

    public static final int MVC_MODEL = 0;
    public static final int MVC_VIEW = 1;
    public static final int MVC_CONTROLLER = 2;
    
    protected Vector subControllers = new Vector();
    protected volatile Vector removedSubControllers = new Vector();

    private volatile int dragMode = DRAG_NONE;
    private volatile int mode = MODE_EDIT;
    private volatile int oldMode = MODE_EDIT;
    private volatile PendingLink pendingLink;
    private volatile Point dragRectPos;
    protected volatile boolean nestedNodeMode = false;
    
    public PSGraphController(PSGraph model) {
        super(model);
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }
    
    public void disconnect() {
        super.disconnect();
        view.removeMouseListener(this);
        view.removeMouseMotionListener(this);
    }

    public void addObserver(Observer observer) {
        MODE_OBSERVABLE.addObserver(observer);
        NAME_OBSERVABLE.addObserver(observer);
    }
    
    public void removeObserver(Observer observer) {
        MODE_OBSERVABLE.deleteObserver(observer);
        NAME_OBSERVABLE.deleteObserver(observer);
    }
    
    public void addController(PSComponentController ctrl) {
        if (ctrl != null) {
            subControllers.addElement(ctrl);
            getView().addComponentView((PSComponentView)ctrl.getViewInternal());
        }
    }

    public void removeController(PSComponentController ctrl) {
        if (ctrl != null) {
            subControllers.removeElement(ctrl);
            removedSubControllers.addElement(ctrl);
            getView().removeComponentView((PSComponentView)ctrl.getViewInternal());
            ctrl.disconnect();
            getView().repaint();
        }
    }

    public PSGraph getModel() {
        return (PSGraph)getModelInternal();
    }

    public PSGraphView getView() {
        return (PSGraphView)getViewInternal();
    }

    public PSComponentController getController(PSComponent model) {
        for (int i = 0; i < subControllers.size(); i++) {
            PSComponentController controller = (PSComponentController)subControllers.elementAt(i);
            if (controller.getModelInternal() == model) {
                return controller;
            }
        }
        return null;
    }

    public PSComponentView getComponentView(PSComponent model) {
        PSComponentController controller = getController(model);
        if (controller != null) {
            return (PSComponentView)controller.getViewInternal();
        }
        for (int i = 0; i < removedSubControllers.size(); i++) {
            controller = (PSComponentController)removedSubControllers.elementAt(i);
            if (controller.getModelInternal() == model) {
                return (PSComponentView)controller.getViewInternal();
            }
        }
        return null;
    }

    public void actionPerformed(ActionEvent event) {
    }

    public void mouseClicked(MouseEvent evt) {
        if (!evt.isControlDown() && !evt.isShiftDown()) {
            getView().setSelection(false);
        }
        getView().setSelection(evt.getPoint());
        if (evt.isMetaDown()) {
            setEditMode(MODE_EDIT);
            PSObject selObj = getComponent(PSComponent.SELECTED);
            if (selObj instanceof PSNode) {
                NodePopupMenu.getMenu().show(getView(), evt.getPoint());
            }
            else if (selObj instanceof PSLink) {
                LinkPopupMenu.getMenu().show(getView(), evt.getPoint());
            }
            else {
                EditPopupMenu.getMenu().show(getView(), evt.getPoint());
            }
        }
    }
    
    public void mouseReleased(MouseEvent evt) {
        if (evt.isMetaDown()) {
            mouseClicked(evt);
        }
        else {
            switch(mode) {
                case MODE_EDIT:
                    if (dragMode == DRAG_NONE) {
                        getView().setSelection(false);
                        getView().setSelection(evt.getPoint());
                    }
                    else if (dragMode == DRAG_RECT) {
                        getView().setSelectionRectangle(null);
                    }
                    dragMode = DRAG_NONE;
                    break;
                case MODE_LINK:
                    if (pendingLink != null) {
                        PSObject comp;
                        PSLink pLink = pendingLink.getModel();
                        int linkDir = pendingLink.dragTarget ? PSConstants.OUT : PSConstants.IN;
                        comp = getComponent(PSComponent.HIGHLIGHTED);
                        if (comp instanceof PSNode) {
                            PSNode node = (PSNode)comp;
                            PSComponentView view = getComponentView(node);
                            if (!pLink.isLinked(node)) {
                                try {
                                    pLink.link(node, linkDir);
                                }
                                catch (PSException excpt) {
                                    excpt.display();
                                }
                                view.setHighlighted(false);
                            }
                        }
                        pendingLink = null;
                        setEditMode(oldMode);
                        AbstractUndoRedoAction.createSnapshot("Link");
                    }
                    break;
                case MODE_NODE:
                    break;
            }
        }
    }
    
    public void mouseExited(MouseEvent evt) {
    }
    
    public void mouseEntered(MouseEvent evt) {
    }
    
    public void mouseMoved(MouseEvent evt) {
    }
    
    public void mousePressed(MouseEvent evt) {
        PSObject obj;
        if (!evt.isMetaDown()) {
            switch(mode) {
                case MODE_EDIT:
                    if ((evt.getModifiers() & InputEvent.CTRL_MASK) != InputEvent.CTRL_MASK && (evt.getModifiers() & InputEvent.SHIFT_MASK) != InputEvent.SHIFT_MASK && !getView().containsSelected(evt.getPoint())) {
                        getView().setSelection(false);
                    }
                    getView().setSelection(evt.getPoint());
                    obj = getComponent(PSComponent.SELECTED);
                    if (obj instanceof PSLink) {
                        PSLink lnk = (PSLink)obj;
                        PSLinkView lnkView = (PSLinkView)getComponentView(lnk);
                        if (lnkView.getDistanceFromTargetPosition(evt.getPoint()) < 8) {
                            if (lnk.getNrOfLinkedComponents(PSConstants.OUT) == 1) {
                                lnk.unlink(lnk.getNode(PSConstants.OUT), PSConstants.OUT);
                            }
                            getView().setSelection(false);
                            lnkView.setSelected(true);
                            pendingLink = new PendingLink(lnkView.getController(), true);
                            setEditMode(MODE_LINK);
                            oldMode = MODE_EDIT;
                        }
                        else if (lnkView.getDistanceFromSourcePosition(evt.getPoint()) < 8) {
                            if (lnk.getNode(PSConstants.IN) != null) {
                                lnk.unlink(lnk.getNode(PSConstants.IN), PSConstants.IN);
                            }
                            getView().setSelection(false);
                            lnkView.setSelected(true);
                            pendingLink = new PendingLink(lnkView.getController(), false);
                            setEditMode(MODE_LINK);
                            oldMode = MODE_EDIT;
                        }
                    }
                    dragMode = getView().isAnyComponentSelected() && getView().contains(evt.getPoint()) ? DRAG_GRAPH : DRAG_RECT;
                    dragRectPos = evt.getPoint();
                    getView().setSelectionRectangle(new Rectangle(dragRectPos));
                    AbstractUndoRedoAction.createSnapshot("Edit");
                    break;
                case MODE_NODE:
                    PSNode node = nestedNodeMode ? new PSNestedGraphNode() : new PSNode();
                    getModel().addComponent(node);
                    PSNodeView view = (PSNodeView)getComponentView(node);
                    view.setPosition(new Point(evt.getPoint().x - view.getRadius(), evt.getPoint().y - view.getRadius()));
                    getView().setSelection(false);
                    view.setSelected(true);
                    // getView().repaint();
                    AbstractUndoRedoAction.createSnapshot("Add Node");
                    break;
                case MODE_LINK:
                    PSLink link;
                    PSNodeView nodeView;
                    getView().setSelection(false);
                    nodeView = getView().getNodeViewAt(evt.getPoint());
                    getModel().addComponent(link = new PSLink());
                    pendingLink = new PendingLink((PSLinkController)getController(link), true);
                    pendingLink.getView().setToPosition(evt.getPoint());
                    pendingLink.getView().setPosition(evt.getPoint());
                    if (nodeView != null) {
                        PSNode srcNode = (PSNode)nodeView.getModelInternal();
                        pendingLink.getModel().generic = srcNode.generic;
                        try {
                            srcNode.link(pendingLink.getModel(), PSConstants.OUT);
                        }
                        catch (PSException excpt) {
                            excpt.display();
                        }
                    }
                    getView().setSelection(false);
                    pendingLink.getView().setSelected(true);
                    oldMode = MODE_LINK; 
                    // AbstractUndoRedoAction.createSnapshot("Add Link");
                    break;
            }
        }
    }
    
    public void mouseDragged(MouseEvent evt) {
        switch(mode) {
            case MODE_EDIT:
                switch(dragMode) {
                    case DRAG_GRAPH:
                        getView().translate(evt.getPoint().x - dragRectPos.x, evt.getPoint().y - dragRectPos.y);
                        dragRectPos = evt.getPoint();
                        // getView().setSelectionRectangle(new Rectangle(evt.getPoint()));
                        break;
                    case DRAG_RECT:
                        Point pos1 = evt.getPoint();
                        Point pos2 = dragRectPos;
                        Point minPos = new Point(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y));
                        Point maxPos = new Point(Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y));
                        Rectangle rec = new Rectangle(minPos, new Dimension(maxPos.x - minPos.x + 1, maxPos.y - minPos.y + 1));
                        getView().setSelectionRectangle(rec);
                        getView().setSelection(getView().getSelectionRectangle());
                        break;
                }
                break;
            case MODE_LINK:
                if (pendingLink != null) {
                    getView().highlightNode(evt.getPoint());
                    if (pendingLink.dragTarget) {
                        pendingLink.getView().setToPosition(evt.getPoint());
                    }
                    else {
                        pendingLink.getView().setPosition(evt.getPoint());
                    }
                }
                break;
        }
    }
    
    public void setNestedNodeMode(boolean nestedNodeModeParam) {
        nestedNodeMode = nestedNodeModeParam;
    }
    
    public boolean getNestedNodeMode() {
        return nestedNodeMode;
    }
    
    public void setEditMode(int mode) {
        if (mode != this.mode) {
            this.mode = mode;
            MODE_OBSERVABLE.setChanged();
            MODE_OBSERVABLE.notifyObservers(new Integer(mode));
        }
    }
    
    public int getEditMode() {
        return mode;
    }

    public void setName(String name) {
        if (name != this.name) {
            this.name = name;
            NAME_OBSERVABLE.setChanged();
            NAME_OBSERVABLE.notifyObservers(name);
        }
    }
    
    public String getName() {
        return name;
    }
    
    protected int getViewId() {
        return PSViewFactory.ID_GRAPH;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PSConstants.PROP_ADD_NOTIFY)) {
            PSComponent comp = (PSComponent)evt.getNewValue();
            if (comp instanceof PSNestedGraphNode) {
                addController(new PSNestedGraphNodeController((PSNestedGraphNode)comp, this));
            }
            else if (comp instanceof PSNode) {
                addController(new PSNodeController((PSNode)comp, this));
            }
            else if (comp instanceof PSLink) {
                addController(new PSLinkController((PSLink)comp, this));
            }
            if (comp.prototype != null) {
                PSComponentView view = getComponentView(comp);
                view.copyBounds(getComponentView(comp.prototype));
                view.translate(20, 20);
                getView().bringToFront(view);
            }
        }
        else if (evt.getPropertyName().equals(PSConstants.PROP_REMOVE_NOTIFY)) {
            removeController(getController((PSComponent)evt.getOldValue()));
        }
    }

    protected String[] getPropertyChanges() {
        return new String[] { PSConstants.PROP_ADD_NOTIFY, PSConstants.PROP_REMOVE_NOTIFY };
    }

    public Vector getComponents(int context) {
        return getComponents(context, MVC_MODEL);
    }

    public Vector getComponentViews(int context) {
        return getComponents(context, MVC_VIEW);
    }

    private Vector getComponents(int context, int mvc) {
        Vector vec = new Vector(100, 100);
        for (int i = 0; i < subControllers.size(); i++) {
            PSComponentController ctrl = (PSComponentController)subControllers.elementAt(i);
            PSComponentView view = (PSComponentView)ctrl.getViewInternal();
            ctrl = (context == PSComponent.SELECTED && view.isSelected()) ||
                   (context == PSComponent.HIGHLIGHTED && view.isHighlighted()) ?
                   ctrl : null;
            if (ctrl != null) {
                switch(mvc) {
                    case MVC_MODEL:
                        vec.add(ctrl.getModelInternal());
                        break;
                    case MVC_VIEW:
                        vec.add(ctrl.getViewInternal());
                        break;
                    case MVC_CONTROLLER:
                        vec.add(ctrl.getModelInternal());
                        break;
                }
            }
        }
        return vec;
    }

    public PSComponent getComponent(int context) {
        for (int i = 0; i < subControllers.size(); i++) {
            PSComponentController ctrl = (PSComponentController)subControllers.elementAt(i);
            switch(context) {
                case PSComponent.SELECTED:
                    if (((PSComponentView)ctrl.getViewInternal()).isSelected()) {
                        return (PSComponent)ctrl.getModelInternal();
                    }
                    break;
                case PSComponent.HIGHLIGHTED:
                    if (((PSComponentView)ctrl.getViewInternal()).isHighlighted()) {
                        return (PSComponent)ctrl.getModelInternal();
                    }
                    break;
            }
        }
        return null;
    }

}