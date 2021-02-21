package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.core.ast.EmptyCoreNode;
import de.mindscan.furiousiron.query.ast.EmptyNode;

public class CoreSearchCompilerTest {

    @Test
        public void testCompile_inputTreeIsNull_expectEmptyCoreNode() {
            // arrange
    
            // act
            CoreQueryNode result = CoreSearchCompiler.compile( null );
    
            // assert
            assertThat( result, is( instanceOf( EmptyCoreNode.class ) ) );
    
        }

    @Test
        public void testCompile_inputTreeIsEmptyNode_expectEmptyCoreNode() {
            // arrange
    
            // act
            CoreQueryNode result = CoreSearchCompiler.compile( new EmptyNode() );
    
            // assert
            assertThat( result, is( instanceOf( EmptyCoreNode.class ) ) );
        }

}
