package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;

public class DebugAction extends AbstractAction {

    private static DebugAction action = new DebugAction();

    public static Action getAction() {
        return action;
    }

    private DebugAction() {
        super("Toggle Debug Mode", new ImageIcon("images/debug.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles app = WebStyles.getApplication();
        PSGraphView graphView = app.getGraphView();
        graphView.setDebugMode(!graphView.isDebugMode());
        app.setStatus((graphView.isDebugMode() ? "Enabled" : "Disabled") + " Debug Mode");
    }

}