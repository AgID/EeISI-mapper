package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.datatypes.Binary;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class BodyFatturaConverter implements ICen2FattPAConverter {

    private static final Logger log = LoggerFactory.getLogger(BodyFatturaConverter.class);

    private BG0000Invoice invoice;
    private FatturaElettronicaBodyType fatturaElettronicaBody;
    private List<ConversionIssue> errors;
    private Double invoiceDiscountAmount;
    private Double invoiceCorrectionAmount;
    private ObjectFactory factory;
    private ConversionRegistry conversionRegistry;

    public BodyFatturaConverter(FatturaElettronicaBodyType fatturaElettronicaBody, ObjectFactory factory, BG0000Invoice invoice, List<ConversionIssue> errors) {
        this.invoice = invoice;
        this.errors = errors;
        this.fatturaElettronicaBody = fatturaElettronicaBody;
        this.factory = factory;
        invoiceDiscountAmount = 0d;
        invoiceCorrectionAmount = 0d;
    }

   /* public BodyFatturaConverter(ObjectFactory factory, BG0000Invoice invoice, List<ConversionIssue> errors) {
        this.factory = factory;
        this.invoice = invoice;
        this.errors = errors;
        this.fatturaElettronicaBody = factory.createFatturaElettronicaBodyType();
        invoiceDiscountAmount = 0d;
        invoiceCorrectionAmount = 0d;
    }*/

    public FatturaElettronicaBodyType getFatturaElettronicaBody() {
        return fatturaElettronicaBody;
    }

    @Override
    public void copyRequiredOne2OneFields() {
        setDatiGenerali();
        setDatiBeniServizi();
        setAllegati();
    }

    private void setDatiBeniServizi() {
        log.info("Starting converting DatiBeniServizi");
        DatiBeniServiziType datiBeniServizi = factory.createDatiBeniServiziType();
        fatturaElettronicaBody.setDatiBeniServizi(datiBeniServizi);

        List<BG0025InvoiceLine> invoiceLineList = invoice.getBG0025InvoiceLine();

        for (BG0025InvoiceLine invoiceLine : invoiceLineList) {
            DettaglioLineeType dettaglioLinee = factory.createDettaglioLineeType();
            datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
            dettaglioLinee.setNumeroLinea(Integer.parseInt(invoiceLine.getBT0126InvoiceLineIdentifier().get(0).getValue()));

            if (!invoiceLine.getBT0127InvoiceLineNote().isEmpty()) {
                AltriDatiGestionaliType altriDatiGestionali = factory.createAltriDatiGestionaliType();
                dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionali);


                altriDatiGestionali.setTipoDato(invoiceLine.getBT0127InvoiceLineNote().get(0).getValue());
                logBt(127, "DatiGestionali.TipoDato");
            }

            if (!invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                CodiceArticoloType codiceArticolo = factory.createCodiceArticoloType();
                dettaglioLinee.getCodiceArticolo().add(codiceArticolo);
                BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier identifierAndSchemeIdentifier = invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().get(0);
                codiceArticolo.setCodiceValore(identifierAndSchemeIdentifier.getValue());
                codiceArticolo.setCodiceTipo(identifierAndSchemeIdentifier.getValue()); //TODO Check how to extract scheme identifier
                logBt(128, "CodiceArticolo");
            }


            try {
                BG0031ItemInformation itemInformation = null;
                try {
                    itemInformation = invoiceLine.getBG0031ItemInformation().get(0);
                } catch (Exception e) {
                    errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
                }

                if (itemInformation != null) {
                    try {
                        dettaglioLinee.setDescrizione(itemInformation.getBT0153ItemName().get(0).getValue());
                    } catch (Exception e) {
                        errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
                    }
                    if (!itemInformation.getBT0155ItemSellerSIdentifier().isEmpty()) {
                        CodiceArticoloType codiceArticolo = factory.createCodiceArticoloType();
                        codiceArticolo.setCodiceTipo("Seller");
                        codiceArticolo.setCodiceValore(itemInformation.getBT0155ItemSellerSIdentifier().get(0).getValue());
                        dettaglioLinee.getCodiceArticolo().add(codiceArticolo);
                        logBt(155, "CodiceArticolo");
                    }

                    if (!itemInformation.getBT0156ItemBuyerSIdentifier().isEmpty()) {
                        CodiceArticoloType codiceArticolo = factory.createCodiceArticoloType();
                        codiceArticolo.setCodiceTipo("Buyer");
                        codiceArticolo.setCodiceValore(itemInformation.getBT0156ItemBuyerSIdentifier().get(0).getValue());
                        dettaglioLinee.getCodiceArticolo().add(codiceArticolo);
                        logBt(156, "CodiceArticolo");
                    }

                    if (!itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier().isEmpty()) {
                        CodiceArticoloType codiceArticolo = factory.createCodiceArticoloType();
                        codiceArticolo.setCodiceValore(itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier().get(0).getValue());
                        dettaglioLinee.getCodiceArticolo().add(codiceArticolo);
                        logBt(157, "CodiceArticolo");
                    }

                    if (!itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().isEmpty()) {
                        for (BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier identifier : itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier()) {
                            CodiceArticoloType codiceArticolo = factory.createCodiceArticoloType();
                            codiceArticolo.setCodiceValore(identifier.getValue());
                            dettaglioLinee.getCodiceArticolo().add(codiceArticolo);
                            logBt(158, "CodiceArticolo");
                        }
                    }

                    if (!itemInformation.getBT0159ItemCountryOfOrigin().isEmpty()) {
                        AltriDatiGestionaliType altriDati = factory.createAltriDatiGestionaliType();
                        altriDati.setRiferimentoTesto(itemInformation.getBT0159ItemCountryOfOrigin().get(0).getValue().getCountryNameInEnglish()); //FIXME WTH does this mapping means? (see excel)
                        dettaglioLinee.getAltriDatiGestionali().add(altriDati);
                        logBt(159, "AltriDatiGestionali.RiferimentoTesto");
                    }

                    if (!itemInformation.getBG0032ItemAttributes().isEmpty()) {
                        for (BG0032ItemAttributes itemAttributes : itemInformation.getBG0032ItemAttributes()) {
                            AltriDatiGestionaliType altriDati = factory.createAltriDatiGestionaliType();
                            altriDati.setTipoDato(itemAttributes.getBT0160ItemAttributeName().get(0).getValue());
                            altriDati.setRiferimentoTesto(itemAttributes.getBT0161ItemAttributeValue().get(0).getValue());
                            dettaglioLinee.getAltriDatiGestionali().add(altriDati);
                            logBt(160, "AltriDatiGestionali.TipoDato");
                            logBt(160, "AltriDatiGestionali.RiferimentoTesto");
                        }
                    }
                }
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_LINE_PROCESSING));
                log.error(e.getMessage());
            }

            try {
                if (invoiceLine.getBG0029PriceDetails().get(0).getBT0149ItemPriceBaseQuantity().isEmpty() &&
                        invoiceLine.getBG0029PriceDetails().get(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
                    dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBT0129InvoicedQuantity().get(0).getValue()));
                    dettaglioLinee.setUnitaMisura(invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().get(0).getValue().getCommonCode());
                    logBt(129, "DettaglioLinee.Quantita");
                    logBt(130, "DettaglioLinee.UnitaMisura");
                }

                dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBG0029PriceDetails().get(0).getBT0146ItemNetPrice().get(0).getValue()));
                logBt(146, "DettaglioLinee.PrezzoUnitario");
                dettaglioLinee.setPrezzoTotale(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBT0131InvoiceLineNetAmount().get(0).getValue()));
                logBt(131, "DettaglioLinee.PrezzoTotale");
                dettaglioLinee.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBG0030LineVatInformation().get(0).getBT0152InvoicedItemVatRate().get(0).getValue()));
                logBt(152, "DettaglioLinee.AliquotaIVA");
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_LINE_PROCESSING));
                log.error(e.getMessage());
            }
            if (!invoiceLine.getBG0027InvoiceLineAllowances().isEmpty()) {
                processLineDiscount(invoiceLine);
            }
            if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
                processLineCharges(invoiceLine);
            }

            if (!invoiceLine.getBT0133InvoiceLineBuyerAccountingReference().isEmpty()) {
                dettaglioLinee.setRiferimentoAmministrazione(invoiceLine.getBT0133InvoiceLineBuyerAccountingReference().get(0).getValue());
                logBt(133, "DettaglioLinee.RiferimentoAmministrazione");
            }
        }

        try {
            DatiRiepilogoType datiRiepilogo = factory.createDatiRiepilogoType();
            DettaglioLineeType dettaglioLinee = factory.createDettaglioLineeType();

            if (!invoice.getBG0013DeliveryInformation().isEmpty()) {
                BG0013DeliveryInformation deliveryInformation = invoice.getBG0013DeliveryInformation().get(0);
                if (!deliveryInformation.getBT0070DeliverToPartyName().isEmpty()) {
                    AltriDatiGestionaliType altriDatiGestionali = factory.createAltriDatiGestionaliType();
                    altriDatiGestionali.setTipoDato(deliveryInformation.getBT0070DeliverToPartyName().get(0).getValue());
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionali);
                    logBt(70, "AltriDatiGestionali.TipoDato");
                }

                if (!deliveryInformation.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().isEmpty()) {
                    AltriDatiGestionaliType altriDatiGestionali = factory.createAltriDatiGestionaliType();
                    altriDatiGestionali.setTipoDato(deliveryInformation.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().get(0).getValue());
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionali);
                    logBt(71, "AltriDatiGestionali.TipoDato");
                }

                if (!deliveryInformation.getBT0072ActualDeliveryDate().isEmpty()) {
                    AltriDatiGestionaliType altriDatiGestionali = factory.createAltriDatiGestionaliType();
                    altriDatiGestionali.setTipoDato(deliveryInformation.getBT0072ActualDeliveryDate().get(0).getValue().toString());
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionali);
                    logBt(71, "AltriDatiGestionali.TipoDato");
                }

                if (!deliveryInformation.getBG0014InvoicingPeriod().isEmpty()) {
                    BG0014InvoicingPeriod invoicingPeriod = deliveryInformation.getBG0014InvoicingPeriod().get(0);
                    if (!invoicingPeriod.getBT0073InvoicingPeriodStartDate().isEmpty()) {
                        dettaglioLinee.setDataInizioPeriodo(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(
                                invoicingPeriod.getBT0073InvoicingPeriodStartDate().get(0).getValue())
                        );
                        logBt(73, "DettaglioLinee.DataInizioPeriodo");
                    }

                    if (!invoicingPeriod.getBT0074InvoicingPeriodEndDate().isEmpty()) {
                        dettaglioLinee.setDataFinePeriodo(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(
                                invoicingPeriod.getBT0074InvoicingPeriodEndDate().get(0).getValue())
                        );
                        logBt(74, "DettaglioLinee.DataFinePeriodo");
                    }
                }

            }

            BG0023VatBreakdown vatBreakdown = null;
            try {
                vatBreakdown = invoice.getBG0023VatBreakdown().get(0);
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }
            if (vatBreakdown != null) {
                datiRiepilogo.setImponibileImporto(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0116VatCategoryTaxableAmount().get(0).getValue()));
                logBt(116, "DatiRiepilogo.ImponibileImporto");
                datiRiepilogo.setImposta(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0117VatCategoryTaxAmount().get(0).getValue()));
                logBt(117, "DatiRiepilogo.ImponibileImporto");
                datiRiepilogo.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate().get(0).getValue()));
                logBt(119, "DatiRiepilogo.AliquotaIva");
                if (!vatBreakdown.getBT0120VatExemptionReasonText().isEmpty()) {
                    datiRiepilogo.setRiferimentoNormativo(vatBreakdown.getBT0120VatExemptionReasonText().get(0).getValue());
                    logBt(120, "DatiRiepilogo.RiferimentoNormativo");
                }

                Untdid5305DutyTaxFeeCategories category = vatBreakdown.getBT0118VatCategoryCode().get(0).getValue();
                switch (category) {
                    case Z:
                        datiRiepilogo.setNatura(NaturaType.N_3);//TODO assert in which case this must be N_3 or N_7 (see code list mapping)
                        break;
                    case E:
                        datiRiepilogo.setNatura(NaturaType.N_4);
                        break;
                    case G:
                        datiRiepilogo.setNatura(NaturaType.N_2);
                        break;
                    case O:
                        datiRiepilogo.setNatura(NaturaType.N_2); //TODO assert in which case this must be N_2 or N_1 (see code list mapping)
                        break;
                    default:
                        datiRiepilogo.setNatura(null);
                }
                logBt(118, "DatiRiepilogo.Natura");
            }


            EsigibilitaIVAType esigibilita = null;
            if (!invoice.getBT0007ValueAddedTaxPointDate().isEmpty() && invoice.getBT0008ValueAddedTaxPointDateCode().isEmpty()) {
                esigibilita = EsigibilitaIVAType.D;
                logBt(7, "DatiRiepilogo.EsigibilitaIVA");
            } else if (invoice.getBT0007ValueAddedTaxPointDate().isEmpty() && !invoice.getBT0008ValueAddedTaxPointDateCode().isEmpty()) {
                switch (invoice.getBT0008ValueAddedTaxPointDateCode().get(0).getValue()) {
                    case Code3:
                        esigibilita = EsigibilitaIVAType.I;
                        break;
                    case Code355:
                    case Code432:
                        esigibilita = EsigibilitaIVAType.D;
                        break;
                }
                logBt(8, "DatiRiepilogo.EsigibilitaIVA");
            }
            datiRiepilogo.setEsigibilitaIVA(esigibilita);
            datiBeniServizi.getDatiRiepilogo().add(datiRiepilogo);


            if (vatBreakdown != null) {
                DatiRiepilogoType datiRiepilogo2 = factory.createDatiRiepilogoType();
                datiRiepilogo2.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate().get(0).getValue()));
                datiRiepilogo2.setImponibileImporto(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0116VatCategoryTaxableAmount().get(0).getValue()));
                datiRiepilogo2.setImposta(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0117VatCategoryTaxAmount().get(0).getValue()));
                datiBeniServizi.getDatiRiepilogo().add(datiRiepilogo2); // TODO check mapping for this
            }
        } catch (Exception e) {
            errors.add(ConversionIssue.newError(e, IConstants.ERROR_TAX_INFORMATION));
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
        log.info("Starting converting DatiGenerali");
        DatiGeneraliType datiGenerali = factory.createDatiGeneraliType();
        DatiGeneraliDocumentoType datiGeneraliDocumento = factory.createDatiGeneraliDocumentoType();
        DatiDocumentiCorrelatiType datiContratto = factory.createDatiDocumentiCorrelatiType();
        DatiDocumentiCorrelatiType datiOrdineAcquisto = factory.createDatiDocumentiCorrelatiType();
        DatiDocumentiCorrelatiType datiRicezione = factory.createDatiDocumentiCorrelatiType();
        DatiDDTType datiDDT = factory.createDatiDDTType();
        DatiTrasportoType datiTrasporto = factory.createDatiTrasportoType();
        datiGenerali.setDatiTrasporto(datiTrasporto);
        IndirizzoType indirizzoResa = factory.createIndirizzoType();
        datiTrasporto.setIndirizzoResa(indirizzoResa);

        if (!invoice.getBG0016PaymentInstructions().isEmpty()) {
            BG0016PaymentInstructions paymentInstructions = invoice.getBG0016PaymentInstructions().get(0);

            if (!paymentInstructions.getBT0082PaymentMeansText().isEmpty()) {
                datiGeneraliDocumento.getCausale().add(paymentInstructions.getBT0082PaymentMeansText().get(0).getValue());
                logBt(82, "DatiGeneraliDocumento.Causale");
            }


        }

        List<BT0011ProjectReference> projectReference = invoice.getBT0011ProjectReference();
        if (!projectReference.isEmpty()) {
            datiContratto.setCodiceCUP(projectReference.get(0).getValue());
            logBt(11, "DatiContratto.CodiceCUP");
        }

        List<BT0012ContractReference> contractReference = invoice.getBT0012ContractReference();
        if (!contractReference.isEmpty()) {
            datiContratto.setIdDocumento(contractReference.get(0).getValue());
            logBt(12, "DatiContratto.IdDocumento");
        }

        List<BT0013PurchaseOrderReference> orderReference = invoice.getBT0013PurchaseOrderReference();
        if (!orderReference.isEmpty()) {
            datiOrdineAcquisto.setIdDocumento(orderReference.get(0).getValue());
            logBt(13, "DatiOrdineAcquisto.IdDocumento");
        }

        for (BG0025InvoiceLine invoiceLine : invoice.getBG0025InvoiceLine())
            if (!invoiceLine.getBT0132ReferencedPurchaseOrderLineReference().isEmpty()) {
                datiOrdineAcquisto.setNumItem(invoiceLine.getBT0132ReferencedPurchaseOrderLineReference().get(0).getValue());
                logBt(132, "DatiOrdineAcquisto.NumItem");
                datiOrdineAcquisto.getRiferimentoNumeroLinea().add(Integer.valueOf(invoiceLine.getBT0126InvoiceLineIdentifier().get(0).getValue()));
                logBt(126, "DatiOrdineAcquisto.RiferimentoNumeroLinea");
            }

        List<BT0015ReceivingAdviceReference> receivingAdviceReference = invoice.getBT0015ReceivingAdviceReference();
        if (!receivingAdviceReference.isEmpty()) {
            datiRicezione.setIdDocumento(receivingAdviceReference.get(0).getValue());
            logBt(15, "DatiRicezione.IdDocumento");
        }

        List<BT0017TenderOrLotReference> references = invoice.getBT0017TenderOrLotReference();
        if (!references.isEmpty()) {
            datiContratto.setCodiceCIG(references.get(0).getValue());
            logBt(17, "DatiContratto.CodiceCIG");
        }

        List<BT0020PaymentTerms> paymentTerms = invoice.getBT0020PaymentTerms();
        if (!paymentTerms.isEmpty()) {
            datiGeneraliDocumento.getCausale().add(paymentTerms.get(0).getValue());
            logBt(20, "DatiGeneraliDocumento.Causale");
        }

        try {

            List<BT0016DespatchAdviceReference> adviceReferences = invoice.getBT0016DespatchAdviceReference();
            if (!adviceReferences.isEmpty()) {
                String adviceReference = adviceReferences.get(0).getValue();
                String[] data = adviceReference.split("_");
                datiDDT.setNumeroDDT(data[0]);
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
                XMLGregorianCalendar date = Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(LocalDate.parse(data[1], formatter));
                datiDDT.setDataDDT(date);
                logBt(16, "DatiDDT.DataDDT");
            }

            try {
                datiGeneraliDocumento.setNumero(invoice.getBT0001InvoiceNumber().get(0).getValue());
                logBt(1, "DatiGeneraliDocumento.Numero");
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }

            try {
                datiGeneraliDocumento.setDivisa(invoice.getBT0005InvoiceCurrencyCode().get(0).getValue().getCode());
                logBt(5, "DatiGeneraliDocumento.Divisa");
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }

            try {
                datiGeneraliDocumento.setData(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(invoice.getBT0002InvoiceIssueDate().get(0).getValue()));
                logBt(2, "DatiGeneraliDocumento.Data");
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }

            try {
                datiGeneraliDocumento.setImportoTotaleDocumento(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0022DocumentTotals().get(0).getBT0112InvoiceTotalAmountWithVat().get(0).getValue()));
                logBt(112, "DatiGeneraliDocumento.ImportoTotaleDocumento");
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }

            List<BG0001InvoiceNote> notes = invoice.getBG0001InvoiceNote();
            if (!notes.isEmpty()) {
                for (BG0001InvoiceNote note : notes) {
                    StringBuilder sb = new StringBuilder();
                    if (!note.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                        sb.append(note.getBT0021InvoiceNoteSubjectCode().get(0).getValue());
                        logBt(21, "DatiGeneraliDocumento.Causale");
                    }

                    if (!note.getBT0022InvoiceNote().isEmpty()) {
                        sb.append(note.getBT0022InvoiceNote().get(0).getValue());
                        logBt(21, "DatiGeneraliDocumento.Causale");
                    }
                    datiGeneraliDocumento.getCausale().add(sb.toString());
                }
            }

            List<BG0003PrecedingInvoiceReference> precedingInvoiceReferences = invoice.getBG0003PrecedingInvoiceReference();
            if (!precedingInvoiceReferences.isEmpty()) {
                for (BG0003PrecedingInvoiceReference reference : precedingInvoiceReferences) {
                    DatiDocumentiCorrelatiType datiFattureCollegate = factory.createDatiDocumentiCorrelatiType();

                    if (!reference.getBT0025PrecedingInvoiceReference().isEmpty()) {
                        datiFattureCollegate.setIdDocumento(reference.getBT0025PrecedingInvoiceReference().get(0).getValue());
                    }

                    if (!reference.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                        LocalDate date = reference.getBT0026PrecedingInvoiceIssueDate().get(0).getValue();
                        datiFattureCollegate.setData(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(date));
                    }
                    datiGenerali.getDatiFattureCollegate().add(datiFattureCollegate);
                }
            }

            BG0004Seller seller = null;
            try {
                seller = invoice.getBG0004Seller().get(0);
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }
            List<BG0006SellerContact> sellerContacts = null;
            try {
                sellerContacts = seller.getBG0006SellerContact();
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }
            if (sellerContacts != null && !sellerContacts.isEmpty()) {
                BG0006SellerContact sellerContact = sellerContacts.get(0);
                if (!sellerContact.getBT0041SellerContactPoint().isEmpty()) {
                    datiGeneraliDocumento.getCausale().add(sellerContact.getBT0041SellerContactPoint().get(0).getValue());
                }
            }

            datiGenerali.getDatiDDT().add(datiDDT);
            datiGenerali.setDatiGeneraliDocumento(datiGeneraliDocumento);
            datiGenerali.getDatiContratto().add(datiContratto);
            datiGenerali.getDatiOrdineAcquisto().add(datiOrdineAcquisto);
            fatturaElettronicaBody.setDatiGenerali(datiGenerali);

            datiGeneraliDocumento.setTipoDocumento(TipoDocumentoType.TD_01);

            BG0007Buyer buyer = null;
            try {
                buyer = invoice.getBG0007Buyer().get(0);
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
            }
            if (buyer != null) {
                if (!buyer.getBG0009BuyerContact().isEmpty()) {
                    BG0009BuyerContact buyerContact = buyer.getBG0009BuyerContact().get(0);
                    if (!buyerContact.getBT0056BuyerContactPoint().isEmpty()) {
                        datiGeneraliDocumento.getCausale().add(buyerContact.getBT0056BuyerContactPoint().get(0).getValue());
                    }
                }

                if (!invoice.getBG0013DeliveryInformation().isEmpty()) {
                    if (!invoice.getBG0013DeliveryInformation().get(0).getBG0015DeliverToAddress().isEmpty()) {
                        BG0015DeliverToAddress deliverToAddress = invoice.getBG0013DeliveryInformation().get(0).getBG0015DeliverToAddress().get(0);
                        StringBuilder sb = new StringBuilder();
                        if (!deliverToAddress.getBT0075DeliverToAddressLine1().isEmpty()) {
                            sb.append(deliverToAddress.getBT0075DeliverToAddressLine1().get(0).getValue()).append(", ");
                        }

                        if (!deliverToAddress.getBT0076DeliverToAddressLine2().isEmpty()) {
                            sb.append(deliverToAddress.getBT0076DeliverToAddressLine2().get(0).getValue()).append(", ");
                        }

                        if (!deliverToAddress.getBT0165DeliverToAddressLine3().isEmpty()) {
                            sb.append(deliverToAddress.getBT0165DeliverToAddressLine3().get(0).getValue());
                        }

                        indirizzoResa.setIndirizzo(sb.toString());

                        if (!deliverToAddress.getBT0077DeliverToCity().isEmpty()) {
                            indirizzoResa.setComune(deliverToAddress.getBT0077DeliverToCity().get(0).getValue());
                        }

                        if (!deliverToAddress.getBT0078DeliverToPostCode().isEmpty()) {
                            indirizzoResa.setCAP(deliverToAddress.getBT0078DeliverToPostCode().get(0).getValue());
                        }

                        if (!deliverToAddress.getBT0079DeliverToCountrySubdivision().isEmpty()) {
                            indirizzoResa.setProvincia(deliverToAddress.getBT0079DeliverToCountrySubdivision().get(0).getValue());
                        }

                        indirizzoResa.setNazione(deliverToAddress.getBT0080DeliverToCountryCode().get(0).getValue().getIso2charCode());
                    }
                }

            }
        } catch (Exception e) {
            errors.add(ConversionIssue.newError(e, IConstants.ERROR_GENERAL_INFORMATION));
        }
    }

    private void setAllegati() {
        log.info("Starting converting Allegati");
        if (!invoice.getBG0024AdditionalSupportingDocuments().isEmpty()) {
            for (BG0024AdditionalSupportingDocuments documents : invoice.getBG0024AdditionalSupportingDocuments()) {
                AllegatiType allegati = factory.createAllegatiType();
                fatturaElettronicaBody.getAllegati().add(allegati);
                allegati.setNomeAttachment(documents.getBT0122SupportingDocumentReference().get(0).getValue());

                if (!documents.getBT0123SupportingDocumentDescription().isEmpty()) {
                    allegati.setDescrizioneAttachment(documents.getBT0123SupportingDocumentDescription().get(0).getValue());
                }

                if (!documents.getBT0124ExternalDocumentLocation().isEmpty() && documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                    allegati.setAttachment(documents.getBT0124ExternalDocumentLocation().get(0).getValue().getBytes());
                } else if (documents.getBT0124ExternalDocumentLocation().isEmpty() && !documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                    Binary file = documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().get(0).getValue();
                    allegati.setAttachment(file.getBytes());
                    allegati.setFormatoAttachment(file.getMimeType().toString());
                    allegati.setNomeAttachment(file.getFileName());
                }

            }
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
                errors.add(ConversionIssue.newError(e, IConstants.ERROR_INVOICE_LEVEL_ALLOWANCES));
            }

            invoiceDiscountAmount = baseAmount * -percentageDiscount;
        }
    }

    /**
     * Checks if the sum of all lines without VAT matches total without VAT from CEN invoice.
     * If not, it generates a correction line. This can occur if discounts or surcharges are converted from percent to values.
     */
    private void calculateCorrectionForTotalAmount() {

        // let's check if we have all the info we need.
        if (!(fatturaElettronicaBody != null &&
                fatturaElettronicaBody
                        .getDatiBeniServizi() != null &&
                fatturaElettronicaBody
                        .getDatiBeniServizi()
                        .getDettaglioLinee() != null)) return;

        Double invoiceTotal = 0d;
        try {
            List<DettaglioLineeType> lineList = fatturaElettronicaBody
                    .getDatiBeniServizi()
                    .getDettaglioLinee();
            for (DettaglioLineeType line : lineList) {
                if (line.getPrezzoTotale() != null) {
                    invoiceTotal += line.getPrezzoTotale().doubleValue();
                }
            }
        } catch (Exception e) {
            errors.add(ConversionIssue.newError(e));
            log.error(e.getMessage(), e);
        }
        try {
            BG0022DocumentTotals documentTotals = invoice.getBG0022DocumentTotals().get(0);
            Double actualInvoiceTotal = documentTotals.getBT0109InvoiceTotalAmountWithoutVat().get(0).getValue();
            invoiceCorrectionAmount = actualInvoiceTotal - invoiceTotal;
        } catch (Exception e) {
            errors.add(ConversionIssue.newError(e, IConstants.ERROR_TOTAL_AMOUNT_CORRECTION));
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
        log.info("Starting converting DatiPagamento");
        DettaglioPagamentoType dettaglioPagamento = factory.createDettaglioPagamentoType();
        DatiPagamentoType datiPagamento = factory.createDatiPagamentoType();
        datiPagamento.getDettaglioPagamento().add(dettaglioPagamento);
//        datiPagamento.setCondizioniPagamento(CondizioniPagamentoType.TP_01); // FIXME no actual mapping, dummy value
        fatturaElettronicaBody.getDatiPagamento().add(datiPagamento);
        try {
            if (!invoice.getBG0010Payee().isEmpty()) {
                BG0010Payee payee = invoice.getBG0010Payee().get(0);
                dettaglioPagamento.setBeneficiario(payee.getBT0059PayeeName().get(0).getValue());
            }

            if (!invoice.getBG0016PaymentInstructions().isEmpty()) {
                BG0016PaymentInstructions paymentInstructions = invoice.getBG0016PaymentInstructions().get(0);

                Untdid4461PaymentMeansCode paymentMeansCode = paymentInstructions.getBT0081PaymentMeansTypeCode().get(0).getValue();
                switch (paymentMeansCode) {

                    case Code30:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_05);
                        break;
                    case Code20:
                    case Code92:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_02);
                        break;
                    case Code10:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_01);
                        break;
                    case Code21:
                    case Code22:
                    case Code23:
                    case Code91:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_03);
                        break;
                    case Code60:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_06);
                        break;
                    case Code70:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_12);
                        break;
                    case Code15:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_15);
                        break;
                    case Code50:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_18);
                        break;
                    case Code1:
                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_22);
                        break;
                    default:
                        dettaglioPagamento.setModalitaPagamento(null);
                }

                if (!paymentInstructions.getBT0083RemittanceInformation().isEmpty()) {
                    dettaglioPagamento.setCodicePagamento(paymentInstructions.getBT0083RemittanceInformation().get(0).getValue());
                }

                if (!paymentInstructions.getBG0017CreditTransfer().isEmpty()) {
                    Iterator<BG0017CreditTransfer> itr = paymentInstructions.getBG0017CreditTransfer().iterator();
                    BG0017CreditTransfer first = itr.next();
                    dettaglioPagamento.setIBAN(first.getBT0084PaymentAccountIdentifier().get(0).getValue());
                    if (!first.getBT0085PaymentAccountName().isEmpty()) {
                        dettaglioPagamento.setBeneficiario(first.getBT0085PaymentAccountName().get(0).getValue());
                    }

                    if (!first.getBT0086PaymentServiceProviderIdentifier().isEmpty()) {
                        dettaglioPagamento.setBIC(first.getBT0086PaymentServiceProviderIdentifier().get(0).getValue());
                    }
                    while (itr.hasNext()) {
                        BG0017CreditTransfer creditTransfer = itr.next();
                        DettaglioPagamentoType dettaglioPagamentoNew = factory.createDettaglioPagamentoType();

                        dettaglioPagamentoNew.setIBAN(creditTransfer.getBT0084PaymentAccountIdentifier().get(0).getValue());
                        if (!creditTransfer.getBT0085PaymentAccountName().isEmpty()) {
                            dettaglioPagamentoNew.setBeneficiario(creditTransfer.getBT0085PaymentAccountName().get(0).getValue());
                        }

                        if (!creditTransfer.getBT0086PaymentServiceProviderIdentifier().isEmpty()) {
                            dettaglioPagamentoNew.setBIC(creditTransfer.getBT0086PaymentServiceProviderIdentifier().get(0).getValue());
                        }
                        datiPagamento.getDettaglioPagamento().add(dettaglioPagamentoNew);
                    }
                }
            }

            dettaglioPagamento.setDataScadenzaPagamento(Cen2FattPAConverterUtils.fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(invoice.getBT0009PaymentDueDate().get(0).getValue()));
            dettaglioPagamento.setImportoPagamento(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoice.getBG0022DocumentTotals().get(0).getBT0115AmountDueForPayment().get(0).getValue()));
        } catch (Exception e) {
            errors.add(ConversionIssue.newError(e, IConstants.ERROR_PAYMENT_INFORMATION));
        }
    }

    @Override
    public void computeMultipleCenElements2FpaField() {
//        transformInvoiceLinesWithItemPriceBaseQuantity(); // This must be first, order is important
//        calculateDiscount();
//        addDiscountLine();
//        calculateCorrectionForTotalAmount();
//        addCorrectionLine();
        addAttachment();
    }

    private void addAttachment() {
        FattPaAttachmentConverter attachmentConverter = FattPaAttachmentConverter.builder(conversionRegistry, new Reflections("it.infocert.eigor"), invoice, errors).pathsList(Lists.newArrayList(
                "/BT0007",
                "/BT0010",
                "/BT0014",
                "/BT0018",
                "/BG0002/BT0023",
                "/BG0004/BT0028",
                "/BG0004/BT0033",
                "/BG0004/BT0034",
                "/BG0007/BT0045",
                "/BG0007/BT0047",
                "/BG0007/BG0009/BT0057",
                "/BG0007/BG0009/BT0058"
        )).build();

        String attachment = attachmentConverter.createAttachment();
        if (!"".equals(attachment)) {
            List<AllegatiType> allegati = fatturaElettronicaBody.getAllegati();
            AllegatiType allegato = new AllegatiType();
            allegato.setNomeAttachment("unmapped-cen-elements"); //TODO How to name it?
            allegato.setFormatoAttachment("txt");
            allegato.setAttachment(attachment.getBytes());
            allegati.add(allegato);
        }
        setAllegati();
    }

    private void transformInvoiceLinesWithItemPriceBaseQuantity() {
        List<BG0025InvoiceLine> invoiceLineList = invoice.getBG0025InvoiceLine();

        if (!invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
            List<BG0020DocumentLevelAllowances> documentLevelAllowances = invoice.getBG0020DocumentLevelAllowances();
            DettaglioLineeType dettaglioLinee;
            for (int j = 0; j < documentLevelAllowances.size(); j++) {
                BG0020DocumentLevelAllowances documentLevelAllowance = documentLevelAllowances.get(j);
                dettaglioLinee = fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee().get(j);
                List<BT0095DocumentLevelAllowanceVatCategoryCode> codes = documentLevelAllowance.getBT0095DocumentLevelAllowanceVatCategoryCode();
                if (!codes.isEmpty()) {
                	Untdid5305DutyTaxFeeCategories category = codes.get(0).getValue();
                	switch (category) {
                	case Z:
                		dettaglioLinee.setNatura(NaturaType.N_3); //TODO assert in which case this must be N_3 or N_7 (see code list mapping)
                		break;
                	case E:
                		dettaglioLinee.setNatura(NaturaType.N_4);
                		break;
                	case G:
                		dettaglioLinee.setNatura(NaturaType.N_2);
                		break;
                	case O:
                		dettaglioLinee.setNatura(NaturaType.N_2); //TODO assert in which case this must be N_2 or N_1 (see code list mapping)
                		break;
                	default:
                		dettaglioLinee.setNatura(null);
                	}
                }

                if (!documentLevelAllowance.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                    dettaglioLinee.setAliquotaIVA(BigDecimal.valueOf(documentLevelAllowance.getBT0096DocumentLevelAllowanceVatRate().get(0).getValue()));
                }

                StringBuilder sb = new StringBuilder();
                if (!documentLevelAllowance.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                    sb.append(documentLevelAllowance.getBT0097DocumentLevelAllowanceReason().get(0).getValue());
                } else {
                    sb.append("Sconto documento");
                }

                if (!documentLevelAllowance.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                    BT0098DocumentLevelAllowanceReasonCode reasonCode = documentLevelAllowance.getBT0098DocumentLevelAllowanceReasonCode().get(0);
                    sb.append(" ").append(reasonCode.getValue());
                }

                dettaglioLinee.setQuantita(BigDecimal.valueOf(documentLevelAllowance.getBT0092DocumentLevelAllowanceAmount().get(0).getValue()));

                if (!invoice.getBG0021DocumentLevelCharges().isEmpty()) {

                    BG0021DocumentLevelCharges documentLevelCharges = invoice.getBG0021DocumentLevelCharges().get(j); //FIXME Why is this mapping to the same fields as BG0020?


                }
            }
        }

        for (int i = 0; i < invoiceLineList.size(); i++) {
            BG0025InvoiceLine invoiceLine = invoiceLineList.get(i);

            if (fatturaElettronicaBody.getDatiBeniServizi() == null ||
                    fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee() == null) break;

            DettaglioLineeType dettaglioLinee = fatturaElettronicaBody
                    .getDatiBeniServizi()
                    .getDettaglioLinee()
                    .get(i);


            if (!(invoiceLine.getBG0029PriceDetails(0).getBT0149ItemPriceBaseQuantity().isEmpty() &&
                    invoiceLine.getBG0029PriceDetails(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty())) {

                try {
                    Double bt0129 = invoiceLine.getBT0129InvoicedQuantity(0).getValue();
                    String bt0130 = invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue().getCommonCode();
                    Double bt0149 = invoiceLine.getBG0029PriceDetails(0).getBT0149ItemPriceBaseQuantity(0).getValue();
                    String bt0150 = invoiceLine.getBG0029PriceDetails(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue().getCommonCode();

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
                } catch (IndexOutOfBoundsException e) {
                    log.warn("Invoice Line number {} is missing some elements", invoiceLine.getBT0126InvoiceLineIdentifier());
                    return;
                } catch (Exception e) {
                    errors.add(ConversionIssue.newError(e, IConstants.ERROR_LINE_PROCESSING));
                    log.error(e.getMessage(), e);
                    return;
                }
            }
        }
    }

    @Override
    public void transformFpaFields() {

    }

    private void logBt(int bt, String name) {
        log.debug("Converted BT-{} into {}", bt, name);
    }

    public void setConversionRegistry(ConversionRegistry conversionRegistry) {
        this.conversionRegistry = conversionRegistry;
    }
}
