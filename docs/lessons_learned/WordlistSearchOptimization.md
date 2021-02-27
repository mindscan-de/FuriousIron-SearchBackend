# Optimizing the Wordlist Search

## Why

After the tri-gram search, a list of candidates (documentIDs) remain, which are likely to 
contain the words which we are searching for. But of course not all documents contain the
the given search terms.

Also the tri-gram search becomes more and more ineffective with every tri-gram we look 
further. The number of rejected documents are declining where the number of documents 
containing the more frequent tri-grams often enough do not filter even one documentID 
further. 

Therefore we break early and proceed on a higher level of search. The next level beyond
the tri-gram-filtering is wordlist-level-filtering. Actually I can't think of a step in 
between right now.  

## What

Our goal is to reduce the number of the remaining candidates by looking into the associated 
wordlist of the document ({{documentid}}.wordlist - file).

The idea is to optimize this reduction step, by increasing early rejections. 

## How

The idea is to pick the most unlikely word in the "AND"-searches first and if not found 
then to drop the document. 

Without even looking / analyzing at the other words of the documents.

Load the wordlist. evaluate the first unlikely word, drop or keep.

Three strategies (2-4) were used and compared against the baseline (1). 

1) Use the native given order given by the search term
2) Use the rarest trigram of a word as an indicator of how unlikly a word is. 
  - Assumption: the rarer the ratest word trigram the more unlikely it is to be found in the document. 
3) Use the word length as an indicator on how likely a word is. 
  - Assumption: the longer the word the more unlikely.  
4) Use a combination of both.

We compile the whole tree. (to have an precise filter)
We compile only a part of the tree. (to have an additional fast filter...)

## Lessons Learned

reduce the number of search-terms instead ordering them only by a criterion. 

### Baseline

I thought the baseline search, by just taking the documentIDs and then applying the AST 
would perform the least best. But it turned out, it's more difficult than that. This tree 
is already compiled and available and executable.

Using this baseline doesn't too much harm.   

### Using the least occurring tri-gram as word importance

The Idea of ordering the "and" search terms by "projected" occurrence, with the most unlikely 
first helped a bit but was not as impressive as i thought. (for 1000 repeats, it was about 
0.8% faster for a full search)  

Looking for documents containing the trigram "shm" will already change the distribution of 
the "sampled" (i mean filtered) documents in a way that it will most likely contain the word 
"hashmap". That what would be reasonable for searching the whole corpus, doesn't apply for 
the prefiltered distribution. Because this trigram is this rare it will already change the 
distribution in a way, that the word "hashmap" becomes very likely. If we looked first for
this word, then we will nearly always look for all the other words for this filtering step
as well.

Just because this technique was useful in the trigram search, it doesn't mean it is useful
for this step too. 

The costs of compiling the tree were worse than the savings due to using this "optimization". 
The less candidates you have the less useful it is to compile this specialized search tree.
But if the rate of expected (surviving) documents is at most 1% of this optimization might 
be useful.  

### Inverse word length approach

Inverse wordlength provides 