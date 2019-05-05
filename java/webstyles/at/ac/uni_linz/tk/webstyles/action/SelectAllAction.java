package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class SelectAllAction extends AbstractAction {

    private static SelectAllAction action = new SelectAllAction();

    public static Action getAction() {
        return action;
    }

    private SelectAllAction() {
        super("Select all");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles.getApplication().getGraphView().setSelection(true);
    }

}