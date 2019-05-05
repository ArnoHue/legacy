package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class InstantiationPanel extends JPanel {
	
	public JLabel minInstancesLabel = new JLabel();
	public JLabel maxInstancesLabel = new JLabel();
	public JLabel nrInstancesLabel = new JLabel();
	public JLabel indInstancesLabel = new JLabel();
	
	public JTextField minInstances = new JTextField();
	public JTextField maxInstances = new JTextField();
	public JTextField nrInstances = new JTextField();
	public JTextField indInstances = new JTextField();

	protected TitledBorder instatiationBorder = new TitledBorder("");
    
    public InstantiationPanel() {

		instatiationBorder.setTitle("Instantiation");
		instatiationBorder.setTitleColor(Color.black);

		setBorder(instatiationBorder);
		setLayout(new GridBagLayout());

		minInstancesLabel.setText("Min. Instances:");
		add(minInstancesLabel, new XGridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		minInstancesLabel.setForeground(Color.black);

		add(minInstances, new XGridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

		add(maxInstances, new XGridBagConstraints(1,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

		maxInstancesLabel.setText("Max. Instances:");
		add(maxInstancesLabel, new XGridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		maxInstancesLabel.setForeground(Color.black);

		nrInstancesLabel.setText("Nr. of Instances:");
		add(nrInstancesLabel, new XGridBagConstraints(0,2,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		nrInstancesLabel.setForeground(Color.black);

		indInstancesLabel.setText("Instance Index:");
		add(indInstancesLabel, new XGridBagConstraints(0,3,1,1,0.0,1.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		indInstancesLabel.setForeground(Color.black);

		add(nrInstances, new XGridBagConstraints(1,2,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

		add(indInstances, new XGridBagConstraints(1,3,1,1,0.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

    }
    
}