package de.mindscan.furiousiron.search.query.tokenizer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import org.junit.Test;

import de.mindscan.furiousiron.search.query.token.SearchQueryToken;

public class SearchQueryTokenizerImplTest {

    @Test
    public void testParse_emptyQueryString_emptyTokenList() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "" );

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testParse_PlusTestQueryString_TwoTokensInTokenList() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "+test" );

        // assert
        assertThat( result, hasSize( 2 ) );
    }

}
