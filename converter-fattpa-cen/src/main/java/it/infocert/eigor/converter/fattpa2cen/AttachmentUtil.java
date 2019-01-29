package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0024AdditionalSupportingDocuments;
import it.infocert.eigor.model.core.model.BT0122SupportingDocumentReference;
import it.infocert.eigor.model.core.model.BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AttachmentUtil {

    private final static Logger log = LoggerFactory.getLogger(AttachmentUtil.class);

    private final static String NOT_MAPPED_FILENAME = "not-mapped-values";

    private final File workdir;

    public AttachmentUtil(final File workdir) {
        Preconditions.checkNotNull(
                workdir,
                "Please provide a not null existing working folder.");
        Preconditions.checkArgument(
                workdir.exists() && workdir.isDirectory(),
                "Path '%s' (resolved to '%s') must be an existing writeable directory, but it's not.", workdir.getPath(), workdir.getAbsolutePath());
        this.workdir = workdir;
    }

    /**
     * Append the given string to the end of the <i>not-mapped-values.txt</i>
     * attachment of the given {@link BG0000Invoice}
     * The input string must be in the format XMLPA-ID: XMLPA-Value.
     * E.G <code>Ritenuta: AAA</code>
     *
     * @param invoice a {@link BG0000Invoice} to attach the file to
     * @param input   the string to append
     */
    void addValuesToAttachment(final BG0000Invoice invoice, final String input, List<IConversionIssue> errors) {

        BG0024AdditionalSupportingDocuments bg0024 = null;

        for (BG0024AdditionalSupportingDocuments aBg0024 : invoice.getBG0024AdditionalSupportingDocuments()) {
            if (!aBg0024.getBT0122SupportingDocumentReference().isEmpty()) {
                String bt0122 = aBg0024.getBT0122SupportingDocumentReference(0).getValue();
                if (NOT_MAPPED_FILENAME.equals(bt0122)) {
                    bg0024 = aBg0024;
                    break;
                }
            }
        }

        if (bg0024 == null) {
            bg0024 = new BG0024AdditionalSupportingDocuments();
            invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);
            BT0122SupportingDocumentReference supportingDocumentReference = new BT0122SupportingDocumentReference(NOT_MAPPED_FILENAME);
            bg0024.getBT0122SupportingDocumentReference().add(supportingDocumentReference);
        }


        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 = null;
        if (!bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
            bt0125 = bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(0);

            FileReference fileReference = bt0125.getValue();
            try {
                appendToFileInBase64(new File(fileReference.getFilePath()), input);
            } catch (IOException e) {
                EigorRuntimeException ere = new EigorRuntimeException(
                        e,
                        ErrorMessage.builder()
                                .message(e.getMessage())
                                .location(ErrorCode.Location.FATTPA_IN)
                                .action(ErrorCode.Action.HARDCODED_MAP)
                                .build());
                errors.add(ConversionIssue.newError(ere));
            }

        } else {
            File dest = new File(workdir + File.separator + "tmp");
            if (!dest.exists()) {
                dest.mkdirs();
            }
            File file = new File(String.format("%s%s%s%s.tmp", dest.getAbsolutePath(), File.separator, NOT_MAPPED_FILENAME, UUID.randomUUID()));

            try {
                appendToFileInBase64(file, input);

                bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(
                        new FileReference(file.getAbsolutePath(), new MimeType("text", "csv"), NOT_MAPPED_FILENAME));
                bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
            } catch (MimeTypeParseException | IOException e) {
                EigorRuntimeException ere = new EigorRuntimeException(
                        e,
                        ErrorMessage.builder()
                                .message(e.getMessage())
                                .location(ErrorCode.Location.FATTPA_IN)
                                .action(ErrorCode.Action.HARDCODED_MAP)
                                .build());
                errors.add(ConversionIssue.newError(ere));
            }
        }
    }

    public void appendToFileInBase64(File file, String text) throws IOException {

        byte[] bytes = text.concat("\n").getBytes();
        byte[] output;

        if (file.isFile()) {
            byte[] base64Content = FileUtils.readFileToByteArray(file);
            byte[] contents = Base64.decodeBase64(base64Content);

            output = new byte[contents.length + bytes.length];
            System.arraycopy(contents, 0, output, 0, contents.length);
            System.arraycopy(bytes, 0, output, contents.length, bytes.length);
        } else {
            output = bytes;
        }

        FileUtils.writeByteArrayToFile(file, Base64.encodeBase64(output));
        log.debug("Not-mapped value \"{}\" appended in attachment at {}", text, file.getAbsolutePath());
    }
}
