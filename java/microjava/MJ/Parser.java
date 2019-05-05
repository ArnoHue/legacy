package MJ;

import java.util.*;
import MJ.SymTab.*;
import MJ.CodeGen.*;

/**
 * The Parser analyzes the syntax of the stream of Tokens provided by the Scanner.
 *
 * @author      Arno Huetter
 */
public abstract class Parser implements Constants {
  
  private static Token t;
  private static Token la;
  private static int sy;

  private static final int[] FIRST_OF_STATEMENT = { IDENT, IF, WHILE, BREAK, RETURN, READ, PRINT, LEFT_BRACE };
  private static final int[] FIRST_OF_EXPR = { MINUS, IDENT, NUMBER, CHARCONST, NEW, LEFT_PARENTHESIS };
  private static final int[] FIRST_OF_RELOP = { EQUAL, NOT_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL };
  private static final int[] FIRST_OF_MULOP = { TIMES, SLASH, MODULO };
                          
  private static BitSet firstOfStatement;
  private static BitSet firstOfExpr;
  private static BitSet firstOfActPars;
  private static BitSet firstOfRelop;
  private static BitSet firstOfMulop;

  static {
    firstOfStatement = new BitSet();
    for (int i = 0; i < FIRST_OF_STATEMENT.length; i++) {
      firstOfStatement.set(FIRST_OF_STATEMENT[i]);
    }
    firstOfExpr = new BitSet();
    for (int i = 0; i < FIRST_OF_EXPR.length; i++) {
      firstOfExpr.set(FIRST_OF_EXPR[i]);
    }
    firstOfActPars = firstOfExpr;
    firstOfRelop = new BitSet();
    for (int i = 0; i < FIRST_OF_RELOP.length; i++) {
      firstOfRelop.set(FIRST_OF_RELOP[i]);
    }
    firstOfMulop = new BitSet();
    for (int i = 0; i < FIRST_OF_MULOP.length; i++) {
      firstOfMulop.set(FIRST_OF_MULOP[i]);
    }
  }

  public static void parse() {
    scan();
    Program();
  }

  private static void scan() {
    t = la;
    la = Scanner.nextToken();
    sy = la.kind;
  }

  private static void check(int expectedKind) {
    if (sy == expectedKind) {
      scan();
    }
    else {
      errorExpecting(expectedKind);
    }
  }

  private static void errorSemantics(String msg) {
    error("Semantic error: " + msg);
    System.exit(0);
  }

  private static void errorExpecting(String expectedKind) {
    error(expectedKind + " expected");
  }

  private static void errorExpecting(int expectedKind) {
    errorExpecting(Scanner.getSymbolName(expectedKind));
  }

  private static void errorExpecting(int expectedKind1, int expectedKind2) {
    errorExpecting(Scanner.getSymbolName(expectedKind1) + " or " + Scanner.getSymbolName(expectedKind2));
  }

  private static void errorExpecting(int expectedKind1, int expectedKind2, int expectedKind3) {
    errorExpecting(Scanner.getSymbolName(expectedKind1) + ", " + Scanner.getSymbolName(expectedKind2) + " or " + Scanner.getSymbolName(expectedKind3));
  }

  private static void errorExpecting(int expectedKind1, int expectedKind2, int expectedKind3, int expectedKind4) {
    errorExpecting(Scanner.getSymbolName(expectedKind1) + ", " + Scanner.getSymbolName(expectedKind2) + ", " + Scanner.getSymbolName(expectedKind3) + " or " + Scanner.getSymbolName(expectedKind4));
  }

  public static void error(String msg) {
    Errors.println(la.line, la.col, msg);
  }

  private static void Program() {
    Obj obj;
    int nrOfStatics = 0;
    check(CLASS);
    check(IDENT);
    obj = Tab.insert(Obj.TYPE, t.string);
    Tab.openScope();
    while (sy != LEFT_BRACE && sy != EOF) {
      if (sy == FINAL) {
        ConstDecl();
        nrOfStatics++;
      }
      else if (sy == IDENT) {
        VarDecl();
        nrOfStatics++;
      }
      else if (sy == CLASS) {
        ClassDecl();
      }
      else {
        errorExpecting(LEFT_BRACE, FINAL, IDENT, CLASS);
        do {
          scan();
        } while (sy != FINAL && sy != IDENT && sy != CLASS && sy != LEFT_BRACE && sy != EOF);
      }
    }
    Code.dataSize = nrOfStatics * 4;
    check(LEFT_BRACE);
    while (sy == IDENT || sy == VOID) {
      MethodDecl();
    }
    check(RIGHT_BRACE);
    Tab.closeScope();
  }

