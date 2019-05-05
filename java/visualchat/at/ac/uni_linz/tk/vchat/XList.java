package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;


/************************************************************************
 * A XList offers a list with extended functionality, such as
 * changing columns' width dynamically, formating text, forcing
 * line breaks, etc.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 ************************************************************************/

public class XList extends Panel implements MouseListener, MouseMotionListener, KeyListener, AdjustmentListener {

  private static final int VERTICAL_CELL_SPACING = 2;
  private static final int HORIZONTAL_CELL_SPACING = 4;
  private static final int MINIMUM_COLUMNWIDTH = 30;
  private static final int MINIMUM_VISIBLELINES = 4;

  public static final int LEFT = 0;
  public static final int RIGHT = 1;
  public static final int FLOW = 2;

  private static final int SCROLLBAR_WIDTH = 16;

  private Vector vecContent[], vecKey, vecColor;
  private Scrollbar scbBar;
  private int iNrOfColumns, iSelectedRow, iNrOfLines, iFirstVisibleLine, iNrOfVisibleLines,  iColumnDraggedX, iColumnSeparator, iSortCriteria;
  private Font fntFont;
  private int iColumnWidth[], iColumnOrientation[], iNrOfLinesPerRow[], iNrOfLinesUntilRow[];
  private String strColumnHeader[];
  private boolean bSelectable, bSorted;

  private Image imgBuffer;

  private Vector vecRowSplitter = null;

 /************************************************************************
  * Creates a new XList.
  * @param iNrOfColumnsParam           the number of columns
  ************************************************************************/

  public XList(int iNrOfColumnsParam) {
    fntFont = ChatRepository.FIXED_FONT;
    iNrOfColumns = Math.max(iNrOfColumnsParam, 1);
    iNrOfVisibleLines = MINIMUM_VISIBLELINES;

    iFirstVisibleLine = 0;
    bSelectable = true;
    bSorted = false;
    iSortCriteria = 0;
    iSelectedRow = 0;

    vecContent = new Vector[iNrOfColumns];
    vecKey = new Vector();
    vecColor = new Vector();
    strColumnHeader = new String[iNrOfColumns];
    iColumnOrientation = new int[iNrOfColumns];
    iColumnWidth = new int[iNrOfColumns];
    for (int i = 0; i < iNrOfColumns; i++) {
      vecContent[i] = new Vector();
      strColumnHeader[i] = "";
      iColumnOrientation[i] = FLOW;
    }

    scbBar = new Scrollbar(Scrollbar.VERTICAL, 0, 0, 0, 0);
    setLayout(new BorderLayout());
    add("East", scbBar);

    addMouseListener(this);
    addMouseMotionListener(this);
    addKeyListener(this);
    scbBar.addAdjustmentListener(this);

    setSize(getPreferredSize());
  }


  /************************************************************************
   * Sets the header for each column.
   * @param strColumnHeaderParam      an array of Strings which contains
   *                                  the headers
   ************************************************************************/

  public void setColumnHeaders(String strColumnHeaderParam[]) {
    if (strColumnHeaderParam.length == iNrOfColumns) {
      strColumnHeader = strColumnHeaderParam;
    }
    repaint();
  }


  /************************************************************************
   * Sets the width for each column.
   * @param iColumnWidthParam      an array of integers which contains
   *                               the widths
   ************************************************************************/

  public void setColumnWidths(int iColumnWidthParam[]) {
    if (iColumnWidthParam.length == iNrOfColumns) {
      iColumnWidth = iColumnWidthParam;
    }
    setSize(getPreferredSize());
    invalidateWrapping();
  }


  /************************************************************************
   * Sets the orientation for each column.
   * @param iColumnOrientationParam      an array of integers which
   *                                     contains the orientations (must be
   *                                     XList.LEFT, XList.RIGHT or
   *                                     XList.FLOW)
   ************************************************************************/

  public void setColumnOrientations(int iColumnOrientationParam[]) {
    if (iColumnOrientationParam.length == iNrOfColumns) {
      iColumnOrientation = iColumnOrientationParam;
    }
    invalidateWrapping();
    repaint();
  }


