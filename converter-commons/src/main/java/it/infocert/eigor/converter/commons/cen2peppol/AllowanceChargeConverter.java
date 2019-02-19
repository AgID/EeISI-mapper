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
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class AllowanceChargeConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub
		
		TypeConverter<BigDecimal, String> bdStrConverter = BigDecimalToStringConverter.newConverter("#0.00");
		Element root = document.getRootElement();
		Element allowanceCharge = root.getChild("AllowanceCharge");
		Element amountElem = new Element("Amount");
		Element baseAmountElem = new Element("BaseAmount");
		Element percentageElem = new Element("MultiplierFactorNumeric");
		BigDecimal base = null, amount = null, percentage = null;
		
		
		if(allowanceCharge == null) {
			allowanceCharge = new Element("AllowanceCharge");
			root.addContent(allowanceCharge);

		}
		
		if(!cenInvoice.getBG0021DocumentLevelCharges().isEmpty()) { 
			if(cenInvoice.getBG0020DocumentLevelAllowances(0).getBT0093DocumentLevelAllowanceBaseAmount().isEmpty() &&
					!cenInvoice.getBG0020DocumentLevelAllowances(0).getBT0094DocumentLevelAllowancePercentage().isEmpty()) {

				base = cenInvoice.getBG0021DocumentLevelCharges(0).getBT0100DocumentLevelChargeBaseAmount(0).getValue();
				amount = cenInvoice.getBG0021DocumentLevelCharges(0).getBT0099DocumentLevelChargeAmount(0).getValue();
				percentage = calculateAllowancePercentage(base, amount);

			}

			else if(cenInvoice.getBG0020DocumentLevelAllowances(0).getBT0093DocumentLevelAllowanceBaseAmount().isEmpty() &&
					!cenInvoice.getBG0020DocumentLevelAllowances(0).getBT0094DocumentLevelAllowancePercentage().isEmpty()) {

				percentage = cenInvoice.getBG0021DocumentLevelCharges(0).getBT0101DocumentLevelChargePercentage(0).getValue();
				amount = cenInvoice.getBG0021DocumentLevelCharges(0).getBT0099DocumentLevelChargeAmount(0).getValue();
				base = calculateAllowanceBase(percentage, amount);

			}

			else {


			} 
			
			try {
				amountElem.setText(bdStrConverter.convert(amount.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP)));
				baseAmountElem.setText(bdStrConverter.convert(base.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP)));
				percentageElem.setText(bdStrConverter.convert(percentage.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP)));

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
			
			allowanceCharge.addContent(amountElem);
			allowanceCharge.addContent(baseAmountElem);
			allowanceCharge.addContent(percentageElem);
		}
		else
			return;
	}

	public BigDecimal calculateAllowancePercentage(BigDecimal base, BigDecimal amount) { 

		return null;
	}

	public BigDecimal calculateAllowanceBase(BigDecimal base, BigDecimal percentage) { 

		return null;
	}

}