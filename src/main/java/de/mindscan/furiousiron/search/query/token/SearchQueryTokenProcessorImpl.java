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
package de.mindscan.furiousiron.search.query.token;

/**
 * 
 */
public class SearchQueryTokenProcessorImpl implements SearchQueryTokenProcessor {

    private SearchQueryTokenProvider tokenProvider;

    /**
     * 
     */
    public SearchQueryTokenProcessorImpl( SearchQueryTokenProvider provider ) {
        this.tokenProvider = provider;
    }

    @Override
    public boolean tryToken( SearchQueryToken acceptableToken ) {
        if (acceptableToken == null) {
            throw new IllegalArgumentException( "The acceptableToken must not be null." );
        }

        SearchQueryToken la = tokenProvider.lookahead();
        return acceptableToken.equals( la );
    }

    @Override
    public boolean tryAndAcceptToken( SearchQueryToken acceptableToken ) {
        if (acceptableToken == null) {
            throw new IllegalArgumentException( " acceptableToken must not be null " );
        }

        SearchQueryToken la = tokenProvider.lookahead();

        if (!acceptableToken.equals( la )) {
            return false;
        }
        tokenProvider.next();

        return true;
    }

    @Override
    public boolean tryAndAcceptAsString( String acceptableString ) {
        SearchQueryToken la = tokenProvider.lookahead();

        if (!la.getValue().equals( acceptableString )) {
            return false;
        }

        tokenProvider.next();

        return true;
    }

    @Override
    public boolean tryType( SearchQueryTokenType acceptableType ) {
        SearchQueryToken la = tokenProvider.lookahead();

        return la.getType() == acceptableType;
    }

    @Override
    public boolean tryAndAcceptType( SearchQueryTokenType acceptableType ) {
        SearchQueryToken la = tokenProvider.lookahead();

        if (la.getType() != acceptableType) {
            return false;
        }

        tokenProvider.next();

        return true;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return tokenProvider.hasNext();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public SearchQueryToken last() {
        return tokenProvider.last();
    }
}
