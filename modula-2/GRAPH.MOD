MODULE Graph;

IMPORT A:=AdjMatrix, O:=Out;

TYPE Graph*=RECORD
       matrix: A.AdjMat;
       nrOfNodes: LONGINT;
     END;


PROCEDURE (VAR g: Graph) DFS (VAR markedNodes: SET; vNode: LONGINT);
  VAR node: LONGINT;
BEGIN
  markedNodes:=markedNodes+{vNode};
  FOR node:=1 TO g.nrOfNodes DO
    IF g.matrix.IsSet(vNode,node) & ~(node IN markedNodes) THEN
      g.DFS(markedNodes,node);
    END;
  END;
END DFS;


PROCEDURE (VAR g: Graph) DFSExcept (VAR markedNodes: SET; vNode, xNode: LONGINT);
  VAR node: LONGINT;
BEGIN
  IF (vNode#xNode) THEN
    markedNodes:=markedNodes+{vNode};
    FOR node:=1 TO g.nrOfNodes DO
      IF g.matrix.IsSet(vNode, node) & ~(node IN markedNodes) THEN
        g.DFSExcept(markedNodes,node,xNode);
      END;
    END;
  END;
END DFSExcept;

PROCEDURE (VAR g: Graph) AnyEdge (vNode: LONGINT): BOOLEAN;
  VAR node: LONGINT;
      anyEdge: BOOLEAN;
BEGIN
  node:=1;
  anyEdge:=FALSE;
  WHILE (node<=g.nrOfNodes) & ~anyEdge DO
    anyEdge:=g.matrix.IsSet(vNode,node);
    INC(node);
  END;
  RETURN anyEdge;
END AnyEdge;


PROCEDURE (VAR g: Graph) Init* (v: LONGINT);
  VAR err: BOOLEAN;
BEGIN
  IF v<=MAX(SET) THEN
  (* Diese Abfrage ist nötig, da <NrComps>, <PrComps> und <IsBridge>
     mit Hilfe von SETs realisiert werden. *)
    g.matrix.Init(err);
    IF ~err THEN
      g.nrOfNodes:=v;
    END;
  END; 
END Init;


PROCEDURE (VAR g: Graph) CleanUp*();
BEGIN
  g.matrix.CleanUp();
  g.nrOfNodes:=0;
END CleanUp;


PROCEDURE (VAR g: Graph) SetEdge* (x, y: LONGINT);
BEGIN
  IF (x<=g.nrOfNodes) & (y<=g.nrOfNodes) & (x#y) THEN
    g.matrix.Set(x,y);
  END;
END SetEdge;


PROCEDURE (VAR g: Graph) NrComps* (): LONGINT;
  VAR markedNodes: SET;
      nrOfComps, node: LONGINT;
BEGIN
  markedNodes:={};
  nrOfComps:=0;
  FOR node:=1 TO g.nrOfNodes DO
    IF g.AnyEdge(node) & ~(node IN markedNodes) THEN
      g.DFS(markedNodes,node);
      INC(nrOfComps);
    END;
  END;
  RETURN nrOfComps;
END NrComps;


PROCEDURE (VAR g: Graph) PrComps* ();
  VAR markedNodes, printedNodes: SET;
      nrOfComps, mNode, pNode: LONGINT;
BEGIN
  markedNodes:={};
  nrOfComps:=0;
  FOR mNode:=1 TO g.nrOfNodes DO
    IF g.AnyEdge(mNode) & ~(mNode IN markedNodes) THEN
      printedNodes:={};
      g.DFS(printedNodes, mNode);
      INC(nrOfComps);
      O.String("Komponente ");
      O.Int(nrOfComps,0);
      O.String(":");
      O.Ln;
      FOR pNode:=1 TO g.nrOfNodes DO
        IF (pNode IN printedNodes) THEN
          O.String("Knoten ");
          O.Int(pNode,0);
          O.Ln;
        END;
      END;
      markedNodes:=markedNodes+printedNodes;
    END;
  END;
END PrComps;


PROCEDURE (VAR g: Graph) PrAdj* ();
  VAR i, j: LONGINT;
BEGIN
  FOR i:=1 TO g.nrOfNodes DO
    FOR j:=1 TO g.nrOfNodes DO
      IF g.matrix.IsSet(i,j) THEN
        O.Int(1,2);
      ELSE
        O.Int(0,2);
      END;
    END;
    O.Ln;
  END;
END PrAdj;


PROCEDURE (VAR g: Graph) Connected* (): BOOLEAN;
BEGIN
  RETURN (g.NrComps()=1);
END Connected;


PROCEDURE (VAR g: Graph) IsBridge* (x, y: LONGINT): BOOLEAN;
  VAR markedNodes: SET;
      node: LONGINT;
BEGIN
  IF ~g.matrix.IsSet(x,y) THEN
    RETURN FALSE;
  END;
  markedNodes:={};
  FOR node:=1 TO g.nrOfNodes DO
    IF g.matrix.IsSet(x,node) & (node#y)THEN
      g.DFSExcept(markedNodes,node,x);
    END;
  END;
  RETURN ~(y IN markedNodes);
END IsBridge;


PROCEDURE (VAR g: Graph) IsTree* (): BOOLEAN;
  VAR nrOfEdges, i, j: LONGINT;
BEGIN
  nrOfEdges:=0;
  FOR i:=1 TO g.nrOfNodes DO
    FOR j:=1 TO i DO
      IF g.matrix.IsSet(i,j) THEN
        INC(nrOfEdges);
      END;
    END;
  END;
  RETURN (nrOfEdges=g.nrOfNodes-1) & (g.NrComps()=1);
END IsTree;


END Graph.