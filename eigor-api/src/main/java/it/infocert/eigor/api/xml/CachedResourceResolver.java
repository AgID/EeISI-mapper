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
 * Retrieve XSDs anc cache them to the file system for quicker later retrieval.
 * <p>
 * <pre>
 *     CachedResourceResolver cachedResourceResolver = new CachedResourceResolver(new File("cachefolder"));
 *     LoggingResourceResolver newResolver = new LoggingResourceResolver();
 *     newResolver.setWrappedResourceResolver(cachedResourceResolver);
 *     schemaFactoryToUse().setResourceResolver(newResolver);
 * </pre>
 * </p>
*/
public class CachedResourceResolver implements LSResourceResolver
{

    private final File folder;
    private Logger log = LoggerFactory.getLogger(CachedResourceResolver.class);

    /** Internal debug flag for console debugging */
    protected static final boolean DEBUG_RESOLVE = false;

    private LSResourceResolver m_aWrappedResourceResolver;

    public CachedResourceResolver(File folder)
    {
        if (folder == null) throw new IllegalArgumentException("Provided folder is null");
        if(!folder.exists() || !folder.isDirectory()) throw new IllegalArgumentException("Folder '"+folder.getAbsolutePath()+"' does not exist.");
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
