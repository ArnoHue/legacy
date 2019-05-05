package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class ZoomOutAction extends ZoomAction {

    private static ZoomOutAction action = new ZoomOutAction();

    public static Action getAction() {
        return action;
    }

    private ZoomOutAction() {
        super("Zoom out", new ImageIcon("images/zoomout.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles.getApplication().getGraphView().zoom(0.5);
        updateEnabledStates();
    }

}