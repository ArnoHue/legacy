package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import symantec.itools.awt.*;


/**
 * Works as a container for the ChatApplet's main user-interface. A ChatFrame will
 * be opened besides the browser window (which contains only the LoginPanel).
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ChatFrame extends Frame implements ComponentListener, WindowListener {

  private TabPanel tabPanel;
  private ChatPanel chatPanel;
  private EditableUserPanel editableUserPanel;
  private UneditableUserPanel uneditableUserPanel;
  protected EditableRoomPanel editableRoomPanel;
  private UneditableRoomPanel uneditableRoomPanel;
  private InfoPanel infoPanel;
  private Panel statusPanel;
  private Label statusLabel;
  private Frame uneditableUserFrame, uneditableRoomFrame;

  private ChatApplet chatApplet;


/**
 * Constructs the ChatFrame.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 * @param repositoryParam         the ChatRepository, where commonly used objects
 *                                are being stored
 */

  public ChatFrame(ChatApplet chatParam) {

    chatApplet = chatParam;

    setIconImage(chatApplet.getImage(ChatRepository.ICON_FILENAME));

    setBackground(ChatRepository.CONTAINER_BACKGROUND);
    setFont(ChatRepository.STANDARD_FONT);
    setLayout(new BorderLayout());

    tabPanel = new TabPanel();
    chatPanel = new ChatPanel(chatParam);
    chatPanel.addComponentListener(this);

    editableUserPanel = new EditableUserPanel(chatParam);
    uneditableUserPanel = new UneditableUserPanel(chatParam);

    editableUserPanel.showUser(chatApplet.getCurrentUser());
    editableUserPanel.addComponentListener(this);

    uneditableUserFrame = new Frame();
    uneditableUserFrame.setIconImage(chatApplet.getImage(ChatRepository.ICON_FILENAME));
    // Required because of MSIE4-bug
    uneditableUserFrame.setLayout(new BorderLayout());
    uneditableUserFrame.setBackground(ChatRepository.CONTAINER_BACKGROUND);
    uneditableUserFrame.setFont(ChatRepository.STANDARD_FONT);
    uneditableUserFrame.add(uneditableUserPanel, "Center");
    uneditableUserFrame.setSize(480, 320);
    uneditableUserFrame.setVisible(false);
    uneditableUserFrame.addWindowListener(this);

    editableRoomPanel = new EditableRoomPanel(chatParam);
    uneditableRoomPanel = new UneditableRoomPanel(chatParam);

    editableRoomPanel.showRoom(chatApplet.getCurrentRoom());
    editableRoomPanel.addComponentListener(this);

    uneditableRoomFrame = new Frame();
    uneditableRoomFrame.setIconImage(chatApplet.getImage(ChatRepository.ICON_FILENAME));

    // Required because of MSIE4-bug
    uneditableRoomFrame.setLayout(new BorderLayout());
    uneditableRoomFrame.setBackground(ChatRepository.CONTAINER_BACKGROUND);
    uneditableRoomFrame.setFont(ChatRepository.STANDARD_FONT);
    uneditableRoomFrame.add(uneditableRoomPanel, "Center");
    uneditableRoomFrame.setSize(560, 440);
    uneditableRoomFrame.setVisible(false);
    uneditableRoomFrame.addWindowListener(this);

    infoPanel = new InfoPanel(chatParam);

    statusPanel = new InsetsPanel(new Insets(0, ChatRepository.INSETS.left, 0, ChatRepository.INSETS.right));
    statusPanel.setLayout(new BorderLayout());
    statusLabel = new Label("Status:");
    statusPanel.add(statusLabel, "Center");
    statusPanel.add(chatApplet.getLogo(), "East");

    tabPanel.addTabPanel("Chat", true, chatPanel);
    tabPanel.addTabPanel("User", true, editableUserPanel);
    tabPanel.addTabPanel("Room", true, editableRoomPanel);
    if (chatApplet.getBooleanParam("InfoTabVisibility", true)) {
      tabPanel.addTabPanel("Info", true, infoPanel);
    }
    add(tabPanel, "Center");
    add(statusPanel, "South");

    setTitle("Chat");
    addWindowListener(this);
    updateRoomTab();
  }


/**
 * Repaints the ViewPanel. Should be called after changes in the User's view.
 */

  public void repaintView() {
    chatPanel.repaintView();
  }


/**
 * Repaints the RoomList. Should be called after a User has moved to a Room.
 */

  public void updateRoomList() {
    chatPanel.updateRoomList();
    updateRoomTab();
  }


/**
 * Repaints the RoomPanel. Should be called after changes of Users' position
 * or heading.
 */

  public void repaintRoom() {
    chatPanel.repaintRoom();
  }


/**
 * Repaints the MoodPanel. Should be called after changes of User's mood.
 */

  public void repaintMood() {
    chatPanel.repaintMood();
  }

/**
 * Repaints all panels of the ChatFrame.
 */

  public void repaintAll() {
    chatPanel.repaintView();
    chatPanel.repaintRoom();
    chatPanel.repaintMood();
  }


/**
 * Repaints all panels of the ChatFrame.
 */

  public void fillRoomList() {
    chatPanel.fillRoomList();
  }


/**
 * Shows a message in the status bar.
 *
 * @param statusString      the message to be shown
 */

  public void setStatus(String statusString) {
    statusLabel.setText("Status: " + statusString);
  }


/**
 * Shows a User's data in a new Frame.
 *
 * @param userIdParam      the id the of the User to be shown
 */

  public void showUser(int userIdParam) {
    if (chatApplet.getUser(userIdParam) != null) {
      uneditableUserPanel.showUser(chatApplet.getUser(userIdParam));
      uneditableUserFrame.setTitle(chatApplet.getUser(userIdParam).getName());
      uneditableUserFrame.setVisible(true);
    }
  }


