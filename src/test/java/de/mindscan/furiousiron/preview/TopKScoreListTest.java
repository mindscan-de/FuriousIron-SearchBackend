package de.mindscan.furiousiron.preview;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class TopKScoreListTest {

    @Test
    public void testTopKScoreList_ZeroAndNoInteraction_expectEmptySet() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 0 );

        // act
        Set<Integer> result = scoreList.getSet();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testTopKScoreList_OneAndNoInteraction_expectEmptySet() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 1 );

        // act
        Set<Integer> result = scoreList.getSet();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testIsCandidateTopK_ZeroWithOneInteraction21_returnsFalse() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 0 );

        // act
        boolean result = scoreList.isCandidateTopK( 21 );

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testIsCandidateTopK_OneWithOneInteraction21_returnsTrue() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 1 );

        // act
        boolean result = scoreList.isCandidateTopK( 21 );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsCandidateTopK_OneWithOneInteraction42_returnsTrue() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 1 );

        // act
        boolean result = scoreList.isCandidateTopK( 42 );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsCandidateTopK_OneWithTwoInteractions12_returnsTrueOnFirst() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 1 );

        // act
        boolean result = scoreList.isCandidateTopK( 1 );
        scoreList.isCandidateTopK( 2 );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsCandidateTopK_OneWithTwoInteractions12_returnsTrueOnSecond() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 1 );

        // act
        scoreList.isCandidateTopK( 1 );
        boolean result = scoreList.isCandidateTopK( 2 );

        // assert
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testIsCandidateTopK_OneWithTwoInteractions21_returnsFalseOnSecond() throws Exception {
        // arrange
        TopKScoreList scoreList = new TopKScoreList( 1 );

        // act
        scoreList.isCandidateTopK( 2 );
        boolean result = scoreList.isCandidateTopK( 1 );

        // assert
        assertThat( result, equalTo( false ) );
    }

}
