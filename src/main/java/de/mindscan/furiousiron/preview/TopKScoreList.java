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
 * This is a dynamic top k score calculator.
 * 
 * It checks whether a given score is a candidate for a top k element. If it is, it is kept as an candidate 
 * and in case a new candidate is found, it will update its current top k score and drop the lowest score 
 * from the current backing set. 
 * 
 * That will move the goal post higher and higher, so less and less times the insertion is happening. Anyhow
 * this should have a good enough performance and it avoids later calculation of the top k score. We want to
 * avoid storing and processing data, which we would discard anyways.  
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
        int lastValidIndex = maxElementsInList - 1;

        if (lastValidIndex < 0) {
            return false;
        }

        // it is more likely that a score is not high enough, 
        // therefore this operation will stop any further useless calculations earlier, even though it is more complex
        if (backingArrayList.get( lastValidIndex ) > score) {
            return false;
        }

        // if we know this particular score, we are complete
        if (backingSet.contains( score )) {
            return true;
        }

        if (backingArrayList.get( lastValidIndex ) < score) {
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

        // this line will actually never been reached / maybe we don't need the last
        // if (backingArrayList.get( lastValidIndex ) < score) comparison at all..
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
