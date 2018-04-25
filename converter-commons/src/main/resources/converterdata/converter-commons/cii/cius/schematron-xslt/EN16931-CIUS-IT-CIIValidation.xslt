<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
                xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
                xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
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
    <svrl:schematron-output schemaVersion="" title="EN16931 model bound to CII">
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
        <xsl:apply-templates mode="M11" select="/"/>
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">CIUS-USAGE-IT</xsl:attribute>
        <xsl:attribute name="name">CIUS-USAGE-IT</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
        <xsl:apply-templates mode="M12" select="/"/>
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">CIUS-SYNTAX-IT</xsl:attribute>
        <xsl:attribute name="name">CIUS-SYNTAX-IT</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
        <xsl:apply-templates mode="M13" select="/"/>
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>EN16931 model bound to CII</svrl:text>
    <xsl:param name="supplierCountry"
               select="if (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountryID) then upper-case(normalize-space(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountryID)) else 'XX'"/>
    <xsl:param name="customerCountry"
               select="if (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CountryID) then upper-case(normalize-space(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CountryID)) else 'XX'"/>

<!--PATTERN CIUS-IT-->


	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount"
            mode="M11" priority="1003">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(ram:IBANID, '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(ram:IBANID, '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')">
          <xsl:attribute name="id">CIUS-BT-84</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-BT-84] BT-84 (Payment account identifier)  shall be an IBAN code and respect the Regular Expression [a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30}) .
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M11" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication"
            mode="M11" priority="1002">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="exists(ram:URIID) and (ram:URIID[@schemeID = 'IT:CODDEST'] or ram:URIID[@schemeID = 'IT:PEC'] or ram:URIID[@schemeID = 'IT:IPA'] )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="exists(ram:URIID) and (ram:URIID[@schemeID = 'IT:CODDEST'] or ram:URIID[@schemeID = 'IT:PEC'] or ram:URIID[@schemeID = 'IT:IPA'] )">
          <xsl:attribute name="id">CIUS-CA-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-2] BT-49 BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario (see the Italian business rules). BT-49-1=IT:PEC or IT:IPA or IT:CODDEST 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M11" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans"
            mode="M11" priority="1001">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(ram:TypeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:TypeCode)">
          <xsl:attribute name="id">CIUS-CA-103</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-103] BT-81 (Payment means type code) -Fields are mandatory in XMLPA. Mapped BTs should be mandatory
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M11" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty"
            mode="M11" priority="1000">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="exists(ram:SpecifiedTaxRegistration/ram:ID and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA']) or exists(ram:GlobalID[@schemeID = 'IT:CF']) or exists(ram:GlobalID[@schemeID = 'IT:VAT'])"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="exists(ram:SpecifiedTaxRegistration/ram:ID and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA']) or exists(ram:GlobalID[@schemeID = 'IT:CF']) or exists(ram:GlobalID[@schemeID = 'IT:VAT'])">
          <xsl:attribute name="id">CIUS-BR-14</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-BR-14] BT-48 BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) -1.4.1.1 is not mandatory in Italy (buyer) but VAT number or Fiscal code should be indicated. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M11" select="*|comment()|processing-instruction()"/>
  </xsl:template>
    <xsl:template match="text()" mode="M11" priority="-1"/>
    <xsl:template match="@*|node()" mode="M11" priority="-2">
        <xsl:apply-templates mode="M11" select="*|comment()|processing-instruction()"/>
  </xsl:template>

<!--PATTERN CIUS-USAGE-IT-->


	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty"
            mode="M12" priority="1005">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not(ram:PostalTradeAddress/ram:CountryID = 'IT') or count(ram:SpecifiedTaxRegistration/ram:ID[@schemeID='FC'])>=1"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not(ram:PostalTradeAddress/ram:CountryID = 'IT') or count(ram:SpecifiedTaxRegistration/ram:ID[@schemeID='FC'])>=1">
          <xsl:attribute name="id">CIUS-BT-98-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-BT-98-2] BT-32 (Seller tax registration identifier). In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8)
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement"
                  mode="M12" priority="1004">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test=" (exists(ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID) and ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID[@schemeID ='VA']) or (exists(ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration/ram:ID) and  ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA'])"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="(exists(ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID) and ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID[@schemeID ='VA']) or (exists(ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration/ram:ID) and ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA'])">
          <xsl:attribute name="id">CIUS-CA-9</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-CA-9] BT-31 BT-63 (Seller VAT identifier - Seller tax representative VAT identifier)
                  -Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax representative).
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress"
            mode="M12" priority="1003">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(ram:LineOne)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:LineOne)">
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
      <xsl:when test="exists(ram:CityName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:CityName)">
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
      <xsl:when test="exists(ram:PostcodeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:PostcodeCode)">
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
        <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress"
            mode="M12" priority="1002">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(ram:LineOne)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:LineOne)">
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
      <xsl:when test="exists(ram:CityName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:CityName)">
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
      <xsl:when test="exists(ram:PostcodeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:PostcodeCode)">
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
        <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress"
            mode="M12" priority="1001">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(ram:LineOne)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:LineOne)">
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
      <xsl:when test="exists(ram:CityName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:CityName)">
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
      <xsl:when test="exists(ram:PostcodeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:PostcodeCode)">
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
        <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument"
            mode="M12" priority="1000">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="exists(ram:URIID) or exists(ram:AttachmentBinaryObject)" />
      <xsl:otherwise>
        <svrl:failed-assert test="exists(ram:URIID) or exists(ram:AttachmentBinaryObject)">
          <xsl:attribute name="id">CIUS-CA-71</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-CA-71] BT-125 (Attached document) -If BT-122 not empty then BT-124 or BT-125 should be mandatory as the mapped field is mandatory in Italy.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()"/>
  </xsl:template>
    <xsl:template match="text()" mode="M12" priority="-1"/>
    <xsl:template match="@*|node()" mode="M12" priority="-2">
        <xsl:apply-templates mode="M12" select="*|comment()|processing-instruction()"/>
  </xsl:template>

