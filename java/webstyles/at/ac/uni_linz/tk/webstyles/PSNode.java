package at.ac.uni_linz.tk.webstyles;

import java.util.*;
import java.util.Enumeration;
import java.awt.*;
import java.awt.geom.*;

import at.ac.uni_linz.tk.webstyles.generic.*;

public class PSNode extends PSComponent {
    
    static final long serialVersionUID = -159528163691870820L;
    
    public ContentTemplate content;
    public String contentText;
    public String contentURI;
      
    protected PSLinkVector inLink, outLink;
    protected PSLinkVector metaInLink, metaOutLink;
    
    public PSNode() {
        name = "Node_" + id;
        type = TYPE_MANDATORY;
        inLink = new PSLinkVector();
        outLink = new PSLinkVector();
        metaInLink = new PSLinkVector();
        metaOutLink = new PSLinkVector();
    }
        
    public void unlinkAll() {
        for (int i = getNrOfLinkedComponents(PSConstants.IN) - 1; i >= 0; i--) {
            unlink(getLinkAt(PSConstants.IN, i), PSConstants.IN);
        }
        for (int i = getNrOfLinkedComponents(PSConstants.OUT) - 1; i >= 0; i--) {
            unlink(getLinkAt(PSConstants.OUT, i), PSConstants.OUT);
        }
    }
        
    public void moveLinksToMetaSpace() {
        for (int i = getNrOfLinkedComponents(PSConstants.IN) - 1; i >= 0; i--) {
            moveLinkToMetaSpace(getLinkAt(PSConstants.IN, i), PSConstants.IN);
        }
        for (int i = getNrOfLinkedComponents(PSConstants.OUT) - 1; i >= 0; i--) {
            moveLinkToMetaSpace(getLinkAt(PSConstants.OUT, i), PSConstants.OUT);
        }
    }
    
    protected void moveLinkToMetaSpace(PSLink link, int dir) {
        switch(dir) {
            case IN:
                if (inLink.contains(link)) {
                    inLink.removeElement(link);
                    metaInLink.addElement(link);
                    if (link.isLinked(this, OUT)) {
                        link.moveLinkToMetaSpace(this, OUT);
                    }
                }
                break;
            case OUT:
                if (outLink.contains(link)) {
                    outLink.removeElement(link);
                    metaOutLink.addElement(link);
                    if (link.isLinked(this, IN)) {
                        link.moveLinkToMetaSpace(this, IN);
                    }
                }
                break;
        }
    }
    
    public boolean supportsSequenceLinks() {
        return type == TYPE_SEQUENCE || type == TYPE_OPTIONAL;
    }

    public void link(PSComponent linkParam, int dir) throws PSNotLinkableException {
        isLinkableWith(linkParam, dir);
        PSLink link = (PSLink)linkParam;
        switch(dir) {
            case IN:
                if (!inLink.contains(link)) {
                    inLink.addElement(link);
                }
                if (!link.isLinked(this, OUT)) {
                    link.link(this, OUT);
                }
                break;
            case OUT:
                if (!outLink.contains(link)) {
                    outLink.addElement(link);
                }
                if (!link.isLinked(this, IN)) {
                    link.link(this, IN);
                }
                break;
        }
    }
      
    public void unlink(PSComponent linkParam, int dir) {
        if (linkParam instanceof PSLink) {
            PSLink link = (PSLink)linkParam;
            switch(dir) {
                case IN:
                    if (inLink.contains(link)) {
                        inLink.removeElement(link);
                        if (link.isLinked(this, OUT)) {
                            link.unlink(this, OUT);
                        }
                    }
                    break;
                case OUT:
                    if (outLink.contains(link)) {
                        outLink.removeElement(link);
                        if (link.isLinked(this, IN)) {
                            link.unlink(this, IN);
                        }
                    }
                    break;
            }
        }
    }
    
