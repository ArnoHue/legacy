package at.ac.uni_linz.tk.webstyles.gui;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.gui.*;
import at.ac.uni_linz.tk.webstyles.util.*;

import at.ac.uni_linz.tk.webstyles.action.*;

public class NestedGraphNodePropertyDialog extends CenteredDialog implements ListSelectionListener, ActionListener {
    
    PSNestedGraphNode node;
    PSNestedGraphNode.NestedGraph nestedGraph;

	JTextField name = new JTextField();
	JLabel nameLabel = new JLabel();

	JLabel graphLabel = new JLabel();
	
	TitledBorder generalBorder = new TitledBorder("Nested Graph");
	TitledBorder portBorder = new TitledBorder("Ports");

	JLabel innerLinkLabel = new JLabel();
	JLabel outerLinkLabel = new JLabel();
	
	JLabel title = new JLabel();
	
	JButton open = new JButton(new ImageIcon("images/open.gif"));
	JButton preview = new JButton("Preview");
	JButton ok = new JButton("Ok");
	JButton cancel = new JButton("Cancel");
	JButton add = new JButton("Add", new ImageIcon("images/additem.gif"));
	JButton remove = new JButton("Remove", new ImageIcon("images/delete.gif"));
	
	JPanel titlePanel = new JPanel();
	JPanel generalPanel = new JPanel();
	JPanel portPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel mainPanel = new JPanel();
	
	JList outerLinkList = new JList();
	JList innerLinkList = new JList();
	JList ports = new JList();
	
	protected JScrollPane innerLinkListScroller = new JScrollPane();
	protected JScrollPane outerLinkListScroller = new JScrollPane();
	protected JScrollPane portsScroller = new JScrollPane();
	
	protected Vector portVector = new Vector();

	public NestedGraphNodePropertyDialog() {
	    super(WebStyles.getApplication().getFrame());
		setTitle("Nested Graph Properties");
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(5,5));
		setSize(600, 600);

		mainPanel.setLayout(new GridBagLayout());
		getContentPane().add(BorderLayout.CENTER, mainPanel);

		generalPanel.setBorder(generalBorder);
		generalPanel.setLayout(new GridBagLayout());
		mainPanel.add(generalPanel, new XGridBagConstraints(0,0,1,1,1.0,0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH));
		mainPanel.add(portPanel, new XGridBagConstraints(0,1,1,1,1.0,1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH));

