package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.base.Preconditions;
import it.infocert.eigor.converter.cen2fattpa.models.DettaglioLineeType;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class LinePostFixSupport {

    private Map<CenLine, FattpaLine> map = new LinkedHashMap<>();

    public void registerForPostFix(BG0025InvoiceLine masterCenLine, DettaglioLineeType renderedFattpaLine) {
        CenLineBG0025InvoiceLine key = new CenLineBG0025InvoiceLine(masterCenLine);
        FattpaLineImpl value = new FattpaLineImpl(renderedFattpaLine);
        registerForPostFix(key, value);
    }

    public void registerForPostFix(CenLine key, FattpaLine value) {
        map.put(key, value);
    }

    public void postfix() {


        Set<CenLine> bg0025InvoiceLines = map.keySet();

        int max = -1;
        for (CenLine bg0025InvoiceLine : bg0025InvoiceLines) {
            try {
                int theInt = Integer.parseInt(bg0025InvoiceLine.lineIdentifier());
                if(theInt>=10000) {
                    throw new NumberFormatException();
                }


                max = Math.max(max, theInt);
            } catch (NumberFormatException e) {

            }
        }

        int current = max == -1 ? 1 : max;
        Set<Map.Entry<CenLine, FattpaLine>> entries = map.entrySet();
        for (Map.Entry<CenLine, FattpaLine> entry : entries) {

            CenLine key = entry.getKey();
            FattpaLine dettaglioLinea = entry.getValue();
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
            return bg0025.getBT0126InvoiceLineIdentifier(0).getValue();
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
