package at.ac.uni_linz.tk.vchat;

/**
 * Represents a request for a deleting a User.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ServerDeleteUserRequest extends ServerAdministrationRequest {

  public String userName;


/**
 * Constructs the ServerDeleteUserRequest.
 *
 * @param keyParam           the server's key which was applied when starting the
 *                           ChatServer
 * @param userNameParam      the name of the User to be deleted.
 */

  public ServerDeleteUserRequest(String keyParam, String userNameParam) {
    super(keyParam);
    userName = userNameParam;
  }

}