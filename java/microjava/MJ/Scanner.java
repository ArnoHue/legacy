package MJ;

import java.util.*;
import java.io.*;

/**
 * Abstract class that provides static fields and methods for scanning an 
 * InputStream and delivering nonterminal symbols as Tokens.
 *
 * @author      Arno Huetter
 */
public abstract class Scanner implements Constants {
 
  // Symbol name and keyword definitions -  we can easily change a keyword here
  private static final Object[][] SYMBOL_TABLE = {
    { new Integer(NONE),  "none",  "" },
    { new Integer(IDENT), "ident", "" },
    { new Integer(NUMBER), "number", "" },
    { new Integer(CHARCONST), "charconst", "" },
    { new Integer(PLUS), "plus", "" },
    { new Integer(MINUS), "minus", "" },
    { new Integer(TIMES), "times", "" },
    { new Integer(SLASH), "slash", "" },
    { new Integer(MODULO), "modulo", "" },
    { new Integer(EQUAL), "equal", "" },
    { new Integer(NOT_EQUAL), "not_equal", "" },
    { new Integer(GREATER), "greater", "" },
    { new Integer(GREATER_EQUAL), "greater_equal", "" },
    { new Integer(LESS), "less", "" },
    { new Integer(LESS_EQUAL), "less_equal", "" },
    { new Integer(AND), "and", "" },
    { new Integer(OR), "or", "" },
    { new Integer(ASSIGN), "assign", "" },
    { new Integer(INC), "inc", "" },
    { new Integer(DEC), "dec", "" },
    { new Integer(SEMICOLON), "semicolon", "" },
    { new Integer(COMMA), "comma", "" },
    { new Integer(PERIOD), "period", "" },
    { new Integer(LEFT_PARENTHESIS), "left parenthesis", "" },
    { new Integer(RIGHT_PARENTHESIS), "right parenthesis", "" },
    { new Integer(LEFT_BRACKET), "left bracket", "" },
    { new Integer(RIGHT_BRACKET), "right bracket", "" },
    { new Integer(LEFT_BRACE), "left brace", "" },
    { new Integer(RIGHT_BRACE), "right brace", "" },
    { new Integer(BREAK), "break", "break" },
    { new Integer(CASE), "case", "case" },
    { new Integer(CLASS), "class", "class" },
    { new Integer(ELSE), "else", "else" },
    { new Integer(FINAL), "final", "final" },
    { new Integer(IF), "if", "if" },
    { new Integer(NEW), "new", "new" },
    { new Integer(PRINT), "print", "print" },
    { new Integer(READ), "read", "read" },
    { new Integer(RETURN), "return", "return" },
    { new Integer(VOID), "void", "void" },
    { new Integer(WHILE), "while", "while" },
    { new Integer(EOF), "eof", "" } };
    
  private static Hashtable symbols;
  private static Hashtable keywords;
  
  public static int line;
  public static int col;
  private static BufferedInputStream in;
  private static char ch;
  
  static {
    Symbol symbol;
    Vector vector;
    symbols = new Hashtable();
    keywords = new Hashtable();
    vector = new Vector();
    
    // Collect information about symbols
    for (int i = 0; i < SYMBOL_TABLE.length; i++) {
      symbol = new Symbol(((Integer)SYMBOL_TABLE[i][0]).intValue(), (String)SYMBOL_TABLE[i][1], (String)SYMBOL_TABLE[i][2]);
      symbols.put(new Integer(symbol.kind), symbol);
      if (symbol.keyword != null && symbol.keyword.length() > 0) {
        vector.addElement(symbol);
      }
    }
    
    /* Transform the Vector with keyword symbols into a Hashtable for faster access,
       as by now we know the optimal size of the Hashtable */
    keywords = new Hashtable(vector.size());
    for (int i = 0; i < vector.size(); i++) {
      keywords.put(((Symbol)vector.elementAt(i)).keyword, new Integer(((Symbol)vector.elementAt(i)).kind));
    }
  }
  
  /**
   * Initializes the Scanner.
   *
   * @param s      the InputStream to read from
   */
  public static void init(InputStream s) {
    // Let's allow all kind of InputStreams, and buffer them here
    in = new BufferedInputStream(s);
    line = 1;
    col = 0;
  }
  
  /**
   * Reads the next character from the InputStream. The character read is stored
   * in the private field ch.
   */
  private static void nextChar() {
    try {
      ch = (char)in.read();
      if (ch == EOL_CHAR) {
        line++;
        col = 0;
      }
      else {
        // Just for testing...
        // System.out.print(ch + "");
        col++;
      }
    }
    catch (IOException excpt) {
      Errors.println(line, col, "Exception while reading from input stream: " + excpt);
    }
  }
  
  /**
   * Reads the next Token as a language keyword or identifier. The first character
   * should be available in ch when this method is called.
   *
   * @return      the Token read
   */
  private static Token readName() {
    Token token;
    token = createToken();
    token.string = "";
    do {
      token.string += ch;
      nextChar();
    } while (isAlpha() || isNumeric() || ch == '_');
    if (keywords.containsKey(token.string)) {
      token.kind = ((Integer)keywords.get(token.string)).intValue();
    }
    else {
      token.kind = IDENT;
    }
    return token;
  }
  
