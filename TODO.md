# FuriousIron - SearchBackend

## Search 2

Well there were at least two implementations of search already. But let's call it version zero,
was the ability to search one word only. But it was a start, and was the minimal viable product

The first search implementation was quite limited and lacks performance in case of many search 
terms, since this system first makes sure that the searched word is in the document. So the
search for two terms "+TermA +TermB" will produce two lists with certain outcome, that the terms
are in the result list. 

But if TermA and TermB are in each list, i will check the wordlist of the document twice once
for TermA and once for TermB. So this takes some extra time. The core search is implemented by 
using trigrams, so if i search for "+searchquery +performance" i look for two terms technically
each searched like this 

* build a list containing all these trigrams "sea","ear","arc","rch","chq","hqu","que","uer","ery"
* build a list containing all these trigrams "per","erf","rfo","for","orm","rma","man","anc","nce"

But if this search would knew, that it looks for two words, it would be more effective to combine
both trigram lists before core search, and then check its result set and each result's word catalog 
for both words. No double access to the word list and even fewer result candidates before that 
"expensive" search. Also think of the idea, that maybe one or two of the trigrams are in in both 
words because they share a common prefix. There is no need to access the indexes for these trigrams 
twice. Just because we search two words instead of one.   

So it is useful, that we have two kind of searches, one on the semantic level of the query and one 
on the technical level of how the index is built. The semantic level would check for the words in 
the document and the technical index/core level would try to reduce the number of potential candidates
to a minimum before checking these potential candidates on a semantic level. The idea is to have a
very high reject rate at the lowest level first.

But even the core search can be optimized, by using a prediction which of he trigrams are the most 
rarest items in the search end to search them first. Because if you look for the word "import", 
"public", "package" in a .java file it is nearly certain that you will find it here. So why even 
look at these words or their trigrams? If we prefer the rarest items over the certain items, the
candidate list is already short, so we don't need to do the rejects if only less than 1 percent of 
the result candidates would be rejected by this rule? So if we can order them by prediction of
finding them in the index, we maybe can stop reducing the number of candidates earlier even before 
going through the whole trigram catalog. If we calculate the join of two rare items, the joined set
is even smaller, repeat that until the rejection rate is below a certain threshold and then just
return the candidate documents. if the list is already small. The next step can take care of it.

Even through sorting according to the predicted length of the results, we can improve the speed of
the search by at least one or two magnitudes.  
