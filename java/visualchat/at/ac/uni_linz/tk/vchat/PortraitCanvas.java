package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;


/**
 * Used to display Images centered within an area, surrounded by a colored frame
 * if required.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class PortraitCanvas extends Canvas {

  Image image;
  double portraitScale;
  boolean drawFrame;
  Color frameColor;


/**
 * Constructs the PortraitCanvas.
 *
 * @param portraitImage      the Image to be displayed in the center
 */

  public PortraitCanvas(Image portraitImage) {
    super();
    portraitScale = 1.0;
    image = portraitImage;
    drawFrame = false;
    frameColor = Color.black;
  }


/**
 * Constructs the PortraitCanvas.
 */

  public PortraitCanvas() {
    this(null);
  }


/**
 * Sets the Image to be displayed.
 *
 * @param portraitImage      the Image to be displayed
 */

  public void setImage(Image portraitImage) {
    image = portraitImage;
    repaint();
  }

/**
 * Sets the scaling of the Image.
 *
 * @param scaleParam      the scaling of the Image
 */

  public void setScale(double scaleParam) {
    portraitScale = scaleParam;
  }


/**
 * Determines wheter there should be a drawn a frame around the Image.
 *
 * @param drawFrameParam      true if there should be a frame, false if not
 */

  public void setDrawFrame(boolean drawFrameParam) {
    drawFrame = drawFrameParam;
    repaint();
  }


/**
 * Sets the Color of the frame to be drawn around the Image.
 *
 * @param frameColorParam      the Color of the Frame
 */

  public void setFrameColor(Color frameColorParam) {
    frameColor = frameColorParam;
    repaint();
  }


/**
 * Paints the PortraitCanvas.
 *
 * @param g       the graphics context
 */

  public void paint(Graphics g) {
    Rectangle imageRectangle;

    if (image != null) {
      imageRectangle = getImageRectangle();
      g.drawImage(image, imageRectangle.x, imageRectangle.y, imageRectangle.width, imageRectangle.height, this);

      if (drawFrame) {
        g.setColor(frameColor);
        g.drawRect(imageRectangle.x, imageRectangle.y, imageRectangle.width - 1, imageRectangle.height - 1);
        g.drawRect(imageRectangle.x + 1, imageRectangle.y + 1, imageRectangle.width - 3, imageRectangle.height - 3);
      }

    }
  }


/**
 * Returns the Rectangle surrounding the Image.
 */

  public Rectangle getImageRectangle() {
    int canvasWidth, canvasHeight;
    Rectangle imageRectangle;
    double scale;

    canvasWidth = getSize().width;
    canvasHeight = getSize().height;

    if (image != null) {
      scale = ((float)canvasWidth / (float)getSize().height) / ((float)image.getWidth(this) / (float)image.getHeight(this));
      imageRectangle = new Rectangle();

      if (scale > 1) {
        imageRectangle.x = (int)(canvasWidth * (1.0 - portraitScale / scale) / 2.0);
        imageRectangle.y = (int)(canvasHeight * (1.0 - portraitScale) / 2.0);
        imageRectangle.width = (int)(canvasWidth * portraitScale / scale);
        imageRectangle.height = (int)(canvasHeight * portraitScale);
      }
      else {
        imageRectangle.x = (int)(canvasWidth * (1.0 - portraitScale) / 2.0);
        imageRectangle.y = (int)(canvasHeight * (1.0 - portraitScale * scale) / 2.0);
        imageRectangle.width = (int)(canvasWidth * portraitScale);
        imageRectangle.height = (int)(canvasHeight * portraitScale * scale);
      }
      return imageRectangle;
    }
    else
      return new Rectangle();
  }

}