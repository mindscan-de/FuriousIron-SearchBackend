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
import java.util.Map;

import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.MetaDataTextNode;
import de.mindscan.furiousiron.query.ast.OrNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;

/**
 * A word-level based AST matching.
 */
public class AstBasedWordlistFilter {

    /**
     * This method tests whether the given AST of QueryNodes matches a wordlist (e.g. a wordlist of an indexed document)
     * This is a word-based filtering mechanism, which is slightly more expensive in terms of compute costs after the 
     * trigram filters are exhausted. This kind of search like testing whether a search term is part of a word, will
     * result in quadratic or higher computing costs (well, if you don't optimize). 
     *  
     * @param ast the ast which is applied onto a wordlist
     * @param documentWordlist a word list of a document
     * @return <code>true</code> iff the AST matches a given word list.
     */
    static boolean isAstMatchingToWordlist( QueryNode ast, List<String> documentWordlist ) {
        if (ast instanceof TextNode) {
            String wordToSearch = ast.getContent();

            if (documentWordlist.contains( wordToSearch )) {
                return true;
            }

            int wordToSearchLength = wordToSearch.length();

            for (String documentWord : documentWordlist) {
                if (documentWord.length() > wordToSearchLength) {
                    if (documentWord.contains( wordToSearch )) {
                        return true;
                    }
                }
            }

            // it is neither contained fully nor partially. 
            return false;
        }

        if (ast instanceof ExactMatchingTextNode) {
            // since we don't have phrases in the wordlist present, 
            // it's a bit more complicated in case a user looks for phrases

            if (!((ExactMatchingTextNode) ast).isPhrase()) {
                return documentWordlist.contains( ast.getContent() );
            }

            // in case the ast has a phrase, it should be split, and each word must be in the list instead.
            Collection<String> wordsInPhrase = ((ExactMatchingTextNode) ast).getUniqueWordlist();

            if (wordsInPhrase.size() == 0) {
                return false;
            }

            // check that every word is included
            for (String singleWord : wordsInPhrase) {
                if (!isFullOrStartOrEndString( singleWord, documentWordlist )) {
                    return false;
                }
            }

            return true;
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

        // because we test the AST, this condition should not be triggered... 
        // but in case of "-filtype:java" it won't execute well and filter everything. 
        if (ast instanceof MetaDataTextNode) {
            // depending of whether we are in an excluide node we should basically return false and oherwise true
            // just do the right thing at this moment - should be improved soon.
            return true;
        }

        throw new RuntimeException( "This Node type is not supported: " + String.valueOf( ast ) );
    }

    static boolean isFullOrStartOrEndString( String singleWord, List<String> documentWordlist ) {
        if (documentWordlist.contains( singleWord )) {
            return true;
        }

        int wordlistLength = singleWord.length();

        for (String documentWord : documentWordlist) {
            if (documentWord.length() <= wordlistLength) {
                continue;
            }

            if (documentWord.startsWith( singleWord )) {
                return true;
            }

            if (documentWord.endsWith( singleWord )) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method tests whether the given AST of QueryNodes matches a wordlist (e.g. a wordlist of an indexed document)
     * This is a word-based filtering mechanism, which is slightly more expensive in terms of compute costs after the 
     * trigram filters are exhausted. This kind of search like testing whether a search term is part of a word, will
     * result in quadratic or higher computing costs (well, if you don't optimize). 
     *  
     * @param ast the ast which is applied onto a wordlist
     * @param documentWordlist a word list of a document
     * @return <code>true</code> iff the AST matches a given word list.
     */
    static boolean isAstMatchingToWordlistAndMetadata( QueryNode ast, List<String> documentWordlist, Map<String, String> metadata ) {
        if (ast instanceof TextNode) {
            String wordToSearch = ast.getContent();

            if (documentWordlist.contains( wordToSearch )) {
                return true;
            }

            int wordToSearchLength = wordToSearch.length();

            for (String documentWord : documentWordlist) {
                if (documentWord.length() > wordToSearchLength) {
                    if (documentWord.contains( wordToSearch )) {
                        return true;
                    }
                }
            }

            // it is neither contained fully nor partially. 
            return false;
        }

        if (ast instanceof ExactMatchingTextNode) {
            // since we don't have phrases in the wordlist present, 
            // it's a bit more complicated in case a user looks for phrases

            if (!((ExactMatchingTextNode) ast).isPhrase()) {
                return documentWordlist.contains( ast.getContent() );
            }

            // in case the ast has a phrase, it should be split, and each word must be in the list instead.
            Collection<String> wordsInPhrase = ((ExactMatchingTextNode) ast).getUniqueWordlist();

            if (wordsInPhrase.size() == 0) {
                return false;
            }

            for (String singleWord : wordsInPhrase) {
                if (!isFullOrStartOrEndString( singleWord, documentWordlist )) {
                    return false;
                }
            }

            return true;
        }

        if (ast instanceof AndNode) {
            if (ast.hasChildren()) {
                Collection<QueryNode> children = ast.getChildren();
                for (QueryNode queryNode : children) {
                    // early exit in case of a "false" - no need to check further if word is not found.
                    if (!isAstMatchingToWordlistAndMetadata( queryNode, documentWordlist, metadata )) {
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
                    if (isAstMatchingToWordlistAndMetadata( queryNode, documentWordlist, metadata )) {
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
                return isAstMatchingToWordlistAndMetadata( first, documentWordlist, metadata );
            }
            else {
                return true;
            }
        }

        if (ast instanceof ExcludingNode) {
            if (ast.hasChildren()) {
                QueryNode first = ast.getChildren().iterator().next();
                return !isAstMatchingToWordlistAndMetadata( first, documentWordlist, metadata );
            }
            else {
                return false;
            }
        }

        if (ast instanceof MetaDataTextNode) {
            String key = ((MetaDataTextNode) ast).getKey();
            String value = ast.getContent();

            if (!metadata.containsKey( key )) {
                return false;
            }

            String metadataContent = metadata.get( key );

            if (metadataContent == null) {
                return false;
            }

            return metadataContent.contains( value );
        }

        throw new RuntimeException( "This Node type is not supported: " + String.valueOf( ast ) );
    }

}
