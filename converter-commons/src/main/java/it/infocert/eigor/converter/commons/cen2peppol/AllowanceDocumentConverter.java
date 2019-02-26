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
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
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

				if (!bg0020.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
					Untdid5189ChargeAllowanceDescriptionCodes allowanceCode = null;
					BT0098DocumentLevelAllowanceReasonCode bt0098 = bg0020.getBT0098DocumentLevelAllowanceReasonCode(0);
					allowanceCode = setDefaultAllowanceChargerCode(bt0098.getValue());
					Element allowanceChargeReasonCode = new Element("AllowanceChargeReasonCode");
					String value = String.valueOf(allowanceCode.getCode());
					allowanceChargeReasonCode.setText(value);
					allowanceCharge.addContent(allowanceChargeReasonCode);
				}

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
						percent = bt0094.getValue();

					
				}


				if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
					BT0092DocumentLevelAllowanceAmount bt0092 = bg0020.getBT0092DocumentLevelAllowanceAmount(0);
						actualamount = bt0092.getValue();
										
				}

				if (!bg0020.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
					BT0093DocumentLevelAllowanceBaseAmount bt0093 = bg0020.getBT0093DocumentLevelAllowanceBaseAmount(0);
					base = bt0093.getValue();
					
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
				
				else if(percent == null && base==null) {	
					
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
					
					BigDecimal calculation = base.multiply(percent);
					int result = actualamount.compareTo(calculation);
					if (currencyCode != null) {
						amount.setAttribute("currencyID", currencyCode.getCode());
						baseAmount.setAttribute("currencyID", currencyCode.getCode());

					}
					if(result == 0) {

						allowanceCharge.addContent(multiplierFactorNumeric);
						allowanceCharge.addContent(amount);
						allowanceCharge.addContent(baseAmount);
						root.addContent(allowanceCharge);

					}else {
						allowanceCharge.addContent(amount);
						root.addContent(allowanceCharge);

					}
				 				
				}

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
	
	public Untdid5189ChargeAllowanceDescriptionCodes setDefaultAllowanceChargerCode(Untdid5189ChargeAllowanceDescriptionCodes val) {

		Untdid5189ChargeAllowanceDescriptionCodes codeValue;

		switch(val.toString())
		{
		case "41":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code41;
		case "42":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code42;
		case "60":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code60;
		case "62":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code62;
		case "63":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code63;
		case "64":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code64;
		case "65":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code65;
		case "66":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code66;
		case "67":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code67;
		case "68":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code68;
		case "70":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code70;
		case "71":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code72;
		case "88":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code88;
		case "95":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code95;
		case "100":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code100;
		case "102":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code102;
		case "103":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code103;
		case "104":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code104;
		case "105":  codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code105;
		default: codeValue = Untdid5189ChargeAllowanceDescriptionCodes.Code95;
		}

		return codeValue;
	}

}