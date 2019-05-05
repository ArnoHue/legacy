package at.ac.uni_linz.tk.webstyles.util;

import java.awt.geom.*;
import java.io.*;

public class Line extends Line2D.Double implements Serializable {

    public Line(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
    }

    public void translate(double x, double y) {
        setLine(getX1() + x, getY1() + y, getX2() + x, getY2() + y);
    }

    public void zoom(double scale) {
        setLine(getX1() * scale, getY1() * scale, getX2() * scale, getY2() * scale);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeDouble(x1);
        out.writeDouble(y1);
        out.writeDouble(x2);
        out.writeDouble(y2);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        x1 = in.readDouble();
        y1 = in.readDouble();
        x2 = in.readDouble();
        y2 = in.readDouble();
    }

    public String toString() {
        return "(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ")";
    }

    public boolean equals(Object obj) {
        if (obj instanceof Line) {
            Line line = (Line)obj;
            return getX1() == line.getX1() && getY1() == line.getY1() &&
                   getX2() == line.getX2() && getY2() == line.getY2();
        }
        return false;
    }

}
