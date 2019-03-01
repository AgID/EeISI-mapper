package it.infocert.eigor.converter.commons.cen2peppol;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.BigDecimalToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0099DocumentLevelChargeAmount;
import it.infocert.eigor.model.core.model.BT0100DocumentLevelChargeBaseAmount;
import it.infocert.eigor.model.core.model.BT0101DocumentLevelChargePercentage;
import it.infocert.eigor.model.core.model.BT0102DocumentLevelChargeVatCategoryCode;
import it.infocert.eigor.model.core.model.BT0104DocumentLevelChargeReason;
import it.infocert.eigor.model.core.model.BT0105DocumentLevelChargeReasonCode;

public class AllowanceChargeConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub

		TypeConverter<BigDecimal, String> bdStrConverter = BigDecimalToStringConverter.newConverter("#0.00");
		Iso4217CurrenciesFundsCodes currencyCode = null;
		if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
			BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
			currencyCode = bt0005.getValue();
		}

		Element root = document.getRootElement();
		if (root != null) {
			for (BG0021DocumentLevelCharges bg0021 : cenInvoice.getBG0021DocumentLevelCharges()) {

				BigDecimal percent = null, base = null, actualamount = null;

				Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
				Element baseAmount = new Element("BaseAmount");
				Element amount = new Element("Amount");



				Element allowanceCharge = new Element("AllowanceCharge");
				allowanceCharge.addContent(new Element("ChargeIndicator").setText("true"));


				
				if (!bg0021.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
					BT0105DocumentLevelChargeReasonCode bt0105 = bg0021.getBT0105DocumentLevelChargeReasonCode(0);
					Element allowanceChargeReasonCode = new Element("AllowanceChargeReasonCode");
					allowanceChargeReasonCode.setText(bt0105.getValue().name());
					allowanceCharge.addContent(allowanceChargeReasonCode);
				}

				if (!bg0021.getBT0104DocumentLevelChargeReason().isEmpty()) {
					Element allowanceChargeReason = new Element("AllowanceChargeReason");

					BT0104DocumentLevelChargeReason bt0104 = bg0021.getBT0104DocumentLevelChargeReason(0);
					allowanceChargeReason.setText(bt0104.getValue());
					allowanceCharge.addContent(allowanceChargeReason);
				} 


				if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
					BT0101DocumentLevelChargePercentage bt0101 = bg0021.getBT0101DocumentLevelChargePercentage(0);
					percent = bt0101.getValue();


				}

				if(!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
					BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
					actualamount = bt0099.getValue();

				}

				if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
					BT0100DocumentLevelChargeBaseAmount bt0100 = bg0021.getBT0100DocumentLevelChargeBaseAmount(0);

					base = bt0100.getValue();

				} 

				if(percent == null && base!=null) {
					percent = calculateAllowancePercentage(actualamount, base);

					try {
						if (currencyCode != null) {
							amount.setAttribute("currencyID", currencyCode.getCode());
							baseAmount.setAttribute("currencyID", currencyCode.getCode());

						}
						amount.setText(bdStrConverter.convert(actualamount));
						baseAmount.setText(bdStrConverter.convert(base));
						multiplierFactorNumeric.setText(bdStrConverter.convert(percent));

					} catch (ConversionFailedException e) {
						errors.add(ConversionIssue.newError(
								e,
								e.getMessage(),
								callingLocation,
								ErrorCode.Action.HARDCODED_MAP,
								ErrorCode.Error.ILLEGAL_VALUE,
								Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, baseAmount.toString())
								));
					}


					allowanceCharge.addContent(multiplierFactorNumeric);
					allowanceCharge.addContent(amount);
					allowanceCharge.addContent(baseAmount);
					root.addContent(allowanceCharge);


				}
				else if(percent != null && base==null) {	
					base = calculateAllowanceBase(actualamount , percent);
					if (currencyCode != null) {
						amount.setAttribute("currencyID", currencyCode.getCode());
						baseAmount.setAttribute("currencyID", currencyCode.getCode());

					}
					try {
						amount.setText(bdStrConverter.convert(actualamount));
						baseAmount.setText(bdStrConverter.convert(base));
						multiplierFactorNumeric.setText(bdStrConverter.convert(percent));

					} catch (ConversionFailedException e) {
						errors.add(ConversionIssue.newError(
								e,
								e.getMessage(),
								callingLocation,
								ErrorCode.Action.HARDCODED_MAP,
								ErrorCode.Error.ILLEGAL_VALUE,
								Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, baseAmount.toString())
								));
					}
					
					allowanceCharge.addContent(multiplierFactorNumeric);
					allowanceCharge.addContent(amount);
					allowanceCharge.addContent(baseAmount);
					root.addContent(allowanceCharge);

				}
				else if(percent == null && base == null) { 

					
					try {
						amount.setText(bdStrConverter.convert(actualamount));
						if (currencyCode != null) {
							amount.setAttribute("currencyID", currencyCode.getCode());

						}
					} catch (ConversionFailedException e) {
						errors.add(ConversionIssue.newError(
								e,
								e.getMessage(),
								callingLocation,
								ErrorCode.Action.HARDCODED_MAP,
								ErrorCode.Error.ILLEGAL_VALUE,
								Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, baseAmount.toString())
								));
					}
					allowanceCharge.addContent(amount);
					root.addContent(allowanceCharge);

				}
				else {  

					if (currencyCode != null) {
						amount.setAttribute("currencyID", currencyCode.getCode());
						baseAmount.setAttribute("currencyID", currencyCode.getCode());

					}
					
					BigDecimal calculation = base.multiply(percent);
					int result = actualamount.compareTo(calculation);
					
					if(result == 0) {

						
						try {
							amount.setText(bdStrConverter.convert(actualamount));
							baseAmount.setText(bdStrConverter.convert(base));
							multiplierFactorNumeric.setText(bdStrConverter.convert(percent));
							
						} catch (ConversionFailedException e) {
							errors.add(ConversionIssue.newError(
									e,
									e.getMessage(),
									callingLocation,
									ErrorCode.Action.HARDCODED_MAP,
									ErrorCode.Error.ILLEGAL_VALUE,
									Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
									Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, baseAmount.toString())
									));
						}

						allowanceCharge.addContent(multiplierFactorNumeric);
						allowanceCharge.addContent(amount);
						allowanceCharge.addContent(baseAmount);
						root.addContent(allowanceCharge);
					}else {
						try {
							amount.setText(bdStrConverter.convert(actualamount));
							
							
						} catch (ConversionFailedException e) {
							errors.add(ConversionIssue.newError(
									e,
									e.getMessage(),
									callingLocation,
									ErrorCode.Action.HARDCODED_MAP,
									ErrorCode.Error.ILLEGAL_VALUE,
									Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
									Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, amount.toString())
									));
						}
						allowanceCharge.addContent(amount);
						root.addContent(allowanceCharge);

					}
				}

				Element taxCategory = new Element("TaxCategory");

				if (!bg0021.getBT0103DocumentLevelChargeVatRate().isEmpty()) {
					BigDecimal percentValue = bg0021.getBT0103DocumentLevelChargeVatRate(0).getValue();

					Element id = new Element("ID");

					if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
						BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
						id.setText(bt0102.getValue().name());
					} else if (BigDecimal.ZERO.compareTo(percentValue) == 0) {
						id.setText(Untdid5305DutyTaxFeeCategories.Z.name());
					} else {
						id.setText(Untdid5305DutyTaxFeeCategories.S.name());
					}
					taxCategory.addContent(id);

					Element percentTax = new Element("Percent");
					try {
						percentTax.setText(bdStrConverter.convert(percentValue));
						taxCategory.addContent(percentTax);
					} catch (ConversionFailedException e) {
						errors.add(ConversionIssue.newError(
								e,
								e.getMessage(),
								callingLocation,
								ErrorCode.Action.HARDCODED_MAP,
								ErrorCode.Error.ILLEGAL_VALUE,
								Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, percentValue.toString())
								));
					}
				} else if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
					BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
					Element id = new Element("ID");
					id.setText(bt0102.getValue().name());
					taxCategory.addContent(id);

					if (Untdid5305DutyTaxFeeCategories.E.equals(bt0102.getValue())) {
						Element percentt = new Element("Percent");
						percentt.setText("0.00");
						taxCategory.addContent(percentt);
					}
				}
				Element taxScheme = new Element("TaxScheme").addContent(new Element("ID").setText("VAT"));
				taxCategory.addContent(taxScheme);
				allowanceCharge.addContent(taxCategory);

			}
		}


	}

	public BigDecimal calculateAllowancePercentage(BigDecimal amount, BigDecimal base) { 
		MathContext mc = new MathContext(2);
		BigDecimal calculation = amount.divide(base, mc);

		return calculation;
	}

	public BigDecimal calculateAllowanceBase(BigDecimal amount, BigDecimal percentage) { 
		MathContext mc = new MathContext(4);
		BigDecimal calculation = amount.divide(percentage, mc);

		return calculation;
	}

}