package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;


/**
 * A FocusPanel graphically displays whether it has the focus or not.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class FocusPanel extends InsetsPanel  {

  private boolean hasFocus;


/**
 * Constructs the FocusPanel.
 */

  public FocusPanel() {
    this(new Insets(0, 0, 0, 0));
  }


/**
 * Constructs the FocusPanel.
 *
 * @param insets      the FocusPanel's Insets
 */

  public FocusPanel(Insets insets) {
    super(insets);
    hasFocus = false;
  }


/**
 * Paints the FocusPanel.
 *
 * @param g      the graphics context to use for painting
 */

  public void paint(Graphics g) {
    super.paint(g);
    if (hasFocus) {
      g.setColor(Color.black);
      for (int i = 2; i < getSize().width - 2; i += 2) {
        g.drawLine(i, 2, i , 2);
        g.drawLine(i, getSize().height - 2, i , getSize().height - 2);
      }
      for (int i = 2; i < getSize().height - 2; i+=2) {
        g.drawLine(2, i, 2, i);
        g.drawLine(getSize().width - 2, i, getSize().width - 2 , i);
      }
    }
  }


/**
 * Requests that the FocusPanel gets the focus.
 */

  public void requestFocus() {
    if (!hasFocus) {
      hasFocus = true;
      repaint();
    }
  }


/**
 * Transfers the focus to the next component.
 */

  public void transferFocus() {
    if (hasFocus) {
      hasFocus = false;
      repaint();
    }
  }


/**
 * Returns true if the FocusPanel has the focus.
 */

  public boolean hasFocus() {
    return hasFocus;
  }

}
