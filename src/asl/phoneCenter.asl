

/* Initial beliefs and rules */

/* Initial goal */

!check(calls).

/* Plans */

+!check(calls) : true
   <- actIfHasCall(call);
      !check(calls).
