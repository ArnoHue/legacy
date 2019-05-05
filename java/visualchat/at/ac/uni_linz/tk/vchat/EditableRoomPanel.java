package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * An EditableRoomPanel is used for displaying and editing Room-data.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class EditableRoomPanel extends RoomPanel implements ActionListener, ItemListener, KeyListener {

  private FramedPanel editUserListPanel;
  private InsetsPanel buttonPanel;
  private Button addUserButton, removeUserButton, applyButton, removeButton;
  private TextField userField;

/**
 * Constructs the EditableRoomPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                Rooms
 */

  public EditableRoomPanel(ChatApplet chatParam) {

    super(chatParam);

    GridBagConstraints constraints;

    userList.setSelectable(true);

    editUserListPanel = new FramedPanel("", ChatRepository.INSETS);
    editUserListPanel.setLayout(new GridBagLayout());
    ChatUtil.addWithBeginningConstraints(editUserListPanel, new Label("User:", Label.RIGHT));
    ChatUtil.addWithRemainingConstraints(editUserListPanel, userField = new TextField());
    ChatUtil.addWithRemainingConstraints(editUserListPanel, addUserButton = new Button(""));
    ChatUtil.addWithRemainingConstraints(editUserListPanel, removeUserButton = new Button(""));

    userAccessPanel.add(editUserListPanel);

    applyButton = new Button("Apply");
    removeButton = new Button("Remove");
    buttonPanel = new InsetsPanel(ChatRepository.INSETS);
    buttonPanel.setLayout(new GridBagLayout());
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    constraints.weightx = 1.0;
    ChatUtil.addWithConstraints(buttonPanel, removeButton, constraints);
    constraints.anchor = GridBagConstraints.EAST;
    constraints.weightx = 1.0;
    ChatUtil.addWithConstraints(buttonPanel, applyButton, constraints);
    add(buttonPanel, "South");

    userField.addKeyListener(this);

    publicAccessCheckbox.addItemListener(this);
    privateAccessCheckbox.addItemListener(this);

    removeButton.addActionListener(this);
    applyButton.addActionListener(this);
    addUserButton.addActionListener(this);
    removeUserButton.addActionListener(this);
    addUserButton.setEnabled(false);
    removeUserButton.setEnabled(userList.getNrOfRows() > 0);
  }


/**
 * Toggles the EditableRoomPanel's display, depending whether the Room is private
 * or not.
 *
 * @param roomIsPrivate      true, if the displayed Room is private.
 */

  public void toggleUserPanel(boolean roomIsPrivate) {
    super.toggleUserPanel(roomIsPrivate);
    if (roomIsPrivate) {
      editUserListPanel.setTitle("(Un)invite User");
      addUserButton.setLabel("Invite");
      removeUserButton.setLabel("Uninvite");
    }
    else {
      editUserListPanel.setTitle("(Un)kick User");
      addUserButton.setLabel("Kick");
      removeUserButton.setLabel("Unkick");
    }
    removeUserButton.setEnabled(userList.getNrOfRows() > 0);
  }

  private boolean containsString(Vector vec, String str) {
  	Enumeration elems = vec.elements();
  	while (elems.hasMoreElements()) {
  		String elemStr = elems.nextElement().toString();
  		if (elemStr != null && elemStr.equals(str)) {
  			return true;
  		}
  	}
  	return false;
  }


  private String findString(Vector vec, String str) {
	Enumeration elems = vec.elements();
	while (elems.hasMoreElements()) {
		String elemStr = elems.nextElement().toString();
		if (elemStr != null && elemStr.equals(str)) {
			return elemStr;
		}
	}
	return null;
  }

/**
 * Invoked when an action occurs.
 *
 * @param event      the ActionEvent
 */

  public void actionPerformed(ActionEvent event) {

    if (event.getSource() == applyButton) {
      updateRoom(chatApplet.getCurrentRoom());
    }
    if (event.getSource() == removeButton) {
      chatApplet.forceRoomRemoval(chatApplet.getCurrentRoomId(), chatApplet.getClient() != null && chatApplet.getClient().connected());
      chatApplet.showChat();
    }
    else {
      if (event.getSource() == addUserButton) {
        String row[];
        row = new String[1];
        if ((row[0] = userField.getText()).length() > 0) {
          if (privateAccessCheckbox.getState() && !containsString(invitedUserVector, row[0])) {
            invitedUserVector.addElement(row[0]);
            updateUserList(invitedUserVector);
          }
          else if (!containsString(kickedUserVector, row[0])){
            kickedUserVector.addElement(row[0]);
            updateUserList(kickedUserVector);
          }
          userField.setText("");
          removeUserButton.setEnabled(userList.getNrOfRows() > 0);
        }
      }
      else if (event.getSource() == removeUserButton) {
        if (privateAccessCheckbox.getState()) {
          invitedUserVector.removeElement(findString(invitedUserVector, userList.getCellText(userList.getSelectedRow(), 0)));
          updateUserList(invitedUserVector);
        }
        else {
          kickedUserVector.removeElement(findString(kickedUserVector, userList.getCellText(userList.getSelectedRow(), 0)));
          updateUserList(kickedUserVector);
        }
        removeUserButton.setEnabled(userList.getNrOfRows() > 0);
      }
    }
  }

  public void showRoom(Room roomParam) {
	super.showRoom(roomParam);
    nameField.setEditable(roomParam.getId() != 0);
    privateAccessCheckbox.setEnabled(roomParam.getId() != 0);
    publicAccessCheckbox.setEnabled(roomParam.getId() != 0);
    infoArea.setEditable(roomParam.getId() != 0);
    rulesArea.setEditable(roomParam.getId() != 0);
  }

/**
 * Invoked when an item's state has been changed.
 *
 * @param event      the ItemEvent
 */

  public void itemStateChanged(ItemEvent event) {
    toggleUserPanel(privateAccessCheckbox.getState());
  }



/**
 * Updates a Rooms's data.
 *
 * @param roomParam      the Rooms's new data
 */

  public void updateRoom(Room roomParam) {
    Room room;

    room = (Room)(roomParam.clone());
    room.setPrivate(privateAccessCheckbox.getState());
    room.setName(nameField.getText());
    room.setInfo(infoArea.getText());
    room.setRules(rulesArea.getText());
    room.setInvitedUsers((Vector)invitedUserVector.clone());
    room.setKickedUsers((Vector)kickedUserVector.clone());
    chatApplet.updateRoom(room, chatApplet.isConnected());
  }


/**
 * Invoked when a key has been pressed.
 *
 * @param event      the KeyEvent
 */

  public void keyPressed(KeyEvent event) {
  }


/**
 * Invoked when a key has been released.
 *
 * @param event      the KeyEvent
 */

  public void keyReleased(KeyEvent event) {
    addUserButton.setEnabled(userField.getText().length() > 0);
  }


/**
 * Invoked when a key has been typed.
 *
 * @param event      the KeyEvent
 */

  public void keyTyped(KeyEvent event) {
  }

}