  /************************************************************************
   * Clears the XList.
   ************************************************************************/

  public synchronized void clear() {
    for (int i = 0; i < iNrOfColumns; i++) {
      vecContent[i] = new Vector();
    }
    vecKey = new Vector();
    vecColor = new Vector();
    iNrOfLines = 0;
    iFirstVisibleLine = 0;
    iSelectedRow = 0;
    invalidateWrapping();
    repaint();
  }


  /************************************************************************
   * Sets the text for a certain cell.
   * @param iRowParam            the index of the cell's row
   * @param iColumnParam         the index of the cell's column
   * @param strContentParam      the new cell text
   ************************************************************************/

  public void setCellText(int iRowParam, int iColumnParam, String strContentParam) {
    if (iRowParam < getNrOfRows() && iColumnParam < iNrOfColumns) {
      vecContent[iColumnParam].removeElementAt(iRowParam);
      vecContent[iColumnParam].insertElementAt(strContentParam, iRowParam);
      invalidateWrapping();
      repaint();
    }
  }


  /************************************************************************
   * Returns the text of a certain cell.
   * @param iRowParam            the index of the cell's row
   * @param iColumnParam         the index of the cell's column
   ************************************************************************/

  public String getCellText(int iRowParam, int iColumnParam) {
    if (iRowParam < getNrOfRows() && iColumnParam < iNrOfColumns) {
      return (String)vecContent[iColumnParam].elementAt(iRowParam);
    }
    else {
      return "";
    }
  }


  /************************************************************************
   * Sets the text Color for a certain row.
   * @param iRowParam      the index of the cell's row
   * @param colParam       the Color to be used
   ************************************************************************/

  public void setRowColor(int iRowParam, Color colParam) {
    if (iRowParam < getNrOfRows()) {
      vecColor.setElementAt(colParam, iRowParam);
      repaint();
    }
  }


  /************************************************************************
   * Adds a row at the bottom of the XList.
   * @param strRowParam      an array of Strings which contains each cell's
   *                         text
   ************************************************************************/

  public synchronized void addRow(String strRowParam[]) {
    addRow(strRowParam, -1, Color.black);
  }


  /************************************************************************
   * Adds a row at the bottom of the XList.
   * @param strRowParam      an array of Strings which contains each cell's
   *                         text
   * @param iKeyParam        an int value working as an index key for the
   *                         row
   * @param colParam         the text Color of the row
   ************************************************************************/

  public synchronized void addRow(String strRowParam[], int iKeyParam, Color colParam) {
    if (strRowParam.length == iNrOfColumns) {
      if (bSorted) {
        addSortedRow(strRowParam, iKeyParam, colParam);
      }
      else {
        for (int i = 0; i < iNrOfColumns; i++) {
          vecContent[i].addElement(strRowParam[i]);
        }
        vecKey.addElement(new Integer(iKeyParam));
        vecColor.addElement(colParam);
      }
      invalidateWrapping();
      repaint();
    }
  }


  /************************************************************************
   * Adds a row at its sorted index.
   * @param strRowParam      an array of Strings which contains each cell's
   *                         text
   * @param iKeyParam        an int value working as an index key for the
   *                         row
   * @param colParam         the text Color of the row
   ************************************************************************/

  private synchronized void addSortedRow(String strRowParam[], int iKeyParam, Color colParam) {
    int iIndex;
    iIndex = getSortedRowIndex(strRowParam[iSortCriteria]);

    for (int i = 0; i < iNrOfColumns; i++) {
      vecContent[i].insertElementAt(strRowParam[i], iIndex);
    }
    vecKey.insertElementAt(new Integer(iKeyParam), iIndex);
    vecColor.insertElementAt(colParam, iIndex);
    invalidateWrapping();
    repaint();
  }


  /************************************************************************
   * Returns the row-index of a certain text depending on the XList's
   * sorting criteria.
   * @param strParam      the text to be indexed
   ************************************************************************/

