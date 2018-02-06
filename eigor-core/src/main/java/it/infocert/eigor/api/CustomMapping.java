package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import java.util.List;

public interface CustomMapping<Invoice> {

    void map(BG0000Invoice cenInvoice, Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation);
}

