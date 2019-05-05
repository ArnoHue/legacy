package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class EditPopupMenu extends JPopupMenu {

    private static EditPopupMenu popup;

    static {
        popup = new EditPopupMenu();
    }

    private EditPopupMenu() {
        EditModeButtonGroup.createRadioGroup().addToContainer(this);
        addSeparator();
        add(ZoomInAction.getAction());
        add(ZoomOutAction.getAction());
    }

    public static EditPopupMenu getMenu() {
        return popup;
    }

    public void show(Component comp, Point pos) {
        show(comp, pos.x, pos.y);
    }

}