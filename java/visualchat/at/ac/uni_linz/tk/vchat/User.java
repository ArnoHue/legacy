package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.util.*;
import java.io.*;


/**
 * Represents a chat participant, including id, name, password, avatars, color,
 * message history, mood history and more.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class User implements Serializable, Cloneable {

  static final long serialVersionUID = -6200835697056167453L;

  public static final int HUMAN_RACE = 0;
  public static final int ROBOT_RACE = 1;

  private int id, mood;
  private String name, password;

  private int room;
  private int heading, race;
  private Vector avatarURL, moodKeywords, moodTimeout, moodName;
  private String message, email, homepage, info;
  private Color color;
  private Date loginDate;

  private Point position;

  private Vector avatar;
  private SerializableImage backAvatar;
  private String backAvatarURL;
  private boolean commercialBanner;


/**
 * Constructs the User.
 *
 * @param idParam      the User's id
 */

  public User(int idParam) {
    this(idParam, "", "", Color.black, new Point(), 0, 0, HUMAN_RACE);
  }


/**
 * Constructs the User.
 *
 * @param nameParam          the User's name
 * @param passwordParam      the User's password
 */

  public User(String nameParam, String passwordParam) {
    this(0, nameParam, passwordParam, Color.black, new Point(), 0, 0, HUMAN_RACE);
  }


/**
 * Constructs the User.
 *
 * @param idParam            the User's id
 * @param nameParam          the User's name
 * @param passwordParam      the User's password
 * @param colorParam         the User's color
 * @param positionParam      the User's position
 * @param headingParam       the User's heading
 * @param moodParam          the User's mood
 */

  public User(int idParam, String nameParam, String passwordParam, Color colorParam, Point positionParam, int headingParam, int moodParam) {
    this(idParam, nameParam, passwordParam, colorParam, positionParam, headingParam, moodParam, HUMAN_RACE);
  }


/**
 * Constructs the User.
 *
 * @param idParam            the User's id
 * @param nameParam          the User's name
 * @param passwordParam      the User's password
 * @param colorParam         the User's color
 * @param positionParam      the User's position
 * @param headingParam       the User's heading
 * @param moodParam          the User's mood
 * @param raceParam          the User's race
 */

  public User(int idParam, String nameParam, String passwordParam, Color colorParam, Point positionParam, int headingParam, int moodParam, int raceParam) {
    id = idParam;
    name = nameParam;
    password = passwordParam;
    color = colorParam;
    position = positionParam;
    heading = headingParam;
    race = raceParam;
    avatar = new Vector();
    avatarURL = new Vector();
    moodKeywords = new Vector();
    moodTimeout = new Vector();
    moodName = new Vector();
    mood = moodParam;
    room = 0;
    loginDate = new Date();
    setNrOfMoods(ChatRepository.PREDEFINED_NR_OF_MOODS);
    commercialBanner = false;
    message = "";
    for (int i = 0; i < getNrOfMoods(); i++)
      setMoodName(i, ChatRepository.MOOD_NAME[i]);
  }


/**
 * Returns the User's id.
 */

  public synchronized int getId() {
    return id;
  }


/**
 * Sets the User's id.
 *
 * @param idParam      the User's id
 */

  public synchronized void setId(int idParam) {
    id = idParam;
  }


/**
 * Returns the User's name.
 */

  public synchronized String getName() {
    return name;
  }


/**
 * Sets the User's name.
 *
 * @param nameParam      the User's name
 */

  public synchronized void setName(String nameParam) {
    name = nameParam;
  }


/**
 * Returns the User's email.
 */

  public synchronized String getEmail() {
    return email;
  }


/**
 * Sets the User's email.
 *
 * @param emailParam      the User's email
 */

  public synchronized void setEmail(String emailParam) {
    email = emailParam;
  }


/**
 * Returns the User's homepage.
 */

  public synchronized String getHomepage() {
    return homepage;
  }


/**
 * Sets the User's homepage.
 *
 * @param homepageParam      the User's homepage
 */

  public synchronized void setHomepage(String homepageParam) {
    homepage = homepageParam;
  }


/**
 * Returns the User's info.
 */

  public synchronized String getInfo() {
    return info;
  }


/**
 * Sets the User's info.
 *
 * @param infoParam      the User's info
 */

  public synchronized void setInfo(String infoParam) {
    info = infoParam;
  }


/**
 * Returns the User's password.
 */

  public synchronized String getPassword() {
    return password;
  }


/**
 * Sets the User's password.
 *
 * @param passwordParam      the User's password
 */

  public synchronized void setPassword(String passwordParam) {
    password = passwordParam;
  }


/**
 * Returns the User's mood.
 */

  public synchronized int getMood() {
    return mood;
  }


