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
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.MetaDataTextNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.query.token.SearchQueryToken;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenProvider;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenType;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokens;
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

        setTokenProvider( SearchQueryTokenizerFactory.getTokenizer(), queryString );

        return parseSearchOperators();
    }

    void setTokenProvider( SearchQueryTokenizer tokenizer, String queryString ) {
        this.tokens = new SearchQueryTokenProvider( tokenizer.parse( queryString ) );
    }

    // +
    // -
    // (
    QueryNode parseSearchOperators() {
        if (tryType( SearchQueryTokenType.SEARCHTERM ) || tryType( SearchQueryTokenType.EXACTSEARCHTERM )) {
            return parseSearchTerminalTextTerm();
        }
        if (tryAndAcceptToken( SearchQueryTokens.OPERATOR_PLUS )) {
            throw new RuntimeException( "Not Yet implemented." );
        }
        else if (tryAndAcceptToken( SearchQueryTokens.OPERATOR_MINUS )) {
            throw new RuntimeException( "Not Yet implemented." );
        }
        else {
            throw new RuntimeException( "Not Yet implemented." );
        }
    }

    // Term
    QueryNode parseSearchTerminalTextTerm() {
        if (tryAndAcceptType( SearchQueryTokenType.EXACTSEARCHTERM )) {
            // TODO: if it is an exact Term, then it must not be followed by double colon
            SearchQueryToken term = tokens.last();
            return new ExactMatchingTextNode( term.getValue() );
        }
        else if (tryAndAcceptType( SearchQueryTokenType.SEARCHTERM )) {
            SearchQueryToken term = tokens.last();

            if (tryAndAcceptToken( SearchQueryTokens.OPERATOR_DOUBLECOLON )) {
                SearchQueryToken key = term;
                QueryNode value = parseSearchTerminalTextTerm();

                // TODO: maybe rewrite this MetaDataTextNode-Value with a QueryNode Type.
                return new MetaDataTextNode( key.getValue(), value.getContent() );
            }

            return new TextNode( term.getValue() );
        }

        return null;
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
        if (acceptableToken == null) {
            throw new IllegalArgumentException( " acceptableToken must not be null " );
        }

        SearchQueryToken la = tokens.lookahead();

        if (!acceptableToken.equals( la )) {
            return false;
        }
        tokens.next();

        return true;
    }

    private boolean tryAndConsumeAsString( String acceptableString ) {
        return false;
    }

    private boolean tryType( SearchQueryTokenType acceptableType ) {
        SearchQueryToken la = tokens.lookahead();

        return la.getType() == acceptableType;
    }

    private boolean tryAndAcceptType( SearchQueryTokenType acceptableType ) {
        SearchQueryToken la = tokens.lookahead();

        if (la.getType() != acceptableType) {
            return false;
        }

        tokens.next();

        return true;
    }
}
