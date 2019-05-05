package at.ac.uni_linz.tk.webstyles.xml;

import at.ac.uni_linz.tk.webstyles.*;
import java.util.*;

public class XMLLink {

    PSLink link;

    public XMLLink() {
    }

    public XMLLink(PSLink link) {
        this.link = link;
    }

    public int getId() {
        return link.id;
    }

    public String getName() {
        return link.getName();
    }

    public Vector getProperties() {
        return new XMLProperties(link.getProperties());
    }

    public int getSourceNodeId() {
        PSNode node = link.getNode(PSConstants.IN);
        return node != null ? node.id : -1;
    }

    public int getTargetNodeId() {
        PSNode node = link.getNode(PSConstants.OUT);
        return node != null ? node.id : -1;
    }

}