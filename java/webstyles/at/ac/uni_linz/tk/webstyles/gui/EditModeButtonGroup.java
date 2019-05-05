package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public abstract class EditModeButtonGroup extends AbstractButtonGroup {
    
    public static final int EDIT = 0;
    public static final int NODE = 1;
    public static final int LINK = 2;
    
    protected static ButtonModel editModel;
    protected static ButtonModel nodeModel;
    protected static ButtonModel linkModel;
    
    public static class EditModeToggleGroup extends EditModeButtonGroup {
        private JToggleButton edit;
        private JToggleButton node;
        private JToggleButton link;

        public EditModeToggleGroup() {
            edit = createToggleButton(EditAction.getAction());
            node = createToggleButton(NodeAction.getAction());
            link = createToggleButton(LinkAction.getAction());
            
            if (editModel != null && nodeModel != null && linkModel != null) {
                edit.setModel(editModel);
                node.setModel(nodeModel);
                link.setModel(linkModel);
            }
            else {
                editModel = edit.getModel();
                nodeModel = node.getModel();
                linkModel = link.getModel();
                editModel.setSelected(true);
            }

            add(edit);
            add(node);
            add(link);
        }
        public void addToContainer(Container cont) {
            cont.add(edit);
            cont.add(node);
            cont.add(link);
        }
    }
    
    public static class EditModeRadioGroup extends EditModeButtonGroup {
        private JRadioButtonMenuItem edit;
        private JRadioButtonMenuItem node;
        private JRadioButtonMenuItem link;

        public EditModeRadioGroup() {
            edit = createRadioButton(EditAction.getAction());
            node = createRadioButton(NodeAction.getAction());
            link = createRadioButton(LinkAction.getAction());
            
            if (editModel != null && nodeModel != null && linkModel != null) {
                edit.setModel(editModel);
                node.setModel(nodeModel);
                link.setModel(linkModel);
            }
            else {
                editModel = edit.getModel();
                nodeModel = node.getModel();
                linkModel = link.getModel();
                editModel.setSelected(true);
            }

            add(edit);
            add(node);
            add(link);
        }

        public void addToContainer(Container cont) {
            cont.add(edit);
            cont.add(node);
            cont.add(link);
        }
    }

    public static EditModeButtonGroup createToggleGroup() {
        return new EditModeToggleGroup();
    }

    public static EditModeButtonGroup createRadioGroup() {
        return new EditModeRadioGroup();
    }

    public static void setMode(int mode) {
        switch(mode) {
            case EDIT:
                editModel.setSelected(true);
                break;
            case NODE:
                nodeModel.setSelected(true);
                break;
            case LINK:
                linkModel.setSelected(true);
                break;
        }
    }

    public abstract void addToContainer(Container cont);

}