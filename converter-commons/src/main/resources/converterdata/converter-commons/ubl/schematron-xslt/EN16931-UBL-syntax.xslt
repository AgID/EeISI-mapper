<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet version="2.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
    <svrl:schematron-output schemaVersion="" title="EN16931  syntax bound to UBL">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ext" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="xs" uri="http://www.w3.org/2001/XMLSchema" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">UBL-syntax</xsl:attribute>
        <xsl:attribute name="name">UBL-syntax</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M7" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>EN16931  syntax bound to UBL</svrl:text>

<!--PATTERN UBL-syntax-->


	<!--RULE -->
<xsl:template match="cac:AdditionalDocumentReference" mode="M7" priority="1012">
    <svrl:fired-rule context="cac:AdditionalDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cbc:DocumentType) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cbc:DocumentType) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-33</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-33]-Supporting document description shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//*[ends-with(name(), 'Amount') and not(ends-with(name(),'PriceAmount')) and not(ancestor::cac:Price/cac:AllowanceCharge)]" mode="M7" priority="1011">
    <svrl:fired-rule context="//*[ends-with(name(), 'Amount') and not(ends-with(name(),'PriceAmount')) and not(ancestor::cac:Price/cac:AllowanceCharge)]" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(substring-after(.,'.'))&lt;=2" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(substring-after(.,'.'))&lt;=2">
          <xsl:attribute name="id">UBL-DT-01</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-01]-Amounts shall be decimal up to two fraction digits</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//*[ends-with(name(), 'BinaryObject')]" mode="M7" priority="1010">
    <svrl:fired-rule context="//*[ends-with(name(), 'BinaryObject')]" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(@mimeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(@mimeCode)">
          <xsl:attribute name="id">UBL-DT-06</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-06]-Binary object elements shall contain the mime code attribute</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(@filename)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(@filename)">
          <xsl:attribute name="id">UBL-DT-07</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-07]-Binary object elements shall contain the file name attribute</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery" mode="M7" priority="1009">
    <svrl:fired-rule context="cac:Delivery" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:DeliveryParty/cac:PartyName/cbc:Name) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:DeliveryParty/cac:PartyName/cbc:Name) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-25</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-25]-Deliver to party name shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AllowanceCharge[cbc:ChargeIndicator = 'false']" mode="M7" priority="1008">
    <svrl:fired-rule context="cac:AllowanceCharge[cbc:ChargeIndicator = 'false']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cbc:AllowanceChargeReason) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cbc:AllowanceChargeReason) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-30</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-30]-Document level allowance reason shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AllowanceCharge[cbc:ChargeIndicator = 'true']" mode="M7" priority="1007">
    <svrl:fired-rule context="cac:AllowanceCharge[cbc:ChargeIndicator = 'true']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cbc:AllowanceChargeReason) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cbc:AllowanceChargeReason) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-31</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-31]-Document level charge reason shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice" mode="M7" priority="1006">
    <svrl:fired-rule context="/ubl:Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ext:UBLExtensions)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ext:UBLExtensions)">
          <xsl:attribute name="id">UBL-CR-001</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-001]-A UBL invoice should not include extensions</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:ProfileExecutionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:ProfileExecutionID)">
          <xsl:attribute name="id">UBL-CR-003</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-003]-A UBL invoice should not include the ProfileExecutionID </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-004</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-004]-A UBL invoice should not include the CopyIndicator </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-005</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-005]-A UBL invoice should not include the UUID </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-006</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-006]-A UBL invoice should not include the IssueTime </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:PricingCurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:PricingCurrencyCode)">
          <xsl:attribute name="id">UBL-CR-007</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-007]-A UBL invoice should not include the PricingCurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:PaymentCurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:PaymentCurrencyCode)">
          <xsl:attribute name="id">UBL-CR-008</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-008]-A UBL invoice should not include the PaymentCurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:PaymentAlternativeCurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:PaymentAlternativeCurrencyCode)">
          <xsl:attribute name="id">UBL-CR-009</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-009]-A UBL invoice should not include the PaymentAlternativeCurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:AccountingCostCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:AccountingCostCode)">
          <xsl:attribute name="id">UBL-CR-010</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-010]-A UBL invoice should not include the AccountingCostCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:LineCountNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:LineCountNumeric)">
          <xsl:attribute name="id">UBL-CR-011</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-011]-A UBL invoice should not include the LineCountNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:StartTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:StartTime)">
          <xsl:attribute name="id">UBL-CR-012</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-012]-A UBL invoice should not include the InvoicePeriod StartTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:EndTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:EndTime)">
          <xsl:attribute name="id">UBL-CR-013</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-013]-A UBL invoice should not include the InvoicePeriod EndTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:DurationMeasure)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:DurationMeasure)">
          <xsl:attribute name="id">UBL-CR-014</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-014]-A UBL invoice should not include the InvoicePeriod DurationMeasure</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:Description)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:Description)">
          <xsl:attribute name="id">UBL-CR-015</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-015]-A UBL invoice should not include the InvoicePeriod Description</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-016</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-016]-A UBL invoice should not include the OrderReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-017</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-017]-A UBL invoice should not include the OrderReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-018</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-018]-A UBL invoice should not include the OrderReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-019</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-019]-A UBL invoice should not include the OrderReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:CustomerReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:CustomerReference)">
          <xsl:attribute name="id">UBL-CR-020</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-020]-A UBL invoice should not include the OrderReference CustomerReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:OrderTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:OrderTypeCode)">
          <xsl:attribute name="id">UBL-CR-021</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-021]-A UBL invoice should not include the OrderReference OrderTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:DocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:DocumentReference)">
          <xsl:attribute name="id">UBL-CR-022</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-022]-A UBL invoice should not include the OrderReference DocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-023</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-023]-A UBL invoice should not include the BillingReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-024</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-024]-A UBL invoice should not include the BillingReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-025</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-025]-A UBL invoice should not include the BillingReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentTypeCode)">
          <xsl:attribute name="id">UBL-CR-026</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-026]-A UBL invoice should not include the BillingReference DocumentTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentType)">
          <xsl:attribute name="id">UBL-CR-027</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-027]-A UBL invoice should not include the BillingReference DocumentType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:Xpath)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:Xpath)">
          <xsl:attribute name="id">UBL-CR-028</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-028]-A UBL invoice should not include the BillingReference Xpath</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:LanguageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:LanguageID)">
          <xsl:attribute name="id">UBL-CR-029</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-029]-A UBL invoice should not include the BillingReference LanguageID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:LocaleCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:LocaleCode)">
          <xsl:attribute name="id">UBL-CR-030</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-030]-A UBL invoice should not include the BillingReference LocaleCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:VersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:VersionID)">
          <xsl:attribute name="id">UBL-CR-031</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-031]-A UBL invoice should not include the BillingReference VersionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentStatusCode)">
          <xsl:attribute name="id">UBL-CR-032</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-032]-A UBL invoice should not include the BillingReference DocumentStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentDescription)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentDescription)">
          <xsl:attribute name="id">UBL-CR-033</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-033]-A UBL invoice should not include the BillingReference DocumenDescription</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:Attachment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:Attachment)">
          <xsl:attribute name="id">UBL-CR-034</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-034]-A UBL invoice should not include the BillingReference Attachment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-035</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-035]-A UBL invoice should not include the BillingReference ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-036</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-036]-A UBL invoice should not include the BillingReference IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:ResultOfVerification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:ResultOfVerification)">
          <xsl:attribute name="id">UBL-CR-037</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-037]-A UBL invoice should not include the BillingReference ResultOfVerification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:SelfBilledInvoiceDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:SelfBilledInvoiceDocumentReference)">
          <xsl:attribute name="id">UBL-CR-038</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-038]-A UBL invoice should not include the BillingReference SelfBilledInvoiceDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:CreditNoteDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:CreditNoteDocumentReference)">
          <xsl:attribute name="id">UBL-CR-039</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-039]-A UBL invoice should not include the BillingReference CreditNoteDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:SelfBilledCreditNoteDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:SelfBilledCreditNoteDocumentReference)">
          <xsl:attribute name="id">UBL-CR-040</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-040]-A UBL invoice should not include the BillingReference SelfBilledCreditNoteDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:DebitNoteDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:DebitNoteDocumentReference)">
          <xsl:attribute name="id">UBL-CR-041</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-041]-A UBL invoice should not include the BillingReference DebitNoteDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:ReminderDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:ReminderDocumentReference)">
          <xsl:attribute name="id">UBL-CR-042</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-042]-A UBL invoice should not include the BillingReference ReminderDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:AdditionalDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:AdditionalDocumentReference)">
          <xsl:attribute name="id">UBL-CR-043</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-043]-A UBL invoice should not include the BillingReference AdditionalDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:BillingReferenceLine)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:BillingReferenceLine)">
          <xsl:attribute name="id">UBL-CR-044</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-044]-A UBL invoice should not include the BillingReference BillingReferenceLine</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-045</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-045]-A UBL invoice should not include the DespatchDocumentReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-046</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-046]-A UBL invoice should not include the DespatchDocumentReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-047</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-047]-A UBL invoice should not include the DespatchDocumentReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-048</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-048]-A UBL invoice should not include the DespatchDocumentReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:DocumentTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:DocumentTypeCode)">
          <xsl:attribute name="id">UBL-CR-049</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-049]-A UBL invoice should not include the DespatchDocumentReference DocumentTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:DocumentType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:DocumentType)">
          <xsl:attribute name="id">UBL-CR-050</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-050]-A UBL invoice should not include the DespatchDocumentReference DocumentType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:Xpath)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:Xpath)">
          <xsl:attribute name="id">UBL-CR-051</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-051]-A UBL invoice should not include the DespatchDocumentReference Xpath</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:LanguageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:LanguageID)">
          <xsl:attribute name="id">UBL-CR-052</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-052]-A UBL invoice should not include the DespatchDocumentReference LanguageID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:LocaleCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:LocaleCode)">
          <xsl:attribute name="id">UBL-CR-053</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-053]-A UBL invoice should not include the DespatchDocumentReference LocaleCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:VersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:VersionID)">
          <xsl:attribute name="id">UBL-CR-054</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-054]-A UBL invoice should not include the DespatchDocumentReference VersionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:DocumentStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:DocumentStatusCode)">
          <xsl:attribute name="id">UBL-CR-055</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-055]-A UBL invoice should not include the DespatchDocumentReference DocumentStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cbc:DocumentDescription)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cbc:DocumentDescription)">
          <xsl:attribute name="id">UBL-CR-056</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-056]-A UBL invoice should not include the DespatchDocumentReference DocumentDescription</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cac:Attachment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cac:Attachment)">
          <xsl:attribute name="id">UBL-CR-057</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-057]-A UBL invoice should not include the DespatchDocumentReference Attachment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-058</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-058]-A UBL invoice should not include the DespatchDocumentReference ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-059</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-059]-A UBL invoice should not include the DespatchDocumentReference IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference/cac:ResultOfVerification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference/cac:ResultOfVerification)">
          <xsl:attribute name="id">UBL-CR-060</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-060]-A UBL invoice should not include the DespatchDocumentReference ResultOfVerification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-061</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-061]-A UBL invoice should not include the ReceiptDocumentReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-062</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-062]-A UBL invoice should not include the ReceiptDocumentReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-063</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-063]-A UBL invoice should not include the ReceiptDocumentReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-064</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-064]-A UBL invoice should not include the ReceiptDocumentReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:DocumentTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:DocumentTypeCode)">
          <xsl:attribute name="id">UBL-CR-065</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-065]-A UBL invoice should not include the ReceiptDocumentReference DocumentTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:DocumentType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:DocumentType)">
          <xsl:attribute name="id">UBL-CR-066</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-066]-A UBL invoice should not include the ReceiptDocumentReference DocumentType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:Xpath)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:Xpath)">
          <xsl:attribute name="id">UBL-CR-067</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-067]-A UBL invoice should not include the ReceiptDocumentReference Xpath</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:LanguageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:LanguageID)">
          <xsl:attribute name="id">UBL-CR-068</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-068]-A UBL invoice should not include the ReceiptDocumentReference LanguageID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:LocaleCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:LocaleCode)">
          <xsl:attribute name="id">UBL-CR-069</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-069]-A UBL invoice should not include the ReceiptDocumentReference LocaleCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:VersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:VersionID)">
          <xsl:attribute name="id">UBL-CR-070</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-070]-A UBL invoice should not include the ReceiptDocumentReference VersionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:DocumentStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:DocumentStatusCode)">
          <xsl:attribute name="id">UBL-CR-071</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-071]-A UBL invoice should not include the ReceiptDocumentReference DocumentStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cbc:DocumentDescription)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cbc:DocumentDescription)">
          <xsl:attribute name="id">UBL-CR-072</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-072]-A UBL invoice should not include the ReceiptDocumentReference DocumentDescription</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cac:Attachment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cac:Attachment)">
          <xsl:attribute name="id">UBL-CR-073</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-073]-A UBL invoice should not include the ReceiptDocumentReference Attachment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-074</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-074]-A UBL invoice should not include the ReceiptDocumentReference ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-075</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-075]-A UBL invoice should not include the ReceiptDocumentReference IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference/cac:ResultOfVerification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference/cac:ResultOfVerification)">
          <xsl:attribute name="id">UBL-CR-076</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-076]-A UBL invoice should not include the ReceiptDocumentReference ResultOfVerification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:StatementDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:StatementDocumentReference)">
          <xsl:attribute name="id">UBL-CR-077</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-077]-A UBL invoice should not include the StatementDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-078</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-078]-A UBL invoice should not include the OriginatorDocumentReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-079</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-079]-A UBL invoice should not include the OriginatorDocumentReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-080</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-080]-A UBL invoice should not include the OriginatorDocumentReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-081</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-081]-A UBL invoice should not include the OriginatorDocumentReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:DocumentTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:DocumentTypeCode)">
          <xsl:attribute name="id">UBL-CR-082</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-082]-A UBL invoice should not include the OriginatorDocumentReference DocumentTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:DocumentType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:DocumentType)">
          <xsl:attribute name="id">UBL-CR-083</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-083]-A UBL invoice should not include the OriginatorDocumentReference DocumentType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:Xpath)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:Xpath)">
          <xsl:attribute name="id">UBL-CR-084</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-084]-A UBL invoice should not include the OriginatorDocumentReference Xpath</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:LanguageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:LanguageID)">
          <xsl:attribute name="id">UBL-CR-085</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-085]-A UBL invoice should not include the OriginatorDocumentReference LanguageID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:LocaleCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:LocaleCode)">
          <xsl:attribute name="id">UBL-CR-086</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-086]-A UBL invoice should not include the OriginatorDocumentReference LocaleCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:VersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:VersionID)">
          <xsl:attribute name="id">UBL-CR-087</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-087]-A UBL invoice should not include the OriginatorDocumentReference VersionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:DocumentStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:DocumentStatusCode)">
          <xsl:attribute name="id">UBL-CR-088</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-088]-A UBL invoice should not include the OriginatorDocumentReference DocumentStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cbc:DocumentDescription)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cbc:DocumentDescription)">
          <xsl:attribute name="id">UBL-CR-089</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-089]-A UBL invoice should not include the OriginatorDocumentReference DocumentDescription</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cac:Attachment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cac:Attachment)">
          <xsl:attribute name="id">UBL-CR-090</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-090]-A UBL invoice should not include the OriginatorDocumentReference Attachment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-091</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-091]-A UBL invoice should not include the OriginatorDocumentReference ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-092</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-092]-A UBL invoice should not include the OriginatorDocumentReference IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference/cac:ResultOfVerification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference/cac:ResultOfVerification)">
          <xsl:attribute name="id">UBL-CR-093</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-093]-A UBL invoice should not include the OriginatorDocumentReference ResultOfVerification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-094</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-094]-A UBL invoice should not include the ContractDocumentReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-095</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-095]-A UBL invoice should not include the ContractDocumentReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-096</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-096]-A UBL invoice should not include the ContractDocumentReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-097</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-097]-A UBL invoice should not include the ContractDocumentReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:DocumentTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:DocumentTypeCode)">
          <xsl:attribute name="id">UBL-CR-098</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-098]-A UBL invoice should not include the ContractDocumentReference DocumentTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:DocumentType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:DocumentType)">
          <xsl:attribute name="id">UBL-CR-099</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-099]-A UBL invoice should not include the ContractDocumentReference DocumentType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:Xpath)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:Xpath)">
          <xsl:attribute name="id">UBL-CR-100</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-100]-A UBL invoice should not include the ContractDocumentReference Xpath</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:LanguageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:LanguageID)">
          <xsl:attribute name="id">UBL-CR-101</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-101]-A UBL invoice should not include the ContractDocumentReference LanguageID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:LocaleCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:LocaleCode)">
          <xsl:attribute name="id">UBL-CR-102</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-102]-A UBL invoice should not include the ContractDocumentReference LocaleCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:VersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:VersionID)">
          <xsl:attribute name="id">UBL-CR-103</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-103]-A UBL invoice should not include the ContractDocumentReference VersionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:DocumentStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:DocumentStatusCode)">
          <xsl:attribute name="id">UBL-CR-104</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-104]-A UBL invoice should not include the ContractDocumentReference DocumentStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:DocumentDescription)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:DocumentDescription)">
          <xsl:attribute name="id">UBL-CR-105</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-105]-A UBL invoice should not include the ContractDocumentReference DocumentDescription</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cac:Attachment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cac:Attachment)">
          <xsl:attribute name="id">UBL-CR-106</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-106]-A UBL invoice should not include the ContractDocumentReference Attachment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-107</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-107]-A UBL invoice should not include the ContractDocumentReference ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-108</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-108]-A UBL invoice should not include the ContractDocumentReference IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cac:ResultOfVerification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cac:ResultOfVerification)">
          <xsl:attribute name="id">UBL-CR-109</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-109]-A UBL invoice should not include the ContractDocumentReference ResultOfVerification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-110</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-110]-A UBL invoice should not include the AdditionalDocumentReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-111</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-111]-A UBL invoice should not include the AdditionalDocumentReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-112</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-112]-A UBL invoice should not include the AdditionalDocumentReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-113</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-113]-A UBL invoice should not include the AdditionalDocumentReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:DocumentType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:DocumentType)">
          <xsl:attribute name="id">UBL-CR-114</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-114]-A UBL invoice should not include the AdditionalDocumentReference DocumentType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:Xpath)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:Xpath)">
          <xsl:attribute name="id">UBL-CR-115</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-115]-A UBL invoice should not include the AdditionalDocumentReference Xpath</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:LanguageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:LanguageID)">
          <xsl:attribute name="id">UBL-CR-116</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-116]-A UBL invoice should not include the AdditionalDocumentReference LanguageID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:LocaleCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:LocaleCode)">
          <xsl:attribute name="id">UBL-CR-117</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-117]-A UBL invoice should not include the AdditionalDocumentReference LocaleCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:VersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:VersionID)">
          <xsl:attribute name="id">UBL-CR-118</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-118]-A UBL invoice should not include the AdditionalDocumentReference VersionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:DocumentStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:DocumentStatusCode)">
          <xsl:attribute name="id">UBL-CR-119</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-119]-A UBL invoice should not include the AdditionalDocumentReference DocumentStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash)">
          <xsl:attribute name="id">UBL-CR-121</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-121]-A UBL invoice should not include the AdditionalDocumentReference Attachment External DocumentHash</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:HashAlgorithmMethod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:HashAlgorithmMethod)">
          <xsl:attribute name="id">UBL-CR-122</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-122]-A UBL invoice should not include the AdditionalDocumentReference Attachment External HashAlgorithmMethod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate)">
          <xsl:attribute name="id">UBL-CR-123</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-123]-A UBL invoice should not include the AdditionalDocumentReference Attachment External ExpiryDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime)">
          <xsl:attribute name="id">UBL-CR-124</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-124]-A UBL invoice should not include the AdditionalDocumentReference Attachment External ExpiryTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:MimeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:MimeCode)">
          <xsl:attribute name="id">UBL-CR-125</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-125]-A UBL invoice should not include the AdditionalDocumentReference Attachment External MimeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:FormatCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:FormatCode)">
          <xsl:attribute name="id">UBL-CR-126</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-126]-A UBL invoice should not include the AdditionalDocumentReference Attachment External FormatCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:EncodingCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:EncodingCode)">
          <xsl:attribute name="id">UBL-CR-127</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-127]-A UBL invoice should not include the AdditionalDocumentReference Attachment External EncodingCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:CharacterSetCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:CharacterSetCode)">
          <xsl:attribute name="id">UBL-CR-128</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-128]-A UBL invoice should not include the AdditionalDocumentReference Attachment External CharacterSetCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:FileName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:FileName)">
          <xsl:attribute name="id">UBL-CR-129</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-129]-A UBL invoice should not include the AdditionalDocumentReference Attachment External FileName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:Descriprion)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:Descriprion)">
          <xsl:attribute name="id">UBL-CR-130</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-130]-A UBL invoice should not include the AdditionalDocumentReference Attachment External Descriprion</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-131</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-131]-A UBL invoice should not include the AdditionalDocumentReference ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-132</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-132]-A UBL invoice should not include the AdditionalDocumentReference IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:ResultOfVerification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:ResultOfVerification)">
          <xsl:attribute name="id">UBL-CR-133</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-133]-A UBL invoice should not include the AdditionalDocumentReference ResultOfVerification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ProjectReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ProjectReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-134</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-134]-A UBL invoice should not include the ProjectReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ProjectReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ProjectReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-135</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-135]-A UBL invoice should not include the ProjectReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ProjectReference/cac:WorkPhaseReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ProjectReference/cac:WorkPhaseReference)">
          <xsl:attribute name="id">UBL-CR-136</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-136]-A UBL invoice should not include the ProjectReference WorkPhaseReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Signature)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Signature)">
          <xsl:attribute name="id">UBL-CR-137</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-137]-A UBL invoice should not include the Signature</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID)">
          <xsl:attribute name="id">UBL-CR-138</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-138]-A UBL invoice should not include the AccountingSupplierParty CustomerAssignedAccountID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cbc:AdditionalAccountID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cbc:AdditionalAccountID)">
          <xsl:attribute name="id">UBL-CR-139</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-139]-A UBL invoice should not include the AccountingSupplierParty AdditionalAccountID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cbc:DataSendingCapability)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cbc:DataSendingCapability)">
          <xsl:attribute name="id">UBL-CR-140</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-140]-A UBL invoice should not include the AccountingSupplierParty DataSendingCapability</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator)">
          <xsl:attribute name="id">UBL-CR-141</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-141]-A UBL invoice should not include the AccountingSupplierParty Party MarkCareIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator)">
          <xsl:attribute name="id">UBL-CR-142</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-142]-A UBL invoice should not include the AccountingSupplierParty Party MarkAttentionIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI)">
          <xsl:attribute name="id">UBL-CR-143</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-143]-A UBL invoice should not include the AccountingSupplierParty Party WebsiteURI</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID)">
          <xsl:attribute name="id">UBL-CR-144</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-144]-A UBL invoice should not include the AccountingSupplierParty Party LogoReferenceID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:IndustryClassificationCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:IndustryClassificationCode)">
          <xsl:attribute name="id">UBL-CR-145</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-145]-A UBL invoice should not include the AccountingSupplierParty Party IndustryClassificationCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Language)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Language)">
          <xsl:attribute name="id">UBL-CR-146</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-146]-A UBL invoice should not include the AccountingSupplierParty Party Language</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-147</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-147]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)">
          <xsl:attribute name="id">UBL-CR-148</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-148]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress AddressTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)">
          <xsl:attribute name="id">UBL-CR-149</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-149]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress AddressFormatCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Postbox)">
          <xsl:attribute name="id">UBL-CR-150</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-150]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress Postbox</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)">
          <xsl:attribute name="id">UBL-CR-151</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-151]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress Floor</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)">
          <xsl:attribute name="id">UBL-CR-152</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-152]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress Room</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)">
          <xsl:attribute name="id">UBL-CR-153</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-153]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress BlockName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)">
          <xsl:attribute name="id">UBL-CR-154</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-154]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress BuildingName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber)">
          <xsl:attribute name="id">UBL-CR-155</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-155]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress BuildingNumber</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)">
          <xsl:attribute name="id">UBL-CR-156</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-156]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress InhouseMail</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Department)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Department)">
          <xsl:attribute name="id">UBL-CR-157</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-157]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress Department</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)">
          <xsl:attribute name="id">UBL-CR-158</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-158]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress MarkAttention</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)">
          <xsl:attribute name="id">UBL-CR-159</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-159]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress MarkCare</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)">
          <xsl:attribute name="id">UBL-CR-160</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-160]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress PlotIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)">
          <xsl:attribute name="id">UBL-CR-161</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-161]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress CitySubdivisionName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentityCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentityCode)">
          <xsl:attribute name="id">UBL-CR-162</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-162]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress CountrySubentityCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)">
          <xsl:attribute name="id">UBL-CR-163</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-163]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress Region</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District)">
          <xsl:attribute name="id">UBL-CR-164</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-164]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress District</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)">
          <xsl:attribute name="id">UBL-CR-165</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-165]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress TimezoneOffset</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-166</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-166]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress Country Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)">
          <xsl:attribute name="id">UBL-CR-167</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-167]-A UBL invoice should not include the AccountingSupplierParty Party PostalAddress LocationCoordinate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation)">
          <xsl:attribute name="id">UBL-CR-168</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-168]-A UBL invoice should not include the AccountingSupplierParty Party PhysicalLocation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)">
          <xsl:attribute name="id">UBL-CR-169</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-169]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme RegistrationName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)">
          <xsl:attribute name="id">UBL-CR-170</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-170]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme TaxLevelCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)">
          <xsl:attribute name="id">UBL-CR-171</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-171]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme ExemptionReasonCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)">
          <xsl:attribute name="id">UBL-CR-172</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-172]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme ExemptionReason</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)">
          <xsl:attribute name="id">UBL-CR-173</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-173]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme RegistrationAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-174</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-174]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme TaxScheme Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)">
          <xsl:attribute name="id">UBL-CR-175</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-175]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme TaxScheme TaxTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-176</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-176]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme TaxScheme CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)">
          <xsl:attribute name="id">UBL-CR-177</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-177]-A UBL invoice should not include the AccountingSupplierParty Party PartyTaxScheme TaxScheme JurisdictionRegionAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationDate)">
          <xsl:attribute name="id">UBL-CR-178</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-178]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity RegistrationDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationExpirationDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationExpirationDate)">
          <xsl:attribute name="id">UBL-CR-179</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-179]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity RegistrationExpirationDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalFormCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalFormCode)">
          <xsl:attribute name="id">UBL-CR-180</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-180]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity CompanyLegalFormCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:SoleProprietorshipIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:SoleProprietorshipIndicator)">
          <xsl:attribute name="id">UBL-CR-181</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-181]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity SoleProprietorshipIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLiquidationStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLiquidationStatusCode)">
          <xsl:attribute name="id">UBL-CR-182</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-182]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity CompanyLiquidationStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CorporationStockAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CorporationStockAmount)">
          <xsl:attribute name="id">UBL-CR-183</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-183]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity CorporationStockAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:FullyPaidSharesIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:FullyPaidSharesIndicator)">
          <xsl:attribute name="id">UBL-CR-184</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-184]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity FullyPaidSharesIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress)">
          <xsl:attribute name="id">UBL-CR-185</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-185]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity RegistrationAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)">
          <xsl:attribute name="id">UBL-CR-186</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-186]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity CorporateRegistrationScheme</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:HeadOfficeParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:HeadOfficeParty)">
          <xsl:attribute name="id">UBL-CR-187</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-187]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity HeadOfficeParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:ShareholderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:ShareholderParty)">
          <xsl:attribute name="id">UBL-CR-188</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-188]-A UBL invoice should not include the AccountingSupplierParty Party PartyLegalEntity ShareholderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-189</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-189]-A UBL invoice should not include the AccountingSupplierParty Party Contact ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telefax)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Telefax)">
          <xsl:attribute name="id">UBL-CR-190</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-190]-A UBL invoice should not include the AccountingSupplierParty Party Contact Telefax</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note)">
          <xsl:attribute name="id">UBL-CR-191</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-191]-A UBL invoice should not include the AccountingSupplierParty Party Contact Note</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)">
          <xsl:attribute name="id">UBL-CR-192</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-192]-A UBL invoice should not include the AccountingSupplierParty Party Contact OtherCommunication</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Person)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Person)">
          <xsl:attribute name="id">UBL-CR-193</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-193]-A UBL invoice should not include the AccountingSupplierParty Party Person</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty)">
          <xsl:attribute name="id">UBL-CR-194</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-194]-A UBL invoice should not include the AccountingSupplierParty Party AgentParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:ServiceProviderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:ServiceProviderParty)">
          <xsl:attribute name="id">UBL-CR-195</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-195]-A UBL invoice should not include the AccountingSupplierParty Party ServiceProviderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PowerOfAttorney)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PowerOfAttorney)">
          <xsl:attribute name="id">UBL-CR-196</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-196]-A UBL invoice should not include the AccountingSupplierParty Party PowerOfAttorney</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:FinancialAccount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:FinancialAccount)">
          <xsl:attribute name="id">UBL-CR-197</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-197]-A UBL invoice should not include the AccountingSupplierParty Party FinancialAccount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:DespatchContact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:DespatchContact)">
          <xsl:attribute name="id">UBL-CR-198</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-198]-A UBL invoice should not include the AccountingSupplierParty DespatchContact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:AccountingContact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:AccountingContact)">
          <xsl:attribute name="id">UBL-CR-199</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-199]-A UBL invoice should not include the AccountingSupplierParty AccountingContact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:SellerContact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:SellerContact)">
          <xsl:attribute name="id">UBL-CR-200</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-200]-A UBL invoice should not include the AccountingSupplierParty SellerContact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID)">
          <xsl:attribute name="id">UBL-CR-201</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-201]-A UBL invoice should not include the AccountingCustomerParty CustomerAssignedAccountID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID)">
          <xsl:attribute name="id">UBL-CR-202</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-202]-A UBL invoice should not include the AccountingCustomerParty SupplierAssignedAccountID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cbc:AdditionalAccountID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cbc:AdditionalAccountID)">
          <xsl:attribute name="id">UBL-CR-203</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-203]-A UBL invoice should not include the AccountingCustomerParty AdditionalAccountID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator)">
          <xsl:attribute name="id">UBL-CR-204</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-204]-A UBL invoice should not include the AccountingCustomerParty Party MarkCareIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator)">
          <xsl:attribute name="id">UBL-CR-205</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-205]-A UBL invoice should not include the AccountingCustomerParty Party MarkAttentionIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI)">
          <xsl:attribute name="id">UBL-CR-206</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-206]-A UBL invoice should not include the AccountingCustomerParty Party WebsiteURI</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID)">
          <xsl:attribute name="id">UBL-CR-207</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-207]-A UBL invoice should not include the AccountingCustomerParty Party LogoReferenceID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:IndustryClassificationCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:IndustryClassificationCode)">
          <xsl:attribute name="id">UBL-CR-208</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-208]-A UBL invoice should not include the AccountingCustomerParty Party IndustryClassificationCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Language)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Language)">
          <xsl:attribute name="id">UBL-CR-209</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-209]-A UBL invoice should not include the AccountingCustomerParty Party Language</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-210</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-210]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)">
          <xsl:attribute name="id">UBL-CR-211</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-211]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress AddressTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)">
          <xsl:attribute name="id">UBL-CR-212</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-212]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress AddressFormatCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Postbox)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Postbox)">
          <xsl:attribute name="id">UBL-CR-213</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-213]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress Postbox</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)">
          <xsl:attribute name="id">UBL-CR-214</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-214]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress Floor</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)">
          <xsl:attribute name="id">UBL-CR-215</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-215]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress Room</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)">
          <xsl:attribute name="id">UBL-CR-216</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-216]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress BlockName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)">
          <xsl:attribute name="id">UBL-CR-217</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-217]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress BuildingName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingNumber)">
          <xsl:attribute name="id">UBL-CR-218</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-218]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress BuildingNumber</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)">
          <xsl:attribute name="id">UBL-CR-219</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-219]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress InhouseMail</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Department)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Department)">
          <xsl:attribute name="id">UBL-CR-220</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-220]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress Department</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)">
          <xsl:attribute name="id">UBL-CR-221</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-221]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress MarkAttention</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)">
          <xsl:attribute name="id">UBL-CR-222</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-222]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress MarkCare</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)">
          <xsl:attribute name="id">UBL-CR-223</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-223]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress PlotIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)">
          <xsl:attribute name="id">UBL-CR-224</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-224]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress CitySubdivisionName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentityCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentityCode)">
          <xsl:attribute name="id">UBL-CR-225</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-225]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress CountrySubentityCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)">
          <xsl:attribute name="id">UBL-CR-226</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-226]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress Region</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District)">
          <xsl:attribute name="id">UBL-CR-227</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-227]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress District</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)">
          <xsl:attribute name="id">UBL-CR-228</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-228]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress TimezoneOffset</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-229</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-229]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress Country Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)">
          <xsl:attribute name="id">UBL-CR-230</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-230]-A UBL invoice should not include the AccountingCustomerParty Party PostalAddress LocationCoordinate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation)">
          <xsl:attribute name="id">UBL-CR-231</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-231]-A UBL invoice should not include the AccountingCustomerParty Party PhysicalLocation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)">
          <xsl:attribute name="id">UBL-CR-232</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-232]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme RegistrationName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)">
          <xsl:attribute name="id">UBL-CR-233</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-233]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme TaxLevelCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)">
          <xsl:attribute name="id">UBL-CR-234</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-234]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme ExemptionReasonCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)">
          <xsl:attribute name="id">UBL-CR-235</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-235]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme ExemptionReason</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)">
          <xsl:attribute name="id">UBL-CR-236</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-236]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme RegistrationAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-237</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-237]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme TaxScheme Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)">
          <xsl:attribute name="id">UBL-CR-238</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-238]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme TaxScheme TaxTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-239</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-239]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme TaxScheme CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)">
          <xsl:attribute name="id">UBL-CR-240</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-240]-A UBL invoice should not include the AccountingCustomerParty Party PartyTaxScheme TaxScheme JurisdictionRegionAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationDate)">
          <xsl:attribute name="id">UBL-CR-241</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-241]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity RegistrationDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationExpirationDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationExpirationDate)">
          <xsl:attribute name="id">UBL-CR-242</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-242]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity RegistrationExpirationDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalFormCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalFormCode)">
          <xsl:attribute name="id">UBL-CR-243</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-243]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity CompanyLegalFormCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalForm)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalForm)">
          <xsl:attribute name="id">UBL-CR-244</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-244]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity CompanyLegalForm</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:SoleProprietorshipIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:SoleProprietorshipIndicator)">
          <xsl:attribute name="id">UBL-CR-245</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-245]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity SoleProprietorshipIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLiquidationStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLiquidationStatusCode)">
          <xsl:attribute name="id">UBL-CR-246</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-246]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity CompanyLiquidationStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CorporationStockAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CorporationStockAmount)">
          <xsl:attribute name="id">UBL-CR-247</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-247]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity CorporationStockAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:FullyPaidSharesIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:FullyPaidSharesIndicator)">
          <xsl:attribute name="id">UBL-CR-248</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-248]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity FullyPaidSharesIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress)">
          <xsl:attribute name="id">UBL-CR-249</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-249]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity RegistrationAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)">
          <xsl:attribute name="id">UBL-CR-250</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-250]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity CorporateRegistrationScheme</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:HeadOfficeParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:HeadOfficeParty)">
          <xsl:attribute name="id">UBL-CR-251</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-251]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity HeadOfficeParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:ShareholderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:ShareholderParty)">
          <xsl:attribute name="id">UBL-CR-252</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-252]-A UBL invoice should not include the AccountingCustomerParty Party PartyLegalEntity ShareholderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-253</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-253]-A UBL invoice should not include the AccountingCustomerParty Party Contact ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telefax)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telefax)">
          <xsl:attribute name="id">UBL-CR-254</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-254]-A UBL invoice should not include the AccountingCustomerParty Party Contact Telefax</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note)">
          <xsl:attribute name="id">UBL-CR-255</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-255]-A UBL invoice should not include the AccountingCustomerParty Party Contact Note</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)">
          <xsl:attribute name="id">UBL-CR-256</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-256]-A UBL invoice should not include the AccountingCustomerParty Party Contact OtherCommunication</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Person)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Person)">
          <xsl:attribute name="id">UBL-CR-257</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-257]-A UBL invoice should not include the AccountingCustomerParty Party Person</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty)">
          <xsl:attribute name="id">UBL-CR-258</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-258]-A UBL invoice should not include the AccountingCustomerParty Party AgentParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:ServiceProviderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:ServiceProviderParty)">
          <xsl:attribute name="id">UBL-CR-259</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-259]-A UBL invoice should not include the AccountingCustomerParty Party ServiceProviderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PowerOfAttorney)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PowerOfAttorney)">
          <xsl:attribute name="id">UBL-CR-260</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-260]-A UBL invoice should not include the AccountingCustomerParty Party PowerOfAttorney</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:FinancialAccount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:FinancialAccount)">
          <xsl:attribute name="id">UBL-CR-261</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-261]-A UBL invoice should not include the AccountingCustomerParty Party FinancialAccount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:DeliveryContact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:DeliveryContact)">
          <xsl:attribute name="id">UBL-CR-262</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-262]-A UBL invoice should not include the AccountingCustomerParty DeliveryContact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:AccountingContact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:AccountingContact)">
          <xsl:attribute name="id">UBL-CR-263</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-263]-A UBL invoice should not include the AccountingCustomerParty AccountingContact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:BuyerContact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:BuyerContact)">
          <xsl:attribute name="id">UBL-CR-264</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-264]-A UBL invoice should not include the AccountingCustomerParty BuyerContact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:MarkCareIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:MarkCareIndicator)">
          <xsl:attribute name="id">UBL-CR-265</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-265]-A UBL invoice should not include the PayeeParty MarkCareIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:MarkAttentionIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:MarkAttentionIndicator)">
          <xsl:attribute name="id">UBL-CR-266</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-266]-A UBL invoice should not include the PayeeParty MarkAttentionIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:WebsiteURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:WebsiteURI)">
          <xsl:attribute name="id">UBL-CR-267</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-267]-A UBL invoice should not include the PayeeParty WebsiteURI</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:LogoReferenceID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:LogoReferenceID)">
          <xsl:attribute name="id">UBL-CR-268</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-268]-A UBL invoice should not include the PayeeParty LogoReferenceID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:EndpointID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:EndpointID)">
          <xsl:attribute name="id">UBL-CR-269</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-269]-A UBL invoice should not include the PayeeParty EndpointID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:IndustryClassificationCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:IndustryClassificationCode)">
          <xsl:attribute name="id">UBL-CR-270</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-270]-A UBL invoice should not include the PayeeParty IndustryClassificationCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:Language)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:Language)">
          <xsl:attribute name="id">UBL-CR-271</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-271]-A UBL invoice should not include the PayeeParty Language</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PostalAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PostalAddress)">
          <xsl:attribute name="id">UBL-CR-272</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-272]-A UBL invoice should not include the PayeeParty PostalAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PhysicalLocation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PhysicalLocation)">
          <xsl:attribute name="id">UBL-CR-273</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-273]-A UBL invoice should not include the PayeeParty PhysicalLocation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyTaxScheme)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyTaxScheme)">
          <xsl:attribute name="id">UBL-CR-274</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-274]-A UBL invoice should not include the PayeeParty PartyTaxScheme</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName)">
          <xsl:attribute name="id">UBL-CR-275</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-275]-A UBL invoice should not include the PayeeParty PartyLegalEntity RegistrationName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationDate)">
          <xsl:attribute name="id">UBL-CR-276</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-276]-A UBL invoice should not include the PayeeParty PartyLegalEntity RegistrationDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationExpirationDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationExpirationDate)">
          <xsl:attribute name="id">UBL-CR-277</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-277]-A UBL invoice should not include the PayeeParty PartyLegalEntity RegistrationExpirationDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyLegalFormCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyLegalFormCode)">
          <xsl:attribute name="id">UBL-CR-278</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-278]-A UBL invoice should not include the PayeeParty PartyLegalEntity CompanyLegalFormCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyLegalForm)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyLegalForm)">
          <xsl:attribute name="id">UBL-CR-279</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-279]-A UBL invoice should not include the PayeeParty PartyLegalEntity CompanyLegalForm</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:SoleProprietorshipIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:SoleProprietorshipIndicator)">
          <xsl:attribute name="id">UBL-CR-280</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-280]-A UBL invoice should not include the PayeeParty PartyLegalEntity SoleProprietorshipIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyLiquidationStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CompanyLiquidationStatusCode)">
          <xsl:attribute name="id">UBL-CR-281</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-281]-A UBL invoice should not include the PayeeParty PartyLegalEntity CompanyLiquidationStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CorporationStockAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:CorporationStockAmount)">
          <xsl:attribute name="id">UBL-CR-282</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-282]-A UBL invoice should not include the PayeeParty PartyLegalEntity CorporationStockAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:FullyPaidSharesIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:FullyPaidSharesIndicator)">
          <xsl:attribute name="id">UBL-CR-283</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-283]-A UBL invoice should not include the PayeeParty PartyLegalEntity FullyPaidSharesIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress)">
          <xsl:attribute name="id">UBL-CR-284</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-284]-A UBL invoice should not include the PayeeParty PartyLegalEntity RegistrationAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)">
          <xsl:attribute name="id">UBL-CR-285</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-285]-A UBL invoice should not include the PayeeParty PartyLegalEntity CorporateRegistrationScheme</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:HeadOfficeParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:HeadOfficeParty)">
          <xsl:attribute name="id">UBL-CR-286</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-286]-A UBL invoice should not include the PayeeParty PartyLegalEntity HeadOfficeParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:ShareholderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:ShareholderParty)">
          <xsl:attribute name="id">UBL-CR-287</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-287]-A UBL invoice should not include the PayeeParty PartyLegalEntity ShareholderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:Contact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:Contact)">
          <xsl:attribute name="id">UBL-CR-288</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-288]-A UBL invoice should not include the PayeeParty Contact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:Person)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:Person)">
          <xsl:attribute name="id">UBL-CR-289</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-289]-A UBL invoice should not include the PayeeParty Person</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:AgentParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:AgentParty)">
          <xsl:attribute name="id">UBL-CR-290</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-290]-A UBL invoice should not include the PayeeParty AgentParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:ServiceProviderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:ServiceProviderParty)">
          <xsl:attribute name="id">UBL-CR-291</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-291]-A UBL invoice should not include the PayeeParty ServiceProviderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PowerOfAttorney)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PowerOfAttorney)">
          <xsl:attribute name="id">UBL-CR-292</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-292]-A UBL invoice should not include the PayeeParty PowerOfAttorney</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:FinancialAccount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:FinancialAccount)">
          <xsl:attribute name="id">UBL-CR-293</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-293]-A UBL invoice should not include the PayeeParty FinancialAccount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BuyerCustomerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BuyerCustomerParty)">
          <xsl:attribute name="id">UBL-CR-294</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-294]-A UBL invoice should not include the BuyerCustomerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:SellerCustomerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:SellerCustomerParty)">
          <xsl:attribute name="id">UBL-CR-295</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-295]-A UBL invoice should not include the SellerCustomerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cbc:MarkCareIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cbc:MarkCareIndicator)">
          <xsl:attribute name="id">UBL-CR-296</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-296]-A UBL invoice should not include the TaxRepresentativeParty MarkCareIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cbc:MarkAttentionIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cbc:MarkAttentionIndicator)">
          <xsl:attribute name="id">UBL-CR-297</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-297]-A UBL invoice should not include the TaxRepresentativeParty MarkAttentionIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cbc:WebsiteURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cbc:WebsiteURI)">
          <xsl:attribute name="id">UBL-CR-298</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-298]-A UBL invoice should not include the TaxRepresentativeParty WebsiteURI</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cbc:LogoReferenceID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cbc:LogoReferenceID)">
          <xsl:attribute name="id">UBL-CR-299</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-299]-A UBL invoice should not include the TaxRepresentativeParty LogoReferenceID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cbc:EndpointID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cbc:EndpointID)">
          <xsl:attribute name="id">UBL-CR-300</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-300]-A UBL invoice should not include the TaxRepresentativeParty EndpointID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cbc:IndustryClassificationCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cbc:IndustryClassificationCode)">
          <xsl:attribute name="id">UBL-CR-301</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-301]-A UBL invoice should not include the TaxRepresentativeParty IndustryClassificationCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyIdentification)">
          <xsl:attribute name="id">UBL-CR-302</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-302]-A UBL invoice should not include the TaxRepresentativeParty PartyIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:Language)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:Language)">
          <xsl:attribute name="id">UBL-CR-303</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-303]-A UBL invoice should not include the TaxRepresentativeParty Language</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-304</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-304]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:AddressTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:AddressTypeCode)">
          <xsl:attribute name="id">UBL-CR-305</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-305]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress AddressTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:AddressFormatCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:AddressFormatCode)">
          <xsl:attribute name="id">UBL-CR-306</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-306]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress AddressFormatCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Postbox)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Postbox)">
          <xsl:attribute name="id">UBL-CR-307</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-307]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress Postbox</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Floor)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Floor)">
          <xsl:attribute name="id">UBL-CR-308</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-308]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress Floor</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Room)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Room)">
          <xsl:attribute name="id">UBL-CR-309</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-309]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress Room</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:BlockName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:BlockName)">
          <xsl:attribute name="id">UBL-CR-310</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-310]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress BlockName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:BuildingName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:BuildingName)">
          <xsl:attribute name="id">UBL-CR-311</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-311]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress BuildingName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:BuildingNumber)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:BuildingNumber)">
          <xsl:attribute name="id">UBL-CR-312</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-312]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress BuildingNumber</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:InhouseMail)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:InhouseMail)">
          <xsl:attribute name="id">UBL-CR-313</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-313]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress InhouseMail</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Department)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Department)">
          <xsl:attribute name="id">UBL-CR-314</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-314]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress Department</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:MarkAttention)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:MarkAttention)">
          <xsl:attribute name="id">UBL-CR-315</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-315]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress MarkAttention</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:MarkCare)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:MarkCare)">
          <xsl:attribute name="id">UBL-CR-316</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-316]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress MarkCare</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:PlotIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:PlotIdentification)">
          <xsl:attribute name="id">UBL-CR-317</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-317]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress PlotIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:CitySubdivisionName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:CitySubdivisionName)">
          <xsl:attribute name="id">UBL-CR-318</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-318]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress CitySubdivisionName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:CountrySubentityCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:CountrySubentityCode)">
          <xsl:attribute name="id">UBL-CR-319</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-319]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress CountrySubentityCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Region)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:Region)">
          <xsl:attribute name="id">UBL-CR-320</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-320]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress Region</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:District)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:District)">
          <xsl:attribute name="id">UBL-CR-321</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-321]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress District</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:TimezoneOffset)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cbc:TimezoneOffset)">
          <xsl:attribute name="id">UBL-CR-322</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-322]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress TimezoneOffset</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cac:Country/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cac:Country/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-323</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-323]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress Country Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cac:LocationCoordinate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PostalAddress/cac:LocationCoordinate)">
          <xsl:attribute name="id">UBL-CR-324</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-324]-A UBL invoice should not include the TaxRepresentativeParty PostalAddress LocationCoordinate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PhysicalLocation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PhysicalLocation)">
          <xsl:attribute name="id">UBL-CR-325</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-325]-A UBL invoice should not include the TaxRepresentativeParty PhysicalLocation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:RegistrationName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:RegistrationName)">
          <xsl:attribute name="id">UBL-CR-326</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-326]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme RegistrationName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:TaxLevelCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:TaxLevelCode)">
          <xsl:attribute name="id">UBL-CR-327</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-327]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme TaxLevelCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:ExemptionReasonCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:ExemptionReasonCode)">
          <xsl:attribute name="id">UBL-CR-328</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-328]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme ExemptionReasonCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:ExemptionReason)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:ExemptionReason)">
          <xsl:attribute name="id">UBL-CR-329</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-329]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme ExemptionReason</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:RegistrationAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:RegistrationAddress)">
          <xsl:attribute name="id">UBL-CR-330</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-330]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme RegistrationAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-331</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-331]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme TaxScheme Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)">
          <xsl:attribute name="id">UBL-CR-332</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-332]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme TaxScheme TaxTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-333</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-333]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme TaxScheme CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)">
          <xsl:attribute name="id">UBL-CR-334</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-334]-A UBL invoice should not include the TaxRepresentativeParty PartyTaxScheme TaxScheme JurisdictionRegionAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PartyLegalEntity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PartyLegalEntity)">
          <xsl:attribute name="id">UBL-CR-335</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-335]-A UBL invoice should not include the TaxRepresentativeParty PartyLegalEntity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:Contact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:Contact)">
          <xsl:attribute name="id">UBL-CR-336</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-336]-A UBL invoice should not include the TaxRepresentativeParty Contact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:Person)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:Person)">
          <xsl:attribute name="id">UBL-CR-337</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-337]-A UBL invoice should not include the TaxRepresentativeParty Person</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:AgentParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:AgentParty)">
          <xsl:attribute name="id">UBL-CR-338</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-338]-A UBL invoice should not include the TaxRepresentativeParty AgentParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:ServiceProviderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:ServiceProviderParty)">
          <xsl:attribute name="id">UBL-CR-339</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-339]-A UBL invoice should not include the TaxRepresentativeParty ServiceProviderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:PowerOfAttorney)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:PowerOfAttorney)">
          <xsl:attribute name="id">UBL-CR-340</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-340]-A UBL invoice should not include the TaxRepresentativeParty PowerOfAttorney</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty/cac:FinancialAccount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty/cac:FinancialAccount)">
          <xsl:attribute name="id">UBL-CR-341</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-341]-A UBL invoice should not include the TaxRepresentativeParty FinancialAccount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-342</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-342]-A UBL invoice should not include the Delivery ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:Quantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:Quantity)">
          <xsl:attribute name="id">UBL-CR-343</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-343]-A UBL invoice should not include the Delivery Quantity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:MinimumQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:MinimumQuantity)">
          <xsl:attribute name="id">UBL-CR-344</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-344]-A UBL invoice should not include the Delivery MinimumQuantity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:MaximumQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:MaximumQuantity)">
          <xsl:attribute name="id">UBL-CR-345</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-345]-A UBL invoice should not include the Delivery MaximumQuantity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:ActualDeliveryTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:ActualDeliveryTime)">
          <xsl:attribute name="id">UBL-CR-346</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-346]-A UBL invoice should not include the Delivery ActualDeliveryTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:LatestDeliveryDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:LatestDeliveryDate)">
          <xsl:attribute name="id">UBL-CR-347</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-347]-A UBL invoice should not include the Delivery LatestDeliveryDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:LatestDeliveryTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:LatestDeliveryTime)">
          <xsl:attribute name="id">UBL-CR-348</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-348]-A UBL invoice should not include the Delivery LatestDeliveryTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:ReleaseID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:ReleaseID)">
          <xsl:attribute name="id">UBL-CR-349</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-349]-A UBL invoice should not include the Delivery ReleaseID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:TrackingID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:TrackingID)">
          <xsl:attribute name="id">UBL-CR-350</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-350]-A UBL invoice should not include the Delivery TrackingID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:Description)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:Description)">
          <xsl:attribute name="id">UBL-CR-351</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-351]-A UBL invoice should not include the Delivery DeliveryLocation Description</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions)">
          <xsl:attribute name="id">UBL-CR-352</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-352]-A UBL invoice should not include the Delivery DeliveryLocation Conditions</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity)">
          <xsl:attribute name="id">UBL-CR-353</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-353]-A UBL invoice should not include the Delivery DeliveryLocation CountrySubentity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode)">
          <xsl:attribute name="id">UBL-CR-354</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-354]-A UBL invoice should not include the Delivery DeliveryLocation CountrySubentityCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:LocationTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:LocationTypeCode)">
          <xsl:attribute name="id">UBL-CR-355</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-355]-A UBL invoice should not include the Delivery DeliveryLocation LocationTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:InformationURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:InformationURI)">
          <xsl:attribute name="id">UBL-CR-356</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-356]-A UBL invoice should not include the Delivery DeliveryLocation InformationURI</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-357</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-357]-A UBL invoice should not include the Delivery DeliveryLocation Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:ValidationPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:ValidationPeriod)">
          <xsl:attribute name="id">UBL-CR-358</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-358]-A UBL invoice should not include the Delivery DeliveryLocation ValidationPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-359</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-359]-A UBL invoice should not include the Delivery DeliveryLocation Address ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode)">
          <xsl:attribute name="id">UBL-CR-360</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-360]-A UBL invoice should not include the Delivery DeliveryLocation Address AddressTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode)">
          <xsl:attribute name="id">UBL-CR-361</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-361]-A UBL invoice should not include the Delivery DeliveryLocation Address AddressFormatCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Postbox)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Postbox)">
          <xsl:attribute name="id">UBL-CR-362</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-362]-A UBL invoice should not include the Delivery DeliveryLocation Address Postbox</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor)">
          <xsl:attribute name="id">UBL-CR-363</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-363]-A UBL invoice should not include the Delivery DeliveryLocation Address Floor</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room)">
          <xsl:attribute name="id">UBL-CR-364</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-364]-A UBL invoice should not include the Delivery DeliveryLocation Address Room</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName)">
          <xsl:attribute name="id">UBL-CR-365</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-365]-A UBL invoice should not include the Delivery DeliveryLocation Address BlockName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName)">
          <xsl:attribute name="id">UBL-CR-366</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-366]-A UBL invoice should not include the Delivery DeliveryLocation Address BuildingName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingNumber)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingNumber)">
          <xsl:attribute name="id">UBL-CR-367</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-367]-A UBL invoice should not include the Delivery DeliveryLocation Address BuildingNumber</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail)">
          <xsl:attribute name="id">UBL-CR-368</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-368]-A UBL invoice should not include the Delivery DeliveryLocation Address InhouseMail</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department)">
          <xsl:attribute name="id">UBL-CR-369</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-369]-A UBL invoice should not include the Delivery DeliveryLocation Address Department</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention)">
          <xsl:attribute name="id">UBL-CR-370</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-370]-A UBL invoice should not include the Delivery DeliveryLocation Address MarkAttention</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare)">
          <xsl:attribute name="id">UBL-CR-371</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-371]-A UBL invoice should not include the Delivery DeliveryLocation Address MarkCare</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification)">
          <xsl:attribute name="id">UBL-CR-372</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-372]-A UBL invoice should not include the Delivery DeliveryLocation Address PlotIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName)">
          <xsl:attribute name="id">UBL-CR-373</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-373]-A UBL invoice should not include the Delivery DeliveryLocation Address CitySubdivisionName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode)">
          <xsl:attribute name="id">UBL-CR-374</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-374]-A UBL invoice should not include the Delivery DeliveryLocation Address CountrySubentityCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region)">
          <xsl:attribute name="id">UBL-CR-375</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-375]-A UBL invoice should not include the Delivery DeliveryLocation Address Region</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District)">
          <xsl:attribute name="id">UBL-CR-376</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-376]-A UBL invoice should not include the Delivery DeliveryLocation Address District</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset)">
          <xsl:attribute name="id">UBL-CR-377</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-377]-A UBL invoice should not include the Delivery DeliveryLocation Address TimezoneOffset</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-378</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-378]-A UBL invoice should not include the Delivery DeliveryLocation Address Country Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate)">
          <xsl:attribute name="id">UBL-CR-379</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-379]-A UBL invoice should not include the Delivery DeliveryLocation Address LocationCoordinate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:SubsidiaryLocation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:SubsidiaryLocation)">
          <xsl:attribute name="id">UBL-CR-380</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-380]-A UBL invoice should not include the Delivery DeliveryLocation SubsidiaryLocation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:LocationCoordinate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:LocationCoordinate)">
          <xsl:attribute name="id">UBL-CR-381</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-381]-A UBL invoice should not include the Delivery DeliveryLocation LocationCoordinate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:AlternativeDeliveryLocation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:AlternativeDeliveryLocation)">
          <xsl:attribute name="id">UBL-CR-382</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-382]-A UBL invoice should not include the Delivery AlternativeDeliveryLocation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:RequestedDeliveryPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:RequestedDeliveryPeriod)">
          <xsl:attribute name="id">UBL-CR-383</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-383]-A UBL invoice should not include the Delivery RequestedDeliveryPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:PromisedDeliveryPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:PromisedDeliveryPeriod)">
          <xsl:attribute name="id">UBL-CR-384</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-384]-A UBL invoice should not include the Delivery PromisedDeliveryPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:CarrierParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:CarrierParty)">
          <xsl:attribute name="id">UBL-CR-385</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-385]-A UBL invoice should not include the Delivery CarrierParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cbc:MarkCareIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cbc:MarkCareIndicator)">
          <xsl:attribute name="id">UBL-CR-386</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-386]-A UBL invoice should not include the DeliveryParty MarkCareIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cbc:MarkAttentionIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cbc:MarkAttentionIndicator)">
          <xsl:attribute name="id">UBL-CR-387</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-387]-A UBL invoice should not include the DeliveryParty MarkAttentionIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cbc:WebsiteURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cbc:WebsiteURI)">
          <xsl:attribute name="id">UBL-CR-388</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-388]-A UBL invoice should not include the DeliveryParty WebsiteURI</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cbc:LogoReferenceID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cbc:LogoReferenceID)">
          <xsl:attribute name="id">UBL-CR-389</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-389]-A UBL invoice should not include the DeliveryParty LogoReferenceID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cbc:EndpointID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cbc:EndpointID)">
          <xsl:attribute name="id">UBL-CR-390</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-390]-A UBL invoice should not include the DeliveryParty EndpointID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cbc:IndustryClassificationCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cbc:IndustryClassificationCode)">
          <xsl:attribute name="id">UBL-CR-391</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-391]-A UBL invoice should not include the DeliveryParty IndustryClassificationCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:PartyIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:PartyIdentification)">
          <xsl:attribute name="id">UBL-CR-392</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-392]-A UBL invoice should not include the DeliveryParty PartyIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:Language)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:Language)">
          <xsl:attribute name="id">UBL-CR-393</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-393]-A UBL invoice should not include the DeliveryParty Language</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:PostalAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:PostalAddress)">
          <xsl:attribute name="id">UBL-CR-394</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-394]-A UBL invoice should not include the DeliveryParty PostalAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:PhysicalLocation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:PhysicalLocation)">
          <xsl:attribute name="id">UBL-CR-395</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-395]-A UBL invoice should not include the DeliveryParty PhysicalLocation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:PartyTaxScheme)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:PartyTaxScheme)">
          <xsl:attribute name="id">UBL-CR-396</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-396]-A UBL invoice should not include the DeliveryParty PartyTaxScheme</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:PartyLegalEntity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:PartyLegalEntity)">
          <xsl:attribute name="id">UBL-CR-397</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-397]-A UBL invoice should not include the DeliveryParty PartyLegalEntity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:Contact)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:Contact)">
          <xsl:attribute name="id">UBL-CR-398</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-398]-A UBL invoice should not include the DeliveryParty Contact</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:Person)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:Person)">
          <xsl:attribute name="id">UBL-CR-399</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-399]-A UBL invoice should not include the DeliveryParty Person</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:AgentParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:AgentParty)">
          <xsl:attribute name="id">UBL-CR-400</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-400]-A UBL invoice should not include the DeliveryParty AgentParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:ServiceProviderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:ServiceProviderParty)">
          <xsl:attribute name="id">UBL-CR-401</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-401]-A UBL invoice should not include the DeliveryParty ServiceProviderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:PowerOfAttorney)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:PowerOfAttorney)">
          <xsl:attribute name="id">UBL-CR-402</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-402]-A UBL invoice should not include the DeliveryParty PowerOfAttorney</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryParty/cac:FinancialAccount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryParty/cac:FinancialAccount)">
          <xsl:attribute name="id">UBL-CR-403</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-403]-A UBL invoice should not include the DeliveryParty FinancialAccount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:NotifyParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:NotifyParty)">
          <xsl:attribute name="id">UBL-CR-404</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-404]-A UBL invoice should not include the Delivery NotifyParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:Despatch)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:Despatch)">
          <xsl:attribute name="id">UBL-CR-405</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-405]-A UBL invoice should not include the Delivery Despatch</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryTerms)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryTerms)">
          <xsl:attribute name="id">UBL-CR-406</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-406]-A UBL invoice should not include the Delivery DeliveryTerms</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:MinimumDeliveryUnit)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:MinimumDeliveryUnit)">
          <xsl:attribute name="id">UBL-CR-407</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-407]-A UBL invoice should not include the Delivery MinimumDeliveryUnit</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:MaximumDeliveryUnit)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:MaximumDeliveryUnit)">
          <xsl:attribute name="id">UBL-CR-408</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-408]-A UBL invoice should not include the Delivery MaximumDeliveryUnit</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:Shipment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:Shipment)">
          <xsl:attribute name="id">UBL-CR-409</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-409]-A UBL invoice should not include the Delivery Shipment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DeliveryTerms)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DeliveryTerms)">
          <xsl:attribute name="id">UBL-CR-410</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-410]-A UBL invoice should not include the DeliveryTerms</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-411</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-411]-A UBL invoice should not include the PaymentMeans ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cbc:PaymentDueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cbc:PaymentDueDate)">
          <xsl:attribute name="id">UBL-CR-412</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-412]-A UBL invoice should not include the PaymentMeans PaymentDueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cbc:PaymentChannelCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cbc:PaymentChannelCode)">
          <xsl:attribute name="id">UBL-CR-413</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-413]-A UBL invoice should not include the PaymentMeans PaymentChannelCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cbc:InstructionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cbc:InstructionID)">
          <xsl:attribute name="id">UBL-CR-414</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-414]-A UBL invoice should not include the PaymentMeans InstructionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:CardTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:CardTypeCode)">
          <xsl:attribute name="id">UBL-CR-415</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-415]-A UBL invoice should not include the PaymentMeans CardAccount CardTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:ValidityStartDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:ValidityStartDate)">
          <xsl:attribute name="id">UBL-CR-416</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-416]-A UBL invoice should not include the PaymentMeans CardAccount ValidityStartDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:ExpiryDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:ExpiryDate)">
          <xsl:attribute name="id">UBL-CR-417</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-417]-A UBL invoice should not include the PaymentMeans CardAccount ExpiryDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:IssuerID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:IssuerID)">
          <xsl:attribute name="id">UBL-CR-418</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-418]-A UBL invoice should not include the PaymentMeans CardAccount IssuerID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:IssuerNumberID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:IssuerNumberID)">
          <xsl:attribute name="id">UBL-CR-419</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-419]-A UBL invoice should not include the PaymentMeans CardAccount IssuerNumberID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:CV2ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:CV2ID)">
          <xsl:attribute name="id">UBL-CR-420</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-420]-A UBL invoice should not include the PaymentMeans CardAccount CV2ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:CardChipCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:CardChipCode)">
          <xsl:attribute name="id">UBL-CR-421</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-421]-A UBL invoice should not include the PaymentMeans CardAccount CardChipCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount/cbc:ChipApplicationID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount/cbc:ChipApplicationID)">
          <xsl:attribute name="id">UBL-CR-422</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-422]-A UBL invoice should not include the PaymentMeans CardAccount ChipApplicationID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayerFinancialAccount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayerFinancialAccount)">
          <xsl:attribute name="id">UBL-CR-423</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-423]-A UBL invoice should not include the PaymentMeans PayerFinancialAccount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AliasName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AliasName)">
          <xsl:attribute name="id">UBL-CR-424</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-424]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount AliasName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode)">
          <xsl:attribute name="id">UBL-CR-425</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-425]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount AccountTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountFormatCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountFormatCode)">
          <xsl:attribute name="id">UBL-CR-426</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-426]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount AccountFormatCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-427</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-427]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:PaymentNote)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:PaymentNote)">
          <xsl:attribute name="id">UBL-CR-428</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-428]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount PaymentNote</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-429</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-429]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount FinancialInstitutionBranch Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-430</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-430]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount FinancialInstitutionBranch FinancialInstitution Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address)">
          <xsl:attribute name="id">UBL-CR-431</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-431]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount FinancialInstitutionBranch FinancialInstitution Address</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address)">
          <xsl:attribute name="id">UBL-CR-432</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-432]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount FinancialInstitutionBranch Address</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country)">
          <xsl:attribute name="id">UBL-CR-433</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-433]-A UBL invoice should not include the PaymentMeans PayeeFinancialAccount Country</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CreditAccount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CreditAccount)">
          <xsl:attribute name="id">UBL-CR-434</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-434]-A UBL invoice should not include the PaymentMeans CreditAccount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:MandateTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:MandateTypeCode)">
          <xsl:attribute name="id">UBL-CR-435</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-435]-A UBL invoice should not include the PaymentMeans PaymentMandate MandateTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:MaximumPaymentInstructionsNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:MaximumPaymentInstructionsNumeric)">
          <xsl:attribute name="id">UBL-CR-436</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-436]-A UBL invoice should not include the PaymentMeans PaymentMandate MaximumPaymentInstructionsNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:MaximumPaidAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:MaximumPaidAmount)">
          <xsl:attribute name="id">UBL-CR-437</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-437]-A UBL invoice should not include the PaymentMeans PaymentMandate MaximumPaidAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:SignatureID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cbc:SignatureID)">
          <xsl:attribute name="id">UBL-CR-438</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-438]-A UBL invoice should not include the PaymentMeans PaymentMandate SignatureID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerParty)">
          <xsl:attribute name="id">UBL-CR-439</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-439]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-440</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-440]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:AliasName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:AliasName)">
          <xsl:attribute name="id">UBL-CR-441</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-441]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount AliasName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:AccountTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:AccountTypeCode)">
          <xsl:attribute name="id">UBL-CR-442</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-442]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount AccountTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:AccountFormatCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:AccountFormatCode)">
          <xsl:attribute name="id">UBL-CR-443</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-443]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount AccountFormatCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-444</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-444]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:PaymentNote)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cbc:PaymentNote)">
          <xsl:attribute name="id">UBL-CR-445</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-445]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount PaymentNote</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cac:FinancialInstitutionBranch)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cac:FinancialInstitutionBranch)">
          <xsl:attribute name="id">UBL-CR-446</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-446]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount FinancialInstitutionBranch</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cac:Country)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount/cac:Country)">
          <xsl:attribute name="id">UBL-CR-447</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-447]-A UBL invoice should not include the PaymentMeans PaymentMandate PayerFinancialAccount Country</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-448</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-448]-A UBL invoice should not include the PaymentMeans PaymentMandate ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PaymentReversalPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:PaymentReversalPeriod)">
          <xsl:attribute name="id">UBL-CR-449</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-449]-A UBL invoice should not include the PaymentMeans PaymentMandate PaymentReversalPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PaymentMandate/cac:Clause)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PaymentMandate/cac:Clause)">
          <xsl:attribute name="id">UBL-CR-450</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-450]-A UBL invoice should not include the PaymentMeans PaymentMandate Clause</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:TradeFinancing)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:TradeFinancing)">
          <xsl:attribute name="id">UBL-CR-451</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-451]-A UBL invoice should not include the PaymentMeans TradeFinancing</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-452</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-452]-A UBL invoice should not include the PaymentTerms ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PaymentMeansID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PaymentMeansID)">
          <xsl:attribute name="id">UBL-CR-453</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-453]-A UBL invoice should not include the PaymentTerms PaymentMeansID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID)">
          <xsl:attribute name="id">UBL-CR-454</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-454]-A UBL invoice should not include the PaymentTerms PrepaidPaymentReferenceID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:ReferenceEventCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:ReferenceEventCode)">
          <xsl:attribute name="id">UBL-CR-455</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-455]-A UBL invoice should not include the PaymentTerms ReferenceEventCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:SettlementDiscountPercent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:SettlementDiscountPercent)">
          <xsl:attribute name="id">UBL-CR-456</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-456]-A UBL invoice should not include the PaymentTerms SettlementDiscountPercent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PenaltySurchargePercent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PenaltySurchargePercent)">
          <xsl:attribute name="id">UBL-CR-457</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-457]-A UBL invoice should not include the PaymentTerms PenaltySurchargePercent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PaymentPercent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PaymentPercent)">
          <xsl:attribute name="id">UBL-CR-458</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-458]-A UBL invoice should not include the PaymentTerms PaymentPercent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:Amount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:Amount)">
          <xsl:attribute name="id">UBL-CR-459</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-459]-A UBL invoice should not include the PaymentTerms Amount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:SettlementDiscountAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:SettlementDiscountAmount)">
          <xsl:attribute name="id">UBL-CR-460</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-460]-A UBL invoice should not include the PaymentTerms SettlementDiscountAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PenaltyAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PenaltyAmount)">
          <xsl:attribute name="id">UBL-CR-461</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-461]-A UBL invoice should not include the PaymentTerms PenaltyAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PaymentTermsDetailsURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PaymentTermsDetailsURI)">
          <xsl:attribute name="id">UBL-CR-462</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-462]-A UBL invoice should not include the PaymentTerms PaymentTermsDetailsURI</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PaymentDueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PaymentDueDate)">
          <xsl:attribute name="id">UBL-CR-463</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-463]-A UBL invoice should not include the PaymentTerms PaymentDueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:InstallmentDueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:InstallmentDueDate)">
          <xsl:attribute name="id">UBL-CR-464</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-464]-A UBL invoice should not include the PaymentTerms InstallmentDueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:InvoicingPartyReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:InvoicingPartyReference)">
          <xsl:attribute name="id">UBL-CR-465</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-465]-A UBL invoice should not include the PaymentTerms InvoicingPartyReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cac:SettlementPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cac:SettlementPeriod)">
          <xsl:attribute name="id">UBL-CR-466</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-466]-A UBL invoice should not include the PaymentTerms SettlementPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cac:PenaltyPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cac:PenaltyPeriod)">
          <xsl:attribute name="id">UBL-CR-467</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-467]-A UBL invoice should not include the PaymentTerms PenaltyPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cac:ExchangeRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cac:ExchangeRate)">
          <xsl:attribute name="id">UBL-CR-468</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-468]-A UBL invoice should not include the PaymentTerms ExchangeRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-469</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-469]-A UBL invoice should not include the PaymentTerms ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PrepaidPayment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PrepaidPayment)">
          <xsl:attribute name="id">UBL-CR-470</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-470]-A UBL invoice should not include the PrepaidPayment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-471</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-471]-A UBL invoice should not include the AllowanceCharge ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:PrepaidIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:PrepaidIndicator)">
          <xsl:attribute name="id">UBL-CR-472</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-472]-A UBL invoice should not include the AllowanceCharge PrepaidIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:SequenceNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:SequenceNumeric)">
          <xsl:attribute name="id">UBL-CR-473</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-473]-A UBL invoice should not include the AllowanceCharge SequenceNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:AccountingCostCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:AccountingCostCode)">
          <xsl:attribute name="id">UBL-CR-474</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-474]-A UBL invoice should not include the AllowanceCharge AccountingCostCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:AccountingCost)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:AccountingCost)">
          <xsl:attribute name="id">UBL-CR-475</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-475]-A UBL invoice should not include the AllowanceCharge AccountingCost</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:PerUnitAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:PerUnitAmount)">
          <xsl:attribute name="id">UBL-CR-476</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-476]-A UBL invoice should not include the AllowanceCharge PerUnitAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-477</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-477]-A UBL invoice should not include the AllowanceCharge TaxCategory Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure)">
          <xsl:attribute name="id">UBL-CR-478</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-478]-A UBL invoice should not include the AllowanceCharge TaxCategory BaseUnitMeasure</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount)">
          <xsl:attribute name="id">UBL-CR-479</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-479]-A UBL invoice should not include the AllowanceCharge TaxCategory PerUnitAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode)">
          <xsl:attribute name="id">UBL-CR-480</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-480]-A UBL invoice should not include the AllowanceCharge TaxCategory TaxExemptionReasonCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason)">
          <xsl:attribute name="id">UBL-CR-481</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-481]-A UBL invoice should not include the AllowanceCharge TaxCategory TaxExemptionReason</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange)">
          <xsl:attribute name="id">UBL-CR-482</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-482]-A UBL invoice should not include the AllowanceCharge TaxCategory TierRange</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent)">
          <xsl:attribute name="id">UBL-CR-483</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-483]-A UBL invoice should not include the AllowanceCharge TaxCategory TierRatePercent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-484</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-484]-A UBL invoice should not include the AllowanceCharge TaxCategory TaxScheme Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)">
          <xsl:attribute name="id">UBL-CR-485</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-485]-A UBL invoice should not include the AllowanceCharge TaxCategory TaxScheme TaxTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-486</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-486]-A UBL invoice should not include the AllowanceCharge TaxCategory TaxScheme CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdiccionRegionAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdiccionRegionAddress)">
          <xsl:attribute name="id">UBL-CR-487</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-487]-A UBL invoice should not include the AllowanceCharge TaxCategory TaxScheme JurisdiccionRegionAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxTotal)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxTotal)">
          <xsl:attribute name="id">UBL-CR-488</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-488]-A UBL invoice should not include the AllowanceCharge TaxTotal</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:PaymentMeans)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:PaymentMeans)">
          <xsl:attribute name="id">UBL-CR-489</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-489]-A UBL invoice should not include the AllowanceCharge PaymentMeans</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxExchangeRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxExchangeRate)">
          <xsl:attribute name="id">UBL-CR-490</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-490]-A UBL invoice should not include the TaxExchangeRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PricingExchangeRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PricingExchangeRate)">
          <xsl:attribute name="id">UBL-CR-491</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-491]-A UBL invoice should not include the PricingExchangeRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentExchangeRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentExchangeRate)">
          <xsl:attribute name="id">UBL-CR-492</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-492]-A UBL invoice should not include the PaymentExchangeRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentAlternativeExchangeRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentAlternativeExchangeRate)">
          <xsl:attribute name="id">UBL-CR-493</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-493]-A UBL invoice should not include the PaymentAlternativeExchangeRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cbc:RoundingAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cbc:RoundingAmount)">
          <xsl:attribute name="id">UBL-CR-494</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-494]-A UBL invoice should not include the TaxTotal RoundingAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cbc:TaxEvidenceIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cbc:TaxEvidenceIndicator)">
          <xsl:attribute name="id">UBL-CR-495</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-495]-A UBL invoice should not include the TaxTotal TaxEvidenceIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cbc:TaxIncludedIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cbc:TaxIncludedIndicator)">
          <xsl:attribute name="id">UBL-CR-496</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-496]-A UBL invoice should not include the TaxTotal TaxIncludedIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric)">
          <xsl:attribute name="id">UBL-CR-497</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-497]-A UBL invoice should not include the TaxTotal TaxSubtotal CalulationSequenceNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount)">
          <xsl:attribute name="id">UBL-CR-498</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-498]-A UBL invoice should not include the TaxTotal TaxSubtotal TransactionCurrencyTaxAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent)">
          <xsl:attribute name="id">UBL-CR-499</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-499]-A UBL invoice should not include the TaxTotal TaxSubtotal Percent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure)">
          <xsl:attribute name="id">UBL-CR-500</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-500]-A UBL invoice should not include the TaxTotal TaxSubtotal BaseUnitMeasure</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount)">
          <xsl:attribute name="id">UBL-CR-501</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-501]-A UBL invoice should not include the TaxTotal TaxSubtotal PerUnitAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange)">
          <xsl:attribute name="id">UBL-CR-502</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-502]-A UBL invoice should not include the TaxTotal TaxSubtotal TierRange</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent)">
          <xsl:attribute name="id">UBL-CR-503</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-503]-A UBL invoice should not include the TaxTotal TaxSubtotal TierRatePercent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-504</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-504]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure)">
          <xsl:attribute name="id">UBL-CR-505</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-505]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory BaseUnitMeasure</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount)">
          <xsl:attribute name="id">UBL-CR-506</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-506]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory PerUnitAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange)">
          <xsl:attribute name="id">UBL-CR-507</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-507]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory TierRange</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent)">
          <xsl:attribute name="id">UBL-CR-508</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-508]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory TierRatePercent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-509</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-509]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory TaxScheme Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)">
          <xsl:attribute name="id">UBL-CR-510</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-510]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory TaxScheme TaxTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-511</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-511]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory TaxScheme CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdiccionRegionAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdiccionRegionAddress)">
          <xsl:attribute name="id">UBL-CR-512</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-512]-A UBL invoice should not include the TaxTotal TaxSubtotal TaxCategory TaxScheme JurisdiccionRegionAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:WithholdingTaxTotal)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:WithholdingTaxTotal)">
          <xsl:attribute name="id">UBL-CR-513</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-513]-A UBL invoice should not include the WithholdingTaxTotal</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:LegalMonetaryTotal/cbc:PayableAlternativeAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:LegalMonetaryTotal/cbc:PayableAlternativeAmount)">
          <xsl:attribute name="id">UBL-CR-514</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-514]-A UBL invoice should not include the LegalMonetaryTotal PayableAlternativeAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-515</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-515]-A UBL invoice should not include the InvoiceLine UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:TaxPointDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:TaxPointDate)">
          <xsl:attribute name="id">UBL-CR-516</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-516]-A UBL invoice should not include the InvoiceLine TaxPointDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:AccountingCostCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:AccountingCostCode)">
          <xsl:attribute name="id">UBL-CR-517</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-517]-A UBL invoice should not include the InvoiceLine AccountingCostCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:PaymentPurposeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:PaymentPurposeCode)">
          <xsl:attribute name="id">UBL-CR-518</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-518]-A UBL invoice should not include the InvoiceLine PaymentPurposeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:FreeOfChargeIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:FreeOfChargeIndicator)">
          <xsl:attribute name="id">UBL-CR-519</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-519]-A UBL invoice should not include the InvoiceLine FreeOfChargeIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:StartTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:StartTime)">
          <xsl:attribute name="id">UBL-CR-520</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-520]-A UBL invoice should not include the InvoiceLine InvoicePeriod StartTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:EndTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:EndTime)">
          <xsl:attribute name="id">UBL-CR-521</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-521]-A UBL invoice should not include the InvoiceLine InvoicePeriod EndTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:DurationMeasure)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:DurationMeasure)">
          <xsl:attribute name="id">UBL-CR-522</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-522]-A UBL invoice should not include the InvoiceLine InvoicePeriod DurationMeasure</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:DescriptionCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:DescriptionCode)">
          <xsl:attribute name="id">UBL-CR-523</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-523]-A UBL invoice should not include the InvoiceLine InvoicePeriod DescriptionCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:Description)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:InvoicePeriod/cbc:Description)">
          <xsl:attribute name="id">UBL-CR-524</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-524]-A UBL invoice should not include the InvoiceLine InvoicePeriod Description</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID)">
          <xsl:attribute name="id">UBL-CR-525</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-525]-A UBL invoice should not include the InvoiceLine OrderLineReference SalesOrderLineID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-526</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-526]-A UBL invoice should not include the InvoiceLine OrderLineReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode)">
          <xsl:attribute name="id">UBL-CR-527</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-527]-A UBL invoice should not include the InvoiceLine OrderLineReference LineStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:OrderReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:OrderReference)">
          <xsl:attribute name="id">UBL-CR-528</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-528]-A UBL invoice should not include the InvoiceLine OrderLineReference OrderReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DespatchLineReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DespatchLineReference)">
          <xsl:attribute name="id">UBL-CR-529</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-529]-A UBL invoice should not include the InvoiceLine DespatchLineReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:ReceiptLineReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:ReceiptLineReference)">
          <xsl:attribute name="id">UBL-CR-530</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-530]-A UBL invoice should not include the InvoiceLine ReceiptLineReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:BillingReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:BillingReference)">
          <xsl:attribute name="id">UBL-CR-531</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-531]-A UBL invoice should not include the InvoiceLine BillingReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:CopyIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:CopyIndicator)">
          <xsl:attribute name="id">UBL-CR-532</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-532]-A UBL invoice should not include the InvoiceLine DocumentReference CopyIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:UUID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:UUID)">
          <xsl:attribute name="id">UBL-CR-533</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-533]-A UBL invoice should not include the InvoiceLine DocumentReference UUID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:IssueDate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:IssueDate)">
          <xsl:attribute name="id">UBL-CR-534</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-534]-A UBL invoice should not include the InvoiceLine DocumentReference IssueDate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:IssueTime)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:IssueTime)">
          <xsl:attribute name="id">UBL-CR-535</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-535]-A UBL invoice should not include the InvoiceLine DocumentReference IssueTime</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:DocumentType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:DocumentType)">
          <xsl:attribute name="id">UBL-CR-537</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-537]-A UBL invoice should not include the InvoiceLine DocumentReference DocumentType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:Xpath)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:Xpath)">
          <xsl:attribute name="id">UBL-CR-538</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-538]-A UBL invoice should not include the InvoiceLine DocumentReference Xpath</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:LanguageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:LanguageID)">
          <xsl:attribute name="id">UBL-CR-539</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-539]-A UBL invoice should not include the InvoiceLine DocumentReference LanguageID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:LocaleCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:LocaleCode)">
          <xsl:attribute name="id">UBL-CR-540</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-540]-A UBL invoice should not include the InvoiceLine DocumentReference LocaleCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:VersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:VersionID)">
          <xsl:attribute name="id">UBL-CR-541</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-541]-A UBL invoice should not include the InvoiceLine DocumentReference VersionID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:DocumentStatusCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:DocumentStatusCode)">
          <xsl:attribute name="id">UBL-CR-542</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-542]-A UBL invoice should not include the InvoiceLine DocumentReference DocumentStatusCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cbc:DocumentDescription)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cbc:DocumentDescription)">
          <xsl:attribute name="id">UBL-CR-543</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-543]-A UBL invoice should not include the InvoiceLine DocumentReference DocumentDescription</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cac:Attachment)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cac:Attachment)">
          <xsl:attribute name="id">UBL-CR-544</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-544]-A UBL invoice should not include the InvoiceLine DocumentReference Attachment</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cac:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-545</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-545]-A UBL invoice should not include the InvoiceLine DocumentReference ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-546</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-546]-A UBL invoice should not include the InvoiceLine DocumentReference IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference/cac:ResultOfVerification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference/cac:ResultOfVerification)">
          <xsl:attribute name="id">UBL-CR-547</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-547]-A UBL invoice should not include the InvoiceLine DocumentReference ResultOfVerification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:PricingReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:PricingReference)">
          <xsl:attribute name="id">UBL-CR-548</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-548]-A UBL invoice should not include the InvoiceLine PricingReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OriginatorParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OriginatorParty)">
          <xsl:attribute name="id">UBL-CR-549</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-549]-A UBL invoice should not include the InvoiceLine OriginatorParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Delivery)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Delivery)">
          <xsl:attribute name="id">UBL-CR-550</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-550]-A UBL invoice should not include the InvoiceLine Delivery</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:PaymentTerms)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:PaymentTerms)">
          <xsl:attribute name="id">UBL-CR-551</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-551]-A UBL invoice should not include the InvoiceLine PaymentTerms</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-552</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-552]-A UBL invoice should not include the InvoiceLine AllowanceCharge ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator)">
          <xsl:attribute name="id">UBL-CR-553</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-553]-A UBL invoice should not include the InvoiceLine AllowanceCharge PrepaidIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric)">
          <xsl:attribute name="id">UBL-CR-554</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-554]-A UBL invoice should not include the InvoiceLine AllowanceCharge SequenceNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode)">
          <xsl:attribute name="id">UBL-CR-555</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-555]-A UBL invoice should not include the InvoiceLine AllowanceCharge AccountingCostCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost)">
          <xsl:attribute name="id">UBL-CR-556</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-556]-A UBL invoice should not include the InvoiceLine AllowanceCharge AccountingCost</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PerUnitAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PerUnitAmount)">
          <xsl:attribute name="id">UBL-CR-557</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-557]-A UBL invoice should not include the InvoiceLine AllowanceCharge PerUnitAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory)">
          <xsl:attribute name="id">UBL-CR-558</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-558]-A UBL invoice should not include the InvoiceLine AllowanceCharge TaxCategory</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal)">
          <xsl:attribute name="id">UBL-CR-559</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-559]-A UBL invoice should not include the InvoiceLine AllowanceCharge TaxTotal</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans)">
          <xsl:attribute name="id">UBL-CR-560</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-560]-A UBL invoice should not include the InvoiceLine AllowanceCharge PaymentMeans</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:TaxTotal)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:TaxTotal)">
          <xsl:attribute name="id">UBL-CR-561</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-561]-A UBL invoice should not include the InvoiceLine TaxTotal</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:WithholdingTaxTotal)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:WithholdingTaxTotal)">
          <xsl:attribute name="id">UBL-CR-562</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-562]-A UBL invoice should not include the InvoiceLine WithholdingTaxTotal</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:PackQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:PackQuantity)">
          <xsl:attribute name="id">UBL-CR-563</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-563]-A UBL invoice should not include the InvoiceLine Item PackQuantity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric)">
          <xsl:attribute name="id">UBL-CR-564</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-564]-A UBL invoice should not include the InvoiceLine Item PackSizeNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator)">
          <xsl:attribute name="id">UBL-CR-565</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-565]-A UBL invoice should not include the InvoiceLine Item CatalogueIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator)">
          <xsl:attribute name="id">UBL-CR-566</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-566]-A UBL invoice should not include the InvoiceLine Item HazardousRiskIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation)">
          <xsl:attribute name="id">UBL-CR-567</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-567]-A UBL invoice should not include the InvoiceLine Item AdditionalInformation</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:Keyword)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:Keyword)">
          <xsl:attribute name="id">UBL-CR-568</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-568]-A UBL invoice should not include the InvoiceLine Item Keyword</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:BrandName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:BrandName)">
          <xsl:attribute name="id">UBL-CR-569</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-569]-A UBL invoice should not include the InvoiceLine Item BrandName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:ModelName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:ModelName)">
          <xsl:attribute name="id">UBL-CR-570</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-570]-A UBL invoice should not include the InvoiceLine Item ModelName</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cbc:ExtendedID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cbc:ExtendedID)">
          <xsl:attribute name="id">UBL-CR-571</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-571]-A UBL invoice should not include the InvoiceLine Item BuyersItemIdentification ExtendedID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cbc:BareCodeSymbologyID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cbc:BareCodeSymbologyID)">
          <xsl:attribute name="id">UBL-CR-572</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-572]-A UBL invoice should not include the InvoiceLine Item BuyersItemIdentification BareCodeSymbologyID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cac:PhysicalAttribute)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cac:PhysicalAttribute)">
          <xsl:attribute name="id">UBL-CR-573</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-573]-A UBL invoice should not include the InvoiceLine Item BuyersItemIdentification PhysicalAttribute</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cac:MeasurementDimension)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cac:MeasurementDimension)">
          <xsl:attribute name="id">UBL-CR-574</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-574]-A UBL invoice should not include the InvoiceLine Item BuyersItemIdentification MeasurementDimension</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-575</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-575]-A UBL invoice should not include the InvoiceLine Item BuyersItemIdentification IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)">
          <xsl:attribute name="id">UBL-CR-576</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-576]-A UBL invoice should not include the InvoiceLine Item SellersItemIdentification ExtendedID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:BareCodeSymbologyID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:BareCodeSymbologyID)">
          <xsl:attribute name="id">UBL-CR-577</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-577]-A UBL invoice should not include the InvoiceLine Item SellersItemIdentification BareCodeSymbologyID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cac:PhysicalAttribute)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cac:PhysicalAttribute)">
          <xsl:attribute name="id">UBL-CR-578</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-578]-A UBL invoice should not include the InvoiceLine Item SellersItemIdentification PhysicalAttribute</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cac:MeasurementDimension)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cac:MeasurementDimension)">
          <xsl:attribute name="id">UBL-CR-579</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-579]-A UBL invoice should not include the InvoiceLine Item SellersItemIdentification MeasurementDimension</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-580</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-580]-A UBL invoice should not include the InvoiceLine Item SellersItemIdentification IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification)">
          <xsl:attribute name="id">UBL-CR-581</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-581]-A UBL invoice should not include the InvoiceLine Item ManufacturersItemIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)">
          <xsl:attribute name="id">UBL-CR-582</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-582]-A UBL invoice should not include the InvoiceLine Item StandardItemIdentification ExtendedID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:BareCodeSymbologyID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:BareCodeSymbologyID)">
          <xsl:attribute name="id">UBL-CR-583</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-583]-A UBL invoice should not include the InvoiceLine Item StandardItemIdentification BareCodeSymbologyID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cac:PhysicalAttribute)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cac:PhysicalAttribute)">
          <xsl:attribute name="id">UBL-CR-584</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-584]-A UBL invoice should not include the InvoiceLine Item StandardItemIdentification PhysicalAttribute</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cac:MeasurementDimension)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cac:MeasurementDimension)">
          <xsl:attribute name="id">UBL-CR-585</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-585]-A UBL invoice should not include the InvoiceLine Item StandardItemIdentification MeasurementDimension</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cac:IssuerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cac:IssuerParty)">
          <xsl:attribute name="id">UBL-CR-586</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-586]-A UBL invoice should not include the InvoiceLine Item StandardItemIdentification IssuerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification)">
          <xsl:attribute name="id">UBL-CR-587</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-587]-A UBL invoice should not include the InvoiceLine Item CatalogueItemIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification)">
          <xsl:attribute name="id">UBL-CR-588</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-588]-A UBL invoice should not include the InvoiceLine Item AdditionalItemIdentification</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference)">
          <xsl:attribute name="id">UBL-CR-589</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-589]-A UBL invoice should not include the InvoiceLine Item CatalogueDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference)">
          <xsl:attribute name="id">UBL-CR-590</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-590]-A UBL invoice should not include the InvoiceLine Item ItemSpecificationDocumentReference</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:OriginCountry/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:OriginCountry/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-591</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-591]-A UBL invoice should not include the InvoiceLine Item OriginCountry Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCode)">
          <xsl:attribute name="id">UBL-CR-592</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-592]-A UBL invoice should not include the InvoiceLine Item CommodityClassification NatureCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode)">
          <xsl:attribute name="id">UBL-CR-593</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-593]-A UBL invoice should not include the InvoiceLine Item CommodityClassification CargoTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode)">
          <xsl:attribute name="id">UBL-CR-594</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-594]-A UBL invoice should not include the InvoiceLine Item CommodityClassification CommodityCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TransactionConditions)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TransactionConditions)">
          <xsl:attribute name="id">UBL-CR-595</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-595]-A UBL invoice should not include the InvoiceLine Item TransactionConditions</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:HazardousItem)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:HazardousItem)">
          <xsl:attribute name="id">UBL-CR-596</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-596]-A UBL invoice should not include the InvoiceLine Item HazardousItem</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-597</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-597]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:BaseUnitMeasure)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:BaseUnitMeasure)">
          <xsl:attribute name="id">UBL-CR-598</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-598]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory BaseUnitMeasure</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:PerUnitAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:PerUnitAmount)">
          <xsl:attribute name="id">UBL-CR-599</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-599]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory PerUnitAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TaxExemptionReasonCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TaxExemptionReasonCode)">
          <xsl:attribute name="id">UBL-CR-600</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-600]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TaxExemptionReasonCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TaxExemptionReason)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TaxExemptionReason)">
          <xsl:attribute name="id">UBL-CR-601</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-601]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TaxExemptionReason</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TierRange)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TierRange)">
          <xsl:attribute name="id">UBL-CR-602</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-602]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TierRange</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TierRatePercent)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:TierRatePercent)">
          <xsl:attribute name="id">UBL-CR-603</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-603]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TierRatePercent</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:Name)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:Name)">
          <xsl:attribute name="id">UBL-CR-604</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-604]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TaxScheme Name</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:TaxTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:TaxTypeCode)">
          <xsl:attribute name="id">UBL-CR-605</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-605]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TaxScheme TaxTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:CurrencyCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:CurrencyCode)">
          <xsl:attribute name="id">UBL-CR-606</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-606]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TaxScheme CurrencyCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cac:JurisdiccionRegionAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cac:JurisdiccionRegionAddress)">
          <xsl:attribute name="id">UBL-CR-607</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-607]-A UBL invoice should not include the InvoiceLine Item ClassifiedTaxCategory TaxScheme JurisdiccionRegionAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-608</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-608]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:NameCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:NameCode)">
          <xsl:attribute name="id">UBL-CR-609</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-609]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty NameCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:TestMethod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:TestMethod)">
          <xsl:attribute name="id">UBL-CR-610</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-610]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty TestMethod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ValueQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ValueQuantity)">
          <xsl:attribute name="id">UBL-CR-611</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-611]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty ValueQuantity</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ValueQualifier)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ValueQualifier)">
          <xsl:attribute name="id">UBL-CR-612</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-612]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty ValueQualifier</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ImportanceCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ImportanceCode)">
          <xsl:attribute name="id">UBL-CR-613</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-613]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty ImportanceCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ListValue)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cbc:ListValue)">
          <xsl:attribute name="id">UBL-CR-614</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-614]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty ListValue</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:UsabilityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:UsabilityPeriod)">
          <xsl:attribute name="id">UBL-CR-615</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-615]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty UsabilityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:ItemPropertyGroup)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:ItemPropertyGroup)">
          <xsl:attribute name="id">UBL-CR-616</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-616]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty ItemPropertyGroup</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:RangeDimension)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:RangeDimension)">
          <xsl:attribute name="id">UBL-CR-617</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-617]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty RangeDimension</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:ItemPropertyRange)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty/cac:ItemPropertyRange)">
          <xsl:attribute name="id">UBL-CR-618</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-618]-A UBL invoice should not include the InvoiceLine Item AdditionalItemProperty ItemPropertyRange</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty)">
          <xsl:attribute name="id">UBL-CR-619</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-619]-A UBL invoice should not include the InvoiceLine Item ManufacturerParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty)">
          <xsl:attribute name="id">UBL-CR-620</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-620]-A UBL invoice should not include the InvoiceLine Item InformationContentProviderParty</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:OriginAddress)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:OriginAddress)">
          <xsl:attribute name="id">UBL-CR-621</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-621]-A UBL invoice should not include the InvoiceLine Item OriginAddress</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ItemInstance)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ItemInstance)">
          <xsl:attribute name="id">UBL-CR-622</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-622]-A UBL invoice should not include the InvoiceLine Item ItemInstance</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Certificate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Certificate)">
          <xsl:attribute name="id">UBL-CR-623</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-623]-A UBL invoice should not include the InvoiceLine Item Certificate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Dimension)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Dimension)">
          <xsl:attribute name="id">UBL-CR-624</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-624]-A UBL invoice should not include the InvoiceLine Item Dimension</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceChangeReason)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceChangeReason)">
          <xsl:attribute name="id">UBL-CR-625</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-625]-A UBL invoice should not include the InvoiceLine Item Price PriceChangeReason</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceTypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceTypeCode)">
          <xsl:attribute name="id">UBL-CR-626</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-626]-A UBL invoice should not include the InvoiceLine Item Price PriceTypeCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceType)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceType)">
          <xsl:attribute name="id">UBL-CR-627</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-627]-A UBL invoice should not include the InvoiceLine Item Price PriceType</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:OrderableUnitFactorRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:OrderableUnitFactorRate)">
          <xsl:attribute name="id">UBL-CR-628</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-628]-A UBL invoice should not include the InvoiceLine Item Price OrderableUnitFactorRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:ValidityPeriod)">
          <xsl:attribute name="id">UBL-CR-629</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-629]-A UBL invoice should not include the InvoiceLine Item Price ValidityPeriod</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceList)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:PriceList)">
          <xsl:attribute name="id">UBL-CR-630</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-630]-A UBL invoice should not include the InvoiceLine Item Price PriceList</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:OrderableUnitFactorRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cbc:OrderableUnitFactorRate)">
          <xsl:attribute name="id">UBL-CR-631</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-631]-A UBL invoice should not include the InvoiceLine Item Price OrderableUnitFactorRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:ID)">
          <xsl:attribute name="id">UBL-CR-632</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-632]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge ID</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)">
          <xsl:attribute name="id">UBL-CR-633</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-633]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge AllowanceChargeReasonCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReason)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReason)">
          <xsl:attribute name="id">UBL-CR-634</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-634]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge AllowanceChargeReason</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:MultiplierFactorNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:MultiplierFactorNumeric)">
          <xsl:attribute name="id">UBL-CR-635</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-635]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge MultiplierFactorNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator)">
          <xsl:attribute name="id">UBL-CR-636</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-636]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge PrepaidIndicator</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric)">
          <xsl:attribute name="id">UBL-CR-637</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-637]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge SequenceNumeric</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode)">
          <xsl:attribute name="id">UBL-CR-638</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-638]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge AccountingCostCode</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AccountingCost)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:AccountingCost)">
          <xsl:attribute name="id">UBL-CR-639</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-639]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge AccountingCost</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:PerUnitAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cbc:PerUnitAmount)">
          <xsl:attribute name="id">UBL-CR-640</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-640]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge PerUnitAmount</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cac:TaxCategory)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cac:TaxCategory)">
          <xsl:attribute name="id">UBL-CR-641</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-641]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge TaxCategory</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cac:TaxTotal)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cac:TaxTotal)">
          <xsl:attribute name="id">UBL-CR-642</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-642]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge TaxTotal</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cac:PaymentMeans)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:AllowanceCharge/cac:PaymentMeans)">
          <xsl:attribute name="id">UBL-CR-643</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-643]-A UBL invoice should not include the InvoiceLine Item Price AllowanceCharge PaymentMeans</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:PricingExchangeRate)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:Price/cac:PricingExchangeRate)">
          <xsl:attribute name="id">UBL-CR-644</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-644]-A UBL invoice should not include the InvoiceLine Item Price PricingExchangeRate</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DeliveryTerms)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DeliveryTerms)">
          <xsl:attribute name="id">UBL-CR-645</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-645]-A UBL invoice should not include the InvoiceLine DeliveryTerms</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:SubInvoiceLine)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:SubInvoiceLine)">
          <xsl:attribute name="id">UBL-CR-646</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-646]-A UBL invoice should not include the InvoiceLine SubInvoiceLine</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:ItemPriceExtension)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:ItemPriceExtension)">
          <xsl:attribute name="id">UBL-CR-647</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-CR-647]-A UBL invoice should not include the InvoiceLine ItemPriceExtension</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@schemeName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@schemeName)">
          <xsl:attribute name="id">UBL-DT-08</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-08]-Scheme name attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@schemeAgencyName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@schemeAgencyName)">
          <xsl:attribute name="id">UBL-DT-09</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-09]-Scheme agency name attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@schemeDataURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@schemeDataURI)">
          <xsl:attribute name="id">UBL-DT-10</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-10]-Scheme data uri attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@schemeURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@schemeURI)">
          <xsl:attribute name="id">UBL-DT-11</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-11]-Scheme uri attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@format)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@format)">
          <xsl:attribute name="id">UBL-DT-12</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-12]-Format attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@unitCodeListIdentifier)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@unitCodeListIdentifier)">
          <xsl:attribute name="id">UBL-DT-13</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-13]-Unit code list identifier attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@unitCodeListAgencyIdentifier)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@unitCodeListAgencyIdentifier)">
          <xsl:attribute name="id">UBL-DT-14</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-14]-Unit code list agency identifier attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@unitCodeListAgencyName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@unitCodeListAgencyName)">
          <xsl:attribute name="id">UBL-DT-15</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-15]-Unit code list agency name attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@listAgencyName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@listAgencyName)">
          <xsl:attribute name="id">UBL-DT-16</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-16]-List agency name attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@listName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@listName)">
          <xsl:attribute name="id">UBL-DT-17</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-17]-List name attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(//@name) - count(//cbc:PaymentMeansCode/@name) &lt;= 0" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(//@name) - count(//cbc:PaymentMeansCode/@name) &lt;= 0">
          <xsl:attribute name="id">UBL-DT-18</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-18]-Name attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@languageID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@languageID)">
          <xsl:attribute name="id">UBL-DT-19</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-19]-Language identifier attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@listURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@listURI)">
          <xsl:attribute name="id">UBL-DT-20</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-20]-List uri attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@listSchemeURI)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@listSchemeURI)">
          <xsl:attribute name="id">UBL-DT-21</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-21]-List scheme uri attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@languageLocaleID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@languageLocaleID)">
          <xsl:attribute name="id">UBL-DT-22</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-22]-Language local identifier attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@uri)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@uri)">
          <xsl:attribute name="id">UBL-DT-23</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-23]-Uri attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@currencyCodeListVersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@currencyCodeListVersionID)">
          <xsl:attribute name="id">UBL-DT-24</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-24]-Currency code list version id should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@characterSetCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@characterSetCode)">
          <xsl:attribute name="id">UBL-DT-25</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-25]-CharacterSetCode attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(//@encodingCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(//@encodingCode)">
          <xsl:attribute name="id">UBL-DT-26</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-DT-26]-EncodingCode attribute should not be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:ContractDocumentReference/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:ContractDocumentReference/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-01</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-01]-Contract identifier shall occur maximum once.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:ReceiptDocumentReference/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:ReceiptDocumentReference/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-02</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-02]-Receive advice identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:DespatchDocumentReference/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:DespatchDocumentReference/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-03</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-03]-Despatch advice identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AdditionalDocumentReference[cbc:DocumentTypeCode='130']/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AdditionalDocumentReference[cbc:DocumentTypeCode='130']/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-04</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-04]-Invoice object identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:PaymentTerms/cbc:Note) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:PaymentTerms/cbc:Note) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-05</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-05]-Payment terms shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:InvoiceDocumentReference) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:InvoiceDocumentReference) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-06</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-06]-Preceding invoice reference shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:InvoicePeriod) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:InvoicePeriod) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-08</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-08]-Invoice period shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-09</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-09]-Seller name shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyName/cbc:Name) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyName/cbc:Name) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-10</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-10]-Seller trader name shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-11</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-11]-Seller legal registration identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-12</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-12]-Seller VAT identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID!='VAT']/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID!='VAT']/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-13</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-13]-Seller tax registration shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalForm) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalForm) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-14</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-14]-Seller additional legal information shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-15</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-15]-Buyer name shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-16</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-16]-Buyer identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-17</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-17]-Buyer legal registration identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-18</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-18]-Buyer VAT identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:Delivery) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:Delivery) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-24</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-24]-Deliver to information shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:PayeeParty/cac:PartyIdentification/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:PayeeParty/cac:PartyIdentification/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-29</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-29]-Bank creditor reference shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:ProjectReference/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:ProjectReference/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-39</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-39]-Project reference shall occur maximum once.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyName/cbc:Name) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:AccountingCustomerParty/cac:Party/cac:PartyName/cbc:Name) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-40</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-40]-Buyer trade name shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine" mode="M7" priority="1005">
    <svrl:fired-rule context="cac:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cbc:Note) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cbc:Note) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-34</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-34]-Invoice line note shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:OrderLineReference/cbc:LineID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:OrderLineReference/cbc:LineID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-35</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-35]-Referenced purchase order line identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:InvoicePeriod) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:InvoicePeriod) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-36</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-36]-Invoice line period shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:Price/cac:AllowanceCharge/cbc:Amount) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:Price/cac:AllowanceCharge/cbc:Amount) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-37</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-37]-Item price discount shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:Item/cac:ClassifiedTaxCategory/cbc:TaxExemptionReason) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:Item/cac:ClassifiedTaxCategory/cbc:TaxExemptionReason) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-38</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-38]-Invoiced item VAT exemption reason text shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PayeeParty" mode="M7" priority="1004">
    <svrl:fired-rule context="cac:PayeeParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:PartyName/cbc:Name) &lt;= 1) and ((cac:PartyName/cbc:Name) != (../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:PartyName/cbc:Name) &lt;= 1) and ((cac:PartyName/cbc:Name) != (../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))">
          <xsl:attribute name="id">UBL-SR-19</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-19]-Payee name shall occur maximum once, if the Payee is different from the Seller</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:PartyIdentification/cbc:ID) &lt;= 1) and ((cac:PartyName/cbc:Name) != (../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:PartyIdentification/cbc:ID) &lt;= 1) and ((cac:PartyName/cbc:Name) != (../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))">
          <xsl:attribute name="id">UBL-SR-20</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-20]-Payee identifier shall occur maximum once, if the Payee is different from the Seller</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:PartyLegalEntity/cbc:CompanyID) &lt;= 1) and ((cac:PartyName/cbc:Name) != (../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:PartyLegalEntity/cbc:CompanyID) &lt;= 1) and ((cac:PartyName/cbc:Name) != (../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))">
          <xsl:attribute name="id">UBL-SR-21</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-21]-Payee legal registration identifier shall occur maximum once, if the Payee is different from the Seller</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans" mode="M7" priority="1003">
    <svrl:fired-rule context="cac:PaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cbc:PaymentID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cbc:PaymentID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-26</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-26]-Payment reference shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cbc:InstructionNote) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cbc:InstructionNote) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-27</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-27]-Payment means text shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:PaymentMandate/cbc:ID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:PaymentMandate/cbc:ID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-28</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-28]-Mandate reference identifier shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:BillingReference" mode="M7" priority="1002">
    <svrl:fired-rule context="cac:BillingReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cac:InvoiceDocumentReference/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cac:InvoiceDocumentReference/cbc:ID)">
          <xsl:attribute name="id">UBL-SR-07</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-07]-If there is a preceding invoice reference, the preceding invoice number shall be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxRepresentativeParty" mode="M7" priority="1001">
    <svrl:fired-rule context="cac:TaxRepresentativeParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:Party/cac:PartyName/cbc:Name) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:Party/cac:PartyName/cbc:Name) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-22</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-22]-Seller tax representative name shall occur maximum once, if the Seller has a tax representative</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:Party/cac:PartyTaxScheme/cbc:CompanyID) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:Party/cac:PartyTaxScheme/cbc:CompanyID) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-23</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-23]-Seller tax representative VAT identifier shall occur maximum once, if the Seller has a tax representative</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxSubtotal" mode="M7" priority="1000">
    <svrl:fired-rule context="cac:TaxSubtotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(count(cac:TaxCategory/cbc:TaxExemptionReason) &lt;= 1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(count(cac:TaxCategory/cbc:TaxExemptionReason) &lt;= 1)">
          <xsl:attribute name="id">UBL-SR-32</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[UBL-SR-32]-VAT exemption reason text shall occur maximum once</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>
  <xsl:template match="text()" mode="M7" priority="-1" />
  <xsl:template match="@*|node()" mode="M7" priority="-2">
    <xsl:apply-templates mode="M7" select="*" />
  </xsl:template>
</xsl:stylesheet>
