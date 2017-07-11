package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0002InvoiceIssueDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

public class ITUbl2CenTest {

    private static final Logger log = LoggerFactory.getLogger(ITUbl2CenTest.class);

    private static Ubl2Cen sut;

    @BeforeClass
    public static void setUp() throws ConfigurationException {

        EigorConfiguration conf = new PropertiesBackedConfiguration()
                .addProperty("eigor.converter.ubl-cen.cius", "classpath:converterdata/converter-ubl-cen/cius/schematron-xslt/CIUS-validation.xslt")
                .addProperty("eigor.converter.ubl-cen.schematron", "classpath:converterdata/converter-ubl-cen/ubl/schematron-xslt/EN16931-UBL-validation.xslt" )
                .addProperty("eigor.converter.ubl-cen.xsd", "converterdata/converter-ubl-cen/ubl/xsd/UBL-Invoice-2.1.xsd")
                .addProperty("eigor.converter.ubl-cen.mapping.many-to-one", "converterdata/converter-ubl-cen/mappings/many_to_one.properties")
                .addProperty("eigor.converter.ubl-cen.mapping.one-to-one", "converterdata/converter-ubl-cen/mappings/one_to_one.properties");

        sut = new Ubl2Cen(
                new Reflections("it.infocert"),
                conf
        );
        sut.configure();
    }
    
    @Test
    public void convertTest() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/ubl-plain.xml");

        ConversionResult<BG0000Invoice> conversionResult = sut.convert(sourceInvoiceStream);

        assertNotNull(conversionResult.getResult());
    }

    @Test
    public void shouldConvertToCenInvoice() throws Exception {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/UBL-Invoice-2.1-Example.xml");
        ConversionResult<BG0000Invoice> conversionResult = sut.convert(sourceInvoiceStream);
        BG0000Invoice invoice = conversionResult.getResult();
        List<BT0001InvoiceNumber> bt0001InvoiceNumbers = invoice.getBT0001InvoiceNumber();
        List<BT0002InvoiceIssueDate> bt0002InvoiceIssueDates = invoice.getBT0002InvoiceIssueDate();
        assertFalse(bt0001InvoiceNumbers.isEmpty());
        assertFalse(bt0002InvoiceIssueDates.isEmpty());
        assertEquals("TOSL108", bt0001InvoiceNumbers.get(0).getValue());
        assertEquals("2009-12-15", bt0002InvoiceIssueDates.get(0).getValue().toString("yyyy-MM-dd"));
        assertEquals("Ordered in our booth at the convention.", invoice.getBG0001InvoiceNote(0).getBT0021InvoiceNoteSubjectCode(0).getValue());
        //// TODO: 6/28/17 check manytoone output after getting examples from Sara 
    }


}