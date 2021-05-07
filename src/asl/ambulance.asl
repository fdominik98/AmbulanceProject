

/* Initial beliefs and rules */

/* Initial goals */



/* Plans */




+injured(X,Y)[source(A)] :  A \== self 
  <-   countAmbulance(X,Y).

 
  
+bid(B) : .my_name(Me)
 <- ?injured(X,Y)[source(A)];
 .send(A,tell,bid(injured(X,Y),B,Me));
  -bid(B).
  
 
 +allocated(Injured) : .my_name(Me)
 <-  ?injured(X,Y)[source(A)];
 .print("allocated ambulance: ",X," ",Y," ",Me);
 saveInjured(X,Y);
  removePercept(allocated(Injured));
  -injured(X,Y)[source(A)].
  
+neededHospital(H,X,Y) <- 
	.print("sending to hospitals");
	.send(H, tell, injured(X,Y));
	removePercept(neededHospital(H,X,Y)).
			
			
+bid(Injured,D,Ag)
  :  .count(bid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_hospital(Injured);
     .abolish(bid(Injured,_,_));
     removePercept(bid(Injured,D,Ag)).
     
+bid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_hospital(Injured) : .my_name(Me)
  <-  .findall(op(Dist,A),bid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));   
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     goToHospital(Closer).

+ambulanceReleased(A) 
	<- .send("phoneCenter",tell, check(A));
	removePercept(ambulanceReleased(A)).
   

 
 

  