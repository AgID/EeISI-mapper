package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;

import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

/**
 * The Deliver To Location Converter
 */
public class DeliverToLocationConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

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

        Element applicableHeaderTradeDelivery = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeDelivery");
        if (applicableHeaderTradeDelivery == null) {
            applicableHeaderTradeDelivery = new Element("ApplicableHeaderTradeDelivery", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeDelivery);
        }

        if (!invoice.getBG0013DeliveryInformation().isEmpty()) {
            BG0013DeliveryInformation bg0013 = invoice.getBG0013DeliveryInformation(0);

            Element shipToTradeParty = findNamespaceChild(applicableHeaderTradeDelivery, namespacesInScope, "ShipToTradeParty");
            if (shipToTradeParty == null) {
                shipToTradeParty = new Element("ShipToTradeParty", ramNs);
                applicableHeaderTradeDelivery.addContent(shipToTradeParty);
            }

            Identifier bt71 = evalExpression(() -> bg0013.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier(0).getValue());
            if(bt71!=null) {
                if(bt71.getIdentifier()!=null) {
                    Element id;
                    if (bt71.getIdentificationSchema()!=null) {
                        id = new Element("GlobalID", ramNs);
                        id.setAttribute("schemeID", bt71.getIdentificationSchema());
                    } else {
                        id = new Element("ID", ramNs);
                    }
                    id.setText(bt71.getIdentifier());
                    shipToTradeParty.addContent(id);
                }
            }

            if (!bg0013.getBT0070DeliverToPartyName().isEmpty()) {
                BT0070DeliverToPartyName bt0070 = bg0013.getBT0070DeliverToPartyName(0);
                Element name = new Element("Name", ramNs);
                name.setText(bt0070.getValue());
                shipToTradeParty.addContent(name);
            }

            if (!bg0013.getBG0015DeliverToAddress().isEmpty()) {
                BG0015DeliverToAddress bg0015 = bg0013.getBG0015DeliverToAddress(0);

                Element postalTradeAddress = new Element("PostalTradeAddress", ramNs);
                shipToTradeParty.addContent(postalTradeAddress);

                if (!bg0015.getBT0078DeliverToPostCode().isEmpty()) {
                    BT0078DeliverToPostCode bt0078 = bg0015.getBT0078DeliverToPostCode(0);
                    Element postcodeCode = new Element("PostcodeCode", ramNs);
                    postcodeCode.setText(bt0078.getValue());
                    postalTradeAddress.addContent(postcodeCode);
                }

                if (!bg0015.getBT0075DeliverToAddressLine1().isEmpty()) {
                    BT0075DeliverToAddressLine1 bt0075 = bg0015.getBT0075DeliverToAddressLine1(0);
                    Element lineOne = new Element("LineOne", ramNs);
                    lineOne.setText(bt0075.getValue());
                    postalTradeAddress.addContent(lineOne);
                }

                if (!bg0015.getBT0076DeliverToAddressLine2().isEmpty()) {
                    BT0076DeliverToAddressLine2 bt0076 = bg0015.getBT0076DeliverToAddressLine2(0);
                    Element lineTwo = new Element("LineTwo", ramNs);
                    lineTwo.setText(bt0076.getValue());
                    postalTradeAddress.addContent(lineTwo);
                }

                if (!bg0015.getBT0165DeliverToAddressLine3().isEmpty()) {
                    BT0165DeliverToAddressLine3 bt0165 = bg0015.getBT0165DeliverToAddressLine3(0);
                    Element lineThree = new Element("LineThree", ramNs);
                    lineThree.setText(bt0165.getValue());
                    postalTradeAddress.addContent(lineThree);
                }

                if (!bg0015.getBT0077DeliverToCity().isEmpty()) {
                    BT0077DeliverToCity bt0077 = bg0015.getBT0077DeliverToCity(0);
                    Element cityName = new Element("CityName", ramNs);
                    cityName.setText(bt0077.getValue());
                    postalTradeAddress.addContent(cityName);
                }

                if (!bg0015.getBT0080DeliverToCountryCode().isEmpty()) {
                    BT0080DeliverToCountryCode bt0080 = bg0015.getBT0080DeliverToCountryCode(0);
                    Element countryID = new Element("CountryID", ramNs);
                    countryID.setText(bt0080.getValue().getIso2charCode());
                    postalTradeAddress.addContent(countryID);
                }

                if (!bg0015.getBT0079DeliverToCountrySubdivision().isEmpty()) {
                    BT0079DeliverToCountrySubdivision bt0079 = bg0015.getBT0079DeliverToCountrySubdivision(0);
                    Element countrySubDivisionName = new Element("CountrySubDivisionName", ramNs);
                    countrySubDivisionName.setText(bt0079.getValue());
                    postalTradeAddress.addContent(countrySubDivisionName);
                }

            }

            if (!bg0013.getBT0072ActualDeliveryDate().isEmpty()) {
                LocalDate bt0072 = bg0013.getBT0072ActualDeliveryDate(0).getValue();
                Element dateTimeString = new Element("DateTimeString", udtNs);
                dateTimeString.setAttribute("format", "102");
                Element occurrenceDateTime = new Element("OccurrenceDateTime", ramNs);
                Element actualDeliverySupplyChainEvent = new Element("ActualDeliverySupplyChainEvent", ramNs);
                try {
                    dateTimeString.setText(dateStrConverter.convert(bt0072));
                    occurrenceDateTime.addContent(dateTimeString);
                    actualDeliverySupplyChainEvent.addContent(occurrenceDateTime);
                    applicableHeaderTradeDelivery.addContent(actualDeliverySupplyChainEvent);
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

            if (!invoice.getBT0016DespatchAdviceReference().isEmpty()) {
                BT0016DespatchAdviceReference bt0016 = invoice.getBT0016DespatchAdviceReference(0);
                Element despatchAdviceReferencedDocument = new Element("DespatchAdviceReferencedDocument", ramNs);
                Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
                issuerAssignedID.setText(bt0016.getValue());
                despatchAdviceReferencedDocument.addContent(issuerAssignedID);
                applicableHeaderTradeDelivery.addContent(despatchAdviceReferencedDocument);
            }

            if (!invoice.getBT0015ReceivingAdviceReference().isEmpty()) {
                BT0015ReceivingAdviceReference bt0015 = invoice.getBT0015ReceivingAdviceReference(0);
                Element receivingAdviceReferencedDocument = new Element("ReceivingAdviceReferencedDocument", ramNs);
                Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
                issuerAssignedID.setText(bt0015.getValue());
                receivingAdviceReferencedDocument.addContent(issuerAssignedID);
                applicableHeaderTradeDelivery.addContent(receivingAdviceReferencedDocument);
            }

        }
    }
}
