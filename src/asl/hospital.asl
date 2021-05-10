

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */

+injured(X,Y)[source(A)]
  <-  countHospital(X,Y).
  
  +bid(B) : .my_name(Me)
 <- ?injured(X,Y)[source(A)];
 .send(A,tell,hospitalBid(injured(X,Y),B,Me));
   -bid(B);
   removePercept(bid(B));
   -injured(_,_)[source(_)].