  /**
   * Reads the next Token as a number. The first character should be available in ch
   * when this method is called.
   *
   * @return      the Token read
   */
  private static Token readNumber() {
    Token token;
    String string;
    token = createToken();
    string = "";
    do {
      string += ch;
      nextChar();
    } while (isNumeric());
    try {
      token.val = Integer.parseInt(string);
      token.kind = NUMBER;
    }
    catch (NumberFormatException excpt) {
      Errors.println(line, col, "Error when converting to int: " + string);
    }
    return token;
  }
  
  /**
   * Reads the next Token as a character constant. The first character "'" should be
   * available in ch when this method is called.
   *
   * @return      the Token read
   */
  private static Token readCharConst() {
    Token token;
    String string;
    token = createToken();
    string = "";
    
    nextChar();
    if (ch != '\'' && ch != EOL_CHAR && ch != EOF_CHAR) {
      string += ch;
    }
    nextChar();

    if (ch == '\'' && string.length() > 0) {
      token.kind = CHARCONST;
      token.string = string;
      token.val = string.length() > 0 ? string.charAt(0) : 0;
      nextChar();
    }
    else {
      Errors.println(line, col, "Error when reading character constant: " + string);
    }
    return token;
  }

  /**
   * Checks the lookahead character whether it is a letter.
   *
   * @return      true if ch is a letter, false if not
   */
  private static boolean isAlpha() {
    return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
  }

  /**
   * Checks the lookahead character whether it is a digit.
   *
   * @return      true if ch is a digit, false if not
   */
  private static boolean isNumeric() {
    return (ch >= '0' && ch <= '9');
  }
  
  /**
   * Creates a default Token within the given context
   *
   * @return      the default Token
   */
  private static Token createToken() {
    Token token;
    token = new Token();
    token.kind = NONE;
    token.line = line;
    token.col = col;
    Errors.count();
    return token;
  }
  
  /**
   * reads from the InputStream and returns the next Token.
   *
   * @return      the next Token
   */
  public static Token nextToken() {
    Token token;
    while (ch <= ' ') {
      nextChar();
    }
    
    if (isAlpha()) {
      token = readName();
    }
    else if (isNumeric()) {
      token = readNumber();
    }
    else {
      token = createToken();
      switch(ch) {
        case '\'':
          token = readCharConst();
          break;
        case '+':
          nextChar();
          if (ch == '+') {
            token.kind = INC;
            nextChar();
          }
          else {
            token.kind = PLUS;
          }
          break;
        case '-':
          nextChar();
          if (ch == '-') {
            token.kind = DEC;
            nextChar();
          }
          else {
            token.kind = MINUS;
          }
          break;
        case '*':
          token.kind = TIMES;
          nextChar();
          break;
        case '/':
          nextChar();
          if (ch == '/') {
            do {
              nextChar();
            } while (ch != EOL_CHAR && ch != EOF_CHAR);
            token = nextToken();
          }
          else {
            token.kind = SLASH;
          }
          break;
        case '%':
          token.kind = MODULO;
          nextChar();
          break;
        case '=':
          nextChar();
          if (ch == '=') {
            token.kind = EQUAL;
            nextChar();
          }
          else {
            token.kind = ASSIGN;
          }
          break;
        case '!':
          nextChar();
          if (ch == '=') {
            token.kind = NOT_EQUAL;
            nextChar();
          }
          break;
        case '>':
          nextChar();
          if (ch == '=') {
            token.kind = GREATER_EQUAL;
            nextChar();
          }
          else {
            token.kind = GREATER;
          }
          break;
        case '<':
          nextChar();
          if (ch == '=') {
            token.kind = LESS_EQUAL;
            nextChar();
          }
          else {
            token.kind = LESS;
          }
          break;
        case '&':
          nextChar();
          if (ch == '&') {
            token.kind = AND;
            nextChar();
          }
          break;
        case '|':
          nextChar();
          if (ch == '|') {
            token.kind = OR;
            nextChar();
          }
          break;
        case ';':
          token.kind = SEMICOLON;
          nextChar();
          break;
        case ',':
          token.kind = COMMA;
          nextChar();
          break;
        case '.':
          token.kind = PERIOD;
          nextChar();
          break;
        case '(':
          token.kind = LEFT_PARENTHESIS;
          nextChar();
          break;
        case ')':
          token.kind = RIGHT_PARENTHESIS;
          nextChar();
          break;
        case '[':
          token.kind = LEFT_BRACKET;
          nextChar();
          break;
        case ']':
          token.kind = RIGHT_BRACKET;
          nextChar();
          break;
        case '{':
          token.kind = LEFT_BRACE;
          nextChar();
          break;
        case '}':
          token.kind = RIGHT_BRACE;
          nextChar();
          break;
        case EOF_CHAR:
          token.kind = EOF;
          break;
        default:
          Errors.println(line, col, "Invalid character: " + ch);
          nextChar();
      }
    }
    return token;
  }
  
  /**
   * Returns the name of a Token.
   *
   * @param kindParam      the kind of the Token
   * @return               the name of the Token
   */
  public static String getSymbolName(int kindParam) {
    if (symbols.containsKey(new Integer(kindParam))) {
      return ((Symbol)symbols.get(new Integer(kindParam))).name;
    }
    else {
      return "";
    }
  }
  
}

                            