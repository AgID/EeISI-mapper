package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * The Document Context Converter
 */
public class DocumentContextConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");

        Element exchangedDocumentContext = findNamespaceChild(rootElement, namespacesInScope, "ExchangedDocumentContext");
        if (exchangedDocumentContext == null) {
            exchangedDocumentContext = new Element("ExchangedDocumentContext", rsmNs);
            rootElement.addContent(exchangedDocumentContext);
        }
        final Element guidelineSpecifiedDocumentContextParameter = new Element("GuidelineSpecifiedDocumentContextParameter", ramNs);
        final Element gId = new Element("ID", ramNs).setText("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0");
        final Element businessProcessSpecifiedDocumentContextParameter = new Element("BusinessProcessSpecifiedDocumentContextParameter", ramNs);
        final Element bId = new Element("ID", ramNs).setText("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");

        guidelineSpecifiedDocumentContextParameter.addContent(gId);
        exchangedDocumentContext.addContent(guidelineSpecifiedDocumentContextParameter);
        businessProcessSpecifiedDocumentContextParameter.addContent(bId);
        exchangedDocumentContext.addContent(businessProcessSpecifiedDocumentContextParameter);

       /* if (!cenInvoice.getBG0002ProcessControl().isEmpty()) {
            BG0002ProcessControl bg0002 = cenInvoice.getBG0002ProcessControl(0);
            {
                Element businessProcessSpecifiedDocumentContextParameter = new Element("BusinessProcessSpecifiedDocumentContextParameter", ramNs);
                Element id = new Element("ID", ramNs);
                if (!bg0002.getBT0023BusinessProcessType().isEmpty()) {
                    BT0023BusinessProcessType bt0023 = bg0002.getBT0023BusinessProcessType(0);
                    id.setText(bt0023.getValue());
                } else {
                    id.setText("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0");
                }
                businessProcessSpecifiedDocumentContextParameter.addContent(id);
                exchangedDocumentContext.addContent(businessProcessSpecifiedDocumentContextParameter);
            }
            {
                final Element guidelineSpecifiedDocumentContextParameter = new Element("GuidelineSpecifiedDocumentContextParameter", ramNs);
                final Element id = new Element("ID", ramNs);
                if (!bg0002.getBT0024SpecificationIdentifier().isEmpty()) {
                    BT0024SpecificationIdentifier bt0024 = bg0002.getBT0024SpecificationIdentifier(0);
                    id.setText(bt0024.getValue());
                } else {
//                    id.setText("urn:cen.eu:en16931:2017");
//                    PEPPOL hardcoding
                    id.setText("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0");
                }
                guidelineSpecifiedDocumentContextParameter.addContent(id);
                exchangedDocumentContext.addContent(guidelineSpecifiedDocumentContextParameter);
            }
        }*/
    }
}
