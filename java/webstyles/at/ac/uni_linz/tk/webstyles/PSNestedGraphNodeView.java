package at.ac.uni_linz.tk.webstyles;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;
import java.beans.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class PSNestedGraphNodeView extends PSNodeView implements Serializable, PSConstants {
      
    public PSNestedGraphNodeView(PSNodeController controller) {
        super(controller);
    }

    protected void fillOval(Graphics g) {
        super.fillOval(g);
        int radius = getRadius();
        int diam = radius * 2;
        g.setColor(Color.black);
        g.fillOval(paintInsets.left + 4, paintInsets.top + 4, diam - 8, diam - 8);
        g.setColor(Color.white);
        g.fillOval(paintInsets.left + 6, paintInsets.top + 6, diam - 12, diam - 12);
    }

    protected void drawSequenceAnchors(Graphics g) {
    }

    protected void drawMark(Graphics g) {
    }
      
    public void editProperties() {
        NestedGraphNodePropertyDialog dlg = new NestedGraphNodePropertyDialog();
        dlg.setNode((PSNestedGraphNode)getModel());
        dlg.setVisible(true);
    }

}