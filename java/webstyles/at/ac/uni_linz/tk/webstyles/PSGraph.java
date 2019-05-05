package at.ac.uni_linz.tk.webstyles;

import java.awt.*;
import java.util.*;
import java.beans.*;

public class PSGraph extends PSObject implements PSConstants, PropertyChangeListener {
    
    static final long serialVersionUID = -7029577061431093479L;

    private Vector nodes;
    private Vector links;

    public PSGraph() {
        nodes = new Vector(100);
        links = new Vector(100);
    }

    public Enumeration getLinks() {
        return links.elements();
    }

    public Enumeration getNodes() {
        return nodes.elements();
    }

    public void addComponent(PSComponent component) {
        if (component != null) {
            Vector objects = component instanceof PSNode ? nodes : links;
            if (!objects.contains(component) && component != null) {
                objects.insertElementAt(component, 0);
                component.setGraph(this);
                firePropertyChange(PROP_ADD_NOTIFY, null, component);
            }
        }
    }

    public void removeComponent(PSComponent component) {
        if (component != null) {
            (component instanceof PSNode ? nodes : links).remove(component);
            if (!component.generic && component.genericComp != null) {
                component.genericComp.nrInstances--;
            }
            // component.setGraph(null);
            firePropertyChange(PROP_REMOVE_NOTIFY, component, null);
        }
    }

