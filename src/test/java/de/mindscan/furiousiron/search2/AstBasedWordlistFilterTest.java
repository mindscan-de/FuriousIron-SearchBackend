package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.query.ast.QueryNode;

public class AstBasedWordlistFilterTest {

    @Test
    public void testIsAstMatchingToWordlist_SingleWordContainedInWordlist_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "package" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_SingleWordNotContainedInWordlist_returnsFalse() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "packageXXX" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_SingleWordContainedInWordlistPartially_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        // part of "package"
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "ackage" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_SingleWordContainedInWordlistPartiallyANDUPPERCASE_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();

        // part of "package" - The word we are looking for is always in lowercase in the wordlists / this is part of the realtree.
        // TODO: has to be incorporated....
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "ACKAGE".toLowerCase() );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothContained_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "+package +import" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothOneContained_returnsFalse() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "+package +importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothNotContained_returnsFalse() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "+packagexx +importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoSupressedWordsBothNotContained_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "-packagexxx -importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsContainedAsOr_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "package import" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsSecondContainedAsOr_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "packagexxx import" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsFirstContainedAsOr_returnsTrue() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "package importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothNotContainedAsOr_returnsFalse() throws Exception {
        // arrange
        SearchQueryExecutorV2 queryExecutor2 = new SearchQueryExecutorV2();
        QueryNode ast = queryExecutor2.compileSearchTreeFromQuery( "packagexxx importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = AstBasedWordlistFilter.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    private List<String> buildWordlist() {
        return Arrays.asList( "package", "org", "common", "import", "java", "nio", "charset", "util", "resourcebundle", "junit", "test", "public", "class",
                        "resourcebundlereadtest", "@test", "void", "bundle", "getbundle", "\"org", "exception", "new", "resourcebundlecontrol", "forname",
                        "\"utf", "string", "value", "getstring", "\"err", "00110\"", "system", "out", "println" );
    }

}
