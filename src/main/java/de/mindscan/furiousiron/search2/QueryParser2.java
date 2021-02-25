/**
 * 
 * MIT License
 *
 * Copyright (c) 2021 Maxim Gansert, Mindscan
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package de.mindscan.furiousiron.search2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.core.CoreSearchCompiler;
import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.search.SearchResultCandidates;
import de.mindscan.furiousiron.search.query.parser.QueryParser;
import de.mindscan.furiousiron.util.StopWatch;

/**
 * 
 */
public class QueryParser2 {

    public Collection<SearchResultCandidates> search( Search search, String query ) {
        QueryCache queryCache = new QueryCache( search.getSearchQueryCache() );
        QueryNode ast = this.compileSearchTreeFromQuery( query );

        List<String> queryDocumentIds;

        // if cached result exist:  just do the ranking and data-presentation.
        if (queryCache.hasCachedSearchResult( ast )) {
            queryDocumentIds = queryCache.loadSearchResult( ast );
        }
        else {
            CoreQueryNode coreSearchAST = CoreSearchCompiler.compile( ast );

            Collection<String> theTrigrams = coreSearchAST.getTrigrams();

            // result is DocumentIDs candidate list
            Set<String> coreCandidatesDocumentIDs = search.collectDocumentIdsForTrigramsOpt( theTrigrams );

            // ----------------------------------------------------------------------
            // We have some coreCandidates now, but some of the document may still 
            // miss trigrams which might still not be filtered out, but it was too
            // expensive to look through large document-id lists
            // ----------------------------------------------------------------------

            boolean tooManyCoreCandidatesAndSkippedTrigramsExist = false;
            if (tooManyCoreCandidatesAndSkippedTrigramsExist) {
                // use the skipped Trigrams to decide what strategy to use? In some kind of second chance

                // but runtime might at least be (number of candidates * number of skipped trigrams)
                // with giga bytes of indexed data we skipped processing 97-98% of the data for a reason.

                // Maybe it is still smarter to not do this calculation at all, when the 
                // slope of decline in the number of candidates is not promising at all

                /*List<TrigramOccurence> skippedTrigrams = */ search.getSkippedTrigramsInOptSearch();

                // only if there are too many results, we still want to filter them down, it depends
                // a lot on the cost of processing the next trigrams. We could use bloom filters
                // for large document numbers for one trigram 

                // TODO: if trigram documentid lists are too big for direct filtering, then use bloom 
                //       filters but don't double check the positive findings, whether they are false 
                //       positives, just use the negatives to kick out the elements in the hope that, 
                //       that a later Bloom filter for a different trigram would also kick out the 
                //       documentid.

                // another solution is to look at the trigrams of the documents itself. and compare 
                // them to the skipped ones.

                // ----------------------------------------------------------------------
                // TODO: introduce another extra filtering step here?
                // ----------------------------------------------------------------------
                // use the trigramcontent of the document and check for the remaining but
                // (skipped) trigrams might reduce searchtimes for, the full word search
                // continue with order of occurence
                // we want to have a high rejection rate very fast, for cheap
                // ----------------------------------------------------------------------

                // remove individual documentIds from the coreCandidatesDocumentIDs...
            }

            // ----------------------------------------------------------------------
            // now wordbased search and give an estimate of the quality of the result
            // ----------------------------------------------------------------------

            QueryNode wordlistSearchAST = WordlistSearchCompiler.compile( ast, search );

            // ---

            StopWatch wordlistWatch = StopWatch.createStarted();
            List<String> queryDocumentIds = filterByDocumentWordlists( search, wordlistSearchAST, coreCandidatesDocumentIDs );
            wordlistWatch.stop();

            System.out.println( "WordlistAST: size: " + queryDocumentIds.size() + "  in " + wordlistWatch.getElapsedTime() );
            System.out.println( wordlistSearchAST.toString() );

            // ----
            // TODO:?
            // if we compile the wordlist tree, we waste some time... in 80 percent this strategy is slightly a few milliseconds faster
            // the way to identify the most important word is not yet the best.
            // ----

//            StopWatch y = StopWatch.createStarted();
//            queryDocumentIds = filterByDocumentWordlists( search, ast, coreCandidatesDocumentIDs );
//            y.stop();
//
//            System.out.println( "AST: size: " + queryDocumentIds.size() + "  in " + y.getElapsedTime() );
//            System.out.println( ast.toString() );

            // TODO: lexical search and look at each "document"
            // filter documents by wordlists and return a list of documents and their state, 
            // how many rules they fulfill, according to the wordlist and the semanticSearchAST

            // we may can do this by using bloom filters and weights at the filter level

            // TODO: queryDocumentIds = filterByBloomFilter( search, wordlistSearchAST, coreCandidatesDocumentIDs );

            // save this Queryresult (we can always improve the order later), when someone spends some again time for searching for it.
            // we can even let the user decide, which result was better... and use that as well for ordering next time.

            // save retained results for future queries.
            queryCache.cacheSearchResult( ast, queryDocumentIds );
        }

        // TODO: predict the order of this documentlist according to the query.

        // Now rank (improve the ranking of) the results 
        List<String> ranked = queryDocumentIds;

        // now how near are the tokens, how many of them are in there
        // take the top 20 documents and do a "simpleSearch" on them, and try to present the user a
        // each time the user uses pagination only some of the results are searched in the real way.

        // We might train the to predict the score of a file vector according to the search vector using
        // transformers ... But this is way too sophisticated. and requires lots of training

        // ATTN: don't like it but let's leave it like this until it works.
        // This is currently a proof of concept.
        List<SearchResultCandidates> searchresult = ranked.stream().map( documentId -> convertToSearchResultCandidate( search, documentId ) )
                        .collect( Collectors.toList() );

        return searchresult;
    }

