# Performance in General

The best performance optimization is reached by code, which is never executed and 
I/O operations which are never performed. 

1) Eliminating unnecessary compute is the main source of performance optimization.
2) Elimination unnecessary I/O operation is the second main source of performance optimization.
3) The next source of performance optimization is to speed up the necessary computations, e.g. by using faster algorithms, hashing, equivalence classes. 
4) The forth source of performance optimization is pre-computation of hard to compute results. (trade I/O for compute)
5) The fifth source of performance optimization is trading of memory (RAM) for compute (CPU) and trading memory (RAM) for (I/O).

In this order. 

## Why in this order ?

Well you can start with the 5th item of the list and probably it will provide enough benefit, to be 
an okay enough result. But in case of a constrained environment like embedded systems, you neither 
have enough compute, nor fast enough I/O nor enough RAM. Even if you have plenty of compute, I/O, or
RAM available somebody has to pay for it beforehand to use your product. 

When you develop your stuff, without any optimizations in mind and start with eliminating unnecessary
compute, your program will always get faster. You just don't have to optimize or trade of CPU-cycles 
you have never spent in the first place. You don't need to spend time or RAM or I/O to cache results 
which don't need any compute at all. 

This is why I didn't start with just an in memory database of the index, though it would of course be 
easy and be very obvious too. But then you make it harder to find the better solutions and all these 
solutions may not justify the gains by any means. But if you start the other way around, the gains may 
still possible and worth the effort and you then can optimize an already good solution, which will
provide again some significant and even adjustable gains, because caching in ram, can be constrained 
and adjusted to a larger system. But optimizing a prematurely bad optimized system is hard to optimize
further.

When you start to think about what computation can be left out, you are also simplifying the solution.