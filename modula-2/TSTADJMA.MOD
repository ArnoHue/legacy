MODULE TestAdjMat;

IMPORT D:=Display, A:=AdjMatrix;

PROCEDURE [WINDOWS] ProgMain*;

VAR matrix: A.AdjMat;
    i: LONGINT;
    err: BOOLEAN;
    
BEGIN
  matrix.Init(err);
  IF err THEN
    D.WriteStr("Fehler bei der Initialisierung");
  ELSE
    FOR i:=1 TO 10 DO
      matrix.Set(i+i,i*i);
    END;
    err:=FALSE;
    i:=1;
    WHILE (i<=10) & (~err) DO
      err:=~matrix.IsSet(i+i,i*i) OR ~matrix.IsSet(i*i,i+i) OR matrix.IsSet(i*i*i,i+i+i);
      INC(i);
    END;
    IF err THEN
      D.WriteStr("Fehler beim Ein/Auslesen");
    ELSE
      D.WriteStr("Der Test war erfolgreich");
    END;
    matrix.CleanUp();
  END;
    
  REPEAT    
  UNTIL D.KeyPressed();                  

END ProgMain;

END TestAdjMat.