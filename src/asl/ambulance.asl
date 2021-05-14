

/* Initial beliefs and rules */


/* Initial goals */



/* Plans */




+injured(ID)[source(A)]
  <-   countAmbulance(ID).

	
  
+bid(B) : .my_name(Me)
 <- ?injured(ID)[source(A)];
 .send(A,tell,ambulanceBid(injured(ID),B,Me));
  -bid(_);
  removePercept(bid(B));
  -injured(_)[source(_)].
  
 
 +allocated(Injured) : .my_name(Me)
 <- 
 .print("allocated ambulance: ", Injured ,Me);
 saveInjured(Injured);
 -allocated(_)[source(_)].



  
+neededHospital(H,ID) <- 
	.print("sending to hospitals");
	.send(H, tell, injured(ID));
	-neededHospital(_,_)[source(percept)].
	
			
			
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



  