package at.ac.uni_linz.tk.webstyles.gui;

import at.ac.uni_linz.tk.webstyles.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class PropertiesPanel extends JPanel implements ActionListener, ListSelectionListener {
	
	public JButton add = new JButton();
	public JButton remove = new JButton();
    public PropertyTableModel tableModel;
	public JTable table = new JTable();
	
	protected JScrollPane scrollPane = new JScrollPane();
	protected TitledBorder propertiesBorder = new TitledBorder("");
	protected ImageIcon addIcon = new ImageIcon("images/additem.gif");
	protected ImageIcon removeIcon = new ImageIcon("images/delete.gif");
    
    public PropertiesPanel() {

		propertiesBorder.setTitle("Properties");
		propertiesBorder.setTitleColor(Color.black);
		
		tableModel = new PropertyTableModel();
		table.setModel(tableModel);
		
		setBorder(propertiesBorder);
		setLayout(new GridBagLayout());

		add.setText("Add");
		add(add, new XGridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		remove.setText("Remove");
		add(remove, new XGridBagConstraints(1,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		add.setIcon(addIcon);
		remove.setIcon(removeIcon);

		scrollPane.setOpaque(true);
		add(scrollPane, new XGridBagConstraints(0,0,2,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));

		table.setGridColor(new Color(153,153,153));
		scrollPane.getViewport().add(table);
		add.addActionListener(this);
		remove.addActionListener(this);
		
		table.getSelectionModel().addListSelectionListener(this);
        updateEnabledStates();
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            tableModel.addRow(new Object[] { "", "" });
        }
        else if (e.getSource() == remove && table.getSelectedRow() != -1) {
            tableModel.removeRow(table.getSelectedRow());
        }
        updateEnabledStates();
    }
    
    public void valueChanged(ListSelectionEvent e) {
        updateEnabledStates();
    }
    
    public void updateEnabledStates() {
        remove.setEnabled(table.getSelectedRow() != -1);
    }
    
}