MODULE Queue;

IMPORT O:=Out;

CONST queueLength=10;

TYPE ElemT*=LONGINT;
     Queue*=RECORD
       element: ARRAY queueLength OF ElemT;
       head, tail: LONGINT;
     END;


PROCEDURE (VAR q: Queue) Init* ();
BEGIN
  q.head:=0;
  q.tail:=0;
END Init;


PROCEDURE (VAR q: Queue) CleanUp* ();
BEGIN
(* CleanUp ist unnötig, da <Queue> nicht dynamisch erzeugt wird *)
END CleanUp;


PROCEDURE (VAR q: Queue) Zap*();
BEGIN
  q.head:=0;
  q.tail:=0;
END Zap;


PROCEDURE (VAR q: Queue) Full* (): BOOLEAN;
BEGIN
  RETURN (q.head + 1) MOD queueLength = q.tail;
END Full;


PROCEDURE (VAR q: Queue) Empty* (): BOOLEAN;
BEGIN
  RETURN q.head=q.tail;
END Empty;


PROCEDURE (VAR q: Queue) NrOfElems* (): LONGINT;
BEGIN
  IF q.head>=q.tail THEN
    RETURN q.head - q.tail;
  ELSE
    RETURN queueLength - (q.head - q.tail);
  END;
END NrOfElems;


PROCEDURE (VAR q: Queue) Put* (e: ElemT);
BEGIN
  IF ~q.Full() THEN
    q.head:=(q.head + 1) MOD queueLength;
    q.element[q.head]:=e;
  END;
END Put;


PROCEDURE (VAR q: Queue) Get* (VAR e: ElemT);
BEGIN
  IF ~q.Empty() THEN
    q.tail:=(q.tail + 1) MOD queueLength;
    e:=q.element[q.tail];
  END;
END Get;


PROCEDURE (VAR q: Queue) Front* (VAR e: ElemT);
BEGIN
  IF ~q.Empty() THEN
    e:=q.element[(q.tail + 1) MOD queueLength];
  END;
END Front;


PROCEDURE (VAR q: Queue) Dump* ();
  VAR i: LONGINT;
BEGIN
  FOR i:=1 TO q.NrOfElems() DO
    O.Ln;
    O.String("Element[");
    O.Int(i,0);
    O.String("]: ");
    O.Int(q.element[(q.tail + i) MOD queueLength],0);
  END;
END Dump;


PROCEDURE (VAR q: Queue) Find* (e: ElemT): BOOLEAN;
  VAR i: LONGINT;
BEGIN
  IF ~q.Empty() THEN
    i:=1;
    WHILE (i <= q.NrOfElems()) & (q.element[(q.tail + i) MOD queueLength] # e) DO
      INC(i);
    END;
    RETURN q.element[(q.tail + i) MOD queueLength] = e;
  ELSE
    RETURN FALSE;
  END;
END Find;

 
END Queue.