package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Document Context Converter
 */
public class DocumentContextConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration configuration) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");

        // <xsd:complexType name="ExchangedDocumentContextType">
        Element exchangedDocumentContext = findNamespaceChild(rootElement, namespacesInScope, "ExchangedDocumentContext");
        if (exchangedDocumentContext == null) {
            exchangedDocumentContext = new Element("ExchangedDocumentContext", rsmNs);
            rootElement.addContent(exchangedDocumentContext);
        }

        // <xsd:sequence>
        // <xsd:element name="SpecifiedTransactionID" type="udt:IDType" minOccurs="0"/>
        // <xsd:element name="TestIndicator" type="udt:IndicatorType" minOccurs="0"/>
        // <xsd:element name="BusinessProcessSpecifiedDocumentContextParameter" type="ram:DocumentContextParameterType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="BIMSpecifiedDocumentContextParameter" type="ram:DocumentContextParameterType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="ScenarioSpecifiedDocumentContextParameter" type="ram:DocumentContextParameterType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="ApplicationSpecifiedDocumentContextParameter" type="ram:DocumentContextParameterType" minOccurs="0" maxOccurs="unbounded"/>

        // <xsd:element name="GuidelineSpecifiedDocumentContextParameter" type="ram:DocumentContextParameterType" minOccurs="0" maxOccurs="unbounded"/>
        final Element guidelineSpecifiedDocumentContextParameter = new Element("GuidelineSpecifiedDocumentContextParameter", ramNs);

        String guidId = bt24OrNull(cenInvoice);
        if(guidId == null || guidId.trim().isEmpty()) {
            guidId = configuration.getMandatoryString("eigor.converter.cen-cii.guideline-context");
        }

        final Element businessProcessSpecifiedDocumentContextParameter = new Element("BusinessProcessSpecifiedDocumentContextParameter", ramNs);

        String bizProcId = bt23OrNull(cenInvoice);
        if(bizProcId == null || guidId.trim().isEmpty()) bizProcId = ""; //configuration.getMandatoryString("eigor.converter.cen-cii.business-context");
        final Element bId = new Element("ID", ramNs).setText(bizProcId);

        businessProcessSpecifiedDocumentContextParameter.addContent(bId);
        exchangedDocumentContext.addContent(businessProcessSpecifiedDocumentContextParameter);
        guidelineSpecifiedDocumentContextParameter.addContent(new Element("ID", ramNs).setText(guidId));
        exchangedDocumentContext.addContent(guidelineSpecifiedDocumentContextParameter);


        // <xsd:element name="SubsetSpecifiedDocumentContextParameter" type="ram:DocumentContextParameterType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="MessageStandardSpecifiedDocumentContextParameter" type="ram:DocumentContextParameterType" minOccurs="0"/>
        // </xsd:sequence>


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
