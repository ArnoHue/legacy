MODULE TestMatrix;

IMPORT In, Out, M:=Matrix;

PROCEDURE [WINDOWS] ProgMain*;

CONST tstMatrixLoBnd = 1;
      tstMatrixUpBnd = 10;
      
VAR upBnd, loBnd, index: M.Index;
    err, found: BOOLEAN;
    key: CHAR;
    element: M.Element;
    i, j: LONGINT;
    
BEGIN
  REPEAT
    Out.String("1 _____ Matrix anlegen");
    Out.Ln;
    Out.String("2 _____ Matrix eingeben");
    Out.Ln;
    Out.String("3 _____ Matrix ausgeben");
    Out.Ln;
    Out.String("4 _____ Element suchen");
    Out.Ln;
    Out.String("5 _____ Matrix löschen");
    Out.Ln;
    Out.String("6 _____ Gesamt-Test");
    Out.Ln;
    Out.String("E _____ Ende");
    Out.Ln;
    Out.Ln;
    Out.String("Befehl: ");
    
    In.Char(key);
    Out.Ln;
    
    CASE(key) OF
    
      "1": Out.Ln;
           FOR i:=0 TO M.DIM-1 DO
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
           M.Init(upBnd,loBnd,err);
           Out.Ln;
           IF err THEN
             Out.String("Fehler bei der Initialisierung!");
           ELSE
             Out.String("Matrix erfolgreich angelegt");
           END;
           
    | "2": FOR i:=0 TO M.MatrixSize()-1 DO
             Out.Ln;
             Out.String("matrix [");
             M.GetIndex(i,index);
             FOR j:=0 TO M.DIM-1 DO
               Out.Int(index[j],0);
               IF j<M.DIM-1 THEN
                 Out.String(",");
               END;
             END;
             Out.String("] : ");
             In.Int(element);
             M.Put(index,element);
           END;
           
    | "3": FOR i:=0 TO M.MatrixSize()-1 DO
             Out.Ln;
             Out.String("matrix [");
             M.GetIndex(i,index);
             FOR j:=0 TO M.DIM-1 DO
               Out.Int(index[j],0);
               IF j<M.DIM-1 THEN
                 Out.String(",");
               END;
             END;
             Out.String("] : ");
             M.Get(index,element);
             Out.Int(element,0);
           END;             
           
    | "4": Out.Ln;
           Out.String("Suchwert: ");
           In.Int(element);
           Out.Ln;
           M.Find(index,found,element);
           IF found THEN
             Out.Ln;
             Out.String("Suche erfolgreich: Matrix [");
             FOR i:=0 TO M.DIM-1 DO
               Out.Int(index[i],0);
               IF i<M.DIM-1 THEN
                 Out.String(",");
               END;
             END;
             Out.String("]");
           ELSE
             Out.String("Suche erfolglos");
           END;
           
    | "5": M.CleanUp();
           Out.Ln;
           Out.String("Die Matrix wurde gelöscht");
           
    | "6": Out.Ln;
           FOR i:=0 TO M.DIM-1 DO
             loBnd[i]:=tstMatrixLoBnd;
             upBnd[i]:=tstMatrixUpBnd;
           END;
           M.Init(upBnd,loBnd,err);
           IF err THEN
             Out.String("Fehler bei der Initialisierung");
           ELSE
             FOR i:=0 TO M.MatrixSize()-1 DO
               M.GetIndex(i,index);
               M.Put(index,SHORT(i));
             END;
             i:=M.MatrixSize()-1;
             WHILE(i>=0) & (~err) DO
               M.GetIndex(i,index);
               M.Get(index,element);
               err:=(element#i);
               DEC(i);
             END;
             IF err THEN
               Out.String("Fehler beim Ein/Auslesen der Matrix");
             ELSE
               i:=0;
               REPEAT
                 M.Find(index,found,SHORT(i));
                 IF found THEN
                   found:=(M.GetAddress(index)=i);
                 END;
                 INC(i);
               UNTIL(i=M.DIM) OR (~found);
               IF ~found THEN
                 Out.String("Fehler beim Suchen in der Matrix");
               ELSE
                 Out.String("Der Gesamt-Test war erfolgreich");
               END;
             END;
             M.CleanUp;
           END;

    ELSE
    
    END;
    
    Out.Ln;
    Out.Ln;
    
  UNTIL CAP(key)="E";                  

END ProgMain;

END TestMatrix.