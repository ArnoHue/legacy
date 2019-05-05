package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * A Room's graphical representation. Users are painted as colored circles, the
 * current User is marked by a special cursor. Changing position or heading can
 * be done by mouse-clicking and -dragging.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomCanvas extends Canvas implements MouseListener, MouseMotionListener, ActionListener {

  private ChatApplet chatApplet;
  private Color nameColor = Color.black;

  private PopupMenu ownRoomPopup, roomPopup, ownUserPopup, userPopup;
  private Menu inviteUserMenu, kickUserMenu;
  private MenuItem roomInfoItem, roomSettingsItem, userInfoItem, userSettingsItem, whisperItem;

  private int selectedUserId;

  public final int ACTION_NONE = 0;
  public final int ACTION_MOVING = 1;
  public final int ACTION_ROTATING = 2;
  private int userAction = ACTION_NONE;

  private int focusedUserId = -1;


/**
 * Constructs the RoomCanvas.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 */

  public RoomCanvas(ChatApplet chatAdministratorParam) {
    super();
    chatApplet = chatAdministratorParam;
    addMouseListener(this);
    addMouseMotionListener(this);

    roomPopup = new PopupMenu();
    ownRoomPopup = new PopupMenu();
    userPopup = new PopupMenu();
    ownUserPopup = new PopupMenu();

    roomInfoItem = new MenuItem("View room settings");
    roomSettingsItem = new MenuItem("Edit room settings");
    userInfoItem = new MenuItem("View user info");
    userSettingsItem = new MenuItem("Edit user info");
    inviteUserMenu =  new Menu("Invite user to room");
    kickUserMenu = new Menu("Kick user from room");
    whisperItem = new MenuItem("Whisper to user");

    roomPopup.add(roomInfoItem);
    ownRoomPopup.add(roomSettingsItem);
    userPopup.add(whisperItem);
    userPopup.add(userInfoItem);
    userPopup.add(inviteUserMenu);
    userPopup.add(kickUserMenu);
    ownUserPopup.add(userSettingsItem);

    add(roomPopup);
    add(ownRoomPopup);
    add(userPopup);
    add(ownUserPopup);

    roomPopup.addActionListener(this);
    ownRoomPopup.addActionListener(this);
    userPopup.addActionListener(this);
    ownUserPopup.addActionListener(this);
    inviteUserMenu.addActionListener(this);
    kickUserMenu.addActionListener(this);
 }


/**
 * Paints the PortraitCanvas.
 *
 * @param g       the graphics context
 */

  public void paint (Graphics g) {

    Hashtable userTable;
    Vector vecCurrentSituation;
    HistoryEntry histEntry;

    g.setFont(ChatRepository.SMALL_FONT);

    vecCurrentSituation = chatApplet.historyMode() ? chatApplet.getHistoryEntryVector(chatApplet.getHistoryDate()) : chatApplet.getCurrentSituationVector();

    for (int i = 0; i < vecCurrentSituation.size(); i++) {
      histEntry = (HistoryEntry)vecCurrentSituation.elementAt(i);
      if (chatApplet.getUser(histEntry.userId) != null) {

        // histEntry has been cloned, so we can work on it...
        if (histEntry.userId == chatApplet.getCurrentUserId()) {
          histEntry.position = chatApplet.getCurrentUser().getPosition();
          histEntry.heading = chatApplet.getCurrentUser().getHeading();
          histEntry.color = chatApplet.getCurrentUser().getColor();
        }

        g.setColor(ChatUtil.brighten(ChatUtil.brighten(histEntry.color)));
        g.fillOval(histEntry.position.x - ChatRepository.USER_SIZE / 2, histEntry.position.y - ChatRepository.USER_SIZE / 2, ChatRepository.USER_SIZE, ChatRepository.USER_SIZE);
        g.setColor(histEntry.color);
        g.fillOval(histEntry.position.x - ChatRepository.USER_SIZE / 3, histEntry.position.y - ChatRepository.USER_SIZE / 3, ChatRepository.USER_SIZE * 2 / 3, ChatRepository.USER_SIZE * 2 / 3);
        // g.drawLine(histEntry.position.x, histEntry.position.y, histEntry.position.x + (int)(ChatMath.getCos(histEntry.heading) * ChatRepository.USER_SIZE  / 2), histEntry.position.y - (int)(ChatMath.getSin(histEntry.heading) * ChatRepository.USER_SIZE / 2));

        g.setColor(ChatUtil.brighten(histEntry.color));
        for (int j = ChatRepository.USER_SIZE + 5; j <= ChatRepository.USER_SIZE + 5; j += 10)
          g.drawArc(histEntry.position.x - j, histEntry.position.y - j, j * 2, j * 2, histEntry.heading - ChatRepository.PHONICAL_ANGLE / 2, ChatRepository.PHONICAL_ANGLE);

        if (histEntry.userId == focusedUserId) {
          g.setColor(Color.lightGray);
          g.drawOval(histEntry.position.x - ChatRepository.USER_SIZE / 2 - 2, histEntry.position.y - ChatRepository.USER_SIZE / 2 - 2, ChatRepository.USER_SIZE + 4, ChatRepository.USER_SIZE + 4);
          g.drawOval(histEntry.position.x - ChatRepository.USER_SIZE / 2 - 3, histEntry.position.y - ChatRepository.USER_SIZE / 2 - 3, ChatRepository.USER_SIZE + 6, ChatRepository.USER_SIZE + 6);
        }
        else if (histEntry.userId == chatApplet.getCurrentUserId()) {
          g.setColor(Color.black);
          g.drawOval(histEntry.position.x - ChatRepository.USER_SIZE / 2 - 2, histEntry.position.y - ChatRepository.USER_SIZE / 2 - 2, ChatRepository.USER_SIZE + 4, ChatRepository.USER_SIZE + 4);
          g.drawOval(histEntry.position.x - ChatRepository.USER_SIZE / 2 - 3, histEntry.position.y - ChatRepository.USER_SIZE / 2 - 3, ChatRepository.USER_SIZE + 6, ChatRepository.USER_SIZE + 6);
        }
        g.setColor(nameColor);
        g.drawString(chatApplet.getUser(histEntry.userId).getName(), histEntry.position.x + ChatRepository.USER_SIZE / 2, histEntry.position.y + ChatRepository.USER_SIZE / 2);
      }
    }

  }


/**
 * Returns the id of a User at a certain Point in the Room, resp. -1 if there is no
 * User at this very Point.
 *
 * @param positionParam      the Point to observe
 */

  private int getUserAtPosition(Point positionParam) {
    Enumeration userIdEnum;
    User user;

    userIdEnum = chatApplet.getRoomUserIdVector(chatApplet.getCurrentRoomId()).elements();

    while (userIdEnum.hasMoreElements()) {
      user = chatApplet.getUser(((Integer)userIdEnum.nextElement()).intValue());
      if (new Rectangle(user.getPosition().x - ChatRepository.USER_SIZE / 2, user.getPosition().y - ChatRepository.USER_SIZE / 2, ChatRepository.USER_SIZE, ChatRepository.USER_SIZE).contains(positionParam))
        return user.getId();
    }
    return -1;
  }


/**
 * Invoked when an action occurs.
 *
 * @param event       the ActionEvent
 */

  public void actionPerformed(ActionEvent event) {
    Room room;
    if (event.getActionCommand().equals(roomInfoItem.getLabel()))
      chatApplet.showRoom(chatApplet.getCurrentRoomId());
    else if (event.getActionCommand().equals(roomSettingsItem.getLabel()))
      chatApplet.editRoom(chatApplet.getCurrentRoomId());
    else if (event.getActionCommand().equals(userInfoItem.getLabel()))
      chatApplet.showUser(selectedUserId);
    else if (event.getActionCommand().equals(userSettingsItem.getLabel()))
      chatApplet.editUser(selectedUserId);
    else if (event.getActionCommand().equals(whisperItem.getLabel()))
      chatApplet.whisperToUser(selectedUserId);
    else if (event.getSource() == inviteUserMenu || event.getSource() == kickUserMenu) {
      room = chatApplet.getRoom(event.getActionCommand());
      if (room != null) {
        if (event.getSource() == inviteUserMenu) {
          room.inviteUser(chatApplet.getUser(selectedUserId).getName());
        }
        else {
          room.kickUser(chatApplet.getUser(selectedUserId).getName());
        }
        chatApplet.updateRoom(room, chatApplet.isConnected());
      }
    }
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseEntered(MouseEvent event) {
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
    int prevFocusedUserId = focusedUserId;
    focusedUserId = getUserAtPosition(event.getPoint());
    if (focusedUserId != prevFocusedUserId) {
        repaint();
    }
    if (userAction == ACTION_MOVING)
      move(event.getPoint(), true);
    else if (userAction == ACTION_ROTATING)
      rotate(event.getPoint(), true);
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event       the MouseEvent
 */

  public void mousePressed(MouseEvent event) {

    int positionX, positionY, angle;
    if (!event.isMetaDown()) {
      if (userAction == ACTION_NONE) {
        positionX = chatApplet.getCurrentUser().getPosition().x;
        positionY = chatApplet.getCurrentUser().getPosition().y;
        if (new Rectangle(positionX - ChatRepository.USER_SIZE / 2, positionY - ChatRepository.USER_SIZE / 2, ChatRepository.USER_SIZE, ChatRepository.USER_SIZE).contains(event.getPoint()))
          userAction = ACTION_MOVING;
        else
          userAction = ACTION_ROTATING;
      }
      if (userAction == ACTION_MOVING)
        move(event.getPoint(), false);
      else if (userAction == ACTION_ROTATING)
        rotate(event.getPoint(), false);
    }
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
    Enumeration roomIdEnum;
    Room room;

    if (event.isMetaDown()) {
      selectedUserId = getUserAtPosition(new Point(event.getX(), event.getY()));

      if (selectedUserId != -1) {
        if (selectedUserId == chatApplet.getCurrentUserId())
          ownUserPopup.show(this, event.getX(), event.getY());
        else {
          inviteUserMenu.removeAll();
          kickUserMenu.removeAll();
          roomIdEnum = chatApplet.getRoomIds();
          while (roomIdEnum.hasMoreElements()) {
            room = chatApplet.getRoom(((Integer)roomIdEnum.nextElement()).intValue());
            if (room.isAdministrator(chatApplet.getCurrentUser().getName())) {
              if (room.isPrivate())
                inviteUserMenu.add(room.getName());
              else
                kickUserMenu.add(room.getName());
            }
          }
          inviteUserMenu.setEnabled(inviteUserMenu.getItemCount() > 0);
          kickUserMenu.setEnabled(kickUserMenu.getItemCount() > 0);
          userPopup.show(this, event.getX(), event.getY());
        }
      }
      else {
        if (chatApplet.getCurrentRoom().isAdministrator(chatApplet.getCurrentUser().getName()))
          ownRoomPopup.show(this, event.getX(), event.getY());
        else
          roomPopup.show(this, event.getX(), event.getY());
      }
    }
    else {
      if (userAction == ACTION_MOVING)
        move(event.getPoint(), true);
      else if (userAction == ACTION_ROTATING)
        rotate(event.getPoint(), true);
      userAction = ACTION_NONE;
    }
  }


/**
 * Invoked when the mouse has been dragged on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseDragged(MouseEvent event) {
    if (!event.isMetaDown()) {
      if (userAction == ACTION_MOVING)
        move(event.getPoint(), false);
      else if (userAction == ACTION_ROTATING)
        rotate(event.getPoint(), false);
    }
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseMoved(MouseEvent event) {
    int prevFocusedUserId = focusedUserId;
    focusedUserId = getUserAtPosition(event.getPoint());
    if (focusedUserId != prevFocusedUserId) {
        repaint();
    }
  }


/**
 * Handles a User's movement.
 *
 * @param positionParam      the Point where the User moved to
 * @param send               determines whether the new position should be
 *                           broadcasted to other Users
 */

  public void move(Point positionParam, boolean send) {
    if (new Rectangle(getSize()).contains(positionParam)) {
      chatApplet.setUserPosition(chatApplet.getCurrentUser().getId(), positionParam, send);
    }
    // When the mouse has exited, we have to adjust the position
    else if (send) {
      if (new Rectangle(getSize()).contains(positionParam.x, 0)) {
        chatApplet.setUserPosition(chatApplet.getCurrentUser().getId(), new Point(positionParam.x, chatApplet.getCurrentUser().getPosition().y), send);
      }
      else if (new Rectangle(getSize()).contains(0, positionParam.y)) {
        chatApplet.setUserPosition(chatApplet.getCurrentUser().getId(), new Point(chatApplet.getCurrentUser().getPosition().x, positionParam.y), send);
      }
    }
    repaint();
  }


/**
 * Handles a change of a User's heading.
 *
 * @param positionParam      the Point where the User is facing
 * @param send               determines whether the new heading should be
 *                           broadcasted to other Users
 */

  public void rotate(Point point, boolean send) {
    chatApplet.setUserHeading(chatApplet.getCurrentUser().getId(), ChatUtil.getMiddleAngle(chatApplet.getCurrentUser().getHeading(), ChatUtil.getAngle(chatApplet.getCurrentUser().getPosition(), point)), send);
    repaint();
  }


}