package at.ac.uni_linz.tk.webstyles.gui;

import java.util.*;
import java.net.*;
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


public class NodeContentDialog extends CenteredDialog implements KeyListener, InputMethodListener, ActionListener, CaretListener, ItemListener {
    
    // public static final String LINK_NONE = "No Link";
    public static final String LINK_NONE = "";
    public static String folder = null;
    
    protected PSComponent comp;
    
	protected JPanel titlePanel = new JPanel();
	protected JPanel northPanel = new JPanel();
	protected JLabel title = new JLabel();
	protected JLabel link = new JLabel();
	protected JLabel font = new JLabel();
	protected JLabel fontSizeLabel = new JLabel();
	protected JPanel buttonPanel = new JPanel();
	protected JToolBar toolbar1 = new JToolBar();
	protected JToolBar toolbar2 = new JToolBar();
	protected JButton ok = new JButton();
	protected JButton cancel = new JButton();
	
	protected JButton open = new JButton(new ImageIcon("images/open.gif"));
	
	protected JToggleButton orderedList = new JToggleButton(new ImageIcon("images/ol.gif"));
	protected JToggleButton unorderedList = new JToggleButton(new ImageIcon("images/ul.gif"));
	protected JToggleButton listItem = new JToggleButton(new ImageIcon("images/item.gif"));
	
	protected JToggleButton left = new JToggleButton(new ImageIcon("images/left.gif"));
	protected JToggleButton center = new JToggleButton(new ImageIcon("images/center.gif"));
	protected JToggleButton right = new JToggleButton(new ImageIcon("images/right.gif"));
	
	protected JToggleButton setBold = new JToggleButton(new ImageIcon("images/bold.gif"));
	protected JToggleButton setItalic = new JToggleButton(new ImageIcon("images/italic.gif"));
	protected JToggleButton setUnderline = new JToggleButton(new ImageIcon("images/underline.gif"));
	
	protected JButton setColor = new JButton(new ImageIcon("images/color.gif"));
	protected JButton image = new JButton(new ImageIcon("images/image.gif"));
	protected JButton clear = new JButton(new ImageIcon("images/notext.gif"));
	
