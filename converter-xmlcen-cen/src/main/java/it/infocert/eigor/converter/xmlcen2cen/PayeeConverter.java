package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0010Payee;
import it.infocert.eigor.model.core.model.BT0060PayeeIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier;
import org.codehaus.plexus.util.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;

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
                final BT0060PayeeIdentifierAndSchemeIdentifier payeeIdentifierAndSchemeIdentifier;
                if(Objects.nonNull(bt60.getAttribute("scheme")) && StringUtils.isNotEmpty(bt60.getAttribute("scheme").getValue())) {
                    final String scheme = bt60.getAttribute("scheme").getValue();
                    payeeIdentifierAndSchemeIdentifier = new BT0060PayeeIdentifierAndSchemeIdentifier(new Identifier(scheme, bt60.getText()));
                } else {
                    payeeIdentifierAndSchemeIdentifier = new BT0060PayeeIdentifierAndSchemeIdentifier(new Identifier(bt60.getText()));
                }
                payee.getBT0060PayeeIdentifierAndSchemeIdentifier().add(payeeIdentifierAndSchemeIdentifier);
            });

            final List<Element> bt61s = rootElement.getChild("BG-10").getChildren("BT-61");
            bt61s.forEach(bt61 -> {
                final BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier payeeLegalRegistrationIdentifierAndSchemeIdentifier;
                if(Objects.nonNull(bt61.getAttribute("scheme")) && StringUtils.isNotEmpty(bt61.getAttribute("scheme").getValue())) {
                    final String scheme = bt61.getAttribute("scheme").getValue();
                    payeeLegalRegistrationIdentifierAndSchemeIdentifier = new BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(scheme, bt61.getText()));
                } else {
                    payeeLegalRegistrationIdentifierAndSchemeIdentifier = new BT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(bt61.getText()));
                }
                payee.getBT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier().add(payeeLegalRegistrationIdentifierAndSchemeIdentifier);
            });
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0010(document, cenInvoice, errors);
    }
}