  private synchronized int getSortedRowIndex(String strParam) {
    int rows = getNrOfRows();
    Vector contVec = vecContent[iSortCriteria];
    Collator collator = Collator.getInstance();
    if (rows == 0) {
        return 0;
    }
    else if (collator.compare(strParam, (String)contVec.elementAt(0)) <= 0) {
        return 0;
    }
    else if (collator.compare(strParam, (String)contVec.elementAt(rows - 1)) >= 0) {
        return rows;
    }
    else {
        int i = 0;
        int left = 0;
        int right = rows - 1;
        while (true) {
            i = (left + right) / 2 + 1;
            if (collator.compare(strParam, (String)contVec.elementAt(i - 1)) < 0) {
                right = i - 1;
            }
            else if (collator.compare(strParam, (String)contVec.elementAt(i)) > 0) {
                left = i;
            }
            else {
                return i;
            }
        }
    }
  }


  /************************************************************************
   * Returns the key value of a certain row.
   * @param iRowParam      the index of the row
   ************************************************************************/

  public int getKey(int iRowParam) {
    return ((Integer)vecKey.elementAt(iRowParam)).intValue();
  }


  /************************************************************************
   * Returns the number of rows.
   ************************************************************************/

  public int getNrOfRows() {
    return vecContent[0].size();
  }


  /************************************************************************
   * Returns the index of the currently selected row. Will be -1 in case
   * the XList is marked as being unselectable.
   ************************************************************************/

  public int getSelectedRow() {
    return iSelectedRow;
  }


  /************************************************************************
   * Determines wheter items of the BXList can be selected or not.
   *
   * @param bSelectableParam      true if items should be selectable, false
   *                              if not
   ************************************************************************/

  public void setSelectable(boolean bSelectableParam) {
    bSelectable = bSelectableParam;
    repaint();
  }


  /************************************************************************
   * Determines wheter the XList should be sorted or not.
   *
   * @param bSortedParam      true if the XList should be sorted, false if
   *                          not
   ************************************************************************/

  public void setSorted(boolean bSortedParam) {
    bSorted = bSortedParam;
  }


