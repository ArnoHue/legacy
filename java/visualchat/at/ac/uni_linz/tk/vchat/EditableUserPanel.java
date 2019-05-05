package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;


/**
 * A UserPanel is used for displaying and editing User-data, including color
 * settings, password changement, Images and keywords for different moods.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class EditableUserPanel extends UserPanel implements ActionListener, AdjustmentListener, ItemListener, FocusListener, MouseListener {

  private InsetsPanel buttonPanel, avatarButtonPanel;
  private Button applyButton, addAvatarButton, removeAvatarButton;


/**
 * Constructs the UserPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                Users
 * @param repositoryParam         the ChatRepository, where commonly used objects
 *                                are being stored
 */

  public EditableUserPanel(ChatApplet chatParam) {

    super(chatParam);

    GridBagConstraints constraints;

    applyButton = new Button("Apply");
    buttonPanel = new InsetsPanel(ChatRepository.INSETS);
    buttonPanel.setLayout(new GridBagLayout());
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.weightx = 1.0;
    ChatUtil.addWithConstraints(buttonPanel, applyButton, constraints);
    add(buttonPanel, "South");

    avatarButtonPanel = new InsetsPanel(ChatRepository.INSETS);
    avatarButtonPanel.setLayout(new GridBagLayout());
    ChatUtil.addWithRemainingConstraints(avatarButtonPanel, addAvatarButton = new Button("Add avatar"));
    ChatUtil.addWithRemainingConstraints(avatarButtonPanel, removeAvatarButton = new Button("Remove avatar"));

    avatarPanelLevel1.add(avatarButtonPanel, "East");

    applyButton.addActionListener(this);
    addAvatarButton.addActionListener(this);
    removeAvatarButton.addActionListener(this);

    redBar.addAdjustmentListener(this);
    greenBar.addAdjustmentListener(this);
    blueBar.addAdjustmentListener(this);

    ((FocusPanel)singleAvatarPanel.elementAt(getNrOfAvatarPanels() - 1)).requestFocus();

  }


/**
 * Invoked when an action occurs.
 *
 * @param event      the ActionEvent
 */

  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == applyButton) {
      updateUser(chatApplet.getCurrentUser());
    }
    else if (event.getSource() == addAvatarButton) {
      addAvatarPanel();
    }
    else if (event.getSource() == removeAvatarButton && focusedAvatarIndex() != -1) {
      removeAvatarPanel(focusedAvatarIndex());
    }
  }


/**
 * Invoked when the value of the adjustable has changed.
 *
 * @param event      the AdjustmentEvent
 */

  public void adjustmentValueChanged(AdjustmentEvent event) {
    if (event.getSource() == redBar || event.getSource() == greenBar || event.getSource() == blueBar) {
      currentPortraitCanvas.setFrameColor(new Color(redBar.getValue(), greenBar.getValue(), blueBar.getValue()));
    }
  }


/**
 * Invoked when an item's state has been changed.
 *
 * @param event      the ItemEvent
 */

  public void itemStateChanged(ItemEvent event) {

    for (int i = 0; i < getNrOfAvatarPanels(); i++) {
      if (event.getItemSelectable() == serverAvatarCheck.elementAt(i) || event.getItemSelectable() == webAvatarCheck.elementAt(i)) {
        boolean webAvatar = ((Checkbox)webAvatarCheck.elementAt(i)).getState();
        ((Choice)serverAvatarChoice.elementAt(i)).setEnabled(!webAvatar);
        ((TextField)avatarURLField.elementAt(i)).setEditable(webAvatar);
      }
    }
  }


