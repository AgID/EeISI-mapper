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

    @Test
    public void shouldSupportLookup1() throws IOException

    {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:un:unece:uncefact:data:standard:QualifiedDataType:100";
            String publicId = null;
            String systemId = "CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd";
            String baseURI = "file:/C:/Users/danidemi/AppData/Local/Temp/eigor/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd";
            checkThatNoCacheIsUsed(type, samespaceURI, publicId, systemId, baseURI);

        }

    @Test
    public void shouldSupportLookup2() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100";
            String publicId = null;
            String systemId = "CrossIndustryInvoice_UnqualifiedDataType_100pD16B.xsd";
            String baseURI = "file:/C:/Users/danidemi/AppData/Local/Temp/eigor/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup3() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100";
            String publicId = null;
            String systemId = "CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd";
            String baseURI = "file:/C:/Users/danidemi/AppData/Local/Temp/eigor/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd";
            checkThatNoCacheIsUsed(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup4() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
            String publicId = null;
            String systemId = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd";
            String baseURI = null;
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup5() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
            String publicId = null;
            String systemId = "UBL-CommonBasicComponents-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup6() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDataTypes-2";
            String publicId = null;
            String systemId = "UBL-QualifiedDataTypes-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonBasicComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup7() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:UnqualifiedDataTypes-2";
            String publicId = null;
            String systemId = "UBL-UnqualifiedDataTypes-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-QualifiedDataTypes-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup8() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2";
            String publicId = null;
            String systemId = "CCTS_CCT_SchemaModule-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-UnqualifiedDataTypes-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup9() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
            String publicId = null;
            String systemId = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonExtensionComponents-2.1.xsd";
            String baseURI = null;
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup10() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
            String publicId = null;
            String systemId = "UBL-ExtensionContentDataType-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonExtensionComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup11() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonSignatureComponents-2";
            String publicId = null;
            String systemId = "UBL-CommonSignatureComponents-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-ExtensionContentDataType-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup12() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:SignatureAggregateComponents-2";
            String publicId = null;
            String systemId = "UBL-SignatureAggregateComponents-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-CommonSignatureComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);

        }

    @Test
    public void shouldSupportLookup13() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:SignatureBasicComponents-2";
            String publicId = null;
            String systemId = "UBL-SignatureBasicComponents-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup14() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "http://www.w3.org/2000/09/xmldsig#";
            String publicId = null;
            String systemId = "UBL-xmldsig-core-schema-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup15() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "http://uri.etsi.org/01903/v1.3.2#";
            String publicId = null;
            String systemId = "UBL-XAdESv132-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup16() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "http://uri.etsi.org/01903/v1.4.1#";
            String publicId = null;
            String systemId = "UBL-XAdESv141-2.1.xsd";
            String baseURI = "http://docs.oasis-open.org/ubl/prd3-UBL-2.1/xsd/common/UBL-SignatureAggregateComponents-2.1.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup17() throws IOException {
            String type = "http://www.w3.org/2001/XMLSchema";
            String samespaceURI = "http://www.w3.org/2000/09/xmldsig#";
            String publicId = null;
            String systemId = "http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd";
            String baseURI = null;
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup18() throws IOException {
            String type = "http://www.w3.org/TR/REC-xml";
            String samespaceURI = null;
            String publicId = "-//W3C//DTD XMLSchema 200102//EN";
            String systemId = "http://www.w3.org/2001/XMLSchema.dtd";
            String baseURI = "http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd";
            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }

    @Test
    public void shouldSupportLookup19() throws IOException {
            String type = "http://www.w3.org/TR/REC-xml";
            String samespaceURI = null;
            String publicId = "datatypes";
            String systemId = "datatypes.dtd";
            String baseURI = "http://www.w3.org/2001/XMLSchema.dtd";

            checkThatCacheIsQuicker(type, samespaceURI, publicId, systemId, baseURI);
        }


    @Test
    public void shouldUseCacheTheSecondTimeSoItShouldBeMuchMoreQuicker() throws IOException {


        String type="http://www.w3.org/TR/REC-xml";
        String namespaceURI=null;
        String publicId="-//W3C//DTD XMLSchema 200102//EN";
        String systemId="http://www.w3.org/2001/XMLSchema.dtd";
        String baseUri="http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd";

        File cacheFolder = checkThatCacheIsQuicker(type, namespaceURI, publicId, systemId, baseUri);

        String expectedContent = IOUtils.toString(getClass().getResourceAsStream("/expected-xmldsig-core-schema.xsd"), "UTF-8");
        String actualContent = FileUtils.readFileToString(cacheFolder.listFiles()[0], "UTF-8");
        assertThat(expectedContent, equalTo(actualContent));

    }

    private File checkThatNoCacheIsUsed(String type, String namespaceURI, String publicId, String systemId, String baseUri) throws IOException {
        // given
        File cacheFolder = tmp.newFolder();
        CachedResourceResolver sut = new CachedResourceResolver(cacheFolder);

        // when
        LSInput lsInput1 = sut.resolveResource(type, namespaceURI, publicId, systemId, baseUri);

        // then
        assertThat( lsInput1, Matchers.nullValue() );
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
        assertThat( lsInput1, Matchers.notNullValue() );
        assertThat(cacheFolder.listFiles().length, is(1));

        // when
        long deltaWithCache = System.currentTimeMillis();
        LSInput lsInput2 = sut.resolveResource(type, namespaceURI, publicId, systemId, baseUri);
        deltaWithCache = System.currentTimeMillis() - deltaWithCache;

        // then
        assertThat( lsInput2, Matchers.notNullValue() );
        assertThat(cacheFolder.listFiles().length, is(1));
        assertThat("elapsed:" + deltaWithoutCache + "," + deltaWithCache, deltaWithCache, lessThan( deltaWithoutCache/10 ));
        return cacheFolder;
    }

}