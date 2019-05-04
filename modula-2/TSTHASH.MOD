MODULE TestHash;

IMPORT I:=In, O:=Out, H:=Hash;

PROCEDURE [WINDOWS] ProgMain*;
      
VAR table: H.TableT;
    key: H.KeyT;
    data: H.DataT;
    act, del, sl: LONGINT;
    command: CHAR;
    found, error: BOOLEAN;
    
BEGIN
  REPEAT

    O.String("1 _____ Tabelle initialisieren");
    O.Ln;
    O.String("2 _____ Element eingeben");
    O.Ln;
    O.String("3 _____ Element ausgeben");
    O.Ln;
    O.String("4 _____ Element löschen");
    O.Ln;
    O.String("5 _____ Tabelle ausgeben");
    O.Ln;
    O.String("6 _____ Analyse");
    O.Ln;
    O.String("7 _____ Tabelle löschen");
    O.Ln;
    O.String("E _____ Ende");
    O.Ln;
    O.Ln;
    O.String("Befehl: ");
    
    I.Char(command);
    O.Ln;
    
    CASE(command) OF
    
      "1": H.Init(table,error);
           O.Ln;
           IF ~error THEN
             O.String("Die Tabelle wurde erfolgreich angelegt");
           ELSE
             O.String("Die Tabelle konnte nicht angelegt werden");
           END;
           
    | "2": O.Ln;
           O.String("Schlüssel: ");
           I.LongInt(key);
           O.Ln;
           O.String("Wert: ");
           I.LongInt(data);
           H.Put(table,key,data);

           
    | "3": O.Ln;
           O.String("Schlüssel: ");
           I.LongInt(key);
           H.Get(table,key,found,data);
           IF found THEN
             O.Ln;
             O.String("Wert: ");
             O.Int(data,0);
           END;
     
    | "4": O.Ln;
           O.String("Schlüssel: ");
           I.LongInt(key);
           H.Del(table,key);
           O.Ln;
           O.Ln;
           O.String("Das Element wurde gelöscht");
           
    | "5": H.Dump(table);
           
    | "6": H.Analyse(table,act,del,sl);
           O.Ln;
           O.String("Belegte Elemente: ");
           O.Int(act,0);
           O.Ln;
           O.String("Gelöschte Elemente: ");
           O.Int(del,0);
           O.Ln;
           O.String("Länge der Kollisionsketten: ");
           O.Int(sl,0);

    | "7": H.CleanUp(table);
           O.Ln;
           O.String("Die wurde Tabelle gelöscht");


    ELSE
    
    END;
    
   O.Ln;
   O.Ln;
 
  UNTIL CAP(command)="E";                  

END ProgMain;

END TestHash.