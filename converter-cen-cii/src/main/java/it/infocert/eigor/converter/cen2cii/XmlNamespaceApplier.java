package it.infocert.eigor.converter.cen2cii;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Parent;

public class XmlNamespaceApplier {

    private final String RAM = "ram";
    private final String RSN = "rsm";
    private final String RAM_VALUE;
    private final String RSM_VALUE;

    public XmlNamespaceApplier(final String ramValue, String rsmValue) {
        RAM_VALUE = ramValue;
        RSM_VALUE = rsmValue;
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


    public void applyCiiNamespaces(Document doc) {
        Element rootElement = doc.getRootElement();

        for (Element element : rootElement.getChildren()) {
            applyCiiRecursively(element);
        }

    }

    private void applyCiiRecursively(Element element) {
        if (!element.getChildren().isEmpty()) {
            applyBySideEffect(element, RSN, RSM_VALUE);
            for (Element child : element.getChildren()) {
                applyCiiRecursively(child);
            }
        } else {
            applyBySideEffect(element, RAM, RAM_VALUE);
        }
    }
}
