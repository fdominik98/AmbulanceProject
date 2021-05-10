

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
		-injured(_,_)[source(_)].

      
+stationBid(Injured,D,Ag,CloserAmb)
  :  .count(stationBid(Injured,_,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_station(Injured);
     .abolish(stationBid(Injured,_,_,_)).     
     
+stationBid(Injured,D,Ag,CloserAmb)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_station(Injured)
  <-  .findall(op(Dist,A,CloserAmb),stationBid(Injured,Dist,A,CloserAmb),LD);
     .min(LD,op(DistCloser,Closer,CloserAmb));
      DistCloser < 1000000;
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     .send(Closer,tell,allocated(Injured,CloserAmb)).     
   
-!allocate_station(Injured)
  <- .print("could not allocate injured ",Injured).  

 
 +plesremove(injured(X,Y))
<-
	.findall(A, station(A),LP);
	.send(LP, tell,remove(injured(X,Y)) );
	-injured(_,_)[source(_)];
	-plesremove(injured(_,_))[source(_)].
	

 