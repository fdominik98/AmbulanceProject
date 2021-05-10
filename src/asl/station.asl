

/* Initial beliefs and rules */

started.
/* Initial goals */



/* Plans */

+started : .my_name(ME) <- .send("phoneCenter", tell, station(ME));
							-started.

+injured(X,Y)[source(A)] 
	<-//.print("found injured at: ",X,";",Y, " from ",A);	
	.findall(B,ambul(B),LP);
	.print(LP);
	 countStation(X,Y);
	-injured(_,_)[source(_)].
		

 
 +neededAmbulance(A, X, Y) <- 
 			//.print("sending to ambulances");
			.send(A, tell, injured(X,Y));
			-neededAmbulance(_,_,_)[source(percept)].
			

+remove(injured(X,Y))
<-
	.findall(A, ambulance(A),LP);
	.send(LP, tell,remove(injured(X,Y)) );
	-allocated(injured(X,Y))[source(_)];
	-remove(injured(X,Y))[source(_)].	
	
+plesremove(injured(X,Y))
<-
	.send("phoneCenter", tell,plesremove(injured(X,Y)) );
	-plesremove(injured(_,_))[source(_)].	
			 
			 
+ambulanceBid(Injured,D,Ag)
  :  .count(ambulanceBid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_ambulance(Injured);
     .abolish(ambulanceBid(Injured,_,_)).
    
     
+ambulanceBid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_ambulance(Injured) : .my_name(Me)
  <-  //?injured(X,Y);
  		.findall(op(Dist,A),ambulanceBid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));   
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     .send(phoneCenter,tell,stationBid(Injured,DistCloser,Me,Closer)).     

  
+allocated(Injured,CloserAmb) 
 <-
 .send(CloserAmb,tell,allocated(Injured));
 -allocated(Injured,_)[source(_)];
 removePercept(allocated(Injured)).

 
  
