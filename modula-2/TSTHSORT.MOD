MODULE TestHeapSort;

IMPORT I:=In, O:=Out, H:=HeapSort;

PROCEDURE [WINDOWS] ProgMain*;
 
CONST maxLen=20;
 
VAR list: ARRAY maxLen OF LONGINT;
    len, index: LONGINT;
    command: CHAR;
    
BEGIN

  len:=0;

  REPEAT
    O.String("1 _____ Liste eingeben");
    O.Ln;
    O.String("2 _____ Liste sortieren");
    O.Ln;
    O.String("3 _____ Liste ausgeben");
    O.Ln;
    O.String("E _____ Ende");
    O.Ln;
    O.Ln;
    O.String("Befehl: ");
    
    I.Char(command);
    O.Ln;
    O.Ln;
    
    CASE(command) OF
    
      "1": O.String("Anzahl der Listenelemente (max. ");
           O.Int(maxLen,0);
           O.String("): ");
           I.LongInt(len);
           IF (len>maxLen) THEN
             len:=maxLen;
           END;
           O.Ln;
           O.Ln;
           FOR index:=0 TO len-1 DO
             O.String("Liste[");
             O.Int(index+1,0);
             O.String("] : ");
             I.LongInt(list[index]);
             O.Ln;
           END;
           
    | "2": H.Sort(list,len);
           O.String("Die Liste wurde sortiert");
           O.Ln;
           
    | "3": FOR index:=0 TO len-1 DO
             O.String("Liste[");
             O.Int(index+1,0);
             O.String("] : ");
             O.Int(list[index],0);
             O.Ln;
           END;
           
     ELSE
    
    END;
    
    O.Ln;
    O.Ln;
    
  UNTIL CAP(command)="E";                  

END ProgMain;

END TestHeapSort.