package de.mindscan.furiousiron.search2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mindscan.furiousiron.index.trigram.TrigramOccurence;

public class WordlistTrigramPenaltyCompilerTest {

    @Test
    public void testGetOrderedWordlist() throws Exception {
        // arrange
        WordlistTrigramPenaltyCompiler trigramPenaltyCompiler = new WordlistTrigramPenaltyCompiler();

        Collection<TrigramOccurence> allTrigramOccurrences = Collections.emptyList();
        Collection<String> wordlist = Collections.emptyList();
        Collection<TrigramUsage> usage = Collections.emptyList();

        // act
        List<String> result = trigramPenaltyCompiler.getOrderedWordlist( allTrigramOccurrences, wordlist, usage );

        // assert
        assertThat( result, not( nullValue() ) );
    }

}
