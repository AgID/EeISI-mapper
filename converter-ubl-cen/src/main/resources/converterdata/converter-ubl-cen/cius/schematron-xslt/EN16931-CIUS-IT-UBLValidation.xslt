<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet version="2.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:cn="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--Implementers: please note that overriding process-prolog or process-root is 
    the preferred method for meta-stylesheets to use where possible. -->
<xsl:param name="archiveDirParameter" />
  <xsl:param name="archiveNameParameter" />
  <xsl:param name="fileNameParameter" />
  <xsl:param name="fileDirParameter" />
  <xsl:variable name="document-uri">
    <xsl:value-of select="document-uri(/)" />
  </xsl:variable>

<!--PHASES-->


<!--PROLOG-->
<xsl:output indent="yes" method="xml" omit-xml-declaration="no" standalone="yes" />

<!--XSD TYPES FOR XSLT2-->


<!--KEYS AND FUNCTIONS-->


<!--DEFAULT RULES-->


<!--MODE: SCHEMATRON-SELECT-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-select-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="." />
  </xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-get-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="parent::*" />
    <xsl:text>/</xsl:text>
    <xsl:choose>
      <xsl:when test="namespace-uri()=''">
        <xsl:value-of select="name()" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>*:</xsl:text>
        <xsl:value-of select="local-name()" />
        <xsl:text>[namespace-uri()='</xsl:text>
        <xsl:value-of select="namespace-uri()" />
        <xsl:text>']</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:variable name="preceding" select="count(preceding-sibling::*[local-name()=local-name(current())                                   and namespace-uri() = namespace-uri(current())])" />
    <xsl:text>[</xsl:text>
    <xsl:value-of select="1+ $preceding" />
    <xsl:text>]</xsl:text>
  </xsl:template>
  <xsl:template match="@*" mode="schematron-get-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="parent::*" />
    <xsl:text>/</xsl:text>
    <xsl:choose>
      <xsl:when test="namespace-uri()=''">@<xsl:value-of select="name()" />
</xsl:when>
      <xsl:otherwise>
        <xsl:text>@*[local-name()='</xsl:text>
        <xsl:value-of select="local-name()" />
        <xsl:text>' and namespace-uri()='</xsl:text>
        <xsl:value-of select="namespace-uri()" />
        <xsl:text>']</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-2-->
<!--This mode can be used to generate prefixed XPath for humans-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-2">
    <xsl:for-each select="ancestor-or-self::*">
      <xsl:text>/</xsl:text>
      <xsl:value-of select="name(.)" />
      <xsl:if test="preceding-sibling::*[name(.)=name(current())]">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1" />
        <xsl:text>]</xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="not(self::*)">
      <xsl:text />/@<xsl:value-of select="name(.)" />
    </xsl:if>
  </xsl:template>
<!--MODE: SCHEMATRON-FULL-PATH-3-->
<!--This mode can be used to generate prefixed XPath for humans 
	(Top-level element has index)-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-3">
    <xsl:for-each select="ancestor-or-self::*">
      <xsl:text>/</xsl:text>
      <xsl:value-of select="name(.)" />
      <xsl:if test="parent::*">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1" />
        <xsl:text>]</xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="not(self::*)">
      <xsl:text />/@<xsl:value-of select="name(.)" />
    </xsl:if>
  </xsl:template>

<!--MODE: GENERATE-ID-FROM-PATH -->
<xsl:template match="/" mode="generate-id-from-path" />
  <xsl:template match="text()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')" />
  </xsl:template>
  <xsl:template match="comment()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')" />
  </xsl:template>
  <xsl:template match="processing-instruction()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')" />
  </xsl:template>
  <xsl:template match="@*" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.@', name())" />
  </xsl:template>
  <xsl:template match="*" mode="generate-id-from-path" priority="-0.5">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:text>.</xsl:text>
    <xsl:value-of select="concat('.',name(),'-',1+count(preceding-sibling::*[name()=name(current())]),'-')" />
  </xsl:template>

