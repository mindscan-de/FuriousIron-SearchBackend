package de.mindscan.furiousiron.search.query.ast;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class OrNodeTest {

    @Test
    public void testGetContent_EmptyCtor_returnsEmptyString() throws Exception {
        // arrange
        OrNode node = new OrNode();

        // act
        String result = node.getContent();

        // assert
        assertThat( result, isEmptyString() );
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
        List<QueryNode> nonEmptyList = new ArrayList<>();
        nonEmptyList.add( new TextNode( "test" ) );

        OrNode node = new OrNode( nonEmptyList );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testGetChildren_CtorWithNonEmptyList_returnsDifferentInstance() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = new ArrayList<>();
        TextNode textNode = new TextNode( "test" );
        nonEmptyList.add( textNode );

        OrNode node = new OrNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, is( not( sameInstance( nonEmptyList ) ) ) );
    }

    @Test
    public void testGetChildren_CtorWithListContainingOneElement_returnsAListWithOneElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = new ArrayList<>();
        TextNode textNode = new TextNode( "test" );
        nonEmptyList.add( textNode );

        OrNode node = new OrNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testGetChildren_CtorWithListContainingTwoElements_returnsAListWithTwoElement() throws Exception {
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

}
