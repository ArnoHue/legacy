package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;

public class PSStatusBar extends JPanel {
    
	protected JLabel statusLabel = new JLabel();
	
	protected JLabel nodesLabel = new JLabel();
	protected JLabel linksLabel = new JLabel();
	protected JLabel selLabel = new JLabel();
	
	protected JLabel xLabel = new JLabel();
	protected JLabel yLabel = new JLabel();
	protected JLabel widthLabel = new JLabel();
	protected JLabel heightLabel = new JLabel();
	
	protected BevelBorder border = new SoftBevelBorder(BevelBorder.LOWERED);
	
	protected static final DecimalFormat DEC_FORMAT = new DecimalFormat("000");
    
    public PSStatusBar() {
		setLayout(new GridBagLayout());
		statusLabel.setForeground(Color.gray);
		statusLabel.setBorder(border);
		add(statusLabel, new XGridBagConstraints(0,0,1,1,1.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		nodesLabel.setForeground(Color.gray);
		nodesLabel.setBorder(border);
		add(nodesLabel, new XGridBagConstraints(1,0,1,1,0.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 30, 0));
		
		linksLabel.setForeground(Color.gray);
		linksLabel.setBorder(border);
		add(linksLabel, new XGridBagConstraints(2,0,1,1,0.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 30, 0));
		
		selLabel.setForeground(Color.gray);
		selLabel.setBorder(border);
		add(selLabel, new XGridBagConstraints(3,0,1,1,0.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 30, 0));
		
		xLabel.setForeground(Color.gray);
		xLabel.setBorder(border);
		add(xLabel, new XGridBagConstraints(4,0,1,1,0.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		yLabel.setForeground(Color.gray);
		yLabel.setBorder(border);
		add(yLabel, new XGridBagConstraints(5,0,1,1,0.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		widthLabel.setForeground(Color.gray);
		widthLabel.setBorder(border);
		add(widthLabel, new XGridBagConstraints(6,0,1,1,0.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		heightLabel.setForeground(Color.gray);
		heightLabel.setBorder(border);
		add(heightLabel, new XGridBagConstraints(7,0,1,1,0.0,0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    
        setStatus("");
        setRectangle(new Rectangle());
        setNrOfComponents(0, 0, 0);
    }
    
    public void setStatus(String status) {
		statusLabel.setText(status);
	}
    
    public void setRectangle(Rectangle rec) {
        if (rec == null) {
            rec = new Rectangle();
        }
		xLabel.setText(DEC_FORMAT.format(rec.x));
		yLabel.setText(DEC_FORMAT.format(rec.y));
		widthLabel.setText(DEC_FORMAT.format(rec.width));
		heightLabel.setText(DEC_FORMAT.format(rec.height));
	}
    
    public void setNrOfComponents(int nrOfNodes, int nrOfLinks, int nrOfSelected) {
		nodesLabel.setText(nrOfNodes + " Nodes");
		linksLabel.setText(nrOfLinks + " Links");
		selLabel.setText(nrOfSelected + " Selected");
	}
        
}