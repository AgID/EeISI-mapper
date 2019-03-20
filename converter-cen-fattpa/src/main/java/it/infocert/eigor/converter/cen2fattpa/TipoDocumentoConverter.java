package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter;
import it.infocert.eigor.fattpa.commons.models.*;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0003InvoiceTypeCode;

import java.util.List;

public class TipoDocumentoConverter implements CustomMapping<FatturaElettronicaType> {

    TypeConverter<Untdid1001InvoiceTypeCode, TipoDocumentoType> typeConverter = Untdid1001InvoiceTypeCodeToTipoDocumentoTypeConverter.newConverter();

    @Override
    public void map(BG0000Invoice cenInvoice, FatturaElettronicaType fatturaElettronicaType, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {


        List<BT0003InvoiceTypeCode> bt0003InvoiceTypeCode = cenInvoice.getBT0003InvoiceTypeCode();
        if(bt0003InvoiceTypeCode==null || bt0003InvoiceTypeCode.size() != 1) return;

        TipoDocumentoType tipoDocumentoType = null;
        try {
            tipoDocumentoType = typeConverter.convert(bt0003InvoiceTypeCode.get(0).getValue());
            if(tipoDocumentoType==null) return;
        } catch (ConversionFailedException e) {
            return;
        }


        FatturaElettronicaBodyType body = fatturaElettronicaType.getFatturaElettronicaBody().get(0);

        DatiGeneraliType dgt = body.getDatiGenerali();
        if(dgt == null) {
            dgt = new DatiGeneraliType();
            body.setDatiGenerali(dgt);
        }

        DatiGeneraliDocumentoType dgd = dgt.getDatiGeneraliDocumento();
        if(dgd == null) {
            dgd = new DatiGeneraliDocumentoType();
        }
        dgt.setDatiGeneraliDocumento(dgd);


        dgd.setTipoDocumento(tipoDocumentoType);


    }
}
