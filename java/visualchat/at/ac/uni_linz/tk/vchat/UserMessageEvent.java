package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.awt.*;


/**
 * Represents a User's message.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserMessageEvent extends UserEvent implements Serializable {

  public String userMessage;


/**
 * Constructs the UserMessageEvent.
 *
 * @param idParam           the id of the User producing the Event
 * @param messageParam      the new message
 */

  public UserMessageEvent (int idParam, String messageParam) {
    super(idParam);
    userMessage = messageParam;
  }
}
