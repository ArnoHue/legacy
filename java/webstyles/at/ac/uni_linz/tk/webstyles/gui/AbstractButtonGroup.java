package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class AbstractButtonGroup extends ButtonGroup {

    protected JRadioButtonMenuItem createRadioButton(Action action) {
        JRadioButtonMenuItem button = new JRadioButtonMenuItem((String)action.getValue(Action.NAME), (Icon)action.getValue(Action.SMALL_ICON));
        button.addActionListener(action);
        return button;
    }

    protected JToggleButton createToggleButton(Action action) {
        JToggleButton button = new JToggleButton((String)action.getValue(Action.NAME), (Icon)action.getValue(Action.SMALL_ICON));
        button.addActionListener(action);
        return button;
    }

}