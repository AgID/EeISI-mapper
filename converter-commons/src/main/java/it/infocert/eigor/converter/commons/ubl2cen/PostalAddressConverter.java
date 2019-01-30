package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0005SellerPostalAddress;
import it.infocert.eigor.model.core.model.BT0039SellerCountrySubdivision;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The Postal Address Custom Converter
 * replaces one2one /BG0004/BG0005/BT0039 = /Invoice/AccountingSupplierParty/Party/PostalAddress/CountrySubentity
 */
public class PostalAddressConverter extends CustomConverterUtils implements CustomMapping<Document> {

    private static final Logger log = LoggerFactory.getLogger(PostalAddressConverter.class);

    public ConversionResult<BG0000Invoice> toBT0039(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element accountingSupplierParty = findNamespaceChild(rootElement, namespacesInScope, "AccountingSupplierParty");

        if (accountingSupplierParty != null) {
            Element party = findNamespaceChild(accountingSupplierParty, namespacesInScope, "Party");

            if (party != null) {
                Element postalAddress = findNamespaceChild(party, namespacesInScope, "PostalAddress");

                if (postalAddress != null) {
                    Element countryCode = null;

                    Element country = findNamespaceChild(postalAddress, namespacesInScope, "Country");
                    if (country != null) {
                        countryCode = findNamespaceChild(country, namespacesInScope, "IdentificationCode");
                    }

                    Element countrySubentity = findNamespaceChild(postalAddress, namespacesInScope, "CountrySubentity");

                    if (countryCode != null && countrySubentity != null) {
                        String countryCodeValue = countryCode.getValue();
                        if (countryCodeValue.equalsIgnoreCase("IT")) {
                            if (isValidProvinceOfItaly(countryCodeValue)) {
                                addBT39(invoice, countrySubentity);
                            }
                        } else {
                            addBT39(invoice, countrySubentity);
                        }
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    private void addBT39(BG0000Invoice invoice, Element countrySubentity) {
        if (!invoice.getBG0004Seller().isEmpty()) {
            final BG0004Seller seller = invoice.getBG0004Seller(0);
            if (!seller.getBG0005SellerPostalAddress().isEmpty()) {
                final BG0005SellerPostalAddress address = seller.getBG0005SellerPostalAddress(0);
                final BT0039SellerCountrySubdivision bt0039 = new BT0039SellerCountrySubdivision(countrySubentity.getValue());
                address.getBT0039SellerCountrySubdivision().add(bt0039);
            }
        }
    }

    private boolean isValidProvinceOfItaly(String countryCodeValue) {
        // if validation is a requirement we need an enum of the italian provinces
        return true;
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBT0039(document, cenInvoice, errors);
    }
}
