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

/**
 * 
 */
public class HFBFilterData {

    // The position (number of bits to shift right before applying the sliceBitMask.
    private int slicePosition;

    // the number of bits for the HFB-Filter
    private int sliceBitSize;

    // the mask for the bits.
    private long sliceBitMask;

    // TODO: will have to implement this later.
    // private byte[] sliceData;

    public HFBFilterData( int slicePosition, int numberOfBits ) {
        setSlicePosition( slicePosition );
        setSliceMaskSize( numberOfBits );
    }

    public void setSliceMaskSize( int numberOfBits ) {
        this.sliceBitSize = numberOfBits;
        long sliceSize = 1L << (numberOfBits);
        this.sliceBitMask = sliceSize - 1L;

        // allocate according to sliceSize () - well maybe this is too large,
        // but we really shouldn't care right now. I leave it for future 
        // development and future improvements
    }

    // TODO: This will set the slice data / e.g if the data is loaded from disk.
    protected void setSliceData() {
    }

    public long getSliceBitMask() {
        return sliceBitMask;
    }

    public int getSliceBitSize() {
        return sliceBitSize;
    }

    public void setSlicePosition( int slicePosition ) {
        this.slicePosition = slicePosition;
    }

    public int getSlicePosition() {
        return slicePosition;
    }
}
