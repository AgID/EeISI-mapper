package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The Custom Converter
 */
public class CustomConverter extends Cii2Cen implements CustomMapping<Document> {

    protected static Logger log = null;
    protected ConversionRegistry conversionRegistry;
    protected InvoiceUtils invoiceUtils;

    public CustomConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, new PropertiesBackedConfiguration());
        this.invoiceUtils = new InvoiceUtils(reflections);
        this.conversionRegistry = conversionRegistry;
        log = LoggerFactory.getLogger(CustomConverter.class);
    }


    public ConversionResult<BG0000Invoice> convert(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        TenderRefInvoicedObjectID bt0017_18 = new TenderRefInvoicedObjectID();
        InvoiceNoteConverter bg0001 = new InvoiceNoteConverter();
        PrecedingInvoiceReferenceConverter bg0003 = new PrecedingInvoiceReferenceConverter();
        SellerConverter bt0029 = new SellerConverter();
        BuyerIdentifierConverter bt0046 = new BuyerIdentifierConverter();
        CreditTransferConverter bg0017 = new CreditTransferConverter();
        DocumentLevelAllowancesConverter bg0020 = new DocumentLevelAllowancesConverter();
        DocumentLevelChargesConverter bg0021 = new DocumentLevelChargesConverter();
        VATBreakdownConverter bg0023 = new VATBreakdownConverter();
        AdditionalSupportingDocumentsConverter bg0024 = new AdditionalSupportingDocumentsConverter();
        InvoiceLineConverter bg0025 = new InvoiceLineConverter();
        DeliverToLocationIdentifierConverter bt0071 = new DeliverToLocationIdentifierConverter();
        PayeeIdentifierConverter bt0060 = new PayeeIdentifierConverter();
        ProjectReferenceConverter bt0011 = new ProjectReferenceConverter();

        bt0011.toBT0011(document, invoice, errors);
        bt0017_18.toBT0017_18(document, invoice, errors);
        bg0001.toBG0001(document, invoice, errors);
        bg0003.toBG0003(document, invoice, errors);
        bt0029.toBT0029(document, invoice, errors);
        bt0046.toBT0046(document, invoice, errors);
        bt0060.toBT0060(document, invoice, errors);
        bt0071.toBT0071(document, invoice, errors);
        bg0017.toBG0017(document, invoice, errors);
        bg0020.toBG0020(document, invoice, errors);
        bg0021.toBG0021(document, invoice, errors);
        bg0023.toBG0023(document, invoice, errors);
        bg0024.toBG0024(document, invoice, errors);
        bg0025.toBG0025(document, invoice, errors);

        return new ConversionResult<>(errors, invoice);
    }

    protected Element findNamespaceChild(Element parent, List<Namespace> namespacesInScope, String childName) {
        for (Namespace namespace : namespacesInScope) {
            Element child = parent.getChild(childName, namespace);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    protected List<Element> findNamespaceChildren(Element parent, List<Namespace> namespacesInScope, String childName) {
        for (Namespace namespace : namespacesInScope) {
            List<Element> children = parent.getChildren(childName, namespace);
            if (!children.isEmpty()) {
                return children;
            }
        }
        return new ArrayList<>();
    }


    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {

    }
}