<!--PATTERN CIUS-SYNTAX-IT-->


	<!--RULE -->
    <xsl:template match="//rsm:CrossIndustryInvoice/rsm:ExchangedDocument" mode="M13" priority="1039">
    <svrl:fired-rule context="//rsm:CrossIndustryInvoice/rsm:ExchangedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-32</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-32] BT-1 (Invoice number) -BT maximum length shall be 20 digits.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template match="//rsm:CrossIndustryInvoice/rsm:ExchangedDocument/ram:IncludedNote" mode="M13" priority="1038">
    <svrl:fired-rule context="//rsm:CrossIndustryInvoice/rsm:ExchangedDocument/ram:IncludedNote" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when test="string-length(concat(ram:SubjectCode,'-',ram:Content )) &lt;= 200"/>
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(concat(ram:SubjectCode,'-',ram:Content )) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-39</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-39] BT-21, BT-22 (Invoice note subject code Invoice note) -The sum of BTs maximum length
                shall be 200 chars.
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument"
            mode="M13" priority="1037">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:AttachmentBinaryObject/@mimeCode) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:AttachmentBinaryObject/@mimeCode) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-72</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-72] BT-125-1 (Attached document Mime code) -BT maximum length shall be 10 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:TypeCode = '50') or (string-length(ram:IssuerAssignedID) &lt;= 15 and ram:TypeCode = '50')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:TypeCode = '50') or (string-length(ram:IssuerAssignedID) &lt;= 15 and ram:TypeCode = '50')">
          <xsl:attribute name="id">CIUS-VD-37</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-37] BT-17 (Tender or lot reference) -BT maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:TypeCode = '916') or ( ram:TypeCode = '916' and string-length(ram:IssuerAssignedID) + string-length(ram:AttachmentBinaryObject/@filename) &lt;= 60)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:TypeCode = '916') or ( ram:TypeCode = '916' and string-length(ram:IssuerAssignedID) + string-length(ram:AttachmentBinaryObject/@filename) &lt;= 60)">
          <xsl:attribute name="id">CIUS-VD-69</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-69] BT-122, BT-125-2 (Supporting document reference - Attached document Filename) - BT maximum length shall be 60 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Name) &lt;= 100" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Name) &lt;= 100">
          <xsl:attribute name="id">CIUS-VD-70</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-70] BT-123 (Supporting document description) -BT maximum length shall be 100 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerOrderReferencedDocument"
            mode="M13" priority="1036">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerOrderReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IssuerAssignedID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IssuerAssignedID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-35</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-35] BT-13 (Purchase order reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:DefinedTradeContact"
            mode="M13" priority="1035">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:DefinedTradeContact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:PersonName) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:PersonName) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-51</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-51] BT-56 (Buyer contact point) -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty"
            mode="M13" priority="1034">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:GlobalID[@schemeID = 'IT:CF'])  or ( (string-length(ram:GlobalID) >= 11) and (string-length(ram:GlobalID) &lt;=16)   and matches(ram:GlobalID,'^[A-Z0-9]{11,16}$')  )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:GlobalID[@schemeID = 'IT:CF']) or ( (string-length(ram:GlobalID) >= 11) and (string-length(ram:GlobalID) &lt;=16) and matches(ram:GlobalID,'^[A-Z0-9]{11,16}$') )">
          <xsl:attribute name="id">CIUS-VD-100-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-100-1] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -BT46-1=IT:CF then BT-46 minimum lenght 11 and maximum lenght shall be 16
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:GlobalID[@schemeID = 'IT:EORI'])  or ( (string-length(ram:GlobalID) >= 13) and (string-length(ram:GlobalID) &lt;=17))"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:GlobalID[@schemeID = 'IT:EORI']) or ( (string-length(ram:GlobalID) >= 13) and (string-length(ram:GlobalID) &lt;=17))">
          <xsl:attribute name="id">CIUS-VD-100-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-100-2] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:EORI then BT-46 minimum lenght 13 and maximum lenght shall be 17
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:GlobalID[@schemeID = 'IT:VAT'])  or ( (string-length(ram:GlobalID) &lt;= 30) and (contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(ram:GlobalID,1,2) ) ))"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:GlobalID[@schemeID = 'IT:VAT']) or ( (string-length(ram:GlobalID) &lt;= 30) and (contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(ram:GlobalID,1,2) ) ))">
          <xsl:attribute name="id">CIUS-VD-100-3</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-100-3] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:VAT then BT-46 maximum length 30 (the first two chars indicates country code).
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:GlobalID[starts-with(.,'IT:CF')])  or ( (string-length(ram:GlobalID) >= 17) and (string-length(ram:GlobalID) &lt;=22)   and matches(ram:GlobalID,'^[A-Z0-9]{11,16}$')  )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:GlobalID[starts-with(.,'IT:CF')]) or ( (string-length(ram:GlobalID) >= 17) and (string-length(ram:GlobalID) &lt;=22) and matches(ram:GlobalID,'^[A-Z0-9]{11,16}$') )">
              <xsl:attribute name="id">CIUS-VD-100-1TMP</xsl:attribute>
              <xsl:attribute name="flag">warning</xsl:attribute>
              <xsl:attribute name="location">
                  <xsl:apply-templates mode="schematron-select-full-path" select="."/>
              </xsl:attribute>
              <svrl:text>[CIUS-VD-100-1TMP] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght shall
                  be 22 starting with "IT:CF ".
              </svrl:text>
          </svrl:failed-assert>
      </xsl:otherwise>
</xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="not (ram:GlobalID[starts-with(.,'IT:EORI')])  or ( (string-length(ram:GlobalID) >= 21) and (string-length(ram:GlobalID) &lt;=25))"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="not (ram:GlobalID[starts-with(.,'IT:EORI')]) or ( (string-length(ram:GlobalID) >= 21) and (string-length(ram:GlobalID) &lt;=25))">
                    <xsl:attribute name="id">CIUS-VD-100-2TMP</xsl:attribute>
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates mode="schematron-select-full-path" select="."/>
                    </xsl:attribute>
                    <svrl:text>[CIUS-VD-100-2TMP] BT-46 (Buyer identifier) - BT-46 minimum lenght 21 and maximum lenght
                        shall be 25 starting with "IT:EORI ".
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="not (ram:ID[starts-with(.,'IT:CF')])  or ( (string-length(ram:ID) >= 17) and (string-length(ram:ID) &lt;=22)   and matches(ram:ID,'^[A-Z0-9]{11,16}$')  )"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="not (ram:ID[starts-with(.,'IT:CF')]) or ( (string-length(ram:ID) >= 17) and (string-length(ram:ID) &lt;=22) and matches(ram:ID,'^[A-Z0-9]{11,16}$') )">
                    <xsl:attribute name="id">CIUS-VD-100-1BTMP</xsl:attribute>
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates mode="schematron-select-full-path" select="."/>
                    </xsl:attribute>
                    <svrl:text>[CIUS-VD-100-1TMP] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght
                        shall be 22 starting with "IT:CF ".
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="not (ram:ID[starts-with(.,'IT:EORI')])  or ( (string-length(ram:ID) >= 21) and (string-length(ram:ID) &lt;=25))"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="not (ram:ID[starts-with(.,'IT:EORI')]) or ( (string-length(ram:ID) >= 21) and (string-length(ram:ID) &lt;=25))">
                    <xsl:attribute name="id">CIUS-VD-100-2BTMP</xsl:attribute>
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates mode="schematron-select-full-path" select="."/>
                    </xsl:attribute>
                    <svrl:text>[CIUS-VD-100-2TMP] BT-46 (Buyer identifier) - BT-46 minimum lenght 21 and maximum lenght
                        shall be 25 starting with "IT:EORI ".
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(exists(ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA'] ) or ( exists(ram:ID) and exists(ram:GlobalID[@schemeID]) )"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="(exists(ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA'] ) or ( exists(ram:ID) and exists(ram:GlobalID[@schemeID]) )">
          <xsl:attribute name="id">CIUS-VD-53</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-53] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-48 is empty then one of the buyer identifiers (0..n) should be the FiscalCode in BT-46. BT-46-1 shall contain the scheme.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (exists (ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA']) or (string-length(ram:SpecifiedTaxRegistration/ram:ID) &lt;= 30 and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA']) "/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (exists (ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA']) or (string-length(ram:SpecifiedTaxRegistration/ram:ID) &lt;= 30 and ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA'])">
          <xsl:attribute name="id">CIUS-VD-43</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-43] BT-48 (Buyer VAT identifier) -BT maximum length shall be 30 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Name) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Name) &lt;= 80">
          <xsl:attribute name="id">CIUS-VD-18</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-18] BT-44 (Buyer name) -BT maximum length shall be 80 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress"
            mode="M13" priority="1033">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:CityName) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-24</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-24] BT-52 (Buyer city) -BT maximum length shall be 60 characters. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:CountryID = 'IT') or not(exists(ram:CountrySubDivisionName)) or string-length(ram:CountrySubDivisionName) = 2" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:CountryID = 'IT') or not(exists(ram:CountrySubDivisionName)) or string-length(ram:CountrySubDivisionName) = 2">
          <xsl:attribute name="id">CIUS-VD-30</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-30] BT-54 (Buyer country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:CountryID = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:CountryID = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName )">
          <xsl:attribute name="id">CIUS-VD-48</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-48] BT-54 (Buyer country subdivision) -If country code=IT it should be coded according to Italian province list.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-21</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-21] BT-50, BT-51, BT-163 (Buyer address line 1 - Buyer address line 2 - Buyer address line 3) -The sum of BTs maximum length shall be 60 chars (including separator).
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test=" not($customerCountry='IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) > 0 )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not($customerCountry='IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) > 0 )">
          <xsl:attribute name="id">CIUS-VD-27-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-27-2] BT-53 (Buyer post code) -BT maximum length, if country code =IT then it should
                  be numeric and maximum length 5.
              </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication"
            mode="M13" priority="1032">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not(ram:URIID[@schemeID = 'IT:PEC']) or ( (string-length(ram:URIID) >= 7 and string-length(ram:URIID) &lt;= 256) and matches(ram:URIID,'^.+@.+[.]+.+$') )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not(ram:URIID[@schemeID = 'IT:PEC']) or ( (string-length(ram:URIID) >= 7 and string-length(ram:URIID) &lt;= 256) and matches(ram:URIID,'^.+@.+[.]+.+$') )">
          <xsl:attribute name="id">CIUS-VD-97-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-97-1] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address
                  identification scheme identifier) -If BT-49-1=IT:PEC schema then BT-49 should be a PEC (email) address
                  and length shall be between 7 and 256 character
              </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not(ram:URIID[@schemeID = 'IT:IPA'] ) or ( (string-length(ram:URIID) = 6) and matches(ram:URIID,'^[A-Z0-9]{6,7}$') )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not(ram:URIID[@schemeID = 'IT:IPA'] ) or ( (string-length(ram:URIID) = 6) and matches(ram:URIID,'^[A-Z0-9]{6,7}$') )">
          <xsl:attribute name="id">CIUS-VD-97-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-97-2] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address
                  identification scheme identifier) =IT:IPA schema then BT-49 should be a IPA code and maximum length
                  shall be 6 chars
              </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:URIID/@schemeID = 'IT:CODDEST') or ( string-length(ram:URIID) = 7  and matches(ram:URIID,'^[A-Z0-9]{6,7}$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:URIID/@schemeID = 'IT:CODDEST') or ( string-length(ram:URIID) = 7 and matches(ram:URIID,'^[A-Z0-9]{6,7}$') )">
          <xsl:attribute name="id">CIUS-VD-97-3</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-97-3] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification
                scheme identifier)=IT:CODDEST schema then BT-49 and maximum length shall be 7 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:ContractReferencedDocument"
            mode="M13" priority="1031">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:ContractReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IssuerAssignedID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IssuerAssignedID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-34</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-34] BT-12 (Contract reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty"
            mode="M13" priority="1030">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Name) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Name) &lt;= 80">
          <xsl:attribute name="id">CIUS-VD-19</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-19] BT-62 (Seller tax representative name) -BT maximum length shall be 80 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount"
            mode="M13" priority="1029">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IBANID) &lt;= 34 and string-length(ram:IBANID) >= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IBANID) &lt;= 34 and string-length(ram:IBANID) >= 15">
          <xsl:attribute name="id">CIUS-VD-57</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-57] BT-84 (Payment account identifier) -BT minimum length shall be 15, maximum length shall be 34 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement"
                  mode="M13" priority="1028">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:PaymentReference) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:PaymentReference) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-56</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-56] BT-83 (Remittance information) -BT maximum length shall be 60 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty"
            mode="M13" priority="1027">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when test="string-length(ram:ID) &lt;= 30 and ram:ID[@schemeID='VA']"/>
      <xsl:otherwise>
          <svrl:failed-assert test="string-length(ram:ID) &lt;= 30 and ram:ID[@schemeID='VA']">
          <xsl:attribute name="id">CIUS-VD-42</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-42] BT-63 (Seller tax representative VAT identifier) -BT maximum length shall be 30 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:DefinedTradeContact"
            mode="M13" priority="1026">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:DefinedTradeContact" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(ram:EmailURIUniversalCommunication/ram:URIID)) or (string-length(ram:EmailURIUniversalCommunication/ram:URIID) &lt;= 256 and string-length(ram:EmailURIUniversalCommunication/ram:URIID) >= 7)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(ram:EmailURIUniversalCommunication/ram:URIID)) or (string-length(ram:EmailURIUniversalCommunication/ram:URIID) &lt;= 256 and string-length(ram:EmailURIUniversalCommunication/ram:URIID) >= 7)">
          <xsl:attribute name="id">CIUS-VD-46</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-46] BT-43 (Seller contact email address) -BT minimum length shall be 7 maximum length shall be 256 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:PersonName) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:PersonName) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-44-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-44-1] BT-41 (Seller contact point)  -BT maximum length shall be 200 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:DepartmentName) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:DepartmentName) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-44-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-44-2] BT-41 (Seller contact point)  -BT maximum length shall be 200 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(ram:TelephoneUniversalCommunication/ram:CompleteNumber)) or (string-length(ram:TelephoneUniversalCommunication/ram:CompleteNumber) &lt;= 12 and string-length(ram:TelephoneUniversalCommunication/ram:CompleteNumber) >= 5)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(ram:TelephoneUniversalCommunication/ram:CompleteNumber)) or (string-length(ram:TelephoneUniversalCommunication/ram:CompleteNumber) &lt;= 12 and string-length(ram:TelephoneUniversalCommunication/ram:CompleteNumber) >= 5)">
          <xsl:attribute name="id">CIUS-VD-45</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-45] BT-42 (Seller contact telephone number) -BT minimum length shall be 5 maximum length shall be 12 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty"
            mode="M13" priority="1025">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:GlobalID[@schemeID = 'IT:CF'])  or ( (string-length(ram:GlobalID[@schemeID = 'IT:CF']) >= 11) and (string-length(ram:GlobalID[@schemeID = 'IT:CF']) &lt;=16))"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:GlobalID[@schemeID = 'IT:CF']) or ( (string-length(ram:GlobalID[@schemeID = 'IT:CF']) >= 11) and (string-length(ram:GlobalID[@schemeID = 'IT:CF']) &lt;=16))">
          <xsl:attribute name="id">CIUS-VD-101-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-101-1B] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme
                  identifier) -BT29-1=IT:CF then BT-29 minimum lenght 11 and maximum lenght shall be 16.
              </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:GlobalID[@schemeID = 'IT:EORI'])  or ( (string-length(ram:GlobalID[@schemeID = 'IT:EORI']) >= 13) and (string-length(ram:GlobalID[@schemeID = 'IT:EORI']) &lt;=17))"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:GlobalID[@schemeID = 'IT:EORI']) or ( (string-length(ram:GlobalID[@schemeID = 'IT:EORI']) >= 13) and (string-length(ram:GlobalID[@schemeID = 'IT:EORI']) &lt;=17))">
          <xsl:attribute name="id">CIUS-VD-101-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-101-2B] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme
                  identifier) -If BT-29-1=IT:EORI then BT-29 minimum lenght 13 and maximum lenght shall be 17 .
              </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:GlobalID[@schemeID = 'IT:VAT'])  or ( (string-length(ram:GlobalID[@schemeID = 'IT:VAT']) &lt;= 30) and ( contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(ram:GlobalID[@schemeID = 'IT:VAT'],1,2) ) ))"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:GlobalID[@schemeID = 'IT:VAT']) or ( (string-length(ram:GlobalID[@schemeID = 'IT:VAT']) &lt;= 30) and ( contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(ram:GlobalID[@schemeID = 'IT:VAT'],1,2) ) ))">
          <xsl:attribute name="id">CIUS-VD-101-3</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-101-3B] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme
                  identifier) -If BT-29-1=IT:VAT then BT-29 maximum length 30 (the first two chars indicates country
                  code).
              </svrl:text>
          </svrl:failed-assert>
      </xsl:otherwise>
</xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="every $ramID in (ram:ID) satisfies (not ($ramID[starts-with(.,'IT:CF')])  or ( (string-length($ramID) >= 17) and (string-length($ramID) &lt;=22)))"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="every $ramID in (ram:ID) satisfies (not ($ramID[starts-with(.,'IT:CF')]) or ( (string-length($ramID) >= 17) and (string-length($ramID) &lt;=22)))">
                    <xsl:attribute name="id">CIUS-VD-101-1TMP</xsl:attribute>
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates mode="schematron-select-full-path" select="."/>
                    </xsl:attribute>
                    <svrl:text>[CIUS-VD-101-1TMP] BT-29 (Seller identifier) - BT-29 minimum lenght 17 and maximum lenght
                        shall be 22 starting with "IT:CF ".
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="every $GlobalID in (ram:GlobalID) satisfies (not ($GlobalID[starts-with(.,'IT:CF')])  or ( (string-length($GlobalID) >= 17) and (string-length($GlobalID) &lt;=22)))"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="every $GlobalID in (ram:GlobalID) satisfies (not ($GlobalID[starts-with(.,'IT:CF')]) or ( (string-length($GlobalID) >= 17) and (string-length($GlobalID) &lt;=22)))">
                    <xsl:attribute name="id">CIUS-VD-101-1BTMP</xsl:attribute>
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates mode="schematron-select-full-path" select="."/>
                    </xsl:attribute>
                    <svrl:text>[CIUS-VD-101B-1TMP] BT-29 (Seller identifier) - BT-29 minimum lenght 17 and maximum
                        lenght shall be 22 starting with "IT:CF ".
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="every $GlobalID in (ram:GlobalID) satisfies (not ($GlobalID[starts-with(.,'IT:EORI')])  or ( (string-length($GlobalID) >= 21) and (string-length($GlobalID) &lt;=25)))"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="every $GlobalID in (ram:GlobalID) satisfies (not ($GlobalID[starts-with(.,'IT:EORI')]) or ( (string-length($GlobalID) >= 21) and (string-length($GlobalID) &lt;=25)))">
                    <xsl:attribute name="id">CIUS-VD-101-2BTMP</xsl:attribute>
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates mode="schematron-select-full-path" select="."/>
                    </xsl:attribute>
                    <svrl:text>[CIUS-VD-101B-2TMP] BT-29 Seller identifier) - BT-29 minimum lenght 21 and maximum lenght
                        shall be 25 starting with "IT:EORI ".
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="every $ramID in (ram:ID) satisfies (not ($ramID[starts-with(.,'IT:EORI')])  or ( (string-length($ramID) >= 21) and (string-length($ramID) &lt;=25)))"/>
            <xsl:otherwise>
                <svrl:failed-assert
                        test="every $ramID in (ram:ID) satisfies (not ($ramID[starts-with(.,'IT:EORI')]) or ( (string-length($ramID) >= 21) and (string-length($ramID) &lt;=25)))">
                    <xsl:attribute name="id">CIUS-VD-101-2TMP</xsl:attribute>
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates mode="schematron-select-full-path" select="."/>
                    </xsl:attribute>
                    <svrl:text>[CIUS-VD-101B-2TMP] BT-29 Seller identifier) - BT-29 minimum lenght 21 and maximum lenght
                        shall be 25 starting with "IT:EORI ".
                    </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Name) &lt;= 80" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Name) &lt;= 80">
          <xsl:attribute name="id">CIUS-VD-17</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-17] BT-27 (Seller name) -BT maximum length shall be 80 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress"
            mode="M13" priority="1024">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:CityName) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-23</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-23] BT-37 (Seller city) -BT maximum length shall be 60 characters.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not(ram:CountryID = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not(ram:CountryID = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName )">
              <xsl:attribute name="id">CIUS-VD-47</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-47] BT-39 (Seller country subdivision) -If country code=IT it should be coded
                  according to Italian province list.
              </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when test="(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60">
              <xsl:attribute name="id">CIUS-VD-20</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-20] BT-35, BT-36, BT-162 (Seller address line 1 - Seller address line 2 - Seller
                  address line 3) -The sum of BTs maximum length shall be 60 chars (including separator).
              </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not ($supplierCountry='IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) > 0 )"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not ($supplierCountry='IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) > 0 )">
              <xsl:attribute name="id">CIUS-VD-26-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
              <svrl:text>[CIUS-VD-26-1-2] BT-38 (Seller post code) -BT maximum length, if country code =IT then it
                  should be numeric and maximum length 5.
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
    </xsl:template>

    <!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization"
            mode="M13" priority="1023">
        <svrl:fired-rule
                context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization"/>

        <!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:ID[@schemeID = 'IT:REA'])  or ( (string-length(ram:ID) >= 3) and (string-length(ram:ID) &lt;=22) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(ram:ID,1,2) )))"/>
    <xsl:otherwise>
        <svrl:failed-assert
                test="not (ram:ID[@schemeID = 'IT:REA']) or ( (string-length(ram:ID) >= 3) and (string-length(ram:ID) &lt;=22) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(ram:ID,1,2) )))">
            <xsl:attribute name="id">CIUS-VD-102-1</xsl:attribute>
            <xsl:attribute name="flag">warning</xsl:attribute>
            <xsl:attribute name="location">
                <xsl:apply-templates mode="schematron-select-full-path" select="." />
            </xsl:attribute>
            <svrl:text>[CIUS-VD-102-1] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration
                identifier identification scheme identifier) -If BT-30-1=IT:REA then BT-30 minimum lenght 3 and maximum
                lenght shall be 22 (first two chars indicate the italian province code).
            </svrl:text>
        </svrl:failed-assert>
    </xsl:otherwise>
