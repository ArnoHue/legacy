package at.ac.uni_linz.tk.vchat;

import java.util.*;
import java.io.*;
import java.awt.*;

/**
 * A MessageEntry represents the message sent by a User at a certain time.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class MessageEntry implements Serializable {

  public Date date;
  public String text;
  public Color color;


/**
 * Constructs the MessageEntry.
 *
 * @param dateParam      the Date when the message was sent
 * @param textParam      the message text
 */

  public MessageEntry(Date dateParam, String textParam) {
    this (dateParam, textParam, Color.black);
  }


/**
 * Constructs the MessageEntry.
 *
 * @param dateParam       the Date when the message was sent
 * @param textParam       the message text
 * @param colorParam      the User's color
 */

  public MessageEntry(Date dateParam, String textParam, Color colorParam) {
    date = dateParam;
    text = textParam;
    color = colorParam;
  }
}