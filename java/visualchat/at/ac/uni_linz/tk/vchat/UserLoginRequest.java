package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Works as an abstract superclass for a Users' LoginRequests.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public abstract class UserLoginRequest implements Serializable  {

  public static final int REQUESTED = 0;
  public static final int ACCEPTED = 1;
  public static final int DENIED = 2;

  public int status;
  public User user;
  public String statusString;


/**
 * Constructs the UserLoginRequest.
 */

  public UserLoginRequest () {
    this(new User(0));
  }


/**
 * Constructs the UserLoginRequest.
 *
 * @param userParam      the User requesting a Login
 */

  public UserLoginRequest (User userParam) {
    user = userParam;
    status = REQUESTED;
    statusString = "";
  }
}
