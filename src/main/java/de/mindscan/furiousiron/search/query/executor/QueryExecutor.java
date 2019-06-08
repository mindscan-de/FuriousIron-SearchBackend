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
import java.util.Set;
import java.util.TreeSet;

import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.search.SearchResultCandidates;
import de.mindscan.furiousiron.search.query.ast.AndNode;
import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.IncludingNode;
import de.mindscan.furiousiron.search.query.ast.OrNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.ast.TextNode;

/**
 * This class will execute a query AST. 
 */
public class QueryExecutor {

    public static Collection<SearchResultCandidates> execute( Search search, QueryNode parsedAST ) {
        if (parsedAST instanceof EmptyNode) {
            return Collections.emptyList();
        }

        if (parsedAST instanceof TextNode) {
            return processTextNode( search, (TextNode) parsedAST );
        }

        if (parsedAST instanceof OrNode) {
            return processOrNode( search, (OrNode) parsedAST );
        }

        if (parsedAST instanceof AndNode) {
            return processAndNode( search, (AndNode) parsedAST );
        }

        Collection<SearchResultCandidates> resultCandidates = search.search( parsedAST.getContent() );

        return resultCandidates;
    }

    private static Collection<SearchResultCandidates> processAndNode( Search search, AndNode parsedAST ) {
        Set<SearchResultCandidates> andset = new TreeSet<>();

        // maybe a bit tricky.
        // get those with and and with including

        // subtract all with excluding in same level...

        return andset;
    }

    private static Collection<SearchResultCandidates> processOrNode( Search search, OrNode parsedAST ) {
        Set<SearchResultCandidates> orset = new TreeSet<>();

        Collection<QueryNode> children = parsedAST.getChildren();
        for (QueryNode queryNode : children) {
            if (queryNode instanceof IncludingNode) {
                Collection<QueryNode> includingchildren = queryNode.getChildren();
                for (QueryNode queryNode2 : includingchildren) {
                    if (queryNode2 instanceof TextNode) {
                        orset.addAll( processTextNode( search, (TextNode) queryNode2 ) );
                    }
                    if (queryNode2 instanceof OrNode) {
                        orset.addAll( processOrNode( search, (OrNode) queryNode2 ) );
                    }
                    if (queryNode2 instanceof AndNode) {
                        orset.addAll( processAndNode( search, (AndNode) queryNode2 ) );
                    }
                }
            }
            else {
                // TODO: raise concern over Tree...
                // we could maybe calculate the de morgan rules to work on the correct tree... subtree
                // how to calculate or/excluding/text
                // or similar
            }
        }

        return orset;
    }

    private static Collection<SearchResultCandidates> processTextNode( Search search, TextNode parsedAST ) {
        return search.search( parsedAST.getContent() );
    }

}
