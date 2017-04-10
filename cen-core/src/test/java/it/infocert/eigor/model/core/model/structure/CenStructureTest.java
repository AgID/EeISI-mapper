package it.infocert.eigor.model.core.model.structure;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CenStructureTest {

    private CenStructure sut;

    @Before
    public void setUp() {
        sut = new CenStructure();
    }

    @Test
    public void shouldKnowTheChildren() {

        // given
        CenStructure.BtBgNode bg0 = sut.findByName("BG0");
        CenStructure.BtBgNode bg25 = sut.findByName("BG25");

        // when
        Set<CenStructure.BtBgNode> children = bg0.getChildren();

        // then
        assertThat( children, hasItem( bg25 ) );

    }

    @Test
    public void shouldKnowWhenThereAreNoChildren() {

        // given
        CenStructure.BtBgNode bt100 = sut.findByName("BT100");

        // when
        Set<CenStructure.BtBgNode> children = bt100.getChildren();

        // then
        assertThat( children, hasSize(0) );

    }

    @Test public void shouldKnowWhoIsTheParent() {

        // when
        CenStructure.BtBgNode bt160 = sut.findByName("BT160");

        // then
        assertThat( bt160.getBtOrBg(), is("BT") );
        assertThat( bt160.getNumber(), is(160) );
        assertThat( bt160.toString(), is("BT-160") );
        assertThat( bt160.getParent().toString(), is("BG-32"));
        assertThat( bt160.getParent().getParent().toString(), is("BG-31"));
        assertThat( bt160.getParent().getParent().getParent().toString(), is("BG-25"));
        assertThat( bt160.getParent().getParent().getParent().getParent().toString(), is("BG-0"));
        assertThat( bt160.getParent().getParent().getParent().getParent().getParent(), nullValue());

    }

    @Test(expected = IllegalArgumentException.class) public void shouldFailWhenNodeDoesNotExist() {

        // when
        CenStructure.BtBgNode bt999 = sut.findByName("BT999");

    }

}