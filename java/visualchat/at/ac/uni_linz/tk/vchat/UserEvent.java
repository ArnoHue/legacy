package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Works as an abstract superclass for different Events thrown by a User.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public abstract class UserEvent implements Serializable {

  public int userId;


/**
 * Constructs the UserEvent.
 *
 * @param idParam      the id of the User throwing the Event
 */

  public UserEvent (int idParam) {
    userId = idParam;
  }
}
