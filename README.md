# FuriousIron-SearchBackend

FuriousIron-SearchBackend is a simple search engine backend. At the moment this is a completely
private educational project and not fit for any purpose whatsoever.

## The Idea

The Search-Frontend (FuriousIron-Frontend) needs a Search-Backend, which provides the frontend 
with some kind of search content/answers. I decided to give Jersey + Tomcat a try to provide a 
RESTful API, since it seems to me being the easiest solution to get the whole pipeline running.
Later adjustments are a planned part of this journey.

The main goal of the SearchBackend is, to make use of the inverse indexes and caches, that are
stored on the disk, in order to implement a RESTful service for a complete search engine. The
main goal is to determine, what kind of forward and inverse indexes are required to build a 
complete code search engine.

Since I don't want to implement a web-server right now, so I picked a technology, which i could
find a simple tutorial for.

A big "Thank you!" goes to [o7planning.org](https://o7planning.org/de/11199/die-anleitung-zum-java-restful-web-services-fur-den-anfanger) for providing a useful tutorial.

What I hope for is, either I will develop a code search engine, or make informed decisions on
how to implement a code search engine using ready stacks, like ElasticSearch. But I also want
to interface some ML components later and I don't want to get stuck on a certain API and its 
behavior.

## MVP

The FuriousIron-Front-end project needs a simple backend to provide search results (and probably 
the metadata and the content - which might move to a completely different project (Content-Delivery)).

* Retrieve the queries [Done]
* Query parsing
* Answer queries with search-results [Done]
* Content-Delivery
  * Provide meta data on search results [Done]
  * Provide content data on search results [Done]
* Since this project is considered a proof of concept, i won't implement any type of sophisticated persistence nor use a Databases [Done]

Simplicity is key. Because you can change a minimal product much faster and adapt that to your needs,
as your needs begin to grow. Because I prefer having a whole pipeline working, the goal is to not 
spend too much time on gold plating the code, which you are going to delete at the next iteration, but 
instead having the whole pipeline running as soon as possible, even if it lacks features. 

I also like to defer unnecessary architectural decisions as long they do not need attention. So please
do not expect me to provide a full architecture, or a full grown idea. It will either evolve or die.

That said, please remember this is a private educational project.

## Nice to have

* Query Parsing: Exact Searches
  * implement something +test -"@test"
* Ranking results (might be out of scope yet / might also not be done here but at a different step)
* Works completely in memory and indexes are read on startup [Not needed yet, because access to index via SSD is currently fast enough]

## Current URL

http://localhost:8080/SearchBackend/rest/search/result?q=123

## License

Usage is provided under the [MIT License](http://opensource.org/licenses/mit-license.php). See LICENSE for the full details.
