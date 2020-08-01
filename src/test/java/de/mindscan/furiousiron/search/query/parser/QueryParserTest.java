/**
 *
 * MIT License
 *
 * Copyright (c) 2019 Maxim Gansert, Mindscan
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.ast.TextNode;
import de.mindscan.furiousiron.search.query.tokenizer.QueryToken;
import de.mindscan.furiousiron.search.query.tokenizer.TextQueryToken;

public class QueryParserTest {

    @Test
    public void testParseQuery_emptyString_returnsEmptyNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "" );

        // Assert
        assertThat( result, is( instanceOf( EmptyNode.class ) ) );
    }

    @Test
    public void testParseQuery_nullAsString_returnsEmptyNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( null );

        // Assert
        assertThat( result, is( instanceOf( EmptyNode.class ) ) );
    }

    @Test
    public void testParseQuery_StringContainsWordTest_returnsTextNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "test" );

        // Assert
        assertThat( result, is( instanceOf( TextNode.class ) ) );
    }

    @Test
    public void testParseQuery_StringContainsWordTest_returnsTextNodeHasWordTest() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "test" );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "test" ) );
    }

    @Test
    public void testParseQuery_StringContainsWordDifferentWord_returnsTextNodeContainingDifferentWord() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "differentword" );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "differentword" ) );
    }

    @Test
    public void testParseQuery_StringContainsWordTestWithSpaces_returnsTextNodeContainsHasWordTest() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( " test " );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "test" ) );
    }

//    @Test
//    public void testParseQuery_StringContainsTwoWords_returnsOrNode() {
//        // Arrange
//        QueryParser queryParser = new QueryParser();
//
//        // Act
//        QueryNode result = queryParser.parseQuery( "test elastic" );
//
//        // Assert
//        assertThat( result, is( instanceOf( OrNode.class ) ) );
//    }

    // ====================================================
    // Token parsing
    // ====================================================

    @Test
    public void testParseQueryTokens_emptyTokenList_returnsEmptyNode() throws Exception {
        // arrange
        QueryParser parser = new QueryParser();
        List<QueryToken> tokenizedQuery = Collections.emptyList();

        // act
        QueryNode result = parser.parseQueryTokens( tokenizedQuery );

        // assert
        assertThat( result, is( instanceOf( EmptyNode.class ) ) );
    }

    @Test
    public void testParseQuery_ContainsOneTextToken_returnsTextNode() throws Exception {
        // arrange
        QueryParser parser = new QueryParser();
        List<QueryToken> tokenizedQuery = new ArrayList<>();
        tokenizedQuery.add( new TextQueryToken( "test" ) );

        // act
        QueryNode result = parser.parseQueryTokens( tokenizedQuery );

        // assert
        assertThat( result, is( instanceOf( TextNode.class ) ) );
    }

    // 

    @Test
    public void testParseQuery_EmptyString_expectASTSerializationIsOnlyEmptyNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'EMPTY' ]" ) );
    }

    @Test
    public void testParseQuery_nullAsString_expectASTSerializationIsOnlyEmptyNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( null );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'EMPTY' ]" ) );
    }

    @Test
    public void testParseQuery_TwoWordsOR_expectASTSerialization() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "first second" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'OR', [ [ 'INCLUDING', [ [ 'TEXT', 'first' ] ] ], [ 'INCLUDING', [ [ 'TEXT', 'second' ] ] ] ] ]" ) );
    }

    @Test
    public void testParseQuery_TwoWordsAND_expectOnlyASTSerializationOfFirstElement() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "first +second" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'AND', [ [ 'INCLUDING', [ [ 'TEXT', 'first' ] ] ], [ 'INCLUDING', [ [ 'TEXT', 'second' ] ] ] ] ]" ) );
    }

    @Test
    public void testParseQuery_TwoWordsButNotSecond_expectOnlyASTSerializationOfFirstElement() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "first -second" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'AND', [ [ 'INCLUDING', [ [ 'TEXT', 'first' ] ] ], [ 'EXCLUDING', [ [ 'TEXT', 'second' ] ] ] ] ]" ) );
    }

// tpxu_method
}