</xsl:choose>

        <!--ASSERT -->
<xsl:choose>
    <xsl:when test="not (ram:ID[@schemeID = 'IT:ALBO'])  or (string-length(ram:ID) &lt;=120)"/>
    <xsl:otherwise>
        <svrl:failed-assert test="not (ram:ID[@schemeID = 'IT:ALBO']) or (string-length(ram:ID) &lt;=120)">
            <xsl:attribute name="id">CIUS-VD-102-2</xsl:attribute>
            <xsl:attribute name="flag">warning</xsl:attribute>
            <xsl:attribute name="location">
                <xsl:apply-templates mode="schematron-select-full-path" select="." />
            </xsl:attribute>
            <svrl:text>[CIUS-VD-102-2] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration
                identifier identification scheme identifier) -If BT-30-1=IT:ALBO then BT-30 maximum length 120 .
            </svrl:text>
        </svrl:failed-assert>
    </xsl:otherwise>
</xsl:choose>

        <!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not (ram:ID[starts-with(.,'IT:REA')])  or ( (string-length(ram:ID) >= 10) and (string-length(ram:ID) &lt;=29) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(ram:ID,1,2) )))"/>
    <xsl:otherwise>
        <svrl:failed-assert
                test="not (ram:ID[starts-with(.,'IT:REA')]) or ( (string-length(ram:ID) >= 10) and (string-length(ram:ID) &lt;=29) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(ram:ID,1,2) )))">
            <xsl:attribute name="id">CIUS-VD-102-1TMP</xsl:attribute>
            <xsl:attribute name="flag">warning</xsl:attribute>
            <xsl:attribute name="location">
                <xsl:apply-templates mode="schematron-select-full-path" select="." />
            </xsl:attribute>
            <svrl:text>[CIUS-VD-102-1TMP] BT-30 (Seller legal registration identifier) - BT-30 minimum lenght 10 and
                maximum lenght shall be 29 starting with "IT:REA " and the following two chars indicate the italian
                province code).
            </svrl:text>
        </svrl:failed-assert>
    </xsl:otherwise>
