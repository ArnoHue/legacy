package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import at.ac.uni_linz.tk.webstyles.*;

public class GeneralNodePanel extends GeneralPanel {
    
    public GeneralNodePanel() {
        super();
		type.addItem(PSComponent.TYPE_MANDATORY_STRING);
		type.addItem(PSComponent.TYPE_OPTIONAL_STRING);
		type.addItem(PSComponent.TYPE_SEQUENCE_STRING);
    }
    
}