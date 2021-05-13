

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */

+injured(ID)[source(A)]
  <-  countHospital(A).
  
  +bid(B) : .my_name(Me)
 <- ?injured(ID)[source(A)];
 .send(A,tell,hospitalBid(injured(ID),B,Me));
   -bid(_);
   removePercept(bid(B));
   -injured(_)[source(_)].


