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
package de.mindscan.furiousiron.search.query.parser;

import java.util.ArrayList;
import java.util.List;

import de.mindscan.furiousiron.search.query.ast.AndNode;
import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.ExcludingNode;
import de.mindscan.furiousiron.search.query.ast.IncludingNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.ast.TextNode;
import de.mindscan.furiousiron.search.query.tokenizer.MinusQueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.PlusQueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.QueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.QueryTokenizer;
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
public class QueryParser {

    /**
     * Parses the given query String.
     * @param queryString the query string which needs to be parsed
     * @return the corresponding AST of the query string. 
     */
    public QueryNode parseQuery( String queryString ) {
        if (queryString == null || queryString.isEmpty()) {
            return new EmptyNode();
        }

        return parseQueryTokens( QueryTokenizer.tokenize( queryString ) );
    }

    QueryNode parseQueryTokens( List<QueryToken> tokenizedQuery ) {
        if (tokenizedQuery.isEmpty()) {
            return new EmptyNode();
        }

        // collect subtrees
        List<QueryNode> astCollector = new ArrayList<>();
        for (int currentTokenIndex = 0; currentTokenIndex < tokenizedQuery.size(); currentTokenIndex++) {
            currentTokenIndex = collectNextAST( currentTokenIndex, tokenizedQuery, astCollector );
        }

        // build composite tree / optimize tree
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
        return astCollector.get( 0 );
    }

    private int collectNextAST( int currentTokenIndex, List<QueryToken> tokenizedQuery, List<QueryNode> astCollector ) {
        int lastReadTokenIndex = currentTokenIndex;
        QueryToken currentToken = tokenizedQuery.get( lastReadTokenIndex );

        if (currentToken instanceof TextQueryToken) {
            // this? add TEXT
            // this? add OR/INCLUDING/TEXT 
            astCollector.add( new TextNode( currentToken.getTokenValue() ) );

            return lastReadTokenIndex;
        }
        else if (currentToken instanceof MinusQueryToken) {
            // add AND/EXCLUDING/TEXT
            QueryToken peekNextToken = tokenizedQuery.get( lastReadTokenIndex + 1 );

            if (peekNextToken instanceof TextQueryToken) {
                astCollector.add( new AndNode( new ExcludingNode( new TextNode( peekNextToken.getTokenValue() ) ) ) );
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
                astCollector.add( new AndNode( new IncludingNode( new TextNode( peekNextToken.getTokenValue() ) ) ) );
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

}
