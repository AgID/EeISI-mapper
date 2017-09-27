package it.infocert.eigor.converter.cen2ubl;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XmlNamespaceApplierTest {

    private final String testString = "test";
    private XmlNamespaceApplier sut;
    private Document doc;

    @Before
    public void setUp() throws Exception {
        sut = new XmlNamespaceApplier("cbc", "cac");
        doc = new Document();
    }

    @Test
    public void shouldApplyOneNsBySideEffect() throws Exception {
        Element test = new Element("Test");

        sut.applyBySideEffect(test, testString, testString);
        assertNamespace(test);
    }

    @Test
    public void shouldApplyOneNamespace() throws Exception {
        Element applied = sut.apply(new Element("Test"), testString, testString);
        assertNamespace(applied);
    }

    @Test
    public void shouldApplyUblNamespacesRecursively() throws Exception {
        final String cac = "cac";
        final String cbc = "cbc";

        Element root = new Element("root");
        root.addNamespaceDeclaration(Namespace.getNamespace(cac, cac));
        root.addNamespaceDeclaration(Namespace.getNamespace(cbc, cbc));

        Element firstEl = new Element("first");
        Element secondEl = new Element("second");


        doc.setRootElement(root.addContent(firstEl.addContent(secondEl)));


        sut.applyUblNamespaces(doc);


        Element first = doc.getRootElement().getChildren().get(0);

        Element second = first.getChildren().get(0);
        assertEquals(cac, first.getNamespacePrefix());
        assertEquals(cbc, second.getNamespacePrefix());
    }


    private void assertNamespace(Element element) {
        assertEquals(testString, element.getNamespace(testString).getURI());
    }
}