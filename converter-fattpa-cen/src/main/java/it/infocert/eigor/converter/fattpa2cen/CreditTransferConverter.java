package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Credit Transfer Custom Converter
 */
public class CreditTransferConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0017(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0017CreditTransfer bg0017 = null;

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiPagamento = fatturaElettronicaBody.getChild("DatiPagamento");
            if (datiPagamento != null) {
                List<Element> dettaglioPagamenti = datiPagamento.getChildren();
                for (Element dettaglioPagamento : dettaglioPagamenti) {
                    if (dettaglioPagamento.getName().equals("DettaglioPagamento")) {
                        bg0017 = new BG0017CreditTransfer();
                        Element iban = dettaglioPagamento.getChild("IBAN");
                        if (iban != null) {
                            BT0084PaymentAccountIdentifier paymentAccountIdentifier = new BT0084PaymentAccountIdentifier(iban.getText());
                            bg0017.getBT0084PaymentAccountIdentifier().add(paymentAccountIdentifier);
                        }
                        Element bic = dettaglioPagamento.getChild("BIC");
                        if (bic != null) {
                            BT0086PaymentServiceProviderIdentifier paymentServiceProviderIdentifier = new BT0086PaymentServiceProviderIdentifier(bic.getText());
                            bg0017.getBT0086PaymentServiceProviderIdentifier().add(paymentServiceProviderIdentifier);
                        }
                        if (invoice.getBG0016PaymentInstructions().isEmpty()) {
                            invoice.getBG0016PaymentInstructions().add(new BG0016PaymentInstructions());
                        }
                        invoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer().add(bg0017);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0017(document, cenInvoice, errors);
    }
}