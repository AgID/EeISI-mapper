package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.AbstractBT;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FattPaAttachmentConverter {

    private final static Logger log = LoggerFactory.getLogger(FattPaAttachmentConverter.class);
    private final ConversionRegistry conversionRegistry;
    private final List<String> cenPaths;
    private final InvoiceUtils invoiceUtils;
    private final BG0000Invoice invoice;
    private final List<IConversionIssue> errors;

    private FattPaAttachmentConverter(ConversionRegistry conversionRegistry, List<String> cenPaths, InvoiceUtils invoiceUtils, BG0000Invoice invoice, List<IConversionIssue> errors) {
        this.conversionRegistry = conversionRegistry;
        this.cenPaths = cenPaths;
        this.invoiceUtils = invoiceUtils;
        this.invoice = invoice;
        this.errors = errors;
    }

    public String createAttachment() {
        StringBuilder sb = new StringBuilder();

        for (String path : cenPaths) {
            List<AbstractBT> foundBts = invoiceUtils.getBtRecursively(invoice, path, new ArrayList<AbstractBT>(0));
            for (AbstractBT bt : foundBts) {
                    Object value = bt.getValue();
                    sb.append(bt.denomination()).append(": ").append(value).append(System.lineSeparator());

            }
        }

        return sb.toString();
    }

    public static Builder builder(ConversionRegistry conversionRegistry, Reflections reflections, BG0000Invoice invoice, List<IConversionIssue> errors) {
        return new Builder(conversionRegistry, reflections, invoice, errors);
    }

    public static class Builder {

        private List<String> cenPaths;
        private final InvoiceUtils invoiceUtils;
        private final BG0000Invoice invoice;
        private final List<IConversionIssue> errors;
        private final ConversionRegistry conversionRegistry;

        private Builder(ConversionRegistry conversionRegistry, Reflections reflections, BG0000Invoice invoice, List<IConversionIssue> errors) {
            this.conversionRegistry = conversionRegistry;
            this.invoiceUtils = new InvoiceUtils(reflections);
            this.invoice = invoice;
            this.errors = errors;
        }

        public Builder path(String cenPath) {
            if (cenPaths == null) {
                cenPaths = new ArrayList<>(1);
            }
            this.cenPaths.add(cenPath);
            return this;
        }

        public Builder pathsList(List<String> paths) {
            cenPaths = paths;
            return this;
        }

        public FattPaAttachmentConverter build() {
            return new FattPaAttachmentConverter(conversionRegistry, cenPaths, invoiceUtils, invoice, errors);
        }

    }
}
