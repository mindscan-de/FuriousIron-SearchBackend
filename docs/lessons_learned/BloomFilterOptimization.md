# Bloom-Filters are a must have in search?

Bloom filters are quite popular in search engine projects. Well I currently think about
using bloom filters to check whether a document id is in a set. The idea is to use a 
compressed dictionary to save IO for the uncertaincy of keeping an document. But maybe 
I already eliminated 85% is it then useful to remove document candidates 15%.

But hashsets of docuemntIds are something similar, they hash the key to search for, so 
I question, whether this is the perfect approach.

## for large documents use (large) bloom filters? 


    // TODO: if trigram documentid lists are too big for direct filtering, then use bloom 
    //       filters but don't double check the positive findings, whether they are false 
    //       positives, just use the negatives to kick out the elements in the hope that, 
    //       that a later Bloom filter for a different trigram would also kick out the 
    //       documentid.

    
we can also use bloomfilters for ranking, where we rank just y accessing he hashvalue and
take it for granted.

## is that a good idea ? at all ? 

    // TODO: queryDocumentIds = filterByBloomFilter( search, wordlistSearchAST, coreCandidatesDocumentIDs );
    
    
## Maybe this is YAGNI?

It seems that bloom-filters are quite over-rated for this particular code search approach.
We always start with a real list of documentsIds at the moment, we can't extract that list 
from the filter.

Also the original bloom-filter was unable to remove documents from the index, or to track 
how many documents were there for this particular hash. (This has been solved somehow, if
you consider that you might be able to "remove" a document which is not in the index.

The current approach is to just read all these documentIds and use an and-operation on 
these two sets. This can be easily done for thousands of DocumentIds. To improve that, 
we need more than that, because this approach has scaling issues if we have millions or
billions of documentIds per trigram. We can for sure solve that by scaling out (e.g. 
doing smaller workloads on many machines). 

But what a bloom filter essentially is, it is a randomized algorithm and data structure where
the hash function compresses the input and maps it into a finite space. If a value which 
should be tested is compressed by the function and the index says, no there is nothing in my
index, which maps to this particular compressed value or it says yes I might know something
which compresses to this particular value.

So if you use different randomized compression functions, one of these may say, well actually
there is nothing I know which compresses to this particular value. If you have a hash function
which has a even distribution, and too many things it has seen all values are going to be 
mapped to the output "maybe i've seen something like that". The solution is now to just grow
the filter with the indexed corpus.

That will also grow the compute for a trigram, when a trigram is very often in the indexed
corpus. I mean that is the reason we skip reading these documentIds in the first place, it 
becomes too burdensome.
