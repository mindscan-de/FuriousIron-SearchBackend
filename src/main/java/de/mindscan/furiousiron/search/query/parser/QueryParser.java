/**
 * 
 * MIT License
 *
 * Copyright (c) 2019, 2021 Maxim Gansert, Mindscan
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.query.tokenizer.ExactTextQueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.MinusQueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.PlusQueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.QueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.QueryTokenizerFactory;
import de.mindscan.furiousiron.search.query.tokenizer.TextQueryToken;

/**
 * This class implements a simple Parser for the query string. It will compile a query string into an 
 * query AST representation. The AST will later be used for ranking and for the calculation of the 
 * query strategy.
 * 
 * So this is classic parser stuff:
 * 
 * - Use a lexer/tokenizer to create lexical tokens of the input (in our case a query string)
 * - parse the lexed tokens and build an AST from it
 * - (maybe optimize the AST)
 * - (build multiple graphs from it, first a query graph (which can be executed to calculate the results), and a second for ranking) 
 * 
 * Maybe later will go for a more sophisticated way, like using a parser generator, and write an 
 * EBNF grammar. But for the moment a handwritten parser looks like the way to go. Since i want 
 * later to describe properties of the source code I am searching for, I might need a different 
 * EBNF grammar anyway. 
 */
public class QueryParser implements SearchQueryParser {

    /**
     * Parses the given query String.
     * @param queryString the query string which needs to be parsed
     * @return the corresponding AST of the query string. 
     */
    public QueryNode parseQuery( String queryString ) {
        if (queryString == null || queryString.isEmpty()) {
            return new EmptyNode();
        }

        return parseQueryTokens( QueryTokenizerFactory.getTokenizer().tokenize( queryString ) );
    }

    QueryNode parseQueryTokens( List<QueryToken> tokenizedQuery ) {
        if (tokenizedQuery.isEmpty()) {
            return new EmptyNode();
        }

        List<QueryNode> astCollector = new ArrayList<>();
        for (int currentTokenIndex = 0; currentTokenIndex < tokenizedQuery.size(); currentTokenIndex++) {
            currentTokenIndex = collectNextAST( currentTokenIndex, tokenizedQuery, astCollector );
        }

        return composeFullAST( astCollector );
    }

    private int collectNextAST( int currentTokenIndex, List<QueryToken> tokenizedQuery, List<QueryNode> astCollector ) {
        int lastReadTokenIndex = currentTokenIndex;
        QueryToken currentToken = tokenizedQuery.get( lastReadTokenIndex );

        if (currentToken instanceof TextQueryToken) {
            // this? add TEXT
            // this? add OR/INCLUDING/TEXT
            astCollector.add( consumeTextToken( (TextQueryToken) currentToken ) );

            return lastReadTokenIndex;
        }
        else if (currentToken instanceof MinusQueryToken) {
            // add AND/EXCLUDING/TEXT
            QueryToken peekNextToken = tokenizedQuery.get( lastReadTokenIndex + 1 );

            if (peekNextToken instanceof TextQueryToken) {
                astCollector.add( new AndNode( new ExcludingNode( consumeTextToken( (TextQueryToken) peekNextToken ) ) ) );
                lastReadTokenIndex++;
                return lastReadTokenIndex;
            }
            else {
                // Error AST?
                return lastReadTokenIndex;
            }
        }
        else if (currentToken instanceof PlusQueryToken) {
            // add AND/INCLUDING/TEXT
            QueryToken peekNextToken = tokenizedQuery.get( lastReadTokenIndex + 1 );

            if (peekNextToken instanceof TextQueryToken) {
                astCollector.add( new AndNode( new IncludingNode( consumeTextToken( (TextQueryToken) peekNextToken ) ) ) );
                lastReadTokenIndex++;
                return lastReadTokenIndex;
            }
            else {
                // error AST?
                return lastReadTokenIndex;
            }
        }
        else {
            // something else happens
        }

        return lastReadTokenIndex;
    }

    private QueryNode consumeTextToken( TextQueryToken queryToken ) {
        // do the special case first
        if (queryToken instanceof ExactTextQueryToken) {

            // collectTextNode
            // TODO: ExactTextQueryToken 
            // TODO: check whether this is a word, or whether it is a phrase, both are differently handled in the 
            //       search execution stage.

            return new ExactMatchingTextNode( queryToken.getTokenValue() );
        }

        // do the simple text query tokens second
        return new TextNode( queryToken.getTokenValue() );
    }

    private QueryNode composeFullAST( List<QueryNode> astCollector ) {
        switch (astCollector.size()) {
            case 0:
                return new EmptyNode();
            case 1:
                return astCollector.get( 0 );
            default:
                return buildOptimizedTree( astCollector );
        }
    }

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
