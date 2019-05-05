package at.ac.uni_linz.tk.vchat;

/**
 * Represents a LoginRequest of a new User.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class NewUserLoginRequest extends UserLoginRequest  {


/**
 * Constructs the NewUserLoginRequest.
 *
 * @param userParam      the User requesting to login
 */

  public NewUserLoginRequest(User userParam) {
    super(userParam);
  }

}
