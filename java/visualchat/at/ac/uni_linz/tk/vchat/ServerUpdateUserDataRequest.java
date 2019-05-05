package at.ac.uni_linz.tk.vchat;

/**
 * Represents a request for updating User data.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ServerUpdateUserDataRequest extends ServerAdministrationRequest {

  public User user;


/**
 * Constructs the ServerUpdateUserDataRequest.
 *
 * @param keyParam        the server's key which was applied when starting the
 *                        ChatServer
 * @param userParam       the new User data
 */

  public ServerUpdateUserDataRequest(String keyParam, User userParam) {
    super(keyParam);
    user = userParam;
  }

}