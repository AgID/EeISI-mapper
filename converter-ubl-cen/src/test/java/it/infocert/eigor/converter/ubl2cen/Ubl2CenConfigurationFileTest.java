package it.infocert.eigor.converter.ubl2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ubl2CenConfigurationFileTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MyUblToCenConverter sut;
    private List<ConversionIssue> conversionIssues;

    @Before
    public void setUp() throws ConfigurationException {
        EigorConfiguration conf = new PropertiesBackedConfiguration()
                .addProperty("eigor.workdir", "classpath:")
                .addProperty("eigor.converter.ubl-cen.mapping.one-to-one", "converterdata/converter-ubl-cen/mappings/one_to_one.properties")
                .addProperty("eigor.converter.ubl-cen.mapping.many-to-one", "converterdata/converter-ubl-cen/mappings/many_to_one.properties")
                .addProperty("eigor.converter.ubl-cen.mapping.one-to-many", "converterdata/converter-ubl-cen/mappings/one_to_many.properties")
                .addProperty("eigor.converter.ubl-cen.xsd", "file:src/test/resources/converterdata/converter-ubl-cen/ubl/xsd/UBL-Invoice-2.1.xsd")
                .addProperty("eigor.converter.ubl-cen.schematron", "converterdata/converter-ubl-cen/ubl/schematron-xslt/EN16931-UBL-validation.xslt")
                .addProperty("eigor.converter.ubl-cen.mapping.custom", "converterdata/converter-ubl-cen/mappings/custom.conf")
                .addProperty("eigor.converter.ubl-cen.cius", "converterdata/converter-ubl-cen/cius/schematron-xslt/EN16931-CIUS-IT-UBLValidation.xslt");
        sut = new MyUblToCenConverter(new JavaReflections(), conf);
        sut.configure();
    }

    @Test
    public void shouldValidateXsd() throws Exception {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/UBL-Invoice-2.1-Example.xml");
        List<IConversionIssue> errors = validate(sourceInvoiceStream);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldNotValidateXsd() throws Exception {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/UBL-Invoice-2.1-Example-KO.xml");
        List<IConversionIssue> errors = validate(sourceInvoiceStream);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void testInvoiceNoteConverter() throws Exception {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("examples/ubl/ubl-tc434-example5-provaCustom.xml");
        Document document = getDocument(sourceInvoiceStream);
        BG0000Invoice invoice = new BG0000Invoice();
        List<IConversionIssue> errors = new ArrayList<>();

        InvoiceNoteConverter bg0001 = new InvoiceNoteConverter();
        BG0000Invoice convertedInvoice = bg0001.toBG0001(document, invoice, errors).getResult();
        assertFalse(convertedInvoice.getBG0001InvoiceNote().isEmpty());
        int found = 0;
        for (BG0001InvoiceNote invoiceNote : convertedInvoice.getBG0001InvoiceNote()) {
            if (!invoiceNote.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                String value = invoiceNote.getBT0021InvoiceNoteSubjectCode(0).getValue();
                if ("Ordered through our website#Ordering information".equals(value)) {
                    found++;
                }
            }

            if (!invoiceNote.getBT0022InvoiceNote().isEmpty()) {
                String value = invoiceNote.getBT0022InvoiceNote(0).getValue();
                if ("Ordered through our website#Ordering information".equals(value)) {
                    found++;
                }
            }
        }
        assertTrue(found > 0);
    }


    private List<IConversionIssue> validate(InputStream sourceInvoiceStream) throws IOException, SAXException {
        byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
        String filePath = getClass().getClassLoader().getResource("xsd/UBL-Invoice-2.1.xsd").getFile();
        File xsdFile = new File(filePath);
        XSDValidator xsdValidator = new XSDValidator(xsdFile);
        return xsdValidator.validate(bytes);
    }


    private Document getDocument(InputStream sourceInvoiceStream) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setIgnoringBoundaryWhitespace(true);
        return saxBuilder.build(sourceInvoiceStream);
    }


    static class MyUblToCenConverter extends Ubl2Cen {
        public MyUblToCenConverter(IReflections reflections, EigorConfiguration configuration) throws ConfigurationException {
            super(reflections, configuration);
        }

        @Override
        public ConversionResult<BG0000Invoice> applyOne2OneTransformationsBasedOnMapping(Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
            return super.applyOne2OneTransformationsBasedOnMapping(document, errors);
        }

        @Override
        public ConversionResult<BG0000Invoice> applyOne2ManyTransformationsBasedOnMapping(BG0000Invoice partialInvoice, Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
            return super.applyOne2ManyTransformationsBasedOnMapping(partialInvoice, document, errors);
        }


    }
}