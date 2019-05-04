MODULE HeapSort;


PROCEDURE BuildHeap(VAR heap: ARRAY OF LONGINT; len, nodeIndex: LONGINT);
  VAR swap, index: LONGINT;
      exit: BOOLEAN;
BEGIN
  swap:=heap[nodeIndex-1];
  exit:=FALSE;
  WHILE (nodeIndex<=len DIV 2) & ~exit DO
    index:=nodeIndex*2;
    IF (index<len) & (heap[index-1]<heap[index]) THEN
      INC(index);
    END;
    IF (swap<=heap[index-1]) THEN
      heap[nodeIndex-1]:=heap[index-1];
      nodeIndex:=index;
    ELSE
      exit:=TRUE;
    END;
  END;
  heap[nodeIndex-1]:=swap;
END BuildHeap;
  
  
PROCEDURE Sort*(VAR a: ARRAY OF LONGINT; len: LONGINT);
  VAR swap, index: LONGINT;
BEGIN
  FOR index:=len DIV 2 TO 1 BY -1 DO
    BuildHeap(a,len,index);
  END;
  WHILE (len>1) DO
    swap:=a[0];
    a[0]:=a[len-1];
    a[len-1]:=swap;
    DEC(len);
    BuildHeap(a,len,1);
  END;
END Sort;

END HeapSort.