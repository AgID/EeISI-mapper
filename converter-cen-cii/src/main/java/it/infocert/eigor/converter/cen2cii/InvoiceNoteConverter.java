package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * The Invoice Note Custom Converter
 */
public class InvoiceNoteConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!cenInvoice.getBG0001InvoiceNote().isEmpty()) {
            TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");

            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace ramNs = rootElement.getNamespace("ram");
            Namespace udtNs = rootElement.getNamespace("udt");

            Element exchangedDocument = findNamespaceChild(rootElement, namespacesInScope, "ExchangedDocument");

            if (exchangedDocument == null) {
                exchangedDocument = new Element("ExchangedDocument", rootElement.getNamespace("rsm"));
                rootElement.addContent(exchangedDocument);
            }

            if (!cenInvoice.getBT0001InvoiceNumber().isEmpty()) {
                String bt0001 = cenInvoice.getBT0001InvoiceNumber(0).getValue();
                Element id = new Element("ID", ramNs);
                id.setText(bt0001);
                exchangedDocument.addContent(id);
            }

            if (!cenInvoice.getBT0003InvoiceTypeCode().isEmpty()) {
                Untdid1001InvoiceTypeCode bt0003 = cenInvoice.getBT0003InvoiceTypeCode(0).getValue();
                Element typeCode = new Element("TypeCode", ramNs);
                typeCode.setText(String.valueOf(bt0003.getCode()));
                exchangedDocument.addContent(typeCode);
            }

            if (!cenInvoice.getBT0002InvoiceIssueDate().isEmpty()) {
                LocalDate bt0002 = cenInvoice.getBT0002InvoiceIssueDate(0).getValue();
                Element issueDateTime = new Element("IssueDateTime", ramNs);
                Element dateTimeString = new Element("DateTimeString", udtNs);
                dateTimeString.setAttribute("format", "102");
                try {
                    dateTimeString.setText(dateStrConverter.convert(bt0002));
                    issueDateTime.addContent(dateTimeString);
                    exchangedDocument.addContent(issueDateTime);
                } catch (IllegalArgumentException | ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            List<BG0001InvoiceNote> bg0001InvoiceNote = cenInvoice.getBG0001InvoiceNote();
            for (BG0001InvoiceNote bg0001 : bg0001InvoiceNote) {
                Element includedNote = new Element("IncludedNote", ramNs);

                if (!bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                    Element subjectCode = new Element("SubjectCode", ramNs);
                    subjectCode.setText(bg0001.getBT0021InvoiceNoteSubjectCode(0).getValue());
                    includedNote.addContent(subjectCode);
                }

                if (!bg0001.getBT0022InvoiceNote().isEmpty()) {
                    Element content = new Element("Content", ramNs);
                    content.setText(bg0001.getBT0022InvoiceNote(0).getValue());
                    includedNote.addContent(content);
                }
                exchangedDocument.addContent(includedNote);
            }
        }
    }
}