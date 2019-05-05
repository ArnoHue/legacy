package at.ac.uni_linz.tk.webstyles;

import java.awt.*;
import java.awt.geom.*;
import java.math.*;
import java.util.*;
import java.util.Enumeration;

public class PSLink extends PSComponent {
    
    static final long serialVersionUID = 3285470972484641630L;
    
    protected boolean join;
    protected boolean sequence;
    
    private PSNode srcNode;
    private PSNode destNode;
    private PSNode metaSrcNode;
    private PSNode metaDestNode;
    
    private String forwRules;
      
    public PSLink() {
        this(TYPE_MANDATORY);
    }
      
    public PSLink(int type) {
        name = "Link_" + id;
        this.type = type;
    }
    
    public void setForwardRules(String forwRulesParam) {
        forwRules = forwRulesParam;
    }
    
    public String getForwardRules() {
        return forwRules;
    }
    
    public void setJoin(boolean join) {
        Boolean oldJoin = new Boolean(this.join);
        this.join = join;
        firePropertyChange(PROP_JOIN, oldJoin, new Boolean(join));
    }
    
    public boolean isJoin() {
        return join;
    }
    
    public void setSequence(boolean sequence) {
        Boolean oldSequence = new Boolean(this.sequence);
        this.sequence = sequence;
        firePropertyChange(PROP_SEQUENCE, oldSequence, new Boolean(sequence));
    }
    
    public boolean isSequence() {
        return sequence;
    }
    
    public int getNrOfLinkedComponents(int dir) {
        if (dir == PSConstants.IN) {
            return srcNode == null ? 0 : 1;
        }
        else if (dir == PSConstants.OUT) {
            return destNode == null ? 0 : 1;
        }
        return 0;
    }
    
    public Enumeration getLinkedComponents(int dir) {
        Vector vec = new Vector();
        if (dir == PSConstants.IN && srcNode != null) {
            vec.addElement(srcNode);
        }
        else if (dir == PSConstants.OUT && destNode != null) {
            vec.addElement(destNode);
        }
        return vec.elements();
    }
    
    public boolean isLinked(PSComponent comp) {
        return srcNode == comp || destNode == comp;
    }
    
    public boolean isLinked(PSComponent comp, int dir) {
        return (dir == PSConstants.IN && srcNode == comp) || (dir == PSConstants.OUT && destNode == comp);
    }
    
    public void link(PSComponent srcComp, PSComponent destComp) throws PSNotLinkableException {
        link(srcComp, IN);
        link(destComp, OUT);
    }
    
    public void link(PSComponent comp, int dir) throws PSNotLinkableException {
        isLinkableWith(comp, dir);
        PSNode node = (PSNode)comp;
        switch(dir) {
            case IN:
                srcNode = node;
                if (!node.isLinked(this, OUT)) {
                    node.link(this, OUT);
                }
                break;
            case OUT:
                destNode = node;
                if (!node.isLinked(this, IN)) {
                    node.link(this, IN);
                }
                break;
        }
    }
    
    public void unlinkAll() {
        if (srcNode != null) {
            unlink(srcNode, PSConstants.IN);
        }
        if (destNode != null) {
            unlink(destNode, PSConstants.OUT);
        }
    }
    
    public void moveLinksToMetaSpace() {
        if (srcNode != null) {
            moveLinkToMetaSpace(srcNode, PSConstants.IN);
        }
        if (destNode != null) {
            moveLinkToMetaSpace(destNode, PSConstants.OUT);
        }
    }
    
    public void moveLinkToMetaSpace(PSNode node, int dir) {
        switch(dir) {
            case IN:
                if (srcNode == node) {
                    metaSrcNode = srcNode;
                    srcNode = null;
                    if (node.isLinked(this, OUT)) {
                        node.moveLinkToMetaSpace(this, OUT);
                    }
                }
                break;
            case OUT:
                if (destNode == node) {
                    metaDestNode = destNode;
                    destNode = null;
                    if (node.isLinked(this, IN)) {
                        node.moveLinkToMetaSpace(this, IN);
                    }
                }
                break;
        }
    }
    
    public void unlink(PSComponent node, int dir) {
        switch(dir) {
            case IN:
                if (srcNode == node) {
                    srcNode = null;
                    if (node.isLinked(this, OUT)) {
                        node.unlink(this, OUT);
                    }
                }
                break;
            case OUT:
                if (destNode == node) {
                    destNode = null;
                    if (node.isLinked(this, IN)) {
                       node.unlink(this, IN);
                    }
                }
                break;
        }
    }    
    
    public boolean isInstantiableForUser() {
        try {
            isInstantiable();
            return ((srcNode != null && !srcNode.generic) ||
            (destNode != null && !destNode.generic) ||
            (type == TYPE_FAN && srcNode != null && srcNode.generic));
        }
        catch (PSNotInstantiableException e) {
            return false;
        }
    }
        
