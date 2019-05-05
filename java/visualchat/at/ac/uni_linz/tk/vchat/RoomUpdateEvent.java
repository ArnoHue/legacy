package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents an update of a Room.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomUpdateEvent extends RoomEvent {

  Room room;


/**
 * Constructs the RoomUpdateEvent.
 *
 * @param roomParam      the Room's new data
 */

  public RoomUpdateEvent (Room roomParam) {
    super(roomParam.getId());
    room = (Room)(roomParam.clone());
  }

/**
 * Constructs the RoomUpdateEvent.
 *
 * @param roomIdParam      the Rooms's id
 */

  public RoomUpdateEvent (int roomIdParam) {
    this(new Room(roomIdParam, "", ChatRepository.ROOM_DIMENSION));
  }

}