package at.ac.uni_linz.tk.webstyles.generic;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.xml.*;
import at.ac.uni_linz.tk.webstyles.util.*;


public class MemoryPropertyDialog extends CenteredDialog implements ListSelectionListener, ActionListener, TableModelListener, KeyListener /* , FocusListener */ {

    private Memory memory;
    private MemoryPairsTableModel pairTableModel;
    
	JPanel mainPanel = new JPanel();

	JTextField name = new JTextField();
	JLabel nameLabel = new JLabel();

	JTextField nrOfPairs = new JTextField();
	JLabel nrOfPairsLabel = new JLabel();

	JTextField width = new JTextField();
	JLabel widthLabel = new JLabel();

	JTextField height = new JTextField();
	JLabel heightLabel = new JLabel();

	JLabel validLabel = new JLabel();

	JTable pairTable = new JTable();
	JPanel pairPanel = new JPanel();
	JPanel generalPanel = new JPanel();
	JButton add = new JButton();
	JButton remove = new JButton();
	JScrollPane pairTableScrollPane = new JScrollPane();

	JPanel titlePanel = new JPanel();
	JLabel title = new JLabel();
	JPanel buttonPanel = new JPanel();
	JButton ok = new JButton();
	JButton cancel = new JButton();
	TitledBorder generalBorder = new TitledBorder("");
	TitledBorder pairBorder = new TitledBorder("");
	ImageIcon addIcon = new ImageIcon("images/additem.gif");
	ImageIcon removeIcon = new ImageIcon("images/delete.gif");

	public MemoryPropertyDialog() {
	    super(WebStyles.getApplication().getFrame());
		setTitle("Memory Properties");
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(5,5));
		setSize(600, 320);
		mainPanel.setLayout(new GridBagLayout());
		getContentPane().add(BorderLayout.CENTER, mainPanel);

		generalPanel.setBorder(generalBorder);
		generalPanel.setLayout(new GridBagLayout());
		mainPanel.add(generalPanel, new XGridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));


		nameLabel.setForeground(Color.black);
		nameLabel.setText("Name:");
		generalPanel.add(nameLabel, new XGridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		generalPanel.add(name, new XGridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

		nrOfPairsLabel.setForeground(Color.black);
		nrOfPairsLabel.setText("Nr. of pairs:");
		nrOfPairs.setEnabled(false);
		generalPanel.add(nrOfPairsLabel, new XGridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		generalPanel.add(nrOfPairs, new XGridBagConstraints(1,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

		widthLabel.setForeground(Color.black);
		widthLabel.setText("Width:");
		generalPanel.add(widthLabel, new XGridBagConstraints(0,2,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		generalPanel.add(width, new XGridBagConstraints(1,2,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

		heightLabel.setForeground(Color.black);
		heightLabel.setText("Height:");
		generalPanel.add(heightLabel, new XGridBagConstraints(0,3,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));
		generalPanel.add(height, new XGridBagConstraints(1,3,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),50,0));

		validLabel.setForeground(Color.black);
		generalPanel.add(validLabel, new XGridBagConstraints(0,4,2,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));

		pairPanel.setBorder(pairBorder);
		pairPanel.setLayout(new GridBagLayout());
		mainPanel.add(pairPanel, new XGridBagConstraints(1,0,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));

		add.setText("Add");
		pairPanel.add(add, new XGridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		remove.setText("Remove");
		pairPanel.add(remove, new XGridBagConstraints(1,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		pairTableScrollPane.setOpaque(true);
		pairPanel.add(pairTableScrollPane, new XGridBagConstraints(0,0,2,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));

		pairTable.setGridColor(new Color(153,153,153));
		pairTableScrollPane.getViewport().add(pairTable);

		titlePanel.setLayout(new GridBagLayout());
		getContentPane().add(BorderLayout.NORTH, titlePanel);

		title.setText("Memory Properties");
		titlePanel.add(title, new XGridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		title.setForeground(Color.black);
		title.setFont(new Font("Dialog", Font.BOLD, 20));

		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
		getContentPane().add(BorderLayout.SOUTH, buttonPanel);

		ok.setText("Ok");
		buttonPanel.add(ok);

		cancel.setText("Cancel");
		buttonPanel.add(cancel);

		generalBorder.setTitle("General");
		generalBorder.setTitleColor(Color.black);

		pairBorder.setTitle("Pairs");
		pairBorder.setTitleColor(Color.black);

		add.setIcon(addIcon);
		remove.setIcon(removeIcon);

		pairTableModel = new MemoryPairsTableModel();
		pairTable.setModel(pairTableModel);

		ok.addActionListener(this);
		cancel.addActionListener(this);
		add.addActionListener(this);
		remove.addActionListener(this);

		pairTableModel.addTableModelListener(this);
		// pairTable.addFocusListener(this);
		pairTable.getSelectionModel().addListSelectionListener(this);

		width.addKeyListener(this);
		height.addKeyListener(this);

        moveToCenter();
	}

    public void tableChanged(TableModelEvent e) {
        updateEnabledStates();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok || e.getSource() == cancel) {
            setVisible(false);
            if (e.getSource() == ok) {
                updateModel();
            }
        }
        else if (e.getSource() == add) {
            pairTableModel.addRow(new Object[] { "", "" });
        }
        else if (e.getSource() == remove && pairTable.getSelectedRow() != -1) {
            pairTableModel.removeRow(pairTable.getSelectedRow());
        }
    }

    public void updateModel() {
		memory.setPairs(pairTableModel.getMemoryPairs());
		memory.width = Integer.parseInt(width.getText());
		memory.height = Integer.parseInt(height.getText());
    }

    public void updateView() {
        name.setText(memory.getName());
        width.setText(Integer.toString(memory.width));
        height.setText(Integer.toString(memory.height));
        nrOfPairs.setText(Integer.toString(memory.getPairs().size()));
        pairTableModel.setMemoryPairs(memory.getPairs());
		updateEnabledStates();
    }

    public void setMemory(Memory memory) {
	    this.memory = memory;
	    updateView();
    }

    public void updateEnabledStates() {
        int nrOfPairsCalc = 0;
        boolean nrOfPairsEven = true;
        try {
            nrOfPairsEven = (Integer.parseInt(width.getText()) * Integer.parseInt(height.getText()) % 2) == 0;
            nrOfPairsCalc = Integer.parseInt(width.getText()) * Integer.parseInt(height.getText()) / 2;
        }
        catch (NumberFormatException excpt) {
        }
        validLabel.setText("Width and height are " + (nrOfPairsEven && nrOfPairsCalc == pairTableModel.getRowCount() ? "valid" : "invalid"));
        remove.setEnabled(pairTable.getSelectedRow() != -1);
        nrOfPairs.setText(Integer.toString(pairTableModel.getRowCount()));
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        updateEnabledStates();
    }

    public void keyTyped(KeyEvent e) {
    }

    /*
    public void focusGained(FocusEvent e) {
        updateEnabledStates();
    }
    public void focusLost(FocusEvent e) {
        updateEnabledStates();
    }
    */

    public void valueChanged(ListSelectionEvent e) {
        updateEnabledStates();
    }
}