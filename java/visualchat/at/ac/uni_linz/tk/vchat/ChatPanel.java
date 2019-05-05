package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;


/**
 * Works as a container for the chat interaction. It includes the ViewPanel,
 * RoomPanel, MoodPanel, the control components for the history mode and the
 * TextField for the message text.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ChatPanel extends Panel implements ActionListener, MouseListener, KeyListener, FocusListener {

  private ChatApplet chatApplet;

  private Dimension roomListPanelSize = ChatRepository.ROOM_DIMENSION;
  private Dimension roomCanvasSize = ChatRepository.ROOM_DIMENSION;
  private Dimension moodCanvasSize = ChatRepository.ROOM_DIMENSION;

  private InsetsPanel viewPanel, controlPanel, mapPanel, roomPanel, textPanel, textNavigationPanel, portraitPanel, historyCheckboxGroupPanel;
  private NavigationCanvas navigationCanvas;
  private RoomListPanel roomListPanel;
  private MoodCanvas moodCanvas;
  protected TextField chatField;
  protected Label chatLabel;
  private RoomCanvas roomCanvas;
  private ViewCanvas viewCanvas;

  private HistoryPanel pnlHistory;
  private TextArea messageArea;


/**
 * Constructs the ChatPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 * @param repositoryParam         the ChatRepository, where commonly used objects
 *                                are being stored
 */

  public ChatPanel(ChatApplet chatParam) {

    super();

    GridBagConstraints constraints;

    chatApplet = chatParam;

    viewPanel = new InsetsPanel(ChatRepository.INSETS);
    viewPanel.setLayout(new BorderLayout());
    viewCanvas = new ViewCanvas(chatApplet);
    viewCanvas.setBackground(ChatRepository.COMPONENT_BACKGROUND);
    viewPanel.add(viewCanvas, "Center");

    controlPanel = new InsetsPanel();
    controlPanel.setLayout(new BorderLayout());

    roomListPanel = new RoomListPanel(chatParam);
    roomListPanel.setSize(roomListPanelSize);

    roomPanel = new InsetsPanel(ChatRepository.INSETS);
    roomCanvas = new RoomCanvas(chatApplet);
    roomCanvas.setBackground(ChatRepository.COMPONENT_BACKGROUND);
    roomCanvas.setSize(roomCanvasSize);
    roomPanel.add(roomCanvas);

    portraitPanel = new InsetsPanel(ChatRepository.INSETS);
    moodCanvas = new MoodCanvas(chatApplet);
    moodCanvas.setSize(moodCanvasSize);

    portraitPanel.add(moodCanvas);

    Panel tmpPanel = new Panel();
    tmpPanel.setLayout(new GridLayout(2, 1));
    tmpPanel.add(roomPanel);
    tmpPanel.add(portraitPanel);

    controlPanel.add(roomListPanel, "Center");
    controlPanel.add(tmpPanel, "South");

    textPanel = new InsetsPanel(new Insets(ChatRepository.INSETS.top, 0, ChatRepository.INSETS.bottom, 0));

    chatLabel = new Label("Chat:", Label.RIGHT);
    pnlHistory = new HistoryPanel("History", chatApplet);
    chatField = new TextField(75);
	messageArea = new TextArea("", 6, 25, TextArea.SCROLLBARS_VERTICAL_ONLY);
    messageArea.setEditable(false);
    chatField.addKeyListener(this);
    chatField.addActionListener(this);
    chatField.addMouseListener(this);

    textPanel.setLayout(new GridBagLayout());
    if (chatApplet.getBooleanParam("HistoryBarVisibility", true)) {
      ChatUtil.addWithRemainingConstraints(textPanel, pnlHistory);
    }
    ChatUtil.addWithBeginningConstraints(textPanel, chatLabel);
    ChatUtil.addWithRemainingConstraints(textPanel, chatField);

    navigationCanvas = new NavigationCanvas(chatApplet,
    chatApplet.getImage(ChatRepository.IMAGE_ARROW_FORWARD),
    chatApplet.getImage(ChatRepository.IMAGE_ARROW_BACKWARD),
    chatApplet.getImage(ChatRepository.IMAGE_ARROW_LEFT),
    chatApplet.getImage(ChatRepository.IMAGE_ARROW_RIGHT)
    );
    textNavigationPanel = new InsetsPanel(new Insets(0, ChatRepository.SMALL_INSETS.left, 0, ChatRepository.SMALL_INSETS.right));
    textNavigationPanel.setLayout(new BorderLayout());
    textNavigationPanel.add(textPanel, "Center");
    textNavigationPanel.add(navigationCanvas, "East");
    textNavigationPanel.add(messageArea, "South");

    viewPanel.add(textNavigationPanel, "South");

    setLayout(new BorderLayout());
    setFont(ChatRepository.STANDARD_FONT);

    add(viewPanel, "Center");
    add(controlPanel, "East");

    chatApplet.setHistoryPanel(pnlHistory);

  }


