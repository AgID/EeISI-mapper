package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;

import java.math.BigDecimal;
import java.util.List;

public class BodyFatturaConverter implements ICen2FattPAConverter {

    private ObjectFactory factory;
    private BG0000Invoice invoice;
    private FatturaElettronicaBodyType fatturaElettronicaBody;
    private Double invoiceDiscountAmount;
    private Double invoiceCorrectionAmount;

    public BodyFatturaConverter(ObjectFactory factory, BG0000Invoice invoice) {
        this.factory = factory;
        this.invoice = invoice;
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
        List<BG0025InvoiceLine> invoiceLineList = invoice.getBG0025InvoiceLine();

        for (int i = 0; i < invoiceLineList.size(); i++) {
            BG0025InvoiceLine invoiceLine = invoiceLineList.get(i);
            DettaglioLineeType dettaglioLinee = factory.createDettaglioLineeType();
            dettaglioLinee.setNumeroLinea(Integer.valueOf(invoiceLine.getBT0126InvoiceLineIdentifier().get(0).getValue()));
            dettaglioLinee.setDescrizione(invoiceLine.getBG0031ItemInformation().get(0).getBT0153ItemName().get(0).getValue());

            if (invoiceLine.getBG0029PriceDetails().get(0).getBT0149ItemPriceBaseQuantity().isEmpty() &&
                    invoiceLine.getBG0029PriceDetails().get(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
                dettaglioLinee.setQuantita(new BigDecimal(invoice.getBG0025InvoiceLine().get(i).getBT0129InvoicedQuantity().get(0).getValue()));
                dettaglioLinee.setUnitaMisura(invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().get(0).getValue().getCommonCode());
            }
            dettaglioLinee.setPrezzoUnitario(new BigDecimal(invoiceLine.getBG0029PriceDetails().get(0).getBT0146ItemNetPrice().get(0).getValue()));
            dettaglioLinee.setPrezzoTotale(new BigDecimal(invoiceLine.getBT0131InvoiceLineNetAmount().get(0).getValue()));
            dettaglioLinee.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBG0030LineVatInformation().get(0).getBT0152InvoicedItemVatRate().get(0).getValue()));
            datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
        }


