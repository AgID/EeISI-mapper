package it.infocert.eigor.converter.cii2cen;

import org.jdom2.Element;
import org.jdom2.Namespace;
import java.util.ArrayList;
import java.util.List;

/**
 * The Custom Converter
 */
public class CustomConverterUtils {

    protected Element findNamespaceChild(Element parent, List<Namespace> namespacesInScope, String childName) {
        for (Namespace namespace : namespacesInScope) {
            Element child = parent.getChild(childName, namespace);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    protected List<Element> findNamespaceChildren(Element parent, List<Namespace> namespacesInScope, String childName) {
        for (Namespace namespace : namespacesInScope) {
            List<Element> children = parent.getChildren(childName, namespace);
            if (!children.isEmpty()) {
                return children;
            }
        }
        return new ArrayList<>();
    }

}
