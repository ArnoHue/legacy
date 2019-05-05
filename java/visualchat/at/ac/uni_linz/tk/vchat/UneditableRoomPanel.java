package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;


/**
 * A RoomPanel is used for displaying Room data to other Users than the Administrator.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UneditableRoomPanel extends RoomPanel {


/**
 * Constructs the UneditableRoomPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                Rooms
 */

  public UneditableRoomPanel(ChatApplet chatParam) {

    super(chatParam);

    nameField.setEditable(false);
    infoArea.setEditable(false);
    rulesArea.setEditable(false);
    privateAccessCheckbox.setEnabled(false);
    publicAccessCheckbox.setEnabled(false);

  }

}