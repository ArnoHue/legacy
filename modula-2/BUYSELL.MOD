MODULE BuySell;

IMPORT O:=Out;

CONST N*=8;

TYPE ListT*=ARRAY N OF LONGINT;
     DateT*=INTEGER;
     
PROCEDURE BuySellDate*(l: ListT; VAR buy, sell: DateT; VAR profit: LONGINT);
  VAR rate: ListT;
      possBuy, possSell, lastLocalMin: DateT;
BEGIN
  buy:=0;
  sell:=0;
  possSell:=0;
  lastLocalMin:=0;
  rate[0]:=l[0];
  WHILE possSell<N-1 DO
    INC(possSell);
    rate[possSell]:=rate[possSell-1]+l[possSell];
    IF (l[possSell]>0) & ((possSell=N-1) OR (l[possSell-1]<=0)) THEN
      (* Lokales Maximum gefunden *)
      possBuy:=lastLocalMin;
      WHILE possBuy<possSell DO
        IF (rate[possBuy]<rate[lastLocalMin]) THEN
          (* Niedrigster Kurs seit dem letzten lokalen Maximum gefunden *)
          lastLocalMin:=possBuy;
        END;
        IF (rate[possSell]-rate[possBuy] > rate[sell]-rate[buy]) THEN
          buy:=possBuy;
          sell:=possSell;
        END;
        INC(possBuy);
      END;
    END;
  END;
  profit:=rate[sell]-rate[buy];
END BuySellDate;

END BuySell.
