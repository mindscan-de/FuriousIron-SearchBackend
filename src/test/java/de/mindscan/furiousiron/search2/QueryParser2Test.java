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

import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search2.corequery.ast.CoreQueryNode;
import de.mindscan.furiousiron.search2.corequery.ast.EmptyCoreNode;
import de.mindscan.furiousiron.search2.corequery.ast.TrigramsCoreNode;

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
        System.out.println( ast );

        // assert
        Collection<String> trigrams = result.getTrigrams();
        assertThat( trigrams, containsInAnyOrder(
                        // searchquery
                        "sea", "ear", "arc", "rch", "chq", "hqu", "que", "uer", "ery",
                        // performance
                        "per", "erf", "rfo", "for", "orm", "rma", "man", "anc", "nce" ) );
    }

    @Test
    public void testMatchWordlistToAst_SingleWordContainedInWordlist_returnsTrue() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "package" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.matchWordlistToAst( ast, wordlist );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testMatchWordlistToAst_SingleWordNotContainedInWordlist_returnsFalse() throws Exception {
        // arrange
        QueryParser2 parser2 = new QueryParser2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "packageXXX" );

        List<String> wordlist = buildWordlist();

        // act
        boolean result = parser2.matchWordlistToAst( ast, wordlist );

        // assert
        assertThat( result, equalTo( false ) );
    }

    private List<String> buildWordlist() {
        return Arrays.asList( "package", "org", "common", "import", "java", "nio", "charset", "util", "resourcebundle", "junit", "test", "public", "class",
                        "resourcebundlereadtest", "@test", "void", "bundle", "getbundle", "\"org", "exception", "new", "resourcebundlecontrol", "forname",
                        "\"utf", "string", "value", "getstring", "\"err", "00110\"", "system", "out", "println" );
    }

}
