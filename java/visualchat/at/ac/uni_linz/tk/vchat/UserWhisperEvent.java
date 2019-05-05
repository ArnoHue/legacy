package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.awt.*;


/**
 * Represents a User's whisper message.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserWhisperEvent extends UserMessageEvent implements Serializable {

  public int receivingUserId;


/**
 * Constructs the UserWhisperEvent.
 *
 * @param idParam           the id of the User producing the Event
 * @param messageParam      the new message
 */

  public UserWhisperEvent (int idParam, String messageParam, int receivingUserIdParam) {
    super(idParam, messageParam);
    receivingUserId = receivingUserIdParam;
  }
}
