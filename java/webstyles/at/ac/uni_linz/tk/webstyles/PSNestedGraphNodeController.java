package at.ac.uni_linz.tk.webstyles;

public class PSNestedGraphNodeController extends PSNodeController {
      
    public PSNestedGraphNodeController(PSComponent model, PSGraphController graphController) {
        super(model, graphController);
    }
    
    protected int getViewId() {
        return PSViewFactory.ID_NESTEDGRAPH_NODE;
    }

}