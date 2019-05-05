package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.zip.*;
import java.util.*;


/**
 * Provides the possibility to serialize Images, so that they can be transmitted
 * over a network or stored to a physical device.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class SerializableImage implements Serializable, ImageObserver, Cloneable {

  static final long serialVersionUID = -2975749503113144495L;

  private transient Image image;
  private transient byte[] buffer;


/**
 * Constructs the SerializableImage
 *
 * @param imageParam       the image to be serialized
 */

  public SerializableImage(byte[] _buffer) {
    buffer = _buffer;
    image = Toolkit.getDefaultToolkit().createImage(buffer);
  }

  public SerializableImage(Image _image) {
    buffer = null;
    image = _image;
  }

/**
 * Writes the image to an ObjectOutputStream. Required to implement the
 * Serializable-Interface. The data is transmitted compressed using a
 * ZipOutputStream.
 *
 * @param out       the ObjectOutputStream to write to
 */

  private void writeObject(ObjectOutputStream out) throws IOException {
    try {

      if (buffer == null) {
        // convert old format to gif
        MediaTracker tracker = new MediaTracker(new Label());
        tracker.addImage(image, 0);
        tracker.waitForAll();

        try {
            ByteArrayOutputStream myOut = new ByteArrayOutputStream();
            new GifEncoder(image).write(myOut);
            buffer = myOut.toByteArray();
        }
        catch (Exception ex) {
            // convert to 256 colors
            int width = image.getWidth(this);

            int height = image.getHeight(this);
            int[] tmpBuffer = new int[width * height];

            PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, tmpBuffer, 0, width);
            try {
              grabber.grabPixels();
            }
            catch (InterruptedException excpt) {
                excpt.printStackTrace();
            }


            byte[] red = new byte[256];
            byte[] green = new byte[256];
            byte[] blue = new byte[256];

            int idx = 0;
            for (int r = 0; r <= 255; r += 51) {
                for (int g = 0; g <= 255; g += 51) {
                    for (int b = 0; b <= 255; b += 51) {
                        red[idx] = (byte)r;
                        green[idx] = (byte)g;
                        blue[idx] = (byte)b;
                        idx++;
                    }
                }
            }

            int transIdx = idx;

            IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue, transIdx);

            buffer = new byte[width * height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgba = tmpBuffer[x + y * width];
                    int a = (rgba >> 24) & 0xff;
                    int r = (rgba >> 16) & 0xff;
                    int g = (rgba >> 8) & 0xff;
                    int b = (rgba) & 0xff;
                    byte val = (byte)(r / 51 * 36 + g / 51 * 6 + b / 51);
                    buffer[x + y * width] = a == 0x00 ? (byte)transIdx : val;
                }
            }


            Image tmpImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, cm, buffer, 0, width));

            ByteArrayOutputStream myOut2 = new ByteArrayOutputStream();
            new GifEncoder(tmpImg).write(myOut2);
            buffer = myOut2.toByteArray();
            image = tmpImg;
          }
      }

      out.writeInt(-1);
      out.writeObject(buffer);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new IOException(ex.getMessage());
    }
  }


/**
 * Reads the image from an ObjectInputStream. Required to implement the
 * Serializable-Interface. The data is transmitted compressed using a
 * ZipInputStream.
 *
 * @param in       the ObjectInputStream to read from
 */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int header = in.readInt();
    if (header == -1) {
    buffer = (byte[])in.readObject();
      image = Toolkit.getDefaultToolkit().createImage(buffer);
    }
    else {
       // old format
      InflaterInputStream infInput;

      int width = header;
      int height = in.readInt();

      infInput = new InflaterInputStream(in, new Inflater(false));
      int[] myBuffer = (int[])new ObjectInputStream(infInput).readObject();

      image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, ColorModel.getRGBdefault(), myBuffer, 0, width));
    }
  }


/**
 * Returns the image as an java.awt.image.
 * @return      the image
 */

  public Image getImage() {
    return image;
  }


/**
 * Called when information about an image which was previously requested using
 * an asynchronous interface becomes available. Required to implement the
 * ImageObserver-Interface. Returns true, if further updates are needed, resp. false
 * if the required information has been acquired
 *
 * @param img         the Image to be tracked
 * @param x           the upper x-coordinate of the image
 * @param y           the upper y-coordinate of the image
 * @param width       the width of the image
 * @param height      the height of the image
 */

  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    return ((infoflags & ALLBITS) == 0);
  }

}

