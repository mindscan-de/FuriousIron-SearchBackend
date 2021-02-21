package de.mindscan.furiousiron.search.query.executor;

import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.query.ast.TextNode;
import de.mindscan.furiousiron.search.Search;

public class QueryExecutorTest {

    private static final String SEARCHTERM1 = "searchterm1";
    private static final String SEARCHTERM2 = "searchterm2";

    @Test
    public void testExecute_ExectureWithTextNodeSearchterm1_expectSearchToBeCalledWithSearchTerm1() throws Exception {
        // arrange
        QueryNode ast = new TextNode( SEARCHTERM1 );
        Search search = Mockito.mock( Search.class, "search" );

        // act
        QueryExecutor.execute( search, ast );

        // assert
        Mockito.verify( search, times( 1 ) ).searchToMap( SEARCHTERM1 );
    }

    @Test
    public void testExecute_ExectureWithTextNodeSearchterm2_expectSearchToBeCalledWithSearchTerm2() throws Exception {
        // arrange
        QueryNode ast = new TextNode( SEARCHTERM2 );
        Search search = Mockito.mock( Search.class, "search" );

        // act
        QueryExecutor.execute( search, ast );

        // assert
        Mockito.verify( search, times( 1 ) ).searchToMap( SEARCHTERM2 );
    }

}
