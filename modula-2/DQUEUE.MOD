MODULE DQueue;

IMPORT O:=Out;

TYPE ElemT*=LONGINT;

     NodeT=RECORD;
       element: ElemT;
       next: POINTER TO NodeT;
     END;

     Queue*=RECORD
       head, tail: POINTER TO NodeT;
       nrOfElems: LONGINT;
     END;


PROCEDURE (VAR q: Queue) Init* ();
BEGIN
  q.head:=NIL;
  q.tail:=NIL;
  q.nrOfElems:=0;
END Init;


PROCEDURE (VAR q: Queue) Zap*();
  VAR ptr: POINTER TO NodeT;
BEGIN
  WHILE (q.tail#NIL) DO
    ptr:=q.tail;
    q.tail:=q.tail^.next;
    DISPOSE(ptr);  
  END;
  q.head:=NIL;
  q.nrOfElems:=0;
END Zap;


PROCEDURE (VAR q: Queue) CleanUp* ();
  VAR ptr: POINTER TO NodeT;
BEGIN
  (* Ein Dispose der Queue selbst ist nicht nötig, da sie nicht dynamisch erzeugt wurde *)
  q.Zap();
END CleanUp;


PROCEDURE (VAR q: Queue) Full* (): BOOLEAN;
  VAR ptr: POINTER TO NodeT;
BEGIN
  NEW(ptr);
  IF (ptr#NIL) THEN
    DISPOSE(ptr);
    RETURN FALSE;
  ELSE
    RETURN TRUE;
  END;
END Full;


PROCEDURE (VAR q: Queue) Empty* (): BOOLEAN;
BEGIN
  RETURN q.nrOfElems=0;
END Empty;


PROCEDURE (VAR q: Queue) NrOfElems* (): LONGINT;
BEGIN
  RETURN q.nrOfElems;
END NrOfElems;


PROCEDURE (VAR q: Queue) Put* (e: ElemT);
BEGIN
  IF (~q.Full()) THEN
    IF (q.nrOfElems=0) THEN
      NEW(q.head);
      q.tail:=q.head;
    ELSE
      NEW(q.head^.next);
      q.head:=q.head^.next;
    END;
    q.head^.element:=e;
    q.head^.next:=NIL;
    INC(q.nrOfElems);
  END;
END Put;


PROCEDURE (VAR q: Queue) Get* (VAR e: ElemT);
  VAR ptr: POINTER TO NodeT;
BEGIN
  IF ~q.Empty() THEN
    e:=q.tail^.element;
    ptr:=q.tail;
    q.tail:=q.tail^.next;
    DISPOSE(ptr);
    DEC(q.nrOfElems)
  END;
END Get;


PROCEDURE (VAR q: Queue) Front* (VAR e: ElemT);
BEGIN
  IF ~q.Empty() THEN
    e:=q.tail^.element;
  END;
END Front;


PROCEDURE (VAR q: Queue) Dump* ();
  VAR ptr: POINTER TO NodeT;
      counter: LONGINT;
BEGIN
  ptr:=q.tail;
  FOR counter:=1 TO q.NrOfElems() DO
    O.Ln;
    O.String("Element[");
    O.Int(counter,0);
    O.String("]: ");
    O.Int(ptr^.element,0);
    ptr:=ptr^.next;
  END;
END Dump;


PROCEDURE (VAR q: Queue) Find* (e: ElemT): BOOLEAN;
  VAR ptr: POINTER TO NodeT;
      found: BOOLEAN;
BEGIN
  ptr:=q.tail;
  found:=FALSE;
  WHILE (ptr#NIL) & (~found) DO
    found:=ptr^.element=e;
    ptr:=ptr^.next;
  END;
  RETURN found;
END Find;

 
END DQueue.