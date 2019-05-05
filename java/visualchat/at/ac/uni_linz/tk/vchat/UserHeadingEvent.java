package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents a changement of a User's heading.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserHeadingEvent extends UserEvent implements Serializable {

  public int userHeading;


/**
 * Constructs the UserHeadingEvent.
 *
 * @param idParam           the id of the User producing the Event
 * @param headingParam      the new heading
 */

  public UserHeadingEvent (int idParam, int headingParam) {
    super(idParam);
    userHeading = headingParam;
  }
}