    public void isLinkableWith(PSComponent comp, int dir) throws PSNotLinkableException{
        if (!(comp instanceof PSLink)) {
            throw new PSNotLinkableException(PSNotLinkableException.REASON_NOT_COMPATIBLE, dir == PSConstants.IN ? comp : this, dir == PSConstants.OUT ? comp : this);
        }
        else if (isLinked(comp, dir)) {
            throw new PSNotLinkableException(PSNotLinkableException.REASON_ALREADY_LINKED_WITH_EACH_OTHER, dir == PSConstants.IN ? comp : this, dir == PSConstants.OUT ? comp : this);
        }
        /*
        else if (comp.generic != generic) {
            PSLink link = (PSLink)comp;
            if ((!generic && !genericComp.isLinked(link, dir)) ||
                (generic && !isLinked(link.genericComp, dir))) {
                    throw new PSNotLinkableException(PSNotLinkableException.REASON_NOT_COMPATIBLE, dir == PSConstants.IN ? comp : this, dir == PSConstants.OUT ? comp : this);
            }
        }
        */
    }
      
    public Enumeration getLinkedComponents(int dir) {
        if (dir == PSConstants.IN) {
            return inLink.elements();
        }
        else if (dir == PSConstants.OUT) {
            return outLink.elements();
        }
        return null;
    }
      
    public Enumeration getLinkedComponents() {
        PSLinkVector vec = new PSLinkVector(inLink.size() + outLink.size());
        vec.addAll(inLink);
        vec.addAll(outLink);
        return vec.elements();
    }
        
    public int getNrOfLinkedComponents(int dir) {
        if (dir == IN) {
            return inLink.size();
        }
        else if (dir == OUT) {
            return outLink.size();
        }
        return 0;
    }
        
    public PSLink getLinkAt(int dir, int index) {
        if (dir == IN) {
            return inLink.elementAt(index);
        }
        else if (dir == OUT) {
            return outLink.elementAt(index);
        }
        return null;
    }
        
    public boolean isLinked(PSComponent link) {
        return link instanceof PSLink && (inLink.contains((PSLink)link) || outLink.contains((PSLink)link));
    }
      
    public boolean isLinked(PSComponent link, int dir) {
        return link instanceof PSLink && ((dir == IN && inLink.contains((PSLink)link)) || (dir == OUT && outLink.contains((PSLink)link)));
    }
      
    public Object clone() {
        PSNode node = (PSNode)super.clone();
        node.inLink = new PSLinkVector();
        node.outLink = new PSLinkVector();
        node.id = createId();
        node.name = "Node_" + node.id;
        return node;
    }
    
    public PSLink getLink(PSNode otherNode, int dir) {
        for (int i = 0; i < getNrOfLinkedComponents(dir); i++) {
            if (otherNode.isLinked(getLinkAt(dir, i), getInverseDir(dir))) {
                return getLinkAt(dir, i);
            }
        }
        return null;
    }
    
