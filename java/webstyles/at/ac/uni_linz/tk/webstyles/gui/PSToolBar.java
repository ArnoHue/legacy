package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class PSToolBar extends AbstractToolBar {
    
    protected JToggleButton debugMode;

    public PSToolBar() {
        JButton but;
        add(NewAction.getAction());
        add(OpenAction.getAction());
        add(SaveAction.getAction());
        addSeparator();
        add(PrintAction.getAction());
        addSeparator();
        add(CutAction.getAction());
        add(CopyAction.getAction());
        add(PasteAction.getAction());
        add(DeleteAction.getAction());
        addSeparator();

        JToggleButton act1 = createToggleButton(EditAction.getAction());
        act1.setSelected(true);
        JToggleButton act2 = createToggleButton(NodeAction.getAction());
        JToggleButton act3 = createToggleButton(LinkAction.getAction());
        ButtonGroup grp = new ButtonGroup();
        grp.add(act1);
        grp.add(act2);
        grp.add(act3);
        
        EditModeButtonGroup.createToggleGroup().addToContainer(this);
        
        addSeparator();
        add(PropertyAction.getAction());
        add(InstanceAction.getAction());
        add(NoInstanceAction.getAction());
        
        addSeparator();
        debugMode = createToggleButton(DebugAction.getAction());
        debugMode.setSelected(false);
        debugMode.setText("");
        add(debugMode);
        addSeparator();

        add(MarkTraceAction.getAction());
    }
    
    public JToggleButton getDebugModeButton() {
        return debugMode;
    }
    
    public JButton add(Action action) {
        JButton button = super.add(action);
        button.setText("");
        button.setToolTipText(action.getValue(Action.NAME).toString());
        return button;
    }

}