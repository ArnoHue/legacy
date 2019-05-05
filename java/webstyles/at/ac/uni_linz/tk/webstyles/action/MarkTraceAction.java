package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class MarkTraceAction extends AbstractAction {

    private static MarkTraceAction action = new MarkTraceAction();

    public static MarkTraceAction getAction() {
        return action;
    }

    private MarkTraceAction() {
        super("Mark Trace", new ImageIcon("images/go.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PSGraph graph = WebStyles.getApplication().getGraph();
        PSComponent comp = WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        graph.markTrace(comp);
    }

}