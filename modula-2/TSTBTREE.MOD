MODULE TestBinTree;

IMPORT I:=In, O:=Out, B:=BinTree;

PROCEDURE [WINDOWS] ProgMain*;
      
VAR binTree: B.BinTreeT;
    element: B.ElemT;
    height: LONGINT;
    ok: BOOLEAN;
    command: CHAR;
    
BEGIN
  REPEAT
    O.String("1 _____ Baum initialisieren");
    O.Ln;
    O.String("2 _____ Fibonacci-Baum generieren");
    O.Ln;
    O.String("3 _____ Baum ausgeben");
    O.Ln;
    O.String("4 _____ Anzahl der Knoten ausgeben");
    O.Ln;
    O.String("5 _____ Höhe des Baumes ausgeben");
    O.Ln;
    O.String("6 _____ Knoten in Suchbaum einfügen");
    O.Ln;
    O.String("7 _____ Knoten in Suchbaum suchen");
    O.Ln;
    O.String("8 _____ Ist Baum ein AVL-Baum?");
    O.Ln;
    O.String("9 _____ Ist Baum ein Suchbaum?");
    O.Ln;
    O.String("0 _____ Baum löschen");
    O.Ln;
    O.String("E _____ Ende");
    O.Ln;
    O.Ln;
    O.String("Befehl: ");
    
    I.Char(command);
    O.Ln;
    O.Ln;
    
    CASE(command) OF
    
      "1": B.Init(binTree);
           O.String("Der Baum wurde angelegt");
           
    | "2": O.String("Höhe des Fibonacci-Baums: ");
           I.LongInt(height);
           O.Ln;
           B.GenFib(binTree,height);
            
    | "3": B.InOrder(binTree);
           
    | "4": O.String("Es sind ");
           O.Int(B.NoOfNodes(binTree),0);
           O.String(" Knoten im Baum");
     
    | "5": O.String("Die Höhe des Baums ist ");
           O.Int(B.Height(binTree),0);
           
    | "6": O.String("Wert des Knotens: ");
           I.LongInt(element);
           B.Insert(binTree,element,ok);
           O.Ln;
           O.Ln;
           IF ok THEN
             O.String("Der Knoten wurde eingefügt");
           ELSE
             O.String("Der Knoten konnte nicht eingefügt werden");
           END;
           
    | "7": O.String("Zu suchender Knoten: ");
           I.LongInt(element);
           O.Ln;
           O.Ln;
           IF B.Search(binTree,element) THEN
             O.String("Der Knoten wurde gefunden");
           ELSE
             O.String("Der Knoten wurde nicht gefunden");
           END;
           
    | "8": IF B.IsAVL(binTree) THEN
             O.String("Der Baum ist ein AVL-Baum");
           ELSE
             O.String("Der Baum ist kein AVL-Baum");
           END;
           
    | "9": IF B.IsSearchTree(binTree) THEN
             O.String("Der Baum ist ein Suchbaum");
           ELSE
             O.String("Der Baum ist kein Suchbaum");
           END;
           
    | "0": B.CleanUp(binTree);
           O.String("Der Baum wurde gelöscht");
           
     ELSE
    
    END;
    
    O.Ln;
    O.Ln;
    
  UNTIL CAP(command)="E";                  

END ProgMain;

END TestBinTree.