package at.ac.uni_linz.tk.webstyles.action;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import at.ac.uni_linz.tk.webstyles.*;


public class PrintAction extends AbstractAction {

    private static PrintAction action = new PrintAction();

    public static Action getAction() {
        return action;
    }

    private PrintAction() {
        super("Print", new ImageIcon("images/print.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        PrintJob job = Toolkit.getDefaultToolkit().getPrintJob(new Frame(), "WebStyles Graph", null);

        if (job != null) {
            Graphics graphics = job.getGraphics();

            if (graphics != null) {
                WebStyles app =  WebStyles.getApplication();
                app.getGraphView().printAll(graphics);
                graphics.dispose();
            }
            job.end();

        }
    }

}