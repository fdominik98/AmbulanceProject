

/* Initial beliefs and rules */

started.
/* Initial goals */



/* Plans */

+started : .my_name(ME) <- .send("phoneCenter", tell, station(ME));
							-started.

+injured(ID)[source(A)] 
	<-//.print("found injured at: ",X,";",Y, " from ",A);	
	.findall(B,ambul(B),LP);
	.print(LP);
	 countStation(ID);
	-injured(_)[source(_)].
		

 
 +neededAmbulance(A,ID) <- 
 			//.print("sending to ambulances");
			.send(A, tell, injured(ID));
			-neededAmbulance(_,_)[source(percept)].			


		 
+ambulanceBid(Injured,D,Ag)
  :  .count(ambulanceBid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_ambulance(Injured);
     .abolish(ambulanceBid(_,_,_)).
    
     
+ambulanceBid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_ambulance(Injured) : .my_name(Me)  <-  
  		.findall(op(Dist,A),ambulanceBid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));   
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     .send(phoneCenter,tell,stationBid(Injured,DistCloser,Me,Closer)).     

  
+allocated(Injured,CloserAmb) 
 <-
 .send(CloserAmb,tell,allocated(Injured));
 -allocated(_,_)[source(_)].


 
  
