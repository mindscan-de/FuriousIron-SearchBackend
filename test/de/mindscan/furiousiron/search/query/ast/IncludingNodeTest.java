package de.mindscan.furiousiron.search.query.ast;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

public class IncludingNodeTest {

    @Test
    public void testHasChildren_CtorOnly_returnsTrue() throws Exception {
        // arrange
        QueryNode node = new IncludingNode( new EmptyNode() );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testGetChildren_CtorOnlyWithEmptyNode_containsSameInstance() throws Exception {
        // arrange
        QueryNode innerNode = new EmptyNode();
        QueryNode node = new IncludingNode( innerNode );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, contains( innerNode ) );
    }

    @Test
    public void testGetChildren_CtorOnlyWithTextNode_containsSameInstance() throws Exception {
        // arrange
        QueryNode innerNode = new TextNode( "test" );
        IncludingNode node = new IncludingNode( innerNode );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, contains( innerNode ) );
    }

    @Test
    public void testToString_CtorOnlyWithEmptyNode_containsEmptyNode() throws Exception {
        // arrange
        QueryNode innerNode = new EmptyNode();
        QueryNode node = new IncludingNode( innerNode );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'INCLUDING', [ [ 'EMPTY' ] ] ]" ) );
    }

    @Test
    public void testToString_CtorOnlyWithTextNode_containsTextNode() throws Exception {
        // arrange
        QueryNode innerNode = new TextNode( "test" );
        QueryNode node = new IncludingNode( innerNode );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'INCLUDING', [ [ 'TEXT', 'test' ] ] ]" ) );
    }

}
