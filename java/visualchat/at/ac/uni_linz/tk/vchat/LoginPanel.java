package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;


/**
 * This Panel is displayed in the original browser window. It is being used for User
 * authentification.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class LoginPanel extends Panel implements ActionListener, ItemListener {

  private Thread checkThread;

  private ChatApplet chatApplet;

  private FramedPanel connectionPanel, userPanel, loginAsPanel;
  private InsetsPanel buttonPanel;
  private Label hostLabel1, hostLabel2, portLabel, portNrLabel, nameLabel, passwordLabel1, passwordLabel2, statusLabel;
  private CheckboxGroup loginCheckboxGroup;
  protected Checkbox newUserCheckbox, existingUserCheckbox;
  protected TextField nameTextField, passwordTextField1, passwordTextField2;
  protected Button connectButton, disconnectButton;

  public static final int MODE_DEFAULT = 0;
  public static final int MODE_NEW_USERS = 1;
  public static final int MODE_EXISTING_USERS = 2;

/**
 * Constructs the LoginPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 */

  public LoginPanel(ChatApplet chatParam, int mode) {

    GridBagConstraints constraints;
    chatApplet = chatParam;

    connectionPanel = new FramedPanel("Connection", ChatRepository.INSETS);
    connectionPanel.setLayout(new GridBagLayout());

    hostLabel1 = new Label("Host:", Label.RIGHT);
    portLabel = new Label("Port:", Label.RIGHT);
    hostLabel2 = new Label(chatApplet.getHost());
    portNrLabel = new Label(new Integer(chatApplet.getDefaultPort()).toString());

    ChatUtil.addWithBeginningConstraints(connectionPanel, hostLabel1);
    ChatUtil.addWithRemainingConstraints(connectionPanel, hostLabel2);

    ChatUtil.addWithBeginningConstraints(connectionPanel, portLabel);
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    ChatUtil.addWithConstraints(connectionPanel, portNrLabel, constraints);

    userPanel = new FramedPanel("User", ChatRepository.INSETS);
    userPanel.setLayout(new GridBagLayout());
    loginAsPanel = new FramedPanel("Login as", ChatRepository.INSETS);
    loginAsPanel.setLayout(new GridBagLayout());

    loginCheckboxGroup = new CheckboxGroup();
    newUserCheckbox = new Checkbox("New User", loginCheckboxGroup, mode != MODE_EXISTING_USERS);
    existingUserCheckbox = new Checkbox("Existing User", loginCheckboxGroup, mode == MODE_EXISTING_USERS);

    newUserCheckbox.setEnabled(mode != MODE_EXISTING_USERS);
    existingUserCheckbox.setEnabled(mode != MODE_NEW_USERS);

    nameLabel = new Label("Name:", Label.RIGHT);
    passwordLabel1 = new Label("Password:", Label.RIGHT);
    passwordLabel2 = new Label("Password (Verification):", Label.RIGHT);
    nameTextField = new TextField(30);
    passwordTextField1 = new TextField(10);
    passwordTextField1.setEchoChar('*');
    passwordTextField2 = new TextField(10);
    passwordTextField2.setEchoChar('*');

    ChatUtil.addWithRemainingConstraints(loginAsPanel, newUserCheckbox);
    ChatUtil.addWithRemainingConstraints(loginAsPanel, existingUserCheckbox);

    ChatUtil.addWithBeginningConstraints(userPanel, nameLabel);
    ChatUtil.addWithRemainingConstraints(userPanel, nameTextField);
    ChatUtil.addWithBeginningConstraints(userPanel, passwordLabel1);
    ChatUtil.addWithRemainingConstraints(userPanel, passwordTextField1);
    ChatUtil.addWithBeginningConstraints(userPanel, passwordLabel2);
    ChatUtil.addWithRemainingConstraints(userPanel, passwordTextField2);

    buttonPanel = new InsetsPanel(ChatRepository.INSETS);
    buttonPanel.setLayout(new BorderLayout());

    connectButton = new Button("Connect");
    disconnectButton = new Button("Disconnect");
    statusLabel = new Label("Status:");

    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = ChatRepository.SMALL_INSETS;
    constraints.weightx = 1.0;
    constraints.gridwidth = 1;

    ChatUtil.addWithConstraints(buttonPanel, connectButton, constraints);
    constraints.anchor = GridBagConstraints.NORTHEAST;
    ChatUtil.addWithConstraints(buttonPanel, disconnectButton, constraints);
    constraints.gridy = 2;
    constraints.gridwidth = 2;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.fill = GridBagConstraints.BOTH;
    ChatUtil.addWithConstraints(buttonPanel, statusLabel, constraints);
    constraints.gridy = 3;
    constraints.fill = GridBagConstraints.NONE;
    constraints.anchor = GridBagConstraints.NORTHEAST;
    ChatUtil.addWithConstraints(buttonPanel, chatApplet.getLogo(), constraints);

    passwordLabel2.setEnabled(newUserCheckbox.getState());
    passwordTextField2.setEnabled(newUserCheckbox.getState());

    connectButton.setEnabled(true);
    disconnectButton.setEnabled(false);

    setLayout(new BorderLayout());
    setFont(ChatRepository.STANDARD_FONT);
    add(connectionPanel, "North");
    add(userPanel, "Center");
    add(buttonPanel, "South");
    add(loginAsPanel, "East");

    newUserCheckbox.addItemListener(this);
    existingUserCheckbox.addItemListener(this);

    connectButton.addActionListener(this);
    disconnectButton.addActionListener(this);

  }