  private static void ConstDecl() {
    Struct struct;
    Obj obj;
    check(FINAL);
    struct = Type();
    check(IDENT);
    obj = Tab.insert(Obj.CON, t.string, struct);
    check(ASSIGN);
    if (sy != NUMBER && sy != CHARCONST) {
      errorExpecting(NUMBER, CHARCONST);
    }
    scan();
    obj.val = t.val;
    check(SEMICOLON);
  }
  
  private static Vector VarDecl() {
    Vector vec = new Vector();
    Struct struct;
    struct = Type();
    
    while (true) {
      check(IDENT);
      vec.addElement(ArrDecl(struct, t.string));
      if (sy == COMMA) {
        scan();
      }
      else {
        break;
      }
    }
    check(SEMICOLON);
    return vec;
 }

  private static void ClassDecl() {
    Struct struct;
    boolean first = true;
    check(CLASS);
    check(IDENT);
    Tab.insert(Obj.TYPE, t.string, struct = new Struct(Struct.CLASS));
    Tab.openScope();
    check(LEFT_BRACE);
    while(sy == IDENT) {
      if (first) {
        struct.fields = (Obj)VarDecl().elementAt(0);
        first = false;
      }
      else {
        VarDecl();
      }
    }
    check(RIGHT_BRACE);
    Tab.closeScope();
  }

  private static void MethodDecl() {
    Obj obj;
    Struct struct = null; 
    switch(sy) {
      case IDENT:
        struct = Type();
        break;
      case VOID:
        struct = Tab.noType;
        scan();
        break;
      default:
        errorExpecting(IDENT, VOID);
    }
    check(IDENT);
    obj = Tab.insert(Obj.METH, t.string, struct);
    obj.val = Code.pc;
    Tab.openScope();
    check(LEFT_PARENTHESIS);
    if (sy == IDENT) {
      obj.locals = FormPars();
      obj.level = obj.getNrOfLocals();
    }
    check(RIGHT_PARENTHESIS);
    if (obj.name.equals("main")) {
      if (obj.level == 0) {
        Code.mainPc = Code.pc;
      }
      else {
        errorSemantics("Method signature main() expected");
      }
    }
    int nrOfVars = 0;
    while (sy == IDENT) {
      nrOfVars += VarDecl().size();
    }
    Code.put(Code.enter);
    Code.put(obj.level);
    Code.put(obj.level + nrOfVars);
    Block(null);
    Code.put(Code.exit);
    Code.put(Code.return_);
    Tab.closeScope();
  }

  private static Obj FormPars() {
    Obj obj;
    Struct struct;
    struct = Type();
    check(IDENT);
    obj = ArrDecl(struct, t.string);
    while(sy == COMMA) {
      scan();
      struct = Type();
      check(IDENT);
      ArrDecl(struct, t.string);
    }
    return obj;
  }
  
  private static Obj ArrDecl(Struct struct, String string) {
    Obj obj;
    if (sy == LEFT_BRACKET) {
      obj = Tab.insert(Obj.VAR, string, new Struct(Struct.ARR, struct));
      scan();
      check(RIGHT_BRACKET);
    }
    else {
      obj = Tab.insert(Obj.VAR, string, struct);
    }
    return obj;
  }

  private static Struct Type() {
    check(IDENT);
    return Tab.find(t.string).type;
  }

