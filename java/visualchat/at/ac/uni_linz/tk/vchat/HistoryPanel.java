package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;


/**
 * A HistoryEntry is a graphical representation of which User sent a message at which
 * point in time.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class HistoryPanel extends FramedPanel implements Runnable, MouseListener, MouseMotionListener {

  private Image imgBuffer, imgSlider;
  private Vector vecMessageEntry;
  private ChatApplet chatAdm;
  private Date dateFrom, dateTo, dateHist;
  private Thread thrUpdate;
  private int iMaxMessageLength;


/**
 * Constructs the HistoryPanel.
 *
 * @param strTitleParam      the HistoryPanel's title
 * @param chatAdmParam       the ChatApplet, which will be informed if the
 *                           User enters the history mode
 */

  public HistoryPanel(String strTitleParam, ChatApplet chatAdmParam) {
    super(strTitleParam, ChatRepository.INSETS);

    chatAdm = chatAdmParam;
    imgSlider = chatAdm.getSliderImage();

    clear();

    addMouseListener(this);
    addMouseMotionListener(this);

    thrUpdate = new Thread(this);
    thrUpdate.setPriority(Thread.MIN_PRIORITY);
    thrUpdate.start();
  }


/**
 * Adds a MessageEntry. It will be displayed as a filled Rectangle of a certain
 * height.
 *
 * @param dateParam            the Date when the message was sent
 * @param strMessageParam      the message text
 * @param colParam             the User's color
 */

  public void addMessageEntry(Date dateParam, String strMessageParam, Color colParam) {
    vecMessageEntry.addElement(new MessageEntry(dateParam, strMessageParam, colParam));
    iMaxMessageLength = Math.max(iMaxMessageLength, strMessageParam.length());
  }


/**
 * Sets the Date when to start the history.
 *
 * @param dateFromParam      the history start Date
 */

  public void setFromDate(Date dateFromParam) {
    vecMessageEntry = new Vector();
    iMaxMessageLength = 1;
    dateFrom = dateFromParam;
    dateHist = dateFromParam;
    setToDate(new Date(dateFromParam.getTime() + 1000l));
    repaint();
  }


/**
 * Sets the Date when to end the history.
 *
 * @param dateToParam      the history end Date
 */

  public void setToDate(Date dateToParam) {
    dateTo = dateToParam;
    repaint();
  }


/**
 * Enters history mode.
 *
 * @param dateHistParam      the Date of the history mode
 */

  private void enterHistoryMode(Date dateHistParam) {
    dateHist = dateHistParam;
    chatAdm.enterHistoryMode(dateHist);
    repaint();
  }


/**
 * Returns the time of the history start Date.
 */

  public long getFromTime() {
    return dateFrom.getTime();
  }


/**
 * Returns the time of the history end Date.
 */

  public long getToTime() {
    return dateTo.getTime();
  }


/**
 * Returns the upper y-coordinate of the scrollbar.
 */

  public int getBarTop() {
    return ChatRepository.SMALL_MARGIN * 2 + ChatRepository.MESSAGE_DOT_DIMENSION.height;
  }


/**
 * Returns the lower y-coordinate of the scrollbar.
 */

  public int getBarBottom() {
    return ChatRepository.SMALL_MARGIN * 2 + ChatRepository.MESSAGE_DOT_DIMENSION.height + imgSlider.getHeight(this);
  }


/**
 * Returns the left x-coordinate of the scrollbar.
 */

  public int getBarLeft() {
    return ChatRepository.SMALL_MARGIN;
  }


/**
 * Returns the right x-coordinate of the scrollbar.
 */

  public int getBarRight() {
    return getSize().width - ChatRepository.SMALL_MARGIN;
  }


/**
 * Returns the x-coordinate of the slider representing a certain Date.
 *
 * @param lTimeParam      the long value of the Date
 */

  public int getSliderX(long lTimeParam) {
    try {
      return (int)(getBarLeft() + (getBarRight() - getBarLeft()) * (lTimeParam - getFromTime()) / (getToTime() - getFromTime()) - imgSlider.getWidth(this) / 2);
    }
    catch (Exception excpt) {
      return 0;
    }
  }


/**
 * Returns the x-coordinate of the slider representing a certain Date.
 *
 * @param datParam      the Date
 */

  public int getSliderX(Date datParam) {
    return getSliderX(datParam.getTime());
  }


/**
 * Returns the x-coordinate representing a certain Date.
 *
 * @param lTimeParam      the long value of the Date
 */

  public int getDotX(long lTimeParam) {
    return (int)(getBarLeft() + (getBarRight() - getBarLeft()) * (lTimeParam - getFromTime()) / (getToTime() - getFromTime()) - ChatRepository.MESSAGE_DOT_DIMENSION.width / 2);
  }


/**
 * Returns the x-coordinate of the slider representing a certain Date.
 *
 * @param datParam      the Date
 */

  public int getDotX(Date datParam) {
    return getDotX(datParam.getTime());
  }


