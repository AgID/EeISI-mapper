package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Additional Supporting Documents Custom Converter
 */
public class AdditionalSupportingDocumentsConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0024(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0024AdditionalSupportingDocuments bg0024;


        TypeConverter<Element, FileReference> strToBinConverter = AttachmentToFileReferenceConverter.newConverter(DefaultEigorConfigurationLoader.configuration(), ErrorCode.Location.FATTPA_IN);

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");
        Element fatturaElettronicaHeader = rootElement.getChild("FatturaElettronicaHeader");

        if (fatturaElettronicaBody != null) {
            List<Element> allegati = fatturaElettronicaBody.getChildren();
            for (Element allegato : allegati) {
                if (allegato.getName().equals("Allegati")) {
                    bg0024 = new BG0024AdditionalSupportingDocuments();
                    Element nomeAttachment = allegato.getChild("NomeAttachment");
                    if (nomeAttachment != null) {
                        BT0122SupportingDocumentReference supportingDocumentReference = new BT0122SupportingDocumentReference(nomeAttachment.getText());
                        bg0024.getBT0122SupportingDocumentReference().add(supportingDocumentReference);
                    }
                    Element descrizioneAttachment = allegato.getChild("DescrizioneAttachment");
                    if (descrizioneAttachment != null) {
                        BT0123SupportingDocumentDescription supportingDocumentDescription = new BT0123SupportingDocumentDescription(descrizioneAttachment.getText());
                        bg0024.getBT0123SupportingDocumentDescription().add(supportingDocumentDescription);
                    }
                    Element attachment = allegato.getChild("Attachment");
                    if (attachment != null) {
                        try {
                            Element formatoAttachment = allegato.getChild("FormatoAttachment");
                            if (formatoAttachment != null) {
                                attachment.setAttribute("mimeCode", getFullMimeNameFromShortFormat(formatoAttachment.getText()));
                            }
                            if (nomeAttachment != null) {
                                attachment.setAttribute("filename", nomeAttachment.getText());
                            }
                            BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                    new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(attachment));
                            bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                        } catch (IllegalArgumentException | ConversionFailedException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(ErrorCode.Location.FATTPA_IN)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, attachment.toString())
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    Element terzoIntermediarioOSoggettoEmittente = fatturaElettronicaHeader.getChild("TerzoIntermediarioOSoggettoEmittente");
                    if(terzoIntermediarioOSoggettoEmittente != null){
                        try {
                            Element datiAnagrafici = terzoIntermediarioOSoggettoEmittente.getChild("DatiAnagrafici");
                            if (datiAnagrafici != null) {
                                Element idFiscaleIVA = datiAnagrafici.getChild("IdFiscaleIVA");
                                if(idFiscaleIVA != null){
                                    Element idPaese = idFiscaleIVA.getChild("IdPaese");
                                    if(idPaese != null){
                                        idPaese.setAttribute("mimeCode", "text/csv");
                                        idPaese.setAttribute("filename", "IdPaese");
                                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                                new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(idPaese));
                                        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                    }
                                    Element idCodice = idFiscaleIVA.getChild("IdCodice");
                                    if(idCodice != null){
                                        idCodice.setAttribute("mimeCode", "text/csv");
                                        idCodice.setAttribute("filename", "IdCodice");
                                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                                new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(idCodice));
                                        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                    }
                                }
                                Element anagrafica = datiAnagrafici.getChild("Anagrafica");
                                if(anagrafica != null){
                                    Element denominazione = anagrafica.getChild("Denominazione");
                                    if(denominazione != null){
                                        denominazione.setAttribute("mimeCode", "text/csv");
                                        denominazione.setAttribute("filename", "Denominazione");
                                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                                new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(denominazione));
                                        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                    }
                                    Element titolo = anagrafica.getChild("Titolo");
                                    if(titolo != null){
                                        titolo.setAttribute("mimeCode", "text/csv");
                                        titolo.setAttribute("filename", "Titolo");
                                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                                new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(titolo));
                                        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                    }
                                    Element codEORI = anagrafica.getChild("CodEORI");
                                    if(codEORI != null){
                                        codEORI.setAttribute("mimeCode", "text/csv");
                                        codEORI.setAttribute("filename", "CodEORI");
                                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                                new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(codEORI));
                                        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                    }
                                }
                                Element codiceFiscale = datiAnagrafici.getChild("CodiceFiscale");
                                if(codiceFiscale != null){
                                    codiceFiscale.setAttribute("mimeCode", "text/csv");
                                    codiceFiscale.setAttribute("filename", "CodiceFiscale");
                                    BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                            new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(codiceFiscale));
                                    bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                }
                            }
                        } catch (IllegalArgumentException | ConversionFailedException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(ErrorCode.Location.FATTPA_IN)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, terzoIntermediarioOSoggettoEmittente.toString())
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    Element soggettoEmittente = fatturaElettronicaHeader.getChild("SoggettoEmittente");
                    if(terzoIntermediarioOSoggettoEmittente != null){
                        try {
                            soggettoEmittente.setAttribute("mimeCode", "text/csv");
                            soggettoEmittente.setAttribute("filename", "SoggettoEmittente");
                            BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                    new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(soggettoEmittente));
                            bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                        } catch (IllegalArgumentException | ConversionFailedException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(ErrorCode.Location.FATTPA_IN)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, soggettoEmittente.toString())
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
                    if(datiGenerali != null) {
                        Element datiContratto = datiGenerali.getChild("DatiContratto");
                        if (datiContratto != null) {
                            try {
                                Element codiceCUP = datiContratto.getChild("CodiceCUP");
                                if (codiceCUP != null) {
                                    codiceCUP.setAttribute("mimeCode", "text/csv");
                                    codiceCUP.setAttribute("filename", "CodiceCUP");
                                    BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                            new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(codiceCUP));
                                    bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                }
                                Element codiceCIG = datiContratto.getChild("CodiceCIG");
                                if (codiceCIG != null) {
                                    codiceCIG.setAttribute("mimeCode", "text/csv");
                                    codiceCIG.setAttribute("filename", "CodiceCIG");
                                    BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                            new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(codiceCIG));
                                    bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                                }
                            } catch (IllegalArgumentException | ConversionFailedException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(
                                        e,
                                        ErrorMessage.builder()
                                                .message(e.getMessage())
                                                .location(ErrorCode.Location.FATTPA_IN)
                                                .action(ErrorCode.Action.HARDCODED_MAP)
                                                .error(ErrorCode.Error.ILLEGAL_VALUE)
                                                .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                                .addParam(ErrorMessage.OFFENDINGITEM_PARAM, datiContratto.toString())
                                                .build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                    }
                    Element datiVeicoli = fatturaElettronicaBody.getChild("DatiVeicoli");
                    if(datiVeicoli != null){
                        try {
                            Element data = datiVeicoli.getChild("Data");
                            if(data != null){
                                data.setAttribute("mimeCode", "text/csv");
                                data.setAttribute("filename", "Data");
                                BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                        new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(data));
                                bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                            }
                            Element totalePercorso = datiVeicoli.getChild("TotalePercorso");
                            if(totalePercorso != null){
                                totalePercorso.setAttribute("mimeCode", "text/csv");
                                totalePercorso.setAttribute("filename", "TotalePercorso");
                                BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                                        new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(totalePercorso));
                                bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                            }
                        } catch (IllegalArgumentException | ConversionFailedException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(ErrorCode.Location.FATTPA_IN)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, datiVeicoli.toString())
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);
                }
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0024(document, cenInvoice, errors);
    }

    private String getFullMimeNameFromShortFormat(String shortFormat) {
        if (shortFormat != null) {
            switch (shortFormat.toLowerCase()) {
                case "pdf":
                    return "application/pdf";
                case "ods":
                    return "application/vnd.oasis.opendocument.spreadsheet";
                case "xlsx":
                    return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                case "png":
                    return "image/png";
                case "jpeg":
                    return "image/jpeg";
                case "csv":
                    return "text/csv";
            }
        }
        return null;
    }
}
