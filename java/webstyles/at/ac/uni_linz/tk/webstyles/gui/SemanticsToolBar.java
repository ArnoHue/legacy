package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.action.*;


public class SemanticsToolBar extends AbstractToolBar {
    /*
    public XGridBagConstraints(int aGridX,
        int aGridY,
        int aGridWidth,
        int aGridHeight,
        double aWeightX,
        double aWeightY,
        int anAnchor,
        int aFill,
        java.awt.Insets anInset,
        int anIpadX,
        int anIpadY) {
    */
    public SemanticsToolBar() {
        JToggleButton dbg = createToggleButton(DebugAction.getAction());
        dbg.setSelected(false);
        dbg.setText("");
        add(dbg);
        addSeparator();
        JComboBox algorithm = new JComboBox();
		algorithm.addItem("Trace Algorithm");
		algorithm.setMaximumSize(new Dimension(200, 40));
		add(algorithm);
        addSeparator();
        add(GoAction.getAction()).setText("");
        addSeparator();
    }

}