package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents a changement of a Room's state.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomEvent implements Serializable {

  public int roomId;


/**
 * Constructs the RoomEvent.
 */

  public RoomEvent () {
    this(0);
  }


/**
 * Constructs the RoomEvent.
 *
 * @param roomIdParam      the Rooms's id
 */

  public RoomEvent (int roomIdParam) {
    roomId = roomIdParam;
  }
}