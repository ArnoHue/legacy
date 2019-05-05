package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.util.*;


/**
 * Contains the list of existing rooms..
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomListEvent extends RoomEvent {

  public IntegerHashtable roomTable;


/**
 * Constructs the RoomListEvent.
 *
 * @param userParam      the User's new data
 */

  public RoomListEvent (IntegerHashtable roomTableParam) {
    Enumeration roomEnum;
    Room room;
    roomTable = new IntegerHashtable();
    roomEnum = roomTableParam.elements();
    while(roomEnum.hasMoreElements()) {
      room = (Room)((Room)roomEnum.nextElement()).clone();
      room.removeAllUsers();
      if (room.getId() != 0)
        roomTable.put(room.getId(), room);
    }
  }

}