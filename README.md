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

## MVP

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

## MVP II - Performance Edition

Some people made a remark, that the search takes quite a long time (e.g. over 30 seconds) using 
the first and naive search strategy. So I had to re-think how to approach the search. Therefore I 
decided to split the search into a core search and an abstract search. The abstract search is 
translated into a core search, which then itself can optimized for very fast candidate drop out 
right from the beginning for very low costs. It is good enough to skip between 30 to 95 of all 
inverse references to be processed, therefore saving lots of I/O. If the candidates are less 
than about three percent of the candidates, it is compared in the next trigram-filtering step, 
we just skip these remaining calculations at all. Not to compute useless things is the biggest 
speedup.

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

## MVP III - Result Preview Edition

Well, the most pressing issues right now are ranking the result candidates and providing a 
valuable snippet of the result to the coder, so the coder can decide whether a search result 
is worth a look or not.

Okay, since we now have some kind of ranking, which I'm currently not too thrilled of, I'll
start providing some teaser for the source code found. Because ranking results is nice but
the disappointment in the expected result quality might be bigger if you are forced to blindly 
click on a result. But scanning results in a glimpse may help to better satisfy the programmer's
needs.

## Nice to have

* Query Parsing: Exact Searches and exact matches
  * implement something +test -"@test"
* Works completely in memory and indexes are read on startup [Not needed yet, because access to index via SSD is currently fast enough]
* Support more than a single search, single query, single threaded environment.
  * many of the objects involved share some information which are not meant to be stored there, but currently are convinient
  * introduce a query-environment/query-workspace/query workflow, where the query can work on, to not share data with other queries at the same time

## What needs to be done next

This is indeed the question.

## Current URL

http://localhost:8000/SearchBackend/rest/search/result?q=123

## Use the FuriousIron-Frontend

http://localhost:8000/SearchBackend/frontend/

## License

Usage is provided under the [MIT License](http://opensource.org/licenses/mit-license.php). See LICENSE for the full details.