    public void isLinkableWith(PSComponent comp, int dir) throws PSNotLinkableException{
        if (!(comp instanceof PSNode)) {
            throw new PSNotLinkableException(PSNotLinkableException.REASON_NOT_COMPATIBLE, dir == PSConstants.IN ? comp : this, dir == PSConstants.OUT ? comp : this);
        }
        else if (getNrOfLinkedComponents(dir) != 0) {
            throw new PSNotLinkableException(PSNotLinkableException.REASON_ALREADY_LINKED, dir == PSConstants.IN ? comp : this, dir == PSConstants.OUT ? comp : this);
        }
        /*
        else if (comp.generic != generic) {
            PSNode node = (PSNode)comp;
            if ((!generic && !genericComp.isLinked(node, dir)) ||
                (generic && !isLinked(node.genericComp, dir))) {
                    throw new PSNotLinkableException(PSNotLinkableException.REASON_NOT_COMPATIBLE, dir == PSConstants.IN ? comp : this, dir == PSConstants.OUT ? comp : this);
            }
        }
        */
    }
    
    public void dontInstantiate() throws PSNotLinkableException {
        if (type == TYPE_FAN) {
            if (destNode != null) {
                if (destNode.generic) {
                    try {
                        graph.markTrace(this);
                        setMark(MARK_NONE);
                        destNode.instantiateTrace(null, PSConstants.IN, INST_MODE_NO_INSTANTIATION);
                    }
                    catch (PSNotInstantiableException excpt) {
                        excpt.printStackTrace();
                    }
                }
                else {
                    destNode.moveToMetaSpace();
                    /*
                    destNode.unlinkAll();
                    graph.removeComponent(destNode);
                    */
                }
            }
        }
        moveToMetaSpace();
        /*
        unlinkAll();
        graph.removeComponent(this);
        */
    }
    
    public void instantiate() throws PSNotInstantiableException, PSNotLinkableException {
        PSLink instLink;
        isInstantiable();
        instLink = (PSLink)instantiateInternal();
        if (srcNode != null) {
            instLink.link(srcNode, PSConstants.IN);
        }
        if (type == TYPE_FAN) {
            if (destNode != null && destNode.generic) {
                graph.markTrace(this);
                setMark(MARK_NONE);
                destNode.instantiateTrace(instLink, IN, nrInstances == maxInstances ? INST_MODE_LAST_TRACE : INST_MODE_DEFAULT);
                graph.moveToMetaSpace();
            }
            else {
                destNode.relink(instLink, this, IN);
                moveToMetaSpace();
                /*
                unlinkAll();
                graph.removeComponent(this);
                */
            }
        }
        else {
            if (destNode != null) {
                instLink.link(destNode, PSConstants.OUT);
            }
        }
        if (nrInstances == maxInstances) {
            moveToMetaSpace();
            /*
            unlinkAll();
            graph.removeComponent(this);
            */
        }
        graph.addComponent(instLink);
    }
    
    public PSNode getNode(int direction) {
        if (isInMetaSpace) {
            if (direction == IN) {
                return metaSrcNode;
            }
            else if (direction == OUT) {
                return metaDestNode;
            }
        }
        else {
            if (direction == IN) {
                return srcNode;
            }
            else if (direction == OUT) {
                return destNode;
            }
        }
        return null;
    }
    
    public void instantiateTrace(PSComponent prevComp, int direction, InstantiationMode mode) throws PSNotInstantiableException, PSNotLinkableException {
        if (mark == MARK_JOIN || mark == MARK_DUPLICATE) {
            PSLink instLink = null;
            if (mode != INST_MODE_NO_INSTANTIATION) {
                // maxInstances = prevComp.maxInstances;
                instLink = (PSLink)duplicate();
                instLink.link(prevComp, direction);
                graph.addComponent(instLink);
            }
            setMark(MARK_NONE);
            PSNode nextNode = getNode(getInverseDir(direction));
            if (nextNode != null) {
                nextNode.instantiateTrace(instLink, direction, mode);
            }
            if (mode != INST_MODE_DEFAULT) {
                setMark(MARK_METASPACE);
                // moveToMetaSpace();
                /*
                unlinkAll();
                graph.removeComponent(this);
                */
            }
        }
    }
    
    public Object clone() {
        PSLink link = (PSLink)super.clone();
        link.srcNode = null;
        link.destNode = null;
        link.name = "Link_" + link.id;
        return link;
    }
   
    public String toString() {
        return super.toString() + (join ? ", join" : "") + (sequence ? ", sequence" : "") + (" [" + (srcNode != null ? srcNode.getName() : "") + " : " + (destNode != null ? destNode.getName() : "")+ "]");
    }

}