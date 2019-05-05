package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class BringToBackAction extends AbstractAction {

    private static BringToBackAction action = new BringToBackAction();

    public static Action getAction() {
        return action;
    }

    private BringToBackAction() {
        super("Brint to back", new ImageIcon("images/toback.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles.getApplication().getGraphView().bringToBack(WebStyles.getApplication().getController().getComponentViews(PSComponent.SELECTED));
        AbstractUndoRedoAction.createSnapshot("Bring to back");
    }

}