package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class SplashScreen extends CenteredDialog implements ActionListener {
	JLabel title = new JLabel();
	
	JButton preview = new JButton("Preview");
	JButton ok = new JButton("Ok");
	
	JPanel titlePanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel mainPanel = new JPanel();
	JPanel aboutPanel = new JPanel();
	
	JLabel programLabel = new JLabel();
	JLabel authorLabel = new JLabel();
	JLabel infoLabel1 = new JLabel();
	JLabel infoLabel2 = new JLabel();
	JLabel infoLabel3 = new JLabel();

	public SplashScreen(Frame owner) {
	    super(owner);
		setTitle("About WebStyles");
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(5,5));
		setSize(360, 400);

		titlePanel.setLayout(new GridBagLayout());
		getContentPane().add(BorderLayout.NORTH, titlePanel);

		title.setText("About WebStyles");
		titlePanel.add(title, new XGridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH));
		title.setForeground(Color.black);
		title.setFont(new Font("Dialog", Font.BOLD, 20));

		mainPanel.setLayout(new BorderLayout(5,5));
		getContentPane().add(BorderLayout.CENTER, mainPanel);

		aboutPanel.setLayout(new GridBagLayout());

		programLabel.setForeground(Color.black);
		programLabel.setText(WebStyles.APP_VERSION);
		aboutPanel.add(programLabel, new XGridBagConstraints(0, 0, 1, 1, 1.0, 0.0));

		authorLabel.setForeground(Color.black);
		authorLabel.setText("By Arno Hütter (arno.huetter@students.uni-linz.ac.at)");
		aboutPanel.add(authorLabel, new XGridBagConstraints(0, 1, 1, 1, 1.0, 0.0));
		
		infoLabel1.setForeground(Color.black);
		infoLabel1.setText("(C)opyright Telecooperation Deptartment");
		aboutPanel.add(infoLabel1, new XGridBagConstraints(0, 2, 1, 1, 1.0, 0.0));
		
		infoLabel2.setForeground(Color.black);
		infoLabel2.setText("Institute for Technical Computer Science");
		aboutPanel.add(infoLabel2, new XGridBagConstraints(0, 3, 1, 1, 1.0, 0.0));
		
		infoLabel3.setForeground(Color.black);
		infoLabel3.setText("University of Linz, Austria");
		aboutPanel.add(infoLabel3, new XGridBagConstraints(0, 4, 1, 1, 1.0, 0.0));
		
		mainPanel.add(BorderLayout.NORTH, new JLabel(new ImageIcon("images/splash.jpg")));
		mainPanel.add(BorderLayout.CENTER, aboutPanel);
		

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