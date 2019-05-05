package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class NavigationPanel extends JPanel implements MouseListener, ActionListener, ListSelectionListener {
    
	public JTextArea forwRules = new JTextArea();
	public JTextArea bckwRules = new JTextArea();
	
	protected JLabel forwRulesLabel = new JLabel();
	protected JLabel bkwRulesLabel = new JLabel();
	protected TitledBorder navigationBorder = new TitledBorder("");
	protected JScrollPane scrollPaneForwRules = new JScrollPane();
	protected JScrollPane scrollPaneBckwRules = new JScrollPane();
	
	protected JList apiList = new JList();
	protected JScrollPane apiScroller = new JScrollPane();
	protected JButton insert = new JButton();
	
    public NavigationPanel() {

		navigationBorder.setTitle("Navigation");
		navigationBorder.setTitleColor(Color.black);

		setBorder(navigationBorder);
		setLayout(new GridBagLayout());

		forwRulesLabel.setText("Forw. Rules:");
		forwRulesLabel.setForeground(Color.black);
		add(forwRulesLabel, new XGridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		bkwRulesLabel.setText("Backw. Rules:");
		bkwRulesLabel.setForeground(Color.black);
		// add(bkwRulesLabel, new XGridBagConstraints(0,1,1,1,0.0,1.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
        
        forwRules.setAutoscrolls(true);
        forwRules.setLineWrap(true);
        forwRules.setRows(3);
		scrollPaneForwRules.setOpaque(true);
		scrollPaneForwRules.getViewport().add(forwRules);
		add(scrollPaneForwRules, new XGridBagConstraints(1,0,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));

		apiScroller.getViewport().add(apiList);
		add(apiScroller, new XGridBagConstraints(2,0,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		
		insert.setText("Insert");
		add(insert, new XGridBagConstraints(2,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
        
        bckwRules.setAutoscrolls(true);
        bckwRules.setLineWrap(true);
        bckwRules.setRows(3);
		scrollPaneBckwRules.setOpaque(true);
		scrollPaneBckwRules.getViewport().add(bckwRules);
		// add(scrollPaneBckwRules, new XGridBagConstraints(1,1,1,1,1.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
        
        apiList.addMouseListener(this);
        apiList.addListSelectionListener(this);
        insert.addActionListener(this);   
    }
    
    public void updateEnabledStates() {
        insert.setEnabled(apiList.getSelectedValue() != null);
    }
    
    protected void insert() {
        forwRules.insert(apiList.getSelectedValue().toString(), forwRules.getCaretPosition());
    }
    
    public void valueChanged(ListSelectionEvent e) {
        updateEnabledStates();
    }
    
	public void actionPerformed(ActionEvent evt) {
	    if (evt.getSource() == insert) {
	        insert();
	    }
    }
    
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            insert();
        }
    }
    
    public void mouseReleased(MouseEvent evt) {
    }
    
    public void mouseExited(MouseEvent evt) {
    }
    
    public void mouseEntered(MouseEvent evt) {
    }
    
    public void mousePressed(MouseEvent evt) {
    }
    
}