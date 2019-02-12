package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.base.Preconditions;
import it.infocert.eigor.converter.cen2fattpa.models.DettaglioLineeType;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

class LinePostFixSupport {

    private Map<CenLine, FattpaLine> cenLinesToCorrespondingFattpaLine = new LinkedHashMap<>();

    public void registerForPostFix(BG0025InvoiceLine cenLine, DettaglioLineeType fattpaLine) {
        registerForPostFix(new CenLineBG0025InvoiceLine(cenLine), new FattpaLineImpl(fattpaLine));
    }

    public void registerForPostFix(CenLine cenLine, FattpaLine fattpaLine) {
        cenLinesToCorrespondingFattpaLine.put(cenLine, fattpaLine);
    }

    public void postfix() {


        Set<CenLine> bg25InvoiceLines = cenLinesToCorrespondingFattpaLine.keySet();

        int max = -1;
        for (CenLine bg25InvoiceLine : bg25InvoiceLines) {
            try {

                String lineIdentifierAsString = bg25InvoiceLine.lineIdentifier();
                if(lineIdentifierAsString == null) continue;

                int identifierOfLine = Integer.parseInt(lineIdentifierAsString);
                if(identifierOfLine>=10000) {
                    throw new NumberFormatException();
                }
                max = Math.max(max, identifierOfLine);
            } catch (NumberFormatException e) {
                // it is not a number after all
            }
        }

        int current = max == -1 ? 1 : max;
        Set<Map.Entry<CenLine, FattpaLine>> cenLinesAndFattpaLines = cenLinesToCorrespondingFattpaLine.entrySet();
        for (Map.Entry<CenLine, FattpaLine> cenLineAndFattpaLine : cenLinesAndFattpaLines) {

            CenLine key = cenLineAndFattpaLine.getKey();
            FattpaLine dettaglioLinea = cenLineAndFattpaLine.getValue();
            try {
                int originalLineNumber = Integer.parseInt(key.lineIdentifier());
                if(originalLineNumber>=10000) {
                    throw new NumberFormatException();
                }

                dettaglioLinea.setNumeroLinea( originalLineNumber );
            } catch (NumberFormatException e) {
                dettaglioLinea.setNumeroLinea( ++current );
            }

        }

    }

    static interface CenLine {

        String lineIdentifier();
    }

    class CenLineBG0025InvoiceLine implements CenLine {
        private final BG0025InvoiceLine bg0025;

        CenLineBG0025InvoiceLine(BG0025InvoiceLine bg0025) {
            this.bg0025 = Preconditions.checkNotNull( bg0025 );
        }

        @Override
        public String lineIdentifier() {
            return evalExpression( ()-> bg0025.getBT0126InvoiceLineIdentifier(0).getValue() );
        }
    }

    static interface FattpaLine {

        void setNumeroLinea(int originalLineNumber);
    }

    static class FattpaLineImpl implements FattpaLine {
        private final DettaglioLineeType line;

        FattpaLineImpl(DettaglioLineeType line) {
            this.line = Preconditions.checkNotNull( line );
        }

        @Override
        public void setNumeroLinea(int originalLineNumber) {
            this.line.setNumeroLinea(originalLineNumber);
        }
    }
}
