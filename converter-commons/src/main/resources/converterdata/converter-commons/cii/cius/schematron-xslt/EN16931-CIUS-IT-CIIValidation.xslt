<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:ccts="urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:qdt="urn:un:unece:uncefact:data:standard:QualifiedDataType:100" xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100" xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
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
    <svrl:schematron-output schemaVersion="" title="Italian Rules for EN16931 model in CII Syntax">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="rsm" uri="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100" />
      <svrl:ns-prefix-in-attribute-values prefix="ccts" uri="urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2" />
      <svrl:ns-prefix-in-attribute-values prefix="udt" uri="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100" />
      <svrl:ns-prefix-in-attribute-values prefix="qdt" uri="urn:un:unece:uncefact:data:standard:QualifiedDataType:100" />
      <svrl:ns-prefix-in-attribute-values prefix="ram" uri="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">CIUS-IT</xsl:attribute>
        <xsl:attribute name="name">CIUS-IT</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M8" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>Italian Rules for EN16931 model in CII Syntax</svrl:text>
  <xsl:param name="supplierCountry" select="if (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountryID) then upper-case(normalize-space(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountryID)) else 'XX'" />
  <xsl:param name="customerCountry" select="if (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CountryID) then upper-case(normalize-space(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CountryID)) else 'XX'" />

