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
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class EmptyNodeTest {

    @Test
    public void testGetContent_CtorOnly_returnsEmptyStringValue() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        String result = node.getContent();

        // assert
        assertThat( result, is( emptyString() ) );
    }

    @Test
    public void testHasChildren_CtorOnly_returnsFalse() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testGetChildren_CtorOnly_returnsNonNullValue() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, not( nullValue() ) );
    }

    @Test
    public void testGetChildren_CtorOnly_returnsEmptyList() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testToString_CtorOnly_returnsSerializationOfEmptyNode() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        String result = node.toString();

        // assert
        assertThat( result, equalTo( "[ 'EMPTY' ]" ) );
    }

}
