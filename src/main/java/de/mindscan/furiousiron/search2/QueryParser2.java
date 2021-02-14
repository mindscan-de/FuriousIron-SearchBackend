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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.search.query.ast.AndNode;
import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.ExcludingNode;
import de.mindscan.furiousiron.search.query.ast.IncludingNode;
import de.mindscan.furiousiron.search.query.ast.OrNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.ast.TextNode;
import de.mindscan.furiousiron.search.query.parser.QueryParser;
import de.mindscan.furiousiron.search2.corequery.ast.CoreQueryNode;
import de.mindscan.furiousiron.search2.corequery.ast.EmptyCoreNode;
import de.mindscan.furiousiron.search2.corequery.ast.TrigramsCoreNode;

/**
 * 
 */
public class QueryParser2 {

    public void search( Search search, String query ) {
        QueryCache queryCache = new QueryCache();
        QueryNode ast = this.compileSearchTreeFromQuery( query );

        List<String> queryDocumentIds;

        // if cached result exist:  just do the ranking and data-presentation.
        if (queryCache.hasCachedSearchResult( ast )) {
            queryDocumentIds = queryCache.loadSearchResult( ast );
        }
        else {
            CoreQueryNode coreSearchAST = this.compileCoreSearch( ast );

            Collection<String> theTrigrams = coreSearchAST.getTrigrams();

            // coreCandidates = coreSearchAST.searchCoreCandidates();
            // result is DocumentIDs candidate list
            Set<String> coreCandidatesDocumentIDs = search.collectDocumentIdsForTrigramsOpt( theTrigrams );

            // ----------------------------------------------------------------------
            // We have some coreCandidates now, but some of the document may still 
            // miss trigrams which might still not be filtered out, but it was too
            // expensive to look through large document'id lists, we might need an 
            // indicator, whether we have used a shortcut
            // ----------------------------------------------------------------------

            /* semanticSearchAST = */ this.compileLexicalSearch( ast );

            // TODO: semanticSearchAST.filterToResults(coreCandidatesDocumentIDs);

            // TODO: lexical search and look at each "document"
            // filter documents by wordlists and return a list of documents and their state, 
            // how many rules they fulfill, according to the wordlist and the semanticSearchAST

            // we may can do this by using bloom filters and weights at the filter level

            queryDocumentIds = filterByDocumentWordlists( search, ast, coreCandidatesDocumentIDs );

            // save this Queryresult (we can always improve the order later), when someone spends some again time for searching for it.
            // we can even let the user decide, which result was better... and use that as well for ordering next time.

            // save retained results for future queries.
            queryCache.cacheSearchResult( ast, queryDocumentIds );
        }

        // TODO: predict the order of this documentlist according to the query.

        // Now rank the results 
        List<String> ranked = queryDocumentIds;

        // now how near are the tokens, how many of them are in there
        // take the top 20 documents and do a "simpleSearch" on them, and try to present the user a
        // each time the user uses pagination only some of the results are searched in the real way.

        // We might train the to predict the score of a file vector according to the search vector using
        // transformers ... But this is way too sophisticated. and requires lots of training
    }

    public QueryNode compileSearchTreeFromQuery( String query ) {
        QueryParser queryParser = new QueryParser();
        QueryNode parsedAST = queryParser.parseQuery( query );

        return parsedAST;
    }

    public CoreQueryNode compileCoreSearch( QueryNode ast ) {
        if (ast == null) {
            return new EmptyCoreNode();
        }

        if (ast instanceof EmptyNode) {
            return new EmptyCoreNode();
        }

        // Compile parsedAST into a technical AST
        if (ast instanceof TextNode) {
            return new TrigramsCoreNode( ast.getContent().toLowerCase() );
        }

        if (ast instanceof AndNode) {
            Set<String> includedwords = new HashSet<String>();

            // collect each word
            for (QueryNode queryNode : ast.getChildren()) {
                CoreQueryNode t = this.compileCoreSearch( queryNode );
                includedwords.addAll( t.getTrigrams() );
            }
            List<String> l = includedwords.stream().collect( Collectors.toList() );
            return new TrigramsCoreNode( l );
        }

        if (ast instanceof OrNode) {
            // We really don't support Or nodes right now.
            throw new RuntimeException( "Or Optimization is not implemented yet" );
        }

        if (ast instanceof ExcludingNode) {
            // We don't support excluding on this level right now.
            return new EmptyCoreNode();
        }

        if (ast instanceof IncludingNode) {
            for (QueryNode queryNode : ast.getChildren()) {
                return this.compileCoreSearch( queryNode );
            }
            return new EmptyCoreNode();
        }

        return null;
    }

    public void compileLexicalSearch( QueryNode ast ) {
        // compile parsedAST into a semantic search description
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
            String wordToSearch = ast.getContent();

            // if it is directly contained
            if (documentWordlist.contains( wordToSearch )) {
                return true;
            }

            int wordToSearchLength = wordToSearch.length();

            for (String documentWord : documentWordlist) {
                if (documentWord.length() > wordToSearchLength) {
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
