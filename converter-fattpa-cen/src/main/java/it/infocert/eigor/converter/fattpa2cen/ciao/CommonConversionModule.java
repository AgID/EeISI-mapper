package it.infocert.eigor.converter.fattpa2cen.ciao;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.xpath.*;
class CommonConversionModule {
    static NodeList evaluateXpath(Document doc, String xPathExpression) {
        Object result = null;
        try {
            result = compile(xPathExpression).evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return (NodeList) result;
    }
    static Boolean hasNode(Document doc, String xPathExpression) {
        String booleanExpression = "boolean(" + xPathExpression + ")";
        Object result = null;
        try {
            result = compile(booleanExpression).evaluate(doc, XPathConstants.BOOLEAN);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return (Boolean) result;
    }
    private static XPathExpression compile(String xPathExpression) throws XPathExpressionException {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        return xpath.compile(xPathExpression);
    }
}