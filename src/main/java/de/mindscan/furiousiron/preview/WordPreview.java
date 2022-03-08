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

    @SuppressWarnings( "unused" )
    private QueryNode ast;
    private Collection<String> theTrigrams;

    private final int MAX_K_SCORES = 5;
    private final int MAX_DOCUMENTS_TO_ANALYZE = 35;

    public WordPreview( QueryNode ast, Collection<String> theTrigrams ) {
        this.ast = ast;
        this.theTrigrams = theTrigrams;
    }

    public HashMap<String, Map<Integer, String>> getBestPreviews( Search search, List<String> queryDocumentIds, int startIndex ) {
        HashMap<String, Map<Integer, String>> result = new HashMap<>();

        for (String documentIdMD5 : queryDocumentIds.subList( 0, Math.min( queryDocumentIds.size(), MAX_DOCUMENTS_TO_ANALYZE ) )) {
            TopKScoreList topKScoreList = new TopKScoreList( MAX_K_SCORES );
            List<String> allLines = search.getDocumentContentLines( documentIdMD5 );

            TreeMap<Integer, String> lineContents = new TreeMap<>();
            TreeMap<Integer, Integer> lineScore = new TreeMap<>();

            int currentLine = 0;
            for (String lineContent : allLines) {

                currentLine++;

                boolean isExceeding = lineContent.length() > 512;

                String shortenedLineContent = lineContent;
                if (isExceeding) {
                    shortenedLineContent = lineContent.substring( 0, 509 ) + "...";
                }

                Collection<String> filteredLineTrigrams = SimpleWordUtils.getTrigramsFromLineFiltered( shortenedLineContent.toLowerCase(), theTrigrams );
                if (filteredLineTrigrams.isEmpty()) {
                    continue;
                }

                // TODO: we should consider to use the trigram occurence over the simple count because, often occuring trigrams will lead to bad line picks in source code.
                int score = filteredLineTrigrams.size();

                if (topKScoreList.isCandidateTopK( score )) {
                    lineContents.put( currentLine, shortenedLineContent );
                    lineScore.put( currentLine, score );
                }
            }

            result.put( documentIdMD5, getTopScoredLinesInDocument( lineContents, lineScore ) );
        }

        return result;
    }

    private Map<Integer, String> getTopScoredLinesInDocument( TreeMap<Integer, String> lineContents, TreeMap<Integer, Integer> lineScore ) {

        // TODO: timing, actually we want to calculate this value, while collecting the line score
        // instead of sorting a list of hundreds candidates with the score.
        ArrayList<Integer> scores = new ArrayList<>( lineScore.values() );
        Collections.sort( scores, Comparator.reverseOrder() );
        Collection<Integer> topKScores = new HashSet<>( scores.subList( 0, Math.min( MAX_K_SCORES, scores.size() ) ) );

        // TODO: is this faster?
        // Collection<Integer> topKScores = lineScore.values().stream().sorted( Comparator.reverseOrder() ).limit( MAX_K_SCORES ).collect( Collectors.toSet() );

        Map<Integer, String> contentResult = new TreeMap<>();

        for (Entry<Integer, Integer> entry : lineScore.entrySet()) {
            if (topKScores.contains( entry.getValue() )) {
                Integer line = entry.getKey();
                contentResult.put( line, lineContents.get( line ) );
            }
        }

        return contentResult;
    }

}
