package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;


/**
 * Main class for the server side's functionality. Starts a listener on the
 * defined port, opens socket connections as needed and handles UserEvents.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ChatServer implements Runnable {

  private WorkerThread worker;

  private static final String STORAGE_FOLDER = "users/";
  private static final String IMAGE_FOLDER = "images/";
  private static final String ROOM_FOLDER = "rooms/";
  private static final String ROOM_FILENAME = "rooms";
  private static final String KEY = "Admin";
  private static final String BANNED_IP_FILENAME = "banned_ip";

  public static final int LOGLEVEL0 = 0;
  public static final int LOGLEVEL1 = 1;
  public static final int LOGLEVEL2 = 2;
  public static final int LOGLEVEL3 = 3;
  public static final int DEFAULT_LOGLEVEL = LOGLEVEL1;
  public static final int STANDARD_LOGLEVEL = LOGLEVEL1;

  public static final int DELAY = 10000;
  public static final int ROOM_EXPIRATION = 2; // days

  private ServerSocket listener;
  private Vector connectionVector, bannedIPs;
  private Thread serverThread;
  private IntegerHashtable onlineTable, roomTable;

  private String key, storageFolder, imageFolder, roomFolder, roomFilename;
  private int logLevel;

  private Random rand = new Random();
  private Object loginLogoutLock = new Object();

  public static class LinkedList {
    private ChatServer server;
    public LinkedList(ChatServer _server) {
    server = _server;
    }
    public class LinkedElem {
      public Object obj;
      public LinkedElem next;
      public LinkedElem(Object _obj) {
        obj = _obj;
      }
    }

    private LinkedElem front;

    public synchronized Object popFront() {
    try {
      // server.log(Thread.currentThread() + " popFront <start>", 2);
      LinkedElem ret = front;
      if (ret == null) {
        return null;
      }
      front = front.next;
      ret.next = null;
      return ret.obj;
      }
      finally {
      // server.log(Thread.currentThread() + " popFront <done>", 2);
    }
    }

    public synchronized void pushBack(Object obj) {
    try {
      // server.log(Thread.currentThread() + " pushBack <start>", 2);
      LinkedElem elem = new LinkedElem(obj);
      if (front == null) {
        front = elem;
        return;
      }
      LinkedElem cur = front;
      while (cur.next != null) {
        cur = cur.next;
      }
      cur.next = elem;
      }
      finally {
      // server.log(Thread.currentThread() + " pushBack <done>", 2);
    }
    }

    public synchronized boolean isEmpty() {
      return front == null;
    }

  }

  // trial
  public static class WorkerThread implements Runnable {

    private Object myLock = new Object();
    private LinkedList pendingEvents;
    private ChatServer server;
    private boolean stopped;

    public class EventContainer {
      public Object event;
      public Connection conn;
      public EventContainer(Object _event, Connection _conn) {
        event = _event;
        conn = _conn;
      }
    }

    public WorkerThread(ChatServer _server) {
      server = _server;
      pendingEvents = new LinkedList(server);
      new Thread(this).start();
      stopped = false;
    }

    public void run() {
      while (!stopped) {
        synchronized(myLock) {
          if (pendingEvents.isEmpty()) {
            // server.log(Thread.currentThread() + " run <pendingEvents empty>", 2);
            try {
              myLock.wait();
              // server.log(Thread.currentThread() + " run <myLock signaled>", 2);
            }
            catch (InterruptedException e) {
            }
          }
        }
        if (!pendingEvents.isEmpty()) {
          // server.log(Thread.currentThread() + " run <pendingEvents exist>", 2);
          EventContainer cont = (EventContainer)pendingEvents.popFront();
          try {
      // server.log(Thread.currentThread() + " Received " + cont.event, LOGLEVEL2);
            // server.log(Thread.currentThread() + " run <handleUserEventInternal start>", 2);
            server.handleUserEventInternal(cont.event, cont.conn);
            // server.log(Thread.currentThread() + " run <handleUserEventInternal done>", 2);
      }
      catch (Exception e) {
      e.printStackTrace();
      }
          catch (Error err) {
      err.printStackTrace();
          }
      }
      }
    }
    public void handleEvent(Object obj, Connection connection) {
      // server.log(Thread.currentThread() + " handleEvent <start>", 2);
      pendingEvents.pushBack(new EventContainer(obj, connection));
      synchronized(myLock) {
        myLock.notify();
      }
      // server.log(Thread.currentThread() + " handleEvent <done>", 2);
    }
    public void stop() {
      stopped = true;
      synchronized(myLock) {
        myLock.notify();
      }
    }
  }

  public class EventContainer {
    public Object event;
    public Connection conn;
    public EventContainer(Object _event, Connection _conn) {
      event = _event;
      conn = _conn;
    }
  }


/**
 * Starts the ChatServer.
 *
 * @param args[]      command line arguments - the first argument is the server
 *                    key
 */

  public static void main(String args[]) {
    int port;

    try {
      port = (args.length >= 1) ? new Integer(args[0]).intValue() : ChatRepository.DEFAULT_PORT;
    }
    catch (NumberFormatException excpt) {
      port = ChatRepository.DEFAULT_PORT;
    }
    if (args.length >= 1 && (args[0].indexOf("?") != -1 || args[0].toLowerCase().indexOf("help") != -1)) {
      System.out.println("Visual Chat Server");
      System.out.println("Usage: java [-DVCHAT_USERLIST=filename] chat.ChatServer [ port [ serverkey [ storagefolder [ imagefolder [ roomfolder [ roomfilename [ loglevel ] ] ] ] ] ] ]");
      System.out.println("Hint: Store banned ip-addresses in a file named \"banned_ip\"");
    }
    else {
      new ChatServer(port, (args.length >= 2) ? args[1] : KEY, (args.length >= 3) ? args[2] : STORAGE_FOLDER, (args.length >= 4) ? args[3] : IMAGE_FOLDER, (args.length >= 5) ? args[4] : ROOM_FOLDER, (args.length >= 6) ? args[5] : ROOM_FILENAME, (args.length >= 7) ? ChatUtil.getInt(args[6]) : DEFAULT_LOGLEVEL);
    }
  }