    /**
     * @param documentId
     * @return
     */
    private SearchResultCandidates convertToSearchResultCandidate( Search search, String documentId ) {
        // ATTN: don't like it but let's leave it like this until it works.
        // This is currently a proof of concept.
        SearchResultCandidates result = new SearchResultCandidates( documentId );
        result.loadFrom( search.getMetaDataCache(), search.getWordlistCache() );
        return result;
    }

    public QueryNode compileSearchTreeFromQuery( String query ) {
        QueryParser queryParser = new QueryParser();
        QueryNode parsedAST = queryParser.parseQuery( query );

        return parsedAST;
    }

    public QueryNode compileWordlistSearch( QueryNode ast ) {
        QueryNode optimizedWordsearchAST = ast;
        // compile parsedAST into an efficient wordlist search strategy

        // TODO: create a new copy of the AST and 
        // rearrange the AND and OR Lists according to the predicted relative word occurence
        // that means that we can save lots of cycles if we search fot the most likely or most unlikely word first.

        return optimizedWordsearchAST;
    }

    // TODO: This is not the correct ast, but still good enough for our purpose.
    // TODO: this should be an AST which is optimized for matching speed
    //       Nodes in optimized AST should be sorted : and - from longest to shortest word
    //       Nodes in optimized AST should be sorted : or  - from shortest to longest
    private List<String> filterByDocumentWordlists( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs ) {
        List<String> retained = new LinkedList<String>();

        for (String documentID : coreCandidatesDocumentIDs) {
            if (isAstMatchingToWordlist( ast, search.getDocumentWordlist( documentID ) )) {
                retained.add( documentID );
            }
        }

        return retained;
    }

    // TODO: for performance reasons the longest words should be checked first
    //       shorter words are more likely to be occuring the wordlist
    // TODO: wordlists should be organized by wordsize in a TreeSet
    //       in an andnode, the most unlikely word should be processed first
    //       int an or node, the most likely word should be processed first
    // TODO: This is not the correct Tree, but still good enough for this usecase right now.  
    boolean isAstMatchingToWordlist( QueryNode ast, List<String> documentWordlist ) {

        if (ast instanceof TextNode) {
            // TODO: added wordTo Search toLowerCase, since the documentwordlist currently contains only lowercase words
            // TODO: the ast for word matching should be compiled with to lowercased words.
            String wordToSearch = ast.getContent().toLowerCase();

            // if it is directly contained
            if (documentWordlist.contains( wordToSearch )) {
                // this should yield highest reward
                return true;
            }

            int wordToSearchLength = wordToSearch.length();

            // we might want to split the loop, to prefer start over ends over contains
            // we might want to return relevance instead of boolean
            for (String documentWord : documentWordlist) {
                if (documentWord.length() > wordToSearchLength) {
                    // this should yield a higher Score
//                    if (documentWord.startsWith( wordToSearch )) {
//                        return true;
//                    }
//
//                    // this should yield high Score
//                    if (documentWord.endsWith( wordToSearch )) {
//                        return true;
//                    }

                    // this should yield some reward
                    if (documentWord.contains( wordToSearch )) {
                        return true;
                    }
                }
            }

            // it is neither contained fully nor partially. 
            return false;
        }

        if (ast instanceof AndNode) {
            if (ast.hasChildren()) {
                // for - AND nodes the rarest words must be searched first.
                Collection<QueryNode> children = ast.getChildren();
                for (QueryNode queryNode : children) {
                    // early exit in case of a "false" - no need to check further if word is not found.
                    if (!isAstMatchingToWordlist( queryNode, documentWordlist )) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return true;
            }
        }

        if (ast instanceof OrNode) {
            if (ast.hasChildren()) {
                // for - OR nodes the most likely words must be searched first.
                Collection<QueryNode> children = ast.getChildren();
                for (QueryNode queryNode : children) {
                    // early exit in case of a "true" - no need to check further if other word is also found.
                    if (isAstMatchingToWordlist( queryNode, documentWordlist )) {
                        return true;
                    }
                }
                return false;
            }
            else {
                return false;
            }
        }

        if (ast instanceof IncludingNode) {
            if (ast.hasChildren()) {
                QueryNode first = ast.getChildren().iterator().next();
                return isAstMatchingToWordlist( first, documentWordlist );
            }
            else {
                return true;
            }
        }

        if (ast instanceof ExcludingNode) {
            if (ast.hasChildren()) {
                QueryNode first = ast.getChildren().iterator().next();
                return !isAstMatchingToWordlist( first, documentWordlist );
            }
            else {
                return false;
            }
        }

        throw new RuntimeException( "This Node type is not supported: " + ast.toString() );
    }
}
