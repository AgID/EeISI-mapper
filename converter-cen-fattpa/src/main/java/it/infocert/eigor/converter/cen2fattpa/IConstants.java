package it.infocert.eigor.converter.cen2fattpa;

public interface IConstants {
    String CONVERTER_SUPPORT = "cenfattpa";
    String SUPPORTED_FORMATS = "cenfattpa";
    String SAMPLE_INVOICE = "Esempio fattura trasformata da CEN a XML-PA";
    String INVOICE_LEVEL_DISCOUNT_DESCRIPTION = "Invoice level discount";
    String DISCOUNT_UNIT = "EA";
    String CORRECTION_DESCRIPTION = "CORRECTION";
    String CORRECTION_UNIT = "EA";
    String ITEM_BASE_QTY = "Base Qty.";
    String ITEM_BASE_PRICE = "Base Unit";
    String LINE_LEVEL_DISCOUNT_DESCRIPTION = "Line level discount";
    String LINE_LEVEL_SURCHARGE_DESCRIPTION = "Line level surcharge";
    String ERROR_XML_GENERATION = "Error generating XML";
    String ERROR_XML_VALIDATION_FAILED = "XSD validation failed!";
    String ERROR_BUYER_INFORMATION = "Buyer information error.";
    String ERROR_SELLER_INFORMATION = "Seller information error";
    String ERROR_TRANSMISSION_INFORMATION = "Dati Transmisioni error";
    String ERROR_PAYMENT_INFORMATION = "Payment information error";
    String ERROR_GENERAL_INFORMATION = "General invoice information error";
    String ERROR_INVOICE_LEVEL_ALLOWANCES = "Invoice level allowances error";
    String ERROR_TOTAL_AMOUNT_CORRECTION = "Invoice level allowances correction line error. Missing total amount without VAT?";
    String ERROR_BASE_QUANTITY_TRANSFORM = "Error transforming base unit/qty for sales line.";
    String ERROR_LINE_PROCESSING = "Error processing invoice line";
}
