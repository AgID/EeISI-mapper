package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.XSDValidator;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0002InvoiceIssueDate;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import org.jdom2.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class Ubl2CenTest {

    private static final Logger log = LoggerFactory.getLogger(Ubl2CenTest.class);

    private Ubl2Cen sut;

    @Before
    public void setUp() {
        sut = new Ubl2Cen(new Reflections("it.infocert"));
    }
    
    @Test
    public void convertTest() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/ubl-plain.xml");

        ConversionResult<BG0000Invoice> conversionResult = sut.convert(sourceInvoiceStream);

        assertThat(conversionResult.getResult(), is(IsNull.notNullValue()));
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
        
        //// TODO: 6/28/17 check manytoone output after getting examples from Sara 
    }

    @Test
    public void canReadDocument() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/ubl-plain.xml");

        Document document = sut.getDocument(sourceInvoiceStream);

        assertThat(document, is(IsNull.notNullValue()));
    }

    @Test
    public void shouldSupportUbl() {
        assertThat(sut.support("ubl"), is(true));
    }

    @Test
    public void shouldSupportedFormatsUbl() {
        assertThat(sut.getSupportedFormats(), contains("ubl"));
    }
    
    @Test
    public void shouldValidateXsd() throws IOException {
    	InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/UBL-Invoice-2.1-Example.xml");
    	byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
    	URL xsdFile = Ubl2Cen.class.getClassLoader().getResource("xsd/Schema_del_file_xml_Ubl_versione_2.1.xsd");
    	XSDValidator xsdValidator = new XSDValidator(xsdFile);
    	List<ConversionIssue> errors = xsdValidator.validate(bytes);
    	assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldNotValidateXsd() throws IOException {
    	InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/UBL-Invoice-2.1-Example-KO.xml");
    	byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
    	URL xsdFile = Ubl2Cen.class.getClassLoader().getResource("xsd/Schema_del_file_xml_Ubl_versione_2.1.xsd");
    	XSDValidator xsdValidator = new XSDValidator(xsdFile);
    	List<ConversionIssue> errors = xsdValidator.validate(bytes);
    	assertFalse(errors.isEmpty());
    }
}