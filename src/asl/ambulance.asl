

/* Initial beliefs and rules */

/* Initial goals */



/* Plans */




+injured(X,Y)[source(A)] :  A \== self 
  <- +source(A); 
  countAmbulance(X,Y).
 
  
+bid(B) : .my_name(Me)
 <- ?injured(X,Y); ?source(S);
 .send(S,tell,bid(injured(X,Y),B,Me));
  removePercept(bid(B));
   -source(A).
 
 +allocated(Injured) : .my_name(Me)
 <- +Injured;
 ?injured(X,Y);
 .print("allocated ambulance: ",X," ",Y," ",Me);
 saveInjured(X,Y);
  removePercept(allocated(Injured));
  -Injured.
 
 

  