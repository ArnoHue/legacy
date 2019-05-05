package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class SequenceAction extends AbstractAction {

    private static SequenceAction action = new SequenceAction();

    public static Action getAction() {
        return action;
    }

    private SequenceAction() {
        super("Sequence");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSComponent comp = (PSComponent)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        if (comp instanceof PSNode) {
            if (comp.getType() != PSComponent.TYPE_SEQUENCE) {
                comp.minInstances = 0;
                comp.maxInstances = 3;
                comp.setType(PSComponent.TYPE_SEQUENCE);
            }
        }
        else if (comp instanceof PSLink) {
            PSLink link = (PSLink)comp;
            if (!link.isSequence()) {
                link.minInstances = 0;
                link.maxInstances = 3;
                link.setSequence(true);
            }
        }
        AbstractUndoRedoAction.createSnapshot("Sequence");
   }

}