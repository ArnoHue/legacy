package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class GoAction extends AbstractAction {

    private static GoAction action = new GoAction();

    public static Action getAction() {
        return action;
    }

    private GoAction() {
        super("Go", new ImageIcon("images/go.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSGraph graph = WebStyles.getApplication().getGraph();
        PSComponent comp = WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        graph.markTrace(comp);
    }

}