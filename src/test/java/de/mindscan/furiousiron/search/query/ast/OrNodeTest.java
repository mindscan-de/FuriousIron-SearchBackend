package de.mindscan.furiousiron.search.query.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class OrNodeTest {

    @Test
    public void testGetContent_EmptyCtor_returnsEmptyString() throws Exception {
        // arrange
        OrNode node = new OrNode();

        // act
        String result = node.getContent();

        // assert
        assertThat( result, is( emptyString() ) );
    }

    @Test
    public void testHasChildren_EmptyCtor_returnsFalse() throws Exception {
        // arrange
        OrNode node = new OrNode();

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testHasChildren_CtorWithEmptyList_returnsFalse() throws Exception {
        // arrange
        OrNode node = new OrNode( Collections.emptyList() );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testHasChildren_CtorWithNonEmptyList_returnsTrue() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithTestTextNode();
        OrNode node = new OrNode( nonEmptyList );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testGetChildren_CtorWithNonEmptyList_returnsDifferentInstance() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithTestTextNode();
        OrNode node = new OrNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, is( not( sameInstance( nonEmptyList ) ) ) );
    }

    @Test
    public void testGetChildren_CtorWithListContainingOneElement_returnsAListWithOneElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithTestTextNode();
        OrNode node = new OrNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testGetChildren_CtorWithListContainingTwoElements_returnsAListWithTwoElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithFirstAndSecondTextNode();
        OrNode node = new OrNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, hasSize( 2 ) );
    }

    @Test
    public void testGetChildren_CtorWithListContainingTwoElements_returnsAListWithBothElements() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = new ArrayList<>();
        TextNode textNode = new TextNode( "first" );
        TextNode textNode2 = new TextNode( "second" );
        nonEmptyList.add( textNode );
        nonEmptyList.add( textNode2 );

        OrNode node = new OrNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, contains( textNode, textNode2 ) );
    }

    @Test
    public void testToString_CtorOnly_expectOrNodeWithEmptyChildList() throws Exception {
        // arrange
        OrNode node = new OrNode();

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'OR', [  ] ]" ) );
    }

    @Test
    public void testToString_CtorWithListContainingOneEmptyElement_expectOrNodeWithEmptyElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = new ArrayList<>();
        nonEmptyList.add( new EmptyNode() );

        OrNode node = new OrNode( nonEmptyList );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'OR', [ [ 'EMPTY' ] ] ]" ) );
    }

    @Test
    public void testToString_CtorWithListContainingOneTestElement_expectOrNodeWithTestTextElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithTestTextNode();

        OrNode node = new OrNode( nonEmptyList );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'OR', [ [ 'TEXT', 'test' ] ] ]" ) );
    }

    @Test
    public void testToString_CtorWithListContainingTwoElements_expectOrNodeWithFirstAndSecondTextElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithFirstAndSecondTextNode();

        OrNode node = new OrNode( nonEmptyList );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'OR', [ [ 'TEXT', 'first' ], [ 'TEXT', 'second' ] ] ]" ) );
    }

    private List<QueryNode> createListWithTestTextNode() {
        List<QueryNode> nonEmptyList = new ArrayList<>();
        nonEmptyList.add( new TextNode( "test" ) );
        return nonEmptyList;
    }

    private List<QueryNode> createListWithFirstAndSecondTextNode() {
        List<QueryNode> nonEmptyList = new ArrayList<>();
        nonEmptyList.add( new TextNode( "first" ) );
        nonEmptyList.add( new TextNode( "second" ) );
        return nonEmptyList;
    }

}
