package de.mindscan.furiousiron.search.query.tokenizer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.util.List;

import org.junit.jupiter.api.Test;

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
    public void testTokenize_containsOnePlus_listContainingAPlusToken() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "+" );

        // assert
        assertThat( result, contains( instanceOf( PlusQueryToken.class ) ) );
    }

    @Test
    public void testTokenize_containsOneMinus_returnsNonEmptyTokenList() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "-" );

        // assert
        assertThat( result, not( empty() ) );
    }

    @Test
    public void testTokenize_containsOneMinus_ListContainsAMinusToken() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "-" );

        // assert
        assertThat( result, contains( instanceOf( MinusQueryToken.class ) ) );
    }

    @Test
    public void testTokenize_containsOneWord_ListContainsATextQueryToken() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "one" );

        // assert
        assertThat( result, contains( instanceOf( TextQueryToken.class ) ) );
    }

    @Test
    public void testTokenize_containsTwoWords_ListContainsTwoTextQueryToken() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "one two" );

        // assert
        assertThat( result, contains( instanceOf( TextQueryToken.class ), instanceOf( TextQueryToken.class ) ) );
    }

    @Test
    public void testTokenize_containsTwoWordsAndPlus_ListContainsTwoTextTokenAndOnePlusToken() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "one +two" );

        // assert
        assertThat( result, contains( instanceOf( TextQueryToken.class ), instanceOf( PlusQueryToken.class ), instanceOf( TextQueryToken.class ) ) );
    }

    @Test
    public void testTokenize_containsThreeWordsAndPlusAndMinus_ListContainsTwoTextTokenAndOnePlusToken() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "one +two -three" );

        // assert
        assertThat( result, contains( instanceOf( TextQueryToken.class ), instanceOf( PlusQueryToken.class ), instanceOf( TextQueryToken.class ),
                        instanceOf( MinusQueryToken.class ), instanceOf( TextQueryToken.class ) ) );
    }

    @Test
    public void testTokenize_containsTest_ListContainsTwoTextTokenAndOnePlusToken() throws Exception {
        // arrange

        // act
        List<QueryToken> result = QueryTokenizer.tokenize( "test -\"test\"" );

        // assert
        assertThat( result, contains( instanceOf( TextQueryToken.class ), instanceOf( MinusQueryToken.class ), instanceOf( TextQueryToken.class ) ) );
    }

}
