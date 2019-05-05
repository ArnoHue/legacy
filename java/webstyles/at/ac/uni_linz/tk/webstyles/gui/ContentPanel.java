package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

public class ContentPanel extends JPanel {
    
	public JComboBox contentType = new JComboBox();
	public JEditorPane contentText = new JEditorPane();
	public JTextField contentURI = new JTextField();
	
	protected JLabel contentTypeLabel = new JLabel();
	protected JLabel contentLabel = new JLabel();
	protected JLabel contentURILabel = new JLabel();
	
	protected JButton edit = new JButton();
	
	protected TitledBorder contentBorder = new TitledBorder("");
	protected JScrollPane scrollPane = new JScrollPane();
    
    public ContentPanel() {

		contentBorder.setTitle("Content");
		contentBorder.setTitleColor(Color.black);

		setBorder(contentBorder);
		setLayout(new GridBagLayout());

		contentTypeLabel.setText("Content Type:");
		contentTypeLabel.setForeground(Color.black);
		add(contentTypeLabel, new XGridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		add(contentType, new XGridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		contentURILabel.setText("Content URI:");
		contentURILabel.setForeground(Color.black);
		add(contentURILabel, new XGridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		add(contentURI, new XGridBagConstraints(1,1,1,1,1.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));

		contentLabel.setText("Content:");
		contentLabel.setForeground(Color.black);
		add(contentLabel, new XGridBagConstraints(0,2,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
        
        contentText.setAutoscrolls(true);
        
        edit.setText("Edit");
        
        contentText.setContentType("text/html"); 
        contentText.setEditable(false);

		scrollPane.setOpaque(true);
		scrollPane.getViewport().add(contentText);

		add(scrollPane, new XGridBagConstraints(1,2,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,50));
		add(edit, new XGridBagConstraints(1,3));
        
    }

}