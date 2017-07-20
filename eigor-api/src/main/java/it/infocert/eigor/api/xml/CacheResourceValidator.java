/**
 * Highly inspired by AbstractLSResourceResolver by Philip Helger (www.helger.com)
 */
package it.infocert.eigor.api.xml;

import com.helger.commons.annotation.Nonempty;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;

public class CacheResourceValidator implements LSResourceResolver
{

    private final File folder;
    private HashMap<CacheKey, LSInput> cache = new HashMap<>();
    private Logger log = LoggerFactory.getLogger(CacheResourceValidator.class);

    /** Internal debug flag for console debugging */
    protected static final boolean DEBUG_RESOLVE = false;

    private LSResourceResolver m_aWrappedResourceResolver;

    public CacheResourceValidator(File folder)
    {
        if(folder == null || !folder.exists() || !folder.isDirectory()) throw new IllegalArgumentException();
        this.folder = folder;
        File file = cacheFile();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
            cache = (HashMap<CacheKey, LSInput>) new ObjectInputStream(bais).readObject();
        }catch(Exception e){
            log.error("Unable to read cache from '{}'.", file.getAbsolutePath(), e);
        }
    }

    @Nullable
    public LSResourceResolver getWrappedResourceResolver ()
    {
        return m_aWrappedResourceResolver;
    }

    @Nonnull
    public CacheResourceValidator setWrappedResourceResolver (@Nullable final LSResourceResolver aWrappedResourceResolver)
    {
        m_aWrappedResourceResolver = aWrappedResourceResolver;
        return this;
    }

    @Nullable
    public final LSInput resolveResource (@Nonnull @Nonempty final String sType,
            @Nullable final String sNamespaceURI,
            @Nullable final String sPublicId,
            @Nullable final String sSystemId,
            @Nullable final String sBaseURI)
    {
        CacheKey cacheKey = new CacheKey(sType, sNamespaceURI, sPublicId, sSystemId, sBaseURI);
        final LSInput fromCache = cache.get(cacheKey);

        if (fromCache != null)
            return fromCache;

        // Pass to parent (if available)
        if (m_aWrappedResourceResolver != null) {
            LSInput lsInput = m_aWrappedResourceResolver.resolveResource(sType, sNamespaceURI, sPublicId, sSystemId, sBaseURI);

            cache.put(cacheKey, lsInput);

            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(cache);
                oos.flush();
                oos.close();
                FileUtils.writeByteArrayToFile(cacheFile(), baos.toByteArray()

                );
            }catch(Exception e){
                log.error("Failed saving the cache.", e);
            }

            return lsInput;
        }

        // Not found
        return null;
    }

    private File cacheFile() {
        return new File(folder, "cache.dat");
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
