package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;


/**
 * A RoomPanel is used for displaying and editing Rser-data.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomPanel extends Panel {

  protected ChatApplet chatApplet;

  protected Panel userAccessPanel;
  protected FramedPanel namePanel, accessPanel, userListPanel;
  protected TextField nameField, administratorField, usersOnlineField;
  protected TextArea infoArea, rulesArea;
  protected Checkbox privateAccessCheckbox, publicAccessCheckbox;
  protected CheckboxGroup accessCheckboxGroup;
  protected XList userList;

  protected Vector invitedUserVector, kickedUserVector;

  private int roomId;


/**
 * Constructs the RoomPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                Rooms
 */

  public RoomPanel(ChatApplet chatParam) {

    super();

    GridBagConstraints constraints;
    int listWidth[] = { 160 };
    String listHead[] = { "User" };

    chatApplet = chatParam;

    invitedUserVector = new Vector();
    kickedUserVector = new Vector();

    namePanel = new FramedPanel("Room", ChatRepository.INSETS);
    namePanel.setLayout(new GridBagLayout());
    ChatUtil.addWithBeginningConstraints(namePanel, new Label("Name:", Label.RIGHT));
    ChatUtil.addWithRemainingConstraints(namePanel, nameField = new TextField());
    ChatUtil.addWithBeginningConstraints(namePanel, new Label("Users online:", Label.RIGHT));
    ChatUtil.addWithRemainingConstraints(namePanel, usersOnlineField = new TextField());
    ChatUtil.addWithBeginningConstraints(namePanel, new Label("Administrator:", Label.RIGHT));
    ChatUtil.addWithRemainingConstraints(namePanel, administratorField = new TextField());
    ChatUtil.addWithRemainingConstraints(namePanel, new Label("Info:", Label.LEFT));
    ChatUtil.addWithRemainingConstraints(namePanel, infoArea = new TextArea("", 3, 80, TextArea.SCROLLBARS_NONE));
    ChatUtil.addWithRemainingConstraints(namePanel, new Label("Rules:", Label.LEFT));
    ChatUtil.addWithRemainingConstraints(namePanel, rulesArea = new TextArea("", 5, 80, TextArea.SCROLLBARS_NONE));
    administratorField.setEditable(false);
    usersOnlineField.setEditable(false);

    accessPanel = new FramedPanel("Access", ChatRepository.INSETS);
    accessPanel.setLayout(new GridBagLayout());
    accessCheckboxGroup = new CheckboxGroup();
    publicAccessCheckbox = new Checkbox("Public", accessCheckboxGroup, true);
    privateAccessCheckbox = new Checkbox("Private", accessCheckboxGroup, true);
    ChatUtil.addWithRemainingConstraints(accessPanel, publicAccessCheckbox);
    ChatUtil.addWithRemainingConstraints(accessPanel, privateAccessCheckbox);

    userListPanel = new FramedPanel("", ChatRepository.INSETS);
    userListPanel.setLayout(new BorderLayout());
    userListPanel.add(userList = new XList(1), "Center");
    userList.setColumnWidths(listWidth);
    userList.setColumnHeaders(listHead);
    userList.setSorted(true);
    userList.setSelectable(false);

    userAccessPanel = new Panel();
    userAccessPanel.setLayout(new GridLayout(3, 1));
    userAccessPanel.add(accessPanel);
    userAccessPanel.add(userListPanel);
    userAccessPanel.add(userListPanel);

    setLayout(new BorderLayout());
    setFont(ChatRepository.STANDARD_FONT);

    add(namePanel, "Center");
    add(userAccessPanel, "East");
  }


/**
 * Toggles the RoomPanel's display depending on if the displayed Room is private or
 * not.
 *
 * @param roomIsPrivate      true, if the Room is private
 */

  public void toggleUserPanel(boolean roomIsPrivate) {
    userListPanel.setTitle(roomIsPrivate ? "Inivited Users" : "Kicked Users");
    updateUserList(roomIsPrivate ? invitedUserVector : kickedUserVector);
  }


/**
 * Forces an update of th Users being display in the XList. Those are th Users who are
 * either invited to or kicked out of the Room.
 *
 * @param userVector      a Vector which contains the names of all Users to be invited
 *                        or kicked
 */

  public void updateUserList(Vector userVector) {
    Enumeration userEnum;
    userEnum = userVector.elements();
    userList.clear();
    while(userEnum.hasMoreElements()) {
      String[] row = { (String)userEnum.nextElement() };
      userList.addRow(row);
    }
  }


/**
 * Displays the data of a given Room within the RoomPanel.
 *
 * @param roomParam      the Room whose data should be displayed
 */

  public void showRoom(Room roomParam) {
    setRoomId(roomParam.getId());
    nameField.setText(roomParam.getName());
    usersOnlineField.setText(new Integer(roomParam.getNrOfUsers()).toString());

    publicAccessCheckbox.setState(!roomParam.isPrivate());
    privateAccessCheckbox.setState(roomParam.isPrivate());

    administratorField.setText(roomParam.getAdministrator());
    infoArea.setText(roomParam.getInfo());
    rulesArea.setText(roomParam.getRules());

    invitedUserVector = roomParam.getInvitedUsers();
    kickedUserVector = roomParam.getKickedUsers();
    toggleUserPanel(roomParam.isPrivate());

  }


/**
 * Sets the id of the Room currently being shown.
 *
 * @param roomIdParam      the id of the Room
 */

  private void setRoomId(int roomIdParam) {
    roomId = roomIdParam;
  }


/**
 * Returns the id of the Room currently being shown.
 */

  public int getRoomId() {
    return roomId;
  }

}