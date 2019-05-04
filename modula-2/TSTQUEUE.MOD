MODULE TestQueue;

IMPORT I:=In, O:=Out, Q:=Queue;

PROCEDURE [WINDOWS] ProgMain*;
      
VAR queue: Q.Queue;
    element: Q.ElemT;
    command: CHAR;
    
BEGIN
  REPEAT
    O.String("1 _____ Queue initialisieren");
    O.Ln;
    O.String("2 _____ Queue entleeren");
    O.Ln;
    O.String("3 _____ Queue leer/voll?");
    O.Ln;
    O.String("4 _____ Elemente zählen");
    O.Ln;
    O.String("5 _____ Element einfügen");
    O.Ln;
    O.String("6 _____ Element auslesen");
    O.Ln;
    O.String("7 _____ Erstes Element auslesen");
    O.Ln;
    O.String("8 _____ Queue-Inhalt ausgeben");
    O.Ln;
    O.String("9 _____ Element suchen");
    O.Ln;
    O.String("E _____ Ende");
    O.Ln;
    O.Ln;
    O.String("Befehl: ");
    
    I.Char(command);
    O.Ln;
    
    CASE(command) OF
    
      "1": queue.Init();
           O.Ln;
           O.String("Queue wurde angelegt");
           
    | "2": queue.Zap();
           O.Ln;
           O.String("Queue wurde entleert");
           
    | "3": O.Ln;
           IF queue.Full() THEN
             O.String("Queue ist voll");
           ELSIF queue.Empty() THEN
             O.String("Queue ist leer");
           ELSE
             O.String("Queue ist weder voll noch leer");
           END;

    | "4": O.Ln;
           O.Int(queue.NrOfElems(),0);
           O.String(" Elemente in der Queue");
     
    | "5": O.Ln;
           O.String("Element eingeben: ");
           I.LongInt(element);
           queue.Put(element);
           O.Ln;
           O.Ln;
           O.String("Das Element wurde eingefügt");
           
    | "6": queue.Get(element);
           O.Ln;
           O.String("Ausgelesenes Element: ");
           O.Int(element,0);
           
    | "7": queue.Front(element);
           O.Ln;
           O.String("Erstes Element: ");
           O.Int(element,0);
           
    | "8": queue.Dump();
    
    | "9": O.Ln;
           O.String("Zu suchendes Element eingeben: ");
           I.LongInt(element);
           O.Ln;
           O.Ln;
           IF queue.Find(element) THEN
             O.String("Das Element wurde gefunden");
           ELSE
             O.String("Das Element wurde nicht gefunden");
           END;
           
     ELSE
    
    END;
    
    O.Ln;
    O.Ln;
    
  UNTIL CAP(command)="E";                  

END ProgMain;

END TestQueue.