package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;


public class RedoAction extends AbstractUndoRedoAction {

    private static RedoAction action = new RedoAction();
    
    public static RedoAction getAction() {
        return action;
    }

    private RedoAction() {
        super("Redo", new ImageIcon("images/redo.gif"));
        snapshotBuffer.addObserver(this);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(snapshotBuffer.moveToNextSnapshot().buffer));
            WebStyles.getApplication().setController((PSGraphController)is.readObject());
            is.close();
        }
        catch (Exception excpt) {
            JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), excpt.toString(), "Undo Error", JOptionPane.ERROR_MESSAGE, null);
        }
    }
    
    public void update(Observable o, Object arg) {
        if (o == snapshotBuffer) {
            if (snapshotBuffer.getIndex() + 1 < snapshotBuffer.getSize()) {
                putValue(Action.NAME, "Redo " + snapshotBuffer.getNextSnapshotName());
                setEnabled(true);
            }
            else {
                putValue(Action.NAME, "Redo");
                setEnabled(false);
            }
        }
    }

}