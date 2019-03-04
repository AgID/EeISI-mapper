package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.base.Preconditions;
import it.infocert.eigor.converter.cen2fattpa.models.AltriDatiGestionaliType;
import it.infocert.eigor.converter.cen2fattpa.models.DettaglioLineeType;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;

import java.util.*;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;
import static java.lang.Math.max;

class LinePostFixSupport {

    private Map<CenLine, FattpaLine> cenLinesToCorrespondingFattpaLine = new LinkedHashMap<>();

    /**
     * Set a pair of corresponding invoice lines.
     * Meaning they represent the same line in the cen invoice and in the fattpa invoice.
     */
    public void registerForPostFix(BG0025InvoiceLine cenLine, DettaglioLineeType fattpaLine) {
        CenLineBG0025InvoiceLine cenLineWrapper = new CenLineBG0025InvoiceLine(cenLine);
        registerForPostFix(cenLineWrapper, new FattpaLineImpl(fattpaLine, cenLineWrapper));
    }

    /**
     * Set a pair of corresponding invoice lines.
     * Meaning they represent the same line in the cen invoice and in the fattpa invoice.
     */
    public void registerForPostFix(CenLine cenLine, FattpaLine fattpaLine) {
        cenLinesToCorrespondingFattpaLine.put(cenLine, fattpaLine);
    }

    /**
     * Renumber the lines of the fattpa invoice according to the defined algorithm.
     * @return A map containing an entry for each line that has been renumbered. You have the original id of the line as key and the new id as value.
     */
    public void appliesRenumbering() {


        int biggestIdentifierUsedInCen = -1;
        Set<CenLine> cenInvoiceLines = cenLinesToCorrespondingFattpaLine.keySet();
        for (CenLine cenInvoiceLine : cenInvoiceLines) {
            try {

                String lineIdentifierAsString = cenInvoiceLine.lineIdentifier();
                if(lineIdentifierAsString == null) continue;

                int identifierOfLine = Integer.parseInt(lineIdentifierAsString);
                if(identifierOfLine>=10000) {
                    throw new NumberFormatException("Invoice line with identifier '" + lineIdentifierAsString + "' uses a number that is too big.");
                }
                biggestIdentifierUsedInCen = max(biggestIdentifierUsedInCen, identifierOfLine);
            } catch (NumberFormatException e) {
                // it is not a number after all
            }
        }

        int currentFattpaLineIdentifier = biggestIdentifierUsedInCen == -1 ? 1 : biggestIdentifierUsedInCen;
        Set<Map.Entry<CenLine, FattpaLine>> cenLinesAndFattpaLines = cenLinesToCorrespondingFattpaLine.entrySet();

        // renumber the lines that needs renumbering to store info about renumbering in the correct XML element
        Map<String, Integer> cenOriginalsToNewFattpa = new HashMap<>();
        for (Map.Entry<CenLine, FattpaLine> cenLineAndFattpaLine : cenLinesAndFattpaLines) {

            int newLineNumber;
            CenLine cenLine = cenLineAndFattpaLine.getKey();
            String cenLineIdentifier = cenLine.lineIdentifier();
            FattpaLine fattpaLine = cenLineAndFattpaLine.getValue();
            try {
                int cenLineNumber;
                cenLineNumber = Integer.parseInt(cenLineIdentifier);
                if(cenLineNumber>=10000) {
                    throw new NumberFormatException();
                }
                newLineNumber = cenLineNumber;
            } catch (NumberFormatException e) {
                newLineNumber = ++currentFattpaLineIdentifier;
                cenOriginalsToNewFattpa.put( cenLineIdentifier, newLineNumber );
            }
            fattpaLine.setNumeroLinea( newLineNumber );

        }

    }

    interface CenLine {

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

    interface FattpaLine {

        void setNumeroLinea(int originalLineNumber);
    }

    static class FattpaLineImpl implements FattpaLine {
        private final DettaglioLineeType line;
        private final CenLine cenLine;
        private boolean changePerformed;

        FattpaLineImpl(DettaglioLineeType line, CenLine cenLine) {
            this.line = Preconditions.checkNotNull( line );
            this.cenLine = Preconditions.checkNotNull( cenLine );
            this.changePerformed = false;
        }

        @Override
        public void setNumeroLinea(int originalLineNumber) {

            Preconditions.checkState( !changePerformed, "The line %d has been already changed.", originalLineNumber );

            String cenIdentifier = cenLine.lineIdentifier();
            if( !String.valueOf(originalLineNumber).equals(cenIdentifier) ) {
               List<AltriDatiGestionaliType> altriDatiGestionali = this.line.getAltriDatiGestionali();
               altriDatiGestionali.add(
                       LineConverter.newAltriDatiGestionaliType("Rinum",
                       String.format("Linea '%s' rinumerata '%s'", cenIdentifier, originalLineNumber)) );
            }

            changePerformed = true;
            this.line.setNumeroLinea(originalLineNumber);

        }
    }
}
