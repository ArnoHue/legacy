package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class MandatoryAction extends AbstractAction {

    private static MandatoryAction action = new MandatoryAction();

    public static Action getAction() {
        return action;
    }

    private MandatoryAction() {
        super("Mandatory");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSComponent comp = (PSComponent)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        if (comp.getType() != PSComponent.TYPE_MANDATORY) {
            comp.minInstances = 1;
            comp.maxInstances = 1;
            comp.setType(PSComponent.TYPE_MANDATORY);
        }
        AbstractUndoRedoAction.createSnapshot("Mandatory");
    }

}