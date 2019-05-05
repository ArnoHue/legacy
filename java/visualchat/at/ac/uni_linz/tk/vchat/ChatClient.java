package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;


/**
 * Implements the methods for the client side's networking. Opens a socket
 * connection and Input- and OutputStreams to the ChatServer, sends and receives
 * data. It also includes the functionality for User logins and logouts.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ChatClient implements Runnable {

  private Socket clientSocket;
  private Thread clientThread;
  private String host;
  private boolean connected;
  private ChatApplet chatApplet;

  private ObjectOutputStream output;
  private ObjectInputStream input;

  private volatile UserLoginRequest userLogin;


/**
 * Constructs the ChatClient.
 *
 * @param hostParam               the host where the ChatApplet descends from
 *                                (that is also where the ChatServer ought to be
 *                                running)
 * @param portParam               the standard port where the ChatServer is
 *                                listening
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 */

  public ChatClient(String hostParam, ChatApplet chatParam) {
    host = hostParam;
    chatApplet = chatParam;
  }


/**
 * Connects to the ChatServer.
 *
 * @param portParam      the port where the ChatServer is listening
 */

  public void connect(int portParam) {
    try {
      chatApplet.setStatus("Connecting...", true);
      clientSocket = new Socket(host, portParam);
      /*
       * Open Input- and OutputStreams
       */
      input = new ObjectInputStream(clientSocket.getInputStream());
      output = new ObjectOutputStream(clientSocket.getOutputStream());

      /*
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException excpt) {
      }
      */

      /*
       * Start the thread that is receiving data
       */
      connected = true;
      clientThread = new Thread(this);
      clientThread.start();
      chatApplet.notifyConnected(connected);
      chatApplet.setStatus("Connected", true);
    }
    catch (Exception excpt) {
      connected = false;
      chatApplet.notifyConnected(connected);
      chatApplet.setStatus("Exception while connecting. Server down, firewall config or file-URL applet.", true);
      excpt.printStackTrace();
    }
  }


/**
 * Connects to the ChatServer and sends a login request for an existing User.
 *
 * @param userNameParam          the name of the User to login
 * @param userPasswordParam      the password of the User to login
 * @param portParam              the port where the ChatServer is listening
 */

  public void connectAsExistingUser(String userNameParam, String userPasswordParam, int portParam) {

    connect(portParam);
    if (connected) {
      chatApplet.setStatus("Connecting as existing user...", true);
      chatApplet.stopSimulator();
      chatApplet.removeAllExceptDefaultRoom();
      userLogin = new ExistingUserLoginRequest(userNameParam, userPasswordParam);
      login();
    }
  }


/**
 * Connects to the ChatServer and sends a login request for a new User.
 *
 * @param userParam      the User to login
 * @param portParam      the port where the ChatServer is listening
 */

  public void connectAsNewUser(User userParam, int portParam) {

    connect(portParam);
    if (connected) {
      chatApplet.setStatus("Connecting as new user...", true);
      chatApplet.stopSimulator();
      chatApplet.removeAllExceptDefaultRoom();

      userLogin = new NewUserLoginRequest(userParam);
      login();
    }
  }

  private void login() {
    chatApplet.setStatus("Waiting for server reply...", true);
    synchronized(this) {
        send(userLogin);
        while(userLogin.status == UserLoginRequest.REQUESTED) {
            try {
                wait();
            }
            catch (InterruptedException excpt) {
            }
        }
    }
    if (userLogin.status == UserLoginRequest.ACCEPTED) {
        int lastUserId = chatApplet.getCurrentUserId();
        chatApplet.setCurrentUser(userLogin.user);
        if (lastUserId != userLogin.user.getId()) {
            chatApplet.removeUser(lastUserId);
        }

        chatApplet.moveUserToRoom(userLogin.user.getId(), 0, true);

        chatApplet.restartHistory();
        chatApplet.setStatus("Login accepted: " + userLogin.statusString , true);
        chatApplet.setFrameVisibility(true);
    }
    else {
        disconnect();
        chatApplet.setStatus("Login denied: " + userLogin.statusString, true);
    }
  }

/**
 * Disconnects from the ChatServer.
 */
  public void disconnect() {
    disconnect(true);
  }

  public void disconnect(boolean sendLogout) {
    try {
      if (sendLogout)
        send(new UserLogoutEvent(chatApplet.getCurrentUserId()));
      if (clientThread != null && clientThread.isAlive())
        clientThread.stop();
      if (output != null)
        output.close();
      if (input != null)
        input.close();
      if (clientSocket != null)
        clientSocket.close();
      chatApplet.setStatus("Disconnected", true);
    }
    catch (Exception excpt) {
      chatApplet.setStatus("Exception while disconnecting", true);
    }
    finally {
      connected = false;
      chatApplet.notifyConnected(connected);
      chatApplet.removeAllExceptCurrentUser();
      chatApplet.removeAllExceptDefaultRoom();
    }
  }


/**
 * Runs a thread that is receiving data from the InpuStream openend to the
 * ChatServer.
 */

  public void run() {
    Object receivedObject;
    User user;
    while (connected) {
      try {
        receivedObject = input.readObject();
        if (receivedObject instanceof NewUserLoginRequest) {
          synchronized(this) {
            userLogin = (NewUserLoginRequest)receivedObject;
            notify();
          }
        }
        else if (receivedObject instanceof ExistingUserLoginRequest) {
          synchronized(this) {
            userLogin = (ExistingUserLoginRequest)receivedObject;
            notify();
          }
        }
        else {
          chatApplet.postReceivedObjectEvent(receivedObject);
        }
      }
      catch (Exception ex) {
        chatApplet.postReceivedObjectEvent(ex);
        connected = false;
      }
    }
    synchronized(this) {
      notify();
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
        // chatApplet.setStatus("Sending data");
        synchronized(output) {
          output.writeObject(sendObject);
        }
        output.flush();
        // output.reset();
      }
    }
    catch (IOException excpt) {
      System.out.println("Exception while sending data: " + excpt + ". Going offline...");
      chatApplet.setStatus("Exception while sending data. Going offline...", true);
      disconnect(false);
    }
  }


/**
 * Sends data over the OutputStream openend to the ChatServer.
 *
 * @param sendObject      the object to be sent
 */

  public boolean connected() {
    return connected;
  }


/**
 * Returns the host where the ChatClient is opening connections to.
 */

  public String getHost() {
    return host;
  }

}