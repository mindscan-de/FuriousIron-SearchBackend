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
package de.mindscan.furiousiron.search.query.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class QueryTokenizer implements Tokenizer {

    @Override
    public List<QueryToken> tokenize( String queryString ) {
        return parse( queryString );
    }

    QueryTokenizer() {
        // do not create instances of tooling class
    }

    @Override
    public List<QueryToken> parse( String queryString ) {
        ArrayList<QueryToken> result = new ArrayList<>();

        char[] charsAsArray = queryString.toCharArray();

        QueryToken currentToken = null;

        for (int index = 0; index < charsAsArray.length; index++) {
            char currentChar = charsAsArray[index];

            // if in exact text mode and not quote
            // - just add char to currentToken and proceed with next char
            // if in exact text mode and is quote
            // - end current token and save proceed with next char
            // if not in exact text mode and is quote
            // - set new current token and proceed with next char
            // else

            if (isWhiteSpace( currentChar )) {
                processResult( result, currentToken );
                currentToken = null;
                continue;
            }

            if (isPlus( currentChar )) {
                // complete previous Token
                processResult( result, currentToken );
                currentToken = null;
                // add a PlusToken
                processResult( result, new PlusQueryToken() );
                continue;
            }

            if (isMinus( currentChar )) {
                // complete previous Token
                processResult( result, currentToken );
                currentToken = null;
                // add a MinusToken
                processResult( result, new MinusQueryToken() );
            }

            if (Character.isLetterOrDigit( currentChar ) || Character.isIdeographic( currentChar )) {
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

    private boolean isMinus( char currentChar ) {
        return currentChar == '-';
    }

    private boolean isPlus( char currentChar ) {
        return currentChar == '+';
    }

    private boolean isWhiteSpace( char currentChar ) {
        return Character.isWhitespace( currentChar );
    }

    private void processResult( ArrayList<QueryToken> result, QueryToken currentToken ) {
        if (currentToken == null) {
            return;
        }

        result.add( currentToken );
    }

}