    public PSLink getSeqLink(int dir) {
        for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(dir); enum.hasMoreElements();) {
            PSLink link = enum.nextLink();
            if (link.isSequence()) {
                return link;
            }
        }
        return null;
    }
    
    public void relinkAfterSeqInstantiation(PSNode instNode, PSLink seqInLink, PSLink seqOutLink) throws PSNotInstantiableException, PSNotLinkableException {
        PSLink instLink;
        if (nrInstances < maxInstances) {
            // TODO: what to do if not generic?
            if (seqOutLink != null) {
                // TODO: cleanup this mess
                instLink = (PSLink)seqOutLink.clone();
                instLink.genericComp = seqOutLink;
                instLink.generic = false;
                instLink.pseudoGeneric = false;
                instLink.link(instNode, PSConstants.IN);
                if (seqOutLink.getNode(PSConstants.OUT) != null) {
                    instLink.link(seqOutLink.getNode(PSConstants.OUT), PSConstants.OUT);
                }
                graph.addComponent(instLink);
                seqOutLink.unlinkAll();
                seqOutLink.link(this, instNode);
            }
            else {
                PSLink link = new PSLink();
                link.sequence = true;
                graph.addComponent(link);
                link.link(this, instNode);
                /*
                instLink = (PSLink)link.instantiateInternal();
                instLink.link(this, instNode);
                graph.addComponent(instLink);
                */
            }
        }
        else {
            // TODO: what to do if not generic?
            if (seqInLink != null) {
                if (seqInLink.generic) {
                    instLink = (PSLink)seqInLink.instantiateInternal();
                    
                    if (seqInLink.getNode(PSConstants.IN) != null) {
                        instLink.link(seqInLink.getNode(PSConstants.IN), PSConstants.IN);
                    }
                    instLink.link(instNode, PSConstants.OUT);
                    graph.addComponent(instLink);
                    seqInLink.moveToMetaSpace();
                    /*
                    seqInLink.unlinkAll();
                    graph.removeComponent(seqInLink);
                    */
                }
                else {
                    seqInLink.relink(instNode, this, PSConstants.OUT);
                }
            }
            if (seqOutLink != null) {
                if (seqOutLink.generic) {
                    instLink = (PSLink)seqOutLink.instantiateInternal();
                    instLink.link(instNode, seqOutLink.getNode(PSConstants.OUT));
                    graph.addComponent(instLink);
                    seqOutLink.moveToMetaSpace();
                    /*
                    seqOutLink.unlinkAll();
                    graph.removeComponent(seqOutLink);
                    */
                }
                else {
                    seqOutLink.relink(this, instNode, PSConstants.IN);
                }
            }
            int dirs[] = { PSConstants.IN, PSConstants.OUT };
            for (int j = 0; j < dirs.length; j++) {
                int dir = dirs[j];
                for (int i = getNrOfLinkedComponents(dir) - 1; i >= 0; i--) {
                    PSLink link = getLinkAt(dir, i);
                    if (!link.generic) {
                        link.relink(instNode, this, getInverseDir(dir));
                    }
                }
            }
        }
    }
    
    public void dontInstantiate() throws PSNotLinkableException {
        PSLink seqInLink = getSeqLink(PSConstants.IN);
        PSLink seqOutLink = getSeqLink(PSConstants.OUT);
        if (seqOutLink != null && seqInLink != null) {
            PSNode prevNode = seqInLink.getNode(PSConstants.IN);
            if (prevNode != null) {
                seqOutLink.unlink(this, PSConstants.IN);
                seqOutLink.link(prevNode, PSConstants.IN);
            }
            
            seqInLink.moveToMetaSpace();
            /*
            seqInLink.unlinkAll();
            graph.removeComponent(seqInLink);
            */
            seqInLink.maxInstances = 1;
            seqOutLink.maxInstances = 1;
        }
        if (type == TYPE_SEQUENCE) {
            graph.markTrace(this);
            setMark(MARK_NONE);
            try {
                for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(IN); enum.hasMoreElements();) {
                    enum.nextLink().instantiateTrace(null, OUT, INST_MODE_NO_INSTANTIATION);
                }
                for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(OUT); enum.hasMoreElements();) {
                    enum.nextLink().instantiateTrace(null, IN, INST_MODE_NO_INSTANTIATION);
                }
            }
            catch (PSNotInstantiableException excpt) {
                excpt.printStackTrace();
            }
            /*
        if (type == TYPE_SEQUENCE && lastInstanceComp != null) {
            PSNode lastInstanceNode = (PSNode)lastInstanceComp;
            int dirs[] = { PSConstants.IN, PSConstants.OUT };
            for (int j = 0; j < dirs.length; j++) {
                int dir = dirs[j];
                for (int i = getNrOfLinkedComponents(dir) - 1; i >= 0; i--) {
                    PSLink link = getLinkAt(dir, i);
                    if (link.type != TYPE_SEQUENCE && !lastInstanceNode.isConnected(link.getNode(dir), dir)) {
                        link.relink(lastInstanceNode, this, getInverseDir(dir));
                    }
                    else {
                        link.unlinkAll();
                        graph.removeComponent(link);
                    }
                }
            }
            */
        }
        moveToMetaSpace();
        /*
        unlinkAll();
        graph.removeComponent(this);
        */
    }
    
    public boolean isConnected(PSNode node, int dir) {
        for (int i = getNrOfLinkedComponents(dir) - 1; i >= 0; i--) {
            PSLink link = getLinkAt(dir, i);
            if (link.isLinked(node, dir)) {
                return true;
            }
        }
        return false;
    }
    
    public void instantiate() throws PSNotInstantiableException, PSNotLinkableException {
        isInstantiable();
        PSLink seqInLink = getSeqLink(PSConstants.IN);
        PSLink seqOutLink = getSeqLink(PSConstants.OUT);
        
        PSNode instNode = (PSNode)instantiateInternal();
            
        graph.addComponent(instNode);
                
        if (type != TYPE_SEQUENCE || pseudoGeneric) {
            instantiateLinks(instNode, PSConstants.IN);
            instantiateLinks(instNode, PSConstants.OUT);
        }
        else {
            graph.markTrace(this);
            if (seqInLink != null) {
                seqInLink.setMark(MARK_NONE);
                seqInLink.maxInstances = maxInstances;
            }
            if (seqOutLink != null) {
                seqOutLink.setMark(MARK_NONE);
                seqOutLink.maxInstances = maxInstances;
            }
            setMark(MARK_NONE);
            for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(IN); enum.hasMoreElements();) {
                enum.nextLink().instantiateTrace(instNode, OUT, nrInstances == maxInstances ? INST_MODE_LAST_TRACE : INST_MODE_DEFAULT);
            }
            for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(OUT); enum.hasMoreElements();) {
                enum.nextLink().instantiateTrace(instNode, IN, nrInstances == maxInstances ? INST_MODE_LAST_TRACE : INST_MODE_DEFAULT);
            }
            seqInLink = getSeqLink(PSConstants.IN);
            seqOutLink = getSeqLink(PSConstants.OUT);
            relinkAfterSeqInstantiation(instNode, seqInLink, seqOutLink);
            graph.moveToMetaSpace();
        }
        if (nrInstances == maxInstances) {
            moveToMetaSpace();
            // graph.removeComponent(this);
        }
    }
    
    public void instantiateTrace(PSComponent prevComp, int direction, InstantiationMode mode) throws PSNotInstantiableException, PSNotLinkableException {
        if (mark == MARK_SEQUENCE || mark == MARK_DUPLICATE || mark == MARK_FAN) {
            PSNode instNode = null;
            if (mode != INST_MODE_NO_INSTANTIATION) {
                isInstantiable();
                if (mark == MARK_FAN) {
                    link(prevComp, direction);
                }
                else if (mark == MARK_DUPLICATE) {
                    maxInstances = prevComp.genericComp.maxInstances;
                    instNode = (PSNode)duplicate();
                    instNode.link(prevComp, direction);
                    graph.addComponent(instNode);
                }
                else if (mark == MARK_SEQUENCE) {
                    PSLink seqInLink = getSeqLink(PSConstants.IN);
                    PSLink seqOutLink = getSeqLink(PSConstants.OUT);
                    // maxInstances = prevComp.maxInstances;
                    if ((type == TYPE_SEQUENCE || type == TYPE_OPTIONAL) && prevComp != seqInLink && prevComp != seqOutLink) {
                        instNode = (PSNode)createPseudoGeneric();
                        instNode.minInstances = 1;
                        instNode.maxInstances = 1;
                    }
                    else {
                        instNode = (PSNode)duplicate();
                    }
                    
                    instNode.link(prevComp, direction);
                    graph.addComponent(instNode);
                    if (type == TYPE_SEQUENCE || type == TYPE_OPTIONAL) {
                        relinkAfterSeqInstantiation(instNode, seqInLink, seqOutLink);
                    }
                }
            }
            if (mark != MARK_FAN) {
                int oldMark = mark;
                setMark(MARK_NONE);
                for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(IN); enum.hasMoreElements();) {
                    enum.nextLink().instantiateTrace(instNode, OUT, mode);
                }
                for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(OUT); enum.hasMoreElements();) {
                    enum.nextLink().instantiateTrace(instNode, IN, mode);
                }
                if (mode != INST_MODE_DEFAULT && (type != TYPE_SEQUENCE || oldMark == MARK_DUPLICATE)) {
                    setMark(MARK_METASPACE);
                    // moveToMetaSpace();
                    /*
                    unlinkAll();
                    graph.removeComponent(this);
                    */
                }
            }
            else {
                setMark(MARK_NONE);
            }
        }
    }
    
    protected void setLinkInstantiationFlags(int dir) {
        PSLink link, genLink;
		for (PSLinkVector.PSLinkEnumeration e = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(dir); e.hasMoreElements(); )
		{
			e.nextLink().instMode = INST_INST;
		}

		for (PSLinkVector.PSLinkEnumeration e = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(dir); e.hasMoreElements(); )
		{
			link = e.nextLink();
			if (link.instMode != INST_IGNORE && link.pseudoGeneric)
			{
				// may be fan or optional
				genLink = (PSLink)link.getGenericRoot();
				if (genLink.getNode(PSConstants.OUT) == this)
				{
					// parent points to this node, so ignore it
					genLink.instMode = INST_IGNORE;	
				}
			} 
		}
    }
    
    protected void instantiateLinks(PSNode instNode, int direction) throws PSNotInstantiableException, PSNotLinkableException {
        PSLink genLink, instLink;
        setLinkInstantiationFlags(direction);
        PSLinkVector genLinks = new PSLinkVector(getNrOfLinkedComponents(direction));
        for (int i = 0; i < getNrOfLinkedComponents(direction); i++) {
            genLinks.addElement(getLinkAt(direction, i));
        }
        for (int i = 0; i < genLinks.size(); i++) {
            genLink = genLinks.elementAt(i);
            if (genLink.instMode != INST_IGNORE) {
                if (genLink.generic) {
                    if (direction == OUT) {
                        boolean isFanOrOptional = genLink.type == TYPE_FAN || genLink.type == TYPE_OPTIONAL;
                        if (isFanOrOptional) {
                            instLink = (PSLink)genLink.createPseudoGeneric();
                        }
                        else {
                            instLink = (PSLink)genLink.instantiateInternal();
                        }
                        
                        // TODO: general solution for the pseudo-gen. prob.
                        instLink.minInstances = genLink.minInstances;
                        instLink.maxInstances = genLink.maxInstances;
                        
                        instNode.link(instLink, OUT);
                        if (genLink.getNode(PSConstants.OUT) != null) {
                            instLink.link(genLink.getNode(PSConstants.OUT), OUT);
                        }
                        graph.addComponent(instLink);
                        if (isFanOrOptional || genLink.nrInstances == genLink.maxInstances) {
                            genLink.moveToMetaSpace();
                            /*
                            genLink.unlinkAll();
                            graph.removeComponent(genLink);
                            */
                        }
                                
                    }
                    else { //dir == IN
                        if (genLink.getType() != TYPE_FAN) {
                            instLink = (PSLink)genLink.instantiateInternal();
                            graph.addComponent(instLink);
                            instNode.link(instLink, IN);
                            if (genLink.getNode(PSConstants.IN) != null) {
                                instLink.link(genLink.getNode(PSConstants.IN), IN);
                            }
                            if (genLink.nrInstances == genLink.maxInstances) {
                                genLink.moveToMetaSpace();
                                /*
                                genLink.unlinkAll();
                                graph.removeComponent(genLink);
                                */
                            }
                        }
                        else {
                            genLink.relink(instNode, this, getInverseDir(direction));
                        }
                    }
                }
                else {
                    genLink.relink(instNode, this, getInverseDir(direction));
                }
            }
        }
    }
    
    public Vector getLinkedNodes(int dir) {
        Vector vec = new Vector();
        for (PSLinkVector.PSLinkEnumeration enum = (PSLinkVector.PSLinkEnumeration)getLinkedComponents(dir); enum.hasMoreElements();) {
            PSLink link = enum.nextLink();
            if (link.getNode(dir) != null) {
                vec.addElement(link.getNode(dir));
            }
        }
        return vec;
    }
    
}