package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class OptionalAction extends AbstractAction {

    private static OptionalAction action = new OptionalAction();

    public static Action getAction() {
        return action;
    }

    private OptionalAction() {
        super("Optional");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSComponent comp = (PSComponent)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        if (comp.getType() != PSComponent.TYPE_OPTIONAL) {
            comp.minInstances = 0;
            comp.maxInstances = 1;
            comp.setType(PSComponent.TYPE_OPTIONAL);
        }
        AbstractUndoRedoAction.createSnapshot("Optional");
    }

}