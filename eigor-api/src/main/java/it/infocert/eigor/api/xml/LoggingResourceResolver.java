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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import javax.annotation.Nullable;

/**
 * A logging only {@link LSResourceResolver} implementation.
 *
 * @author Philip Helger
 */
public class LoggingResourceResolver extends AbstractLSResourceResolver
{
    private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingResourceResolver.class);

    public LoggingResourceResolver()
    {}

    @Override
    @Nullable
    public LSInput mainResolveResource (@Nullable final String sType,
            @Nullable final String sNamespaceURI,
            @Nullable final String sPublicId,
            @Nullable final String sSystemId,
            @Nullable final String sBaseURI)
    {
        if (s_aLogger.isInfoEnabled ())
            s_aLogger.info ("mainResolveResource (Type=" + sType +
                    ", NamespaceURI=" +
                    sNamespaceURI +
                    ", PublicId=" +
                    sPublicId +
                    ", SystemId=" +
                    sSystemId +
                    ", BaseURI=" +
                    sBaseURI +
                    ")");
        return null;
    }
}
