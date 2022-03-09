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
package de.mindscan.furiousiron.searchbackend;

import static de.mindscan.furiousiron.config.SearchBackendConfiguration.INDEX_BASE_PATH;
import static de.mindscan.furiousiron.config.SearchBackendConfiguration.INDEX_INDEXED_PATH;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.mindscan.furiousiron.search.Search;

/**
 * This service provides cached content. 
 * 
 * The content is retrieved from the local document cache. This service is required for the ability of the 
 * user to have a look at the document in the browser.
 * 
 */
@javax.ws.rs.Path( "/cached" )
public class CachedContentRESTfulService {

    // example URL is: localhost:8081/SearchBackend/rest/cached/content/?p={THE PATH COMES HERE} 
    @javax.ws.rs.Path( "/content" )
    @GET
    @Produces( "text/plain" )
    public String getCachedContent( @QueryParam( "p" ) String path ) {
        Path indexFolder = Paths.get( INDEX_BASE_PATH, INDEX_INDEXED_PATH );

        Search search = new Search( indexFolder );

        // TODO: do not use exceptions for normal workflow, so that if the document is not avail, measures can be taken here...
        // Maybe the getDocumentContent could provide an Optional, which means that the content is either available
        // and if not, then another action must be taken, by this method

        return search.getDocumentContent( path );
    }

}
