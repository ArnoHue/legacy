package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;


import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.*;

import JSX.*;

public abstract class AbstractSaveAction extends AbstractAction {

    protected class SaveFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".pre") || file.isDirectory();
        }
        public String getDescription() {
            return new String("PreScript Graphs (*.pre)");
        }
    }

    protected AbstractSaveAction(String name, ImageIcon icon) {
        super(name, icon);
    }

    protected javax.swing.filechooser.FileFilter[] getFileFilters() {
        return new javax.swing.filechooser.FileFilter[] { new SaveFileFilter() };
    }

    public void actionPerformed(ActionEvent e) {
        String saveFolder = (String)PSEditorProperties.getProperties().get(PSEditorProperties.PROPERTY_SAVE_FOLDER);
        JFileChooser chooser = new JFileChooser(saveFolder);
        javax.swing.filechooser.FileFilter[] filter = getFileFilters();
        for (int i = 0; i < filter.length; i++) {
            chooser.addChoosableFileFilter(filter[i]);
        }
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showSaveDialog(WebStyles.getApplication().getFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                try {
                    save(file);
                }
                catch (Exception excpt) {
                    JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), excpt.toString(), "Save Error", JOptionPane.ERROR_MESSAGE, null);
                }
            }
            else {
                JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), "Invalid File", "Save Error", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }

    protected void save(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".xml")) {
            ObjOut out = new ObjOut(new BufferedOutputStream(new FileOutputStream(file)));
            PSGraphController ctrl = WebStyles.getApplication().getController();
            WebStyles.getApplication().setController(new PSGraphController(new PSGraph()));
            out.writeObject(ctrl);
            out.close();
            WebStyles.getApplication().setController(ctrl);
        }
        else {
            PSGraphController ctrl = WebStyles.getApplication().getController();
            WebStyles.getApplication().setController(new PSGraphController(new PSGraph()));
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            os.writeObject(ctrl);
            os.flush();
            os.close();
            WebStyles.getApplication().setController(ctrl);
        }
        WebStyles.getApplication().getController().setName(file.getPath());
        PSMenuBar.getMenuBar().addFileHistoryEntry(file.getPath());
        PSEditorProperties.getProperties().put(PSEditorProperties.PROPERTY_SAVE_FOLDER, file.getPath().substring(0, file.getPath().lastIndexOf(File.separatorChar)));
    }

}