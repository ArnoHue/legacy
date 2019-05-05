package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.applet.*;
import java.io.*;


/**
 * Provides frequently needed utility methods.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ChatUtil extends Object {


/**
 * Returns the distance between two Points.
 *
 * @param point1      the first point
 * @param point2      the second point
 */

  public static int getDistance(Point point1, Point point2) {
    return (int)Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
  }


/**
 * Brightens up a given color.
 *
 * @param color      the color to be brightened
 */

  public static Color brighten(Color color) {
    return new Color((255 + color.getRed()) / 2, (255 + color.getGreen()) / 2, (255 + color.getBlue()) / 2);
  }


/**
 * Adds up two angles.
 *
 * @param angle1      the first angle
 * @param angle2      the second angle
 */

  public static int addAngle(int angle1, int angle2) {
    return (angle1 + angle2) % 360;
  }


/**
 * Substracts one angle from another angle.
 *
 * @param angle1      the angle to substract from
 * @param angle2      the angle to be substracted
 */

  public static int subAngle(int angle1, int angle2) {
    int angle;
    return Math.abs(angle1 - angle2 + 360) % 360;
  }


/**
 * Returns the mean value of two angles.
 *
 * @param angle1      the first angle
 * @param angle2      the second angle
 */

  public static int getMiddleAngle(int angle1, int angle2) {
    int angle;
    angle = (angle1 + angle2) / 2 % 360;
    return Math.abs(angle1 - angle2) > 180 ? (angle + 180) % 360 : angle;
  }


/**
 * Returns the angle bewteen two Points (&lt; 180 degrees).
 *
 * @param point1      the first Point
 * @param point2      the second Point
 */

  public static int getAngle(Point point1, Point point2) {
    return ((int)(Math.atan2((double)(point1.y - point2.y), (double)(point2.x - point1.x)) / ChatMath.PI_RAD) + 360) % 360;
  }


/**
 * Returns true if one angle lies in bewteen two given angles.
 *
 * @param angle           the angle to be checked
 * @param angleStart      the minimum angle
 * @param angleStop       the maximum angle
 */

  public static boolean inAngleRange(int angle, int angleStart, int angleStop) {
    if (angleStart > angleStop)
      return (!inAngleRange(angle, angleStop, angleStart));
    else
      return (angle >= angleStart && angle <= angleStop);
  }


/**
 * Paints a pattern onto a given Graphics conext.
 *
 * @param g                the Graphics context
 * @param clipRect         the clipping area
 * @param colorParam       the color in which to paint
 */

  public static void paintPattern(Graphics g, Rectangle clipRect, Color colorParam) {
    Rectangle previousClipRect;
    Color previousColor;

    previousClipRect = (Rectangle)(g.getClip());
    previousColor = g.getColor();

    g.setClip(clipRect);
    g.setColor(colorParam);

    for (int i = - clipRect.height; i <= clipRect.width; i += 2) {
      g.drawLine(clipRect.x + i, clipRect.y, clipRect.x + i - clipRect.height, clipRect.y + clipRect.height);
      g.drawLine(clipRect.x + i, clipRect.y, clipRect.x + i + clipRect.height, clipRect.y + clipRect.height);
    }

    g.setClip(previousClipRect);
    g.setColor(previousColor);
  }


/**
 * Adds a Component to a Container's GridBagLayout.
 *
 * @param containerParam         the Container to add to
 * @param componentParam         the Comnponent to be added
 * @param constraintsParam       the GridBagConstraints to use for adding
 */

  public static void addWithConstraints(Container containerParam, Component componentParam, GridBagConstraints constraintsParam) {
    if (!(containerParam.getLayout() instanceof GridBagLayout))
      containerParam.setLayout(new GridBagLayout());
    ((GridBagLayout)containerParam.getLayout()).setConstraints(componentParam, constraintsParam);
    containerParam.add(componentParam);
  }

/**
 * Adds a Component to the beginning of a new row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithBeginningConstraints(Container containerParam, Component componentParam, Insets insetsParam) {
    GridBagConstraints constraints;
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(insetsParam.top / 2, insetsParam.left / 2, insetsParam.bottom / 2, insetsParam.right / 2);
    constraints.fill = GridBagConstraints.BOTH;
    addWithConstraints(containerParam, componentParam, constraints);
  }


/**
 * Adds a Component to the beginning of a new row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithBeginningConstraints(Container containerParam, Component componentParam) {
    addWithBeginningConstraints(containerParam, componentParam, ChatRepository.INSETS);
  }


/**
 * Adds a Component to the remainings of a row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithRemainingConstraints(Container containerParam, Component componentParam, Insets insetsParam) {
    GridBagConstraints constraints;
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(insetsParam.top / 2, insetsParam.left / 2, insetsParam.bottom / 2, insetsParam.right / 2);
    constraints.weightx = 1.0;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    addWithConstraints(containerParam, componentParam, constraints);
  }


/**
 * Adds a Component to the remainings of a row in a Container's GridBagLayout.
 *
 * @param containerParam      the Container to add to
 * @param componentParam      the Comnponent to be added
 */

  public static void addWithRemainingConstraints(Container containerParam, Component componentParam) {
    addWithRemainingConstraints(containerParam, componentParam, ChatRepository.INSETS);
  }


