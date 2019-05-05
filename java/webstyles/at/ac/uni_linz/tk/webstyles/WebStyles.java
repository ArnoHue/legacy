package at.ac.uni_linz.tk.webstyles;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.action.*;
import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class WebStyles implements Observer {
    
    private static WebStyles webStyles;
    
    private CenteredFrame frame;
    private JScrollPane scroller;
    private JViewport graphViewContainer;
    private PSGraphController controller;
    private PSStatusBar statusBar;
    private PSToolBar toolBar;
    
    public static final String APP_VERSION = "WebStyles Editor";
    
    public static void main(String[] args) {
        webStyles = new WebStyles();
        NewAction.getAction().actionPerformed(new ActionEvent(webStyles.frame, 0, null));
    }
    
    public static WebStyles getApplication() {
        return webStyles;
    }
    
    private WebStyles() {
        JPanel toolbarPanel;
        PSEditorProperties.getProperties().load();
        frame = new CenteredFrame();
        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BorderLayout());
        frame.setSize(800, 600);
        frame.setJMenuBar(PSMenuBar.getMenuBar());
        frame.getContentPane().setLayout(new BorderLayout());
        toolbarPanel.add(BorderLayout.NORTH, toolBar = new PSToolBar());
        frame.getContentPane().add(BorderLayout.NORTH, toolbarPanel);
	    scroller = new JScrollPane();
	    graphViewContainer = scroller.getViewport();
        frame.getContentPane().add(BorderLayout.CENTER, scroller);
        frame.getContentPane().add(BorderLayout.SOUTH, statusBar = new PSStatusBar());
        
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/link.gif"));
        frame.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    ExitAction.getAction().exit();
                }
            }
        );
        int index = 0;
        String className;
        do {
            index++;
            className = (String)PSEditorProperties.getProperties().get(PSEditorProperties.PROPERTY_GENERIC_CONTENT + PSEditorProperties.PROPERTY_SUFFIX + index);
            if (className != null) {
                try {
                    Class.forName(className);
                }
                catch (ClassNotFoundException excpt) {
                    excpt.printStackTrace();
                }
            }
        } while (className != null);
        frame.moveToCenter();
        frame.setVisible(true);
    }
    
    public PSStatusBar getStatusBar() {
        return statusBar;
    }

    public void setController(PSGraphController controllerParam) {
        if (controller != null) {
            PSGraphView oldView = controller.getView();
            oldView.removeObserver(this);
            controller.removeObserver(this);
            graphViewContainer.remove(oldView);
        }
        controllerParam.getView().addObserver(this);
        controllerParam.addObserver(this);
        graphViewContainer.add(controllerParam.getView());
        controller = controllerParam;
        updateActionStates();
    }

    public PSGraphController getController() {
        return controller;
    }

    public PSGraph getGraph() {
        return controller.getModel();
    }

    public PSGraphView getGraphView() {
        return controller.getView();
    }

    public Frame getFrame() {
        return frame;
    }

    public void updateActionStates() {
        Vector vec = controller.getComponents(PSComponent.SELECTED);
        int nrSelected = vec.size();
        boolean selected = nrSelected > 0;
        PSComponent selectedComp = selected ? (PSComponent)vec.elementAt(0) : null;
        boolean oneNodeSelected = nrSelected == 1 && selectedComp instanceof PSNode;
        boolean oneLinkSelected = nrSelected == 1 && selectedComp instanceof PSLink;
        boolean oneCompSelected = oneNodeSelected || oneLinkSelected;
        int editMode = controller.getEditMode();
        
        NodeTypeButtonGroup nodeGroup = NodeTypeButtonGroup.getGroup();
        LinkTypeButtonGroup linkGroup = LinkTypeButtonGroup.getGroup();
        
        // TODO: let enabling/disabling be implemented by each action

        PropertyAction.getAction().setEnabled(oneCompSelected);
        InstanceAction.getAction().setEnabled(oneCompSelected && selectedComp.isInstantiableForUser());
        NoInstanceAction.getAction().setEnabled(oneCompSelected && selectedComp.generic && (selectedComp.minInstances == 0 || selectedComp.pseudoGeneric));
        SelectAllAction.getAction().setEnabled(controller.getModel().getNrOfComponents() > 0);
        MarkTraceAction.getAction().setEnabled((oneNodeSelected && selectedComp.type == PSComponent.TYPE_SEQUENCE) || (oneLinkSelected && selectedComp.type == PSComponent.TYPE_FAN));
        DeleteAction.getAction().setEnabled(selected);
        CutAction.getAction().setEnabled(selected);
        CopyAction.getAction().setEnabled(selected);
        PasteAction.getAction().setEnabled(PSClipboard.getClipboard().getContent().size() > 0);
        BringToFrontAction.getAction().setEnabled(selected);
        BringToBackAction.getAction().setEnabled(selected);
        SaveAction.getAction().setEnabled(controller.getModel().getNrOfComponents() > 0 && controller.getName() != null);
        SaveAsAction.getAction().setEnabled(controller.getModel().getNrOfComponents() > 0);
        ExportXMLAction.getAction().setEnabled(controller.getModel().getNrOfComponents() > 0);
        ExportEngineAction.getAction().setEnabled(controller.getModel().getNrOfComponents() > 0);
        ExportHTMLAction.getAction().setEnabled(controller.getModel().getNrOfComponents() > 0);
        PrintAction.getAction().setEnabled(controller.getModel().getNrOfComponents() > 0);

        nodeGroup.mandatory.setEnabled(oneNodeSelected && !(selectedComp instanceof PSNestedGraphNode));
        nodeGroup.mandatory.setSelected(oneNodeSelected && selectedComp.getType() == PSComponent.TYPE_MANDATORY);
        nodeGroup.optional.setEnabled(oneNodeSelected && !(selectedComp instanceof PSNestedGraphNode));
        nodeGroup.optional.setSelected(oneNodeSelected && selectedComp.getType() == PSComponent.TYPE_OPTIONAL);
        nodeGroup.sequential.setEnabled(oneNodeSelected && !(selectedComp instanceof PSNestedGraphNode));
        nodeGroup.sequential.setSelected(oneNodeSelected && selectedComp.getType() == PSComponent.TYPE_SEQUENCE);
        
        ExpandNestedGraphNodeAction.getAction().setEnabled(oneNodeSelected && selectedComp instanceof PSNestedGraphNode && ((PSNestedGraphNode)selectedComp).getNestedGraph() != null);
        
        setStatus("");
        if (oneNodeSelected && (selectedComp.getType() == PSComponent.TYPE_SEQUENCE || selectedComp.getType() == PSComponent.TYPE_OPTIONAL)) {
            PSNode node = (PSNode)selectedComp;
            if (node.getSeqLink(PSConstants.IN) == null || node.getSeqLink(PSConstants.OUT) == null) {
                setStatus("Warning: " + PSComponent.getTypeString(node.getType()) + " Node is missing Sequence Link(s)");
            }
        }

        linkGroup.mandatory.setEnabled(oneLinkSelected);
        linkGroup.mandatory.setSelected(oneLinkSelected && selectedComp.getType() == PSComponent.TYPE_MANDATORY);
        linkGroup.optional.setEnabled(oneLinkSelected);
        linkGroup.optional.setSelected(oneLinkSelected && selectedComp.getType() == PSComponent.TYPE_OPTIONAL);
        linkGroup.fan.setEnabled(oneLinkSelected);
        linkGroup.fan.setSelected(oneLinkSelected && selectedComp.getType() == PSComponent.TYPE_FAN);
    
        LinkPopupMenu.getMenu().join.setSelected(oneLinkSelected && ((PSLink)selectedComp).isJoin());
        LinkPopupMenu.getMenu().sequence.setSelected(oneLinkSelected && ((PSLink)selectedComp).isSequence());
        
        EditModeButtonGroup.setMode(editMode == PSGraphController.MODE_NODE ? EditModeButtonGroup.NODE : (editMode == PSGraphController.MODE_LINK ? EditModeButtonGroup.LINK : EditModeButtonGroup.EDIT));
        frame.setTitle(APP_VERSION + (controller != null && controller.getName() != null ? " - " + controller.getName() : ""));
        statusBar.setNrOfComponents(controller.getModel().getNrOfNodes(), controller.getModel().getNrOfLinks(), nrSelected);
        PSMenuBar.getMenuBar().getNestedNodeModeItem().setState(controller.getNestedNodeMode());
        toolBar.getDebugModeButton().setSelected(controller.getView().isDebugMode());
    }
    
    public void update(Observable obs, Object obj) {
        if (obs == controller.getView().SELECTION_RECTANGLE_OBSERVABLE) {
            setRectangle(controller.getView().getSelectionRectangle());
        }
        else {
            updateActionStates();
        }
    }
    
    public void setStatus(String status) {
        statusBar.setStatus(status);
    }        
    
    public void setRectangle(Rectangle rect) {
        statusBar.setRectangle(rect);
    }        

}