package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents a changement of a User's mood.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserMoodEvent extends UserEvent implements Serializable {

  public int userMood;


/**
 * Constructs the UserMoodEvent.
 *
 * @param idParam        the id of the User producing the Event
 * @param moodParam      the new mood
 */

  public UserMoodEvent (int idParam, int moodParam) {
    super(idParam);
    userMood = moodParam;
  }
}
