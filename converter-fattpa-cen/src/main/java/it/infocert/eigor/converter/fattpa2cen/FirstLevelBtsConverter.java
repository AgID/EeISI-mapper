package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0016DespatchAdviceReference;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class FirstLevelBtsConverter implements CustomMapping<Document> {
    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        mapBt16(invoice, document, errors);
    }

    private void mapBt16(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        BT0016DespatchAdviceReference bt0016 = null;

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            if (datiGenerali != null) {
                List<Element> children = datiGenerali.getChildren();
                for (Element datiDDT : children) {
                    if (datiDDT.getName().equals("DatiDDT")) {
                        Element numero = datiDDT.getChild("NumeroDDT");
                        Element data = datiDDT.getChild("DataDDT");
                        if (numero != null && data != null) {
                            bt0016 = new BT0016DespatchAdviceReference(numero.getText() + ":" + data.getText());
                        }
                        if (bt0016 != null) invoice.getBT0016DespatchAdviceReference().add(bt0016);
                    }
                }
            }
        }
    }
}
