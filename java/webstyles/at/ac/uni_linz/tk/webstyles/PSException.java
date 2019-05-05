package at.ac.uni_linz.tk.webstyles;

import javax.swing.*;


public class PSException extends Exception {
    
    public PSExceptionReason reason;
    public String details;
        
    public PSException(PSExceptionReason reason, String details) {
        super(reason.msg);
        this.reason = reason;
        this.details = details;
    }
    
    public void display() {
        printStackTrace();
        JOptionPane.showMessageDialog(WebStyles.getApplication().getFrame(), reason.msg + "\n" + details, reason.title, JOptionPane.ERROR_MESSAGE, null);
    }
}