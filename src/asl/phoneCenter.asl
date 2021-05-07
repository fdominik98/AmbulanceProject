

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
		removePercept(injured(X,Y)).	

      
+bid(Injured,D,Ag)
  :  .count(bid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_station(Injured);
     .abolish(bid(Injured,_,_)).
     
+bid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_station(Injured)
  <-  .findall(op(Dist,A),bid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));
      DistCloser < 1000000;
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     .send(Closer,tell,allocated(Injured)).     
   
-!allocate_station(Injured)
  <- .print("could not allocate injured ",Injured).
  
+check(B)[source(A)]
 <- !check(calls);
 removePercept(check(B)[source(A)]).
