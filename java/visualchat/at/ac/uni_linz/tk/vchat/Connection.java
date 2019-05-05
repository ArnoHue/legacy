package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.net.*;
import java.util.zip.*;


/**
 * Works as server-side connection that sends and receives data to / from the
 * ChatClient.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class Connection extends Thread {

  private Object closeLock = new Object();

  // trial
  public static class SenderThread extends Thread {

    private Object myLock = new Object();
    private ChatServer.LinkedList pendingEvents;
    public Connection conn;
    private boolean stopped = false;

    public SenderThread(Connection _conn) {
      conn = _conn;
      pendingEvents = new ChatServer.LinkedList(conn.getServer());
    }

    public void run() {
      /*
    synchronized(conn) {
    conn.notify();
      }
      */
      while (!stopped) {
        synchronized(myLock) {
          if (pendingEvents.isEmpty()) {
            try {
              myLock.wait();
            }
            catch (InterruptedException e) {
            }
          }
        }
        if (!stopped && !pendingEvents.isEmpty()) {
          Object obj = (Object)pendingEvents.popFront();
          conn.sendInternal(obj);
        }
      }
   }

    public void send(Object obj) {
      pendingEvents.pushBack(obj);
      synchronized(myLock) {
        myLock.notify();
      }
    }

    public void terminate() {
      stopped = true;
      synchronized(myLock) {
        myLock.notify();
      }
    }

  }


  private Socket clientSocket;
  private ChatServer server;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private boolean connected;
  private boolean disconnecting;

  private int userId;
  private SenderThread sender;

/**
 * Constructs the Connection.
 *
 * @param serverParam      the ChatServer, which handles incoming UserEvents
 * @param socketParam      the Socket that has been openend by the ChatServer
 */

  public Connection(ChatServer serverParam, Socket socketParam) {
    super();

    server = serverParam;
    clientSocket = socketParam;
    userId = 0;
    disconnecting = false;
  }

  private ChatServer getServer() {
  return server;
  }


  public String getIPAddress() {
    if (connected) {
      return clientSocket.getInetAddress().getHostAddress();
    }
    else {
      return "";
    }
  }

/**
 * Runs a thread that is receiving data from the InpuStream openend to the
 * ChatClient.
 */

  public void run() {
    sender = new SenderThread(this);
    sender.start();
    /*
    while (!sender.isAlive()) {
      synchronized(this) {
    try {
        this.wait();
        }
        catch (InterruptedException excpt) {
    }
    }
    }
    */

    try {
      output = new ObjectOutputStream(clientSocket.getOutputStream());
      input = new ObjectInputStream(clientSocket.getInputStream());

      connected = true;
    }
    catch (IOException excpt) {
      server.log("IOException while opening connection: " + excpt);
    }
    while (connected) {
      try {
        Object object;
        synchronized(input) {
          object = input.readObject();
        }
        // server.log("Received " + object);
        server.handleUserEvent(object, this);
      }
      catch (Exception excpt) {
        // if (!(object instanceof UserLogoutEvent))
        //   server.log("Exception while receiving " + object);
        /*
        User user = server.getOnlineUser(userId);
        server.log("Exception while receiving " + object + " from " + (user != null ? user.getName() : null));
        excpt.printStackTrace();
        */
        // excpt.printStackTrace();
        if (connected && !disconnecting) {
          try {
            disconnecting = true;
            server.handleUserEvent(new UserLogoutEvent(userId), this);
          }
          finally {
            close();
          }
        }
      }
    }
  }


  public void send(Object obj) {
    sender.send(obj);
  }

/**
 * Sends data over the OutputStream openend to the ChatClient.
 *
 * @param sendObject      the object to be sent
 */

  private void sendInternal(Object sendObject) {
    if (connected) {
      try {
        /*
        // send may never block forever, if not all our server threads might be deadlocked
        if (streamMutex.obtain(TIMEOUT)) {
          output.writeObject(sendObject);
          // output.flush();
        }
        else {
          User user = server.getOnlineUser(userId);
          server.log("Timeout while sending to " + (user != null ? user.getName() : "unknown user"));
          server.closeConnection(this);
        }
        */
        synchronized(output) {
          output.writeObject(sendObject);
        }
      }
      catch (Exception excpt) {
        /*
        server.log("Exception while sending data");
        User user = server.getOnlineUser(userId);
        server.log("Exception while sending " + sendObject + " to " + (user != null ? user.getName() : "unknown user"));
        excpt.printStackTrace();
        */
        // excpt.printStackTrace();
        if (connected && !disconnecting) {
          try {
            disconnecting = true;
            server.handleUserEvent(new UserLogoutEvent(userId), this);
          }
          finally {
            close();
          }
        }
      }
      finally {
        // streamMutex.release();
      }
    }
  }


/**
 * Closes the connection.
 */

  public void close() {
    if (!connected) {
      return;
    }
    new Thread(new Runnable() {
      public void run() {
        synchronized(closeLock) {
          if (connected) {
            connected = false;
            try {
              sender.terminate();
            }
            catch (Exception excpt) {
              server.log("Exception while closing connection");
            }
            try {
              if (output != null)
                output.close();
            }
            catch (Exception excpt) {
              server.log("Exception while closing connection");
            }
            try {
              if (input != null)
                input.close();
            }
            catch (Exception excpt) {
              server.log("Exception while closing connection");
            }
            try {
              if (clientSocket != null)
                clientSocket.close();
            }
            catch (Exception excpt) {
              server.log("Exception while closing connection");
            }
          }
        }
      }
    }).start();
  }


/**
 * Returns the id of the User who is using this Connection, resp. 0 if the User's
 * id has not been set yet or the Administrator holds the Connection with the
 * ServerAdministrationApplet.
 */

  public int getUserId() {
    return userId;
  }

/**
 * Sets the id of the User who is using this Connection.
 */

  public void setUserId(int userIdParam) {
    userId = userIdParam;
  }


/**
 * Returns the Connection's Socket.
 */

  public Socket getSocket() {
    return clientSocket;
  }

  public boolean isConnected() {
    return connected;
  }

  public void setConnected(boolean connectedParam) {
    connected = connectedParam;
  }


}