/**
 * Sets the User's mood.
 *
 * @param moodParam      the User's mood
 */

  public synchronized void setMood(int moodParam) {
    mood = moodParam;
  }


/**
 * Returns the Room the User is currently positioned in.
 */

  public synchronized int getRoom() {
    return room;
  }


/**
 * Sets the Room the User should be positioned.
 *
 * @param roomParam      the Room's id
 */

  public synchronized void setRoom(int roomParam) {
    room = roomParam;
    message = "";
  }


/**
 * Returns the User's login date.
 */

  public synchronized Date getLoginDate() {
    return loginDate;
  }


/**
 * Sets the User's login date.
 *
 * @param dateParam      the User's login date
 */

  public synchronized void setLoginDate(Date dateParam) {
    loginDate = dateParam;
  }


/**
 * Returns the User's heading.
 */

  public synchronized int getHeading() {
    return heading;
  }


/**
 * Sets the User's heading.
 *
 * @param headingParam      the User's heading
 */

  public synchronized void setHeading(int headingParam) {
    heading = headingParam;
  }


/**
 * Returns the User's race.
 */

  public synchronized int getRace() {
    return race;
  }


/**
 * Returns whether the User is human or not.
 */

  public synchronized boolean isHuman() {
    return race == HUMAN_RACE;
  }

/**
 * Sets the User's race.
 *
 * @param raceParam      the User's race
 */

  public synchronized void setRace(int raceParam) {
    race = raceParam;
  }


/**
 * Returns the User's Color.
 */

  public synchronized Color getColor() {
    return color;
  }


/**
 * Sets the User's Color.
 *
 * @param colorParam      the User's Color
 */

  public synchronized void setColor(Color colorParam) {
    color = colorParam;
  }


/**
 * Returns the User's current position.
 */

  public synchronized Point getPosition() {
    return position;
  }


/**
 * Sets the User's current position.
 *
 * @param positionParam      the User's position
 */

  public synchronized void setPosition(Point positionParam) {
    position = positionParam;
  }


/**
 * Returns the URL of the avatar of a certain mood.
 *
 * @param moodParam      the mood to return the avatar's URL for
 */

  public synchronized String getAvatarURL(int moodParam) {
    return avatarURL.size() > moodParam ? (String)avatarURL.elementAt(moodParam) : "";
  }


/**
 * Sets the URL of the avatar of a certain mood.
 *
 * @param moodParam             the mood to set the avatar's URL for
 * @param avatarURLParam      the avatar's URL
 */

  public synchronized void setAvatarURL(int moodParam, String avatarURLParam) {
    if (avatarURL.size() > moodParam) {
        avatarURL.setElementAt(avatarURLParam, moodParam);
    }
  }


/**
 * Returns the URL of the avatar for the User's back.
 */

  public synchronized String getBackAvatarURL() {
    return backAvatarURL;
  }


/**
 * Sets the URL of the avatar for the User's back.
 *
 * @param backAvatarURLParam      the avatar's URL
 */

  public synchronized void setBackAvatarURL(String backAvatarURLParam) {
    backAvatarURL = backAvatarURLParam;
  }


/**
 * Returns the keywords for a certain mood.
 *
 * @param moodParam      the mood to return the keywords for
 */

  public synchronized String getMoodKeywords(int moodParam) {
    return (moodName.size() > moodParam) ? (String)moodKeywords.elementAt(moodParam) : "";
  }


/**
 * Sets the keywords for a certain mood.
 *
 * @param moodParam          the mood
 * @param keywordsParam      the keywords for this mood
 */

  public synchronized void setMoodKeywords(int moodParam, String keywordsParam) {
    if (moodName.size() > moodParam) {
        moodKeywords.setElementAt(keywordsParam, moodParam);
    }
  }


/**
 * Returns the timeout for the current mood.
 *
 * @param moodParam      the mood to return the keywords for
 */

  public synchronized int getMoodTimeout() {
    return getMoodTimeout(getMood());
  }


/**
 * Returns the timeout for a certain mood.
 *
 * @param moodParam      the mood to return the keywords for
 */

  public synchronized int getMoodTimeout(int moodParam) {
      return moodTimeout.size() > moodParam && moodTimeout.elementAt(moodParam) != null ? ((Integer)moodTimeout.elementAt(moodParam)).intValue() : 0;
  }


/**
 * Returns the name of a certain mood.
 *
 * @param moodParam      the mood of which to return name
 */

  public synchronized String getMoodName(int moodParam) {
    return  (moodName.size() > moodParam && moodName.elementAt(moodParam) != null) ? (String)moodName.elementAt(moodParam) : "";
  }


