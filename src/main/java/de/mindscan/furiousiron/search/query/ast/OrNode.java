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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 */
public class OrNode implements QueryNode {

    private List<QueryNode> children;

    /**
     * 
     */
    public OrNode() {
        children = new ArrayList<>();
    }

    public OrNode( List<QueryNode> nodes ) {
        children = new ArrayList<>( nodes );
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public String getContent() {
        return "";
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Collection<QueryNode> getChildren() {
        return children;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        // print and Node
        sb.append( "[ 'OR'," );

        // start of child list
        sb.append( "[ " );

        List<String> allNodesAsString = children.stream().map( QueryNode::toString ).collect( Collectors.toList() );
        sb.append( String.join( ", ", allNodesAsString ) );

        // end of child list
        sb.append( " ]" );

        // end of Node
        sb.append( " ]" );

        return sb.toString();
    }

}
