package it.infocert.eigor.model.core.datatypes;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class IdentifierTest {

    @Test
    public void testToString() {
        Identifier identifier = new Identifier("a", "b", "c");
        assertThat(identifier.toString(), is("a:b:c"));
    }



}