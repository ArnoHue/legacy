package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class NestedGraphNodePreviewDialog extends CenteredDialog implements ActionListener {
	
	TitledBorder viewBorder = new TitledBorder("Nested Graph");
	
	JLabel title = new JLabel();
	
	JButton ok = new JButton("Ok");
    JPanel mainPanel = new JPanel();
    JPanel titlePanel = new JPanel();
    JPanel buttonPanel = new JPanel(); Label l;
	protected JScrollPane scroller = new JScrollPane();

	public NestedGraphNodePreviewDialog(Frame owner, PSGraphView view) {
	    super(owner);
		setTitle("Nested Graph Preview");
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(5,5));
		setSize(600, 600);

        view.setEnabled(false);
		scroller.getViewport().add(view);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(viewBorder);
		viewBorder.setTitleColor(Color.black);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(BorderLayout.CENTER, scroller);
		getContentPane().add(BorderLayout.CENTER, mainPanel);

		titlePanel.setLayout(new GridBagLayout());
		getContentPane().add(BorderLayout.NORTH, titlePanel);

		title.setText("Nested Graph Properties");
		titlePanel.add(title, new XGridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH));
		title.setForeground(Color.black);
		title.setFont(new Font("Dialog", Font.BOLD, 20));

		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
		getContentPane().add(BorderLayout.SOUTH, buttonPanel);

		ok.setText("Ok");
		buttonPanel.add(ok);

		ok.addActionListener(this);
		moveToCenter();
	}

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            setVisible(false);
        }
    }

}