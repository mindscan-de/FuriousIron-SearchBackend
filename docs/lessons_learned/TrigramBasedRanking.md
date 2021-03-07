# Ranking the Documents for Cheap

One of the problems of the code search engine is that the results must be 
ranked somehow. So we want a technical ranking instead of a reputation based 
ranking. Because all code is garbage. Just because a beautiful code fragment 
mentions a single term, it is not necessarily the code fragment you are looking 
for. So developing something like a page rank algorithm seems not to be very 
useful.

Approach for initial ranking TTF-IDF

## TrigramTermFrequency (TTF) in the Document (TTF) 

We know which trigrams are in a document with the ".trigram"-files. Currently
we know that it occurs at least once, but it should be more precise, how many
times it is in this document. 

What we should try to find out, is to how many times a trigram occurs in the 
document. This must be part of the indexing process.   

## TrigramOccurrence in the whole corpus (IDF)

We currently have a ".refcount" file for each tri-gram. Which is essentially 
an information on how many documents contain this particular trigram.

This is comparable to the so called IDF (Inverse Document Frequency) of a 
particular trigram instead of the word. (We don't have this information about 
all these words, and maybe we don't need them either.)

## Ranking vs Result-Preview

A good ranking mechanism can make up for the lack of a preview of the most 
interesting parts of a document in the search result. Because people will
click the first elements one after the other, if they are really into finding
something particular. I often tried 20 documents or so, but i never did that 
for 600.

Result-Previews can make up for bad or absent ranking, because the user can
scan the results faster whether they fit their search criteria.

But doing actually both of them is even more useful. But you should solve at 
least one of these problems, and maybe identifying the most interesting lines
in a document might be the same problem as identifying which of many documents
is the most interesting one to start with. 

## Do not normalize

A source code mentioning a certain word/trigram you are looking for more often, 
it is very probably the source code, which you are looking for. So we won't normalize, 
because all the other words are already known to be in the document.   

The number of occurrences of the trigrams matter, otherwise on exact searches, all documents have the same score.  