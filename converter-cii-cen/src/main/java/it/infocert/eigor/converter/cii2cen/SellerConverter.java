package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0031SellerVatIdentifier;
import it.infocert.eigor.model.core.model.BT0032SellerTaxRegistrationIdentifier;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBT0029_31_32(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BT0029SellerIdentifierAndSchemeIdentifier bt0029;
        BT0031SellerVatIdentifier bt0031;
        BT0032SellerTaxRegistrationIdentifier bt0032;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element sellerTradeParties = null;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeAgreement");

            if (child1 != null) {
                sellerTradeParties = findNamespaceChild(child1, namespacesInScope, "SellerTradeParty");

                if (sellerTradeParties != null) {

                    List<Element> id =  findNamespaceChildren(sellerTradeParties, namespacesInScope, "ID");
                    List<Element> globalID = findNamespaceChildren(sellerTradeParties, namespacesInScope, "GlobalID");
                    Attribute schemeID = null;

                    if (!globalID.isEmpty()) {
                        for (Element elemGlobalID : globalID) {
                            schemeID = elemGlobalID.getAttribute("schemeID");
                            if (schemeID != null) {
                                bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(elemGlobalID.getAttributeValue("schemeID"), elemGlobalID.getText()));
                            } else {
                                bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(elemGlobalID.getText()));
                            }
                            invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt0029);
                        }
                    }
                    else {
                        for (Element elemID : id) {
                            schemeID = elemID.getAttribute("schemeID");
                            if (schemeID != null) {
                                bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(elemID.getAttributeValue("schemeID"), elemID.getText()));
                            } else {
                                bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(elemID.getText()));
                            }
                            invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt0029);
                        }
                    }

                    //BT0031-BT0032
                    List<Element> specifiedTaxRegistrations = findNamespaceChildren(sellerTradeParties, namespacesInScope, "SpecifiedTaxRegistration");
                    Element idTax = null;
                    Attribute schemeIDTax = null;
                    for (Element elemSpecTax : specifiedTaxRegistrations) {
                        idTax = findNamespaceChild(elemSpecTax, namespacesInScope, "ID");
                        if (idTax != null) {
                            schemeIDTax = idTax.getAttribute("schemeID");
                            if (schemeIDTax != null) {
                                if (schemeIDTax.getValue().equals("VAT")) {
                                    bt0031 = new BT0031SellerVatIdentifier(idTax.getText());
                                    invoice.getBG0004Seller(0).getBT0031SellerVatIdentifier().add(bt0031);
                                } else if (schemeIDTax.getValue().equals("FC")) {
                                    bt0032 = new BT0032SellerTaxRegistrationIdentifier(idTax.getText());
                                    invoice.getBG0004Seller(0).getBT0032SellerTaxRegistrationIdentifier().add(bt0032);
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBT0029_31_32(document, cenInvoice, errors);
    }
}
