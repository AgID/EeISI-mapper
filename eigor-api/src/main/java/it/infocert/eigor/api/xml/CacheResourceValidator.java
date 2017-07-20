/**
 * Highly inspired by AbstractLSResourceResolver by Philip Helger (www.helger.com)
 */
package it.infocert.eigor.api.xml;

import com.helger.commons.annotation.Nonempty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Examples
 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:un:unece:uncefact:data:standard:QualifiedDataType:100,
 PublicId=null,
 SystemId=CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd,
 BaseURI=file:/C:/Users/danidemi/AppData/Local/Temp/eigor/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100,
 PublicId=null,
 SystemId=CrossIndustryInvoice_UnqualifiedDataType_100pD16B.xsd,
 BaseURI=file:/C:/Users/danidemi/AppData/Local/Temp/eigor/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100,
 PublicId=null,
 SystemId=CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd,
 BaseURI=file:/C:/Users/danidemi/AppData/Local/Temp/eigor/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2,
 PublicId=null,
 SystemId=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd,
 BaseURI=null

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2,
 PublicId=null,
 SystemId=UBL-CommonBasicComponents-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:QualifiedDataTypes-2,
 PublicId=null,
 SystemId=UBL-QualifiedDataTypes-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonBasicComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:UnqualifiedDataTypes-2,
 PublicId=null,
 SystemId=UBL-UnqualifiedDataTypes-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-QualifiedDataTypes-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2,
 PublicId=null,
 SystemId=CCTS_CCT_SchemaModule-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-UnqualifiedDataTypes-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2,
 PublicId=null,
 SystemId=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonExtensionComponents-2.1.xsd,
 BaseURI=null

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2,
 PublicId=null,
 SystemId=UBL-ExtensionContentDataType-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonExtensionComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:CommonSignatureComponents-2,
 PublicId=null,
 SystemId=UBL-CommonSignatureComponents-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-ExtensionContentDataType-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:SignatureAggregateComponents-2,
 PublicId=null,
 SystemId=UBL-SignatureAggregateComponents-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonSignatureComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=urn:oasis:names:specification:ubl:schema:xsd:SignatureBasicComponents-2,
 PublicId=null,
 SystemId=UBL-SignatureBasicComponents-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=http://www.w3.org/2000/09/xmldsig#,
 PublicId=null,
 SystemId=UBL-xmldsig-core-schema-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=http://uri.etsi.org/01903/v1.3.2#,
 PublicId=null,
 SystemId=UBL-XAdESv132-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=http://uri.etsi.org/01903/v1.4.1#,
 PublicId=null,
 SystemId=UBL-XAdESv141-2.1.xsd,
 BaseURI=http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd

 Type=http://www.w3.org/2001/XMLSchema,
 NamespaceURI=http://www.w3.org/2000/09/xmldsig#,
 PublicId=null,
 SystemId=http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd,
 BaseURI=null

 Type=http://www.w3.org/TR/REC-xml,
 NamespaceURI=null,
 PublicId=-//W3C//DTD XMLSchema 200102//EN,
 SystemId=http://www.w3.org/2001/XMLSchema.dtd,
 BaseURI=http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd

 Type=http://www.w3.org/TR/REC-xml,
 NamespaceURI=null,
 PublicId=datatypes,
 SystemId=datatypes.dtd,
 BaseURI=http://www.w3.org/2001/XMLSchema.dtd

 */
public class CacheResourceValidator implements LSResourceResolver
{

    private final File folder;
    private Logger log = LoggerFactory.getLogger(CacheResourceValidator.class);

    /** Internal debug flag for console debugging */
    protected static final boolean DEBUG_RESOLVE = false;

    private LSResourceResolver m_aWrappedResourceResolver;

    public CacheResourceValidator(File folder)
    {
        if(folder == null || !folder.exists() || !folder.isDirectory()) throw new IllegalArgumentException("Folder '"+folder.getAbsolutePath()+"' does not exist.");
        this.folder = folder;
    }

