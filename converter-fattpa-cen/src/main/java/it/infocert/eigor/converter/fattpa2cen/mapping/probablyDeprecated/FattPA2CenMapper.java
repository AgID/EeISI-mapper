package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.*;
import it.infocert.eigor.model.core.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;

public class FattPA2CenMapper {

    public static BG0000Invoice mapToCoreInvoice(FatturaElettronicaType fattura) {
        BG0000Invoice coreInvoice = new BG0000Invoice();
        mapHeader(fattura.getFatturaElettronicaHeader(), coreInvoice);
        mapBody(fattura.getFatturaElettronicaBody(), coreInvoice);

        return coreInvoice;
    }

    private static void mapHeader(FatturaElettronicaHeaderType header, BG0000Invoice invoice) {
        CedentePrestatoreType cedentePrestatore = header.getCedentePrestatore();
        invoice.getBG0004Seller().add(BG04SellerMapper.mapSeller(cedentePrestatore));
        invoice.getBG0007Buyer().add(BG07BuyerMapper.mapBuyer(header.getCessionarioCommittente()));

        RappresentanteFiscaleType rappresentanteFiscale = header.getRappresentanteFiscale();
        if (rappresentanteFiscale != null) {
            invoice.getBG0011SellerTaxRepresentativeParty().add(BG11SellerTaxRepresentativePartyMapper.mapSellerTaxRepresentativeParty(rappresentanteFiscale));
        }

        String riferimentoAmministrazione = mapBT19(cedentePrestatore);
        if (riferimentoAmministrazione != null) {
            invoice.getBT0019BuyerAccountingReference().add(new BT0019BuyerAccountingReference(riferimentoAmministrazione));
        }

        String buyerCode = mapBT49(header.getDatiTrasmissione());
        if (buyerCode != null) {
            invoice.getBG0007Buyer().get(0)
                    .getBT0049BuyerElectronicAddressAndSchemeIdentifier()
                    .add(new BT0049BuyerElectronicAddressAndSchemeIdentifier(buyerCode));
        }
    }

