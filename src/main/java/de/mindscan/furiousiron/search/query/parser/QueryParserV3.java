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
package de.mindscan.furiousiron.search.query.parser;

import de.mindscan.furiousiron.query.ASTTransformer;
import de.mindscan.furiousiron.query.ast.AndNode;
import de.mindscan.furiousiron.query.ast.ExcludingNode;
import de.mindscan.furiousiron.query.ast.IncludingNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.QueryNodeListNode;
import de.mindscan.furiousiron.search.query.parser.transform.QueryParserListToAndOrAstTransformer;
import de.mindscan.furiousiron.search.query.token.SearchQueryToken;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenProcessor;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenProcessorFactory;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenType;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokens;

/**
 * This class implements a simple parser for the search query string. 
 * 
 * It will compile a query string into an query AST representation. The same AST will be 
 * later used for ranking and for calculations of the query strategy.
 * 
 * This thing is using classic parser stuff
 * - use a lexxer/tokenizer to create lexical tokens of the input (in our case a query string)
 * - parse the lexxed tokens and build an AST from it (using a higher level "token processor")
 * - after first pass (reading a simple AST with groups) it gets translated into "and"- and "or"-nodes
 * - this AST is later interpreted and used to build search execution strategies, and filter strategies and so on.
 * 
 */
public class QueryParserV3 implements SearchQueryParser {

    private SearchQueryTokenProcessor tokenProcessor;

    // AST Transformer ("List Phase") - which will compile QueryNodeListNodes into complete AND/OR trees. 
    private ASTTransformer listPhase;

    /**
     * 
     */
    public QueryParserV3() {
        this.listPhase = new QueryParserListToAndOrAstTransformer();
    }

    public QueryNode parseQuery( String queryString ) {
        if (queryString == null || queryString.isEmpty()) {
            return ASTNodeFactory.createEmptyNode();
        }

        setTokenProcessor( SearchQueryTokenProcessorFactory.create( queryString ) );

        // TODO: actually it 
        // 1.: parses the query and 
        // 2.: then solves the AST and replaces all ListNodes 
        // 3.: this phases should be split and should be outside of this parser. The AST transformation should not be here or configured outside. 
        return listPhase.transform( parseSearchTermList() );
    }

    void setTokenProcessor( SearchQueryTokenProcessor tokenProcessor ) {
        this.tokenProcessor = tokenProcessor;
    }

    QueryNodeListNode parseSearchTermList() {
        QueryNodeListNode listNode = new QueryNodeListNode();

        while (tokenProcessor.hasNext()) {
            listNode.addNode( parseSearchOperators() );
        }
        return listNode;
    }

    // +
    // -
    // TODO: '(',')' - parenthesis not yet implemented. (not so easy either.)
    QueryNode parseSearchOperators() {
        if (tokenProcessor.tryType( SearchQueryTokenType.SEARCHTERM ) || tokenProcessor.tryType( SearchQueryTokenType.EXACTSEARCHTERM )) {
            return parseSearchTerminalTextTerm();
        }
        if (tokenProcessor.tryAndAcceptToken( SearchQueryTokens.OPERATOR_PLUS )) {
            QueryNode postPlusAST = parseSearchOperators();
            return new AndNode( new IncludingNode( postPlusAST ) );
        }
        else if (tokenProcessor.tryAndAcceptToken( SearchQueryTokens.OPERATOR_MINUS )) {
            QueryNode postMinusAST = parseSearchOperators();
            return new AndNode( new ExcludingNode( postMinusAST ) );
        }
        else {
            throw new RuntimeException( "Not Yet implemented." );
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // SearchTerminalTextTerm :=
    //    {ExactMatchingTextNode} current=EXACTSEARCHTERM | 
    //    {TextNode} current=SEARCHTERM ( {MetaDataTextNode} =>?':' key=current value=SearchTerminalTextTerm ) )
    // ---------------------------------------------------------------------------------------------------

    QueryNode parseSearchTerminalTextTerm() {
        if (tokenProcessor.tryAndAcceptType( SearchQueryTokenType.EXACTSEARCHTERM )) {
            SearchQueryToken current = tokenProcessor.last();
            return ASTNodeFactory.createExactMatchingTextNode( current );
        }
        else if (tokenProcessor.tryAndAcceptType( SearchQueryTokenType.SEARCHTERM )) {
            SearchQueryToken current = tokenProcessor.last();

            if (tokenProcessor.tryAndAcceptToken( SearchQueryTokens.OPERATOR_DOUBLECOLON )) {
                SearchQueryToken key = current;
                QueryNode value = parseSearchTerminalTextTerm();

                return ASTNodeFactory.createMetaDataTextNode( key, value );
            }

            return ASTNodeFactory.createTextNode( current );
        }

        return ASTNodeFactory.createEmptyNode();
    }

}
