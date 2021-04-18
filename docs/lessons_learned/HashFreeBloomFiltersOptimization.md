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

If we have 13137 unique document IDs, then only an upper bound of 13137 different values can be 
calculated by any hash function.

For bloom filters to be effective we usually need more than one hash function. So the question is,
which particular hash function(s) to use? If the hash value is derived from a cryptographically
secure hash function, we can assume the hash values are independent and uniformly distributed.
So is for every selected subset of this hash value. The first slice of n bit, are independent 
from the second slice of n bit and so on. Therefore having multiple hash functions is easy. 

We can just pick a different slice of n bit for every hash function, we want to apply. So the 
hash function in X, where X is a document ID, simply reduces to:

    h_{ slice_position, slice_size}(X) = (X>>slice_position )&((2^slice_size)-1)
    
So the hash function is a simple bit shift in combination with the and operation. Both of these
operations are directly supported by the CPU and are extremely fast, in comparison to every other
hash function.

    (SHR / SHRX) have a low latency and low cycle count

Multiple hash functions can be calculated in parallel if they are aligned at 32 bits, then the
vector instructions / SSSE3 / AVX instructions can be used to calculate 4 or 8 hashes in 
parallel.

So we don't eliminate the hash functions all-together, but we use a very cheap one, which is
indistinguishable from a simple memory access. Also if we align our document IDs optimally, 
then we just read a value from memory, perform an and operation and then we access the filter 
data to check, whether there is a hit or nohit.