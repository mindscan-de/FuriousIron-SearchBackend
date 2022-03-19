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
package de.mindscan.furiousiron.search.query.tokenizer;

import java.util.ArrayList;
import java.util.List;

import de.mindscan.furiousiron.search.query.token.SearchQueryToken;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenFactory;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenType;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenizerTerminals;
import de.mindscan.furiousiron.search.query.tokenizer.lexer.StringBackedLexerImpl;

/**
 * 
 */
public class SearchQueryTokenizerImpl implements SearchQueryTokenizer {

    private boolean ignoreWhitespaces = true;

    public SearchQueryTokenizerImpl() {
    }

    @Override
    public List<SearchQueryToken> parse( String queryString ) {
        ArrayList<SearchQueryToken> result = new ArrayList<>();

        StringBackedLexerImpl lexer = new StringBackedLexerImpl( queryString );

        while (lexer.isTokenStartBeforeInputEnd()) {
            lexer.prepareNextToken();

            SearchQueryTokenType tokenType = consumeSearchQueryToken( lexer );

            if (isValidTokenType( tokenType )) {
                result.add( createToken( tokenType, lexer ) );
            }
            else {
                if (tokenType == null) {
                    String tokenString = lexer.getTokenString();
                    System.out.println( "Couldn't process this tokenstring: '" + String.valueOf( tokenString ) + "'" );
                }
            }

            lexer.advanceToNextToken();
        }

        return result;
    }

    private boolean isValidTokenType( SearchQueryTokenType tokenType ) {
        if (tokenType == null) {
            return false;
        }

        if (tokenType == SearchQueryTokenType.NONE) {
            return false;
        }

        return true;
    }

    private SearchQueryToken createToken( SearchQueryTokenType tokenType, StringBackedLexerImpl lexer ) {
        String valueString = lexer.getTokenString();

        if (tokenType == SearchQueryTokenType.EXACTSEARCHTERM) {
            char startsWith = valueString.charAt( 0 );
            char endsWith = valueString.charAt( valueString.length() - 1 );

            if (startsWith == endsWith) {
                // Strip beginning and end.
                valueString = valueString.substring( 1, valueString.length() - 1 );
            }

            return SearchQueryTokenFactory.createToken( tokenType, valueString );
        }

        // create the token....
        return SearchQueryTokenFactory.createToken( tokenType, valueString );
    }

    private SearchQueryTokenType consumeSearchQueryToken( StringBackedLexerImpl lexer ) {
        char charAtTokenStart = lexer.charAtTokenStart();

        if (SearchQueryTokenizerTerminals.isWhiteSpace( charAtTokenStart )) {
            return consumeWhiteSpace( lexer );
        }
        else if (SearchQueryTokenizerTerminals.isParenthesis( charAtTokenStart )) {
            return SearchQueryTokenType.PARENTHESIS;
        }
        else if (SearchQueryTokenizerTerminals.isStartOfOperator( charAtTokenStart )) {
            return consumeOperator( lexer );
        }
        else if (SearchQueryTokenizerTerminals.isStartOfQuote( charAtTokenStart )) {
            return consumeQuotedSearchTerm( lexer );
        }
        else {
            // numbers or aphanumeric stuff and 
            return consumeSearchTerm( lexer );
        }

        // return null;
    }

    private SearchQueryTokenType consumeWhiteSpace( StringBackedLexerImpl lexer ) {
        lexer.incrementTokenEndWhile( SearchQueryTokenizerTerminals::isWhiteSpace );

        return ignoreWhitespaces ? SearchQueryTokenType.NONE : SearchQueryTokenType.WHITESPACE;
    }

    private SearchQueryTokenType consumeOperator( StringBackedLexerImpl lexer ) {
        String operatorCandidate = lexer.getTokenString();

        if (SearchQueryTokenizerTerminals.isOneCharOperator( operatorCandidate )) {
            return SearchQueryTokenType.OPERATOR;
        }

        return SearchQueryTokenType.NONE;
    }

    private SearchQueryTokenType consumeQuotedSearchTerm( StringBackedLexerImpl lexer ) {
        char firstChar = lexer.charAtTokenStart();

        if (SearchQueryTokenizerTerminals.isStartOfQuote( firstChar )) {
            lexer.incrementTokenEndWhileNot( c -> c == firstChar );
        }

        if (!lexer.isTokenEndBeforeInputEnd()) {
            return SearchQueryTokenType.EXACTSEARCHTERM;
        }

        lexer.incrementTokenEnd();

        return SearchQueryTokenType.EXACTSEARCHTERM;
    }

    private SearchQueryTokenType consumeSearchTerm( StringBackedLexerImpl lexer ) {
        lexer.incrementTokenEndWhileNot( SearchQueryTokenizerTerminals::isFollowMengeToTerm );

        return SearchQueryTokenType.SEARCHTERM;
    }

}
