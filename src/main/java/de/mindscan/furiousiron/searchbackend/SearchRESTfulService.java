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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.google.gson.Gson;

import de.mindscan.furiousiron.document.DocumentMetadata;
import de.mindscan.furiousiron.query.ast.QueryNode;
import de.mindscan.furiousiron.search.Search;
import de.mindscan.furiousiron.search.SearchResultCandidates;
import de.mindscan.furiousiron.search.outputmodel.QueryResultItemJsonModel;
import de.mindscan.furiousiron.search.outputmodel.QueryResultJsonModel;
import de.mindscan.furiousiron.search.query.executor.QueryExecutor;
import de.mindscan.furiousiron.search.query.parser.QueryParser;
import de.mindscan.furiousiron.search2.QueryParser2;
import de.mindscan.furiousiron.util.StopWatch;

/**
 * 
 */
@javax.ws.rs.Path( "/search" )
public class SearchRESTfulService {

    // Example URL: http://localhost:8080/SearchBackend/rest/search/result?q=123    
    @javax.ws.rs.Path( "/result" )
    @GET
    @Produces( "application/json" )
    public String getQueryResult2_JSON( @QueryParam( "q" ) String query ) {
        Path indexFolder = Paths.get( "D:\\Analysis\\CrawlerProjects", "Indexed" );

        // in case the search contains OR tree parts
        // it will throw an exception then we do the old way. 
        try {
            StopWatch optimizedResultStopwatch = StopWatch.createStarted();

            Search search = new Search( indexFolder );
            QueryParser2 queryParser = new QueryParser2();
            Collection<SearchResultCandidates> resultCandidates = queryParser.search( search, query );

            QueryResultJsonModel jsonResult = convertResultsToOutputModel( resultCandidates );

            optimizedResultStopwatch.stop();
            jsonResult.setSearchTimeInMs( optimizedResultStopwatch.getElapsedTime() );

            System.out.println( "q2:=" + query + " / time(opt): " + (optimizedResultStopwatch.getElapsedTime()) + " ms" );

            Gson gson = new Gson();
            return gson.toJson( jsonResult );

        }
        catch (Exception ex) {
            StopWatch unoptimizedResultStopWatch = StopWatch.createStarted();

            Search search = new Search( indexFolder );
            QueryParser queryParser = new QueryParser();
            QueryNode parsedAST = queryParser.parseQuery( query.toLowerCase() );
            Collection<SearchResultCandidates> resultCandidates = QueryExecutor.execute( search, parsedAST );

            QueryResultJsonModel jsonResult = convertResultsToOutputModel( resultCandidates );

            unoptimizedResultStopWatch.stop();
            jsonResult.setSearchTimeInMs( unoptimizedResultStopWatch.getElapsedTime() );

            System.out.println( "q:=" + query + " / time(not opt): " + (unoptimizedResultStopWatch.getElapsedTime()) + " ms" );

            Gson gson = new Gson();
            return gson.toJson( jsonResult );
        }

    }

    private QueryResultJsonModel convertResultsToOutputModel( Collection<SearchResultCandidates> resultCandidates ) {
        QueryResultJsonModel jsonResult = new QueryResultJsonModel();
        for (SearchResultCandidates candidate : resultCandidates) {
            DocumentMetadata metadata = candidate.getMetadata();

            QueryResultItemJsonModel item = new QueryResultItemJsonModel( metadata.getSimpleFilename(), metadata.getRelativePath() );
            item.setFileSize( metadata.getFileSize() );
            item.setNumberOfLinesInFile( metadata.getNumberOfLines() );
            item.setClassifierMap( metadata.getClassifierMap() );

            jsonResult.addQueryResultItem( item );
        }
        return jsonResult;
    }

    // @MatrixParam("q") String query

}
