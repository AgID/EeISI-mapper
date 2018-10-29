package it.infocert.eigor.converter.cen2fattpa;



import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
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

    String getShortFileFormat(MimeType mimeType) {
        if(mimeType == null) return null;

        String primaryType = mimeType.getPrimaryType();
        String subType = mimeType.getSubType();

        switch (primaryType) {
            case "application":
                if ("pdf".equals(subType)) return "pdf";
                if ("vnd.oasis.opendocument.spreadsheet".equals(subType)) return "ods";
                if ("vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(subType)) return "xlsx";
                break;
            case "image":
                if ("png".equals(subType)) return "png";
                if ("jpeg".equals(subType)) return "jpeg";
                break;
            case "text":
                if ("csv".equals(subType)) return "csv";
                break;
        }

        log.trace("No short file format for MIME {}", mimeType.getBaseType());
        return null;
    }
}
