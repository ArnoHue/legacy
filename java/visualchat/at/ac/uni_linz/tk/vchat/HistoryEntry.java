package at.ac.uni_linz.tk.vchat;

import java.util.*;
import java.io.*;
import java.awt.*;

/**
 * A HistoryEntry represents the message sent by a User at a certain time.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class HistoryEntry extends MessageEntry {

  public int userId, mood, roomId, heading;
  public Point position;


/**
 * Constructs the HistoryEntry.
 *
 * @param dateParam           the Date when the event happened
 * @param roomIdParam         the id of the Room where the event happened
 * @param userIdParam         the id of the User who caused the event
 * @param positionParam       the User's position
 * @param headingParam        the User's heading
 * @param colorParam          the User's color
 * @param textParam           the message text
 * @param moodParam           the User's mood
 */

  public HistoryEntry(Date dateParam, int roomIdParam, int userIdParam, Point positionParam, int headingParam, Color colorParam, String textParam, int moodParam) {
    super(dateParam, textParam, colorParam);
    roomId = roomIdParam;
    position = positionParam;
    mood = moodParam;
    userId = userIdParam;
    heading = headingParam;
  }
}