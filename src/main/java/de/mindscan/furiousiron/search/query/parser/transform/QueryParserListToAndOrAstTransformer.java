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
package de.mindscan.furiousiron.search.query.parser.transform;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.query.ASTTransformer;
import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.QueryNodeListNode;
import de.mindscan.furiousiron.query.ast.TextNode;

/**
 * Implementation of an AST-Transformer.
 * 
 * This ASTTransformer implementation will combine / calculate the AND OR tree from the terms, 
 * which do not have a correct order, but an order which the user selected. Therefore we don't
 * want to overly complicate the parser nor we want to solve this complex problem in the parser
 * itself.  
 */
public class QueryParserListToAndOrAstTransformer implements ASTTransformer {

    /** 
     * {@inheritDoc}
     */
    @Override
    public QueryNode transform( QueryNode input ) {
        if (input instanceof QueryNodeListNode) {
            return compileASTList( (QueryNodeListNode) input );
        }

        return input;
    }

    // ----------------------------
    // compile the query from parts
    // ----------------------------    

    private QueryNode compileASTList( QueryNodeListNode listNode ) {
        List<QueryNode> astList = listNode.getChildrenAsList();

        switch (astList.size()) {
            case 0:
                return new EmptyNode();
            case 1:
                return astList.get( 0 );
            default:
                return buildOptimizedTree( astList );
        }
    }

    // -----------------------
    // copied from QueryParser
    // -----------------------

    private QueryNode buildOptimizedTree( List<QueryNode> astCollector ) {
        List<QueryNode> textNodes = astCollector.stream().filter( x -> (x instanceof TextNode) ).collect( Collectors.toList() );
        List<QueryNode> orNodes = astCollector.stream().filter( x -> (x instanceof OrNode) ).collect( Collectors.toList() );
        List<QueryNode> andNodes = astCollector.stream().filter( x -> (x instanceof AndNode) ).collect( Collectors.toList() );

        if (!andNodes.isEmpty()) {
            // And is active must be combined with or and text Nodes inside
            List<QueryNode> resultAndList = new LinkedList<>();

            if (textNodes.size() > 0 || orNodes.size() > 0) {
                if ((textNodes.size() + orNodes.size()) == 1) {
                    resultAndList.add( new IncludingNode( textNodes.get( 0 ) ) );
                }
                else {
                    OrNode orNode = new OrNode( buildOrNode( textNodes, orNodes ) );
                    resultAndList.add( new IncludingNode( orNode ) );
                }

            }

            for (QueryNode andNode : andNodes) {
                resultAndList.addAll( andNode.getChildren() );
            }

            return new AndNode( resultAndList );
        }
        else {
            return new OrNode( buildOrNode( textNodes, orNodes ) );
        }

    }

    private List<QueryNode> buildOrNode( List<QueryNode> textNodes, List<QueryNode> orNodes ) {
        List<QueryNode> resultOrList = new LinkedList<>();
        // No AndNode available only TextNodes or OrNodes avail...
        for (QueryNode textNode : textNodes) {
            resultOrList.add( new IncludingNode( textNode ) );
        }

        for (QueryNode orNode : orNodes) {
            resultOrList.addAll( orNode.getChildren() );
        }
        return resultOrList;
    }

}
