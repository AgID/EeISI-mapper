package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.collect.ImmutableList;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0020PaymentTerms;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ToBt20Converter implements CustomMapping<Document> {

    private static final XPathFactory xpfac = XPathFactory.instance();
    private final List<Holder> holders;

    public ToBt20Converter() {
        holders = ImmutableList.of(
                new Holder("//*[local-name()='CondizioniPagamento']/text()", "Condizioni di Pagamento"),
        new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='ScontoPagamentoAnticipato']/text()","xx"),
        new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='DataLimitePagamentoAnticipato']/text()","xx"),
        new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='PenalitaPagamentiRitardati']/text()","xx"),
        new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='DataDecorrenzaPenale']/text()","xx")
        );
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        StringBuilder sb = new StringBuilder();
        XPathExpression xpath;
        for (Holder holder : holders) {
            xpath = xpfac.compile(holder.getxPath());
            List matches = xpath.evaluate(document);

            Element k = null;
            Text t;
            Content c = k;

            if(!matches.isEmpty()) {
                if(sb.length()>0) sb.append("\n");
                sb.append(holder.getLabel() + " = " + matches.stream().map(m -> ((Content)m).getValue()).collect(Collectors.joining(", ")));
            }
        }

        cenInvoice.getBT0020PaymentTerms().set(0, new BT0020PaymentTerms(sb.toString()));

    }

    static class Holder {
        private String xPath;
        private String label;

        public Holder(String xPath, String label) {
            this.xPath = xPath;
            this.label = label;
        }

        public String getxPath() {
            return xPath;
        }

        public String getLabel() {
            return label;
        }
    }
}