/**
 * Constructs the ChatServer.
 *
 * @param portParam                the port the server is running on
 * @param keyParam                 the server key
 * @param storageFolderParam       the foldername for storing user-files
 * @param imageFolderParam         the foldername for retrieving images
 * @param roomFolderParam          the foldername for storing user-files
 * @param roomFileNameParam        the foldername for retrieving images
 * @param logLevelParam            the logLevel
 */

  public ChatServer(int portParam, String keyParam, String storageFolderParam, String imageFolderParam, String roomFolderParam, String roomFilenameParam, int logLevelParam) {
    Object loadedObject;
    Room room;

    key = keyParam;
    storageFolder = storageFolderParam + (storageFolderParam.endsWith("/") ? "" : "/");
    imageFolder = imageFolderParam + (imageFolderParam.endsWith("/") ? "" : "/");
    roomFolder = roomFolderParam + (roomFolderParam.endsWith("/") ? "" : "/");
    roomFilename = roomFilenameParam;
    logLevel = logLevelParam;

    try {
      new File(storageFolder).mkdirs();
      new File(imageFolder).mkdirs();
      new File(roomFolder).mkdirs();

      listener = new ServerSocket(portParam);
      connectionVector = new Vector();
      onlineTable = new IntegerHashtable();

      if (!exists(ChatRepository.ADMIN)) {
        saveUser(new User(ChatRepository.ADMIN, keyParam));
        log("Created admin user...");
      }
      if (new File(roomFolder + roomFilename).exists()) {
        try {
          roomTable = (IntegerHashtable)load(roomFolder + roomFilename);
          Vector expiredRooms = new Vector();
          GregorianCalendar expirationCal = new GregorianCalendar();
          expirationCal.add(Calendar.DATE, -ROOM_EXPIRATION);
//          log("Deadline at " + DateFormat.getDateInstance(DateFormat.SHORT).format(expirationCal.getTime()));
          for (Enumeration enm = roomTable.elements(); enm.hasMoreElements();) {
            Room myRoom = (Room)enm.nextElement();
//            log("Last access on room " + myRoom.getName() + " at " + DateFormat.getDateInstance(DateFormat.SHORT).format(myRoom.getLastAccess().getTime()));
            if (myRoom.getId() != 0 && myRoom.getLastAccess().before(expirationCal)) {
//            if (myRoom.getId() != 0 && myRoom.getLastAccess().before(expirationCal)) {
              expiredRooms.addElement(myRoom);
            }
          }
          for (Enumeration enm = expiredRooms.elements(); enm.hasMoreElements();) {
            Room myRoom = (Room)enm.nextElement();
            roomTable.remove(myRoom.getId());
          }
          saveRooms();
        }
        catch (Exception excpt) {
          log(excpt.toString());
          createStandardRooms();
        }
      }
      else {
        createStandardRooms();
      }

      try {
        roomTable = (IntegerHashtable)load(roomFolder + roomFilename);
      }
      catch (Exception excpt) {
        log(excpt.toString());
        createStandardRooms();
      }
      bannedIPs = new Vector();
      try {
        StringBuffer content = new StringBuffer(1024);
        StringTokenizer tokenizer;
        char ch;
        BufferedInputStream is = null;
        try {
          is = new BufferedInputStream(new FileInputStream(new File(BANNED_IP_FILENAME)));
          while ((ch = (char)is.read()) != (char)-1) {
            content.append(ch);
          }
        }
        finally {
          if (is != null) {
            is.close();
          }
        }
        tokenizer = new StringTokenizer(content.toString(), System.getProperty("line.separator"));
        while(tokenizer.hasMoreTokens()) {
          bannedIPs.addElement(tokenizer.nextToken().trim());
        }
      }
      catch (Exception excpt) {
        log(excpt.toString());
      }

      Thread gcThread = new Thread(new Runnable() {
      public void run() {
        while (true) {
          try {
            Thread.sleep(60000);
          }
          catch (InterruptedException e) {
          }
          System.gc();
        }
      }
    });
    gcThread.start();

      log("Listening on port " + portParam + "...");
      log("Storage folder is " + storageFolder + "...");
      log("Image folder is " + imageFolder + "...");
      log("Room folder is " + roomFolder + "...");
      log("Room filename is " + roomFilename + "...");
      log("Log level is " + logLevel + "...");
      log("Nr of banned ip-addresses read from " +  BANNED_IP_FILENAME + ": " + bannedIPs.size());

      worker = new WorkerThread(this);

      serverThread = new Thread(this);
      serverThread.start();
      log("ChatServer up and running...");
    }
    catch (IOException excpt) {
      log("IOException while starting ChatServer: " + excpt);
    }
    saveUserList();
    // new PingThread(this).start();
  }