/**
 * Updates a User's data.
 *
 * @param userParam      the User's new data
 */

  public void updateUser(User userParam) {
    User user;
    boolean connected;

    connected = chatApplet.getClient() != null && chatApplet.getClient().connected();
    user = (User)(userParam.clone());
    user.setEmail(emailField.getText());
    user.setHomepage(homepageField.getText());
    user.setInfo(infoArea.getText());
    user.setColor(new Color(redBar.getValue(), greenBar.getValue(), blueBar.getValue()));
    user.setNrOfMoods(getNrOfAvatarPanels() - 1);

    user.setBackAvatarURL(((Checkbox)webAvatarCheck.firstElement()).getState() ? ((TextField)avatarURLField.firstElement()).getText() : ChatApplet.SERVER_POTRAIT_FILENAME[((Choice)serverAvatarChoice.firstElement()).getSelectedIndex()][0]);
    for (int i = 1; i < getNrOfAvatarPanels(); i++) {
      user.setAvatarURL(i - 1, ((Checkbox)webAvatarCheck.elementAt(i)).getState() || i > ChatRepository.PREDEFINED_NR_OF_MOODS ? ((TextField)avatarURLField.elementAt(i)).getText() : ChatApplet.SERVER_POTRAIT_FILENAME[((Choice)serverAvatarChoice.elementAt(i)).getSelectedIndex()][i]);
      user.setMoodKeywords(i - 1, ((TextField)avatarKeywordsField.elementAt(i)).getText());
      user.setMoodName(i - 1, ((TextField)moodNameField.elementAt(i)).getText());
      try {
        user.setMoodTimeout(i - 1, Integer.parseInt(((TextField)moodTimeoutField.elementAt(i)).getText()));
      }
      catch (NumberFormatException excpt) {
        user.setMoodTimeout(i - 1, 0);
      }
    }
    if (!(oldPasswordField.getText().equals("") && newPasswordField1.getText().equals("") && newPasswordField2.getText().equals(""))) {
      if (oldPasswordField.getText().equals("") || newPasswordField1.getText().equals("") || newPasswordField2.getText().equals("") || (!newPasswordField1.getText().equals(newPasswordField2.getText())) || (!oldPasswordField.getText().equals(userParam.getPassword()))) {
        chatApplet.setStatus("Password could not be changed: Invalid data");
        return;
      }
      else {
        user.setPassword(newPasswordField1.getText());
      }
    }
    chatApplet.updateUser(user, connected);
    chatApplet.showChat();
  }


/**
 * Adds a Panel for an additional avatar.
 */

  public void addAvatarPanel() {
    Component components[];

    super.addAvatarPanel();
    updateAvatarButtons();
    ((Checkbox)webAvatarCheck.lastElement()).addItemListener(this);
    ((Checkbox)serverAvatarCheck.lastElement()).addItemListener(this);
    ((FocusPanel)singleAvatarPanel.lastElement()).addFocusListener(this);
    ((FocusPanel)singleAvatarPanel.lastElement()).addMouseListener(this);
    components = ((FocusPanel)singleAvatarPanel.lastElement()).getComponents();
    for (int i = 0; i < components.length; i++) {
      components[i].addFocusListener(this);
      components[i].addMouseListener(this);
    }
  }


/**
 * Removes a Panel holding an avatar.
 *
 * @param avatarIndexParam      the index of the avatar to be removed
 */

  public void removeAvatarPanel(int avatarIndexParam) {
    super.removeAvatarPanel(avatarIndexParam);
    focusGained(new FocusEvent((FocusPanel)singleAvatarPanel.firstElement(), FocusEvent.FOCUS_GAINED));
    updateAvatarButtons();
  }


/**
 * Toggles the Enabled-state of the Buttons for adding and removing avatars,
 * depending on which avatar has the focus.
 */

 public void updateAvatarButtons() {
    if (removeAvatarButton != null)
      removeAvatarButton.setEnabled(focusedAvatarIndex() > ChatRepository.PREDEFINED_NR_OF_MOODS);
  }


/**
 * Invoked when a Component gains the keyboard focus.
 *
 * @param event      the FocusEvent
 */

  public void focusGained(FocusEvent event) {
    for (int i = 0; i < getNrOfAvatarPanels(); i++) {
      FocusPanel avtPanel = (FocusPanel)singleAvatarPanel.elementAt(i);
      if (avtPanel.isAncestorOf(event.getComponent())) {
        if (focusedAvatarIndex() != -1 ) {
          ((FocusPanel)singleAvatarPanel.elementAt(focusedAvatarIndex())).transferFocus();
        }
        avtPanel.requestFocus();
        updateAvatarButtons();
      }
    }
  }


/**
 * Invoked when a Component loses the keyboard focus.
 *
 * @param event      the FocusEvent
 */

  public void focusLost(FocusEvent event) {
  }


/**
 * Returns the index of the Panel which currently holds the focus.
 */

  public int focusedAvatarIndex() {
    for (int i = 0; i < getNrOfAvatarPanels(); i++) {
      if (((FocusPanel)singleAvatarPanel.elementAt(i)).hasFocus()) {
        return i;
      }
    }
    return -1;
  }


/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseClicked(MouseEvent event) {
  }


/**
 * Invoked when the mouse enters a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseEntered(MouseEvent event) {
  }


/**
 * Invoked when the mouse xits a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseExited(MouseEvent event) {
  }


/**
 * Invoked when a mouse button has been pressed on a component.
 *
 * @param event       the MouseEvent
 */

  public void mousePressed(MouseEvent event) {
    focusGained(new FocusEvent(event.getComponent(), FocusEvent.FOCUS_GAINED));
  }


/**
 * Invoked when a mouse button has been released on a component.
 *
 * @param event       the MouseEvent
 */

  public void mouseReleased(MouseEvent event) {
  }

}