package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;

public class CutAction extends AbstractAction {

    private static CutAction action = new CutAction();

    public static Action getAction() {
        return action;
    }

    private CutAction() {
        super("Cut", new ImageIcon("images/cut.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSClipboard.getClipboard().cut();
        WebStyles.getApplication().updateActionStates();
        AbstractUndoRedoAction.createSnapshot("Cut");
    }

}