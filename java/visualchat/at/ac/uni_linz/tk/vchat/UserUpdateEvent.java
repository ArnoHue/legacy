package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents a changement of a User's data.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserUpdateEvent implements Serializable {

  public User user;
  public String statusString;
  public boolean done;


/**
 * Constructs the UserUpdateEvent.
 *
 * @param userParam      the User's new data
 */

  public UserUpdateEvent (User userParam) {
	this(userParam, true);
  }

  public UserUpdateEvent (User userParam, boolean resetAvatars) {
	done = false;
    user = (User)(userParam.clone());
    statusString = "";

    if (resetAvatars) {
      user.setBackAvatar(null);
      for (int i = 0; i < user.getNrOfMoods(); i++) {
        user.setAvatar(i, null);
      }
    }
  }
}