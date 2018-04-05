<?xml version="1.0" encoding="UTF-8"?>
<!-- 

            UBL syntax binding to the CIUS ITALIA  
            Created by eIGOR Project
            Timestamp: 2018-03-12 12:00:00 +0100
            Release: 0.10.3

-->

<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        xmlns:cn="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"
        xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
        xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
        xmlns:UBL="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xs="http://www.w3.org/2001/XMLSchema"
        queryBinding="xslt2">

		<title>EN16931 - eIGOR Project - UBL CIUS IT</title>
    <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
    <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
    <ns prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"/>
    <ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
    <ns prefix="xs" uri="http://www.w3.org/2001/XMLSchema"/>

  <!-- Parameters for country identification-->  
  <let name="supplierCountry" value="if (/*/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) else 'XX'"/>
  <let name="customerCountry" value="if (/*/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) else 'XX'"/>
  <let name="deliveryCountry" value="if (/*/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode)) else 'XX'"/>
  
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

  <!--  
    ID: BT-84	
    Cardinality:  1..1	
    Level: 3
    Business Term:  Payment account identifier	
    Description:  A unique identifier of the financial payment account, at a payment service provider, to which payment should be made.
    eIGOR CIUS Reference: CIUS-BT-84

    Restriction:  The payment account identifier shall be an IBAN code
    -->  
  <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount">
      <assert test=  "matches(cbc:ID, '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')" 
        id="CIUS-BT-84" 
        flag="fatal"> [CIUS-BT-84] BT-84 (Payment account identifier) shall be an IBAN code and respect the Regular Expression [a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30}) . 
      </assert>
  </rule>

  <rule context="cac:AccountingCustomerParty/cac:Party">
    <!--  
    ID: BT-49 - BT-49-1	
    Cardinality:  0..1 - 1..1	
    Level: 2
    Business Term:  Buyer electronic address	- Buyer electronic address identification scheme identifier
    Description:  Identifies the Buyer's electronic address to which a business document should be delivered - The identification scheme identifier of the Buyer electronic address.
    eIGOR CIUS Reference: CIUS-CA-2

    Restriction:  BT-49 shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario (see the Italian business rules). BT-49-1=IT:PEC or IT:IPA or IT:CODDEST
    -->
    <assert test=  "exists(cbc:EndpointID) and (cbc:EndpointID[@schemeID = 'IT:CODDEST'] or cbc:EndpointID[@schemeID = 'IT:PEC'] or cbc:EndpointID[@schemeID = 'IT:IPA'] )" 
        id="CIUS-CA-2" 
        flag="fatal"> [CIUS-CA-2] BT-49 BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario (see the Italian business rules). BT-49-1=IT:PEC or IT:IPA or IT:CODDEST 
        </assert>
    <!--  
    ID: BT-48 BT-46 BT-46-1	
    Cardinality:  0..1 - 0..n - 0..1	
    Level: 2 2 2 
    Business Term: Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier	
    Description: The Buyer's VAT identifier (also known as Buyer VAT identification number). - An identifier of the Buyer. - The identification scheme identifier of the Buyer identifier. 
    eIGOR CIUS Reference: CIUS-BR-14
        
    Restriction: 1.4.1.1 is not mandatory in XMLPA (buyer) but VAT number or Fiscal code should be indicated as IT:CF in BT-46 when BT-55="IT" 
    -->
    <assert test=  "exists(cac:PartyTaxScheme/cbc:CompanyID) or exists(cac:PartyIdentification/cbc:ID[@schemeID = 'IT:CF']) or exists(cac:PartyIdentification/cbc:ID[@schemeID = 'IT:VAT'])" 
        id="CIUS-BR-14" 
        flag="fatal"> [CIUS-BR-14] BT-48 BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) -1.4.1.1 is not mandatory in Italy (buyer) but VAT number or Fiscal code should be indicated. 
        </assert>
   </rule>

  <!--  
    ID: BT-81	
    Cardinality:  1..1	
    Level 2
    Business Term:  Payment means type code	
    Description:  The means, expressed as code, for how a payment is expected to be or has been settled.
    eIGOR CIUS Reference: CIUS-CA-103

    Restriction:  Fields are mandatory in XMLPA. Mapped BG-16 Payment instructions should be mandatory and BT-81 is 1..1
    -->
  <rule context="cac:PaymentMeans">
      <assert test=  "exists(cbc:PaymentMeansCode)" 
        id="CIUS-CA-103" 
        flag="fatal"> [CIUS-CA-103] BT-81 (Payment means type code) -Fields are mandatory in XMLPA. Mapped BTs should be mandatory 
        </assert>
  </rule>
  
 </pattern>

  <!-- 
            CIUS-USAGE-IT  - Usage Rules
            - FATAL ERRORS
  -->

<pattern xmlns="http://purl.oclc.org/dsdl/schematron"  id="CIUS-USAGE-IT">
  
  <!--  
    ID: BT-32	
    Cardinality:  0..1	
    Business Term:  Seller tax registration identifier
    Description:  The local identification (defined by the Seller’s address) of the Seller for tax purposes or a reference that enables the Seller to state his registered tax status.
    eIGOR CIUS Reference: CIUS-BT-98-1
    
    Restriction:  BT is a conditional field and shall not be used by a foreign seller as it is not possible to map into XMLPA. CEN business rules are not broken. In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8)    

  // controllare se rilassare la regola e permettere uso del BT-32 anche se fornitore non Italiano - In questo caso non sarà mappato in Regime Fiscale
  // VALUTARE SE RIFORMARE IN BASE A COME E' STATA SCRITTA RESTRIZIONE PER PEPPOL

  2018-03-09
  RULE DELETED
  
  <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme">
    <assert test=  "(cac:TaxScheme/cbc:ID='VAT') or (($supplierCountry = 'IT') and (exists(cbc:CompanyID)))" 
        id="CIUS-BT-98-1" 
        flag="fatal"> [CIUS-BT-98-1] BT-32 (Seller tax registration identifier) is a conditional field and shall not be used by a foreign seller as it is not possible to map into XMLPA. 
    </assert>
   </rule>
 -->