		nameLabel.setForeground(Color.black);
		nameLabel.setText("Name:");
		generalPanel.add(nameLabel, new XGridBagConstraints(0,0,1,1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
		generalPanel.add(name, new XGridBagConstraints(1,0,1,1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, XGridBagConstraints.DEFAULT_INSETS, 100, 0));
		generalPanel.add(open, new XGridBagConstraints(2,0,1,1));
		generalPanel.add(preview, new XGridBagConstraints(3,0,1,1));
		
		portPanel.setBorder(portBorder);
		portPanel.setLayout(new GridBagLayout());
		innerLinkListScroller.getViewport().add(innerLinkList);
		outerLinkListScroller.getViewport().add(outerLinkList);
		portsScroller.getViewport().add(ports);
		
		innerLinkLabel.setForeground(Color.black);
		innerLinkLabel.setText("Inner Links:");
		outerLinkLabel.setForeground(Color.black);
		outerLinkLabel.setText("Outer Links:");
		portPanel.add(outerLinkLabel, new XGridBagConstraints(0,0));
		portPanel.add(innerLinkLabel, new XGridBagConstraints(1,0));
		portPanel.add(outerLinkListScroller, new XGridBagConstraints(0,1,1,1, 1.0, 1.0, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH));
		portPanel.add(innerLinkListScroller, new XGridBagConstraints(1,1,1,1, 1.0, 1.0, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH));
		portPanel.add(portsScroller, new XGridBagConstraints(0,2,2,1, 1.0, 1.0, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH));
		portPanel.add(add, new XGridBagConstraints(0,3,1,1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE));
		portPanel.add(remove, new XGridBagConstraints(1,3,1,1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE));

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

		cancel.setText("Cancel");
		buttonPanel.add(cancel);

		generalBorder.setTitleColor(Color.black);

		portBorder.setTitleColor(Color.black);

		ok.addActionListener(this);
		cancel.addActionListener(this);
		add.addActionListener(this);
		remove.addActionListener(this);
		open.addActionListener(this);
		preview.addActionListener(this);
		
		innerLinkList.addListSelectionListener(this);
		outerLinkList.addListSelectionListener(this);
		ports.addListSelectionListener(this);
		
		moveToCenter();
	}

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok || e.getSource() == cancel) {
            setVisible(false);
            if (e.getSource() == ok) {
                updateModel();
            }
        }
        else if (e.getSource() == add) {
            portVector.addElement(new PSNestedGraphNode.Port((PSLink)innerLinkList.getSelectedValue(), (PSLink)outerLinkList.getSelectedValue()));
            ports.setListData(portVector);
        }
        else if (e.getSource() == open) {
            String fileName = OpenAction.getAction().selectFile();
            if (fileName != null) {
                PSGraphController ctrl = OpenAction.getAction().loadGraph(fileName);
                if (ctrl != null) {
                    graphLabel.setText(fileName);
                    nestedGraph = new PSNestedGraphNode.NestedGraph(ctrl);
                    portVector = new Vector();
                    ports.setListData(portVector);
                    innerLinkList.setListData(getVector(node.getNestedGraphLinks(nestedGraph)));
                    ctrl.disconnect();
                    updateEnabledStates();
                }
            }
        }
        else if (e.getSource() == preview) {
            new NestedGraphNodePreviewDialog(WebStyles.getApplication().getFrame(), nestedGraph.view).setVisible(true);
        }
    }

    public void updateModel() {
        node.removeAllPorts();
        for (int i = 0; i < portVector.size(); i++) {
            node.addPort((PSNestedGraphNode.Port)portVector.elementAt(i));
        }
        node.setNestedGraph(nestedGraph);
        name.setName(name.getText());
    }
    
    protected Vector getVector(Enumeration enum) {
        Vector vec = new Vector();
        while (enum.hasMoreElements()) {
            vec.addElement(enum.nextElement());
        }
        return vec;
    }
    
    public void updateView() {
        nestedGraph = node.getNestedGraph();
        name.setText(node.getName());
        portVector = getVector(node.getPorts());
        ports.setListData(portVector);
        outerLinkList.setListData(getVector(node.getLinkedComponents()));
        innerLinkList.setListData(getVector(node.getNestedGraphLinks()));
		updateEnabledStates();
    }

    public void setNode(PSNestedGraphNode nodeParam) {
        node = nodeParam;
        updateView();
    }

    public void updateEnabledStates() {
        PSLink innerLink = (PSLink)innerLinkList.getSelectedValue();
        PSLink outerLink = (PSLink)outerLinkList.getSelectedValue();
        add.setEnabled(
        innerLink != null && outerLink != null && innerLink.getType() == outerLink.getType() && 
        innerLink.isSequence() == outerLink.isSequence() && innerLink.isJoin() == outerLink.isJoin() && 
        !portContainsLink(innerLink) && !portContainsLink(outerLink) &&
        ((node.isLinked(outerLink, PSConstants.IN) && innerLink.getNode(PSConstants.IN) == null) ||
        (node.isLinked(outerLink, PSConstants.OUT) && innerLink.getNode(PSConstants.OUT) == null)));
        remove.setEnabled(ports.getSelectedValue() != null);
        preview.setEnabled(nestedGraph != null);
    }
    
    public boolean portContainsLink(PSLink link) {
        for (int i = 0; i < portVector.size(); i++) {
            PSNestedGraphNode.Port port = (PSNestedGraphNode.Port)portVector.elementAt(i);
            if (port.innerLink == link || port.outerLink == link) {
                return true;
            }
        }
        return false;
    }   
    
    public void valueChanged(ListSelectionEvent e) {
        updateEnabledStates();
    }

}