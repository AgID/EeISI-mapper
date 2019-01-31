package it.infocert.eigor.converter.cen2peoppl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.cen2peoppl.InvoiceLineConverter;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BG0002ProcessControl;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0016PaymentInstructions;
import it.infocert.eigor.model.core.model.BG0019DirectDebit;
import it.infocert.eigor.model.core.model.BT0002InvoiceIssueDate;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0006VatAccountingCurrencyCode;
import it.infocert.eigor.model.core.model.BT0007ValueAddedTaxPointDate;
import it.infocert.eigor.model.core.model.BT0009PaymentDueDate;
import it.infocert.eigor.model.core.model.BT0021InvoiceNoteSubjectCode;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;
import it.infocert.eigor.model.core.model.BT0034SellerElectronicAddressAndSchemeIdentifier;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.joda.time.LocalDate;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class InvoiceLineConverterTest {
    private Document document;

    @Before
    public void setUp() throws Exception {
        document = new Document(new Element("Invoice", Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2")));
    }

   
    
//    @Test 
//    public void checkBT0089MandateReferenceIdentifier() {
//    	
//    	BG0000Invoice cenInvoice =	makeBG0019Invoice();
//    	InvoiceLineConverter converter = new InvoiceLineConverter();
//        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);
//        Element rootElement = document.getRootElement();
//        Element paymentInstructions = rootElement.getChild("PaymentInstructions");
//        Element directDebit = paymentInstructions.getChild("DirectDebit");
//        Element bt0089MandateReferenceIdentifier = directDebit.getChild("MandateReferenceIdentifier");
//        
//        assertTrue(bt0089MandateReferenceIdentifier.getText().equals("NA"));
//        
//    }
    
    @Test
    public void setBT0010BuyerReferenceNA() throws Exception{
        BG0000Invoice cenInvoice = makeBuyerReference();
        
        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("BuyerReference");
        assertTrue("NA".equals(invoiceLine.getText()));

    	
    }
    
    
    @Test
    public void setBT0013PurchaseOrderReferenceNA() throws Exception{
        BG0000Invoice cenInvoice = makeBuyerReference();
        
        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("PurchaseOrderReference");
        assertTrue("NA".equals(invoiceLine.getText()));

    	
    }
    
    @Test
    public void checkCBT0005EqualityWithBT0006() throws Exception{
    	
    	BG0000Invoice cenInvoice =	invoiceCurrencyCodeAndVatAccountingCurrencyCode();
    	InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);

        Element rootElement = document.getRootElement();
        Element currencyCode = rootElement.getChild("InvoiceCurrencyCode");
        Element vatAccountingCurrencyCode = rootElement.getChild("VatAccountingCurrencyCode");
        
        
       assertTrue(!currencyCode.getText().isEmpty() && vatAccountingCurrencyCode == null);
        
    }
   
    

    @Test
    public void checkBT0023AndBT0024Value() {
    	BG0000Invoice cenInvoice =	makeProcessControl();
    	InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);

        Element rootElement = document.getRootElement();
        Element processControl = rootElement.getChild("ProcessControl");
        Element businessProcessType = processControl.getChild("BusinessProcessType");
        Element specificationIdentifier = processControl.getChild("SpecificationIdentifier");
        assertTrue(businessProcessType.getText().equals("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"));
        assertTrue(specificationIdentifier.getText().equals("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0"));
        
    }

    @Test
    public void concatenateBT0021InvoiceNoteSubjectCode() {
    	BG0000Invoice cenInvoice =	makeBG0001InvoiceNote();
    	InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);
        
        Element rootElement = document.getRootElement();
        Element invoiceNote = rootElement.getChild("InvoiceNote");
        
        assertTrue(invoiceNote.getText().equals("12-12"));

    }

    @Test
     public void invoiceWithSellerBT0034ShouldNotBeEmpty(){
    	 BG0000Invoice cenInvoice = makeCenBG0004Seller();
         InvoiceLineConverter converter = new InvoiceLineConverter();
         converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);
         Element rootElement = document.getRootElement();
         Element seller = rootElement.getChild("Seller");
         Element sellerElectronicAddressAndSchemeIdentifier = seller.getChild("SellerElectronicAddressAndSchemeIdentifier");
         Element id = sellerElectronicAddressAndSchemeIdentifier.getChild("identifier");
         Element identificationSchema = sellerElectronicAddressAndSchemeIdentifier.getChild("identificationSchema");
         assertTrue(identificationSchema.getText().equals("0130"));
         assertTrue(id.getText().contentEquals("NA"));
    }
    
    @Test
    public void invoiceWithDateFormatYYYYMMDD() {
    	BG0000Invoice cenInvoice = makeCENInvoiceWithDates();
        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);
        Element rootElement = document.getRootElement();
        Element invoiceIssueDate = rootElement.getChild("InvoiceIssueDate");
        Element valueAddedTaxPointDate = rootElement.getChild("ValueAddedTaxPointDate");
        Element paymentDueDate = rootElement.getChild("PaymentDueDate");
        assertTrue(invoiceIssueDate.getText().length() == 10);
        assertTrue(valueAddedTaxPointDate.getText().length() == 10);
        assertTrue(paymentDueDate.getText().length() == 10);

        
        
        
    
    
    }
    
    private BG0000Invoice makeCENInvoiceWithDates(){
    	BG0000Invoice invoice = new BG0000Invoice();
    	LocalDate lTime = new LocalDate(2004, 12, 25);
    	BT0002InvoiceIssueDate bt02 = new BT0002InvoiceIssueDate(lTime);
    	BT0007ValueAddedTaxPointDate bt07 = new BT0007ValueAddedTaxPointDate(lTime);
    	BT0009PaymentDueDate bt09 = new BT0009PaymentDueDate(lTime);
    	
    	invoice.getBT0002InvoiceIssueDate().add(bt02);
    	invoice.getBT0007ValueAddedTaxPointDate().add(bt07);
    	invoice.getBT0009PaymentDueDate().add(bt09);
    	
    	
    	return invoice;
    }
    
   private BG0000Invoice makeCenBG0004Seller() {
		BG0000Invoice invoice = new BG0000Invoice();
		BG0004Seller BG004 = new BG0004Seller();
		invoice.getBG0004Seller().add(BG004);
		
		return invoice;
   }
    
	private BG0000Invoice makeCENInvoiceWithBT0034(){
		BG0000Invoice invoice = new BG0000Invoice();
		BG0004Seller seller = new BG0004Seller();
		
		invoice.getBG0004Seller().add(seller);
		return invoice;
	}
    
    private BG0000Invoice makeBG0001InvoiceNote() {
    	
    	BG0000Invoice invoice = new BG0000Invoice();
    	BG0001InvoiceNote invoiceNote = new BG0001InvoiceNote();
    	BT0021InvoiceNoteSubjectCode BT0016= new BT0021InvoiceNoteSubjectCode("12");
    	BT0022InvoiceNote BT0116= new BT0022InvoiceNote("12");
    	
    	invoiceNote.getBT0021InvoiceNoteSubjectCode().add(BT0016);
    	invoiceNote.getBT0022InvoiceNote().add(BT0116);
    	invoice.getBG0001InvoiceNote().add(invoiceNote);
    	
    	return invoice;
    }
    
	private BG0000Invoice makeBG0019Invoice(){
		
		BG0000Invoice invoice = new BG0000Invoice();
		BG0019DirectDebit bg0019 = new BG0019DirectDebit();
		BG0016PaymentInstructions bg0016 = new BG0016PaymentInstructions();
		bg0016.getBG0019DirectDebit().add(bg0019);
		
		invoice.getBG0016PaymentInstructions().add(bg0016);
		
		return invoice;
	}
    
    private BG0000Invoice makeProcessControl() {
    	 BG0000Invoice invoice = new BG0000Invoice();
    	 BG0002ProcessControl bg002 = new BG0002ProcessControl(); 
    	 invoice.getBG0002ProcessControl().add(bg002);
    	 
    	 return invoice;    
    }
    
    private BG0000Invoice makeBuyerReference() {
        BG0000Invoice invoice = new BG0000Invoice();
        return invoice;
    }
    
    private BG0000Invoice invoiceCurrencyCodeAndVatAccountingCurrencyCode() {
    	 BG0000Invoice invoice = new BG0000Invoice();
    	 BT0005InvoiceCurrencyCode bt0005 = new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.AED);
    	 BT0006VatAccountingCurrencyCode bt0006 = new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.AED);
    	 invoice.getBT0005InvoiceCurrencyCode().add(bt0005);
    	 invoice.getBT0006VatAccountingCurrencyCode().add(bt0006);
    	 
    	 return invoice;
    			 
    	}


    
    
}
