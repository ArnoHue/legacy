package at.ac.uni_linz.tk.vchat;

import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import symantec.itools.awt.*;


/**
 * Provides the Chat's main client functionality, it administrates users and takes
 * control over UserEvents. The user interface shown in the applet area is the
 * LoginPanel only, the ChatPanel is part of an own Frame (ChatFrame).
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ChatApplet extends Applet implements ChatRepository, Runnable {

  protected LoginPanel loginPanel;
  private ChatFrame chatFrame;
  private ChatClient chatClient;

  private Image[] moodIcon, defaultAvatar;
  private Image emptyIcon, unknownIcon, defaultBackAvatar, imgSlider;

  private IntegerHashtable userTable, roomTable, userRoomTable;
  private int currentUserId;

  private Date histDate, chatStartDate;
  private boolean historyMode, chatRunning;
  private String status;
  private Simulator simulator;

  private HistoryPanel pnlHistory;
  private Hashtable hashHistory;
  private Thread thrMoodTimeout;

  private ImageCanvas logoCanvas;
  private MediaTracker tracker;
  private int whisperUserId = -1;

  private int trackerId = 0;

  public static String SERVER_POTRAIT_FILENAME[][] = { { "smiley6.gif", "smiley0.gif", "smiley1.gif", "smiley2.gif", "smiley3.gif", "smiley4.gif", "smiley5.gif" },
                                                             { "man1.gif", "man1.gif", "man1.gif", "man1.gif", "man1.gif", "man1.gif", "man1.gif" },
                                                             { "man2.gif", "man2.gif", "man2.gif", "man2.gif", "man2.gif", "man2.gif", "man2.gif" },
                                                             { "woman1.gif", "woman1.gif", "woman1.gif", "woman1.gif", "woman1.gif", "woman1.gif", "woman1.gif" },
                                                             { "penguin.gif", "penguin.gif", "penguin.gif", "penguin.gif", "penguin.gif", "penguin.gif", "penguin.gif" },
                                                             { "ninja.gif", "ninja.gif", "ninja.gif", "ninja.gif", "ninja.gif", "ninja.gif", "ninja.gif" },
                                                             { "girl.gif", "girl.gif", "girl.gif", "girl.gif", "girl.gif", "girl.gif", "girl.gif" },
                                                             { "pirate.gif", "pirate.gif", "pirate.gif", "pirate.gif", "pirate.gif", "pirate.gif", "pirate.gif" },
                                                             { "alien.gif", "alien.gif", "alien.gif", "alien.gif", "alien.gif", "alien.gif", "alien.gif" }
                                                             };
  public static String SERVER_POTRAIT_NAME[] = { "Smiley", "Man 1", "Man 2", "Woman 1", "Penguin", "Ninja", "Girl", "Pirate", "Alien" };

  private Random random = new Random();

  public class AvatarParam {
    public String name;
    public String[] image = new String[ChatRepository.PREDEFINED_NR_OF_MOODS + 1];
  }

  public class ChatAWTEvent extends AWTEvent {

    private Object receivedObject;

    public ChatAWTEvent(Object target, Object _receivedObject) {
      super(target, ChatApplet.CHAT_EVENT_ID);
      receivedObject = _receivedObject;
    }

    public Object getReceivedObject() {
      return receivedObject;
    }


  }

  protected boolean getBooleanParam(String name, boolean defaultVal) {
    return getParameter(name) == null ? defaultVal : new Boolean(getParameter(name)).booleanValue();
  }

  public static int CHAT_EVENT_ID = AWTEvent.RESERVED_ID_MAX + 1;


/**
 * Initializes the ChatApplet.
 */

  public void init() {
    enableEvents(ChatApplet.CHAT_EVENT_ID);
    String param;
    Vector avatars = new Vector();
    for (int i = 0; (param = getParameter("PredefinedAvatar[" + i + "]")) != null; i++) {
        int index = param.indexOf(":");
        if (index != -1) {
            AvatarParam avatar = new AvatarParam();
            avatar.name = param.substring(0, index);
            StringTokenizer name_list = new StringTokenizer(param.substring(index + 1), ",");
            for (int j = 0; j <= ChatRepository.PREDEFINED_NR_OF_MOODS; j++) {
                avatar.image[j] = name_list.hasMoreTokens() ? name_list.nextToken() : ( j > 1 ? avatar.image[1] : new String(""));
                // System.out.println(avatar.image[j]);
            }
            avatars.addElement(avatar);
        }
    }

    if (avatars.size() > 0) {
        SERVER_POTRAIT_NAME = new String[avatars.size()];
        SERVER_POTRAIT_FILENAME = new String[avatars.size()][ChatRepository.PREDEFINED_NR_OF_MOODS + 1];
        for (int i = 0; i < avatars.size(); i++) {
            SERVER_POTRAIT_NAME[i] = ((AvatarParam)avatars.elementAt(i)).name;
            for (int j = 0; j <= ChatRepository.PREDEFINED_NR_OF_MOODS; j++) {
                SERVER_POTRAIT_FILENAME[i][j] = ((AvatarParam)avatars.elementAt(i)).image[j];
            }
        }
    }

    logoCanvas = new ImageCanvas(getImage(LOGO_FILENAME, true), LOGO_DIMENSION);
    logoCanvas.showDocumentOnClick(this, LOGO_URL);

    moodIcon = new Image[PREDEFINED_NR_OF_MOODS];
    defaultAvatar = new Image[PREDEFINED_NR_OF_MOODS];

    defaultBackAvatar = getImage(SERVER_POTRAIT_FILENAME[0][0]);

    for (int i = 0; i < PREDEFINED_NR_OF_MOODS; i++) {
      moodIcon[i] = getImage(MOOD_ICON_NAME[i]);
      defaultAvatar[i] = getImage(SERVER_POTRAIT_FILENAME[0][i + 1]);
    }

    emptyIcon = getImage(EMPTY_ICON_NAME);
    unknownIcon = getImage(UNKNOWN_ICON_NAME);
    setBackground(CONTAINER_BACKGROUND);

    imgSlider = getImage(IMAGE_SLIDER, true);

    setLayout(new GridLayout());
    loginPanel = new LoginPanel(this, getParameter("LoginMode") == null ? LoginPanel.MODE_DEFAULT : ChatUtil.getInt(getParameter("LoginMode")));
    add(loginPanel);

    chatClient = new ChatClient(getHost(), this);

    startChat(getBooleanParam("DemoMode", true));
    if (getBooleanParam("AutoLogin", false) && getParameter("User") != null  && getParameter("Password") != null) {
        loginPanel.nameTextField.setText(getParameter("User"));
        loginPanel.passwordTextField1.setText(getParameter("Password"));
        loginPanel.passwordTextField2.setText(getParameter("Password"));
        loginPanel.actionPerformed(new ActionEvent(loginPanel.connectButton,ActionEvent.ACTION_PERFORMED,loginPanel.connectButton.getActionCommand()));
    }

  }


