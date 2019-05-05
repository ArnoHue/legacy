package at.ac.uni_linz.tk.webstyles.util;

import java.awt.geom.*;

public class Bounds extends Rectangle2D.Double {

    public Bounds translate(double x, double y) {
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        return (Bounds)transform.createTransformedShape(this);
    }

    public Bounds scale(double scale) {
        AffineTransform transform = new AffineTransform();
        transform.scale(scale, scale);
        return (Bounds)transform.createTransformedShape(this);
    }

}
