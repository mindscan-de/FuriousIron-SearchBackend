/**
 * 
 * MIT License
 *
 * Copyright (c) 2019 Maxim Gansert, Mindscan
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
package de.mindscan.furiousiron.search.query.executor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.MetaDataTextNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.search.SearchResultCandidates;

/**
 * This class will execute a query AST. Attention this is not optimized. Intersect, Minus(Exclude), Union could be implemented 
 * via Skip-Lists, and documentId should be sorted by value, so that they can be processed in linear time.
 * 
 * This seems to be good enough for the moment.
 */
public class QueryExecutor {

    public static Collection<SearchResultCandidates> execute( Search search, QueryNode parsedAST ) {
        // TODO: since this search strategy is the most expensive search right now, we might to consider caching the result especially.
        // QueryCache queryCache = new QueryCache( search.getSearchQueryCache() );

        Collection<SearchResultCandidates> resultCandidates = processNode( search, parsedAST ).values();

        // TODO: save the result.
        // queryCache.cacheSearchResult( parsedAST, queryDocumentIds );

        return resultCandidates;
    }

    private static Map<String, SearchResultCandidates> processNode( Search search, QueryNode node ) {
        if (node instanceof EmptyNode) {
            return Collections.emptyMap();
        }

        if (node instanceof ExactMatchingTextNode) {
            return processExactTextNode( search, (ExactMatchingTextNode) node );
        }

        if (node instanceof TextNode) {
            return processTextNode( search, (TextNode) node );
        }

        if (node instanceof OrNode) {
            return processOrNode( search, (OrNode) node );
        }

        if (node instanceof AndNode) {
            return processAndNode( search, (AndNode) node );
        }

        if (node instanceof MetaDataTextNode) {
            return processMetaDataTextNode( search, (MetaDataTextNode) node );
        }

        throw new RuntimeException( "The Tree is not well formatted." + String.valueOf( node ) );
    }

    private static Map<String, SearchResultCandidates> processAndNode( Search search, AndNode parsedAST ) {
        if (parsedAST.getChildren().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, SearchResultCandidates> andMap = new HashMap<>();
        boolean isFirst = true;
        for (QueryNode node : parsedAST.getChildren()) {
            if (node instanceof IncludingNode) {
                Collection<QueryNode> includeNodeChildren = node.getChildren();
                for (QueryNode queryNode : includeNodeChildren) {
                    Map<String, SearchResultCandidates> nodeResults = processNode( search, queryNode );
                    intersectMaps( andMap, nodeResults, isFirst );
                    isFirst = false;
                }
            }
            else if (node instanceof ExcludingNode) {
                Collection<QueryNode> excludeNodeChildren = node.getChildren();
                for (QueryNode queryNode : excludeNodeChildren) {
                    Map<String, SearchResultCandidates> nodeResults = processNode( search, queryNode );
                    excludeMaps( andMap, nodeResults );
                }
            }
            else {
                // process all other types of nodes as include Node.
                Collection<QueryNode> includeNodeChildren = node.getChildren();
                for (QueryNode queryNode : includeNodeChildren) {
                    Map<String, SearchResultCandidates> nodeResults = processNode( search, queryNode );
                    intersectMaps( andMap, nodeResults, isFirst );
                    isFirst = false;
                }
            }
        }

        return andMap;
    }

    private static Map<String, SearchResultCandidates> processOrNode( Search search, OrNode parsedAST ) {
        if (parsedAST.getChildren().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, SearchResultCandidates> orMap = new HashMap<>();

        for (QueryNode node : parsedAST.getChildren()) {
            if (node instanceof IncludingNode) {
                Collection<QueryNode> includeNodeChildren = node.getChildren();
                for (QueryNode queryNode : includeNodeChildren) {
                    Map<String, SearchResultCandidates> nodeResults = processNode( search, queryNode );
                    unionMaps( orMap, nodeResults );
                }
            }
            else if (node instanceof ExcludingNode) {
                // this is something impossible yet for me...
                // intentionally left blank
            }
            else {
                // treat all others node types as OR
                Collection<QueryNode> includeNodeChildren = node.getChildren();
                for (QueryNode queryNode : includeNodeChildren) {
                    Map<String, SearchResultCandidates> nodeResults = processNode( search, queryNode );
                    unionMaps( orMap, nodeResults );
                }
            }
        }

        return orMap;
    }

    private static Map<String, SearchResultCandidates> processTextNode( Search search, TextNode parsedAST ) {
        return search.searchToMap( parsedAST.getContent() );
    }

    private static Map<String, SearchResultCandidates> processExactTextNode( Search search, ExactMatchingTextNode parsedAST ) {
        return search.searchToMap( parsedAST.getContent() );
    }

    private static Map<String, SearchResultCandidates> processMetaDataTextNode( Search search, MetaDataTextNode parsedAST ) {
        return search.searchMetadataToMap( parsedAST.getContent() );
    }

    private static void unionMaps( Map<String, SearchResultCandidates> orMap, Map<String, SearchResultCandidates> nodeResults ) {
        for (Entry<String, SearchResultCandidates> entry : nodeResults.entrySet()) {
            if (!orMap.containsKey( entry.getKey() )) {
                orMap.put( entry.getKey(), entry.getValue() );
            }
        }
    }

    private static void intersectMaps( Map<String, SearchResultCandidates> andMap, Map<String, SearchResultCandidates> nodeResults, boolean isFirst ) {
        if (!isFirst) {
            // calculate intersection
            Set<String> removeKeys = new HashSet<>();
            for (Entry<String, SearchResultCandidates> andSetEntry : andMap.entrySet()) {
                if (!nodeResults.containsKey( andSetEntry.getKey() )) {
                    removeKeys.add( andSetEntry.getKey() );
                }
            }

            // remove non intersecting keys from andMap
            for (String key : removeKeys) {
                andMap.remove( key );
            }
        }
        else {
            andMap.putAll( nodeResults );
        }
    }

    private static void excludeMaps( Map<String, SearchResultCandidates> andMap, Map<String, SearchResultCandidates> nodeResults ) {
        // calculate intersection
        Set<String> removeKeys = new HashSet<>();
        for (Entry<String, SearchResultCandidates> andSetEntry : andMap.entrySet()) {
            if (nodeResults.containsKey( andSetEntry.getKey() )) {
                removeKeys.add( andSetEntry.getKey() );
            }
        }

        // remove intersecting keys from andMap
        for (String key : removeKeys) {
            andMap.remove( key );
        }
    }

}
