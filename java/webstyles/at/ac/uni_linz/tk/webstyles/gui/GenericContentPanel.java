package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class GenericContentPanel extends JPanel {
    
	public JButton edit = new JButton();
	public JList list = new JList();

	protected JScrollPane scrollPane = new JScrollPane();
    protected TitledBorder titledBorder = new TitledBorder("");
    
    public GenericContentPanel() {
		titledBorder.setTitle("Generic Content");
		titledBorder.setTitleColor(Color.black);

		edit.setText("Edit");
		setBorder(titledBorder);
		setLayout(new GridBagLayout());

		add(edit, new XGridBagConstraints(1,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		scrollPane.setOpaque(true);
		add(scrollPane, new XGridBagConstraints(0,0,2,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));

		scrollPane.getViewport().add(list);

		add(scrollPane, new XGridBagConstraints(0,0,2,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
    }
    
}