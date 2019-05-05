package at.ac.uni_linz.tk.webstyles.gui;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;
import at.ac.uni_linz.tk.webstyles.xml.*;
import at.ac.uni_linz.tk.webstyles.generic.*;
import at.ac.uni_linz.tk.webstyles.generic.ContentManager;

public class NodePropertyDialog extends PropertyDialog implements ActionListener, ListSelectionListener, KeyListener, FocusListener {
    
	InstantiationPanel instantiationPanel = new InstantiationPanel();
	OnNodeInstantiationPanel onInstantiationPanel = new OnNodeInstantiationPanel();
	ContentPanel contentPanel = new ContentPanel();
	GenericContentPanel genericContentPanel = new GenericContentPanel();
	GeneralNodePanel generalPanel = new GeneralNodePanel();

	PropertiesPanel propertiesPanel = new PropertiesPanel();
    NodeContentDialog contentDialog = new NodeContentDialog();
    
	public NodePropertyDialog() {
	    super();

		mainPanel.add(generalPanel, new XGridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		mainPanel.add(instantiationPanel, new XGridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		mainPanel.add(genericContentPanel, new XGridBagConstraints(0,1,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		mainPanel.add(propertiesPanel, new XGridBagConstraints(1,1,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		mainPanel.add(contentPanel, new XGridBagConstraints(0,2,2,1,0.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		
		instantiationPanel.add(onInstantiationPanel, new XGridBagConstraints(3,0,1,4,1.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

		generalPanel.generic.addActionListener(this);
		generalPanel.instance.addActionListener(this);

		generalPanel.type.addActionListener(this);
		genericContentPanel.edit.addActionListener(this);
		genericContentPanel.list.addListSelectionListener(this);
		
		contentPanel.edit.addActionListener(this);
		contentPanel.contentURI.addKeyListener(this);
		// contentPanel.contentURI.addFocusListener(this);
		contentPanel.contentURI.addActionListener(this);
	}


    public void actionPerformed(ActionEvent e) {
        PSNode node = (PSNode)comp;
        super.actionPerformed(e);
        if (e.getSource() == generalPanel.kind) {
            updateEnabledStates();
        }
        else if (e.getSource() == genericContentPanel.edit && genericContentPanel.list.getSelectedValue() != null) {
            if (node.content == null || !node.content.getName().equals((String)genericContentPanel.list.getSelectedValue())) {
                node.content = ContentManager.getManager().createContent((String)genericContentPanel.list.getSelectedValue());
            }
            node.content.editProperties();
        }
        else if (e.getSource() == contentPanel.edit) {
            contentDialog = new NodeContentDialog();
            Vector vec = node.getLinkedNodes(PSConstants.OUT);
            for (int i = 0; i < vec.size(); i++) {
                PSNode linkedNode = (PSNode)vec.elementAt(i);
                contentDialog.linkList.addItem(linkedNode.contentURI != null && linkedNode.contentURI.length() > 0 ? linkedNode.contentURI : linkedNode.getName());
            }
            contentDialog.linkList.addItem(NodeContentDialog.LINK_NONE);
		    contentDialog.ok.addActionListener(this);
		    getHTMLDoc(getHTMLString((HTMLDocument)contentPanel.contentText.getDocument()), (HTMLDocument)contentDialog.content.getDocument());
            contentDialog.setVisible(true);
        }
        else if (e.getSource() == contentDialog.ok) {
            contentPanel.contentText.setDocument(contentDialog.content.getDocument());
        }
        else if (e.getSource() == generalPanel.type) {
            Object selItem = generalPanel.type.getSelectedItem();
            if (selItem.equals(PSComponent.TYPE_MANDATORY_STRING)) {
                instantiationPanel.minInstances.setText("1");
                instantiationPanel.maxInstances.setText("1");
            }
            else if (selItem.equals(PSComponent.TYPE_OPTIONAL_STRING)) {
                instantiationPanel.minInstances.setText("0");
                instantiationPanel.maxInstances.setText("1");
            }
            else if (selItem.equals(PSComponent.TYPE_SEQUENCE_STRING)) {
                instantiationPanel.minInstances.setText("0");
                instantiationPanel.maxInstances.setText("3");
            }
            updateEnabledStates();
        }
        else if (e.getSource() == contentPanel.contentURI) {
            if (contentPanel.contentURI.getText().length() != 0) {
                try {
                    contentPanel.contentText.setPage(contentPanel.contentURI.getText());
                }
                catch (IOException excpt) {
                    JOptionPane.showMessageDialog(this, excpt.toString(), "Content Error", JOptionPane.ERROR_MESSAGE, null);
                }
            }
        }
    }
    
    public void updateModel() {
        PSNode node = (PSNode)comp;
        node.setName(generalPanel.name.getText().replace(' ', '_'));
        node.setType(PSComponent.getTypeId(generalPanel.type.getSelectedItem().toString()));
		node.maxInstances = Integer.parseInt(instantiationPanel.maxInstances.getText());
		node.minInstances = Integer.parseInt(instantiationPanel.minInstances.getText());
		node.nrInstances = Integer.parseInt(instantiationPanel.nrInstances.getText());
		node.indInstances = Integer.parseInt(instantiationPanel.indInstances.getText());
        node.properties = propertiesPanel.tableModel.getProperties();
        node.contentText = contentPanel.contentURI.getText().length() != 0 ? null : getHTMLString((HTMLDocument)contentPanel.contentText.getDocument());
        node.contentURI = contentPanel.contentURI.getText().length() == 0 ? null : contentPanel.contentURI.getText();
    }
    
    protected String getHTMLString(HTMLDocument doc) {
        try {
            StringWriter writer = new StringWriter();
            new HTMLEditorKit().write(writer, doc, 0, doc.getLength() - 1);
            return writer.getBuffer().toString();
        }
        catch (Exception excpt) {
             excpt.printStackTrace();
        }
        return null;
    }
    
    protected void getHTMLDoc(String src, HTMLDocument doc) {
        try {
            new HTMLEditorKit().read(new StringReader(src), doc, 0);
        }
        catch (Exception excpt) {
             excpt.printStackTrace();
        }
    }
    
    public void updateView() {
        PSNode node = (PSNode)comp;
        generalPanel.name.setText(node.getName());
		generalPanel.type.setSelectedItem(PSComponent.getTypeString(node.getType()));
		generalPanel.generic.setSelected(node.generic && !node.pseudoGeneric);
		generalPanel.pseudoGeneric.setSelected(node.pseudoGeneric);
		generalPanel.instance.setSelected(!node.generic && !node.pseudoGeneric);
		propertiesPanel.tableModel.setProperties(node.properties);
		instantiationPanel.maxInstances.setText(Integer.toString(node.maxInstances));
		instantiationPanel.minInstances.setText(Integer.toString(node.minInstances));
		instantiationPanel.nrInstances.setText(Integer.toString(node.nrInstances));
		instantiationPanel.indInstances.setText(Integer.toString(node.indInstances));
		genericContentPanel.list.setListData(ContentManager.getManager().getContentNames());
        genericContentPanel.list.setSelectedValue(node.content != null ? node.content.getName() : null, true);
        updateContentView((HTMLDocument)contentPanel.contentText.getDocument());
        contentPanel.contentURI.setText(node.contentURI);
		updateEnabledStates();
    }  
    
    protected void updateContentView(HTMLDocument htmlDoc) {
        PSNode node = (PSNode)comp;
        try {
            HTMLEditorKit htmlKit = new HTMLEditorKit();
            htmlKit.read(new StringReader(node.contentText != null ? node.contentText : "<HTML><BODY><P></P></BODY></HTML>"), htmlDoc, 0);
        }
        catch (Exception excpt) {
             excpt.printStackTrace();
        }
    }
    
    public String getTitleString() {
	    return new String("Node Properties");
    }
    
    public void updateEnabledStates() {
        boolean generic = generalPanel.generic.getModel().isSelected();
        boolean editInstances = generalPanel.type.getSelectedItem().equals(PSComponent.TYPE_SEQUENCE_STRING);
        generalPanel.kind.setEnabled(false);
        generalPanel.generic.setEnabled(false);
        generalPanel.pseudoGeneric.setEnabled(false);
        generalPanel.instance.setEnabled(false);
        generalPanel.type.setEnabled(generic);
        generalPanel.typeLabel.setEnabled(generic);
        instantiationPanel.minInstances.setEnabled(generic && editInstances);
        instantiationPanel.minInstancesLabel.setEnabled(generic && editInstances);
        instantiationPanel.maxInstances.setEnabled(generic && editInstances);
        instantiationPanel.maxInstancesLabel.setEnabled(generic && editInstances);
        instantiationPanel.nrInstances.setEnabled(false);
        instantiationPanel.nrInstancesLabel.setEnabled(false);
        instantiationPanel.indInstances.setEnabled(!generic);
        instantiationPanel.indInstancesLabel.setEnabled(!generic);
        onInstantiationPanel.setEnabled(generic);
        genericContentPanel.edit.setEnabled(genericContentPanel.list.getSelectedIndex() != -1);
        contentPanel.edit.setEnabled(contentPanel.contentURI.getText().length() == 0);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        updateEnabledStates();
    }
    
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getSource() == contentPanel.contentURI) {
            contentPanel.edit.setEnabled(contentPanel.contentURI.getText().length() == 0);
        }
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void focusGained(FocusEvent e) {
    }
    
    public void focusLost(FocusEvent e) {
    }

}