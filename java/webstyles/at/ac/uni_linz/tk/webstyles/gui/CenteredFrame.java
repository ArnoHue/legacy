package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;

public class CenteredFrame extends JFrame {
    
    public CenteredFrame() {
        super();
    }
    
    public CenteredFrame(String title) {
        super(title);
    }
    
    public void moveToCenter() {
        Dimension screenDim = getToolkit().getScreenSize();
        Dimension winDim = getSize();
        setLocation((screenDim.width - winDim.width) / 2, (screenDim.height - winDim.height) / 2);
    }
    
}