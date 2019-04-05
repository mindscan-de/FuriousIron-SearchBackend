# FuriousIron-SearchBackend

FuriousIron-SearchBackend is a simple search engine backend. At the moment this is a completely
private educational project and not fit for any purpose whatsoever.

## The Idea

A Search-Frontend simply needs a Search-Backend, which provides the search frontend with some
kind of content / answers. I could use a ready technology, but since I want to experiment a
little, I decided to implement a SearchBackend on my own.

Neiher the architecture nor the technology is decided yet.

The main goal is to determine what kind of forward and inverse indexes are required to implement
a complete search engine.

## MVP

The FuriousIron-Frontend project needs a simple backend to provide search results (and probably 
the metadata and the content - which might move to a completely different project (Content-Delivery)).

* Retrieve the queries
* Query parsing
* Answer queries with searchresults
* (ranking - might be out of scope yet / might also not be done here but at a different step)
* Content-Delivery
** Provide meta data on search results
** Provide content data on search results
* Works completely in memory and indexes are read on startup
* Since this peoject is considered a proof of concept, i won't implement any type of persistence nor use Databases

Simplicity is key. Because you can change a mimimal product much faster and adapt that to your needs,
as your needs begin to grow. Because I prefer having a whole pipeline working, the goal is to not 
spend too much time on gold plating the code you are going to delete at the next iteration, but instead
having the whole pipeline running as soon as possible, even if it lacks features. 

I also like to defer unneccecary architectural decisions as long they do not need attention. So please
do not expect me to provide a full architecture, or a full grown idea. It will either evolve or die.

That said, please remember this is a private educational project.

## License

Usage is provided under the [MIT License](http://opensource.org/licenses/mit-license.php). See LICENSE for the full details.