  /************************************************************************
   * Returns the BXList's minimum size.
   ************************************************************************/

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }


  /************************************************************************
   * Resizes the XList to a certain Dimension.
   * @param dim      the XList's new Dimension
   ************************************************************************/

  public void setSize(Dimension dim) {
    super.setSize(dim);
    iNrOfVisibleLines = dim.height / getLineHeight();
    repaint();
  }


  /************************************************************************
   * Resizes the XList to a certain Dimension.
   * @param width       the XList's new width
   * @param height      the XList's new height
   ************************************************************************/

  public void setSize(int width, int height) {
    super.setSize(width, height);
    iNrOfVisibleLines = height / getLineHeight();
    repaint();
  }


  /************************************************************************
   * Reshapes the XList to a certain Dimension.
   * @param x           the XList's new x-ccordinate
   * @param y           the XList's new y-coordinate
   * @param width       the XList's new width
   * @param height      the XList's new height
   ************************************************************************/

  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    iNrOfVisibleLines = height / getLineHeight();
    repaint();
  }


  /************************************************************************
   * Returns the current width of the XList.
   ************************************************************************/

  public Dimension getPreferredSize() {
    int iWidth;
    iWidth = 0;
    for (int i = 0; i < iNrOfColumns; i++) {
      iWidth += iColumnWidth[i];
    }
    return new Dimension(iWidth + SCROLLBAR_WIDTH, getLineHeight() * (iNrOfVisibleLines + 1));
  }


  /************************************************************************
   * Returns the height required to display one line.
   ************************************************************************/

  private int getLineHeight() {
    return getFontMetrics(fntFont).getAscent() + getFontMetrics(fntFont).getDescent() + VERTICAL_CELL_SPACING * 2;
  }



  /************************************************************************
   * Returns the starting Point for displaying right-orientated text.
   * @param iColumnIndex      the column index of the text
   * @param iLineIndex        the line index of the text
   * @param strText           the text
   ************************************************************************/

 private Point getRightTextPosition(int iColumnIndex, int iLineIndex, String strText) {
    Point pntPosition;
    pntPosition = getLeftTextPosition(iColumnIndex, iLineIndex);

    return new Point(pntPosition.x + (iColumnWidth[iColumnIndex] - HORIZONTAL_CELL_SPACING * 2 - getFontMetrics(fntFont).stringWidth(strText)), pntPosition.y);
  }


  /************************************************************************
   * Returns the starting Point for displaying left-orientated text.
   * @param iColumnIndex      the column index of the text
   * @param iLineIndex        the line index of the text
   ************************************************************************/

 private Point getLeftTextPosition(int iColumnIndex, int iLineIndex) {
    int iX;
    iX = 0;

    for (int i = 0; i < iColumnIndex; i++) {
      iX += iColumnWidth[i];
    }
    return new Point(iX + HORIZONTAL_CELL_SPACING, getLineHeight() * (iLineIndex - iFirstVisibleLine + 2) - getFontMetrics(fntFont).getDescent() - VERTICAL_CELL_SPACING);
  }


  /************************************************************************
   * Returns the clipping rectangle of a line.
   * @param iLineIndex        the line index
   ************************************************************************/

  private Rectangle getLineRectangle(int iLineIndex) {
    int iX;
    iX = 0;

    for (int i = 0; i < iNrOfColumns; i++) {
      iX += iColumnWidth[i];
    }
    return new Rectangle(1, getLineHeight() * (iLineIndex - iFirstVisibleLine + 1), iX, getLineHeight());
  }


  /************************************************************************
   * Splits the text of a row into lines.
   * @param iRowParam        the row index
   ************************************************************************/

  private synchronized String[][] getRowSplitter(int iRowParam) {
    int iMaxNrOfLines;
    String strRowSplitter[][];
    String strSingleCell[];
    Vector vecColumnSplitter[];

    strRowSplitter = new String[iNrOfColumns][];
    vecColumnSplitter = new Vector[iNrOfColumns];
    iMaxNrOfLines = 0;
    for (int i = 0; i < iNrOfColumns; i++) {
      if (iColumnOrientation[i] == FLOW) {
        vecColumnSplitter[i] = getCellTextSplitter(i, iRowParam);
      }
      else {
        vecColumnSplitter[i] = new Vector();
        vecColumnSplitter[i].addElement(getFormatedText(iRowParam < vecContent[i].size() ? (String)vecContent[i].elementAt(iRowParam) : "", i, iColumnOrientation[i]));
      }
      iMaxNrOfLines = Math.max(iMaxNrOfLines, vecColumnSplitter[i].size());
    }
    for (int i = 0; i < iNrOfColumns; i++) {
      strRowSplitter[i] = new String[iMaxNrOfLines];
      for (int j = 0; j < iMaxNrOfLines; j++) {
        if (j < vecColumnSplitter[i].size()) {
          strRowSplitter[i][j] = (String)vecColumnSplitter[i].elementAt(j);
        }
        else {
          strRowSplitter[i][j] = "";
        }
      }
    }
    return strRowSplitter;
  }


  /************************************************************************
   * Wraps a text so it will fit into its column.
   * @param strTextParam         the text to be wrapped
   * @param iAvailableWidth      the available column width
   * @return                     the wrapped text
   ************************************************************************/
  private synchronized String getWrappedText(String strTextParam, int iAvailableWidth) {
    StringTokenizer stkText;
    String strText, strWord, strBrokenWord;
    FontMetrics fnmMetrics;
    int iIndex;
    StringBuffer buf = new StringBuffer(strTextParam.length() * 2); // that should be more than enough
    if ((iIndex = strTextParam.indexOf("\n")) != -1) {
      buf.append(getWrappedText(strTextParam.substring(0, iIndex), iAvailableWidth));
      buf.append("\n");
      buf.append(getWrappedText(strTextParam.substring(iIndex + 1), iAvailableWidth));
    }
    else {
      stkText = new StringTokenizer(strTextParam, " ");
      fnmMetrics = getFontMetrics(fntFont);

      while (stkText.hasMoreTokens()) {
        strWord = stkText.nextToken();
        if (fnmMetrics.stringWidth(strWord) < iAvailableWidth) {
            buf.append(strWord);
            buf.append(" ");
        }
        else {
          StringBuffer bufBrokenWord = new StringBuffer(strWord.length());
          int width = 0;
          for (int i = 0; (i < strWord.length()) && width + fnmMetrics.charWidth(strWord.charAt(i)) < iAvailableWidth; i++) {
            width += fnmMetrics.charWidth(strWord.charAt(i));
            bufBrokenWord.append(strWord.charAt(i));
          }
          buf.append(bufBrokenWord);
          buf.append(" ");
          buf.append(getWrappedText(strWord.substring(bufBrokenWord.length()), iAvailableWidth));
          buf.append(" ");
        }
      }
    }
    return buf.toString();
  }

  /************************************************************************
   * Formats a text depending on its orientation.
   * @param strTextParam           the text to be wrapped
   * @param iColumnParam           the index of the column
   * @param iOrientationParam      the orientation
   * @return                       the formated text
   ************************************************************************/
  private synchronized String getFormatedText(String strTextParam, int iColumnParam, int iOrientationParam) {
    FontMetrics fnmMetrics;
    int iAvailableWidth;

    fnmMetrics = getFontMetrics(fntFont);
    iAvailableWidth = iColumnWidth[iColumnParam] - HORIZONTAL_CELL_SPACING * 2;

    StringBuffer buf = new StringBuffer(strTextParam.length() + 2);
    int pointWidth = fnmMetrics.stringWidth("...");

    if (iOrientationParam == LEFT || iOrientationParam == RIGHT) {
      if (fnmMetrics.stringWidth(strTextParam) <= iAvailableWidth) {
        return strTextParam;
      }
      else {
        int width = 0;
        if (iOrientationParam == RIGHT) {
          for (int i = strTextParam.length() - 1; i >= 0 && width + pointWidth + fnmMetrics.charWidth(strTextParam.charAt(i)) < iAvailableWidth; i--) {
            width += fnmMetrics.charWidth(strTextParam.charAt(i));
            buf.insert(0, strTextParam.charAt(i));
          }
          buf.insert(0, "...");
        }
        else {
          for (int i = 0; i < strTextParam.length() && width + pointWidth + fnmMetrics.charWidth(strTextParam.charAt(i)) < iAvailableWidth; i++) {
            width += fnmMetrics.charWidth(strTextParam.charAt(i));
            buf.append(strTextParam.charAt(i));
          }
          buf.append("...");
        }
      }
    }
    return buf.toString();
  }


  /************************************************************************
   * Splits a cell's text into several lines.
   * @param iColumnParam           the index of the column
   * @param iRowParam              the index of the row
   * @return                       the splitted text
   ************************************************************************/
  private synchronized Vector getCellTextSplitter(int iColumnParam, int iRowParam) {
    StringTokenizer stkCellText;
    Vector vecCellTextSplitter;
    String strLine, strWord;
    int iAvailableWidth, iRequiredWidth, iIndex;
    FontMetrics fnmMetrics;

    vecCellTextSplitter = new Vector();
    fnmMetrics = getFontMetrics(fntFont);
    iAvailableWidth = iColumnWidth[iColumnParam] - HORIZONTAL_CELL_SPACING * 2;

    stkCellText = new StringTokenizer(getWrappedText((String)vecContent[iColumnParam].elementAt(iRowParam), iAvailableWidth), " ");

    strLine = "";
    while (stkCellText.hasMoreTokens()) {
      strWord = stkCellText.nextToken();
      String strLineStarter = getLineStarter(strLine);
      if ((iIndex = strWord.indexOf("\n")) == -1) {
        iRequiredWidth = fnmMetrics.stringWidth(strLineStarter) + fnmMetrics.stringWidth(strWord);
        if (iRequiredWidth < iAvailableWidth ) {
          strLine = new StringBuffer(strLineStarter.length() + strWord.length()).append(strLineStarter).append(strWord).toString();
        }
        else if (iRequiredWidth >= iAvailableWidth) {
          vecCellTextSplitter.addElement(strLine);
          strLine = strWord;
        }
      }
      else {
        String strWordPart = strWord.substring(0, iIndex);
        iRequiredWidth = fnmMetrics.stringWidth(strLineStarter) +  fnmMetrics.stringWidth(strWordPart);
        if (iRequiredWidth < iAvailableWidth) {
          vecCellTextSplitter.addElement(new StringBuffer(strLineStarter.length() + strWordPart.length()).append(strLineStarter).append(strWordPart).toString());
          strLine = strWord.substring(iIndex + 1);
        }
        else {
          vecCellTextSplitter.addElement(strLine);
          if (iIndex != 0) {
            vecCellTextSplitter.addElement(strWordPart);
          }
          strLine = strWord.substring(iIndex + 1);
        }
      }
    }
    vecCellTextSplitter.addElement(strLine);
    return vecCellTextSplitter;
  }


  /************************************************************************
   * Returns the beginning of a new line depending of its first word.
   * @param strLineParam      the first word
   * @return                  the beginning of a new line
   ************************************************************************/
  private String getLineStarter(String strLineParam) {
    if (strLineParam.equals("")) {
        return "";
    }
    else {
        StringBuffer buf = new StringBuffer(strLineParam.length() + 1);
        buf.append(strLineParam);
        buf.append(" ");
        return buf.toString();
    }
  }


  /************************************************************************
   * Updates the scrollbar regarding to the number of lines and the lines
   * currently visible.
   ************************************************************************/

  private void updateScrollbar() {
    scbBar.setValues(iFirstVisibleLine, iNrOfVisibleLines, 0, (iNrOfLines + iNrOfVisibleLines - 1));
  }


  /************************************************************************
   * Updates the XList. No clearing needed as we use double-buffering.
   * @param g      the graphical context
   ************************************************************************/

  public void update(Graphics g) {
    paint(g);
  }


  public synchronized void invalidateWrapping() {
    vecRowSplitter = null;
  }

  /************************************************************************
   * Paints the XList.
   * @param g      the graphical context
   ************************************************************************/
  public synchronized void paint(Graphics g) {
    int iX, iY, iWidth, iHeight, iNrOfRows, iLineHeight, iNrOfLinesSoFar;
    String strHeader;
    Rectangle recLine;
    Point pntPosition;
    Graphics graBuffer;

    iWidth = getSize().width;
    iHeight = getSize().height;

    if (imgBuffer == null || imgBuffer.getWidth(this) != iWidth || imgBuffer.getHeight(this) != iHeight) {
      imgBuffer = createImage(iWidth, iHeight);
    }

    graBuffer = imgBuffer.getGraphics();
    graBuffer.setColor(Color.white);
    graBuffer.fillRect(0, 0, iWidth - 1, iHeight - 1);

    iNrOfRows = getNrOfRows();
    iLineHeight = getLineHeight();

    graBuffer.setFont(fntFont);

    iNrOfLinesPerRow = new int[iNrOfRows];
    iNrOfLinesUntilRow = new int[iNrOfRows];
    iNrOfLinesSoFar = 0;

    boolean needsWrapping = vecRowSplitter == null;
    if (needsWrapping) {
      vecRowSplitter = new Vector();
    }
    for (int i = 0; i < iNrOfRows; i++) {
      String[][] strRowSplitter;
      if (needsWrapping) {
        // wrapping necessary
        strRowSplitter = getRowSplitter(i);
        vecRowSplitter.addElement(strRowSplitter);
      }
      else {
        strRowSplitter = (String[][])vecRowSplitter.elementAt(i);
      }
      iNrOfLinesPerRow[i] = strRowSplitter[0].length;
      iNrOfLinesUntilRow[i] = iNrOfLinesSoFar;
      for (int j = 0; j < iNrOfLinesPerRow[i]; j++) {
        if (iNrOfLinesSoFar >= iFirstVisibleLine && iNrOfLinesSoFar <= iFirstVisibleLine + iNrOfVisibleLines) {
          if (i == iSelectedRow && bSelectable) {
            recLine = getLineRectangle(iNrOfLinesSoFar);
            graBuffer.setColor(Color.gray);
            graBuffer.fillRect(recLine.x, recLine.y, recLine.width, recLine.height);
            graBuffer.setColor(Color.white);
          }
          else if (vecColor.size() > i) {
            graBuffer.setColor((Color)vecColor.elementAt(i));
          }
          for (int k = 0; k < iNrOfColumns; k++) {
            if (iColumnOrientation[k] == RIGHT) {
              pntPosition = getRightTextPosition(k, iNrOfLinesSoFar, strRowSplitter[k][j]);
            }
            else {
              pntPosition = getLeftTextPosition(k, iNrOfLinesSoFar);
            }
            graBuffer.drawString(strRowSplitter[k][j], pntPosition.x, pntPosition.y);
          }
        }
        iNrOfLinesSoFar++;
      }
    }
    if (iNrOfRows > 0) {
      iNrOfLines = iNrOfLinesUntilRow[iNrOfRows - 1] + iNrOfLinesPerRow[iNrOfRows - 1];
    }

    graBuffer.setColor(Color.lightGray);
    graBuffer.fillRect(1, 0, iWidth - 1, iLineHeight - 1);

    iX = 1;

    for (int i = 0; i < iNrOfColumns; i++) {
      graBuffer.setColor(Color.white);
      graBuffer.drawLine(iX, 1, iX + iColumnWidth[i] - 2, 1);
      graBuffer.drawLine(iX, 1, iX, iLineHeight - 1);

      graBuffer.setColor(Color.darkGray);
      graBuffer.drawLine(iX, iLineHeight - 1, iX + iColumnWidth[i] - 2, iLineHeight - 1);
      graBuffer.drawLine(iX + iColumnWidth[i] - 1, 1, iX + iColumnWidth[i] - 1, iLineHeight - 1);

      graBuffer.setColor(Color.black);
      graBuffer.drawLine(iX + iColumnWidth[i] - 1, 1, iX + iColumnWidth[i] - 1, iHeight);
      iX += iColumnWidth[i];

      strHeader = getFormatedText(strColumnHeader[i], i, iColumnOrientation[i] == RIGHT ? RIGHT : LEFT);
      if (iColumnOrientation[i] == RIGHT) {
        pntPosition = getRightTextPosition(i, iFirstVisibleLine - 1, strHeader);
      }
      else {
        pntPosition = getLeftTextPosition(i, iFirstVisibleLine - 1);
      }
      graBuffer.drawString(strHeader, pntPosition.x, pntPosition.y);
    }

    graBuffer.setColor(Color.black);
    graBuffer.drawRect(0, 0, iWidth - 1, iHeight - 1);

    g.drawImage(imgBuffer, 0, 0, this);
    updateScrollbar();

  }