<!--MODE: GENERATE-ID-2 -->
<xsl:template match="/" mode="generate-id-2">U</xsl:template>
  <xsl:template match="*" mode="generate-id-2" priority="2">
    <xsl:text>U</xsl:text>
    <xsl:number count="*" level="multiple" />
  </xsl:template>
  <xsl:template match="node()" mode="generate-id-2">
    <xsl:text>U.</xsl:text>
    <xsl:number count="*" level="multiple" />
    <xsl:text>n</xsl:text>
    <xsl:number count="node()" />
  </xsl:template>
  <xsl:template match="@*" mode="generate-id-2">
    <xsl:text>U.</xsl:text>
    <xsl:number count="*" level="multiple" />
    <xsl:text>_</xsl:text>
    <xsl:value-of select="string-length(local-name(.))" />
    <xsl:text>_</xsl:text>
    <xsl:value-of select="translate(name(),':','.')" />
  </xsl:template>
<!--Strip characters-->  <xsl:template match="text()" priority="-1" />

<!--SCHEMA SETUP-->
<xsl:template match="/">
    <svrl:schematron-output schemaVersion="" title="EN16931 UBL CIUS">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" />
      <svrl:ns-prefix-in-attribute-values prefix="xs" uri="http://www.w3.org/2001/XMLSchema" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">CIUS-IT</xsl:attribute>
        <xsl:attribute name="name">CIUS-IT</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M7" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>EN16931 UBL CIUS</svrl:text>

<!--PATTERN CIUS-IT-->


	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party" mode="M7" priority="1089">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(cbc:EndpointID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(cbc:EndpointID)">
          <xsl:attribute name="id">CIUS-BT-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-BT-2] BT-49 (Buyer electronic address) shall contain a legal mail address (PEC) or IndicePA/CodiceUfficio (see the Italian business rules). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme" mode="M7" priority="1088">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CompanyID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CompanyID)">
          <xsl:attribute name="id">CIUS-BT-98</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-BT-98] BT-32 (Buyer electronic address)  is a conditional field and shall not be used by a foreign seller as it is not possible to map into XMLPA. In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8) 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount" mode="M7" priority="1087">
    <svrl:fired-rule context="cac:PaymentMeans/cac:PayeeFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(cbc:ID, '([A-Z,0-9]{15,34})')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(cbc:ID, '([A-Z,0-9]{15,34})')">
          <xsl:attribute name="id">CIUS-BT-84</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-BT-84] BT-84 (Payment account identifier)  shall be an IBAN code. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice" mode="M7" priority="1086">
    <svrl:fired-rule context="//ubl:Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID)">
          <xsl:attribute name="id">CIUS-CA-9-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-9-1] BT-31 (Seller VAT identifier) -Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax representative). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID)">
          <xsl:attribute name="id">CIUS-CA-9-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-9-2] BT-63 (Seller tax representative VAT identifier) -Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax representative). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M7" priority="1085">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)">
          <xsl:attribute name="id">CIUS-CA-10-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-10-1] BT-35 (Seller address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)">
          <xsl:attribute name="id">CIUS-CA-10-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-10-2] BT-37 (Seller city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)">
          <xsl:attribute name="id">CIUS-CA-10-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-10-3] BT-38 (Seller post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M7" priority="1084">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)">
          <xsl:attribute name="id">CIUS-CA-11-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-11-1] BT-50 (Buyer address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)">
          <xsl:attribute name="id">CIUS-CA-11-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-11-2] BT-52 (Buyer city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)">
          <xsl:attribute name="id">CIUS-CA-11-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-11-3] BT-53 (Buyer post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M7" priority="1083">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:StreetName))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:StreetName))">
          <xsl:attribute name="id">CIUS-CA-12-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-12-1] BT-75 (Deliver to address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:CityName))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:CityName))">
          <xsl:attribute name="id">CIUS-CA-12-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-12-2] BT-77 (Deliver to city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:PostalZone))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:PostalZone))">
          <xsl:attribute name="id">CIUS-CA-12-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-12-3] BT-78 (Deliver to post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AdditionalDocumentReference" mode="M7" priority="1082">
    <svrl:fired-rule context="cac:AdditionalDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(cac:Attachment/cac:ExternalReference/cbc:URI) or exists(cac:Attachment/cbc:EmbeddedDocumentBinaryObject)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(cac:Attachment/cac:ExternalReference/cbc:URI) or exists(cac:Attachment/cbc:EmbeddedDocumentBinaryObject)">
          <xsl:attribute name="id">CIUS-CA-71</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-71] BT-125 (Attached document) -If BT-124 is empty then the BT-125 should be mandatory as the mapped field is mandatory in Italy. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M7" priority="1081">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="number(cbc:ID) > 0" />
      <xsl:otherwise>
        <svrl:failed-assert test="number(cbc:ID) > 0">
          <xsl:attribute name="id">CIUS-SD-73</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-SD-73] BT-126 (Invoice line identifier) -The BT value should be numeric. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice" mode="M7" priority="1080">
    <svrl:fired-rule context="//ubl:Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or matches(cbc:TaxCurrencyCode, 'EUR')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or matches(cbc:TaxCurrencyCode, 'EUR')">
          <xsl:attribute name="id">CIUS-CI-13</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CI-13] BT-6 (VAT accounting currency code)  should be € for invoices from EU to IT in accordance with 2006/112/CE art. 9. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice/cac:AccountingCustomerParty/cac:Party" mode="M7" priority="1079">
    <svrl:fired-rule context="//ubl:Invoice/cac:AccountingCustomerParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )">
          <xsl:attribute name="id">CIUS-BR-14</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-BR-14] BT-48
