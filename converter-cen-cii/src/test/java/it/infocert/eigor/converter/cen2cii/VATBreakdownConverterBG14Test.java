package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.infocert.eigor.converter.cen2cii.Cen2Cii.RAM_NS;
import static it.infocert.eigor.converter.cen2cii.Cen2Cii.RSM_NS;

public class VATBreakdownConverterBG14Test {

    @Test public void shouldMapBg14() throws IOException {

        // given

        // ...a cen invoice
        BG0014InvoicingPeriod bg14 = new BG0014InvoicingPeriod();
        bg14.getBT0073InvoicingPeriodStartDate().add( new BT0073InvoicingPeriodStartDate(new LocalDate(2001, 1, 31)) );
        bg14.getBT0074InvoicingPeriodEndDate().add( new BT0074InvoicingPeriodEndDate(new LocalDate(2007, 2, 28)) );

        BG0013DeliveryInformation bg13 = new BG0013DeliveryInformation();
        bg13.getBG0014InvoicingPeriod().add(bg14);

        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBG0013DeliveryInformation().add(bg13);

        Document doc = Cen2Cii.createDocumentWithCiiRootElement();
        ErrorCode.Location location = ErrorCode.Location.CII_OUT;

        new VATBreakdownConverter().map(invoice, doc, new ArrayList<IConversionIssue>(), location);

        Assert.assertNotNull(doc);
        Assert.assertNotNull(doc.getRootElement());

        Element billingSpecifiedPeriod = new Selectors()
                .child("SupplyChainTradeTransaction", RSM_NS)
                .child("ApplicableHeaderTradeSettlement", RAM_NS)
                .child("BillingSpecifiedPeriod", RAM_NS)
                .selectStartingFrom(doc.getRootElement());

        Assert.assertEquals( "<ram:BillingSpecifiedPeriod xmlns:ram=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100\"><ram:StartDateTime><udt:DateTimeString xmlns:udt=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100\" format=\"102\">2001-01-31</udt:DateTimeString></ram:StartDateTime><ram:EndDateTime><udt:DateTimeString xmlns:udt=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100\" format=\"102\">2007-02-28</udt:DateTimeString></ram:EndDateTime></ram:BillingSpecifiedPeriod>", elementToString(billingSpecifiedPeriod));


    }

    private String elementToString(Element billingSpecifiedPeriod) throws IOException {
        StringWriter out = new StringWriter();
        new XMLOutputter().output(billingSpecifiedPeriod, out);
        return out.toString();
    }

    static interface Selector {
        Element selectStartingFrom(Element e);
    }

    static class ChildSelector implements Selector {

        private final String cname;
        private final Namespace ns;
        private final int index;

        ChildSelector(String cname, Namespace ns, int index) {
            this.cname = checkNotNull( cname );
            this.ns = checkNotNull( ns );
            this.index = index;
        }

        ChildSelector(String cname, Namespace ns) {
            this(cname, ns, 0);
        }

        @Override public Element selectStartingFrom(Element e) {
            List<Element> children = e.getChildren(cname, ns);
            if(children==null) return null;

            Element element = null;
            try {
                element = children.get(index);
            } catch (IndexOutOfBoundsException e1) {
                String msg = String.format("Element %s has only %d children %s:%s, you asked the one at index %d.\nAll available children are %s",
                        e.getNamespacePrefix() + ":" + e.getName(), children.size(), ns.getPrefix(), cname, this.index, e.getChildren());
                throw new IndexOutOfBoundsException(msg);

            }
            return element;
        }

        @Override public String toString() {
            return "[Selecting #" + index + " child called " + ns.getPrefix() + ":" + cname + "]";
        }
    }

    static class Selectors implements Selector {

        private final List<Selector> selectors;

        public Selectors(Selector... selectors) {
            this.selectors = new ArrayList<>( Arrays.asList(selectors) );
        }

        public Selectors() {
            this.selectors = new ArrayList<>(  );
        }

        Selectors add(Selector s){
            selectors.add(s);
            return this;
        }

        public Selectors child(String crossIndustryInvoice, Namespace rsmNs) {
            return add(new ChildSelector(crossIndustryInvoice, rsmNs));
        }


        @Override public Element selectStartingFrom(Element e) {

            Selector last = null;
            for (Selector selector : selectors) {
                if(e == null) throw new NullPointerException("Selector " + last + " found nothing.");
                e = selector.selectStartingFrom(e);
                last = selector;
            }

            return e;
        }

    }

}