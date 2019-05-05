package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class ExportEngineAction extends AbstractSaveAction {

    private static ExportEngineAction action = new ExportEngineAction();

    public static Action getAction() {
        return action;
    }

    private ExportEngineAction() {
        super("Export Engine", new ImageIcon("images/export.gif"));
    }
    
    public void actionPerformed(ActionEvent e) {
        File path;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Engine Target Folder");
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
        try {
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(file.getPath(), "engine.pre"))));
            os.writeObject(WebStyles.getApplication().getController());
            os.flush();
            os.close();
        }
        catch (IOException excpt) {
            excpt.printStackTrace();
        }
        for (Enumeration enum = graph.getNodes(); enum.hasMoreElements(); ) {
            PSNode node = (PSNode)enum.nextElement();
            File tplFile = new File(file.getPath(), node.getName() + ".tpl");
            File srvFile = new File(file.getPath(), node.getName() + ".java");
            if (tplFile != null && srvFile != null) {
                try {
                    BufferedWriter out;
                    out = new BufferedWriter(new FileWriter(tplFile));
                    out.write(node.contentText != null ? node.contentText : "<HTML><BODY></BODY></HTML>");
                    out.close();
                    String linkCond = "";
                    for (Enumeration links = node.getLinkedComponents(); links.hasMoreElements();) {
                        PSLink link = (PSLink)links.nextElement();
                        if (link.getForwardRules() != null && link.getForwardRules().length() > 0) {
                            linkCond += "    if (linkName.equals(\"" + link.getName() + "\")) return (" + link.getForwardRules() + ");\n";
                        }
                    }
                    out = new BufferedWriter(new FileWriter(srvFile));
                    out.write("import at.ac.uni_linz.tk.webstyles.engine.*;\nimport javax.servlet.*;\nimport javax.servlet.http.*;\n\npublic class " + node.getName() + " extends PSServlet {\n  public " + node.getName() + "() {\n    super(\"" + node.getName() + "\", \"engine.pre\");\n  }\n\n  protected boolean isLinkEnabledInternal(HttpSession session, String linkName) {\n" + linkCond + "\n    return true;\n  }\n}");
                    out.close();
                }
                catch (IOException excpt) {
                    excpt.printStackTrace();
                }
            }
            else {
                JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), "Invalid Folder", "Invalid Folder", JOptionPane.ERROR_MESSAGE, null);
                return;
            }
        }
    }

}