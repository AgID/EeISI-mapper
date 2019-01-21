package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

//import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
//import it.infocert.eigor.api.conversion.converter.TypeConverter;
//import org.joda.time.LocalDate;

/**
 * The Document Context Converter
 */
public class DocumentContextConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        EigorConfiguration configuration = DefaultEigorConfigurationLoader.configuration();

//        TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");

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

        String guidId = bt24OrNull(cenInvoice);
        if(guidId == null || guidId.trim().isEmpty()) guidId = configuration.getMandatoryString("eigor.converter.cen-cii.guideline-context");

        final Element gId = new Element("ID", ramNs).setText(guidId);
        final Element businessProcessSpecifiedDocumentContextParameter = new Element("BusinessProcessSpecifiedDocumentContextParameter", ramNs);

        String bizProcId = bt23OrNull(cenInvoice);
        if(bizProcId == null || guidId.trim().isEmpty()) bizProcId = ""; //configuration.getMandatoryString("eigor.converter.cen-cii.business-context");
        final Element bId = new Element("ID", ramNs).setText(bizProcId);

        businessProcessSpecifiedDocumentContextParameter.addContent(bId);
        exchangedDocumentContext.addContent(businessProcessSpecifiedDocumentContextParameter);
        guidelineSpecifiedDocumentContextParameter.addContent(gId);
        exchangedDocumentContext.addContent(guidelineSpecifiedDocumentContextParameter);

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

    private String bt23OrNull(BG0000Invoice cenInvoice) {
        try {
            return cenInvoice.getBG0002ProcessControl(0).getBT0023BusinessProcessType(0).getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    private String bt24OrNull(BG0000Invoice cenInvoice) {
        try {
            return cenInvoice.getBG0002ProcessControl(0).getBT0024SpecificationIdentifier(0).getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return null;
        }
    }

}
