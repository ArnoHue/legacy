MODULE TestBinMat;

IMPORT In, Out, B:=BinMat;


PROCEDURE WriteBool(bool: BOOLEAN);
BEGIN
  IF bool THEN
    Out.String("1");
  ELSE
    Out.String("0");
  END;
END WriteBool;
    

PROCEDURE ReadBool(): BOOLEAN;

VAR key: CHAR;

BEGIN
  In.Char(key);
  RETURN (key="1");
END ReadBool;
    

PROCEDURE [WINDOWS] ProgMain*;

CONST tstMatrixLoBnd = 1;
      tstMatrixUpBnd = 8;
      
VAR upBnd, loBnd, index: B.Index;
    err: BOOLEAN;
    key: CHAR;
    i, j: LONGINT;
    
BEGIN
  REPEAT
    Out.String("1 _____ Matrix anlegen");
    Out.Ln;
    Out.String("2 _____ Matrix eingeben");
    Out.Ln;
    Out.String("3 _____ Matrix ausgeben");
    Out.Ln;
    Out.String("4 _____ Matrix löschen");
    Out.Ln;
    Out.String("5 _____ Gesamt-Test");
    Out.Ln;
    Out.String("E _____ Ende");
    Out.Ln;
    Out.Ln;
    Out.String("Befehl: ");
    
    In.Char(key);
    Out.Ln;
    
    CASE(key) OF
    
      "1": Out.Ln;
           FOR i:=0 TO B.DIM-1 DO
             Out.String("lowerBoundary [");
             Out.Int(i+1,0);
             Out.String("] : ");
             In.LongInt(loBnd[i]);
             Out.Ln;
             Out.String("upperBoundary [");
             Out.Int(i+1,0);
             Out.String("] : ");
             In.LongInt(upBnd[i]);
             Out.Ln;
           END;
           B.Init(upBnd,loBnd,err);
           Out.Ln;
           IF err THEN
             Out.String("Fehler bei der Initialisierung");
           ELSE
             Out.String("Die Matrix wurde erfolgreich angelegt");
           END;
           
    | "2": FOR i:=0 TO B.MatrixSize()-1 DO
             Out.Ln;
             Out.String("matrix [");
             B.GetIndex(i,index);
             FOR j:=0 TO B.DIM-1 DO
               Out.Int(index[j],0);
               IF j<B.DIM-1 THEN
                 Out.String(",");
               END;
             END;
             Out.String("] : ");
             B.Put(index,ReadBool());
           END;
           
    | "3": FOR i:=0 TO B.MatrixSize()-1 DO
             Out.Ln;
             Out.String("matrix [");
             B.GetIndex(i,index);
             FOR j:=0 TO B.DIM-1 DO
               Out.Int(index[j],0);
               IF j<B.DIM-1 THEN
                 Out.String(",");
               END;
             END;
             Out.String("] = ");
             WriteBool(B.Get(index));
           END;             

    | "4": B.CleanUp();
           Out.Ln;
           Out.String("Die Matrix wurde gelöscht");
           
    | "5": Out.Ln;
           FOR i:=0 TO B.DIM-1 DO
             loBnd[i]:=tstMatrixLoBnd;
             upBnd[i]:=tstMatrixUpBnd;
           END;
           B.Init(upBnd,loBnd,err);
           IF err THEN
             Out.String("Fehler bei der Initialisierung");
           ELSE
             FOR i:=0 TO B.MatrixSize()-1 DO
               B.GetIndex(i,index);
               B.Put(index,(i MOD 2 = 0));
             END;
             i:=B.MatrixSize()-1;
             WHILE (i>=0) & (~err) DO
               B.GetIndex(i,index);
               err:=(B.Get(index)#(i MOD 2 = 0));
               DEC(i);
             END;
             IF err THEN
               Out.String("Fehler beim Ein/Auslesen der Matrix");
             ELSE
               Out.String("Der Gesamt-Test war erfolgreich");
             END;
             B.CleanUp;
           END;
             
      ELSE
    
    END;
    
    Out.Ln;
    Out.Ln;
    
  UNTIL CAP(key)="E";                  

END ProgMain;

END TestBinMat.