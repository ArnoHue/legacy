package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class EditAction extends AbstractAction {

    private static EditAction action = new EditAction();

    public static Action getAction() {
        return action;
    }

    private EditAction() {
        super("Edit", new ImageIcon("images/edit.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles.getApplication().getController().setEditMode(PSGraphController.MODE_EDIT);
    }

}