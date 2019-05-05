package at.ac.uni_linz.tk.webstyles.action;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class NewAction extends AbstractAction {

    private static NewAction action = new NewAction();

    public static Action getAction() {
        return action;
    }

    private NewAction() {
        super("New", new ImageIcon("images/new.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        PSComponent.resetIds();
        WebStyles.getApplication().setController(new PSGraphController(new PSGraph()));
        ZoomAction.updateEnabledStates();
        AbstractUndoRedoAction.createSnapshot("New Graph");
    }

}