/**
 * Displays the current User's data in the User TabPanel and lets him edit it.
 *
 * @param userIdParam      the id the of the User to be edited
 */

  public void editUser(int userIdParam) {
    if (userIdParam == chatApplet.getCurrentUserId() && chatApplet.getUser(userIdParam) != null) {
      editableUserPanel.showUser(chatApplet.getUser(userIdParam));
      try {
        tabPanel.setTabPanel("User", true, editableUserPanel, 1);
      }
      catch (Exception excpt) {
      }
      tabPanel.showTabPanel(1);
    }
  }


/**
 * Repaints the current User's data in the User TabPanel.
 */

  public void repaintCurrentUser() {
    editableUserPanel.showUser(chatApplet.getCurrentUser());
    try {
      tabPanel.setTabPanel("User", true, editableUserPanel, 1);
    }
    catch (Exception excpt) {
    }
  }


/**
 * Brings the Chat TabPanel back to the front.
 */

  public void showChat() {
    tabPanel.showTabPanel(0);
  }


/**
 * Shows a Room's data in a new Frame.
 *
 * @param roomIdParam      the id the of the Room to be shown
 */

  public void showRoom(int roomIdParam) {
    Room room;
    room = chatApplet.getRoom(roomIdParam);
    if (room != null) {
      uneditableRoomPanel.showRoom(room);
      uneditableRoomFrame.setTitle(room.getName());
      uneditableRoomFrame.setVisible(true);
    }
  }


/**
 * Updates Room data in the Room TabPanel, which can be edited by the Room's
 * Administrator.
 *
 * @param userId      the Id of the Room to be edited
 */
  public void editRoom(int roomIdParam) {
    editRoom(roomIdParam, true);
  }

  public void editRoom(int roomIdParam, boolean show) {
    Room room;
    room = chatApplet.getRoom(roomIdParam);
    if (room != null && room.isAdministrator(chatApplet.getCurrentUser().getName())) {
      editableRoomPanel.showRoom(room);
      try {
        tabPanel.setTabPanel("Room", true, editableRoomPanel, 2);
      }
      catch (Exception excpt) {
      }
      if (show) {
        tabPanel.showTabPanel(2);
      }
    }
  }


/**
 * Toggles whether the current Room can be edited by the current User or not.
 */

  public void updateRoomTab() {
    Room room;
    room = chatApplet.getCurrentRoom();
    try {
      tabPanel.setEnabled(room != null && room.isAdministrator(chatApplet.getCurrentUser().getName()), 2);
    }
    catch (Exception excpt) {
    }
  }


/**
 * Invoked when a component has been hidden.
 *
 * @param event      the ComponentEvent
 */

  public void componentHidden(ComponentEvent event) {
    if (event.getComponent() == editableUserPanel) {
      editableUserPanel.showUser(chatApplet.getCurrentUser());
      try {
        tabPanel.setTabPanel("User", true, editableUserPanel, 1);
      }
      catch (Exception excpt) {
      }
    }
  }


/**
 * Invoked when a component has been moved.
 *
 * @param event      the ComponentEvent
 */

 public void componentMoved(ComponentEvent event) {
 }


/**
 * Invoked when a component has been resized.
 *
 * @param event      the ComponentEvent
 */

 public void componentResized(ComponentEvent event) {
 }


/**
 * Invoked when a component has been shown.
 *
 * @param event      the ComponentEvent
 */

 public void componentShown(ComponentEvent event) {
 }


/**
 * Invoked when the ChatFrame has been activated.
 *
 * @param event      the WindowEvent
 */

 public void windowActivated(WindowEvent event) {
 }


/**
 * Invoked when the ChatFrame has been closed.
 *
 * @param event      the WindowEvent
 */

 public void windowClosed(WindowEvent event) {
 }


/**
 * Invoked when the ChatFrame is in the process of being closed.
 *
 * @param event      the WindowEvent
 */

 public void windowClosing(WindowEvent event) {
   if (event.getComponent() == this) {
     chatApplet.stopChat();
   }
   else if (event.getComponent() == uneditableUserFrame) {
     uneditableUserFrame.setVisible(false);
   }
   else if (event.getComponent() == uneditableRoomFrame) {
     uneditableRoomFrame.setVisible(false);
   }
 }


/**
 * Invoked when the ChatFrame has been deactivated.
 *
 * @param event      the WindowEvent
 */

 public void windowDeactivated(WindowEvent event) {
 }


/**
 * Invoked when the ChatFrame has been deiconfied.
 *
 * @param event      the WindowEvent
 */

 public void windowDeiconified(WindowEvent event) {
 }


/**
 * Invoked when the ChatFrame has been iconfied.
 *
 * @param event      the WindowEvent
 */

  public void windowIconified(WindowEvent event) {
  }


/**
 * Invoked when the ChatFrame has been opened.
 *
 * @param event      the WindowEvent
 */

  public void windowOpened(WindowEvent event) {
  }

  public void addMessage(String userName, String message) {
    chatPanel.addMessage(userName, message);
  }

  public void clearMessage() {
    chatPanel.clearMessage();
  }

  public void setMessageLabel(String label) {
    chatPanel.chatLabel.setText(label);
    chatPanel.chatLabel.invalidate();
    chatPanel.chatLabel.validate();
    chatPanel.invalidate();
    chatPanel.validate();
    chatPanel.chatField.requestFocus();
  }

}


