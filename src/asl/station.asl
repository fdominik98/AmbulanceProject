

/* Initial beliefs and rules */


/* Initial goals */



/* Plans */



+injured(X,Y)[source(A)] :  A \== self
	<-//.print("found injured at: ",X,";",Y, " from ",A);	
	 countStation(X,Y);
	-injured(X,Y)[source(A)].
		

 
 +neededAmbulance(A, X, Y) <- 
 			//.print("sending to ambulances");
			.send(A, tell, injured(X,Y));
			-neededAmbulance(A,X,Y).	
			
			 
			 
+bid(Injured,D,Ag)
  :  .count(bid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_ambulance(Injured);
     .abolish(bid(Injured,_,_));
     removePercept(bid(Injured,D,Ag)).
     
+bid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_ambulance(Injured) : .my_name(Me)
  <-  .findall(op(Dist,A),bid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));   
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     .send(phoneCenter,tell,bid(Injured,DistCloser,Me));
     +closer(Closer).

  
+allocated(Injured) 
 <- ?closer(Closer);
 .send(Closer,tell,allocated(Injured));
 removePercept(allocated(Injured));
  -closer(Closer).