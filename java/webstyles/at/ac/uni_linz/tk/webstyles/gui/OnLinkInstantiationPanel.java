package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class OnLinkInstantiationPanel extends JPanel {
    
	JCheckBox join = new JCheckBox();
	JCheckBox sequence = new JCheckBox();
	TitledBorder onInstantiationBorder = new TitledBorder("");
    
    public OnLinkInstantiationPanel() {
		onInstantiationBorder.setTitle("On Instantiation");
		onInstantiationBorder.setTitleColor(Color.black);
		
		setBorder(onInstantiationBorder);
		setLayout(new GridLayout(3,1,5,5));

		join.setText("Join");
		add(join);
		sequence.setText("Sequence");
		add(sequence);
    }
    
}