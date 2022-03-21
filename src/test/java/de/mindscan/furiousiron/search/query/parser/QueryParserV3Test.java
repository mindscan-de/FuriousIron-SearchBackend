package de.mindscan.furiousiron.search.query.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.QueryNode;

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
        // assertThat( result, is( instanceOf( TextNode.class ) ) );

    }

    @Test
    public void testParseQuery_testExactQuery_() throws Exception {
        // arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // act
        QueryNode result = parserV3.parseQuery( "\"test\"" );

        // assert
        // cpxuas

    }

}
