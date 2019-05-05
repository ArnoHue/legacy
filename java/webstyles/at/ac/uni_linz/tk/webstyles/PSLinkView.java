package at.ac.uni_linz.tk.webstyles;

import java.awt.*;
import java.awt.geom.*;
import java.math.*;
import java.util.*;
import java.beans.*;

import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class PSLinkView extends PSComponentView implements PSConstants {

    protected Line line;
    protected Insets insets;
    
    private static final int MAX_TEXT_LEN = 100;

    public PSLinkView(PSLinkController controller) {
        super(controller);
        insets = new Insets(20, 20, 20, MAX_TEXT_LEN);
        setLine(new Line(0, 0, 0, 0));
    }
    
    public boolean isContainedBy(Rectangle rec) {
        return new Rectangle(getLocation().x + insets.left,
                             getLocation().y + insets.top,
                             getSize().width - insets.left - insets.right,
                             getSize().height - insets.top - insets.bottom).intersects(rec);
    }

    protected void setLine(Line line) {
        if (!line.equals(this.line)) {
            Rectangle oldBounds = new Rectangle(getBounds());
            this.line = line;
            super.setLocation((int)Math.min(line.getX1(), line.getX2()) - insets.left,
                            (int)Math.min(line.getY1(), line.getY2()) - insets.top);
            super.setSize((int)(Math.abs(line.getX2() - line.getX1()) + 1 + insets.left + insets.right),
                        (int)(Math.abs(line.getY2() - line.getY1()) + 1 + insets.top + insets.bottom));
            firePropertyChange(PROP_BOUNDS, oldBounds, getBounds());
        }
    }

    public void copyBounds(PSComponentView view) {
        if (view instanceof PSLinkView) {
            PSLinkView linkView = (PSLinkView) view;
            setLine((Line)linkView.line.clone());
        }
    }

    public void setPosition(Point position) {
        setLine(new Line(Math.max(position.x, 0), Math.max(position.y, 0), line.getX2(), line.getY2()));
    }

    public int getDistanceFromTargetPosition(Point point) {
        return (int)line.getP2().distance(point.x, point.y);
    }

    public int getDistanceFromSourcePosition(Point point) {
        return (int)line.getP1().distance(point.x, point.y);
    }

    public void translate(int x, int y) {
        Line transLine = new Line(line.getX1(), line.getY1(), line.getX2(), line.getY2());
        transLine.translate(x, y);
        setLine(transLine);
    }

    public void setToPosition(Point position) {
        setLine(new Line(line.getX1(), line.getY1(), Math.max(position.x, 0), Math.max(position.y, 0)));
    }

    public Point getToPosition() {
        return new Point((int)line.getX2(), (int)line.getY2());
    }

    public void editProperties() {
        LinkPropertyDialog dlg = new LinkPropertyDialog();
        dlg.setComponent((PSLink)getModel());
        dlg.setVisible(true);
    }

    public Color getForeground() {
        return Color.black;
    }

    public PSLinkController getController() {
        return (PSLinkController)getControllerInternal();
    }

    public void repaint() {
        super.repaint();
        PSComponentView nodeView;
        PSLink link = (PSLink)getModel();
        if (link!= null && getGraphView() != null) {
            if (link.getNode(PSConstants.IN) != null) {
                nodeView = getGraphView().getComponentView(link.getNode(PSConstants.IN));
                if (nodeView != null) {
                    nodeView.repaint();
                }
            }
            if (link.getNode(PSConstants.OUT) != null) {
                nodeView = getGraphView().getComponentView(link.getNode(PSConstants.OUT));
                if (nodeView != null) {
                    nodeView.repaint();
                }
            }
        }
    }

    protected void adjustPositions() {
        Point srcPosition = null;
        Point tgtPosition = null;
        double length;
        Dimension delta;
        PSLink link = (PSLink)getModel();

        PSNode srcNode = link.getNode(PSConstants.IN);
        PSNode tgtNode = link.getNode(PSConstants.OUT);
        PSNodeView srcNodeView = (PSNodeView)getController().getView(srcNode);
        PSNodeView tgtNodeView = (PSNodeView)getController().getView(tgtNode);

        if (srcNodeView != null || tgtNodeView != null) {
            srcPosition = srcNodeView != null ? srcNodeView.getCenter() : getPoint(line.getP1());
            tgtPosition = tgtNodeView != null ? tgtNodeView.getCenter() : getPoint(line.getP2());

            if (!srcPosition.equals(tgtPosition)) {
                double angleRad = Math.atan2((double)(srcPosition.y - tgtPosition.y), (double)(tgtPosition.x - srcPosition.x));
                double angleOffsetRad = 0.0;
                if (tgtNode != null && srcNode != null) {
                    if (tgtNode.getLinkedNodes(PSConstants.OUT).contains(srcNode)) {
                        if (tgtNode.id > srcNode.id) {
                            angleOffsetRad = 10.0 * Math.PI / 180.0;
                        }
                        else {
                            angleOffsetRad = 10.0 * Math.PI / 180.0;
                        }
                    }
                }
                if (srcNode != null) {
                    int cos = (int)(Math.cos(angleRad + angleOffsetRad) * srcNodeView.getRadius());
                    int sin = (int)(Math.sin(angleRad + angleOffsetRad) * srcNodeView.getRadius());
                    setPosition(new Point((int)(srcNodeView.getCenter().x + cos), (int)(srcNodeView.getCenter().y - sin)));
                }
                if (tgtNode != null) {
                    int cos = (int)(Math.cos(angleRad - angleOffsetRad) * tgtNodeView.getRadius());
                    int sin = (int)(Math.sin(angleRad - angleOffsetRad) * tgtNodeView.getRadius());
                    setToPosition(new Point((int)(tgtNodeView.getCenter().x - cos), (int)(tgtNodeView.getCenter().y + sin)));
                }
            }
            else {
                if (srcNodeView != null) {
                    setPosition(new Point(srcNodeView.getCenter()));
                }
                if (tgtNode != null) {
                    setToPosition(new Point(tgtNodeView.getCenter()));
                }
            }
        }
    }
      
    public Point getCenter() {
        Point center = super.getCenter();
        return new Point(center.x + (insets.left - insets.right) / 2, center.y + (insets.top - insets.bottom) / 2);
    }

    protected Point getPoint(Point2D point) {
        return new Point((int)point.getX(), (int)point.getY());
    }

    public PSLink getModel() {
        return (PSLink)getModelInternal();
    }

    public void paint(Graphics g) {
        PSLink link = getModel();
        Point p1 = new Point((int)line.getX1() - getLocation().x, (int)line.getY1() - getLocation().y);
        Point p2 = new Point((int)line.getX2() - getLocation().x, (int)line.getY2() - getLocation().y);
        g.setColor(Color.black);
        if (getGraphView().getZoomFactor() >= 0.5) {
            if (link.type == PSComponent.TYPE_FAN && link.generic) {
                GraphicTools.paintFanPSArrow(g, p1, p2, link.generic, isSelected());
            }
            else {
                if (link.getNode(PSConstants.IN) != null && link.getNode(PSConstants.OUT) != null) {
                    GraphicTools.paintPSArrow(g, p1, p2, link.generic, isSelected(), link.join);
                }
                else {
                    GraphicTools.paintSimpleArrow(g, p1, p2, isSelected());
                }
            }

            if (getGraphView().isDebugMode()) {
                GraphicTools.paintMark(g, link.getMark(), p1, 32);
                g.setColor(Color.black);
            }
            if (getGraphView().getZoomFactor() >= 1.0) {
                String name = link.getName();
                g.setColor(Color.black);
                g.setFont(new Font("Helvetica", Font.PLAIN, (int)(getGraphView().getZoomFactor() * 10.0)));
                while(g.getFontMetrics().stringWidth(name) > MAX_TEXT_LEN - 32) {
                    name = name.substring(0, name.length() - 1);
                }
                if (!name.equals(link.getName())) {
                    name += ".";
                }
                g.drawString(name, (p1.x + p2.x) / 2 + 10, (p1.y + p2.y) / 2);
            }
            
        }
        else {
            GraphicTools.paintSimpleArrow(g, p1, p2, isSelected(), 4);
        }
        adjustPositions();
    }

    protected double getLength(Dimension vector) {
        return Math.sqrt(vector.width * vector.width + vector.height * vector.height);
    }

    protected Dimension getDelta(Point fromPos, Point toPos) {
        return new Dimension(toPos.x - fromPos.x, toPos.y - fromPos.y);
    }

    public boolean contains(Point pos) {
        return line.ptSegDist(pos) < 5;
    }

    public void zoom(double factor) {
        Line zoomLine = new Line(line.getX1(), line.getY1(), line.getX2(), line.getY2());
        zoomLine.zoom(factor);
        setLine(zoomLine);
    }

}