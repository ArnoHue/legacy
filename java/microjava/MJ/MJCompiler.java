package MJ;

import java.io.*;
import MJ.SymTab.*;
import MJ.CodeGen.*;

/**
 * MJCompiler is a commandline tool for calling up the different stages of the compiler.
 *
 * @author      Arno Huetter
 */
public class MJCompiler {
    
  /**
   * Main method.
   *
   * @param args     commandline arguments
   */
  public static void main(String[] args) {
    
    FileInputStream in;
    FileOutputStream out;
    String outfile;
    
    if (args.length > 0) {
        int index;
        if ((index = args[0].lastIndexOf(".")) != -1) {
            outfile = args[0].substring(0, index) + ".class";
        }
        else {
            outfile = args[0] + ".class";
        }
      try {
        Token token;
        Scanner.init(in = new FileInputStream(new File(args[0])));
        Tab.init();
        Code.init();
        Parser.parse();
        in.close();
        Code.write(out = new FileOutputStream(new File(outfile)));
        out.close();
        // Tab.print();
        Errors.printStatistics();
      }
      catch (Exception excpt) {
        excpt.printStackTrace();
        System.out.println("Exception while compiling file '" + args[0] + "': " + excpt);
      }
    }
    else {
      System.out.println("Usage: java MJ.MJCompiler ClassName.java");
    }
  }
  
}
            
