package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.util.*;
import java.io.*;


/**
 * A Room is a rectangular area in a User can be move around and perceive other
 * users messages.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class Room implements Cloneable, Serializable {

  static final long serialVersionUID = -2034561648711108435L;

  private int id;
  private String name, administrator, info, rules;
  private Dimension size;
  private Vector userVector, invitedUserVector, kickedUserVector;
  private boolean privacy, demo;
  private long lastAccess; // don't serialize it as GregCal


/**
 * Constructs the Room.
 *
 * @param idParam        a unique identifier for the room
 * @param sizeParam      the Room's size
 */

  public Room(int idParam, String nameParam, Dimension sizeParam) {
    name = nameParam;
    id = idParam;
    size = sizeParam;
    userVector = new Vector();
    invitedUserVector = new Vector();
    kickedUserVector = new Vector();
    privacy = false;
    demo = false;
    administrator = "";
    info = "";
    rules = "";
    lastAccess = System.currentTimeMillis();
  }

  public GregorianCalendar getLastAccess() {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(new Date(lastAccess));
    return cal;
  }

  public void setLastAccess(GregorianCalendar lastAccessParam) {
    lastAccess = lastAccessParam.getTime().getTime();
  }


/**
 * Returns the size of the Room.
 */

  public Dimension getSize() {
    return size;
  }


/**
 * Returns the Id of the Room.
 */

  public int getId() {
    return id;
  }


/**
 * Sets the Id of the Room.
 */

  public void setId(int idParam) {
    id = idParam;
  }


/**
 * Sets the Name of the Room.
 */

  public void setName(String nameParam) {
    name = nameParam;
  }


/**
 * Returns the Name of the Room.
 */

  public String getName() {
    return name;
  }

/**
 * Adds a User to the Room.
 */

  public void addUser(String userName) {
    if ((!isPrivate() && !isKicked(userName)) || (isPrivate() && isInvited(userName))|| isAdministrator(userName))
      userVector.addElement(userName);
  }


/**
 * Removes a User from the Room.
 */

  public void removeUser(String userName) {
    userVector.removeElement(userName);
  }


/**
 * Returns the number of Users who are currently in the Room.
 */

  public int getNrOfUsers() {
    return userVector.size();
  }


/**
 * Removes all Users from the Room.
 */

  public void removeAllUsers() {
    userVector = new Vector();
  }


/**
 * Returns a Vector of the Users that are currently in the Room.
 */

  public Vector getUserNameVector() {
    return userVector;
  }


/**
 * Returns true if the Room is private, false if not.
 */

  public boolean isPrivate() {
    return privacy;
  }


/**
 * Sets the privacy-flag of the Room.
 *
 * @param privacyParam      true, if the Room is meant to be private
 */

  public void setPrivate(boolean privacyParam) {
    privacy = privacyParam;
  }


/**
 * Returns true if the Room is in demo mode, false if not.
 */

  public boolean isDemo() {
    return demo;
  }


/**
 * Sets the demo-flag of the Room.
 *
 * @param demoParam      true, if the Room is in demo-mode
 */

  public void setDemo(boolean demoParam) {
    demo = demoParam;
  }


/**
 * Defines a User to be invited to the Room (only makes sence if the Room is private).
 *
 * @param userName      the name of the User to be invited
 */

  public void inviteUser(String userName) {
    if (!isInvited(userName))
      invitedUserVector.addElement(userName);
  }


/**
 * Defines a User not to be invited any more to the Room (only makes sence if the
 * Room is private).
 *
 * @param userName      the name of the User not to be invited
 */

  public void uninviteUser(String userName) {
    invitedUserVector.removeElement(userName);
  }


/**
 * Defines a User to be kicked out of the Room (only makes sence if the Room is
 * public).
 *
 * @param userName      the name of the User to be kicked out
 */

  public void kickUser(String userName) {
    if (!isKicked(userName))
      kickedUserVector.addElement(userName);
  }


/**
 * Defines a User not to be kicked out of the Room any more (only makes sence if the
 * Room is public).
 *
 * @param userName      the name of the User not to be kicked out
 */

  public void unkickUser(String userName) {
    kickedUserVector.removeElement(userName);
  }


