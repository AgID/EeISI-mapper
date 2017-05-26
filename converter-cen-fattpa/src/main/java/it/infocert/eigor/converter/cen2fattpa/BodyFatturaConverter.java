package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0027InvoiceLineAllowances;
import it.infocert.eigor.model.core.model.BG0028InvoiceLineCharges;
import org.joda.time.LocalDate;

import java.util.List;

public class BodyFatturaConverter implements ICen2FattPAConverter {

    private ObjectFactory factory;
    private BG0000Invoice invoice;
    private FatturaElettronicaBodyType fatturaElettronicaBody;
    private List<Exception> errors;
    private Double invoiceDiscountAmount;
    private Double invoiceCorrectionAmount;

    public BodyFatturaConverter(ObjectFactory factory, BG0000Invoice invoice, List<Exception> errors) {
        this.factory = factory;
        this.invoice = invoice;
        this.errors = errors;
        this.fatturaElettronicaBody = factory.createFatturaElettronicaBodyType();
        invoiceDiscountAmount = 0d;
        invoiceCorrectionAmount = 0d;
    }

    public FatturaElettronicaBodyType getFatturaElettronicaBody() {
        return fatturaElettronicaBody;
    }

    @Override
    public void copyRequiredOne2OneFields() {

        setDatiGenerali();
        setDatiBeniServizi();
    }