<!--  
    ID: BT-32	
    Cardinality:  0..1	
    Business Term:  Seller tax registration identifier
    Description:  The local identification (defined by the Seller’s address) of the Seller for tax purposes or a reference that enables the Seller to state his registered tax status.
    eIGOR CIUS Reference: CIUS-BT-98-2
    
    Restriction:  BT is a conditional field and shall not be used by a foreign seller as it is not possible to map into XMLPA. CEN business rules are not broken. In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8)    

  // controllare se rilassare la regola e permettere uso del BT-32 anche se fornitore non Italiano - In questo caso non sarà mappato in Regime Fiscale
  // VALUTARE SE RIFORMARE IN BASE A COME E' STATA SCRITTA RESTRIZIONE PER PEPPOL
  -->
  <rule context="cac:AccountingSupplierParty/cac:Party">
    <assert test=  "not($supplierCountry = 'IT') or count(cac:PartyTaxScheme/cac:TaxScheme[not(cbc:ID='VAT')]) &gt;=1" 
      id="CIUS-BT-98-2" 
      flag="fatal"> [CIUS-BT-98-2] BT-32 (Seller tax registration identifier). In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8) 
    </assert>
   </rule>

  <!--  
    ID: BT-31	- BT-63
    Cardinality:  0..1	
    Business Term:  Seller VAT identifier	- Seller tax representative VAT identifier
    Description:  The Seller's VAT identifier (also known as Seller VAT identification number). - 
    eIGOR CIUS Reference: CIUS-CA-9

    Restriction: 1.2.1.1 is mandatory in XMLPA (seller). BT-31 should be mandatory or copied from BT-63 (tax representative). 
    -->
  <rule context="//ubl:Invoice | //cn:CreditNote">
    <assert test=  "( exists(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) and cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID = 'VAT') or exists(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID)" 
        id="CIUS-CA-9" 
        flag="fatal"> [CIUS-CA-9] BT-31 BT-63 (Seller VAT identifier - Seller tax representative VAT identifier) - Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax representative). 
    </assert>
    <!--  
    ID: BT-35 - BT-37 - BT-38	
    Cardinality:  0..1	
    Business Term: Seller address line 1 - Seller city - Seller post code 	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.  
    eIGOR CIUS Reference: CIUS-CA-10-1 - CIUS-CA-10-2 - CIUS-CA-10-3

    Restriction:  Fields are mandatory in XMLPA for Italian Seller - Mapped BTs should be mandatory
    -->
  </rule>
   <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
      <assert test=  "not ($supplierCountry = 'IT') or exists(cbc:StreetName)" 
        id="CIUS-CA-10-1" 
        flag="fatal"> [CIUS-CA-10-1] BT-35 (Seller address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "not ($supplierCountry = 'IT') or exists(cbc:CityName)" 
        id="CIUS-CA-10-2" 
        flag="fatal"> [CIUS-CA-10-2] BT-37 (Seller city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "not ($supplierCountry = 'IT') or exists(cbc:PostalZone)" 
        id="CIUS-CA-10-3" 
        flag="fatal"> [CIUS-CA-10-3] BT-38 (Seller post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
   </rule>

  <!--  
    ID: BT-50 - BT-52 - BT-53	
    Cardinality:  0..1	
    Business Term: Buyer address line 1 - Buyer city - Buyer post code 	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.  
    eIGOR CIUS Reference: CIUS-CA-11-1 - CIUS-CA-11-2 - CIUS-CA-11-3

    Restriction:  Fields are mandatory in XMLPA. Mapped BTs should be mandatory
    -->
  <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
      <assert test=  "not ($customerCountry = 'IT') or exists(cbc:StreetName)" 
        id="CIUS-CA-11-1" 
        flag="fatal"> [CIUS-CA-11-1] BT-50 (Buyer address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "not ($customerCountry = 'IT') or exists(cbc:CityName)" 
        id="CIUS-CA-11-2" 
        flag="fatal"> [CIUS-CA-11-2] BT-52 (Buyer city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
      <assert test=  "not ($customerCountry = 'IT') or exists(cbc:PostalZone)" 
        id="CIUS-CA-11-3" 
        flag="fatal"> [CIUS-CA-11-3] BT-53 (Buyer post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
      </assert>
   </rule>
  
  <!--  
    ID: BT-75 - BT-77 - BT-78	
    Cardinality:  0..1
    Level: 3 - 3 -3
    Business Term: Deliver to address line 1 - Deliver to city - Deliver to post code 	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.
    eIGOR CIUS Reference: CIUS-CA-12-1 - CIUS-CA-12-2 - CIUS-CA-12-3

    Restriction:  Fields are mandatory in XMLPA. Mapped BTs should be mandatory (If BG-15 is present)
    --> 
   <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
     <assert test=  "not ($deliveryCountry = 'IT') or exists(cbc:StreetName)" 
        id="CIUS-CA-12-1" 
        flag="fatal"> [CIUS-CA-12-1] BT-75 (Deliver to address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
     <assert test=  "not ($deliveryCountry = 'IT') or exists(cbc:CityName)" 
        id="CIUS-CA-12-2" 
        flag="fatal"> [CIUS-CA-12-2] BT-77 (Deliver to city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
     <assert test=  "not ($deliveryCountry = 'IT') or exists(cbc:PostalZone)" 
        id="CIUS-CA-12-3" flag="fatal"> [CIUS-CA-12-3] BT-78 (Deliver to post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
   </rule>
  
  <!--  
    ID: BT-125 - BT-124
    Cardinality:  0..1	- 0..1
    Level: 2 - 2
    Business Term: Attached document - External document location	
    Description:  An attached document embedded as binary object or sent together with the invoice. - The URL (Uniform Resource Locator) that identifies where the external document is located.
    eIGOR CIUS Reference: CIUS-CA-71

    Restriction: If BT-122 not empty then BT-124 or BT-125 should be mandatory as the mapped field is mandatory in XMLPA. 

    Fix: 2018-03-09
    UBL Specific:
    BT-18 is mapped in AdditionalDocument with the condition cac:AdditionalDocumentReference/cbc:DocumentTypeCode=’130’ 
  --> 
  <rule context="cac:AdditionalDocumentReference">
    <assert test=  "cbc:DocumentTypeCode='130' or exists(cac:Attachment/cac:ExternalReference/cbc:URI) or exists(cac:Attachment/cbc:EmbeddedDocumentBinaryObject)" 
        id="CIUS-CA-71" 
        flag="fatal"> [CIUS-CA-71] BT-125 (Attached document) - If BT-122 not empty then BT-124 or BT-125 should be mandatory as the mapped field is mandatory in Italy. 
      </assert>
   </rule>
 </pattern>

	 <!-- 
            CIUS-SYNTAX-IT  - Syntax rules
            Rules to enable a CEN invoice to be convertible in XML-PA
     -->
 
<pattern xmlns="http://purl.oclc.org/dsdl/schematron"  id="CIUS-SYNTAX-IT">
   <rule context="//ubl:Invoice | //cn:CreditNote">
     
    <!--  
    ID: BT-1	
    Cardinality:  1..1	
    Business Term: Invoice number	
    Description:  A unique identification of the Invoice.
    eIGOR CIUS Reference: CIUS-VD-32

    Restriction:  BT maximum lenght shall be 20 digit
    -->
      <assert test=  "string-length(cbc:ID) &lt;= 20" 
        id="CIUS-VD-32" 
        flag="warning"> [CIUS-VD-32] BT-1 (Invoice number) - BT maximum length shall be 20 digits. 
      </assert>
     <!-- 
     ID: BT-21 BT-22	
     Cardinality:  (0..n) 0..1 1..1
     Business Term: 	Invoice note subject code - Invoice note
     Description:  The subject of the following textual note. - A textual note that gives unstructured information that is relevant to the Invoice as a whole.  
     eIGOR CIUS Reference: CIUS-VD-39

     Restriction:  The sum of BTs maximum lenght shall be 200 chars or a split mechanism in multiple lines should be implemented

      Multiple Note fields are permitted, it is required to test the length of each note
     -->
     <assert test=  "every $cbcNote in (cbc:Note) satisfies (string-length($cbcNote) &lt;= 200)" 
        id="CIUS-VD-39" 
        flag="warning"> [CIUS-VD-39] BT-21, BT-22 (Invoice note subject code Invoice note) - The sum of BTs maximum length shall be 200 chars. 
      </assert>
   </rule>
  
   <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification">
    <!--  
    ID: BT-46 BT-46-1	
    Cardinality:  0..1 - 0..1	
    Business Term: Buyer identifier - Buyer identifier identification scheme identifier	
    Description: An identifier of the Buyer. - The identification scheme identifier of the Buyer identifier. 
    eIGOR CIUS Reference: CIUS-VD-100-1 CIUS-VD-100-2 CIUS-VD-100-3
    
    Restriction: 
    BT46-1=IT:CF then BT-46 should a CodiceFiscale and minimum length shal be 11 and maximum lenght shall be 16 
    BT-46-1=IT:EORI then BT-46 minimum lenght 13 and maximum lenght shall be 17 
    BT-46-1=IT:VAT then BT-46 maximum length 30 (the first two chars indicates country code)
    -->
     <assert test=  "not (cbc:ID[@schemeID = 'IT:CF'])  or ( (string-length(cbc:ID) &gt;= 11) and (string-length(cbc:ID) &lt;=16)   and matches(cbc:ID,'^[A-Z0-9]{11,16}$')  )" 
        id="CIUS-VD-100-1" 
        flag="warning"> [CIUS-VD-100-1] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -BT46-1=IT:CF then BT-46 minimum lenght 11 and maximum lenght shall be 16 
     </assert>
     <assert test=  "not (cbc:ID[@schemeID = 'IT:EORI'])  or ( (string-length(cbc:ID) &gt;= 13) and (string-length(cbc:ID) &lt;=17))" 
       id="CIUS-VD-100-2" 
       flag="warning"> [CIUS-VD-100-2] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:EORI then BT-46 minimum lenght 13 and maximum lenght shall be 17 
     </assert>
     <assert test=  "not (cbc:ID[@schemeID = 'IT:VAT'])  or ( (string-length(cbc:ID) &lt;= 30) and (contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(cbc:ID,1,2) ) ))" 
       id="CIUS-VD-100-3" 
       flag="warning"> [CIUS-VD-100-3] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:VAT then BT-46 maximum length 30 (the first two chars indicates country code). 
     </assert>

     <!-- Temporary rules inserted waiting for availability of the required schemes in ISO 6523 
          BT-46 has cardinality 0..1 
          it is not required to test multiple instances
     --> 
     <assert test=  "not (cbc:ID[starts-with(.,'IT:CF')])  or ( (string-length(cbc:ID) &gt;= 17) and (string-length(cbc:ID) &lt;=22))" 
       id="CIUS-VD-100-1TMP" 
       flag="warning"> [CIUS-VD-100-1TMP] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght shall be 22  starting with "IT:CF ". 
     </assert>    
     <assert test=  "not (cbc:ID[starts-with(.,'IT:EORI')])  or ( (string-length(cbc:ID) &gt;= 21) and (string-length(cbc:ID) &lt;=25))" 
       id="CIUS-VD-100-2TMP" 
       flag="warning"> [CIUS-VD-100-2TMP] BT-46 (Buyer identifier) - BT-46 minimum lenght 21 and maximum lenght shall be 25 starting with "IT:EORI ". 
     </assert>
   </rule>

  <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification">
    <!--  
    ID: BT-29 BT-29-1	
    Cardinality:  0..n	0..1
    Business Term:  Seller identifier - Seller identifier identification scheme identifier 	
    Description:  An identification of the Seller. - The identification scheme identifier of the Seller identifier.  
    eIGOR CIUS Reference: CIUS-VD-101-1 CIUS-VD-101-2 CIUS-VD-101-3

    Restriction:
    BT29-1=IT:CF then BT-29 minimum lenght 11 and maximum lenght shall be 16 
    BT-29-1=IT:EORI then BT-29 minimum lenght 13 and maximum lenght shall be 17 
    BT-29-1=IT:VAT then BT-29 maximum length 30 (the first two chars indicates country code)

    -->        
    <assert test=  "not (cbc:ID[@schemeID = 'IT:CF'])  or ( (string-length(cbc:ID) &gt;= 11) and (string-length(cbc:ID) &lt;=16))" 
        id="CIUS-VD-101-1" 
        flag="warning"> [CIUS-VD-101-1] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -BT29-1=IT:CF then BT-29 minimum lenght 11 and maximum lenght shall be 16. 
    </assert>
    <assert test=  "not (cbc:ID[@schemeID = 'IT:EORI'])  or ( (string-length(cbc:ID) &gt;= 13) and (string-length(cbc:ID) &lt;=17))" 
      id="CIUS-VD-101-2" 
      flag="warning"> [CIUS-VD-101-2] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -If BT-29-1=IT:EORI then BT-29 minimum lenght 13 and maximum lenght shall be 17. 
    </assert>   
    <assert test=  "not (cbc:ID[@schemeID = 'IT:VAT'])  or ( (string-length(cbc:ID) &lt;= 30) and ( contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',substring(cbc:ID,1,2) ) ))" 
      id="CIUS-VD-101-3" 
      flag="warning"> [CIUS-VD-101-3] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -If BT-29-1=IT:VAT then BT-29 maximum length 30 (the first two chars indicates country code). 
    </assert>

     <!--  
           Temporary rules inserted waiting for availability of these schemes in ISO 6523 
           BT-29 has cardinality 0..n 
           it is  required to test multiple instances
    -->
    <assert test=  "every $cbcID in (cbc:ID) satisfies (not ($cbcID[starts-with(.,'IT:CF')])  or ( (string-length($cbcID) &gt;= 17) and (string-length($cbcID) &lt;=22)))" 
      id="CIUS-VD-101-1TMP" 
      flag="warning"> [CIUS-VD-101-1TMP] BT-29 (Seller identifier) - BT-29 minimum length 17 and maximum length shall be 22 starting with "IT:CF ".      
    </assert> 
    <assert test=  "every $cbcID in (cbc:ID) satisfies (not (cbc:ID[starts-with(.,'IT:EORI')])  or ( (string-length(cbc:ID) &gt;= 21) and (string-length(cbc:ID) &lt;=25)))" 
      id="CIUS-VD-101-2TMP" 
      flag="warning"> [CIUS-VD-101-2TMP] BT-29 Seller identifier) - BT-29 minimum lenght 21 and maximum lenght shall be 25 starting with "IT:EORI ". 
    </assert> 
         
   </rule>

  <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity">
     <!--  
    ID: BT-30	- BT-30-1
    Cardinality:  0..1	0..1
    Business Term:  Seller legal registration identifier - Seller legal registration identifier identification scheme identifier - The identification scheme identifier of the Seller legal registration identifier.	
    Description:  An identifier issued by an official registrar that identifies the Seller as a legal entity or person
    eIGOR CIUS Reference: CIUS-VD-102-1 CIUS-VD-102-2

    Restriction:
    BT-30-1=IT:REA then BT-30 minimum lenght 3 and maximum lenght shall be 22 (first two chars indicate the italian province code)
    BT-30-1=IT:ALBO then BT-30 maximum length 60 
    -->
     <assert test=  "not (cbc:CompanyID[@schemeID = 'IT:REA'])  or ( (string-length(cbc:CompanyID) &gt;= 3) and (string-length(cbc:CompanyID) &lt;=22) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(cbc:CompanyID,1,2) )))" 
        id="CIUS-VD-102-1" 
        flag="warning"> [CIUS-VD-102-1] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration identifier identification scheme identifier) -If BT-30-1=IT:REA then BT-30 minimum lenght 3 and maximum lenght shall be 22 (first two chars indicate the italian province code). 
     </assert>    
     <assert test=  "not (cbc:CompanyID[@schemeID = 'IT:ALBO'])  or (string-length(cbc:CompanyID) &lt;=120)" 
       id="CIUS-VD-102-2" 
       flag="warning"> [CIUS-VD-102-2] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration identifier identification scheme identifier) -If BT-30-1=IT:ALBO then BT-30 maximum length 120. 
     </assert>     
     <!--  
           Temporary rules inserted waiting for availability of these schemes in ISO 6523 
           BT-30 has cardinality 0..1 
           it is not required to test multiple instances
    -->
     <assert test=  "every $CompanyID in (cbc:CompanyID) satisfies (not ($CompanyID[starts-with(.,'IT:REA')])  or ( (string-length($CompanyID) &gt;= 10) and (string-length($CompanyID) &lt;=29) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring($CompanyID,8,2) ))))" 
       id="CIUS-VD-102-1-TMP" 
       flag="warning"> [CIUS-VD-102-1-TMP] BT-30 (Seller legal registration identifier) - BT-30 minimum lenght 10 and maximum lenght shall be 29 starting with "IT:REA " and the following two chars indicate the italian province code). 
     </assert>
     <assert test=  "every $CompanyID in (cbc:CompanyID) satisfies (not ($CompanyID[starts-with(.,'IT:ALBO')])  or (string-length($CompanyID) &lt;=128))" 
       id="CIUS-VD-102-2-TMP" 
       flag="warning"> [CIUS-VD-102-2TMP] BT-30 (Seller legal registration identifier) - BT-30 maximum length 128 starting with "IT:ALBO ". 
     </assert>
 
     <!--  
       Below the simple implementation

     <assert test=  "not (cbc:CompanyID[starts-with(.,'IT:REA')])  or ( (string-length(./cbc:CompanyID) &gt;= 10) and (string-length(./cbc:CompanyID) &lt;=29) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(cbc:CompanyID,8,2) )))" 
       id="CIUS-VD-102-1TMP" flag="warning"> [CIUS-VD-102-1TMP] BT-30 (Seller legal registration identifier) - BT-30 minimum lenght 10 and maximum lenght shall be 29 starting with "IT:REA " and the following two chars indicate the italian province code). 
     </assert>    
     <assert test=  "not (cbc:CompanyID[starts-with(.,'IT:ALBO')])  or (string-length(cbc:CompanyID) &lt;=128)" 
       id="CIUS-VD-102-2TMP" flag="warning"> [CIUS-VD-102-2TMP] BT-30 (Seller legal registration identifier) - BT-30 maximum length 128 starting with "IT:ALBO ". 
     </assert>
    -->
   </rule>
   
 <rule context="cac:AccountingCustomerParty/cac:Party">
   <!--  
    ID: BT-48 BT-46 BT-46-1	
    Cardinality:  0..1 - 0..n - 0..1	
    Business Term: Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier	
    Description: The Buyer's VAT identifier (also known as Buyer VAT identification number). - An identifier of the Buyer. - The identification scheme identifier of the Buyer identifier. 
    eIGOR CIUS Reference: CIUS-VD-53
    
    Restriction: 1.4.1.1 is not mandatory in XMLPA (buyer) but VAT number or Fiscal code should be indicated as IT:CF in BT-46 when BT-55="IT" 
    -->
   
   <assert test=  "exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )" 
     id="CIUS-VD-53" 
     flag="warning"> [CIUS-VD-53] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-48 is empty then one of the buyer identifiers (0..n) should be the FiscalCode in BT-46. BT-46-1 shall contain the scheme. 
   </assert>
   <!--  
    ID: BT-49 - BT-49-1	
    Cardinality:  0..1	1..1
    Business Term:  Buyer electronic address - Buyer electronic address identification scheme identifier 	
    Description:  Identifies the Buyer's electronic address to which a business document should be delivered. - The identification scheme identifier of the Buyer electronic address.  
    eIGOR CIUS Reference: CIUS-VD-97-1 CIUS-VD-97-2 CIUS-VD-97-3

    Restriction:
    If BT-49-1= IT:PEC schema then BT-49  minimum length shall be 7 maximum lenght shall be 256 chars
    else if BT-49-1 = IT:IPA schema then BT-49 maximum lenght shall be 6 chars
    else if BT-49-1 = IT:CODDEST schema then BT-49 maximum lenght shall be 7 chars
    -->      
    <assert test=  "not(cbc:EndpointID[@schemeID = 'IT:PEC']) or ( (string-length(cbc:EndpointID) &gt;= 7 and string-length(cbc:EndpointID) &lt;= 256) and matches(cbc:EndpointID,'^.+@.+[.]+.+$') )" 
        id="CIUS-VD-97-1" 
        flag="warning"> [CIUS-VD-97-1] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) -If BT-49-1=IT:PEC schema then BT-49 should be a PEC (email) address and  length shall be between 7 and 256 character 
    </assert>
    <assert test=  "not(cbc:EndpointID[@schemeID = 'IT:IPA']) or ( (string-length(cbc:EndpointID) = 6) and matches(cbc:EndpointID,'^[A-Z0-9]{6,7}$') )" 
          id="CIUS-VD-97-2" 
          flag="warning"> [CIUS-VD-97-2] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) =IT:IPA schema then BT-49 should be a IPA code and maximum length shall be 6 chars 
    </assert> 
    <assert test=  "not(cbc:EndpointID[@schemeID = 'IT:CODDEST']) or ( string-length(cbc:EndpointID) = 7  and matches(cbc:EndpointID,'^[A-Z0-9]{6,7}$') )" 
        id="CIUS-VD-97-3" 
        flag="warning"> [CIUS-VD-97-3] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier)=IT:CODDEST schema then BT-49 and maximum length shall be 7 chars. 
    </assert>
   </rule>
  
   <rule context="cac:AccountingCustomerParty/cac:Party/cac:Contact">
     <!--  
    ID: BT-56	
    Cardinality:  0..1	
    Business Term:  Buyer contact point 	
    Description:  A contact point for a legal entity or person.
    eIGOR CIUS Reference: CIUS-VD-51

    Restriction:  BT maximum lenght shall be 200 chars
    -->
     <assert test=  "string-length(cbc:Name) &lt;= 200" 
        id="CIUS-VD-51" 
        flag="warning"> [CIUS-VD-51] BT-56 (Buyer contact point) -BT maximum length shall be 200 chars. 
     </assert>
   </rule>
  <!--  
    ID: BT-44	
    Cardinality:  1..1	
    Business Term: Buyer name	
    Description:  The full name of the Buyer.  
    eIGOR CIUS Reference: CIUS-VD-18

    Restriction:  BT maximum lenght shall be 80 chars
    -->
   <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity">
      <assert test=  "string-length(cbc:RegistrationName) &lt;= 80" 
        id="CIUS-VD-18" 
        flag="warning"> [CIUS-VD-18] BT-44 (Buyer name) -BT maximum length shall be 80 chars. 
      </assert>
   </rule>
  <!--  
    ID: BT-48	
    Cardinality:  0..1
    Level: 2
    Business Term:  Buyer VAT identifier	
    Description:  The Buyer's VAT identifier (also known as Buyer VAT identification number).  
    eIGOR CIUS Reference: CIUS-VD-43

    Restriction:  BT maximum lenght shall be 30 chars
    -->      
   <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme">
      <assert test=  "string-length(cbc:CompanyID) &lt;= 30" 
        id="CIUS-VD-43" 
        flag="warning"> [CIUS-VD-43] BT-48 (Buyer VAT identifier) - BT maximum length shall be 30 chars. 
      </assert>
   </rule>
     
   <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
     <!--  
    ID: BT-50 - BT-51 - BT-163	
    Cardinality:  0..1 - 0..1 - 0..1	
    Business Term:  Buyer address line 1 - Buyer address line 2 - Buyer address line 3 	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.  
    eIGOR CIUS Reference: CIUS-VD-21

    Restriction:  The sum of BTs maximum lenght shall be 60 chars (including separator)
    -->
     <assert test=  "(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 60" 
        id="CIUS-VD-21" 
        flag="warning"> [CIUS-VD-21] BT-50, BT-51, BT-163 (Buyer address line 1 - Buyer address line 2 - Buyer address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
     </assert>
     <!--  
    ID: BT-52	
    Cardinality:  0..1	
    Business Term:  Buyer city 	
    Description:  The common name of the city, town or village, where the Buyer's address is located.  
    eIGOR CIUS Reference: CIUS-VD-24

    Restriction:  BT maximum lenght shall be 60 chars
    -->
      <assert test=  "string-length(cbc:CityName) &lt;= 60" 
        id="CIUS-VD-24" 
        flag="warning"> [CIUS-VD-24] BT-52 (Buyer city) -BT maximum length shall be 60 characters. 
      </assert>
    <!--  
    ID: BT-53	
    Cardinality:  0..1	
    Business Term:  Buyer post code 	
    Description:  The identifier for an addressable group of properties according to the relevant postal service.  
    eIGOR CIUS Reference: CIUS-VD-27-1 CIUS-VD-27-2

    Restriction:  BT maximum lenght shall be 15 chars (if country code =IT then it should be numeric and maximum length 5). In case the XMLPA will not be modified if country-code not =IT then CAP=99999 and CAP will be saved in attachment
    -->
     <!--  2018-3-9  Rule VD-26-1 Deleted - mapped as 9999 in XML-PA and the value reported in attachment

      <assert test=  "string-length(cbc:PostalZone) &lt;= 15" 
        id="CIUS-VD-27-1" 
        flag="warning"> [CIUS-VD-27-1] BT-53 (Buyer post code) -BT maximum length shall be 15 chars  if country-code not =IT and 5 chars if country-code=IT. 
      </assert>
     -->
      <assert test=  "not($customerCountry = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )" 
        id="CIUS-VD-27-2" 
        flag="warning"> [CIUS-VD-27-2] BT-53 (Buyer post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
      </assert>
     <!--  
    ID: BT-54	
    Cardinality:  0..1	
    Business Term:  Buyer country subdivision 	
    Description:  The subdivision of a country.  
    eIGOR CIUS Reference: CIUS-VD-30 - CIUS-VD-48

    Restriction:  
    BT maximum lenght shall be 2 chars only used if country code=IT else the BT is not used
    Only if country code=IT coded according to Italian province list
    -->
      <assert test=  "not($customerCountry = 'IT') or not(exists(cbc:CountrySubentity)) or string-length(cbc:CountrySubentity) = 2" 
        id="CIUS-VD-30" 
        flag="warning"> [CIUS-VD-30] BT-54 (Buyer country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
      </assert>   
      <assert test=  "not($customerCountry = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',cbc:CountrySubentity )" 
        id="CIUS-VD-48" 
        flag="warning"> [CIUS-VD-48] BT-54 (Buyer country subdivision) -If country code=IT it should be coded according to Italian province list. 
      </assert>
   </rule>
  
   <rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact">
     <!--  
    ID: BT-41	
    Cardinality:  0..1
    Level: 3
    Business Term: Seller contact point	
    Description:  A contact point for a legal entity or person.  
    eIGOR CIUS Reference: CIUS-VD-44

    Restriction:  BT maximum lenght shall be 200 chars
    -->
     <assert test=  "string-length(cbc:Name) &lt;= 200" 
        id="CIUS-VD-44" 
        flag="warning"> [CIUS-VD-44] BT-41 (Seller contact point) - BT maximum length shall be 200 chars. 
     </assert>
     <!--  
    ID: BT-42	
    Cardinality:  0..1
    Level:  3
    Business Term:  Seller contact telephone number 	
    Description:  A phone number for the contact point.
    eIGOR CIUS Reference: CIUS-VD-45

    Restriction:  BT minimum length shall be 5 maximum lenght shall be 12 chars
    -->
     <assert test=  "not(exists(cbc:Telephone)) or (string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) &gt;= 5)" 
        id="CIUS-VD-45" 
        flag="warning"> [CIUS-VD-45] BT-42 (Seller contact telephone number) -BT minimum length shall be 5 maximum length shall be 12 chars. 
     </assert>
     <!--  
    ID: BT-43	
    Cardinality:  0..1
    Level:  3
    Business Term:  Seller contact email address 	
    Description:  An e-mail address for the contact point.
    eIGOR CIUS Reference: CIUS-VD-46

    Restriction:  BT minimum length shall be 7 maximum lenght shall be 256 chars
    -->
      <assert test=  "not(exists(cbc:ElectronicMail)) or (string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) &gt;= 7)" 
        id="CIUS-VD-46" 
        flag="warning"> [CIUS-VD-46] BT-43 (Seller contact email address) -BT minimum length shall be 7 maximum length shall be 256 chars. 
      </assert>
   </rule>
  <!--  
    ID: BT-27	
    Cardinality:  1..1
    Level:  2
    Business Term:  Seller name 	
    Description:The full formal name by which the Seller is registered in the national registry of legal entities or as a Taxable person or otherwise trades as a person or persons.    
    eIGOR CIUS Reference: CIUS-VD-17

    Restriction:  BT maximum lenght shall be 80 chars
    -->
   <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity">
      <assert test=  "string-length(cbc:RegistrationName) &lt;= 80" 
        id="CIUS-VD-17" 
        flag="warning"> [CIUS-VD-17] BT-27 (Seller name) -BT maximum length shall be 80 chars. 
        </assert>
   </rule>
  <!--  
    ID: BT-32	
    Cardinality:  0..1
    Level:  2
    Business Term:  Seller tax registration identifier	
    Description:  The local identification (defined by the Seller’s address) of the Seller for tax purposes or a reference that enables the Seller to state his registered tax status.  
    eIGOR CIUS Reference: CIUS-VD-99

    Restriction:  In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8 from RF01 to RF19)
    -->
  <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID">
      <assert test=  "not($supplierCountry = 'IT') or (../cac:TaxScheme/cbc:ID='VAT') or contains( 'RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19 ',.)" 
        id="CIUS-VD-99" 
        flag="warning"> [CIUS-VD-99] BT-32 (Seller tax registration identifier) -In case the seller is Italian this field must contain the codification of RegimeFiscale 
      </assert>
    <!--  
    ID: BT-31	
    Cardinality:  0..1
    Level:  2
    Business Term:  Seller VAT identifier 	
    Description:  The Seller's VAT identifier (also known as Seller VAT identification number).  
    eIGOR CIUS Reference: CIUS-VD-41

    Restriction:  BT maximum lenght shall be 30 chars
    -->
      <assert test=  "string-length(cbc:CompanyID) &lt;= 30" 
        id="CIUS-VD-41" 
        flag="warning"> [CIUS-VD-41] BT-31 (Seller VAT identifier) -BT maximum length shall be 30 chars. 
      </assert>
   </rule>

   <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
     <!--  
    ID: BT-35 - BT-36 - BT-162	
    Cardinality:  0..1 -0.. -0..1
    Level:  3 - 3 - 3
    Business Term:   	Seller address line 1 - Seller address line 2 - Seller address line 3 
    Description:   The main address line in an address - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.
    eIGOR CIUS Reference: CIUS-VD-20

    Restriction:  The sum of BTs maximum lenght shall be 60 chars (including separator)
    -->
     <assert test=  "(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 60" 
        id="CIUS-VD-20" 
        flag="warning"> [CIUS-VD-20] BT-35, BT-36, BT-162 (Seller address line 1 - Seller address line 2 - Seller address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
     </assert>
     <!--  
    ID: BT-37	
    Cardinality:  0..1
    Level:  3
    Business Term:  Seller city 	
    Description:  The common name of the city, town or village, where the Seller address is located.  
    eIGOR CIUS Reference: CIUS-VD-23

    Restriction:  BT maximum lenght shall be 60 chars
    -->
     <assert test=  "string-length(cbc:CityName) &lt;= 60" 
        id="CIUS-VD-23" 
        flag="warning"> [CIUS-VD-23] BT-37 (Seller city) -BT maximum length shall be 60 characters. 
     </assert>
     <!--  
    ID: BT-38	
    Cardinality:  0..1	
    Business Term:  Seller post code 	
    Description:  The identifier for an addressable group of properties according to the relevant postal service.  
    eIGOR CIUS Reference: CIUS-VD-26-1 CIUS-VD-26-2

    Restriction:  BT maximum lenght shall be 15 chars (if country code =IT then it should be numeric and maximum length 5). In case the XMLPA will not be modified if country-code not =IT then CAP=99999 and CAP will be saved in attachment

    -->
<!--  2018-3-9  Rule VD-26-1 Deleted - mapped as 9999 in XML-PA and the value reported in attachment
     <assert test=  "string-length(cbc:PostalZone) &lt;= 15" 
        id="CIUS-VD-26-1" 
        flag="warning"> [CIUS-VD-26-1] BT-38 (Seller post code) - BT maximum length shall be 15 chars if country-code not =IT and 5 chars if country-code=IT. 
      </assert>
-->
      <assert test=  "not ($supplierCountry='IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )" 
        id="CIUS-VD-26-2" 
        flag="warning"> [CIUS-VD-26-2] BT-38 (Seller post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
      </assert>
     <!--  
    ID: BT-39	
    Cardinality:  0..1	
    Business Term:  Seller country subdivision 	
    Description:  The subdivision of a country.  
    eIGOR CIUS Reference: CIUS-VD-29 CIUS-VD-47

    Restriction:  BT maximum lenght shall be 2 chars only used if country code=IT else the BT is not used
    Only if country code=IT coded according to Italian province list

    -->
     <assert test=  "not ($supplierCountry='IT') or not(exists(cbc:CountrySubentity)) or string-length(cbc:CountrySubentity) = 2" 
        id="CIUS-VD-29" 
        flag="warning"> [CIUS-VD-29] BT-39 (Seller country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
      </assert> 
     <assert test=  "not ($supplierCountry='IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',cbc:CountrySubentity )" 
        id="CIUS-VD-47" 
        flag="warning"> [CIUS-VD-47] BT-39 (Seller country subdivision) -If country code=IT it should be coded according to Italian province list. 
      </assert>
   </rule>
  
   <rule context="cac:AdditionalDocumentReference">
     <!--
    ID: BT-122 - 125-2
    Cardinality:  1..1  (opz.) 1..1
    Level: 2 - 3
    Business Term:  Supporting document reference - Attached document Filename 	
    Description:  An identifier of the supporting document - The file name of the attached document  
    eIGOR CIUS Reference: CIUS-VD-69

    Restriction:  BTs maximum lenght shall be 60 chars
    -->  
      <assert test=  "(string-length(cbc:ID) + string-length(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename)) &lt;= 60" 
        id="CIUS-VD-69" 
        flag="warning"> [CIUS-VD-69] BT-122, BT-125-2 (Supporting document reference - Attached document Filename) - BT maximum length shall be 60 chars. 
      </assert>
     <!-- 
    ID: BT-123	
    Cardinality:  0..1
    Level: 2
    Business Term:  Supporting document description 	
    Description:  A description of the supporting document.  
    eIGOR CIUS Reference: CIUS-VD-70

    Restriction:  BT maximum lenght shall be 100 chars

    -->
      <assert test=  "string-length(cbc:DocumentType) &lt;= 100" 
        id="CIUS-VD-70" 
        flag="warning"> [CIUS-VD-70] BT-123 (Supporting document description) -BT maximum length shall be 100 chars. 
      </assert>
   </rule>
  
   <rule context="cac:AdditionalDocumentReference/cac:Attachment">
     <!--  
    ID: BT-125-1
    Cardinality:  1..1
    Business Term:  Attached document Mime code
    Description:  The mime code of the attached document.
    eIGOR CIUS Reference: CIUS-VD-72

    Restriction: BT maximum lenght shall be 10 chars
    
    ??? controllare perchè comunque dovrebbe essere una lista
    -->
     <assert test=  "string-length(EmbeddedDocumentBinaryObject/@mimeCode) &lt;= 10" 
        id="CIUS-VD-72" 
        flag="warning"> [CIUS-VD-72] BT-125-1 (Attached document Mime code) - BT maximum length shall be 10 chars. 
     </assert>
   </rule>

   <rule context="cac:AllowanceCharge">
    <!--  
    ID: BT-97 - BT-98
    Cardinality:  0..1
    Level: 2 - 2
    Business Term:  Document level allowance reason - Document level allowance reason code   	
    Description:  The reason for the document level allowance, expressed as text - The reason for the document level allowance, expressed as a code.  
    eIGOR CIUS Reference: CIUS-VD-60
    
    ID: BT-104 - BT-105
    Cardinality:  0..1 - 0..1
    Level:  2 - 2
    Business Term:  Document level charge reason - Document level charge reason code
    Description:  The reason for the document level charge, expressed as text. - The reason for the document level charge, expressed as a code.  
    eIGOR CIUS Reference: CIUS-VD-61
    
    2018-03-09
    The same rule should be applied to Invoiceline level
    
    ID: BT-139 - BT-140
    Cardinality:  0..1 - 0..1
    Level:  3 - 3
    Business Term:  Invoice line allowance reason - Invoice line allowance reason code
    Description:  The reason for the Invoice line allowance, expressed as text. - The reason for the Invoice line allowance, expressed as a code.
    eIGOR CIUS Reference: CIUS-VD-81

    ID: BT-144 - BT-145
    Cardinality:  0..1 - 0..1
    Level:  3 - 3
    Business Term:  Invoice line charge reason - Invoice line charge reason code
    Description:  The reason for the Invoice line charge, expressed as text. - The reason for the Invoice line charge, expressed as a code.
    eIGOR CIUS Reference: CIUS-VD-82
    
    Restriction:  BTs maximum lenght shall be 1000 chars
    Reviewed message: (BT-97+BT-98, BT-104+BT-105, BT-139+BT-140, BT-144+BT-145 ) => cbc:AllowanceChargeReason maximum lenght shall be 1000 chars

    IMPROVEMENT TO DO: manage the presence of only one of the BT and if both precent include the length of the separator
    -->
     <assert test=  "(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000" 
        id="CIUS-VD-60-61-81-82" 
        flag="warning"> (BT-97+BT-98, BT-104+BT-105, BT-139+BT-140, BT-144+BT-145 ) => cbc:AllowanceChargeReason maximum lenght shall be 1000 chars.
     </assert>
      
 <!-- 
    ID: BT-92 - BT-99	
    Cardinality:  0..1
    Level:  2 - 2
    Business Term:  Document level allowance amount - Document level charge amount 	
    Description:  The amount of an allowance, without VAT. - The amount of a charge, without VAT.  
    eIGOR CIUS Reference: CIUS-VD-64

    ID: BT-136 - BT-141
    Cardinality:  (opz.) 1..1	- 1..1
    Level:  3 - 3
    Business Term:  Invoice line allowance amount - Invoice line charge amount 	
    Description:  The amount of an allowance, without VAT. - The amount of a charge, without VAT.  
    eIGOR CIUS Reference: CIUS-VD-80

    UBL specific: 
    also BT-147	is mapped in cbc:Amount under ca:Price node but it is mapped in a descriptive element (as TipoDato - RiferimentoTesto (60 chars))

    Restriction:  BT minimum length shall be 4 maximum lenght shall be 21 chars
    Reviewed message: (BT-92, BT-99, BT-136, BT-141) => cbc:Amount minimum length shall be 4 maximum lenght shall be 21 chars.
    -->
     <assert test=  "(string-length(cbc:Amount) &lt;= 21 and string-length(cbc:Amount) &gt;= 4) or (ancestor::cac:Price)" 
        id="CIUS-VD-64-80" 
        flag="warning"> (BT-92, BT-99, BT-136, BT-141 and BT-147) => cbc:Amount minimum length shall be 4 maximum lenght shall be 21 chars. 
     </assert>
   </rule>
  
  <!--  
    ID: BT-25	
    Cardinality:  (opz.) 1..1
    Level: 2
    Business Term: Preceding Invoice number -  	
    Description:  The identification of an Invoice that was previously sent by the Seller.  
    eIGOR CIUS Reference: CIUS-VD-40

    Restriction:  BT maximum lenght shall be 20 chars
    -->
  <rule context="cac:BillingReference/cac:InvoiceDocumentReference">
      <assert test=  "string-length(cbc:ID) &lt;= 20" 
        id="CIUS-VD-40" 
        flag="warning"> [CIUS-VD-40] BT-25 (Preceding Invoice number)-BT maximum length shall be 20 chars. 
      </assert>
  </rule>

  <!--  
    ID: BT-12	
    Cardinality:  0..1
    Level:  1
    Business Term:  Contract reference 	
    Description:  The identification of a contract.
    eIGOR CIUS Reference: CIUS-VD-34

    Restriction:  BT maximum lenght shall be 20 chars
    -->      
   <rule context="cac:ContractDocumentReference">
      <assert test=  "string-length(cbc:ID) &lt;= 20" 
        id="CIUS-VD-34" 
        flag="warning"> [CIUS-VD-34] BT-12 (Contract reference) -BT maximum length shall be 20 chars. 
      </assert>
   </rule>

  <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
    <!--  
    ID: BT-75 - BT-76 - BT-165	
    Cardinality:  0..1
    Level: 3
    Business Term:  Deliver to address line 1 - Deliver to address line 2 -Deliver to address line 3	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.  
    eIGOR CIUS Reference: CIUS-VD-22

    Restriction:  The sum of BTs maximum lenght shall be 60 chars (including separator)
    -->
    <assert test=  "(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 60" 
        id="CIUS-VD-22" 
        flag="warning"> [CIUS-VD-22] BT-75, BT-76, BT-165 (Deliver to address line 1 - Deliver to address line 2 - Deliver to address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
    </assert>
    <!--  
    ID: BT-77	
    Cardinality:  0..1
    Level: 3
    Business Term:  Deliver to city
    Description:  The common name of the city, town or village, where the deliver to address is located.  
    eIGOR CIUS Reference: CIUS-VD-25

    Restriction:  BT maximum lenght shall be 60 chars
    -->
      <assert test=  "string-length(cbc:CityName) &lt;= 60" 
        id="CIUS-VD-25" 
        flag="warning"> [CIUS-VD-25] BT-77 (Deliver to city) -BT maximum length shall be 60 characters. 
      </assert>
    <!--  
    ID: BT-78	
    Cardinality:  0..1
    Level: 3
    Business Term:  Deliver to post code
    Description:  The identifier for an addressable group of properties according to the relevant postal service.
    eIGOR CIUS Reference: CIUS-VD-28-1 CIUS-VD-28-2
    
    Restriction:  BT maximum lenght shall be 15 chars (if country code =IT then it should be numeric and maximum length 5). In case the XMLPA will not be modified if country-code not =IT then CAP=99999 and CAP will be saved in attachment

    -->
    <!--  2018-3-9  Rule VD-26-1 Deleted - mapped as 9999 in XML-PA and the value reported in attachment

      <assert test=  "string-length(cbc:PostalZone) &lt;= 15" 
        id="CIUS-VD-28-1" 
        flag="warning"> [CIUS-VD-28-1] BT-78 (Deliver to post code) -BT maximum length shall be 15 chars if country-code not =IT and 5 chars if country-code=IT. 
      </assert>
-->
      <assert test=  "not($deliveryCountry = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )" 
        id="CIUS-VD-28-2" 
        flag="warning"> [CIUS-VD-28-2] BT-78 (Deliver to post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
      </assert>
    <!--  
    ID: BT-79
    Cardinality:  0..1
    Level:  3
    Business Term:  Deliver to country subdivision
    Description:  The subdivision of a country.
    eIGOR CIUS Reference: CIUS-VD-31 - CIUS-VD-49

    Restriction:  BT maximum lenght shall be 2 chars only used if country code=IT else the BT is not used
    -->
      <assert test=  "not($deliveryCountry = 'IT') or string-length(cbc:CountrySubentity) = 2" 
        id="CIUS-VD-31" 
        flag="warning"> [CIUS-VD-31] BT-79 (Deliver to country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
      </assert>
    <assert test=  "not($deliveryCountry = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',cbc:CountrySubentity )" 
        id="CIUS-VD-49" 
        flag="warning"> [CIUS-VD-49] BT-79 (Deliver to country subdivision) -If country code=IT it should be coded according to Italian province list. 
      </assert>
  
  </rule>
  
   <rule context="cac:DespatchDocumentReference">
     <!--  
    ID: BT-16
    Cardinality:  0..1
    Level: 1
    Business Term:  Despatch advice reference 	
    Description:  An identifier of a referenced despatch advice. 
    eIGOR CIUS Reference: CIUS-VD-15 - CIUS-VD-16

    Restriction:  
    BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05)
    BT maximum lenght shall be 30 chars (20 digit + YYYY-MM-DD)
   
     -->
     <!-- FIX 2018-03-09: CIUS-VD-15 not required
     <assert test=  "matches(cbc:ID, '(^\c{1,20})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})$')" 
        id="CIUS-VD-15" 
        flag="warning"> [CIUS-VD-15] BT-16 (Despatch advice reference) -BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05) 
     </assert>
     -->
      <assert test=  "string-length(cbc:ID) &lt;= 31" 
        id="CIUS-VD-16" 
        flag="warning"> [CIUS-VD-16] BT-16 (Despatch advice reference) -BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD). 
      </assert>
   </rule>
  
   <rule context="cac:InvoiceLine">
     <!--  
    ID: BT-126	
    Cardinality:  1..1
    Level:  2
    Business Term:  Invoice line identifier 	
    Description:  A unique identifier for the individual line within the Invoice.  
    eIGOR CIUS Reference: CIUS-SD-73

    Restriction:  The BT value should be numeric

    -->
     <assert test=  "number(cbc:ID) &gt; 0 and number(cbc:ID) &lt;=9999" 
        id="CIUS-SD-73" 
        flag="warning"> [CIUS-SD-73] BT-126 (Invoice line identifier) -The BT value should be numeric. 
     </assert>
     <!--  
    ID: BT-126	
    Cardinality:  1..1
    Level:  2
    Business Term:  Invoice line identifier 	
    Description:  A unique identifier for the individual line within the Invoice.  
    eIGOR CIUS Reference: CIUS-VD-74

    Restriction:  BT maximum lenght shall be 4 digits
    -->
     <assert test=  "string-length(cbc:ID) &lt;= 4" 
       id="CIUS-VD-74" 
       flag="warning"> [CIUS-VD-74] BT-126 (Invoice line identifier) -BT maximum length shall be 4 chars. 
     </assert>
     <!--  
    ID: BT-19	
    Cardinality:  0..1
    Level: 1
    Business Term:  Buyer accounting reference
    Description:  A textual value that specifies where to book the relevant data into the Buyer's financial accounts.
    eIGOR CIUS Reference: CIUS-VD-38

    Restriction:  BT maximum lenght shall be 20 chars
    -->
     <assert test=  "string-length(cbc:AccountingCost) &lt;= 20" 
        id="CIUS-VD-38" 
        flag="warning"> [CIUS-VD-38] BT-19 (Buyer accounting reference) -BT maximum length shall be 20 chars. 
     </assert>
     <!--  
    ID: BT-127	
    Cardinality:  0..1
    Level:  2
    Business Term:  Invoice line note
    Description:  A textual note that gives unstructured information that is relevant to the Invoice line.
    eIGOR CIUS Reference: CIUS-VD-75

    Restriction:  BT maximum lenght shall be 60 chars
    -->
      <assert test=  "string-length(cbc:Note) &lt;= 60" 
        id="CIUS-VD-75" 
        flag="warning"> [CIUS-VD-75] BT-127 (Invoice line note) -BT maximum length shall be 60 chars. 
      </assert>
     <!--  
    ID: BT-130	
    Cardinality:  1..1
    level: 2
    Business Term:  Invoiced quantity unit of measure 	
    Description:  The unit of measure that applies to the invoiced quantity.
    eIGOR CIUS Reference: CIUS-VD-78-1

    Restriction:  BTs maximum lenght shall be 10 chars
    -->
      <assert test=  "string-length(cbc:InvoicedQuantity/@unitCode) &lt;= 10" 
        id="CIUS-VD-78-1" 
        flag="warning"> [CIUS-VD-78-1] BT-130 (Invoiced quantity unit of measure) -BT maximum length shall be 10 chars. 
      </assert>
     <!--  
    ID: BT-149	
    Cardinality:  0..1	
    Business Term:  Item price base quantity
    Level:  3
    Description:  The number of item units to which the price applies.  
    eIGOR CIUS Reference: CIUS-VD-78-2

    Restriction:  BTs maximum lenght shall be 10 chars  
    -->
      <assert test=  "string-length(cac:Price/cbc:BaseQuantity) &lt;= 10" 
        id="CIUS-VD-78-2" 
        flag="warning"> [CIUS-VD-78-2] BT-149 (Item price base quantity) -BT maximum length shall be 10 chars. 
      </assert>
     <!--  
    ID: BT-150	
    Cardinality:  0..1
    Level:  3
    Business Term:  Item price base quantity unit of measure code
    Description:  The unit of measure that applies to the Item price base quantity.
    eIGOR CIUS Reference: CIUS-VD-78-3

    Restriction:  BTs maximum lenght shall be 10 chars
    -->
      <assert test=  "string-length(cac:Price/cbc:BaseQuantity/@unitCode) &lt;= 10" 
        id="CIUS-VD-78-3" 
        flag="warning"> [CIUS-VD-78-3] BT-150 (Item price base quantity unit of measure code) -BT maximum length shall be 10 chars. 
      </assert>
     <!--  
    ID: BT-133
    Cardinality:  0..1
    Level:  2
    Business Term:  Invoice line Buyer accounting reference
    Description:  A textual value that specifies where to book the relevant data into the Buyer's financial accounts.
    eIGOR CIUS Reference: CIUS-VD-79

    Restriction:  BT maximum lenght shall be 20 chars
    -->
      <assert test=  "string-length(cbc:AccountingCost) &lt;= 20" 
        id="CIUS-VD-79" 
        flag="warning"> [CIUS-VD-79] BT-133 (Invoice line Buyer accounting reference)-BT maximum length shall be 20 chars. 
      </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:DocumentReference">
     <!--  
    ID: BT-128-1
    Cardinality:  0..1
    Level:  2
    Business Term:  Invoice line object identifier identification scheme identifier	
    Description:  The identification scheme identifier of the Invoice line object identifier.
    eIGOR CIUS Reference: CIUS-VD-76

    Restriction:  BT maximum lenght shall be 35 chars
    -->
     <assert test=  "string-length(cbc:ID/@schemeID) &lt;= 35" 
        id="CIUS-VD-76" 
        flag="warning"> [CIUS-VD-76] BT-128-1 (Invoice line object identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
     </assert>
     <!--  
    ID: BT-128
    Cardinality:  0..1
    Level:  2
    Business Term: 	Invoice line object identifier
    Description:  An identifier for an object on which the invoice line is based, given by the Seller.
    eIGOR CIUS Reference: CIUS-VD-77

    Restriction:  BT maximum lenght shall be 35 chars
    -->
      <assert test=  "string-length(cbc:ID) &lt;= 35" 
        id="CIUS-VD-77" 
        flag="warning"> [CIUS-VD-77] BT-128 (Invoice line object identifier) -BT maximum length shall be 35 chars. 
      </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:Item">
     <!--  
    ID: BT-153
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item name
    Description:  A name for an item.
    eIGOR CIUS Reference: CIUS-VD-85-1

    Restriction:  BTs maximum lenght shall be 1000 chars
    -->
     <assert test=  "string-length(cbc:Name) &lt;= 1000" 
        id="CIUS-VD-85-1" 
        flag="warning"> [CIUS-VD-85-1] BT-153 (Item name) -BT maximum length shall be 1000 chars. 
     </assert>
     <!--  
    ID: BT-154
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item description
    Description:  A description for an item.
    eIGOR CIUS Reference: CIUS-VD-85-2

    Restriction:  BTs maximum lenght shall be 1000 chars
    -->
      <assert test=  "string-length(cbc:Description) &lt;= 1000" 
        id="CIUS-VD-85-2" 
        flag="warning"> [CIUS-VD-85-2] BT-154 (Item description) -BT maximum length shall be 1000 chars. 
      </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty">
     <!--  
    ID: BT-160	
    Cardinality:  1..1
    Level:  4
    Business Term: 	Item attribute name
    Description:  The name of the attribute or property of the item.
    eIGOR CIUS Reference: CIUS-VD-93

    Restriction:  BT maximum lenght shall be 10 chars
    -->
     <assert test=  "string-length(cbc:Name) &lt;= 10" 
        id="CIUS-VD-93" 
        flag="warning"> [CIUS-VD-93] BT-160 (Item attribute name) -BT maximum length shall be 10 chars. 
     </assert>
     <!--  
    ID: BT-161
    Cardinality:  1..1
    Level:  4
    Business Term: 	Item attribute value
    Description:  The value of the attribute or property of the item.
    eIGOR CIUS Reference: CIUS-VD-94

    Restriction:  BT maximum lenght shall be 60 chars
    -->
      <assert test=  "string-length(cbc:Value) &lt;= 60" 
        id="CIUS-VD-94" 
        flag="warning"> [CIUS-VD-94] BT-161 (Item attribute value) -BT maximum length shall be 60 chars. 
      </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification">
     <!--  
    ID: BT-156
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item Buyer's identifier
    Description:  An identifier, assigned by the Buyer, for the item.
    eIGOR CIUS Reference: CIUS-VD-87

    Restriction:  BT maximum lenght shall be 35 chars
    -->
     <assert test=  "string-length(cbc:ID) &lt;= 35" 
        id="CIUS-VD-87" 
        flag="warning"> [CIUS-VD-87] BT-156 (Item Buyer's identifier) -BT maximum length shall be 35 chars. 
     </assert>
   </rule>
  <!--  
    ID: BT-158	
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item classification identifier
    Description:  A code for classifying the item by its type or nature.
    eIGOR CIUS Reference: CIUS-VD-89

    Restriction:  BT maximum lenght shall be 35 chars
    -->
   <rule context="cac:InvoiceLine/cac:Item/cac:CommodityClassification">
      <assert test=  "string-length(cbc:ItemClassificationCode) &lt;= 35" 
        id="CIUS-VD-89" 
        flag="warning"> [CIUS-VD-89] BT-158 (Item classification identifier) -BT maximum length shall be 35 chars. 
      </assert>
   </rule>


  <rule context="cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode">
    <!--  
    ID: BT-158-1
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item classification identifier identification scheme identifier
    Description:  The identification scheme identifier of the Item classification identifier
    eIGOR CIUS Reference: CIUS-VD-91-1

    Restriction:  BTs maximum lenght shall be 35 chars
    -->
    <assert test=  "string-length(@listVersionID) &lt;= 35" 
        id="CIUS-VD-91-1" 
        flag="warning"> [CIUS-VD-91-1] BT-158-1 (Item classification identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
    </assert>
    <!--  
    ID: BT-158-2
    Cardinality:  0..1
    Level:  3
    Business Term: 	Scheme version identifer
    Description:  The version of the identification scheme.
    eIGOR CIUS Reference: CIUS-VD-91-2

    Restriction:  BTs maximum lenght shall be 35 chars
    -->
      <assert test=  "string-length(@listID) &lt;= 35" 
        id="CIUS-VD-91-2" 
        flag="warning"> [CIUS-VD-91-2] BT-158-2 (Scheme version identifer) -BT maximum length shall be 35 chars. 
      </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:Item/cac:OriginCountry">
     <!--  
    ID: BT-159
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item country of origin
    Description:  The code identifying the country from which the item originates.
    eIGOR CIUS Reference: CIUS-VD-92

    Restriction:  BT maximum lenght shall be 60 chars
    -->
     <assert test=  "string-length(cbc:IdentificationCode) &lt;= 60" 
        id="CIUS-VD-92" 
        flag="warning"> [CIUS-VD-92] BT-159 (Item country of origin) -BT maximum length shall be 60 chars. 
     </assert>
   </rule>

  <rule context="cac:InvoiceLine/cac:Item/cac:SellersItemIdentification">
    <!--  
    ID: BT-155	
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item Seller's identifier
    Description:  An identifier, assigned by the Seller, for the item.
    eIGOR CIUS Reference: CIUS-VD-86

    Restriction:  BT maximum lenght shall be 35 chars
    -->
    <assert test=  "string-length(cbc:ID) &lt;= 35" 
        id="CIUS-VD-86" 
        flag="warning"> [CIUS-VD-86] BT-155 (Item Seller's identifier) -BT maximum length shall be 35 chars. 
    </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification">
     <!--  
    ID: BT-157
    Cardinality:  0..1	
    Business Term: 	Item standard identifier
    Description:  An item identifier based on a registered scheme.
    eIGOR CIUS Reference: CIUS-VD-88

    Restriction:  BT maximum lenght shall be 35 chars
    -->
     <assert test=  "string-length(cbc:ID) &lt;= 35" 
        id="CIUS-VD-88" 
        flag="warning"> [CIUS-VD-88] BT-157 (Item standard identifier) -BT maximum length shall be 35 chars. 
     </assert>
     <!--  
    ID: BT-157-1
    Cardinality:  1..1
    Level:  3
    Business Term: 	Item standard identifier identification scheme identifier
    Description:  The identification scheme identifier of the Item standard identifier
    eIGOR CIUS Reference: CIUS-VD-90

    Restriction:  BT maximum lenght shall be 35 chars
    -->
     <assert test=  "string-length(cbc:ID/@schemeID) &lt;= 35" 
        id="CIUS-VD-90" 
        flag="warning"> [CIUS-VD-90] BT-157-1 (Item standard identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
     </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:OrderLineReference">
     <!--  
    ID: BT-132
    Cardinality:  0..1
    Level:  2
    Business Term: 	Referenced purchase order line reference
    Description:  An identifier for a referenced line within a purchase order, issued by the Buyer.
    eIGOR CIUS Reference: CIUS-VD-96

    Restriction:  BT maximum lenght shall be 20 chars
    -->
     <assert test=  "string-length(cbc:LineID) &lt;= 20" 
        id="CIUS-VD-96" 
        flag="warning"> [CIUS-VD-96] BT-132 (Referenced purchase order line reference) -BT maximum length shall be 20 chars. 
     </assert>
   </rule>
  
   <rule context="cac:InvoiceLine/cac:Price">
     <!--  
    ID: BT-146
    Cardinality:  1..1
    Level:  3
    Business Term: 	Item net price
    Description:  The price of an item, exclusive of VAT, after subtracting item price discount.
    eIGOR CIUS Reference: CIUS-VD-83 CIUS-VD-95

    Restriction:  
    BT minimum length shall be 4 maximum lenght shall be 21 chars
    BT allowed fraction digits shall be 8
     -->
     <assert test=  "string-length(cbc:PriceAmount) &lt;= 21 and string-length(cbc:PriceAmount) &gt;= 4" 
        id="CIUS-VD-83" 
        flag="warning"> [CIUS-VD-83] BT-146 (Item net price) -BT minimum length shall be 4 maximum length shall be 21 chars. 
     </assert>
     <assert test=  "matches(cbc:PriceAmount, '^[0-9]+(\.[0-9]{0,8})*$')" 
        id="CIUS-VD-95" 
        flag="warning"> [CIUS-VD-95] BT-146 (Item net price) -BT allowed fraction digits shall be 8. 
     </assert>
   </rule>
  
   <rule context="cac:LegalMonetaryTotal">
     <!--  
    ID: BT-112
    Cardinality:  1..1
    Level:  2
    Business Term: 	Invoice total amount with VAT
    Description:  The total amount of the Invoice with VAT.
    eIGOR CIUS Reference: CIUS-VD-62

    Restriction:  BT minimum length shall be 4 maximum lenght shall be 15 chars
    -->
     <assert test=  "string-length(cbc:TaxInclusiveAmount) &lt;= 15 and string-length(cbc:TaxInclusiveAmount) &gt;= 4 and matches(cbc:TaxInclusiveAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" 
        id="CIUS-VD-62" 
        flag="warning"> [CIUS-VD-62] BT-112 (Invoice total amount with VAT) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
     </assert>
     <!--  
    ID: BT-115	
    Cardinality:  1..1	
    Business Term: 	Amount due for payment
    Description:  The outstanding amount that is requested to be paid.
    eIGOR CIUS Reference: CIUS-VD-63

    Restriction:  BT minimum length shall be 4 maximum lenght shall be 15 chars
    -->
     <assert test=  "string-length(cbc:PayableAmount) &lt;= 15 and string-length(cbc:PayableAmount) &gt;= 4 and matches(cbc:PayableAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" 
        id="CIUS-VD-63" 
        flag="warning"> [CIUS-VD-63] BT-115 (Amount due for payment) -BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
     </assert>
     <!--  
    ID: BT-114	
    Cardinality:  0..1
    Level:  2
    Business Term: 	Rounding amount
    Description:  The amount to be added to the invoice total to round the amount to be paid.
    eIGOR CIUS Reference: CIUS-VD-65

    Restriction:  BT minimum length shall be 4 maximum lenght shall be 15 chars
    -->
      <assert test=  "not(exists(cbc:PayableRoundingAmount)) or (string-length(cbc:PayableRoundingAmount) &lt;= 15 and string-length(cbc:PayableRoundingAmount) &gt;= 4 and matches(cbc:PayableRoundingAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2,8}$'))" 
        id="CIUS-VD-65" 
        flag="warning"> [CIUS-VD-65] BT-114 (Rounding amount) -BT minimum length shall be 4 maximum length shall be 15 chars including from 2 to 8 fraction digit. 
      </assert>
   </rule>

  <!--
    ID: BT-13
    Cardinality:  0..1
    Level:  1
    Business Term: 	Purchase order reference
    Description:  An identifier of a referenced purchase order, issued by the Buyer.
    eIGOR CIUS Reference: CIUS-VD-35

    Restriction:  BT maximum lenght shall be 20 chars
    --> 
   <rule context="cac:OrderReference">
      <assert test=  "string-length(cbc:ID) &lt;= 20" 
        id="CIUS-VD-35" 
        flag="warning"> [CIUS-VD-35] BT-13 (Purchase order reference) -BT maximum length shall be 20 chars. 
      </assert>
   </rule>
  
  <!--  
    ID: BT-17
    Cardinality:  0..1
    Level:  1
    Business Term: 	Tender or lot reference
    Description:  The identification of the call for tender or lot the invoice relates to.
    eIGOR CIUS Reference: CIUS-VD-37

    Restriction:  BT maximum lenght shall be 15 chars
    --> 
   <rule context="cac:OriginatorDocumentReference">
      <assert test=  "string-length(cbc:ID) &lt;= 15" 
        id="CIUS-VD-37" 
        flag="warning"> [CIUS-VD-37] BT-17 (Tender or lot reference) -BT maximum length shall be 15 chars. 
      </assert>
   </rule>
  <!--  
    ID: BT-59	
    Cardinality:  1..1
    Level:  2
    Business Term: 	Payee name
    Description:  The name of the Payee.
    eIGOR CIUS Reference: CIUS-VD-50

    Restriction:  BT maximum lenght shall be 200 chars
    -->
   <rule context="cac:PayeeParty/cac:PartyName">
      <assert test=  "string-length(cbc:Name) &lt;= 200" 
        id="CIUS-VD-50" 
        flag="warning"> [CIUS-VD-50] BT-59 (Payee name) -BT maximum length shall be 200 chars. 
      </assert>
   </rule>

  <rule context="cac:PaymentMeans">
    <!--  
    ID: BT-82
    Cardinality:  0..1
    Level: 2
    Business Term: 	Payment means text
    Description:  The means, expressed as text, for how a payment is expected to be or has been settled.
    eIGOR CIUS Reference: CIUS-VD-55

    Restriction:  BT maximum lenght shall be 200 chars
    -->
    <assert test=  "string-length(cbc:InstructionNote) &lt;= 200" 
        id="CIUS-VD-55" 
        flag="warning"> [CIUS-VD-55] BT-82 (Payment means text) -BT maximum length shall be 200 chars. 
    </assert>
    <!--  
    ID: BT-83
    Cardinality:  0..1
    Level:  2
    Business Term: 	Remittance information
    Description: A textual value used to establish a link between the payment and the Invoice, issued by the Seller. 
    eIGOR CIUS Reference: CIUS-VD-56

    Restriction:  BT maximum lenght shall be 60 chars
    -->
      <assert test=  "string-length(cbc:PaymentID) &lt;= 60" 
        id="CIUS-VD-56" 
        flag="warning"> [CIUS-VD-56] BT-83 (Remittance information) -BT maximum length shall be 60 chars. 
      </assert>
   </rule>
  
   <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount">
     <!--  
    ID: BT-84
    Cardinality:  1..1
    Level: 3
    Business Term: Payment account identifier	
    Description: A unique identifier of the financial payment account, at a payment service provider, to which payment should be made. 
    eIGOR CIUS Reference: CIUS-VD-57

    Restriction:  BT minimum length shall be 15 maximum lenght shall be 34 chars
    -->
     <assert test=  "string-length(cbc:ID) &lt;= 34 and string-length(cbc:ID) &gt;= 15" 
        id="CIUS-VD-57" 
        flag="warning"> [CIUS-VD-57] BT-84 (Payment account identifier) -BT minimum length shall be 15, maximum length shall be 34 chars. 
     </assert>
     <!--  
    ID: BT-85
    Cardinality:  0..1
    Level: 3
    Business Term: Payment account name	
    Description:  The name of the payment account, at a payment service provider, to which payment should be made.
    eIGOR CIUS Reference: CIUS-VD-58

    Restriction:  BT maximum lenght shall be 200 chars
    -->
     <assert test=  "string-length(cbc:Name) &lt;= 200" 
        id="CIUS-VD-58" 
        flag="warning"> [CIUS-VD-58] BT-85 (Payment account name) -BT maximum length shall be 200 chars. 
     </assert>
   </rule>
  <!--  
    ID: BT-86
    Cardinality:  0..1
    Level: 3 
    Business Term: 	Payment service provider identifier
    Description:  An identifier for the payment service provider where a payment account is located.
    eIGOR CIUS Reference: CIUS-VD-59

    Restriction:  BT minimum length shall be 8 maximum lenght shall be 11 chars
    -->
   <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch">
      <assert test=  "string-length(cbc:ID) &lt;= 11 and string-length(cbc:ID) &gt;= 8 and matches(cbc:ID,'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')" 
        id="CIUS-VD-59" 
        flag="warning"> [CIUS-VD-59] BT-86 (Payment service provider identifier) - BT should contain a SWIFT/BIC (bank identifier code) according to structure defined in ISO 9362 (minimum length shall be 8- maximum length shall be 11 chars). 
      </assert>
   </rule>
  <!--  
    ID: BT-11
    Cardinality:  0..1
    Level:  1
    Business Term: Project reference	
    Description: The identification of the project the invoice refers to 
    eIGOR CIUS Reference: CIUS-VD-33

    Restriction:  BT maximum lenght shall be 15 chars
    -->
   <rule context="cac:ProjectReference">
      <assert test=  "string-length(cbc:ID) &lt;= 15" 
        id="CIUS-VD-33" 
        flag="warning"> [CIUS-VD-33] BT-11 (Project reference) -BT maximum length shall be 15 chars. 
      </assert>
   </rule>
  <!--  
    ID: BT-15
    Cardinality:  0..1
    Level: 1
    Business Term: 	Receiving advice reference
    Description:  An identifier of a referenced receiving advice.
    eIGOR CIUS Reference: CIUS-VD-36

    Restriction:  BT maximum lenght shall be 20 chars
    -->
  <rule context="cac:ReceiptDocumentReference">
      <assert test=  "string-length(cbc:ID) &lt;= 20" 
        id="CIUS-VD-36" 
        flag="warning"> [CIUS-VD-36] BT-15 (Receiving advice reference) -BT maximum length shall be 20 chars. 
      </assert>
   </rule>
  <!--  
    ID: BT-62
    Cardinality:  1..1
    Level:  2
    Business Term: 	Seller tax representative name
    Description:  The full name of the Seller's tax representative party.
    eIGOR CIUS Reference: CIUS-VD-19

    Restriction:  BT maximum lenght shall be 80 chars
    -->
  <rule context="cac:TaxRepresentativeParty/cac:PartyName">
      <assert test=  "string-length(cbc:Name) &lt;= 80" 
        id="CIUS-VD-19" 
        flag="warning"> [CIUS-VD-19] BT-62 (Seller tax representative name) -BT maximum length shall be 80 chars. 
      </assert>
   </rule>
  <!--  
    ID: BT-63
    Cardinality:  1..1
    Level: 2
    Business Term:  Seller tax representative VAT identifier
    Description:  The VAT identifier of the Seller's tax representative party.
    eIGOR CIUS Reference: CIUS-VD-42

    Restriction:  BT maximum lenght shall be 30 chars
    -->
   <rule context="cac:TaxRepresentativeParty/cac:PartyTaxScheme">
      <assert test=  "string-length(cbc:CompanyID) &lt;= 30" 
        id="CIUS-VD-42" 
        flag="warning"> [CIUS-VD-42] BT-63 (Seller tax representative VAT identifier) -BT maximum length shall be 30 chars. 
      </assert>
   </rule>
  <!--  
    ID: BT-116 - BT-117
    Cardinality:  1..1	- 1..1
    Level:  2 - 2
    Business Term:  VAT category taxable amount - VAT category tax amount
    Description:  Sum of all taxable amounts subject to a specific VAT category code and VAT category rate (if the VAT category rate is applicable). - The total VAT amount for a given VAT category.
    eIGOR CIUS Reference: CIUS-VD-66 - CIUS-VD-67

    Restriction:  
    BT minimum length shall be 4 maximum lenght shall be 15 chars
    BT minimum length shall be 4 maximum lenght shall be 15 chars
    -->
   <rule context="cac:TaxTotal/cac:TaxSubtotal">
      <assert test=  "string-length(cbc:TaxableAmount) &lt;= 15 and string-length(cbc:TaxableAmount) &gt;= 4 and matches(cbc:TaxableAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" 
        id="CIUS-VD-66" 
        flag="warning"> [CIUS-VD-66] BT-116 (VAT category taxable amount) - BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
      </assert>
      <assert test=  "string-length(cbc:TaxAmount) &lt;= 15 and string-length(cbc:TaxAmount) &gt;= 4 and matches(cbc:TaxAmount,'^[\-]?[0-9]{1,11}\.[0-9]{2}$')" 
        id="CIUS-VD-67" 
        flag="warning"> [CIUS-VD-67] BT-117 (VAT category tax amount) - BT minimum length shall be 4 maximum length shall be 15 chars, including two fraction digits. 
      </assert>
   </rule>
  <!--  
    ID: BT-120
    Cardinality:  0..1
    Level: 2
    Business Term:  VAT exemption reason text
    Description:  A textual statement of the reason why the amount is exempted from VAT or why no VAT is being charged  
    eIGOR CIUS Reference: CIUS-VD-68

    Restriction:  BT maximum lenght shall be 100 chars
    -->
   <rule context="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory">
      <assert test=  "string-length(cbc:TaxExemptionReason) &lt;= 100" 
        id="CIUS-VD-68" 
        flag="warning"> [CIUS-VD-68] BT-120 (VAT exemption reason text) - BT maximum length shall be 100 chars. 
      </assert>
   </rule>
 </pattern>
	 
	 
</schema>
