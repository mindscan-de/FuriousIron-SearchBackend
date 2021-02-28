package de.mindscan.furiousiron.search2;

import static de.mindscan.furiousiron.search2.TrigramUsage.TrigramUsageState.FAILED;
import static de.mindscan.furiousiron.search2.TrigramUsage.TrigramUsageState.SUCCESS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

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
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( allTrigramOccurrences, wordlist, usage );

        // assert
        assertThat( result, not( nullValue() ) );
    }

    @Test
    public void testGetOrderedWordlist_NonEmptyWordlists_returnsSameWordlist() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<TrigramOccurence> allTrigramOccurrences = createTrigramOccurrenceList();
        Collection<String> wordlist = createWordlist();
        Collection<TrigramUsage> usage = createFullTrigramUsageList();

        // act
        Collection<String> result = trigramPenaltyCompiler.getOrderedWordlist( allTrigramOccurrences, wordlist, usage );

        // assert
        assertThat( result, containsInAnyOrder( wordlist.toArray() ) );
    }

    private List<TrigramOccurence> createTrigramOccurrenceList() {
        return Arrays.asList( //
                        new TrigramOccurence( "shm", 1769 ), //
                        new TrigramOccurence( "hma", 1840 ), //
                        new TrigramOccurence( "abs", 3471 ), //
                        new TrigramOccurence( "bst", 3663 ), //
                        new TrigramOccurence( "ash", 4290 ), //
                        new TrigramOccurence( "sto", 4429 ), //
                        new TrigramOccurence( "map", 4990 ), //
                        new TrigramOccurence( "rac", 5471 ), //
                        new TrigramOccurence( "iel", 5653 ), //
                        new TrigramOccurence( "eld", 5723 ), //
                        new TrigramOccurence( "has", 6191 ), //
                        new TrigramOccurence( "fie", 6418 ), //
                        new TrigramOccurence( "tra", 6581 ), //
                        new TrigramOccurence( "dex", 6840 ), //
                        new TrigramOccurence( "act", 9190 ), //
                        new TrigramOccurence( "ind", 11084 ), //
                        new TrigramOccurence( "rin", 11535 ), //
                        new TrigramOccurence( "ore", 12201 ), //
                        new TrigramOccurence( "tor", 12852 ), //
                        new TrigramOccurence( "nde", 13192 ), //
                        new TrigramOccurence( "cla", 13336 ), //
                        new TrigramOccurence( "mpo", 13367 ), //
                        new TrigramOccurence( "ass", 13438 ), //
                        new TrigramOccurence( "str", 13495 ), //
                        new TrigramOccurence( "por", 13510 ), //
                        new TrigramOccurence( "kag", 13535 ), //
                        new TrigramOccurence( "cka", 13542 ), //
                        new TrigramOccurence( "ort", 13594 ), //
                        new TrigramOccurence( "tri", 13740 ), //
                        new TrigramOccurence( "ing", 13755 ), //
                        new TrigramOccurence( "ack", 13791 ), //
                        new TrigramOccurence( "las", 13809 ), //
                        new TrigramOccurence( "pac", 13830 ), //
                        new TrigramOccurence( "imp", 13869 ), //
                        new TrigramOccurence( "age", 13870 ) // 
        );
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
}