BT-46, BT-46-1 (Buyer VAT identifier
Buyer identifier
Buyer identifier identification scheme identifier) -1.4.1.1 is not mandatory in Italy (buyer) but VAT number or Fiscal code should be indicated. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:DespatchDocumentReference" mode="M7" priority="1078">
    <svrl:fired-rule context="cac:DespatchDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(cbc:ID, '/([0-9]{1,9})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})/')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(cbc:ID, '/([0-9]{1,9})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})/')">
          <xsl:attribute name="id">CIUS-VD-15</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-15] BT-16 (Despatch advice reference) -BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05) 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:DespatchDocumentReference" mode="M7" priority="1077">
    <svrl:fired-rule context="cac:DespatchDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 30">
          <xsl:attribute name="id">CIUS-VD-16</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-16] BT-16 (Despatch advice reference) -BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity" mode="M7" priority="1076">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:RegistrationName) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:RegistrationName) &lt;= 80">
          <xsl:attribute name="id">CIUS-VD-17</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-17] BT-27 (Seller name) -BT maximum length shall be 80 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity" mode="M7" priority="1075">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:RegistrationName) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:RegistrationName) &lt;= 80">
          <xsl:attribute name="id">CIUS-VD-18</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-18] BT-44 (Buyer name) -BT maximum length shall be 80 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxRepresentativeParty/cac:PartyName" mode="M7" priority="1074">
    <svrl:fired-rule context="cac:TaxRepresentativeParty/cac:PartyName" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 80">
          <xsl:attribute name="id">CIUS-VD-19</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-19] BT-62 (Seller tax representative name) -BT maximum length shall be 80 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M7" priority="1073">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180">
          <xsl:attribute name="id">CIUS-VD-20</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-20] BT-35, BT-36, BT-162 (Seller address line 1
Seller address line 2
Seller address line 3) -The sum of BTs maximum length shall be 180 chars (including separator). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M7" priority="1072">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180">
          <xsl:attribute name="id">CIUS-VD-21</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-21] BT-50, BT-51, BT-163 (Buyer address line 1
Buyer address line 2
Buyer address line 3) -The sum of BTs maximum length shall be 180 chars (including separator). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M7" priority="1071">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180">
          <xsl:attribute name="id">CIUS-VD-22</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-22] BT-75, BT-76, BT-165 (Deliver to address line 1
Deliver to address line 2
Deliver to address line 3) -The sum of BTs maximum length shall be 180 chars (including separator). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M7" priority="1070">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CityName) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-23</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-23] BT-37 (Seller city) -BT maximum length shall be 60 characters. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M7" priority="1069">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CityName) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-24</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-24] BT-52 (Buyer city) -BT maximum length shall be 60 characters. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M7" priority="1068">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CityName) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-25</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-25] BT-77 (Deliver to city) -BT maximum length shall be 60 characters. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M7" priority="1067">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:PostalZone) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:PostalZone) &lt;= 15">
          <xsl:attribute name="id">CIUS-VD-26-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-26-1] BT-38 (Seller post code) -BT maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) > 0 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) > 0 )">
          <xsl:attribute name="id">CIUS-VD-26-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-26-2] BT-38 (Seller post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M7" priority="1066">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:PostalZone) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:PostalZone) &lt;= 15">
          <xsl:attribute name="id">CIUS-VD-27-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-27-1] BT-53 (Buyer post code) -BT maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) > 0 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) > 0 )">
          <xsl:attribute name="id">CIUS-VD-27-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-27-2] BT-53 (Buyer post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M7" priority="1065">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:PostalZone) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:PostalZone) &lt;= 15">
          <xsl:attribute name="id">CIUS-VD-28-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-28-1] BT-78 (Deliver to post code) -BT maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) > 0 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) > 0 )">
          <xsl:attribute name="id">CIUS-VD-28-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-28-2] BT-78 (Deliver to post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M7" priority="1064">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2">
          <xsl:attribute name="id">CIUS-VD-29</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-29] BT-39 (Seller country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M7" priority="1063">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2">
          <xsl:attribute name="id">CIUS-VD-30</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-30] BT-54 (Buyer country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M7" priority="1062">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2">
          <xsl:attribute name="id">CIUS-VD-31</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-31] BT-79 (Deliver to country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice" mode="M7" priority="1061">
    <svrl:fired-rule context="//ubl:Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-32</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-32] BT-1 (Invoice number) -BT maximum length shall be 20 digits. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:ProjectReference" mode="M7" priority="1060">
    <svrl:fired-rule context="cac:ProjectReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 15">
          <xsl:attribute name="id">CIUS-VD-33</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-33] BT-11 (Project reference) -BT maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:ContractDocumentReference" mode="M7" priority="1059">
    <svrl:fired-rule context="cac:ContractDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-34</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-34] BT-12 (Contract reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:OrderReference" mode="M7" priority="1058">
    <svrl:fired-rule context="cac:OrderReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-35</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-35] BT-13 (Purchase order reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:ReceiptDocumentReference" mode="M7" priority="1057">
    <svrl:fired-rule context="cac:ReceiptDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-36</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-36] BT-15 (Receiving advice reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:OriginatorDocumentReference" mode="M7" priority="1056">
    <svrl:fired-rule context="cac:OriginatorDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 15">
          <xsl:attribute name="id">CIUS-VD-37</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-37] BT-17 (Tender or lot reference) -BT maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M7" priority="1055">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:AccountingCost) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:AccountingCost) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-38</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-38] BT-19 (Buyer accounting reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice" mode="M7" priority="1054">
    <svrl:fired-rule context="//ubl:Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Note) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Note) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-39</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-39] BT-21, BT-22 (Invoice note subject code