    private void setDatiBeniServizi() {
        DatiBeniServiziType datiBeniServizi = factory.createDatiBeniServiziType();
        fatturaElettronicaBody.setDatiBeniServizi(datiBeniServizi);

        List<BG0025InvoiceLine> invoiceLineList = invoice.getBG0025InvoiceLine();

        for (int i = 0; i < invoiceLineList.size(); i++) {
            BG0025InvoiceLine invoiceLine = invoiceLineList.get(i);
            DettaglioLineeType dettaglioLinee = factory.createDettaglioLineeType();
            dettaglioLinee.setNumeroLinea(datiBeniServizi.getDettaglioLinee().size() + 1);
            dettaglioLinee.setDescrizione(invoiceLine.getBG0031ItemInformation().get(0).getBT0153ItemName().get(0).getValue());

            try {
                if (invoiceLine.getBG0029PriceDetails().get(0).getBT0149ItemPriceBaseQuantity().isEmpty() &&
                        invoiceLine.getBG0029PriceDetails().get(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
                    dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0025InvoiceLine().get(i).getBT0129InvoicedQuantity().get(0).getValue()));
                    dettaglioLinee.setUnitaMisura(invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().get(0).getValue().getCommonCode());
                }

                dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBG0029PriceDetails().get(0).getBT0146ItemNetPrice().get(0).getValue()));
                dettaglioLinee.setPrezzoTotale(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBT0131InvoiceLineNetAmount().get(0).getValue()));
                dettaglioLinee.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBG0030LineVatInformation().get(0).getBT0152InvoicedItemVatRate().get(0).getValue()));
                datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
            } catch (Exception e) {
                errors.add(new RuntimeException(IConstants.ERROR_LINE_PROCESSING, e));
            }
            if (!invoiceLine.getBG0027InvoiceLineAllowances().isEmpty()) {
                processLineDiscount(invoiceLine);
            }
            if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
                processLineCharges(invoiceLine);
            }
        }

        try {
            DatiRiepilogoType datiRiepilogo1 = factory.createDatiRiepilogoType();
            datiRiepilogo1.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0119VatCategoryRate().get(0).getValue()));
            datiRiepilogo1.setImponibileImporto(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0116VatCategoryTaxableAmount().get(0).getValue()));
            datiRiepilogo1.setImposta(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0117VatCategoryTaxAmount().get(0).getValue()));
            datiBeniServizi.getDatiRiepilogo().add(datiRiepilogo1);

            DatiRiepilogoType datiRiepilogo2 = factory.createDatiRiepilogoType();
            datiRiepilogo2.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0119VatCategoryRate().get(0).getValue()));
            datiRiepilogo2.setImponibileImporto(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0116VatCategoryTaxableAmount().get(0).getValue()));
            datiRiepilogo2.setImposta(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0117VatCategoryTaxAmount().get(0).getValue()));
            datiBeniServizi.getDatiRiepilogo().add(datiRiepilogo2);
        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_TAX_INFORMATION, e));
        }
    }

    private void processLineCharges(BG0025InvoiceLine invoiceLine) {
        Double surchargeValue = 0d;
        BG0028InvoiceLineCharges bg0028 = invoiceLine.getBG0028InvoiceLineCharges().get(0);
        Double chargeAmount = bg0028.getBT0141InvoiceLineChargeAmount().isEmpty() ? 0 : bg0028.getBT0141InvoiceLineChargeAmount().get(0).getValue();
        Double baseAmount = bg0028.getBT0142InvoiceLineChargeBaseAmount().isEmpty() ? 0 : bg0028.getBT0142InvoiceLineChargeBaseAmount().get(0).getValue();
        Double percentage = bg0028.getBT0143InvoiceLineChargePercentage().isEmpty() ? 0 : bg0028.getBT0143InvoiceLineChargePercentage().get(0).getValue();

        if (chargeAmount > 0) {
            surchargeValue = chargeAmount;
        } else if (baseAmount != 0 && percentage != 0) {
            surchargeValue = baseAmount * percentage;
        }


        if (surchargeValue > 0) {
            createAndAppendLine(IConstants.LINE_LEVEL_SURCHARGE_DESCRIPTION, IConstants.DISCOUNT_UNIT, 1d, surchargeValue);
        }
    }

    /**
     * Process line level discount (allowance)
     *
     * @param invoiceLine
     */
    private void processLineDiscount(BG0025InvoiceLine invoiceLine) {
        Double discountValue = 0d;
        BG0027InvoiceLineAllowances bg0027 = invoiceLine.getBG0027InvoiceLineAllowances().get(0);
        Double allowanceAmount = bg0027.getBT0136InvoiceLineAllowanceAmount().isEmpty() ? 0 : bg0027.getBT0136InvoiceLineAllowanceAmount().get(0).getValue();
        Double baseAmount = bg0027.getBT0137InvoiceLineAllowanceBaseAmount().isEmpty() ? 0 : bg0027.getBT0137InvoiceLineAllowanceBaseAmount().get(0).getValue();
        Double percentage = bg0027.getBT0138InvoiceLineAllowancePercentage().isEmpty() ? 0 : bg0027.getBT0138InvoiceLineAllowancePercentage().get(0).getValue();

        if (allowanceAmount > 0) {
            discountValue = -allowanceAmount;
        } else if (baseAmount != 0 && percentage != 0) {
            discountValue = baseAmount * -percentage;
        }


        if (discountValue < 0) {
            createAndAppendLine(IConstants.LINE_LEVEL_DISCOUNT_DESCRIPTION, IConstants.DISCOUNT_UNIT, 1d, discountValue);
        }
    }

    private void setDatiGenerali() {
        DatiGeneraliType datiGenerali = factory.createDatiGeneraliType();
        DatiGeneraliDocumentoType datiGeneraliDocumento = factory.createDatiGeneraliDocumentoType();
        datiGeneraliDocumento.getCausale().add(IConstants.SAMPLE_INVOICE);
        datiGenerali.setDatiGeneraliDocumento(datiGeneraliDocumento);
        fatturaElettronicaBody.setDatiGenerali(datiGenerali);

        try {
            datiGeneraliDocumento.setTipoDocumento(TipoDocumentoType.TD_01);

            datiGeneraliDocumento.setDivisa(invoice.getBT0005InvoiceCurrencyCode().get(0).getValue().getCode());
            LocalDate value = invoice.getBT0002InvoiceIssueDate().get(0).getValue();
            datiGeneraliDocumento.setData(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(value));
            datiGeneraliDocumento.setNumero(invoice.getBT0001InvoiceNumber().get(0).getValue());

            datiGeneraliDocumento.setImportoTotaleDocumento(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0022DocumentTotals().get(0).getBT0112InvoiceTotalAmountWithVat().get(0).getValue()));
        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_GENERAL_INFORMATION, e));
        }
    }


    private void calculateDiscount() {


        if (!invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
            Double percentageDiscount = 0d;
            Double baseAmount = 0d;
            try {
                percentageDiscount = invoice.getBG0020DocumentLevelAllowances().get(0).getBT0094DocumentLevelAllowancePercentage().get(0).getValue();
                baseAmount = invoice.getBG0020DocumentLevelAllowances().get(0).getBT0093DocumentLevelAllowanceBaseAmount().get(0).getValue();
            } catch (Exception e) {
                errors.add(new RuntimeException(IConstants.ERROR_INVOICE_LEVEL_ALLOWANCES, e));
            }

            invoiceDiscountAmount = baseAmount * -percentageDiscount;
        }
    }

    /**
     * Checks if the sum of all lines without VAT matches total without VAT from CEN invoice.
     * If not, it generates a correction line. This can occur if discounts or surcharges are converted from percent to values.
     */
    private void calculateCorrectionForTotalAmount() {
        List<DettaglioLineeType> lineList = fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee();
        Double invoiceTotal = 0d;
        for (DettaglioLineeType line : lineList) {
            invoiceTotal += line.getPrezzoTotale().doubleValue();
        }
        try {
            Double actualInvoiceTotal = invoice.getBG0022DocumentTotals().get(0).getBT0109InvoiceTotalAmountWithoutVat().get(0).getValue();
            invoiceCorrectionAmount = actualInvoiceTotal - invoiceTotal;
        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_TOTAL_AMOUNT_CORRECTION, e));
        }
    }

    private void addDiscountLine() {
        if (invoiceDiscountAmount < 0) {
            createAndAppendLine(IConstants.INVOICE_LEVEL_DISCOUNT_DESCRIPTION, IConstants.DISCOUNT_UNIT, 1d, invoiceDiscountAmount);
        }
    }

    private void addCorrectionLine() {
        if (invoiceCorrectionAmount != 0) {
            createAndAppendLine(IConstants.CORRECTION_DESCRIPTION, IConstants.CORRECTION_UNIT, 1d, invoiceCorrectionAmount);
        }
    }

    private void createAndAppendLine(String description, String mUnitName, Double quantity, Double unitPrice) {
        DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
        DettaglioLineeType dettaglioLinee = factory.createDettaglioLineeType();

        dettaglioLinee.setNumeroLinea(datiBeniServizi.getDettaglioLinee().size() + 1);
        dettaglioLinee.setDescrizione(description);
        dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity));
        dettaglioLinee.setUnitaMisura(mUnitName);
        dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(unitPrice));
        dettaglioLinee.setPrezzoTotale(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(unitPrice * quantity));
        dettaglioLinee.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0119VatCategoryRate().get(0).getValue()));

        datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
    }

    @Override
    public void copyOptionalOne2OneFields() {
        setDatiPagamento();
    }

    private void setDatiPagamento() {
        DettaglioPagamentoType dettaglioPagamento = factory.createDettaglioPagamentoType();
        DatiPagamentoType datiPagamento = factory.createDatiPagamentoType();
        datiPagamento.getDettaglioPagamento().add(dettaglioPagamento);
//        datiPagamento.setCondizioniPagamento(CondizioniPagamentoType.TP_01); // FIXME no actual mapping, dummy value
        fatturaElettronicaBody.getDatiPagamento().add(datiPagamento);
        try {
            dettaglioPagamento.setDataScadenzaPagamento(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(invoice.getBT0009PaymentDueDate().get(0).getValue()));
            dettaglioPagamento.setImportoPagamento(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0022DocumentTotals().get(0).getBT0115AmountDueForPayment().get(0).getValue()));
//            dettaglioPagamento.setBeneficiario("dummy"); // FIXME no actual mapping, dummy value
//            dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_01); // FIXME no actual mapping, dummy value
        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_PAYMENT_INFORMATION, e));
        }
    }

    @Override
    public void computeMultipleCenElements2FpaField() {
        transformInvoiceLinesWithItemPriceBaseQuantity();
        calculateDiscount();
        addDiscountLine();
        calculateCorrectionForTotalAmount();
        addCorrectionLine();
    }

    private void transformInvoiceLinesWithItemPriceBaseQuantity() {
        List<BG0025InvoiceLine> invoiceLineList = invoice.getBG0025InvoiceLine();

        for (int i = 0; i < invoiceLineList.size(); i++) {
            BG0025InvoiceLine invoiceLine = invoiceLineList.get(i);
            DettaglioLineeType dettaglioLinee = fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee().get(i);

            if (!(invoiceLine.getBG0029PriceDetails().get(0).getBT0149ItemPriceBaseQuantity().isEmpty() &&
                    invoiceLine.getBG0029PriceDetails().get(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty())) {

                try {
                    Double bt0129 = invoiceLine.getBT0129InvoicedQuantity().get(0).getValue();
                    String bt0130 = invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().get(0).getValue().getCommonCode();
                    Double bt0149 = invoiceLine.getBG0029PriceDetails().get(0).getBT0149ItemPriceBaseQuantity().get(0).getValue();
                    String bt0150 = invoiceLine.getBG0029PriceDetails().get(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().get(0).getValue().getCommonCode();

                    dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(bt0129 / bt0149));
                    dettaglioLinee.setUnitaMisura(bt0149.toString() + " " + bt0130);

                    AltriDatiGestionaliType altriDatiGestionaliQty = factory.createAltriDatiGestionaliType();
                    AltriDatiGestionaliType altriDatiGestionaliUnit = factory.createAltriDatiGestionaliType();
                    altriDatiGestionaliQty.setTipoDato(IConstants.ITEM_BASE_QTY);
                    altriDatiGestionaliUnit.setTipoDato(IConstants.ITEM_BASE_PRICE);

                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliQty);
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliUnit);

                    altriDatiGestionaliUnit.setRiferimentoTesto(bt0150);
                    altriDatiGestionaliQty.setRiferimentoNumero(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(bt0149));
                } catch (Exception e) {
                    errors.add(new RuntimeException(IConstants.ERROR_BASE_QUANTITY_TRANSFORM, e));
                }

            }
        }
    }

    @Override
    public void transformFpaFields() {

    }
}
