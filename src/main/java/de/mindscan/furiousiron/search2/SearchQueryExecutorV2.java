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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.core.CoreSearchCompiler;
import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.preview.WordPreview;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.rank.TtfIdfRanking;
import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.search.SearchResultCandidates;
import de.mindscan.furiousiron.search.query.parser.QueryParserV3;
import de.mindscan.furiousiron.util.StopWatch;
import de.mindscan.furiousiron.wordlists.WordlistCompilerFactory;
import de.mindscan.furiousiron.wordlists.wordorder.TrigramPenaltyStrategy;

/**
 * This is a different implementation using a different search strategy.
 * 
 * The Problem is the Collected TextTokens, is not yet part of the 
 * new QueryParserV3. Idea is to replace the original QueryParser.
 */
public class SearchQueryExecutorV2 {

    private List<String> collectedTextTokens;

    public Collection<SearchResultCandidates> search( Search search, String query ) {
        QueryCache queryCache = new QueryCache( search.getSearchQueryCache() );
        QueryNode ast = this.compileSearchTreeFromQuery( query );
        Map<String, Map<Integer, String>> resultPreviews = null;

        List<String> queryDocumentIds;

        // if cached result exist:  just do the ranking and data-presentation.
        if (queryCache.hasCachedSearchResult( ast )) {
            queryDocumentIds = queryCache.loadSearchResult( ast );

            resultPreviews = queryCache.loadSearchResultPreview( ast );
        }
        else {
            // Evaluate the AST for metadata modifiers
            boolean hasMetaDataTextNode = CoreSearchCompiler.hasMetaDataTextNode( ast );

            // Compile the AST
            StopWatch compileCoreSearchASTStopWatch = StopWatch.createStarted();
            CoreQueryNode coreSearchAST = CoreSearchCompiler.compile( ast );
            compileCoreSearchASTStopWatch.stop();

            // get the trigrams from AST
            Collection<String> theTrigrams = coreSearchAST.getTrigrams();
            // get the metadata trigrams from AST 
            Collection<String> theMetadataTrigrams = coreSearchAST.getMetadataTrigrams();

            // result is DocumentIDs candidate list
            // ----------------------------------------------------------------------
            // We have some coreCandidates now, but some of the document may 
            // still contain false positive documents but no false negatives. 
            // But for performance reasons maybe it was too expensive speaking 
            // performance-wise, to cut them further down.
            // ----------------------------------------------------------------------

            // search - Step 3.1 ContentSearch
            StopWatch searchTrigramStopWatch = StopWatch.createStarted();
            Set<String> coreContentCandidatesDocumentIDs = search.collectDocumentIdsForTrigramsOpt( theTrigrams );
            searchTrigramStopWatch.stop();

            // search - Step 3.2 MetadataSearch
            StopWatch searchMetadataTrigramStopWatch = StopWatch.createStarted();
            if (!theMetadataTrigrams.isEmpty()) {
                Set<String> coreMetadataCandidatesCodumentIDs = search.collectDocumentIdsForMetadataTrigramsOptV2( theMetadataTrigrams );

                // combine the documents here in case the metadata trigrams are available  
                System.out.println( "Reduction starts from: " + coreContentCandidatesDocumentIDs.size() + " with " + coreMetadataCandidatesCodumentIDs.size()
                                + " metadata candidates" );
                coreContentCandidatesDocumentIDs.retainAll( coreMetadataCandidatesCodumentIDs );
                System.out.println( "Reduction to: " + coreContentCandidatesDocumentIDs.size() + " elements using metadata filtering." );
            }
            searchMetadataTrigramStopWatch.stop();

            // TODO: if we have exact matches, we might be interested in the trigrams of the candidates,
            //       and check whether all other trigrams are fulfilled, in case we have exact matches, which 
            //       contain spaces E.g. strings of log messages, then we might change the strategy a bit.
            //       if it is an exact match of a word, we just can use the filterW step.
            //       To match a phrase exactly should be an optional step supported again by a trigram search 

            // orderW - Step
            // Calculate Word order an use this as an input for the ast compiler step, instead of implementing different Compiler different "strategies"
            // better to just determine the final word order and use a compiler bringing them in that order.
            StopWatch optimizeWordOrderStopWatch = StopWatch.createStarted();
            TrigramPenaltyStrategy penaltyStrategy = new TrigramPenaltyStrategy();
            Collection<String> orderedWordlist = penaltyStrategy.buildWordlist( getCollectedTextTokens(), search.getSearchDetails().getTrigramUsage() );
            optimizeWordOrderStopWatch.stop();

            // filterW - Step
            StopWatch filterWordsStopWatch = StopWatch.createStarted();
            if (!hasMetaDataTextNode) {
                // in case we have no metadata search, we should avoid reading metadata (while filtering) from disk 
                queryDocumentIds = filterWordsByGenericWordOrder( search, ast, coreContentCandidatesDocumentIDs, orderedWordlist );
            }
            else {
                // in case we have metadata search in query, we actually must read metadata (while filtering) from disk and therefore 
                // use a more expensive filter method (on already fewer elements)
                queryDocumentIds = filterWordsAndMetadatByGenericWordOrder( search, ast, coreContentCandidatesDocumentIDs, orderedWordlist );
            }
            filterWordsStopWatch.stop();

            // rank - Step (1st rank) 
            StopWatch rankDocumentsStopWatch = StopWatch.createStarted();
            queryDocumentIds = ttfidfRank( search, queryDocumentIds );
            rankDocumentsStopWatch.stop();

            // IDEA?
            // -----
            // TODO: lexical search and look at each "document"
            // filter documents by wordlists and return a list of documents and their state, 
            // how many rules they fulfill, according to the wordlist and the semanticSearchAST

            // preview - calculcation 
            StopWatch calculatePreviewStopWatch = StopWatch.createStarted();
            WordPreview wordPreview = new WordPreview( ast, theTrigrams );
            resultPreviews = wordPreview.getBestPreviews( search, queryDocumentIds, 0 );
            calculatePreviewStopWatch.stop();

            // Thought:
            // save this Queryresult (we can always improve the order later), when someone spends some again time for searching for it.
            // we can even let the user decide, which result was better... and use that as well for ordering next time.

            // save retained results for future queries.
            StopWatch cacheSearchResult = StopWatch.createStarted();
            queryCache.cacheSearchQuery( ast );
            queryCache.cacheSearchResult( ast, queryDocumentIds );
            queryCache.cacheSearchResultPreview( ast, resultPreviews );
            cacheSearchResult.stop();

            // Build log message
            StringBuilder sb = new StringBuilder();
            sb.append( "compile/search3/orderW/filterW/rank/preview/cache : " );
            sb.append( compileCoreSearchASTStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( searchTrigramStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( optimizeWordOrderStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( filterWordsStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( rankDocumentsStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( calculatePreviewStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( cacheSearchResult.getElapsedTime() ).append( "ms" );

            System.out.println( sb.toString() );
            System.out.println( "size: 3-gram: " + queryDocumentIds.size() );
        }

        // TODO: predict the order of this document list according to the query.
        // This would be a nice candidate for machine learning - to try
        // How can this be achieved? We might have an document vector and a query vector and then we train
        // a model, to predict the position in the result set.

        // Now rank (improve the ranking of) the results 
        List<String> ranked = queryDocumentIds;

        // now how near are the tokens, how many of them are in there
        // take the top 20 documents and do a "simpleSearch" on them, and try to present the user a
        // each time the user uses pagination only some of the results are searched in the real way.

        // We might train the to predict the score of a file vector according to the search vector using
        // transformers ... But this is way too sophisticated. and requires lots of training

        // TODO: Maybe save the whole search result

        if (resultPreviews == null) {
            return ranked.stream().map( documentId -> convertToSearchResultCandidate( search, documentId ) ).collect( Collectors.toList() );
        }

        // ATTN: don't like it but let's leave it like this until it works.
        // This is currently a proof of concept.

        final Map<String, Map<Integer, String>> previewContent = resultPreviews;
        return ranked.stream().map( documentId -> convertToSearchResultCandidate( search, documentId, previewContent ) ).collect( Collectors.toList() );
    }

    private List<String> filterWordsByGenericWordOrder( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs,
                    Collection<String> orderedWordlist ) {
        QueryNode wordlistSearchAST = WordlistCompilerFactory.createOrderedWordlistCompiler( orderedWordlist ).compile( ast, search );

        return filterByDocumentWordlistsOnly( search, wordlistSearchAST, coreCandidatesDocumentIDs );
    }

    private List<String> filterWordsAndMetadatByGenericWordOrder( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs,
                    Collection<String> orderedWordlist ) {
        QueryNode wordlistSearchAST = WordlistCompilerFactory.createOrderedWordlistCompiler( orderedWordlist ).compile( ast, search );

        return filterByDocumentWordlistsAndMetadata( search, wordlistSearchAST, coreCandidatesDocumentIDs );
    }

    private List<String> ttfidfRank( Search search, List<String> queryDocumentIds ) {
        TtfIdfRanking ttfIdfRanking = new TtfIdfRanking();

        return ttfIdfRanking.rank( search, search.getSearchDetails().getLastQueryTrigramOccurences(), queryDocumentIds );
    }

    private SearchResultCandidates convertToSearchResultCandidate( Search search, String documentId ) {
        SearchResultCandidates result = new SearchResultCandidates( documentId );
        result.loadFrom( search.getMetaDataCache(), search.getWordlistCache() );

        return result;
    }

    private SearchResultCandidates convertToSearchResultCandidate( Search search, String documentId, Map<String, Map<Integer, String>> documentPreviews ) {
        SearchResultCandidates result = new SearchResultCandidates( documentId );
        result.loadFrom( search.getMetaDataCache(), search.getWordlistCache() );
        result.setPreview( documentPreviews.get( documentId ) );

        return result;
    }

    public QueryNode compileSearchTreeFromQuery( String query ) {
        QueryParserV3 queryParser = new QueryParserV3();
        QueryNode parsedAST = queryParser.parseQuery( query );

        setCollectedTextTokens( parsedAST );

        return parsedAST;
    }

    private List<String> filterByDocumentWordlistsOnly( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs ) {
        return coreCandidatesDocumentIDs.stream()
                        .filter( documentId -> AstBasedWordlistFilter.isAstMatchingToWordlist( ast, search.getDocumentWordlist( documentId ) ) )
                        .collect( Collectors.toList() );
    }

    private List<String> filterByDocumentWordlistsAndMetadata( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs ) {
        return coreCandidatesDocumentIDs
                        .stream().filter( documentId -> AstBasedWordlistFilter.isAstMatchingToWordlistAndMetadata( ast,
                                        search.getDocumentWordlist( documentId ), search.getDocumentMetadataMap( documentId ) ) )
                        .collect( Collectors.toList() );
    }

    private void setCollectedTextTokens( QueryNode queryAST ) {
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();

        Collection<String> collected = collector.collectAllTextTokens( queryAST );
        setCollectedTextTokens( List.copyOf( collected ) );
    }

    private void setCollectedTextTokens( List<String> collectedTextTokens ) {
        this.collectedTextTokens = collectedTextTokens;
    }

    private List<String> getCollectedTextTokens() {
        return collectedTextTokens;
    }
}
