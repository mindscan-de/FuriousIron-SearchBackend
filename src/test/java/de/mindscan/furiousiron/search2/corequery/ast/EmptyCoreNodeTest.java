package de.mindscan.furiousiron.search2.corequery.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class EmptyCoreNodeTest {

    @Test
    public void testHasChildren_Ctor_returnsFalse() throws Exception {
        // arrange
        EmptyCoreNode node = new EmptyCoreNode();

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testGetChildren_Ctor_returnsEmptyCollection() throws Exception {
        // arrange
        EmptyCoreNode node = new EmptyCoreNode();

        // act
        Collection<CoreQueryNode> result = node.getChildren();

        // assert
        assertThat( result, empty() );
    }

    @Test
    public void testGetTrigrams_Ctor_returnEmptyConnection() throws Exception {
        // arrange
        EmptyCoreNode node = new EmptyCoreNode();

        // act
        Collection<String> result = node.getTrigrams();

        // assert
        assertThat( result, empty() );
    }

}
