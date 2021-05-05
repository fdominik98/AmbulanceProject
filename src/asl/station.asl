

/* Initial beliefs and rules */


/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").

+injured(X,Y)[source(A)] :  A \== self 
  <- countStation(bid).  

 
 +neededAmbulance(A) <- ?injured(X,Y);
			.send(A, tell, injured(X,Y)).
			 
			 
+bid(Injured,D,Ag)
  :  .count(bid(Injured,_,_),2)  // two bids were received
  <- .print("bid from ",Ag," for ",Injured," is ",D);
     !allocate_ambulance(Injured);
     .abolish(bid(Injured,_,_)).
     
+bid(Injured,D,Ag)
  <- .print("bid from ",Ag," for ",Injured," is ",D).

+!allocate_ambulance(Injured) : .my_name(Me)
  <-  .findall(op(Dist,A),bid(Injured,Dist,A),LD);
     .min(LD,op(DistCloser,Closer));
    // DistCloser < 10000;
     .print("Injured ",Injured," was allocated to ",Closer, " options were ",LD);
     .send(phoneCenter,tell,bid(Injured,DistCloser,Me));
     +closer(Closer).
   
-!allocate_ambulance(Injured)
  <- .print("could not allocate injured ",Injured).
  
+allocated(Injured) 
 <- ?closer(Closer);
 .send(Closer,tell,allocated(Injured)).  