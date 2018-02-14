package it.infocert.eigor.converter.cen2fattpa;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Utility class to deal with FattPA Attachment
 */
class AttachmentUtil {
    private final static Logger log = LoggerFactory.getLogger(AttachmentUtil.class);

    /**
     * Append the given string to the end of the <i>not-mapped-values.txt</i>
     * attachment of the given {@link FatturaElettronicaBodyType}
     * The input string must be in the format BTXXXX: schema : value.
     * E.G <code>BT0049: IT:PEC : mail@pec.com</code>
     * @param body a {@link FatturaElettronicaBodyType} body to attach the file to
     * @param input the string to append
     */
    void addToUnmappedValuesAttachment(final FatturaElettronicaBodyType body, final String input) {
        log.debug("Adding string '{}' to FattPA attachment", input);
        List<AllegatiType> allegati = body.getAllegati();
        String content = "";
        AllegatiType allegato;
        if (allegati.isEmpty()) {
            allegato = new AllegatiType();
            allegato.setNomeAttachment("not-mapped-values");
            allegato.setFormatoAttachment("txt");
            allegati.add(allegato);
        } else {
            allegato = Stream.of(allegati).filter(new Filter<AllegatiType>() {
                @Override
                public boolean apply(AllegatiType allegato) {
                    return "not-mapped-values".equals(allegato.getNomeAttachment());
                }
            }).first();
            content = new String(allegato.getAttachment());
        }
        final String updated = "".equalsIgnoreCase(content) ? input : content + System.lineSeparator() + input;
        allegato.setAttachment(updated.getBytes());
    }
}
