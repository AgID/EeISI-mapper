package it.infocert.eigor.api.xml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.ls.LSInput;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CachedResourceResolverTest {

    @Rule public TemporaryFolder tmp = new TemporaryFolder();

    @Test public void shouldSupportLookup4() throws IOException {
        String type = "http://www.w3.org/2001/XMLSchema";
        String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
        String publicId = null;
        String systemId = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd";
        String baseURI = null;
        checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
    }

    @Test public void shouldSupportLookup5() throws IOException {
        String type = "http://www.w3.org/2001/XMLSchema";
        String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
        String publicId = null;
        String systemId = "UBL-CommonBasicComponents-2.1.xsd";
        String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd";
        checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
    }

    @Test public void shouldUseCacheTheSecondTimeSoItShouldBeMuchMoreQuicker() throws IOException {

        String type = "http://www.w3.org/TR/REC-xml";
        String namespaceURI = null;
        String publicId = "-//W3C//DTD XMLSchema 200102//EN";
        String systemId = "http://www.w3.org/2001/XMLSchema.dtd";
        String baseUri = "http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd";

        File cacheFolder = checkThatCacheIsQuicker(type, namespaceURI, publicId, systemId, baseUri);
        String expectedContent = IOUtils.toString(getClass().getResourceAsStream("/expected-xmldsig-core-schema.xsd"), "UTF-8").replaceAll("[\t\r\n]", "");
        String actualContent = FileUtils.readFileToString(cacheFolder.listFiles()[0], "UTF-8").replaceAll("[\t\r\n]", "");
        assertThat(expectedContent, equalTo(actualContent));

    }

    private File checkThatNoCacheIsUsed(String type, String namespaceURI, String publicId, String systemId, String baseUri) throws IOException {
        // given
        File cacheFolder = tmp.newFolder();
        CachedResourceResolver sut = new CachedResourceResolver(cacheFolder);

        // when
        LSInput lsInput1 = sut.resolveResource(type, namespaceURI, publicId, systemId, baseUri);

        // then
        assertThat(lsInput1, Matchers.nullValue());
        assertThat(cacheFolder.listFiles().length, is(0));

        return cacheFolder;
    }

    private File checkThatCacheIsQuicker(String type, String namespaceURI, String publicId, String systemId, String baseUri) throws IOException {
        // given
        File cacheFolder = tmp.newFolder();
        CachedResourceResolver sut = new CachedResourceResolver(cacheFolder);

        // when
        long deltaWithoutCache = System.currentTimeMillis();
        LSInput lsInput1 = sut.resolveResource(type, namespaceURI, publicId, systemId, baseUri);
        deltaWithoutCache = System.currentTimeMillis() - deltaWithoutCache;

        // then
        assertThat(lsInput1, Matchers.notNullValue());
        assertThat(cacheFolder.listFiles().length, is(1));

        // when
        long deltaWithCache = System.currentTimeMillis();
        LSInput lsInput2 = sut.resolveResource(type, namespaceURI, publicId, systemId, baseUri);
        deltaWithCache = System.currentTimeMillis() - deltaWithCache;

        // then
        assertThat(lsInput2, Matchers.notNullValue());
        assertThat(cacheFolder.listFiles().length, is(1));
        assertThat("elapsed:" + deltaWithoutCache + "," + deltaWithCache, deltaWithCache, lessThan(deltaWithoutCache / 10));
        return cacheFolder;
    }

}