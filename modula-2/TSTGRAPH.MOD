MODULE TestGraph;

IMPORT I:=In, O:=Out, G:=Graph;

PROCEDURE [WINDOWS] ProgMain*;
      
VAR graph: G.Graph;
    x, y: LONGINT;
    command: CHAR;
    
BEGIN
  REPEAT
    O.String("1 _____ Graph initialisieren");
    O.Ln;
    O.String("2 _____ Kante setzen");
    O.Ln;
    O.String("3 _____ Komponentenanzahl ausgeben");
    O.Ln;
    O.String("4 _____ Komponenten ausgeben");
    O.Ln;
    O.String("5 _____ Adjazenzmatrix ausgeben");
    O.Ln;
    O.String("6 _____ Ist der Graph zusammenhängend?");
    O.Ln;
    O.String("7 _____ Ist die Kante x-y eine Brücke?");
    O.Ln;
    O.String("8 _____ Ist der Graph ein Baum?");
    O.Ln;
    O.String("9 _____ Graph freigeben");
    O.Ln;
    O.String("E _____ Ende");
    O.Ln;
    O.Ln;
    O.String("Befehl: ");
    
    I.Char(command);
    O.Ln;
    
    CASE(command) OF
    
      "1": O.Ln;
           O.String("Anzahl der Knoten: ");
           I.LongInt(x);
           graph.Init(x);
           O.Ln;
           O.Ln;
           O.String("Der Graph wurde initialisiert");
           O.Ln;
           
    | "2": O.Ln;
           O.String("Von Knoten: ");
           I.LongInt(x);
           O.Ln;
           O.String("Zu Knoten: ");
           I.LongInt(y);
           graph.SetEdge(x,y);
           O.Ln;
           O.Ln;
           O.String("Die Kante wurde gesetzt");
           O.Ln;
           
    | "3": O.Ln;
           O.String("Es sind ");
           O.Int(graph.NrComps(),0);
           O.String(" Komponenten im Graph");
           O.Ln;
           
    | "4": O.Ln;
           graph.PrComps();
     
    | "5": O.Ln;
           graph.PrAdj();
           
    | "6": O.Ln;
           IF (graph.Connected()) THEN
             O.String("Der Graph ist zusammenhängend");
           ELSE
             O.String("Der Graph ist nicht zusammenhängend");
           END;
           O.Ln;
           
    | "7": O.Ln;
           O.String("Von Knoten: ");
           I.LongInt(x);
           O.Ln;
           O.String("Zu Knoten: ");
           I.LongInt(y);
           O.Ln;
           O.Ln;
           IF (graph.IsBridge(x,y)) THEN
             O.String("Die Kante ist eine Brücke");
           ELSE
             O.String("Die Kante ist keine Brücke");
           END;
           O.Ln;
           
    | "8": O.Ln;
           IF (graph.IsTree()) THEN
             O.String("Der Graph ist ein Baum");
           ELSE
             O.String("Der Graph ist kein Baum");
           END;
           O.Ln;
           
    | "9": graph.CleanUp();
           O.Ln;
           O.String("Der Graph wurde freigegeben");
           O.Ln;
           
     ELSE
    
    END;
    
    O.Ln;
    O.Ln;
    
  UNTIL CAP(command)="E";                  

END ProgMain;

END TestGraph.