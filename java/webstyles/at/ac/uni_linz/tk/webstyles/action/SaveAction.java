package at.ac.uni_linz.tk.webstyles.action;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

import at.ac.uni_linz.tk.webstyles.*;


public class SaveAction extends AbstractSaveAction {
    
    private static SaveAction saveAction;
    
    static {
        saveAction = new SaveAction();
    }

    public static SaveAction getAction() {
        return saveAction;
    }

    private SaveAction() {
        super("Save", new ImageIcon("images/save.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        try {
            save(new File(WebStyles.getApplication().getController().getName()));
        }
        catch (Exception excpt) {
            JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), excpt.toString(), "Save Error", JOptionPane.ERROR_MESSAGE, null);
        }
    }

}