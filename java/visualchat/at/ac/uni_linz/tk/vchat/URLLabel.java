package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;


/**
 * A clickable Label which can be used to open an URL in a browser-window.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class URLLabel extends Component implements MouseListener {

  private String text, url, target;
  private ChatApplet chatApplet;


/**
 * Constructs the URLLabel.
 *
 * @param textParam                the URLLabel's text
 * @param urlParam                 the String of the URL to be shown after clicking
 *                                 on the URLLabel
 * @param chatParam       the ChatApplet repsonsible for opening the
 *                                 URL
 */

  public URLLabel(String textParam, String urlParam, ChatApplet chatParam) {
    this(textParam, urlParam, "_blank", chatParam);
  }


/**
 * Constructs the URLLabel.
 *
 * @param textParam                the URLLabel's text
 * @param urlParam                 the String of the URL to be shown after clicking
 *                                 on the URLLabel
 * @param targetParam              the name of the target browser window
 * @param chatParam       the ChatApplet repsonsible for opening the
 *                                 URL
 */

  public URLLabel(String textParam, String urlParam, String targetParam, ChatApplet chatParam) {
    text = textParam;
    url = urlParam;
    target = targetParam;
    chatApplet = chatParam;
    addMouseListener(this);
    setForeground(Color.blue);
  }


/**
 * Paints the URLLabel.
 *
 * @param g       the graphics context
 */

  public void paint(Graphics g) {
    int y;
    y = getSize().height - (getSize().height - getFontMetrics(getFont()).getHeight()) / 2;
    g.setColor(getForeground());
    g.drawString(text, 0, y - 2);
    g.drawLine(0, y, getFontMetrics(getFont()).stringWidth(text), y);
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
    try {
      chatApplet.showDocument(new URL(url), target);
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
    setForeground(Color.red);
    repaint();
  }


/**
 * Invoked when the mouse exits a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    setForeground(Color.blue);
    repaint();
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

}