/**
 * Returns true if a User is invited to the Room.
 *
 * @param userName      the name of the User
 */

  public boolean isInvited(String userName) {
	for (int i = 0; i < invitedUserVector.size(); i++) {
	  if (userName.equals(invitedUserVector.elementAt(i))) {
	     return true;
	  }
    }
    return false;
  }


/**
 * Returns true if a User is kicked out of the Room.
 *
 * @param userName      the name of the User
 */

  public boolean isKicked(String userName) {
	for (int i = 0; i < kickedUserVector.size(); i++) {
	  if (userName.equals(kickedUserVector.elementAt(i))) {
	     return true;
	  }
    }
    return false;
  }


/**
 * Returns true if a User has access to the Room. This means the Room is either public and the User
 * has not been kicked, or the Room is private and the User has been invited.
 *
 * @param userName      the name of the User
 */

  public boolean hasAccess(String userName) {
    return isAdministrator(userName) || (isPrivate() && isInvited(userName)) || (!isPrivate() && !isKicked(userName));
  }


/**
 * Sets the User who has the permissions to administrate the Room. He can kick and
 * invite Users, change the name and other attributed of the Room or delete it as
 * well.
 *
 * @param chatParam      the name of the Administrator
 */

  public void setAdministrator(String chatParam) {
    administrator = chatParam;
  }


/**
 * Returns the name of the Administrator.
 */
  public String getAdministrator() {
    return administrator;
  }

  public boolean isAdministrator(String name) {
    return name != null && (name.equals(administrator) || name.equals(ChatRepository.ADMIN));
  }

/**
 * Sets the info text of the Room.
 *
 * @param infoParam      the info text
 */

  public void setInfo(String infoParam) {
    info = infoParam;
  }


/**
 * Returns the info text of the Room.
 */

  public String getInfo() {
    return info;
  }


/**
 * Sets the rules text of the Room.
 *
 * @param rulesParam      the rules text
 */

  public void setRules(String rulesParam) {
    rules = rulesParam;
  }


/**
 * Returns the rules text of the Room.
 */

  public String getRules() {
    return rules;
  }


/**
 * Returns a Vector which contains the names of all the Users who are invited to the
 * Room.
 */

  public Vector getInvitedUsers() {
    return invitedUserVector;
  }


/**
 * Sets the Vector which contains the names of all the Users who are invited to the
 * Room.
 *
 * @param invitedUserVectorParam      the Vector containing the User names
 */

  public void setInvitedUsers(Vector invitedUserVectorParam) {
    invitedUserVector = invitedUserVectorParam;
  }


/**
 * Returns a Vector which contains the names of all the Users who are kicked out of
 * the Room.
 */

  public Vector getKickedUsers() {
    return kickedUserVector;
  }


/**
 * Sets the Vector which contains the names of all the Users who are kicked out of
 * the Room.
 *
 * @param kickedUserVectorParam      the Vector containing the User names
 */

  public void setKickedUsers(Vector kickedUserVectorParam) {
    kickedUserVector = kickedUserVectorParam;
  }


/**
 * Clones the Room.
 */

  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException excpt) {
      return null;
    }
  }

  /*
  private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    id = in.readInt();
    name = (String)in.readObject();
    administrator = (String)in.readObject();
    info = (String)in.readObject();
    rules = (String)in.readObject();
    size = (Dimension)in.readObject();
    userVector = (Vector)in.readObject();
    invitedUserVector = (Vector)in.readObject();
    kickedUserVector = (Vector)in.readObject();
    privacy = in.readBoolean();
    demo = in.readBoolean();
  }

  private synchronized void writeObject(ObjectOutputStream out) throws IOException {
     out.writeInt(id);
     out.writeObject(name);
     out.writeObject(administrator);
     out.writeObject(info);
     out.writeObject(rules);
     out.writeObject(size);
     out.writeObject(userVector);
     out.writeObject(invitedUserVector);
     out.writeObject(kickedUserVector);
     out.writeBoolean(privacy);
     out.writeBoolean(demo);
  }
  */
}