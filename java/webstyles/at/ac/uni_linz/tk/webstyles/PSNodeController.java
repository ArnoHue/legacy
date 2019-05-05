package at.ac.uni_linz.tk.webstyles;

public class PSNodeController extends PSComponentController {
      
    public PSNodeController(PSComponent model, PSGraphController graphController) {
        super(model, graphController);
    }
    
    protected int getViewId() {
        return PSViewFactory.ID_NODE;
    }
    
    public PSNode getModel() {
        return (PSNode)getModelInternal();
    }
    
    public PSNodeView getView() {
        return (PSNodeView)getViewInternal();
    }

}