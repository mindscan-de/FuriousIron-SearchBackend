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

    private int maxElementsInList;
    private ArrayList<Integer> backingArrayList = new ArrayList<>();

    public TopKScoreList( int maxElementsInList ) {
        this.maxElementsInList = maxElementsInList;
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
            for (int i = 0; i < backingArrayList.size(); i++) {
                if (backingArrayList.get( i ) == score) {
                    // if already in list do not add again / only add once.
                    break;
                }

                if (backingArrayList.get( i ) < score) {
                    backingArrayList.add( i, score );
                    removeLast();
                    break;
                }
            }

        }

        return true;
    }

    public void removeLast() {
        backingArrayList.remove( maxElementsInList );
    }

    public Set<Integer> getSet() {
        return new HashSet<>( backingArrayList );
    }

}
