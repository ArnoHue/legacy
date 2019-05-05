package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;


public class UndoAction extends AbstractUndoRedoAction {

    private static UndoAction action = new UndoAction();
    
    public static UndoAction getAction() {
        return action;
    }

    private UndoAction() {
        super("Undo", new ImageIcon("images/undo.gif"));
        snapshotBuffer.addObserver(this);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(snapshotBuffer.moveToPreviousSnapshot().buffer));
            WebStyles.getApplication().setController((PSGraphController)is.readObject());
            is.close();
        }
        catch (Exception excpt) {
            excpt.printStackTrace();
            JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), excpt.toString(), "Undo Error", JOptionPane.ERROR_MESSAGE, null);
        }
    }
    
    public void update(Observable o, Object arg) {
        if (o == snapshotBuffer) {
            if (snapshotBuffer.getIndex() > 0) {
                putValue(Action.NAME, "Undo " + snapshotBuffer.getSnapshotName());
                setEnabled(true);
            }
            else {
                putValue(Action.NAME, "Undo");
                setEnabled(false);
            }
        }
    }

}