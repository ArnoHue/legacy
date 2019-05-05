package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents an administrative request, e.g. for remote stopping or restarting
 * of the ChatServer.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ServerAdministrationRequest implements Serializable {

  public static final int REQUESTED = 0;
  public static final int ACCEPTED = 1;
  public static final int DENIED = 2;
  public static final int ERROR_OCCURED = 3;

  public int status;
  public String key;


/**
 * Constructs the ServerAdministrationRequest.
 *
 * @param keyParam       the server's key which was applied when starting the
 *                       ChatServer
 */

  public ServerAdministrationRequest (String keyParam) {
    key = keyParam;
    status = REQUESTED;
  }
}
