# Performance in General

The best performance optimization is code, which is never executed and I/O operations which are never performed. 

1) So eliminating unnecessary compute is the main source of performance optimization.
2) Elimination unnecessary I/O operation is the second main source of performance optimization.
3) The next source of performance optimization is to speed up the necessary computations, e.g. by using faster algorithms, hashing, equivalence classes. 
4) The forth source of performance optimization is pre-computation of hard to compute results. (trade I/O for compute)
5) The fifth source of performance optimization is trading of memory (RAM) for compute (CPU) and trading memory (RAM) for (I/O).

In this order. 

## Why in this order ?

Well you can start with the 5th source, and probably it will provide enough benefit, to be an okay enough result. 
But in case you are in a constrained environment like embedded systems, you neither have enough compute, nor fast 
enough I/O nor enough RAM. Even if you have plenty of compute, I/O, or Ram available somebody has to pay for it. 

When you develop your stuff, without any optimizations in mind and start with eliminating unnecessary compute, your
program will always get faster. And you don't have to optimize or trade of cpu-cycles you have never spent. You don't
need to spend time and ram or I/O to cache results which don't need any compute at all. 

This is why I didn't start with just an in memory database of the index, though it would of course be easy and
be obvious of course too. But then you can't find the better solutions and all these solutions would not justify
the gains by any means. But if you start the other way around, the gains are worth the effort, and you then can
optimize an already optimized solution, which will provide again some significant and even adjustable gains, 
because caching in ram, can be constrained and adjusted to the larger system.