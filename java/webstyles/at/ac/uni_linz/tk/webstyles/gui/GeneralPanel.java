package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class GeneralPanel extends JPanel {
	
	public JTextField name = new JTextField();
	public JRadioButton pseudoGeneric = new JRadioButton();
	public JRadioButton generic = new JRadioButton();
	public JRadioButton instance = new JRadioButton();
	public JComboBox type = new JComboBox();
	public JPanel kind = new JPanel();
	public JLabel typeLabel = new JLabel();
    
	protected TitledBorder kindBorder = new TitledBorder("");
	protected TitledBorder generalBorder = new TitledBorder("");
	protected JLabel nameLabel = new JLabel();
	protected ButtonGroup kindGroup = new ButtonGroup();
    
    public GeneralPanel() {
		generalBorder.setTitle("General");
		generalBorder.setTitleColor(Color.black);
		
		kindBorder.setTitle("Kind");
		kindBorder.setTitleColor(Color.black);
        
		setBorder(generalBorder);
		setLayout(new GridBagLayout());
		add(name, new XGridBagConstraints(1,0,1,1,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL));

		nameLabel.setText("Name:");
		add(nameLabel, new XGridBagConstraints(0,0,1,1,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE));
		nameLabel.setForeground(Color.black);

		typeLabel.setText("Type:");
		add(typeLabel, new XGridBagConstraints(0,1,1,1,GridBagConstraints.EAST,GridBagConstraints.NONE));
		typeLabel.setForeground(Color.black);

		add(type, new XGridBagConstraints(1,1,1,1,1.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL));

		generic.setText("Generic");
		pseudoGeneric.setText("Pseudo");

		instance.setText("Instance");
		instance.setBorder(kindBorder);
		
		kind.setBorder(kindBorder);
		kind.setLayout(new GridLayout(1,3,0,0));
		kind.add(generic);
		kind.add(pseudoGeneric);
		kind.add(instance);
		add(kind, new XGridBagConstraints(0,2,2,1));

    }
    
}