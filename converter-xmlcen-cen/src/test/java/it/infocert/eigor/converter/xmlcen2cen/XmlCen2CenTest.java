package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.utils.JavaReflections;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class XmlCen2CenTest {

    private XmlCen2Cen converter;

    @Before
    public void setup() {
        converter = new XmlCen2Cen(new JavaReflections());
    }

    @Test
    public void convert() throws IOException, SyntaxErrorInInvoiceFormatException {

        File f = new File("C:\\Users\\aless\\IdeaProjects\\eigor\\converter-xmlcen-cen\\src\\test\\resources\\semanticCEN.xml");
        converter.convert(new FileInputStream(f));
    }
}