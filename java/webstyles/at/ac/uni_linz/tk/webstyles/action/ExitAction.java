package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.gui.*;

public class ExitAction extends AbstractAction {

    private static ExitAction action = new ExitAction();

    public static ExitAction getAction() {
        return action;
    }

    private ExitAction() {
        super("Exit");
    }

    public void actionPerformed(ActionEvent e) {
        exit();
    }
    
    public void exit() {
        PSEditorProperties.getProperties().save();
        System.exit(0);
    }

}