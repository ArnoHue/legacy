package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class PropertyAction extends AbstractAction {

    private static PropertyAction action = new PropertyAction();

    public static Action getAction() {
        return action;
    }

    private PropertyAction() {
        super("Properties", new ImageIcon("images/properties.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSComponent comp = WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        WebStyles.getApplication().getController().getComponentView(comp).editProperties();
        AbstractUndoRedoAction.createSnapshot("Properties");
    }

}