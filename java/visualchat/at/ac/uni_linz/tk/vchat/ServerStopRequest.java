package at.ac.uni_linz.tk.vchat;

/**
 * Represents a request for stopping the ChatServer.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ServerStopRequest extends ServerAdministrationRequest {


/**
 * Constructs the ServerStopRequest.
 *
 * @param keyParam       the server's key which was applied when starting the
 *                       ChatServer
 */

  public ServerStopRequest(String keyParam) {
    super(keyParam);
  }

}