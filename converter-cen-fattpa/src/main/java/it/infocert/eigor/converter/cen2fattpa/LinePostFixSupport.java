package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.base.Preconditions;
import it.infocert.eigor.fattpa.commons.models.AltriDatiGestionaliType;
import it.infocert.eigor.fattpa.commons.models.DettaglioLineeType;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;
import static java.lang.Math.max;

class LinePostFixSupport {

    private final Map<CenLine, FattpaLine> cenLinesToCorrespondingFattpaLine;
    private final List<FattpaLine> artifactedFattpaLines;
    private boolean renumberingPerformed;
    private final int maxExcluded;

    /** Initialize the LinePostFix support with a proper default. */
    public LinePostFixSupport() {
        this(10000);
    }

    public LinePostFixSupport(int maxExcluded) {
        checkArgument( maxExcluded > 0 );
        this.maxExcluded = maxExcluded;
        renumberingPerformed = false;
        artifactedFattpaLines = new LinkedList<FattpaLine>();
        cenLinesToCorrespondingFattpaLine = new LinkedHashMap<>();
    }

    /**
     * Register a pair of corresponding invoice lines for later renumbering.
     * "Corresponding lines" means they represent the same line in the cen invoice and in the fattpa invoice.
     */
    public void registerForPostFix(BG0025InvoiceLine cenLine, DettaglioLineeType fattpaLine) {

        checkRenumberingHasNotOccurredYet();

        CenLineBG0025InvoiceLine cenLineWrapper = new CenLineBG0025InvoiceLine(cenLine);
        registerForPostFix(cenLineWrapper, new FattpaLineImpl(fattpaLine, cenLineWrapper));
    }

    /**
     * Set a pair of corresponding invoice lines for later renumbering.
     * "Corresponding lines" means they represent the same line in the cen invoice and in the fattpa invoice.
     */
    public void registerForPostFix(CenLine cenLine, FattpaLine fattpaLine) {

        checkRenumberingHasNotOccurredYet();

        cenLinesToCorrespondingFattpaLine.put(cenLine, fattpaLine);
    }

    public void registerForPostFix(FattpaLine line) {

        checkRenumberingHasNotOccurredYet();

        artifactedFattpaLines.add(line);
    }

    public void registerForPostFix(DettaglioLineeType line) {

        checkRenumberingHasNotOccurredYet();

        artifactedFattpaLines.add(new SingleFattpaLineImpl(line));
    }

    /**
     * Renumber the lines of the fattpa invoice according to the defined algorithm.
     * @return A map containing an entry for each line that has been renumbered. You have the original id of the line as key and the new id as value.
     */
    public void appliesRenumbering() {

        checkRenumberingHasNotOccurredYet();

        int biggestIdentifierUsedInCen = -1;
        Set<CenLine> cenInvoiceLines = cenLinesToCorrespondingFattpaLine.keySet();
        for (CenLine cenInvoiceLine : cenInvoiceLines) {
            try {

                String lineIdentifierAsString = cenInvoiceLine.lineIdentifier();
                if(lineIdentifierAsString == null) continue;

                int identifierOfLine = Integer.parseInt(lineIdentifierAsString);
                if(identifierOfLine>= maxExcluded) {
                    throw new NumberFormatException("Invoice line with identifier '" + lineIdentifierAsString + "' uses a number that is bigger than the max allowd: " + (maxExcluded-1));
                }
                biggestIdentifierUsedInCen = max(biggestIdentifierUsedInCen, identifierOfLine);
            } catch (NumberFormatException e) {
                // it is not a number after all, or it is too big.
            }
        }

        int currentFattpaLineIdentifier = biggestIdentifierUsedInCen == -1 ? 1 : biggestIdentifierUsedInCen;
        Set<Map.Entry<CenLine, FattpaLine>> cenLinesAndFattpaLines = cenLinesToCorrespondingFattpaLine.entrySet();

        // renumber the lines that needs renumbering to store info about renumbering in the correct XML element
        HashSet<Integer> takenNumbers = new HashSet<>( cenLinesAndFattpaLines.size() );
        Map<String, Integer> cenOriginalsToNewFattpa = new HashMap<>();
        for (Map.Entry<CenLine, FattpaLine> cenLineAndFattpaLine : cenLinesAndFattpaLines) {

            int newLineNumber;
            CenLine cenLine = cenLineAndFattpaLine.getKey();
            String cenLineIdentifier = cenLine.lineIdentifier();
            FattpaLine fattpaLine = cenLineAndFattpaLine.getValue();
            try {
                int cenLineNumber;
                cenLineNumber = Integer.parseInt(cenLineIdentifier);
                if(cenLineNumber>= maxExcluded) {
                    throw new NumberFormatException();
                }
                newLineNumber = cenLineNumber;
            } catch (NumberFormatException e) {
                newLineNumber = ++currentFattpaLineIdentifier;
                cenOriginalsToNewFattpa.put( cenLineIdentifier, newLineNumber );
            }


            if(newLineNumber>=maxExcluded) {
                throw new IllegalStateException("It was impossible to find an available id for line " + cenLine);
            }

            fattpaLine.setNumeroLinea( newLineNumber );
            takenNumbers.add( newLineNumber );

        }

        int max = maxExcluded - 1;
        for (FattpaLine artifactedFattpaLine : artifactedFattpaLines) {
            int proposedNewNumber;
            do {
                proposedNewNumber = max--;
            }while( takenNumbers.contains(proposedNewNumber) );

            if(proposedNewNumber<=0) {
                throw new IllegalStateException("It was impossible to find an available id for line " + artifactedFattpaLine);
            }

            artifactedFattpaLine.setNumeroLinea(proposedNewNumber);
        }

        renumberingPerformed = true;

    }

    private void checkRenumberingHasNotOccurredYet() {
        checkState( !renumberingPerformed );
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

            checkState( !changePerformed, "The line %s has been already changed.", originalLineNumber );

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

    static class SingleFattpaLineImpl implements FattpaLine {
        private final DettaglioLineeType line;
        private boolean changePerformed;

        SingleFattpaLineImpl(DettaglioLineeType line) {
            this.line = Preconditions.checkNotNull( line );
            this.changePerformed = false;
        }

        @Override
        public void setNumeroLinea(int newLineNumber) {

            checkState( !changePerformed, "The line %s has been already changed.", newLineNumber );
            changePerformed = true;
            this.line.setNumeroLinea(newLineNumber);

        }
    }

}
