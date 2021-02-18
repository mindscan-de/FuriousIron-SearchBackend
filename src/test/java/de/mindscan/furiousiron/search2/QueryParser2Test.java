package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.core.ast.EmptyCoreNode;
import de.mindscan.furiousiron.core.ast.TrigramsCoreNode;
import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;

public class QueryParser2Test {

    @Test
    public void testCompileCoreSearch_inputTreeIsNull_expectEmptyCoreNode() {
        // arrange
        QueryParser2 parser2 = new QueryParser2();

        // act
        CoreQueryNode result = parser2.compileCoreSearch( null );

        // assert
        assertThat( result, is( instanceOf( EmptyCoreNode.class ) ) );
    }

    @Test
    public void testCompileCoreSearch_inputTreeIsEmptyNode_expectEmptyCoreNode() {
        // arrange
        QueryParser2 parser2 = new QueryParser2();

        // act
        CoreQueryNode result = parser2.compileCoreSearch( new EmptyNode() );

        // assert
        assertThat( result, is( instanceOf( EmptyCoreNode.class ) ) );
    }

    @Test
    public void testCompileCoreSearch_OneSearchQuerySearchTerm_expectTrigramsCoreNode() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "searchquery" );

        // act
        CoreQueryNode result = parser2.compileCoreSearch( ast );

        // assert
        assertThat( result, is( instanceOf( TrigramsCoreNode.class ) ) );
    }

    @Test
    public void testCompileCoreSearch_TwoSearchQuerySearchTerms_expect() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "+searchquery +performance" );

        // act
        CoreQueryNode result = parser2.compileCoreSearch( ast );

        // assert
        Collection<String> trigrams = result.getTrigrams();
        assertThat( trigrams, containsInAnyOrder(
                        // searchquery
                        "sea", "ear", "arc", "rch", "chq", "hqu", "que", "uer", "ery",
                        // performance
                        "per", "erf", "rfo", "for", "orm", "rma", "man", "anc", "nce" ) );
    }

    @Test
    public void testCompileCoreSearch_TwoSearchQuerySearchTermsMixedCase_expect() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        // problem is, we actually have mixed case here...
        QueryNode ast = parser2.compileSearchTreeFromQuery( "+SEARCHquery +PERFORMance" );

        // act
        CoreQueryNode result = parser2.compileCoreSearch( ast );

        // assert
        Collection<String> trigrams = result.getTrigrams();
        assertThat( trigrams, containsInAnyOrder(
                        // searchquery
                        "sea", "ear", "arc", "rch", "chq", "hqu", "que", "uer", "ery",
                        // performance
                        "per", "erf", "rfo", "for", "orm", "rma", "man", "anc", "nce" ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_SingleWordContainedInWordlist_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "package" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_SingleWordNotContainedInWordlist_returnsFalse() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "packageXXX" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_SingleWordContainedInWordlistPartially_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        // part of "package"
        QueryNode ast = parser2.compileSearchTreeFromQuery( "ackage" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_SingleWordContainedInWordlistPartiallyANDUPPERCASE_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();

        // part of "package" - The word we are looking for is always in lowercase in the wordlists / this is part of the realtree.
        // TODO: has to be incorporated....
        QueryNode ast = parser2.compileSearchTreeFromQuery( "ACKAGE".toLowerCase() );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothContained_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "+package +import" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothOneContained_returnsFalse() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "+package +importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothNotContained_returnsFalse() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "+packagexx +importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoSupressedWordsBothNotContained_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "-packagexxx -importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsContainedAsOr_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "package import" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsSecondContainedAsOr_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "packagexxx import" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsFirstContainedAsOr_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "package importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsAstMatchingToWordlist_TwoWordsBothNotContainedAsOr_returnsFalse() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "packagexxx importxxx" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.isAstMatchingToWordlist( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    private List<String> buildWordlist() {
        return Arrays.asList( "package", "org", "common", "import", "java", "nio", "charset", "util", "resourcebundle", "junit", "test", "public", "class",
                        "resourcebundlereadtest", "@test", "void", "bundle", "getbundle", "\"org", "exception", "new", "resourcebundlecontrol", "forname",
                        "\"utf", "string", "value", "getstring", "\"err", "00110\"", "system", "out", "println" );
    }

}
