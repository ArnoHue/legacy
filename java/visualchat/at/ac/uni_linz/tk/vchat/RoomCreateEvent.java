package at.ac.uni_linz.tk.vchat;

import java.io.*;


/**
 * Represents the new creation of a room.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class RoomCreateEvent extends RoomEvent {

  public Room room;


/**
 * Constructs the RoomCreateEvent.
 *
 * @param userParam      the User's new data
 */

  public RoomCreateEvent (Room roomParam) {
    super(roomParam.getId());
    room = (Room)(roomParam.clone());
  }

/**
 * Constructs the RoomCreateEvent.
 *
 * @param roomIdParam      the Rooms's id
 */

  public RoomCreateEvent (int roomIdParam) {
    this(new Room(roomIdParam, "", ChatRepository.ROOM_DIMENSION));
  }

}