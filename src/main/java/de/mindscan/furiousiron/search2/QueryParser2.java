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

import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.parser.QueryParser;

/**
 * 
 */
public class QueryParser2 {

    public QueryNode compileSearchTreeFromQuery( String query ) {
        QueryParser queryParser = new QueryParser();
        QueryNode parsedAST = queryParser.parseQuery( query );

        return parsedAST;
    }

    public void compileCoreSearch( QueryNode ast ) {
        // Compile parsedAST into a technical AST
    }

    public void compileLexicalSearch( QueryNode ast ) {
        // compile parsedAST into a semantic search descriptio
    }

    public void search( String query ) {
        QueryNode ast = this.compileSearchTreeFromQuery( query );

        /* coreSearchAST = */ this.compileCoreSearch( ast );

        // TODO: coreCandidates = coreSearchAST.searchCoreCandidates();

        // result is DocumentIDs candidate list

        /* semanticSearchAST = */ this.compileLexicalSearch( ast );

        // TODO: semanticSearchAST.filterToResults(coreCandidates);

        // TODO: lexical search and look at each "document"
        // filter documents by wordlists and return a list of documents and their state, 
        // how many rules they fulfill, according to the wordlist and the semanticSearchAST

        // we may can do this by using bloom filters and weights at the filter level 

        // save this Queryresult (we can always improve the order later), when someone spends some again time for searching for it.
        // we can even let the user decide, which result was better... and use that as well for ordering next time.

        // TODO: predict the order of this documentlist according to the query.

        // now how near are the tokens, how many of them are in there
        // take the top 20 documents and do a "simpleSearch" on them, and try to present the user a
        // each time the user uses pagination only some of the results are searched in the real way.

        // We might train the to predict the score of a file vector according to the search vector using
        // transformers ... But this is way too sophisticated. and requires lots of training
    }
}