/**
 * Invoked when the mouse has been dragged on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseDragged(MouseEvent event) {
    int iDeltaX;
    if (getCursor().getType() == Cursor.E_RESIZE_CURSOR) {
      iDeltaX = event.getX() - iColumnDraggedX;
      if (iColumnWidth[iColumnSeparator] + iDeltaX >= MINIMUM_COLUMNWIDTH && iColumnWidth[iColumnSeparator + 1] - iDeltaX >= MINIMUM_COLUMNWIDTH) {
        iColumnWidth[iColumnSeparator] += iDeltaX;
        iColumnWidth[iColumnSeparator + 1] -= iDeltaX;
        iColumnDraggedX = event.getX();
        invalidateWrapping();
        repaint();
        if (iFirstVisibleLine > iNrOfLines - 1) {
          scrollTo(iNrOfLines - 1);
        }
      }
    }
  }


/**
 * Invoked when the mouse has been moved on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseMoved(MouseEvent event) {
    if (getCursor() != null && getCursor().getType() == Cursor.E_RESIZE_CURSOR && getColumnSeparator(event.getX()) == -1) {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    else if (getCursor() != null && getCursor().getType() == Cursor.DEFAULT_CURSOR && (iColumnSeparator = getColumnSeparator((iColumnDraggedX = event.getX()))) != -1) {
      setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
    }
  }


/**
 * Invoked when a key has been pressed on a component.
 *
 * @param event      the KeyEvent
 */

  public void keyPressed(KeyEvent event) {
    if (bSelectable) {
      if (event.getKeyCode() == KeyEvent.VK_DOWN && iSelectedRow < getNrOfRows() - 1) {
        iSelectedRow++;
        if (!rowIsVisible(iSelectedRow)) {
          scrollTo(iNrOfLinesUntilRow[iSelectedRow] + iNrOfLinesPerRow[iSelectedRow] - iNrOfVisibleLines);
        }
        else {
          repaint();
        }
      }
      else if (event.getKeyCode() == KeyEvent.VK_UP && iSelectedRow > 0) {
        iSelectedRow--;
        if (!rowIsVisible(iSelectedRow)) {
          scrollTo(iNrOfLinesUntilRow[iSelectedRow]);
        }
        else {
          repaint();
        }
      }
    }
  }


