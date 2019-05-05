package at.ac.uni_linz.tk.webstyles;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;

import at.ac.uni_linz.tk.webstyles.generic.*;

public class PSNestedGraphNode extends PSNode implements Serializable {
    
    public static class Port implements Serializable {
        public PSLink innerLink;
        public PSLink outerLink;
        public int direction;
        
        public Port(PSLink innerLinkParam, PSLink outerLinkParam) {
            innerLink = innerLinkParam;
            outerLink = outerLinkParam;
            if (innerLink.getNode(PSConstants.IN) == null) {
                direction = PSConstants.IN;
            }
            else if (innerLink.getNode(PSConstants.OUT) == null) {
                direction = PSConstants.OUT;
            }
            else {
                direction = PSConstants.UNKNOWN;
            }
        }
        
        public String toString() {
            return outerLink.toString() + " [inner] / " + outerLink.toString() + " [outer]";
        }
    }
    
    public static class ViewMap implements Serializable {
        public PSComponent comp;
        public Point pos;
        
        public ViewMap(PSComponent compParam, Point posParam) {
            comp = compParam;
            pos = posParam;
        }
    }
    
    public static class NestedGraph implements Serializable {
        public PSGraph graph;
        public PSGraphView view;
        public Hashtable viewMap;
        
        public NestedGraph(PSGraphController ctrl) {
            graph = ctrl.getModel();
            view = ctrl.getView();
            viewMap = new Hashtable();
            for (Enumeration enum = view.getComponentViews(); enum.hasMoreElements();) {
                PSComponentView view = (PSComponentView)enum.nextElement();
                viewMap.put(view.getModelInternal(), new ViewMap((PSComponent)view.getModelInternal(), view.getLocation()));
            }
        }
    }
    
    protected Hashtable ports;
    protected NestedGraph nestedGraph;
    
    public PSNestedGraphNode() {
        name = "NestedGraph_" + id;
        type = TYPE_MANDATORY;
        ports = new Hashtable();
    }
      
    public void unlink(PSComponent linkParam, int dir) {
        super.unlink(linkParam, dir);
        if (ports.containsKey(linkParam)) {
            ports.remove(linkParam);
        }
    }
    
    public void isInstantiable() throws PSNotInstantiableException {
        throw new PSNotInstantiableException(PSNotInstantiableException.REASON_NESTEDGRAPHNODE, this);
    }

    public void dontInstantiate() throws PSNotLinkableException {
    }
    
    public void removeAllPorts() {
        ports.clear();
    }
    
    public void addPort(Port port) {
        ports.put(port.outerLink, port);
    }
    
    public Enumeration getPorts() {
        return ports.elements();
    }
    
    public NestedGraph getNestedGraph() {
        return nestedGraph;
    }
    
    public void setNestedGraph(NestedGraph nestedGraphParam) {
        nestedGraph = nestedGraphParam;
    }
    
    public Enumeration getNestedGraphLinks(NestedGraph graphParam) {
        Vector vec = new Vector();
        if (graphParam != null) {
            for (Enumeration enum = graphParam.graph.getLinks(); enum.hasMoreElements();) {
                PSLink link = (PSLink)enum.nextElement();
                if (link.getNode(PSConstants.IN) == null || link.getNode(PSConstants.OUT) == null) {
                    vec.addElement(link);
                }
            }
        }
        return vec.elements();
    }
    
    public Enumeration getNestedGraphLinks() {
        return getNestedGraphLinks(nestedGraph);
    }
     
    public void expand() {
        if (nestedGraph != null) {
            for (Enumeration enum = nestedGraph.graph.getComponents(); enum.hasMoreElements();) {
                graph.addComponent((PSComponent)enum.nextElement());
            }
            for (Enumeration enum = ports.elements(); enum.hasMoreElements();) {
                Port port = (Port)enum.nextElement();
                port.outerLink.unlink(this, getInverseDir(port.direction));
                try {
                    port.outerLink.link(port.innerLink.getNode(getInverseDir(port.direction)), getInverseDir(port.direction));
                }
                catch (PSNotLinkableException excpt) {
                    excpt.printStackTrace();
                }
                port.innerLink.unlinkAll();
                graph.removeComponent(port.innerLink);
            }
        }
        unlinkAll();
        graph.removeComponent(this);
    }
   
}