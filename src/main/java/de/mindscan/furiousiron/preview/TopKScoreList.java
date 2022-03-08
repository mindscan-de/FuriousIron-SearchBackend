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
package de.mindscan.furiousiron.preview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public class TopKScoreList {

    private final int maxElementsInList;
    private final ArrayList<Integer> backingArrayList;
    private final Set<Integer> backingSet;

    public TopKScoreList( int maxElementsInList ) {
        this.maxElementsInList = maxElementsInList;
        this.backingSet = new HashSet<>();
        this.backingArrayList = new ArrayList<>();
        for (int i = 0; i < maxElementsInList; i++) {
            backingArrayList.add( Integer.MIN_VALUE );
        }
    }

    public boolean isCandidateTopK( int score ) {
        if (backingArrayList.get( maxElementsInList - 1 ) > score) {
            return false;
        }

        if (backingArrayList.get( maxElementsInList - 1 ) < score) {
            if (backingSet.contains( score )) {
                return true;
            }

            // find position to insert...
            for (int i = 0; i < backingArrayList.size(); i++) {
                if (backingArrayList.get( i ) < score) {
                    backingArrayList.add( i, score );
                    backingSet.add( score );
                    removeLast();
                    return true;
                }
            }
        }

        return true;
    }

    public void removeLast() {
        Integer removed = backingArrayList.remove( maxElementsInList );
        backingSet.remove( removed );
    }

    public Set<Integer> getSet() {
        return backingSet;
    }

}
