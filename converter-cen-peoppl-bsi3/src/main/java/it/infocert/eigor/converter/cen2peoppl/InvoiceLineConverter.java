package it.infocert.eigor.converter.cen2peoppl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InvoiceLineConverter implements CustomMapping<Document> {

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub


		Element root = document.getRootElement();
		if (root != null) {

			
			/**
			 *  if bg-19 exist and and bt-89 is empty set bt-19=NA
			 * 
			 */
			
//			List<BG0019DirectDebit> bg0019 = cenInvoice.getBG0016PaymentInstructions(0).getBG0019DirectDebit();
//			if(!bg0019.isEmpty()) {
//				Element paymentInstructions = new Element("PaymentInstructions");
//				for(BG0019DirectDebit elemBg19: bg0019) {
//					Element directDebit = new Element("DirectDebit");
//					Element mandateReferenceIdentifier = new Element("MandateReferenceIdentifier");
//					if(elemBg19.getBT0089MandateReferenceIdentifier().isEmpty()) {
//
//						mandateReferenceIdentifier.setText("NA");
//						directDebit.addContent(mandateReferenceIdentifier);
//						paymentInstructions.addContent(directDebit);
//					}else {
//						mandateReferenceIdentifier.setText(elemBg19.getBT0089MandateReferenceIdentifier(0).getValue());
//						directDebit.addContent(mandateReferenceIdentifier);
//						paymentInstructions.addContent(directDebit);
//
//					}
//					root.addContent(paymentInstructions);
//				}
//			}
				/**
				 * BT 10 and 13
				 */
				if(cenInvoice.getBT0010BuyerReference().isEmpty() && cenInvoice.getBT0013PurchaseOrderReference().isEmpty()) {

					final Element BuyerReference = new Element("BuyerReference"); 
					final Element PurchaseOrderReference = new Element("PurchaseOrderReference");
					PurchaseOrderReference.setText("NA");
					BuyerReference.setText("NA");
					root.addContent(BuyerReference);
					root.addContent(PurchaseOrderReference);

				}

				/**
				 * if BT-6 = BT-5 only map BT-5
				 * 
				 */
				if(!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty() && !cenInvoice.getBT0006VatAccountingCurrencyCode().isEmpty()) {
					final Element invoiceCurrencyCode = new Element("InvoiceCurrencyCode");		 
					final Element acctCurrencyCode = new Element("VatAccountingCurrencyCode");
					BT0005InvoiceCurrencyCode Bt05 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
					BT0006VatAccountingCurrencyCode Bt06 = cenInvoice.getBT0006VatAccountingCurrencyCode(0);
					if(Bt05.getValue().toString().equals(Bt06.getValue().toString())) {

						invoiceCurrencyCode.setText(Bt05.getValue().toString());		
						root.addContent(invoiceCurrencyCode);	
					}
					else {
						invoiceCurrencyCode.setText(Bt05.getValue().toString());
						acctCurrencyCode.setText(Bt06.getValue().toString());
						root.addContent(acctCurrencyCode);
						root.addContent(invoiceCurrencyCode);					 
					}

				}
//
//				/**
//				 * 
//				 * if BT10 and BT13 empty map BT10 = NA
//				 */
//				BT0010BuyerReference Bt10 = cenInvoice.getBT0010BuyerReference(0);
//				BT0013PurchaseOrderReference Bt13 = cenInvoice.getBT0013PurchaseOrderReference(0);
//				final Element purchaseOrder = new Element("PurchaseOrderReference");
//				final Element BuyerReference = new Element("BuyerReference");
//
//				if(Bt10.getValue().isEmpty() && Bt13.getValue().isEmpty()) {
//					purchaseOrder.setText("NA");
//					//root.addContent(purchaseOrder);
//
//				}else {
//					BuyerReference.setText(Bt10.getValue());
//					purchaseOrder.setText(Bt13.getValue());
//				//	root.addContent(purchaseOrder);
//					//root.addContent(BuyerReference);
//				}
//			
				/**
				 * rule for BT-23 and BT-24
				 * 
				 */
				List<BG0002ProcessControl> bg0002 = cenInvoice.getBG0002ProcessControl();
				for(BG0002ProcessControl elembg02: bg0002) {
					Element ProcessControl = new Element("ProcessControl");
					if(elembg02.getBT0023BusinessProcessType().isEmpty()) {
						Element businessProcessType = new Element("BusinessProcessType");
						businessProcessType.setText("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");
						ProcessControl.addContent(businessProcessType);

					}
					if(elembg02.getBT0024SpecificationIdentifier().isEmpty()) {
						Element SpecificationIdentifier = new Element("SpecificationIdentifier");
						SpecificationIdentifier.setText("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0");
						ProcessControl.addContent(SpecificationIdentifier);

					}

					root.addContent(ProcessControl);
				}
//
				
//
//				/**
//				 * if base amount or percentage not calculated and charge amount present calculate one from another
//				 * if base amount or percentage present and charge amount value differ from their division calculation, replace 
//				 * 
//				 */
//				List<BG0021DocumentLevelCharges> bg0021 = cenInvoice.getBG0021DocumentLevelCharges();
//				if(!bg0021.isEmpty()) {
//					for(BG0021DocumentLevelCharges elemBg21: bg0021) {
//
//						Element levelCharge = new Element("DocumentLevelCharges");
//						Element ChargeAmount= new Element("DocumentLevelCharges");
//						Element percentage = new Element("DocumentLevelChargePercentage");
//						Element baseAmount = new Element("DocumentLevelChargeBaseAmount");
//
//						if((elemBg21.getBT0100DocumentLevelChargeBaseAmount(0).getValue() == null) &&
//								(elemBg21.getBT0101DocumentLevelChargePercentage(0).getValue() != null) &&
//								(elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue() != null)) {
//
//							percentage.setText(elemBg21.getBT0101DocumentLevelChargePercentage(0).getValue().toString());
//							ChargeAmount.setText(elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue().toString());
//							baseAmount.setText(elemBg21.getBT0101DocumentLevelChargePercentage(0).getValue().multiply( 
//									elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue()).toString());
//
//							levelCharge.addContent(percentage);
//							levelCharge.addContent(baseAmount);
//							levelCharge.addContent(ChargeAmount);
//
//						}
//						if((elemBg21.getBT0100DocumentLevelChargeBaseAmount(0).getValue() != null) &&
//								(elemBg21.getBT0101DocumentLevelChargePercentage(0).getValue() == null) &&
//								(elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue() != null)) {
//
//							ChargeAmount.setText(elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue().toString());
//							baseAmount.setText(elemBg21.getBT0100DocumentLevelChargeBaseAmount(0).getValue().toString());
//							percentage.setText(elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue().multiply( 
//									elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue()).toString());
//
//							levelCharge.addContent(percentage);
//							levelCharge.addContent(baseAmount);
//							levelCharge.addContent(ChargeAmount);
//
//						}
//						if((elemBg21.getBT0100DocumentLevelChargeBaseAmount(0).getValue() != null) &&
//								(elemBg21.getBT0101DocumentLevelChargePercentage(0).getValue() != null) &&
//								(elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue() != null)){
//
//							percentage.setText(elemBg21.getBT0101DocumentLevelChargePercentage(0).getValue().toString());
//							baseAmount.setText(elemBg21.getBT0100DocumentLevelChargeBaseAmount(0).getValue().toString());
//							ChargeAmount.setText(elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue().divide(
//									elemBg21.getBT0099DocumentLevelChargeAmount(0).getValue(), 3, RoundingMode.HALF_UP).toString());
//
//
//
//							levelCharge.addContent(percentage);
//							levelCharge.addContent(baseAmount);
//							levelCharge.addContent(ChargeAmount);
//						}
//
//					}
//				}
//
				/**
				 * BT-21 concatenate
				 * 
				 */
				List<BG0001InvoiceNote> bg01 = cenInvoice.getBG0001InvoiceNote();
				if(!bg01.isEmpty()) {
					Element invoiceNote = new Element("InvoiceNote");
					String invoiceTag = "";
				for(BG0001InvoiceNote  elemBg01: bg01) {
					
					if(!elemBg01.getBT0022InvoiceNote(0).getValue().isEmpty()) {
						invoiceTag = elemBg01.getBT0022InvoiceNote(0).getValue();
					}		
					if(!elemBg01.getBT0021InvoiceNoteSubjectCode(0).getValue().isEmpty()) {
						invoiceTag = invoiceTag +  "-" + elemBg01.getBT0021InvoiceNoteSubjectCode(0).getValue();
					}
								
					
				}
				
					invoiceNote.setText(invoiceTag);
					root.addContent(invoiceNote);
				}

				/**
				 * if BT-34 not present ="NA"
				 * 
				 */			
				List<BG0004Seller> bg04 = cenInvoice.getBG0004Seller();
				if(!bg04.isEmpty()) {
					for(BG0004Seller elemBg04: bg04) {
						Element seller = new Element("Seller");
						if(elemBg04.getBT0034SellerElectronicAddressAndSchemeIdentifier().isEmpty()) {
							Element sellerElectronic = new Element("SellerElectronicAddressAndSchemeIdentifier");
							Element identifier = new Element("identifier");
							identifier.setText("NA");
							Element identificationSchema = new Element("identificationSchema");
							identificationSchema.setText("0130");
							sellerElectronic.addContent(identifier);
							sellerElectronic.addContent(identificationSchema);
							seller.addContent(sellerElectronic);
						}
						else
						{
							seller.addContent(elemBg04.getBT0034SellerElectronicAddressAndSchemeIdentifier(0).getValue().toString());
						}
						root.addContent(seller);
					}
				}
//				/**
//				 * BT-49 mandatory, not present ="NA"
//				 * 
//				 */
//				List<BG0007Buyer> bg07 = cenInvoice.getBG0007Buyer();
//				if(!bg07.isEmpty()) {
//					for(BG0007Buyer elemBg07: bg07) {
//						Element buyer = new Element("Buyer");
//						if(elemBg07.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0).getValue() != null) {
//							Element buyerElectronic = new Element("BuyerElectronicAddressAndSchemeIdentifier");
//							Element identifier = new Element("identifier");
//							identifier.setText("NA");
//							Element identificationSchema = new Element("identificationSchema");
//							identificationSchema.setText("0130");
//							buyerElectronic.addContent(identifier);
//							buyerElectronic.addContent(identificationSchema);
//							buyer.addContent(buyerElectronic);
//
//						}
//						//						buyer.addContent(elemBg07.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0));
//					}
//				}
				
				
//				/**
//				 * BT-98 set value to Code95 if its value is different from specified list
//				 * 
//				 */
//				List<BT0098DocumentLevelAllowanceReasonCode> bt0098 = cenInvoice.getBG0020DocumentLevelAllowances(0).getBT0098DocumentLevelAllowanceReasonCode();
//				Element DocumentLevelAllowances = new Element("DocumentLevelAllowances");
//				if(!bt0098.isEmpty()) {
//					for(BT0098DocumentLevelAllowanceReasonCode elemBt98: bt0098) {
//						Element documentLevelAllowance = new Element("DocumentLevelAllowanceReasonCode");
//						Untdid5189ChargeAllowanceDescriptionCodes unitCode95 = Untdid5189ChargeAllowanceDescriptionCodes.Code95;
//						if(!Untdid5189ChargeAllowanceDescriptionCodes.Code41.equals(elemBt98.getValue()) ||
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code42.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code60.equals(elemBt98.getValue()) ||
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code62.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code63.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code64.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code65.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code66.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code67.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code68.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code70.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code71.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code88.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code95.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code100.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code102.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code103.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code104.equals(elemBt98.getValue()) || 
//								!Untdid5189ChargeAllowanceDescriptionCodes.Code105.equals(elemBt98.getValue()))
//						{
//							Attribute name = new Attribute("Untdid5189ChargeAllowanceDescriptionCodes", unitCode95.toString());
//							documentLevelAllowance.setAttribute(name);
//							DocumentLevelAllowances.addContent(documentLevelAllowance);
//
//
//						}
//						else {
//							Attribute name = new Attribute("Untdid5189ChargeAllowanceDescriptionCodes", elemBt98.getValue().toString());
//							documentLevelAllowance.setAttribute(name);
//							DocumentLevelAllowances.addContent(elemBt98.getValue().toString());
//						}
//
//					}
//				}
//
				/**
				 * format Date with(YYY-MM-DD) 
				 * 
				 */
				List<BT0002InvoiceIssueDate> bt02 =  cenInvoice.getBT0002InvoiceIssueDate();
				if(!bt02.isEmpty()) { 
					for(BT0002InvoiceIssueDate elembt02: bt02) {
						if(elembt02.getValue() != null) {
							Element issueDate = new Element("InvoiceIssueDate");
							String formattedDate = elembt02.getValue().toString("yyyy-mm-dd");
							issueDate.addContent(formattedDate);
							root.addContent(issueDate);
						}
					}
				}
				
				List<BT0007ValueAddedTaxPointDate> bt07 =  cenInvoice.getBT0007ValueAddedTaxPointDate();
				if(!bt07.isEmpty()){ 
					for(BT0007ValueAddedTaxPointDate elembt07: bt07) {
						if(elembt07.getValue() != null) {
							Element addedTaxDate = new Element("ValueAddedTaxPointDate");
							String formattedDate = elembt07.getValue().toString("yyyy-mm-dd");
							addedTaxDate.addContent(formattedDate);
							root.addContent(addedTaxDate);
						}
					}
				}
				

			List<BT0009PaymentDueDate> bt09 =  cenInvoice.getBT0009PaymentDueDate();
			if(!bt09.isEmpty()) { 
				for(BT0009PaymentDueDate elembt09: bt09) {
					if(elembt09.getValue() != null) {
						Element paymentDueDate = new Element("PaymentDueDate");
						String formattedDate = elembt09.getValue().toString("yyyy-mm-dd");
						paymentDueDate.addContent(formattedDate);
						root.addContent(paymentDueDate);
					}
				}
			}
	}
}




}
