package it.infocert.eigor.schematron;

import com.google.common.base.Preconditions;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.state.EValidity;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.svrl.SVRLReader;
import org.oclc.purl.dsdl.svrl.ActivePattern;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.FiredRule;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * A fixed version of an {@link ISchematronResource}.
 */
public class FixedSchematronResource implements ISchematronResource {

    private final ISchematronResource delegateSchematronResource;
    private static final Logger log = LoggerFactory.getLogger(FixedSchematronResource.class);

    public FixedSchematronResource(ISchematronResource originalSchematronResource) {
        this.delegateSchematronResource = Preconditions.checkNotNull( originalSchematronResource );
    }

    @Override
    @Nonnull
    public IReadableResource getResource() {
        return delegateSchematronResource.getResource();
    }

    @Override
    public boolean isValidSchematron() {
        return delegateSchematronResource.isValidSchematron();
    }

    @Override
    @Nonnull
    public EValidity getSchematronValidity(@Nonnull IHasInputStream aXMLResource) throws Exception {
        return delegateSchematronResource.getSchematronValidity(aXMLResource);
    }

    @Override
    @Nonnull
    public EValidity getSchematronValidity(@Nonnull Source aXMLSource) throws Exception {
        return delegateSchematronResource.getSchematronValidity(aXMLSource);
    }

    @Override
    @Nullable
    public Document applySchematronValidation(@Nonnull IHasInputStream aXMLResource) throws Exception {
        return delegateSchematronResource.applySchematronValidation(aXMLResource);
    }

    @Override
    @Nullable
    public Document applySchematronValidation(@Nonnull Source aXMLSource) throws Exception {
        return delegateSchematronResource.applySchematronValidation(aXMLSource);
    }

    @Override
    @Nullable
    public SchematronOutputType applySchematronValidationToSVRL(@Nonnull IHasInputStream xml) throws Exception {
        return getSchematronOutputType(delegateSchematronResource.applySchematronValidation(xml));
    }



    @Override
    @Nullable
    public SchematronOutputType applySchematronValidationToSVRL(@Nonnull Source xml) throws Exception {
        return getSchematronOutputType(delegateSchematronResource.applySchematronValidation(xml));
    }

    @Override
    @Nonnull
    public String getID() {
        return delegateSchematronResource.getID();
    }



    private SchematronOutputType getSchematronOutputType(Document document) {

        if(log.isTraceEnabled()){
            log.trace( "SVRL before replacing IDs:\n{}", prettyPrintDocument(document) );
        }

        visitAllIdAttributesAndPrefixThemWithANumber(document);

        SchematronOutputType output = SVRLReader.readXML(document);

        if(log.isTraceEnabled()){
            log.trace( "SVRL after replacing IDs:\n{}", prettyPrintDocument(document) );
        }


        List<Object> activePatternAndFiredRuleAndFailedAssert = output.getActivePatternAndFiredRuleAndFailedAssert();
        for (Object item : activePatternAndFiredRuleAndFailedAssert) {
            if(item instanceof FiredRule) {
                FiredRule itemWithId = (FiredRule) item;
                itemWithId.setId(deNormalizeId(itemWithId.getId()));
            }else if(item instanceof FailedAssert) {
                FailedAssert itemWithId = (FailedAssert) item;
                itemWithId.setId(deNormalizeId(itemWithId.getId()));
            }else if(item instanceof ActivePattern) {
                ActivePattern itemWithId = (ActivePattern) item;
                itemWithId.setId(deNormalizeId(itemWithId.getId()));
            }else{
                throw new IllegalStateException("Unsupported " + item);
            }
        }

        return output;
    }

    private String deNormalizeId(String id) {
        if(id!=null) {
            id = id.substring(id.indexOf('-') + 1);
        }
        return id;
    }

    private void visitAllIdAttributesAndPrefixThemWithANumber(Node element) {
        visitAllIdAttributesAndPrefixThemWithANumber(element, new SequenceOfLongs());
    }

    private void visitAllIdAttributesAndPrefixThemWithANumber(Node element, SequenceOfLongs ids) {
        Preconditions.checkNotNull(element);

        if(element.getNodeType() == Node.ELEMENT_NODE){
            Element theE = (Element) element;
            if(theE.hasAttribute("id")){
                String originalId = theE.getAttribute("id");
                theE.setAttribute("id", "id" + ids.next() + "-" + originalId);
            }
        }

        NodeList childNodes = element.getChildNodes();
        for(int i=0; i<childNodes.getLength(); i++){
            Node childNode = childNodes.item(i);
            if(childNode!=null) visitAllIdAttributesAndPrefixThemWithANumber(childNode, ids);
        }
    }

    private static String prettyPrintDocument(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream out = new BufferedOutputStream(baos);
            transformer.transform(new DOMSource(doc),
                    new StreamResult(new OutputStreamWriter(out, "UTF-8")));
            out.flush();
            return new String(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class SequenceOfLongs implements Iterator<Long> {

        private long next = 0;

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Long next() {
            return next++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
