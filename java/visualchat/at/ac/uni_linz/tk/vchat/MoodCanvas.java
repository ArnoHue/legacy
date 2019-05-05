package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Used as a graphical representance of the actual mood of the User. Allows the
 * User to change his mood by clicking on icons surrounding the avatar.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class MoodCanvas extends PortraitCanvas implements MouseListener, MouseMotionListener, ActionListener {

  private static final double PORTRAIT_SCALE = 0.6;

  private ChatApplet chatApplet;
  private Image bufferImage;

  private PopupMenu moodPopup;


/**
 * Constructs the MessageEntry.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 */

  public MoodCanvas(ChatApplet chatAdministratorParam) {
    super();
    portraitScale = PORTRAIT_SCALE;
    chatApplet = chatAdministratorParam;

    moodPopup = new PopupMenu();
    add(moodPopup);

    addMouseListener(this);
    addMouseMotionListener(this);
    moodPopup.addActionListener(this);
  }


/**
 * Sets the image that will be displayed in the MoodCanvas'es center.
 *
 * @param portraitImage      the image to be displayed
 */

  public void setImage(Image portraitImage) {
    image = portraitImage;
  }


/**
 * Paints the MoodCanvas.
 *
 * @param g       the graphics context
 */

  public void paint (Graphics g) {
    Graphics bufferGraphics;
    int canvasWidth, canvasHeight;

    moodPopup.removeAll();
    for (int i = 0; i < chatApplet.getCurrentUser().getNrOfMoods(); i++) {
      moodPopup.add(chatApplet.getCurrentUser().getMoodName(i));
    }

    canvasWidth = getSize().width;
    canvasHeight = getSize().height;

    if (bufferImage == null || bufferImage.getWidth(this) != canvasWidth || bufferImage.getHeight(this) != canvasHeight)
      bufferImage = createImage(canvasWidth, canvasHeight);

    bufferGraphics = bufferImage.getGraphics();
    bufferGraphics.setColor(getBackground());
    bufferGraphics.fillRect(0, 0, canvasWidth, canvasHeight);

    setImage(chatApplet.getUserAvatar(chatApplet.getCurrentUserId()));
    super.paint(bufferGraphics);

    bufferGraphics.setColor(Color.darkGray);
    bufferGraphics.drawOval(ChatRepository.ICON_DIMENSION.width / 2, ChatRepository.ICON_DIMENSION.height / 2, canvasWidth - ChatRepository.ICON_DIMENSION.width, canvasHeight - ChatRepository.ICON_DIMENSION.height);
    bufferGraphics.setColor(Color.black);

    for (int i = 0; i < chatApplet.getCurrentUser().getNrOfMoods(); i += 1) {
 	  double angle = i * 360 / chatApplet.getCurrentUser().getNrOfMoods();
 	  int x = (int)(canvasWidth / 2 + ChatMath.getSin(angle) * (canvasWidth / 2 - ChatRepository.ICON_DIMENSION.width / 2)) - ChatRepository.ICON_DIMENSION.width / 2;
 	  int y = (int)(canvasHeight / 2 + ChatMath.getCos(angle) * (canvasHeight / 2  - ChatRepository.ICON_DIMENSION.height / 2) - ChatRepository.ICON_DIMENSION.height / 2);
      bufferGraphics.drawImage(chatApplet.getMoodIcon(i), x, y, this);
      if (i == chatApplet.getCurrentUser().getMood()) {
        bufferGraphics.drawOval(x, y, ChatRepository.ICON_DIMENSION.width - 1, ChatRepository.ICON_DIMENSION.height - 1);
        bufferGraphics.drawOval(x + 1, y + 1, ChatRepository.ICON_DIMENSION.width - 3, ChatRepository.ICON_DIMENSION.height - 3);
      }
    }

    g.drawImage(bufferImage, 0, 0, this);

  }


/**
 * Returns the mood that is related to a certain Point in the MoodCanvas.
 *
 * @param pointParam       the Point the User clicked on
 */

  public int getMood(Point pointParam) {
    return (ChatUtil.addAngle((ChatUtil.getAngle(new Point(getSize().width / 2, getSize().height / 2), pointParam)), (90 + 360 / chatApplet.getCurrentUser().getNrOfMoods() / 2))) * chatApplet.getCurrentUser().getNrOfMoods() / 360;
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseEntered(MouseEvent event) {
  }


/**
 * Invoked when the mouse exits a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
  }


/**
 * Invoked when a mouse button has been pressed on a component.
 *
 * @param event      the MouseEvent
 */

  public void mousePressed(MouseEvent event) {
    if (!event.isMetaDown() && getMood(event.getPoint()) != chatApplet.getCurrentUser().getMood()) {
      chatApplet.setUserMood(chatApplet.getCurrentUser().getId(), getMood(event.getPoint()), true);
    }
  }


/**
 * Invoked when a mouse button has been released on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
    if (event.isMetaDown())
      moodPopup.show(this, event.getX(), event.getY());
    else if (getMood(event.getPoint()) != chatApplet.getCurrentUser().getMood())
      chatApplet.setUserMood(chatApplet.getCurrentUser().getId(), getMood(event.getPoint()), true);
  }


/**
 * Invoked when a mouse button is pressed on a component and then dragged.
 *
 * @param event      the MouseEvent
 */

  public void mouseDragged(MouseEvent event) {
    if (!event.isMetaDown() && getMood(event.getPoint()) != chatApplet.getCurrentUser().getMood()) {
      chatApplet.setUserMood(chatApplet.getCurrentUser().getId(), getMood(event.getPoint()), true);
    }
  }


/**
 * Invoked when the mouse button has been moved on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseMoved(MouseEvent event) {
  }


/**
 * Invoked when an action occurs.
 *
 * @param event       the ActionEvent
 */
  public void actionPerformed(ActionEvent event) {

    if (event.getSource() == moodPopup) {
      for (int i = 0; i < moodPopup.getItemCount(); i++) {
        if (moodPopup.getItem(i).getLabel().equals(event.getActionCommand()))
          chatApplet.setUserMood(chatApplet.getCurrentUserId(), i, chatApplet.isConnected());
      }
    }
  }

}