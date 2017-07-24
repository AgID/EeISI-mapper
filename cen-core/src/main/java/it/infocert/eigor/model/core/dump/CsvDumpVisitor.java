package it.infocert.eigor.model.core.dump;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.model.Visitor;

/** Dump a CEN invoice into a CSV that can be later reimported.
 * Just a very basic impl.
 */
public class CsvDumpVisitor implements Visitor {

    private final StringBuilder sb = new StringBuilder();
    private final char separator = ',';

    @Override
    public void startInvoice(BG0000Invoice invoice) {
        sb.append("BG/BT,Value\n");
    }

    @Override
    public void startBTBG(BTBG btbg) {

        String name = String.valueOf(btbg.denomination());
        String value = btbg.toString();
        sb.append(String.format("%s%c%s\n", name, separator, value));
    }

    @Override
    public void endBTBG(BTBG btbg) {
        // nothing to do
    }

    @Override
    public void endInvoice(BG0000Invoice invoice) {
        // nothing to do
    }

    @Override
    public String toString() {
        return sb.toString();
    }

}
