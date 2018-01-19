package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0016PaymentInstructions;
import it.infocert.eigor.model.core.model.BG0019DirectDebit;
import it.infocert.eigor.model.core.model.BT0089MandateReferenceIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * The Payment Terms Custom Converter
 */
public class PaymentTermsConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");
        Namespace udtNs = rootElement.getNamespace("udt");

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
            rootElement.addContent(supplyChainTradeTransaction);
        }

        Element applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
        if (applicableHeaderTradeAgreement == null) {
            applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
        }

        Element specifiedTradePaymentTerms = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "SpecifiedTradePaymentTerms");
        if (specifiedTradePaymentTerms == null) {
            specifiedTradePaymentTerms = new Element("SpecifiedTradePaymentTerms", ramNs);
            applicableHeaderTradeAgreement.addContent(specifiedTradePaymentTerms);
        }

        if (!cenInvoice.getBT0009PaymentDueDate().isEmpty()) {
            LocalDate bt0009 = cenInvoice.getBT0009PaymentDueDate(0).getValue();
            Element dueDateDateTime = new Element("DueDateDateTime", ramNs);
            Element dateTimeString = new Element("DateTimeString", udtNs);
            dateTimeString.setAttribute("format", "102");
            try {
                dateTimeString.setText(dateStrConverter.convert(bt0009));
                dueDateDateTime.addContent(dateTimeString);
                specifiedTradePaymentTerms.addContent(dueDateDateTime);
            } catch (IllegalArgumentException | ConversionFailedException e) {
                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("InvoiceNoteConverter").build());
                errors.add(ConversionIssue.newError(ere));
            }
        }

        if (!cenInvoice.getBT0020PaymentTerms().isEmpty()) {
            String bt0020 = cenInvoice.getBT0020PaymentTerms(0).getValue();
            Element description = new Element("Description", ramNs);
            description.setText(bt0020);
            specifiedTradePaymentTerms.addContent(description);
        }

        List<BG0016PaymentInstructions> bg0016s = cenInvoice.getBG0016PaymentInstructions();
        if (!bg0016s.isEmpty()) {
            List<BG0019DirectDebit> bg0019s = bg0016s.get(0).getBG0019DirectDebit();
            if (!bg0019s.isEmpty()) {
                List<BT0089MandateReferenceIdentifier> bt0089s = bg0019s.get(0).getBT0089MandateReferenceIdentifier();
                if (!bt0089s.isEmpty()) {
                    BT0089MandateReferenceIdentifier bt0089 = bt0089s.get(0);
                    Element directDebitMandateID = new Element("DirectDebitMandateID", ramNs);
                    directDebitMandateID.setText(bt0089.getValue());
                    specifiedTradePaymentTerms.addContent(directDebitMandateID);
                }
            }
        }
    }
}

