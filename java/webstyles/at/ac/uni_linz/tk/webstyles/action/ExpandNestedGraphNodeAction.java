package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class ExpandNestedGraphNodeAction extends AbstractAction {

    private static ExpandNestedGraphNodeAction action = new ExpandNestedGraphNodeAction();

    public static Action getAction() {
        return action;
    }

    private ExpandNestedGraphNodeAction() {
        super("Expand Nested Graph Node", new ImageIcon("images/expand.gif"));
    }

    public void actionPerformed(ActionEvent e) {
        PSNestedGraphNode node = (PSNestedGraphNode)WebStyles.getApplication().getController().getComponent(PSComponent.SELECTED);
        PSGraphView graphView = WebStyles.getApplication().getGraphView();
        PSNestedGraphNodeView nodeView = (PSNestedGraphNodeView)graphView.getComponentView(node);
        node.expand();
        if (node.getNestedGraph() != null) {
            Point center, sum;
            Hashtable map = node.getNestedGraph().viewMap;
            int count = 0;
            PSGraphView view = node.getNestedGraph().view;
            sum = new Point(0, 0);
            for (int i = 0; i < view.getComponentCount() - 1; i++) {
                Component comp = view.getComponents()[i];
                if (comp instanceof PSNodeView) {
                    count++;
                    sum.translate(comp.getLocation().x, comp.getLocation().y);
                }
            }
            center = new Point(sum.x / count, sum.y / count);
            for (Enumeration enum = map.elements(); enum.hasMoreElements();) {
                Point pos;
                PSNestedGraphNode.ViewMap viewMap = (PSNestedGraphNode.ViewMap)enum.nextElement();
                PSComponentView compView = graphView.getComponentView(viewMap.comp);
                if (compView != null) {
                    compView.translate(viewMap.pos.x + nodeView.getLocation().x - center.x, viewMap.pos.y + nodeView.getLocation().y - center.y);
                }
            }
        }
    }

}