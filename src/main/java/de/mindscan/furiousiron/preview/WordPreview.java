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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.mindscan.furiousiron.indexer.SimpleWordUtils;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.search.Search;

/**
 * 
 */
public class WordPreview {

    private QueryNode ast;
    private Collection<String> theTrigrams;

    private final int MAX_K_SCORES = 5;

    public WordPreview( QueryNode ast, Collection<String> theTrigrams ) {
        this.ast = ast;
        this.theTrigrams = theTrigrams;
    }

    public HashMap<String, Map<Integer, String>> getBestPreviews( Search search, List<String> queryDocumentIds, int startIndex ) {
        HashMap<String, Map<Integer, String>> result = new HashMap<>();

        // just limit the number of detail results to 25
        for (String documentIdMD5 : queryDocumentIds.subList( 0, Math.min( queryDocumentIds.size(), 25 ) )) {
            List<String> allLines = search.getDocumentContentLines( documentIdMD5 );

            TreeMap<Integer, String> lineContents = new TreeMap<>();
            TreeMap<Integer, Integer> lineScore = new TreeMap<>();

            int currentLine = 0;
            for (String lineContent : allLines) {

                currentLine++;

                // TODO: foreach line calc the trigrams count them while comparing them to "theTrigrams"
                Collection<String> lineTrigrams = SimpleWordUtils.getTrigramsFromLine( lineContent.toLowerCase() );

                lineTrigrams.retainAll( theTrigrams );
                if (lineTrigrams.isEmpty()) {
                    continue;
                }

                lineContents.put( currentLine, lineContent );
                lineScore.put( currentLine, lineTrigrams.size() );
            }

            result.put( documentIdMD5, getTopScoredLinesInDocument( lineContents, lineScore ) );
        }

        return result;
    }

    private Map<Integer, String> getTopScoredLinesInDocument( TreeMap<Integer, String> lineContents, TreeMap<Integer, Integer> lineScore ) {
        ArrayList<Integer> scores = new ArrayList<>( lineScore.values() );
        Collections.sort( scores, Comparator.reverseOrder() );
        Collection<Integer> topKScores = new HashSet<>( scores.subList( 0, Math.min( MAX_K_SCORES, scores.size() ) ) );

        Map<Integer, String> contentResult = new TreeMap<>();

        for (Entry<Integer, Integer> entry : lineScore.entrySet()) {
            if (topKScores.contains( entry.getValue() )) {
                int line = entry.getKey();
                contentResult.put( line, lineContents.get( line ) );
            }
        }

        return contentResult;
    }

}
