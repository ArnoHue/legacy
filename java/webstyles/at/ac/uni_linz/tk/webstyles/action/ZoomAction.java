package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public abstract class ZoomAction extends AbstractAction {

    public ZoomAction(String name, Icon icon) {
        super(name, icon);
    }

    public static void updateEnabledStates() {
        double zoomFactor = WebStyles.getApplication().getGraphView().getZoomFactor();
        ZoomOutAction.getAction().setEnabled(zoomFactor > 0.125);
        ZoomInAction.getAction().setEnabled(zoomFactor < 8);
    }

}