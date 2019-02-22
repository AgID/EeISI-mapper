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
 * The Buyer Custom Converter
 */
public class BuyerTradePartyConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");

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

        // <xsd:element name="Reference" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>
        // not generated

        // <xsd:element name="BuyerReference" type="udt:TextType" minOccurs="0"/>
        // <xsd:element name="SellerTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="BuyerTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="SalesAgentTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="BuyerRequisitionerTradeParty" type="ram:TradePartyType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="BuyerAssignedAccountantTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="SellerAssignedAccountantTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="BuyerTaxRepresentativeTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="SellerTaxRepresentativeTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="ProductEndUserTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="ApplicableTradeDeliveryTerms" type="ram:TradeDeliveryTermsType" minOccurs="0"/>
        // <xsd:element name="SellerOrderReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="BuyerOrderReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="QuotationReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="OrderResponseReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="ContractReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="DemandForecastReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="SupplyInstructionReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="PromotionalDealReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="PriceListReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="AdditionalReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="RequisitionerReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="BuyerAgentTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="PurchaseConditionsReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="SpecifiedProcuringProject" type="ram:ProcuringProjectType" minOccurs="0"/>
        // <xsd:element name="UltimateCustomerOrderReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>




    }
}

