package at.ac.uni_linz.tk.vchat;

import java.awt.*;


/**
 * A UneditableUserPanel is used for displaying User-data to other Users.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UneditableUserPanel extends UserPanel {


/**
 * Constructs the UneditableUserPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                Users
 */

  public UneditableUserPanel(ChatApplet chatParam) {

    super(chatParam);

    nameField.setEditable(false);
    roomField.setEditable(false);
    loginDateField.setEditable(false);
    emailField.setEditable(false);
    homepageField.setEditable(false);
    infoArea.setEditable(false);

    userDataPanel.remove(colorPasswordPanel);
    remove(avatarPanelLevel1);

  }

}