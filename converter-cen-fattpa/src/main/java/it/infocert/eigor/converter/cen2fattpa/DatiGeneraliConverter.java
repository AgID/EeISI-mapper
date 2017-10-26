package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.LocalDateToXMLGregorianCalendarConverter;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.*;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class DatiGeneraliConverter implements CustomMapping<FatturaElettronicaType> {
    private static final Logger log = LoggerFactory.getLogger(DatiGeneraliConverter.class);

    private final LocalDateToXMLGregorianCalendarConverter dateConverter = new LocalDateToXMLGregorianCalendarConverter();

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
        int size = bodies.size();
        if (size > 1) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("Too many FatturaElettronicaBody found in current FatturaElettronica")));
        } else if (size < 1) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No FatturaElettronicaBody found in current FatturaElettronica")));
        } else {
            FatturaElettronicaBodyType fatturaElettronicaBody = bodies.get(0);
            DatiGeneraliType datiGenerali = fatturaElettronicaBody.getDatiGenerali();
            addCausale(invoice, datiGenerali, errors);
            addFattureCollegate(invoice, datiGenerali, errors);
            addDatiTrasporto(invoice, datiGenerali, errors);
        }
    }


    private void addCausale(BG0000Invoice invoice, DatiGeneraliType datiGenerali, List<IConversionIssue> errors) {
        if (datiGenerali != null) {
            DatiGeneraliDocumentoType datiGeneraliDocumento = datiGenerali.getDatiGeneraliDocumento();
            if (datiGeneraliDocumento != null) {
                if (!invoice.getBT0020PaymentTerms().isEmpty()) {
                    BT0020PaymentTerms paymentTerms = invoice.getBT0020PaymentTerms(0);
                    datiGeneraliDocumento.getCausale().add(paymentTerms.getValue());
                }
                if (!invoice.getBG0001InvoiceNote().isEmpty()) {
                    BG0001InvoiceNote invoiceNote = invoice.getBG0001InvoiceNote(0);
                    if (!invoiceNote.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                        BT0021InvoiceNoteSubjectCode invoiceNoteSubjectCode = invoiceNote.getBT0021InvoiceNoteSubjectCode(0);
                        String noteText = invoiceNoteSubjectCode.getValue();
                        manageNoteText(datiGeneraliDocumento, noteText);
                    }
                    if (!invoiceNote.getBT0022InvoiceNote().isEmpty()) {
                        String note = invoiceNote.getBT0022InvoiceNote(0).getValue();
                        manageNoteText(datiGeneraliDocumento, note);
                    }
                }
                if (!invoice.getBG0004Seller().isEmpty()) {
                    BG0004Seller seller = invoice.getBG0004Seller(0);
                    if (!seller.getBG0006SellerContact().isEmpty()) {
                        BG0006SellerContact sellerContact = seller.getBG0006SellerContact(0);
                        if (!sellerContact.getBT0041SellerContactPoint().isEmpty()) {
                            BT0041SellerContactPoint sellerContactPoint = sellerContact.getBT0041SellerContactPoint(0);
                            datiGeneraliDocumento.getCausale().add(sellerContactPoint.getValue());
                        }
                    }
                }
                if (!invoice.getBG0007Buyer().isEmpty()) {
                    BG0007Buyer buyer = invoice.getBG0007Buyer(0);
                    if (!buyer.getBG0009BuyerContact().isEmpty()) {
                        BG0009BuyerContact buyerContact = buyer.getBG0009BuyerContact(0);
                        if (!buyerContact.getBT0056BuyerContactPoint().isEmpty()) {
                            BT0056BuyerContactPoint buyerContactPoint = buyerContact.getBT0056BuyerContactPoint(0);
                            datiGeneraliDocumento.getCausale().add(buyerContactPoint.getValue());
                        }
                    }
                }
                if (!invoice.getBG0016PaymentInstructions().isEmpty()) {
                    BG0016PaymentInstructions paymentInstructions = invoice.getBG0016PaymentInstructions(0);
                    if (!paymentInstructions.getBT0082PaymentMeansText().isEmpty()) {
                        BT0082PaymentMeansText paymentMeansText = paymentInstructions.getBT0082PaymentMeansText(0);
                        datiGeneraliDocumento.getCausale().add(paymentMeansText.getValue());
                    }
                }
            } else {
                errors.add(ConversionIssue.newError(new IllegalArgumentException("No DatiGeneraliDocumento was found in current DatiGenerali")));
            }
        } else {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No DatiGenerali was found in current FatturaElettronicaBody")));
        }

    }

    private void addFattureCollegate(BG0000Invoice invoice, DatiGeneraliType datiGenerali, List<IConversionIssue> errors) {
        if (!invoice.getBG0003PrecedingInvoiceReference().isEmpty()) {
            List<DatiDocumentiCorrelatiType> datiFattureCollegate = datiGenerali.getDatiFattureCollegate();
            for (BG0003PrecedingInvoiceReference precedingInvoiceReference : invoice.getBG0003PrecedingInvoiceReference()) {
                DatiDocumentiCorrelatiType fatturaCollegata = new DatiDocumentiCorrelatiType();
                datiFattureCollegate.add(fatturaCollegata);

                if (!precedingInvoiceReference.getBT0025PrecedingInvoiceReference().isEmpty()) {
                    BT0025PrecedingInvoiceReference invoiceReference = precedingInvoiceReference.getBT0025PrecedingInvoiceReference(0);
                    fatturaCollegata.setIdDocumento(invoiceReference.getValue());
                }

                if (!precedingInvoiceReference.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                    try {
                        XMLGregorianCalendar precedingInvoiceIssueDate = dateConverter.convert(precedingInvoiceReference.getBT0026PrecedingInvoiceIssueDate(0).getValue());
                        fatturaCollegata.setData(precedingInvoiceIssueDate);
                    } catch (EigorRuntimeException e) {
                        errors.add(ConversionIssue.newError(e));
                    }
                }
            }
        }

    }

    private void addDatiTrasporto(BG0000Invoice invoice, DatiGeneraliType datiGenerali, List<IConversionIssue> errors) {
        if (!invoice.getBG0013DeliveryInformation().isEmpty()) {
            BG0013DeliveryInformation deliveryInformation = invoice.getBG0013DeliveryInformation(0);
            if (!deliveryInformation.getBT0072ActualDeliveryDate().isEmpty()) {
                LocalDate actualDeliveryDate = deliveryInformation.getBT0072ActualDeliveryDate(0).getValue();
                DatiTrasportoType datiTrasporto = datiGenerali.getDatiTrasporto();
                if (datiTrasporto == null) {
                    datiTrasporto = new DatiTrasportoType();
                    datiGenerali.setDatiTrasporto(datiTrasporto);
                }
                XMLGregorianCalendar convertedDate = dateConverter.convert(actualDeliveryDate);
                datiTrasporto.setDataOraConsegna(convertedDate);
            }
            if (!deliveryInformation.getBG0015DeliverToAddress().isEmpty()) {
                BG0015DeliverToAddress deliverToAddress = deliveryInformation.getBG0015DeliverToAddress(0);
//                if (invoicingPeriod.)
            }
        }
    }

    private void manageNoteText(DatiGeneraliDocumentoType datiGeneraliDocumento, String noteText) {
        if (noteText.length() > 200) {
            datiGeneraliDocumento.getCausale().add(noteText.substring(0, 200));
            manageNoteText(datiGeneraliDocumento, noteText.substring(200));
        } else {
            datiGeneraliDocumento.getCausale().add(noteText);
        }
    }
}
