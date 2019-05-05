package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class ZoomInAction extends ZoomAction {

    private static ZoomInAction action = new ZoomInAction();

    public static Action getAction() {
        return action;
    }

    private ZoomInAction() {
        super("Zoom in", new ImageIcon("images/zoomin.gif"));
        // setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        WebStyles.getApplication().getGraphView().zoom(2.0);
        updateEnabledStates();
    }

}