/**
 * Runs a thread that listenes on the defined port to new socket connections.
 * Starts a new thread for each Connection.
 */

  public void run() {
    Connection connection;
    while (true) {
      try {
        connection = new Connection(this, listener.accept());
        connection.start();
        log("Opened connection to " + connection.getSocket().getInetAddress());
      }
      catch (Exception excpt) {
        log("Exception while opening a new connection: " + excpt);
      }
    catch (Error err) {
    log("Error while opening a new connection: " + err);
    }
    }
  }

  private Room createDeepCopy(Room srcRoom) {
    Room room = new Room(srcRoom.getId(), new String(srcRoom.getName()), new Dimension(srcRoom.getSize().width, srcRoom.getSize().height));
    room.setAdministrator(new String(srcRoom.getAdministrator()));
    room.setInfo(new String(srcRoom.getInfo()));
    room.setRules(new String(srcRoom.getRules()));
    room.setPrivate(srcRoom.isPrivate());
    room.setLastAccess(new GregorianCalendar());
    for (int i = 0; i < srcRoom.getUserNameVector().size(); i++) {
        room.addUser(new String((String)srcRoom.getUserNameVector().elementAt(i)));
    }
    for (int i = 0; i < srcRoom.getInvitedUsers().size(); i++) {
        room.inviteUser(new String((String)srcRoom.getInvitedUsers().elementAt(i)));
    }
    for (int i = 0; i < srcRoom.getKickedUsers().size(); i++) {
        room.kickUser(new String((String)srcRoom.getKickedUsers().elementAt(i)));
    }
    return room;
  }

  public boolean isBannedIP(String ipAddress) {
    for (int i = 0; i < bannedIPs.size(); i++) {
        if (ipAddress.startsWith((String)bannedIPs.elementAt(i))) {
            return true;
        }
    }
    return false;
  }

  public void handleUserEvent(Object object, Connection connection) {
    worker.handleEvent(object, connection);
  }