/**
 * Invoked when a component gains the keyboard focus.
 *
 * @param event      the FocusEvent
 */

  public void focusGained(FocusEvent event) {
  }


/**
 * Invoked when a component loses the keyboard focus.
 *
 * @param event      the FocusEvent
 */

  public void focusLost(FocusEvent event) {
  }


/**
 * Invoked when an action occurs.
 *
 * @param event      the ActionEvent
 */

  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == chatField) {
      chatApplet.setUserMessage(chatApplet.getCurrentUser().getId(), chatField.getText(), true);
      // lastMsgLabel2.setText(chatField.getText());
      chatField.setText("");
    }
  }


/**
 * Contains functionality for all possible KeyEvents on the ChatPanel and its
 * components.
 *
 * @param event      the KeyEvent
 */

  public void handleKeyEvent(KeyEvent event) {
    if (event.isAltDown()) {
      if (event.getKeyCode() == KeyEvent.VK_DOWN) {
        chatApplet.setUserPosition(chatApplet.getCurrentUser().getId(), new Point(chatApplet.getCurrentUser().getPosition().x - (int)(ChatMath.getCos((chatApplet.getCurrentUser().getHeading())) * 2.0), chatApplet.getCurrentUser().getPosition().y + (int)(ChatMath.getSin(chatApplet.getCurrentUser().getHeading()) * 2.0)), true);
      }
      else if (event.getKeyCode() == KeyEvent.VK_UP) {
        chatApplet.setUserPosition(chatApplet.getCurrentUser().getId(), new Point(chatApplet.getCurrentUser().getPosition().x + (int)(ChatMath.getCos((chatApplet.getCurrentUser().getHeading())) * 2.0), chatApplet.getCurrentUser().getPosition().y - (int)(ChatMath.getSin(chatApplet.getCurrentUser().getHeading()) * 2.0)), true);
      }
      else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
        chatApplet.setUserHeading(chatApplet.getCurrentUser().getId(), chatApplet.getCurrentUser().getHeading() + 2, true);
      }
      else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
        chatApplet.setUserHeading(chatApplet.getCurrentUser().getId(), chatApplet.getCurrentUser().getHeading() - 2, true);
      }
    }
    if (event.getComponent() == chatField) {
      if (chatApplet.historyMode())
        chatApplet.exitHistoryMode();
    }
  }


/**
 * Invoked when a key has been pressed.
 *
 * @param event      the KeyEvent
 */

  public void keyPressed(KeyEvent event) {
    handleKeyEvent(event);
  }


/**
 * Invoked when a key has been released.
 *
 * @param event      the KeyEvent
 */

  public void keyReleased(KeyEvent event) {
    handleKeyEvent(event);
  }


/**
 * Invoked when a key has been typed.
 *
 * @param event      the KeyEvent
 */

  public void keyTyped(KeyEvent event) {
    handleKeyEvent(event);
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
    if (event.getComponent() == chatField) {
      if (chatApplet.historyMode())
        chatApplet.exitHistoryMode();
    }
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseEntered(MouseEvent event) {
  }


/**
 * Invoked when the mouse exits a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
  }


/**
 * Invoked when a mouse button has been pressed on a component.
 *
 * @param event      the MouseEvent
 */

  public void mousePressed(MouseEvent event) {
  }


/**
 * Invoked when a mouse button has been released on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
  }


/**
 * Repaints the ViewPanel. Should be called after changes in the User's view.
 */

  public void repaintView() {
    viewCanvas.repaint();
  }


/**
 * Repaints the RoomList. Should be called after Users have moved to Rooms.
 */

  public void updateRoomList() {
    roomListPanel.updateRoomList();
  }


/**
 * Fills the RoomList. Should be called after Romm data has changed.
 */

  public void fillRoomList() {
    roomListPanel.fillRoomList();
  }


/**
 * Repaints the RoomPanel. Should be called after changes of users' position
 * or heading.
 */

  public void repaintRoom() {
    roomCanvas.repaint();
  }


/**
 * Repaints the MoodPanel. Should be called after changes of User's mood.
 */

  public void repaintMood() {
    moodCanvas.repaint();
  }

  public void addMessage(String userName, String message) {
    messageArea.append("\n<" + userName + "> " + message);
  }

  public void clearMessage() {
    messageArea.setText("");
  }

}