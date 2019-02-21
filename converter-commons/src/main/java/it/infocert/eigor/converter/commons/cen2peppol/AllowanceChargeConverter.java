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
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0092DocumentLevelAllowanceAmount;
import it.infocert.eigor.model.core.model.BT0093DocumentLevelAllowanceBaseAmount;
import it.infocert.eigor.model.core.model.BT0094DocumentLevelAllowancePercentage;
import it.infocert.eigor.model.core.model.BT0097DocumentLevelAllowanceReason;
import it.infocert.eigor.model.core.model.BT0098DocumentLevelAllowanceReasonCode;
import it.infocert.eigor.model.core.model.BT0099DocumentLevelChargeAmount;
import it.infocert.eigor.model.core.model.BT0100DocumentLevelChargeBaseAmount;
import it.infocert.eigor.model.core.model.BT0101DocumentLevelChargePercentage;
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

                Element allowanceChargeReason = new Element("AllowanceChargeReason");
                if (!bg0021.getBT0104DocumentLevelChargeReason().isEmpty()) {
                    BT0104DocumentLevelChargeReason bt0104 = bg0021.getBT0104DocumentLevelChargeReason(0);
                    allowanceChargeReason.setText(bt0104.getValue());
                } else {
                    allowanceChargeReason.setText("Maggiorazione documento");
                }
                allowanceCharge.addContent(allowanceChargeReason);
                
                

				if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
					BT0101DocumentLevelChargePercentage bt0101 = bg0021.getBT0101DocumentLevelChargePercentage(0);
					try {
						percent = bt0101.getValue().divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);
						multiplierFactorNumeric.setText(bdStrConverter.convert(percent));

					} catch (ConversionFailedException e) {
						errors.add(ConversionIssue.newError(
								e,
								e.getMessage(),
								callingLocation,
								ErrorCode.Action.HARDCODED_MAP,
								ErrorCode.Error.ILLEGAL_VALUE,
								Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0101.toString())
								));
					}
				}


				if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
					BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
					try {
						actualamount = bt0099.getValue();
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
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0099.toString())
								));
					}
				}

				if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
					BT0100DocumentLevelChargeBaseAmount bt0100 = bg0021.getBT0100DocumentLevelChargeBaseAmount(0);
					try {
						base = bt0100.getValue();
						baseAmount.setText(bdStrConverter.convert(bt0100.getValue()));
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
								Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0100.toString())
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

		return null;
	}

	public BigDecimal calculateAllowanceBase(BigDecimal base, BigDecimal percentage) { 

		return null;
	}

}