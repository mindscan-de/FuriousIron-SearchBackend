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
package de.mindscan.furiousiron.search.query.tokenizer.lexer;

import java.util.function.Predicate;

/**
 * 
 */
public class StringBackedLexerImpl {

    private int tokenStart;
    private int tokenEnd;
    private String inputString;

    public StringBackedLexerImpl() {
    }

    public StringBackedLexerImpl( String inputString ) {
        this.inputString = inputString;
        resetTokenPositions();
    }

    public void resetTokenPositions() {
        tokenStart = 0;
        tokenEnd = 0;
    }

    public void setInputString( String inputString ) {
        this.inputString = inputString;
    }

    public char charAtTokenStart() {
        return inputString.charAt( tokenStart );
    }

    public char charAtTokenEnd() {
        return inputString.charAt( tokenEnd );
    }

    public boolean isTokenStartBeforeInputEnd() {
        return tokenStart < inputString.length();
    }

    public boolean isTokenEndBeforeInputEnd() {
        return tokenEnd < inputString.length();
    }

    public void advanceToNextToken() {
        tokenStart = tokenEnd;
    }

    public void incrementTokenEnd() {
        tokenEnd++;
    }

    public void prepareNextToken() {
        tokenEnd = tokenStart + 1;
    }

    public String getTokenString() {
        return inputString.substring( tokenStart, tokenEnd );
    }

    public int getTokenStart() {
        return tokenStart;
    }

    public int getTokenEnd() {
        return tokenEnd;
    }

    public void incrementTokenEndWhile( Predicate<Character> object ) {
        while (isTokenEndBeforeInputEnd() && object.test( charAtTokenEnd() )) {
            incrementTokenEnd();
        }
    }

    public void incrementTokenEndWhileNot( Predicate<Character> object ) {
        while (isTokenEndBeforeInputEnd() && !object.test( charAtTokenEnd() )) {
            incrementTokenEnd();
        }
    }

}
