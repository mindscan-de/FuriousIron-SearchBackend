package de.mindscan.furiousiron.search.query.ast;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

public class EmptyNodeTest {

    @Test
    public void testGetContent_CtorOnly_returnsEmptyStringValue() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        String result = node.getContent();

        // assert
        assertThat( result, isEmptyString() );
    }

    @Test
    public void testHasChildren_CtorOnly_returnsFalse() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        boolean result = node.hasChildren();

        // assert
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testGetChildren_CtorOnly_returnsNonNullValue() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, not( nullValue() ) );
    }

    @Test
    public void testGetChildren_CtorOnly_returnsEmptyList() throws Exception {
        // arrange
        EmptyNode node = new EmptyNode();

        // act
        Collection<QueryNode> result = node.getChildren();

        // assert
        assertThat( result, empty() );
    }

}