/**
 * Called by the browser or applet viewer to inform this applet that it is
 * being reclaimed.
 */

  public void destroy() {

    /*
     * Stop the chat on Netscape browsers
     */
    if (System.getProperty("java.vendor").indexOf("Netscape") != -1)
      stopChat();
  }


/**
 * Called by the browser or applet viewer to inform this applet that it should
 * stop its execution.
 */

  public void stop() {
    /*
     * Netscape is buggy on the stop-method() - it might also be called when
     * resizing the browser window
     */
    if (System.getProperty("java.vendor").indexOf("Netscape") == -1)
      stopChat();

  }

  public void setFrameVisibility(boolean visible) {
    chatFrame.setVisible(visible);
  }

/**
 * Starts the chat. Initializes variables, opens the ChatFrame and  runs the
 * simulator.
 */

  public void startChat(boolean demoMode) {
    User robot[];
    Room room;

    userTable = new IntegerHashtable();
    roomTable = new IntegerHashtable();
    hashHistory = new Hashtable();
    chatStartDate = new Date();

    for (int i = 0; i < STANDARD_ROOM_NAME.length; i++) {
        room = new Room(i, STANDARD_ROOM_NAME[i], ChatRepository.ROOM_DIMENSION);
        room.setAdministrator(ChatRepository.ADMIN);
        room.setPrivate(STANDARD_ROOM_PRIVATE[i]);
        room.setDemo(i != 0);
        if (STANDARD_ROOM_INVITED[i]) {
            room.inviteUser("Guest");
        }
        addRoom(room);
    }

    /*
    * Init the demo-version's Users
    */
    User user = new User(UNKNOWN_USER_ID, "Guest", "", Color.red, new Point (ROOM_DIMENSION.width / 2, ROOM_DIMENSION.height / 2), 0, 0, User.HUMAN_RACE);

    user.setBackAvatar(defaultBackAvatar);
    for (int i = 0; i < PREDEFINED_NR_OF_MOODS; i++)
        user.setAvatar(i, defaultAvatar[i]);

    setCurrentUser(user);


    if (demoMode) {

        /*
        * Start the simulator and add the Users
        */
        simulator = new Simulator(this);
        simulator.start();

        robot = new User[ROBOT_NAME.length];
        for (int i = 0; i < ROBOT_NAME.length; i++) {
        addUser(robot[i] = new User(ROBOT_USER_ID + i, ROBOT_NAME[i], "", new Color((int)(random.nextDouble() * MAX_COLOR_VALUE), (int)(random.nextDouble() * MAX_COLOR_VALUE), (int)(random.nextDouble() * MAX_COLOR_VALUE)), new Point ((int)(random.nextDouble() * 150), (int)(random.nextDouble() * 150)), (int)(random.nextDouble() * 360), 0, User.ROBOT_RACE));
        robot[i].setAvatar(0, getImage(ROBOT_PORTRAIT_FILENAME[i]));
        robot[i].setCommercialBanner(ROBOT_COMMERCIAL_BANNER[i]);
        simulator.addUser(robot[i]);
        }
    }

    chatFrame = new ChatFrame(this);
    chatFrame.setSize(800, 720);
    chatFrame.setVisible(demoMode);

    historyMode = false;
    setStatus("", true);

    chatRunning = true;
  }


/**
 * Closes a possible server-connection, stops the simulator and closes the
 * ChatFrame.
 */

  public void stopChat() {
    chatRunning = false;
    chatClient.disconnect();
    if (simulator != null && simulator.isAlive())
      simulator.stop();

    if (chatFrame != null) {
      chatFrame.setVisible(false);
      // chatFrame = null;
    }
  }


/**
 * Returns true in case the ChatClient is running.
 */

  public boolean chatRunning() {
    return chatRunning;
  }


/**
 * Fills up the RoomListPanel's XList with the Rooms' data.
 */

  public void fillRoomList() {
    if (chatFrame != null)
      chatFrame.fillRoomList();
  }


/**
 * Repaints the whole ChatFrame.
 */

  public void repaintAll() {
    if (chatFrame != null)
      chatFrame.repaintAll();
  }


/**
 * Repaints the ChatFrame's ViewCanvas.
 */

  public void repaintView() {
    if (chatFrame != null)
      chatFrame.repaintView();
  }


/**
 * Updates RoomListPanel's XList with the Rooms' data.
 */

  public void updateRoomList() {
    if (chatFrame != null)
      chatFrame.updateRoomList();
  }


/**
 * Repaints the ChatFrame's RoomPanel.
 */

  public void repaintRoom() {
    if (chatFrame != null)
      chatFrame.repaintRoom();
  }


/**
 * Repaints the ChatFrame's MoodCanvas.
 */

  public void repaintMood() {
    if (chatFrame != null)
      chatFrame.repaintMood();
  }


/**
 * Repaints the current User.
 */

  public void repaintUser() {
  }


/**
 * Adds a new User.
 *
 * @param userParam      the User to be added
 */

  public synchronized void addUser(User userParam) {
    userTable.put(userParam.getId(), userParam);
    moveUserToRoom(userParam.getId(), userParam.getRoom(), false);
    if (userParam.getRoom() == getCurrentRoomId()) {
      repaintRoom();
      if (userParam.getId() == getCurrentUserId() || inVisualRange(getCurrentUserId(), userParam.getId()))
        repaintView();
    }
  }


