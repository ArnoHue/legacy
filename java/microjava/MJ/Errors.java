package MJ;

public class Errors {
  
  public static final int MIN_ERROR_DISTANCE = 4;
  
  private static int nrOfErrors;
  private static int lastErrorTokenCount;
  private static int tokenCount;
  
  public static void reset() {
    nrOfErrors = 0;
    tokenCount = -1;
    lastErrorTokenCount = -1;
  }
    
  public static void count() {
    tokenCount++;
  }
  
  public static void println(int line, int col, String msg) {
    if (tokenCount >= (lastErrorTokenCount + MIN_ERROR_DISTANCE) && lastErrorTokenCount != -1) {
      System.out.println(msg + " at line " + line + ", col " + col);
      lastErrorTokenCount = tokenCount;
      nrOfErrors++;
    }
  }
  
  public static void printStatistics() {
    System.out.println(nrOfErrors + " errors overall");
  }
  
}