package it.infocert.eigor.converter.cen2ubl;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Parent;

public class XmlNamespaceApplier {

    private final String CBC = "cbc";
    private final String CAC = "cac";
    private final String CBC_VALUE;
    private final String CAC_VALUE;

    public XmlNamespaceApplier(final String cbcValue, String cacValue) {
        CBC_VALUE = cbcValue;
        CAC_VALUE = cacValue;
    }

    public Element apply(final Element element, final String prefix, final String value) {
        final Element cloned = element.clone();

        preserveParent(element, cloned);

        cloned.setNamespace(Namespace.getNamespace(prefix, value));
        return cloned;
    }

    private void preserveParent(Element element, Element cloned) {
        if (element.getParent() != null) {
            Parent parent = element.getParent();
            parent.addContent(cloned);
            parent.removeContent(element);
        }
    }

    public void applyBySideEffect(Element element, final String prefix, final String value) {
        element.setNamespace(Namespace.getNamespace(prefix, value));
    }


    public void applyUblNamespaces(Document doc) {
        Element rootElement = doc.getRootElement();

        for (Element element : rootElement.getChildren()) {
            applyUblRecursively(element);
        }

    }

    private void applyUblRecursively(Element element) {
        if (!element.getChildren().isEmpty()) {
            applyBySideEffect(element, CAC, CAC_VALUE);
            for (Element child : element.getChildren()) {
                applyUblRecursively(child);
            }
        } else {
            applyBySideEffect(element, CBC, CBC_VALUE);
        }
    }
}
