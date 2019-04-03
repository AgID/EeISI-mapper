package it.infocert.eigor.converter.common.cen2peppol;

import it.infocert.eigor.converter.commons.cen2peppol.AllowanceDocumentConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BT0092DocumentLevelAllowanceAmount;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class AllowanceDocumentConverterTest {
    private BG0000Invoice invoice;
    private Document doc;
    private AllowanceDocumentConverter sut;

    @Before
    public void setUp() {
        invoice = new BG0000Invoice();
        BG0020DocumentLevelAllowances bg20 = new BG0020DocumentLevelAllowances();
        bg20.getBT0092DocumentLevelAllowanceAmount().add(new BT0092DocumentLevelAllowanceAmount(new BigDecimal(200)));

        this.doc = new Document(new Element("Invoice"));
        this.sut = new AllowanceDocumentConverter();
    }

    @Test
    public void shouldMapDefaultMapping() {


    }

}
