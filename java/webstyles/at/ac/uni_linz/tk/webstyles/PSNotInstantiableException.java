package at.ac.uni_linz.tk.webstyles;

public class PSNotInstantiableException extends PSException {
    
    public static final PSExceptionReason REASON_NOT_GENERIC = new PSExceptionReason(0, "Not instantiable", "Component is not generic");
    public static final PSExceptionReason REASON_MAX_INSTANCES = new PSExceptionReason(1, "Not instantiable", "Maximal number of instances reached.");
    public static final PSExceptionReason REASON_PSEUDOGENERIC_TO_PSEUDOGENERIC = new PSExceptionReason(2, "Not instantiable", "Pseudogeneric component can't be instantiated to another pseudogeneric component.");
    public static final PSExceptionReason REASON_NESTEDGRAPHNODE = new PSExceptionReason(3, "Not instantiable", "Nested Graph Node can't be instantiated.");
    
    public PSNotInstantiableException(PSExceptionReason reason) {
        this(reason, null);
    }
    
    public PSNotInstantiableException(PSExceptionReason reason, PSComponent comp) {
        super(reason, comp == null ? "null" : comp.toString());
    }
    
}