/**
 * Invoked when a key has been released on a component.
 *
 * @param event      the KeyEvent
 */

  public void keyReleased(KeyEvent event) {
  }


/**
 * Invoked when a key has been typed on a component.
 *
 * @param event      the KeyEvent
 */

  public void keyTyped(KeyEvent event) {
  }


/**
 * Invoked when the mouse has been released on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
    int iRow;
    if (bSelectable) {
      iRow = getRowAtPixel(event.getY());
      if (iRow != -1 && iRow != iSelectedRow) {
        iSelectedRow = getRowAtPixel(event.getY());
        repaint();
      }
    }
  }


/**
 * Invoked when the mouse has entered a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseEntered(MouseEvent event) {
  }


/**
 * Invoked when the mouse has exited a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
  }


/**
 * Invoked when the mouse has been pressed on a component.
 *
 * @param event      the MouseEvent
 */

  public void mousePressed(MouseEvent event) {
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event      the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
  }


/**
 * Invoked when the value of the adjustable has changed.
 *
 * @param event      the AdjustmentEvent
 */

  public void adjustmentValueChanged(AdjustmentEvent event) {
    scrollTo(event.getValue());
  }


  /************************************************************************
   * Scrolls to a certain line.
   * @param iLineParam      the line's index
   ************************************************************************/

  private void scrollTo(int iLineParam) {
    if (iLineParam >= 0 && iLineParam < iNrOfLines && iLineParam != iFirstVisibleLine) {
      iFirstVisibleLine = iLineParam;
      repaint();
    }
  }


  /************************************************************************
   * Determines if a row is visible or not.
   * @param iRowParam      the row's index
   ************************************************************************/

  private boolean rowIsVisible(int iRowParam) {
    return iRowParam >= getRowOfLine(iFirstVisibleLine) && (iNrOfLines < iNrOfVisibleLines || iRowParam <= getRowOfLine(iFirstVisibleLine + iNrOfVisibleLines - 1));
  }


  /************************************************************************
   * Returns the row's index a line is belonging to.
   * @param iLineParam      the line's index
   ************************************************************************/

  private int getRowOfLine(int iLineParam) {
    int iLineCounter;
    iLineCounter = 0;
    for (int i = 0; i < getNrOfRows(); i++) {
      iLineCounter += iNrOfLinesPerRow[i];
      if (iLineCounter > iLineParam) {
        return i;
      }
    }
    return -1;
  }


  /************************************************************************
   * Returns the row's index at a certain pixel.
   * @param iYParam      the pixel's y-coordinate
   ************************************************************************/

  private int getRowAtPixel(int iYParam) {
    return (iYParam > getLineHeight() ? getRowOfLine(iFirstVisibleLine + iYParam / getLineHeight() - 1) : -1);
  }


  /************************************************************************
   * Returns the index of the column-separator at a certain pixel.
   * @param iXParam      the pixel's x-coordinate
   ************************************************************************/

  public int getColumnSeparator(int iXParam) {
    int iX;
    iX = 0;
    for (int i = 0; i < iNrOfColumns - 1; i++) {
      iX += iColumnWidth[i];
      if (iX - HORIZONTAL_CELL_SPACING <= iXParam && iXParam <= iX + HORIZONTAL_CELL_SPACING) {
        return i;
      }
    }
    return -1;
  }


}