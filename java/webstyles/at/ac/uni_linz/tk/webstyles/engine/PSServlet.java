package at.ac.uni_linz.tk.webstyles.engine;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import at.ac.uni_linz.tk.webstyles.*;

public abstract class PSServlet extends HttpServlet {
    
    public static final String SESSION_ATTR_VISITED_NODES = "VISITED_NODES";
    public static final String SESSION_ATTR_TRAVERSED_LINKS = "TRAVERSED_LINKS";
    public static final String SESSION_ATTR_GRAPH = "GRAPH";
    public static final String SESSION_ATTR_PREVIOUS_VISITED_NODE = "PREVIOUS_VISITED_NODES";
    
    protected String name;
    protected String graphFileName;
    
    protected PSGraph graph;
    
    public PSServlet(String nameParam, String graphFileNameParam) {
        name = nameParam;
        graphFileName = graphFileNameParam;
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        
        res.setContentType("text/html");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(res.getOutputStream()));
        String content = getParsedContent(session);
        writer.write(content);
        writer.flush();
        
        setTraversed(session, getLinkName(session, getPreviousVisitedNodeName(session), name));
        setVisited(session, name);
        setPreviousVisitedNodeName(session, name);
    }
    
    public PSGraph getGraph(HttpSession session) {
        PSGraph graph = (PSGraph)session.getAttribute(SESSION_ATTR_GRAPH);
        if (graph == null) {
            try {
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getRealPath() + graphFileName)));
                graph = ((PSGraphController)is.readObject()).getModel();
                is.close();
                session.setAttribute(SESSION_ATTR_GRAPH, graph);
            }
            catch (Exception excpt) {
                log("Exception in PSServlet.getGraph", excpt);
            }
        }
        return graph;
    }
    
    public HashSet getVisitedNodes(HttpSession session) {
        HashSet visitedNodes = (HashSet)session.getAttribute(SESSION_ATTR_VISITED_NODES);
        if (visitedNodes == null) {
            visitedNodes = new HashSet();
            session.setAttribute(SESSION_ATTR_VISITED_NODES, visitedNodes);
        }
        return visitedNodes;
    }
    
    public HashSet getTraversedLinks(HttpSession session) {
        HashSet traversedLinks = (HashSet)session.getAttribute(SESSION_ATTR_TRAVERSED_LINKS);
        if (traversedLinks == null) {
            traversedLinks = new HashSet();
            session.setAttribute(SESSION_ATTR_TRAVERSED_LINKS, traversedLinks);
        }
        return traversedLinks;
    }
    
    public boolean hasBeenVisited(HttpSession session, String nodeName) {
        HashSet visitedNodes = getVisitedNodes(session);
        log("hasBeenVisited(" + nodeName + ") = " + visitedNodes.contains(nodeName));
        return visitedNodes.contains(nodeName);
    }
    
    public boolean hasBeenTraversed(HttpSession session, String linkName) {
        HashSet traversedLinks = getTraversedLinks(session);
        return traversedLinks.contains(linkName);
    }
    
    public void setVisited(HttpSession session, String nodeName) {
        HashSet visitedNodes = getVisitedNodes(session);
        visitedNodes.add(nodeName);
    }
    
    public void setTraversed(HttpSession session, String linkName) {
        HashSet traversedLinks = getTraversedLinks(session);
        traversedLinks.add(linkName);
        log("setTraversed: " + linkName);
    }
    
    public String getPreviousVisitedNodeName(HttpSession session) {
        return (String)session.getAttribute(SESSION_ATTR_PREVIOUS_VISITED_NODE);
    }
    
    public void setPreviousVisitedNodeName(HttpSession session, String nodeName) {
        session.setAttribute(SESSION_ATTR_PREVIOUS_VISITED_NODE, nodeName);
    }
    
    public String getLinkName(HttpSession session, String fromNodeName, String toNodeName) {
        PSNode fromNode = getNode(session, fromNodeName);
        PSNode toNode = getNode(session, toNodeName);
        if (fromNode != null && toNode != null) {
            PSLink link = fromNode.getLink(toNode, PSConstants.OUT);
            if (link != null) {
                return link.getName();
            }
        }
        return null;
    }
    
    public String getRealPath() {
        return getServletContext().getRealPath("");
    }
    
    public String getTemplate() {
        String template = "";
        String line;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getRealPath() + name + ".tpl")));
            while ((line = reader.readLine()) != null) {
                template += line;
            }
            reader.close();
        }
        catch (Exception excpt) {
            log("Exception in PSServlet.getParsedTemplate", excpt);
        }
        return template;
    }
    
    public String getParsedContent(HttpSession session) {
        String content = "";
        try {
            String template = getTemplate();
        
            HTMLEditorKit htmlKit = new HTMLEditorKit();
            HTMLDocument doc = new HTMLDocument();
            htmlKit.read(new StringReader(template), doc, 0);
            HTMLDocument.Iterator it = doc.getIterator(HTML.Tag.A);
            
            while (it.isValid()) {
                if (!isLinkEnabled(session, getLinkName(session, name, it.getAttributes().getAttribute(HTML.Attribute.HREF).toString()))) {
                    SimpleAttributeSet attr = new SimpleAttributeSet(doc.getCharacterElement(it.getStartOffset()).getAttributes());
                    attr.removeAttribute(HTML.Tag.A);
                    doc.setCharacterAttributes(it.getStartOffset(), it.getEndOffset() - it.getStartOffset() + 1, attr, true); 
                }
                it.next();
            }
            StringWriter writer = new StringWriter();
            new HTMLEditorKit().write(writer, doc, 0, doc.getLength() - 1);
            content = writer.getBuffer().toString();
        }
        catch (Exception excpt) {
            log("Exception in PSServlet.getParsedContent", excpt);
        }
        return content;
    }
    
    protected PSNode getNode(HttpSession session, String nodeName) {
        PSGraph graph = getGraph(session);
        for (Enumeration enum = graph.getNodes(); enum.hasMoreElements(); ) {
            PSNode node = (PSNode)enum.nextElement();
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }
    
    protected PSLink getLink(HttpSession session, String linkName) {
        PSGraph graph = getGraph(session);
        for (Enumeration enum = graph.getLinks(); enum.hasMoreElements(); ) {
            PSLink link = (PSLink)enum.nextElement();
            if (link.getName().equals(linkName)) {
                return link;
            }
        }
        return null;
    }
    
    public boolean isLinkEnabled(HttpSession session, String linkName) {
        log("isLinkEnabled: " + linkName);
        PSLink link = getLink(session, linkName);
        log("link =  " + link);
        return link == null || isLinkEnabledInternal(session, linkName);
    }
    
    protected boolean isLinkEnabledInternal(HttpSession session, String linkName) {
        return true;
    }
      
    public Hashtable getProperties(HttpSession session, String nodeName) {
        PSNode node = getNode(session, nodeName);
        if (node != null) {
            return node.getProperties();
        }
        else {
            return null;
        }
    }
    
    public String getProperty(HttpSession session, String propertyName) {
        return getProperty(session, name, propertyName);
    }
    
    public String getProperty(HttpSession session, String nodeName, String propertyName) {
        PSNode node = getNode(session, nodeName);
        if (node != null) {
            return node.getProperty(propertyName);
        }
        else {
            return null;
        }
    }
    
}