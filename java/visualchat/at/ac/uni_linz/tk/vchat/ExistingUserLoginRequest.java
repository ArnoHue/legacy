package at.ac.uni_linz.tk.vchat;

/**
 * Represents a LoginRequest of an existing User.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ExistingUserLoginRequest extends UserLoginRequest  {


/**
 * Constructs the ExistingUserLoginRequest.
 *
 * @param userNameParam          the name of the existing User requesting to login
 * @param userPasswordParam      the password of the existing User requesting to
 *                               login
 */

  public ExistingUserLoginRequest (String userNameParam, String userPasswordParam) {
    super(new User(userNameParam, userPasswordParam));
  }
}
