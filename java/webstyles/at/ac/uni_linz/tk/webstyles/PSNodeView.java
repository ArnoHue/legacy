package at.ac.uni_linz.tk.webstyles;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.beans.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class PSNodeView extends PSComponentView implements PSConstants {
    
    protected static final int DEFAULT_RADIUS = 32;
    protected static final int INSETS_MARGIN = 10;
    
    protected Ellipse ellipse;
    protected Insets paintInsets;
    // protected JTextArea textArea;
      
    public PSNodeView(PSNodeController controller) {
        super(controller);
        paintInsets = new Insets(INSETS_MARGIN, INSETS_MARGIN, INSETS_MARGIN, INSETS_MARGIN);
        setEllipse(new Ellipse(0, 0, DEFAULT_RADIUS * 2, DEFAULT_RADIUS * 2));
        /*
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(false);
        textArea.setRows(3);
        setLayout(null);
        add(textArea);
        */
    }
    
    public void addNotify() {
        super.addNotify();
        double zoomFactor = getGraphView().getZoomFactor();
        setEllipse(new Ellipse(ellipse.getX(), ellipse.getY(), DEFAULT_RADIUS * zoomFactor * 2, DEFAULT_RADIUS * zoomFactor * 2));
    }
    
    public void copyBounds(PSComponentView view) {
        if (view instanceof PSNodeView) {
            PSNodeView nodeView = (PSNodeView) view;
            setEllipse((Ellipse)nodeView.ellipse.clone());
        }
    }
    
    protected Ellipse getEllipse() {
        return ellipse;
    }
    
    public boolean isContainedBy(Rectangle rec) {
        return new Rectangle(getLocation().x + INSETS_MARGIN,
                             getLocation().y + INSETS_MARGIN,
                             getSize().width - INSETS_MARGIN * 2,
                             getSize().height - INSETS_MARGIN * 2).intersects(rec);
    }
    
    protected void setEllipse(Ellipse ellipse) {
        Rectangle oldBounds = getBounds();
        this.ellipse = ellipse;
        // grid for screenshots
        /*
        super.setLocation((ellipse.getBounds().getLocation().x - paintInsets.left) - ((ellipse.getBounds().getLocation().x - paintInsets.left) % 32),
                          (ellipse.getBounds().getLocation().y - paintInsets.top) - ((ellipse.getBounds().getLocation().y - paintInsets.top) % 32));
        */
        super.setLocation((ellipse.getBounds().getLocation().x - paintInsets.left),
                          (ellipse.getBounds().getLocation().y - paintInsets.top));
        super.setSize(new Dimension(
                      ellipse.getBounds().getSize().width + paintInsets.left + paintInsets.right,
                      ellipse.getBounds().getSize().height + paintInsets.top + paintInsets.bottom));
        firePropertyChange(PROP_BOUNDS, oldBounds, getBounds());
    }
    
    public boolean contains(Point pos) {
        return new Ellipse(ellipse.getX(), ellipse.getY(), ellipse.getWidth() - 2, ellipse.getHeight() - 2).contains(pos);
    }
    
    public PSNode getModel() {
        return (PSNode)getModelInternal();
    }
    
    public void translate(int x, int y) {
        Ellipse ell = new Ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
        ell.translate(x, y);
        setEllipse(ell);
    }
    
    public void setRadius(int radius) {
        setSize(new Dimension(radius * 2, radius * 2));
    }
  
    public void setPosition(Point position) {
        Ellipse ell = new Ellipse(Math.max(position.x, 0), Math.max(position.y, 0), ellipse.getWidth(), ellipse.getWidth());
        setEllipse(ell);
    }
    
    protected Point getSeqLinkPos(int dir) {
        Point pos;
        PSLinkView linkView = (PSLinkView)(getGraphView().getComponentView(getModel().getSeqLink(dir)));
        if (linkView != null) {
            linkView.adjustPositions();
            pos = new Point((int)(dir == PSConstants.IN ? linkView.line.getX2() : linkView.line.getX1()), (int)(dir == PSConstants.IN ? linkView.line.getY2() : linkView.line.getY1()));
            pos.translate(- getLocation().x - 5, - getLocation().y - 5);
        }
        else {
            pos = new Point(dir == PSConstants.IN ? paintInsets.left - 10 : paintInsets.left + 2 * getRadius(), paintInsets.top + getRadius() - 5);
        }
        return pos;
    }       

    protected void fillOval(Graphics g) {
        PSNode node = (PSNode)getModel();
        int radius = getRadius();
        int diam = radius * 2;
        g.setColor(node.getType() != PSComponent.TYPE_OPTIONAL ? Color.black : Color.gray);
        g.fillOval(paintInsets.left, paintInsets.top, diam, diam);
        g.setColor(node.generic ? Color.lightGray : Color.white);
        g.fillOval(paintInsets.left + 2, paintInsets.top + 2, diam - 4, diam - 4);
    }

    protected void drawSequenceAnchors(Graphics g) {
        PSNode node = (PSNode)getModel();
        int radius = getRadius();
        int diam = radius * 2;
        if (node.getType() == PSComponent.TYPE_SEQUENCE || node.getType() == PSComponent.TYPE_OPTIONAL) {
            // TODO: get rid of double code, optimize!
            Point seqInPos, seqOutPos;
            int seqInAngle, seqOutAngle;
            seqInPos = getSeqLinkPos(PSConstants.IN);
            seqOutPos = getSeqLinkPos(PSConstants.OUT);

            seqInAngle = (int)(((Math.atan2((double)(paintInsets.top + radius - seqInPos.y - 5), (double)(seqInPos.x + 5 - paintInsets.left - radius)) * 180 / Math.PI) + 360) % 360);
            seqOutAngle = (int)(((Math.atan2((double)(paintInsets.top + radius - seqOutPos.y - 5), (double)(seqOutPos.x + 5 - paintInsets.left - radius)) * 180 / Math.PI) + 360) % 360);;
            
            g.setColor(Color.black);
            g.fillOval(seqInPos.x, seqInPos.y, 10, 10);
            g.fillOval(seqOutPos.x, seqOutPos.y, 10, 10);
            
            if (node.generic && node.getType() == PSComponent.TYPE_SEQUENCE) {
                g.drawArc(paintInsets.left - 3, paintInsets.top - 3, diam + 6, diam + 6, seqInAngle, seqOutAngle - seqInAngle > 0 ? seqOutAngle - seqInAngle - 360 : seqOutAngle - seqInAngle);
                GraphicTools.paintNormalArrowPeak(g, new Point(paintInsets.left + radius, paintInsets.top + radius), new Point(seqInPos.x + 5, seqInPos.y + 5));
            }
        }
    }
    
    protected String cutText(String src, FontMetrics metrics, int width) {
        int i;
        String target = src;
        for (i = 0; metrics.stringWidth(target) > width; i++) {
            target = target.substring(0, target.length() - 1);
        }
        if (i > 0) {
            target = target + ".";
        }
        return target;
    }    
  
  
    // ported from the visual chat project
    private void wrapText(String strTextParam, int iAvailableWidth, FontMetrics fnmMetrics, Vector resultingRows) {
        StringTokenizer stkText;
        String strText, strWord, strBrokenWord;
        int iIndex;

        stkText = new StringTokenizer(strTextParam, " _");
        strText = "";

        while (stkText.hasMoreTokens()) {
            strWord = stkText.nextToken();
            if (fnmMetrics.stringWidth(strWord) < iAvailableWidth) {
                resultingRows.addElement(strWord);
            }
            else {
                strBrokenWord = "";
                for (int i = 0; i < strWord.length() && fnmMetrics.stringWidth(strBrokenWord + strWord.charAt(i)) < iAvailableWidth; i++) {
                    strBrokenWord += strWord.charAt(i);
                }
                resultingRows.addElement(strBrokenWord);
                wrapText(strWord.substring(strBrokenWord.length()), iAvailableWidth, fnmMetrics, resultingRows);
            }
        }
    }
    protected void drawName(Graphics g) {
        int NR_OF_ROWS = 2;
        PSNode node = (PSNode)getModel();
        int radius = getRadius();
        int width = radius * 2 - 8;
        Vector rows = new Vector();
        g.setColor(Color.black);
        g.setFont(new Font("Helvetica", Font.PLAIN, (int)(getGraphView().getZoomFactor() * 10.0)));
        wrapText(node.getName(), width, g.getFontMetrics(), rows);
        if (rows.size() > 1) {
            g.drawString((String)rows.elementAt(0), paintInsets.left + radius - g.getFontMetrics().stringWidth((String)rows.elementAt(0)) / 2, paintInsets.top + radius - 2);
            g.drawString((String)rows.elementAt(1), paintInsets.left + radius - g.getFontMetrics().stringWidth((String)rows.elementAt(1)) / 2, paintInsets.top + radius + g.getFontMetrics().getAscent());
        }
        else  {
            g.drawString((String)rows.elementAt(0), paintInsets.left + radius - g.getFontMetrics().stringWidth((String)rows.elementAt(0)) / 2, paintInsets.top + radius + g.getFontMetrics().getAscent() / 2 - 2);
        }
    }

    protected void drawMark(Graphics g) {
        PSNode node = (PSNode)getModel();
        g.setColor(Color.black);
        GraphicTools.paintMark(g, node.getMark(), new Point(paintInsets.left, paintInsets.top), getRadius());
    }

    protected void drawAnchor(Graphics g, Color col) {
        int diam = getRadius() * 2;
        g.setColor(col);
        g.fillRect(paintInsets.left, paintInsets.top, 4, 4);
        g.fillRect(paintInsets.left + diam - 4, paintInsets.top, 4, 4);
        g.fillRect(paintInsets.left, paintInsets.top + diam - 4, 4, 4);
        g.fillRect(paintInsets.left + diam - 4, paintInsets.top + diam - 4, 4, 4);
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        PSNode node = (PSNode)getModel();
        fillOval(g);
        if (getRadius() >= DEFAULT_RADIUS) {
            drawName(g);
            if (node.getType() == PSComponent.TYPE_SEQUENCE || node.getType() == PSComponent.TYPE_OPTIONAL) {
                drawSequenceAnchors(g);
            }
            if (getGraphView().isDebugMode()) {
                drawMark(g);
            }
        }
        if (isSelected() || isHighlighted()) {
            drawAnchor(g, isSelected() ? Color.blue : Color.cyan);
        }
    }
      
    public int getRadius() {
        return (int)(ellipse.getWidth() / 2);
    }
    
    public void zoom(double factor) {
        Ellipse ell = new Ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
        ell.zoom(factor);
        setEllipse(ell);
    }
      
    public void editProperties() {
        NodePropertyDialog dlg = new NodePropertyDialog();
        dlg.setComponent((PSNode)getModel());
        dlg.setVisible(true);
    }

}