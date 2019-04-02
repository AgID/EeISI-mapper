package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.codehaus.plexus.util.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NewApplicableHeaderTradeAgreementConverter extends CustomConverterUtils implements CustomMapping<Document> {

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


        // <xsd:complexType name="HeaderTradeAgreementType">
        // <xsd:sequence>
        // <xsd:element name="Reference" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="BuyerReference" type="udt:TextType" minOccurs="0"/>
        addBuyerReference(invoice, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="SellerTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        addSellerTradeParty(invoice, namespacesInScope, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="BuyerTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        addBuyerTradeParty(invoice, namespacesInScope, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="SalesAgentTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="BuyerRequisitionerTradeParty" type="ram:TradePartyType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="BuyerAssignedAccountantTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="SellerAssignedAccountantTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="BuyerTaxRepresentativeTradeParty" type="ram:TradePartyType" minOccurs="0"/>

        // <xsd:element name="SellerTaxRepresentativeTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        addSellerTaxRepresentativeParty(invoice, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="ProductEndUserTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="ApplicableTradeDeliveryTerms" type="ram:TradeDeliveryTermsType" minOccurs="0"/>

        // <xsd:element name="SellerOrderReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        addSellerOrderReferenceDocument(invoice, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="BuyerOrderReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        addBuyerOrderReferenceDocument(invoice, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="QuotationReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="OrderResponseReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>

        // <xsd:element name="ContractReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        addContractReferenceDocument(invoice, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="DemandForecastReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="SupplyInstructionReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="PromotionalDealReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>
        // <xsd:element name="PriceListReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0"/>

        // <xsd:element name="AdditionalReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>
        newAdditionalReferencedDocument(invoice, errors, callingLocation, rootElement, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="RequisitionerReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>
        // <xsd:element name="BuyerAgentTradeParty" type="ram:TradePartyType" minOccurs="0"/>
        // <xsd:element name="PurchaseConditionsReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>

        // <xsd:element name="SpecifiedProcuringProject" type="ram:ProcuringProjectType" minOccurs="0"/>
        addSpecifiedProcuringProject(invoice, ramNs, applicableHeaderTradeAgreement);

        // <xsd:element name="UltimateCustomerOrderReferencedDocument" type="ram:ReferencedDocumentType" minOccurs="0" maxOccurs="unbounded"/>
        // </xsd:sequence>
        // </xsd:complexType>

    }

    private void addContractReferenceDocument(BG0000Invoice invoice, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBT0012ContractReference().isEmpty()) {
            BT0012ContractReference bt0012 = invoice.getBT0012ContractReference(0);
            Element contractReferencedDocument = new Element("ContractReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0012.getValue());
            contractReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(contractReferencedDocument);
        }
    }

    private void addBuyerOrderReferenceDocument(BG0000Invoice invoice, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBT0013PurchaseOrderReference().isEmpty()) {
            BT0013PurchaseOrderReference bt0013 = invoice.getBT0013PurchaseOrderReference(0);
            Element buyerOrderReferencedDocument = new Element("BuyerOrderReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0013.getValue());
            buyerOrderReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(buyerOrderReferencedDocument);
        }
    }

    private void addSellerOrderReferenceDocument(BG0000Invoice invoice, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBT0014SalesOrderReference().isEmpty()) {
            BT0014SalesOrderReference bt0014 = invoice.getBT0014SalesOrderReference(0);
            Element sellerOrderReferencedDocument = new Element("SellerOrderReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0014.getValue());
            sellerOrderReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(sellerOrderReferencedDocument);
        }
    }

    private void addSellerTaxRepresentativeParty(BG0000Invoice invoice, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBG0011SellerTaxRepresentativeParty().isEmpty()) {
            final BG0011SellerTaxRepresentativeParty bg0011 = invoice.getBG0011SellerTaxRepresentativeParty(0);

            final Element sellerTaxRepresentativeTradeParty = new Element("SellerTaxRepresentativeTradeParty", ramNs);
            applicableHeaderTradeAgreement.addContent(sellerTaxRepresentativeTradeParty);


            if (!bg0011.getBT0062SellerTaxRepresentativeName().isEmpty()) {
                BT0062SellerTaxRepresentativeName bt0062 = bg0011.getBT0062SellerTaxRepresentativeName(0);
                Element name = new Element("Name", ramNs);
                name.setText(bt0062.getValue());
                sellerTaxRepresentativeTradeParty.addContent(name);
            }

            if (!bg0011.getBG0012SellerTaxRepresentativePostalAddress().isEmpty()) {
                BG0012SellerTaxRepresentativePostalAddress bg0012 = bg0011.getBG0012SellerTaxRepresentativePostalAddress(0);

                Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                sellerTaxRepresentativeTradeParty.addContent(postalTradeAddress);


                // <xsd:element name="ID" type="udt:IDType" minOccurs="0"/>
                // <xsd:element name="PostcodeCode" type="udt:CodeType" minOccurs="0"/>
                if (!bg0012.getBT0067TaxRepresentativePostCode().isEmpty()) {
                    BT0067TaxRepresentativePostCode bt0067 = bg0012.getBT0067TaxRepresentativePostCode(0);
                    Element postcodeCode = new Element("PostcodeCode", ramNs);
                    postcodeCode.setText(bt0067.getValue());
                    postalTradeAddress.addContent(postcodeCode);
                }

                // <xsd:element name="PostOfficeBox" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="BuildingName" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="LineOne" type="udt:TextType" minOccurs="0"/>
                if (!bg0012.getBT0064TaxRepresentativeAddressLine1().isEmpty()) {
                    BT0064TaxRepresentativeAddressLine1 bt0064 = bg0012.getBT0064TaxRepresentativeAddressLine1(0);
                    Element lineOne = new Element("LineOne", ramNs);
                    lineOne.setText(bt0064.getValue());
                    postalTradeAddress.addContent(lineOne);
                }

                // <xsd:element name="LineTwo" type="udt:TextType" minOccurs="0"/>
                if (!bg0012.getBT0065TaxRepresentativeAddressLine2().isEmpty()) {
                    BT0065TaxRepresentativeAddressLine2 bt0065 = bg0012.getBT0065TaxRepresentativeAddressLine2(0);
                    Element lineTwo = new Element("LineTwo", ramNs);
                    lineTwo.setText(bt0065.getValue());
                    postalTradeAddress.addContent(lineTwo);
                }

                // <xsd:element name="LineThree" type="udt:TextType" minOccurs="0"/>
                if (!bg0012.getBT0164TaxRepresentativeAddressLine3().isEmpty()) {
                    BT0164TaxRepresentativeAddressLine3 bt0164 = bg0012.getBT0164TaxRepresentativeAddressLine3(0);
                    Element lineThree = new Element("LineThree", ramNs);
                    lineThree.setText(bt0164.getValue());
                    postalTradeAddress.addContent(lineThree);
                }

                // <xsd:element name="LineFour" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="LineFive" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="StreetName" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="CityName" type="udt:TextType" minOccurs="0"/>
                if (!bg0012.getBT0066TaxRepresentativeCity().isEmpty()) {
                    BT0066TaxRepresentativeCity bt0066 = bg0012.getBT0066TaxRepresentativeCity(0);
                    Element cityName = new Element("CityName", ramNs);
                    cityName.setText(bt0066.getValue());
                    postalTradeAddress.addContent(cityName);
                }

                // <xsd:element name="CitySubDivisionName" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="CountryID" type="qdt:CountryIDType" minOccurs="0"/>
                if (!bg0012.getBT0069TaxRepresentativeCountryCode().isEmpty()) {
                    BT0069TaxRepresentativeCountryCode bt0069 = bg0012.getBT0069TaxRepresentativeCountryCode(0);
                    Element countryID = new Element("CountryID", ramNs);
                    countryID.setText(bt0069.getValue().getIso2charCode());
                    postalTradeAddress.addContent(countryID);
                }

                // <xsd:element name="CountryName" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>
                // <xsd:element name="CountrySubDivisionID" type="udt:IDType" minOccurs="0"/>
                // <xsd:element name="CountrySubDivisionName" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>
                if (!bg0012.getBT0068TaxRepresentativeCountrySubdivision().isEmpty()) {
                    BT0068TaxRepresentativeCountrySubdivision bt0068 = bg0012.getBT0068TaxRepresentativeCountrySubdivision(0);
                    Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                    countrySubDivisionName.setText(bt0068.getValue());
                    postalTradeAddress.addContent(countrySubDivisionName);
                }
                // <xsd:element name="AttentionOf" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="CareOf" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="BuildingNumber" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="DepartmentName" type="udt:TextType" minOccurs="0"/>
                // <xsd:element name="AdditionalStreetName" type="udt:TextType" minOccurs="0"/>


                if (!bg0011.getBT0063SellerTaxRepresentativeVatIdentifier().isEmpty()) {
                    Identifier bt0063 = bg0011.getBT0063SellerTaxRepresentativeVatIdentifier(0).getValue();
                    Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                    Element id = new Element("ID", ramNs);
                    String schema = bt0063.getIdentificationSchema();
                    String identifier = bt0063.getIdentifier();
                    if (schema != null) {
                        id.setText(String.format("%s %s", schema, identifier));
                    } else {
                        id.setText(identifier);
                    }
                    id.setAttribute("schemeID", "VA");
                    specifiedTaxRegistration.addContent(id);
                    sellerTaxRepresentativeTradeParty.addContent(specifiedTaxRegistration);
                }
            }
        }
    }

    private void addBuyerTradeParty(BG0000Invoice invoice, List<Namespace> namespacesInScope, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer bg0007 = invoice.getBG0007Buyer(0);
            Element buyerTradeParty = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "BuyerTradeParty");
            if (buyerTradeParty == null) {
                buyerTradeParty = new Element("BuyerTradeParty", ramNs);
                applicableHeaderTradeAgreement.addContent(buyerTradeParty);
            }

            if (!bg0007.getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0046 = bg0007.getBT0046BuyerIdentifierAndSchemeIdentifier(0).getValue();
                Element id = new Element("ID", ramNs); // maybe GlobalID ?
                id.setText(bt0046.getIdentifier());
                if (bt0046.getIdentificationSchema() != null) {
                    id.setAttribute("schemeID", bt0046.getIdentificationSchema());
                } else {
                    id.setAttribute("schemeID", "");
                }
                buyerTradeParty.addContent(id);
            }

            if (!bg0007.getBT0044BuyerName().isEmpty()) {
                BT0044BuyerName bt0044 = bg0007.getBT0044BuyerName(0);
                Element name = new Element("Name", ramNs);
                name.setText(bt0044.getValue());
                buyerTradeParty.addContent(name);
            } else if (!bg0007.getBG0009BuyerContact().isEmpty()) {
                BG0009BuyerContact bg0009 = bg0007.getBG0009BuyerContact(0);
                if (!bg0009.getBT0056BuyerContactPoint().isEmpty()) {
                    BT0056BuyerContactPoint bt0056 = bg0009.getBT0056BuyerContactPoint(0);
                    Element name = new Element("Name", ramNs);
                    name.setText(bt0056.getValue());
                    buyerTradeParty.addContent(name);
                }
            }

            Element specifiedLegalOrganization = new Element("SpecifiedLegalOrganization", ramNs);
            buyerTradeParty.addContent(specifiedLegalOrganization);

            if (!bg0007.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0047 = bg0007.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue();
                Element id = new Element("ID", ramNs);
                id.setText(bt0047.getIdentifier());
                if (bt0047.getIdentificationSchema() != null) {
                    id.setAttribute("schemeID", bt0047.getIdentificationSchema());
                }
                specifiedLegalOrganization.addContent(id);
            }

            if (!bg0007.getBT0045BuyerTradingName().isEmpty()) {
                BT0045BuyerTradingName bt0045 = bg0007.getBT0045BuyerTradingName(0);
                Element tradingBusinessName = new Element("TradingBusinessName", ramNs);
                tradingBusinessName.setText(bt0045.getValue());
                specifiedLegalOrganization.addContent(tradingBusinessName);
            }

            if (!bg0007.getBG0009BuyerContact().isEmpty()) {
                BG0009BuyerContact bg0009 = bg0007.getBG0009BuyerContact(0);
                Element definedTradeContact = new Element("DefinedTradeContact", ramNs);
                buyerTradeParty.addContent(definedTradeContact);

                if (!bg0009.getBT0056BuyerContactPoint().isEmpty()) {
                    BT0056BuyerContactPoint bt0056 = bg0009.getBT0056BuyerContactPoint(0);
                    Element personName = new Element("PersonName", ramNs);
                    personName.setText(bt0056.getValue());
                    definedTradeContact.addContent(personName);
                }

                if (!bg0009.getBT0057BuyerContactTelephoneNumber().isEmpty()) {
                    BT0057BuyerContactTelephoneNumber bt0057 = bg0009.getBT0057BuyerContactTelephoneNumber(0);
                    Element telephoneUniversalCommunication = new Element("TelephoneUniversalCommunication", ramNs);
                    Element completeNumber = new Element("CompleteNumber", ramNs);
                    completeNumber.setText(bt0057.getValue());
                    telephoneUniversalCommunication.addContent(completeNumber);
                    definedTradeContact.addContent(telephoneUniversalCommunication);
                }

                if (!bg0009.getBT0058BuyerContactEmailAddress().isEmpty()) {
                    BT0058BuyerContactEmailAddress bt0058 = bg0009.getBT0058BuyerContactEmailAddress(0);
                    Element emailURIUniversalCommunication = new Element("EmailURIUniversalCommunication", ramNs);
                    Element uriid = new Element("URIID", ramNs);
                    uriid.setText(bt0058.getValue());
                    emailURIUniversalCommunication.addContent(uriid);
                    definedTradeContact.addContent(emailURIUniversalCommunication);
                }
            }

            if (!bg0007.getBG0008BuyerPostalAddress().isEmpty()) {
                BG0008BuyerPostalAddress bg0008 = bg0007.getBG0008BuyerPostalAddress(0);
                Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                buyerTradeParty.addContent(postalTradeAddress);

                if (!bg0008.getBT0053BuyerPostCode().isEmpty()) {
                    BT0053BuyerPostCode bt0053 = bg0008.getBT0053BuyerPostCode(0);
                    Element postcodeCode = new Element("PostcodeCode", ramNs);
                    postcodeCode.setText(bt0053.getValue());
                    postalTradeAddress.addContent(postcodeCode);
                }

                if (!bg0008.getBT0050BuyerAddressLine1().isEmpty()) {
                    BT0050BuyerAddressLine1 bt0050 = bg0008.getBT0050BuyerAddressLine1(0);
                    Element lineOne = new Element("LineOne", ramNs);
                    lineOne.setText(bt0050.getValue());
                    postalTradeAddress.addContent(lineOne);
                }

                if (!bg0008.getBT0051BuyerAddressLine2().isEmpty()) {
                    BT0051BuyerAddressLine2 bt0051 = bg0008.getBT0051BuyerAddressLine2(0);
                    Element lineTwo = new Element("LineTwo", ramNs);
                    lineTwo.setText(bt0051.getValue());
                    postalTradeAddress.addContent(lineTwo);
                }

                if (!bg0008.getBT0163BuyerAddressLine3().isEmpty()) {
                    BT0163BuyerAddressLine3 bt0163 = bg0008.getBT0163BuyerAddressLine3(0);
                    Element lineThree = new Element("LineThree", ramNs);
                    lineThree.setText(bt0163.getValue());
                    postalTradeAddress.addContent(lineThree);
                }

                if (!bg0008.getBT0052BuyerCity().isEmpty()) {
                    BT0052BuyerCity bt0052 = bg0008.getBT0052BuyerCity(0);
                    Element cityName = new Element("CityName", ramNs);
                    cityName.setText(bt0052.getValue());
                    postalTradeAddress.addContent(cityName);
                }

                if (!bg0008.getBT0055BuyerCountryCode().isEmpty()) {
                    BT0055BuyerCountryCode bt0055 = bg0008.getBT0055BuyerCountryCode(0);
                    Element countryID = new Element("CountryID", ramNs);
                    countryID.setText(bt0055.getValue().getIso2charCode());
                    postalTradeAddress.addContent(countryID);
                }

                if (!bg0008.getBT0054BuyerCountrySubdivision().isEmpty()) {
                    BT0054BuyerCountrySubdivision bt0054 = bg0008.getBT0054BuyerCountrySubdivision(0);
                    Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                    countrySubDivisionName.setText(bt0054.getValue());
                    postalTradeAddress.addContent(countrySubDivisionName);
                }
            }

            if (!bg0007.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                Identifier bt0049 = bg0007.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0).getValue();
                Element uriUniversalCommunication = new Element("URIUniversalCommunication", ramNs);
                Element uriid = new Element("URIID", ramNs);
                uriid.setText(bt0049.getIdentifier());
                if (bt0049.getIdentificationSchema() != null) {
                    uriid.setAttribute("schemeID", bt0049.getIdentificationSchema());
                }
                uriUniversalCommunication.addContent(uriid);
                buyerTradeParty.addContent(uriUniversalCommunication);
            }

            if (!bg0007.getBT0048BuyerVatIdentifier().isEmpty()) {
                Identifier bt0048 = bg0007.getBT0048BuyerVatIdentifier(0).getValue();
                Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                Element id = new Element("ID", ramNs);
                String identifier = bt0048.getIdentifier();
                String schema = bt0048.getIdentificationSchema();
                if (schema != null) {
                    id.setText(String.format("%s %s", schema, identifier));
                } else {
                    id.setText(identifier);
                }
                id.setAttribute("schemeID", "VA");

                specifiedTaxRegistration.addContent(id);
                buyerTradeParty.addContent(specifiedTaxRegistration);
            }

        }
    }

    private void addBuyerReference(BG0000Invoice invoice, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBT0010BuyerReference().isEmpty()) {
            BT0010BuyerReference bt0010 = invoice.getBT0010BuyerReference(0);
            Element buyerReference = new Element("BuyerReference", ramNs);
            buyerReference.setText(bt0010.getValue());
            applicableHeaderTradeAgreement.addContent(buyerReference);
        }
    }

    private void addSpecifiedProcuringProject(BG0000Invoice invoice, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBT0011ProjectReference().isEmpty()) {
            Element specifiedProcuringProject = new Element("SpecifiedProcuringProject", ramNs);

            specifiedProcuringProject
                    .addContent(new Element("ID", ramNs)
                            .setText( invoice.getBT0011ProjectReference(0).getValue() ))
                    .addContent(new Element("Name", ramNs)
                            .setText( "Name of " + invoice.getBT0011ProjectReference(0).getValue() ));

            applicableHeaderTradeAgreement.addContent(specifiedProcuringProject);
        }
    }

    private void addSellerTradeParty(BG0000Invoice invoice, List<Namespace> namespacesInScope, Namespace ramNs, Element applicableHeaderTradeAgreement) {
        if (!invoice.getBG0004Seller().isEmpty()) {

            Element sellerTradeParty = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "SellerTradeParty");
            if (sellerTradeParty == null) {
                sellerTradeParty = new Element("SellerTradeParty", ramNs);
                applicableHeaderTradeAgreement.addContent(sellerTradeParty);
            }

            BG0004Seller bg0004 = invoice.getBG0004Seller(0);
            if (!bg0004.getBT0029SellerIdentifierAndSchemeIdentifier().isEmpty()) {
                for (BT0029SellerIdentifierAndSchemeIdentifier bt0029 : bg0004.getBT0029SellerIdentifierAndSchemeIdentifier()) {
                    Identifier identifier = bt0029.getValue();
                    Element id = new Element("ID", ramNs);
                    id.setText(identifier.getIdentifier());
                    if (identifier.getIdentificationSchema() != null) {
                        id.setAttribute("schemeID", identifier.getIdentificationSchema());
                    }
                    sellerTradeParty.addContent(id);
                }

            }

            if (!bg0004.getBT0027SellerName().isEmpty()) {
                BT0027SellerName bt0027 = bg0004.getBT0027SellerName(0);
                Element name = new Element("Name", ramNs);
                name.setText(bt0027.getValue());
                sellerTradeParty.addContent(name);
            }

            if (!bg0004.getBT0033SellerAdditionalLegalInformation().isEmpty()) {
                BT0033SellerAdditionalLegalInformation bt0033 = bg0004.getBT0033SellerAdditionalLegalInformation(0);
                Element description = new Element("Description", ramNs);
                description.setText(bt0033.getValue());
                sellerTradeParty.addContent(description);
            }

            Element specifiedLegalOrganization = new Element("SpecifiedLegalOrganization", ramNs);

            if (!bg0004.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0030 = bg0004.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue();
                Element id = new Element("ID", ramNs);
                id.setText(bt0030.getIdentifier());
                if (bt0030.getIdentificationSchema() != null) {
                    id.setAttribute("schemeID", bt0030.getIdentificationSchema());
                }
                specifiedLegalOrganization.addContent(id);
            }

            if (!bg0004.getBT0028SellerTradingName().isEmpty()) {
                BT0028SellerTradingName bt0028 = bg0004.getBT0028SellerTradingName(0);
                Element tradingBusinessName = new Element("TradingBusinessName", ramNs);
                tradingBusinessName.setText(bt0028.getValue());
                specifiedLegalOrganization.addContent(tradingBusinessName);
            }

            if (!specifiedLegalOrganization.getChildren().isEmpty()) {
                sellerTradeParty.addContent(specifiedLegalOrganization);
            }


            if (!bg0004.getBG0006SellerContact().isEmpty()) {
                BG0006SellerContact bg0006 = bg0004.getBG0006SellerContact(0);
                Element definedTradeContact = new Element("DefinedTradeContact", ramNs);
                sellerTradeParty.addContent(definedTradeContact);

                if (!bg0006.getBT0041SellerContactPoint().isEmpty()) {
                    BT0041SellerContactPoint bt0041 = bg0006.getBT0041SellerContactPoint(0);
                    Element personName = new Element("PersonName", ramNs);
                    personName.setText(bt0041.getValue());
                    definedTradeContact.addContent(personName);
                }

                if (!bg0006.getBT0042SellerContactTelephoneNumber().isEmpty()) {
                    BT0042SellerContactTelephoneNumber bt0042 = bg0006.getBT0042SellerContactTelephoneNumber(0);
                    Element telephoneUniversalCommunication = new Element("TelephoneUniversalCommunication", ramNs);
                    Element completeNumber = new Element("CompleteNumber", ramNs);
                    completeNumber.setText(bt0042.getValue());
                    telephoneUniversalCommunication.addContent(completeNumber);
                    definedTradeContact.addContent(telephoneUniversalCommunication);
                }

                if (!bg0006.getBT0043SellerContactEmailAddress().isEmpty()) {
                    BT0043SellerContactEmailAddress bt0043 = bg0006.getBT0043SellerContactEmailAddress(0);
                    Element emailURIUniversalCommunication = new Element("EmailURIUniversalCommunication", ramNs);
                    Element uriid = new Element("URIID", ramNs);
                    uriid.setText(bt0043.getValue());
                    emailURIUniversalCommunication.addContent(uriid);
                    definedTradeContact.addContent(emailURIUniversalCommunication);
                }
            }

            if (!bg0004.getBG0005SellerPostalAddress().isEmpty()) {
                BG0005SellerPostalAddress bg0005 = bg0004.getBG0005SellerPostalAddress(0);
                Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                sellerTradeParty.addContent(postalTradeAddress);

                if (!bg0005.getBT0038SellerPostCode().isEmpty()) {
                    BT0038SellerPostCode bt0038 = bg0005.getBT0038SellerPostCode(0);
                    Element postcodeCode = new Element("PostcodeCode", ramNs);
                    postcodeCode.setText(bt0038.getValue());
                    postalTradeAddress.addContent(postcodeCode);
                }

                if (!bg0005.getBT0035SellerAddressLine1().isEmpty()) {
                    BT0035SellerAddressLine1 bt0035 = bg0005.getBT0035SellerAddressLine1(0);
                    Element lineOne = new Element("LineOne", ramNs);
                    lineOne.setText(bt0035.getValue());
                    postalTradeAddress.addContent(lineOne);
                }

                if (!bg0005.getBT0036SellerAddressLine2().isEmpty()) {
                    BT0036SellerAddressLine2 bt0036 = bg0005.getBT0036SellerAddressLine2(0);
                    Element lineTwo = new Element("LineTwo", ramNs);
                    lineTwo.setText(bt0036.getValue());
                    postalTradeAddress.addContent(lineTwo);
                }

                if (!bg0005.getBT0162SellerAddressLine3().isEmpty()) {
                    BT0162SellerAddressLine3 bt0162 = bg0005.getBT0162SellerAddressLine3(0);
                    Element lineThree = new Element("LineThree", ramNs);
                    lineThree.setText(bt0162.getValue());
                    postalTradeAddress.addContent(lineThree);
                }

                if (!bg0005.getBT0037SellerCity().isEmpty()) {
                    BT0037SellerCity bt0037 = bg0005.getBT0037SellerCity(0);
                    Element cityName = new Element("CityName", ramNs);
                    cityName.setText(bt0037.getValue());
                    postalTradeAddress.addContent(cityName);
                }

                if (!bg0005.getBT0040SellerCountryCode().isEmpty()) {
                    BT0040SellerCountryCode bt0040 = bg0005.getBT0040SellerCountryCode(0);
                    Element countryID = new Element("CountryID", ramNs);
                    countryID.setText(bt0040.getValue().getIso2charCode());
                    postalTradeAddress.addContent(countryID);
                }

                if (!bg0005.getBT0039SellerCountrySubdivision().isEmpty()) {
                    BT0039SellerCountrySubdivision bt0039 = bg0005.getBT0039SellerCountrySubdivision(0);
                    Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                    countrySubDivisionName.setText(bt0039.getValue());
                    postalTradeAddress.addContent(countrySubDivisionName);
                }
            }


            if (!bg0004.getBT0034SellerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                Identifier bt0034 = bg0004.getBT0034SellerElectronicAddressAndSchemeIdentifier(0).getValue();
                Element uriUniversalCommunication = new Element("URIUniversalCommunication", ramNs);
                Element uriid = new Element("URIID", ramNs);
                uriid.setText(bt0034.getIdentifier());
                if (bt0034.getIdentificationSchema() != null) {
                    uriid.setAttribute("schemeID", bt0034.getIdentificationSchema());
                }
                uriUniversalCommunication.addContent(uriid);
                sellerTradeParty.addContent(uriUniversalCommunication);
            }

            for (BT0031SellerVatIdentifier bt0031 : bg0004.getBT0031SellerVatIdentifier()) {
                Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                Element id = new Element("ID", ramNs);
                id.setText(bt0031.getValue());
                id.setAttribute("schemeID", "VA");
                specifiedTaxRegistration.addContent(id);
                sellerTradeParty.addContent(specifiedTaxRegistration);
            }

            for (BT0032SellerTaxRegistrationIdentifier bt0032 : bg0004.getBT0032SellerTaxRegistrationIdentifier()) {
                Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", ramNs);
                Element id = new Element("ID", ramNs);
                id.setText(bt0032.getValue());
                id.setAttribute("schemeID", "FC");
                specifiedTaxRegistration.addContent(id);
                sellerTradeParty.addContent(specifiedTaxRegistration);
            }

        }
    }

    private enum Source {
        BT17, BT18, BT122
    }

    private void newAdditionalReferencedDocument(BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation, Element rootElement, Namespace ramNs, Element applicableHeaderTradeAgreement) {

        for (BG0024AdditionalSupportingDocuments bg0024 : invoice.getBG0024AdditionalSupportingDocuments()) {

            Source source = null;

            Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", rootElement.getNamespace("ram"));

            // <xsd:complexType name="ReferencedDocumentType">

            // <xsd:element name="IssuerAssignedID" type="udt:IDType" minOccurs="0"/>
            if (!bg0024.getBT0122SupportingDocumentReference().isEmpty()) {
                BT0122SupportingDocumentReference bt0122 = bg0024.getBT0122SupportingDocumentReference(0);
                Element issuerAssignedID = new Element("IssuerAssignedID", rootElement.getNamespace("ram"));
                issuerAssignedID.setText(bt0122.getValue());
                additionalReferencedDocument.addContent(issuerAssignedID);

            }else if (!invoice.getBT0017TenderOrLotReference().isEmpty()) {
                final BT0017TenderOrLotReference bt0017 = invoice.getBT0017TenderOrLotReference(0);
                Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
                issuerAssignedID.setText(bt0017.getValue());
                additionalReferencedDocument.addContent(issuerAssignedID);
            }

            // <xsd:element name="URIID" type="udt:IDType" minOccurs="0"/>
            if (!bg0024.getBT0124ExternalDocumentLocation().isEmpty()) {
                BT0124ExternalDocumentLocation bt0124 = bg0024.getBT0124ExternalDocumentLocation(0);
                Element uriid = new Element("URIID", rootElement.getNamespace("ram"));
                uriid.setText(bt0124.getValue());
                additionalReferencedDocument.addContent(uriid);
            }

            // <xsd:element name="StatusCode" type="qdt:DocumentStatusCodeType" minOccurs="0"/>
            // <xsd:element name="CopyIndicator" type="udt:IndicatorType" minOccurs="0"/>
            // <xsd:element name="LineID" type="udt:IDType" minOccurs="0"/>

            // <xsd:element name="TypeCode" type="qdt:DocumentCodeType" minOccurs="0"/>
            Element typeCode = null;
            if (    invoice.hasBT0017TenderOrLotReference() &&
                    invoice.getBT0017TenderOrLotReference(0).getValue().equals(bg0024.getBT0122SupportingDocumentReference(0).getValue()) ) {
                typeCode = new Element("TypeCode", ramNs);
                typeCode.setText("50");
                source = Source.BT17;
            } else if (!bg0024.getBT0122SupportingDocumentReference().isEmpty()) {
                typeCode = new Element("TypeCode", ramNs);
                typeCode.setText("916");
                source = Source.BT122;
            } else if (!invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                final BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 = invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier(0);
                typeCode = new Element("TypeCode", ramNs);
                typeCode.setText("130");
                source = Source.BT18;
            }
            if(typeCode!=null) additionalReferencedDocument.addContent(typeCode);


            // <xsd:element name="GlobalID" type="udt:IDType" minOccurs="0"/>
            // <xsd:element name="RevisionID" type="udt:IDType" minOccurs="0"/>

            // <xsd:element name="Name" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>
            for (BT0123SupportingDocumentDescription bt0123 : bg0024.getBT0123SupportingDocumentDescription()) {
                Element name = new Element("Name", rootElement.getNamespace("ram"));
                name.setText(bt0123.getValue());
                additionalReferencedDocument.addContent(name);
            }

            // <xsd:element name="AttachmentBinaryObject" type="udt:BinaryObjectType" minOccurs="0" maxOccurs="unbounded"/>
            for (BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename filename : bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename()) {
                final FileReference bt0125 = filename.getValue();
                Element attachmentBinaryObject = new Element("AttachmentBinaryObject", rootElement.getNamespace("ram"));
                attachmentBinaryObject.setAttribute("mimeCode", bt0125.getMimeType().toString());
                attachmentBinaryObject.setAttribute("filename", bt0125.getFileName());
                try {
                    String content = FileUtils.fileRead(new File(bt0125.getFilePath()));
                    attachmentBinaryObject.setText(content);
                    additionalReferencedDocument.addContent(attachmentBinaryObject);
                } catch (IOException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            String.format("Cannot read attachment file %s!", bt0125.getFileName()),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            // <xsd:element name="Information" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>

            // <xsd:element name="ReferenceTypeCode" type="qdt:ReferenceCodeType" minOccurs="0"/>
            if (!invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().isEmpty() && source==Source.BT18) {
                final BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 = invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier(0);

                String identificationSchema = bt0018.getValue().getIdentificationSchema();
                if (identificationSchema != null) {
                    Element referenceTypeCode = new Element("ReferenceTypeCode", ramNs);
                    referenceTypeCode.setText(identificationSchema);
                    additionalReferencedDocument.addContent(referenceTypeCode);
                }
            }

            // <xsd:element name="SectionName" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>
            // <xsd:element name="PreviousRevisionID" type="udt:IDType" minOccurs="0" maxOccurs="unbounded"/>
            // <xsd:element name="FormattedIssueDateTime" type="qdt:FormattedDateTimeType" minOccurs="0"/>
            // <xsd:element name="EffectiveSpecifiedPeriod" type="ram:SpecifiedPeriodType" minOccurs="0"/>
            // <xsd:element name="IssuerTradeParty" type="ram:TradePartyType" minOccurs="0"/>
            // <xsd:element name="AttachedSpecifiedBinaryFile" type="ram:SpecifiedBinaryFileType" minOccurs="0" maxOccurs="unbounded"/>

            applicableHeaderTradeAgreement.addContent(additionalReferencedDocument);
        }
    }

}
