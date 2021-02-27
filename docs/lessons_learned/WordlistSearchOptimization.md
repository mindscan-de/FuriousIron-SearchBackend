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

The goal is to reduce the number of the remaining candidates by looking into the associated 
wordlist of the document ({{documentid}}.wordlist - file).

The idea is to optimize this reduction step, by increasing very early rejections. One 
Idea for example would be to look for the longest word first, because longer words are 
less likely in documents than short ones. Or think about the probably rarest word in 
the remaining documents. One of them will produce the highest candidate drop out. We
want to use this word first and then eliminate the suggested document from the list of
candidates.
 

## How

The idea is to pick the most unlikely word in the "AND"-searches first and if not found 
then to drop the document. Every early decision we make, helps us to save compute and 
further disk accesses, especially when we want to rank the documents.

Load the wordlist for the candidate documentID. Evaluate an optimized compiled AST which
is hopfully optimized to have a high early high drop-out rate.

Three strategies (2-4) are used and compared against the baseline (1). 

1) Use the native given order given by the search term
2) Use the rarest trigram of a word as an indicator of how unlikely a word is 
  - Assumption: the rarer the rarest word tri-gram the more unlikely it is to be found in the document 
3) Use the word length as an indicator on how likely a word is
  - Assumption: the longer the word the more unlikely
4) Use a combination of 2) and 3)

Another Idea is to assume that we just limit the number of elements we look for in the tree:

We compile the whole tree. (to have an precise filter)
We compile only a part of the tree. (to have an fast filter...)


## Lessons Learned

### Baseline

I thought the baseline search, by just taking the documentIDs and then applying the AST 
would perform the least best. But it turned out, it's more difficult than that. This tree 
is already compiled and available and executable.

Using this baseline doesn't do too much harm.

### Using the least occurring tri-gram as word importance

The idea of ordering the "and"-combined search terms by "projected" occurrence, with the 
most unlikely first helped only a bit but was not as impressive as i thought. (for 1000 
repeats, it was about 0.8% faster compared to the baseline)  

Looking for documents containing the trigram "shm" will already change the distribution of 
the "sampled" (I mean filtered) documents in a way, that it will most likely contain the word 
"hashmap". That what would be reasonable for searching the whole corpus, doesn't apply for 
the pre-filtered document distribution. Because this tri-gram is this rare it will already 
change the distribution in a way, that the word "hashmap" becomes highly likely. That means
it will reduce the ability of the wordlist based filtering to reject a document fast. There-
fore we have to consider the whole optimized query AST - to be improved further. 

Just because this sorting technique was useful in the trigram search, it doesn't mean it 
is useful for this step too. But i'm still intrigued, whether there is a way to optimize
this problem.

But this technique is not completely useless. It will still improve the rejection speed, 
but not at the very first words. 

The costs of compiling the tree were slightly worse than the savings due to using this "optimization". 
The less candidates you have the less useful it is to compile this specialized search tree.
But if the rate of expected (surviving) documents is at most 1% of this optimization might 
be useful.

But we also need  to compile the baseline tree to match the word-encoding in the wordlist
stored on disk, for a certain document. So this additional sorting/word projection might 
still be okay. 

### Inverse word length approach

Inverse word length provides not much effect either. (well, I had an implementation error
while measuring the efficiency of this approach). But after correction i could measure that
it is sometimes slightly better or slightly worse than the projected word occurrence. (0.3%).
This is currently not much to invest too much time.

### Combination of inverse word length and projected occurrence

It seems to be often enough 0,5 ms faster than the naive baseline. So this configuration 
was kept for further search engine development.    

### Keeping the most interesting words

If we limit the scope of the full words we are looking for, we might assume, that we can be
less precise withj filtering and gain another reduction for very few searches. By filtering 
first more. It took only 1 ms more to retrieve the precise list of documents. A 1 ms saving
would still contain many false positive documents. 

As explained in the previous section, we can not reliably detect the most interesting words.
The dropoutrate sometimes not present on the first comarisons like in this search for: 

    "+import +package +class +index +store +abstract +field +hashmap +string"

picking the most interesting words will reduce to (after optimized trigrams 169 Candidates)
after the first most "interesting" word, we end up with 168 documents, then 155, and only the
third word will approximately halving the candidates to 72 whereas the true reduction should
deliver 61 results.  

169: 

"hashmap" -> 168
"hashmap", "abstract" -> 155
"hashmap", "abstract", "store" -> 72
"hashmap", "abstract", "store", field" -> 72
"hashmap", "abstract", "store", field", "index" -> 61

All other words missing words compared to above do not provide any additional rejects.

"import", "package", "string", "class". It would be really cool to identify those words
in advance and skip them in the filtering operation. That would save some useless Map
operations.

Reduction starts from: 1769 for shm
Reduction to: 1750 using trigram: hma
Reduction to: 632 using trigram: abs
Reduction to: 497 using trigram: bst
Reduction to: 497 using trigram: ash
Reduction to: 291 using trigram: sto
Reduction to: 291 using trigram: map
Reduction to: 288 using trigram: rac
Reduction to: 169 using trigram: iel

Using a different candidate to document-trigram ratio 1:32 -> 1:64 will reduce this query
to 143 possible documents to start with so it is 26 documents less to start with. Where we 
have to load the wordlists from disk. So shifting back workload to the trigram search will 
definetly provide some minor gains. As long as we can keep the search for each trigram 
somehow constant. resulting in a linear 
