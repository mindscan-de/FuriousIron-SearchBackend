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
package de.mindscan.furiousiron.search2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.wordlists.WordlistCompiler;

/**
 * 
 */
public class WordlistOrderedWordlistCompiler implements WordlistCompiler {

    // private List<String> orderedWordlist;
    private Map<String, Integer> wordOrderMap = new HashMap<>();

    /**
     * Compare the words in given pre-defined ordered Wordlist.
     */
    public WordlistOrderedWordlistCompiler( Collection<String> orderedWordlist ) {
        this.wordOrderMap = new HashMap<>();
        // this.orderedWordlist = orderedWordlist;

        int index = 0;
        for (String word : orderedWordlist) {
            wordOrderMap.put( word, index );
            index++;
        }
    }

    public QueryNode compile( QueryNode ast, Search search ) {
        if (ast == null) {
            return new EmptyNode();
        }

        if (ast instanceof EmptyNode) {
            return new EmptyNode();
        }

        if (ast instanceof TextNode) {
            return new TextNode( ast.getContent().toLowerCase() );
        }

        if (ast instanceof AndNode) {
            List<QueryNode> andList = new ArrayList<>();
            Map<QueryNode, Integer> map = new HashMap<>();

            if (ast.hasChildren()) {
                for (QueryNode queryNode : ast.getChildren()) {
                    // create a new Node 
                    QueryNode newNode = compile( queryNode, search );

                    map.put( newNode, calculateProjectedWordIndex( newNode, search ) );
                    andList.add( newNode );
                }

                andList.sort( Comparator.comparingInt( node -> map.get( node ).intValue() ) );
            }

            return new AndNode( andList );
        }

        if (ast instanceof OrNode) {
            List<QueryNode> orList = new ArrayList<>();

            Map<QueryNode, Integer> map = new HashMap<>();

            if (ast.hasChildren()) {
                for (QueryNode queryNode : ast.getChildren()) {
                    // create a new Node 
                    QueryNode newNode = compile( queryNode, search );

                    // for new node calculate projectedRelativeOccurence / reverse order
                    map.put( newNode, -(calculateProjectedWordIndex( queryNode, search )) );
                    orList.add( newNode );
                }

                // sort by projectedRelativeOccurence from seldom to high probability
                orList.sort( Comparator.comparingInt( node -> map.get( node ).intValue() ) );
            }

            return new OrNode( orList );
        }

        if (ast instanceof ExcludingNode) {
            if (ast.hasChildren()) {
                for (QueryNode node : ast.getChildren()) {
                    return new ExcludingNode( compile( node, search ) );
                }
            }
            return new ExcludingNode( new EmptyNode() );
        }

        if (ast instanceof IncludingNode) {
            if (ast.hasChildren()) {
                for (QueryNode node : ast.getChildren()) {
                    return new IncludingNode( compile( node, search ) );
                }
            }
            return new IncludingNode( new EmptyNode() );
        }

        return null;
    }

    private int calculateProjectedWordIndex( QueryNode ast, Search search ) {
        if (ast == null) {
            return 1000000;
        }

        if (ast instanceof EmptyNode) {
            return 1000000;
        }

        if (ast instanceof TextNode) {
            try {
                return projectWordindex( ast.getContent().toLowerCase(), search );
            }
            catch (IndexOutOfBoundsException e) {
                return 1000000;
            }
        }

        if (ast instanceof AndNode) {
            if (ast.hasChildren()) {
                int theMinimum = 100000;
                for (QueryNode node : ast.getChildren()) {
                    try {
                        theMinimum = Math.min( calculateProjectedWordIndex( node, search ), theMinimum );
                    }
                    catch (Exception ignore) {
                    }
                }

                return theMinimum;
            }
            else {
                return 1000000;
            }
        }

        if (ast instanceof OrNode) {
            if (ast.hasChildren()) {
                int theMaximum = 0;
                for (QueryNode node : ast.getChildren()) {
                    try {
                        theMaximum = Math.max( calculateProjectedWordIndex( node, search ), theMaximum );
                    }
                    catch (Exception ignore) {
                    }
                }

                return theMaximum;
            }
            else {
                return 1000000;
            }
        }

        if (ast instanceof IncludingNode) {
            if (ast.hasChildren()) {
                for (QueryNode node : ast.getChildren()) {
                    return calculateProjectedWordIndex( node, search );
                }
            }
            return 1000000;
        }

        if (ast instanceof ExcludingNode) {
            if (ast.hasChildren()) {
                for (QueryNode node : ast.getChildren()) {
                    return calculateProjectedWordIndex( node, search );
                }
            }
            return 1000000;
        }

        return 1000000;
    }

    private int projectWordindex( String word, Search search ) {
        return this.wordOrderMap.get( word );
    }

}
