MODULE AdjMatrix;

CONST N*=31;
      empty=MIN(LONGINT);

TYPE AdjMat*=RECORD 
       key: ARRAY N OF LONGINT;
     END;
     

PROCEDURE Key(i,j: LONGINT): LONGINT;
BEGIN
  IF i>=j THEN
    RETURN(i * (i + 1) DIV 2 + j);
  ELSE
    RETURN(j * (j + 1) DIV 2 + i);
  END;
END Key;    


PROCEDURE (VAR m: AdjMat) Init* (VAR err: BOOLEAN);
  VAR i: LONGINT;
BEGIN
  FOR i:=0 TO N-1 DO
    m.key[i]:=empty;
  END;
  err:=FALSE;
END Init;


PROCEDURE (VAR m: AdjMat) CleanUp* ();
BEGIN
(* CleanUp ist unnötig, da <AdjMat> nicht dynamisch erzeugt wird *)
END CleanUp;


PROCEDURE (VAR m: AdjMat) IsSet* (i,j: LONGINT): BOOLEAN;
  VAR key, address, counter: LONGINT;
BEGIN
  key:=Key(i,j);
  address:=key MOD N;
  counter:=1;
  WHILE (m.key[address]#key) & (counter<=N) DO
    address:=(address + counter) MOD N;
    INC(counter);
  END;
  RETURN(m.key[address]=key);
END IsSet;


PROCEDURE (VAR m: AdjMat) Set* (i,j: LONGINT);
  VAR key, address, counter: LONGINT;
BEGIN
  key:=Key(i,j);
  address:=key MOD N;
  counter:=1;
  WHILE (m.key[address]#key) & (counter<=N) DO
    IF m.key[address]=empty THEN
      m.key[address]:=key;
    ELSE
      address:=(address + counter) MOD N;
      INC(counter);
    END;
  END;
END Set;  


END AdjMatrix.