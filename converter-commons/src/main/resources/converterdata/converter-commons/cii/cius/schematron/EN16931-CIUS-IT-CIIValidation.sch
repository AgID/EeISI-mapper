<?xml version="1.0" encoding="UTF-8"?>
<!-- 
            CII syntax binding to the CIUS ITALIA  
            Created by eIGOR Project
            Timestamp: 2018-02-01 12:00:00 +0100
            Release: 0.1.2A
-->
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
  xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
  xmlns:ccts="urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
  xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"
  xmlns:qdt="urn:un:unece:uncefact:data:standard:QualifiedDataType:100"
  xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
  queryBinding="xslt2">

 <title>EN16931 model bound to CII</title>
  <ns prefix="rsm" uri="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"/>
  <ns prefix="ccts" uri="urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"/>
  <ns prefix="udt" uri="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"/>
  <ns prefix="qdt" uri="urn:un:unece:uncefact:data:standard:QualifiedDataType:100"/>
  <ns prefix="ram" uri="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"/>


  <phase id="ItalianBusinessRules">
      <active pattern="CIUS-IT"/>
  </phase>
  
  <phase id="ItalianUsageRules">
      <active pattern="CIUS-USAGE-IT"/>
  </phase>
 
  <phase id="ItalianSyntaxRestrictions">
      <active pattern="CIUS-SYNTAX-IT"/>
  </phase>
 
