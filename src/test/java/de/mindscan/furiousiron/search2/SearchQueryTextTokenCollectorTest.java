package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.query.ast.EmptyNode;

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

}
