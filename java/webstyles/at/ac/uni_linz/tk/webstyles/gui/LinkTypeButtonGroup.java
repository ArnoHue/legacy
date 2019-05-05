package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class LinkTypeButtonGroup extends ButtonGroup {

    public JRadioButtonMenuItem mandatory;
    public JRadioButtonMenuItem optional;
    // public JRadioButtonMenuItem sequential;
    public JRadioButtonMenuItem fan;

    private static LinkTypeButtonGroup group;

    static {
        group = new LinkTypeButtonGroup();
    }

    public static LinkTypeButtonGroup getGroup() {
        return group;
    }

    private LinkTypeButtonGroup() {
        mandatory = createRadioButton(MandatoryAction.getAction());
        optional = createRadioButton(OptionalAction.getAction());
        // sequential = createRadioButton(SequenceAction.getAction());
        fan = createRadioButton(FanAction.getAction());

        add(mandatory);
        add(optional);
        // add(sequential);
        add(fan);
    }

    public void addToContainer(Container cont) {
        cont.add(mandatory);
        cont.add(optional);
        // cont.add(sequential);
        cont.add(fan);
    }

    public JRadioButtonMenuItem createRadioButton(Action action) {
        JRadioButtonMenuItem button = new JRadioButtonMenuItem((String)action.getValue(Action.NAME), (Icon)action.getValue(Action.SMALL_ICON));
        button.addActionListener(action);
        return button;
    }

}