/**
 * Handles incoming UserEvents or UserLoginRequests.
 *
 * @param object          the UserEvent or UserLoginRequest
 * @param connection      the Connection that received the UserEvent
 */
  private void handleUserEventInternal(Object object, Connection connection) {
    ExistingUserLoginRequest existingUserLogin;
    NewUserLoginRequest newUserLogin;
    User user;
    Room room;
    Connection suspendedConnection;
    String userName;

    room = null;

    if (object instanceof UserLoginRequest) {
      if (isBannedIP(connection.getIPAddress())) {
        UserLoginRequest req = ((UserLoginRequest)object);
        req.status = UserLoginRequest.DENIED;
        req.statusString = "IP-Address has been banned";
        log("Login for new user " + req.user.getName() + " denied: Banned IP-Address");
        send(object, connection);
      }

      else {
        if (object instanceof ExistingUserLoginRequest) {
          existingUserLogin = (ExistingUserLoginRequest)object;
          log("Login for user " + existingUserLogin.user.getName() + " received");
          User loadedUser = loadUser(existingUserLogin.user.getName());
          boolean exists = loadedUser != null;
          boolean pwdMatches = loadedUser != null && loadedUser.getPassword().equals(existingUserLogin.user.getPassword());
          if (!exists || !pwdMatches) {
            existingUserLogin.status = UserLoginRequest.DENIED;
            if (!exists) {
              existingUserLogin.statusString = "User does not exist. Correct name or login as new user";
              log("Login for user " + existingUserLogin.user.getName() + " denied: User does not exist");
            }
            else if (!pwdMatches) {
              existingUserLogin.statusString = "Wrong password. Correct password or create new user";
              log("Login for user " + existingUserLogin.user.getName() + " denied: Wrong password");
            }
            send(existingUserLogin, connection);
          }
          else {
            log("Login for user " + existingUserLogin.user.getName() + " accepted");
            existingUserLogin.status = UserLoginRequest.ACCEPTED;
            existingUserLogin.statusString = "Everything ok";
            existingUserLogin.user = loadedUser;

            if (isOnline(existingUserLogin.user.getName())) {
              log("Closing old connection of user " + existingUserLogin.user.getName());
              closeConnection(getConnection(getOnlineUserId(existingUserLogin.user.getName())));
            }
            handleAcceptedUserLoginRequest(existingUserLogin, connection);
          }
        }

        else if (object instanceof NewUserLoginRequest) {
          newUserLogin = (NewUserLoginRequest)object;
          log("Login for user " + newUserLogin.user.getName() + " received");
          if (exists(newUserLogin.user.getName())) {
            newUserLogin.status = UserLoginRequest.DENIED;
            newUserLogin.statusString = "User exists. Choose different name or login as existing user";
            log("Login for new user " + newUserLogin.user.getName() + " denied: Already existing");
            send(newUserLogin, connection);
          }
          else {
            newUserLogin.status = UserLoginRequest.ACCEPTED;
            newUserLogin.statusString = "Everything ok";
            log("Login for new user " + newUserLogin.user.getName() + " accepted");
            saveUser(newUserLogin.user);
            handleAcceptedUserLoginRequest(newUserLogin, connection);
          }
        }
      }
    }
    else if (object instanceof UserUpdateEvent) {
      UserUpdateEvent userUpdate = (UserUpdateEvent)object;
      if (!userUpdate.done) {
        log("Update for user " + userUpdate.user.getName() + " received");
        new UserUpdateThread(userUpdate, this, connection).start();
      }
      else {
        handleUserUpdateInternal(userUpdate, connection);
      }
    }

    else if (object instanceof RoomEvent) {
      RoomEvent roomEvent;
      Enumeration userEnum;

      if (object instanceof RoomUpdateEvent || object instanceof RoomCreateEvent) {
        room = object instanceof RoomUpdateEvent ? ((RoomUpdateEvent)object).room : ((RoomCreateEvent)object).room;
        if (object instanceof RoomCreateEvent) {
          if (roomExists(room.getName())) {
            int i;
            for (i = 1; roomExists(room.getName() + i); i++);
            room.setName(room.getName() + i);
          }
        }

        if (object instanceof RoomUpdateEvent) {
          roomEvent = (RoomUpdateEvent)object;
          if (room.getId() != 0) {
            addRoom(room);
          }
          roomEvent = new RoomUpdateEvent(room);
          log("Handling room update for room " + room.getName() + " from user " + getOnlineUser(connection.getUserId()).getName());
        }
        else {
          roomEvent = (RoomCreateEvent)object;
          room.removeAllUsers();
          setNextAvailableRoomIdAndAdd(room);
          roomEvent = new RoomCreateEvent(room);
          log("Handling room creation for room " + room.getName() + " from user " + getOnlineUser(connection.getUserId()).getName());
        }

        if (room.getId() != 0) {
          broadcast(roomEvent);
        }

        synchronized(onlineTable) {
          userEnum = onlineTable.elements();
          while(userEnum.hasMoreElements()) {
            user = (User)userEnum.nextElement();

            if (room.getId() == 0 && ChatRepository.ADMIN.equals(getOnlineUser(connection.getUserId()).getName()) && room.isKicked(user.getName())) {
              log("User " + user.getName() + " is kicked out");
              Connection conn = getConnection(user.getId());
              if (conn != null) {
                bannedIPs.addElement(new String(conn.getIPAddress()));
                closeConnection(conn);
                // save banned IP's
                try {
                  BufferedOutputStream os = null;
                  try {
                    os = new BufferedOutputStream(new FileOutputStream(new File(BANNED_IP_FILENAME)));
                    for (Enumeration enm = bannedIPs.elements(); enm.hasMoreElements();) {
                      os.write(enm.nextElement().toString().getBytes("ISO-8859-1"));
                      os.write(System.getProperty("line.separator").getBytes("ISO-8859-1"));
                    }
                  }
                  finally {
                    if (os != null) {
                      os.close();
                    }
                  }
                }
                catch (IOException e) {
                  log("Exception while saving banned IPs: " + e.getMessage());
                }
              }
            }
            else if (room.getId() != 0 && user.getRoom() == room.getId() && !room.hasAccess(user.getName())) {
              log("User " + user.getName() + " is moved back to the lobby");
              user.setRoom(0);
              user.setPosition(getAvailablePosition(0));
              broadcast(new UserRoomEvent(user.getId(), user.getRoom(), user.getPosition()));
            }
          }
        }
      }
      else if (object instanceof RoomRemoveEvent) {
        room = getRoom(((RoomRemoveEvent)object).roomId);
        if (room != null && room.getId() != 0) {
          synchronized(onlineTable) {
            userEnum = onlineTable.elements();
            while(userEnum.hasMoreElements()) {
              user = (User)userEnum.nextElement();
              if (user.getRoom() == room.getId()) {
                log("User " + user.getName() + " is moved back to the lobby");
                user.setRoom(0);
                user.setPosition(getAvailablePosition(0));
                broadcast(new UserRoomEvent(user.getId(), user.getRoom(), user.getPosition()));
              }
            }
          }
          removeRoom(room.getId());
          broadcast(object);
        }
      }
      saveRooms();
    }

    else if (object instanceof UserEvent) {
      boolean broadcastExcluding;
      broadcastExcluding = true;

      user = getOnlineUser(((UserEvent)object).userId);
      if (user != null) {
        if (object instanceof UserPositionEvent) {
          user.setPosition(((UserPositionEvent)object).userPosition);
        }
        else if (object instanceof UserHeadingEvent) {
          user.setHeading(((UserHeadingEvent)object).userHeading);
        }
        else if (object instanceof UserWhisperEvent) {
          log("User " + user.getName() + " whispers " + ((UserWhisperEvent)object).userMessage);
          send(object, getConnection(((UserWhisperEvent)object).receivingUserId));
          return;
        }
        else if (object instanceof UserMessageEvent) {
          user.setMessage(((UserMessageEvent)object).userMessage);
          log("User " + user.getName() + " says " + user.getMessage());
        }
        else if (object instanceof UserMoodEvent) {
          user.setMood(((UserMoodEvent)object).userMood);
        }
        else if (object instanceof UserRoomEvent) {
          room = getRoom(((UserRoomEvent)object).roomId);
          if (room != null) {
            getRoom(room.getId()).removeUser(user.getName());
            user.setRoom(room.getId());
            room.addUser(user.getName());
            room.setLastAccess(new GregorianCalendar());
            // saveRooms();
              // logSemaObtaining("availablePosLock", "handleUserEvent()");
              // synchronized(availablePosLock) {
               // logSemaObtained("availablePosLock", "handleUserEvent()");
            user.setPosition(getAvailablePosition(room.getId()));
              // }
            ((UserRoomEvent)object).position = user.getPosition();
          }
          broadcastExcluding = false;
        }
        if (object instanceof UserLogoutEvent) {
          closeConnection(connection);
        }
        else {
          addOnlineUser(user);
          if (broadcastExcluding) {
            broadcastExcluding(object, connection);
          }
          else {
            broadcast(object);
          }
        }
      }
    }

    else if (object instanceof ServerAdministrationRequest) {
      if (key != null && key.equals(((ServerAdministrationRequest)object).key)) {
        ((ServerAdministrationRequest)object).status = ServerAdministrationRequest.ACCEPTED;

        if (object instanceof ServerStopRequest) {
          Enumeration connectionEnum;
          Connection closingConnection;
          String[] command;

          try {
            log("Stopping server...");

            listener.close();
            serverThread.stop();

            // logSemaObtaining("connectionVector", "handleUserEvent()");
            synchronized(connectionVector) {
              // logSemaObtained("connectionVector", "handleUserEvent()");
              connectionEnum = connectionVector.elements();
              while (connectionEnum.hasMoreElements()) {
                closingConnection = (Connection)connectionEnum.nextElement();
                if (closingConnection.getUserId() != 0)
                  closeConnection(closingConnection);
              }
            }

            saveUserList();
            Runtime.getRuntime().exit(0);

          }
          catch (IOException excpt) {
            log("Exception while stopping server: " + excpt);
          }
        }
        else if (object instanceof ServerUserListRequest) {
          final ServerUserListRequest req = (ServerUserListRequest)object;
          final Connection adminConn = connection;
          if (req.userListVector == null) {
            new Thread(new Runnable() {
                public void run() {
                  log("Received ServerUserListRequest...");
                  String[] userFileList;
                  SortedStringVector userListVector;

                  userListVector = new SortedStringVector(1000);

                  log("Retrieving user list...");
                  userFileList = new File(storageFolder).list();
                  for (int i = 0; i < userFileList.length; i++) {
                  userListVector.addElement(userFileList[i]);
                  }

                  log("Sorting user list...");
                  userListVector.sort();

                  req.userListVector = userListVector;
                  handleUserEvent(req, adminConn);
                }
            }).start();
          }
          else {
            log("Sending user list...");
            send(req, adminConn);
          }
        }
        else if (object instanceof ServerGetUserDataRequest) {
          ServerGetUserDataRequest req = (ServerGetUserDataRequest)object;
          log("Retrieving data for user " + req.user.getName());
          user = loadUser(req.user.getName());
          if (user != null) {
            req.user.setPassword(user.getPassword());
            req.user.setEmail(user.getEmail());
          }
          else {
            log("Error: User " + req.user.getName() + " doesn't exist");
            req.status = ServerAdministrationRequest.ERROR_OCCURED;
          }
          send(object, connection);
        }
        else if (object instanceof ServerUpdateUserDataRequest) {
          ServerUpdateUserDataRequest req = (ServerUpdateUserDataRequest)object;
          log("Updating data for user " + req.user.getName());
          user = loadUser(req.user.getName());
          if (user != null) {
            user.setPassword(req.user.getPassword());
            user.setEmail(req.user.getEmail());
            saveUser(user);
            if (isOnline(user.getName())) {
              addOnlineUser(user);
              User userCpy = (User)user.clone();
              userCpy.setPassword("");
              handleUserUpdateInternal(new UserUpdateEvent(userCpy), getConnection(user.getId()));
            }
          }
          else {
            log("Error: User " + req.user.getName() + " doesn't exist");
            req.status = ServerAdministrationRequest.ERROR_OCCURED;
          }
          send(object, connection);
        }
        else if (object instanceof ServerDeleteUserRequest) {
          ServerDeleteUserRequest req = (ServerDeleteUserRequest)object;
          log("Deleting user " + req.userName);
          userName = req.userName;
          if (exists(userName)) {
            if (isOnline(userName))
              closeConnection(getConnection(getOnlineUserId(userName)));
            deleteUser(userName);
          }
          else {
            log("Error: User " + req.userName + " doesn't exist");
            req.status = ServerAdministrationRequest.ERROR_OCCURED;
          }
          send(object, connection);
        }
      }
      else {
        log("Wrong server key");
        ((ServerAdministrationRequest)object).status = ServerAdministrationRequest.DENIED;
        send(object, connection);
      }
    }
  }