/**
 * Adds a new Room.
 *
 * @param roomParam      the Room to be added
 */

  public synchronized void addRoom(Room roomParam) {
    roomTable.put(roomParam.getId(), roomParam);
    repaintAll();
    fillRoomList();
  }


/**
 * Removes a User.
 *
 * @param idParam      the id of the User to be removed
 */

  public synchronized void removeUser(int idParam) {
    int roomId;

    if (idParam != getCurrentUserId() && userTable.containsKey(idParam)) {
      roomId = getUser(idParam).getRoom();
      getRoom(roomId).removeUser(getUser(idParam).getName());
      userTable.remove(idParam);
      updateRoomList();
      if (roomId == getCurrentRoomId()) {
        repaintRoom();
        repaintView();
      }
    }
  }


/**
 * Removes a Room.
 *
 * @param idParam      the Id of the Room to be removed
 */
  public synchronized void removeRoom(int roomIdParam) {
    removeRoom(roomIdParam, true);
  }

  public synchronized void removeRoom(int roomIdParam, boolean repaint) {
    if (getRoom(roomIdParam) != null && getRoom(roomIdParam).getNrOfUsers() == 0) {
      roomTable.remove(roomIdParam);
      if (repaint) {
        repaintAll();
        fillRoomList();
      }
    }
  }

  public void notifyConnected(boolean connected) {
    loginPanel.setConnected(connected);
  }

/**
 * Removes all but the current User from the chat system. Needed when connecting/
 * disconnecting to/from the chat server.
 */

  public synchronized void removeAllExceptCurrentUser() {
    User user;
    Enumeration roomIds;

    user = getCurrentUser();

    userTable = new IntegerHashtable();
    roomIds = getRoomIds();
    while (roomIds.hasMoreElements())
      getRoom(((Integer)roomIds.nextElement()).intValue()).removeAllUsers();

    addUser(user);
    repaintRoom();
    repaintView();
  }


/**
 * Removes User with "unknown" id (demo User).
 */

  public synchronized void removeUnknownUser() {
    if (getCurrentUserId() != UNKNOWN_USER_ID)
      removeUser(UNKNOWN_USER_ID);
  }


/**
 * Stops the simulator. Called when connecting to the chat server.
 */

  public void stopSimulator() {
    for (int i = 0; i < ROBOT_NAME.length; i++) {
      if (getUser(ROBOT_USER_ID + i) != null)
        removeUser(ROBOT_USER_ID + i);
    }
  }


/**
 * Removes all ROoms except the default Room.
 */

  public synchronized void removeAllExceptDefaultRoom() {
    Enumeration roomEnum;
    Room room;
    moveUserToRoom(currentUserId, 0, false);
    roomEnum = roomTable.elements();
    while (roomEnum.hasMoreElements()) {
      room = (Room)roomEnum.nextElement();
      if (room.getId() != 0)
        forceRoomRemoval(room.getId(), false, false);
    }
    repaintAll();
    fillRoomList();
  }


/**
 * Returns the User with a certain id.
 *
 * @param idParam      the User's id
 */

  public User getUser(int idParam) {
    Object object;
    object = userTable.get(idParam);
    if (object != null)
      return (User)object;
    else
      return null;
  }


/**
 * Sets the current User by his id. This User has to exist already.
 *
 * @param idParam      the User's id
 */

  public void setCurrentUserId(int idParam) {
    if (userTable.containsKey(idParam)) {
      currentUserId = idParam;
      moveUserToRoom(idParam, getUser(idParam).getRoom(), false);
      repaintView();
      repaintRoom();
      repaintMood();
      repaintCurrentUser();
    }
  }


/**
 * Returns the current User's id.
 */

  public int getCurrentUserId() {
    return currentUserId;
  }


/**
 * Returns the current User.
 */

  public User getCurrentUser() {
    return getUser(currentUserId);
  }


/**
 * Sets the current User.
 *
 * @param userParam      the User to be the new current User.
 */

  public void setCurrentUser(User userParam) {
    addUser(userParam);
    setCurrentUserId(userParam.getId());
  }


/**
 * Returns all Users as an IntegerHashtable.
 */

  public IntegerHashtable getAllUsers() {
    return userTable;
  }


/**
 * Sets the position of a certain User.
 *
 * @param idParam       the User's id
 * @param position      the new position (refer's to the room's dimension)
 * @param send          determines wheter the new position should be broadcasted
 */
  public void setUserPosition(int idParam, Point positionParam, boolean send) {
    setUserPosition(idParam, positionParam, send, true);
  }

  public void setUserPosition(int idParam, Point positionParam, boolean send, boolean checkForCollision) {
    User userParam, user;
    Room currentRoom;
    boolean wasInSight;

    if ((currentRoom = getCurrentRoom()) != null) {
      positionParam = new Point(Math.max(Math.min(positionParam.x, currentRoom.getSize().width - 2), 2), Math.max(Math.min(positionParam.y, currentRoom.getSize().height - 2), 2));
      userParam = getUser(idParam);

      if (userParam != null) {
    boolean collides = false;
    if (checkForCollision) {
          Enumeration userIdEnum = getRoomUserIdVector(getCurrentRoomId()).elements();
          while (userIdEnum.hasMoreElements() && !collides) {
            user = getUser(((Integer)userIdEnum.nextElement()).intValue());
            if (user != null && user != userParam) {
              collides = new Rectangle(user.getPosition().x - USER_SIZE / 2 - MINIMUM_DISTANCE, user.getPosition().y - USER_SIZE / 2 - MINIMUM_DISTANCE, USER_SIZE + MINIMUM_DISTANCE * 2, USER_SIZE + MINIMUM_DISTANCE * 2).contains(positionParam);
        }
          }
      }

        if (!checkForCollision || !collides) {
          wasInSight = inVisualRange(getCurrentUser().getId(), userParam.getId());
          userParam.setPosition(positionParam);

          if (wasInSight || inVisualRange(getCurrentUser().getId(), userParam.getId()) || userParam == getCurrentUser()) {
            repaintView();
      }

          repaintRoom();

          if (idParam != getCurrentUserId() && userParam.getRoom() == getCurrentRoomId()) {
            generateHistoryEntry();
        }

        }

        /*
        * Send the position, if the User collides send the last valid position
        */
        if (userParam == getCurrentUser() && send && isConnected()) {
          chatClient.send(new UserPositionEvent(userParam.getId(), userParam.getPosition()));
        }
      }
    }
  }


