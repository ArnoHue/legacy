package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;
import at.ac.uni_linz.tk.webstyles.xml.*;


public abstract class PropertyDialog extends CenteredDialog implements ActionListener {
    
    protected PSComponent comp;
    
	protected JPanel mainPanel = new JPanel();
	protected JPanel titlePanel = new JPanel();
	protected JLabel title = new JLabel();
	protected JPanel buttonPanel = new JPanel();
	protected JButton ok = new JButton();
	protected JButton cancel = new JButton();
	
    public abstract String getTitleString();
    
    public abstract void updateModel();
    
    public abstract void updateView();
    
	public PropertyDialog() {
	    super(WebStyles.getApplication().getFrame());
		setTitle(getTitleString());
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(5,5));
		setSize(600, 720);
		setVisible(false);
		
		mainPanel.setLayout(new GridBagLayout());
		getContentPane().add(BorderLayout.CENTER, mainPanel);
		
		titlePanel.setLayout(new GridBagLayout());
		getContentPane().add(BorderLayout.NORTH, titlePanel);

		title.setText(getTitleString());
		titlePanel.add(title, new XGridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		title.setForeground(Color.black);
		title.setFont(new Font("Dialog", Font.BOLD, 20));

		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
		getContentPane().add(BorderLayout.SOUTH, buttonPanel);

		ok.setText("Ok");
		buttonPanel.add(ok);

		cancel.setText("Cancel");
		buttonPanel.add(cancel);
	
		ok.addActionListener(this);
		cancel.addActionListener(this);
		
		moveToCenter();
	}

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok || e.getSource() == cancel) {
            if (e.getSource() == ok) {
                updateModel();
            }
            setVisible(false);
		    WebStyles.getApplication().getGraphView().repaint();
        }
    }
    
    public void setComponent(PSComponent comp) {
	    this.comp = comp;
	    updateView();
    }

}