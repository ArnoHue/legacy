package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class InstanceAction extends AbstractAction {

    private static InstanceAction action = new InstanceAction();

    public static Action getAction() {
        return action;
    }

    private InstanceAction() {
        super("Instantiate", new ImageIcon("images/instance.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            ((PSComponent)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED)).instantiate();
            AbstractUndoRedoAction.createSnapshot("Instantiate");
        }
        catch (PSException excpt) {
            excpt.display();
        }
    }

}