MODULE BinTree;

IMPORT I:=In, O:=Out;

TYPE ElemT*=LONGINT;

     BinTreeT*=POINTER TO TreeNodeT;
     
     TreeNodeT=RECORD;
       key: ElemT;
       left, right: BinTreeT;
     END;

VAR dummyPtr: BinTreeT;


PROCEDURE Init* (VAR t: BinTreeT);
BEGIN
  t:=dummyPtr;
END Init;


PROCEDURE CleanUp* (VAR t: BinTreeT);
BEGIN
  ASSERT(t#NIL);
  IF t#dummyPtr THEN
    CleanUp(t^.left);
    CleanUp(t^.right);
    DISPOSE(t);
    t:=dummyPtr;
  END;
END CleanUp;


PROCEDURE GenFib* (VAR t: BinTreeT; h: LONGINT);
  VAR ptr: BinTreeT;
BEGIN
  IF h>0 THEN
    NEW(t);
    GenFib(t^.left,h-1);
    GenFib(t^.right,h-2);
    O.Ln;
    O.String("Wert des Knotens: ");
    I.LongInt(t^.key);
  ELSE
    t:=dummyPtr;
  END;
END GenFib;


PROCEDURE InOrder*(t: BinTreeT);

  PROCEDURE InOrderInternal(t: BinTreeT; height: LONGINT);
  BEGIN
    IF t#dummyPtr THEN
      InOrderInternal(t^.left,height+1);
      O.String("Wert des Knotens: ");
      O.Int(t^.key,2);
      O.String("  (Höhe: ");
      O.Int(height,0);
      O.String(")");
      O.Ln;
      InOrderInternal(t^.right,height+1);
    END;
  END InOrderInternal;  

BEGIN
  ASSERT(t#NIL);
  InOrderInternal(t,1);
END InOrder;


PROCEDURE NoOfNodes* (t: BinTreeT): LONGINT;
BEGIN
  ASSERT(t#NIL);
  IF t=dummyPtr THEN
    RETURN 0;
  ELSE
    RETURN 1 + NoOfNodes(t^.left) + NoOfNodes(t^.right);
  END;
END NoOfNodes;


PROCEDURE Height* (t: BinTreeT): LONGINT;

  PROCEDURE Max (a, b: LONGINT): LONGINT;
  BEGIN
    IF a>b THEN
      RETURN a;
    ELSE
      RETURN b;
    END;
  END Max;

BEGIN
  ASSERT(t#NIL);
  IF t=dummyPtr THEN
    RETURN 0;
  ELSE
    RETURN 1 + Max(Height(t^.left),Height(t^.right));
  END;
END Height;


PROCEDURE Insert* (VAR t: BinTreeT; k: ElemT; VAR ok: BOOLEAN);
BEGIN
  ASSERT(t#NIL);
  IF t=dummyPtr THEN
    NEW(t);
    ok:=t#NIL;
    IF ok THEN
      t^.key:=k;
      t^.left:=dummyPtr;
      t^.right:=dummyPtr;
    END;
  ELSIF k<t^.key THEN
    Insert(t^.left,k,ok);
  ELSE
    Insert(t^.right,k,ok);
  END;
END Insert;


PROCEDURE Search* (t: BinTreeT; k: ElemT): BOOLEAN;
BEGIN
  ASSERT(t#NIL);
  dummyPtr^.key:=k;
  WHILE k#t^.key DO
    IF k<t^.key THEN
      t:=t^.left;
    ELSE
      t:=t^.right;
    END;
  END;
  RETURN t#dummyPtr;
END Search;


PROCEDURE IsAVL* (t: BinTreeT): BOOLEAN;
BEGIN
  ASSERT(t#NIL);
  IF t#dummyPtr THEN
    RETURN IsAVL(t^.left) & IsAVL(t^.right) & (ABS(Height(t^.left) - Height(t^.right))<=1);
  ELSE
    RETURN TRUE;
  END;
END IsAVL;


PROCEDURE IsSearchTree* (t: BinTreeT): BOOLEAN;
BEGIN
  ASSERT((t#NIL) & (t^.left#NIL) & (t^.right#NIL));
  IF t#dummyPtr THEN
    RETURN ((t^.key>t^.left^.key) OR (t^.left=dummyPtr)) & ((t^.key<=t^.right^.key) OR (t^.right=dummyPtr)) & IsSearchTree(t^.left) & IsSearchTree(t^.right);
  ELSE
    RETURN TRUE;
  END;
END IsSearchTree;

BEGIN

  NEW(dummyPtr);
  ASSERT(dummyPtr#NIL);
  dummyPtr^.left:=dummyPtr;
  dummyPtr^.right:=dummyPtr; 

END BinTree.