    private static void mapBody(List<FatturaElettronicaBodyType> bodyList, BG0000Invoice invoice) {
        for (FatturaElettronicaBodyType body : bodyList) {
            DatiGeneraliType datiGenerali = body.getDatiGenerali();

            invoice.getBG0003PrecedingInvoiceReference()
                    .add(BG03PrecedingInvoiceReferenceMapper.mapPrecedingInvoiceReferenceMapper(datiGenerali));

            invoice.getBT0001InvoiceNumber()
                    .add(new BT0001InvoiceNumber(mapBT01(datiGenerali)));

            invoice.getBT0002InvoiceIssueDate()
                    .add(new BT0002InvoiceIssueDate(mapBT02(datiGenerali)));


            String documentType = mapBT03(datiGenerali);

            List<DatiDocumentiCorrelatiType> datiContratto = datiGenerali.getDatiContratto();
            if (!datiContratto.isEmpty()) {
                for (DatiDocumentiCorrelatiType datiCorrelati : datiContratto) {
                    invoice.getBT0012ContractReference()
                            .add(new BT0012ContractReference(mapBT12(datiCorrelati)));

                    invoice.getBT0011ProjectReference()
                            .add(new BT0011ProjectReference(mapBT11(datiCorrelati)));

                    invoice.getBT0017TenderOrLotReference()
                            .add(new BT0017TenderOrLotReference(mapBT17(datiCorrelati)));
                }
            }

            List<DatiDocumentiCorrelatiType> datiOrdineAcquisto = datiGenerali.getDatiOrdineAcquisto();
            if (!datiOrdineAcquisto.isEmpty()) {
                for (DatiDocumentiCorrelatiType datiCorrelati : datiOrdineAcquisto) {
                    invoice.getBT0013PurchaseOrderReference()
                            .add(new BT0013PurchaseOrderReference(mapBT13(datiCorrelati)));
                }
            }

            List<DatiDocumentiCorrelatiType> datiRicezione = datiGenerali.getDatiRicezione();
            if (!datiRicezione.isEmpty()) {
                for (DatiDocumentiCorrelatiType datiCorrelati : datiRicezione) {
                    invoice.getBT0015ReceivingAdviceReference()
                            .add(new BT0015ReceivingAdviceReference(mapBT15(datiCorrelati)));
                }
            }

            List<DatiDDTType> datiDDT = datiGenerali.getDatiDDT();
            if (!datiDDT.isEmpty()) {
                for (DatiDDTType dati : datiDDT) {
                    invoice.getBT0016DespatchAdviceReference()
                            .add(new BT0016DespatchAdviceReference(mapBT16(dati)));
                }
            }

            List<DatiPagamentoType> datiPagamentoList = body.getDatiPagamento();
            if (!datiPagamentoList.isEmpty()) {
                for (DatiPagamentoType datiPagamento : datiPagamentoList) {
                    List<DettaglioPagamentoType> dettaglioPagamentoList = datiPagamento.getDettaglioPagamento();
                    if (!dettaglioPagamentoList.isEmpty()) {
                        for (DettaglioPagamentoType dettaglioPagamento : dettaglioPagamentoList) {
                            invoice.getBG0010Payee()
                                    .add(BG10PayeeMapper.mapPayee(dettaglioPagamento));
                        }
                    }
                }
            }

            DatiTrasportoType datiTrasporto = datiGenerali.getDatiTrasporto();
            if (datiTrasporto != null) {
                invoice.getBG0013DeliveryInformation()
                        .add(BG13DeliveryInformationMapper.mapDeliveryInformation(datiTrasporto));
            }

            String substring = documentType.substring(documentType.length() - 2);
//            Untdid1001InvoiceTypeCode attribute = Untdid1001InvoiceTypeCode.valueOf(substring); //TODO: Sta roba non corrisponde, il TipoDocumento non è mappabile a Untdid1001
//            invoice.getBT0003InvoiceTypeCode().add(new BT0003InvoiceTypeCode(attribute));

            if (datiGenerali.getDatiGeneraliDocumento().getDatiCassaPrevidenziale().isEmpty()) {
                invoice.getBG0021DocumentLevelCharges()
                        .add(BG21DocumentLevelChargesMapper.mapDocumentLevelCharges(datiGenerali.getDatiGeneraliDocumento()));
            }

            List<DatiRiepilogoType> datiRiepilogoList = body.getDatiBeniServizi().getDatiRiepilogo();
            if (!datiRiepilogoList.isEmpty()) {
                for (DatiRiepilogoType datiRiepilogo : datiRiepilogoList) {
                    invoice.getBG0023VatBreakdown()
                            .add(BG23VatBreakdownMapper.mapVatBreakdown(datiRiepilogo));
                }

            }

            List<DettaglioLineeType> dettaglioLinee = body.getDatiBeniServizi().getDettaglioLinee();
            if (!dettaglioLinee.isEmpty()) {
                for (DettaglioLineeType dettaglio : dettaglioLinee) {
                    invoice.getBG0025InvoiceLine()
                            .add(BG25InvoiceLineMapper.mapInvoiceLine(dettaglio));
                }
            }

        }
    }

    private static String mapBT01(DatiGeneraliType dati) {
        return dati.getDatiGeneraliDocumento().getNumero();
    }

    private static LocalDate mapBT02(DatiGeneraliType dati) {
        XMLGregorianCalendar data = dati.getDatiGeneraliDocumento().getData();
        return data.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private static String mapBT03(DatiGeneraliType dati) {
        return dati.getDatiGeneraliDocumento().getTipoDocumento().value();
    }

    private static String mapBT11(DatiDocumentiCorrelatiType dati) {
        return dati.getCodiceCUP();
    }

    private static String mapBT12(DatiDocumentiCorrelatiType dati) {
        return dati.getIdDocumento();
    }

    private static String mapBT13(DatiDocumentiCorrelatiType dati) {
        return dati.getIdDocumento();
    }

    private static String mapBT15(DatiDocumentiCorrelatiType dati) {
        return dati.getIdDocumento();
    }

    private static String mapBT16(DatiDDTType dati) {
        String numeroDDT = dati.getNumeroDDT();
        String dataDDT = dati.getDataDDT().toString();

        return numeroDDT + " " + dataDDT;
    }

    private static String mapBT17(DatiDocumentiCorrelatiType dati) {
        return dati.getCodiceCIG();
    }

    private static String mapBT19(CedentePrestatoreType cedentePrestatore) {
        return cedentePrestatore.getRiferimentoAmministrazione();
    }

    private static String mapBT49(DatiTrasmissioneType datiTrasmissione) {
        String codiceDestinatario = datiTrasmissione.getCodiceDestinatario();
        if ("0000000".equals(codiceDestinatario)) {
            return datiTrasmissione.getPECDestinatario();
        } else {
            return codiceDestinatario;
        }

    }

}
