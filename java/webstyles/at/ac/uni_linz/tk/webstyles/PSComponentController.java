package at.ac.uni_linz.tk.webstyles;

public abstract class PSComponentController extends PSController {
    
    protected PSGraphController graphController;
      
    public PSComponentController(PSComponent model, PSGraphController graphController) {
        super(model);
        this.graphController = graphController;
    }
    
    public PSComponentView getView(PSComponent model) {
        return graphController.getComponentView(model);
    }
    
    public PSGraphController getGraphController() {
        return graphController;
    }
    
    public String[] getPropertyChanges() {
        return new String[] { PROP_INSTANTIATION_NOTIFY };
    }

}