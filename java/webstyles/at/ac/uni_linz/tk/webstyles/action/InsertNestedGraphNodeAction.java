package at.ac.uni_linz.tk.webstyles.action;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class InsertNestedGraphNodeAction extends AbstractAction {

    private static InsertNestedGraphNodeAction action = new InsertNestedGraphNodeAction();

    public static Action getAction() {
        return action;
    }

    private InsertNestedGraphNodeAction() {
        super("Insert Nested Graph Node", new ImageIcon("images/nested.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        PSGraphController ctrl = WebStyles.getApplication().getController();
        ctrl.setNestedNodeMode(!ctrl.getNestedNodeMode());
    }

}