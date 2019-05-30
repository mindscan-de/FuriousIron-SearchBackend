package de.mindscan.furiousiron.search.query.tokenizer;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class QueryTokenizerTest {

    @Test
    public void testTokenize_emptyString_returnsEmptyTokenList() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "" );

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testTokenize_containsOnePlus_returnsNonEmptyTokenList() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "+" );

        // assert
        assertThat( result, not( empty() ) );
    }

    @Test
    public void testTokenize_containsOneMinus_returnsNonEmptyTokenList() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "-" );

        // assert
        assertThat( result, not( empty() ) );
    }

}
