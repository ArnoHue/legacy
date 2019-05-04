MODULE Hash;

IMPORT O:=Out;

CONST N*=31;
      empty=0;
      full=1;
      deleted=2;

TYPE KeyT*=LONGINT;
     DataT*=LONGINT;

     EntryT=RECORD
       key: KeyT;
       data: DataT;
       status: INTEGER;
     END;

     TableT*=POINTER TO ARRAY N OF EntryT;


PROCEDURE HFind(table: TableT; key: KeyT; VAR addr, nrColl: LONGINT; VAR found: BOOLEAN);
  VAR address, counter: LONGINT;
BEGIN
  address:=key MOD N;
  counter:=1;
  WHILE (table^[address].key#key) & (table^[address].status=full) & (counter<=N) DO
     address:=(address+counter) MOD N;
    INC(counter);
   END;
  addr:=address;
  WHILE (table^[address].key#key) & (table^[address].status=deleted) & (counter<=N) DO
    address:=(address+counter) MOD N;
    INC(counter);
  END;
  (* O.Ln;
  O.String("*****"); *)
  found:=(table^[address].key=key) & (table^[address].status=full);
  IF table^[address].key=key THEN
    addr:=address;
    IF found THEN
      nrColl:=counter-1;
    END;
  END;
END HFind;


PROCEDURE Init*(VAR t:TableT; VAR err:BOOLEAN);
  VAR i: LONGINT;
BEGIN
  NEW(t);
  err:=t=NIL;
  IF ~err THEN   
    FOR i:=0 TO N-1 DO
      t^[i].status:=empty;
    END;
  END;
END Init;


PROCEDURE CleanUp*(VAR t:TableT);
BEGIN
  DISPOSE(t);
END CleanUp;


PROCEDURE Dump*(t:TableT);
  VAR i: LONGINT;
BEGIN;
  ASSERT(t#NIL);
  O.Ln;
  FOR i:=0 TO N-1 DO
    IF (t^[i].status=full) THEN
      O.String("Element[");
      O.Int(t^[i].key,0);
      O.String("]: ");
      O.Int(t^[i].data,0);
      O.Ln;
    END;
  END;
END Dump;


PROCEDURE Get*(t:TableT; k:KeyT; VAR f:BOOLEAN; VAR d:DataT);
  VAR address, nrColl: LONGINT;
BEGIN;
  ASSERT(t#NIL);
  HFind(t, k, address, nrColl, f);
  IF f THEN
    d:=t^[address].data;
  END;
END Get;


PROCEDURE Put*(VAR t:TableT; k:KeyT; d:DataT);
  VAR address, nrColl: LONGINT;
      found: BOOLEAN;
BEGIN;
  ASSERT(t#NIL);
  HFind(t, k, address, nrColl, found);
  IF (found) OR (t^[address].status#full) THEN
     t^[address].key:=k;
    t^[address].data:=d;
    t^[address].status:=full;
  END;
END Put;


PROCEDURE Del*(VAR t:TableT; k:KeyT);
  VAR address, nrColl: LONGINT;
      found: BOOLEAN;
BEGIN;
  ASSERT(t#NIL);
  HFind(t, k, address, nrColl, found);
  IF found THEN
    t^[address].status:=deleted;
  END;
END Del;


PROCEDURE Analyse*(t:TableT; VAR act, del, sl:LONGINT);
  VAR address, nrColl, i: LONGINT;
      found: BOOLEAN;
BEGIN;
  ASSERT(t#NIL);
  act:=0;
  del:=0;
  sl:=0;
  FOR i:=0 TO N-1 DO
    IF t^[i].status=deleted THEN
      INC(del);
    ELSIF t^[i].status=full THEN
      INC(act);
      HFind(t, t^[i].key, address, nrColl, found);
      sl:=sl+nrColl;
    END;
  END;
END Analyse;


END Hash.