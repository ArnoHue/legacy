package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;


/**
 * A UserPanel is used for displaying and editing User-data, including color
 * settings, password changement, Images and keywords for different moods.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserPanel extends Panel {

  static final double PORTRAIT_SCALE = 0.6;

  protected ChatApplet chatApplet;

  protected InsetsPanel userDataPanel;
  protected Panel avatarPanelLevel2, colorPasswordPanel;
  protected FramedPanel namePanel, infoPanel, colorPanel, passwordPanel, avatarPanelLevel1;
  protected TextField nameField, roomField, loginDateField, emailField, homepageField, oldPasswordField, newPasswordField1, newPasswordField2;
  protected TextArea infoArea;
  protected Vector singleAvatarPanel, avatarURLField, avatarKeywordsField, avatarCanvas, iconCanvas, webAvatarCheck, serverAvatarCheck, avatarGroup, serverAvatarChoice, moodNameField, moodTimeoutField;
  protected PortraitCanvas currentPortraitCanvas;
  protected Scrollbar redBar, greenBar, blueBar;
  protected ScrollPane avatarScrollPane;


/**
 * Constructs the UserPanel.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                Users
 */

  public UserPanel(ChatApplet chatParam) {

    super();

    Image image;
    GridBagConstraints constraints;

    Label label;

    chatApplet = chatParam;

    namePanel = new FramedPanel("User", ChatRepository.INSETS);
    namePanel.setLayout(new GridBagLayout());
    currentPortraitCanvas = new PortraitCanvas();
    currentPortraitCanvas.setSize((int)(ChatRepository.PORTRAIT_DIMENSION.width * PORTRAIT_SCALE), (int)(ChatRepository.PORTRAIT_DIMENSION.height * PORTRAIT_SCALE));
    ChatUtil.addWithRemainingConstraints(namePanel, currentPortraitCanvas, ChatRepository.SMALL_INSETS);
    ChatUtil.addWithBeginningConstraints(namePanel, new Label("Name: ", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(namePanel, nameField = new TextField(), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithBeginningConstraints(namePanel, new Label("Room: ", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(namePanel, roomField = new TextField(), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithBeginningConstraints(namePanel, new Label("Online since: ", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(namePanel, loginDateField = new TextField(), ChatRepository.SMALL_INSETS);
    nameField.setEditable(false);
    roomField.setEditable(false);
    loginDateField.setEditable(false);
    currentPortraitCanvas.setDrawFrame(true);

    infoPanel = new FramedPanel("Info", ChatRepository.INSETS);
    infoPanel.setLayout(new GridBagLayout());
    ChatUtil.addWithRemainingConstraints(infoPanel, new Label("E-Mail:", Label.LEFT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(infoPanel, emailField = new TextField(), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(infoPanel, new Label("Homepage:", Label.LEFT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(infoPanel, homepageField = new TextField(), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(infoPanel, new Label("Info:", Label.LEFT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(infoPanel, infoArea = new TextArea("", 3, 25, TextArea.SCROLLBARS_NONE), ChatRepository.SMALL_INSETS);

    colorPanel = new FramedPanel("Color", ChatRepository.INSETS);
    colorPanel.setLayout(new GridBagLayout());
    ChatUtil.addWithBeginningConstraints(colorPanel, new Label("Red: ", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(colorPanel, redBar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, ChatRepository.MAX_COLOR_VALUE), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithBeginningConstraints(colorPanel, new Label("Green: ", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(colorPanel, greenBar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, ChatRepository.MAX_COLOR_VALUE), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithBeginningConstraints(colorPanel, new Label("Blue: ", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(colorPanel, blueBar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, ChatRepository.MAX_COLOR_VALUE), ChatRepository.SMALL_INSETS);

    passwordPanel = new FramedPanel("Password", ChatRepository.INSETS);
    passwordPanel.setLayout(new GridBagLayout());
    ChatUtil.addWithBeginningConstraints(passwordPanel, new Label("Old:", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(passwordPanel, oldPasswordField = new TextField(), ChatRepository.SMALL_INSETS);
    oldPasswordField.setEchoChar('*');
    ChatUtil.addWithBeginningConstraints(passwordPanel, new Label("New:", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(passwordPanel, newPasswordField1 = new TextField(), ChatRepository.SMALL_INSETS);
    newPasswordField1.setEchoChar('*');
    ChatUtil.addWithBeginningConstraints(passwordPanel, new Label("New (Verify):", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(passwordPanel, newPasswordField2 = new TextField(), ChatRepository.SMALL_INSETS);
    newPasswordField2.setEchoChar('*');

    colorPasswordPanel = new Panel();
    colorPasswordPanel.setLayout(new GridLayout(2, 1));
    if (chatApplet.getBooleanParam("ExtendedAvatarConfigVisibility", true)) {
      colorPasswordPanel.add(colorPanel);
    }
    colorPasswordPanel.add(passwordPanel);

    userDataPanel = new InsetsPanel();
    userDataPanel.setLayout(new GridLayout(1, 3));
    userDataPanel.add(namePanel);
    userDataPanel.add(infoPanel);
    userDataPanel.add(colorPasswordPanel);

    avatarCanvas = new Vector();
    iconCanvas = new Vector();
    singleAvatarPanel = new Vector();
    avatarURLField = new Vector();
    avatarKeywordsField = new Vector();
    moodNameField = new Vector();
    moodTimeoutField = new Vector();

    webAvatarCheck = new Vector();
    serverAvatarCheck = new Vector();
    avatarGroup = new Vector();
    serverAvatarChoice = new Vector();

    avatarPanelLevel2 = new Panel();

    avatarScrollPane = new ScrollPane();
    avatarScrollPane.add(avatarPanelLevel2);

    for (int i = 0; i <= ChatRepository.PREDEFINED_NR_OF_MOODS; i++) {
      addAvatarPanel();
    }

    avatarPanelLevel1 = new FramedPanel("Avatars", ChatRepository.INSETS);
    avatarPanelLevel1.setLayout(new BorderLayout());
    avatarPanelLevel1.add(avatarScrollPane, "Center");

    setLayout(new BorderLayout());
    setFont(ChatRepository.STANDARD_FONT);

    add(userDataPanel, "North");
    add(avatarPanelLevel1, "Center");
  }


/**
 * Displays the data of a given User within the UserPanel.
 *
 * @param userParam      the User whose data should be displayed
 */

  public void showUser(User userParam) {
    Image image;

    currentPortraitCanvas.setImage(chatApplet.getUserAvatar(userParam.getId()));
    currentPortraitCanvas.setFrameColor(userParam.getColor());
    nameField.setText(userParam.getName());
    roomField.setText(chatApplet.getRoom(userParam.getRoom()).getName());
    loginDateField.setText(DateFormat.getTimeInstance().format(userParam.getLoginDate()));
    emailField.setText(userParam.getEmail());
    homepageField.setText(userParam.getHomepage());
    infoArea.setText(userParam.getInfo());
    redBar.setValue(userParam.getColor().getRed());
    greenBar.setValue(userParam.getColor().getGreen());
    blueBar.setValue(userParam.getColor().getBlue());

//    removeAllAvatarPanels();

    for (int i = ChatRepository.PREDEFINED_NR_OF_MOODS; i > userParam.getNrOfMoods(); i++) {
      removeLastAvatarPanel();
    }

    for (int i = 0; i <= userParam.getNrOfMoods(); i++) {
      if (i >= getNrOfAvatarPanels()) {
        addAvatarPanel();
      }
      if (i == 0) {
        ((PortraitCanvas)avatarCanvas.firstElement()).setImage(chatApplet.getUserBackAvatar(userParam.getId()));
        ((TextField)avatarURLField.firstElement()).setText(userParam.getBackAvatarURL());
      }
      else {
        ((PortraitCanvas)avatarCanvas.elementAt(i)).setImage(chatApplet.getUserAvatar(userParam.getId(), i - 1));
        ((TextField)avatarURLField.elementAt(i)).setText(userParam.getAvatarURL(i - 1));
        ((TextField)avatarKeywordsField.elementAt(i)).setText(userParam.getMoodKeywords(i - 1));
        ((TextField)moodNameField.elementAt(i)).setText(userParam.getMoodName(i - 1));
        ((TextField)moodTimeoutField.elementAt(i)).setText(new Integer(userParam.getMoodTimeout(i - 1)).toString());
      }
      toggleAvatarPanel(i);
    }
    currentPortraitCanvas.setFrameColor(userParam.getColor());
  }


/**
 * Removes the last (right-most) Panel which holds a User avatar.
 */

  public void removeLastAvatarPanel() {
    removeAvatarPanel(getNrOfAvatarPanels() - 1);
  }


/**
 * Removes a certain Panel which holds a User avatar. The first, predefined avatars
 * can not be removed.
 *
 * @param panelIndex      the index of the Panel to be removed
 */

  public void removeAvatarPanel(int panelIndex) {

    if (panelIndex > ChatRepository.PREDEFINED_NR_OF_MOODS && panelIndex < getNrOfAvatarPanels()) {
      avatarPanelLevel2.remove(((InsetsPanel)singleAvatarPanel.elementAt(panelIndex)));

      avatarCanvas.removeElementAt(panelIndex);
      singleAvatarPanel.removeElementAt(panelIndex);
      iconCanvas.removeElementAt(panelIndex);
      avatarGroup.removeElementAt(panelIndex);
      webAvatarCheck.removeElementAt(panelIndex);
      avatarURLField.removeElementAt(panelIndex);
      serverAvatarChoice.removeElementAt(panelIndex);
      avatarKeywordsField.removeElementAt(panelIndex);
      moodNameField.removeElementAt(panelIndex);
      moodTimeoutField.removeElementAt(panelIndex);

      avatarPanelLevel2.setLayout(new GridLayout(1, singleAvatarPanel.size()));
      if (System.getProperty("java.vendor").indexOf("Netscape") != -1) {
        avatarPanelLevel2.validate();
        avatarScrollPane.validate();
      }

      if (avatarCanvas.size() > ChatRepository.PREDEFINED_NR_OF_MOODS)
        avatarPanelLevel2.setSize(avatarPanelLevel2.getSize().width * (singleAvatarPanel.size() - 1) / singleAvatarPanel.size(), avatarPanelLevel2.getSize().height);
    }
  }


/**
 * Returns the number of Panels holding User avatars.
 */

  public int getNrOfAvatarPanels() {
    return avatarCanvas.size();
  }


/**
 * Removes all but the predefined Panels holding avatars.
 */

  public void removeAllAvatarPanels() {
    while (getNrOfAvatarPanels() > 0)
      removeLastAvatarPanel();
  }


/**
 * Adds a Panel for displaying a User avatar.
 */

  public void addAvatarPanel() {

    Label label1, label2, label3;

    avatarCanvas.addElement(new PortraitCanvas());
    ((Canvas)avatarCanvas.lastElement()).setSize((int)(ChatRepository.PORTRAIT_DIMENSION.width * PORTRAIT_SCALE / 2), (int)(ChatRepository.PORTRAIT_DIMENSION.height * PORTRAIT_SCALE / 2));

    singleAvatarPanel.addElement(new FocusPanel(ChatRepository.INSETS));
    ((InsetsPanel)singleAvatarPanel.lastElement()).setLayout(new GridBagLayout());

    iconCanvas.addElement(new PortraitCanvas());
    ((PortraitCanvas)iconCanvas.lastElement()).setSize(ChatRepository.ICON_DIMENSION);
    ((PortraitCanvas)iconCanvas.lastElement()).setImage(iconCanvas.size() == 1 ? chatApplet.getEmptyIcon() : (iconCanvas.size() <= (ChatRepository.PREDEFINED_NR_OF_MOODS + 1) ? chatApplet.getMoodIcon(iconCanvas.size() - 2) : chatApplet.getUnknownIcon()));

    avatarGroup.addElement(new CheckboxGroup());
    webAvatarCheck.addElement(new Checkbox("URL:", ((CheckboxGroup)avatarGroup.lastElement()), true));
    avatarURLField.addElement(new TextField(15));
    serverAvatarCheck.addElement(new Checkbox("Predefined:", ((CheckboxGroup)avatarGroup.lastElement()), true));
    serverAvatarChoice.addElement(new Choice());
    avatarKeywordsField.addElement(new TextField(15));
    moodNameField.addElement(new TextField(10));
    moodTimeoutField.addElement(new TextField(3));

    ChatUtil.addWithRemainingConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((PortraitCanvas)iconCanvas.lastElement()), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((Canvas)avatarCanvas.lastElement()), ChatRepository.SMALL_INSETS);

    ChatUtil.addWithBeginningConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), label1 = new Label("Name:", Label.RIGHT), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((TextField)moodNameField.lastElement()), ChatRepository.SMALL_INSETS);

    ChatUtil.addWithBeginningConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((Checkbox)webAvatarCheck.lastElement()), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((TextField)avatarURLField.lastElement()), ChatRepository.SMALL_INSETS);

    ChatUtil.addWithBeginningConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((Checkbox)serverAvatarCheck.lastElement()), ChatRepository.SMALL_INSETS);
    ChatUtil.addWithRemainingConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((Choice)serverAvatarChoice.lastElement()), ChatRepository.SMALL_INSETS);

    label2 = new Label("Keywords:", Label.RIGHT);
    label3 = new Label("Timeout (Seconds):", Label.RIGHT);

    if (chatApplet.getBooleanParam("ExtendedAvatarConfigVisibility", true)) {
      ChatUtil.addWithBeginningConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), label2, ChatRepository.SMALL_INSETS);
      ChatUtil.addWithRemainingConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((TextField)avatarKeywordsField.lastElement()), ChatRepository.SMALL_INSETS);

      ChatUtil.addWithBeginningConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), label3, ChatRepository.SMALL_INSETS);
      ChatUtil.addWithRemainingConstraints(((InsetsPanel)singleAvatarPanel.lastElement()), ((TextField)moodTimeoutField.lastElement()), ChatRepository.SMALL_INSETS);
    }
    for (int j = 0; j < ChatApplet.SERVER_POTRAIT_NAME.length && getNrOfAvatarPanels() <= ChatRepository.PREDEFINED_NR_OF_MOODS + 1; j++)
      ((Choice)serverAvatarChoice.lastElement()).add(ChatApplet.SERVER_POTRAIT_NAME[j]);

    avatarPanelLevel2.setLayout(new GridLayout(1, singleAvatarPanel.size()));
    avatarPanelLevel2.add(((InsetsPanel)singleAvatarPanel.lastElement()));
    avatarPanelLevel2.validate();
    avatarScrollPane.validate();

    if (avatarCanvas.size() == 1) {
      label1.setEnabled(false);
      label2.setEnabled(false);
      label3.setEnabled(false);
      ((TextField)moodNameField.lastElement()).setEditable(false);
      ((TextField)moodNameField.lastElement()).setText("Backside");
      ((TextField)avatarKeywordsField.lastElement()).setEditable(false);
      ((TextField)moodTimeoutField.lastElement()).setEditable(false);
    }
    else if (avatarCanvas.size() > ChatRepository.PREDEFINED_NR_OF_MOODS) {
      avatarPanelLevel2.setSize(avatarPanelLevel2.getSize().width * singleAvatarPanel.size() / (singleAvatarPanel.size() - 1), avatarPanelLevel2.getSize().height);
    }

    toggleLastAvatarPanel();
  }


/**
 * Toggles the last (right-most) a certain Panel Panel holding a User avatar depending on if the ChatClient is
 * connected to a ChatServer and on user input.
 */

  public void toggleLastAvatarPanel() {
    toggleAvatarPanel(getNrOfAvatarPanels() - 1);
  }


/**
 * Toggles a certain Panel holding a User avatar depending on if the ChatClient is
 * connected to a ChatServer and on user input.
 *
 * @param panelIndex      the index of the Panel to be toggled
 */

  public void toggleAvatarPanel(int panelIndex) {
    Checkbox webAvtCheck = (Checkbox)webAvatarCheck.elementAt(panelIndex);
    Choice svrAvtChoice = (Choice)serverAvatarChoice.elementAt(panelIndex);
    boolean connected = chatApplet.isConnected();
    webAvtCheck.setEnabled(connected);
    ((Checkbox)serverAvatarCheck.elementAt(panelIndex)).setEnabled(svrAvtChoice.getItemCount() > 0);
    webAvtCheck.setState(connected);
    svrAvtChoice.setEnabled(!connected);
    ((TextField)avatarURLField.elementAt(panelIndex)).setEditable(connected);
  }

}