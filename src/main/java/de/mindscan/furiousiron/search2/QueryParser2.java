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
import java.util.Set;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.core.CoreSearchCompiler;
import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.search.SearchResultCandidates;
import de.mindscan.furiousiron.search.query.parser.QueryParser;
import de.mindscan.furiousiron.util.StopWatch;
import de.mindscan.furiousiron.wordlists.WordlistCompilerFactory;

/**
 * 
 */
public class QueryParser2 {

    private List<String> collectedTextTokens;

    public Collection<SearchResultCandidates> search( Search search, String query ) {
        QueryCache queryCache = new QueryCache( search.getSearchQueryCache() );
        QueryNode ast = this.compileSearchTreeFromQuery( query );

        List<String> queryDocumentIds;

        // if cached result exist:  just do the ranking and data-presentation.
        if (queryCache.hasCachedSearchResult( ast )) {
            queryDocumentIds = queryCache.loadSearchResult( ast );
        }
        else {
            // Compile the AST
            StopWatch compileCoreSearchASTStopWatch = StopWatch.createStarted();
            CoreQueryNode coreSearchAST = CoreSearchCompiler.compile( ast );
            compileCoreSearchASTStopWatch.stop();

            // get the trigrams from AST
            Collection<String> theTrigrams = coreSearchAST.getTrigrams();

            // result is DocumentIDs candidate list
            // ----------------------------------------------------------------------
            // We have some coreCandidates now, but some of the document may 
            // still contain false positive documents but no false negatives. 
            // But for performance reasons maybe it was too expensive speaking 
            // performance-wise, to cut them further down.
            // ----------------------------------------------------------------------

            // search3 - Step
            StopWatch searchTrigramStopWatch = StopWatch.createStarted();
            Set<String> coreCandidatesDocumentIDs = search.collectDocumentIdsForTrigramsOpt( theTrigrams );
            searchTrigramStopWatch.stop();

            // TODO: Maybe use these too? search.getSkippedTrigramsInOptSearch()

            // orderW - Step
            // Calculate Word order an use this as an input for the ast compiler step, instead of implementing different Compiler different "strategies"
            // better to just determine the final word order and use a compiler bringing them in that order.
            StopWatch optimizeWordOrderStopWatch = StopWatch.createStarted();
            WordlistTrigramPenaltyCompiler penaltyCompiler = new WordlistTrigramPenaltyCompiler();
            Collection<String> orderedWordlist = penaltyCompiler.getOrderedWordlist( getCollectedTextTokens(), search.getTrigramUsage() );
            optimizeWordOrderStopWatch.stop();

            // filterW - Step
            StopWatch filterWordsStopWatch = StopWatch.createStarted();
            queryDocumentIds = filterWordsByGenericWordOrder( search, ast, coreCandidatesDocumentIDs, orderedWordlist );
            filterWordsStopWatch.stop();

            // rank - Step

            // TODO: lexical search and look at each "document"
            // filter documents by wordlists and return a list of documents and their state, 
            // how many rules they fulfill, according to the wordlist and the semanticSearchAST

            // Thought:
            // save this Queryresult (we can always improve the order later), when someone spends some again time for searching for it.
            // we can even let the user decide, which result was better... and use that as well for ordering next time.

            // save retained results for future queries.
            StopWatch cacheSearchResult = StopWatch.createStarted();
            // queryCache.cacheSearchResult( ast, queryDocumentIds );
            cacheSearchResult.stop();

            // Build log message
            StringBuilder sb = new StringBuilder();
            sb.append( "compile/search3/orderW/filterW/rank/cache : " );
            sb.append( compileCoreSearchASTStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( searchTrigramStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( optimizeWordOrderStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( filterWordsStopWatch.getElapsedTime() ).append( "ms / " );
            sb.append( 0L ).append( "ms / " );
            sb.append( cacheSearchResult.getElapsedTime() ).append( "ms" );

            System.out.println( sb.toString() );
            System.out.println( "size: 3-gram: " + queryDocumentIds.size() );
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

    private List<String> filterWordsByGenericWordOrder( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs,
                    Collection<String> orderedWordlist ) {

        StopWatch wordlistCompileWatch = StopWatch.createStarted();
        WordlistOrderedWordlistCompiler wlc = new WordlistOrderedWordlistCompiler( orderedWordlist );
        QueryNode wordlistSearchAST = wlc.compile( ast, search );
        wordlistCompileWatch.stop();

        StopWatch wordlistWatch = StopWatch.createStarted();
        List<String> queryDocumentIds = filterByDocumentWordlists( search, wordlistSearchAST, coreCandidatesDocumentIDs );
        wordlistWatch.stop();

        // System.out.println( "WordlistGenericOrderAST: compile in: " + wordlistCompileWatch.getElapsedTime() );
        // System.out.println( "WordlistGenericOrderAST: size: " + queryDocumentIds.size() + "  in " + wordlistWatch.getElapsedTime() );
        System.out.println( "WordlistGenericOrderAST: " + wordlistSearchAST.toString() );
        return queryDocumentIds;
    }

    @SuppressWarnings( "unused" )
    // words are ordered by trigram importance
    private List<String> filterWordsForDocumentsByTrigramImportance( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs ) {
        StopWatch wordlistCompileWatch = StopWatch.createStarted();
        QueryNode wordlistSearchAST = WordlistCompilerFactory.createTrigramOccurrenceCompiler().compile( ast, search );
        wordlistCompileWatch.stop();

        StopWatch wordlistWatch = StopWatch.createStarted();
        List<String> queryDocumentIds = filterByDocumentWordlists( search, wordlistSearchAST, coreCandidatesDocumentIDs );
        wordlistWatch.stop();

        // System.out.println( "Wordlist3GramAST: compile in: " + wordlistCompileWatch.getElapsedTime() );
        // System.out.println( "Wordlist3GramAST: size: " + queryDocumentIds.size() + "  in " + wordlistWatch.getElapsedTime() );
        System.out.println( "Wordlist3GramAST: " + wordlistSearchAST.toString() );
        return queryDocumentIds;
    }

    @SuppressWarnings( "unused" )
    // words are ordered by wordlength importance
    private List<String> filterWordsForDocumentsByWordlengthImportance( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs ) {
        StopWatch wordlistCompileWatch = StopWatch.createStarted();
        QueryNode wordlistSearchAST = WordlistCompilerFactory.createWordLengthbasedCompiler().compile( ast, search );
        wordlistCompileWatch.stop();

        // ---

        StopWatch wordlistWatch = StopWatch.createStarted();
        List<String> queryDocumentIds = filterByDocumentWordlists( search, wordlistSearchAST, coreCandidatesDocumentIDs );
        wordlistWatch.stop();

        // System.out.println( "WordlistLengthAST: compile in: " + wordlistCompileWatch.getElapsedTime() );
        // System.out.println( "WordlistLengthAST: size: " + queryDocumentIds.size() + "  in " + wordlistWatch.getElapsedTime() );
        System.out.println( "WordlistLengthAST: " + wordlistSearchAST.toString() );
        return queryDocumentIds;
    }

    @SuppressWarnings( "unused" )
    // words are not ordered by any means, other than the order of word occurrence in the search.
    private List<String> filterWordsForDocumentsByNaturalImportance( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs ) {
        StopWatch wordlistCompileWatch = StopWatch.createStarted();
        QueryNode wordlistSearchAST = WordlistCompilerFactory.createToLowercaseCompiler().compile( ast, search );
        wordlistCompileWatch.stop();

        StopWatch y = StopWatch.createStarted();
        List<String> queryDocumentIds = filterByDocumentWordlists( search, wordlistSearchAST, coreCandidatesDocumentIDs );
        y.stop();

        // System.out.println( "NaturalAST: compile in: " + wordlistCompileWatch.getElapsedTime() );        
        // System.out.println( "NaturalAST: size: " + queryDocumentIds.size() + "  in " + y.getElapsedTime() );
        System.out.println( "NaturalAST: " + ast.toString() );
        return queryDocumentIds;
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

        setCollectedTextTokens( queryParser.getCollectedTextTokens() );

        return parsedAST;
    }

    private List<String> filterByDocumentWordlists( Search search, QueryNode ast, Set<String> coreCandidatesDocumentIDs ) {
        return coreCandidatesDocumentIDs.stream()
                        .filter( documentId -> AstBasedWordlistFilter.isAstMatchingToWordlist( ast, search.getDocumentWordlist( documentId ) ) )
                        .collect( Collectors.toList() );
    }

    private void setCollectedTextTokens( List<String> collectedTextTokens ) {
        this.collectedTextTokens = collectedTextTokens;
    }

    private List<String> getCollectedTextTokens() {
        return collectedTextTokens;
    }
}