  private static void Statement(Label breakLab) {
    Item x, y;
    Label breakLab1;
    switch(sy) {
      case IDENT:
        x = Designator();
        switch(sy) {
          case ASSIGN:
            scan();
            y = Expr();
            if (x.type.isAssignable(y.type)) {
              Code.assign(x, y);
            }
            else {
              errorSemantics("Incompatible assignment types");
            }
            break;
          case LEFT_PARENTHESIS:
            handleMethodCall(x);
            check(RIGHT_PARENTHESIS);
            break;
          case INC:
          case DEC:
            Code.load(x);
            Code.loadConst(sy == INC ? 1 : -1);
            Code.put(Code.iadd);
            Code.assign(x);
            scan();
            break;
        }
        check(SEMICOLON);
        break;
      case IF:
        scan();
        check(LEFT_PARENTHESIS);
        x = Condition();
        Code.fJump(x);
        check(RIGHT_PARENTHESIS);
        Statement(breakLab);
        if (sy == ELSE) {
          Label end = new Label();
          Code.jump(end);
          x.fLabel.here();
          scan();
          Statement(breakLab);
          end.here();
        }
        else {
          x.fLabel.here();
        }
        break;
      case WHILE:
        Label top = new Label();
        breakLab1 = new Label();
        top.here();
        scan();
        check(LEFT_PARENTHESIS);
        x = Condition();
        Code.fJump(x);
        check(RIGHT_PARENTHESIS);
        Statement(breakLab1);
        Code.jump(top);
        breakLab1.here();
        x.fLabel.here();
        break;
      case BREAK:
        if (breakLab != null) {
          Code.jump(breakLab);
        }
        else {
          errorSemantics("Displaced break instruction");
        }
        scan();
        check(SEMICOLON);
        break;
      case RETURN:
        scan();
        if (firstOfExpr.get(sy)) {
          x = Expr();
          Code.load(x);
        }
        check(SEMICOLON);
        break;
      case READ:
        scan();
        check(LEFT_PARENTHESIS);
        x = Designator();
        if (x.type == Tab.intType) {
          Code.put(Code.iread);
        }
        else if (x.type == Tab.charType) {
          Code.put(Code.bread);
        }
        else {
          errorSemantics("Integer or char expected");
        }
        Code.assign(x);
        check(RIGHT_PARENTHESIS);
        check(SEMICOLON);
        break;
      case PRINT:
        int op = -1;
        scan();
        check(LEFT_PARENTHESIS);
        x = Expr();
        Code.load(x);
        if (x.type == Tab.intType) {
          op = Code.iprint;
        }
        else if (x.type == Tab.charType) {
          op = Code.bprint;
        }
        else {
          errorSemantics("Integer or char expected");
        }
        if (sy == COMMA) {
          scan();
          check(NUMBER);
          Code.loadConst(t.val);
        }
        else {
          Code.loadConst(0);
        }
        Code.put(op);
        check(RIGHT_PARENTHESIS);
        check(SEMICOLON);
        break;
      case LEFT_BRACE:
        Block(breakLab);
        break;
      default:
        errorExpecting("statement");
        break;
    }
  }
  
  private static void Block(Label breakLab) {
    check(LEFT_BRACE);
    while (sy != RIGHT_BRACE && sy != EOF) {
      if (firstOfStatement.get(sy)) {
        Statement(breakLab);
      }
      else {
        errorExpecting("statement");
        do {
          scan();
        } while (!firstOfStatement.get(sy) && sy != RIGHT_BRACE && sy != EOF);
      }
    }
    check(RIGHT_BRACE);
  }
  
  private static Vector ActPars() {
    Item x;
    Vector pars = new Vector();
    do {
      x = Expr();
      Code.load(x);
      pars.addElement(x);
      if (sy != COMMA) {
        break;
      }
      scan();
    } while (true);
    return pars;
  }

  private static Item Condition() {
    Item x, y;
    x = CondTerm();
    while (sy == OR) {
      Code.tJump(x);
      x.fLabel.here();
      scan();
      y = CondTerm();
      x.tLabel.add(y.tLabel);
      x.fLabel = y.fLabel;
      x.type = y.type;
      x.val = y.val;
    }
    return x;
  }

  private static Item CondTerm() {
    Item x, y;
    x = CondFact();
    while (sy == AND) {
      Code.fJump(x);
      x.tLabel.here();
      scan();
      y = CondFact();
      x.fLabel.add(y.fLabel);
      x.tLabel = y.tLabel;
      x.type = y.type;
      x.val = y.val;
    }
    return x;
  }

  private static Item CondFact() {
    Item x, y;
    int op;
    x = Code.load(Expr());
    op = Relop();
    y = Code.load(Expr());
    if (x.type.isComparable(y.type)) {
      x = new Item(Item.COND, op, x.type);
    }
    else {
      errorSemantics("Type mismatch");
    }
    return x;
  }

  private static Item Expr() {
    Item x, y;
    int op;
    boolean negate;
    if (negate = (sy == MINUS)) {
      scan();
    }
    x = Term();
    if (negate) {
      if (x.type != Tab.intType) {
        errorSemantics("Integer operand required");
      }
      if (x.kind == Item.CON) {
        x.val = -x.val;
      }
      else {
        x = Code.load(x);
        Code.put(Code.ineg);
      }
    }
    while (sy == PLUS || sy == MINUS) {
      op = sy == PLUS ? Code.iadd : Code.isub;
      scan();
      x = Code.load(x);
      y = Term();
      if (x.type == Tab.intType && y.type == Tab.intType) {
        y = Code.load(y);
        Code.put(op);
      }
      else {
        errorSemantics("Integer operands required");
      }
    }
    return x;
  }

  private static Item Term() {
    Item x, y;
    int op;
    x = Factor();
    while (firstOfMulop.get(sy)) {
      switch(sy) {
        case TIMES:
          op = Code.imul;
          break;
        case SLASH:
          op = Code.idiv;
          break;
        case MODULO:
          op = Code.irem;
          break;
        default:
          errorSemantics("Invalid operator");
          op = -1;
      }
      scan();
      x = Code.load(x);
      y = Factor();
      if (x.type == Tab.intType && y.type == Tab.intType) {
        y = Code.load(y);
        Code.put(op);
      }
      else {
        errorSemantics("Integer operands required");
      }
    }
    return x;
  }

