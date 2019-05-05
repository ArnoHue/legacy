package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class FanAction extends AbstractAction {

    private static FanAction action = new FanAction();

    public static Action getAction() {
        return action;
    }

    private FanAction() {
        super("Fan");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSComponent comp = (PSComponent)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        if (comp instanceof PSLink && comp.getType() != PSComponent.TYPE_FAN) {
            comp.minInstances = 0;
            comp.maxInstances = 3;
            comp.setType(PSComponent.TYPE_FAN);
        }
        AbstractUndoRedoAction.createSnapshot("Fan");
    }

}