<!-- 
            CIUS ITALIA - Business Rules
			- FATAL ERROR
     -->
 <pattern xmlns="http://purl.oclc.org/dsdl/schematron"  id="CIUS-IT">
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount">
      <assert test=  "matches(ram:IBANID, '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')" id="CIUS-BT-84" flag="fatal"> [CIUS-BT-84] BT-84 (Payment account identifier)  shall be an IBAN code and respect the Regular Expression [a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30}) . 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication">
      <assert test=  "exists(ram:URIID) and (ram:URIID/@schemeID = 'IT:CODDEST' or ram:URIID/@schemeID = 'IT:PEC' or ram:URIID/@schemeID = 'IT:IPA' )" id="CIUS-CA-2" flag="fatal"> [CIUS-CA-2] BT-49 BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario (see the Italian business rules). BT-49-1=IT:PEC or IT:IPA or IT:CODDEST 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans">
      <assert test=  "exists(ram:TypeCode)" id="CIUS-CA-103" flag="fatal"> [CIUS-CA-103] BT-81 (Payment means type code) -Fields are mandatory in XMLPA. Mapped BTs should be mandatory 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty">
      <assert test=  "exists(ram:SpecifiedTaxRegistration/ram:ID and ram:SpecifiedTaxRegistration/ram:ID/@schemeID='VA') or exists(ram:GlobalID/@schemeID = 'IT:CF') or exists(ram:GlobalID/@schemeID = 'IT:VAT')" id="CIUS-BR-14" flag="fatal"> [CIUS-BR-14] BT-48 BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) -1.4.1.1 is not mandatory in Italy (buyer) but VAT number or Fiscal code should be indicated. 
        </assert>
   </rule>
 </pattern>

 <!-- 

            CIUS-USAGE-IT  - Usage Rules

     -->

  <pattern xmlns="http://purl.oclc.org/dsdl/schematron"  id="CIUS-USAGE-IT">
    <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration">
      <assert test=  "(ram:ID/@schemeID='VA') or ((../ram:PostalTradeAddress/ram:CountryID = 'IT') and (exists(ram:ID) and ram:ID/@schemeID='FC'))
        " id="CIUS-BT-98-1" flag="fatal"> [CIUS-BT-98-1] BT-32 (Seller tax registration identifier)  is a conditional field and shall not be used by a foreign seller as it is not possible to map into XMLPA. 
      </assert>
    </rule>
    <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty">
      <assert test=  "not(ram:PostalTradeAddress/ram:CountryID = 'IT') or count(ram:SpecifiedTaxRegistration[(ram:ID/@SchemeID='FC')]) &gt;=1
        " id="CIUS-BT-98-2" flag="fatal"> [CIUS-BT-98-2] BT-32 (Seller tax registration identifier). In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8) 
      </assert>
    </rule>
    <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement">
      <assert test=  " (exists(ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID) and ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID/@schemeID ='VA') or (exists(ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration/ram:ID) and  ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration/ram:ID/@schemeID='VA')" id="CIUS-CA-9" flag="fatal"> [CIUS-CA-9] BT-31
        BT-63 (Seller VAT identifier - Seller tax representative VAT identifier) -Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax representative). 
      </assert>
    </rule>
    <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress">
      <assert test=  "exists(ram:LineOne)" id="CIUS-CA-10-1" flag="fatal"> [CIUS-CA-10-1] BT-35 (Seller address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "exists(ram:CityName)" id="CIUS-CA-10-2" flag="fatal"> [CIUS-CA-10-2] BT-37 (Seller city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "exists(ram:PostcodeCode)" id="CIUS-CA-10-3" flag="fatal"> [CIUS-CA-10-3] BT-38 (Seller post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
    </rule>
    <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress">
      <assert test=  "exists(ram:LineOne)" id="CIUS-CA-11-1" flag="fatal"> [CIUS-CA-11-1] BT-50 (Buyer address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "exists(ram:CityName)" id="CIUS-CA-11-2" flag="fatal"> [CIUS-CA-11-2] BT-52 (Buyer city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "exists(ram:PostcodeCode)" id="CIUS-CA-11-3" flag="fatal"> [CIUS-CA-11-3] BT-53 (Buyer post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
    </rule>
    <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress">
      <assert test=  "exists(ram:LineOne)" id="CIUS-CA-12-1" flag="fatal"> [CIUS-CA-12-1] BT-75 (Deliver to address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "exists(ram:CityName)" id="CIUS-CA-12-2" flag="fatal"> [CIUS-CA-12-2] BT-77 (Deliver to city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "exists(ram:PostcodeCode)" id="CIUS-CA-12-3" flag="fatal"> [CIUS-CA-12-3] BT-78 (Deliver to post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
    </rule>
    <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument">
      <assert test=  "exists(ram:URIID) or exists(ram:AttachmentBinaryObject)" id="CIUS-CA-71" flag="fatal"> [CIUS-CA-71] BT-125 (Attached document) -If BT-122 not empty then BT-124 or BT-125 should be mandatory as the mapped field is mandatory in Italy. 
      </assert>
    </rule>
  </pattern>
  
  
	 	 
	 <!-- 

            CIUS-SYNTAX-IT  - Syntax rules

     -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron"  id="CIUS-SYNTAX-IT">
   <rule context="//rsm:CrossIndustryInvoice/rsm:ExchangedDocument">
      <assert test=  "string-length(ram:ID) &lt;= 20" id="CIUS-VD-32" flag="warning"> [CIUS-VD-32] BT-1 (Invoice number) -BT maximum length shall be 20 digits. 
        </assert>
   </rule>
   <rule context="//rsm:CrossIndustryInvoice/rsm:ExchangedDocument/ram:IncludedNote">
      <assert test=  "string-length(concat(ram:SubjectCode,'-',ram:Content )) &lt;= 200
" id="CIUS-VD-39" flag="warning"> [CIUS-VD-39] BT-21, BT-22 (Invoice note subject code Invoice note) - The sum of BTs maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument">
      <assert test=  "string-length(ram:AttachmentBinaryObject/@mimeCode) &lt;= 10" id="CIUS-VD-72" flag="warning"> [CIUS-VD-72] BT-125-1 (Attached document Mime code) -BT maximum length shall be 10 chars. 
        </assert>
      <assert test=  "not(ram:TypeCode = '50') or (string-length(ram:IssuerAssignedID) &lt;= 15 and ram:TypeCode = '50')" id="CIUS-VD-37" flag="warning"> [CIUS-VD-37] BT-17 (Tender or lot reference) -BT maximum length shall be 15 chars. 
        </assert>
      <assert test=  "not(ram:TypeCode = '916') or ( ram:TypeCode = '916' and string-length(ram:IssuerAssignedID) + string-length(ram:AttachmentBinaryObject/@filename) &lt;= 60)" id="CIUS-VD-69" flag="warning"> [CIUS-VD-69] BT-122, BT-125-2 (Supporting document reference - Attached document Filename) - BT maximum length shall be 60 chars. 
        </assert>
      <assert test=  "string-length(ram:Name) &lt;= 100" id="CIUS-VD-70" flag="warning"> [CIUS-VD-70] BT-123 (Supporting document description) -BT maximum length shall be 100 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerOrderReferencedDocument">
      <assert test=  "string-length(ram:IssuerAssignedID) &lt;= 20" id="CIUS-VD-35" flag="warning"> [CIUS-VD-35] BT-13 (Purchase order reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:DefinedTradeContact">
      <assert test=  "string-length(ram:PersonName) &lt;= 200" id="CIUS-VD-51" flag="warning"> [CIUS-VD-51] BT-56 (Buyer contact point) -BT maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty">
      <assert test=  "not (ram:GlobalID/@schemeID = 'IT:CF')  or ( (string-length(ram:ID) &gt;= 11) and (string-length(ram:ID) &lt;=16)   and matches(ram:ID,'^[A-Z0-9]{11,16}$')  )" id="CIUS-VD-100-1" flag="warning"> [CIUS-VD-100-1] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -BT46-1=IT:CF then BT-46 minimum lenght 11 and maximum lenght shall be 16 
        </assert>
      <assert test=  "not (ram:GlobalID/@schemeID = 'IT:EORI')  or ( (string-length(ram:ID) &gt;= 13) and (string-length(ram:ID) &lt;=17))" id="CIUS-VD-100-2" flag="warning"> [CIUS-VD-100-2] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:EORI then BT-46 minimum lenght 13 and maximum lenght shall be 17 
        </assert>
      <assert test=  "not (ram:GlobalID/@schemeID = 'IT:VAT')  or ( (string-length(ram:ID) &lt;= 30) and (contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(ram:ID,1,2) ) ))" id="CIUS-VD-100-3" flag="warning"> [CIUS-VD-100-3] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:VAT then BT-46 maximum length 30 (the first two chars indicates country code). 
        </assert>
      <assert test=  "(exists(ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID/@schemeID='VA' ) or ( exists(ram:ID) and exists(ram:GlobalID/@schemeID
) )" id="CIUS-VD-53" flag="warning"> [CIUS-VD-53] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-48 is empty then one of the buyer identifiers (0..n) should be the FiscalCode in BT-46. BT-46-1 shall contain the scheme. 
        </assert>
      <assert test=  "not (exists (ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID/@schemeID='VA') or (string-length(ram:SpecifiedTaxRegistration/ram:ID) &lt;= 30 and ram:SpecifiedTaxRegistration/ram:ID/@schemeID='VA') 
" id="CIUS-VD-43" flag="warning"> [CIUS-VD-43] BT-48 (Buyer VAT identifier) -BT maximum length shall be 30 chars. 
        </assert>
      <assert test=  "string-length(ram:Name) &lt;= 80" id="CIUS-VD-18" flag="warning"> [CIUS-VD-18] BT-44 (Buyer name) -BT maximum length shall be 80 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress">
      <assert test=  "string-length(ram:CityName) &lt;= 60" id="CIUS-VD-24" flag="warning"> [CIUS-VD-24] BT-52 (Buyer city) -BT maximum length shall be 60 characters. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or not(exists(ram:CountrySubDivisionName)) or string-length(ram:CountrySubDivisionName) = 2" id="CIUS-VD-30" flag="warning"> [CIUS-VD-30] BT-54 (Buyer country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName )" id="CIUS-VD-48" flag="warning"> [CIUS-VD-48] BT-54 (Buyer country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </assert>
      <assert test=  "(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60" id="CIUS-VD-21" flag="warning"> [CIUS-VD-21] BT-50, BT-51, BT-163 (Buyer address line 1 - Buyer address line 2 - Buyer address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
        </assert>
      <assert test=  "string-length(ram:PostcodeCode) &lt;= 15" id="CIUS-VD-27-1" flag="warning"> [CIUS-VD-27-1-1] BT-53 (Buyer post code) -BT maximum length shall be 15 chars  if country-code not =IT and 5 chars if country-code=IT. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) &gt; 0 )" id="CIUS-VD-27-2" flag="warning"> [CIUS-VD-27-1-2] BT-53 (Buyer post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication">
      <assert test=  "not(ram:URIID/@schemeID = 'IT:PEC') or ( (string-length(ram:URIID) &gt;= 7 and string-length(ram:URIID) &lt;= 256) and matches(ram:URIID,'^.+@.+[.]+.+$') )
" id="CIUS-VD-97-1" flag="warning"> [CIUS-VD-97-1-1] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) -If BT-49-1=IT:PEC schema then BT-49 should be a PEC (email) address and  length shall be between 7 and 256 character 
        </assert>
      <assert test=  "not(ram:URIID/@schemeID = 'IT:IPA' ) or ( (string-length(ram:URIID) = 6) and matches(ram:URIID,'^[A-Z0-9]{6,7}$') )" id="CIUS-VD-97-2" flag="warning"> [CIUS-VD-97-1-2] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) =IT:IPA schema then BT-49 should be a IPA code and maximum length shall be 6 chars 
        </assert>
      <assert test=  "not(ram:URIID/@schemeID = 'IT:CODDEST') or ( string-length(ram:URIID) = 7  and matches(ram:URIID,'^[A-Z0-9]{6,7}$') )" id="CIUS-VD-97-3" flag="warning"> [CIUS-VD-97-1-3] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier)=IT:CODDEST schema then BT-49 and maximum length shall be 7 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:ContractReferencedDocument">
      <assert test=  "string-length(ram:IssuerAssignedID) &lt;= 20" id="CIUS-VD-34" flag="warning"> [CIUS-VD-34] BT-12 (Contract reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty">
      <assert test=  "string-length(ram:Name) &lt;= 80" id="CIUS-VD-19" flag="warning"> [CIUS-VD-19] BT-62 (Seller tax representative name) -BT maximum length shall be 80 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount">
      <assert test=  "string-length(ram:IBANID) &lt;= 34 and string-length(ram:IBANID) &gt;= 15" id="CIUS-VD-57" flag="warning"> [CIUS-VD-57] BT-84 (Payment account identifier) -BT minimum length shall be 15, maximum length shall be 34 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement">
      <assert test=  "string-length(ram:PaymentReference) &lt;= 60" id="CIUS-VD-56" flag="warning"> [CIUS-VD-56] BT-83 (Remittance information) -BT maximum length shall be 60 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty">
      <assert test=  "string-length(ram:ID) &lt;= 30 and ram:ID/@schemeID='VA' " id="CIUS-VD-42" flag="warning"> [CIUS-VD-42] BT-63 (Seller tax representative VAT identifier) -BT maximum length shall be 30 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:DefinedTradeContact">
      <assert test=  "not(exists(ram:EmailURIUniversalCommunication/ram:URIID)) or (string-length(ram:EmailURIUniversalCommunication/ram:URIID) &lt;= 256 and string-length(ram:EmailURIUniversalCommunication/ram:URIID) &gt;= 7)" id="CIUS-VD-46" flag="warning"> [CIUS-VD-46] BT-43 (Seller contact email address) -BT minimum length shall be 7 maximum length shall be 256 chars. 
        </assert>
      <assert test=  "string-length(ram:PersonName) &lt;= 200" id="CIUS-VD-44-1" flag="warning"> [CIUS-VD-44-1] BT-41 (Seller contact point)  -BT maximum length shall be 200 chars. 
        </assert>
      <assert test=  "string-length(ram:DepartmentName) &lt;= 200" id="CIUS-VD-44-2" flag="warning"> [CIUS-VD-44-2] BT-41 (Seller contact point)  -BT maximum length shall be 200 chars. 
        </assert>
      <assert test=  "not(exists(ram:TelephoneUniversalCommunication/ram:CompleteNumber)) or (string-length(ram:TelephoneUniversalCommunication/ram:CompleteNumber) &lt;= 12 and string-length(ram:TelephoneUniversalCommunication/ram:CompleteNumber) &gt;= 5)" id="CIUS-VD-45" flag="warning"> [CIUS-VD-45] BT-42 (Seller contact telephone number) -BT minimum length shall be 5 maximum length shall be 12 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty">
      <assert test=  "not (ram:GlobalID/@schemeID = 'IT:CF')  or ( (string-length(ram:ID) &gt;= 11) and (string-length(ram:ID) &lt;=16))" id="CIUS-VD-101-1" flag="warning"> [CIUS-VD-101-1] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -BT29-1=IT:CF then BT-29 minimum lenght 11 and maximum lenght shall be 16. 
        </assert>
      <assert test=  "not (ram:GlobalID/@schemeID = 'IT:EORI')  or ( (string-length(ram:ID) &gt;= 13) and (string-length(ram:ID) &lt;=17))" id="CIUS-VD-101-2" flag="warning"> [CIUS-VD-101-2] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -If BT-29-1=IT:EORI then BT-29 minimum lenght 13 and maximum lenght shall be 17 . 
        </assert>
      <assert test=  "not (ram:GlobalID/@schemeID = 'IT:VAT')  or ( (string-length(ram:ID) &lt;= 30) and ( contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(ram:ID,1,2) ) ))" id="CIUS-VD-101-3" flag="warning"> [CIUS-VD-101-3] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -If BT-29-1=IT:VAT then BT-29 maximum length 30 (the first two chars indicates country code). 
        </assert>
      <assert test=  "string-length(ram:Name) &lt;= 80" id="CIUS-VD-17" flag="warning"> [CIUS-VD-17] BT-27 (Seller name) -BT maximum length shall be 80 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress">
      <assert test=  "string-length(ram:CityName) &lt;= 60" id="CIUS-VD-23" flag="warning"> [CIUS-VD-23] BT-37 (Seller city) -BT maximum length shall be 60 characters. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or not(exists(ram:CountrySubDivisionName)) or string-length(ram:CountrySubDivisionName) = 2" id="CIUS-VD-29" flag="warning"> [CIUS-VD-29] BT-39 (Seller country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName )" id="CIUS-VD-47" flag="warning"> [CIUS-VD-47] BT-39 (Seller country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </assert>
      <assert test=  "(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60" id="CIUS-VD-20" flag="warning"> [CIUS-VD-20] BT-35, BT-36, BT-162 (Seller address line 1 - Seller address line 2 - Seller address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
        </assert>
      <assert test=  "string-length(ram:PostcodeCode) &lt;= 15" id="CIUS-VD-26-1" flag="warning"> [CIUS-VD-26-1-1] BT-38 (Seller post code) - BT maximum length shall be 15 chars if country-code not =IT and 5 chars if country-code=IT. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) &gt; 0 )" id="CIUS-VD-26-2" flag="warning"> [CIUS-VD-26-1-2] BT-38 (Seller post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization">
      <assert test=  "not (ram:ID/@schemeID = 'IT:REA')  or ( (string-length(ram:ID) &gt;= 3) and (string-length(ram:ID) &lt;=22) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(ram:ID,1,2) )))" id="CIUS-VD-102-1" flag="warning"> [CIUS-VD-102-1] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration identifier identification scheme identifier) -If BT-30-1=IT:REA then BT-30 minimum lenght 3 and maximum lenght shall be 22 (first two chars indicate the italian province code). 
        </assert>
      <assert test=  "not (ram:ID/@schemeID = 'IT:ALBO')  or (string-length(ram:ID) &lt;=30)" id="CIUS-VD-102-2" flag="warning"> [CIUS-VD-102-2] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration identifier identification scheme identifier) -If BT-30-1=IT:ALBO then BT-30 maximum length 60 . 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration">
      <assert test=  "not(../ram:PostalTradeAddress/ram:CountryID = 'IT') or not(ram:ID/@schemeID='FC') or contains( 'RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19 ', ram:ID)
" id="CIUS-VD-99" flag="warning"> [CIUS-VD-99] BT-32 (Seller tax registration identifier) -In case the seller is Italian this field must contain the codification of RegimeFiscale 
        </assert>
      <assert test=  "string-length(ram:ID) &lt;= 30 and ram:ID/@schemeID='VA' " id="CIUS-VD-41" flag="warning"> [CIUS-VD-41] BT-31 (Seller VAT identifier) -BT maximum length shall be 30 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject">
      <assert test=  "string-length(ram:ID) &lt;= 15 and ram:Name = 'Project reference' " id="CIUS-VD-33" flag="warning"> [CIUS-VD-33] BT-11 (Project reference) -BT maximum length shall be 15 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:DespatchAdviceReferencedDocument">
      <assert test=  "matches(ram:IssuerAssignedID, '(^[0-9]{1,20})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})$')" id="CIUS-VD-15" flag="warning"> [CIUS-VD-15] BT-16 (Despatch advice reference) -BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05) 
        </assert>
      <assert test=  "string-length(ram:IssuerAssignedID) &lt;= 31" id="CIUS-VD-16" flag="warning"> [CIUS-VD-16] BT-16 (Despatch advice reference) -BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD). 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ReceivingAdviceReferencedDocument">
      <assert test=  "string-length(ram:IssuerAssignedID) &lt;= 20" id="CIUS-VD-36" flag="warning"> [CIUS-VD-36] BT-15 (Receiving advice reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress">
      <assert test=  "string-length(ram:CityName) &lt;= 60" id="CIUS-VD-25" flag="warning"> [CIUS-VD-25] BT-77 (Deliver to city) -BT maximum length shall be 60 characters. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or string-length(ram:CountrySubDivisionName) = 2" id="CIUS-VD-31" flag="warning"> [CIUS-VD-31] BT-79 (Deliver to country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',ram:CountrySubDivisionName)" id="CIUS-VD-49" flag="warning"> [CIUS-VD-49] BT-79 (Deliver to country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </assert>
      <assert test=  "(string-length(ram:LineOne) + string-length(ram:LineTwo) + string-length(ram:LineThree)) &lt;= 60" id="CIUS-VD-22" flag="warning"> [CIUS-VD-22] BT-75, BT-76, BT-165 (Deliver to address line 1 - Deliver to address line 2 - Deliver to address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
        </assert>
      <assert test=  "string-length(ram:PostcodeCode) &lt;= 15" id="CIUS-VD-28-1" flag="warning"> [CIUS-VD-28-1-1] BT-78 (Deliver to post code) -BT maximum length shall be 15 chars if country-code not =IT and 5 chars if country-code=IT. 
        </assert>
      <assert test=  "not(ram:CountryID = 'IT') or ( string-length(ram:PostcodeCode) &lt;= 5 and number(ram:PostcodeCode) &gt; 0 )" id="CIUS-VD-28-2" flag="warning"> [CIUS-VD-28-1-2] BT-78 (Deliver to post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax">
      <assert test=  "string-length(ram:BasisAmount) &lt;= 15 and string-length(ram:BasisAmount) &gt;= 4 and matches(ram:BasisAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" id="CIUS-VD-66" flag="warning"> [CIUS-VD-66] BT-116 (VAT category taxable amount) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
        </assert>
      <assert test=  "string-length(ram:CalculatedAmount) &lt;= 15 and string-length(ram:CalculatedAmount) &gt;= 4 and matches(ram:CalculatedAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" id="CIUS-VD-67" flag="warning"> [CIUS-VD-67] BT-117 (VAT category tax amount) - BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
        </assert>
      <assert test=  "string-length(ram:ExemptionReason) &lt;= 100" id="CIUS-VD-68" flag="warning"> [CIUS-VD-68] BT-120 (VAT exemption reason text) -BT maximum length shall be 100 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceReferencedDocument">
      <assert test=  "string-length(ram:IssuerAssignedID) &lt;= 20" id="CIUS-VD-40" flag="warning"> [CIUS-VD-40] BT-25 (Preceding Invoice number)-BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:PayeeTradeParty">
      <assert test=  "string-length(ram:Name) &lt;= 200" id="CIUS-VD-50" flag="warning"> [CIUS-VD-50] BT-59 (Payee name) -BT maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount">
      <assert test=  "string-length(ram:ID) &lt;= 20" id="CIUS-VD-38" flag="warning"> [CIUS-VD-38] BT-19 (Buyer accounting reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge">
      <assert test=  "string-length(ram:ActualAmount) &lt;= 21 and string-length(ram:ActualAmount) &gt;= 4" id="CIUS-VD-64" flag="warning"> [CIUS-VD-64] BT-92, BT-99 (Document level allowance amount - Document level charge amount) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </assert>
      <assert test=  "(string-length(ram:Reason) + string-length(ram:ReasonCode)) &lt;= 1000" id="CIUS-VD-60" flag="warning"> [CIUS-VD-60] BT-97, BT-98 (Document level allowance reason - Document level allowance reason code)-BTs maximum length shall be 1000 chars. 
        </assert>
      <assert test=  "(string-length(ram:Reason) + string-length(ram:ReasonCode)) &lt;= 1000" id="CIUS-VD-61" flag="warning"> [CIUS-VD-61] BT-104, BT-105 (Document level charge reason - Document level charge reason code)-BTs maximum length shall be 1000 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation">
      <assert test=  "string-length(ram:DuePayableAmount) &lt;= 15 and string-length(ram:DuePayableAmount) &gt;= 4 and matches(ram:DuePayableAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" id="CIUS-VD-63" flag="warning"> [CIUS-VD-63] BT-115 (Amount due for payment) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
        </assert>
      <assert test=  "string-length(ram:GrandTotalAmount) &lt;= 15 and string-length(ram:GrandTotalAmount) &gt;= 4 and matches(ram:GrandTotalAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" id="CIUS-VD-62" flag="warning"> [CIUS-VD-62] BT-112 (Invoice total amount with VAT) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
        </assert>
      <assert test=  "not(exists(ram:RoundingAmount)) or (string-length(ram:RoundingAmount) &lt;= 15 and string-length(ram:RoundingAmount) &gt;= 4 and matches(ram:RoundingAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2,8}$'))" id="CIUS-VD-65" flag="warning"> [CIUS-VD-65] BT-114 (Rounding amount) -BT minimum length shall be 4 maximum length shall be 15 chars including from 2 to 8 fraction digit. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans">
      <assert test=  "string-length(ram:Information) &lt;= 200" id="CIUS-VD-55" flag="warning"> [CIUS-VD-55] BT-82 (Payment means text) -BT maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount">
      <assert test=  "string-length(ram:AccountName) &lt;= 200" id="CIUS-VD-58" flag="warning"> [CIUS-VD-58] BT-85 (Payment account name) -BT maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayerSpecifiedDebtorFinancialInstitution">
      <assert test=  "string-length(ram:BICID) &lt;= 11 and string-length(ram:BICID) &gt;= 8 and matches(ram:BICID,'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')" id="CIUS-VD-59" flag="warning"> [CIUS-VD-59] BT-86 (Payment service provider identifier) - BT should contain a SWIFT/BIC (bank identifier code) according to structure defined in ISO 9362 (minimum length shall be 8- maximum length shall be 11 chars). 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:AssociatedDocumentLineDocument">
      <assert test=  "string-length(ram:IncludedNote/ram:Content) &lt;= 60" id="CIUS-VD-75" flag="warning"> [CIUS-VD-75] BT-127 (Invoice line note) -BT maximum length shall be 60 chars. 
        </assert>
      <assert test=  "number(ram:LineID) &gt; 0 and number(ram:LineID) &lt;=9999" id="CIUS-SD-73" flag="warning"> [CIUS-SD-73] BT-126 (Invoice line identifier) -The BT value should be numeric. 
        </assert>
      <assert test=  "string-length(ram:LineID) &lt;= 4" id="CIUS-VD-74" flag="warning"> [CIUS-VD-74] BT-126 (Invoice line identifier) -BT maximum length shall be 4 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement/ram:BuyerOrderReferencedDocument">
      <assert test=  "string-length(ram:LineID) &lt;= 20" id="CIUS-VD-96" flag="warning"> [CIUS-VD-96] BT-132 (Referenced purchase order line reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement">
      <assert test=  "string-length(ram:GrossPriceProductTradePrice/ram:BasisQuantity) &lt;= 10" id="CIUS-VD-78-2" flag="warning"> [CIUS-VD-96-2] BT-149 (Item price base quantity) -BT maximum length shall be 10 chars. 
        </assert>
      <assert test=  "string-length(ram:GrossPriceProductTradePrice/ram:BasisQuantity/@unitCode) &lt;= 10" id="CIUS-VD-78-3" flag="warning"> [CIUS-VD-96-3] BT-150 (Item price base quantity unit of measure code) -BT maximum length shall be 10 chars. 
        </assert>
      <assert test=  "string-length(ram:NetPriceProductTradePrice/ram:ChargeAmount) &lt;= 21 and string-length(ram:NetPriceProductTradePrice/ram:ChargeAmount) &gt;= 4" id="CIUS-VD-83" flag="warning"> [CIUS-VD-83] BT-146 (Item net price) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </assert>
      <assert test=  "matches(ram:NetPriceProductTradePrice/ram:ChargeAmount, '^[0-9]+(\.[0-9]{0,8})*$')" id="CIUS-VD-95" flag="warning"> [CIUS-VD-95] BT-146 (Item net price) -BT allowed fraction digits shall be 8. 
        </assert>
      <assert test=  "string-length(ram:BilledQuantity/@unitCode) &lt;= 10" id="CIUS-VD-78-1" flag="warning"> [CIUS-VD-78-1-1] BT-130 (Invoiced quantity unit of measure) -BT maximum length shall be 10 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument">
      <assert test=  "string-length(ram:IssuerAssignedID) &lt;= 35 and ram:TypeCode='130'" id="CIUS-VD-77" flag="warning"> [CIUS-VD-77] BT-128 (Invoice line object identifier) -BT maximum length shall be 35 chars. 
        </assert>
      <assert test=  "string-length(ram:ReferenceTypeCode) &lt;= 35" id="CIUS-VD-76" flag="warning"> [CIUS-VD-76] BT-128-1 (Invoice line object identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount">
      <assert test=  "string-length(ram:ID) &lt;= 20" id="CIUS-VD-79" flag="warning"> [CIUS-VD-79] BT-133 (Invoice line Buyer accounting reference)-BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge">
      <assert test=  "string-length(ram:ActualAmount) &gt;= 4 and string-length(ram:ActualAmount) &lt;= 21" id="CIUS-VD-80" flag="warning"> [CIUS-VD-80] BT-136, BT-141 (Invoice line allowance amount - Invoice line charge amount)-BT minimum length shall be 4, maximum length shall be 21 chars. 
        </assert>
      <assert test=  "string-length(ram:Reason) &lt;= 1000" id="CIUS-VD-81-1" flag="warning"> [CIUS-VD-81-1-1] BT-139 (Invoice line allowance reason)-BT maximum length shall be 1000 chars. 
        </assert>
      <assert test=  "string-length(ram:Reason) &lt;= 1000" id="CIUS-VD-82-1" flag="warning"> [CIUS-VD-82-1-1] BT-144 (Invoice line charge reason)-BT maximum length shall be 1000 chars. 
        </assert>
      <assert test=  "string-length(ram:ReasonCode) &lt;= 1000" id="CIUS-VD-82-2" flag="warning"> [CIUS-VD-82-1-2] BT-145 (Invoice line charge reason code)-BT maximum length shall be 1000 chars. 
        </assert>
      <assert test=  "string-length(ram:ReasonCode) &lt;= 1000" id="CIUS-VD-81-2" flag="warning"> [CIUS-VD-82-2-2] BT-140 (Invoice line allowance reason code)-BT maximum length shall be 1000 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:ApplicableProductCharacteristic">
      <assert test=  "string-length(ram:Description) &lt;= 10" id="CIUS-VD-93" flag="warning"> [CIUS-VD-93] BT-160 (Item attribute name) -BT maximum length shall be 10 chars. 
        </assert>
      <assert test=  "string-length(ram:Value) &lt;= 60" id="CIUS-VD-94" flag="warning"> [CIUS-VD-94] BT-161 (Item attribute value) -BT maximum length shall be 60 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct">
      <assert test=  "string-length(ram:BuyerAssignedID) &lt;= 35" id="CIUS-VD-87" flag="warning"> [CIUS-VD-87] BT-156 (Item Buyer's identifier) -BT maximum length shall be 35 chars. 
        </assert>
      <assert test=  "string-length(ram:Description) &lt;= 1000" id="CIUS-VD-85-2" flag="warning"> [CIUS-VD-87-2] BT-154 (Item description) -BT maximum length shall be 1000 chars. 
        </assert>
      <assert test=  "string-length(ram:GlobalID) &lt;= 35" id="CIUS-VD-88" flag="warning"> [CIUS-VD-88] BT-157 (Item standard identifier) -BT maximum length shall be 35 chars. 
        </assert>
      <assert test=  "string-length(ram:GlobalID/@schemeID) &lt;= 35" id="CIUS-VD-90" flag="warning"> [CIUS-VD-90] BT-157-1 (Item standard identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
      <assert test=  "string-length(ram:Name) &lt;= 1000" id="CIUS-VD-85-1" flag="warning"> [CIUS-VD-85-1-1] BT-153 (Item name) -BT maximum length shall be 1000 chars. 
        </assert>
      <assert test=  "string-length(ram:OriginTradeCountry/ram:ID) &lt;= 60" id="CIUS-VD-92" flag="warning"> [CIUS-VD-92] BT-159 (Item country of origin) -BT maximum length shall be 60 chars. 
        </assert>
      <assert test=  "string-length(ram:SellerAssignedID) &lt;= 35" id="CIUS-VD-86" flag="warning"> [CIUS-VD-86] BT-155 (Item Seller's identifier) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification">
      <assert test=  "string-length(ram:ClassCode) &lt;= 35" id="CIUS-VD-89" flag="warning"> [CIUS-VD-89] BT-158 (Item classification identifier) -BT maximum length shall be 35 chars. 
        </assert>
      <assert test=  "string-length(ram:ClassCode/@listID) &lt;= 35" id="CIUS-VD-91-1" flag="warning"> [CIUS-VD-91-1-1] BT-158-1 (Item classification identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
      <assert test=  "string-length(ram:ClassCode/@listVersionID) &lt;= 35" id="CIUS-VD-91-2" flag="warning"> [CIUS-VD-91-1-2] BT-158-2 (Scheme version identifer) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
 </pattern>

  


</schema>
