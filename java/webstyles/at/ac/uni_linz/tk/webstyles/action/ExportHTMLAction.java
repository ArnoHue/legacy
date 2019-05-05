package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class ExportHTMLAction extends AbstractSaveAction {

    private static ExportHTMLAction action = new ExportHTMLAction();

    public static Action getAction() {
        return action;
    }

    private ExportHTMLAction() {
        super("Export HTML", new ImageIcon("images/export.gif"));
    }
    
    public void actionPerformed(ActionEvent e) {
        File path;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select HTML Target Folder");
        if (chooser.showSaveDialog(WebStyles.getApplication().getFrame()) == JFileChooser.APPROVE_OPTION) {
            try {
                save(chooser.getSelectedFile());
            }
            catch (Exception excpt) {
                JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), excpt.toString(), "Export Error", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }
    
    protected void save(File file) throws Exception {
        PSGraph graph = WebStyles.getApplication().getGraph();
        TreeSet nodeNames = new TreeSet();
        for (Enumeration enum = graph.getNodes(); enum.hasMoreElements(); ) {
            PSNode node = (PSNode)enum.nextElement();
            nodeNames.add(node.getName());
        }
        for (Enumeration enum = graph.getNodes(); enum.hasMoreElements(); ) {
            PSNode node = (PSNode)enum.nextElement();
            File htmlFile = new File(file.getPath(), node.getName() + ".html");
            if (htmlFile != null) {
                try {
                    String contentText = node.contentText != null ? node.contentText : "<HTML><BODY></BODY></HTML>";
                    
                    HTMLEditorKit htmlKit = new HTMLEditorKit();
                    HTMLDocument doc = new HTMLDocument();
                    htmlKit.read(new StringReader(contentText), doc, 0);
                    HTMLDocument.Iterator it = doc.getIterator(HTML.Tag.A);
                    
                    while (it.isValid()) {
                        if (nodeNames.contains(it.getAttributes().getAttribute(HTML.Attribute.HREF).toString())) {
                            SimpleAttributeSet attrTag = new SimpleAttributeSet(doc.getCharacterElement(it.getStartOffset()).getAttributes());
                            SimpleAttributeSet href = new SimpleAttributeSet();
 			                href.addAttribute(HTML.Attribute.HREF, it.getAttributes().getAttribute(HTML.Attribute.HREF).toString() + ".html");
			                attrTag.addAttribute(HTML.Tag.A, href);
                            doc.setCharacterAttributes(it.getStartOffset(), it.getEndOffset() - it.getStartOffset() + 1, attrTag, true); 
                        }
                        it.next();
                    }
                    StringWriter writer = new StringWriter();
                    htmlKit.write(writer, doc, 0, doc.getLength() - 1);

                    BufferedWriter out = new BufferedWriter(new FileWriter(htmlFile));
                    out.write(writer.getBuffer().toString());
                    out.close();
                }
                catch (IOException excpt) {
                    excpt.printStackTrace();
                }
            }
            else {
                JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), "Invalid Folder", "Invalid Folder", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }

}