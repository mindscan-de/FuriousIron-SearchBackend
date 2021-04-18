# Hash Free Bloom Filters

## Problem Statement

Bloom-Filters can be quite useful in calculating the joint set for two or more sets. But is there a 
way to implement a bloom filter without using an explicit hash function, or where the hash function
is so simple that the effort for the hash function can be reduced to one or two cpu cycles per test?

## Solution

As always, it depends... 

In our case our set contains document IDs, which we want to filter using other sets of document IDs
and only retain these document IDs which are present in all other sets.

A document ID is in our case is basically a cryptographic hash of the document URL. This value is
calculated once for each document URL e.g. MD5, SHA1 or SHA256. The result is an array of bits of
size of 128 to 256. 

Let's assume we have a list or a set of k unsorted document IDs, then we can find out, what the 
next bigger power of 2 to enumerate all document IDs. E.g. Lets assume that the term "createElement"
is contained 13137, then the next bigger power of two is 16384. 

    2^( math.ceil( log_2( k ) ) )

To create a more sparse representation we can introduce a sparsety factor to improve the dropout:

    2^( math.ceil( sparsety_factor + log_2( k ) ) )

Let's assume we have chosen our sparsety factor that the we calculate this value to 65536. That 
means that we have an effective way to calculate a modulo operation by a bitwise and-operation.
If we have 13137 unique document IDs, then only an upper bound of 13137 values can be calculated
by any hash operation.