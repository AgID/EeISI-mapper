package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Function;
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
import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
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
        TypeConverter<String, Double> strDblConverter = StringToDoubleConverter.newConverter();

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            BG0022DocumentTotals totals;
            if (invoice.getBG0022DocumentTotals().isEmpty()) {
                totals = new BG0022DocumentTotals();
                invoice.getBG0022DocumentTotals().add(totals);
            } else {
                totals = invoice.getBG0022DocumentTotals(0);
            }
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            final List<BT0112InvoiceTotalAmountWithVat> amountsWithVat = totals.getBT0112InvoiceTotalAmountWithVat();
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
                                    final Optional<Element> imponibileImporto = Optional.fromNullable(datiRiepilogo.getChild("ImponibileImporto"));
                                    final Optional<Element> imposta = Optional.fromNullable(datiRiepilogo.getChild("Imposta"));
                                    final Function<Element, Double> function = new Function<Element, Double>() {
                                        @Override
                                        public Double apply(Element input) {
                                            return Double.parseDouble(input.getText());
                                        }
                                    };

                                    final Double imponibileD = imponibileImporto.transform(function).or(0d);
                                    final Double impostaD = imposta.transform(function).or(0d);
                                    amountsWithVat.add(new BT0112InvoiceTotalAmountWithVat(imponibileD + impostaD));
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

                        Element datiRitenuta = datiGeneraliDocumento.getChild("DatiRitenuta");
                        if (datiRitenuta != null) {
                            BG0021DocumentLevelCharges bg0021 = new BG0021DocumentLevelCharges();
                            invoice.getBG0021DocumentLevelCharges().add(bg0021);

                            BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(0d);
                            bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);

                            Element importoRitenuta = datiRitenuta.getChild("ImportoRitenuta");
                            if (importoRitenuta != null) {
                                try {
                                    Double importoRitenutaValue = strDblConverter.convert(importoRitenuta.getText());

                                    BT0100DocumentLevelChargeBaseAmount bt0100 = new BT0100DocumentLevelChargeBaseAmount(importoRitenutaValue);
                                    bg0021.getBT0100DocumentLevelChargeBaseAmount().add(bt0100);

                                    BT0113PaidAmount bt0113 = new BT0113PaidAmount(importoRitenutaValue);
                                    invoice.getBG0022DocumentTotals(0).getBT0113PaidAmount().add(bt0113);
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

                            Element aliquotaRitenuta = datiRitenuta.getChild("AliquotaRitenuta");
                            if (aliquotaRitenuta != null) {
                                BT0101DocumentLevelChargePercentage bt0101 = null;
                                try {
                                    bt0101 = new BT0101DocumentLevelChargePercentage(strDblConverter.convert(aliquotaRitenuta.getText()));
                                } catch (NumberFormatException | ConversionFailedException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                                bg0021.getBT0101DocumentLevelChargePercentage().add(bt0101);
                            }

                            BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.E);
                            bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);

                            Element tipoRitenuta = datiRitenuta.getChild("TipoRitenuta");
                            Element causalePagamento = datiRitenuta.getChild("CausalePagamento");
                            BT0104DocumentLevelChargeReason bt0104 = null;
                            if (tipoRitenuta != null) {
                                if (causalePagamento != null) {
                                    bt0104 = new BT0104DocumentLevelChargeReason(String.format("%s %s", tipoRitenuta.getText(), causalePagamento.getText()));
                                } else {
                                    bt0104 = new BT0104DocumentLevelChargeReason(tipoRitenuta.getText());
                                }
                            } else if (causalePagamento != null) {
                                bt0104 = new BT0104DocumentLevelChargeReason(causalePagamento.getText());
                            }
                            if (bt0104 != null) {
                                bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);
                            }

                            BT0020PaymentTerms bt0020 = new BT0020PaymentTerms("BT-113 represents Withholding tax amount");
                            invoice.getBT0020PaymentTerms().add(bt0020);
                        }
                    }
                }
            }

            final Element datiPagamento = fatturaElettronicaBody.getChild("DatiPagamento");
            if (datiPagamento != null) {
                final Element dettaglioPagamento = datiPagamento.getChild("DettaglioPagamento");
                if (dettaglioPagamento != null) {
                    final Element importoPagamento = dettaglioPagamento.getChild("ImportoPagamento");
                    if (!totals.getBT0112InvoiceTotalAmountWithVat().isEmpty()) {
                        final Double amountWithVat = totals.getBT0112InvoiceTotalAmountWithVat(0).getValue();
                        if (importoPagamento != null) {
                            final Double importoD = Double.valueOf(importoPagamento.getText());

                            totals.getBT0113PaidAmount().add(new BT0113PaidAmount(amountWithVat - importoD));
                            totals.getBT0115AmountDueForPayment().add(new BT0115AmountDueForPayment(importoD));
                        } else {
                            final List<BT0113PaidAmount> paidAmounts = totals.getBT0113PaidAmount();
                            final List<BT0114RoundingAmount> roundingAmounts = totals.getBT0114RoundingAmount();
                            if (!amountsWithVat.isEmpty() && !paidAmounts.isEmpty() && !roundingAmounts.isEmpty()) {
                                final Double bt113 = totals.getBT0113PaidAmount(0).getValue();
                                final Double bt114 = totals.getBT0114RoundingAmount(0).getValue();
                                totals.getBT0115AmountDueForPayment().add(new BT0115AmountDueForPayment(amountWithVat + bt113 - bt114));
                            } else {
                                log.debug("One of [BT-112], [BT-113] or [BT-114] is missing. BT-112: {}, BT-113: {}, BT-114: {}", amountsWithVat.size(), paidAmounts.size(), roundingAmounts.size());
                            }
                        }
                    }

                }
            }




        }
    }
}
