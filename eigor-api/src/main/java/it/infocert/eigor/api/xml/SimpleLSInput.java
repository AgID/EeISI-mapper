/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infocert.eigor.api.xml;

import com.helger.commons.annotation.UnsupportedOperation;
import org.w3c.dom.ls.LSInput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.assertj.core.util.Preconditions.checkNotNull;

public class SimpleLSInput implements LSInput
{
    private String m_sEncoding;
    private String m_sPublicId;
    private String m_sSystemId;
    private boolean m_bCertifiedText;
    private String m_sBaseURI;
    private String m_sStringData;
    private final byte[] content;

    public SimpleLSInput(byte[] content) {
        this.content = checkNotNull( content );
    }

    @UnsupportedOperation
    public void setByteStream (final InputStream aByteStream)
    {
        throw new UnsupportedOperationException ();
    }

    @UnsupportedOperation
    public void setCharacterStream (final Reader aCharacterStream)
    {
        throw new UnsupportedOperationException ();
    }

    public void setBaseURI (@Nullable final String sBaseURI)
    {
        m_sBaseURI = sBaseURI;
    }

    @Nullable
    public String getBaseURI ()
    {
        return m_sBaseURI;
    }

    @Nonnull
    public InputStream getByteStream ()
    {
        return new ByteArrayInputStream(content);
    }


    public boolean getCertifiedText ()
    {
        return m_bCertifiedText;
    }

    public void setCertifiedText (final boolean bCertifiedText)
    {
        m_bCertifiedText = bCertifiedText;
    }

    @Nullable
    public Reader getCharacterStream ()
    {
        return new InputStreamReader(getByteStream());
    }


    @Nullable
    public String getEncoding ()
    {
        return m_sEncoding;
    }

    public void setEncoding (@Nullable final String sEncoding)
    {
        m_sEncoding = sEncoding;
    }

    @Nullable
    public String getPublicId ()
    {
        return m_sPublicId;
    }

    public void setPublicId (@Nullable final String sPublicId)
    {
        m_sPublicId = sPublicId;
    }

    @Nullable
    public String getStringData ()
    {
        return m_sStringData;
    }

    public void setStringData (@Nullable final String sStringData)
    {
        m_sStringData = sStringData;
    }

    @Nullable
    public String getSystemId ()
    {
        return m_sSystemId;
    }

    public void setSystemId (@Nullable final String sSystemId)
    {
        m_sSystemId = sSystemId;
    }

    @Override public String toString() {
        return "SimpleLSInput{" + "m_sEncoding='" + m_sEncoding + '\'' + ", m_sPublicId='" + m_sPublicId + '\'' + ", m_sSystemId='" + m_sSystemId + '\'' + ", m_bCertifiedText=" + m_bCertifiedText + ", m_sBaseURI='" + m_sBaseURI + '\'' + ", m_sStringData='"
                + m_sStringData + '\'' + ", content=" + (content!=null ? content.length + " bytes" : "empty") + '}';
    }
}