/**
 * Handles a UserLoginRequest that has been accepted.
 *
 * @param loginRequest      the UserLoginRequest
 * @param connection        the Connection that received the UserLoginRequest
 */

  private void handleAcceptedUserLoginRequest(UserLoginRequest loginRequest, Connection connection) {
    User user;
    Room room;
    UserLoginEvent loginEvent;
    Enumeration userEnum, roomEnum;

    loginRequest.user.setRoom(0);
    loginRequest.user.setLoginDate(new Date());
    loginRequest.user.setPosition(getAvailablePosition(0));
    setNextAvailableUserIdAndAdd(loginRequest.user);
    connection.setUserId(loginRequest.user.getId());
    send(loginRequest, connection);

    // logSemaObtaining("roomTable", "handleAcceptedUserLoginRequest()");
    synchronized(roomTable) {
        // logSemaObtained("roomTable", "handleAcceptedUserLoginRequest()");
        send(new RoomListEvent(roomTable), connection);
    }

    Vector users = new Vector();

    // logSemaObtaining("onlineTable", "handleAcceptedUserLoginRequest()");
    synchronized(onlineTable) {
        // logSemaObtained("onlineTable", "handleAcceptedUserLoginRequest()");
        userEnum = onlineTable.elements();

        while(userEnum.hasMoreElements()) {
            users.addElement(userEnum.nextElement());
        }
    }

    for (Enumeration enm = users.elements(); enm.hasMoreElements(); ) {
        User online_user = (User)enm.nextElement();
        online_user = (User)online_user.clone();
        online_user.setPassword("");
        if (online_user.getId() != loginRequest.user.getId()) {
            send(new UserLoginEvent(online_user), connection);
        }
    }

    // log("Obtaining connectionVector for user " + loginRequest.user.getName());
    synchronized(connectionVector) {
        connectionVector.addElement(connection);
    }
    // log("Releasing connectionVector for user " + loginRequest.user.getName());

    User loginUser = (User)loginRequest.user.clone();
    loginUser.setPassword("");
    loginEvent = new UserLoginEvent(loginUser);
    // log("Broadcasting");
    broadcastExcluding(loginEvent, connection);
    // log("Done broadcasting");
    saveUserList();
  }


