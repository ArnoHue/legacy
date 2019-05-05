package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;

public class AbstractToolBar extends JToolBar {

    public AbstractToolBar() {
        // setFloatable(false);
    }

    public JButton createButton(String imageFile) {
        JButton button = new JButton(new ImageIcon("images/" + imageFile));
        return button;
    }

    public JToggleButton createToggleButton(Action action) {
        JToggleButton button = new JToggleButton((String)action.getValue(Action.NAME), (Icon)action.getValue(Action.SMALL_ICON));
        button.addActionListener(action);
        button.setToolTipText(action.getValue(Action.NAME).toString());
        return button;
    }

}