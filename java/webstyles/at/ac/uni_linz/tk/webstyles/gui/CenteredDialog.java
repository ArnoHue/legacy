package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;

public class CenteredDialog extends JDialog {
    
    public CenteredDialog(Frame owner) {
        super(owner);
    }
    
    public void moveToCenter() {
        Dimension screenDim = getToolkit().getScreenSize();
        Dimension winDim = getSize();
        setLocation((screenDim.width - winDim.width) / 2, (screenDim.height - winDim.height) / 2);
    }
    
}