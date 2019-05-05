package at.ac.uni_linz.tk.vchat;

/**
 * Represents a request for obtaining a User's data.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ServerGetUserDataRequest extends ServerAdministrationRequest {

  public User user;


/**
 * Constructs the ServerGetUserDataRequest.
 *
 * @param keyParam            the server's key which was applied when starting the
 *                            ChatServer
 * @param userNameParam       the name of the User whose data should be obtained
 */

  public ServerGetUserDataRequest(String keyParam, String userNameParam) {
    super(keyParam);
    user = new User(userNameParam, "");
  }

}