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
package de.mindscan.furiousiron.search.query.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.MetaDataTextNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.QueryNodeListNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.query.token.SearchQueryToken;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenProcessor;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenProcessorFactory;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenType;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokens;

/**
 * 
 */
public class QueryParserV3 implements SearchQueryParser {

    private SearchQueryTokenProcessor tokenProcessor;

    public QueryNode parseQuery( String queryString ) {
        if (queryString == null || queryString.isEmpty()) {
            return new EmptyNode();
        }

        setTokenProcessor( SearchQueryTokenProcessorFactory.create( queryString ) );

        QueryNodeListNode listNode = new QueryNodeListNode();

        while (tokenProcessor.hasNext()) {
            listNode.addNode( parseSearchOperators() );
        }

        return compileASTList( listNode );
    }

    void setTokenProcessor( SearchQueryTokenProcessor tokenProcessor ) {
        this.tokenProcessor = tokenProcessor;
    }

    // +
    // -
    // TODO: '(',')' - parenthesis not yet implemented. (not so easy either.)
    QueryNode parseSearchOperators() {
        if (tokenProcessor.tryType( SearchQueryTokenType.SEARCHTERM ) || tokenProcessor.tryType( SearchQueryTokenType.EXACTSEARCHTERM )) {
            return parseSearchTerminalTextTerm();
        }
        if (tokenProcessor.tryAndAcceptToken( SearchQueryTokens.OPERATOR_PLUS )) {
            QueryNode postPlusAST = parseSearchOperators();
            return new AndNode( new IncludingNode( postPlusAST ) );
        }
        else if (tokenProcessor.tryAndAcceptToken( SearchQueryTokens.OPERATOR_MINUS )) {
            QueryNode postMinusAST = parseSearchOperators();
            return new AndNode( new ExcludingNode( postMinusAST ) );
        }
        else {
            throw new RuntimeException( "Not Yet implemented." );
        }
    }

    // Term
    QueryNode parseSearchTerminalTextTerm() {
        if (tokenProcessor.tryAndAcceptType( SearchQueryTokenType.EXACTSEARCHTERM )) {
            // TODO: if it is an exact Term, then it must not be followed by double colon
            SearchQueryToken term = tokenProcessor.last();
            return new ExactMatchingTextNode( term.getValue() );
        }
        else if (tokenProcessor.tryAndAcceptType( SearchQueryTokenType.SEARCHTERM )) {
            SearchQueryToken term = tokenProcessor.last();

            if (tokenProcessor.tryAndAcceptToken( SearchQueryTokens.OPERATOR_DOUBLECOLON )) {
                SearchQueryToken key = term;
                QueryNode value = parseSearchTerminalTextTerm();

                // TODO: maybe rewrite this MetaDataTextNode-Value with a QueryNode Type.
                return new MetaDataTextNode( key.getValue(), value.getContent() );
            }

            return new TextNode( term.getValue() );
        }

        return null;
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