/**
 * Sets the heading of a certain User.
 *
 * @param idParam      the User's id
 * @param heading      the new heading
 * @param send         determines wheter the new heading should be broadcasted
 */

  public void setUserHeading(int idParam, int headingParam, boolean send) {
    User userParam;

    if ((userParam = getUser(idParam)) != null) {
      userParam.setHeading(headingParam);

      if (userParam == getCurrentUser() || inVisualRange(getCurrentUser().getId(), userParam.getId())) {
        repaintView();
      }
      repaintRoom();

      if (idParam != getCurrentUserId() && userParam.getRoom() == getCurrentRoomId()) {
        generateHistoryEntry();
      }

      if (userParam == getCurrentUser() && send && isConnected()) {
        chatClient.send(new UserHeadingEvent(userParam.getId(), userParam.getHeading()));
      }
    }
  }


/**
 * Returns the distance between a User's position and a certain point.
 *
 * @param idParam       the User
 * @param position      the position to calculate the distance to
 */

  public int getDistance(int idParam, Point position) {
    return getDistance(getUser(idParam).getPosition(), position);
  }

 /**
 * Returns the distance between a User's position and a certain point.
 *
 * @param idParam       the User
 * @param position      the position to calculate the distance to
 */

  public int getDistance(Point position1, Point position2) {
    return (int)Math.sqrt(Math.pow(position1.x - position2.x, 2) + Math.pow(position1.y - position2.y, 2));
  }


/**
 * Returns true if a position can be seen by a User.
 *
 * @param idParam       the id of the User watching
 * @param position      position to be seen
 */

  public boolean inVisualRange(int idParam, Point position) {
    try {
      return inVisualRange(getUser(idParam).getPosition(), getUser(idParam).getHeading(), position);
    }
    catch (Exception excpt) {
      return false;
    }
  }

  public boolean inVisualRange(Point position1, int headingParam, Point position2) {
    int distance, angle;
    distance = getDistance(position1, position2);
    angle = getAngle(position1, position2);
    return (distance <= VISUAL_RANGE && ChatUtil.inAngleRange(angle, ChatUtil.subAngle(headingParam, VISUAL_ANGLE / 2), ChatUtil.addAngle(headingParam, VISUAL_ANGLE / 2)));
  }


/**
 * Returns true if one User can be seen by another User.
 *
 * @param idParam1      the id of the User watching
 * @param idParam2      the id of the User to be seen
 */

  public boolean inVisualRange(int idParam1, int idParam2) {
    User user1, user2;
    try {
      user1 = getUser(idParam1);
      user2 = getUser(idParam2);

      if ((user1 != user2) && (user1.getRoom() == user2.getRoom())) {
        return inVisualRange(user1.getPosition(), user1.getHeading(), user2.getPosition());
      }
    }
    catch (Exception excpt) {
    }
    return false;
  }


/**
 * Returns true one User can listen to another User (means his avatar's balloon
 * is visible for the other User).
 *
 * @param idParam1      the User who listens
 * @param idParam2      position User to be listened to
 */

  public boolean inPhonicalRange(int idParam1, int idParam2) {
    User user1, user2;
    user1 = getUser(idParam1);
    user2 = getUser(idParam2);

    if ((user1 != user2) && (user1.getRoom() == user2.getRoom()))
      return inPhonicalRange(idParam1, user2.getPosition());
    else
      return false;
  }


/**
 * Returns true one User can listen to another User (means his avatar's balloon
 * is visible for the other User).
 *
 * @param idParam1      the User who listens
 * @param idParam2      position User to be listened to
 */

  public boolean inPhonicalRange(int idParam, Point pointParam) {
    User user;
    int distance, angle;
    user = getUser(idParam);

    distance = getDistance(idParam, pointParam);
    angle = getAngle(idParam, pointParam);
    return (distance <= PHONICAL_RANGE && ChatUtil.inAngleRange(angle, ChatUtil.subAngle(user.getHeading(), PHONICAL_ANGLE / 2), ChatUtil.addAngle(user.getHeading(),PHONICAL_ANGLE / 2)));
  }


/**
 * Determines the angle between a User and a point (&lt; 180 degrees).
 *
 * @param idParam       the id of the  User
 * @param position      the point to calculate the angle within
 */

  public int getAngle(int idParam, Point position) {
    return ChatUtil.getAngle(getUser(idParam).getPosition(), position);
  }

 /**
 * Determines the angle between a User and a point (&lt; 180 degrees).
 *
 * @param idParam       the id of the  User
 * @param position      the point to calculate the angle within
 */

  public int getAngle(Point position1, Point position2) {
    return ChatUtil.getAngle(position1, position2);
  }


/**
 * Determines the angle between two Users (&lt; 180 degrees).
 *
 * @param idParam1      the id of one User
 * @param idParam2      the id of another User to calculate the angle within
 */

  public int getAngle(int idParam1, int idParam2) {
    return ChatUtil.getAngle(getUser(idParam1).getPosition(), getUser(idParam2).getPosition());
  }


/**
 * Sets the message text of a certain User.
 *
 * @param idParam      the User's id
 * @param message      the new message text
 * @param send         determines wheter the new message should be broadcasted.
 */

  public void setUserMessage(int idParam, String messageParam, boolean send) {
    User user;
    user = getUser(idParam);
    if (user != null && ((messageParam != null && messageParam.length() > 0) || (user.getMessage() != null && user.getMessage().length() > 0))) {
      user.setMessage(messageParam);
      // Repaint in case it's the current User or the User can be seen
      if (user == getCurrentUser()) {
        if (send && isConnected()) {
          if (whisperUserId != -1) {
            chatClient.send(new UserWhisperEvent(user.getId(), messageParam, whisperUserId));
          }
          else {
            chatClient.send(new UserMessageEvent(user.getId(), messageParam));
          }
        }
        setUserMood(user.getId(), user.getMood(messageParam), send);
        repaintView();
      }
      else if (inVisualRange(getCurrentUser().getId(), user.getId())) {
        repaintView();
      }
      if (user.getRoom() == getCurrentRoomId()) {
        if (pnlHistory != null) {
          pnlHistory.addMessageEntry(new Date(), user != getCurrentUser() || whisperUserId == -1 ? messageParam : "[Whispers] " + messageParam, user.getColor());
          generateHistoryEntry();
        }
        if (chatFrame != null) {
          chatFrame.addMessage(user.getName(), user != getCurrentUser() || whisperUserId == -1 ? messageParam : "[Whispers] " + messageParam);
        }
      }
    }
    if (user == getCurrentUser() && whisperUserId != -1) {
      whisperUserId = -1;
      chatFrame.setMessageLabel("Chat:");
    }
  }


