<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet version="2.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:cn="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
    <svrl:schematron-output schemaVersion="" title="EN16931 CIUS">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ext" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" />
      <svrl:ns-prefix-in-attribute-values prefix="xs" uri="http://www.w3.org/2001/XMLSchema" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M0" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M1" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M2" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M3" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M4" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M5" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M6" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M7" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M8" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M9" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M10" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M11" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M12" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M13" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M14" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M15" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M16" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M17" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M18" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M19" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M20" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M21" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M22" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M23" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M24" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M25" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M26" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M27" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M28" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M29" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M30" select="/" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M31" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>EN16931 CIUS</svrl:text>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M0" priority="1000">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="number(cbc:ID) > 0" />
      <xsl:otherwise>
        <svrl:failed-assert test="number(cbc:ID) > 0">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-SD-73]-The BT value should be numeric.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M0" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M0" priority="-1" />
  <xsl:template match="@*|node()" mode="M0" priority="-2">
    <xsl:apply-templates mode="M0" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="/Invoice" mode="M1" priority="1000">
    <svrl:fired-rule context="/Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(cbc:TaxCurrencyCode, 'EUR')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(cbc:TaxCurrencyCode, 'EUR')">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-CI-13]-VAT accounting currency code should be € for invoices from EU to IT in accordance with  2006/112/CE art. 9.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M1" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M1" priority="-1" />
  <xsl:template match="@*|node()" mode="M1" priority="-2">
    <xsl:apply-templates mode="M1" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:DespatchDocumentReference" mode="M2" priority="1000">
    <svrl:fired-rule context="cac:DespatchDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(cbc:ID, '/([0-9]{1,9})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})/')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(cbc:ID, '/([0-9]{1,9})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})/')">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-15]-BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05).
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M2" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M2" priority="-1" />
  <xsl:template match="@*|node()" mode="M2" priority="-2">
    <xsl:apply-templates mode="M2" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:DespatchDocumentReference" mode="M3" priority="1000">
    <svrl:fired-rule context="cac:DespatchDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 30">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-16]-BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD).
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M3" priority="-1" />
  <xsl:template match="@*|node()" mode="M3" priority="-2">
    <xsl:apply-templates mode="M3" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity" mode="M4" priority="1000">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:RegistrationName) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:RegistrationName) &lt;= 80">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-17]-BT maximum length shall be 80 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M4" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M4" priority="-1" />
  <xsl:template match="@*|node()" mode="M4" priority="-2">
    <xsl:apply-templates mode="M4" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity" mode="M5" priority="1000">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:RegistrationName) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:RegistrationName) &lt;= 80">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-18]-BT maximum length shall be 80 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M5" priority="-1" />
  <xsl:template match="@*|node()" mode="M5" priority="-2">
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:TaxRepresentativeParty/cac:PartyName" mode="M6" priority="1000">
    <svrl:fired-rule context="cac:TaxRepresentativeParty/cac:PartyName" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 80">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-19]-BT maximum length shall be 80 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M6" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M6" priority="-1" />
  <xsl:template match="@*|node()" mode="M6" priority="-2">
    <xsl:apply-templates mode="M6" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M7" priority="1000">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-20]-The sum of BTs maximum length shall be 180 chars (including separator).
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

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M8" priority="1000">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-21]-The sum of BTs maximum length shall be 180 chars (including separator).
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M8" priority="-1" />
  <xsl:template match="@*|node()" mode="M8" priority="-2">
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M9" priority="1000">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-22]-The sum of BTs maximum length shall be 180 chars (including separator).
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M9" priority="-1" />
  <xsl:template match="@*|node()" mode="M9" priority="-2">
    <xsl:apply-templates mode="M9" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" mode="M10" priority="1000">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CityName) &lt;= 60">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-23]-BT maximum length shall be 60 characters.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M10" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M10" priority="-1" />
  <xsl:template match="@*|node()" mode="M10" priority="-2">
    <xsl:apply-templates mode="M10" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" mode="M11" priority="1000">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CityName) &lt;= 60">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-24]-BT maximum length shall be 60 characters.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M11" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M11" priority="-1" />
  <xsl:template match="@*|node()" mode="M11" priority="-2">
    <xsl:apply-templates mode="M11" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address" mode="M12" priority="1000">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CityName) &lt;= 60">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-25]-BT maximum length shall be 60 characters.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M12" priority="-1" />
  <xsl:template match="@*|node()" mode="M12" priority="-2">
    <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="/Invoice" mode="M13" priority="1000">
    <svrl:fired-rule context="/Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-32]-BT maximum length shall be 20 digits.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M13" priority="-1" />
  <xsl:template match="@*|node()" mode="M13" priority="-2">
    <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:ProjectReference" mode="M14" priority="1000">
    <svrl:fired-rule context="cac:ProjectReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 15">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-33]-BT maximum length shall be 15 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M14" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M14" priority="-1" />
  <xsl:template match="@*|node()" mode="M14" priority="-2">
    <xsl:apply-templates mode="M14" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:ContractDocumentReference" mode="M15" priority="1000">
    <svrl:fired-rule context="cac:ContractDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-34]-BT maximum length shall be 20 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M15" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M15" priority="-1" />
  <xsl:template match="@*|node()" mode="M15" priority="-2">
    <xsl:apply-templates mode="M15" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:OrderReference" mode="M16" priority="1000">
    <svrl:fired-rule context="cac:OrderReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-35]-BT maximum length shall be 20 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M16" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M16" priority="-1" />
  <xsl:template match="@*|node()" mode="M16" priority="-2">
    <xsl:apply-templates mode="M16" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:ReceiptDocumentReference" mode="M17" priority="1000">
    <svrl:fired-rule context="cac:ReceiptDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-36]-BT maximum length shall be 20 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M17" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M17" priority="-1" />
  <xsl:template match="@*|node()" mode="M17" priority="-2">
    <xsl:apply-templates mode="M17" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:OriginatorDocumentReference" mode="M18" priority="1000">
    <svrl:fired-rule context="cac:OriginatorDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 15">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-37]-BT maximum length shall be 15 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M18" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M18" priority="-1" />
  <xsl:template match="@*|node()" mode="M18" priority="-2">
    <xsl:apply-templates mode="M18" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="/Invoice" mode="M19" priority="1000">
    <svrl:fired-rule context="/Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:AccountingCost) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:AccountingCost) &lt;= 20">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-38]-BT maximum length shall be 20 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M19" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M19" priority="-1" />
  <xsl:template match="@*|node()" mode="M19" priority="-2">
    <xsl:apply-templates mode="M19" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="/Invoice" mode="M20" priority="1000">
    <svrl:fired-rule context="/Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Note) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Note) &lt;= 200">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-39]-The sum of BTs maximum length shall be 200 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M20" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M20" priority="-1" />
  <xsl:template match="@*|node()" mode="M20" priority="-2">
    <xsl:apply-templates mode="M20" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:BillingReference/cac:InvoiceDocumentReference" mode="M21" priority="1000">
    <svrl:fired-rule context="cac:BillingReference/cac:InvoiceDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ID) &lt;= 20">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-40]-BT maximum length shall be 20 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M21" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M21" priority="-1" />
  <xsl:template match="@*|node()" mode="M21" priority="-2">
    <xsl:apply-templates mode="M21" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme" mode="M22" priority="1000">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CompanyID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CompanyID) &lt;= 30">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-41]-BT maximum length shall be 30 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M22" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M22" priority="-1" />
  <xsl:template match="@*|node()" mode="M22" priority="-2">
    <xsl:apply-templates mode="M22" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:TaxRepresentativeParty/cac:PartyTaxScheme" mode="M23" priority="1000">
    <svrl:fired-rule context="cac:TaxRepresentativeParty/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CompanyID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CompanyID) &lt;= 30">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-42]-BT maximum length shall be 30 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M23" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M23" priority="-1" />
  <xsl:template match="@*|node()" mode="M23" priority="-2">
    <xsl:apply-templates mode="M23" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme" mode="M24" priority="1000">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:CompanyID) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:CompanyID) &lt;= 30">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-43]-BT maximum length shall be 30 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M24" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M24" priority="-1" />
  <xsl:template match="@*|node()" mode="M24" priority="-2">
    <xsl:apply-templates mode="M24" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:Contact" mode="M25" priority="1000">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 200">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-44]-BT maximum length shall be 200 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M25" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M25" priority="-1" />
  <xsl:template match="@*|node()" mode="M25" priority="-2">
    <xsl:apply-templates mode="M25" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:Contact" mode="M26" priority="1000">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) >= 5" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) >= 5">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-45]-BT minimum length shall be 5 maximum length shall be 12 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M26" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M26" priority="-1" />
  <xsl:template match="@*|node()" mode="M26" priority="-2">
    <xsl:apply-templates mode="M26" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:Contact" mode="M27" priority="1000">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) >= 7" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) >= 7">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-46]-BT minimum length shall be 7 maximum length shall be 256 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M27" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M27" priority="-1" />
  <xsl:template match="@*|node()" mode="M27" priority="-2">
    <xsl:apply-templates mode="M27" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:PayeeParty/cac:PartyName" mode="M28" priority="1000">
    <svrl:fired-rule context="cac:PayeeParty/cac:PartyName" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 200">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-50]-BT maximum length shall be 200 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M28" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M28" priority="-1" />
  <xsl:template match="@*|node()" mode="M28" priority="-2">
    <xsl:apply-templates mode="M28" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:Contact" mode="M29" priority="1000">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:Contact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:Name) &lt;= 200">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-51]-BT maximum length shall be 200 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M29" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M29" priority="-1" />
  <xsl:template match="@*|node()" mode="M29" priority="-2">
    <xsl:apply-templates mode="M29" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:PaymentMeans" mode="M30" priority="1000">
    <svrl:fired-rule context="cac:PaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:InstructionNote) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:InstructionNote) &lt;= 200">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-55]-BT maximum length shall be 200 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M30" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M30" priority="-1" />
  <xsl:template match="@*|node()" mode="M30" priority="-2">
    <xsl:apply-templates mode="M30" select="*|comment()|processing-instruction()" />
  </xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="cac:PaymentMeans" mode="M31" priority="1000">
    <svrl:fired-rule context="cac:PaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(cbc:PaymentID) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(cbc:PaymentID) &lt;= 60">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>
                    [CIUS-VD-55]-BT maximum length shall be 60 chars.
                </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M31" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M31" priority="-1" />
  <xsl:template match="@*|node()" mode="M31" priority="-2">
    <xsl:apply-templates mode="M31" select="*|comment()|processing-instruction()" />
  </xsl:template>
</xsl:stylesheet>
