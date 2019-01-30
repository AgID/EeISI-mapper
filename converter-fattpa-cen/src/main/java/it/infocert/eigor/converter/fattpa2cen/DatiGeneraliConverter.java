package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.DateTime;
import java.util.List;

/**
 * The Dati Generali Converter
 */
public class DatiGeneraliConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0003(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");
        if(fatturaElettronicaBody != null) {
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            if (datiGenerali != null) {
                Element datiFattureCollegate = datiGenerali.getChild("DatiFattureCollegate");
                if (datiFattureCollegate != null) {
                    BG0003PrecedingInvoiceReference bg0003 = new BG0003PrecedingInvoiceReference();
                    Element idDocumento = datiFattureCollegate.getChild("IdDocumento");
                    if(idDocumento != null){
                        BT0025PrecedingInvoiceReference bg0025 = new BT0025PrecedingInvoiceReference(idDocumento.getText());
                        bg0003.getBT0025PrecedingInvoiceReference().add(bg0025);
                    }
                    Element data = datiFattureCollegate.getChild("Data");
                    if(data != null){
                        BT0026PrecedingInvoiceIssueDate bt0026 = new BT0026PrecedingInvoiceIssueDate(new org.joda.time.LocalDate(data.getText()));
                        bg0003.getBT0026PrecedingInvoiceIssueDate().add(bt0026);
                    }
                    invoice.getBG0003PrecedingInvoiceReference().add(bg0003);
                }
                Element fatturaPrincipale = datiGenerali.getChild("FatturaPrincipale");
                if (fatturaPrincipale != null) {
                    BG0003PrecedingInvoiceReference bg0003PrecedingInvoiceReference = new BG0003PrecedingInvoiceReference();
                    Element numeroFatturaPrincipale = fatturaPrincipale.getChild("NumeroFatturaPrincipale");
                    if(numeroFatturaPrincipale != null){
                        BT0025PrecedingInvoiceReference bg0025 = new BT0025PrecedingInvoiceReference(numeroFatturaPrincipale.getText());
                        bg0003PrecedingInvoiceReference.getBT0025PrecedingInvoiceReference().add(bg0025);
                    }
                    Element dataFatturaPrincipale = fatturaPrincipale.getChild("DataFatturaPrincipale");
                    if(dataFatturaPrincipale != null){
                        BT0026PrecedingInvoiceIssueDate bt0026 = new BT0026PrecedingInvoiceIssueDate(new org.joda.time.LocalDate(dataFatturaPrincipale.getText()));
                        bg0003PrecedingInvoiceReference.getBT0026PrecedingInvoiceIssueDate().add(bt0026);
                    }
                    invoice.getBG0003PrecedingInvoiceReference().add(bg0003PrecedingInvoiceReference);
                }
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    public ConversionResult<BG0000Invoice> toBG0013(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        BG0013DeliveryInformation bg0013 = new BG0013DeliveryInformation();

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");
        if(fatturaElettronicaBody != null) {
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            if (datiGenerali != null) {
                Element datiTrasporto = datiGenerali.getChild("DatiTrasporto");
                if (datiTrasporto != null) {
                    BT0072ActualDeliveryDate bt0072 = null;
                    Element dataInizioTrasporto = datiTrasporto.getChild("DataInizioTrasporto");
                    if(dataInizioTrasporto != null){
                        bt0072 = new BT0072ActualDeliveryDate(new org.joda.time.LocalDate(dataInizioTrasporto.getText()));
                        bg0013.getBT0072ActualDeliveryDate().add(bt0072);
                    }
                    Element dataOraConsegna = datiTrasporto.getChild("DataOraConsegna");
                    if(dataOraConsegna != null){
                        bt0072 = new BT0072ActualDeliveryDate(new org.joda.time.LocalDate(DateTime.parse(dataOraConsegna.getText())));
                    }
                    BG0013DeliveryInformation bg0013DeliveryInformation;
                    if(!invoice.getBG0013DeliveryInformation().isEmpty() && bt0072 != null){
                        bg0013DeliveryInformation = invoice.getBG0013DeliveryInformation().get(0);
                        bg0013DeliveryInformation.getBT0072ActualDeliveryDate().add(bt0072);
                    } else if (bt0072 != null) {
                        bg0013DeliveryInformation = new BG0013DeliveryInformation();
                        bg0013DeliveryInformation.getBT0072ActualDeliveryDate().add(bt0072);
                        invoice.getBG0013DeliveryInformation().add(bg0013DeliveryInformation);
                    }
                }
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0003(document, cenInvoice, errors, callingLocation);
        toBG0013(document, cenInvoice, errors, callingLocation);
    }
}
