package MJ;

/**
 * A Symbol contains information for a certain class of nonterminal symbols.
 *
 * @author      Arno Huetter
 */
public class Symbol {
    
  public int kind;
  public String name;
  public String keyword;
  
 /**
  * Constructor.
  *
  * @param kindParam         the kind of the Symbol
  * @param nameParam         the name of the Symbol
  * @param keywordParam      the keyword of the Symbol (in case it is a language 
  *                          keyword symbol)
  */
  public Symbol(int kindParam, String nameParam, String keywordParam) {
    kind = kindParam;
    name = nameParam;
    keyword = keywordParam;
  }
    
}