package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0013DeliveryInformation;
import it.infocert.eigor.model.core.model.BG0015DeliverToAddress;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;

import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class DeliveryConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document target, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {


        // Generates element at /*[name()='Invoice']/cac:PayeeParty
        final Element root = target.getRootElement();

        BG0013DeliveryInformation bg13 = evalExpression(() -> invoice.getBG0013DeliveryInformation(0));
        BG0015DeliverToAddress bg15 = evalExpression(() -> bg13.getBG0015DeliverToAddress(0));

        if(bg13 == null) return;

        Element deliveryTag = new Element("Delivery");

        // <cbc:ActualDeliveryDate>2017-10-15</cbc:ActualDeliveryDate><!--BT-72-->
        LocalDate bt72 = evalExpression(() -> bg13.getBT0072ActualDeliveryDate(0).getValue());
        if(bt72!=null) {
            deliveryTag.addContent(
                new Element("ActualDeliveryDate")
                        .addContent(bt72.toString())
            );
        }

        // <cac:DeliveryLocation> ... </cac:DeliveryLocation>
        Element deliveryLocationTag = mapDeliveryLocationOrNull(bg13, bg15);
        if(deliveryLocationTag!=null) {
            deliveryTag.addContent(deliveryLocationTag);
        }

        if(!deliveryTag.getChildren().isEmpty()) {
            root.addContent( deliveryTag );
        }

    }

    private Element mapDeliveryLocationOrNull(BG0013DeliveryInformation bg13, BG0015DeliverToAddress bg15) {

        // <cac:DeliveryLocation>
        //
        //     <cac:Address><!--BG-15-->
        //         <cbc:StreetName>Delivery Street </cbc:StreetName><!--BT-75-->
        //         <cbc:AdditionalStreetName>Delivery Additional street</cbc:AdditionalStreetName><!--BT-76-->
        //         <cbc:CityName>Delivery City </cbc:CityName><!--BT-77-->
        //         <cbc:PostalZone>34100</cbc:PostalZone><!--BT-78-->
        //         <cbc:CountrySubentity>TN</cbc:CountrySubentity><!--BT-79-->
        //         <cac:AddressLine>
        //             <cbc:Line>Delivery Line</cbc:Line><!--BT-165-->
        //         </cac:AddressLine>
        //         <cac:Country>
        //             <cbc:IdentificationCode>IT</cbc:IdentificationCode><!--BT-80-->
        //         </cac:Country>
        //     </cac:Address>
        // </cac:DeliveryLocation>
        Element deliveryLocationTag = new Element("DeliveryLocation");

        // <cbc:ID schemeID="0090">6754238987648</cbc:ID><!--BT-71, BT-71-1-->
        Identifier bt71 = evalExpression(() -> bg13.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier(0).getValue());
        if(bt71!=null) {
            Element id = new Element("ID").addContent(bt71.getIdentifier());
            if(bt71.getIdentificationSchema()!=null) {
                id.setAttribute("schemeID", bt71.getIdentificationSchema());
            }
            deliveryLocationTag.addContent(id);
        }

        Element addressTag = new Element("Address");

        // <cbc:StreetName>Delivery Street </cbc:StreetName><!--BT-75-->
        {
            String bt75 = evalExpression(() -> bg15.getBT0075DeliverToAddressLine1(0).getValue());
            if (bt75 != null) {
                addressTag.addContent(
                        new Element("StreetName")
                                .addContent(bt75)
                );
            }
        }


        // <cbc:AdditionalStreetName>Delivery Additional street</cbc:AdditionalStreetName><!--BT-76-->
        {
            String bt76 = evalExpression(() -> bg15.getBT0076DeliverToAddressLine2(0).getValue());
            if (bt76 != null) {
                addressTag.addContent(
                        new Element("AdditionalStreetName")
                                .addContent(bt76)
                );
            }
        }


        // <cbc:CityName>Delivery City </cbc:CityName><!--BT-77-->
        {
            String bt77 = evalExpression(() -> bg15.getBT0077DeliverToCity(0).getValue());
            if (bt77 != null) {
                addressTag.addContent(
                        new Element("CityName")
                                .addContent(bt77)
                );
            }
        }

        // <cbc:PostalZone>34100</cbc:PostalZone><!--BT-78-->
        {
            String bt78 = evalExpression(() -> bg15.getBT0078DeliverToPostCode(0).getValue() );
            if (bt78 != null) {
                addressTag.addContent(
                        new Element("PostalZone")
                                .addContent(bt78)
                );
            }
        }

        // <cbc:CountrySubentity>TN</cbc:CountrySubentity><!--BT-79-->
        {
            String bt79 = evalExpression(() -> bg15.getBT0079DeliverToCountrySubdivision(0).getValue() );
            if (bt79 != null) {
                addressTag.addContent(
                        new Element("CountrySubentity")
                                .addContent(bt79)
                );
            }
        }

        // <cac:AddressLine>
        //     <cbc:Line>Delivery Line</cbc:Line><!--BT-165-->
        // </cac:AddressLine>
        {
            String bt165 = evalExpression(() -> bg15.getBT0165DeliverToAddressLine3 (0).getValue() );
            if (bt165 != null) {
                addressTag.addContent(
                        new Element("AddressLine")
                                .addContent( new Element("Line").addContent(bt165) )
                );
            }
        }


        // <cac:Country>
        //     <cbc:IdentificationCode>IT</cbc:IdentificationCode><!--BT-80-->
        // </cac:Country>
        {
            Iso31661CountryCodes bt80 = evalExpression(() -> bg15.getBT0080DeliverToCountryCode(0).getValue());
            if (bt80 != null) {
                addressTag.addContent(
                        new Element("Country")
                                .addContent( new Element("IdentificationCode").addContent(bt80.getIso2charCode()) )
                );
            }
        }

        if(!addressTag.getChildren().isEmpty()) {
            deliveryLocationTag.addContent( addressTag );
        }else{
            deliveryLocationTag = null;
        }

        return deliveryLocationTag;
    }


}
