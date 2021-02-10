package de.mindscan.furiousiron.search2.corequery.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;

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

}
