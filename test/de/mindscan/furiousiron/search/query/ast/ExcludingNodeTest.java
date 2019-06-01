package de.mindscan.furiousiron.search.query.ast;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

public class ExcludingNodeTest {

    @Test
    public void testHasChildren_CtorOnly_returnsTrue() throws Exception {
        // arrange
        QueryNode node = new ExcludingNode( new EmptyNode() );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testGetChildren_CtorOnlyWithEmptyNode_containsSameInstance() throws Exception {
        // arrange
        QueryNode innerNode = new EmptyNode();
        QueryNode node = new ExcludingNode( innerNode );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, contains( innerNode ) );
    }

    @Test
    public void testGetChildren_CtorOnlyWithTextNode_containsSameInstance() throws Exception {
        // arrange
        QueryNode innerNode = new TextNode( "test" );
        QueryNode node = new ExcludingNode( innerNode );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, contains( innerNode ) );
    }

    @Test
    public void testToString_CtorOnlyWithEmptyNode_containsEmptyNode() throws Exception {
        // arrange
        QueryNode innerNode = new EmptyNode();
        QueryNode node = new ExcludingNode( innerNode );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'EXCLUDING', [ [ 'EMPTY' ] ] ]" ) );
    }

    @Test
    public void testToString_CtorOnlyWithTextNode_containsTextNode() throws Exception {
        // arrange
        QueryNode innerNode = new TextNode( "test" );
        QueryNode node = new ExcludingNode( innerNode );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'EXCLUDING', [ [ 'TEXT', 'test' ] ] ]" ) );
    }

}
