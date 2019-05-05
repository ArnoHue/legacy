package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;

public class PasteAction extends AbstractAction {

    private static PasteAction action = new PasteAction();

    public static Action getAction() {
        return action;
    }

    private PasteAction() {
        super("Paste", new ImageIcon("images/paste.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSClipboard.getClipboard().paste();
        WebStyles.getApplication().updateActionStates();
        AbstractUndoRedoAction.createSnapshot("Paste");
    }

}