/**
 * Sets the mood of a certain User.
 *
 * @param idParam      the User's id
 * @param mood         the mood
 * @param send         determines wheter the new mood should be broadcasted.
 */

  public void setUserMood(int idParam, int moodParam, boolean send) {
    User user;
    user = getUser(idParam);

    if (user != null) {
      user.setMood(moodParam);

      // Repaint in case it's the current User or the User can be seen
      if (user == getCurrentUser()) {
        chatFrame.repaintMood();
        if (send && isConnected())
          chatClient.send(new UserMoodEvent(user.getId(), moodParam));
        if (user.getMoodTimeout() > 0) {
          if (thrMoodTimeout != null && thrMoodTimeout.isAlive())
            thrMoodTimeout.stop();
          thrMoodTimeout = new Thread(this);
          thrMoodTimeout.start();
        }
      }
      else if (inVisualRange(getCurrentUser().getId(), user.getId()))
        repaintView();

      if (user.getRoom() == getCurrentRoomId())
        generateHistoryEntry();
    }

   }


/**
 * Update's a User's data.
 *
 * @param user      the User to be updated
 * @param send      determines wheter the new User's data should be broadcasted
 */

  public void updateUser(User user, boolean send) {
    if (send) {
      chatClient.send(new UserUpdateEvent((User)user.clone(), true));
    }
    else {
      if (!isConnected()) {
        if (!user.getBackAvatarURL().equals(""))
          user.setBackAvatar(getImage(user.getBackAvatarURL()));
        else
          user.setBackAvatar(null);
        for (int i = 0; i < user.getNrOfMoods(); i++) {
          if (!user.getAvatarURL(i).equals(""))
            user.setAvatar(i, getImage(user.getAvatarURL(i)));
          else
            user.setAvatar(i, null);
        }
      }
      addUser(user);
      repaintAll();
      if (user.getId() == getCurrentUser().getId() && chatFrame != null) {
        chatFrame.repaintCurrentUser();
      }
    }
  }



/**
 * Update's a Room's data.
 *
 * @param room      the Room to be updated
 * @param send      determines wheter the new Room's data should be broadcasted
 */

  public void updateRoom(Room room, boolean send) {
    if (send) {
      if (room.getId() == NEW_ROOM_ID)
        chatClient.send(new RoomCreateEvent(room));
      else {
        chatClient.send(new RoomUpdateEvent(room));
        if (room.getId() == 0 && room.isAdministrator(getCurrentUser().getName())) {
        // reset lobby
        room.setInvitedUsers(new Vector());
        room.setKickedUsers(new Vector());
          chatFrame.editableRoomPanel.showRoom(room);
    }
    }
    }
    else {
      addRoom(room);
      if (room.isAdministrator(getCurrentUser().getName())) {
        if (chatFrame.editableRoomPanel.getRoomId() == room.getId())
          chatFrame.editableRoomPanel.showRoom(room);
        if (getCurrentRoomId() == NEW_ROOM_ID) {
          moveUserToRoom(getCurrentUserId(), room.getId(), isConnected());
          removeRoom(NEW_ROOM_ID);
          chatFrame.editableRoomPanel.showRoom(room);
        }
      }
      repaintAll();
    }
  }


/**
 * Removes a Room.
 *
 * @param roomId      the Id of the Room to be removed
 */

  public synchronized void forceRoomRemoval(int roomId, boolean send) {
    forceRoomRemoval(roomId, send, true);
  }

  public synchronized void forceRoomRemoval(int roomId, boolean send, boolean repaint) {
    Enumeration userEnum;
    Room room;

    if ((room = getRoom(roomId)) != null) {
      userEnum = getRoomUserIdVector(roomId).elements();
      while (userEnum.hasMoreElements()) {
        moveUserToRoom(((Integer)userEnum.nextElement()).intValue(), 0, send);
      }
      removeRoom(roomId, repaint);
      if (send)
        chatClient.send(new RoomRemoveEvent(roomId));
    }
  }


/**
 * Enters the history mode. In history mode, the messages and moods of a certain
 * period of time will be displayed.
 *
 * @param histDateParam      the start date of the history mode
 */

  public void enterHistoryMode(Date histDateParam) {
    historyMode = true;
    histDate = histDateParam;
    repaintView();
    repaintRoom();
  }


/**
 * Exists the history mode. User messages and moods will be displayed as flooding
 * in.
 */

  public void exitHistoryMode() {
    historyMode = false;
    histDate = null;
    repaintView();
    repaintRoom();
  }


/**
 * Returns true if the chat is running in history mode.
 */

  public boolean historyMode() {
    return historyMode;
  }


/**
 * Returns the start date of the history mode (in case history mode is activated).
 */

  public Date getHistoryDate() {
    return histDate;
  }


/**
 * Returns the start date of the chat, that is when the chat client started up.
 */

  public Date getChatStartDate() {
    return chatStartDate;
  }


/**
 * Shows a message in the status bar.
 *
 * @param statusString      the message to be shown
 */

  public void setStatus(String statusString) {
    setStatus(statusString, false);
  }


/**
 * Shows a message in the status bar and if - intended - in the login panel as
 * well.
 *
 * @param statusString      the message to be shown
 * @param login             true if the message should be shown in the login panel
 */

  public void setStatus(String statusString, boolean login) {
    status = statusString;
    if (chatFrame != null)
      chatFrame.setStatus(statusString);
    if (loginPanel != null && login)
      loginPanel.setStatus(statusString);
  }


/**
 * Returns the message that is currently shown in the status bar.
 */

 public String getStatus() {
   return status;
 }


