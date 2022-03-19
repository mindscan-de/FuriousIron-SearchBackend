package de.mindscan.furiousiron.search.query.tokenizer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.util.List;

import org.junit.Test;

import de.mindscan.furiousiron.search.query.token.SearchQueryToken;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenImpl;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokenType;
import de.mindscan.furiousiron.search.query.token.SearchQueryTokens;

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

    @Test
    public void testParse_PlusTestQueryString_containsPlusTokenAndTextTokenInList() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "+test" );

        // assert
        assertThat( result, contains( SearchQueryTokens.OPERATOR_PLUS, textToken( "test" ) ) );
    }

    @Test
    public void testParse_MinusTestQueryString_TwoTokensInTokenList() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "-test" );

        // assert
        assertThat( result, hasSize( 2 ) );
    }

    @Test
    public void testParse_MinusTestQueryString_containsMinusTokenAndTextTokenInList() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "-test" );

        // assert
        assertThat( result, contains( SearchQueryTokens.OPERATOR_MINUS, textToken( "test" ) ) );
    }

    @Test
    public void testParse_PlusTestExactQueryString_containsPLusTokenAndExactTextTokenInList() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "+\"test\"" );

        // assert
        assertThat( result, contains( SearchQueryTokens.OPERATOR_PLUS, exactTextToken( "test" ) ) );
    }

    @Test
    public void testParse_OpenCloseParenthesis_containsTwoTokens() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "()" );

        // assert
        assertThat( result, hasSize( 2 ) );
    }

    @Test
    public void testParse_PlusTestinusExactQueryString_containsPLusTokenTestAndMinusTokenExactText() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "+test -\"test\"" );

        // assert
        assertThat( result, contains( SearchQueryTokens.OPERATOR_PLUS, textToken( "test" ), SearchQueryTokens.OPERATOR_MINUS, exactTextToken( "test" ) ) );
    }

    @Test
    public void testParse_Plus1234Minus123456QueryString_containsPLusToken1234AndMinus123456Token() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "+1234 -123456" );

        // assert
        assertThat( result, contains( SearchQueryTokens.OPERATOR_PLUS, textToken( "1234" ), SearchQueryTokens.OPERATOR_MINUS, textToken( "123456" ) ) );
    }

    @Test
    public void testParse_PlusTestNotPythonFile_containsPlusTokenTestAndMinusFiletypeTyphon() throws Exception {
        // arrange
        SearchQueryTokenizerImpl tokenizer = new SearchQueryTokenizerImpl();

        // act
        List<SearchQueryToken> result = tokenizer.parse( "+test -filetype:python" );

        // assert
        assertThat( result, contains( SearchQueryTokens.OPERATOR_PLUS, textToken( "test" ), SearchQueryTokens.OPERATOR_MINUS, textToken( "filetype" ),
                        SearchQueryTokens.OPERATOR_DOUBLECOLON, textToken( "python" ) ) );
    }

    private SearchQueryTokenImpl textToken( String value ) {
        return new SearchQueryTokenImpl( SearchQueryTokenType.SEARCHTERM, value );
    }

    private SearchQueryTokenImpl exactTextToken( String value ) {
        return new SearchQueryTokenImpl( SearchQueryTokenType.EXACTSEARCHTERM, value );
    }

}
