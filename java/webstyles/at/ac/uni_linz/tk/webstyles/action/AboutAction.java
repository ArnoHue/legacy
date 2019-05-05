package at.ac.uni_linz.tk.webstyles.action;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.gui.*;

public class AboutAction extends AbstractAction {

    private static AboutAction action = new AboutAction();

    public static Action getAction() {
        return action;
    }

    private AboutAction() {
        super("About WebStyles");
    }

    public void actionPerformed(ActionEvent e) {
        new SplashScreen(WebStyles.getApplication().getFrame()).setVisible(true);
    }

}