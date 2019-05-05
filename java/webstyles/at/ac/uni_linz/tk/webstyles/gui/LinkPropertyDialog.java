package at.ac.uni_linz.tk.webstyles.gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import at.ac.uni_linz.tk.webstyles.*;
import at.ac.uni_linz.tk.webstyles.util.*;

public class LinkPropertyDialog extends PropertyDialog implements ActionListener {

	InstantiationPanel instantiationPanel = new InstantiationPanel();
	OnLinkInstantiationPanel onInstantiationPanel = new OnLinkInstantiationPanel();
	NavigationPanel navigationPanel = new NavigationPanel();
	PropertiesPanel propertiesPanel = new PropertiesPanel();
	GeneralLinkPanel generalPanel = new GeneralLinkPanel();
    
	public LinkPropertyDialog() {
		mainPanel.add(generalPanel, new XGridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		mainPanel.add(instantiationPanel, new XGridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		
		mainPanel.add(propertiesPanel, new XGridBagConstraints(1,1,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		mainPanel.add(navigationPanel, new XGridBagConstraints(0,2,2,1,0.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));

		instantiationPanel.add(onInstantiationPanel, new XGridBagConstraints(2,0,1,4,1.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		generalPanel.generic.addActionListener(this);
		generalPanel.instance.addActionListener(this);
		generalPanel.type.addActionListener(this);

	}
	
	public String getTitleString() {
	    return new String("Link Properties");
	}

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == generalPanel.kind) {
            updateEnabledStates();
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
            else if (selItem.equals(PSComponent.TYPE_FAN_STRING)) {
                instantiationPanel.minInstances.setText("0");
                instantiationPanel.maxInstances.setText("3");
            }
            updateEnabledStates();
        }
    }
    
    public void updateModel() {
        PSLink link = (PSLink)comp;
        link.setName(generalPanel.name.getText().replace(' ', '_'));
        link.setType(PSComponent.getTypeId(generalPanel.type.getSelectedItem().toString()));
        link.setJoin(onInstantiationPanel.join.getModel().isSelected());
        link.setSequence(onInstantiationPanel.sequence.getModel().isSelected());
		link.generic = generalPanel.generic.getModel().isSelected();
		link.maxInstances = new Integer(instantiationPanel.maxInstances.getText()).intValue();
		link.minInstances = new Integer(instantiationPanel.minInstances.getText()).intValue();
		link.nrInstances = new Integer(instantiationPanel.nrInstances.getText()).intValue();
		link.indInstances = new Integer(instantiationPanel.indInstances.getText()).intValue();
        link.properties = propertiesPanel.tableModel.getProperties();
		link.setForwardRules(navigationPanel.forwRules.getText());
    }  
    
    public void updateView() {
        PSLink link = (PSLink)comp;
        generalPanel.name.setText(link.getName());
		generalPanel.type.setSelectedItem(PSComponent.getTypeString(link.getType()));
        generalPanel.generic.setSelected(link.generic && !link.pseudoGeneric);
		generalPanel.pseudoGeneric.setSelected(link.pseudoGeneric);
		generalPanel.instance.setSelected(!link.generic);
		onInstantiationPanel.join.setSelected(link.isJoin());
		onInstantiationPanel.sequence.setSelected(link.isSequence());
		generalPanel.instance.setSelected(!link.generic);
		instantiationPanel.maxInstances.setText("" + link.maxInstances);
		instantiationPanel.minInstances.setText("" + link.minInstances);
		instantiationPanel.nrInstances.setText("" + link.nrInstances);
		instantiationPanel.indInstances.setText("" + link.indInstances);
		propertiesPanel.tableModel.setProperties(link.properties);
		navigationPanel.forwRules.setText(link.getForwardRules());
		updateEnabledStates();
		
		PSGraph graph = link.getGraph();
		Vector data = new Vector(graph.getNrOfComponents());
		TreeSet tmpSortSet = new TreeSet();
		for (Enumeration enum = graph.getComponents(); enum.hasMoreElements();) {
		    Object obj = enum.nextElement();
		    if (obj instanceof PSNode) {
		        tmpSortSet.add("hasBeenVisited(session, \"" + ((PSNode)obj).getName() + "\")");
		    }
		    else if (obj instanceof PSLink) {
		        tmpSortSet.add("hasBeenTraversed(session, \"" + ((PSLink)obj).getName() + "\")");
		    }
		}
		for (Iterator it = tmpSortSet.iterator(); it.hasNext();) {
		    data.addElement(it.next());
		}
		navigationPanel.apiList.setListData(data);
    }  
    
    public void updateEnabledStates() {
        boolean generic = generalPanel.generic.getModel().isSelected();
        boolean editInstances =generalPanel.type.getSelectedItem().equals(PSComponent.TYPE_FAN_STRING);
        generalPanel.generic.setEnabled(false);
        generalPanel.pseudoGeneric.setEnabled(false);
        generalPanel.instance.setEnabled(false);
        onInstantiationPanel.join.setEnabled(generic);
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
        instantiationPanel.setEnabled(generic);
    }

}