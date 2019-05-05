package at.ac.uni_linz.tk.vchat;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Used for remote administration of the ChatServer. An administrator can connect
 * and stop or restart the ChatServer.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ServerAdministrationApplet extends Applet implements Runnable, ActionListener, ItemListener {

  private Thread clientThread;
  private Socket clientSocket;

  private ObjectOutputStream output;
  private ObjectInputStream input;

  private Button connectButton, disconnectButton, stopButton, deleteUserButton, updateUserButton, getUserListButton;
  private TextField keyField, portField;
  private Label keyLabel;
  private String host;
  private boolean connected;

  private FramedPanel connectionPanel, userPanel;
  private Choice userListChoice;
  private TextField passwordField, emailField;


/**
 * Initializes the ServerAdministrationApplet.
 */

  public void init() {
    enableEvents(ServerAdministrationApplet.ADMIN_EVENT_ID);
    GridBagConstraints constraints;

    connectionPanel = new FramedPanel("Connection", getInsets());
    userPanel = new FramedPanel("User", getInsets());

    setBackground(Color.lightGray);
    connectionPanel.setLayout(new GridBagLayout());

    addWithBeginningConstraints(connectionPanel, new Label("Server-Key:", Label.RIGHT), getInsets());
    addWithRemainingConstraints(connectionPanel, keyField = new TextField(20), getInsets());
    addWithBeginningConstraints(connectionPanel, new Label("Port:", Label.RIGHT), getInsets());
    addWithRemainingConstraints(connectionPanel, portField = new TextField(5), getInsets());

    keyField.setEchoChar('*');

    connectButton = new Button("Connect");
    getUserListButton = new Button("Get User List");
    disconnectButton = new Button("Disconnect");
    stopButton = new Button("Stop Server");

    constraints = new GridBagConstraints();
    constraints.insets = getInsets();
    constraints.weightx = 1.0;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth = 1;

    addWithConstraints(connectionPanel, connectButton, constraints);
    addWithConstraints(connectionPanel, getUserListButton, constraints);
    addWithConstraints(connectionPanel, stopButton, constraints);
    addWithConstraints(connectionPanel, disconnectButton, constraints);

    userPanel.setLayout(new GridBagLayout());
    addWithBeginningConstraints(userPanel, new Label("User:", Label.RIGHT), getInsets());
    addWithRemainingConstraints(userPanel, userListChoice = new Choice(), getInsets());
    addWithBeginningConstraints(userPanel, new Label("Password:", Label.RIGHT), getInsets());
    addWithRemainingConstraints(userPanel, passwordField = new TextField(), getInsets());
    addWithBeginningConstraints(userPanel, new Label("E-Mail:", Label.RIGHT), getInsets());
    addWithRemainingConstraints(userPanel, emailField = new TextField(), getInsets());


    addWithConstraints(userPanel, updateUserButton = new Button("Update User"), constraints);
    addWithConstraints(userPanel, deleteUserButton = new Button("Delete User"), constraints);

    setLayout(new BorderLayout());
    add(connectionPanel, "North");
    add(userPanel, "Center");

    connectButton.addActionListener(this);
    getUserListButton.addActionListener(this);
    disconnectButton.addActionListener(this);
    stopButton.addActionListener(this);

    userListChoice.addItemListener(this);
    updateUserButton.addActionListener(this);
    deleteUserButton.addActionListener(this);

    host = getCodeBase().getHost();
    portField.setText(getParameter("Port"));
    disconnect();
  }


/**
 * Adds a Component to a Container's GridBagLayout.
 *
 * @param containerParam         the Container to add to
 * @param componentParam         the Comnponent to be added
 * @param constraintsParam       the GridBagConstraints to use for adding
 */

  public static void addWithConstraints(Container containerParam, Component componentParam, GridBagConstraints constraintsParam) {
    if (!(containerParam.getLayout() instanceof GridBagLayout))
      containerParam.setLayout(new GridBagLayout());
    ((GridBagLayout)containerParam.getLayout()).setConstraints(componentParam, constraintsParam);
    containerParam.add(componentParam);
  }


/**
 * Adds a Component to the beginning of a new row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithBeginningConstraints(Container containerParam, Component componentParam, Insets insetsParam) {
    GridBagConstraints constraints;
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(insetsParam.top / 2, insetsParam.left / 2, insetsParam.bottom / 2, insetsParam.right / 2);
    constraints.fill = GridBagConstraints.BOTH;
    addWithConstraints(containerParam, componentParam, constraints);
  }


/**
 * Adds a Component to the beginning of a new row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithBeginningConstraints(Container containerParam, Component componentParam) {
    addWithBeginningConstraints(containerParam, componentParam, ChatRepository.INSETS);
  }


/**
 * Adds a Component to the remainings of a row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithRemainingConstraints(Container containerParam, Component componentParam, Insets insetsParam) {
    GridBagConstraints constraints;
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(insetsParam.top / 2, insetsParam.left / 2, insetsParam.bottom / 2, insetsParam.right / 2);
    constraints.weightx = 1.0;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    addWithConstraints(containerParam, componentParam, constraints);
  }


/**
 * Adds a Component to the remainings of a row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithRemainingConstraints(Container containerParam, Component componentParam) {
    addWithRemainingConstraints(containerParam, componentParam, ChatRepository.INSETS);
  }


/**
 * Disconnects from the ChatServer.
 */

  public void disconnect() {
    try {

      connected = false;

      if (clientSocket != null)
        clientSocket.close();
      if (output != null)
        output.close();
      if (input != null)
        input.close();

      connectButton.setEnabled(true);
      getUserListButton.setEnabled(false);
      disconnectButton.setEnabled(false);
      stopButton.setEnabled(false);
      userPanel.setEnabled(false);

      System.out.println("Disconnected");
    }
    catch (IOException excpt) {
      System.out.println("Exception while disconnecting: " + excpt);
    }
  }


/**
 * Connects to the ChatServer.
 */

  public void connect() {
    try {
      System.out.println("Connecting...");
      clientSocket = new Socket(host, new Integer(portField.getText()).intValue());

      input = new ObjectInputStream(clientSocket.getInputStream());
      output = new ObjectOutputStream(clientSocket.getOutputStream());

      clientThread = new Thread(this);
      clientThread.start();

      connected = true;

      connectButton.setEnabled(false);
      getUserListButton.setEnabled(true);
      disconnectButton.setEnabled(true);
      stopButton.setEnabled(true);

      System.out.println("Connected");
    }
    catch (IOException excpt) {
      System.out.println("Exception while connecting: " + excpt);
    }
  }


/**
 * Invoked when an action occurs.
 *
 * @param event      the ActionEvent
 */

  public void actionPerformed(ActionEvent event) {
    User user;
    if (event.getSource() == stopButton) {
      send(new ServerStopRequest(keyField.getText()));
    }
    else if (event.getSource() == getUserListButton) {
      send(new ServerUserListRequest(keyField.getText()));
    }
    else if (event.getSource() == updateUserButton) {
      user = new User(userListChoice.getSelectedItem(), passwordField.getText());
      user.setEmail(emailField.getText());
      send(new ServerUpdateUserDataRequest(keyField.getText(), user));
    }
    else if (event.getSource() == deleteUserButton) {
      send(new ServerDeleteUserRequest(keyField.getText(), userListChoice.getSelectedItem()));
    }
    else if (event.getSource() == connectButton) {
      connect();
    }
    else if (event.getSource() == disconnectButton) {
      disconnect();
    }
  }


/**
 * Invoked when an item's state has been changed.
 *
 * @param event      the ItemEvent
 */

  public void itemStateChanged(ItemEvent event) {
    if (event.getSource() == userListChoice)
      send(new ServerGetUserDataRequest(keyField.getText(), userListChoice.getSelectedItem()));
  }


/**
 * Runs a thread that checks the connection status. The connect- and
 * disconnect-Buttons will be enabled/disabled depending on this status.
 */

  public void run() {
    Object receivedObject;

    while (connected) {
      try {
        receivedObject = input.readObject();
        postReceivedObjectEvent(receivedObject);
      }
      catch (Exception excpt) {
        postReceivedObjectEvent(excpt);
      }
    }
  }


/**
 * Sends data over the OutputStream openend to the ChatServer.
 *
 * @param sendObject      the object to be sent
 */

  public void send(Object sendObject) {
    try {
      if (connected) {
        System.out.println("Sending: " + sendObject);
        output.writeObject(sendObject);
      }
    }
    catch (IOException excpt) {
      System.out.println("Exception while sending data: " + excpt + ". Going offline...");
      disconnect();
    }
  }


/**
 * Called by the browser or applet viewer to inform this applet that it should
 * stop its execution.
 */

  public void stop() {
    disconnect();
  }


/**
 * Determines the Insets, which indicate the size of the Container's border.
 */

  public Insets getInsets() {
    return new Insets(10, 10, 10, 10);
  }

  public void postReceivedObjectEvent(Object obj) {
    EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    eventQueue.postEvent(new AdminAWTEvent(this, obj));
  }

  protected void processEvent(AWTEvent event) {
    if (event instanceof AdminAWTEvent) {
      AdminAWTEvent ev = (AdminAWTEvent)event;
      try {
        User user;
        Object receivedObject = ev.getReceivedObject();
        if (receivedObject instanceof ServerAdministrationRequest) {
          switch (((ServerAdministrationRequest)receivedObject).status) {
            case ServerAdministrationRequest.ACCEPTED:
              System.out.println(receivedObject + " has been accepted");
              break;
            case ServerAdministrationRequest.DENIED:
              System.out.println(receivedObject + " has been denied");
              break;
            case ServerAdministrationRequest.ERROR_OCCURED:
              System.out.println("An error occured while handling " + receivedObject);
              break;
            case ServerAdministrationRequest.REQUESTED:
              System.out.println(receivedObject + " has not been processed");
              break;
          }

          if (((ServerAdministrationRequest)receivedObject).status == ServerAdministrationRequest.ACCEPTED) {

            if (receivedObject instanceof ServerUserListRequest) {
              SortedStringVector userListVector = ((ServerUserListRequest)receivedObject).userListVector;
              userListChoice.setVisible(false);
              userListChoice.removeAll();

              for (int i = 0; i < userListVector.size(); i++)
                userListChoice.add((String)userListVector.elementAt(i));

              if (userListChoice.getItemCount() > 0) {
                userListChoice.select(0);
                send(new ServerGetUserDataRequest(keyField.getText(), userListChoice.getItem(0)));
              }
              userListChoice.setVisible(true);

              userPanel.setEnabled(true);

            }
            else if (receivedObject instanceof ServerGetUserDataRequest) {
              user = ((ServerGetUserDataRequest)receivedObject).user;
              userListChoice.select(user.getName());
              passwordField.setText(user.getPassword());
              emailField.setText(user.getEmail());
            }
            else if (receivedObject instanceof ServerDeleteUserRequest) {
              send(new ServerUserListRequest(keyField.getText()));
            }
            else if (receivedObject instanceof Exception) {
              throw new Exception(((Exception)receivedObject).getMessage());
            }
          }
        }
      }
      catch (Exception excpt) {
        System.out.println("Exception while receiving data: " + excpt + ". Going offline...");
        disconnect();
      }
    }
    else {
      super.processEvent(event);
    }
  }

  public class AdminAWTEvent extends AWTEvent {

    private Object receivedObject;

    public AdminAWTEvent(Object target, Object _receivedObject) {
      super(target, ServerAdministrationApplet.ADMIN_EVENT_ID);
      receivedObject = _receivedObject;
    }

    public Object getReceivedObject() {
      return receivedObject;
    }

  }

  public static int ADMIN_EVENT_ID = AWTEvent.RESERVED_ID_MAX + 1;

}


