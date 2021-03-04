# Trigram Search Optimization

One of the best optimizations in this project was to sort the trigrams by document occurrence.
 
If we start with a long list, we need more steps to reduce the number of elements, in theory
we can start with any trigram look up the document ids and apply all other trigrams and and 
build a joint set of the documents until we reach the minimum.

We can start with any trigram, right? If a trigram occurs in 6.000 documents another in 31.000
documents then there can "only" be 6000 documents. So it is safe to start with a list of 6.000
instead of with a list of 31.000 (we need more memory right from the start) ...

Of course we should start with the smallest set possible, so we pick the trigram which is rarest
in the corpus first and it provides the "initial candidate list" of documents. If we combine that
with the second rarest trigram, we might be able to reduce that list with a single operation to a
fraction of the original list of 6.000 candidate documents.

    Reduction starts from: 1769 for shm
    Reduction to: 1750 using trigram: hma
    Reduction to: 632 using trigram: abs
    Reduction to: 497 using trigram: bst
    Reduction to: 497 using trigram: ash
    Reduction to: 291 using trigram: sto
    Reduction to: 291 using trigram: map
    Reduction to: 288 using trigram: rac
    Reduction to: 169 using trigram: iel
    
    
You can see that we reduced the number of candidates in this example from 1769 candidate documents
to 169 candidate documents by combining the 9 rarest trigrams first. So the biggest reduction occurs 
first and when the trigrams come from different words.

Now let's see how many documents there are in the demo index.

    Debug-TrigramOccurence: 'shm': 1769
    Debug-TrigramOccurence: 'hma': 1840
    Debug-TrigramOccurence: 'abs': 3471
    Debug-TrigramOccurence: 'bst': 3663
    Debug-TrigramOccurence: 'ash': 4290
    Debug-TrigramOccurence: 'sto': 4429
    Debug-TrigramOccurence: 'map': 4990
    Debug-TrigramOccurence: 'rac': 5471
    Debug-TrigramOccurence: 'iel': 5653
    Debug-TrigramOccurence: 'eld': 5723
    Debug-TrigramOccurence: 'has': 6191
    Debug-TrigramOccurence: 'fie': 6418
    Debug-TrigramOccurence: 'tra': 6581
    Debug-TrigramOccurence: 'dex': 6840
    Debug-TrigramOccurence: 'act': 9190
    Debug-TrigramOccurence: 'ind': 11084
    Debug-TrigramOccurence: 'rin': 11535
    Debug-TrigramOccurence: 'ore': 12201
    Debug-TrigramOccurence: 'tor': 12852
    Debug-TrigramOccurence: 'nde': 13192
    Debug-TrigramOccurence: 'cla': 13336
    Debug-TrigramOccurence: 'mpo': 13367
    Debug-TrigramOccurence: 'ass': 13438
    Debug-TrigramOccurence: 'str': 13495
    Debug-TrigramOccurence: 'por': 13510
    Debug-TrigramOccurence: 'kag': 13535
    Debug-TrigramOccurence: 'cka': 13542
    Debug-TrigramOccurence: 'ort': 13594
    Debug-TrigramOccurence: 'tri': 13740
    Debug-TrigramOccurence: 'ing': 13755
    Debug-TrigramOccurence: 'ack': 13791
    Debug-TrigramOccurence: 'las': 13809
    Debug-TrigramOccurence: 'pac': 13830
    Debug-TrigramOccurence: 'imp': 13869
    Debug-TrigramOccurence: 'age': 13870
    
There is no reason to start with a list of 13870 documents and to try to reduce that to a list of 
169 documents. It takes longer to read in the document'ids of a list of 13870 documents than for
1769. The Idea is to use the lists which are fast to load, and combine this list, with a slightly
more expensive list.

Well I can reveal, that if we continue with all trigrams and their document id's, we end up with 
141 candidates. So removing 28 further document candidates, costs us to read in 265.345 more 
document'ids from disk. Removing 28 candidates comes at costs to compare these to a list of about 
10.000 times that size.

That means we should stop as soon as this ratio becomes bad enough.