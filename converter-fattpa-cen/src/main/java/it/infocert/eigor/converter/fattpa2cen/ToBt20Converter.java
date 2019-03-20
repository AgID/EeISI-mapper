package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0020PaymentTerms;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;

public class ToBt20Converter implements CustomMapping<Document> {

    private static final XPathFactory xpfac = XPathFactory.instance();
    private static final List<Holder> holders;

    static {
        List<Holder> tmp = Arrays.asList(
                new Holder("//*[local-name()='CondizioniPagamento']/text()", "Condizioni di Pagamento"),
                new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='DataRiferimentoTerminiPagamento']/text()", "Data Riferimento Termini Pagamento"),
                new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='GiorniTerminiPagamento']/text()", "Giorni Termini Pagamento"),
                new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='ScontoPagamentoAnticipato']/text()", "Sconto Pagamento Anticipato"),
                new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='DataLimitePagamentoAnticipato']/text()", "Data Limite Pagamento Anticipato"),
                new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='PenalitaPagamentiRitardati']/text()", "Penalita Pagamenti Ritardati"),
                new Holder("//*[local-name()='DettaglioPagamento']//*[local-name()='DataDecorrenzaPenale']/text()", "Data Decorrenza Penale")
        );
        sort(tmp);
        holders = unmodifiableList( tmp );
    }


    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        StringBuilder sb = new StringBuilder();
        XPathExpression xpath;
        for (Holder holder : holders) {
            xpath = xpfac.compile(holder.getxPath());
            List matches = xpath.evaluate(document);
            if(!matches.isEmpty()) {
                if( sb.length() > 0) sb.append(", ");
                sb.append(holder.getLabel() + "=" + matches.stream().map(m -> ((Content)m).getValue()).collect(Collectors.joining(", ")));
            }
        }

        if(sb.length() == 0) {
            sb.append("N/A Payment Terms");
        }

        BT0020PaymentTerms newBt20 = new BT0020PaymentTerms(sb.toString());
        if(cenInvoice.hasBT0020PaymentTerms()){
            cenInvoice
                    .getBT0020PaymentTerms()
                    .set(0, newBt20);
        }else{
            cenInvoice.getBT0020PaymentTerms().add(newBt20);
        }

    }

    static class Holder implements Comparable<Holder> {
        private String xPath;
        private String label;

        public Holder(String xPath, String label) {
            checkArgument( xPath != null && !xPath.isEmpty() );
            checkArgument( label != null && !label.isEmpty() );
            this.xPath = xPath;
            this.label = label;
        }

        public String getxPath() {
            return xPath;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public int compareTo(@NotNull Holder o) {
            return this.label.compareTo(o.label);
        }
    }
}
