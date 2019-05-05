package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.action.*;

import at.ac.uni_linz.tk.webstyles.*;

public class LinkPopupMenu extends JPopupMenu implements ItemListener {

    private static LinkPopupMenu popup;
    public JCheckBoxMenuItem join;
    public JCheckBoxMenuItem sequence;

    static {
        popup = new LinkPopupMenu();
    }

    private LinkPopupMenu() {
        /*
        add(ZoomInAction.getAction());
        add(ZoomOutAction.getAction());
        addSeparator();
        */
        add(PropertyAction.getAction());
        add(InstanceAction.getAction());
        add(NoInstanceAction.getAction());
        addSeparator();
        
        LinkTypeButtonGroup.getGroup().addToContainer(this);
        addSeparator();
        
        add(join = new JCheckBoxMenuItem("Join"));
        add(sequence = new JCheckBoxMenuItem("Sequence"));
        addSeparator();
        
        add(CutAction.getAction());
        add(CopyAction.getAction());
        add(PasteAction.getAction());
        addSeparator();
        
        add(DeleteAction.getAction());
        addSeparator();
        
        add(BringToFrontAction.getAction());
        add(BringToBackAction.getAction());
        
        
        join.addItemListener(this);
        sequence.addItemListener(this);
    }

    public static LinkPopupMenu getMenu() {
        return popup;
    }

    public void show(Component comp, Point pos) {
        show(comp, pos.x, pos.y);
    }
    
    public void itemStateChanged(ItemEvent e) {
        PSComponent comp = (PSComponent)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        if (comp instanceof PSLink) {
            PSLink link = (PSLink)comp;
            if (e.getItem() == join) {
                link.setJoin(join.isSelected());
            }
            else if (e.getItem() == sequence) {
                link.setSequence(sequence.isSelected());
            }
        }
    }
}