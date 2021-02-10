package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.search2.corequery.ast.CoreQueryNode;
import de.mindscan.furiousiron.search2.corequery.ast.EmptyCoreNode;

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

}