	protected JComboBox linkList = new JComboBox();
	protected JButton anchor = new JButton(new ImageIcon("images/anchor.gif"));
	protected JComboBox fontList = new JComboBox(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
	protected JComboBox fontSizeList = new JComboBox(new String[] { "6", "8", "10", "12", "14", "16", "20", "24", "32", "40", "56", "64", "72" });
	protected JScrollPane scrollPane = new JScrollPane();
	
	protected JTextPane content = new JTextPane();
	protected JTextField fontSize = new JTextField(3);
	
	protected class FormatAction extends StyledEditorKit.StyledTextAction {
		HTML.Tag htmlTag;

		public FormatAction(String actionName, HTML.Tag inTag) {
			super(actionName);
			htmlTag = inTag;
		}

		public void actionPerformed(ActionEvent ae) {
		    MutableAttributeSet attrInput = content.getInputAttributes();
		    
		    if (attrInput.getAttribute(htmlTag) == null) {
				attrInput.addAttribute(htmlTag, new SimpleAttributeSet());
		    }
		    else {
			    attrInput.removeAttribute(htmlTag);
			}
			content.setCharacterAttributes(attrInput, true);
            content.setText(content.getText());
		}
	}
	
    protected class HTMLFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".html") || file.isDirectory();
        }
        public String getDescription() {
            return new String("HTML Files (*.html)");
        }
    }
	
	public NodeContentDialog() {
	    super(WebStyles.getApplication().getFrame());
		setTitle("Node Content");
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(5,5));
		setSize(640, 600);
	    	
		link.setText("Link:");
		link.setForeground(Color.black);
	    	
		font.setText("Font:");
		font.setForeground(Color.black);
	    	
		fontSizeLabel.setText("Size:");
		fontSizeLabel.setForeground(Color.black);

		toolbar1.setFloatable(false);
		toolbar2.setFloatable(false);
        // toolbar2.setLayout(new GridBagLayout());

		toolbar1.addSeparator();
		toolbar1.add(open);
		toolbar1.addSeparator();
		
		toolbar1.add(setBold);
		toolbar1.add(setItalic);
		toolbar1.add(setUnderline);
		toolbar1.addSeparator();
		
		left.setToolTipText("Align Left");
		center.setToolTipText("Align Center");
		right.setToolTipText("Align Right");
		
		ButtonGroup alignmentGroup = new ButtonGroup();
		alignmentGroup.add(left);
		alignmentGroup.add(center);
		alignmentGroup.add(right);
		
		toolbar1.add(left);
		toolbar1.add(center);
		toolbar1.add(right);
		toolbar1.addSeparator();
		
		orderedList.setToolTipText("Ordered List");
		unorderedList.setToolTipText("Unordered List");
		listItem.setToolTipText("List Item");
		toolbar1.add(orderedList);
		toolbar1.add(unorderedList);
		toolbar1.add(listItem);
		toolbar1.addSeparator();
		
		clear.setToolTipText("Clear Format");
		toolbar1.add(setColor);
		toolbar1.add(image);		
		toolbar1.addSeparator();
		toolbar1.add(clear);
		linkList.setEditable(true);
		
		setBold.setToolTipText("Bold");
		setItalic.setToolTipText("Italic");
		setUnderline.setToolTipText("Underline");
		
		toolbar2.addSeparator();
		toolbar2.add(font);
		toolbar2.addSeparator();
		toolbar2.add(fontList);
		
		toolbar2.addSeparator();
		toolbar2.add(fontSizeLabel);
		toolbar2.addSeparator();
		toolbar2.add(fontSizeList);
		
		toolbar2.addSeparator();

		toolbar2.add(link);
		toolbar2.addSeparator();
		toolbar2.add(linkList);
		toolbar2.addSeparator();
		toolbar2.add(anchor);
		toolbar2.addSeparator();

		northPanel.setLayout(new GridLayout(3, 1, 5, 0));
		
		titlePanel.setLayout(new GridBagLayout());

		title.setText("Node Content");
		title.setForeground(Color.black);
		title.setFont(new Font("Dialog", Font.BOLD, 20));
		titlePanel.add(title, new XGridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));
		
		northPanel.add(titlePanel/*, BorderLayout.NORTH*/);
		northPanel.add(toolbar1/*, BorderLayout.CENTER*/);
		northPanel.add(toolbar2/*, BorderLayout.CENTER*/);
		getContentPane().add(BorderLayout.NORTH, northPanel);

		scrollPane.setOpaque(true);
		scrollPane.getViewport().add(content);
		
		getContentPane().add(BorderLayout.CENTER, scrollPane);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		getContentPane().add(BorderLayout.SOUTH, buttonPanel);

		ok.setText("Ok");
		buttonPanel.add(ok);

		cancel.setText("Cancel");
		buttonPanel.add(cancel);
		
		content.addCaretListener(this);
		content.addInputMethodListener(this);
		content.addKeyListener(this);
	
		ok.addActionListener(this);
		cancel.addActionListener(this);
		
		open.addActionListener(this);

		setBold.addActionListener(this);
		setItalic.addActionListener(this);
		setUnderline.addActionListener(this);

		setColor.addActionListener(this);
		image.addActionListener(this);
		anchor.addActionListener(this);
		
		linkList.addItemListener(this);
		fontList.addItemListener(this);
		fontSizeList.addItemListener(this);
		fontSize.addKeyListener(this);
		
		left.addActionListener(this);
		center.addActionListener(this);
		right.addActionListener(this);
		
		orderedList.addActionListener(this);
		unorderedList.addActionListener(this);
		listItem.addActionListener(this);
		clear.addActionListener(this);
		
        content.setContentType("text/html");
        updateToolbar();
        moveToCenter();
	}
	
	public void clearFormat() {
		MutableAttributeSet attrInput = (MutableAttributeSet)content.getInputAttributes();
		attrInput.addAttribute(new HTML.UnknownTag(""), new SimpleAttributeSet());
		ActionEvent actionEvent = new ActionEvent(content, 0, content.getSelectedText());
		new DefaultEditorKit.InsertContentAction().actionPerformed(actionEvent);
	}
	
	public void insertAnchor(String name) {
		MutableAttributeSet attrInput = content.getInputAttributes();
    			
		if (name != null && name.length() > 0) {
			SimpleAttributeSet attrTag  = new SimpleAttributeSet();
			SimpleAttributeSet attrParam = new SimpleAttributeSet();

			attrParam.addAttribute(HTML.Attribute.NAME, name);
			attrTag.addAttribute(HTML.Tag.A, attrParam);

			attrInput.addAttributes(attrTag);
		}
		else {
			attrInput.removeAttribute(HTML.Tag.A);
		}
		content.setCharacterAttributes(attrInput, true);
	}
	
	public void setLink(String url) {
		MutableAttributeSet attrInput = content.getInputAttributes();
    			
		if (url != null && url.length() > 0) {
			SimpleAttributeSet attrTag  = new SimpleAttributeSet();
			SimpleAttributeSet attrParam = new SimpleAttributeSet();

			attrParam.addAttribute(HTML.Attribute.HREF, url);
			attrTag.addAttribute(HTML.Tag.A, attrParam);

			attrInput.addAttributes(attrTag);
		}
		else {
			attrInput.removeAttribute(HTML.Tag.A);
		}
		content.setCharacterAttributes(attrInput, true);
	}
	
	public void insertImage(String imgURL) {
	    try {
		    HTMLEditorKit htmlKit = (HTMLEditorKit)content.getEditorKit();
		    int caretPos = content.getCaretPosition();
		    htmlKit.insertHTML((HTMLDocument)content.getDocument(), content.getCaretPosition(), "<IMG SRC=\"" + imgURL + "\">", 0, 0, HTML.Tag.IMG);
	        content.setCaretPosition(caretPos + 1);
	    }
	    catch (Exception e) {
	    }
	}
	
	public void insertNewLine() {
	    try {
		    HTMLEditorKit htmlKit = (HTMLEditorKit)content.getEditorKit();
		    int caretPos = content.getCaretPosition();
		    htmlKit.insertHTML((HTMLDocument)content.getDocument(), content.getCaretPosition(), "<BR>", 0, 0, HTML.Tag.BR);
	        content.setCaretPosition(caretPos + 1);
	    }
	    catch (Exception e) {
	    }
	}

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok || e.getSource() == cancel) {
		    setVisible(false);
        }
        else if (e.getSource() == open) {
            if (folder == null) {
                folder = (String)PSEditorProperties.getProperties().get(PSEditorProperties.PROPERTY_SAVE_FOLDER);
            }
            JFileChooser chooser = new JFileChooser(folder);
            chooser.setFileFilter(new HTMLFileFilter());
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getSelectedFile().getPath();
                folder = fileName;
                try {
                    content.setPage("file:" + fileName);
                }
                catch (Exception excpt) {
                    excpt.printStackTrace();
                    JOptionPane.showMessageDialog(this, excpt.toString(), "Open Error", JOptionPane.ERROR_MESSAGE, null);
                }
            }
        }
        else if (e.getSource() == setBold) {
            new StyledEditorKit.BoldAction().actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == setItalic) {
            new StyledEditorKit.ItalicAction().actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == setUnderline) {
            new StyledEditorKit.UnderlineAction().actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == left) {
            new StyledEditorKit.AlignmentAction("Align Left", StyleConstants.ALIGN_LEFT).actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == center) {
            new StyledEditorKit.AlignmentAction("Align Center", StyleConstants.ALIGN_CENTER).actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == right) {
            new StyledEditorKit.AlignmentAction("Align Right", StyleConstants.ALIGN_RIGHT).actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == clear) {
            clearFormat();
        }
        else if (e.getSource() == setColor) {
            Color col = JColorChooser.showDialog(this, "Choose Color", StyleConstants.getForeground(content.getInputAttributes()));
            if (col != null) {
                new StyledEditorKit.ForegroundAction("Foreground",  col).actionPerformed(new ActionEvent(content, 0, Integer.toString(col.getRGB())));
            }
        }
        else if (e.getSource() == orderedList) {
            new FormatAction("Ordered List", HTML.Tag.OL).actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == unorderedList) {
            new FormatAction("Unordered List", HTML.Tag.UL).actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == listItem) {
            new FormatAction("List Item", HTML.Tag.LI).actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == image) {
            String imgURL = JOptionPane.showInputDialog(WebStyles.getApplication().getFrame(), "Enter Image URL:", "Insert Image", JOptionPane.PLAIN_MESSAGE);
            if (imgURL != null) {
                insertImage(imgURL);
            }
        }
        else if (e.getSource() == anchor) {
            String name = JOptionPane.showInputDialog(WebStyles.getApplication().getFrame(), "Enter Anchor Name:", "Insert Anchor", JOptionPane.PLAIN_MESSAGE);
            if (name != null) {
                insertAnchor(name);
            }
        }
        content.requestFocus();
    }
    
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == linkList) {
            setLink(linkList.getSelectedItem().toString().length() == 0 ? null : linkList.getSelectedItem().toString());
        }
        else if (e.getSource() == fontList) {
            new StyledEditorKit.FontFamilyAction("Font", fontList.getSelectedItem().toString()).actionPerformed(new ActionEvent(content, 0, null));
        }
        else if (e.getSource() == fontSizeList) {
            new StyledEditorKit.FontSizeAction("FontSize", Integer.parseInt(fontSizeList.getSelectedItem().toString())).actionPerformed(new ActionEvent(content, 0, null));
        }
        content.requestFocus();
    }
    
    public void updateToolbar() {
        SimpleAttributeSet attr;
		MutableAttributeSet attrInput;
		AttributeSet attrPar;

		attrInput = content.getInputAttributes();
		attrPar = content.getParagraphAttributes();
        setBold.setSelected(StyleConstants.isBold(attrInput));
        setItalic.setSelected(StyleConstants.isItalic(attrInput));
        setUnderline.setSelected(StyleConstants.isUnderline(attrInput));

		linkList.removeItemListener(this);
		if ((attr = (SimpleAttributeSet)attrInput.getAttribute(HTML.Tag.A)) != null) {
		    linkList.setSelectedItem(attr.getAttribute(HTML.Attribute.HREF));
		}
		else {
		    linkList.setSelectedItem("");
		}
		linkList.addItemListener(this);
		linkList.setEnabled(content.getSelectedText() != null && content.getSelectedText().length() > 0);
		
		orderedList.setSelected(attrPar.getAttribute(HTML.Tag.OL) != null);
		unorderedList.setSelected(attrPar.getAttribute(HTML.Tag.UL) != null);
		listItem.setSelected(attrPar.getAttribute(HTML.Tag.LI) != null);
		
		left.setSelected(StyleConstants.getAlignment(attrInput) == StyleConstants.ALIGN_LEFT);
		center.setSelected(StyleConstants.getAlignment(attrInput) == StyleConstants.ALIGN_CENTER);
		right.setSelected(StyleConstants.getAlignment(attrInput) == StyleConstants.ALIGN_RIGHT);
        
        fontList.setSelectedItem(StyleConstants.getFontFamily(attrInput));
        fontSizeList.setSelectedItem(Integer.toString(StyleConstants.getFontSize(attrInput)));
    }
    
    public void caretUpdate(CaretEvent e) {
        updateToolbar();
    }
    
    public void inputMethodTextChanged(InputMethodEvent event) {
        try {
            StringWriter writer = new StringWriter();
            HTMLDocument doc = (HTMLDocument)content.getDocument();
            new HTMLEditorKit().write(writer, doc, 0, doc.getLength() - 1);
            // System.out.println(writer);
        }
        catch (Exception excpt) {
             excpt.printStackTrace();
        }
        
    }
    
    public void caretPositionChanged(InputMethodEvent event) {
    }
    
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == content) {
            if (e.isShiftDown() && e.getKeyChar() == KeyEvent.VK_ENTER) {
                // insertNewLine();
                // e.consume();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
}