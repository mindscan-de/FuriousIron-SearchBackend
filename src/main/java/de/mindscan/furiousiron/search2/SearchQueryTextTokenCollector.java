/**
 * 
 * MIT License
 *
 * Copyright (c) 2022 Maxim Gansert, Mindscan
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
import java.util.function.Function;

import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.MetaDataTextNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;

/**
 * 
 */
public class SearchQueryTextTokenCollector {

    public Collection<String> collectAllTextTokens( QueryNode queryAST ) {
        Collection<String> result = new HashSet<String>();

        if (queryAST == null || queryAST instanceof EmptyNode) {
            return result;
        }

        collectTextTokens( result::add, queryAST );

        return result;
    }

    private void collectTextTokens( Function<String, Boolean> consumer, QueryNode queryAST ) {
        if (queryAST == null || queryAST instanceof EmptyNode) {
            return;
        }

        if (!queryAST.hasChildren()) {
            if (queryAST instanceof TextNode) {
                consumer.apply( queryAST.getContent().toLowerCase() );
            }
            else if (queryAST instanceof ExactMatchingTextNode) {
                consumer.apply( queryAST.getContent().toLowerCase() );
            }
            else if (queryAST instanceof MetaDataTextNode) {
                consumer.apply( queryAST.getContent().toLowerCase() );
            }
            else {
                throw new RuntimeException( "Not yet implemented: " + queryAST.getClass().getSimpleName() );
            }
        }
        else {
            if (queryAST instanceof IncludingNode) {
                collectChildren( consumer, queryAST );
            }
            else if (queryAST instanceof ExcludingNode) {
                collectChildren( consumer, queryAST );
            }
            else if (queryAST instanceof AndNode) {
                collectChildren( consumer, queryAST );
            }
            else if (queryAST instanceof OrNode) {
                collectChildren( consumer, queryAST );
            }
            else {
                throw new RuntimeException( "Not yet implemented: " + queryAST.getClass().getSimpleName() );
            }
        }
    }

    private void collectChildren( Function<String, Boolean> consumer, QueryNode queryAST ) {
        Collection<QueryNode> c = queryAST.getChildren();
        for (QueryNode queryNode : c) {
            collectTextTokens( consumer, queryNode );
        }
    }

}
