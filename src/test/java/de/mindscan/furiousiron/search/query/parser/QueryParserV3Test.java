package de.mindscan.furiousiron.search.query.parser;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.query.ast.QueryNode;

public class QueryParserV3Test {

    @Test
    public void testParseQuery_testTextQuery_() throws Exception {
        // arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // act
        QueryNode result = parserV3.parseQuery( "test" );

        // assert
        // cpxuas

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