        DatiRiepilogoType datiRiepilogo1 = factory.createDatiRiepilogoType();
        datiRiepilogo1.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0119VatCategoryRate().get(0).getValue()));
        datiRiepilogo1.setImponibileImporto(new BigDecimal(invoice.getBG0023VatBreakdown().get(0).getBT0116VatCategoryTaxableAmount().get(0).getValue()));
        datiRiepilogo1.setImposta(new BigDecimal(invoice.getBG0023VatBreakdown().get(0).getBT0117VatCategoryTaxAmount().get(0).getValue()));
        datiBeniServizi.getDatiRiepilogo().add(datiRiepilogo1);

        DatiRiepilogoType datiRiepilogo2 = factory.createDatiRiepilogoType();
        datiRiepilogo2.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0119VatCategoryRate().get(0).getValue()));
        datiRiepilogo2.setImponibileImporto(new BigDecimal(invoice.getBG0023VatBreakdown().get(0).getBT0116VatCategoryTaxableAmount().get(0).getValue()));
        datiRiepilogo2.setImposta(new BigDecimal(invoice.getBG0023VatBreakdown().get(0).getBT0117VatCategoryTaxAmount().get(0).getValue()));
        datiBeniServizi.getDatiRiepilogo().add(datiRiepilogo2);

        fatturaElettronicaBody.setDatiBeniServizi(datiBeniServizi);
    }

    private void setDatiGenerali() {
        DatiGeneraliType datiGenerali = factory.createDatiGeneraliType();
        DatiGeneraliDocumentoType datiGeneraliDocumento = factory.createDatiGeneraliDocumentoType();

        datiGeneraliDocumento.setTipoDocumento(TipoDocumentoType.TD_01); // FIXME values from cen do not match fattpa enum
//        datiGeneraliDocumento.setTipoDocumento(TipoDocumentoType.valueOf(invoice.getBT0003InvoiceTypeCode().get(0).getValue().getCode()));

        datiGeneraliDocumento.setDivisa(invoice.getBT0005InvoiceCurrencyCode().get(0).getValue().getCode());
        datiGeneraliDocumento.setData(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(invoice.getBT0002InvoiceIssueDate().get(0).getValue()));
        datiGeneraliDocumento.setNumero(invoice.getBT0001InvoiceNumber().get(0).getValue());

        datiGeneraliDocumento.setImportoTotaleDocumento(new BigDecimal(invoice.getBG0022DocumentTotals().get(0).getBT0112InvoiceTotalAmountWithVat().get(0).getValue()));
        datiGeneraliDocumento.getCausale().add(IConstants.SAMPLE_INVOICE);
        datiGenerali.setDatiGeneraliDocumento(datiGeneraliDocumento);
        fatturaElettronicaBody.setDatiGenerali(datiGenerali);
    }


    private void calculateDiscount() {
        Double percentageDiscount = 0d;
        Double baseAmount = 0d;

        try {
            percentageDiscount = invoice.getBG0020DocumentLevelAllowances().get(0).getBT0094DocumentLevelAllowancePercentage().get(0).getValue();
            baseAmount = invoice.getBG0020DocumentLevelAllowances().get(0).getBT0093DocumentLevelAllowanceBaseAmount().get(0).getValue();
        } catch (IndexOutOfBoundsException e) {
            // do nothing if there is no document level discount
        }

        invoiceDiscountAmount = (baseAmount * percentageDiscount) / -100;
    }

    private void calculateCorrectionForTotalAmount() {
        List<DettaglioLineeType> lineList = fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee();
        Double invoiceTotal = 0d;
        for (DettaglioLineeType line : lineList) {
            invoiceTotal += line.getPrezzoTotale().doubleValue();
        }
        Double actualInvoiceTotal = invoice.getBG0022DocumentTotals().get(0).getBT0109InvoiceTotalAmountWithoutVat().get(0).getValue();
        invoiceCorrectionAmount = actualInvoiceTotal - invoiceTotal;
    }

    private void addDiscountLine() {
        if (invoiceDiscountAmount < 0) {
            createAndAppendLine(IConstants.DISCOUNT_DESCRIPTION, IConstants.DISCOUNT_UNIT, 1d, invoiceDiscountAmount);
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
        dettaglioLinee.setQuantita(new BigDecimal(quantity));
        dettaglioLinee.setUnitaMisura(mUnitName);
        dettaglioLinee.setPrezzoUnitario(new BigDecimal(unitPrice));
        dettaglioLinee.setPrezzoTotale(new BigDecimal(unitPrice * quantity));
        dettaglioLinee.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0023VatBreakdown().get(0).getBT0119VatCategoryRate().get(0).getValue()));

        datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
    }

    @Override
    public void copyOptionalOne2OneFields() {
        setDatiPagamento();
    }

    private void setDatiPagamento() {
        DettaglioPagamentoType dettaglioPagamento = factory.createDettaglioPagamentoType();
        dettaglioPagamento.setDataScadenzaPagamento(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(invoice.getBT0009PaymentDueDate().get(0).getValue()));
        dettaglioPagamento.setImportoPagamento(new BigDecimal(invoice.getBG0022DocumentTotals().get(0).getBT0115AmountDueForPayment().get(0).getValue()));
        DatiPagamentoType datiPagamento = factory.createDatiPagamentoType();
        datiPagamento.getDettaglioPagamento().add(dettaglioPagamento);
        fatturaElettronicaBody.getDatiPagamento().add(datiPagamento);
    }

    @Override
    public void computeMultipleCenElements2FpaField() {
        transformInvoiceLinesWithItemPriceBaseQuatity();
        calculateDiscount();
        addDiscountLine();
        calculateCorrectionForTotalAmount();
        addCorrectionLine();
    }

    private void transformInvoiceLinesWithItemPriceBaseQuatity() {
        List<BG0025InvoiceLine> invoiceLineList = invoice.getBG0025InvoiceLine();

        for (int i = 0; i < invoiceLineList.size(); i++) {
            BG0025InvoiceLine invoiceLine = invoiceLineList.get(i);
            DettaglioLineeType dettaglioLinee = fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee().get(i);

            if (!(invoiceLine.getBG0029PriceDetails().get(0).getBT0149ItemPriceBaseQuantity().isEmpty() &&
                    invoiceLine.getBG0029PriceDetails().get(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty())) {

                Double bt0129 = invoiceLine.getBT0129InvoicedQuantity().get(0).getValue();
                String bt0130 = invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().get(0).getValue().getCommonCode();
                Double bt0149 = invoiceLine.getBG0029PriceDetails().get(0).getBT0149ItemPriceBaseQuantity().get(0).getValue();
                String bt0150 = invoiceLine.getBG0029PriceDetails().get(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().get(0).getValue().getCommonCode();

                dettaglioLinee.setQuantita(new BigDecimal(bt0129 / bt0149));
                dettaglioLinee.setUnitaMisura(bt0149.toString() + " " + bt0130);

                AltriDatiGestionaliType altriDatiGestionaliQty = factory.createAltriDatiGestionaliType();
                altriDatiGestionaliQty.setRiferimentoNumero(new BigDecimal(bt0149));
                altriDatiGestionaliQty.setTipoDato(IConstants.ITEM_BASE_QTY);
                dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliQty);

                AltriDatiGestionaliType altriDatiGestionaliUnit = factory.createAltriDatiGestionaliType();
                altriDatiGestionaliUnit.setRiferimentoTesto(bt0150);
                altriDatiGestionaliUnit.setTipoDato(IConstants.ITEM_BASE_PRICE);
                dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliUnit);
            }
        }
    }

    @Override
    public void transformFpaFields() {

    }
}
