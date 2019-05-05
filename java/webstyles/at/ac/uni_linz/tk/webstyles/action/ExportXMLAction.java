package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import org.exolab.castor.mapping.*;
import org.exolab.castor.xml.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.xml.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class ExportXMLAction extends AbstractSaveAction {

    private static ExportXMLAction action = new ExportXMLAction();

    protected class ExportFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".xml") || file.isDirectory();
        }
        public String getDescription() {
            return new String("XML Files (Game Engine Project) (*.xml)");
        }
    }

    public static ExportXMLAction getAction() {
        return action;
    }

    private ExportXMLAction() {
        super("Export XML (Generic Game Engine)", new ImageIcon("images/export.gif"));
    }

    protected javax.swing.filechooser.FileFilter[] getFileFilters() {
        return new javax.swing.filechooser.FileFilter[] { new ExportFileFilter() };
    }

    protected void save(File file) throws Exception {
        Mapping mapping = new Mapping();
        mapping.loadMapping("mapping.xml");

        Marshaller marshaller = new Marshaller(new OutputStreamWriter(new FileOutputStream(file)));
        marshaller.setMapping(mapping);
        marshaller.marshal(new XMLGraph(WebStyles.getApplication().getGraph()));
    }

}