package at.ac.uni_linz.tk.webstyles.xml;

import at.ac.uni_linz.tk.webstyles.*;
import java.util.*;

public class XMLGraph {
    
    PSGraph graph;
        
    public XMLGraph() {
    }
    
    public XMLGraph(PSGraph graph) {
        this.graph = graph;
    }
        
    public Vector getLinks() {
        Enumeration graphLinks = graph.getLinks();
        Vector links = new Vector();
        while (graphLinks.hasMoreElements()) {
            XMLLink link = new XMLLink((PSLink)graphLinks.nextElement());
            links.addElement(link);
        }
        return links;
    }
    
    public Vector getNodes() {
        Enumeration graphNodes = graph.getNodes();
        Vector nodes = new Vector();
        while (graphNodes.hasMoreElements()) {
            XMLNode node = new XMLNode((PSNode)graphNodes.nextElement());
            nodes.addElement(node);
        }
        return nodes;
    }
    
}