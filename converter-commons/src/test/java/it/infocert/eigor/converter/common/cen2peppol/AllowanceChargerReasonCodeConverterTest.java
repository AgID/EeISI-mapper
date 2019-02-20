package it.infocert.eigor.converter.common.cen2peppol;

import static org.junit.Assert.assertEquals;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.AllowanceChargerReasonCodeConverter;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BT0098DocumentLevelAllowanceReasonCode;

public class AllowanceChargerReasonCodeConverterTest {

	private BG0000Invoice invoice;
	private Document doc;
	private AllowanceChargerReasonCodeConverter sut;

	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
		BG0020DocumentLevelAllowances bg0020 = new BG0020DocumentLevelAllowances();
		BT0098DocumentLevelAllowanceReasonCode bt0098 = new BT0098DocumentLevelAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code41);
		invoice.getBG0020DocumentLevelAllowances().add(bg0020);
		invoice.getBG0020DocumentLevelAllowances(0).getBT0098DocumentLevelAllowanceReasonCode().add(bt0098);
		this.doc = new Document(new Element("Invoice"));
		this.sut =  new AllowanceChargerReasonCodeConverter();
	}

	@Test
	public void shouldMapDefaultMapping() {
		 sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		 Element code = doc.getRootElement().getChild("AllowanceCharge").getChild("AllowanceChargeReasonCode");
		 System.out.print(code.getValue());

	}
}