package it.infocert.eigor.converter.cen2fattpa;



import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.AbstractBT;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0024AdditionalSupportingDocuments;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class AttachmentConverter implements CustomMapping<FatturaElettronicaType> {

    private final static Logger log = LoggerFactory.getLogger(AttachmentConverter.class);

    private final List<String> cenPaths = Lists.newArrayList(
            "/BT0007",
            "/BT0006",
            "/BT0010",
            "/BT0014",
            "/BT0018",
            "/BG0002/BT0023",
            "/BG0004/BT0028",
            "/BG0004/BT0033",
            "/BG0004/BT0034",
            "/BG0007/BT0045",
            "/BG0007/BG0009/BT0057",
            "/BG0007/BG0009/BT0058",
            "/BG0010/BT0060",
            "/BG0010/BT0061",
            "/BG0011/BG0012/BT0064",
            "/BG0011/BG0012/BT0065",
            "/BG0011/BG0012/BT0066",
            "/BG0011/BG0012/BT0067",
            "/BG0011/BG0012/BT0068",
            "/BG0011/BG0012/BT0069",
            "/BG0011/BG0012/BT0164",
            "/BG0016/BG0018/BT0087",
            "/BG0016/BG0018/BT0088",
            "/BG0016/BG0019/BT0089",
            "/BG0016/BG0019/BT0090",
            "/BG0016/BG0019/BT0091",
            "/BG0022/BT0111",
            "/BG0022/BT0113"
    );


    private final InvoiceUtils invoiceUtils = new InvoiceUtils(new JavaReflections());
    private final String attachmentName = "not-mapped-values";

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
        int size = bodies.size();
        if (size > 1) {
            final String message = "Too many FatturaElettronicaBody found in current FatturaElettronica";
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    message,
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.ILLEGAL_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "FatturaElettronicaBody")
            )));
        } else if (size < 1) {
            final String message = "No FatturaElettronicaBody found in current FatturaElettronica";
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    message,
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.MISSING_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "FatturaElettronicaBody")
            )));
        } else {
            FatturaElettronicaBodyType fatturaElettronicaBody = bodies.get(0);
            setUnmappedCenElements(invoice, fatturaElettronicaBody);
            forwardExistingAttachments(invoice, fatturaElettronicaBody, errors, callingLocation);
        }
    }

    public String createAttachment(BG0000Invoice invoice) {
        StringBuilder sb = new StringBuilder();

        for (String path : cenPaths) {
            List<AbstractBT> foundBts = invoiceUtils.getBtRecursively(invoice, path, new ArrayList<AbstractBT>(0));
            for (AbstractBT bt : foundBts) {
                Object value = bt.getValue();
                sb.append(bt.denomination()).append(": ").append(value).append(System.lineSeparator());

            }
        }

        log.warn("Created attachment {}.txt with unmapped cen elements.", attachmentName);
        log.debug("{}: {}.", attachmentName, cenPaths);

        return sb.toString();
    }

    private void setUnmappedCenElements(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody) {

        String attachment = createAttachment(invoice);
        if (!"".equals(attachment)) {
            final List<AllegatiType> allegati = fatturaElettronicaBody.getAllegati();
            if (allegati.isEmpty()) {
                AllegatiType allegato = new AllegatiType();
                allegato.setNomeAttachment(attachmentName); //TODO How to name it?
                allegato.setFormatoAttachment("txt");
                allegato.setAttachment(attachment.getBytes());
                allegati.add(allegato);
            } else {
                AllegatiType unmapped = allegati.stream() .filter(allegato -> "not-mapped-values".equals(allegato.getNomeAttachment())).findFirst().orElse(null);
                unmapped.setAttachment(attachment.getBytes());
            }
        }
    }

    private void forwardExistingAttachments(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        log.info("Starting converting Allegati");
        if (!invoice.getBG0024AdditionalSupportingDocuments().isEmpty()) {
            for (BG0024AdditionalSupportingDocuments documents : invoice.getBG0024AdditionalSupportingDocuments()) {
                if (!documents.getBT0122SupportingDocumentReference().isEmpty()) {
                    AllegatiType allegatiExternal = new AllegatiType();
                    String documentReference = documents.getBT0122SupportingDocumentReference().get(0).getValue();

                    if (!documents.getBT0123SupportingDocumentDescription().isEmpty()) {
                        allegatiExternal.setDescrizioneAttachment(documents.getBT0123SupportingDocumentDescription().get(0).getValue());
                    }

                    if (!documents.getBT0124ExternalDocumentLocation().isEmpty()) {
                        allegatiExternal.setAttachment(documents.getBT0124ExternalDocumentLocation().get(0).getValue().getBytes());
                        allegatiExternal.setFormatoAttachment("csv");
                        allegatiExternal.setNomeAttachment(documentReference + ".link.txt");
                        fatturaElettronicaBody.getAllegati().add(allegatiExternal);
                    } else if (documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                        allegatiExternal.setAttachment(documentReference.getBytes());
                        allegatiExternal.setFormatoAttachment("csv");
                        allegatiExternal.setNomeAttachment("document-reference");
                        fatturaElettronicaBody.getAllegati().add(allegatiExternal);
                    }

                    if (!documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                        AllegatiType allegatiEmbedded = new AllegatiType();

                        if (!documents.getBT0123SupportingDocumentDescription().isEmpty()) {
                            allegatiEmbedded.setDescrizioneAttachment(documents.getBT0123SupportingDocumentDescription().get(0).getValue());
                        }

                        FileReference file = documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().get(0).getValue();
                        try {
                            allegatiEmbedded.setAttachment(FileUtils.readFileToByteArray(new File(file.getFilePath())));
                            allegatiEmbedded.setFormatoAttachment(new AttachmentUtil().getShortFileFormat(file.getMimeType()));
                            allegatiEmbedded.setNomeAttachment(String.format("%s-%s",documentReference,file.getFileName()));
                            fatturaElettronicaBody.getAllegati().add(allegatiEmbedded);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                            errors.add(ConversionIssue.newError(
                                    e,
                                    e.getMessage(),
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.INVALID,
                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                            ));
                        }
                    }
                }
            }
        }
    }
}
