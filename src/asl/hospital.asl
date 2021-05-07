

/* Initial beliefs and rules */

/* Initial goals */


/* Plans */

+injured(X,Y)[source(A)] :  A \== self 
  <-  countHospital(X,Y).
  
  +bid(B) : .my_name(Me)
 <- ?injured(X,Y)[source(A)];
 .send(A,tell,bid(injured(X,Y),B,Me));
   -bid(B);
   -injured(X,Y)[source(A)].


