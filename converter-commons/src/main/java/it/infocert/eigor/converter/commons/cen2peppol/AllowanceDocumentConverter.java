package it.infocert.eigor.converter.commons.cen2peppol;

import java.math.BigDecimal;
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
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0092DocumentLevelAllowanceAmount;
import it.infocert.eigor.model.core.model.BT0093DocumentLevelAllowanceBaseAmount;
import it.infocert.eigor.model.core.model.BT0094DocumentLevelAllowancePercentage;
import it.infocert.eigor.model.core.model.BT0097DocumentLevelAllowanceReason;
import it.infocert.eigor.model.core.model.BT0098DocumentLevelAllowanceReasonCode;

public class AllowanceDocumentConverter implements CustomMapping<Document>{

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
			for (BG0020DocumentLevelAllowances bg0020 : cenInvoice.getBG0020DocumentLevelAllowances()) {
				
			
				BigDecimal percent = null, base = null, actualamount = null;
				Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
				Element baseAmount = new Element("BaseAmount");
				Element amount = new Element("Amount");



				Element allowanceCharge = new Element("AllowanceCharge");
				allowanceCharge.addContent(new Element("ChargeIndicator").setText("false"));

//				if (!bg0020.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
//					BT0098DocumentLevelAllowanceReasonCode bt0098 = bg0020.getBT0098DocumentLevelAllowanceReasonCode(0);
//					Element allowanceChargeReasonCode = new Element("AllowanceChargeReasonCode");
//					String value = String.valueOf(bt0098.getValue().getCode());
//					allowanceChargeReasonCode.setText(value);
//					allowanceCharge.addContent(allowanceChargeReasonCode);
//				}

				Element allowanceChargeReason = new Element("AllowanceChargeReason");
				if (!bg0020.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
					BT0097DocumentLevelAllowanceReason bt0097 = bg0020.getBT0097DocumentLevelAllowanceReason(0);
					allowanceChargeReason.setText(bt0097.getValue());
				} else {
					allowanceChargeReason.setText("Sconto documento");
				}
				allowanceCharge.addContent(allowanceChargeReason);


				if (!bg0020.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
					BT0094DocumentLevelAllowancePercentage bt0094 = bg0020.getBT0094DocumentLevelAllowancePercentage(0);
					try {
						percent = bt0094.getValue().divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);
						multiplierFactorNumeric.setText(bdStrConverter.convert(percent));

					} catch (ConversionFailedException e) {
						errors.add(ConversionIssue.newError(
								e,
								e.getMessage(),
								callingLocation,
								ErrorCode.Action.HARDCODED_MAP,
								ErrorCode.Error.ILLEGAL_VALUE,
								Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0094.toString())
								));
					}
				}


				if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
					BT0092DocumentLevelAllowanceAmount bt0092 = bg0020.getBT0092DocumentLevelAllowanceAmount(0);
					try {
						actualamount = bt0092.getValue();
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
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0092.toString())
								));
					}
				}

				if (!bg0020.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
					BT0093DocumentLevelAllowanceBaseAmount bt0093 = bg0020.getBT0093DocumentLevelAllowanceBaseAmount(0);
					try {
						base = bt0093.getValue();
						baseAmount.setText(bdStrConverter.convert(bt0093.getValue()));
						if (currencyCode != null) {

							baseAmount.setAttribute("currencyID", currencyCode.getCode());
						}
					} catch (ConversionFailedException e) {
						errors.add(ConversionIssue.newError(
								e,
								e.getMessage(),
								callingLocation,
								ErrorCode.Action.HARDCODED_MAP,
								ErrorCode.Error.ILLEGAL_VALUE,
								Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0093.toString())
								));
					}
				}



				if(percent == null && base!=null) {
					
					percent = calculateAllowancePercentage(base, actualamount);
					
					allowanceCharge.addContent(amount);
					allowanceCharge.addContent(baseAmount);
					allowanceCharge.addContent(multiplierFactorNumeric);
				
				}
				else if(percent != null && base==null) {	
					base = calculateAllowanceBase(percent, actualamount);
					
					allowanceCharge.addContent(amount);
					allowanceCharge.addContent(baseAmount);
					allowanceCharge.addContent(multiplierFactorNumeric);
					
				}
				
				else {  
					
				
					allowanceCharge.addContent(amount);
					allowanceCharge.addContent(baseAmount);
					allowanceCharge.addContent(multiplierFactorNumeric);
				 				
				}

				root.addContent(allowanceCharge);
			}
		}

	}

	public BigDecimal calculateAllowancePercentage(BigDecimal base, BigDecimal amount) { 

		return base;
	}

	public BigDecimal calculateAllowanceBase(BigDecimal base, BigDecimal percentage) { 

		return null;
	}

}