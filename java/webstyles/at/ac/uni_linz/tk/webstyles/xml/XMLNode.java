package at.ac.uni_linz.tk.webstyles.xml;

import java.util.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.generic.ContentTemplate;

public class XMLNode {
    
    PSNode node;
    
    public XMLNode() {
    }
    
    public XMLNode(PSNode node) {
        this.node = node;
    }
        
    public int getId() {
        return node.id;
    }
        
    public String getName() {
        return node.getName();
    }
    
    public Vector getProperties() {
        return new XMLProperties(node.getProperties());
    }
    
    public Object getContent() {
        return XMLContentFactory.getXMLContent(node.content);
    }
    
}