Invoice note) -The sum of BTs maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:BillingReference/cac:InvoiceDocumentReference" mode="M7" priority="1053">
    <svrl:fired-rule context="cac:BillingReference/cac:InvoiceDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-40</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-40] BT-25 (Preceding Invoice number)-BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme" mode="M7" priority="1052">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CompanyID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CompanyID) &lt;= 30">
          <xsl:attribute name="id">CIUS-VD-41</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-41] BT-31 (Seller VAT identifier) -BT maximum length shall be 30 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxRepresentativeParty/cac:PartyTaxScheme" mode="M7" priority="1051">
    <svrl:fired-rule context="cac:TaxRepresentativeParty/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CompanyID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CompanyID) &lt;= 30">
          <xsl:attribute name="id">CIUS-VD-42</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-42] BT-63 (Seller tax representative VAT identifier) -BT maximum length shall be 30 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme" mode="M7" priority="1050">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CompanyID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CompanyID) &lt;= 30">
          <xsl:attribute name="id">CIUS-VD-43</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-43] BT-48 (Buyer VAT identifier) -BT maximum length shall be 30 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:Contact" mode="M7" priority="1049">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-44</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-44] BT-41 (Seller contact point)  -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:Contact" mode="M7" priority="1048">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) >= 5" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) >= 5">
          <xsl:attribute name="id">CIUS-VD-45</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-45] BT-42 (Seller contact telephone number) -BT minimum length shall be 5 maximum length shall be 12 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:Contact" mode="M7" priority="1047">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) >= 7" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) >= 7">
          <xsl:attribute name="id">CIUS-VD-46</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-46] BT-43 (Seller contact email address) -BT minimum length shall be 7 maximum length shall be 256 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M7" priority="1046">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )">
          <xsl:attribute name="id">CIUS-VD-47</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-47] BT-39 (Seller country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M7" priority="1045">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )">
          <xsl:attribute name="id">CIUS-VD-48</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-48] BT-54 (Buyer country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M7" priority="1044">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )">
          <xsl:attribute name="id">CIUS-VD-49</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-49] BT-79 (Deliver to country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PayeeParty/cac:PartyName" mode="M7" priority="1043">
    <svrl:fired-rule context="cac:PayeeParty/cac:PartyName" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-50</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-50] BT-59 (Payee name) -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:Contact" mode="M7" priority="1042">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-51</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-51] BT-56 (Buyer contact point) -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party" mode="M7" priority="1041">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )">
          <xsl:attribute name="id">CIUS-VD-53</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-53] BT-46, BT-46-1 (Buyer identifier
Buyer identifier identification scheme identifier) -If BT-48 is empty then one of the buyer identifiers (0..n) should be the FiscalCode in BT-46. BT-46-1 shall contain the scheme. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans" mode="M7" priority="1040">
    <svrl:fired-rule context="cac:PaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:InstructionNote) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:InstructionNote) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-55</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-55] BT-82 (Payment means text) -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans" mode="M7" priority="1039">
    <svrl:fired-rule context="cac:PaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:PaymentID) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:PaymentID) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-56</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-56] BT-83 (Remittance information) -BT maximum length shall be 60 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount" mode="M7" priority="1038">
    <svrl:fired-rule context="cac:PaymentMeans/cac:PayeeFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 34 and string-length(cbc:ID) >= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 34 and string-length(cbc:ID) >= 15">
          <xsl:attribute name="id">CIUS-VD-57</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-57] BT-84 (Payment account identifier) -BT minimum length shall be 15, maximum length shall be 34 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount" mode="M7" priority="1037">
    <svrl:fired-rule context="cac:PaymentMeans/cac:PayeeFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-58</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-58] BT-85 (Payment account name) -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch" mode="M7" priority="1036">
    <svrl:fired-rule context="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 11 and string-length(cbc:ID) >= 8" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 11 and string-length(cbc:ID) >= 8">
          <xsl:attribute name="id">CIUS-VD-59</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-59] BT-86 (Payment service provider identifier) -BT minimum length shall be 8 maximum length shall be 11 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AllowanceCharge" mode="M7" priority="1035">
    <svrl:fired-rule context="cac:AllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-60</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-60] BT-97, BT-98 (Document level allowance reason