/**
 * Returns a User's avatar.
 *
 * @param userIdParam      the User's id
 */

  public Image getUserAvatar(int userIdParam) {
    return getUserAvatar(userIdParam, getUser(userIdParam).getMood());
  };


/**
 * Returns a User's avatar for a certain mood.
 *
 * @param userIdParam      the User's id
 * @param moodParam        the User's mood
 */

  public Image getUserAvatar(int userIdParam, int moodParam) {
    Image portrait;

    portrait = getUser(userIdParam).getAvatar(moodParam);
    if (portrait == null) {
      portrait = moodParam < PREDEFINED_NR_OF_MOODS ? defaultAvatar[moodParam] : defaultAvatar[0];
    }
    return portrait;
  }


/**
 * Returns a User's back image.
 *
 * @param userIdParam      the User's id
 */

  public Image getUserBackAvatar(int userIdParam) {
    return getUser(userIdParam).getBackAvatar();
  }


/**
 * Returns the icon for a certain mood.
 *
 * @param moodParam      the mood
 */

  public Image getMoodIcon(int moodParam) {
    if (moodParam < PREDEFINED_NR_OF_MOODS) {
      return moodIcon[moodParam];
    }
    else {
      return unknownIcon;
    }
  }


/**
 * Returns the icon for user-defined moods.
 */

  public Image getUnknownIcon() {
    return unknownIcon;
  }


/**
 * Returns the icon for back-avatar.
 */

  public Image getEmptyIcon() {
    return emptyIcon;
  }



/**
 * Returns the host where the ChatApplet descends from.
 */

  public String getHost() {
    return getCodeBase().getHost();
  }


/**
 * Returns the default port where the ChatServer is listening.
 */

  public int getDefaultPort() {
    try {
      return getParameter("Port") != null ? new Integer(getParameter("Port")).intValue() : DEFAULT_PORT;
    }
    catch (NumberFormatException excpt) {
      return DEFAULT_PORT;
    }
  }


/**
 * Returns the ChatClient.
 */

  public ChatClient getClient() {
    return chatClient;
  }


/**
 * Requests that the browser or applet viewer show the Web page indicated by the
 * url argument.
 *
 * @param url         an absolute URL giving the location of the document
 * @param target      a String indicating where to display the page
 */

  public void showDocument(URL url, String target) {
    getAppletContext().showDocument(url, target);
  }


/**
 * Returns the Id of the current Room.
 */

  public int getCurrentRoomId() {
    try {
      return getCurrentUser().getRoom();
    }
    catch (Exception excpt) {
      return 0;
    }
  }


/**
 * Returns the current Room.
 */

  public Room getCurrentRoom() {
    return getRoom(getCurrentUser().getRoom());
  }


/**
 * Sets the Id of the current Room.
 *
 * @param roomIdParam      the Id of the current Room
 */

  public void setCurrentRoomId(int roomIdParam) {
    moveUserToRoom(getCurrentUserId(), roomIdParam, isConnected());
  }



/**
 * Returns the Room with a certain id.
 *
 * @param idParam      the Room's id
 */

  public Room getRoom(int idParam) {
    Object object;
    object = roomTable.get(idParam);
    if (object != null)
      return (Room)object;
    else
      return null;
  }


/**
 * Returns an Enumeration of the Rooms' Ids.
 */

  public Enumeration getRoomIds() {
    return roomTable.keys();
  }


/**
 * Returns a Rooms' Users' Ids as a Vector.
 *
 * @param roomId      the Room's id
 */

  public Vector getRoomUserIdVector(int roomId) {
    Room room;
    Vector roomUserIdVector;
    Enumeration userNameEnum;

    roomUserIdVector = new Vector();

    if ((room = getRoom(roomId)) != null) {
      userNameEnum = room.getUserNameVector().elements();

      while (userNameEnum.hasMoreElements()) {
        roomUserIdVector.addElement(new Integer(getUserId((String)userNameEnum.nextElement())));
      }
    }
    return roomUserIdVector;
  }


  public int getUserId(String userNameParam) {
    Enumeration userIdEnum;
    int id;

    userIdEnum = userTable.keys();

    while (userIdEnum.hasMoreElements()) {
      if (((User)userTable.get(id = ((Integer)userIdEnum.nextElement()).intValue())).getName().equals(userNameParam))
        return id;
    }

    return -1;
  }



/**
 * Moves a User to a Room.
 *
 * @param userIdParam      the Id of the User to be moved
 * @param roomIdParam      the Id of the Room to move the User to
 */

  public void moveUserToRoom(int userIdParam, int roomIdParam, boolean send) {
    Room room;
    User user;
    boolean bGenerateHistoryEntry, bRestartHistory;

    if (userTable.containsKey(userIdParam) && roomTable.containsKey(roomIdParam)) {
      room = getRoom(roomIdParam);
      user = getUser(userIdParam);
      if (room.hasAccess(user.getName())) {

        bRestartHistory = (user.getRoom() == getCurrentRoomId() && roomIdParam != getCurrentRoomId());
        bGenerateHistoryEntry = (user.getRoom() == getCurrentRoomId() || roomIdParam == getCurrentRoomId());

        if (getRoom(getUser(userIdParam).getRoom()) != null) {
          getRoom(user.getRoom()).removeUser(user.getName());
        }

        user.setRoom(roomIdParam);
        room.addUser(user.getName());

        if (userIdParam == getCurrentUserId()) {
          setStatus("Joined room \"" + room.getName() +"\"");
          if (room.isAdministrator(getCurrentUser().getName())) {
            chatFrame.editRoom(roomIdParam, false);
          }
          if (bRestartHistory) {
            restartHistory();
        chatFrame.clearMessage();
          }
          if (send)
            chatClient.send(new UserRoomEvent(userIdParam, roomIdParam, user.getPosition()));
        }

        repaintView();
        repaintRoom();
        updateRoomList();

        if (bGenerateHistoryEntry)
          generateHistoryEntry();

      }
      else if (userIdParam == getCurrentUserId()) {
        setStatus("You do not have access to room \"" + room.getName() +"\"");
      }
    }
  }


/**
 * Returns the number of Rooms.
 */

  public int getNrOfRooms() {
    return roomTable.size();
  }


