package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0017TenderOrLotReference;
import it.infocert.eigor.model.core.model.BT0018InvoicedObjectIdentifierAndSchemeIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The TenderRefInvoicedObjectID Converter
 */
public class TenderRefInvoicedObjectID extends CustomConverter {

    public TenderRefInvoicedObjectID() {
        super(new Reflections("it.infocert"), new ConversionRegistry());
    }

    public ConversionResult<BG0000Invoice> toBT0017_18(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        if (supplyChainTradeTransaction != null) {
            Element applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement != null) {
                List<Element> additionalReferencedDocument = findNamespaceChildren(applicableHeaderTradeAgreement, namespacesInScope, "AdditionalReferencedDocument");

                for (Element elemAddRefDoc : additionalReferencedDocument) {

                    Element issuerAssignedID = findNamespaceChild(elemAddRefDoc, namespacesInScope, "IssuerAssignedID");
                    Element typeCode = findNamespaceChild(elemAddRefDoc, namespacesInScope, "TypeCode");

                    if (issuerAssignedID != null && typeCode != null) {
                        if (typeCode.getText().equals("50")) {
                            BT0017TenderOrLotReference bt0017 = new BT0017TenderOrLotReference(issuerAssignedID.getText() + " " + typeCode.getText());
                            invoice.getBT0017TenderOrLotReference().add(bt0017);
                        }
                        if (typeCode.getText().equals("130")) {
                            BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 = new BT0018InvoicedObjectIdentifierAndSchemeIdentifier(issuerAssignedID.getText() + " " + typeCode.getText());
                            invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().add(bt0018);
                        }
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }
}