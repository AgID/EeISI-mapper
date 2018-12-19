package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

/**
 * The VAT Breakdown Custom Converter
 */
public class VATBreakdownConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0023(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        TypeConverter<String, Untdid5305DutyTaxFeeCategories> dutyTaxFeeCategories = ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter.newConverter();

        BG0023VatBreakdown bg0023;

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiBeniServizi = fatturaElettronicaBody.getChild("DatiBeniServizi");
            if (datiBeniServizi != null) {
                List<Element> datiRiepiloghi = datiBeniServizi.getChildren();
                for (Element datiRiepilogo : datiRiepiloghi) {
                    if (datiRiepilogo.getName().equals("DatiRiepilogo")) {
                        bg0023 = new BG0023VatBreakdown();
                        Element imponibileImporto = datiRiepilogo.getChild("ImponibileImporto");
                        if (imponibileImporto != null) {
                            try {
                                BT0116VatCategoryTaxableAmount vatCategoryTaxableAmount = new BT0116VatCategoryTaxableAmount(new BigDecimal(imponibileImporto.getText()));
                                bg0023.getBT0116VatCategoryTaxableAmount().add(vatCategoryTaxableAmount);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        Element imposta = datiRiepilogo.getChild("Imposta");
                        if (imposta != null) {
                            try {
                                BT0117VatCategoryTaxAmount vatCategoryTaxAmount = new BT0117VatCategoryTaxAmount(new BigDecimal(imposta.getText()));
                                bg0023.getBT0117VatCategoryTaxAmount().add(vatCategoryTaxAmount);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        Element natura = datiRiepilogo.getChild("Natura");
                        Untdid5305DutyTaxFeeCategories code = null;
                        if (natura != null) {
                            try {
                                code = dutyTaxFeeCategories.convert(natura.getText());
                            } catch (NullPointerException | ConversionFailedException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        } else {
                            code = Untdid5305DutyTaxFeeCategories.S;
                        }
                        BT0118VatCategoryCode bt0118 = new BT0118VatCategoryCode(code);
                        bg0023.getBT0118VatCategoryCode().add(bt0118);
                        Element aliquotaIVA = datiRiepilogo.getChild("AliquotaIVA");
                        try {
                            BT0119VatCategoryRate bt0119 = new BT0119VatCategoryRate(new BigDecimal(aliquotaIVA.getText()));
                            bg0023.getBT0119VatCategoryRate().add(bt0119);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                    .location(callingLocation)
                                    .action(ErrorCode.Action.HARDCODED_MAP)
                                    .error(ErrorCode.Error.ILLEGAL_VALUE)
                                    .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                    .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                        invoice.getBG0023VatBreakdown().add(bg0023);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0023(document, cenInvoice, errors, callingLocation);
    }
}