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

package de.mindscan.furiousiron.search.query.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.ast.TextNode;

public class QueryParserTest {

    @Test
    public void testParseQuery_emptyString_returnsEmptyNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "" );

        // Assert
        assertThat( result, is( instanceOf( EmptyNode.class ) ) );
    }

    @Test
    public void testParseQuery_nullAsString_returnsEmptyNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( null );

        // Assert
        assertThat( result, is( instanceOf( EmptyNode.class ) ) );
    }

    @Test
    public void testParseQuery_StringContainsWordTest_returnsTextNode() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "test" );

        // Assert
        assertThat( result, is( instanceOf( TextNode.class ) ) );
    }

    @Test
    public void testParseQuery_StringContainsWordTest_returnsTextNodeContainsHasWordTest() {
        // Arrange
        QueryParser queryParser = new QueryParser();

        // Act
        QueryNode result = queryParser.parseQuery( "test" );

        // Assert
        String content = result.getContent();
        assertThat( content, equalTo( "test" ) );
    }

// tpxu_method
}
