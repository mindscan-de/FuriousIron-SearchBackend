package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.core.ast.EmptyCoreNode;
import de.mindscan.furiousiron.search.query.ast.EmptyNode;

public class CompileCoreSearchTest {

    @Test
    public void testCompileCoreSearch_inputTreeIsNull_expectEmptyCoreNode() {
        // arrange

        // act
        CoreQueryNode result = CompileCoreSearch.compileCoreSearch( null );

        // assert
        assertThat( result, is( instanceOf( EmptyCoreNode.class ) ) );

    }

    @Test
    public void testCompileCoreSearch_inputTreeIsEmptyNode_expectEmptyCoreNode() {
        // arrange

        // act
        CoreQueryNode result = CompileCoreSearch.compileCoreSearch( new EmptyNode() );

        // assert
        assertThat( result, is( instanceOf( EmptyCoreNode.class ) ) );
    }

}
