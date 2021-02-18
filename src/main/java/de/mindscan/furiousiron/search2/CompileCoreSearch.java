/**
 * 
 * MIT License
 *
 * Copyright (c) 2021 Maxim Gansert, Mindscan
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
package de.mindscan.furiousiron.search2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.mindscan.furiousiron.core.ast.CoreQueryNode;
import de.mindscan.furiousiron.core.ast.EmptyCoreNode;
import de.mindscan.furiousiron.core.ast.TrigramsCoreNode;
import de.mindscan.furiousiron.search.query.ast.AndNode;
import de.mindscan.furiousiron.search.query.ast.EmptyNode;
import de.mindscan.furiousiron.search.query.ast.ExcludingNode;
import de.mindscan.furiousiron.search.query.ast.IncludingNode;
import de.mindscan.furiousiron.search.query.ast.OrNode;
import de.mindscan.furiousiron.search.query.ast.QueryNode;
import de.mindscan.furiousiron.search.query.ast.TextNode;

/**
 * 
 */
public class CompileCoreSearch {

    public static CoreQueryNode compileCoreSearch( QueryNode ast ) {
        if (ast == null) {
            return new EmptyCoreNode();
        }
    
        if (ast instanceof EmptyNode) {
            return new EmptyCoreNode();
        }
    
        // Compile parsedAST into a technical AST
        if (ast instanceof TextNode) {
            return new TrigramsCoreNode( ast.getContent().toLowerCase() );
        }
    
        if (ast instanceof AndNode) {
            Set<String> includedwords = new HashSet<String>();
    
            // collect each word
            for (QueryNode queryNode : ast.getChildren()) {
                CoreQueryNode t = compileCoreSearch( queryNode );
                includedwords.addAll( t.getTrigrams() );
            }
            List<String> l = includedwords.stream().collect( Collectors.toList() );
            return new TrigramsCoreNode( l );
        }
    
        if (ast instanceof OrNode) {
            // We really don't support Or nodes right now.
            throw new RuntimeException( "Or Optimization is not implemented yet" );
        }
    
        if (ast instanceof ExcludingNode) {
            // We don't support excluding on this level right now.
            return new EmptyCoreNode();
        }
    
        if (ast instanceof IncludingNode) {
            for (QueryNode queryNode : ast.getChildren()) {
                return compileCoreSearch( queryNode );
            }
            return new EmptyCoreNode();
        }
    
        return null;
    }

}
