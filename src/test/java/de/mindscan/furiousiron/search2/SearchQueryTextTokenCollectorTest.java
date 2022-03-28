package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Collection;

import org.junit.jupiter.api.Test;

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

}
