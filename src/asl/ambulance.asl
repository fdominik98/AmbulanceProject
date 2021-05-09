

/* Initial beliefs and rules */

/* Initial goals */



/* Plans */




+injured(X,Y)[source(A)]
  <-   countAmbulance(X,Y).

 
  
+bid(B) : .my_name(Me)
 <- ?injured(X,Y)[source(A)];
 .send(A,tell,ambulanceBid(injured(X,Y),B,Me));
  -bid(B);
  removePercept(bid(B)).
  
 
 +allocated(Injured) : .my_name(Me)
 <-  ?injured(X,Y)[source(A)];
 .print("allocated ambulance: ",X," ",Y," ",Me);
 saveInjured(X,Y);
  -allocated(Injured);
  removePercept(allocated(Injured));
 -injured(X,Y)[source(A)];
 removePercept(injured(X,Y)[source(A)]).
  
+neededHospital(H,X,Y) <- 
	.print("sending to hospitals");
	.send(H, tell, injured(X,Y));
	-neededHospital(H,X,Y);
	removePercept(neededHospital(H,X,Y)).
			
			
+hospitalBid(Injured,D,Ag)
  :  .count(hospitalBid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_hospital(Injured);
     .abolish(hospitalBid(Injured,_,_)).
     
     
+hospitalBid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_hospital(Injured) : .my_name(Me)
  <-  .findall(op(Dist,A),hospitalBid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));   
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     goToHospital(Closer).

+ambulanceReleased(A) 
	<- .send("phoneCenter",tell, check(A));
	-ambulanceReleased(A);
	removePercept(ambulanceReleased(A)).
   

 
 

  