

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */

+injured(X,Y,ID)[source(A)]
  <-  countHospital(X,Y).
  
  +bid(B) : .my_name(Me)
 <- ?injured(X,Y,ID)[source(A)];
 .send(A,tell,hospitalBid(injured(X,Y,ID),B,Me));
   -bid(B);
   removePercept(bid(B));
   -injured(_,_,_)[source(_)].


