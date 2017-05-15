package it.infocert.eigor.converter.ubl2cen.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;

/**
 * Common conversion utilities module.
 */
class CommonConversionModule {

    private static Logger log = LoggerFactory.getLogger(CommonConversionModule.class);

    /**
     * Evaluate xpath and get node list.
     *
     * @param doc             the doc
     * @param xPathExpression the x path expression
     * @return the node list
     */
    static NodeList evaluateXpath(Document doc, String xPathExpression) {
        Object result = null;
        try {
            result = compile(xPathExpression).evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error(e.getMessage(), e);
        }
        return (NodeList) result;
    }

    /**
     * Check if a node exists.
     *
     * @param doc             the doc
     * @param xPathExpression the x path expression
     * @return True if node exists
     */
    static Boolean hasNode(Document doc, String xPathExpression) {
        String booleanExpression = "boolean(" + xPathExpression + ")";
        Object result = null;
        try {
            result = compile(booleanExpression).evaluate(doc, XPathConstants.BOOLEAN);
        } catch (XPathExpressionException e) {
            log.error(e.getMessage(), e);
        }
        return (Boolean) result;
    }

    private static XPathExpression compile(String xPathExpression) throws XPathExpressionException {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        return xpath.compile(xPathExpression);
    }

}