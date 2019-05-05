package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class NodeTypeButtonGroup extends ButtonGroup {

    public JRadioButtonMenuItem mandatory;
    public JRadioButtonMenuItem optional;
    public JRadioButtonMenuItem sequential;

    private static NodeTypeButtonGroup group;

    static {
        group = new NodeTypeButtonGroup();
    }

    public static NodeTypeButtonGroup getGroup() {
        return group;
    }

    private NodeTypeButtonGroup() {
        mandatory = createRadioButton(MandatoryAction.getAction());
        optional = createRadioButton(OptionalAction.getAction());
        sequential = createRadioButton(SequenceAction.getAction());

        add(mandatory);
        add(optional);
        add(sequential);
    }

    public void addToContainer(Container cont) {
        cont.add(mandatory);
        cont.add(optional);
        cont.add(sequential);
    }

    public JRadioButtonMenuItem createRadioButton(Action action) {
        JRadioButtonMenuItem button = new JRadioButtonMenuItem((String)action.getValue(Action.NAME), (Icon)action.getValue(Action.SMALL_ICON));
        button.addActionListener(action);
        return button;
    }

}