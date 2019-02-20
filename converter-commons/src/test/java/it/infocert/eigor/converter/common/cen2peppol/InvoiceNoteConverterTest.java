
package it.infocert.eigor.converter.common.cen2peppol;

import static org.junit.Assert.assertEquals;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.InvoiceNoteConverter;
import it.infocert.eigor.converter.commons.cen2peppol.InvoiceTypeCodeConverter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BG0002ProcessControl;
import it.infocert.eigor.model.core.model.BG0022DocumentTotals;
import it.infocert.eigor.model.core.model.BT0003InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BT0021InvoiceNoteSubjectCode;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;

public class InvoiceNoteConverterTest {

	private BG0000Invoice invoice;
	private Document doc;
	private InvoiceNoteConverter sut;
	
	@Before
	public void setUp() {
		invoice = new BG0000Invoice();		
		invoice.getBG0001InvoiceNote().add(new BG0001InvoiceNote());
		invoice.getBG0002ProcessControl().add(new BG0002ProcessControl());
		invoice.getBG0001InvoiceNote(0).getBT0021InvoiceNoteSubjectCode().add(new BT0021InvoiceNoteSubjectCode("bt0021"));
		invoice.getBG0001InvoiceNote(0).getBT0022InvoiceNote().add(new BT0022InvoiceNote("bt0022"));

		
		this.doc = new Document(new Element("Invoice"));
		this.sut =  new InvoiceNoteConverter();
	}
	
	@Test
	public void shouldMapDefault() { 
		 sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		 Element name = doc.getRootElement().getChild("Note");
		 System.out.println(name.getText());
			
//		 assertEquals("3", name.getText());
	}
}