    @Nullable
    public final LSInput resolveResource (@Nonnull @Nonempty final String sType,
            @Nullable final String sNamespaceURI,
            @Nullable final String sPublicId,
            @Nullable final String sSystemId,
            @Nullable final String sBaseURI)
    {
        CacheKey cacheKey = new CacheKey(sType, sNamespaceURI, sPublicId, sSystemId, sBaseURI);

        // Try to download it.
        URL urlOfXsd = null;
        try {
            log.trace("Try to load publicId='{}' systemId='{}' baseUri='{}' from '{}'.", sPublicId, sSystemId, sBaseURI, sBaseURI);
            urlOfXsd = new URL(sBaseURI);
        } catch (MalformedURLException e) {
            try {
                log.trace("Try to load publicId='{}' systemId='{}' baseUri='{}' from '{}'.", sPublicId, sSystemId, sBaseURI, sSystemId);
                urlOfXsd = new URL(sSystemId);
            } catch (MalformedURLException e1) {
                try {
                    log.trace("Try to load publicId='{}' systemId='{}' baseUri='{}' from '{}'.", sPublicId, sSystemId, sBaseURI, sPublicId);
                    urlOfXsd = new URL(sPublicId);
                } catch (MalformedURLException e2) {
                }
            }
        }
        if(urlOfXsd == null) {
            log.warn("Unable to load publicId='{}' systemId='{}' baseUri='{}' from '{}'.", sPublicId, sSystemId, sBaseURI, sPublicId);
            return null;
        }
        if(urlOfXsd.getProtocol().toLowerCase().startsWith("file")){
            log.warn("PublicId='{}' systemId='{}' baseUri='{}' is read from file, no need to cache.", sPublicId, sSystemId, sBaseURI);
            return null;
        }

        LSInput result = null;
        File cacheFile = new File(folder, "cache" + cacheKey.hashCode());
        try {
            if (!cacheFile.exists()) {
                FileUtils.writeByteArrayToFile(cacheFile, IOUtils.toByteArray(urlOfXsd));
                log.trace("Resource '{}' locally cached to '{}'.", urlOfXsd, cacheFile.getAbsolutePath());
            }
            SimpleLSInput simpleLSInput = new SimpleLSInput( FileUtils.readFileToByteArray(cacheFile) );
            simpleLSInput.setBaseURI(cacheKey.getBaseURI());
            simpleLSInput.setEncoding("UTF8");
            simpleLSInput.setSystemId(cacheKey.getSystemId());
            simpleLSInput.setPublicId(cacheKey.getPublicId());
            result = simpleLSInput;
            log.trace("Resource publicId='{}' systemId='{}' baseUri='{}' found locally in '{}'.", sPublicId, sSystemId, sBaseURI, cacheFile.getAbsolutePath());

        } catch (IOException e) {
            log.warn("Unable to load local copy of publicId='{}' systemId='{}' baseUri='{}'.", sPublicId, sSystemId, sBaseURI, e);
            result = null;
        }

        return result;

    }

    static class CacheKey implements Serializable {
        final String sType;
        final String sNamespaceURI;
        final String sPublicId;
        final String sSystemId;
        final String sBaseURI;

        CacheKey(String sNamespaceURI){
            this(null, null, null, null, null);
        }

        CacheKey(String sType, String sNamespaceURI, String sPublicId, String sSystemId, String sBaseURI) {
            this.sType = sType;
            this.sNamespaceURI = sNamespaceURI;
            this.sPublicId = sPublicId;
            this.sSystemId = sSystemId;
            this.sBaseURI = sBaseURI;
        }

        public String getType() {
            return sType;
        }

        public String getNamespaceURI() {
            return sNamespaceURI;
        }

        public String getPublicId() {
            return sPublicId;
        }

        public String getSystemId() {
            return sSystemId;
        }

        public String getBaseURI() {
            return sBaseURI;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            CacheKey cacheKey = (CacheKey) o;

            if (sType != null ? !sType.equals(cacheKey.sType) : cacheKey.sType != null)
                return false;
            if (sNamespaceURI != null ? !sNamespaceURI.equals(cacheKey.sNamespaceURI) : cacheKey.sNamespaceURI != null)
                return false;
            if (sPublicId != null ? !sPublicId.equals(cacheKey.sPublicId) : cacheKey.sPublicId != null)
                return false;
            if (sSystemId != null ? !sSystemId.equals(cacheKey.sSystemId) : cacheKey.sSystemId != null)
                return false;
            return sBaseURI != null ? sBaseURI.equals(cacheKey.sBaseURI) : cacheKey.sBaseURI == null;
        }

        @Override public int hashCode() {
            int result = sType != null ? sType.hashCode() : 0;
            result = 31 * result + (sNamespaceURI != null ? sNamespaceURI.hashCode() : 0);
            result = 31 * result + (sPublicId != null ? sPublicId.hashCode() : 0);
            result = 31 * result + (sSystemId != null ? sSystemId.hashCode() : 0);
            result = 31 * result + (sBaseURI != null ? sBaseURI.hashCode() : 0);
            return result;
        }
    }


}
