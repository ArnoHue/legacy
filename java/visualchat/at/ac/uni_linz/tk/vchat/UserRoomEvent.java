package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.awt.*;


/**
 * Represents a changement of the Room a User is currently in.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserRoomEvent extends UserEvent implements Serializable {

  public int roomId;
  public Point position;


/**
 * Constructs the UserRoomEvent.
 *
 * @param userIdParam      the Id of the User producing the Event
 * @param roomIdParam      the Id of the new Room
 */

  public UserRoomEvent (int userIdParam, int roomIdParam, Point positionParam) {
    super(userIdParam);
    roomId = roomIdParam;
    position = positionParam;
  }
}