/**
 * Returns the Id of a Room of a given name.
 */

  public int getRoomId(String roomNameParam) {
    Enumeration roomIds;
    int roomId;

    roomIds = getRoomIds();
    while (roomIds.hasMoreElements()) {
      roomId = ((Integer)roomIds.nextElement()).intValue();
      if (getRoom(roomId).getName().equals(roomNameParam))
        return roomId;
    }
    return -1;
  }

  public byte[] getResourceFromArchive(String strResource) {
    InputStream inStr;
    byte[] buffer;

    try {
      inStr = getClass().getResourceAsStream(strResource.startsWith("/") ? strResource : "/" + strResource);
      if (inStr == null) {
        return null;
      }
      buffer = new byte [4096];
      ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
      int bytes = inStr.read(buffer);
      while (bytes != -1) {
      out.write(buffer, 0, bytes);
      bytes = inStr.read(buffer);
    }
      return out.toByteArray();
    }
    catch (IOException excpt) {
      excpt.printStackTrace();
    }
    return null;
  }
/**
 * Retrieves an image from the webserver.
 *
 * @param name      the file name
 */

  public Image getImage(String name, boolean wait) {
    Image img;
    byte buffer[] = getResourceFromArchive(IMAGE_FOLDER + name);
    if (buffer != null) {
      img =  Toolkit.getDefaultToolkit().createImage(buffer);
    }
    else {
        img = getImage(getCodeBase(), IMAGE_FOLDER + name);
    if (wait) {
      synchronized(this) {
          if (tracker == null) {
          tracker = new MediaTracker(this);
        }
        tracker.addImage(img, ++trackerId);
        try {
        tracker.waitForID(trackerId, 1000);
        }
        catch (InterruptedException excpt) {
        excpt.printStackTrace();
        }
        }
    }
    }
    return img;
  }

  public Image getImage(String name) {
    return getImage(name, false);
  }

/**
 * Displays another User's data in a new Frame.
 *
 * @param userId      the Id of the User to be shown
 */

  public void showUser(int userIdParam) {
    if (chatFrame != null)
      chatFrame.showUser(userIdParam);
  }


/**
 * Updates User data in the User TabPanel, which can be edited.
 *
 * @param userId      the Id of the User to be edited
 */

  public void editUser(int userIdParam) {
    if (chatFrame != null)
      chatFrame.editUser(userIdParam);
  }


  public void whisperToUser(int userIdParam) {
    whisperUserId = userIdParam;
    if (chatFrame != null)
      chatFrame.setMessageLabel("Whisper to " + getUser(userIdParam).getName() + ":");
  }

/**
 * Displays a Room's data in a new Frame.
 *
 * @param roomId      the Id of the Room to be shown
 */

  public void showRoom(int roomIdParam) {
    if (chatFrame != null)
      chatFrame.showRoom(roomIdParam);
  }


/**
 * Updates Room data in the Room TabPanel, which can be edited by the Room's
 * Administrator.
 *
 * @param userId      the Id of the Room to be edited
 */

  public void editRoom(int roomIdParam) {
    if (chatFrame != null)
      chatFrame.editRoom(roomIdParam);
  }


/**
 * Creates a new Room and lets the User edit its data.
 */

  public void createRoom() {
    Room room;
    int roomId;

    roomId = isConnected() ? NEW_ROOM_ID : getNrOfRooms();
    room = new Room(roomId, "New Room", ROOM_DIMENSION);
    if (roomExists(room.getName())) {
      int i;
      for (i = 1; roomExists(room.getName() + i); i++);
      room.setName(room.getName() + i);
    }
    room.setAdministrator(getCurrentUser().getName());
    room.setDemo(!isConnected());
    addRoom(room);
    moveUserToRoom(getCurrentUserId(), roomId, false);
    if (chatFrame != null)
      chatFrame.editRoom(roomId);
    updateRoom(room, isConnected());
  }


/**
 * Repaint the current User. Should be called after his data has been updated.
 * Administrator.
 */

  public void repaintCurrentUser() {
    if (chatFrame != null)
      chatFrame.repaintCurrentUser();
  }


/**
 * Returns the Room with a certain name.
 *
 * @param roomNameParam      the Rooms's name
 */

  public Room getRoom(String roomNameParam) {
    return getRoom(getRoomId(roomNameParam));
  }


/**
 * Returns true if a Room of a certain name exists.
 *
 * @param roomNameParam      the Name of the Room
 */

  public boolean roomExists(String roomNameParam) {
    return getRoom(roomNameParam) != null;
  }


/**
 * Returns true if the ChatClient is currently connected to a ChatServer.
 */

  public boolean isConnected() {
    return chatClient != null && chatClient.connected();
  }


/**
 * Defines the ChatApplet's HistoryPanel. This Panel will receive
 * MessageEntries.
 *
 * @param pnlHistoryParam      the ChatApplet's HistoryPanel
 */

  public void setHistoryPanel(HistoryPanel pnlHistoryParam) {
    pnlHistory = pnlHistoryParam;
  }


/**
 * Returns an Image being used as the slider in the HistoryPanel's scrollbar.
 * MessageEntries.
 */

  public Image getSliderImage() {
    return imgSlider;
  }


/**
 * Brings the Chat TabPanel back to the foreground.
 */

  public void showChat() {
    if (chatFrame != null)
      chatFrame.showChat();
  }


/**
 * Stores the Chat's current situation into the history.
 */

  private void generateHistoryEntry() {
    hashHistory.put(new Date(), getCurrentSituationVector());
  }


/**
 * Returns a Vector which reflects the Chat's current situation.
 */

  public Vector getCurrentSituationVector() {
    Vector vecHistoryEntry, vecCurrentRoomUserIds;
    Date dateNow;
    User user;

    dateNow = new Date();
    vecHistoryEntry = new Vector();
    vecCurrentRoomUserIds = getRoomUserIdVector(getCurrentRoomId());
    for (int i = 0; i < vecCurrentRoomUserIds.size(); i++) {
      user = getUser(((Integer)vecCurrentRoomUserIds.elementAt(i)).intValue());
      if (user != null)
        vecHistoryEntry.addElement(new HistoryEntry(dateNow, getCurrentRoomId(), user.getId(), user.getPosition(), user.getHeading(), user.getColor(), user.getMessage(), user.getMood()));
    }
    return (Vector)vecHistoryEntry.clone();
  }


