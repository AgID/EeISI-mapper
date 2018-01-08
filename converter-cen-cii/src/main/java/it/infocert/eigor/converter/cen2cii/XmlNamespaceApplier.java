package it.infocert.eigor.converter.cen2cii;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlNamespaceApplier {

    // we assume root element contains URI definitions for all namespaces used
    private Map<String, String> nsPrefixMap;

    public XmlNamespaceApplier() {
        this.nsPrefixMap = new HashMap<>();
        nsPrefixMap.put("ID", "ram");
        nsPrefixMap.put("TypeCode", "ram");
        nsPrefixMap.put("GuidelineSpecifiedDocumentContextParameter", "ram");
        nsPrefixMap.put("BusinessProcessSpecifiedDocumentContextParameter", "ram");
        nsPrefixMap.put("SpecifiedTradeSettlementHeaderMonetarySummation", "ram");
        nsPrefixMap.put("ApplicableHeaderTradeSettlement", "ram");
        nsPrefixMap.put("SpecifiedTradeSettlementPaymentMeans", "ram");
        nsPrefixMap.put("ExchangedDocument", "rsm");
        nsPrefixMap.put("ExchangedDocumentContext", "rsm");
        nsPrefixMap.put("SupplyChainTradeTransaction", "rsm");
    }

    public void applyCiiNamespaces(Document doc) {
        Element rootElement = doc.getRootElement();
        for (Element element : rootElement.getChildren()) {
            applyCiiRecursively(element, rootElement);
        }

    }

    private void applyNamespace(Element element, Element rootElement) {
        final String elementName = element.getName();
        if (nsPrefixMap.containsKey(elementName)) {
            String prefix = nsPrefixMap.get(elementName);
            element.setNamespace(rootElement.getNamespace(prefix));
        }
    }

    private void applyCiiRecursively(Element element, Element rootElement) {
        if (!element.getChildren().isEmpty()) {
            applyNamespace(element, rootElement);
            for (Element child : element.getChildren()) {
                applyCiiRecursively(child, rootElement);
            }
        } else {
            applyNamespace(element, rootElement);
        }
    }
}
