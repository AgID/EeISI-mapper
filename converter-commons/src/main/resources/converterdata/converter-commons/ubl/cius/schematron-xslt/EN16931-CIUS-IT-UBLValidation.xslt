<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:cn="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
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
    <svrl:schematron-output schemaVersion="" title="Italian Rules for EN16931 model in UBL Syntax">
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
        <xsl:attribute name="id">CIUS-IT-FATAL</xsl:attribute>
        <xsl:attribute name="name">CIUS-IT-FATAL</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M9" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>Italian Rules for EN16931 model in UBL Syntax</svrl:text>
  <xsl:param name="supplierCountry" select="if (/*/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) else 'XX'" />
  <xsl:param name="customerCountry" select="if (/*/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) else 'XX'" />
  <xsl:param name="deliveryCountry" select="if (/*/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode)) else 'XX'" />

<!--PATTERN CIUS-IT-FATAL-->


	<!--RULE -->
<xsl:template match="/ubl:Invoice | /cn:CreditNote" mode="M9" priority="1040">
    <svrl:fired-rule context="/ubl:Invoice | /cn:CreditNote" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(cbc:ID)) &lt;= 20 and matches(normalize-space(cbc:ID),'(^\p{IsBasicLatin}*[0-9]+\p{IsBasicLatin}*$)')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(cbc:ID)) &lt;= 20 and matches(normalize-space(cbc:ID),'(^\p{IsBasicLatin}*[0-9]+\p{IsBasicLatin}*$)')">
          <xsl:attribute name="id">BR-IT-010</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-010] BT-1 (Invoice number) - BT maximum length shall be 20 chars with at least a digit.
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:AccountingCost),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:AccountingCost),'^\p{IsBasicLatin}{0,20}$')">
          <xsl:attribute name="id">BR-IT-080</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-080] BT-19 (Buyer accounting reference) - BT maximum length shall be 20 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party" mode="M9" priority="1039">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or cac:PartyTaxScheme/cbc:CompanyID or cac:PartyIdentification/cbc:ID[starts-with(normalize-space(.),'IT:CF:')]" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or cac:PartyTaxScheme/cbc:CompanyID or cac:PartyIdentification/cbc:ID[starts-with(normalize-space(.),'IT:CF:')]">
          <xsl:attribute name="id">BR-IT-160</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BR-IT-160-2] BT-48 BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) - If BT-55 = "IT", then BT-48 or BT-46 shall be indicated.       </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or (exists(cbc:EndpointID) and         (cbc:EndpointID[normalize-space(@schemeID) = 'IT:CODDEST']         or cbc:EndpointID[normalize-space(@schemeID) = 'IT:PEC']         or cbc:EndpointID[normalize-space(@schemeID) = '9921'] ))" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or (exists(cbc:EndpointID) and (cbc:EndpointID[normalize-space(@schemeID) = 'IT:CODDEST'] or cbc:EndpointID[normalize-space(@schemeID) = 'IT:PEC'] or cbc:EndpointID[normalize-space(@schemeID) = '9921'] ))">
          <xsl:attribute name="id">BR-IT-190</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-190] BT-49 BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario. BT-49-1=IT:PEC or IT:IPA (9921) or IT:CODDEST
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or  not(cbc:EndpointID[normalize-space(@schemeID) = 'IT:PEC'])         or ( (string-length(normalize-space(cbc:EndpointID)) >= 7 and string-length(normalize-space(cbc:EndpointID)) &lt;= 256) and matches(normalize-space(cbc:EndpointID),'^.+@.+[.]+.+$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(cbc:EndpointID[normalize-space(@schemeID) = 'IT:PEC']) or ( (string-length(normalize-space(cbc:EndpointID)) >= 7 and string-length(normalize-space(cbc:EndpointID)) &lt;= 256) and matches(normalize-space(cbc:EndpointID),'^.+@.+[.]+.+$') )">
          <xsl:attribute name="id">BR-IT-200-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-200-1] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) -If BT-49-1=IT:PEC schema then BT-49 shall be a PEC (email) address and  length shall be between 7 and 256 character
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or  not(cbc:EndpointID[normalize-space(@schemeID) = '9921'])         or ( matches(normalize-space(cbc:EndpointID),'^[A-Z0-9]{6}$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(cbc:EndpointID[normalize-space(@schemeID) = '9921']) or ( matches(normalize-space(cbc:EndpointID),'^[A-Z0-9]{6}$') )">
          <xsl:attribute name="id">BR-IT-200-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-200-2] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) =IT:IPA schema (9921) then BT-49 shall be a IPA code and maximum length shall be 6 chars
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or  not(cbc:EndpointID[normalize-space(@schemeID) = 'IT:CODDEST'])         or ( matches(normalize-space(cbc:EndpointID),'^[A-Z0-9]{7}$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(cbc:EndpointID[normalize-space(@schemeID) = 'IT:CODDEST']) or ( matches(normalize-space(cbc:EndpointID),'^[A-Z0-9]{7}$') )">
          <xsl:attribute name="id">BR-IT-200-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-200-3] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier)=IT:CODDEST schema then BT-49 and maximum length shall be 7 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification" mode="M9" priority="1038">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry != 'IT' or not(cbc:ID[starts-with(normalize-space(.), 'IT:CF')]) or matches( normalize-space(cbc:ID[starts-with(normalize-space(.), 'IT:CF')]), '(^IT:CF:[A-Z0-9]{11,16}$)')" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry != 'IT' or not(cbc:ID[starts-with(normalize-space(.), 'IT:CF')]) or matches( normalize-space(cbc:ID[starts-with(normalize-space(.), 'IT:CF')]), '(^IT:CF:[A-Z0-9]{11,16}$)')">
          <xsl:attribute name="id">BR-IT-160-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-160] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght shall be 22 starting with "IT:CF: ".
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity[starts-with(cbc:CompanyID,'IT:EORI:')]" mode="M9" priority="1037">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity[starts-with(cbc:CompanyID,'IT:EORI:')]" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($customerCountry!='IT') or (string-length(normalize-space(cbc:CompanyID)) >= 21 and string-length(normalize-space(cbc:CompanyID)) &lt;=25)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($customerCountry!='IT') or (string-length(normalize-space(cbc:CompanyID)) >= 21 and string-length(normalize-space(cbc:CompanyID)) &lt;=25)">
          <xsl:attribute name="id">BR-IT-170</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-170] BT-47 Buyer legal registration identifier. If BT-55 = "IT", if BT-47 starts with "IT:EORI:" then BT-47 minimum lenght shall be 21 and maximum lenght shall be 25
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme" mode="M9" priority="1036">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(cbc:CompanyID)) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(cbc:CompanyID)) &lt;= 30">
          <xsl:attribute name="id">BR-IT-180</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-180] BT-48 (Buyer VAT identifier) - BT maximum length shall be 30 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress[cac:Country/cbc:IdentificationCode='IT']" mode="M9" priority="1035">
    <svrl:fired-rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress[cac:Country/cbc:IdentificationCode='IT']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="cbc:StreetName" />
      <xsl:otherwise>
        <svrl:failed-assert test="cbc:StreetName">
          <xsl:attribute name="id">BR-IT-210-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-210-1] BT-50 (Buyer address line 1) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="cbc:CityName" />
      <xsl:otherwise>
        <svrl:failed-assert test="cbc:CityName">
          <xsl:attribute name="id">BR-IT-210-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-210-2] BT-52 (Buyer city) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')">
          <xsl:attribute name="id">BR-IT-210-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-210-3] BT-53 (Buyer post code) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')">
          <xsl:attribute name="id">BR-IT-220</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-220] BT-54 (Buyer country subdivision) - BT maximum length shall be 2 chars and shall be coded according to Italian province list else save in attachment. .
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity" mode="M9" priority="1034">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="every $CompanyID in (cbc:CompanyID[starts-with(normalize-space(.), 'IT:REA:')])         satisfies ($supplierCountry != 'IT' or matches(normalize-space($CompanyID), '(^IT:REA:[A-Z]{2}:[\p{IsBasicLatin}]{1,20}$)'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $CompanyID in (cbc:CompanyID[starts-with(normalize-space(.), 'IT:REA:')]) satisfies ($supplierCountry != 'IT' or matches(normalize-space($CompanyID), '(^IT:REA:[A-Z]{2}:[\p{IsBasicLatin}]{1,20}$)'))">
          <xsl:attribute name="id">BR-IT-110</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-110] BT-30 (Seller legal registration identifier) - BT-30 minimum lenght 10 and maximum lenght shall be 30 starting with "IT:REA:" and shall be represented as "IT:REA:Ufficio:NumeroREA".
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification" mode="M9" priority="1033">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="every $cbcID in (cbc:ID[starts-with(normalize-space(.), 'IT:EORI:')])         satisfies (($supplierCountry != 'IT') or ((string-length(normalize-space($cbcID)) >= 21) and (string-length(normalize-space($cbcID)) &lt;= 25)))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $cbcID in (cbc:ID[starts-with(normalize-space(.), 'IT:EORI:')]) satisfies (($supplierCountry != 'IT') or ((string-length(normalize-space($cbcID)) >= 21) and (string-length(normalize-space($cbcID)) &lt;= 25)))">
          <xsl:attribute name="id">BR-IT-100-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-100-1] BT-29 (Seller identifier) - BT-29 minimum lenght 21 and maximum lenght shall be 25 starting with "IT:EORI ".
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="every $SubID in cbc:ID[starts-with(normalize-space(.), 'IT:ALBO:')]         satisfies (($supplierCountry != 'IT') or (matches(normalize-space($SubID), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $SubID in cbc:ID[starts-with(normalize-space(.), 'IT:ALBO:')] satisfies (($supplierCountry != 'IT') or (matches(normalize-space($SubID), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))">
          <xsl:attribute name="id">BR-IT-100-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-100-2] BT-29 (Seller identifier) - BT-29 starting with "IT:ALBO has the format IT:ALBO:AlboProfessionale(1-60chars):NumeroIscrizioneAlbo(1-60chars) - (:) colon is permitted only as separator".
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']" mode="M9" priority="1032">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$supplierCountry != 'IT' or matches(normalize-space(.[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']/cbc:CompanyID), '^[A-Z0-9]{11,16}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="$supplierCountry != 'IT' or matches(normalize-space(.[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']/cbc:CompanyID), '^[A-Z0-9]{11,16}$')">
          <xsl:attribute name="id">BR-IT-130</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-130] BT-32 (Seller tax registration identifier) - then BT-32 minimum lenght shall be 11 and maximum lenght shall be 16.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) = 'VAT']" mode="M9" priority="1031">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) = 'VAT']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$supplierCountry!='IT' or string-length(normalize-space(cbc:CompanyID)) &lt;= 30 " />
      <xsl:otherwise>
        <svrl:failed-assert test="$supplierCountry!='IT' or string-length(normalize-space(cbc:CompanyID)) &lt;= 30">
          <xsl:attribute name="id">BR-IT-120</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-120] BT-31 (Seller VAT identifier) - BT maximum length shall be 30 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress[normalize-space(cac:Country/cbc:IdentificationCode)='IT']" mode="M9" priority="1030">
    <svrl:fired-rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress[normalize-space(cac:Country/cbc:IdentificationCode)='IT']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($supplierCountry = 'IT') or cbc:StreetName" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or cbc:StreetName">
          <xsl:attribute name="id">BR-IT-140-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-140-1] BT-35 (Seller address line 1) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($supplierCountry = 'IT') or cbc:CityName" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or cbc:CityName">
          <xsl:attribute name="id">BR-IT-140</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-140-2] BT-37 (Seller city) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($supplierCountry = 'IT') or matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')">
          <xsl:attribute name="id">BR-IT-140-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-140-3] BT-38 (Seller post code) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($supplierCountry = 'IT') or not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')">
          <xsl:attribute name="id">BR-IT-150</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-150] BT-39 (Seller country subdivision) - BT maximum length shall be 2 chars and shall be coded according to Italian province list else save in attachment.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:AdditionalDocumentReference[(normalize-space(cbc:DocumentTypeCode)!='130' and normalize-space(cbc:DocumentTypeCode)!='50') or not(exists(cbc:DocumentTypeCode))]" mode="M9" priority="1029">
    <svrl:fired-rule context="cac:AdditionalDocumentReference[(normalize-space(cbc:DocumentTypeCode)!='130' and normalize-space(cbc:DocumentTypeCode)!='50') or not(exists(cbc:DocumentTypeCode))]" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="cac:Attachment/cac:ExternalReference/cbc:URI or cac:Attachment/cbc:EmbeddedDocumentBinaryObject" />
      <xsl:otherwise>
        <svrl:failed-assert test="cac:Attachment/cac:ExternalReference/cbc:URI or cac:Attachment/cbc:EmbeddedDocumentBinaryObject">
          <xsl:attribute name="id">BR-IT-360</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-360] BT-124 (External document location) BT-125 (Attached document) - If BT-122 (Supporting document reference) not empty then BT-124 or BT-125 shall be mandatory.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice/cac:AllowanceCharge/cbc:Amount | //cn:CreditNote/cac:AllowanceCharge/cbc:Amount" mode="M9" priority="1028">
    <svrl:fired-rule context="//ubl:Invoice/cac:AllowanceCharge/cbc:Amount | //cn:CreditNote/cac:AllowanceCharge/cbc:Amount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
          <xsl:attribute name="id">BR-IT-290</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>  [BR-IT-290] BT-92 BT-99 (Document level allowance amount - Document level charge amount) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//ubl:Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:Amount | //cn:CreditNote/cac:CreditNoteLine/cac:AllowanceCharge/cbc:Amount" mode="M9" priority="1027">
    <svrl:fired-rule context="//ubl:Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:Amount | //cn:CreditNote/cac:CreditNoteLine/cac:AllowanceCharge/cbc:Amount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
          <xsl:attribute name="id">BR-IT-420</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>  [BR-IT-420] BT-136 BT-141 (Invoice line allowance amount - Invoice line charge amount) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:BillingReference/cac:InvoiceDocumentReference" mode="M9" priority="1026">
    <svrl:fired-rule context="cac:BillingReference/cac:InvoiceDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{1,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{1,20}$')">
          <xsl:attribute name="id">BR-IT-090</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-090] BT-25 (Preceding Invoice number)-BT maximum length shall be 20 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:OrderReference" mode="M9" priority="1025">
    <svrl:fired-rule context="cac:OrderReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')">
          <xsl:attribute name="id">BR-IT-040</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-040] BT-13 (Purchase order reference) - BT maximum length shall be 20 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Delivery/cac:DeliveryLocation/cac:Address[cac:Country/cbc:IdentificationCode='IT']" mode="M9" priority="1024">
    <svrl:fired-rule context="cac:Delivery/cac:DeliveryLocation/cac:Address[cac:Country/cbc:IdentificationCode='IT']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="cbc:StreetName" />
      <xsl:otherwise>
        <svrl:failed-assert test="cbc:StreetName">
          <xsl:attribute name="id">BR-IT-240-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-240-1] BT-75 (Deliver to address line 1) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="cbc:CityName" />
      <xsl:otherwise>
        <svrl:failed-assert test="cbc:CityName">
          <xsl:attribute name="id">BR-IT-240-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-240-2] BT-77 (Deliver to city) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')">
          <xsl:attribute name="id">BR-IT-240-3</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-240-3] BT-78 (Deliver to post code) - Fields are mandatory in Italy.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')">
          <xsl:attribute name="id">BR-IT-250</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-250] BT-79 (Deliver to country subdivision) - BT maximum length shall be 2 chars and shall be coded according to Italian province list else save in attachment.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:ContractDocumentReference" mode="M9" priority="1023">
    <svrl:fired-rule context="cac:ContractDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')">
          <xsl:attribute name="id">BR-IT-030</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-030] BT-12 (Contract reference) - BT maximum length shall be 20 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:DespatchDocumentReference" mode="M9" priority="1022">
    <svrl:fired-rule context="cac:DespatchDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')">
          <xsl:attribute name="id">BR-IT-060</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-060] BT-16 (Despatch advice reference) - BT maximum length shall be 20 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:DocumentReference" mode="M9" priority="1021">
    <svrl:fired-rule context="cac:DocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not (cbc:ID) or matches(normalize-space(cbc:ID), '^\p{IsBasicLatin}{1,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not (cbc:ID) or matches(normalize-space(cbc:ID), '^\p{IsBasicLatin}{1,35}$')">
          <xsl:attribute name="id">BR-IT-370</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-370] BT-128 (Invoice line object identifier) - BT maximum length shall be 35 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine | cac:CreditNoteLine " mode="M9" priority="1020">
    <svrl:fired-rule context="cac:InvoiceLine | cac:CreditNoteLine " />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:AccountingCost),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:AccountingCost),'^\p{IsBasicLatin}{0,20}$')">
          <xsl:attribute name="id">BR-IT-410</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-410] BT-133 (Invoice line Buyer accounting reference)- BT maximum length shall be 20 chars.
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cac:Price | cac:CreditNoteLine/cac:Price" mode="M9" priority="1019">
    <svrl:fired-rule context="cac:InvoiceLine/cac:Price | cac:CreditNoteLine/cac:Price" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:PriceAmount), '^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:PriceAmount), '^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')">
          <xsl:attribute name="id">BR-IT-430</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-430] BT-146 (Item net price) - BT  maximum lenght shall be 21. BT allowed fraction digits shall be 8
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cbc:InvoicedQuantity | cac:CreditNoteLine/cbc:CreditedQuantity" mode="M9" priority="1018">
    <svrl:fired-rule context="cac:InvoiceLine/cbc:InvoicedQuantity | cac:CreditNoteLine/cbc:CreditedQuantity" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[0-9]{1,12}([\.][0-9]{1,8})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[0-9]{1,12}([\.][0-9]{1,8})?$')">
          <xsl:attribute name="id">BR-IT-380</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-380] BT-129 (Invoiced quantity) - BT maximum lenght shall be 21 chars and BT allowed fraction digits shall be 8
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:InvoiceLine/cbc:LineExtensionAmount | cac:CreditNoteLine/cbc:LineExtensionAmount" mode="M9" priority="1017">
    <svrl:fired-rule context="cac:InvoiceLine/cbc:LineExtensionAmount | cac:CreditNoteLine/cbc:LineExtensionAmount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
          <xsl:attribute name="id">BR-IT-390</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-390] BT-131 (Invoice line net amount) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Item/cac:BuyersItemIdentification" mode="M9" priority="1016">
    <svrl:fired-rule context="cac:Item/cac:BuyersItemIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')">
          <xsl:attribute name="id">BR-IT-450</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-450] BT-156 (Item Buyer's identifier) - BT maximum length shall be 35 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Item/cac:CommodityClassification" mode="M9" priority="1015">
    <svrl:fired-rule context="cac:Item/cac:CommodityClassification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ItemClassificationCode),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ItemClassificationCode),'^\p{IsBasicLatin}{0,35}$')">
          <xsl:attribute name="id">BR-IT-470</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-470] BT-158 (Item classification identifier) - BT maximum length shall be 35 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Item/cac:SellersItemIdentification" mode="M9" priority="1014">
    <svrl:fired-rule context="cac:Item/cac:SellersItemIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')">
          <xsl:attribute name="id">BR-IT-440</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-440] BT-155 (Item Seller's identifier) - BT maximum length shall be 35 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Item/cac:StandardItemIdentification" mode="M9" priority="1013">
    <svrl:fired-rule context="cac:Item/cac:StandardItemIdentification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')">
          <xsl:attribute name="id">BR-IT-460</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-460] BT-157 (Item standard identifier) - BT maximum length shall be 35 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:LegalMonetaryTotal" mode="M9" priority="1012">
    <svrl:fired-rule context="cac:LegalMonetaryTotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:TaxInclusiveAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:TaxInclusiveAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
          <xsl:attribute name="id">BR-IT-300</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-300] BT-112 (Invoice total amount with VAT) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:PayableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:PayableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
          <xsl:attribute name="id">BR-IT-320</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-320] BT-115 (Amount due for payment) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(cbc:PayableRoundingAmount)) or (matches(normalize-space(cbc:PayableRoundingAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(cbc:PayableRoundingAmount)) or (matches(normalize-space(cbc:PayableRoundingAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$'))">
          <xsl:attribute name="id">BR-IT-310</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-310] BT-114 (Rounding amount) - BT maximum length shall be 15, including 2 fraction digit.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:OrderLineReference" mode="M9" priority="1011">
    <svrl:fired-rule context="cac:OrderLineReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:LineID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:LineID),'^\p{IsBasicLatin}{0,20}$')">
          <xsl:attribute name="id">BR-IT-400</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-400] BT-132 (Referenced purchase order line reference) - BT maximum length shall be 20 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:OriginatorDocumentReference" mode="M9" priority="1010">
    <svrl:fired-rule context="cac:OriginatorDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{1,15}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{1,15}$')">
          <xsl:attribute name="id">BR-IT-070</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-070] BT-17 (Tender or lot reference) -BT maximum length shall be 15 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans" mode="M9" priority="1009">
    <svrl:fired-rule context="cac:PaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="." />
      <xsl:otherwise>
        <svrl:failed-assert test=".">
          <xsl:attribute name="id">BR-IT-260</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-260] BG-16 Payment instructions - BG-16 shall be mandatory
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount" mode="M9" priority="1008">
    <svrl:fired-rule context="cac:PaymentMeans/cac:PayeeFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID), '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID), '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')">
          <xsl:attribute name="id">BR-IT-270</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-270] BT-84 (Payment account identifier) shall be an IBAN code according the pattern [a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30}) .
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:ProjectReference" mode="M9" priority="1007">
    <svrl:fired-rule context="cac:ProjectReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,15}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,15}$')">
          <xsl:attribute name="id">BR-IT-020-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-020] BT-11 (Project reference) - BT maximum length shall be 15 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="CreditNote/cac:AdditionalDocumentReference/cbc:ID[normalize-space(cbc:DocumentTypeCode) = '50']" mode="M9" priority="1006">
    <svrl:fired-rule context="CreditNote/cac:AdditionalDocumentReference/cbc:ID[normalize-space(cbc:DocumentTypeCode) = '50']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" matches(normalize-space(.),'^\p{IsBasicLatin}{0,15}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,15}$')">
          <xsl:attribute name="id">BR-IT-020-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-020-1] BT-11 (Project reference) - BT maximum length shall be 15 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:ReceiptDocumentReference" mode="M9" priority="1005">
    <svrl:fired-rule context="cac:ReceiptDocumentReference" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')">
          <xsl:attribute name="id">BR-IT-050</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-050] BT-15 (Receiving advice reference) - BT maximum length shall be 20 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch" mode="M9" priority="1004">
    <svrl:fired-rule context="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:ID) or matches(normalize-space(cbc:ID),'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:ID) or matches(normalize-space(cbc:ID),'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')">
          <xsl:attribute name="id">BR-IT-280</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-280] BT-86 (Payment service provider identifier) - BT shall contain a SWIFT/BIC (bank identifier code) according to structure defined in ISO 9362 (minimum length shall be 8- maximum length shall be 11 chars).
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxCategory/cbc:ID" mode="M9" priority="1003">
    <svrl:fired-rule context="cac:TaxCategory/cbc:ID" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE E S G K ',concat(' ',normalize-space(.),' ') ) ) )" />
      <xsl:otherwise>
        <svrl:failed-assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE E S G K ',concat(' ',normalize-space(.),' ') ) ) )">
          <xsl:attribute name="id">BR-IT-350</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-350] VAT category codes - only values E (Exempt from VAT), K	(Intra-community supply), AE (Reverse charge), G (Export outside the EU) ,S (Standard rated) shall be allowed .
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:Item/cac:ClassifiedTaxCategory/cbc:ID" mode="M9" priority="1002">
    <svrl:fired-rule context="cac:Item/cac:ClassifiedTaxCategory/cbc:ID" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE E S G K ',concat(' ',normalize-space(.),' ') ) ) )" />
      <xsl:otherwise>
        <svrl:failed-assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE E S G K ',concat(' ',normalize-space(.),' ') ) ) )">
          <xsl:attribute name="id">BR-IT-350-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-350] VAT category codes - only values E (Exempt from VAT), K	(Intra-community supply), AE (Reverse charge), G (Export outside the EU) ,S (Standard rated) shall be allowed .
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxRepresentativeParty/cac:PartyTaxScheme" mode="M9" priority="1001">
    <svrl:fired-rule context="cac:TaxRepresentativeParty/cac:PartyTaxScheme" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(cbc:CompanyID)) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(cbc:CompanyID)) &lt;= 30">
          <xsl:attribute name="id">BR-IT-230</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-230] BT-31 (Seller VAT identifier) - BT maximum length shall be 30 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="cac:TaxTotal/cac:TaxSubtotal" mode="M9" priority="1000">
    <svrl:fired-rule context="cac:TaxTotal/cac:TaxSubtotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:TaxableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:TaxableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?')">
          <xsl:attribute name="id">BR-IT-330</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-330] BT-116 (VAT category taxable amount) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(cbc:TaxAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(cbc:TaxAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
          <xsl:attribute name="id">BR-IT-340</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-340] BT-117 (VAT category tax amount) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>
  <xsl:template match="text()" mode="M9" priority="-1" />
  <xsl:template match="@*|node()" mode="M9" priority="-2">
    <xsl:apply-templates mode="M9" select="*" />
  </xsl:template>
</xsl:stylesheet>
