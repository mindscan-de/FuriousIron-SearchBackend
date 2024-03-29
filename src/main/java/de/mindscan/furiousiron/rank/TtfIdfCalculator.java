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

import java.util.Collection;
import java.util.Map;

import de.mindscan.furiousiron.index.trigram.TrigramOccurrence;
import de.mindscan.furiousiron.search.Search;

/**
 * 
 */
public class TtfIdfCalculator {

    /*
     * because the document already passed the word filter, and we know that this wordfilter 
     */
    public float calculateForDocument( Search search, TrigramOccurrence max, Collection<TrigramOccurrence> globalTrigramOccurrences, String documentId ) {
        return calculateForOccurrences( max, globalTrigramOccurrences, calculateTtfForDocument( search, documentId ) );
    }

    private Map<String, Integer> calculateTtfForDocument( Search search, String documentId ) {
        return search.getTrigramTermFrequencyData( documentId );
    }

    private float calculateForOccurrences( TrigramOccurrence maximumGlobalTrigramOccurences, Collection<TrigramOccurrence> globalTrigramOccurrences,
                    Map<String, Integer> documentTrigramOccurrences ) {

        double maxGTO = maximumGlobalTrigramOccurences.getOccurrenceCount();

        float document_ttf_idf = 0.000000000001f;

        for (TrigramOccurrence globalOccurrence : globalTrigramOccurrences) {
            // skip if the document doesn't contain this tri gram, for whatever reason...
            if (!documentTrigramOccurrences.containsKey( globalOccurrence.getTrigram() )) {
                continue;
            }

            double ttf = documentTrigramOccurrences.get( globalOccurrence.getTrigram() ).doubleValue();

            // high precision
            double local_tfidf = Math.log( ttf * (maxGTO / ((double) (1 + globalOccurrence.getOccurrenceCount()))) );

            // low precision
            document_ttf_idf += (float) local_tfidf;
        }

        return document_ttf_idf;
    }
}
