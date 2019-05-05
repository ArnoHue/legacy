package at.ac.uni_linz.tk.webstyles;

public class PSNotLinkableException extends PSException {
    
    public static final PSExceptionReason REASON_NOT_COMPATIBLE = new PSExceptionReason(0, "Not linkable", "Not compatible");
    public static final PSExceptionReason REASON_ALREADY_LINKED = new PSExceptionReason(1, "Not linkable", "Already linked");
    public static final PSExceptionReason REASON_ALREADY_LINKED_WITH_EACH_OTHER = new PSExceptionReason(2, "Not linkable", "Already linked with each other");
    
    public PSNotLinkableException(PSExceptionReason reason) {
        this(reason, null, null);
    }
    
    public PSNotLinkableException(PSExceptionReason reason, PSComponent comp1, PSComponent comp2) {
        super(reason, comp1 + " -> " + comp2);
    }
    
}