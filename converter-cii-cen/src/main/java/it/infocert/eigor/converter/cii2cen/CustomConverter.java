package it.infocert.eigor.converter.cii2cen;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marco Basilico on 20/07/2017.
 */
public class CustomConverter extends Cii2Cen {

    protected static Logger log = null;
    protected ConversionRegistry conversionRegistry;
    protected InvoiceUtils invoiceUtils;

    public CustomConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, new PropertiesBackedConfiguration());
        this.invoiceUtils = new InvoiceUtils(reflections);
        this.conversionRegistry = conversionRegistry;
        log = LoggerFactory.getLogger(CustomConverter.class);
    }

    //BT0017
    public ConversionResult<BG0000Invoice> toBT0017(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {
        String xPath1 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/AdditionalReferencedDocument/IssuerAssignedID";
        String xPath2 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/AdditionalReferencedDocument/TypeCode";

        String xPath1Text = "";
        String xPath2Text = "";
        String xPathTextTot = "";

        //Utilizzo della classe CommonConversionModule
        List<Element> xPath1elementList = CommonConversionModule.evaluateXpath(document, xPath1);
        List<Element> xPath2elementList = CommonConversionModule.evaluateXpath(document, xPath2);

        if (!xPath2elementList.isEmpty() && !xPath1elementList.isEmpty()){
            xPath2Text = xPath2elementList.get(0).getText();

            if(xPath2Text.equals("50")) {
                xPath1Text = xPath1elementList.get(0).getText();

                xPathTextTot = xPath1Text + " " + xPath2Text;

                Object assignedAssignedID = transformer("/BT0017", invoice, xPathTextTot, errors);
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    //BT0018
    public ConversionResult<BG0000Invoice> toBT0018(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {
        String xPath1 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/AdditionalReferencedDocument/IssuerAssignedID";
        String xPath2 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/AdditionalReferencedDocument/TypeCode";
        String xPath3 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/AdditionalReferencedDocument/ReferenceTypeCode";

        String xPath1Text = "";
        String xPath2Text = "";
        String xPath3Text = "";
        String xPathTextTot = "";

        //Utilizzo della classe CommonConversionModule
        List<Element> xPath1elementList = CommonConversionModule.evaluateXpath(document, xPath1);
        List<Element> xPath2elementList = CommonConversionModule.evaluateXpath(document, xPath2);
        List<Element> xPath3elementList = CommonConversionModule.evaluateXpath(document, xPath3);
        if (!xPath2elementList.isEmpty() && !xPath1elementList.isEmpty()){
            xPath2Text = xPath2elementList.get(0).getText();

            if(xPath2Text.equals("130")) {
                xPath1Text = xPath1elementList.get(0).getText();

                xPathTextTot = xPath1Text + " " + xPath2Text;

                if (!xPath3elementList.isEmpty()){
                    xPath3Text = xPath3elementList.get(0).getText();
                    xPathTextTot += " " + xPath3Text;
                }
                Object assignedAssignedID = transformer("/BT0018", invoice, xPathTextTot, errors);

                return new ConversionResult<>(errors, invoice);
            }
        }
        if (!xPath3elementList.isEmpty()){
            xPath3Text = xPath3elementList.get(0).getText();
            Object assignedAssignedID = transformer("/BT0018", invoice, xPathTextTot, errors);
        }

        return new ConversionResult<>(errors, invoice);
    }



    //CONVERTER GENERALE
//    PrecedingInvoiceReferenceConverter bg0003 = new PrecedingInvoiceReferenceConverter(new Reflections("it.infocert"), conversionRegistry);
    public ConversionResult<BG0000Invoice> convert(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {
        PrecedingInvoiceReferenceConverter bg0003 = new PrecedingInvoiceReferenceConverter(new Reflections("it.infocert"), conversionRegistry);
        InvoiceNoteConverter bg0001 = new InvoiceNoteConverter(new Reflections("it.infocert"), conversionRegistry);

        bg0003.toBG0003(document, invoice, errors);
        bg0001.toBG0001(document, invoice, errors);

        return new ConversionResult<>(errors, invoice);
    }


    /*
    TRANSFORMER
     */
    public Object transformer(String cenPath, BG0000Invoice invoice, final String xPathText, final List<IConversionIssue> errors) {
        final Object[] constructorParam = new Object[]{null};

        // find the parent BG
        String bgPath = cenPath.substring(0, cenPath.lastIndexOf("/"));
        invoiceUtils.ensurePathExists(cenPath, invoice);
        BTBG bg;
        if (cenPath.startsWith("/BT")) {
            bg = invoice;
        } else {
            bg = invoiceUtils.getFirstChild(bgPath, invoice);
        }
        log.trace(cenPath + " has BG parent: " + bg);

        // create BT element
        String btName = cenPath.substring(cenPath.lastIndexOf("/") + 1);
        Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
        if(btClass==null) {
            throw new RuntimeException("Unable to find BT with name '" + btName + "'");
        }

        Constructor<?>[] constructors = btClass.getConstructors();
        final ArrayList<BTBG> bt = new ArrayList<>(1);
        com.amoerie.jstreams.functions.Consumer<Constructor<?>> k = new com.amoerie.jstreams.functions.Consumer<Constructor<?>>() {
            @Override
            public void consume(final Constructor<?> constructor) {
                try {
                    if (constructor.getParameterTypes().length == 0) {
                        bt.add((BTBG) constructor.newInstance());
                    } else {
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        List<Class<?>> classes = Arrays.asList(parameterTypes);
                        Stream<Class<?>> classes1 = Stream.create(classes);

                        classes1.forEach(new Consumer<Class<?>>() {
                            @Override public void consume(Class<?> paramType) {

                                try {
                                    constructorParam[0] = conversionRegistry.convert(String.class, paramType, xPathText);
                                    try {
                                        bt.add((BTBG) constructor.newInstance(constructorParam[0]));
                                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                        log.error(e.getMessage(), e);
                                        errors.add(ConversionIssue.newError(e));
                                    }
                                } catch (IllegalArgumentException e) {
                                    log.error("There is no converter registered for: " + paramType, e);
                                    errors.add(ConversionIssue.newError(e));
                                }
                            }
                        });

                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                    errors.add(ConversionIssue.newError(e));
                }
            }
        };
        Stream.create(Arrays.asList(constructors)).forEach(k);

        log.trace(cenPath + " - bt element created: " + bt);
        System.out.println(cenPath + " - bt element created: " + bt);

        // add BT element to BG parent
        if (!bt.isEmpty()) {
            try {
                invoiceUtils.addChild(bg, bt.get(0));
            } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                    errors.add(ConversionIssue.newError(e));
            }
        }
        return constructorParam[0];
    }

}
