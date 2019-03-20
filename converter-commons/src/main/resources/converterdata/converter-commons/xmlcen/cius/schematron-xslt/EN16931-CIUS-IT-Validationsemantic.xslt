<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
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
    <svrl:schematron-output schemaVersion="iso" title="">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M3" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<xsl:param name="supplierCountry" select="if (//BG-4/BG-5/BT-40) then upper-case(normalize-space(//BG-4/BG-5/BT-40)) else 'XX'" />
  <xsl:param name="customerCountry" select="if (//BG-4/BG-5/BT-55) then upper-case(normalize-space(//BG-4/BG-5/BT-55)) else 'XX'" />
  <xsl:param name="deliveryCountry" select="if (//BG-4/BG-5/BT-80) then upper-case(normalize-space(//BG-4/BG-5/BT-80)) else 'XX'" />

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="SEMANTIC-INVOICE" mode="M3" priority="1046">
    <svrl:fired-rule context="SEMANTIC-INVOICE" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="BG-16" />
      <xsl:otherwise>
        <svrl:failed-assert test="BG-16">
          <xsl:attribute name="id">BR-IT-260</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-260] BG-16 Payment instructions - BT-81 (Payment means type code) -  BG-16 shall be mandatory
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-1" mode="M3" priority="1045">
    <svrl:fired-rule context="BT-1" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(.)) &lt;= 20 and matches(normalize-space(.),'(^\p{IsBasicLatin}*[0-9]+\p{IsBasicLatin}*$)')" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(.)) &lt;= 20 and matches(normalize-space(.),'(^\p{IsBasicLatin}*[0-9]+\p{IsBasicLatin}*$)')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-11" mode="M3" priority="1044">
    <svrl:fired-rule context="BT-11" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" matches(normalize-space(.),'^\p{IsBasicLatin}{0,15}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,15}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-12" mode="M3" priority="1043">
    <svrl:fired-rule context="BT-12" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-13" mode="M3" priority="1042">
    <svrl:fired-rule context="BT-13" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-15" mode="M3" priority="1041">
    <svrl:fired-rule context="BT-15" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-16" mode="M3" priority="1040">
    <svrl:fired-rule context="BT-16" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-17" mode="M3" priority="1039">
    <svrl:fired-rule context="BT-17" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" matches(normalize-space(.),'^\p{IsBasicLatin}{1,15}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{1,15}$')">
          <xsl:attribute name="id">BR-IT-070</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-070] BT-17 (Tender or lot reference) - BT maximum length shall be 15 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-19" mode="M3" priority="1038">
    <svrl:fired-rule context="BT-19" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-25" mode="M3" priority="1037">
    <svrl:fired-rule context="BT-25" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{1,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{1,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BG-4" mode="M3" priority="1036">
    <svrl:fired-rule context="BG-4" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="every $BT-29 in (BT-29[starts-with(normalize-space(.), 'IT:EORI:')])         satisfies (($supplierCountry != 'IT') or ((string-length(normalize-space($BT-29)) >= 21) and (string-length(normalize-space($BT-29)) &lt;= 25)))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $BT-29 in (BT-29[starts-with(normalize-space(.), 'IT:EORI:')]) satisfies (($supplierCountry != 'IT') or ((string-length(normalize-space($BT-29)) >= 21) and (string-length(normalize-space($BT-29)) &lt;= 25)))">
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
      <xsl:when test="every $BT-29 in (BT-29[starts-with(normalize-space(.), 'IT:ALBO:')])         satisfies (($supplierCountry  != 'IT') or (matches(normalize-space($BT-29), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))" />
      <xsl:otherwise>
        <svrl:failed-assert test="every $BT-29 in (BT-29[starts-with(normalize-space(.), 'IT:ALBO:')]) satisfies (($supplierCountry != 'IT') or (matches(normalize-space($BT-29), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-30" mode="M3" priority="1035">
    <svrl:fired-rule context="BT-30" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($supplierCountry != 'IT' or matches(normalize-space(.), '(^IT:REA:[A-Z]{2}:[\p{IsBasicLatin}]{1,20}$)'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="($supplierCountry != 'IT' or matches(normalize-space(.), '(^IT:REA:[A-Z]{2}:[\p{IsBasicLatin}]{1,20}$)'))">
          <xsl:attribute name="id">BR-IT-110</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-110] BT-30 (Seller legal registration identifier) - BT-30 minimum lenght 10 and maximum lenght shall be 30 starting with "IT:REA:" and  shall be represented as "IT:REA:Ufficio:NumeroREA".
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-31" mode="M3" priority="1034">
    <svrl:fired-rule context="BT-31" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$supplierCountry  != 'IT' or string-length(normalize-space(.)) &lt;= 30 " />
      <xsl:otherwise>
        <svrl:failed-assert test="$supplierCountry != 'IT' or string-length(normalize-space(.)) &lt;= 30">
          <xsl:attribute name="id">BR-IT-120</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-1x0] BT-31 (Seller VAT identifier) - BT maximum length shall be 30 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-32" mode="M3" priority="1033">
    <svrl:fired-rule context="BT-32" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($supplierCountry  != 'IT') or matches(normalize-space(.), '^[A-Z0-9]{11,16}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="($supplierCountry != 'IT') or matches(normalize-space(.), '^[A-Z0-9]{11,16}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BG-5" mode="M3" priority="1032">
    <svrl:fired-rule context="BG-5" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test=" not ($supplierCountry = 'IT') or BT-35" />
      <xsl:otherwise>
        <svrl:failed-assert test="not ($supplierCountry = 'IT') or BT-35">
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
      <xsl:when test="not ($supplierCountry ='IT') or BT-37" />
      <xsl:otherwise>
        <svrl:failed-assert test="not ($supplierCountry ='IT') or BT-37">
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
      <xsl:when test="not ($supplierCountry ='IT') or matches(normalize-space(BT-38), '^[0-9]{5}$') " />
      <xsl:otherwise>
        <svrl:failed-assert test="not ($supplierCountry ='IT') or matches(normalize-space(BT-38), '^[0-9]{5}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-39" mode="M3" priority="1031">
    <svrl:fired-rule context="BT-39" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$supplierCountry !='IT' or not(exists(.)) or matches(normalize-space(.),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="$supplierCountry !='IT' or not(exists(.)) or matches(normalize-space(.),'^[A-Z]{2}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BG-7" mode="M3" priority="1030">
    <svrl:fired-rule context="BG-7" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="BT-48 or BT-46[starts-with(normalize-space(.),'IT:CF:')]" />
      <xsl:otherwise>
        <svrl:failed-assert test="BT-48 or BT-46[starts-with(normalize-space(.),'IT:CF:')]">
          <xsl:attribute name="id">BR-IT-160-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-160] BT-48 BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) - If BT-48 is empty then BT-46 shall be the FiscalCode. BT-46, if existing, shall starts with "IT:CF".
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or BT-48 or BT-46[starts-with(normalize-space(.),'IT:CF:')]" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or BT-48 or BT-46[starts-with(normalize-space(.),'IT:CF:')]">
          <xsl:attribute name="id">BR-IT-160-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-160] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght shall be 22 starting with "IT:CF: ".
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-47[starts-with(normalize-space(.),'IT:EORI:')]" mode="M3" priority="1029">
    <svrl:fired-rule context="BT-47[starts-with(normalize-space(.),'IT:EORI:')]" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($customerCountry!='IT') or (string-length(normalize-space(BT-47)) >= 21 and string-length(normalize-space(BT-47)) &lt;=25)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($customerCountry!='IT') or (string-length(normalize-space(BT-47)) >= 21 and string-length(normalize-space(BT-47)) &lt;=25)">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-48" mode="M3" priority="1028">
    <svrl:fired-rule context="BT-48" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(.)) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(.)) &lt;= 30">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-49" mode="M3" priority="1027">
    <svrl:fired-rule context="BT-49" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or (         (.[normalize-space(@scheme) = 'IT:CODDEST']         or .[normalize-space(@scheme) = 'IT:PEC']         or .[normalize-space(@scheme) = '9921'] ))" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or ( (.[normalize-space(@scheme) = 'IT:CODDEST'] or .[normalize-space(@scheme) = 'IT:PEC'] or .[normalize-space(@scheme) = '9921'] ))">
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
      <xsl:when test="$customerCountry!='IT' or  not(.[normalize-space(@scheme) = 'IT:PEC'])         or ( (string-length(normalize-space(.)) >= 7 and string-length(normalize-space(.)) &lt;= 256) and matches(normalize-space(.),'^.+@.+[.]+.+$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(.[normalize-space(@scheme) = 'IT:PEC']) or ( (string-length(normalize-space(.)) >= 7 and string-length(normalize-space(.)) &lt;= 256) and matches(normalize-space(.),'^.+@.+[.]+.+$') )">
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
      <xsl:when test="$customerCountry!='IT' or  not(.[normalize-space(@scheme) = '9921'])         or ( matches(normalize-space(.),'^[A-Z0-9]{6}$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(.[normalize-space(@scheme) = '9921']) or ( matches(normalize-space(.),'^[A-Z0-9]{6}$') )">
          <xsl:attribute name="id">BR-IT-200-2</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-200-2] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) =IT:IPA schema 9921 then BT-49 shall be a IPA code and maximum length shall be 6 chars
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="$customerCountry!='IT' or  not(.[normalize-space(@scheme) = 'IT:CODDEST'])         or ( matches(normalize-space(.),'^[A-Z0-9]{7}$') )" />
      <xsl:otherwise>
        <svrl:failed-assert test="$customerCountry!='IT' or not(.[normalize-space(@scheme) = 'IT:CODDEST']) or ( matches(normalize-space(.),'^[A-Z0-9]{7}$') )">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BG-8" mode="M3" priority="1026">
    <svrl:fired-rule context="BG-8" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($customerCountry = 'IT') or BT-50" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or BT-50">
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
      <xsl:when test="not($customerCountry = 'IT') or BT-52" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or BT-52">
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
      <xsl:when test="not($customerCountry = 'IT') or matches(normalize-space(BT-53), '^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or matches(normalize-space(BT-53), '^[0-9]{5}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-54" mode="M3" priority="1025">
    <svrl:fired-rule context="BT-54" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($customerCountry = 'IT') or not(exists(.)) or matches(normalize-space(.),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($customerCountry = 'IT') or not(exists(.)) or matches(normalize-space(.),'^[A-Z]{2}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-63" mode="M3" priority="1024">
    <svrl:fired-rule context="BT-63" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="string-length(normalize-space(.)) &lt;= 30" />
      <xsl:otherwise>
        <svrl:failed-assert test="string-length(normalize-space(.)) &lt;= 30">
          <xsl:attribute name="id">BR-IT-230</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-230] BT-63 (Seller Tax Representative VAT identifier) - BT maximum length shall be 30 chars.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BG-15" mode="M3" priority="1023">
    <svrl:fired-rule context="BG-15" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not ($deliveryCountry='IT') or BT-75" />
      <xsl:otherwise>
        <svrl:failed-assert test="not ($deliveryCountry='IT') or BT-75">
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
      <xsl:when test="not ($deliveryCountry='IT') or BT-77" />
      <xsl:otherwise>
        <svrl:failed-assert test="not ($deliveryCountry='IT') or BT-77">
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
      <xsl:when test="not ($deliveryCountry='IT') or matches(normalize-space(BT-78), '^[0-9]{5}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not ($deliveryCountry='IT') or matches(normalize-space(BT-78), '^[0-9]{5}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-79" mode="M3" priority="1022">
    <svrl:fired-rule context="BT-79" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not($deliveryCountry = 'IT') or not(exists(.)) or matches(normalize-space(.),'^[A-Z]{2}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="not($deliveryCountry = 'IT') or not(exists(.)) or matches(normalize-space(.),'^[A-Z]{2}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-84" mode="M3" priority="1021">
    <svrl:fired-rule context="BT-84" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.), '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.), '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-86" mode="M3" priority="1020">
    <svrl:fired-rule context="BT-86" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-92" mode="M3" priority="1019">
    <svrl:fired-rule context="BT-92" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')">
          <xsl:attribute name="id">BR-IT-290</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>  [BR-IT-290] BT-92  (Document level allowance amount ) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-99" mode="M3" priority="1018">
    <svrl:fired-rule context="BT-99" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')">
          <xsl:attribute name="id">BR-IT-290-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>  [BR-IT-290-1]  BT-99 (Document level allowance amount - Document level charge amount) - BT maximum lenght shall be 21.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-112" mode="M3" priority="1017">
    <svrl:fired-rule context="BT-112" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-114" mode="M3" priority="1016">
    <svrl:fired-rule context="BT-114" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(exists(.)) or (matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$'))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(exists(.)) or (matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$'))">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-115" mode="M3" priority="1015">
    <svrl:fired-rule context="BT-115" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-116" mode="M3" priority="1014">
    <svrl:fired-rule context="BT-116" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-117" mode="M3" priority="1013">
    <svrl:fired-rule context="BT-117" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-95 | BT-102 | BT-118 | BT-151" mode="M3" priority="1012">
    <svrl:fired-rule context="BT-95 | BT-102 | BT-118 | BT-151" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE E S B G K ',concat(' ',normalize-space(.),' ') ) ) )" />
      <xsl:otherwise>
        <svrl:failed-assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE E S B G K ',concat(' ',normalize-space(.),' ') ) ) )">
          <xsl:attribute name="id">BR-IT-350-1</xsl:attribute>
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text> [BR-IT-350] VAT category codes - only values E (Exempt from VAT), K	(Intra-community supply), AE (Reverse charge), G (Export outside the EU) ,S (Standard rated), B (Split payment) shall be allowed .
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BG-24" mode="M3" priority="1011">
    <svrl:fired-rule context="BG-24" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="BT-124 or BT-125" />
      <xsl:otherwise>
        <svrl:failed-assert test="BT-124 or BT-125">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-128" mode="M3" priority="1010">
    <svrl:fired-rule context="BT-128" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.), '^\p{IsBasicLatin}{1,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.), '^\p{IsBasicLatin}{1,35}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-129" mode="M3" priority="1009">
    <svrl:fired-rule context="BT-129" />

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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-131" mode="M3" priority="1008">
    <svrl:fired-rule context="BT-131" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(.,'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(.,'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-132" mode="M3" priority="1007">
    <svrl:fired-rule context="BT-132" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-133" mode="M3" priority="1006">
    <svrl:fired-rule context="BT-133" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,20}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-136" mode="M3" priority="1005">
    <svrl:fired-rule context="BT-136" />

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
          <svrl:text>  [BR-IT-420] BT-136(Invoice line allowance amount - Invoice line charge amount) - BT maximum length shall be 15, including two fraction digits.
      </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-146" mode="M3" priority="1004">
    <svrl:fired-rule context="BT-146" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.), '^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.), '^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-155" mode="M3" priority="1003">
    <svrl:fired-rule context="BT-155" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-156" mode="M3" priority="1002">
    <svrl:fired-rule context="BT-156" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-157" mode="M3" priority="1001">
    <svrl:fired-rule context="BT-157" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="BT-158" mode="M3" priority="1000">
    <svrl:fired-rule context="BT-158" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')" />
      <xsl:otherwise>
        <svrl:failed-assert test="matches(normalize-space(.),'^\p{IsBasicLatin}{0,35}$')">
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
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>
  <xsl:template match="text()" mode="M3" priority="-1" />
  <xsl:template match="@*|node()" mode="M3" priority="-2">
    <xsl:apply-templates mode="M3" select="*" />
  </xsl:template>
</xsl:stylesheet>
