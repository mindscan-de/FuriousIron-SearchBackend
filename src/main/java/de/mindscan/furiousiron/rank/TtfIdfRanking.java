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
package de.mindscan.furiousiron.rank;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.mindscan.furiousiron.index.trigram.TrigramOccurrence;

/**
 * 
 */
public class TtfIdfRanking {

    private final TtfIdfCalculator ttfIdfCalculator;

    public TtfIdfRanking() {
        ttfIdfCalculator = new TtfIdfCalculator();
    }

    public List<String> rank( List<TrigramOccurrence> searchQueryTrigramOccurences, List<String> queryDocumentIds ) {
        TrigramOccurrence max = searchQueryTrigramOccurences.stream().max( Comparator.comparingLong( trigram -> trigram.getOccurrenceCount() ) ).get();

        HashMap<String, Float> scoredDocuments = new HashMap<>( queryDocumentIds.size() );

        for (String documentId : queryDocumentIds) {
            float score = ttfIdfCalculator.calculateForDocument( max, searchQueryTrigramOccurences, documentId );
            scoredDocuments.put( documentId, score );
        }

        queryDocumentIds.sort( Comparator.comparingDouble( documentId -> scoredDocuments.get( documentId ) ).reversed() );
        return queryDocumentIds;
    }

}