Document level allowance reason code)-BTs maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AllowanceCharge" mode="M7" priority="1034">
    <svrl:fired-rule context="cac:AllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-61</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-61] BT-104, BT-105 (Document level charge reason
Document level charge reason code)-BTs maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:LegalMonetaryTotal" mode="M7" priority="1033">
    <svrl:fired-rule context="cac:LegalMonetaryTotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:TaxInclusiveAmount) &lt;= 15 and string-length(cbc:TaxInclusiveAmount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:TaxInclusiveAmount) &lt;= 15 and string-length(cbc:TaxInclusiveAmount) >= 4">
          <xsl:attribute name="id">CIUS-VD-62</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-62] BT-112 (Invoice total amount with VAT) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:LegalMonetaryTotal" mode="M7" priority="1032">
    <svrl:fired-rule context="cac:LegalMonetaryTotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:PayableAmount) &lt;= 15 and string-length(cbc:PayableAmount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:PayableAmount) &lt;= 15 and string-length(cbc:PayableAmount) >= 4">
          <xsl:attribute name="id">CIUS-VD-63</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-63] BT-115 (Amount due for payment) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AllowanceCharge" mode="M7" priority="1031">
    <svrl:fired-rule context="cac:AllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Amount) &lt;= 21 and string-length(cbc:Amount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Amount) &lt;= 21 and string-length(cbc:Amount) >= 4">
          <xsl:attribute name="id">CIUS-VD-64</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-64] BT-92, BT-99 (Document level allowance amount
Document level charge amount) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:LegalMonetaryTotal" mode="M7" priority="1030">
    <svrl:fired-rule context="cac:LegalMonetaryTotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(cbc:PayableRoundingAmount)) or (string-length(cbc:PayableRoundingAmount) &lt;= 15 and string-length(cbc:PayableRoundingAmount) >= 4)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(cbc:PayableRoundingAmount)) or (string-length(cbc:PayableRoundingAmount) &lt;= 15 and string-length(cbc:PayableRoundingAmount) >= 4)">
          <xsl:attribute name="id">CIUS-VD-65</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-65] BT-114 (Rounding amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxTotal/cac:TaxSubtotal" mode="M7" priority="1029">
    <svrl:fired-rule context="cac:TaxTotal/cac:TaxSubtotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:TaxableAmount) &lt;= 15 and string-length(cbc:TaxableAmount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:TaxableAmount) &lt;= 15 and string-length(cbc:TaxableAmount) >= 4">
          <xsl:attribute name="id">CIUS-VD-66</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> TEST[CIUS-VD-67] BT-116 (VAT category taxable amount) -BT minimum length shall be 4 maximum length shall be 15 chars.
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxTotal/cac:TaxSubtotal" mode="M7" priority="1028">
    <svrl:fired-rule context="cac:TaxTotal/cac:TaxSubtotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:TaxAmount) &lt;= 15 and string-length(cbc:TaxAmount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:TaxAmount) &lt;= 15 and string-length(cbc:TaxAmount) >= 4">
          <xsl:attribute name="id">CIUS-VD-67</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-67] BT-117 (VAT category tax amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory" mode="M7" priority="1027">
    <svrl:fired-rule context="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:TaxExemptionReason) &lt;= 100" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:TaxExemptionReason) &lt;= 100">
          <xsl:attribute name="id">CIUS-VD-68</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-68] BT-120 (VAT exemption reason text) -BT maximum length shall be 100 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AdditionalDocumentReference" mode="M7" priority="1026">
    <svrl:fired-rule context="cac:AdditionalDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:ID) + string-length(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename)) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:ID) + string-length(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename)) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-69</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-69] BT-122, BT-125-2 (Supporting document reference
