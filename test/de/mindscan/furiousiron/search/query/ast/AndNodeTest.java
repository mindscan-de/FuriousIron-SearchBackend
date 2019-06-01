/**
 *
 * MIT License
 *
 * Copyright (c) 2019 Maxim Gansert, Mindscan
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
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

public class AndNodeTest {

    @Test
    public void testGetContent_EmptyCtor_returnsEmptyString() throws Exception {
        // arrange
        AndNode node = new AndNode();

        // act
        String result = node.getContent();

        // assert
        assertThat( result, isEmptyString() );
    }

    @Test
    public void testHasChildren_EmptyCtor_returnsFalse() throws Exception {
        // arrange
        AndNode node = new AndNode();

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testHasChildren_CtorWithEmptyList_returnsFalse() throws Exception {
        // arrange
        AndNode node = new AndNode( Collections.emptyList() );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testHasChildren_CtorWithNonEmptyList_returnsTrue() throws Exception {
        List<QueryNode> nonEmptyList = createListWithTestTextNode();

        AndNode node = new AndNode( nonEmptyList );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testGetChildren_CtorWithNonEmptyList_returnsDifferentInstance() throws Exception {
        List<QueryNode> nonEmptyList = createListWithTestTextNode();

        AndNode node = new AndNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, is( not( sameInstance( nonEmptyList ) ) ) );
    }

    @Test
    public void testGetChildren_CtorWithListContainingOneElement_returnsAListWithOneElement() throws Exception {
        List<QueryNode> nonEmptyList = createListWithTestTextNode();

        AndNode node = new AndNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, hasSize( 1 ) );
    }

    @Test
    public void testGetChildren_CtorWithListContainingTwoElements_returnsAListWithTwoElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithFirstAndSecondTextNode();

        AndNode node = new AndNode( nonEmptyList );

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

        AndNode node = new AndNode( nonEmptyList );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, contains( textNode, textNode2 ) );
    }

    @Test
    public void testToString_CtorOnly_expectAndNodeWithEmptyChildList() throws Exception {
        // arrange
        AndNode node = new AndNode();

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'AND', [  ] ]" ) );
    }

    @Test
    public void testToString_CtorWithListContainingOneEmptyElement_expectAndNodeWithEmptyElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = new ArrayList<>();
        nonEmptyList.add( new EmptyNode() );

        AndNode node = new AndNode( nonEmptyList );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'AND', [ [ 'EMPTY' ] ] ]" ) );
    }

    @Test
    public void testToString_CtorWithListContainingOneTestElement_expectAndNodeWithTestTextElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithTestTextNode();

        AndNode node = new AndNode( nonEmptyList );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'AND', [ [ 'TEXT', 'test' ] ] ]" ) );
    }

    @Test
    public void testToString_CtorWithListContainingTwoElements_expectAndNodeWithFirstAndSecondTextElement() throws Exception {
        // arrange
        List<QueryNode> nonEmptyList = createListWithFirstAndSecondTextNode();

        AndNode node = new AndNode( nonEmptyList );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'AND', [ [ 'TEXT', 'first' ], [ 'TEXT', 'second' ] ] ]" ) );
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