/**
 * Handles an UserUpdateEvent.
 *
 * @param updateEvent      the UserUpdateEvent
 * @param connection       the Connection that received the UserUpdateEvent
 */

  private void handleUserUpdateInternal(UserUpdateEvent updateEvent, Connection connection) {
    saveUser(updateEvent.user);
    addOnlineUser(updateEvent.user);
    send(updateEvent, connection);
    User cpyUser = (User)updateEvent.user.clone();
    cpyUser.setPassword("");
    UserUpdateEvent updateEventCpy = new UserUpdateEvent(cpyUser, false);
    updateEventCpy.statusString = "";
    updateEventCpy.done = true;
    broadcastExcluding(updateEventCpy, connection);
  }



/**
 * Broadcasts a UserEvent over all except one Connection.
 *
 * @param broadcastObject         the UserEvent to be broadcasted
 * @param excludeConnection       the Connection to be excluded from the broadcast
 */

  private void broadcastExcluding(Object broadcastObject, Connection excludeConnection) {
    Enumeration connectionEnum;
    Connection broadcastConnection;

    log("Broadcasting...", LOGLEVEL3);
    // logSemaObtaining("connectionVector", "broadcastExcluding()");
    connectionEnum = getConnectionEnum(excludeConnection);
    // logSemaObtained("connectionVector", "broadcastExcluding()");
    while (connectionEnum.hasMoreElements()) {
      broadcastConnection = (Connection)connectionEnum.nextElement();
      broadcastConnection.send(broadcastObject);
    }
  }


  private Enumeration getConnectionEnum(Connection exclConn) {
    Connection conn;
    Enumeration enm;
    Vector localVec = new Vector();

    synchronized(connectionVector) {
      // logSemaObtained("connectionVector", "broadcastExcluding()");
      enm = connectionVector.elements();
      while (enm.hasMoreElements()) {
        conn = (Connection)enm.nextElement();
        if (conn.getUserId() != 0 && conn != exclConn)
          localVec.addElement(conn);
      }
    }
    return localVec.elements();
  }


/**
 * Broadcasts a UserEvent over all Connections.
 *
 * @param broadcastObject      the UserEvent to be broadcasted
 */

  private void broadcast(Object broadcastObject) {
    broadcastExcluding(broadcastObject, null);
  }


/**
 * Sends a UserEvent or UserLoginRequest over one connection.
 *
 * @param broadcastObject      the UserEvent to be broadcasted
 */

  private void send(Object broadcastObject, Connection connection) {
    if (connection != null) {
      log("Sending...", LOGLEVEL3);
      connection.send(broadcastObject);
    }
  }


/**
 * Closes a Connection.
 *
 * @param connection      the Connection to be closed
 */

  private void closeConnection(Connection connection) {
    if (connection != null) {
      int id = 0;
      User user = null;
      try {
        id = connection.getUserId();
        user = getOnlineUser(id);
        removeOnlineUser(id);
        if (user != null) {
          log("Closing connection of " + user.getName());
        }
      }
      catch (Exception excpt) {
        log("Exception while closing connection: " + excpt);
        excpt.printStackTrace();
      }
      try {
        connection.close();
      }
      catch (Exception excpt) {
        log("Exception while closing connection: " + excpt);
        excpt.printStackTrace();
      }
      try {
        synchronized(connectionVector) {
          connectionVector.removeElement(connection);
        }
        if (id != 0) {
          broadcastExcluding(new UserLogoutEvent(id), connection);
          if (user != null) {
            Room room = getRoom(user.getRoom());
            if (room != null) {
              room.removeUser(user.getName());
            }
          }
        }
      }
      catch (Exception excpt) {
        log("Exception while closing connection: " + excpt);
        excpt.printStackTrace();
      }
      saveUserList();
    }
  }


/**
 * Returns the connection of a certain User currently being logged in.
 *
 * @param userIdParam      the User's id
 */

  private Connection getConnection(int userIdParam) {
    Enumeration connectionEnum;
    Connection connection;

    // logSemaObtaining("connectionVector", "getConnection()");
    synchronized(connectionVector) {
     // logSemaObtained("connectionVector", "getConnection()");
     connectionEnum = connectionVector.elements();

      while (connectionEnum.hasMoreElements()) {
        connection = (Connection)connectionEnum.nextElement();
        if (connection.getUserId() == userIdParam)
          return connection;
      }
    }

    return null;
  }


/**
 * Returns true if a Room of a given name exists.
 *
 * @param roomNameParam      the Room's name
 */

  private boolean roomExists(String roomNameParam) {
    Enumeration roomEnum;

    // logSemaObtaining("roomTable", "roomExists()");
    synchronized(roomTable) {
      if (ChatRepository.STANDARD_ROOM_NAME[0].equals(roomNameParam)) {
         return true;
      }
      // logSemaObtained("roomTable", "roomExists()");
      roomEnum = roomTable.elements();

      while(roomEnum.hasMoreElements()) {
        if (((Room)roomEnum.nextElement()).getName().equals(roomNameParam))
          return true;
      }
    }
    return false;
  }


/**
 * Returns the true if a certain User is curently logged in.
 *
 * @param userIdParam      the User's id
 */

  private boolean isOnline(int userIdParam) {
    return onlineTable.containsKey(userIdParam);
  }


