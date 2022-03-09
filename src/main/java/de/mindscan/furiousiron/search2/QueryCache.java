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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import de.mindscan.furiousiron.index.cache.SearchQueryCache;
import de.mindscan.furiousiron.query.ast.QueryNode;

/**
 * 
 */
public class QueryCache {

    // backend
    private SearchQueryCache searchQueryCache;

    /**
     * @param searchQueryCache
     */
    public QueryCache( SearchQueryCache searchQueryCache ) {
        this.searchQueryCache = searchQueryCache;

    }

    /**
     * @param ast
     * @return
     */
    public boolean hasCachedSearchResult( QueryNode ast ) {
        String qkey = calculateQueryKey( ast );

        return searchQueryCache.isQueryResultAvailable( qkey );
    }

    /**
     * @param ast
     * @param retained
     */
    public void cacheSearchResult( QueryNode ast, List<String> documentIds ) {
        String qkey = calculateQueryKey( ast );

        searchQueryCache.saveQueryResult( qkey, documentIds );
    }

    /**
     * @param ast
     * @param resultPreviews
     */
    public void cacheSearchResultPreview( QueryNode ast, Map<String, Map<Integer, String>> resultPreviews ) {
        String qkey = calculateQueryKey( ast );

        searchQueryCache.savePreviewResult( qkey, resultPreviews );
    }

    /**
     * @param ast
     */
    public void cacheSearchQuery( QueryNode ast ) {
        String qkey = calculateQueryKey( ast );

        searchQueryCache.saveQuery( qkey, QueryPrinter.toPrettyQuery( ast ) );
    }

    /**
     * @param ast
     * @return
     */
    public List<String> loadSearchResult( QueryNode ast ) {
        String qkey = calculateQueryKey( ast );

        return searchQueryCache.loadQueryResult( qkey );
    }

    public Map<String, Map<Integer, String>> loadSearchResultPreview( QueryNode ast ) {
        String qkey = calculateQueryKey( ast );

        return searchQueryCache.loadPreviewResult( qkey );
    }

    private String calculateQueryKey( QueryNode ast ) {
        return md5( ast.toString() );
    }

    public static String md5( String queryString ) {
        try {
            byte[] relativePathAsBytes = queryString.getBytes( StandardCharsets.UTF_8 );

            MessageDigest md5sum = MessageDigest.getInstance( "MD5" );
            byte[] md5 = md5sum.digest( relativePathAsBytes );

            BigInteger md5bi = new BigInteger( 1, md5 );
            String md5hex = md5bi.toString( 16 );

            while (md5hex.length() < 32) {
                md5hex = "0" + md5hex;
            }

            return md5hex;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

}