/**
 * Returns the time represented by the current slider position.
 */

  public long getSliderTime() {
    return dateHist.getTime();
  }


/**
 * Returns the Date represented by the current slider position.
 */

  public Date getSliderDate() {
    return dateHist;
  }


/**
 * Updates the HistoryPanel.
 *
 * @param g      the specified context to use for updating
 */

  public void update(Graphics g) {
    paint(g);
  }


/**
 * Paints the HistoryPanel.
 *
 * @param g      the graphics context to use for painting.
 */

  public void paint(Graphics g) {
    int iWidth, iHeight, iMessageDotHeight;
    Graphics graBuffer;
    Enumeration enmKeys;
    MessageEntry msgEntry;

    iWidth = getSize().width;
    iHeight = getSize().height;

    if (imgBuffer == null || imgBuffer.getWidth(this) != iWidth || imgBuffer.getHeight(this) != iHeight) {
      imgBuffer = createImage(iWidth, iHeight);
    }

    graBuffer = imgBuffer.getGraphics();
    graBuffer.setColor(ChatRepository.CONTAINER_BACKGROUND);
    graBuffer.fillRect(0, 0, iWidth, iHeight);

    graBuffer.setColor(Color.black);
    graBuffer.drawLine(getBarLeft(), (getBarTop() + getBarBottom()) / 2, getBarRight(), (getBarTop() + getBarBottom()) / 2);
    graBuffer.drawLine(getBarLeft(), (getBarTop() + getBarBottom()) / 2 + 1, getBarRight(), (getBarTop() + getBarBottom()) / 2 + 1);
    graBuffer.drawLine(getBarLeft(), (getBarTop() + getBarBottom()) / 2 - 2, getBarLeft(), (getBarTop() + getBarBottom()) / 2 + 3);
    graBuffer.drawLine(getBarLeft() + 1, (getBarTop() + getBarBottom()) / 2 - 2, getBarLeft() + 1, (getBarTop() + getBarBottom()) / 2 + 3);
    graBuffer.drawLine(getBarRight(), (getBarTop() + getBarBottom()) / 2 - 2, getBarRight(), (getBarTop() + getBarBottom()) / 2 + 3);
    graBuffer.drawLine(getBarRight() - 1, (getBarTop() + getBarBottom()) / 2 - 2, getBarRight() - 1, (getBarTop() + getBarBottom()) / 2 + 3);

    graBuffer.drawString(DateFormat.getTimeInstance().format(dateFrom), getBarLeft(), getBarBottom() + ChatRepository.SMALL_MARGIN);
    graBuffer.drawString(DateFormat.getTimeInstance().format(dateTo), getBarRight() - getFontMetrics(getFont()).stringWidth(DateFormat.getTimeInstance().format(dateFrom)), getBarBottom() + ChatRepository.SMALL_MARGIN);

    graBuffer.drawImage(imgSlider, getSliderX(dateHist), getBarTop(), this);

    for (int i = 0; i < vecMessageEntry.size(); i++) {
      msgEntry =(MessageEntry)vecMessageEntry.elementAt(i);
      iMessageDotHeight = 1 + (ChatRepository.MESSAGE_DOT_DIMENSION.height - 1) * msgEntry.text.length() / iMaxMessageLength;
      graBuffer.setColor(msgEntry.color);
      graBuffer.fillRect(getDotX(msgEntry.date) - ChatRepository.MESSAGE_DOT_DIMENSION.width / 2, ChatRepository.SMALL_MARGIN + ChatRepository.MESSAGE_DOT_DIMENSION.height - iMessageDotHeight, ChatRepository.MESSAGE_DOT_DIMENSION.width, iMessageDotHeight);
    }

    g.drawImage(imgBuffer, 0, 0, this);

  }


/**
 * Invoked when the mouse has been dragged on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseDragged(MouseEvent event) {
    if (event.getX() > getBarLeft() && event.getX() < getBarRight())
      enterHistoryMode(new Date(getFromTime() + (getToTime() - getFromTime()) * (event.getX() - getBarLeft()) / (getBarRight() - getBarLeft())));
  }


/**
 * Invoked when the mouse has been moved on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseMoved(MouseEvent event) {
  }


/**
 * Invoked when the mouse has been released on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
  }


/**
 * Invoked when the mouse has entered a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseEntered(MouseEvent event) {
  }


/**
 * Invoked when the mouse has exited a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
  }


/**
 * Invoked when the mouse has been pressed on a component.
 *
 * @param event       the MouseEvent
 */

  public void mousePressed(MouseEvent event) {
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
  }


/**
 * Runs a thread that updates the value of the scrollbar for the history mode.
 */

  public void run() {
    while(true) {
      try {
        Thread.sleep(1000);
        if (!chatAdm.historyMode()) {
          setToDate(new Date());
        }
      }
      catch (Exception excpt) {
      }
    }
  }


/**
 * Clears the history panel.
 */

  public void clear() {
    iMaxMessageLength = 1;
    vecMessageEntry = new Vector();
    setFromDate(new Date());
  }

}