package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;


/**
 * Displays icons for navigating through a Room.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class NavigationCanvas extends Canvas implements MouseListener, MouseMotionListener, Runnable {

  private Image imgForward, imgBackward, imgLeft, imgRight;
  private Rectangle rectForward, rectBackward, rectLeft, rectRight;
  private ChatApplet chatApplet;
  private Thread mouseListenerThread;

  private static final int PROLONGATION_DELAY = 100;
  private static final int INITIAL_PROLONGATION_DELAY = 250;

  private static final int MOTION_NONE = -1;
  private static final int MOTION_FORWARD = 0;
  private static final int MOTION_LEFT = 1;
  private static final int MOTION_RIGHT = 2;
  private static final int MOTION_BACKWARD = 3;
  private int motion = MOTION_NONE;
  private int prevMotion = MOTION_NONE;

/**
 * Constructs the NavigationCanvas.
 *
 * @param chatAdministratorParam      the ChatApplet which administrates the
 *                                    users
 */

  public NavigationCanvas(ChatApplet chatAdministratorParam,
    Image imgForwardParam,
    Image imgBackwardParam,
    Image imgLeftParam,
    Image imgRightParam) {
    chatApplet = chatAdministratorParam;

    imgForward = imgForwardParam;
    imgBackward = imgBackwardParam;
    imgLeft = imgLeftParam;
    imgRight = imgRightParam;

    updateRectangles();

    addMouseListener(this);
    addMouseMotionListener(this);
    mouseListenerThread = new Thread(this);
  }


/**
 * Returns the preferred size of the NavigationCanvas.
 */

  public Dimension getPreferredSize() {
    return getCalculatedSize();
  }


/**
 * Returns the minimum size of the NavigationCanvas.
 */

  public Dimension getMinimumSize() {
    return getCalculatedSize();
  }


/**
 * Calculates and returns the optimal size of the NavigationCanvas.
 */

  private Dimension getCalculatedSize() {
    return new Dimension(ChatRepository.MARGIN * 2 + ChatRepository.NAVIGATION_ARROW_DIMENSION.width * 3, ChatRepository.MARGIN * 2 + ChatRepository.NAVIGATION_ARROW_DIMENSION.height * 3);
  }


/**
 * Calculates the imagemap of the NavigationCanvas's icons regarding to its actual
 * size.
 */

  private void updateRectangles() {
    rectForward = new Rectangle((getSize().width - imgForward.getWidth(this)) / 2, getSize().height / 2 - ChatRepository.NAVIGATION_ARROW_DIMENSION.height * 3 / 2, imgForward.getWidth(this), imgForward.getHeight(this));
    rectLeft = new Rectangle(getSize().width / 2 - ChatRepository.NAVIGATION_ARROW_DIMENSION.width * 3 / 2, (getSize().height - imgLeft.getHeight(this)) / 2, imgLeft.getWidth(this), imgLeft.getHeight(this));
    rectRight = new Rectangle(getSize().width / 2 + ChatRepository.NAVIGATION_ARROW_DIMENSION.width / 2, (getSize().height - imgRight.getHeight(this)) / 2, imgRight.getWidth(this), imgRight.getHeight(this));
    rectBackward = new Rectangle((getSize().width - imgBackward.getWidth(this)) / 2, getSize().height / 2 + ChatRepository.NAVIGATION_ARROW_DIMENSION.height / 2, imgBackward.getWidth(this), imgBackward.getHeight(this));
  }


