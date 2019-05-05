package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents a removale of a Room.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomRemoveEvent extends RoomEvent {


/**
 * Constructs the RoomRemoveEvent.
 *
 * @param roomIdParam      the Rooms's id
 */

  public RoomRemoveEvent (int roomIdParam) {
    super(roomIdParam);
  }

}