package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.net.*;


/**
 * Works as a repository for commonly used objects and system settings.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public interface ChatRepository {

  public static final int MARGIN = 10;
  public static final int SMALL_MARGIN = MARGIN / 2;
  public static final Dimension MESSAGE_DOT_DIMENSION = new Dimension(2, 10);
  public static final Insets INSETS = new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
  public static final Insets SMALL_INSETS = new Insets(MARGIN / 2, MARGIN / 2, MARGIN / 2, MARGIN / 2);
  public static final Color COMPONENT_BACKGROUND = Color.white;
  public static final Color CONTAINER_BACKGROUND = Color.lightGray;
  public static final Font BIG_FONT = new Font("SansSerif", Font.PLAIN, 14);
  public static final Font STANDARD_FONT = new Font("SansSerif", Font.PLAIN, 12);
  public static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 12);
  public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 10);
  public static final Font FIXED_FONT = new Font("Courier", Font.PLAIN, 12);
  public static final int DEFAULT_PORT = 5555;
  public static final int USER_SIZE = 12;
  public static final Dimension ICON_DIMENSION = new Dimension(20, 20);
  public static final Dimension PORTRAIT_DIMENSION = new Dimension(100, 150);
  public static final Dimension ROOM_DIMENSION = new Dimension(150, 150);
  public static final Dimension BALLOON_DIMENSION = new Dimension(150, 75);
  public static final int MINIMUM_DISTANCE = 0;
  public static final int USER_HEIGHT = 8;
  public static final int PHONICAL_RANGE = 60;
  public static final int PHONICAL_ANGLE= 120;
  public static final int VISUAL_RANGE = 240;
  public static final int VISUAL_ANGLE = 160;
  public static final int UNKNOWN_USER_ID = 0;
  public static final int ROBOT_USER_ID = 1000000;
  public static final int NEW_ROOM_ID = -1;

  public static final String ADMIN = "Admin";

  // Define your demo-users here
  public static final String ROBOT_NAME[] = { "Christina", "Frank", "Mark", "Ninja" };
  public static final String ROBOT_PORTRAIT_FILENAME[] = { "woman1.gif", "man1.gif", "man2.gif", "ninja.gif" };
  public static final String ROBOT_MESSAGE[][] = { { "Hello, I am Christina, and who are you?", "Have you seen Frank?", "Welcome to Visual Chat!", "You know you are currently in the demo-mode, and we are not really human, don't you? Please switch back to the original browser window and log in, in order to talk to real people.", "This chat is freeware, why don't you place it upon your own homepage? Check http://visualchat.weirdoz.org/embed.html for details." },
                                                   { "Hello, I am Teddy, and who are you?", "Have you seen Mark?", "Welcome to Visual Chat!", "You know you are currently in the demo-mode, and we are not really human, don't you? Please switch back to the original browser window and log in, in order to talk to real people.", "This chat is freeware, why don't you place it upon your own homepage? Check http://visualchat.weirdoz.org/embed.html for details." },
                                                   { "Hello, I am Mark, and who are you?", "Have you seen Ninja?", "Welcome to Visual Chat!", "You know you are currently in the demo-mode, and we are not really human, don't you? Please switch back to the original browser window and log in, in order to talk to real people.", "This chat is freeware, why don't you place it upon your own homepage? Check http://visualchat.weirdoz.org/embed.html for details." },
                                                   { "Hello, I am Ninja, and who are you?", "Have you seen Christina?", "Welcome to Visual Chat!", "You know you are currently in the demo-mode, and we are not really human, don't you? Please switch back to the original browser window and log in, in order to talk to real people.", "This chat is freeware, why don't you place it upon your own homepage? Check http://visualchat.weirdoz.org/embed.html for details." } };
  public static final boolean ROBOT_COMMERCIAL_BANNER[] = { false, false, false, false };

  public static final int PREDEFINED_NR_OF_MOODS = 6;
  public static final String MOOD_ICON_NAME[] = { "icon0.gif", "icon1.gif", "icon2.gif", "icon3.gif", "icon4.gif", "icon5.gif" };
  public static final String EMPTY_ICON_NAME = "iconEmpty.gif";
  public static final String UNKNOWN_ICON_NAME = "iconUnknown.gif";
  public static final int MAX_PORTRAIT_SIZE = 200000;
  public static final int MAX_USERFILE_SIZE = 400000;
  /*
  public static final String STANDARD_ROOM_NAME[] = { "Lobby", "Java", "Computers", "Sports", "Austria", "Internet", "TK-Chat" , "Politics", "University", "Fun", "Nonsense", "Today", "USA", "Private" };
  public static final boolean STANDARD_ROOM_PRIVATE[] = { false, false, false, false, false, false, false, false, false, false, false, false, false, true };
  public static final boolean STANDARD_ROOM_INVITED[] = { false, false, false, false, false, false, false, false, false, false, false, false, false, false };
  */
  public static final String STANDARD_ROOM_NAME[] = { "Lobby" };
  public static final boolean STANDARD_ROOM_PRIVATE[] = { false };
  public static final boolean STANDARD_ROOM_INVITED[] = { false };

  public static final int MAX_COLOR_VALUE = 160;
  public static final String IMAGE_FOLDER = "images/";
  public static final String IMAGE_SLIDER = "slider.gif";
  public static final Dimension NAVIGATION_ARROW_DIMENSION = new Dimension(20, 20);
  public static final String IMAGE_ARROW_BACKWARD = "arrowbackward.gif";
  public static final String IMAGE_ARROW_LEFT = "arrowleft.gif";
  public static final String IMAGE_ARROW_RIGHT = "arrowright.gif";
  public static final String IMAGE_ARROW_FORWARD = "arrowforward.gif";
  public static final String MOOD_NAME[] = { "Happy", "Normal", "Serious", "Talking", "Angry", "Sad" };
  public static final String LOGO_FILENAME = "logo.gif";
  public static final String ICON_FILENAME = "v.gif";
  public static final String LOGO_URL = "http://www.tk.uni-linz.ac.at";
  public static final Dimension LOGO_DIMENSION = new Dimension(32, 32);

}