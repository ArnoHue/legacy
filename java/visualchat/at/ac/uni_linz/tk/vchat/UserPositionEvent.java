package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.awt.*;


/**
 * Represents a changement of a User's position.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserPositionEvent extends UserEvent implements Serializable {

  public Point userPosition;


/**
 * Constructs the UserPositionEvent.
 *
 * @param idParam            the id of the User producing the Event
 * @param positionParam      the new position
 */

  public UserPositionEvent (int idParam, Point positionParam) {
    super(idParam);
    userPosition = new Point(positionParam);
  }
}
