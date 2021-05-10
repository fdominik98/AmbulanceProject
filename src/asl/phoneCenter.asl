

/* Initial beliefs and rules */

/* Initial goal */

!check(calls).

/* Plans */

      
+!check(calls)  <-	
    checkIfHas(call);
      !check(calls).  
 

+injured(X,Y) <- 
		//.print("found injured at: ",X,";",Y);
       .send(station1, tell, injured(X,Y));
		.send(station2, tell, injured(X,Y));
		-injured(X,Y);
		removePercept(injured(X,Y)).

      
+stationBid(Injured,D,Ag)
  :  .count(stationBid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_station(Injured);
     .abolish(stationBid(Injured,_,_)).     
     
+stationBid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_station(Injured)
  <-  .findall(op(Dist,A),stationBid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));
      DistCloser < 1000000;
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     .send(Closer,tell,allocated(Injured)).     
   
-!allocate_station(Injured)
  <- .print("could not allocate injured ",Injured).
  
+check(B)[source(A)]
 <- !check(calls);
 -check(B);
 removePercept(check(B)).
 
 +plesremove(injured(X,Y))
<-
	.findall(A, station(A),LP);
	.send(LP, tell,remove(injured(X,Y)) );
	-injured(X,Y)[source(_)];
	-plesremove(injured(X,Y))[source(_)].
	

 