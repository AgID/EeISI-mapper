package it.infocert.eigor.converter.common.cen2peppol;

import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.ProjectReferenceConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0011ProjectReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

@Ignore
public class ProjectReferenceTest {
    private Document document;

    @Before
    public void setUp() throws Exception {
        document = new Document(new Element("Invoice", Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2")));
    }

    @Test
    public void invoiceLineWithBT0128shouldHaveDocumentReferenceAndTypeCode() throws Exception {
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0128();
        ProjectReferenceConverter converter = new ProjectReferenceConverter();
        converter.map(cenInvoice, document, new ArrayList<>(), ErrorCode.Location.UBL_OUT, null);

        Element rootElement = document.getRootElement();
        Element additionalDocumentReference = rootElement.getChild("ProjectReference");

        Element id = additionalDocumentReference.getChild("ID");
        assertTrue("bt11".equals(id.getText()));

       
    }


    private BG0000Invoice makeCenInvoiceWithBT0128() {
        BG0000Invoice invoice = new BG0000Invoice();

        BT0011ProjectReference projectReference = new BT0011ProjectReference("bt11");
        invoice.getBT0011ProjectReference().add(projectReference);

        return invoice;
    }

}
