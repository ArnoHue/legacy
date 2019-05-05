package at.ac.uni_linz.tk.webstyles;

public class PSLinkController extends PSComponentController {
      
    public PSLinkController(PSComponent model, PSGraphController graphController) {
        super(model, graphController);
    }
    
    protected int getViewId() {
        return PSViewFactory.ID_LINK;
    }
    
    public PSLink getModel() {
        return (PSLink)getModelInternal();
    }
    
    public PSLinkView getView() {
        return (PSLinkView)getViewInternal();
    }

}