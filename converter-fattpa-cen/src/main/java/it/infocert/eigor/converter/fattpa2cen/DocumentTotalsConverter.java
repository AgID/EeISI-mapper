package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Optional;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToDoubleConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DocumentTotalsConverter implements CustomMapping<Document> {
    private final static Logger log = LoggerFactory.getLogger(DocumentTotalsConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        addInvoiceTotalAmountWithVatDefault(invoice, document, errors, callingLocation);
    }

    private void addInvoiceTotalAmountWithVatDefault(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            if (datiGenerali != null) {
                List<Element> datiGeneraliDocumenti = datiGenerali.getChildren();
                for (Element datiGeneraliDocumento : datiGeneraliDocumenti) {
                    if (datiGeneraliDocumento.getName().equals("DatiGeneraliDocumento")) {
                        Element importoTotaleDocumento = datiGeneraliDocumento.getChild("ImportoTotaleDocumento");
                        if (importoTotaleDocumento == null) {
                            final Element datiBeniServizi = fatturaElettronicaBody.getChild("DatiBeniServizi");
                            if (datiBeniServizi != null) {
                                final Element datiRiepilogo = datiBeniServizi.getChild("DatiRiepilogo");
                                if (datiRiepilogo != null) {
                                    final Element imponibileImporto = Optional.fromNullable(datiRiepilogo.getChild("ImponibileImporto")).or(new Element("ImponibileImport").setText(""));
                                    final Element imposta = Optional.fromNullable(datiRiepilogo.getChild("Imposta")).or(new Element("Imposta").setText(""));
                                    BG0022DocumentTotals totals;
                                    if (invoice.getBG0022DocumentTotals().isEmpty()) {
                                        totals = new BG0022DocumentTotals();
                                        invoice.getBG0022DocumentTotals().add(totals);
                                    } else {
                                        totals = invoice.getBG0022DocumentTotals(0);
                                    }
                                    final double imponibileD = Double.parseDouble(!"".equals(imponibileImporto.getText()) ? imponibileImporto.getText() : "0");
                                    final double impostaD = Double.parseDouble(!"".equals(imposta.getText()) ? imposta.getText() : "0");
                                    totals.getBT0112InvoiceTotalAmountWithVat().add(new BT0112InvoiceTotalAmountWithVat(imponibileD + impostaD));
                                }
                            }
                        } else {
                            log.error("ImportoTotaleDocumento [BT-112] isn't present but ImponibileImporto [BT-109] and Imposta [BT-110] (used to calculate the default value)" +
                                    "are missing too.");
                        }

                        Element datiBollo = datiGeneraliDocumento.getChild("DatiBollo");
                        if (datiBollo != null) {

                            Element bolloVirtuale = datiBollo.getChild("BolloVirtuale");
                            Element importoBollo = datiBollo.getChild("ImportoBollo");

                            if (bolloVirtuale != null) {
                                BG0021DocumentLevelCharges bg0021 = new BG0021DocumentLevelCharges();
                                invoice.getBG0021DocumentLevelCharges().add(bg0021);

                                String bolloVirtualeText = bolloVirtuale.getText();
                                if ("SI".equals(bolloVirtualeText)) {
                                    BT0104DocumentLevelChargeReason bt0104 = new BT0104DocumentLevelChargeReason(bolloVirtualeText);
                                    bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);
                                } else if (importoBollo != null) {
                                    TypeConverter<String, Double> strDblConverter = StringToDoubleConverter.newConverter();

                                    BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(0d);
                                    bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);

                                    BT0101DocumentLevelChargePercentage bt0101 = new BT0101DocumentLevelChargePercentage(0d);
                                    bg0021.getBT0101DocumentLevelChargePercentage().add(bt0101);

                                    BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.E);
                                    bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);

                                    try {
                                        BT0100DocumentLevelChargeBaseAmount bt0100 = new BT0100DocumentLevelChargeBaseAmount(strDblConverter.convert(importoBollo.getText()));
                                        bg0021.getBT0100DocumentLevelChargeBaseAmount().add(bt0100);
                                    } catch (NumberFormatException | ConversionFailedException e) {
                                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                                .location(callingLocation)
                                                .action(ErrorCode.Action.HARDCODED_MAP)
                                                .error(ErrorCode.Error.ILLEGAL_VALUE)
                                                .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                                .build());
                                        errors.add(ConversionIssue.newError(ere));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
