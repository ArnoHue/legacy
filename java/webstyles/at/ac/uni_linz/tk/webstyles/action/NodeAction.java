package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class NodeAction extends AbstractAction {

    private static NodeAction action = new NodeAction();

    public static Action getAction() {
        return action;
    }

    private NodeAction() {
        super("Node", new ImageIcon("images/node.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles.getApplication().getController().setEditMode(PSGraphController.MODE_NODE);
    }

}