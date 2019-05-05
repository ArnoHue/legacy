package at.ac.uni_linz.tk.webstyles.util;

import java.awt.geom.*;
import java.io.*;

public class Ellipse extends Ellipse2D.Double implements Serializable {

    public Ellipse(double x, double y, double w, double h)  {
        super(x, y, w, h);
    }

    public void translate(double x, double y) {
        setFrame(Math.max(0, getX() + x), Math.max(0, getY() + y), getWidth(), getHeight());
    }

    public void zoom(double scale) {
        setFrame(getX() * scale, getY() * scale, getWidth() * scale, getHeight() * scale);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(width);
        out.writeDouble(height);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        x = in.readDouble();
        y = in.readDouble();
        width = in.readDouble();
        height = in.readDouble();
    }

    public String toString() {
        return "(" + x + ", " + y + "), (" + width + ", " + height + ")";
    }

}