Attached document Filename) - BT maximum length shall be 60 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AdditionalDocumentReference" mode="M7" priority="1025">
    <svrl:fired-rule context="cac:AdditionalDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:DocumentType) &lt;= 100" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:DocumentType) &lt;= 100">
          <xsl:attribute name="id">CIUS-VD-70</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-70] BT-123 (Supporting document description) -BT maximum length shall be 100 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AdditionalDocumentReference/cac:Attachment" mode="M7" priority="1024">
    <svrl:fired-rule context="cac:AdditionalDocumentReference/cac:Attachment" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:EmbeddedDocumentBinaryObject) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:EmbeddedDocumentBinaryObject) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-72</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-72] BT-125-1 (Attached document Mime code) -BT maximum length shall be 10 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M7" priority="1023">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 4">
          <xsl:attribute name="id">CIUS-VD-74</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-74] BT-126 (Invoice line identifier) -BT maximum length shall be 4 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M7" priority="1022">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Note) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Note) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-75</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-75] BT-127 (Invoice line note) -BT maximum length shall be 60 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:DocumentReference" mode="M7" priority="1021">
    <svrl:fired-rule context="cac:InvoiceLine/cac:DocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID/@schemeID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID/@schemeID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-76</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-76] BT-128-1 (Invoice line object identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:DocumentReference" mode="M7" priority="1020">
    <svrl:fired-rule context="cac:InvoiceLine/cac:DocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-77</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-77] BT-128 (Invoice line object identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M7" priority="1019">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:InvoicedQuantity/@unitCode) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:InvoicedQuantity/@unitCode) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-78-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-78-1] BT-130 (Invoiced quantity unit of measure) -BT maximum length shall be 10 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cac:Price/cbc:BaseQuantity) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cac:Price/cbc:BaseQuantity) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-78-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-78-2] BT-149 (Item price base quantity) -BT maximum length shall be 10 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cac:Price/cbc:BaseQuantity/@unitCode) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cac:Price/cbc:BaseQuantity/@unitCode) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-78-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-78-3] BT-150 (Item price base quantity unit of measure code) -BT maximum length shall be 10 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M7" priority="1018">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:AccountingCost) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:AccountingCost) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-79</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-79] BT-133 (Invoice line Buyer accounting reference)-BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:AllowanceCharge" mode="M7" priority="1017">
    <svrl:fired-rule context="cac:InvoiceLine/cac:AllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Amount) >= 4 and string-length(cbc:Amount) &lt;= 21" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Amount) >= 4 and string-length(cbc:Amount) &lt;= 21">
          <xsl:attribute name="id">CIUS-VD-80</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-80] BT-136, BT-141 (Invoice line allowance amount
Invoice line charge amount)-BT minimum length shall be 4, maximum length shall be 21 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:AllowanceCharge" mode="M7" priority="1016">
    <svrl:fired-rule context="cac:InvoiceLine/cac:AllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:AllowanceChargeReason) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:AllowanceChargeReason) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-81-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-81-1] BT-139 (Invoice line allowance reason)-BT maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-81-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-81-2] BT-140 (Invoice line allowance reason code)-BT maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:AllowanceCharge" mode="M7" priority="1015">
    <svrl:fired-rule context="cac:InvoiceLine/cac:AllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:AllowanceChargeReason) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:AllowanceChargeReason) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-82-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-82-1] BT-144 (Invoice line charge reason)-BT maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-82-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-82-2] BT-145 (Invoice line charge reason code)-BT maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Price" mode="M7" priority="1014">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Price" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:PriceAmount) &lt;= 21 and string-length(cbc:PriceAmount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:PriceAmount) &lt;= 21 and string-length(cbc:PriceAmount) >= 4">
          <xsl:attribute name="id">CIUS-VD-83</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-83] BT-146 (Item net price) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item" mode="M7" priority="1013">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-85-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-85-1] BT-153 (Item name) -BT maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Description) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Description) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-85-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-85-2] BT-154 (Item description) -BT maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:SellersItemIdentification" mode="M7" priority="1012">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:SellersItemIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-86</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-86] BT-155 (Item Seller's identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification" mode="M7" priority="1011">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-87</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-87] BT-156 (Item Buyer's identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification" mode="M7" priority="1010">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-88</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-88] BT-157 (Item standard identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:CommodityClassification" mode="M7" priority="1009">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:CommodityClassification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ItemClassificationCode) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ItemClassificationCode) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-89</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-89] BT-158 (Item classification identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification" mode="M7" priority="1008">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID/@schemeID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID/@schemeID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-90</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-90] BT-157-1 (Item standard identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode" mode="M7" priority="1007">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(@listVersionID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(@listVersionID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-91-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-91-1] BT-158-1 (Item classification identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(@listID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(@listID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-91-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-91-2] BT-158-2 (Scheme version identifer) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:OriginCountry" mode="M7" priority="1006">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:OriginCountry" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:IdentificationCode) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:IdentificationCode) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-92</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-92] BT-159 (Item country of origin) -BT maximum length shall be 60 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty" mode="M7" priority="1005">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-93</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-93] BT-160 (Item attribute name) -BT maximum length shall be 10 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty" mode="M7" priority="1004">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Value) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Value) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-94</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-94] BT-161 (Item attribute value) -BT maximum length shall be 60 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Price" mode="M7" priority="1003">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Price" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(cbc:PriceAmount, '^[0-9]+(\.[0-9]{0,8})*$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(cbc:PriceAmount, '^[0-9]+(\.[0-9]{0,8})*$')">
          <xsl:attribute name="id">CIUS-VD-95</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-95] BT-146 (Item net price) -BT allowed fraction digits shall be 8. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:OrderLineReference" mode="M7" priority="1002">
    <svrl:fired-rule context="cac:InvoiceLine/cac:OrderLineReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:LineID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:LineID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-96</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-96] BT-132 (Referenced purchase order line reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party" mode="M7" priority="1001">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:EndpointID/@schemeID = ' PEC') or ( string-length(cbc:EndpointID) >= 7  and string-length(cbc:EndpointID) &lt;= 256 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:EndpointID/@schemeID = ' PEC') or ( string-length(cbc:EndpointID) >= 7 and string-length(cbc:EndpointID) &lt;= 256 )">
          <xsl:attribute name="id">CIUS-VD-97-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-97-1] BT-49, BT-49-1 (Buyer electronic address
Buyer electronic address identification scheme identifier) -If BT-49-1= PEC schema then BT-49 minimum length shall be 7 maximum length shall be 256 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:EndpointID/@schemeID = ' IPA') or ( string-length(cbc:EndpointID) &lt;= 6 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:EndpointID/@schemeID = ' IPA') or ( string-length(cbc:EndpointID) &lt;= 6 )">
          <xsl:attribute name="id">CIUS-VD-97-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-97-2] BT-49, BT-49-1 (Buyer electronic address
Buyer electronic address identification scheme identifier) -Indice IPA schema then BT-49 maximum length shall be 6 chars 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:EndpointID/@schemeID = ' CodiceUfficio') or ( string-length(cbc:EndpointID) &lt;= 7 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:EndpointID/@schemeID = ' CodiceUfficio') or ( string-length(cbc:EndpointID) &lt;= 7 )">
          <xsl:attribute name="id">CIUS-VD-97-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-97-3] BT-49, BT-49-1 (Buyer electronic address
Buyer electronic address identification scheme identifier) -CodiceUfficio schema then BT-49 maximum length shall be 7 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID" mode="M7" priority="1000">
    <svrl:fired-rule context="//ubl:Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or ( ( ( not(contains(normalize-space(.),' ')) and contains( ' RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19',concat(' ',normalize-space(.),' ') ) ) ) )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or ( ( ( not(contains(normalize-space(.),' ')) and contains( ' RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19',concat(' ',normalize-space(.),' ') ) ) ) )">
          <xsl:attribute name="id">CIUS-VD-99</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-99] BT-32 (Seller tax registration identifier) -In case the seller is Italian this field must contain the codification of RegimeFiscale 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M7" priority="-1" />
  <xsl:template match="@*|node()" mode="M7" priority="-2">
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>
</xsl:stylesheet>
