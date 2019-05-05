package at.ac.uni_linz.tk.vchat;

import java.awt.*;


/**
 * A FramedPanel is a labeled Panel with a surrounding frame and allows a
 * a graphical arrangement of Components.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class FramedPanel extends InsetsPanel {

  private String title;


/**
 * Constructs the FramedPanel.
 *
 * @param titleParam       the tiele of the Panel which will be displayed in the
 *                         upper right corner
 * @param insetsParam      the FramedPanel's Insets
 */

  public FramedPanel(String titleParam, Insets insetsParam) {
    super(insetsParam.top * 2, insetsParam.left * 2, insetsParam.bottom * 2, insetsParam.right * 2);
    title = titleParam;
  }


  public void setTitle(String titleParam) {
    title = titleParam;
    repaint();
  }


/**
 * Paints the FramedPanel.
 *
 * @param g       the graphics context
 */

  public void paint(Graphics g) {

    super.paint(g);

    setForeground(Color.black);
    g.drawLine(insets.left / 2, insets.top / 2, insets.left, insets.top / 2);
    g.drawLine(insets.left + getFontMetrics(getFont()).stringWidth(title) + 10, insets.top / 2, getSize().width - insets.right / 2, insets.top / 2);
    g.drawLine(insets.left / 2, insets.top / 2, insets.left / 2, getSize().height - insets.bottom / 2);
    g.drawLine(getSize().width - insets.right / 2, insets.top / 2, getSize().width - insets.right / 2, getSize().height - insets.bottom / 2);
    g.drawLine(insets.left / 2, getSize().height - insets.bottom / 2, getSize().width - insets.right / 2, getSize().height - insets.bottom / 2);
    g.drawString(title, insets.left + 5, getFontMetrics(getFont()).getHeight());
  }
}