/**
 * Sets the name of a certain mood.
 *
 * @param moodParam          the mood to set the name for
 * @param moodNameParam      the name
 */

  public synchronized void setMoodName(int moodParam, String moodNameParam) {
    moodName.setElementAt(moodNameParam, moodParam);
  }


/**
 * Sets the timeout for a certain mood.
 *
 * @param moodParam          the mood
 * @param keywordsParam      the keywords for this mood
 */

  public void setMoodTimeout(int moodParam, int timeOutParam) {
    moodTimeout.setElementAt(new Integer(timeOutParam), moodParam);
  }


/**
 * Returns the Image for the User's back.
 */

  public synchronized Image getBackAvatar() {
    if (backAvatar != null)
      return backAvatar.getImage();
    else
      return null;
  }


/**
 * Returns the Image for a certain mood.
 *
 * @param moodParam      the mood to return the Image for
 */

  public synchronized Image getAvatar(int moodParam) {
    if (moodParam < avatar.size() && avatar.elementAt(moodParam) != null)
      return ((SerializableImage)avatar.elementAt(moodParam)).getImage();
    else if (avatar.size() > 0 && avatar.elementAt(0) != null)
      return ((SerializableImage)avatar.elementAt(0)).getImage();
    else
      return null;
  }


/**
 * Returns the Image for the User's current mood.
 */

  public synchronized Image getAvatar() {
    return getAvatar(getMood());
  }


/**
 * Sets the Image for a certain mood.
 *
 * @param moodParam       the mood to set the Image for
 * @param imageParam      the Image
 */

  public synchronized void setAvatar(int moodParam, Image imageParam) {
    avatar.setElementAt(imageParam == null ? null : new SerializableImage(imageParam), moodParam);
  }

  public synchronized void setAvatarBuffer(int moodParam, byte[] buffer) {
    avatar.setElementAt(buffer == null ? null : new SerializableImage(buffer), moodParam);
  }

/**
 * Sets the Image for the User's back.
 *
 * @param imageParam      the Image for the User's back
 */

  public synchronized void setBackAvatar(Image imageParam) {
    backAvatar = (imageParam == null) ? null : new SerializableImage(imageParam);
  }

  public synchronized void setBackAvatarBuffer(byte[] buffer) {
    backAvatar = (buffer == null ? null : new SerializableImage(buffer));
  }


/**
 * Sets the User's current message.
 *
 * @param messageParam      the User's message
 */

  public synchronized void setMessage(String messageParam) {
    message =  messageParam;
  }


/**
 * Returns the User's last message.
 */

  public synchronized String getMessage() {
    return message;
  }


/**
 * Returns the User's mood for a certain message text. This will be a mood that is
 * connected to a keyword contained in the message or the current mood.
 *
 * @param messageParam      the message text
 */

  public synchronized int getMood(String messageParam) {
    StringTokenizer messageTokenizer;
    String strWord;
    messageParam = messageParam.toLowerCase();

    for (int i = 0; i < getNrOfMoods(); i++) {
      String keyword = (String)moodKeywords.elementAt(i);
      if (keyword != null) {
        keyword = keyword.toLowerCase();
        messageTokenizer = new StringTokenizer(messageParam, " ,.;!:-?\"()/");
        while(messageTokenizer.hasMoreTokens()) {
            strWord = messageTokenizer.nextToken();
            if (((String)moodKeywords.elementAt(i)).indexOf(strWord) != -1) {
                return i;
            }
        }
      }
    }
    return getMood();
  }


/**
 * Clones the User.
 */

  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException excpt) {
      return null;
    }
  }


/**
 * Returns the number of moods the User has.
 */

  public synchronized int getNrOfMoods() {
    return avatar.size();
  }


/**
 * Sets the number of moods for the User.
 *
 * @param nrOfMoodsParam      the new number of moods
 */

  public synchronized void setNrOfMoods(int nrOfMoodsParam) {
    avatar.setSize(nrOfMoodsParam);
    avatarURL.setSize(nrOfMoodsParam);
    moodKeywords.setSize(nrOfMoodsParam);
    moodName.setSize(nrOfMoodsParam);
    moodTimeout.setSize(nrOfMoodsParam);
  }


/**
 * Sets whether the User is being used for displaying a commercial banner. It will not
 * be removed after logging in like other demo Users.
 *
 * @param commercialBannerParam      true if the User is being used for displaying a
 *                                   commercial banner
 */

  public synchronized void setCommercialBanner(boolean commercialBannerParam) {
    commercialBanner = commercialBannerParam;
  }


/**
 * Returns true if the User is being used for displaying a commercial banner, false
 * if not.
 */

  public synchronized boolean isCommercialBanner() {
    return commercialBanner;
  }

}