/**
 * Returns a formated Date value (short format).
 *
 * @param dateParam      the Date to be formatted
 */

  public static String dateToTimeString(Date dateParam) {
    return DateFormat.getTimeInstance(DateFormat.SHORT).format(dateParam);
  }


/**
 * Rotates a point around another point by a given angle.
 *
 * @param centerPoint      the center Point of the rotation
 * @param rotatePoint      the Point to be rotated
 * @param rotateAngle      the angle of the rotation
 */

  public static Point rotatePoint(Point centerPoint, Point rotatePoint, int rotateAngle) {
    int angle, distance;
    distance = getDistance(centerPoint, rotatePoint);
    angle = addAngle(getAngle(centerPoint, rotatePoint), rotateAngle);
    rotatePoint.x = centerPoint.x + (int)(ChatMath.getCos(angle) * distance);
    rotatePoint.y = centerPoint.y + (int)(ChatMath.getSin(angle) * distance);
    return rotatePoint;
  }


/**
 * Returns the signum of a given double value.
 *
 * @param x      the double value to calculate the signum of
 */

  public static double sgn(double x) {
    return (x != 0.0) ? Math.abs(x) / x : 0.0;
  }


/**
 * Converts an array of bytes to an array of ints.
 *
 * @param byteArray      the byte array to be converted
 */

  public static int[] byteArrayToIntArray(byte[] byteArray) {
    int[] intArray = new int[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      intArray[i] = (int)byteArray[i];
    }
    return intArray;
  }


/**
 * Converts an array of ints to an array of bytes.
 *
 * @param intArray      the int array to be converted
 */

  public static byte[] intArrayToByteArray(int[] intArray) {
    byte[] byteArray = new byte[intArray.length];
    for (int i = 0; i < intArray.length; i++) {
      byteArray[i] = (byte)intArray[i];
    }
    return byteArray;
  }


/**
 * Loads an image from a given URL.
 *
 * @param url      the image's URL
 */

  public static Image getImage (URL url) {
    Image image;
    MediaTracker tracker = new MediaTracker(new Label());

    image = Toolkit.getDefaultToolkit().getImage(url);
    tracker.addImage(image, 0);

    try {
      tracker.waitForAll();
    }
    catch (InterruptedException e)
    { }

    return image;
  }

  public static byte[] getBinaryContent(URL url) throws IOException {
    InputStream connIn = null;
    try {
      URLConnection conn = url.openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(false);
      connIn = conn.getInputStream();
      BufferedInputStream in = new BufferedInputStream(connIn);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[2048];
      int cnt = 0;
      while ((cnt = in.read(buffer, 0, 2048)) != -1) {
        out.write(buffer, 0, cnt);
      }

      return out.toByteArray();
    }
    finally {
      if (connIn != null) {
        connIn.close();
     }
    }
  }

/**
 * Loads an image with a given filename from the server.
 *
 * @param name      the image's filename
 */

  public static byte[] getBinaryContent (String name) throws IOException {
    FileInputStream fIn = new FileInputStream(name);
    try {
      BufferedInputStream in = new BufferedInputStream(fIn);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[2048];
      int cnt = 0;
      while ((cnt = in.read(buffer, 0, 2048)) != -1) {
        out.write(buffer, 0, cnt);
      }

      return out.toByteArray();
    }
    finally {
      if (fIn != null) {
        fIn.close();
     }
    }
  }


/**
 * Fills up a String to a certain length.
 *
 * @param sourceString      the original String
 * @param fillChar          the char to use for filling up the String
 * @param length            the length of the String to be returned
 */

  public static String pad(String sourceString, char fillChar, int length) {
    return pad (sourceString, fillChar, length, true);
  }


/**
 * Fills up a String to a certain length.
 *
 * @param sourceString        the original String
 * @param fillChar            the char to use for filling up the String
 * @param length              the length of the String to be returned
 * @param orientatedLeft      true if the orginal String should be placed at the end
 */

  public static String pad(String sourceString, char fillChar, int length, boolean orientatedLeft) {
    if (sourceString.length() >= length)
      return sourceString.substring(0, length);
    else {
      StringBuffer wStrBuf = new StringBuffer(length);
      wStrBuf.insert(0, sourceString);
      while (wStrBuf.length() < length) {
        if (orientatedLeft)
          wStrBuf.append(fillChar);
        else
          wStrBuf.insert(0, fillChar);
      }
      return wStrBuf.toString();
    }
  }

  public static int getInt(String string) {
    try {
      return new Integer(string).intValue();
    }
    catch (Exception excpt) {
      return 0;
    }
  }

}
