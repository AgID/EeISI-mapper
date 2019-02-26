package it.infocert.eigor.converter.cii2cen;

import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.util.Map;

class CiiXPathfactory {
    private XPathFactory xPFactory = XPathFactory.instance();
    private Namespace rsm = Namespace.getNamespace("rsm", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100");
    private Namespace ram = Namespace.getNamespace("ram", "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100");


    public <T> XPathExpression<T> compile(String expression, Filter<T> filter, Map<String, Object> variables) {
        return xPFactory.compile(expression, filter, variables, rsm, ram);
    }

    public <T> XPathExpression<T> compile(String expression, Filter<T> filter) {
        return xPFactory.compile(expression, filter, null, rsm, ram);
    }

}