  private static Item Factor() {
    Item x, y;
    Struct struct;
    int op;
    
    op = -1;
    x = new Item();
      
    switch(sy) {
      case IDENT:
        x = Designator();
        if (sy == LEFT_PARENTHESIS) {
          handleMethodCall(x);
          check(RIGHT_PARENTHESIS);
        }
        break;
      case NUMBER:
        scan();
        x = new Item(t.val);
        x.type = Tab.intType;
        break;
      case CHARCONST:
        scan();
        x = new Item(t.val);
        x.type = Tab.charType;
        break;
      case NEW:
        scan();
        x = new Item();
        struct = Type();
        if (struct.kind != Struct.INT && struct.kind != Struct.CHAR && struct.kind != Struct.CLASS) {
            errorSemantics("Integer, char or class type expected");
        }
        else if (sy == LEFT_BRACKET) {
          x.type = new Struct(Struct.ARR);
          x.type.elemType = struct;
          scan();
          y = Expr();
          if (y.type == Tab.intType) {
            y = Code.load(y);
            if (struct.kind == Struct.CLASS) {
              Code.put(Code.anewarray);
            }
            else {
              Code.put(Code.newarray);
              Code.put(struct.kind == Struct.INT ? 10 : 5);
            }
            // Adresse des neu erzeugten Arrays / Objekts am Stack
            x.kind = Item.STACK;
          }
          else {
            errorSemantics("Integer expected");
          }
          check(RIGHT_BRACKET);
        }
        else {
          if (struct.kind == Struct.CLASS) {
            x.type = struct;
            Code.put(Code.new_);
            Code.put(struct.getNrOfFields());
          }
          else {
            errorSemantics("Class type expected");
          }
        }
        break;
      case LEFT_PARENTHESIS:
        scan();
        x = Expr();
        check(RIGHT_PARENTHESIS);
        break;
      default:
        errorExpecting("factor");
    }
    return x;
  }

  private static Item Designator() {
    Item item;
    check(IDENT);
    item = new Item(Tab.find(t.string));
    while (true) {
      if (sy == PERIOD) {
        Obj obj;
        item = Code.load(item);
        scan();
        check(IDENT);
        if (item.type.kind != Struct.CLASS) {
          errorSemantics("Class type expected");
        }
        else {
          obj = Tab.findField(t.string, item.type);
          item.val = obj.val;
          item.type = obj.type;
        }
        item.kind = Item.FLD;
      }
      else if (sy == LEFT_BRACKET) {
        Item eItem;
        item = Code.load(item);
        scan();
        eItem = Expr();
        check(RIGHT_BRACKET);
        eItem = Code.load(eItem);
        item.kind = Item.ELEM;
        item.type = item.type.elemType;
      }
      else {
        break;
      }
    }
    return item;
  }

  private static int Relop() {
    if (firstOfRelop.get(sy)) {
      scan();
      switch(t.kind) {
        case EQUAL:
          return Code.eq;
        case NOT_EQUAL:
          return Code.ne;
        case GREATER:
          return Code.gt;
        case GREATER_EQUAL:
          return Code.ge;
        case LESS:
          return Code.lt;
        case LESS_EQUAL:
          return Code.le;
      }
    }
    else {
      errorExpecting("relop");
    }
    return -1;
  }

  private static void Mulop() {
    if (firstOfMulop.get(sy)) {
     scan();
    }
    else {
      errorExpecting("mulop");
    }
  }
  
  private static void handleMethodCall(Item methodItem) {
    Vector pars = new Vector();
    if (methodItem.kind != Item.METH) {
      errorSemantics("Method expected");
    }
    else {
      scan();
      if (firstOfActPars.get(sy)) {
        pars = ActPars();
      }
      if (methodItem.obj.level != pars.size()) {
        errorSemantics("Wrong number of parameters");
      }
      else {
        Obj obj = methodItem.obj.locals;
        for (int i = 0; i < pars.size(); i++) {
          if (((Item)pars.elementAt(i)).type.kind != obj.type.kind) {
            errorSemantics("Parameter type mismatch");
          }
          obj = obj.next;
        }
        if (methodItem.obj == Tab.ordObj || methodItem.obj == Tab.chrObj) {
          Code.load((Item)pars.elementAt(0));
        }
        else if (methodItem.obj == Tab.lenObj) {
          Code.load((Item)pars.elementAt(0));
          Code.put(Code.arraylength);
        }
        else {
          Code.put(Code.call);
          Code.put2(methodItem.val - Code.pc + 1);
        }
      }
    }
  }
  
}

                            