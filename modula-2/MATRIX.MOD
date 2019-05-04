MODULE Matrix;

CONST DIM*=3;

TYPE Index*=ARRAY DIM OF LONGINT;
     Element*=INTEGER;
     
VAR matrix: POINTER TO ARRAY OF INTEGER;
    upperBoundary, lowerBoundary, dope: Index;


PROCEDURE GetAddress*(inx: Index): LONGINT;

(* GetAddress wird exportiert, um die Überprüfung der Suche nach Elementen
   in der Matrix im Testmodul zu ermöglichen - siehe dort *)
     
VAR address, i: LONGINT;
    err: BOOLEAN;
    
BEGIN
  address:=0;
  i:=0;
  err:=FALSE;
  
  WHILE (i<DIM) & (~err) DO
    err:=(inx[i]<lowerBoundary[i]) OR (inx[i]>upperBoundary[i]);
    IF ~err THEN
      address:=address+dope[i]*(inx[i]-lowerBoundary[i]);
    END;
    INC(i);
  END;
  
  IF err THEN
    RETURN 0;
  ELSE
    RETURN address;
  END; 
END GetAddress;  


PROCEDURE MatrixSize*(): LONGINT;

(* MatrixSize wird exportiert, um die Ausgabe der Matrix im Testmodul
   zu ermöglichen - siehe dort *)
   
BEGIN
  RETURN GetAddress(upperBoundary)+1;
END MatrixSize;


PROCEDURE GetIndex*(addr: LONGINT; VAR inx: Index);

(* GetIndex wird exportiert, um die Ausgabe der Matrix im Testmodul
   zu ermöglichen - siehe dort *)

VAR address, i: LONGINT;

BEGIN
  IF (matrix#NIL) & (addr<=MatrixSize())THEN
    address:=addr;
    FOR i:=0 TO DIM-1 DO
      inx[i]:=address DIV dope[i] + lowerBoundary[i];
      address:=address MOD dope[i];
    END;
  END;
END GetIndex;

  
PROCEDURE CleanUp*();

VAR i: LONGINT;

BEGIN
  DISPOSE(matrix);
  FOR i:=0 TO DIM-1 DO
    upperBoundary[i]:=0;
    lowerBoundary[i]:=0;
  END;
END CleanUp;


PROCEDURE Init*(upBnd,loBnd: Index; VAR err: BOOLEAN);

VAR i: LONGINT;

BEGIN
  IF matrix#NIL THEN
    CleanUp();
  END;
  
  err:=FALSE;
  i:=DIM-1;
  dope[DIM-1]:=1;

  REPEAT
    IF upBnd[i]>=loBnd[i] THEN
      upperBoundary[i]:=upBnd[i];
      lowerBoundary[i]:=loBnd[i];
      IF i>0 THEN
        dope[i-1]:=dope[i]*(upperBoundary[i]-lowerBoundary[i]+1);
      END;
    ELSE
      err:=TRUE
    END;
    DEC(i);
  UNTIL (i<0) OR (err);
  
  IF ~err THEN
    NEW(matrix,MatrixSize());
  END;
END Init;
  

PROCEDURE Get*(inx: Index; VAR ele: Element);

BEGIN
  IF matrix#NIL THEN
    ele:=matrix^[GetAddress(inx)];
  END
END Get;


PROCEDURE Put*(inx: Index; ele: Element);

BEGIN
  IF matrix#NIL THEN
    matrix^[GetAddress(inx)]:=ele;
  END
END Put;


PROCEDURE Find*(VAR outx: Index; VAR found: BOOLEAN; ele: Element);

VAR i: LONGINT;

BEGIN
  found:=FALSE;
  
  IF matrix#NIL THEN
    i:=0;

    REPEAT
      IF matrix^[i]=ele THEN
        GetIndex(i,outx);
        found:=TRUE;
      END;
      INC(i);
    UNTIL (i=MatrixSize()) OR (found);
  END;
END Find;


END Matrix.