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
package de.mindscan.furiousiron.preview;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.search.Search;

/**
 * 
 */
public class WordPreview {

    private QueryNode ast;
    private Collection<String> theTrigrams;

    public WordPreview( QueryNode ast, Collection<String> theTrigrams ) {
        this.ast = ast;
        this.theTrigrams = theTrigrams;
    }

    public Map<String, String> getBestPreviews( Search search, List<String> queryDocumentIds, int startIndex ) {

        // just limit the number of detail results to 25
        for (String documentIdMD5 : queryDocumentIds.subList( 0, Math.min( queryDocumentIds.size(), 25 ) )) {
            List<String> allLines = search.getDocumentContentLines( documentIdMD5 );

            // TODO: foreach line calc the trigrams count them while comparing them to "theTrigrams"
            // keep line if more than X trigrams are satisfied.

            // TODO: also calculate the n best for the file 
        }

        return new HashMap<>();
    }

}
