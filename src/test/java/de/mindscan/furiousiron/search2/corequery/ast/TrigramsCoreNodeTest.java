package de.mindscan.furiousiron.search2.corequery.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class TrigramsCoreNodeTest {

    @Test
    public void testHasChildren_CtorEmptyWord_expectFalse() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "" );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testHasChildren_CtorNonEmptyWord_expectFalse() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "nonemptyword" );

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testGetChildren_CtorEmptyWord_expectEmptyCollection() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "" );

        // act
        Collection<CoreQueryNode> result = node.getChildren();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testGetChildren_CtorNonEmptyWord_expectEmptyCollection() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "nonemptyword" );

        // act
        Collection<CoreQueryNode> result = node.getChildren();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testGetTrigrams_EmptyWord_expectEmptyCollection() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "" );

        // act
        Collection<String> result = node.getTrigrams();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testGetTrigrams_WordWithThreeLetters_expectEmptyCollection() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "aaa" );

        // act
        Collection<String> result = node.getTrigrams();

        // assert
        assertThat( result, not( empty() ) );
    }

    @Test
    public void testGetTrigrams_WordWithThreeLetters_expectCollectionContainsWord() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "aaa" );

        // act
        Collection<String> result = node.getTrigrams();

        // assert
        assertThat( result, contains( "aaa" ) );
    }

    @Test
    public void testGetTrigrams_DifferentWordWithThreeLetters_expectCollectionContainsWord() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "abc" );

        // act
        Collection<String> result = node.getTrigrams();

        // assert
        assertThat( result, contains( "abc" ) );
    }

    @Test
    public void testGetTrigrams_DifferentWordWithTwoLetters_expectEmptyCollection() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "ab" );

        // act
        Collection<String> result = node.getTrigrams();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testGetTrigrams_ContainsSearch_expectContainsAllTrigrams() throws Exception {
        // arrange
        TrigramsCoreNode node = new TrigramsCoreNode( "search" );

        // act
        Collection<String> result = node.getTrigrams();

        // assert
        assertThat( result, containsInAnyOrder( "sea", "ear", "arc", "rch" ) );
    }

}
