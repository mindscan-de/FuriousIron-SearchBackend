package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.core.CoreSearchCompiler;
import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.core.ast.TrigramsCoreNode;
import de.mindscan.furiousiron.query.ast.QueryNode;

public class SearchQueryExecutorV2Test {

    @Test
    public void testCompile_OneSearchQuerySearchTerm_expectTrigramsCoreNode() throws Exception {
        // arrange
        SearchQueryExecutorV2 parser2 = new SearchQueryExecutorV2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "searchquery" );

        // act
        CoreQueryNode result = CoreSearchCompiler.compile( ast );

        // assert
        assertThat( result, is( instanceOf( TrigramsCoreNode.class ) ) );
    }

    @Test
    public void testCompile_TwoSearchQuerySearchTerms_expect() throws Exception {
        // arrange
        SearchQueryExecutorV2 parser2 = new SearchQueryExecutorV2();
        QueryNode ast = parser2.compileSearchTreeFromQuery( "+searchquery +performance" );

        // act
        CoreQueryNode result = CoreSearchCompiler.compile( ast );

        // assert
        Collection<String> trigrams = result.getTrigrams();
        assertThat( trigrams, containsInAnyOrder(
                        // searchquery
                        "sea", "ear", "arc", "rch", "chq", "hqu", "que", "uer", "ery",
                        // performance
                        "per", "erf", "rfo", "for", "orm", "rma", "man", "anc", "nce" ) );
    }

    @Test
    public void testCompile_TwoSearchQuerySearchTermsMixedCase_expect() throws Exception {
        // arrange
        SearchQueryExecutorV2 parser2 = new SearchQueryExecutorV2();
        // problem is, we actually have mixed case here...
        QueryNode ast = parser2.compileSearchTreeFromQuery( "+SEARCHquery +PERFORMance" );

        // act
        CoreQueryNode result = CoreSearchCompiler.compile( ast );

        // assert
        Collection<String> trigrams = result.getTrigrams();
        assertThat( trigrams, containsInAnyOrder(
                        // searchquery
                        "sea", "ear", "arc", "rch", "chq", "hqu", "que", "uer", "ery",
                        // performance
                        "per", "erf", "rfo", "for", "orm", "rma", "man", "anc", "nce" ) );
    }

}
