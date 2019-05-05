package at.ac.uni_linz.tk.webstyles;

public abstract class PSViewFactory implements PSConstants {
    
    public static final int MODE_DEFAULT = 0;
    
    public static final int ID_GRAPH = 0;
    public static final int ID_NODE = 1;
    public static final int ID_LINK = 2;
    public static final int ID_NESTEDGRAPH_NODE = 3;
    
    private static int mode = MODE_DEFAULT;
    
    public static void init(int modeParam) {
        mode = modeParam;
    }
    
    public static PSView createView(PSController controller, int id) {
        switch (mode) {
            case MODE_DEFAULT:
                if (id == ID_GRAPH) {
                    return new PSGraphView((PSGraphController)controller);
                }
                else if (id == ID_NODE) {
                    return new PSNodeView((PSNodeController)controller);
                }
                else if (id == ID_LINK) {
                    return new PSLinkView((PSLinkController)controller);
                }
                else if (id == ID_NESTEDGRAPH_NODE) {
                    return new PSNestedGraphNodeView((PSNodeController)controller);
                }
        }
        return null;
    }

}