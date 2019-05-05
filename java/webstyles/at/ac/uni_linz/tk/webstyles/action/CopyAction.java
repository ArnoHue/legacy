package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;

public class CopyAction extends AbstractAction {

    private static CopyAction action = new CopyAction();

    public static Action getAction() {
        return action;
    }

    private CopyAction() {
        super("Copy", new ImageIcon("images/copy.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSClipboard.getClipboard().copy();
        WebStyles.getApplication().updateActionStates();
    }

}