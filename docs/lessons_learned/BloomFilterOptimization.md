# Bloom-Filters are a must have in search?

Bloom filters are quite popular in search engine projects. Well I currently think about
using bloom filters to check whether a document id is in a set. The idea is to use a 
compressed dictionary to save IO for the uncertaincy of keeping an document. But maybe 
I already eliminated 85% is it then useful to remove document candidates 15%.

but hashsets are something similar, they hash the key to search for...

# for large documents use bloom filters? 


    // TODO: if trigram documentid lists are too big for direct filtering, then use bloom 
    //       filters but don't double check the positive findings, whether they are false 
    //       positives, just use the negatives to kick out the elements in the hope that, 
    //       that a later Bloom filter for a different trigram would also kick out the 
    //       documentid.
