package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverter {

    public SellerConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    public ConversionResult<BG0000Invoice> toBT0029(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BT0029SellerIdentifierAndSchemeIdentifier bt0029 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> sellerTradeParties = null;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeAgreement");

            if (child1 != null) {
                sellerTradeParties = findNamespaceChildren(child1, namespacesInScope, "SellerTradeParty");

                for(Element elem : sellerTradeParties) {

                    Element id =  findNamespaceChild(elem, namespacesInScope, "ID");
                    Element globalID = findNamespaceChild(elem, namespacesInScope, "GlobalID");

                    if (globalID != null) {
                        bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(globalID.getText());
                        invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt0029);
                    }
                    else if (id != null) {
                        bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(id.getText());
                        invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt0029);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }
}