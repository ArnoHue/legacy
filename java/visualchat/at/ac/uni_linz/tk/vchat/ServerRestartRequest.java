package at.ac.uni_linz.tk.vchat;

/**
 * Represents a request for restarting the ChatServer.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ServerRestartRequest extends ServerAdministrationRequest {


/**
 * Constructs the ServerRestartRequest.
 *
 * @param keyParam       the server's key which was applied when starting the
 *                       ChatServer
 */

  public ServerRestartRequest(String keyParam) {
    super(keyParam);
  }

}