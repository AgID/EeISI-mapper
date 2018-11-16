package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0010Payee;
import it.infocert.eigor.model.core.model.BT0060PayeeIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PayeeConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0010(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element bg10 = rootElement.getChild("BG-10");

        if(Objects.nonNull(bg10)) {
            if (invoice.getBG0010Payee().isEmpty()) {
                invoice.getBG0010Payee().add(new BG0010Payee());
            }
            final BG0010Payee payee = invoice.getBG0010Payee().get(0);
            
            final List<Element> bt60s = rootElement.getChild("BG-10").getChildren("BT-60");
            bt60s.forEach(bt60 -> {
                final Optional<BTBG> bt0060 = ConverterUtils.getBt.apply("BT0060", bt60);
                if(bt0060.isPresent()) {
                    payee.getBT0060PayeeIdentifierAndSchemeIdentifier().add((BT0060PayeeIdentifierAndSchemeIdentifier) bt0060.get());
                }
            });

            final List<Element> bt61s = rootElement.getChild("BG-10").getChildren("BT-61");
            bt61s.forEach(bt61 -> {
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0061", bt61);
                if(bt.isPresent()) {
                    payee.getBT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier().add((BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier) bt.get());
                }
            });
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0010(document, cenInvoice, errors);
    }
}
