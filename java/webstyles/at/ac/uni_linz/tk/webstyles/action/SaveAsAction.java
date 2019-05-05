package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;


import at.ac.uni_linz.tk.webstyles.gui.*;

public class SaveAsAction extends AbstractSaveAction {

    private static SaveAsAction action;

    protected class SaveXMLFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".xml") || file.isDirectory();
        }
        public String getDescription() {
            return new String("XML Graphs (*.xml)");
        }
    }

    protected javax.swing.filechooser.FileFilter[] getFileFilters() {
        return new javax.swing.filechooser.FileFilter[] { new SaveXMLFileFilter(), new SaveFileFilter() };
    }
    
    static {
        action = new SaveAsAction();
    }

    public static AbstractSaveAction getAction() {
        return action;
    }

    protected SaveAsAction(String name, ImageIcon icon) {
        super(name, icon);
    }

    private SaveAsAction() {
        this("Save as", new ImageIcon("images/save.gif"));
    }

}