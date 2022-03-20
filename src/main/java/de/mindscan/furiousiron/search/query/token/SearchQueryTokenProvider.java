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

import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public class SearchQueryTokenProvider {

    private List<SearchQueryToken> tokenList;
    private SearchQueryToken defaultToken;
    private SearchQueryToken value;

    private int currentPosition;
    private int markedPosition;

    private final static int MARKED_NONE = Integer.MIN_VALUE;

    public SearchQueryTokenProvider( List<SearchQueryToken> tokens ) {
        this.tokenList = new LinkedList<>( tokens );
        this.currentPosition = 0;
        this.defaultToken = SearchQueryTokens.END_OF_INPUT;
        this.value = null;
        this.markedPosition = MARKED_NONE;
    }

    /**
     * @param defaultToken the defaultToken to set
     */
    public void setDefaultToken( SearchQueryToken defaultToken ) {
        this.defaultToken = defaultToken;
    }

}
