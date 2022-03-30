package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.query.ast.EmptyNode;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.parser.QueryParserV3;

public class SearchQueryTextTokenCollectorTest {

    @Test
    public void testCollectAllTextTokens_null_returnsEmptyList() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();

        // act
        Collection<String> result = collector.collectAllTextTokens( null );

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testCollectAllTextTokens_EmptyNode_returnsEmptyList() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();

        // act
        Collection<String> result = collector.collectAllTextTokens( new EmptyNode() );

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testCollectAllTextTokens_QueryStringOneWord_returnsListOfLengthOne() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryParserV3 parser = new QueryParserV3();
        QueryNode parsedAST = parser.parseQuery( "first" );

        // act
        Collection<String> result = collector.collectAllTextTokens( parsedAST );

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testCollectAllTextTokens_QueryStringPlusOneWord_returnsListOfLengthOne() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryParserV3 parser = new QueryParserV3();
        QueryNode parsedAST = parser.parseQuery( "+first" );

        // act
        Collection<String> result = collector.collectAllTextTokens( parsedAST );

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testCollectAllTextTokens_QueryStringMinusOneWord_returnsListOfLengthOne() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryParserV3 parser = new QueryParserV3();
        QueryNode parsedAST = parser.parseQuery( "-first" );

        // act
        Collection<String> result = collector.collectAllTextTokens( parsedAST );

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testCollectAllTextTokens_QueryStringOneMetadataProperty_returnsListOfLengthOne() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryParserV3 parser = new QueryParserV3();
        QueryNode parsedAST = parser.parseQuery( "language:java" );

        // act
        Collection<String> result = collector.collectAllTextTokens( parsedAST );

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testCollectAllTextTokens_QueryStringOneExactText_returnsListOfLengthOne() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryParserV3 parser = new QueryParserV3();
        QueryNode parsedAST = parser.parseQuery( "\"java\"" );

        // act
        Collection<String> result = collector.collectAllTextTokens( parsedAST );

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testCollectAllTextTokens_QueryStringTwoWords_returnsListOfLengthTwo() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryParserV3 parser = new QueryParserV3();
        QueryNode parsedAST = parser.parseQuery( "first second" );

        // act
        Collection<String> result = collector.collectAllTextTokens( parsedAST );

        // assert
        assertThat( result, hasSize( 2 ) );
    }

    @Test
    public void testCollectAllTextTokens_unknownQueryNodeTypeWithoutChildren_ThrowsException() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryNode unknownQueryNodeTypeWithNoChildren = new QueryNode() {
            @Override
            public boolean hasChildren() {
                return false;
            }

            @Override
            public String getContent() {
                return null;
            }

            @Override
            public Collection<QueryNode> getChildren() {
                return null;
            }
        };

        // act + assert
        assertThrows( RuntimeException.class, () -> {
            collector.collectAllTextTokens( unknownQueryNodeTypeWithNoChildren );
        } );
    }

    @Test
    public void testCollectAllTextTokens_unknownQueryNodeTypeWithChildren_ThrowsException() throws Exception {
        // arrange
        SearchQueryTextTokenCollector collector = new SearchQueryTextTokenCollector();
        QueryNode unknownQueryNodeTypeWithChildren = new QueryNode() {
            @Override
            public boolean hasChildren() {
                return true;
            }

            @Override
            public String getContent() {
                return null;
            }

            @Override
            public Collection<QueryNode> getChildren() {
                return null;
            }
        };

        // act + assert
        assertThrows( RuntimeException.class, () -> {
            collector.collectAllTextTokens( unknownQueryNodeTypeWithChildren );
        } );
    }

}
