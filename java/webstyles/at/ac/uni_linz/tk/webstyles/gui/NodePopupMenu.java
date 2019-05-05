package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class NodePopupMenu extends JPopupMenu implements PopupMenuListener {

    private static NodePopupMenu popup;
    private boolean lock;

    static {
        popup = new NodePopupMenu();
    }

    private NodePopupMenu() {
        /*
        addSeparator();
        add(ZoomInAction.getAction());
        add(ZoomOutAction.getAction());
        */
        add(PropertyAction.getAction());
        add(InstanceAction.getAction());
        add(NoInstanceAction.getAction());
        addSeparator();
        
        NodeTypeButtonGroup.getGroup().addToContainer(this);
        addSeparator();
        
        add(ExpandNestedGraphNodeAction.getAction());
        addSeparator();
        
        add(CutAction.getAction());
        add(CopyAction.getAction());
        add(PasteAction.getAction());
        addSeparator();
        
        add(DeleteAction.getAction());
        addSeparator();
        
        add(BringToFrontAction.getAction());
        add(BringToBackAction.getAction());

        addPopupMenuListener(this);
    }

    public static NodePopupMenu getMenu() {
        return popup;
    }

    public void show(Component comp, Point pos) {
        show(comp, pos.x, pos.y);
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
        // System.out.println("popupMenuCanceled");
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // System.out.println("popupMenuWillBecomeInvisible");
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // System.out.println("popupMenuWillBecomeVisible");
    }

}