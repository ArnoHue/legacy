package at.ac.uni_linz.tk.webstyles.action;

import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class LinkAction extends AbstractAction {

  private static LinkAction action = new LinkAction();

  public static Action getAction() {
    return action;
  }

  private LinkAction() {
    super("Link", new ImageIcon("images/link.gif"));
  }

  public void actionPerformed(ActionEvent e) {
    WebStyles.getApplication().getController().setEditMode(PSGraphController.MODE_LINK);
  }

}