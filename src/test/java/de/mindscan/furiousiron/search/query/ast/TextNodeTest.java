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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class TextNodeTest {

    @Test
    public void testHasChildren_CtorOnlyEmptyWord_returnsFalse() throws Exception {
        // arrange
        TextNode node = new TextNode( "" );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testGetChildren_CtorOnlyEmptyWord_returnsEmptyCollection() throws Exception {
        // arrange
        TextNode node = new TextNode( "" );

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testGetContent_CtorWithStringAAA_returnsAAA() throws Exception {
        // arrange
        TextNode node = new TextNode( "AAA" );

        // act
        String result = node.getContent();

        // assert
        assertThat( result, equalTo( "AAA" ) );
    }

    @Test
    public void testGetContent_CtorWithStringBBB_returnsBBB() throws Exception {
        // arrange
        TextNode node = new TextNode( "BBB" );

        // act
        String result = node.getContent();

        // assert
        assertThat( result, equalTo( "BBB" ) );
    }

    @Test
    public void testToString_CtorWithEmptyWord_() throws Exception {
        // arrange
        TextNode node = new TextNode( "" );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'TEXT', '' ]" ) );
    }

    @Test
    public void testToString_CtorWithAAAWord_() throws Exception {
        // arrange
        TextNode node = new TextNode( "AAA" );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'TEXT', 'AAA' ]" ) );
    }

    @Test
    public void testToString_CtorWithBBBWord_() throws Exception {
        // arrange
        TextNode node = new TextNode( "BBB" );

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'TEXT', 'BBB' ]" ) );
    }

}
