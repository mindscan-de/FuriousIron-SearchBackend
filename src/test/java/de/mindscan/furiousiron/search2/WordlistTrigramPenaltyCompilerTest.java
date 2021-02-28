package de.mindscan.furiousiron.search2;

import static de.mindscan.furiousiron.search2.TrigramUsage.TrigramUsageState.FAILED;
import static de.mindscan.furiousiron.search2.TrigramUsage.TrigramUsageState.SUCCESS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.index.trigram.TrigramOccurence;

public class WordlistTrigramPenaltyCompilerTest {

    @Test
    public void testGetOrderedWordlist_emptyWordlists_returnsNonNullValue() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<TrigramOccurence> allTrigramOccurrences = Collections.emptyList();
        Collection<String> wordlist = Collections.emptyList();
        Collection<TrigramUsage> usage = Collections.emptyList();

        // act
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( wordlist, usage );

        // assert
        assertThat( result, not( nullValue() ) );
    }

    @Test
    public void testGetOrderedWordlist_NonEmptyWordlists_returnsSameWordlist() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<String> wordlist = createWordlist();
        Collection<TrigramUsage> usage = createFullTrigramUsageList();

        // act
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( wordlist, usage );

        // assert
        assertThat( result, containsInAnyOrder( wordlist.toArray() ) );
    }

    @Test
    public void testGetOrderedWordlist_UseFullTrigramTruth_returnsWordlistmostImportant() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<String> wordlist = createWordlist();
        Collection<TrigramUsage> usage = createFullTrigramUsageList();

        // act
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( wordlist, usage );
        List<String> foo = new ArrayList<>( result );
        List<String> importantSublist = foo.subList( 0, 5 );

        // assert
        assertThat( importantSublist, containsInAnyOrder( "abstract", "index", "field", "store", "hashmap" ) );
    }

    @Test
    public void testGetOrderedWordlist_UseFullTrigramTruth_returnsWordlistMostUselessAtEnd() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<String> wordlist = createWordlist();
        Collection<TrigramUsage> usage = createFullTrigramUsageList();

        // act
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( wordlist, usage );
        List<String> foo = new ArrayList<>( result );
        List<String> importantSublist = foo.subList( 5, result.size() );

        // assert
        assertThat( importantSublist, containsInAnyOrder( "import", "class", "string", "package" ) );
    }

    @Test
    public void testGetOrderedWordlist_UseIncompleteTruth143_returnsWordlistMostFullAtStart() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<String> wordlist = createWordlist();
        Collection<TrigramUsage> usage = createIncompleteTrigramUsageList_143();

        // act
        System.out.println( "143..." );
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( wordlist, usage );
        List<String> foo = new ArrayList<>( result );
        List<String> importantSublist = foo.subList( 0, 3 );

        // assert
        assertThat( importantSublist, containsInAnyOrder( "abstract", "store", "field" ) );
    }

    @Test
    public void testGetOrderedWordlist_UseIncompleteTruth167_returnsWordlistMostFullAtStart() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<String> wordlist = createWordlist();
        Collection<TrigramUsage> usage = createIncompleteTrigramUsageList_167();

        // act
        System.out.println( "167..." );
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( wordlist, usage );
        List<String> foo = new ArrayList<>( result );
        List<String> importantSublist = foo.subList( 0, 3 );

        // assert
        assertThat( importantSublist, containsInAnyOrder( "abstract", "store", "field" ) );
    }

    @Test
    public void testGetOrderedWordlist_UseIncompleteTruth169_returnsWordlistMostFullAtStart() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<String> wordlist = createWordlist();
        Collection<TrigramUsage> usage = createIncompleteTrigramUsageList_169();

        // act
        System.out.println( "169..." );
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( wordlist, usage );
        List<String> foo = new ArrayList<>( result );
        List<String> importantSublist = foo.subList( 0, 3 );

        // assert
        assertThat( importantSublist, containsInAnyOrder( "abstract", "store", "field" ) );
    }

    private List<String> createWordlist() {
        return Arrays.asList( "import", "package", "class", "index", "store", "abstract", "field", "hashmap", "string" );
    }

    private List<TrigramUsage> createFullTrigramUsageList() {
        return Arrays.asList( //
                        new TrigramUsage( "shm", SUCCESS ), //
                        new TrigramUsage( "hma", SUCCESS ), //
                        new TrigramUsage( "abs", SUCCESS ), //
                        new TrigramUsage( "bst", SUCCESS ), // 
                        new TrigramUsage( "ash", FAILED ), //
                        new TrigramUsage( "sto", SUCCESS ), //
                        new TrigramUsage( "map", FAILED ), // 
                        new TrigramUsage( "rac", SUCCESS ), //
                        new TrigramUsage( "iel", SUCCESS ), //
                        new TrigramUsage( "eld", FAILED ), // 
                        new TrigramUsage( "has", FAILED ), // 
                        new TrigramUsage( "fie", SUCCESS ), //
                        new TrigramUsage( "tra", SUCCESS ), // 
                        new TrigramUsage( "dex", SUCCESS ), //
                        new TrigramUsage( "act", FAILED ), //
                        new TrigramUsage( "ind", SUCCESS ), // 
                        new TrigramUsage( "rin", FAILED ), //
                        new TrigramUsage( "ore", FAILED ), // 
                        new TrigramUsage( "tor", FAILED ), //
                        new TrigramUsage( "nde", FAILED ), //
                        new TrigramUsage( "cla", FAILED ), //
                        new TrigramUsage( "mpo", SUCCESS ), //
                        new TrigramUsage( "ass", FAILED ), //
                        new TrigramUsage( "str", FAILED ), //
                        new TrigramUsage( "por", FAILED ), //
                        new TrigramUsage( "kag", FAILED ), //
                        new TrigramUsage( "cka", FAILED ), //
                        new TrigramUsage( "ort", FAILED ), //
                        new TrigramUsage( "tri", FAILED ), // 
                        new TrigramUsage( "ing", FAILED ), //
                        new TrigramUsage( "ack", FAILED ), // 
                        new TrigramUsage( "las", FAILED ), // 
                        new TrigramUsage( "pac", FAILED ), //
                        new TrigramUsage( "imp", FAILED ), //
                        new TrigramUsage( "age", FAILED ) );
    }

    private List<TrigramUsage> createIncompleteTrigramUsageList_143() {
        return Arrays.asList( //
                        new TrigramUsage( "shm", SUCCESS ), //
                        new TrigramUsage( "hma", SUCCESS ), //
                        new TrigramUsage( "abs", SUCCESS ), //
                        new TrigramUsage( "bst", SUCCESS ), // 
                        new TrigramUsage( "ash", FAILED ), //
                        new TrigramUsage( "sto", SUCCESS ), //
                        new TrigramUsage( "map", FAILED ), // 
                        new TrigramUsage( "rac", SUCCESS ), //
                        new TrigramUsage( "iel", SUCCESS ), //
                        new TrigramUsage( "eld", FAILED ), // 
                        new TrigramUsage( "has", FAILED ), // 
                        new TrigramUsage( "fie", SUCCESS ), //
                        new TrigramUsage( "tra", SUCCESS ), // 
                        new TrigramUsage( "dex", SUCCESS ), //
                        new TrigramUsage( "act", FAILED ) //
        );
    }

    private List<TrigramUsage> createIncompleteTrigramUsageList_167() {
        return Arrays.asList( //
                        new TrigramUsage( "shm", SUCCESS ), //
                        new TrigramUsage( "hma", SUCCESS ), //
                        new TrigramUsage( "abs", SUCCESS ), //
                        new TrigramUsage( "bst", SUCCESS ), // 
                        new TrigramUsage( "ash", FAILED ), //
                        new TrigramUsage( "sto", SUCCESS ), //
                        new TrigramUsage( "map", FAILED ), // 
                        new TrigramUsage( "rac", SUCCESS ), //
                        new TrigramUsage( "iel", SUCCESS ), //
                        new TrigramUsage( "eld", FAILED ), // 
                        new TrigramUsage( "has", FAILED ), // 
                        new TrigramUsage( "fie", SUCCESS ), //
                        new TrigramUsage( "tra", SUCCESS ) // 
        );
    }

    private List<TrigramUsage> createIncompleteTrigramUsageList_169() {
        return Arrays.asList( //
                        new TrigramUsage( "shm", SUCCESS ), //
                        new TrigramUsage( "hma", SUCCESS ), //
                        new TrigramUsage( "abs", SUCCESS ), //
                        new TrigramUsage( "bst", SUCCESS ), // 
                        new TrigramUsage( "ash", FAILED ), //
                        new TrigramUsage( "sto", SUCCESS ), //
                        new TrigramUsage( "map", FAILED ), // 
                        new TrigramUsage( "rac", SUCCESS ), //
                        new TrigramUsage( "iel", SUCCESS ), //
                        new TrigramUsage( "eld", FAILED ), // 
                        new TrigramUsage( "has", FAILED ) // 
        );
    }

}