/**
 * Returns the true if a certain User is curently logged in.
 *
 * @param userNameParam      the User's name
 */

  private boolean isOnline(String userNameParam) {
    return getOnlineUserId(userNameParam) != -1;
  }


/**
 * Returns dynamic id of a User currently online or -1 if he is not online.
 *
 * @param userNameParam      the User's name
 */

  private int getOnlineUserId(String userNameParam) {
    Enumeration userEnum;
    User user;

    // logSemaObtaining("onlineTable", "getOnlineUserId()");
    synchronized(onlineTable) {
      // logSemaObtained("onlineTable", "getOnlineUserId()");
      userEnum = onlineTable.elements();

      while(userEnum.hasMoreElements()) {
        if (((user = (User)userEnum.nextElement())).getName().equals(userNameParam))
          return user.getId();
      }
    }
    return -1;
  }


/**
 * Returns the true if a User's name exists already.
 *
 * @param userNameParam      the User's name
 */

  private boolean exists(String userNameParam) {
    return new File(storageFolder + userNameParam).exists();
  }


/**
 * Stores a User to the ChatServer's local file system. The User's name will be
 * used as the filename.
 *
 * @param userParam      the User to be stored
 */

  private void saveUser(User userParam) {
    save(userParam, storageFolder + userParam.getName());
  }


/**
 * Loads a User from the ChatServer's local file system.
 *
 * @param userNameParam      the name of the User to be loaded
 */

  public User loadUser(String userNameParam) {
    Object loadedObject;
    loadedObject = load(storageFolder + userNameParam);
    if (loadedObject instanceof User)
      return (User)loadedObject;
    else
      return null;
  }


/**
 * Deletes a User from the ChatServer's local file system.
 *
 * @param userNameParam      the name of the User to be loaded
 */

  private void deleteUser(String userNameParam) {
    new File(storageFolder + userNameParam).delete();
  }


/**
 * Returns the next available User id. The id will be created dynamically.
 */

  private void setNextAvailableUserIdAndAdd(User user) {
    // logSemaObtaining("onlineTable", "getNextAvailableUserId()");
    synchronized(onlineTable) {
      // logSemaObtained("onlineTable", "getNextAvailableUserId()");
      int id;
      do {
        id = rand.nextInt();
      } while (onlineTable.containsKey(id));
      user.setId(id);
      addOnlineUser(user);
    }
  }


/**
 * Returns the next available Room id. The id will be created dynamically.
 */

  private void setNextAvailableRoomIdAndAdd(Room room) {
    synchronized(roomTable) {
      room.setId(Math.max(1, roomTable.getMaxKey() + 1));
      addRoom(room);
    }
  }


/**
 * Returns whether a password is correct or not.
 */

  private boolean correctPassword(String userNameParam, String userPasswordParam) {
    Object loadedObject;
    loadedObject = loadUser(userNameParam);
    if (loadedObject instanceof User)
      return ((User)loadedObject).getPassword().equals(userPasswordParam);
    else
      return false;
  }


/**
 * Stores an object under a given filename to the ChatServer's local file system.
 *
 * @param objectParam      the object to be stored
 * @param fileName         the filename under which to store the object
 */

  private void save(Object objectParam, String fileName) {
    ObjectOutputStream output = null;
    try {
      output = new ObjectOutputStream(new FileOutputStream(fileName));
      output.writeObject(objectParam);
    }
    catch (IOException excpt) {
      log("IOException while writing file " + fileName);
    }
    finally {
      try {
        if (output != null) {
          output.close();
        }
      }
      catch (Exception excpt) {
      }
    }
  }


  private void saveUserList() {
    String vchat_userlist = (String)System.getProperties().get("VCHAT_USERLIST");
    if (vchat_userlist != null && vchat_userlist.length() > 0) {
      FileOutputStream output = null;
      try {
        output = new FileOutputStream(vchat_userlist);
        // logSemaObtaining("onlineTable", "saveUserList()");
        synchronized(onlineTable) {
          // logSemaObtained("onlineTable", "saveUserList()");
          Enumeration userEnum = onlineTable.elements();
          byte[] buffer;
          while (userEnum.hasMoreElements()) {
            buffer = ("(" + ((User)userEnum.nextElement()).getName() + ")").getBytes();
            output.write(buffer);
            output.write('\n');
          }
        }
      }
      catch (IOException excpt) {
        log("IOException while writing file " + vchat_userlist);
      }
      finally {
        if (output != null) {
          try {
            output.close();
          }
          catch (Exception excpt) {
          }
        }
      }
    }
  }

/**
 * Loads an object from the ChatServer's local file system.
 *
 * @param fileName      the filename under which the object is stored
 */

  private Object load(String fileName) {
    ObjectInputStream input = null;
    Object object;
    object = null;

    try {
      input = new ObjectInputStream(new FileInputStream(fileName));
      object = input.readObject();
    }
    catch (ClassNotFoundException excpt) {
      log("ClassNotFoundException while reading file " + fileName);
    }
    catch (IOException excpt) {
      log("IOException while reading file " + fileName);
    }
    finally {
      try {
        if (input != null) {
          input.close();
        }
      }
      catch (Exception excpt) {
      }
    }
    return object;
  }


/**
 * Adds a User to the list of currently logged in users.
 *
 * @param userParam      the User to be added
 */

  private void addOnlineUser(User userParam) {
    // logSemaObtaining("onlineTable", "addOnlineUser()");
    synchronized(onlineTable) {
      // logSemaObtained("onlineTable", "addOnlineUser()");
      onlineTable.put(userParam.getId(), userParam);
    }
  }


