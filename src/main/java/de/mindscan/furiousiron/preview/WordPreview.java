/**
 * 
 * MIT License
 *
 * Copyright (c) 2021, 2022 Maxim Gansert, Mindscan
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import de.mindscan.furiousiron.indexer.SimpleWordUtils;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.search.Search;

/**
 * Calculate and filter the best preview lines of a document. 
 */
public class WordPreview {

    @SuppressWarnings( "unused" )
    private QueryNode ast;
    private Collection<String> theTrigrams;

    private final int MAX_K_SCORES = 5;
    private final int MAX_DOCUMENTS_TO_ANALYZE = 35;

    private final String CLIPPING_INDICATOR = "...";

    private final int MAX_LINE_LENGTH = 512;
    private final int MAX_LINE_LENGTH_CLIPPED = MAX_LINE_LENGTH - CLIPPING_INDICATOR.length();

    public WordPreview( QueryNode ast, Collection<String> theTrigrams ) {
        this.ast = ast;
        this.theTrigrams = theTrigrams;
    }

    public HashMap<String, Map<Integer, String>> getBestPreviews( Search search, List<String> queryDocumentIds, int startIndex ) {
        HashMap<String, Map<Integer, String>> documentLinePreviews = new HashMap<>();

        int maxDocumentsToAnalyze = Math.min( queryDocumentIds.size(), MAX_DOCUMENTS_TO_ANALYZE );

        for (String documentIdMD5 : queryDocumentIds.subList( 0, maxDocumentsToAnalyze )) {
            List<String> documentContent = search.getDocumentContentLines( documentIdMD5 );
            documentLinePreviews.put( documentIdMD5, extractTopScoringLinesForDocument( documentContent ) );
        }

        return documentLinePreviews;
    }

    private Map<Integer, String> extractTopScoringLinesForDocument( List<String> documentContent ) {
        TopKScoreList topKScoreList = new TopKScoreList( MAX_K_SCORES );
        TreeMap<Integer, String> lineContents = new TreeMap<>();
        TreeMap<Integer, Integer> lineScores = new TreeMap<>();

        int currentLineNumber = 0;
        for (String lineContent : documentContent) {

            currentLineNumber++;

            String shortenedLineContent = lineContent;
            if (lineContent.length() > MAX_LINE_LENGTH) {
                shortenedLineContent = lineContent.substring( 0, MAX_LINE_LENGTH_CLIPPED ) + CLIPPING_INDICATOR;
            }

            Collection<String> filteredLineTrigrams = SimpleWordUtils.getTrigramsFromLineFiltered( shortenedLineContent.toLowerCase(), theTrigrams );

            // avoid littering the preview with worthless lines, which yield a score of zero 
            if (filteredLineTrigrams.isEmpty()) {
                continue;
            }

            int currentLineScore = calculateLineScore( filteredLineTrigrams );

            // only further process lines, which have a (currently) good enough score and 
            // move the goal post in case of a new good score - this reduces compute and memory usage in later stages
            if (topKScoreList.isCandidateTopK( currentLineScore )) {
                lineContents.put( currentLineNumber, shortenedLineContent );
                lineScores.put( currentLineNumber, currentLineScore );
            }
        }

        return filterTopScoredLines( lineContents, lineScores, topKScoreList.getSet() );
    }

    private int calculateLineScore( Collection<String> filteredLineTrigrams ) {
        // TODO: we should consider to use the trigram occurrence over the simple count because, often occurring trigrams will lead to bad line picks in source code.        
        return filteredLineTrigrams.size();
    }

    private Map<Integer, String> filterTopScoredLines( Map<Integer, String> lineContents, Map<Integer, Integer> scores, Set<Integer> topK ) {
        Map<Integer, String> contentResult = new TreeMap<>();

        for (Entry<Integer, Integer> entry : scores.entrySet()) {
            if (topK.contains( entry.getValue() )) {
                Integer line = entry.getKey();
                contentResult.put( line, lineContents.get( line ) );
            }
        }

        return contentResult;
    }

}
