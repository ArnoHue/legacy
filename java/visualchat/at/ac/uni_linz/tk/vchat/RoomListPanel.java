package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import symantec.itools.awt.*;


/**
 * This Panel is being used to list, add and jump to Rooms.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomListPanel extends InsetsPanel implements ActionListener {

  private ChatApplet chatApplet;

  private XList roomList;
  private InsetsPanel buttonPanel;
  private Button jumpButton, createButton;
  private String[] headings = { "Room", "Users" };
  private int[] widths = { 110, 45 };
  private int[] orientations = { XList.FLOW, XList.RIGHT };


/**
 * Constructs the RoomListPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 */

  public RoomListPanel(ChatApplet chatParam) {

    super(ChatRepository.INSETS);

    GridBagConstraints constraints;
    chatApplet = chatParam;

    setFont(ChatRepository.SMALL_FONT);

    roomList = new XList(2);
    roomList.setColumnHeaders(headings);
    roomList.setColumnWidths(widths);
    roomList.setColumnOrientations(orientations);
    roomList.setSelectable(true);
    roomList.setSorted(true);

    jumpButton = new Button("Join");
    jumpButton.setEnabled(chatApplet.getBooleanParam("RoomJoining", true));
    createButton = new Button("Create");
    createButton.setEnabled(chatApplet.getBooleanParam("RoomCreation", true));
    buttonPanel = new InsetsPanel(ChatRepository.INSETS);
    buttonPanel.setLayout(new GridLayout(1, 2, ChatRepository.MARGIN, ChatRepository.MARGIN));
    buttonPanel.add(jumpButton);
    buttonPanel.add(createButton);

    setLayout(new BorderLayout());
    add(roomList, "Center");
    add(buttonPanel, "South");

    setFont(ChatRepository.STANDARD_FONT);

    jumpButton.addActionListener(this);
    createButton.addActionListener(this);

    fillRoomList();
  }


/**
 * Fills the XList with data provided by the ChatApplet.
 */

  public synchronized void fillRoomList() {
    Enumeration roomIds;
    String row[];
    int roomId;

    roomList.clear();
    roomIds = chatApplet.getRoomIds();
    row = new String[2];

    for (int i = 0; roomIds.hasMoreElements(); i++) {
      Integer roomIdInt = (Integer)roomIds.nextElement();
      if (roomIdInt != null) {
        roomId = roomIdInt.intValue();
        Room room = chatApplet.getRoom(roomId);
        if (room != null) {
          row[0] = room.getName();
          row[1] = new Integer(room.getNrOfUsers()).toString();
          roomList.addRow(row, roomId, new Color(0, ChatRepository.MAX_COLOR_VALUE, 0));
        }
      }
    }
    updateRoomList();
  }


/**
 * Updates the XList regarding to the data provided by the ChatApplet.
 */

  public synchronized void updateRoomList() {
    Room room;
    String row[];
    int roomId;

    row = new String[2];

    for (int i = 0; i < roomList.getNrOfRows(); i++) {
      roomId = roomList.getKey(i);
      room = chatApplet.getRoom(roomId);
      if (room != null) {
        roomList.setCellText(i, 1, new Integer(room.getNrOfUsers()).toString());

        if (room.getId() == chatApplet.getCurrentRoomId())  {
          roomList.setRowColor(i, new Color(0, 0, ChatRepository.MAX_COLOR_VALUE));
        }
        else if (!room.hasAccess(chatApplet.getCurrentUser().getName())) {
          roomList.setRowColor(i, new Color(ChatRepository.MAX_COLOR_VALUE, 0, 0));
        }
        else {
          roomList.setRowColor(i, new Color(0, ChatRepository.MAX_COLOR_VALUE, 0));
        }
      }
    }
  }


/**
 * Invoked when an action occurs.
 *
 * param @event      the ActionEvent that occured
 */

  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == jumpButton && chatApplet.getCurrentRoomId() != roomList.getKey(roomList.getSelectedRow())) {
      chatApplet.moveUserToRoom(chatApplet.getCurrentUserId(), roomList.getKey(roomList.getSelectedRow()), true);
    }
    else if (event.getSource() == createButton) {
      chatApplet.createRoom();
    }
  }

}