/**
 * Removes a User from the list of currently logged in users.
 *
 * @param idParam      the id of the User to be removed
 */

  private void removeOnlineUser(int idParam) {
    // logSemaObtaining("onlineTable", "removeOnlineUser()");
    synchronized(onlineTable) {
      // logSemaObtained("onlineTable", "removeOnlineUser()");
      onlineTable.remove(idParam);
    }
  }


/**
 * Adds a Room.
 *
 * @param roomParam      the Room to be added
 */

  private void addRoom(Room roomParam) {
    // logSemaObtaining("roomTable", "addRoom()");
    synchronized(roomTable) {
      // logSemaObtained("roomTable", "addRoom()");
      roomTable.put(roomParam.getId(), roomParam);
    }
  }


/**
 * Removes a Room.
 *
 * @param idParam      the id of the Room to be removed
 */

  private void removeRoom(int idParam) {
    // logSemaObtaining("roomTable", "removeRoom()");
    synchronized(roomTable) {
      // logSemaObtained("roomTable", "removeRoom()");
      roomTable.remove(idParam);
    }
  }


/**
 * Returns a User from the list of currently logged in users.
 *
 * @param idParam      the id of the User to be returned
 */

  private User getOnlineUser(int idParam) {
    Object object;
    object = onlineTable.get(idParam);
    if (object != null)
      return (User)object;
    else
      return null;
  }


/**
 * Returns a Room.
 *
 * @param idParam      the id of the Room to be returned
 */

  private Room getRoom(int idParam) {
    Object object;
    object = roomTable.get(idParam);
    if (object != null)
      return (Room)object;
    else
      return null;
  }


/**
 * Returns a free position in a certain Room to place the User after logging in.
 *
 * @param roomid      the number of the Room to place the User
 */

  private Point getAvailablePosition(int roomId) {
    Point position, userPosition;
    Enumeration userEnum;

    do {
      position = new Point(Math.abs(rand.nextInt()) % (ChatRepository.ROOM_DIMENSION.width - 10) + 5, Math.abs(rand.nextInt()) % (ChatRepository.ROOM_DIMENSION.height - 10) + 5);
    }
    while (collides(roomId, position, -1));

    return position;
  }


/**
 * Checks whether a Point in a Room collides with a User being placed there.
 *
 * @param roomid             the number of the Room
 * @param position           the position to be checked
 * @param excludeUserId      exclude the position of this User
 */

  private boolean collides(int roomId, Point position, int excludeUserId) {
    Enumeration userEnum;
    User user;

    // logSemaObtaining("onlineTable", "collides()");
    synchronized(onlineTable) {
      // logSemaObtained("onlineTable", "collides()");
      userEnum = onlineTable.elements();
      while (userEnum.hasMoreElements()) {
        if ((user = (User)userEnum.nextElement()).getRoom() == roomId && user.getId() != excludeUserId) {
          if (new Rectangle(user.getPosition().x - ChatRepository.USER_SIZE / 2 - ChatRepository.MINIMUM_DISTANCE, user.getPosition().y - ChatRepository.USER_SIZE / 2 - ChatRepository.MINIMUM_DISTANCE, ChatRepository.USER_SIZE + ChatRepository.MINIMUM_DISTANCE * 2, ChatRepository.USER_SIZE + ChatRepository.MINIMUM_DISTANCE * 2).contains(position))
            return true;
        }
      }
    }
    return false;
  }

/**
 * Writes a timestamp and logging information to the standard output.
 *
 * @param logText      the text to be logged
 */

  public synchronized void log(String logText) {
    log(logText, STANDARD_LOGLEVEL);
  }


/**
 * Writes a timestamp and logging information to the standard output.
 *
 * @param logText      the text to be logged
 * @param level        the logLevel
 */

  public synchronized void log(String logText, int level) {
    if (level <= logLevel) {
      System.out.println(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + " " + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()) + ": " + logText);
    }
  }


/**
 * Returns the folder where images should be stored.
 */

  public String getImageFolder() {
    return imageFolder;
  }


/**
 * Creates the standard Rooms. Is being called after starting the ChatServer for
 * the first time.
 */

  private void createStandardRooms() {
    Room room;
    roomTable = new IntegerHashtable();
    for (int i = 0; i < ChatRepository.STANDARD_ROOM_NAME.length; i++) {
      room = new Room(i, ChatRepository.STANDARD_ROOM_NAME[i], ChatRepository.ROOM_DIMENSION);
      room.setPrivate(ChatRepository.STANDARD_ROOM_PRIVATE[i]);
      room.setAdministrator(ChatRepository.ADMIN);
      addRoom(room);
    }
    log("Created standard rooms...");
    saveRooms();
  }


/**
 * Persistently stores all the Rooms to the server's file system.
 */

  private void saveRooms() {
    synchronized(roomTable) {
      save(roomTable, roomFolder + roomFilename);
    }
    log("Saved rooms...");
  }

  /*
  public void logSemaObtaining(Object semaphore, String context) {
    logSemaEvent(semaphore, "tries to obtain", context);
  }

  public void logSemaObtained(Object semaphore, String context) {
    logSemaEvent(semaphore, "obtains", context);
  }

  public void logSemaReleasing(Object semaphore, String context) {
    logSemaEvent(semaphore, "tries to release", context);
  }

  public void logSemaReleased(Object semaphore, String context) {
    logSemaEvent(semaphore, "releases", context);
  }

  public void logSemaEvent(Object semaphore, String action, String context) {
    System.out.println("-----> " + Thread.currentThread() + " " + action + " semaphore " + semaphore + " from " + context);
  }
  */
}