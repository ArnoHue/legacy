package MJ;

import java.io.*;

/**
 * A TestDriver is a commandline tool for calling up the Scanner.
 *
 * @author      Arno Huetter
 */
public class TestDriver {
    
  /**
   * Starting method.
   *
   * @param args     commandline arguments
   */
  public static void main(String[] args) {
    
    FileInputStream in;
    
    if (args.length > 0) {
      try {
        Token token;
        String col1, col2, col3;
        Scanner.init(in = new FileInputStream(new File(args[0])));
        Parser.parse();
        in.close();
      }
      catch (Exception excpt) {
        System.out.println("Exception while parsing file '" + args[0] + "': " + excpt);
      }
    }
    else {
      System.out.println("Usage: java MJ.TestDrive ClassName.java");
    }
  }
  
  /**
   * Fills up a String with blanks or cuts it to a certain length.
   *
   * @param text        the original String
   * @param length      the required length of the String
   * @return            a String of the required length
   */
  private static String pad(String text, int length) {
    if (text.length() < length) {
      for (int i = text.length(); i < length; i++) {
        text += " ";
      }
      return text;
    }
    else {
      return text.substring(length);
    }
  }
  
}
            
