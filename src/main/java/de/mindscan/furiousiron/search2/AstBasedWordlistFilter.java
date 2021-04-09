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

import java.util.Collection;
import java.util.List;

import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;

/**
 * 
 */
public class AstBasedWordlistFilter {

    // TODO: wordlists should be organized by wordsize in a TreeSet
    //       in an andnode, the most unlikely word should be processed first
    //       int an or node, the most likely word should be processed first
    static boolean isAstMatchingToWordlist( QueryNode ast, List<String> documentWordlist ) {

        if (ast instanceof TextNode) {
            String wordToSearch = ast.getContent();

            // if it is directly contained
            if (documentWordlist.contains( wordToSearch )) {
                // this should yield highest reward
                return true;
            }

            int wordToSearchLength = wordToSearch.length();

            // we might want to split the loop, to prefer start over ends over contains
            // we might want to return relevance instead of boolean
            for (String documentWord : documentWordlist) {
                if (documentWord.length() > wordToSearchLength) {
                    // this should yield a higher Score
//                    if (documentWord.startsWith( wordToSearch )) {
//                        return true;
//                    }
//
//                    // this should yield high Score
//                    if (documentWord.endsWith( wordToSearch )) {
//                        return true;
//                    }

                    // this should yield some reward
                    if (documentWord.contains( wordToSearch )) {
                        return true;
                    }
                }
            }

            // it is neither contained fully nor partially. 
            return false;
        }

        if (ast instanceof ExactMatchingTextNode) {
            // TODO: implement a correct strategy to handle exact matching text nodes
            // TODO: we must distinguish between a phrase containing WS and such, then this is more complicated.

            String wordToSearch = ast.getContent();

            if (documentWordlist.contains( wordToSearch )) {
                return true;
            }

            return false;
        }

        if (ast instanceof AndNode) {
            if (ast.hasChildren()) {
                Collection<QueryNode> children = ast.getChildren();
                for (QueryNode queryNode : children) {
                    // early exit in case of a "false" - no need to check further if word is not found.
                    if (!isAstMatchingToWordlist( queryNode, documentWordlist )) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return true;
            }
        }

        if (ast instanceof OrNode) {
            if (ast.hasChildren()) {
                Collection<QueryNode> children = ast.getChildren();
                for (QueryNode queryNode : children) {
                    // early exit in case of a "true" - no need to check further if other word is also found.
                    if (isAstMatchingToWordlist( queryNode, documentWordlist )) {
                        return true;
                    }
                }
                return false;
            }
            else {
                return false;
            }
        }

        if (ast instanceof IncludingNode) {
            if (ast.hasChildren()) {
                QueryNode first = ast.getChildren().iterator().next();
                return isAstMatchingToWordlist( first, documentWordlist );
            }
            else {
                return true;
            }
        }

        if (ast instanceof ExcludingNode) {
            if (ast.hasChildren()) {
                QueryNode first = ast.getChildren().iterator().next();
                return !isAstMatchingToWordlist( first, documentWordlist );
            }
            else {
                return false;
            }
        }

        throw new RuntimeException( "This Node type is not supported: " + ast.toString() );
    }

}
