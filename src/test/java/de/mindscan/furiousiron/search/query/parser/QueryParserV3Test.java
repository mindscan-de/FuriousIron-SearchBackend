package de.mindscan.furiousiron.search.query.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;

public class QueryParserV3Test {

    @Test
    public void testParseQuery_emptyString_returnsEmptyNode() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( "" );

        // Assert
        assertThat( result, is( instanceOf( EmptyNode.class ) ) );
    }

    @Test
    public void testParseQuery_nullAsString_returnsEmptyNode() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( null );

        // Assert
        assertThat( result, is( instanceOf( EmptyNode.class ) ) );
    }

    @Test
    public void testParseQuery_testTextQuery_() throws Exception {
        // arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // act
        QueryNode result = parserV3.parseQuery( "test" );

        // assert
        assertThat( result, is( instanceOf( TextNode.class ) ) );
    }

    @Test
    public void testParseQuery_StringContainsWordTest_returnsTextNodeHasWordTest() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( "test" );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "test" ) );
    }

    @Test
    public void testParseQuery_StringContainsWordDifferentWord_returnsTextNodeContainingDifferentWord() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( "differentword" );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "differentword" ) );
    }

    @Test
    public void testParseQuery_StringContainsWordTestWithSpaces_returnsTextNodeContainsWordTest() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( " test " );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "test" ) );
    }

    @Test
    public void testParseQuery_testExactQuery_returnsExactMatchingTextNode() throws Exception {
        // arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // act
        QueryNode result = parserV3.parseQuery( "\"test\"" );

        // assert
        assertThat( result, is( instanceOf( ExactMatchingTextNode.class ) ) );
    }

    @Test
    public void testParseQuery_testExactQuery__returnsExactTextNodeContainsWordTest() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( "\"test\"" );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "test" ) );
    }

    // TODO: OR of two terms (result.toString -> equalto)
    // TODO: AND of two terms (result.toString -> equalto)
    // TODO: INCLUDING
    // TODO: EXCLUDING

}