/**
 * Invoked when an action occurs.
 *
 * param @event      the ActionEvent that occured
 */

  public void actionPerformed(ActionEvent event) {
    int port;

    if (event.getSource() == connectButton) {

      try {
        port = new Integer(portNrLabel.getText()).intValue();
      }
      catch (NumberFormatException excpt) {
        port = chatApplet.getDefaultPort();
      }

      portNrLabel.setText(new Integer(port).toString());

      if (nameTextField.getText().equals("")) {
        chatApplet.setStatus("Can't connect: Empty name", true);
        nameTextField.requestFocus();
      }
      else if (nameTextField.getText().indexOf('/') != -1 || nameTextField.getText().indexOf('\\') != -1 || nameTextField.getText().indexOf(':') != -1) {
        chatApplet.setStatus("Can't connect: Invalid name", true);
        nameTextField.requestFocus();
      }
      else if (passwordTextField1.getText().equals("")) {
        chatApplet.setStatus("Can't connect: Empty password", true);
      }
      else if (newUserCheckbox.getState() && passwordTextField2.getText().equals("")) {
        chatApplet.setStatus("Can't connect: Empty verification password", true);
      }
      else if (newUserCheckbox.getState() && !passwordTextField1.getText().equals(passwordTextField2.getText())) {
        chatApplet.setStatus("Can't connect: Unmatching verification password", true);
      }
      else {
        if (newUserCheckbox.getState()) {
          chatApplet.getClient().connectAsNewUser(new User(nameTextField.getText(), passwordTextField1.getText()), port);
        }
        else if (existingUserCheckbox.getState()) {
          chatApplet.getClient().connectAsExistingUser(nameTextField.getText(), passwordTextField1.getText(), port);
        }
      }
    }
    else if (event.getSource() == disconnectButton) {
      chatApplet.getClient().disconnect();
      chatApplet.setFrameVisibility(false);
    }
  }

  public void setStatus(String statusString) {
    statusLabel.setText("Status: " + statusString);
  }


/**
 * Invoked when an item's state has been changed.
 *
 * param @event      the ItemEvent that occured
 */

  public void itemStateChanged(ItemEvent event) {
    passwordLabel2.setEnabled(newUserCheckbox.getState());
    passwordTextField2.setEnabled(newUserCheckbox.getState());
  }

  public void setConnected(boolean connected) {
    connectButton.setEnabled(!connected);
    disconnectButton.setEnabled(connected);
  }

}