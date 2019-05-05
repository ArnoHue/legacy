package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class DeleteAction extends AbstractAction {

    private static DeleteAction action = new DeleteAction();

    public static Action getAction() {
        return action;
    }

    private DeleteAction() {
        super("Delete", new ImageIcon("images/delete.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        Vector content = PSClipboard.getClipboard().getContent();
        PSClipboard.getClipboard().cut();
        PSClipboard.getClipboard().setContent(content);
        WebStyles.getApplication().updateActionStates();
        AbstractUndoRedoAction.createSnapshot("Delete");
    }

}