package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class BringToFrontAction extends AbstractAction {

    private static BringToFrontAction action = new BringToFrontAction();

    public static Action getAction() {
        return action;
    }

    private BringToFrontAction() {
        super("Brint to front", new ImageIcon("images/tofront.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles.getApplication().getGraphView().bringToFront(WebStyles.getApplication().getController().getComponentViews(PSComponent.SELECTED));
        AbstractUndoRedoAction.createSnapshot("Bring to front");
    }

}