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

import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.token.SearchQueryToken;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenProvider;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenType;
import de.mindscan.furiousiron.search.query.tokenizer.SearchQueryTokenizer;
import de.mindscan.furiousiron.search.query.tokenizer.SearchQueryTokenizerFactory;

/**
 * 
 */
public class QueryParserV3 implements SearchQueryParser {

    private SearchQueryTokenProvider tokens;

    public QueryNode parseQuery( String queryString ) {
        if (queryString == null || queryString.isEmpty()) {
            return new EmptyNode();
        }

        // tokenize the search query and prepare tokens for parsing.
        SearchQueryTokenizer tokenizer = SearchQueryTokenizerFactory.getTokenizer();
        this.tokens = new SearchQueryTokenProvider( tokenizer.parse( queryString ) );

        return new EmptyNode();
    }

    // -------------------
    // parser support code
    // -------------------    

    private boolean tryToken( SearchQueryToken acceptableToken ) {
        if (acceptableToken == null) {
            throw new IllegalArgumentException( "The acceptableToken must not be null." );
        }

        return false;
    }

    private boolean tryAndAcceptToken( SearchQueryToken acceptableToken ) {
        return false;
    }

    private boolean tryAndConsumeAsString( String acceptableString ) {
        return false;
    }

    private boolean tryType( SearchQueryTokenType acceptableType ) {
        return false;
    }

    private boolean tryAndAcceptType( String acceptableType ) {
        return false;
    }
}
