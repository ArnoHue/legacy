package at.ac.uni_linz.tk.vchat;

import java.awt.*;


/**
 * Extends a normVec Panel's functionality with methods for defining margins.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class InsetsPanel extends Panel {

  Insets insets;

  public InsetsPanel() {
    this(0, 0, 0, 0);
  }


/**
 * Constructs the InsetsPanel.
 *
 * @param top         the inset from the top
 * @param left        the inset from the left
 * @param bottom      the inset from the bottom
 * @param right       the inset from the right
 */

  public InsetsPanel(int top, int left, int bottom, int right) {
    super();
    insets = new Insets(top, left, bottom, right);
  }


/**
 * Constructs the InsetsPanel.
 *
 * @param insetsParam      the Insets
 */

  public InsetsPanel(Insets insetsParam) {
    super();
    insets = insetsParam;
  }


/**
 * Determines the Insets, which indicate the size of the Container's border.
 */

  public Insets getInsets() {
    return insets;
  }

}