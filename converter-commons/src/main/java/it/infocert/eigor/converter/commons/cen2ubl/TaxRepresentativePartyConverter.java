package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0011SellerTaxRepresentativeParty;
import it.infocert.eigor.model.core.model.BG0012SellerTaxRepresentativePostalAddress;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class TaxRepresentativePartyConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document target, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {


        // Generates element at /*[name()='Invoice']/cac:PayeeParty
        final Element root = target.getRootElement();

        BG0011SellerTaxRepresentativeParty bg11 = evalExpression(() -> invoice.getBG0011SellerTaxRepresentativeParty(0));

        if(bg11 == null) return;

        Element taxRepresentativeParty = new Element("TaxRepresentativeParty");

        // <cac:PartyName>
        //      <cbc:Name>Tax representative name</cbc:Name><!--BT-62-->
        // </cac:PartyName>
        String bt62 = evalExpression(() -> bg11.getBT0062SellerTaxRepresentativeName(0).getValue());
        if(bt62!=null) {

            new Element("PartyName")
                    .addContent(
                            new Element("Name")
                                .addContent(bt62)
                    );

        }



        // <cac:PostalAddress><!--BG-12-->
        //    ...
        // </cac:PostalAddress>
        BG0012SellerTaxRepresentativePostalAddress bg12 = evalExpression(() -> bg11.getBG0012SellerTaxRepresentativePostalAddress(0));
        if(bg12!=null) {

            Element postalAddress = new Element("PostalAddress");

            // <cbc:StreetName>Street tax representative</cbc:StreetName><!--BT-64-->
            String bt64 = evalExpression(() -> bg12.getBT0064TaxRepresentativeAddressLine1(0).getValue());
            if(bt64!=null) {
                postalAddress.addContent(
                        new Element("StreetName")
                                .addContent(bt64)

                );
            }

            // <cbc:AdditionalStreetName>Additional street tax representative</cbc:AdditionalStreetName><!--BT-65-->
            String bt65 = evalExpression(() -> bg12.getBT0065TaxRepresentativeAddressLine2(0).getValue());
            if(bt65!=null) {
                postalAddress.addContent(
                        new Element("AdditionalStreetName")
                                .addContent(bt65)

                );
            }

            // <cbc:CityName>City tax representative</cbc:CityName><!--BT-66-->
            String bt66 = evalExpression(() -> bg12.getBT0066TaxRepresentativeCity(0).getValue());
            if(bt66!=null) {
                postalAddress.addContent(
                        new Element("CityName")
                                .addContent(bt66)

                );
            }

            // <cbc:PostalZone>34100</cbc:PostalZone><!--BT-67-->
            String bt67 = evalExpression(() -> bg12.getBT0067TaxRepresentativePostCode(0).getValue());
            if(bt67!=null) {
                postalAddress.addContent(
                        new Element("PostalZone")
                                .addContent(bt67)

                );
            }

            // <cbc:CountrySubentity>TN</cbc:CountrySubentity><!--BT-68-->
            String bt68 = evalExpression(() -> bg12.getBT0068TaxRepresentativeCountrySubdivision(0).getValue());
            if(bt68!=null) {
                postalAddress.addContent(
                        new Element("CountrySubentity")
                                .addContent(bt68)

                );
            }

            // <cac:AddressLine>
            //      <cbc:Line>Line tax representative</cbc:Line><!--BT-164-->
            // </cac:AddressLine>
            String bt164 = evalExpression(() -> bg12.getBT0164TaxRepresentativeAddressLine3(0).getValue());
            if(bt164!=null) {
                postalAddress.addContent(
                        new Element("AddressLine")
                                .addContent(
                                        new Element("Line").addContent(bt164))

                );
            }

            // <cac:Country>
            //      <cbc:IdentificationCode>IT</cbc:IdentificationCode><!--BT-69-->
            // </cac:Country>
            String bt69 = evalExpression(() -> bg12.getBT0069TaxRepresentativeCountryCode(0).getValue().getIso2charCode());
            if(bt69!=null) {
                postalAddress.addContent(
                        new Element("Country")
                                .addContent(
                                        new Element("IdentificationCode").addContent(bt69))

                );
            }

            if(!postalAddress.getChildren().isEmpty()) {
                taxRepresentativeParty.addContent( postalAddress );
            }

        }

        // <cac:PartyTaxScheme>
        //      <cbc:CompanyID>IT343563160B01</cbc:CompanyID><!--BT-63-->
        //      <cac:TaxScheme>
        //          <cbc:ID>VAT</cbc:ID>
        //      </cac:TaxScheme>
        // </cac:PartyTaxScheme>
        Identifier bt63 = evalExpression(() -> bg11.getBT0063SellerTaxRepresentativeVatIdentifier(0).getValue());
        if(bt63!=null){

            Element tag = new Element("PartyTaxScheme")
                    .addContent(
                            new Element("CompanyID")
                                    .addContent(bt63.getIdentifier())
                                    .addContent(
                                            new Element("TaxScheme")
                                                    .addContent(
                                                            new Element("ID")
                                                                    .addContent(bt63.getIdentificationSchema())
                                                    )
                                    )

                    );

            taxRepresentativeParty.addContent(tag);

        }


    }
}
