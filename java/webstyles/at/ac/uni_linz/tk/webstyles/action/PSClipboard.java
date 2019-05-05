package at.ac.uni_linz.tk.webstyles.action;

import java.util.*;
import javax.swing.*;
import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class PSClipboard {

    private Vector content;

    private static PSClipboard board = new PSClipboard();

    public static PSClipboard getClipboard() {
        return board;
    }

    private PSClipboard() {
        content = new Vector();
    }

    public Vector getContent() {
        return content;
    }

    public void setContent(Vector content) {
        this.content = content;
    }
    
    private void unlinkContent(int dir) {
        for (int i = 0; i < content.size(); i++) {
            PSComponent comp = (PSComponent)content.elementAt(i);
            Vector linkedComps = new Vector();
            for (Enumeration enum = comp.getLinkedComponents(dir); enum.hasMoreElements();) {
                linkedComps.addElement(enum.nextElement());
            }
            for (int j = 0; j < linkedComps.size(); j++) {
                PSComponent linkedComp = (PSComponent)linkedComps.elementAt(j);
                if (!content.contains(linkedComp)) {
                    comp.unlink(linkedComp, dir);
                }
            }
        }        
    }
    
    public void cut() {
        PSGraph graph = WebStyles.getApplication().getGraph();
        content = WebStyles.getApplication().getController().getComponents(PSComponent.SELECTED);
        unlinkContent(PSConstants.IN);
        unlinkContent(PSConstants.OUT);
        graph.removeComponents(WebStyles.getApplication().getController().getComponents(PSComponent.SELECTED));
    }

    public void copy() {
        content = WebStyles.getApplication().getController().getComponents(PSComponent.SELECTED);
    }
    
    private void relinkAfterPaste(PSComponent comp, Hashtable cloneMap, int dir) {
        PSComponent clonedComp = (PSComponent)cloneMap.get(comp);
        for (Enumeration enum = comp.getLinkedComponents(dir); enum.hasMoreElements();) {
            try {
                PSComponent comp1 = (PSComponent)enum.nextElement();
                PSComponent comp2 = (PSComponent)cloneMap.get(comp1);
                if (!clonedComp.isLinked(comp2, dir)) {
                    clonedComp.link(comp2, dir);
                }
            }
            catch (PSException excpt) {
                excpt.display();
            }
        }
    }

    public void paste() {
        Hashtable cloneMap = new Hashtable();
        for (int i = 0; i < content.size(); i++) {
            PSComponent obj =  (PSComponent)content.elementAt(i);
            PSComponent clone = (PSComponent)obj.clone();
            WebStyles.getApplication().getGraph().addComponent(clone);
            cloneMap.put(obj, clone);
        }

        Enumeration keys = cloneMap.keys();
        while (keys.hasMoreElements()) {
            PSComponent obj = (PSComponent)keys.nextElement();
            PSComponent clonedObj = (PSComponent)cloneMap.get(obj);
            relinkAfterPaste(obj, cloneMap, PSConstants.IN);
            relinkAfterPaste(obj, cloneMap, PSConstants.OUT);
        }
        copy();
    }

}