<!--PATTERN CIUS-IT-->


	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:ExchangedDocument" mode="M8" priority="1038">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:ExchangedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(ram:ID)) &lt;= 20 and matches(normalize-space(ram:ID),'(^\p{IsBasicLatin}*[0-9]+\p{IsBasicLatin}*$)')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(ram:ID)) &lt;= 20 and matches(normalize-space(ram:ID),'(^\p{IsBasicLatin}*[0-9]+\p{IsBasicLatin}*$)')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction" mode="M8" priority="1037">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="        count(distinct-values(//ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:ALIQUOTA']/ram:Value))&lt;= 1        and        count(distinct-values(//ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:TIPO']/ram:Value))&lt;= 1       and       count(distinct-values(//ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:CAUSALE']/ram:Value))&lt;= 1       " />
      <xsl:otherwise>
        <svrl:failed-assert test="count(distinct-values(//ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:ALIQUOTA']/ram:Value))&lt;= 1 and count(distinct-values(//ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:TIPO']/ram:Value))&lt;= 1 and count(distinct-values(//ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:CAUSALE']/ram:Value))&lt;= 1">
          <xsl:attribute name="id">BR-IT-490</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-490] BT-160 - Item attribute name - if more than one instance of BG-25 has BT-160="IT:RITENUTA:ALIQUOTA" or "IT:RITENUTA:TIPO" or "IT:RITENUTA:CAUSALE", then BT-161 shall have the same values". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement" mode="M8" priority="1036">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="( count(ram:SpecifiedTradeAllowanceCharge[ram:ChargeIndicator/udt:Indicator='true'][normalize-space(ram:Reason)='IT:BOLLO']) &lt;= 1 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="( count(ram:SpecifiedTradeAllowanceCharge[ram:ChargeIndicator/udt:Indicator='true'][normalize-space(ram:Reason)='IT:BOLLO']) &lt;= 1 )">
          <xsl:attribute name="id">BR-IT-295</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-295] BG-21 (DOCUMENT LEVEL CHARGES) - Only one instance of BG-21 can have BT-104="IT:BOLLO".
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount" mode="M8" priority="1035">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:ID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:ID),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication" mode="M8" priority="1034">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(ram:URIID)        and (ram:URIID[normalize-space(@schemeID) = 'IT:CODDEST'] or ram:URIID[normalize-space(@schemeID) = 'IT:PEC'] or ram:URIID[normalize-space(@schemeID) = '9921'] )" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:URIID) and (ram:URIID[normalize-space(@schemeID) = 'IT:CODDEST'] or ram:URIID[normalize-space(@schemeID) = 'IT:PEC'] or ram:URIID[normalize-space(@schemeID) = '9921'] )">
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
      <xsl:when test="$customerCountry!='IT' or not(ram:URIID[normalize-space(@schemeID) = 'IT:PEC'])        or ( (string-length(normalize-space(ram:URIID)) >= 7 and string-length(normalize-space(ram:URIID)) &lt;= 256) and matches(normalize-space(ram:URIID),'^.+@.+[.]+.+$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(ram:URIID[normalize-space(@schemeID) = 'IT:PEC']) or ( (string-length(normalize-space(ram:URIID)) >= 7 and string-length(normalize-space(ram:URIID)) &lt;= 256) and matches(normalize-space(ram:URIID),'^.+@.+[.]+.+$') )">
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
      <xsl:when test="$customerCountry!='IT' or not(ram:URIID[normalize-space(@schemeID) = '9921'] ) or (matches(normalize-space(ram:URIID),'^[A-Z0-9]{6}$'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(ram:URIID[normalize-space(@schemeID) = '9921'] ) or (matches(normalize-space(ram:URIID),'^[A-Z0-9]{6}$'))">
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
      <xsl:when test="$customerCountry!='IT' or not(ram:URIID[normalize-space(@schemeID)] = 'IT:CODDEST') or (matches(normalize-space(ram:URIID),'^[A-Z0-9]{7}$'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(ram:URIID[normalize-space(@schemeID)] = 'IT:CODDEST') or (matches(normalize-space(ram:URIID),'^[A-Z0-9]{7}$'))">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty" mode="M8" priority="1033">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or ram:SpecifiedTaxRegistration/ram:ID[normalize-space(@schemeID)='VA']        or ram:GlobalID[starts-with(normalize-space(.),'IT:CF:')] or ram:ID[starts-with(normalize-space(.),'IT:CF:')]" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or ram:SpecifiedTaxRegistration/ram:ID[normalize-space(@schemeID)='VA'] or ram:GlobalID[starts-with(normalize-space(.),'IT:CF:')] or ram:ID[starts-with(normalize-space(.),'IT:CF:')]">
          <xsl:attribute name="id">BR-IT-160</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-160] BT-48 BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) - f BT-48 is empty then BT-46 should be the FiscalCode. BT-46 shall starts with "IT:CF". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry != 'IT' or not(ram:GlobalID[starts-with(normalize-space(.),'IT:CF')])       or ( matches(normalize-space(ram:GlobalID[starts-with(normalize-space(.),'IT:CF')]),'(^IT:CF:[A-Z0-9]{11,16}$)'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry != 'IT' or not(ram:GlobalID[starts-with(normalize-space(.),'IT:CF')]) or ( matches(normalize-space(ram:GlobalID[starts-with(normalize-space(.),'IT:CF')]),'(^IT:CF:[A-Z0-9]{11,16}$)'))">
          <xsl:attribute name="id">BR-IT-160-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-160] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght shall be 22 starting with "IT:CF: ". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry != 'IT' or not(ram:ID[starts-with(normalize-space(.),'IT:CF')])        or ( matches(normalize-space(ram:ID[starts-with(normalize-space(.),'IT:CF')]),'(^IT:CF:[A-Z0-9]{11,16}$)'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry != 'IT' or not(ram:ID[starts-with(normalize-space(.),'IT:CF')]) or ( matches(normalize-space(ram:ID[starts-with(normalize-space(.),'IT:CF')]),'(^IT:CF:[A-Z0-9]{11,16}$)'))">
          <xsl:attribute name="id">BR-IT-160-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-160] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght shall be 22 starting with "IT:CF: ". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization" mode="M8" priority="1032">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry != 'IT' or not(ram:ID[starts-with(normalize-space(.),'IT:EORI:')])       or (string-length(normalize-space(ram:ID)) >= 21 and string-length(normalize-space(ram:ID)) &lt;=25)" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry != 'IT' or not(ram:ID[starts-with(normalize-space(.),'IT:EORI:')]) or (string-length(normalize-space(ram:ID)) >= 21 and string-length(normalize-space(ram:ID)) &lt;=25)">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedTaxRegistration" mode="M8" priority="1031">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedTaxRegistration" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not (ram:ID[normalize-space(@schemeID)='VA']) or (string-length(normalize-space(ram:ID[normalize-space(@schemeID)='VA'])) &lt;= 30 ) " />
      <xsl:otherwise>
        <svrl:failed-assert test="not (ram:ID[normalize-space(@schemeID)='VA']) or (string-length(normalize-space(ram:ID[normalize-space(@schemeID)='VA'])) &lt;= 30 )">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress" mode="M8" priority="1030">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($customerCountry = 'IT') or ram:LineOne" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or ram:LineOne">
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
      <xsl:when test="not($customerCountry = 'IT') or ram:CityName" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or ram:CityName">
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
      <xsl:when test="not($customerCountry = 'IT') or matches(normalize-space(ram:PostcodeCode),'^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or matches(normalize-space(ram:PostcodeCode),'^[0-9]{5}$')">
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
      <xsl:when test="not($customerCountry = 'IT') or not(ram:CountrySubDivisionName) or matches(normalize-space(ram:CountrySubDivisionName),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or not(ram:CountrySubDivisionName) or matches(normalize-space(ram:CountrySubDivisionName),'^[A-Z]{2}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty" mode="M8" priority="1029">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="every $GlobalID in (ram:GlobalID[starts-with(normalize-space(.),'IT:EORI:')]) satisfies (($supplierCountry!='IT')         or ( (string-length(normalize-space($GlobalID)) >= 21) and (string-length(normalize-space($GlobalID)) &lt;=25)))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $GlobalID in (ram:GlobalID[starts-with(normalize-space(.),'IT:EORI:')]) satisfies (($supplierCountry!='IT') or ( (string-length(normalize-space($GlobalID)) >= 21) and (string-length(normalize-space($GlobalID)) &lt;=25)))">
          <xsl:attribute name="id">BR-IT-100-1A</xsl:attribute>
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
      <xsl:when test="every $ramID in (ram:ID[starts-with(normalize-space(.),'IT:EORI:')]) satisfies (($supplierCountry!='IT')        or ( (string-length(normalize-space($ramID)) >= 21) and (string-length(normalize-space($ramID)) &lt;=25)))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $ramID in (ram:ID[starts-with(normalize-space(.),'IT:EORI:')]) satisfies (($supplierCountry!='IT') or ( (string-length(normalize-space($ramID)) >= 21) and (string-length(normalize-space($ramID)) &lt;=25)))">
          <xsl:attribute name="id">BR-IT-100-1B</xsl:attribute>
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
      <xsl:when test="every $GlobalID in (ram:GlobalID[starts-with(normalize-space(.),'IT:ALBO:')])        satisfies (($supplierCountry!='IT') or (matches(normalize-space($GlobalID), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $GlobalID in (ram:GlobalID[starts-with(normalize-space(.),'IT:ALBO:')]) satisfies (($supplierCountry!='IT') or (matches(normalize-space($GlobalID), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))">
          <xsl:attribute name="id">BR-IT-100-2A</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-100-2] BT-29 (Seller identifier) - BT-29 starting with "IT:ALBO has the format IT:ALBO:AlboProfessionale(1-60chars):NumeroIscrizioneAlbo(1-60chars) - (:) colon is permitted only as separator". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="every $ramID in (ram:ID[starts-with(normalize-space(.),'IT:ALBO:')])        satisfies (($supplierCountry!='IT') or (matches(normalize-space($ramID), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $ramID in (ram:ID[starts-with(normalize-space(.),'IT:ALBO:')]) satisfies (($supplierCountry!='IT') or (matches(normalize-space($ramID), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))">
          <xsl:attribute name="id">BR-IT-100-2B</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-100-2] BT-29 (Seller identifier) - BT-29 starting with "IT:ALBO has the format IT:ALBO:AlboProfessionale(1-60chars):NumeroIscrizioneAlbo(1-60chars) - (:) colon is permitted only as separator". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization" mode="M8" priority="1028">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="every $ramID in (ram:ID[starts-with(normalize-space(.), 'IT:REA:')])       satisfies ($supplierCountry != 'IT' or matches(normalize-space($ramID), '(^IT:REA:[A-Z]{2}:[\p{IsBasicLatin}]{1,20}$)'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $ramID in (ram:ID[starts-with(normalize-space(.), 'IT:REA:')]) satisfies ($supplierCountry != 'IT' or matches(normalize-space($ramID), '(^IT:REA:[A-Z]{2}:[\p{IsBasicLatin}]{1,20}$)'))">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration" mode="M8" priority="1027">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$supplierCountry!='IT' or ram:ID[normalize-space(@schemeID) !='FC'] or matches(normalize-space(ram:ID[normalize-space(@schemeID) ='FC']) ,'^[A-Z0-9]{11,16}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="$supplierCountry!='IT' or ram:ID[normalize-space(@schemeID) !='FC'] or matches(normalize-space(ram:ID[normalize-space(@schemeID) ='FC']) ,'^[A-Z0-9]{11,16}$')">
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

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$supplierCountry!='IT' or not (ram:ID[normalize-space(@schemeID) = 'VA']) or string-length(normalize-space(ram:ID[normalize-space(@schemeID) ='VA'])) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="$supplierCountry!='IT' or not (ram:ID[normalize-space(@schemeID) = 'VA']) or string-length(normalize-space(ram:ID[normalize-space(@schemeID) ='VA'])) &lt;= 30">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress" mode="M8" priority="1026">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($supplierCountry = 'IT') or not(ram:CountrySubDivisionName) or matches(normalize-space(ram:CountrySubDivisionName),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or not(ram:CountrySubDivisionName) or matches(normalize-space(ram:CountrySubDivisionName),'^[A-Z]{2}$')">
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

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($supplierCountry = 'IT') or ram:LineOne" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or ram:LineOne">
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
      <xsl:when test="not($supplierCountry = 'IT') or ram:CityName" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or ram:CityName">
          <xsl:attribute name="id">BR-IT-140-2</xsl:attribute>
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
      <xsl:when test="not($supplierCountry = 'IT') or matches(normalize-space(ram:PostcodeCode),'^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($supplierCountry = 'IT') or matches(normalize-space(ram:PostcodeCode),'^[0-9]{5}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[ram:TypeCode ='916']" mode="M8" priority="1025">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[ram:TypeCode ='916']" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="ram:URIID or ram:AttachmentBinaryObject" />
      <xsl:otherwise>
        <svrl:failed-assert test="ram:URIID or ram:AttachmentBinaryObject">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge" mode="M8" priority="1024">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:ActualAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:ActualAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge" mode="M8" priority="1023">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" matches(normalize-space(ram:ActualAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:ActualAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceReferencedDocument" mode="M8" priority="1022">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{1,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{1,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax" mode="M8" priority="1021">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:BasisAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:BasisAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?')">
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
      <xsl:when test="matches(normalize-space(ram:CalculatedAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:CalculatedAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument" mode="M8" priority="1020">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(normalize-space(ram:TypeCode) = '50') or (matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{1,15}$') and ram:TypeCode = '50')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(normalize-space(ram:TypeCode) = '50') or (matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{1,15}$') and ram:TypeCode = '50')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerOrderReferencedDocument" mode="M8" priority="1019">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerOrderReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress" mode="M8" priority="1018">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($customerCountry = 'IT') or ram:LineOne" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or ram:LineOne">
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
      <xsl:when test="not($customerCountry = 'IT') or ram:CityName" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or ram:CityName">
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
      <xsl:when test="not($customerCountry = 'IT') or matches(normalize-space(ram:PostcodeCode),'^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or matches(normalize-space(ram:PostcodeCode),'^[0-9]{5}$')">
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
      <xsl:when test="not(ram:CountryID = 'IT') or not(ram:CountrySubDivisionName) or matches(normalize-space(ram:CountrySubDivisionName),'^[A-Z]{2}$') " />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:CountryID = 'IT') or not(ram:CountrySubDivisionName) or matches(normalize-space(ram:CountrySubDivisionName),'^[A-Z]{2}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:ContractReferencedDocument" mode="M8" priority="1017">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:ContractReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:DespatchAdviceReferencedDocument" mode="M8" priority="1016">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:DespatchAdviceReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ReceivingAdviceReferencedDocument" mode="M8" priority="1015">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ReceivingAdviceReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:IssuerAssignedID),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration" mode="M8" priority="1014">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(ram:ID[normalize-space(@schemeID)='VA'])) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(ram:ID[normalize-space(@schemeID)='VA'])) &lt;= 30">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject" mode="M8" priority="1013">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" matches(normalize-space(ram:ID),'^\p{IsBasicLatin}{0,15}$') and ram:Name = 'Project reference'" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:ID),'^\p{IsBasicLatin}{0,15}$') and ram:Name = 'Project reference'">
          <xsl:attribute name="id">BR-IT-020</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-020] BT-11 (Project reference) - BT maximum length shall be 15 chars. 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation" mode="M8" priority="1012">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:GrandTotalAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:GrandTotalAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
      <xsl:when test="not(exists(ram:RoundingAmount)) or (matches(normalize-space(ram:RoundingAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(ram:RoundingAmount)) or (matches(normalize-space(ram:RoundingAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$'))">
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

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:DuePayableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:DuePayableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans" mode="M8" priority="1011">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans" />

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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount" mode="M8" priority="1010">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:IBANID), '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:IBANID), '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayerSpecifiedDebtorFinancialInstitution" mode="M8" priority="1009">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayerSpecifiedDebtorFinancialInstitution" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:BICID) or matches(normalize-space(ram:BICID),'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:BICID) or matches(normalize-space(ram:BICID),'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument" mode="M8" priority="1008">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:IssuerAssignedID) or (matches(normalize-space(ram:IssuerAssignedID), '^\p{IsBasicLatin}{1,35}$') and ram:TypeCode='130')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:IssuerAssignedID) or (matches(normalize-space(ram:IssuerAssignedID), '^\p{IsBasicLatin}{1,35}$') and ram:TypeCode='130')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount" mode="M8" priority="1007">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:ID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:ID),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement" mode="M8" priority="1006">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:NetPriceProductTradePrice/ram:ChargeAmount), '^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:NetPriceProductTradePrice/ram:ChargeAmount), '^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeDelivery" mode="M8" priority="1005">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeDelivery" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:BilledQuantity),'^[0-9]{1,12}([\.][0-9]{1,8})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:BilledQuantity),'^[0-9]{1,12}([\.][0-9]{1,8})?$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation" mode="M8" priority="1004">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:LineTotalAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:LineTotalAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct" mode="M8" priority="1003">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="normalize-space(ram:Name) = 'IT:CASSA' and        count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:CASSA:TIPO'])=1 and        count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:CASSA:ALIQUOTA'])=1        or       not(normalize-space(ram:Name) = 'IT:CASSA')             " />
      <xsl:otherwise>
        <svrl:failed-assert test="normalize-space(ram:Name) = 'IT:CASSA' and count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:CASSA:TIPO'])=1 and count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:CASSA:ALIQUOTA'])=1 or not(normalize-space(ram:Name) = 'IT:CASSA')">
          <xsl:attribute name="id">BR-IT-435</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-435] BT-153, BT-160 (Item name - Item attribute name) - if BT-153="IT:CASSA", then two instances of BG-32 shall have BT-160="IT:CASSA:TIPO" and BT-160="IT:CASSA:ALIQUOTA". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="        count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:ALIQUOTA'])=1 and        count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:TIPO'])=1 and       count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:CAUSALE'])=1       or       (       count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:ALIQUOTA'])=0 and        count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:TIPO'])=0 and       count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:CAUSALE'])=0       )" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:ALIQUOTA'])=1 and count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:TIPO'])=1 and count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:CAUSALE'])=1 or ( count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:ALIQUOTA'])=0 and count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:TIPO'])=0 and count(ram:ApplicableProductCharacteristic[normalize-space(ram:Description) ='IT:RITENUTA:CAUSALE'])=0 )">
          <xsl:attribute name="id">BR-IT-480</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-480] BT-160 - Item attribute name - if BT-160="IT:RITENUTA:ALIQUOTA" or BT-160="IT:RITENUTA:TIPO" or BT-160="IT:RITENUTA:CAUSALE", then three instances of BG-32 shall have BT-160="IT:RITENUTA:ALIQUOTA", BT-160="IT:RITENUTA:TIPO" and BT-160="IT:RITENUTA:CAUSALE". 
    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:SellerAssignedID),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:SellerAssignedID),'^\p{IsBasicLatin}{0,35}$')">
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

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:BuyerAssignedID),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:BuyerAssignedID),'^\p{IsBasicLatin}{0,35}$')">
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

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:GlobalID),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:GlobalID),'^\p{IsBasicLatin}{0,35}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification" mode="M8" priority="1002">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:ClassCode),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:ClassCode),'^\p{IsBasicLatin}{0,35}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement/ram:BuyerOrderReferencedDocument" mode="M8" priority="1001">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement/ram:BuyerOrderReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(ram:LineID),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(ram:LineID),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M8" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="ram:CategoryCode" mode="M8" priority="1000">
    <svrl:fired-rule context="ram:CategoryCode" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE L M E S G K ',concat(' ',normalize-space(.),' ') ) ) )" />
      <xsl:otherwise>
        <svrl:failed-assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE L M E S G K ',concat(' ',normalize-space(.),' ') ) ) )">
          <xsl:attribute name="id">BR-IT-350</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-350] VAT category codes - only values E (Exempt from VAT), K	(Intra-community supply), AE (Reverse charge), G (Export outside the EU) ,S (Standard rated), L (IGIC), M (IPSI) shall be allowed . 
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
</xsl:stylesheet>
