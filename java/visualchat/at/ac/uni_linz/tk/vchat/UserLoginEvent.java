package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents a login of a User.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserLoginEvent implements Serializable {

  public User user;


/**
 * Constructs the UserLoginEvent.
 *
 * @param userParam      the User who logged in
 */

  public UserLoginEvent (User userParam) {
    user = (User)userParam.clone();
    user.setPassword("");
  }
}