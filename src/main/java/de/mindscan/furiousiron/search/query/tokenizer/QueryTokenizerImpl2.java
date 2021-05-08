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
package de.mindscan.furiousiron.search.query.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a newer implementation of the Tokenizer, which should also tokenize
 * exact matches and phrases.
 */
public class QueryTokenizerImpl2 implements QueryTokenizer {

    private final static int DEFAULT_MODE = 0;
    private final static int INQUOTE_MODE = 1;

    /**
     * 
     */
    public QueryTokenizerImpl2() {
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public List<QueryToken> parse( String queryString ) {

        ArrayList<QueryToken> result = new ArrayList<>();

        char[] charsAsArray = queryString.toCharArray();

        int mode = DEFAULT_MODE;

        QueryToken currentToken = null;
        for (int index = 0; index < charsAsArray.length; index++) {
            char currentChar = charsAsArray[index];

            if (isDoubleQuote( currentChar )) {
                if (mode == DEFAULT_MODE) {
                    currentToken = new ExactTextQueryToken();
                    mode = INQUOTE_MODE;
                }
                else if (mode == INQUOTE_MODE) {
                    processResult( result, currentToken );
                    currentToken = null;
                    mode = DEFAULT_MODE;
                }
                continue;
            }

            if (mode == INQUOTE_MODE) {
                if (currentToken != null) {
                    currentToken.addChar( currentChar );
                }
                continue;
            }

            // otherwise do the old stuff...
            if (isWhiteSpace( currentChar )) {
                processResult( result, currentToken );
                currentToken = null;
            }
            else if (isPlus( currentChar )) {
                // complete previous Token
                processResult( result, currentToken );
                currentToken = null;
                // add a PlusToken
                processResult( result, new PlusQueryToken() );
            }
            else if (isMinus( currentChar )) {
                // complete previous Token
                processResult( result, currentToken );
                currentToken = null;
                // add a MinusToken
                processResult( result, new MinusQueryToken() );
            }
            else if (Character.isLetterOrDigit( currentChar ) || Character.isIdeographic( currentChar )) {
                if (currentToken == null) {
                    currentToken = new TextQueryToken();
                }

                // add currentChar to the currentToken
                currentToken.addChar( currentChar );
            }

        }

        processResult( result, currentToken );
        currentToken = null;

        return result;
    }

    private boolean isDoubleQuote( char currentChar ) {
        return currentChar == '"';
    }

    private boolean isMinus( char currentChar ) {
        return currentChar == '-';
    }

    private boolean isPlus( char currentChar ) {
        return currentChar == '+';
    }

    private boolean isWhiteSpace( char currentChar ) {
        return Character.isWhitespace( currentChar );
    }

    private void processResult( List<QueryToken> result, QueryToken currentToken ) {
        if (currentToken == null) {
            return;
        }

        result.add( currentToken );
    }

}