/**
 * Paints the NavigationPanel.
 *
 * @param g      the graphics context to use for painting
 */

  public void paint(Graphics g) {

    updateRectangles();
    g.drawImage(imgForward, rectForward.x, rectForward.y, this);
    g.drawImage(imgLeft, rectLeft.x, rectLeft.y, this);
    g.drawImage(imgRight, rectRight.x, rectRight.y, this);
    g.drawImage(imgBackward, rectBackward.x, rectBackward.y, this);
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event      the MouseEvent
 */
  public void mouseClicked(MouseEvent event) {
    synchronized(this) {
	  if (motion == MOTION_NONE) {
	    setMotion(event);
        motion();
        motion = MOTION_NONE;
        repaint();
	  }
    }
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
    synchronized(this) {
      prevMotion = motion;
      motion = MOTION_NONE;
      repaint();
    }
  }


/**
 * Invoked when a mouse button has been pressed on a component.
 *
 * @param event      the MouseEvent
 */

  public void mousePressed(MouseEvent event) {
    synchronized(this) {
	  setMotion(event);
      motion();
      if (!mouseListenerThread.isAlive()) {
      	mouseListenerThread.start();
      }
    }
  }


/**
 * Invoked when a mouse button has been released on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
    synchronized(this) {
      prevMotion = motion;
      motion = MOTION_NONE;
      repaint();
      // mouseListenerThread.suspend();
    }
  }


/**
 * Invoked when a mouse button is pressed on a component and then dragged.
 *
 * @param event      the MouseEvent
 */

  public void mouseDragged(MouseEvent event) {
    synchronized(this) {
      mousePressed(event);
    }
  }


/**
 * Invoked when the mouse button has been moved on a component (with no buttons no down).
 *
 * @param event      the MouseEvent
 */

  public void mouseMoved(MouseEvent event) {
  }

  private void setMotion(MouseEvent event) {
      prevMotion = motion;
      if (rectBackward != null && rectBackward.contains(event.getPoint())) {
        motion = MOTION_BACKWARD;
      }
      else if (rectForward != null && rectForward.contains(event.getPoint())) {
        motion = MOTION_FORWARD;
      }
      else if (rectLeft != null && rectLeft.contains(event.getPoint())) {
        motion = MOTION_LEFT;
      }
      else if (rectRight != null && rectRight.contains(event.getPoint())) {
        motion = MOTION_RIGHT;
      }
      else {
        motion = MOTION_NONE;
      }
  }

/**
 * Contains functionality for all possible MouseEvents on the NavigationPanel.
 *
 * @param event      the MouseEvent
 */

  public void motion() {
    synchronized(this) {
	    Point userPos = chatApplet.getCurrentUser().getPosition();
 	    int userHeading = chatApplet.getCurrentUser().getHeading();
	    int userId = chatApplet.getCurrentUser().getId();

      if (motion == MOTION_BACKWARD) {
        if (prevMotion != motion) {
          getGraphics().drawImage(imgBackward, rectBackward.x + 1, rectBackward.y + 1, this);
        }
        chatApplet.setUserPosition(userId, new Point(userPos.x - (int)(ChatMath.getCos(userHeading) * 4.0), userPos.y + (int)(ChatMath.getSin(userHeading) * 4.0)), true);
      }

      else if (motion == MOTION_FORWARD) {
        if (prevMotion != motion) {
          getGraphics().drawImage(imgForward, rectForward.x + 1, rectForward.y + 1, this);
        }
        chatApplet.setUserPosition(userId, new Point(userPos.x + (int)(ChatMath.getCos(userHeading) * 4.0), userPos.y - (int)(ChatMath.getSin(userHeading) * 4.0)), true);
      }

      else if (motion == MOTION_LEFT) {
        if (prevMotion != motion) {
          getGraphics().drawImage(imgLeft, rectLeft.x + 1, rectLeft.y + 1, this);
        }
        chatApplet.setUserHeading(userId, userHeading + 4, true);
      }

      else if (motion == MOTION_RIGHT) {
        if (prevMotion != motion) {
          getGraphics().drawImage(imgRight, rectRight.x + 1, rectRight.y + 1, this);
        }
        chatApplet.setUserHeading(userId, userHeading - 4, true);
      }
    }
  }

  public void run() {
    while (true) {
      try {
        Thread.sleep(motion == MOTION_NONE ? INITIAL_PROLONGATION_DELAY : PROLONGATION_DELAY);
      }
      catch (InterruptedException excpt) {
      }
      synchronized(this) {
        if (motion != MOTION_NONE) {
            motion();
        }
      }
    }
  }


}