/**
 * Returns a Vector which reflects the Chat's situation at a certain point in time.
 *
 * @param dateParam      the Date to return the Vector for
 */

  public Vector getHistoryEntryVector(Date dateParam) {
    Object objHistory;
    HistoryEntry histEntry;
    Date date, dateEntry;
    Enumeration enumDate;

    date = new Date(0l);
    enumDate = hashHistory.keys();

    while (enumDate.hasMoreElements()) {
      dateEntry = (Date)enumDate.nextElement();
      if (dateEntry.getTime() < dateParam.getTime() && dateEntry.getTime() > date.getTime())
        date = dateEntry;
    }

    return ((objHistory = hashHistory.get(date)) != null) ? (Vector)(((Vector)objHistory)).clone() : new Vector();
  }


/**
 * Restarts the Chat's history.
 */

  public void restartHistory() {
    hashHistory = new Hashtable();
    if (pnlHistory != null)
      pnlHistory.clear();
  }


/**
 * Runs a Thread being used to timeout User moods.
 */

  public void run() {
    try {
      Thread.sleep(getCurrentUser().getMoodTimeout() * 1000);
    }
    catch (InterruptedException excpt) {
    }
    finally {
      setUserMood(getCurrentUserId(), 0, isConnected());
    }
  }


/**
 * Returns an incidently chosen User.
 */

  public User getRandomUser() {
    Vector roomUserIdVector;
    roomUserIdVector = getRoomUserIdVector(getCurrentRoomId());
    return getUser(((Integer)roomUserIdVector.elementAt((int)(roomUserIdVector.size() * random.nextFloat()))).intValue());
  }


/**
 * Returns the TK-Logo.
 */

  public ImageCanvas getLogo() {
    return (ImageCanvas)logoCanvas.clone();
  }


/**
 * Returns the default avatar for a certain mood.
 *
 * @param moodParam        the User's mood
 */

  public Image getDefaultAvatar(int moodParam) {
    return (moodParam >= 0 && moodParam < PREDEFINED_NR_OF_MOODS) ? defaultAvatar[moodParam] : null;
  }


/**
 * Returns the default back-side avatar.
 */

  public Image getDefaultBackAvatar() {
    return defaultBackAvatar;
  }

  public synchronized void setRoomTable(IntegerHashtable roomTableParam) {
    Room room;
    room = (Room)roomTable.get(0);
    roomTable = roomTableParam;
    roomTable.put(0, room);
    if (getParameter("Room") != null) {
        int roomId = getRoomId(getParameter("Room"));
        if (roomId != -1) {
            moveUserToRoom(getCurrentUserId(), roomId, true);
        }
    }
    else {
        moveUserToRoom(getCurrentUserId(), 0, false);
    }
    fillRoomList();
    repaintAll();
  }

  public void postReceivedObjectEvent(Object obj) {
    EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    eventQueue.postEvent(new ChatAWTEvent(this, obj));
  }

  protected void processEvent(AWTEvent event) {
    if (event instanceof ChatAWTEvent) {
      ChatAWTEvent ev = (ChatAWTEvent)event;
      try {
        Object receivedObject = ev.getReceivedObject();
        if (receivedObject instanceof UserPositionEvent)
          setUserPosition(((UserPositionEvent)receivedObject).userId, ((UserPositionEvent)receivedObject).userPosition, false, false);
        else if (receivedObject instanceof UserHeadingEvent)
          setUserHeading(((UserHeadingEvent)receivedObject).userId, ((UserHeadingEvent)receivedObject).userHeading, false);
        else if (receivedObject instanceof UserWhisperEvent)
          setUserMessage(((UserWhisperEvent)receivedObject).userId, "[Whispers] " + ((UserWhisperEvent)receivedObject).userMessage, false);
        else if (receivedObject instanceof UserMessageEvent)
          setUserMessage(((UserMessageEvent)receivedObject).userId, ((UserMessageEvent)receivedObject).userMessage, false);
        else if (receivedObject instanceof UserMoodEvent)
          setUserMood(((UserMoodEvent)receivedObject).userId, ((UserMoodEvent)receivedObject).userMood, false);
        else if (receivedObject instanceof UserRoomEvent) {
          UserRoomEvent evt = (UserRoomEvent)receivedObject;
          moveUserToRoom(evt.userId, evt.roomId, false);
          setUserPosition(evt.userId, evt.position, false, false);
        }
        else if (receivedObject instanceof UserUpdateEvent) {
          UserUpdateEvent evt = (UserUpdateEvent)receivedObject;
          updateUser(evt.user, false);
          if (evt.user.getId() == getCurrentUserId()) {
            setStatus(evt.statusString, false);
          }
        }
        else if (receivedObject instanceof UserLoginEvent)
          addUser(((UserLoginEvent)receivedObject).user);
        else if (receivedObject instanceof UserLogoutEvent) {
          removeUser(((UserLogoutEvent)receivedObject).userId);
        }
        else if (receivedObject instanceof RoomUpdateEvent) {
          setStatus("Received room update", true);
          updateRoom(((RoomUpdateEvent)receivedObject).room, false);
        }
        else if (receivedObject instanceof RoomCreateEvent) {
          setStatus("Received room creation", true);
          updateRoom(((RoomCreateEvent)receivedObject).room, false);
        }
        else if (receivedObject instanceof RoomListEvent) {
          setStatus("Received room list", true);
          setRoomTable(((RoomListEvent)receivedObject).roomTable);
        }
        else if (receivedObject instanceof RoomRemoveEvent) {
          setStatus("Received room removal", true);
          forceRoomRemoval(((RoomRemoveEvent)receivedObject).roomId, false);
        }
        else if (receivedObject instanceof Exception) {
          throw new Exception(((Exception)receivedObject).getMessage());
        }
      }
      catch (Exception excpt) {
        excpt.printStackTrace();
        System.out.println("Exception while receiving data: " + excpt + ". Going offline...");
        setStatus("Exception while receiving data. Going offline...", true);
        chatClient.disconnect();
      }
    }
    else {
      super.processEvent(event);
    }
  }

}
