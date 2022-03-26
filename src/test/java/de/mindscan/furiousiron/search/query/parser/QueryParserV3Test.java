package de.mindscan.furiousiron.search.query.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.ExactMatchingTextNode;
import de.mindscan.furiousiron.query.ast.MetaDataTextNode;
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
    public void testParseQuery_testExactQuery_returnsExactTextNodeContainsWordTest() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( "\"test\"" );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "test" ) );
    }

    @Test
    public void testParseQuery_testMetaDataFiletypeIsJava_returnsMetaData() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( "filetype:java" );

        // Assert
        assertThat( result, is( instanceOf( MetaDataTextNode.class ) ) );
    }

    @Test
    public void testParseQuery_testMetaDataFiletypeIsJava_returnsMetaDataKeyIsFiletype() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        MetaDataTextNode node = (MetaDataTextNode) parserV3.parseQuery( "filetype:java" );

        // Assert
        String result = node.getKey();
        assertThat( result, equalTo( "filetype" ) );
    }

    @Test
    public void testParseQuery_testMetaDataFiletypeIsJava_returnsMetaDataValueIsJava() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        MetaDataTextNode node = (MetaDataTextNode) parserV3.parseQuery( "filetype:java" );

        // Assert
        String result = node.getContent();
        assertThat( result, equalTo( "java" ) );
    }

    @Test
    public void testParseQuery_testMetaDataFiletypeIsExacltyJava_returnsMetaData() {
        // Arrange
        QueryParserV3 parserV3 = new QueryParserV3();

        // Act
        QueryNode result = parserV3.parseQuery( "filetype:\"java\"" );

        // Assert
        assertThat( result, is( instanceOf( MetaDataTextNode.class ) ) );
    }

    @Test
    public void testParseQuery_EmptyString_expectASTSerializationIsOnlyEmptyNode() {
        // Arrange
        QueryParserV3 queryParser = new QueryParserV3();

        // Act
        QueryNode result = queryParser.parseQuery( "" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'EMPTY' ]" ) );
    }

    @Test
    public void testParseQuery_nullAsString_expectASTSerializationIsOnlyEmptyNode() {
        // Arrange
        QueryParserV3 queryParser = new QueryParserV3();

        // Act
        QueryNode result = queryParser.parseQuery( null );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'EMPTY' ]" ) );
    }

    @Test
    public void testParseQuery_PlusAndOneWord_expectASTSerialization() {
        // Arrange
        QueryParserV3 queryParser = new QueryParserV3();

        // Act
        QueryNode result = queryParser.parseQuery( "+first" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'AND', [ [ 'INCLUDING', [ [ 'TEXT', 'first' ] ] ] ] ]" ) );
    }

    @Test
    public void testParseQuery_MinusAndOneWord_expectASTSerialization() {
        // Arrange
        QueryParserV3 queryParser = new QueryParserV3();

        // Act
        QueryNode result = queryParser.parseQuery( "-first" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'AND', [ [ 'EXCLUDING', [ [ 'TEXT', 'first' ] ] ] ] ]" ) );
    }

    // TODO: OR of two terms (result.toString -> equalto)
    @Test
    public void testParseQuery_TwoWordsOR_expectASTSerialization() {
        // Arrange
        QueryParserV3 queryParser = new QueryParserV3();

        // Act
        QueryNode result = queryParser.parseQuery( "first second" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'OR', [ [ 'INCLUDING', [ [ 'TEXT', 'first' ] ] ], [ 'INCLUDING', [ [ 'TEXT', 'second' ] ] ] ] ]" ) );
    }

    // TODO: AND of two terms (result.toString -> equalto) (INCLUDING)
    @Disabled
    @Test
    public void testParseQuery_TwoWordsAND_expectOnlyASTSerializationOfFirstElement() {
        // Arrange
        QueryParserV3 queryParser = new QueryParserV3();

        // Act
        QueryNode result = queryParser.parseQuery( "first +second" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'AND', [ [ 'INCLUDING', [ [ 'TEXT', 'first' ] ] ], [ 'INCLUDING', [ [ 'TEXT', 'second' ] ] ] ] ]" ) );
    }

    // TODO: AND of two terms (result.toString -> equalto) (EXCLUDING)
    @Disabled
    @Test
    public void testParseQuery_TwoWordsButNotSecond_expectOnlyASTSerializationOfFirstElement() {
        // Arrange
        QueryParserV3 queryParser = new QueryParserV3();

        // Act
        QueryNode result = queryParser.parseQuery( "first -second" );

        // Assert
        assertThat( result.toString(), equalTo( "[ 'AND', [ [ 'INCLUDING', [ [ 'TEXT', 'first' ] ] ], [ 'EXCLUDING', [ [ 'TEXT', 'second' ] ] ] ] ]" ) );
    }

}
