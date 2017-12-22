package it.infocert.eigor.model.core;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.utils.ReflectionsReflections;
import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvoiceUtilsTest {


    private BG0000Invoice invoice;
    private InvoiceUtils sut;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        sut = new InvoiceUtils(new ReflectionsReflections("it.infocert.eigor"));
    }

    @Test
    public void ensurePathExistsShouldCreateAllCenElements() throws Exception {
        BG0000Invoice invoice = sut.ensurePathExists("/BG-4/BG-5", this.invoice);
        assertFalse("The invoice should have a BG-4 Seller",
                invoice.getBG0004Seller().isEmpty());

        BG0004Seller seller = invoice.getBG0004Seller(0);
        assertFalse("The BG-4 should have a BG-5 SellerPostalAddress",
                seller.getBG0005SellerPostalAddress().isEmpty());
    }

    @Test
    public void ensurePathExistsShouldNotCreateIfCreatingBTs() throws Exception {
        BG0000Invoice invoice = sut.ensurePathExists("/BG-4/BT-27", this.invoice);

        assertFalse(invoice.getBG0004Seller().isEmpty());
        assertTrue(invoice.getBG0004Seller(0).getBT0027SellerName().isEmpty());
    }

    @Test
    public void getChildrenAsListShouldReturnAllChildrenOfAParent() throws Exception {
        populateWithBt1(5);

        List<BTBG> children = sut.getChildrenAsList(invoice, "BT-1");

        assertNotNull(children);
        assertFalse(children.isEmpty());
        assertThat(children.size(), is(5));
    }

    @Test
    public void getChildrenAsListShouldReturnNullIfNoChildrenWithGivenNameExistsInParent() throws Exception {
        List<BTBG> children = sut.getChildrenAsList(invoice, "BT-27");
        assertNull(children);
    }

    @Test
    public void getBtBgByNameShouldReturnTheRightClassByString() throws Exception {
        assertBT1Class(sut.getBtBgByName("BT-1"));
    }

    @Test
    public void getBtBgByNameShouldReturnTheRightClassByBtBgName() throws Exception {
        assertBT1Class(sut.getBtBgByName(BtBgName.parse("BT-1")));
    }

    @Test
    public void getFirstChildShouldReturnTheFirstChild() throws Exception {
        populateWithBt1(3);
        BT0001InvoiceNumber firstChild = invoice.getBT0001InvoiceNumber(0);

        BTBG foundChild = sut.getFirstChild("/BT-1", invoice);

        assertNotNull(foundChild);
        assertEquals(firstChild, foundChild);
    }

    @Test
    public void addChild() throws Exception {
        BT0001InvoiceNumber invoiceNumber3 = new BT0001InvoiceNumber("3");
        populateWithBt1(2);

        boolean result = sut.addChild(invoice, invoiceNumber3);
        assertTrue(result);

        BT0001InvoiceNumber found3 = invoice.getBT0001InvoiceNumber(2);
        assertNotNull(found3);
        assertEquals(invoiceNumber3, found3);
    }

    @Test
    public void hasChildReturnsTrueIfPresent() throws Exception {
        populateWithBt1(1);
        assertTrue(sut.hasChild(invoice, "/BT-1"));
    }

    @Test
    public void hasChildReturnsFalseIfAbsent() throws Exception {
        assertFalse(sut.hasChild(invoice, "/BT-1"));
    }

    @Test
    public void getBtRecursivelyShouldReturnBTsFromPath() throws Exception {
        assertBtRecursively("/BG-25/BG-29/BT-146", 5, true);
    }

    @Test
    public void getBtRecursivelyShouldReturnBTsFromSteps() throws Exception {
       assertBtRecursively("/BG-25/BG-29/BT-146", 5, false);
    }

    private void assertBT1Class(Class<? extends BTBG> btBgByName) {
        assertEquals(BT0001InvoiceNumber.class, btBgByName);
    }

    private void assertBtRecursively(String path, int num, boolean byPath) {
        populateWithBT146(num);
        List<AbstractBT> bts;
        if (byPath) {
            bts = sut.getBtRecursively(invoice, path, Lists.<AbstractBT>newArrayList());
        } else {
            bts = sut.getBtRecursively(invoice, Lists.newArrayList((path.substring(1)).split("/")), Lists.<AbstractBT>newArrayList());
        }
        assertFalse(bts.isEmpty());
        assertThat(bts.size(), is(num));
    }


    private void populateWithBt1(int num) {
        List<BT0001InvoiceNumber> invoiceNumbers = invoice.getBT0001InvoiceNumber();
        for (int i = 0; i < num; i++) {
            invoiceNumbers.add(new BT0001InvoiceNumber(String.valueOf(i + 1)));
        }
    }

    private void populateWithBT146(int num) {
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        BG0029PriceDetails priceDetails = new BG0029PriceDetails();
        invoiceLine.getBG0029PriceDetails().add(priceDetails);
        invoice.getBG0025InvoiceLine().add(invoiceLine);

        for (int i = 0; i < num; i++) {
            priceDetails.getBT0146ItemNetPrice().add(new BT0146ItemNetPrice(2d));
        }
    }
}
