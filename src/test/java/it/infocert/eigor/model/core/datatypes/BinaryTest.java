package it.infocert.eigor.model.core.datatypes;

import org.junit.Test;

import javax.activation.MimeType;

import static org.junit.Assert.*;

public class BinaryTest {

    @Test public void createABinary() {

        Binary sut = new Binary(new byte[]{}, new MimeType(), "file");

        assertArrayEquals( new byte[]{}, sut.getBytes() );
        assertEquals( new MimeType().toString(), sut.getMimeType().toString() );
        assertEquals( "file", sut.getFileName() );

    }

}