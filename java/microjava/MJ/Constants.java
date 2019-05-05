package MJ;

/**
 * This interface contains project-wide constants.
 *
 * @author      Arno Huetter
 */
public interface Constants {

  // Nonterminal symbol constants for fast access                          
  public static final int NONE = 0,
                          IDENT = 1,
                          NUMBER = 2,
                          CHARCONST = 3,
                          PLUS = 4,
                          MINUS = 5,
                          TIMES = 6,
                          SLASH = 7,
                          MODULO = 8,
                          EQUAL = 9,
                          NOT_EQUAL = 10,
                          GREATER = 11,
                          GREATER_EQUAL = 12,
                          LESS = 13,
                          LESS_EQUAL = 14,
                          AND = 15,
                          OR = 16,
                          ASSIGN = 17,
                          INC = 18,
                          DEC = 19,
                          SEMICOLON = 20,
                          COMMA = 21,
                          PERIOD = 22,
                          LEFT_PARENTHESIS = 23,
                          RIGHT_PARENTHESIS = 24,
                          LEFT_BRACKET = 25,
                          RIGHT_BRACKET = 26,
                          LEFT_BRACE = 27,
                          RIGHT_BRACE = 28,
                          BREAK = 29,
                          CASE = 30,
                          CLASS = 31,
                          ELSE = 32,
                          FINAL = 33,
                          IF = 34,
                          NEW = 35,
                          PRINT = 36,
                          READ = 37,
                          RETURN = 38,
                          VOID = 39,
                          WHILE = 40,
                          EOF = 41;
                          
  // Special character definitions (partly runtime dependent)
  public static final char EOL_CHAR = System.getProperty("line.separator").charAt(1),
                           EOF_CHAR = (char)-1;

}