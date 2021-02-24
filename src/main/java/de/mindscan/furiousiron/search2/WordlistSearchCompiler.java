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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mindscan.furiousiron.indexer.SimpleWordUtils;
import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.Search;

/**
 * 
 */
public class WordlistSearchCompiler {

    public static QueryNode compile( QueryNode ast, Search search ) {
        if (ast == null) {
            return new EmptyNode();
        }

        if (ast instanceof EmptyNode) {
            return new EmptyNode();
        }

        if (ast instanceof TextNode) {
            return new TextNode( ast.getContent() );
        }

        if (ast instanceof AndNode) {
            List<QueryNode> andList = new ArrayList<>();

            // TODO: go through all andNodes in AST
            // create foreach a new Node 
            // foreach new node calculate projectedRelativeOccurence
            // sort by projectedRelativeOccurence from seldom to high probability

            return new AndNode( andList );
        }

        if (ast instanceof OrNode) {
            List<QueryNode> orList = new ArrayList<>();

            return new OrNode( orList );
        }

        if (ast instanceof ExcludingNode) {
            // return new ExcludingNode(  );
        }

        if (ast instanceof IncludingNode) {
            // return new IncludingNode( );
        }

        return null;
    }

    private static float calculateProjectedWordOccurrence( QueryNode ast, Search search ) {
        if (ast == null) {
            return 1000000.0f;
        }

        if (ast instanceof EmptyNode) {
            return 1000000.0f;
        }

        if (ast instanceof TextNode) {
            try {
                Collection<String> wordTrigrams = SimpleWordUtils.getUniqueTrigramsFromWord( ast.getContent() );
                return search.getTrigramOccurrencesSortedByOccurrence( wordTrigrams ).get( 0 ).getOccurenceCount();
            }
            catch (IndexOutOfBoundsException e) {
                return 1000000f;
            }
        }

        return 1000000.0f;
    }

}
