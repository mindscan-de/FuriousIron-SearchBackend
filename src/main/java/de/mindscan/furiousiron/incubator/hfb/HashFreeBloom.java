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
package de.mindscan.furiousiron.incubator.hfb;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.mindscan.furiousiron.index.trigram.TrigramOccurrence;
import de.mindscan.furiousiron.search.Search;

/**
 * Actually the idea is to implement an effective hash-free alternative to 
 * bloom-filters. Bloom filters are nice, but somehow over-rated, and the 
 * most annoying thing is the requirement for a hash function, which actually
 * compresses the input. I don't want to spend cpu-cycles on hashing things 
 * over and over again. We often enough have hashes already in place, so
 * hashing the hashes is just a big waste of compute.
 * 
 * I also want a lookup of O(1) for a no/maybe decision. What's also useful is,
 * that different filters of the same size and the same position can be joined
 * before applying two filter operations.
 */
public class HashFreeBloom {

    private Search index;

    /**
     * 
     */
    public HashFreeBloom() {
        index = new Search( Paths.get( "D:\\Analysis\\CrawlerProjects", "Indexed" ) );
    }

    public void createHFBFilter() {
        String[] x = new String[] { "imp" };
        List<TrigramOccurrence> trigramOccurrencesSortedByOccurrence = index.getTrigramOccurrencesSortedByOccurrence( Arrays.asList( x ) );
        Set<String> y = index.collectDocumentIdsForTrigramsOpt( Arrays.asList( x ) );

        TrigramOccurrence trigramOccurrence = trigramOccurrencesSortedByOccurrence.get( 0 );
        System.out.println( trigramOccurrence );

        HFBFilterBank bank = new HFBFilterBank();
        bank.initFilters( 128, trigramOccurrence.getOccurrenceCount(), 5 );

        long highestBitMasked = Long.highestOneBit( trigramOccurrence.getOccurrenceCount() * 5 );
        int sliceSize = (int) Long.numberOfTrailingZeros( highestBitMasked );

        int slicePosition = 128 - sliceSize;
        // build the filter.
        HFBFilterData hfbdata = new HFBFilterData( slicePosition, sliceSize );
        hfbdata.initFilter();

        // use a common mask.
        BigInteger sliceMaskBi = new BigInteger( Long.toString( hfbdata.getSliceBitMask() ) );

        for (String documentId : y) {
            BigInteger bi = new BigInteger( documentId, 16 );

            // TODO. we may have multiple filter to fill, so we can go multiple times, 
            //       and we want to go over about half of the bits of the documentid

            // this can be done in parallel for all filters
            BigInteger bi_s = bi.shiftRight( slicePosition );
            BigInteger bi_s_m = bi_s.and( sliceMaskBi );

            hfbdata.setIndex( bi_s_m.intValueExact() );
        }

        // ein filter dieser Art hat eine rejection rate von 81 prozent, wenn bspw der Faktor 5 verwendet wird, aber in wirklichkeit irgendwo zwischen 3 und 6
        // eine kombination aus zwei filtern gleicher größe und gleicher hashlänge können vorher kombiniert werden und können die rejection rate erhöhen.

    }
}
