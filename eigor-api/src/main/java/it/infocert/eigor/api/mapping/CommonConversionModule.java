package it.infocert.eigor.api.mapping;

import com.google.common.collect.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    static List<Element> evaluateXpath(Document doc, String xPathExpression) {
        xPathExpression = localizeNames(xPathExpression);
        XPathExpression<Element> compiled = compile(xPathExpression, doc.getRootElement().getAdditionalNamespaces());
        return  compiled.evaluate(doc);
    }

    static void ensurePathExists(Document doc, String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] slices = path.split("/");
        Element currentNode = doc.getRootElement();
            for (int i = 1; i < slices.length; i++) {
            String s = slices[i];
            List<Element> list = currentNode.getChildren(s);
            if (list.size() == 0) {
                Element element = new Element(s);
                currentNode.addContent(element);
                currentNode = element;
            } else {
                currentNode = (Element) list.get(0);
            }
        }
    }

    private static XPathExpression<Element> compile(String xPathExpression, List<Namespace> namespaces){
        XPathFactory xpathfactory = XPathFactory.instance();

        return xpathfactory.compile(xPathExpression, Filters.element(), null, namespaces);
    }

    private static String localizeNames(String xpathExpression) {
        List<String> steps = Lists.newArrayList(xpathExpression.split("/"));
        if (steps.contains("")) {
            steps.remove("");
        }
        StringBuilder sb = new StringBuilder();
        for (String step : steps) {
            sb.append(String.format("/*[local-name()='%s']", step));
        }
        return sb.toString();
    }

}