    public void removeComponents(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
          PSComponent comp = (PSComponent)vector.elementAt(i);
          removeComponent(comp);
        }
    }

    public Enumeration getComponents() {
        Vector vec = new Vector(nodes.size() + links.size());
        vec.addAll(nodes);
        vec.addAll(links);
        return vec.elements();
    }

    public int getNrOfComponents() {
        return nodes.size() + links.size();
    }

    public boolean containsComponent(PSComponent comp) {
        return nodes.contains(comp) || links.contains(comp);
    }

    public int getNrOfLinks() {
        return links.size();
    }

    public int getNrOfNodes() {
        return nodes.size();
    }

    public void resetMarks() {
        for (int i = 0; i < links.size(); i++) {
            PSLink obj = (PSLink)links.elementAt(i);
            obj.setMark(PSComponent.MARK_NONE);
        }
        for (int i = 0; i < nodes.size(); i++) {
            PSNode obj = (PSNode)nodes.elementAt(i);
            obj.setMark(PSComponent.MARK_NONE);
        }
    }

    public synchronized boolean markTrace(PSComponent startComp) {
        resetMarks();
        if (startComp instanceof PSNode && startComp.type == PSComponent.TYPE_SEQUENCE) {
            PSNode startNode = (PSNode)startComp;
            startNode.setMark(PSComponent.MARK_SEQUENCE);
            Enumeration links;
            links = startNode.getLinkedComponents(PSConstants.OUT);
            while (links.hasMoreElements()) {
                PSLink link = (PSLink)links.nextElement();
                if (link != startNode.getSeqLink(PSConstants.OUT)) {
                    if (!markLinksRecursive(link, PSConstants.OUT)) {
                        return false;
                    }
                }
            }
            links = startNode.getLinkedComponents(PSConstants.IN);
            while (links.hasMoreElements()) {
                PSLink link = (PSLink)links.nextElement();
                if (link != startNode.getSeqLink(PSConstants.IN)) {
                    if (!markLinksRecursive(link, PSConstants.IN)) {
                        return false;
                    }
                }
            }
            return true;
        }
        else if (startComp instanceof PSLink && startComp.type == PSComponent.TYPE_FAN) {
            PSLink link = (PSLink)startComp;
            link.setMark(PSComponent.MARK_JOIN);
            PSNode srcNode = link.getNode(PSConstants.IN);
            PSNode dstNode = link.getNode(PSConstants.OUT);
            if (srcNode != null) {
                srcNode.setMark(PSComponent.MARK_FAN);
            }
            if (dstNode != null) {
                markNodesRecursive(dstNode, link);
            }
        }
        return true;
    }

    public synchronized boolean markNodesRecursive(PSNode node, PSLink link) {
        if (node.getMark() == PSComponent.MARK_FAN) {
            return false;
        }
        if (node.getType() == PSComponent.TYPE_SEQUENCE) {
            if (link == node.getSeqLink(PSConstants.IN) || link == node.getSeqLink(PSConstants.OUT)) {
                if (node.getMark() == PSComponent.MARK_DUPLICATE) {
                    return true;
                }
                if (node.getMark() == PSComponent.MARK_SEQUENCE) {
                    return false;
                }
                node.setMark(PSComponent.MARK_DUPLICATE);
                for (Enumeration enum = node.getLinkedComponents(PSConstants.OUT); enum.hasMoreElements();) {
                    PSLink outLink = (PSLink)enum.nextElement();
                    if (outLink != link) {
                        if (!markLinksRecursive(outLink, PSConstants.OUT)) {
                            return false;
                        }
                    }
                }
                for (Enumeration enum = node.getLinkedComponents(PSConstants.IN); enum.hasMoreElements();) {
                    PSLink inLink = (PSLink)enum.nextElement();
                    if (inLink != link) {
                        if (!markLinksRecursive(inLink, PSConstants.IN)) {
                            return false;
                        }
                    }
                }
                return true;
            }
            else {
                if (node.getMark() == PSComponent.MARK_DUPLICATE) {
                    return false;
                }
                if (node.getMark() == PSComponent.MARK_SEQUENCE) {
                    return true;
                }
                node.setMark(PSComponent.MARK_SEQUENCE);
                for (Enumeration enum = node.getLinkedComponents(PSConstants.OUT); enum.hasMoreElements();) {
                    PSLink outLink = (PSLink)enum.nextElement();
                    if (outLink != link && outLink != node.getSeqLink(PSConstants.OUT)) {
                        if (!markLinksRecursive(outLink, PSConstants.OUT)) {
                            return false;
                        }
                    }
                }
                for (Enumeration enum = node.getLinkedComponents(PSConstants.IN); enum.hasMoreElements();) {
                    PSLink inLink = (PSLink)enum.nextElement();
                    if (inLink != link && inLink != node.getSeqLink(PSConstants.IN)) {
                        if (!markLinksRecursive(inLink, PSConstants.IN)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        if (node.getMark() == PSComponent.MARK_DUPLICATE) {
            return false;
        }
        if (node.getMark() == PSComponent.MARK_SEQUENCE) {
            return true;
        }
        node.setMark(PSComponent.MARK_SEQUENCE);
        Enumeration links;
        links = node.getLinkedComponents(PSConstants.OUT);
        while (links.hasMoreElements()) {
            PSLink outLink = (PSLink)links.nextElement();
            if (outLink != link) {
                if (!markLinksRecursive(outLink, PSConstants.OUT)) {
                    return false;
                }
            }
        }
        links = node.getLinkedComponents(PSConstants.IN);
        while (links.hasMoreElements()) {
            PSLink inLink = (PSLink)links.nextElement();
            if (inLink != link) {
                if (!markLinksRecursive(inLink, PSConstants.IN)) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized boolean markLinksRecursive(PSLink link, int direction) {
        PSNode targetNode = link.getNode(PSConstants.OUT);
        PSNode sourceNode = link.getNode(PSConstants.IN);
        if (link.getMark() == PSComponent.MARK_JOIN) {
            return false;
        }
        if (direction == PSConstants.OUT && link.isJoin()) {
            if (link.getMark() == PSComponent.MARK_DUPLICATE || (targetNode != null && targetNode.getMark() == PSComponent.MARK_DUPLICATE)) {
                return false;
            }
            link.setMark(PSComponent.MARK_JOIN);
            if (targetNode != null) {
                targetNode.setMark(PSComponent.MARK_FAN);
            }
            return true;
        }
        if (direction == PSConstants.IN && link.getType() == PSComponent.TYPE_FAN) {
            if (link.getMark() == PSComponent.MARK_DUPLICATE || (sourceNode != null && sourceNode.getMark() == PSComponent.MARK_DUPLICATE)) {
                return false;
            }
            link.setMark(PSComponent.MARK_JOIN);
            if (sourceNode != null) {
                sourceNode.setMark(PSComponent.MARK_FAN);
            }
            return true;
        }
        if (link.getMark() == PSComponent.MARK_DUPLICATE) {
            return true;
        }
        link.setMark(PSComponent.MARK_DUPLICATE);
        if (direction == PSConstants.OUT) {
            if (targetNode != null) {
                if (!markNodesRecursive(targetNode, link)) {
                    return false;
                }
            }
        }
        else if (direction == PSConstants.IN) {
            if (sourceNode != null) {
                if (!markNodesRecursive(sourceNode, link)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void moveToMetaSpace() {
        for (Enumeration enum = getComponents(); enum.hasMoreElements();) {
            PSComponent comp = (PSComponent)enum.nextElement();
            if (comp.getMark() == PSComponent.MARK_METASPACE) {
                comp.moveToMetaSpace();
                comp.setMark(PSComponent.MARK_NONE);
            }
        }
    }

}