MODULE TestBuySellDate;

IMPORT I:=In, O:=Out, B:=BuySell;

PROCEDURE [WINDOWS] ProgMain*;
 
  VAR list: B.ListT;
      buyDate, sellDate, date: B.DateT;
      profit: LONGINT;
      command: CHAR;
    
BEGIN

  REPEAT
    O.String("1 _____ Kursentwicklung eingeben");
    O.Ln;
    O.String("2 _____ Kursentwicklung ausgeben");
    O.Ln;
    O.String("3 _____ Optimale Kauf/Verkauf-Strategie ermitteln");
    O.Ln;
    O.String("E _____ Ende");
    O.Ln;
    O.Ln;
    O.String("Befehl: ");
    
    I.Char(command);
    O.Ln;
    O.Ln;
    
    CASE(command) OF
    
      "1": FOR date:=0 TO B.N-1 DO
             O.String("Tag ");
             O.Int(date+1,0);
             O.String(": ");
             I.LongInt(list[date]);
             O.Ln;
           END;
           
    | "2": O.String("Tag:  ");
           FOR date:=0 TO B.N-1 DO
             O.Int(date+1,5);
           END;
           O.Ln;
           O.String("Kurs: ");
           FOR date:=0 TO B.N-1 DO
             O.Int(list[date],5);
           END;
           O.Ln;
           
    | "3": B.BuySellDate(list,buyDate,sellDate,profit);
           IF profit#0 THEN
             O.String("Optimale Strategie: ");
             O.Ln;
             O.String("Aktienkauf am ");
             O.Int(buyDate+1,0);
             O.String(".Tag");
             O.Ln;
             O.String("Aktienverkauf am ");
             O.Int(sellDate+1,0);
             O.String(".Tag");
             O.Ln;
             O.String("Profit: ");
             O.Int(profit,0);
             O.String(",- / Aktie");
           ELSE
             O.String("Es gibt keine profitable Strategie");
           END;
           O.Ln;
           
     ELSE
    
    END;
    
    O.Ln;
    O.Ln;
    
  UNTIL CAP(command)="E";                  

END ProgMain;

END TestBuySellDate.