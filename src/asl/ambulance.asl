

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").


+injured(X,Y)[source(A)] :  A \== self 
  <- +source(A); 
  countAmbulance(X,Y).
  
+bid(B) : .my_name(Me)
 <- ?injured(X,Y); ?source(S);
 .send(S,tell,bid(injured(X,Y),B,Me)).
 
 +allocated(Injured) : .my_name(Me)
 <- +Injured;
 ?injured(X,Y);
 .print(X," ",Y," ",Me).
 
 

  