</xsl:choose>

        <!--ASSERT -->
<xsl:choose>
    <xsl:when test="not (ram:ID[starts-with(.,'IT:ALBO')])  or (string-length(ram:ID) &lt;=128)"/>
    <xsl:otherwise>
        <svrl:failed-assert test="not (ram:ID[starts-with(.,'IT:ALBO')]) or (string-length(ram:ID) &lt;=128)">
            <xsl:attribute name="id">CIUS-VD-102-2TMP</xsl:attribute>
            <xsl:attribute name="flag">warning</xsl:attribute>
            <xsl:attribute name="location">
                <xsl:apply-templates mode="schematron-select-full-path" select="." />
            </xsl:attribute>
            <svrl:text>[CIUS-VD-102-2TMP] BT-30 (Seller legal registration identifier) - BT-30 maximum length 128
                starting with "IT:ALBO ".
            </svrl:text>
        </svrl:failed-assert>
    </xsl:otherwise>
</xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
    </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration"
            mode="M13" priority="1022">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration" />

		<!--ASSERT -->
<xsl:choose>
    <xsl:when
            test="not(../ram:PostalTradeAddress/ram:CountryID = 'IT') or not(ram:ID[@schemeID='FC']) or contains( 'RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19 ', ram:ID)"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not(../ram:PostalTradeAddress/ram:CountryID = 'IT') or not(ram:ID[@schemeID='FC']) or contains( 'RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19 ', ram:ID)">
          <xsl:attribute name="id">CIUS-VD-99</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-99] BT-32 (Seller tax registration identifier) -In case the seller is Italian this field must contain the codification of RegimeFiscale 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when test="not (ram:ID[@schemeID = 'VA']) or (string-length(ram:ID) &lt;= 30 and ram:ID[@schemeID='VA'])"/>
      <xsl:otherwise>
          <svrl:failed-assert
                  test="not (ram:ID[@schemeID = 'VA']) or (string-length(ram:ID) &lt;= 30 and ram:ID[@schemeID='VA'])">
          <xsl:attribute name="id">CIUS-VD-41</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-41] BT-31 (Seller VAT identifier) -BT maximum length shall be 30 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject"
            mode="M13" priority="1021">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ID) &lt;= 15 and ram:Name = 'Project reference' " />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ID) &lt;= 15 and ram:Name = 'Project reference'">
          <xsl:attribute name="id">CIUS-VD-33</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-33] BT-11 (Project reference) -BT maximum length shall be 15 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:DespatchAdviceReferencedDocument"
            mode="M13" priority="1020">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:DespatchAdviceReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IssuerAssignedID) &lt;= 31" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IssuerAssignedID) &lt;= 31">
          <xsl:attribute name="id">CIUS-VD-16</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-16] BT-16 (Despatch advice reference) -BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD).
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ReceivingAdviceReferencedDocument"
            mode="M13" priority="1019">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ReceivingAdviceReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IssuerAssignedID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IssuerAssignedID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-36</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-36] BT-15 (Receiving advice reference) -BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress"
            mode="M13" priority="1018">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:CityName) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:CityName) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-25</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-25] BT-77 (Deliver to city) -BT maximum length shall be 60 characters.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:CountryID = 'IT') or string-length(ram:CountrySubDivisionName) = 2" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:CountryID = 'IT') or string-length(ram:CountrySubDivisionName) = 2">
          <xsl:attribute name="id">CIUS-VD-31</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-31] BT-79 (Deliver to country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:CountryID = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:CountryID = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName)">
          <xsl:attribute name="id">CIUS-VD-49</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-49] BT-79 (Deliver to country subdivision) -If country code=IT it should be coded according to Italian province list.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-22</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-22] BT-75, BT-76, BT-165 (Deliver to address line 1 - Deliver to address line 2 - Deliver to address line 3) -The sum of BTs maximum length shall be 60 chars (including separator).
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:PostcodeCode) &lt;= 15" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:PostcodeCode) &lt;= 15">
          <xsl:attribute name="id">CIUS-VD-28-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-28-1-1] BT-78 (Deliver to post code) -BT maximum length shall be 15 chars if country-code not =IT and 5 chars if country-code=IT.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(ram:CountryID = 'IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) > 0 )" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(ram:CountryID = 'IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) > 0 )">
          <xsl:attribute name="id">CIUS-VD-28-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-28-1-2] BT-78 (Deliver to post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax"
            mode="M13" priority="1017">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:BasisAmount) &lt;= 15 and string-length(ram:BasisAmount) >= 4 and matches(ram:BasisAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:BasisAmount) &lt;= 15 and string-length(ram:BasisAmount) >= 4 and matches(ram:BasisAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')">
          <xsl:attribute name="id">CIUS-VD-66</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-66] BT-116 (VAT category taxable amount) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:CalculatedAmount) &lt;= 15 and string-length(ram:CalculatedAmount) >= 4 and matches(ram:CalculatedAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:CalculatedAmount) &lt;= 15 and string-length(ram:CalculatedAmount) >= 4 and matches(ram:CalculatedAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')">
          <xsl:attribute name="id">CIUS-VD-67</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-67] BT-117 (VAT category tax amount) - BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ExemptionReason) &lt;= 100" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ExemptionReason) &lt;= 100">
          <xsl:attribute name="id">CIUS-VD-68</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-68] BT-120 (VAT exemption reason text) -BT maximum length shall be 100 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceReferencedDocument"
            mode="M13" priority="1016">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IssuerAssignedID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IssuerAssignedID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-40</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-40] BT-25 (Preceding Invoice number)-BT maximum length shall be 20 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:PayeeTradeParty"
            mode="M13" priority="1015">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:PayeeTradeParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Name) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Name) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-50</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-50] BT-59 (Payee name) -BT maximum length shall be 200 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount"
            mode="M13" priority="1014">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-38</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-38] BT-19 (Buyer accounting reference) -BT maximum length shall be 20 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge"
            mode="M13" priority="1013">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ActualAmount) &lt;= 21 and string-length(ram:ActualAmount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ActualAmount) &lt;= 21 and string-length(ram:ActualAmount) >= 4">
          <xsl:attribute name="id">CIUS-VD-64</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-64] BT-92, BT-99 (Document level allowance amount - Document level charge amount) -BT minimum length shall be 4 maximum length shall be 21 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(ram:Reason) + string-length(ram:ReasonCode)) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(ram:Reason) + string-length(ram:ReasonCode)) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-60</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-60] BT-97, BT-98 (Document level allowance reason - Document level allowance reason code)-BTs maximum length shall be 1000 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(string-length(ram:Reason) + string-length(ram:ReasonCode)) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="(string-length(ram:Reason) + string-length(ram:ReasonCode)) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-61</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-61] BT-104, BT-105 (Document level charge reason - Document level charge reason code)-BTs maximum length shall be 1000 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation"
            mode="M13" priority="1012">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:DuePayableAmount) &lt;= 15 and string-length(ram:DuePayableAmount) >= 4 and matches(ram:DuePayableAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:DuePayableAmount) &lt;= 15 and string-length(ram:DuePayableAmount) >= 4 and matches(ram:DuePayableAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')">
          <xsl:attribute name="id">CIUS-VD-63</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-63] BT-115 (Amount due for payment) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:GrandTotalAmount) &lt;= 15 and string-length(ram:GrandTotalAmount) >= 4 and matches(ram:GrandTotalAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:GrandTotalAmount) &lt;= 15 and string-length(ram:GrandTotalAmount) >= 4 and matches(ram:GrandTotalAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')">
          <xsl:attribute name="id">CIUS-VD-62</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-62] BT-112 (Invoice total amount with VAT) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(ram:RoundingAmount)) or (string-length(ram:RoundingAmount) &lt;= 15 and string-length(ram:RoundingAmount) >= 4 and matches(ram:RoundingAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2,8}$'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(ram:RoundingAmount)) or (string-length(ram:RoundingAmount) &lt;= 15 and string-length(ram:RoundingAmount) >= 4 and matches(ram:RoundingAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2,8}$'))">
          <xsl:attribute name="id">CIUS-VD-65</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-65] BT-114 (Rounding amount) -BT minimum length shall be 4 maximum length shall be 15 chars including from 2 to 8 fraction digit.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans"
            mode="M13" priority="1011">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Information) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Information) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-55</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-55] BT-82 (Payment means text) -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount"
            mode="M13" priority="1010">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:AccountName) &lt;= 200" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:AccountName) &lt;= 200">
          <xsl:attribute name="id">CIUS-VD-58</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-58] BT-85 (Payment account name) -BT maximum length shall be 200 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayerSpecifiedDebtorFinancialInstitution"
            mode="M13" priority="1009">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayerSpecifiedDebtorFinancialInstitution" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:BICID) &lt;= 11 and string-length(ram:BICID) >= 8 and matches(ram:BICID,'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:BICID) &lt;= 11 and string-length(ram:BICID) >= 8 and matches(ram:BICID,'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')">
          <xsl:attribute name="id">CIUS-VD-59</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-59] BT-86 (Payment service provider identifier) - BT should contain a SWIFT/BIC (bank identifier code) according to structure defined in ISO 9362 (minimum length shall be 8- maximum length shall be 11 chars). 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:AssociatedDocumentLineDocument"
            mode="M13" priority="1008">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:AssociatedDocumentLineDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IncludedNote/ram:Content) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IncludedNote/ram:Content) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-75</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-75] BT-127 (Invoice line note) -BT maximum length shall be 60 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="number(ram:LineID) > 0 and number(ram:LineID) &lt;=9999" />
      <xsl:otherwise>
        <svrl:failed-assert test="number(ram:LineID) > 0 and number(ram:LineID) &lt;=9999">
          <xsl:attribute name="id">CIUS-SD-73</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-SD-73] BT-126 (Invoice line identifier) -The BT value should be numeric.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:LineID) &lt;= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:LineID) &lt;= 4">
          <xsl:attribute name="id">CIUS-VD-74</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-74] BT-126 (Invoice line identifier) -BT maximum length shall be 4 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement/ram:BuyerOrderReferencedDocument"
            mode="M13" priority="1007">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement/ram:BuyerOrderReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:LineID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:LineID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-96</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-96] BT-132 (Referenced purchase order line reference) -BT maximum length shall be 20 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement"
            mode="M13" priority="1006">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:GrossPriceProductTradePrice/ram:BasisQuantity) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:GrossPriceProductTradePrice/ram:BasisQuantity) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-78-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-78-2] BT-149 (Item price base quantity) -BT maximum length shall be 10 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:GrossPriceProductTradePrice/ram:BasisQuantity/@unitCode) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:GrossPriceProductTradePrice/ram:BasisQuantity/@unitCode) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-78-3</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-78-3] BT-150 (Item price base quantity unit of measure code) -BT maximum length shall be
                10 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:NetPriceProductTradePrice/ram:ChargeAmount) &lt;= 21 and string-length(ram:NetPriceProductTradePrice/ram:ChargeAmount) >= 4" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:NetPriceProductTradePrice/ram:ChargeAmount) &lt;= 21 and string-length(ram:NetPriceProductTradePrice/ram:ChargeAmount) >= 4">
          <xsl:attribute name="id">CIUS-VD-83</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-83] BT-146 (Item net price) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(ram:NetPriceProductTradePrice/ram:ChargeAmount, '^[0-9]+(\.[0-9]{0,8})*$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(ram:NetPriceProductTradePrice/ram:ChargeAmount, '^[0-9]+(\.[0-9]{0,8})*$')">
          <xsl:attribute name="id">CIUS-VD-95</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-95] BT-146 (Item net price) -BT allowed fraction digits shall be 8. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:BilledQuantity/@unitCode) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:BilledQuantity/@unitCode) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-78-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-78-1] BT-130 (Invoiced quantity unit of measure) -BT maximum length shall be 10 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument"
            mode="M13" priority="1005">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:IssuerAssignedID) &lt;= 35 and ram:TypeCode='130'" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:IssuerAssignedID) &lt;= 35 and ram:TypeCode='130'">
          <xsl:attribute name="id">CIUS-VD-77</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-77] BT-128 (Invoice line object identifier) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ReferenceTypeCode) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ReferenceTypeCode) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-76</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-76] BT-128-1 (Invoice line object identifier identification scheme identifier) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount"
            mode="M13" priority="1004">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ID) &lt;= 20" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ID) &lt;= 20">
          <xsl:attribute name="id">CIUS-VD-79</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-79] BT-133 (Invoice line Buyer accounting reference)-BT maximum length shall be 20 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge"
            mode="M13" priority="1003">
    <svrl:fired-rule context="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ActualAmount) >= 4 and string-length(ram:ActualAmount) &lt;= 21" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ActualAmount) >= 4 and string-length(ram:ActualAmount) &lt;= 21">
          <xsl:attribute name="id">CIUS-VD-80</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-80] BT-136, BT-141 (Invoice line allowance amount - Invoice line charge amount)-BT minimum length shall be 4, maximum length shall be 21 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Reason) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Reason) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-81-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-81-1] BT-139 (Invoice line allowance reason)-BT maximum length shall be 1000 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Reason) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Reason) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-82-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-82-1] BT-144 (Invoice line charge reason)-BT maximum length shall be 1000 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ReasonCode) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ReasonCode) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-82-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-82-2] BT-145 (Invoice line charge reason code)-BT maximum length shall be 1000 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ReasonCode) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ReasonCode) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-81-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-82-2-2] BT-140 (Invoice line allowance reason code)-BT maximum length shall be 1000 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:ApplicableProductCharacteristic"
            mode="M13" priority="1002">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:ApplicableProductCharacteristic" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Description) &lt;= 10" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Description) &lt;= 10">
          <xsl:attribute name="id">CIUS-VD-93</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-93] BT-160 (Item attribute name) -BT maximum length shall be 10 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Value) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Value) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-94</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-94] BT-161 (Item attribute value) -BT maximum length shall be 60 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct"
            mode="M13" priority="1001">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:BuyerAssignedID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:BuyerAssignedID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-87</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-87] BT-156 (Item Buyer's identifier) -BT maximum length shall be 35 chars. 
        </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Description) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Description) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-85-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
            <svrl:text>[CIUS-VD-85-2] BT-154 (Item description) -BT maximum length shall be 1000 chars.
            </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:GlobalID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:GlobalID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-88</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-88] BT-157 (Item standard identifier) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:GlobalID/@schemeID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:GlobalID/@schemeID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-90</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-90] BT-157-1 (Item standard identifier identification scheme identifier) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:Name) &lt;= 1000" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:Name) &lt;= 1000">
          <xsl:attribute name="id">CIUS-VD-85-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-85-1-1] BT-153 (Item name) -BT maximum length shall be 1000 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:OriginTradeCountry/ram:ID) &lt;= 60" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:OriginTradeCountry/ram:ID) &lt;= 60">
          <xsl:attribute name="id">CIUS-VD-92</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-92] BT-159 (Item country of origin) -BT maximum length shall be 60 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:SellerAssignedID) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:SellerAssignedID) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-86</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-86] BT-155 (Item Seller's identifier) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>

	<!--RULE -->
    <xsl:template
            match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification"
            mode="M13" priority="1000">
    <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(ram:ClassCode) &lt;= 35" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(ram:ClassCode) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-89</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-89] BT-158 (Item classification identifier) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when test="string-length(ram:ClassCode[@listID]) &lt;= 35"/>
      <xsl:otherwise>
          <svrl:failed-assert test="string-length(ram:ClassCode[@listID]) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-91-1</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-91-1-1] BT-158-1 (Item classification identifier identification scheme identifier) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
    <xsl:when test="string-length(ram:ClassCode[@listVersionID]) &lt;= 35"/>
      <xsl:otherwise>
          <svrl:failed-assert test="string-length(ram:ClassCode[@listVersionID]) &lt;= 35">
          <xsl:attribute name="id">CIUS-VD-91-2</xsl:attribute>
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [CIUS-VD-91-1-2] BT-158-2 (Scheme version identifer) -BT maximum length shall be 35 chars.
          </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>
    <xsl:template match="text()" mode="M13" priority="-1"/>
    <xsl:template match="@*|node()" mode="M13" priority="-2">
        <xsl:apply-templates mode="M13" select="*|comment()|processing-instruction()"/>
  </xsl:template>
</xsl:stylesheet>
