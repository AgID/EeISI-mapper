package it.infocert.eigor.converter.cen2peoppl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0002ProcessControl;
import it.infocert.eigor.model.core.model.BG0019DirectDebit;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0026InvoiceLinePeriod;
import it.infocert.eigor.model.core.model.BG0029PriceDetails;
import it.infocert.eigor.model.core.model.BG0030LineVatInformation;
import it.infocert.eigor.model.core.model.BG0031ItemInformation;
import it.infocert.eigor.model.core.model.BG0032ItemAttributes;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0126InvoiceLineIdentifier;
import it.infocert.eigor.model.core.model.BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0130InvoicedQuantityUnitOfMeasureCode;
import it.infocert.eigor.model.core.model.BT0131InvoiceLineNetAmount;
import it.infocert.eigor.model.core.model.BT0146ItemNetPrice;
import it.infocert.eigor.model.core.model.BT0149ItemPriceBaseQuantity;
import it.infocert.eigor.model.core.model.BT0151InvoicedItemVatCategoryCode;
import it.infocert.eigor.model.core.model.BT0152InvoicedItemVatRate;
import it.infocert.eigor.model.core.model.BT0153ItemName;
import it.infocert.eigor.model.core.model.BT0160ItemAttributeName;
import it.infocert.eigor.model.core.model.BT0161ItemAttributeValue;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InvoiceLineConverter implements CustomMapping<Document> {
	private static final Logger log = LoggerFactory.getLogger(InvoiceLineConverter.class);

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

		Element root = document.getRootElement();
		if (root != null) {
			if (!cenInvoice.getBG0025InvoiceLine().isEmpty()) {

				Iso4217CurrenciesFundsCodes currencyCode = null;
				if(!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
					BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
					currencyCode = bt0005.getValue();

				}
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
				 if(cenInvoice.getBT0005InvoiceCurrencyCode().equals(cenInvoice.getBT0006VatAccountingCurrencyCode())) {
					 final Element invoiceCurrencyCode = new Element("InvoiceCurrencyCode");
					 BT0005InvoiceCurrencyCode Bt05 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
					 invoiceCurrencyCode.setText(invoiceCurrencyCode.getValue());
					  
					root.addContent(invoiceCurrencyCode);
				 }
				 
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


				 /**
				  *  if bg-19 exist and and bt-89 is empty set bt-19=NA
				  * 
				  */
				 List<BG0019DirectDebit> bg0019 = cenInvoice.getBG0016PaymentInstructions(0).getBG0019DirectDebit();
				 for(BG0019DirectDebit elemBg19: bg0019) {
					 Element DirectDebit = new Element("DirectDebit");
					 if(elemBg19.getBT0089MandateReferenceIdentifier().isEmpty()) {
						 Element mandateReferenceIdentifier = new Element("MandateReferenceIdentifier");
						 mandateReferenceIdentifier.setText("NA");
						 DirectDebit.addContent(mandateReferenceIdentifier);
					 }
				 }

				 /**
				  *
				  * if baseamount or percentage not calculated simply delete them
				  * 
				  */
				 List<BG0020DocumentLevelAllowances> bg0020 = cenInvoice.getBG0020DocumentLevelAllowances();
				 for(BG0020DocumentLevelAllowances elemBg20: bg0020) {
					 Element documentLevelAllowancePercentage = new Element("");
					 if(elemBg20.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
						 
					 }
					 
					 if(elemBg20.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {

						 
					 }
				 }
				 
				 
				 
				 List<BG0025InvoiceLine> bg0025 = cenInvoice.getBG0025InvoiceLine();
				 for (BG0025InvoiceLine elemBg25 : bg0025) {
					 Element invoiceLine = new Element("InvoiceLine");
					 if(!elemBg25.getBT0126InvoiceLineIdentifier().isEmpty()) {
						 BT0126InvoiceLineIdentifier bt0126 = elemBg25.getBT0126InvoiceLineIdentifier(0);
						 Element id = new Element("ID");
						 id.setText(bt0126.getValue());
						 invoiceLine.addContent(id);
					 }

					 if(!elemBg25.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().isEmpty()) {
						 Element documentReference = new Element("DocumentReference");
						 BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = elemBg25.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(0);
						 Element documentTypeCode = new Element("DocumentTypeCode");
						 documentTypeCode.setText("130");
						 Element id = new Element("ID");
						 id.setText(bt0128.getValue().getIdentifier());
						 id.setAttribute("schemeID", bt0128.getValue().getIdentificationSchema());
						 documentReference.addContent(id);
						 documentReference.addContent(documentTypeCode);
						 invoiceLine.addContent(documentReference);
					 }

					 if (!elemBg25.getBT0129InvoicedQuantity().isEmpty()) {

						 BigDecimal quantity;
						 BigDecimal bt129Quantity = elemBg25.getBT0129InvoicedQuantity().isEmpty() ? BigDecimal.ZERO : elemBg25.getBT0129InvoicedQuantity(0).getValue();

						 if (!elemBg25.getBG0029PriceDetails(0).getBT0149ItemPriceBaseQuantity().isEmpty()) {
							 BigDecimal bt0149BaseQuantity = elemBg25.getBG0029PriceDetails(0).getBT0149ItemPriceBaseQuantity(0).getValue();
							 quantity = bt129Quantity.divide(bt0149BaseQuantity, RoundingMode.HALF_UP);
						 } else {
							 quantity = bt129Quantity;
						 }

						 Element invoicedQuantity = new Element("InvoicedQuantity");
						 invoicedQuantity.setText(quantity.setScale(8, RoundingMode.HALF_UP).toString());

						 if (!elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty()) {
							 BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode(0);
							 if (bt0130 != null) {
								 UnitOfMeasureCodes unitOfMeasureCodes = bt0130.getValue();
								 Attribute unitCode = new Attribute("unitCode", unitOfMeasureCodes.getCommonCode());
								 invoicedQuantity.setAttribute(unitCode);
							 }
						 }
						 invoiceLine.addContent(invoicedQuantity);
					 }

					 if (!elemBg25.getBT0131InvoiceLineNetAmount().isEmpty()) {
						 BT0131InvoiceLineNetAmount bt0131 = elemBg25.getBT0131InvoiceLineNetAmount(0);
						 Element lineExtensionAmount = new Element("LineExtensionAmount");
						 final BigDecimal value = bt0131.getValue();
						 lineExtensionAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
						 if (currencyCode != null) {
							 lineExtensionAmount.setAttribute(new Attribute("currencyID", currencyCode.name()));
						 }
						 invoiceLine.addContent(lineExtensionAmount);
					 }

					 if (!elemBg25.getBG0026InvoiceLinePeriod().isEmpty()) {
						 List<BG0026InvoiceLinePeriod> bg0026 = elemBg25.getBG0026InvoiceLinePeriod();
						 for (BG0026InvoiceLinePeriod ignored : bg0026) {
							 Element invoicePeriod = new Element("InvoicePeriod");
							 if(invoicePeriod!=null && !invoicePeriod.getChildren().isEmpty()) {
								 invoiceLine.addContent(invoicePeriod);
							 }
						 }
					 }

					 if (!elemBg25.getBG0031ItemInformation().isEmpty()) {
						 List<BG0031ItemInformation> bg0031 = elemBg25.getBG0031ItemInformation();
						 for (BG0031ItemInformation elemBg31 : bg0031) {
							 Element item = new Element("Item");
							 if (!elemBg31.getBT0153ItemName().isEmpty()) {
								 BT0153ItemName bt0153 = elemBg31.getBT0153ItemName(0);
								 Element name = new Element("Name");
								 name.setText(bt0153.getValue());
								 item.addContent(name);
							 }
							 if (!elemBg25.getBG0030LineVatInformation().isEmpty()) {
								 List<BG0030LineVatInformation> bg0030 = elemBg25.getBG0030LineVatInformation();
								 for (BG0030LineVatInformation elemBg30 : bg0030) {
									 Element classifiedTaxCategory = new Element("ClassifiedTaxCategory");
									 if (!elemBg30.getBT0151InvoicedItemVatCategoryCode().isEmpty()) {
										 BT0151InvoicedItemVatCategoryCode bt0151 = elemBg30.getBT0151InvoicedItemVatCategoryCode(0);
										 Element id = new Element("ID");
										 Untdid5305DutyTaxFeeCategories dutyTaxFeeCategories = bt0151.getValue();
										 id.setText(dutyTaxFeeCategories.name());
										 classifiedTaxCategory.addContent(id);
									 }
									 if (!elemBg30.getBT0152InvoicedItemVatRate().isEmpty()) {
										 BT0152InvoicedItemVatRate bt0152 = elemBg30.getBT0152InvoicedItemVatRate(0);
										 Element percent = new Element("Percent");
										 final BigDecimal value = bt0152.getValue();
										 percent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
										 classifiedTaxCategory.addContent(percent);
									 }
									 Element taxScheme = new Element("TaxScheme");
									 Element id = new Element("ID");
									 id.setText("VAT");
									 taxScheme.addContent(id);
									 classifiedTaxCategory.addContent(taxScheme);

									 item.addContent(classifiedTaxCategory);
								 }
							 }

							 if (!elemBg31.getBG0032ItemAttributes().isEmpty()) {
								 List<BG0032ItemAttributes> bg0032 = elemBg31.getBG0032ItemAttributes();
								 for (BG0032ItemAttributes elemBg32 : bg0032) {
									 Element additionalItemProperty = new Element("AdditionalItemProperty");
									 if (!elemBg32.getBT0160ItemAttributeName().isEmpty()) {
										 BT0160ItemAttributeName bt0160 = elemBg32.getBT0160ItemAttributeName(0);
										 Element name = new Element("Name");
										 name.setText(bt0160.getValue());
										 additionalItemProperty.addContent(name);
									 }
									 if (!elemBg32.getBT0161ItemAttributeValue().isEmpty()) {
										 BT0161ItemAttributeValue bt0161 = elemBg32.getBT0161ItemAttributeValue(0);
										 Element value = new Element("Value");
										 value.setText(bt0161.getValue());
										 additionalItemProperty.addContent(value);
									 }
									 item.addContent(additionalItemProperty);
								 }
							 }

							 invoiceLine.addContent(item);
						 }
					 }

					 if (!elemBg25.getBG0029PriceDetails().isEmpty()) {
						 List<BG0029PriceDetails> bg0029 = elemBg25.getBG0029PriceDetails();
						 for (BG0029PriceDetails elemBg29 : bg0029) {
							 Element price = new Element("Price");
							 if (!elemBg29.getBT0146ItemNetPrice().isEmpty()) {
								 BT0146ItemNetPrice bt0146 = elemBg29.getBT0146ItemNetPrice(0);
								 Element priceAmount = new Element("PriceAmount");
								 final BigDecimal value = bt0146.getValue();
								 priceAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
								 if (currencyCode != null) {
									 priceAmount.setAttribute(new Attribute("currencyID", currencyCode.name()));
								 }
								 price.addContent(priceAmount);
							 }
							 if (!elemBg29.getBT0149ItemPriceBaseQuantity().isEmpty()) {
								 BT0149ItemPriceBaseQuantity bt0149 = elemBg29.getBT0149ItemPriceBaseQuantity(0);
								 Element baseQuantity = new Element("BaseQuantity");
								 final BigDecimal value = bt0149.getValue();
								 baseQuantity.setText(value.setScale(2, RoundingMode.HALF_UP).toString());

								 /*
                                 In case BT-130 different from BT-150 set BT-150=BT-130.
                                 If different from UNECE 20 and 21 BT-130 is set =EA or C62. No values for BT-150 thus cannot be different
                                 (Following previous rule, actually BT150 won't be mapped and BT130 value is used instead, except for C62 and EA
                                  which are will not be mapped)
								  */
								 if (!elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty()) {
									 final BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode().get(0);
									 UnitOfMeasureCodes unitOfMeasureCodes = bt0130.getValue();
									 if(!UnitOfMeasureCodes.C62_ONE.equals(unitOfMeasureCodes) && !UnitOfMeasureCodes.EACH_EA.equals(unitOfMeasureCodes)) {
										 Attribute unitCode = new Attribute("unitCode", unitOfMeasureCodes.getCommonCode());
										 baseQuantity.setAttribute(unitCode);
									 }
								 }
								 price.addContent(baseQuantity);
							 }
							 invoiceLine.addContent(price);
						 }
					 }

					 root.addContent(invoiceLine);
					 //                    root.addContent(ProcessControl);
				 }
			}

		}
	}

	
}
