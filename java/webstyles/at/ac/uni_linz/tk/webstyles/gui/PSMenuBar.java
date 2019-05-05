package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class PSMenuBar extends JMenuBar implements ActionListener {
    
    private static PSMenuBar menuBar;
    
    private JMenuItem[] fileHistoryItem = new JMenuItem[4];
    private JCheckBoxMenuItem nestedNodeModeItem;
    
    static {
        menuBar = new PSMenuBar();
    }
    
    public static PSMenuBar getMenuBar() {
        return menuBar;
    }
    
    public JCheckBoxMenuItem getNestedNodeModeItem() {
        return nestedNodeModeItem;
    }

    private PSMenuBar() {
        JMenu file = new JMenu("File");
        file.add(NewAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));;
        file.add(OpenAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));;
        file.add(SaveAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));;
        file.add(SaveAsAction.getAction());
        file.addSeparator();
        file.add(ExportHTMLAction.getAction());
        file.add(ExportEngineAction.getAction());
        file.add(ExportXMLAction.getAction());
        file.addSeparator();
        file.add(PrintAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));;
        file.addSeparator();

        for (int i = 0; i < fileHistoryItem.length; i++) {
            file.add(fileHistoryItem[i] = new JMenuItem(PSEditorProperties.getProperties().getProperty(PSEditorProperties.PROPERTY_FILE_HISTORY[i])));
            fileHistoryItem[i].addActionListener(this);
        }
        file.addSeparator();
        
        file.add(ExitAction.getAction());
        add(file);

        JMenu edit = new JMenu("Edit");
        edit.add(UndoAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
        edit.add(RedoAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
        edit.addSeparator();
        edit.add(CutAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
        edit.add(CopyAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
        edit.add(PasteAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
        edit.add(DeleteAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        edit.addSeparator();
        edit.add(SelectAllAction.getAction()).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
        add(edit);

        JMenu view = new JMenu("View");
        view.add(ZoomInAction.getAction());
        view.add(ZoomOutAction.getAction());
        view.addSeparator();
        view.add(BringToFrontAction.getAction());
        view.add(BringToBackAction.getAction());
        add(view);

        JMenu tools = new JMenu("Tools");
        JRadioButtonMenuItem editButton = createRadioButtonMenuItem(EditAction.getAction());
        JRadioButtonMenuItem nodeButton = createRadioButtonMenuItem(NodeAction.getAction());
        JRadioButtonMenuItem linkButton = createRadioButtonMenuItem(LinkAction.getAction());
        editButton.setSelected(true);
        ButtonGroup grp = new ButtonGroup();
        grp.add(editButton);
        grp.add(nodeButton);
        grp.add(linkButton);

        EditModeButtonGroup.createRadioGroup().addToContainer(tools);
        tools.addSeparator();
        tools.add(nestedNodeModeItem = createCheckBoxMenuItem(InsertNestedGraphNodeAction.getAction()));
        tools.add(ExpandNestedGraphNodeAction.getAction());
        
        tools.addSeparator();
        tools.add(PropertyAction.getAction());
        tools.add(InstanceAction.getAction());
        tools.add(NoInstanceAction.getAction());
        add(tools);
        
        JMenu help = new JMenu("Help");
        help.add(AboutAction.getAction());
        add(help);
	}
	
	public void addFileHistoryEntry(String fileName) {
        for (int i = 0; i < fileHistoryItem.length; i++) {
            if (fileHistoryItem[i].getText().equals(PSEditorProperties.FILE_HISTORY_EMPTY)) {
                setFileHistoryEntry(i, fileName);
                return;
            }
            else if (fileHistoryItem[i].getText().equals(fileName)) {
                return;
            }
        }
        for (int i = fileHistoryItem.length - 1; i > 0; i--) {
            setFileHistoryEntry(i, fileHistoryItem[i - 1].getText());
        }
        setFileHistoryEntry(0, fileName);
    }
    
    public void setFileHistoryEntry(int index, String fileName) {
        fileHistoryItem[index].setText(fileName);
        PSEditorProperties.getProperties().put(PSEditorProperties.PROPERTY_FILE_HISTORY[index], fileName);
    }  
    
    protected JRadioButtonMenuItem createRadioButtonMenuItem(Action action) {
        JRadioButtonMenuItem button = new JRadioButtonMenuItem((String)action.getValue(Action.NAME), (Icon)action.getValue(Action.SMALL_ICON));
        button.addActionListener(action);
        return button;
    }
    
    protected JCheckBoxMenuItem createCheckBoxMenuItem(Action action) {
        JCheckBoxMenuItem button = new JCheckBoxMenuItem((String)action.getValue(Action.NAME), (Icon)action.getValue(Action.SMALL_ICON));
        button.addActionListener(action);
        return button;
    }
    
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < fileHistoryItem.length; i++) {
            if (e.getSource() == fileHistoryItem[i] && !fileHistoryItem[i].getText().equals(PSEditorProperties.FILE_HISTORY_EMPTY)) {
                OpenAction.getAction().open(fileHistoryItem[i].getText());
            }
        }
    }

}