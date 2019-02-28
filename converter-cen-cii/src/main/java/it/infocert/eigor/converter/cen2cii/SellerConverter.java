package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        if (!invoice.getBG0004Seller().isEmpty()) {
            BG0004Seller bg0004 = invoice.getBG0004Seller(0);
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace ramNs = rootElement.getNamespace("ram");
            Namespace rsmNs = rootElement.getNamespace("rsm");

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeAgreement = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
                rootElement.addContent(supplyChainTradeTransaction);
            }

            applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement == null) {
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            }





        }
    }
}
