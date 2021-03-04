# Optimizing the Wordlist Search

## Why

After a tri-gram search, a list of candidates (documentIDs) remains, which is likely to 
contain the words which we are searching for. But of course not all documents contain
the given search terms.

Also a tri-gram search becomes more and more ineffective with every tri-gram we look 
further. The number of rejected documents are declining where the number of documents
containing the more frequent tri-grams raises. Often enough these more frequent tri-
rams do not filter even one documentID further.

Therefore we break the tri-gram-search early and proceed on a higher level of search 
after enough elements were removed or it becomes wasteful to continue. The next level 
beyond the tri-gram-filtering is wordlist-level-filtering. Actually I can't think of 
a step in between right now.

Actually:
I really thought about bloom filters where the documentIDs are the "words". This might
be useful (in a setting of hundred of thousands) of documents per tri-gram, but it will 
take too much effort, right now, to do it. Loading a bloom filter from disk into memory
and prepare the datastucture in memory seems too much of a hassle for me right now.  

## What

The goal is to reduce the number of the remaining candidates by looking into the associated 
wordlist of the document (the ```{{documentID}}.wordlist``` - file).

The idea is to optimize this reduction step, by increasing very early rejections. One 
idea for example would be to look for the longest word first, because longer words are 
less likely to occur in documents rather than short ones. Or think about the probably 
rarest word in the remaining documents. One of them will produce the highest candidate 
drop out. We want to use this word first and then eliminate the suggested document from 
the list of candidates. At best, for the cheapest price.
 

## How

The idea is to pick the most unlikely word in the "AND"-searches first and if not found 
then to drop the document candidate. Every early decision we make, helps us to save a ton
of compute and further disk accesses, especially when we want to rank the documents.

Load the word list for the candidate documentID. Evaluate an optimized compiled AST against 
the word list where the AST hopefully is optimized to have an early high drop-out rate.

Three strategies (2-4) are used and compared against the baseline (1). 

1) Use the native given order given by the search term.
2) Use the rarest tri-gram of a word as an indicator of how unlikely a word is 
    - Assumption: the more rare the rarest word tri-gram the more unlikely the word is to be found in the document 
3) Use the word length as an indicator on how likely a word is
    - Assumption: the longer the word the more unlikely
4) Use a combination of 2) and 3)

Another Idea is to assume that we just limit the number of elements we look for in the AST.

* We compile the whole tree. (to have an precise filter)
* We compile only a part of the tree. (to have again a fast filtering mechanism)

## Lessons Learned

### Baseline

I thought the baseline search, by just taking the documentIDs and then applying the AST 
would perform the least best. But it turned out, it's more difficult than that. Humans 
are often quite correct about which word is of most importance for them as well as for 
the search, therefore the human performs quite well at optimizing the search strategy.

Using this baseline doesn't do too much harm.

The real question is, can we do better?

### Using the least occurring tri-gram as word importance

The idea of ordering the "and"-combined search terms by their "projected" occurrence, with
the most unlikely projected word occurrence first helped just a bit, but was not as impressive 
as I thought it would be. For 1000 repeats, it was about 0.8% faster compared to the baseline.  

Looking for documents containing the trigram "shm" will already change the distribution of 
the "sampled" (I mean filtered) documents in a way, that it will most likely contain the word 
"hashmap". That what would be reasonable for searching the whole corpus, doesn't apply for 
the pre-filtered document distribution. Because this tri-gram is this rare it will already 
change the distribution in a way, that the word "hashmap" becomes highly likely. That means
it will reduce the ability of the wordlist based filtering to reject a document fast. There-
fore we have to consider the whole optimized query AST - must be improved further. 

Just because this sorting technique was useful in the trigram search, it doesn't mean it 
is useful for this step too. But I'm still intrigued, whether there is a way to optimize
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

    +import +package +class +index +store +abstract +field +hashmap +string
    

picking the most interesting words will reduce to (after optimized trigrams 169 Candidates)
after the first most "interesting" word, we end up with 168 documents, then 155, and only the
third word will approximately halving the candidates to 72 whereas the true reduction should
deliver 61 results.  

    Starting at 169 candidates
    
    "hashmap" -> 168
    "hashmap", "abstract" -> 155
    "hashmap", "abstract", "store" -> 72
    "hashmap", "abstract", "store", "field" -> 72
    "hashmap", "abstract", "store", "field", "index" -> 61

The better solution according to the elimination pattern is
    
    +store + abstract +index +hashmap +field +import +package +class  +string

All other words missing words compared to above do not provide any additional rejects -
"import", "package", "string", "class". It would be really cool to identify those words
in advance and skip them at the filtering operation. That would save some useless Map
operations. While "hashmap" cancelled at least one document, it might be also be removed
for a different reason. In that case "hashmap" is not providing additional value. 
  

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

## A new idea based on an observation of tri-gram elimination

One idea of identifying words with less importance would be to identify the tri-grams, 
which did not provide (significant) reduction or even none at all and sort them to a 
later comparison step. If you look at the reduction rates the "ash" and "map" provided, 
then you see that this word should be penalized and moved further back in the AST search.

So let's have a look into the tri-grams again and which provided value and which don't.

    Reduction starts from: 1769 for shm
    Reduction to: 1750 using trigram: hma
    Reduction to: 632 using trigram: abs
    Reduction to: 497 using trigram: bst
    Reduction to: 497 using trigram: ash  * hashmap
    Reduction to: 291 using trigram: sto
    Reduction to: 291 using trigram: map  ** hashmap
    Reduction to: 288 using trigram: rac
    Reduction to: 169 using trigram: iel
    Reduction to: 169 using trigram: eld  * field
    Reduction to: 169 using trigram: has  *** hashmap
    Reduction to: 168 using trigram: fie
    Reduction to: 167 using trigram: tra
    Reduction to: 143 using trigram: dex
    Reduction to: 143 using trigram: act  * abstract
    Reduction to: 142 using trigram: ind
    Reduction to: 142 using trigram: rin  * string
    Reduction to: 142 using trigram: ore  * store
    Reduction to: 142 using trigram: tor  ** store
    Reduction to: 142 using trigram: nde  * index
    Reduction to: 142 using trigram: cla  * class
    Reduction to: 141 using trigram: mpo  
    Reduction to: 141 using trigram: ass  ** class
    Reduction to: 141 using trigram: str  ** string
    Reduction to: 141 using trigram: por  * import
    Reduction to: 141 using trigram: kag  * package
    Reduction to: 141 using trigram: cka  ** package
    Reduction to: 141 using trigram: ort  ** import
    Reduction to: 141 using trigram: tri  *** string
    Reduction to: 141 using trigram: ing  **** string
    Reduction to: 141 using trigram: ack  *** package
    Reduction to: 141 using trigram: las  *** class
    Reduction to: 141 using trigram: pac  **** package
    Reduction to: 141 using trigram: imp  *** import
    Reduction to: 141 using trigram: age  ***** package

Package (*****), String(****), Import, Class, HashMap (***) collected the highest penalties. 
We should be able to approximate the scheme for future penalization of unprocessed trigrams.
 
But also have a look that the first trigram (mpo) of import provided "value". So we might be
able to optimize the words. We reward the tri-grams, which led to a reduction, we penalize 
the trigrams not advancing the mission, and we penalize all trigrams not processed. 
But if a word was not taken into account, we award each unseen word once, but penalize every 
other trigram of the word.

This is a calculation we can do for cheap.