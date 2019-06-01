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

/**
 * 
 */
public class IncludingNode implements QueryNode {

    private QueryNode innerNode;

    /**
     * 
     */
    public IncludingNode( QueryNode innerNode ) {
        this.innerNode = innerNode;
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
        return true;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Collection<QueryNode> getChildren() {
        List<QueryNode> childrenList = new ArrayList<>();
        childrenList.add( innerNode );
        return childrenList;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append( "[ 'INCLUDING', [ " );

        sb.append( innerNode.toString() );

        sb.append( " ] ]" );

        return sb.toString();
    }
}
