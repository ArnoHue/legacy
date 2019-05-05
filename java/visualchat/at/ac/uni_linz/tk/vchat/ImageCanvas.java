package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.net.*;


/**
 * Works as a Container for Images, that fill be scaled to fit into the
 * ImageCanvas'es Dimension.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ImageCanvas extends Canvas implements MouseListener, Cloneable {

  private Image image;
  private ChatApplet chatApplet;
  private String url;


/**
 * Constructs the ImageCanvas.
 */

  public ImageCanvas() {
    this(null, new Dimension(0, 0));
  }


/**
 * Constructs the ImageCanvas.
 *
 * @param sizeParam       the Dimension of the ImageCanvas
 */

  public ImageCanvas(Dimension sizeParam) {
    this(null, sizeParam);
  }


/**
 * Constructs the ImageCanvas.
 *
 * @param imageParam       the Image to fill the ImageCanvas
 * @param sizeParam        the Dimension of the ImageCanvas
 */

  public ImageCanvas(Image imageParam, Dimension sizeParam) {
    super();
    image = imageParam;
    setSize(sizeParam);
  }


/**
 * Constructs the ImageCanvas.
 *
 * @param imageParam       the Image to fill the ImageCanvas
 */

  public void setImage(Image imageParam) {
    image = imageParam;
  }


/**
 * Paints the ImageCanvas.
 *
 * @param g       the graphics context
 */

  public void paint(Graphics g) {
    if (image != null)
      g.drawImage(image, 0, 0, getSize().width, getSize().height, this);
  }


/**
 * Asserts a link to the image, whose URL will be opened in a new browser window when clicking
 * on it.
 *
 * @param chatAdministratorParam      the ChatApplet (implements showDocument())
 * @param urlParam                    the String of the URL to be opened
 */

  public void showDocumentOnClick(ChatApplet chatAdministratorParam, String urlParam) {
    if (chatAdministratorParam != null) {
      chatApplet = chatAdministratorParam;
      url = urlParam;
      addMouseListener(this);
    }
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
    try {
      chatApplet.showDocument(new URL(url), "_blank");
    }
    catch (MalformedURLException excpt) {
    }
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseEntered(MouseEvent event) {
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }


/**
 * Invoked when the mouse exits a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }


/**
 * Invoked when a mouse button has been pressed on a component.
 *
 * @param event      the MouseEvent
 */

  public void mousePressed(MouseEvent event) {
  }


/**
 * Invoked when a mouse button has been released on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
  }


/**
 * Clones the ImageCanvas.
 */

  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException excpt) {
      return null;
    }
  }

}
