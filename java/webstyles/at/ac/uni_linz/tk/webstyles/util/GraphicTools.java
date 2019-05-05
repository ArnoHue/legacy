package at.ac.uni_linz.tk.webstyles.util;

import java.awt.*;
import java.math.*;
import java.util.*;

import at.ac.uni_linz.tk.webstyles.*;

public abstract class GraphicTools {

    public static double getLength(Dimension vector) {
        return Math.sqrt(vector.width * vector.width + vector.height * vector.height);
    }

    public static Dimension getDelta(Point fromPos, Point toPos) {
        return new Dimension(toPos.x - fromPos.x, toPos.y - fromPos.y);
    }

    public static void paintPSArrow(Graphics g, Point fromPos, Point toPos, boolean generic, boolean selected) {
        paintPSArrow(g, fromPos, toPos, generic, selected, false);
    }

    public static void paintPSArrow(Graphics g, Point fromPos, Point toPos, boolean generic, boolean selected, boolean join) {
        Dimension delta = getDelta(fromPos, toPos);
        double length = getLength(delta);
        paintSimpleArrow(g, fromPos, toPos, selected);
        if (length > 20) {
            paintSquare(g, new Point(fromPos.x + delta.width / 2, fromPos.y + delta.height / 2), 8);
            Color col = g.getColor();
            g.setColor(generic ? Color.lightGray : Color.white);
            paintSquare(g, new Point(fromPos.x + delta.width / 2, fromPos.y + delta.height / 2), 6);
            g.setColor(col);
            if (generic && join) {
                // g.setFont(new Font("Helvetica", Font.PLAIN, (int)(WebStyles.getApplication().getGraph().getZoomFactor() * 10.0)));
                g.setFont(new Font("Helvetica", Font.PLAIN, 10));
                g.drawString("J", fromPos.x + delta.width / 2 - g.getFontMetrics().stringWidth("J") / 2, fromPos.y + delta.height / 2 + g.getFontMetrics().getAscent() / 2);
            }
        }
    }

    public static void paintFanPSArrow(Graphics g, Point fromPos, Point toPos, boolean generic, boolean selected) {
        paintPSArrow(g, fromPos, toPos, generic, selected);
        Dimension delta = getDelta(fromPos, toPos);
        double theta = Math.atan2(delta.height, delta.width);
        Point pos1 = new Point((int)(fromPos.x + 40 * Math.cos(theta + Math.PI / 6)), (int)(fromPos.y + 40 * Math.sin(theta + Math.PI / 6)));
        Point pos2 = new Point((int)(fromPos.x + 40 * Math.cos(theta - Math.PI / 6)), (int)(fromPos.y + 40 * Math.sin(theta - Math.PI / 6)));
        paintSimpleArrow(g, fromPos, pos1, false);
        paintSimpleArrow(g, fromPos, pos2, false);
    }

    public static void paintSimpleArrow(Graphics g, Point fromPos, Point toPos, boolean selected) {
        paintSimpleArrow(g, fromPos, toPos, selected, 8);
    }

    public static void paintSimpleArrow(Graphics g, Point fromPos, Point toPos, boolean selected, int size) {
        Dimension delta = getDelta(fromPos, toPos);
        double length = getLength(delta);
        if (length > 0) {
            paintArrowPeak(g, fromPos, toPos, size);
            g.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);
            if (selected) {
                Color col = g.getColor();
                g.setColor(Color.red);
                g.fillRect(fromPos.x - 2, fromPos.y - 2, 4, 4);
                g.fillRect(toPos.x - 2,  toPos.y - 2, 4, 4);
                g.setColor(col);
            }
        }
    }

    public static void paintArrowPeak(Graphics g, Point fromPos, Point toPos) {
        paintArrowPeak(g, fromPos, toPos, 8);
    }

    public static void paintNormalArrowPeak(Graphics g, Point fromPos, Point toPos) {
        Dimension delta = new Dimension(fromPos.x - toPos.x, fromPos.y - toPos.y);
        paintArrowPeak(g, new Point(toPos.x + delta.height, toPos.y - delta.width), toPos);
    }

    public static void paintArrowPeak(Graphics g, Point fromPos, Point toPos, int size) {
        Dimension delta = getDelta(fromPos, toPos);
        double length = getLength(delta);
        double widthFac = delta.width / length;
        double heightFac = delta.height / length;
        Polygon poly = new Polygon();
        Point crossPos = new Point((int)(toPos.x - size * 2 * widthFac), (int)(toPos.y - size * 2 * heightFac));
        poly.addPoint((int)(crossPos.x - size * heightFac), (int)(crossPos.y + size * widthFac));
        poly.addPoint((int)(toPos.x - size * 1.5 * widthFac), (int)(toPos.y - size * 1.5 * heightFac));
        poly.addPoint((int)(crossPos.x + size * heightFac), (int)(crossPos.y - size * widthFac));
        poly.addPoint(toPos.x, toPos.y);
        g.fillPolygon(poly);
    }

    public static void paintSquare(Graphics g, Point pos, int len) {
        Polygon poly = new Polygon();
        poly.addPoint(pos.x, pos.y - len);
        poly.addPoint(pos.x + len, pos.y);
        poly.addPoint(pos.x, pos.y + len);
        poly.addPoint(pos.x - len, pos.y);
        g.fillPolygon(poly);
    }

    public static void paintMark(Graphics g, int mark, Point position, int radius) {
        String id = "";
        g.setColor(Color.white);
        g.fillOval(position.x, position.y , radius / 2, radius / 2);
        g.setColor(Color.black);
        g.drawOval(position.x, position.y, radius / 2, radius / 2);
        switch(mark) {
            case PSComponent.MARK_FAN:
                g.setColor(Color.red);
                id = "F";
                break;
            case PSComponent.MARK_SEQUENCE:
                g.setColor(Color.green);
                id = "S";
                break;
            case PSComponent.MARK_JOIN:
                g.setColor(Color.blue);
                id = "J";
                break;
            case PSComponent.MARK_DUPLICATE:
                g.setColor(Color.cyan);
                id = "D";
                break;
        }
        g.drawString(id, position.x + radius / 4 - g.getFontMetrics().stringWidth(id) / 2, position.y + radius / 4 + g.getFontMetrics().getAscent() / 2 - 2);
    }

}