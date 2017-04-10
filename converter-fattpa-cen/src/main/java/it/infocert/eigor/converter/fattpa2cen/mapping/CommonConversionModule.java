package it.infocert.eigor.converter.fattpa2cen.mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.xpath.*;
class CommonConversionModule {

    private static Logger LOGGER = LoggerFactory.getLogger(CommonConversionModule.class);
    
    static NodeList evaluateXpath(Document doc, String xPathExpression) {
        Object result = null;
        try {
            result = compile(xPathExpression).evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return (NodeList) result;
    }
    static Boolean hasNode(Document doc, String xPathExpression) {
        String booleanExpression = "boolean(" + xPathExpression + ")";
        Object result = null;
        try {
            result = compile(booleanExpression).evaluate(doc, XPathConstants.BOOLEAN);
        } catch (XPathExpressionException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return (Boolean) result;
    }
    private static XPathExpression compile(String xPathExpression) throws XPathExpressionException {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        return xpath.compile(xPathExpression);
    }
}