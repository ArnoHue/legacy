package at.ac.uni_linz.tk.webstyles.action;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import at.ac.uni_linz.tk.webstyles.*;

import at.ac.uni_linz.tk.webstyles.gui.*;
import JSX.*;

public class OpenAction extends AbstractAction implements PropertyChangeListener {

    private static OpenAction action = new OpenAction();
    protected JScrollPane scroller;
    protected JFileChooser chooser;
    
    protected class OpenFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".pre") || file.isDirectory();
        }
        public String getDescription() {
            return new String("PreScript Graphs");
        }
    }

    protected class OpenXMLFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".xml") || file.isDirectory();
        }
        public String getDescription() {
            return new String("XML Graphs");
        }
    }

    public static OpenAction getAction() {
        return action;
    }

    private OpenAction() {
        super("Open", new ImageIcon("images/open.gif"));
        chooser = new JFileChooser();
        scroller = new JScrollPane();
        scroller.setMaximumSize(new Dimension(200, 200));
        scroller.setPreferredSize(new Dimension(200, 200));
        chooser.setAccessory(scroller);
        chooser.addPropertyChangeListener(this);
        chooser.addChoosableFileFilter(new OpenXMLFileFilter());
        chooser.setFileFilter(new OpenFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    public void actionPerformed(ActionEvent e) {
        open(selectFile());
    }
    
    public void open(String fileName) {
        if (fileName != null) {
            PSEditorProperties.getProperties().put(PSEditorProperties.PROPERTY_SAVE_FOLDER, fileName);
            PSGraphController ctrl = loadGraph(fileName);
            if (ctrl != null) {
                ctrl.setName(fileName);
                WebStyles.getApplication().setController(ctrl);
                PSMenuBar.getMenuBar().addFileHistoryEntry(fileName);
                ZoomAction.updateEnabledStates();
                AbstractUndoRedoAction.createSnapshot("Open Graph");
            }
        }
    }
    
    public String selectFile() {
        String saveFolder = (String)PSEditorProperties.getProperties().get(PSEditorProperties.PROPERTY_SAVE_FOLDER);
        if (saveFolder != null) {
            chooser.setCurrentDirectory(new File(saveFolder));
        }
        scroller.getViewport().removeAll();
        updatePreview();
        if (chooser.showOpenDialog(WebStyles.getApplication().getFrame()) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getPath();
        }
        return null;
    }
    
    protected PSGraphController loadGraphInternal(String fileName) throws Exception {
        PSGraphController ctrl = null;
        File file = new File(fileName);
        if (file.getName().toLowerCase().endsWith(".xml")) {
            ObjIn in = new ObjIn(new BufferedInputStream(new FileInputStream(file)));
            ctrl = (PSGraphController)in.readObject();
            in.close();
        }
        else {
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            ctrl = (PSGraphController)is.readObject();
            is.close();
        }
        return ctrl;
    }
    
    public PSGraphController loadGraph(String fileName) {
        try {
            return loadGraphInternal(fileName);
        }
        catch (Exception excpt) {
            excpt.printStackTrace();
            JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), excpt.toString(), "Open Error", JOptionPane.ERROR_MESSAGE, null);
        }
        return null;
    }
    
    public void propertyChange (PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            updatePreview();
        }
    }
    
    private void updatePreview() {
        scroller.getViewport().removeAll();
        if (chooser.getSelectedFile() != null) {
            if (!chooser.getSelectedFile().isDirectory()) {
                try {
                    PSGraphController ctrl = loadGraphInternal(chooser.getSelectedFile().getPath());
                    if (ctrl != null) {
                        ctrl.getView().zoom(0.5);
                        scroller.getViewport().add(ctrl.getView());
                        ctrl.disconnect();
                    }
                }
                catch (Exception excpt) {
                    JTextArea text = new JTextArea(excpt.toString());
                    text.setLineWrap(true);
                    scroller.getViewport().add(text);
                    text.setEnabled(false);
                }
            }
            scroller.repaint();
        }
    }

}