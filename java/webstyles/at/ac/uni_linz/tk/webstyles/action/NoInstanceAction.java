package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class NoInstanceAction extends AbstractAction {

    private static NoInstanceAction action = new NoInstanceAction();

    public static Action getAction() {
        return action;
    }

    private NoInstanceAction() {
        super("Don't Instantiate", new ImageIcon("images/noinstance.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            PSComponent comp = (PSComponent)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
            comp.dontInstantiate();
        }
        catch (PSException excpt) {
            excpt.display();
        }
    }

}