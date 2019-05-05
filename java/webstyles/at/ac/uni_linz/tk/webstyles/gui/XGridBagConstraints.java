package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;

public class XGridBagConstraints extends GridBagConstraints {
    
    public static final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);
    public static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    
    public XGridBagConstraints(int aGridX,
        int aGridY) {
            this (aGridX, aGridY, 1, 1);
    }
        
    public XGridBagConstraints(int aGridX,
        int aGridY,
        int aGridWidth,
        int aGridHeight) {
            this(aGridX, aGridY, aGridWidth, aGridHeight, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
    }
    
    public XGridBagConstraints(int aGridX,
        int aGridY,
        int aGridWidth,
        int aGridHeight,
        int anAnchor,
        int aFill) {
            this(aGridX, aGridY, aGridWidth, aGridHeight, 0.0, 0.0, anAnchor, aFill);
    } 
    
    public XGridBagConstraints(int aGridX,
        int aGridY,
        int aGridWidth,
        int aGridHeight,
        double aWeightX,
        double aWeightY) {
            this(aGridX, aGridY, aGridWidth, aGridHeight, aWeightX, aWeightY, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
    }
    
    public XGridBagConstraints(int aGridX,
        int aGridY,
        int aGridWidth,
        int aGridHeight,
        double aWeightX,
        double aWeightY,
        int anAnchor,
        int aFill) {
            this(aGridX, aGridY, aGridWidth, aGridHeight, aWeightX, aWeightY, anAnchor, aFill, DEFAULT_INSETS, 0, 0);
    }
        
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
        
        gridx = aGridX;
        gridy = aGridY;
        gridwidth = aGridWidth;
        gridheight = aGridHeight;
        weightx = aWeightX;
        weighty = aWeightY;
        anchor = anAnchor;
        fill = aFill;
        insets = anInset;
        ipadx = anIpadX;
        ipady = anIpadY;
        
    }
}
