# FuriousIron-SearchBackend

FuriousIron-SearchBackend is a simple search engine backend. At the moment this is a completely
private educational project and not fit for any purpose whatsoever.

## The Idea

The Search-Frontend (FuriousIron-Frontend) needs a Search-Backend, which provides the frontend 
with some kind of search content/answers. I decided to give Jersey + Tomcat a try to provide a 
RESTful API, since it seems to me being the easiest solution to get the whole pipeline running.
Later adjustments are a planned part of this journey.

The main goal of the SearchBackend is, to make use of the inverse indexes and caches, that are
stored on disk, in order to implement a RESTful service for a complete search engine. The main 
goal is to determine, what kind of forward and inverse indexes are required to build a complete 
code search engine.

Since I don't want to implement a web-server right now, I picked a technology, which i could
find a simple tutorial for.

A big "Thank you!" goes to [o7planning.org](https://o7planning.org/de/11199/die-anleitung-zum-java-restful-web-services-fur-den-anfanger) for providing a useful tutorial.

What I hope for is, either I will develop a code search engine, or make informed decisions on
how to implement a code search engine using ready stacks, like ElasticSearch. But I also want
to interface some ML components later and I don't want to get stuck on a certain API and its 
behavior. And because code eventually contains bugs, i don't want to rely on other parties to
fix or integrate them, before I can mode on. So doing it on my own, is a quite obvious choice.

## MVP [Done]

The FuriousIron-Front-end project needs a simple backend to provide search results (and probably 
the metadata and the content - which might move to a completely different project (Content-Delivery)).

* Retrieve the queries __[done]__
* Query parsing __[done]__
  * QueryExample working now: "getoutput -stream -@test" __[done]__
* Answer queries with search-results __[done]__
* Content-Delivery
  * Provide meta data on search results __[done]__
  * Provide content data on search results __[done]__
* Since this project is considered a proof of concept, i won't implement any type of sophisticated persistence nor use a database __[done]__

Simplicity is key. Because a minimal product can be changed much faster and be adapted to your 
needs, as your needs begin to grow. I prefer having a whole pipeline working, the goal is to not 
spend too much time on gold plating the code, which I'm going to delete at the next iteration, 
but instead having the whole pipeline running as soon as possible, even if it lacks features. 

I also prefer to defer unnecessary architectural decisions, as long they don't need attention. 
So please don't expect me to provide a full architecture, or a full grown idea. It will either 
evolve or die.

That said, please remember this is a private educational project.

### MVP II - Performance Edition [Done]

Some people made a remark, that the search takes quite a long time (e.g. over 30 seconds) using 
the first and naive search strategy. So I had to re-think how to approach the search. Therefore
I decided to split the search into a core search and an abstract search. The search query leads
to an abstract search tree, which is translated into a core search, which then itself can be 
optimized for very fast candidate drop out right from the beginning for very low costs. 

It is good enough to skip between 30 to 95 of all inverse references to be processed, therefore 
saving lots of I/O time. If the number of filtered candidates are less than about three percent 
of the candidates for the next trigram, we just skip all remaining calculations. Not to compute 
useless things is the biggest speedup.

If we are searching for combination of words in a document e.g. by "and"-ing search terms we
can search for the documents containing all tri-grams first, before sending them to the next 
stage of elimination. We prefer a high drop out rate for very low costs.  

The word level should be optimized too. The word is more valuable for a high rejection if it
is more seldom. We can measure the seldomness, by evaluating the trigram occurence and sort 
the words by trigram occurence. Words are the "min" of all contained trigrams, if equal, then 
the longer word is the most valuable. I call this method relative word occurrence prediction.
(spoiler: this calculation still can be improved)    

Also a cache for the queries should be provided. Because filtering on word level and ranking on 
word level, as well ranking on document level are kind of expensive operations. These results 
should be cached as long as the index is not renewed.

### MVP III - Result Preview Edition [Done]

Well, the most pressing issues right now is ranking the result candidates and providing a 
valuable snippet of the search result to the user, so the user can decide whether a search
result is worth a look or not.

That ranking doesn't solve the, problem to find the right search result fast.  

Okay, since we now have some kind of ranking, which I'm currently not too thrilled of, I'll
start providing some teaser/preview for the source code found. The ranking results is nice 
but the disappointment in the expected result quality is bigger when you are forced to blindly 
click on a result. But scanning results in a glimpse may help to better satisfy the users
needs.

### MVP IV - Improved Query Parsing [Done]

The idea here is to implement a QueryParser which is able to parse exact matches and parse 
metadata modifiers, in search queries like:
 
  * +test -"@test"
  * +test -unittest:unittest

Actually this resulted in a rewrite of the whole query parser and search query tokenization.

### MVP V - Preview Performance Edition

The idea here is to optimize the preview calculation with a dynamic top k calculator while calculating 
the preview line scores. Instead of calculating each line score and then calculate the top k scores 
and then filter for the top k scored lines. We drop about 70% of the "low quality" lines for further
processing, while calculating their score. That leads to a lower memory footprint and a lower number 
of final candidate lines to process. That improved the preview calculation performance by at least a
factor of 5.

### MVP VI - Build Metadata Index and Search Metadata [Done]

The idea is to build an inverse index for the document metadata and modify the search strategy
to combine content searches with metadata searches.

### MVP VII - Phrase based searches [Done]

The idea is to allow phrase based searches. It is still not perfect but leads to good enough results. 
Phrases can only be filtered and found on the full document level. That should be another filter and 
ranking mechanism, after calculating the preview or while calculating the preview.

### MVP VIII - Use HFB-Filters (metadata search - proof of concept)

The idea is to replace the loading of the document id lists for the trigrams by loading only the 
shortest list and then apply the HFB filters for each of the trigrams. The HFB Filters require 
less I/O time and very less compute. The speedup for the metadata filtering was about 2 to 6 times. 
(Even though some compute is really wasted right now)

Saving sparse filters should reduce the amount of I/O time required for loading and then keeping HFB 
filters in memory and keeping them on disk (SSD). The HFB filter writer will get a configuration and
act accordingly.


### MVP ? - Use HFB-Filters (content search)

In case of good enough performance we want to transit the content search / candidate filtering as 
well to HFB-Filters.

### MVP ? - Optimize Index Storage Footprint

The index size should be reduced by a factor of two or three.

## Nice to have

* Works completely in memory and indexes are read on startup [Not needed yet, because access to index via SSD is currently fast enough]
* Support more than a single search, single query, single threaded environment.
  * many of the objects involved share some information which are not meant to be stored there, but currently are convenient
  * introduce a query-environment/query-workspace/query workflow, where the query can work on, to not share data with other queries at the same time
* implement a configurable pipeline of search, and search environment, to make search multiclient and multithreaded


## What needs to be done next

This is indeed the question.

## Current URL

http://localhost:8000/SearchBackend/rest/search/result?q=123

## Use the FuriousIron-Frontend

http://localhost:8000/SearchBackend/frontend/

## License

Usage is provided under the [MIT License](http://opensource.org/licenses/mit-license.php). See LICENSE for the full details.
