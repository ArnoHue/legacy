package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.applet.*;


/**
 * An InfoPanel includes a FramedPanel that can be filled with several lines of
 * formatted text.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class InfoPanel extends Panel  {

  private FramedPanel framedPanel1, framedPanel2;
  private ChatApplet chatApplet;
  private TextArea htmlText;

/**
 * Constructs the InfoPanel.
 */

  public InfoPanel(ChatApplet chatParam) {

    String[] labelText = { "Visual Chat V2.73",
                           "",
                           "(C)opyright",
                           "Institute for Technical Computer Science and Telematics, Telecooperation Department, University of Linz / Austria",
                           "Developed within a practical study in summerterm 1998",
                           "",
                           "Basic concept",
                           "Theodorich Kopetzky",
                           "",
                           "Implementation",
                           "Arno Hütter",
                           "",
                           "The Visual Chat Homepage",
                           "http://www.weirdoz.org/visualchat" };
    boolean[] bold = { true, false, true, false, false, false, true, false, false, true, false, false, true, false };
    int[] alignment = { Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT, Label.LEFT };
    String[] url = { "",
                     "",
                     "",
                     "http://www.tk.uni-linz.ac.at",
                     "",
                     "",
                     "",
                     "http://www.tk.uni-linz.ac.at/~theo",
                     "",
                     "",
                     "http://www.geocities.com/arno_huetter",
                     "",
                     "",
                     "http://www.weirdoz.org/visualchat"};
    Component label;

    chatApplet = chatParam;

    setFont(ChatRepository.STANDARD_FONT);
    framedPanel1 = new FramedPanel(labelText[0], ChatRepository.INSETS);
    framedPanel1.setLayout(new GridLayout(labelText.length, 1));

    for (int i = 1; i < labelText.length; i++) {
      if (url[i].equals(""))
        label = new Label(labelText[i], alignment[i]);
      else
        label = new URLLabel(labelText[i], url[i], chatApplet );

      label.setFont(new Font(ChatRepository.STANDARD_FONT.getName(), bold[i] ? Font.BOLD : Font.PLAIN, ChatRepository.STANDARD_FONT.getSize()));
      framedPanel1.add(label);
    }

    framedPanel2 = new FramedPanel("Free Visual Chat Client", ChatRepository.INSETS);
    framedPanel2.setLayout(new BorderLayout());
    framedPanel2.add(new Label("Use the following HTML-code:"), "North");

    htmlText = new TextArea();
    htmlText.setText("<TABLE align=\"center\" border=\"5\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                     "  <TR>\n" +
                     "    <TD>\n" +
                     "      <APPLET CODE=\"chat.ChatApplet\" ARCHIVE=\"http://www.weirdoz.org/visualchat/chat.jar\" CODEBASE=\"http://www.weirdoz.org/visualchat\" WIDTH=480 HEIGHT=400>\n" +
                     "        <PARAM NAME=\"CabBase\" VALUE=\"http://www.weirdoz.org/visualchat/chat.cab\">\n" +
                     "        <PARAM NAME=\"Port\" VALUE=\"5555\">\n" +
                     "        <PARAM NAME=\"Room\" VALUE=\"Lobby\">\n" +
                     "      </APPLET>\n" +
                     "    </TD>\n" +
                     "  </TR>\n" +
                     "</TABLE>\n");
    framedPanel2.add(htmlText, "Center");
    setLayout(new GridLayout(2, 1));
    add(framedPanel1);
    add(framedPanel2);
  }

}