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

/**
 * A filterbank is a collection of multiple filters, they don't need to be
 * 
 * The cool thing about such a filterbank is, that it could already combine often
 * used search terms instead of only trigrams and build a hash of these and cache 
 * this particular filterbank. 
 */
public class HFBFilterBank {

    public void addDocumentId( BigInteger documentId ) {
        // we use each HFBFilterdata and add it to each filter we currently know.
        // foreach hfbfilterdata {
        //   calculate the partial id
        //   int pid = documentId.shiftright(hfbfilterdata.sliceposition).and(hfbfilterdata.sliceMask).intValueExact();
        //   hfbfilterdata.setIndex(pid);
        // }
    }

    // TODO: save filterbank
    // TODO: load filterbank
    // TODO: load reduced filterbank, will load the filterbank partially 
    //       e.g. load only 3 out of 8 hfbfilters -> save io and compute

    public boolean containsDocumentId( BigInteger documentId ) {
        // now the trick, is how many of these filters we do want to apply?
        // count the number of applied filters and return true, if we passed 3 max. 4 filters?
        // with 80% dropout rate we get a maximum false positive error rate 
        // * of 0,8 percent when 3 hfb filters are asked = 3 times O(1) lookup
        // * of 0,16 percent when 4 hfb filters are asked = 4 times O(1) lookup
        return false;
    }
}
