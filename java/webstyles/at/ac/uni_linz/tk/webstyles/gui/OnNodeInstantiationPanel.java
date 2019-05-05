package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class OnNodeInstantiationPanel extends JPanel {
    
	public JCheckBox autocreate = new JCheckBox();
	public JCheckBox keepAggregate = new JCheckBox();
	public JCheckBox create = new JCheckBox();
	TitledBorder titledBorder = new TitledBorder("");
    
    public OnNodeInstantiationPanel() {

		titledBorder.setTitle("On Instantiation");
		titledBorder.setTitleColor(Color.black);

		autocreate.setText("Autocreate");
		add(autocreate);

		keepAggregate.setText("Keep aggregate");
		add(keepAggregate);

		create.setText("Create");
		add(create);

		setBorder(titledBorder);
		setLayout(new GridLayout(3,1,5,5));
    }
    
}