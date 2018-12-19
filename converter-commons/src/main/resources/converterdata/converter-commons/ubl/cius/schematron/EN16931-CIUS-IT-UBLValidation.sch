<?xml version="1.0" encoding="UTF-8"?>
<!-- 

            UBL syntax binding to the CIUS ITALIA  
            Created by Luigi Fabbro - Sara Facchinetti
            Updated by EeISI - European eInvoicing Standard in Italy Project
            Timestamp: 2018-Oct-29 11:00:00 +0100
            Release: 1.0.2 DRAFT

-->

<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        xmlns:cn="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"
        xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
        xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
        xmlns:UBL="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xs="http://www.w3.org/2001/XMLSchema"
        queryBinding="xslt2">

		<title>EN16931 - Eeisi Project - UBL CIUS IT</title>
    <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
    <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
    <ns prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"/>
    <ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
    <ns prefix="xs" uri="http://www.w3.org/2001/XMLSchema"/>

  <!-- Parameters for country identification -->
  <!-- ATTENZIONE - PEPPOL USA upper-case() e quindi considera it come IT   -->
  <let name="supplierCountry" value="if (/*/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) else 'XX'"/>
  <let name="customerCountry" value="if (/*/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) else 'XX'"/>
  <let name="deliveryCountry" value="if (/*/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode) then upper-case(normalize-space(/*/cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode)) else 'XX'"/>


  <pattern id="CIUS-IT-FATAL">  

    <rule context="//ubl:Invoice | //cn:CreditNote">
    <!--  
    ID: BT-1	
    Cardinality:  1..1	
    Business Term: Invoice number	
    Description:  A unique identification of the Invoice.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-32
    eEISI CIUS Referemce: BR-IT-010

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri   
     (ENG): BT maximum lenght shall be 20 digit

      CEN: 
      /BT-1
      
      UBL Syntax:
      BT-1: /Invoice/cbc:ID
            /CreditNote/cbc:ID

      CII Syntax:
      BT-1 (CII): /rsm:CrossIndustryInvoice/rsm:ExchangedDocument/ram:ID

      
    Error Message: BT maximum lenght shall be 20 chars with at least a digit.
    -->   
    <assert test=  "string-length(normalize-space(cbc:ID)) &lt;= 20 and matches(normalize-space(cbc:ID),'(^\p{IsBasicLatin}*[0-9]+\p{IsBasicLatin}*$)')" 
      id="BR-IT-010" 
      flag="fatal"> [BR-IT-010] BT-1 (Invoice number) - BT maximum length shall be 20 chars with at least a digit. 
    </assert>
    
    <!--  
    ID: BT-19	
    Cardinality:  0..1
    Level: 1
    Business Term:  Buyer accounting reference
    Description:  A textual value that specifies where to book the relevant data into the Buyer's financial accounts.    
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-38
    eEISI CIUS Referemce: BR-IT-080
    
    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri   
     (ENG): BT maximum lenght shall be 20 chars
     
      CEN: 
      /BT-19
      
      UBL Syntax:
      BT-19 (UBL): /Invoice/cbc:AccountingCost
                   /CreditNote/cbc:AccountingCost
      CII Syntax:
      BT-19 (CII): /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount/ram:ID 
    
    Error Message: 
    -->
      <assert test=  "matches(normalize-space(cbc:AccountingCost),'^\p{IsBasicLatin}{0,20}$')" 
        id="BR-IT-080" 
        flag="fatal"> [BR-IT-080] BT-19 (Buyer accounting reference) - BT maximum length shall be 20 chars. 
      </assert>
      
    </rule>

    <rule context="cac:AccountingCustomerParty/cac:Party">
    <!--  
    ID: BT-48 BT-46 BT-46-1	
    Cardinality:  0..1 - 0..1 - 0..1	
    Business Term: Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier	
    Description: The Buyer's VAT identifier (also known as Buyer VAT identification number). - An identifier of the Buyer. - The identification scheme identifier of the Buyer identifier. 
    (Previous Rule) eIGOR CIUS Reference: CIUS-BR-14
    EeISI CIUS Reference: BR-IT-160 (to check VAT or CF exists)

    EeISI Rule
      ITA: Se il valore dell’elemento BT-55 Buyer country code è ”IT” , 
            almeno uno degli elementi  BT-48 Buyer VAT identifier e BT-46 Buyer identifier deve essere valorizzato. 
            BT-46 Buyer identifier deve iniziare con IT:CF e la sua lunghezza deve essere compresa fra 17 e 22 caratteri
      ENG: If BT-55 = "IT", 
            then BT-48 or BT-46 should be indicated.  
            BT-46 shall starts with "IT:CF" and minimum lenght shall be 17 and maximum lenght shall be 22
    
      CEN:
      /BG-7/BT-46
      /BG-7/BT-46/@scheme
      /BG-7/BT-48
      
      UBL Syntax:
      BT-46:   /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID
               /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID
      BT-46-1: /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID/@schemeID
               /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID/@schemeID
      BT-48:   /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID
               /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID
  
      CII Syntax:
      BT-46:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:ID (GlobalID, if global identifier exists and can be stated in @schemeID, ID else)
              /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID
      BT-46-1 /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID/@schemeID
      BT-48:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedTaxRegistration/ram:ID with @schemeID="VA"
      
    Note:
    Dato che il CF può esserci solo per le aziende Italiane, nel caso di BT-55!= IT il BT-48 (VAT) è obbligatorio
    nel caso di BT-55 = IT, o si ha BT-48 (VAT) o il BT-46 (CF)
    
    Error Message: 1.4.1.1 is not mandatory in XMLPA (buyer) but VAT number or Fiscal code should be indicated as IT:CF in BT-46 when BT-55="IT"     
    -->
      <assert test=  "$customerCountry!='IT' or cac:PartyTaxScheme/cbc:CompanyID or cac:PartyIdentification/cbc:ID[starts-with(normalize-space(.),'IT:CF:')]" 
        id="BR-IT-160" 
        flag="fatal">[BR-IT-160-2] BT-48 BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) - If BT-55 = "IT", then BT-48 or BT-46 shall be indicated.       </assert>  

    <!--  
    ID: BT-49 - BT-49-1	
    Cardinality:  0..1 - 1..1	
    Level: 2
    Business Term:  Buyer electronic address	- Buyer electronic address identification scheme identifier
    Description:  Identifies the Buyer's electronic address to which a business document should be delivered - The identification scheme identifier of the Buyer electronic address.
    (Previous Rule) eIGOR CIUS Reference: CIUS-CA-2
    eEISI CIUS Reference: BR-IT-190
 
     EeISI Rule 
     (ITA): L'elemento BT-49 Buyer electronic address deve contenere la PEC del destinatario della fattura, 
            oppure l’indice IPA oppure il codice destinatario. 
            Di conseguenza per l'elemento BT-49-1 Buyer electronic address identification scheme identifier sono previsti i valori IT:PEC, IT:IPA oppure IT:CODDEST  
     (ENG): BT-49 shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario. 
            BT-49-1=IT:PEC or IT:IPA or IT:CODDEST

      CEN:
      /BG-7/BT-49
      /BG-7/BT-49/@scheme
      
      UBL Syntax:
      BT-49:  	/Invoice/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID
                /CreditNote/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID
      BT-49-1:  /Invoice/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID
                /CreditNote/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID                          
  
      CII Syntax:
      BT-49:  	/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID
      BT-49-1:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID/@schemeID
     
   Error Message:
   -->
      <assert test=  "$customerCountry!='IT' or (exists(cbc:EndpointID) and 
        (cbc:EndpointID[normalize-space(@schemeID) = 'IT:CODDEST'] 
        or cbc:EndpointID[normalize-space(@schemeID) = 'IT:PEC'] 
        or cbc:EndpointID[normalize-space(@schemeID) = 'IT:IPA'] ))" 
        id="BR-IT-190" 
        flag="fatal"> [BR-IT-190] BT-49 BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario. BT-49-1=IT:PEC or IT:IPA or IT:CODDEST 
      </assert>      
      
    <!--  
    ID: BT-49 - BT-49-1	
    Cardinality:  0..1 - 1..1	
    Level: 2
    Business Term:  Buyer electronic address	- Buyer electronic address identification scheme identifier
    Description:  Identifies the Buyer's electronic address to which a business document should be delivered - The identification scheme identifier of the Buyer electronic address.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-97-1 CIUS-VD-97-2 CIUS-VD-97-3
    eEISI CIUS Referemce: BR-IT-200-1, BR-IT-200-2, BR-IT-200-3

     EeISI Rule 
     (ITA): Se il valore dell’elemento BT-55 Buyer country code è ”IT”, 
            se l'elemento BT-49-1 Buyer electronic address identification scheme identifier contiene il valore "IT:PEC", 
                la lunghezza dell'elemento BT-49 Buyer electronic address deve essere compresa fra 7 e 256 caratteri. 
            Altrimenti, se l'elemento BT-49-1 Buyer electronic address identification scheme identifier contiene il valore "IT:IPA", 
                la lunghezza dell'elemento BT-49 Buyer electronic address deve essere di 6 caratteri. 
            Altrimenti, se l'elemento BT-49-1 Buyer electronic address identification scheme identifier contiene il valore "IT:CODDEST", 
                la lunghezza dell'elemento BT-49 Buyer electronic address deve essere di 7 caratteri    
     (ENG): If BT-55 = "IT", 
        if BT-49-1= IT:PEC schema then BT-49  minimum length shall be 7 maximum lenght shall be 256 chars
        else if BT-49-1 = IT:IPA schema then BT-49 lenght shall be 6 chars
        else if BT-49-1 = IT:CODDEST schema then BT-49 lenght shall be 7 chars

      CEN:
      /BG-7/BT-49
      /BG-7/BT-49/@scheme
      
      UBL Syntax:
      BT-49:  	/Invoice/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID
                /CreditNote/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID
      BT-49-1:  /Invoice/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID
                /CreditNote/cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID                          
  
      CII Syntax:
      BT-49:  	/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID
      BT-49-1:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID/@schemeID
  
    Error Message:
    -->      
      <assert test=  "$customerCountry!='IT' or  not(cbc:EndpointID[normalize-space(@schemeID) = 'IT:PEC']) 
        or ( (string-length(normalize-space(cbc:EndpointID)) &gt;= 7 and string-length(normalize-space(cbc:EndpointID)) &lt;= 256) and matches(normalize-space(cbc:EndpointID),'^.+@.+[.]+.+$') )" 
        id="BR-IT-200-1" 
        flag="fatal"> [BR-IT-200-1] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) -If BT-49-1=IT:PEC schema then BT-49 shall be a PEC (email) address and  length shall be between 7 and 256 character 
      </assert>
      <assert test=  "$customerCountry!='IT' or  not(cbc:EndpointID[normalize-space(@schemeID) = 'IT:IPA']) 
        or ( matches(normalize-space(cbc:EndpointID),'^[A-Z0-9]{6}$') )" 
        id="BR-IT-200-2" 
        flag="fatal"> [BR-IT-200-2] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) =IT:IPA schema then BT-49 shall be a IPA code and maximum length shall be 6 chars 
      </assert> 
      <assert test=  "$customerCountry!='IT' or  not(cbc:EndpointID[normalize-space(@schemeID) = 'IT:CODDEST']) 
        or ( matches(normalize-space(cbc:EndpointID),'^[A-Z0-9]{7}$') )" 
        id="BR-IT-200-3" 
        flag="fatal"> [BR-IT-200-3] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier)=IT:CODDEST schema then BT-49 and maximum length shall be 7 chars. 
      </assert>      
    </rule>
 
    <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification">     
      <!--  
      ID: BT-46 BT-46-1	
      Cardinality:  0..1 - 0..1	
      Business Term: Buyer identifier - Buyer identifier identification scheme identifier	
      Description: An identifier of the Buyer. - The identification scheme identifier of the Buyer identifier. 
      (Previous Rule) eIGOR CIUS Reference: CIUS-VD-100-1 CIUS-VD-100-2 CIUS-VD-100-3
      EeISI CIUS Reference: BR-IT-160 (to check CF format)
    
  EeISI Rule
      ITA: Se il valore dell’elemento BT-55 Buyer country code è ”IT” , 
            almeno uno degli elementi  BT-48 Buyer VAT identifier e BT-46 Buyer identifier deve essere valorizzato. 
            BT-46 Buyer identifier deve iniziare con IT:CF e la sua lunghezza deve essere compresa fra 17 e 22 caratteri
      ENG: If BT-55 = "IT", 
            then BT-48 or BT-46 should be indicated.  
            BT-46 shall starts with "IT:CF" and minimum lenght shall be 17 and maximum lenght shall be 22
  
        CEN:
        /BG-7/BT-46
        /BG-7/BT-46/@scheme
        
        UBL Syntax:
        BT-46:   /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID
                 /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID
        BT-46-1: /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID/@schemeID
                 /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID/@schemeID
    
        CII Syntax:
        BT-46:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:ID (GlobalID, if global identifier exists and can be stated in @schemeID, ID else)
                /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID
        BT-46-1 /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID/@schemeID
    
   Error Message: BT46-1=IT:CF then BT-46 should a CodiceFiscale and minimum length shal be 11 and maximum lenght shall be 16 
   -->      
      <assert test="$customerCountry != 'IT' or not(cbc:ID[starts-with(normalize-space(.), 'IT:CF')]) or matches( normalize-space(cbc:ID[starts-with(normalize-space(.), 'IT:CF')]), '(^IT:CF:[A-Z0-9]{11,16}$)')"
        id="BR-IT-160-1" 
        flag="fatal"> [BR-IT-160] BT-46 (Buyer identifier) - BT-46 minimum lenght 17 and maximum lenght shall be 22 starting with "IT:CF: ". 
      </assert>
    </rule>
    
    <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity[starts-with(cbc:CompanyID,'IT:EORI:')]">
    <!--  
    ID: BT-47 BT-47-1	
    Cardinality:  0..1 - 0..1
    Level: 2 2 2 
    Business Term: Buyer legal registration identifier - Buyer legal registration identifier identification scheme identifier	
    Description: An identifier issued by an official registrar that identifies the Buyer as a legal entity or person. - The identification scheme identifier of the Buyer legal registration identifier. 
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-107
    eEISI CIUS Referemce: BR-IT-170
 
     EeISI Rule 
     (ITA): Se il valore dell’elemento BT-55 Buyer country code è ”IT”, 
            se l'elemento BT-47 Buyer legal registration identifier inizia con "IT:EORI:", 
            la lunghezza dell'elemento BT-47 Buyer legal registration identifier deve essere compresa fra 21 e 25 caratteri
     (ENG): If BT-55 = "IT", if BT-47 starts with "IT:EORI:" then BT-47 minimum lenght shall be 21 and maximum lenght shall be 25
      

        CEN:
        /BG-7/BT-47
        /BG-7/BT-47/@scheme
        
        UBL Syntax:
        BT-47:      /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID
                    /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID
        BT-47-1:    /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID 
                    /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID
        
        CII Syntax:
        BT-47:      /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization/ram:ID
        BT-47-1:    /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/@schemeID

    Error Message:
    -->
      <assert test=  "($customerCountry!='IT') or (string-length(normalize-space(cbc:CompanyID)) &gt;= 21 and string-length(normalize-space(cbc:CompanyID)) &lt;=25)" 
        id="BR-IT-170" 
        flag="fatal"> [BR-IT-170] BT-47 Buyer legal registration identifier. If BT-55 = "IT", if BT-47 starts with "IT:EORI:" then BT-47 minimum lenght shall be 21 and maximum lenght shall be 25 
      </assert>
    </rule>    
    
    <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme">
    <!--  
    ID: BT-48	
    Cardinality:  0..1
    Level: 2
    Business Term:  Buyer VAT identifier	
    Description:  The Buyer's VAT identifier (also known as Buyer VAT identification number).  
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-43
    eEISI CIUS Reference: BR-IT-180

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 30 caratteri
     (ENG): BT maximum lenght shall be 30 chars

      CEN:
      /BG-7/BT-48      
      
      UBL Syntax:
      BT-48 (UBL):   /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID
                     /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID
  
      CII Syntax:
      BT-48 (CII):   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedTaxRegistration/ram:ID

    Error Message:
    -->       
      <assert test=  "string-length(normalize-space(cbc:CompanyID)) &lt;= 30" 
        id="BR-IT-180" 
        flag="fatal"> [BR-IT-180] BT-48 (Buyer VAT identifier) - BT maximum length shall be 30 chars. 
      </assert>
    </rule>
        
    <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress[cac:Country/cbc:IdentificationCode='IT']">      
      <!--  
    ID: BT-50 - BT-52 - BT-53	
    Cardinality:  0..1	
    Business Term: Buyer address line 1 - Buyer city - Buyer post code 	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.  
    (Previous Rule) eIGOR CIUS Reference: CIUS-CA-11-1 - CIUS-CA-11-2 - CIUS-CA-11-3
    eEISI CIUS Referemce: BR-IT-210-1, BR-IT-210-2, BR-IT-210-3

     EeISI Rule 
     (ITA): Se il valore dell’elemento BT-55 Buyer country code è ”IT”, gli elementi devono essere obbligatoriamente valorizzati     
     (ENG): If BT-55 = "IT", BTs should be mandatory

     UBL Syntax:
     BT-50: /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName
            /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName
     BT-52: /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName
            /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName
     BT-53: /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone
            /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone

     CII Syntax:
     BT-50: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:LineOne
     BT-52: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CityName
     BT-53: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:PostcodeCode 

     CEN:
     /BG-7/BG-8/BT-50
     /BG-7/BG-8/BT-52
     /BG-7/BG-8/BT-53
    -->
      <assert test=  "cbc:StreetName" 
        id="BR-IT-210-1" 
        flag="fatal"> [BR-IT-210-1] BT-50 (Buyer address line 1) - Fields are mandatory in Italy. 
      </assert>
      <assert test=  "cbc:CityName" 
        id="BR-IT-210-2" 
        flag="fatal"> [BR-IT-210-2] BT-52 (Buyer city) - Fields are mandatory in Italy. 
      </assert>
      <assert test=  "matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')" 
        id="BR-IT-210-3" 
        flag="fatal"> [BR-IT-210-3] BT-53 (Buyer post code) - Fields are mandatory in Italy. 
      </assert>

    <!--  
    ID: BT-54	
    Cardinality:  0..1	
    Business Term:  Buyer country subdivision 	
    Description:  The subdivision of a country.  
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-30 - CIUS-VD-48
    eEISI CIUS Referemce: BR-IT-220

    EeISI Rule 
     (ITA): Se l'elemento BT-55 Buyer country code ha valore "IT", 
            per l'elemento BT-54 Buyer country subdivision deve essere utilizzato uno dei valori della lista delle province italiane. 
            Altrimenti l'informazione è riportata in allegato   
     (ENG): If BT-55=IT, then BR-54 shall be coded according to Italian province list else save in attachment

      CEN:
      /BG-7/BG-8/BT-54
      
      UBL Syntax:
      BT-54: /Invoice/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity
             /CreditNote/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity
      
      CII Syntax:
      BT-54: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CountrySubDivisionName

    Nota: per controllo puntuale su provincie modificare con:  contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',cbc:CountrySubentity )    
    Error Message:
    -->     
      <assert test=  "not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')" 
        id="BR-IT-220" 
        flag="fatal"> [BR-IT-220] BT-54 (Buyer country subdivision) - BT maximum length shall be 2 chars and shall be coded according to Italian province list else save in attachment. . 
      </assert> 
    </rule>

    <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity">
      <!--  
    ID: BT-30	- BT-30-1
    Cardinality:  0..1	0..1
    Business Term:  Seller legal registration identifier - Seller legal registration identifier 
                    identification scheme identifier - The identification scheme identifier of the Seller legal registration identifier.	
    Description:  An identifier issued by an official registrar that identifies the Seller as a legal entity or person
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-102-1 CIUS-VD-102-2
    EeISI CIUS Reference: BR-IT-110
  
    EeISI Rule
      ITA:
        Se il valore dell’elemento BT-40 Seller country code è ”IT”, 
        se il valore dell'elemento BT-30 Seller legal registration identifier comincia con "IT:REA:", 
        la sua lunghezza deve essere compresa fra 10 e 29 caratteri, i primi due dei quali indicano la provincia italiana.
      ENG:
        If BT-40 = "IT", 
        if BT-30 starts with "IT:REA:" 
        then BT-30 minimum lenght shall be 10 and maximum lenght shall be 29 (first two chars indicate the italian province code)

     CEN:
     /BG-4/BT-30
     /BG-4/BT-30/@scheme

     UBL Syntax: 
     BT-30:   /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID
              /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID
     BT-30-1: /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID
              /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID

     CII Syntax:
     BT-30:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID
     BT-30-1: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/@schemeID

     Ufficio-NumeroREA (XML-PA) Pattern: [A-Z]{2}[\p{IsBasicLatin}]{1,20}
    
    Se si vuole fa controllo su Provincia aggiungere
      contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',substring(cbc:CompanyID,1,2) )    
        
    Restriction:
    BT-30-1=IT:REA then BT-30 minimum lenght 3 and maximum lenght shall be 22 (first two chars indicate the italian province code)

     Temporary rules inserted waiting for availability of these schemes in ISO 6523 
     BT-30 has cardinality 0..1 
     It is not required to test multiple instances
     -->
      <assert
        test="every $CompanyID in (cbc:CompanyID[starts-with(normalize-space(.), 'IT:REA:')])
        satisfies ($supplierCountry != 'IT' or matches(normalize-space($CompanyID), '(^IT:REA:[A-Z]{2}:[\p{IsBasicLatin}]{1,20}$)'))"
        id="BR-IT-110" 
        flag="fatal"> [BR-IT-110] BT-30 (Seller legal registration identifier) - BT-30 minimum lenght 10 and maximum lenght shall be 30 starting with "IT:REA:" and shall be represented as "IT:REA:Ufficio:NumeroREA". 
      </assert>
      
    </rule>
    
    <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification">
    <!--  
    ID: BT-29 BT-29-1	
    Cardinality:  0..n	0..1
    Business Term:  Seller identifier - Seller identifier identification scheme identifier 	
    Description:  An identification of the Seller. - The identification scheme identifier of the Seller identifier.  

    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-101-1 CIUS-VD-101-2 CIUS-VD-101-3
    EeISI CIUS Reference: BR-IT-100 (BR-IT-100-1, BR-IT-100-2, ...)

    EeISI Rule (ITA):
      Se il valore dell’elemento BT-40 Seller country code è ”IT”, 
      se il valore dell'elemento BT-29 Seller identifier comincia con "IT:EORI:",  la sua lunghezza deve essere compresa fra 21 e 25 caratteri. 
      Altrimenti, 
      se il valore dell'elemento BT-29 Seller identifier comincia con "IT:ALBO:",  la sua lunghezza non può superare i 128 caratteri.

    CEN:
    /BG-4/BT-29
    /BG-4/BT-29/@scheme

    CII Syntax:
    BT-29:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:ID
            /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID 
    BT-29-1:/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID/@schemeID 
    
    CII Specific: check carefully syntax mapping - use of ram:GlobalID or ram:ID
    => GlobalID, if global identifier exists and can be stated in @schemeID, ID else ram:ID


    Attention Point UBL:
    BT-90: /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID with @schemeID = 'SEPA' when the identifier refers to the Seller.
            /Invoice/cac:PayeeParty/cac:PartyIdentification/cbc:ID with @schemeID = 'SEPA' when the identifier refers to the Payee
            /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID with @schemeID = 'SEPA'
            /CreditNote/cac:PayeeParty/cac:PartyIdentification/cbc:ID with @schemeID = 'SEPA'
    Nello schematron CEN
    <rule context="cac:PartyIdentification/cbc:ID[@schemeID]" flag="fatal">
    <assert
      test="((not(contains(normalize-space(@schemeID), ' ')) and contains(' 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025 0026 0027 0028 0029 0030 0031 0032 0033 0034 0035 0036 0037 0038 0039 0040 0041 0042 0043 0044 0045 0046 0047 0048 0049 0050 0051 0052 0053 0054 0055 0056 0057 0058 0059 0060 0061 0062 0063 0064 0065 0066 0067 0068 0069 0070 0071 0072 0073 0074 0075 0076 0077 0078 0079 0080 0081 0082 0083 0084 0085 0086 0087 0088 0089 0090 0091 0092 0093 0094 0095 0096 0097 0098 0099 0100 0101 0102 0103 0104 0105 0106 0107 0108 0109 0110 0111 0112 0113 0114 0115 0116 0117 0118 0119 0120 0121 0122 0123 0124 0125 0126 0127 0128 0129 0130 0131 0132 0133 0134 0135 0136 0137 0138 0139 0140 0141 0142 0143 0144 0145 0146 0147 0148 0149 0150 0151 0152 0153 0154 0155 0156 0157 0158 0159 0160 0161 0162 0163 0164 0165 0166 0167 0168 0169 0170 0171 0172 0173 0174 0175 0176 0177 0178 0179 0180 0183 0184 0190 0191 0192 0193', concat(' ', normalize-space(@schemeID), ' '))))  or ((not(contains(normalize-space(@schemeID), ' ')) and contains(' SEPA ', concat(' ', normalize-space(@schemeID), ' '))) and ((ancestor::cac:AccountingSupplierParty) or (ancestor::cac:PayeeParty)))" 
      id="BR-CL-10"
      flag="fatal">[BR-CL-10]-Any identifier identification scheme identifier MUST be coded using one of the ISO 6523 ICD list.</assert>
    </rule>

    Note interne:
    BT-90 e BT-29 si distinguono dal fatto che lo SchemID sia SEPA
    Essendo BT-90 di cardinalità 0..1 non si comprende se questo sia controllato negli schematron CEN
    Assumption (PER SEMPLICITA'): anche per il BT-29 dovremmo controllare di non avere lo stessa SchemeID riportato più volte. Assumiamo sia così,
                                  altrimenti per il mapping si prenderà solo uno dei valori (nel caso fossero anche differenti)


    As schemeID doesn't exist using ram:ID, no test can be done as it is not possible to understand the schemeID
    => we need a default schemeID in case of ram:ID to agree on the identifier

    Assumption: not permitted to have the same schemeID multiple time

    Using schema identifer BT-29-1 (CURRENTLY NOT AVAILABLE)
      BT-29-1=IT:EORI then BT-29 minimum lenght 13 and maximum lenght shall be 17 
      BT-29-1=IT:ALBO then BT-29 maximum lenght shall be 121 (AlboProfessionale:NumeroIscrizioneAlbo)

    Temporary rules (PRODUCTION)
      If BT-29 starts with IT:EORI then BT-29 minimum lenght 21 and maximum lenght shall be 25 (IT:EORI:CodiceEORI)
      (ADDED) If BT-29 starts with IT:ALBO then BT-29 maximum lenght shall be 128 (IT:ALBO:AlboProfessionale:NumeroIscrizioneAlbo)
     Temporary rules inserted waiting for availability of these schemes in ISO 6523 
     BT-29 has cardinality 0..n 
     It is  required to test multiple instances
     
      -->        
      
      <!-- EORI -->
      <assert
        test="every $cbcID in (cbc:ID[starts-with(normalize-space(.), 'IT:EORI:')])
        satisfies (($supplierCountry != 'IT') or ((string-length(normalize-space($cbcID)) &gt;= 21) and (string-length(normalize-space($cbcID)) &lt;= 25)))"
        id="BR-IT-100-1" 
        flag="fatal"> [BR-IT-100-1] BT-29 (Seller identifier) - BT-29 minimum lenght 21 and maximum lenght shall be 25 starting with "IT:EORI ". 
      </assert>
      
      <!-- ALBO -->
      <!--  Controllo che ogni sottostringa sia meno di 60 caratteri - verifico inoltre che ci siano solo due stringhe separate dal carattere divisore ":"   -->
      <assert
        test="every $SubID in cbc:ID[starts-with(normalize-space(.), 'IT:ALBO:')]
        satisfies (($supplierCountry != 'IT') or (matches(normalize-space($SubID), '(^IT:ALBO:[\p{IsBasicLatin} -[:]]{1,60}:[\p{IsBasicLatin}\p{IsLatin-1Supplement} -[:]]{1,60}$)')))"
        id="BR-IT-100-2" 
        flag="fatal"> [BR-IT-100-2] BT-29 (Seller identifier) - BT-29 starting with "IT:ALBO has the format IT:ALBO:AlboProfessionale(1-60chars):NumeroIscrizioneAlbo(1-60chars) - (:) colon is permitted only as separator". 
      </assert>
      
    </rule>
    
    <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']">
      <!--  
      ID: BT-32	
      Cardinality:  0..1
      Level:  2
      Business Term:  Seller tax registration identifier	
      Description:  The local identification (defined by the Seller’s address) of the Seller for tax purposes or a reference that enables the Seller to state his registered tax status.  
      (Previous Rule) eIGOR CIUS Reference: CIUS-VD-99
      EeISI CIUS Reference: BR-IT-130
  
      EeISI Rule (ITA):
        (ITA): Se il valore dell’elemento BT-40 Seller country code è ”IT”, la lunghezza dell'elemento BT-32 Seller tax registration identifier deve essere compresa fra 17 e 22 caratteri.
        (ENG): If BT-40 = "IT", then BT-32 minimum lenght shall be 17 and maximum lenght shall be 22 
        => FATAL

      CEN:
      /BG-4/BT-32
        
      UBL Syntax:
      BT-32:   /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID with cac:TaxScheme/cbc:ID ! = “VAT”
               /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID with cac:TaxScheme/cbc:ID ! = “VAT”

      CII Syntax:
      BT-32:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID with @schemeID="FC"
      
      CodiceFiscale (XML-PA) pattern: [A-Z0-9]{11,16}

      Note interne (UBL):
      DOVREBBE ESSERCI SOLO UN BT-32
      /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID with cac:TaxScheme/cbc:ID ! = “VAT”
          <cac:PartyTaxScheme>
              <cbc:CompanyID>IT07945211006</cbc:CompanyID>
              <cac:TaxScheme>
                  <cbc:ID>NOVAT</cbc:ID>
              </cac:TaxScheme>
          </cac:PartyTaxScheme>
  
      lo schematron non sembra impedire il fatto che ci sia una riga senza TaxScheme ma non sembra poi "catalogarla" correttamente -
      Non mi sembra corretta.
            <assert test="(count(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID!='VAT']/cbc:ID) &lt;= 1)"
                  flag="warning"
                  id="UBL-SR-13">[UBL-SR-13]-Seller tax registration shall occur maximum once</assert>
  -->
      <assert
        test="$supplierCountry != 'IT' or matches(normalize-space(.[normalize-space(cac:TaxScheme/cbc:ID) != 'VAT']/cbc:CompanyID), '^[A-Z0-9]{11,16}$')"
        id="BR-IT-130" 
        flag="fatal"> [BR-IT-130] BT-32 (Seller tax registration identifier) - then BT-32 minimum lenght shall be 11 and maximum lenght shall be 16. 
      </assert>

    </rule>
    
    <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme[normalize-space(cac:TaxScheme/cbc:ID) = 'VAT']">         
    <!--  
    ID: BT-31	
    Cardinality:  0..1
    Level:  2
    Business Term:  Seller VAT identifier 	
    Description:  The Seller's VAT identifier (also known as Seller VAT identification number).  
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-41
    EeISI CIUS Reference: BR-IT-120

    EeISI Rule:
      (ITA) La lunghezza dell'elemento non può superare i 30 caratteri
      (ENG) BT maximum lenght shall be 30 chars

    CEN:
    /BG-4/BT-31
    
    UBL Syntax:
    BT-31: /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID     with cac:TaxScheme/cbc:ID = “VAT”
           /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID  with cac:TaxScheme/cbc:ID = “VAT”

    CII Syntax:
    BT-31: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedTaxRegistration/ram:ID with @schemeID="VA"

    Error Message:
    -->
      <assert test=  "$supplierCountry!='IT' or string-length(normalize-space(cbc:CompanyID)) &lt;= 30 " 
        id="BR-IT-120" 
        flag="fatal"> [BR-IT-120] BT-31 (Seller VAT identifier) - BT maximum length shall be 30 chars. 
      </assert>      
    </rule>
    
    <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress[normalize-space(cac:Country/cbc:IdentificationCode)='IT']">
    <!--  
    ID: BT-35 - BT-37 - BT-38	
    Cardinality:  0..1	
    Business Term: Seller address line 1 - Seller city - Seller post code 	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.  
    (Previous Rule) eIGOR CIUS Reference: CIUS-CA-10-1 - CIUS-CA-10-2 - CIUS-CA-10-3
    EeISI CIUS Reference: BR-IT-140-1 - BR-IT-140-2 - BR-IT-140-3

    EeISI Rule:
      (ITA) Se il valore dell’elemento BT-40 Seller country code è ”IT”, gli elementi devono essere obbligatoriamente valorizzati
      (ENG) If BT-40 = "IT", BTs should be mandatory

     CEN:
     /BG-4/BG-5/BT-35
     /BG-4/BG-5/BT-37
     /BG-4/BG-5/BT-38
     
    UBL Syntax
    BT-35: /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName
           /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName
    BT-37: /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName
           /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName
    BT-38: /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone
           /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone

    CII Syntax
    BT-35: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:LineOne
    BT-37: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CityName
    BT-38: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:PostcodeCode

    Error Message:
    -->

      <assert test=  "not($supplierCountry = 'IT') or cbc:StreetName" 
        id="BR-IT-140-1" 
        flag="fatal"> [BR-IT-140-1] BT-35 (Seller address line 1) - Fields are mandatory in Italy.
      </assert>
      <assert test=  "not($supplierCountry = 'IT') or cbc:CityName" 
        id="BR-IT-140" 
        flag="fatal"> [BR-IT-140-2] BT-37 (Seller city) - Fields are mandatory in Italy. 
      </assert>
      <assert test=  "not($supplierCountry = 'IT') or matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')" 
        id="BR-IT-140-3" 
        flag="fatal"> [BR-IT-140-3] BT-38 (Seller post code) - Fields are mandatory in Italy.
      </assert>
      <!--  
    ID: BT-39	
    Cardinality:  0..1	
    Business Term:  Seller country subdivision 	
    Description:  The subdivision of a country.  
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-47 CIUS-VD-29
    eEISI CIUS Referemce: BR-IT-150

    EeISI Rule 
     (ITA): Se l'elemento BT-40 Seller country code ha valore "IT", 
             per l'elemento BT-39 Seller country subdivision deve essere utilizzato uno dei valori della lista delle province italiane. 
             Altrimenti l'informazione è riportata in allegato   
     (ENG): If BT-40=IT, then BT-39 shall be coded according to Italian province list else save in attachment

     CEN:
     /BG-4/BG-5/BT-39

     UBL Syntax:
     BT-39: /Invoice/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity
            /CreditNote/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity

     CII Syntax:
     BT-39: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountrySubDivisionName


    Nota: per controllo puntuale su provincie modificare con:  contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',cbc:CountrySubentity )     
    Error Message:
    --> 
      <assert test=  "not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')" 
        id="BR-IT-150" 
        flag="fatal"> [BR-IT-150] BT-39 (Seller country subdivision) - BT maximum length shall be 2 chars and shall be coded according to Italian province list else save in attachment. 
      </assert>   
      
      
    </rule>

    <rule context="cac:AdditionalDocumentReference[normalize-space(cbc:DocumentTypeCode)!='130' or not(exists(cbc:DocumentTypeCode))]">        
    <!--  
    ID: BT-125 - BT-124
    Cardinality:  0..1	- 0..1
    Level: 2 - 2
    Business Term: Attached document - External document location	
    Description:  An attached document embedded as binary object or sent together with the invoice. - The URL (Uniform Resource Locator) that identifies where the external document is located.
    (Previous Rule) eIGOR CIUS Reference: CIUS-CA-71
    eEISI CIUS Referemce: BR-IT-360

     EeISI Rule 
     (ITA): Se l'elemento l’elemento BT-122 Supporting document reference è valorizzato, 
            è obbligatorio valorizzare almeno uno degli elementi BT-124 External document location e BT-125 Attached document   
     (ENG): If BT-122 not empty then BT-124 or BT-125 should be mandatory 

    CEN:
    /BG-24/BT-122
    /BG-24/BT-124
    /BG-24/BT-125
    /BG-24/BT-125/@mime
    /BG-24/BT-125/@filename
    
    UBL Syntax:
    BT-122:   /Invoice/cac:AdditionalDocumentReference/cbc:ID
              /CreditNote/cac:AdditionalDocumentReference/cbc:ID
    BT-124:   /Invoice/cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:URI
              /CreditNote/cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:URI
    BT-125:   /Invoice/cac:AdditionalDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject
              /CreditNote/cac:AdditionalDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject
    BT-125-1: /Invoice/cac:AdditionalDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode
              /CreditNote/cac:AdditionalDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode
    BT-125-2: /Invoice/cac:AdditionalDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename
              /CreditNote/cac:AdditionalDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename
    
    UBL Specific:
    BT-18 is mapped in AdditionalDocument with the condition cac:AdditionalDocumentReference/cbc:DocumentTypeCode=’130’ 
    BT-18: //cac:AdditionalDocumentReference/cbc:ID with cbc:DocumentTypeCode=130

    CII Syntax:
    BT-122:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:IssuerAssignedID
              Use for "ADDITIONAL SUPPORTING DOCUMENTS" with TypeCode "916" (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:TypeCode)
    BT-124:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:URIID
    BT-125:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:AttachmentBinaryObject
    BT-125-1: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:AttachmentBinaryObject/@mimeCode
    BT-125-2: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:AttachmentBinaryObject/@filename

    Error Message:
    --> 
      <assert test=  "cac:Attachment/cac:ExternalReference/cbc:URI or cac:Attachment/cbc:EmbeddedDocumentBinaryObject" 
        id="BR-IT-360" 
        flag="fatal"> [BR-IT-360] BT-124 (External document location) BT-125 (Attached document) - If BT-122 (Supporting document reference) not empty then BT-124 or BT-125 shall be mandatory. 
      </assert>
    </rule>

    <rule context="//ubl:Invoice/cac:AllowanceCharge/cbc:Amount | //cn:CreditNote/cac:AllowanceCharge/cbc:Amount">
    <!--  
    ID: BT-92 - BT-99	
    Cardinality:  0..1
    Level:  2 - 2
    Business Term:  Document level allowance amount - Document level charge amount 	
    Description:  The amount of an allowance, without VAT. - The amount of a charge, without VAT.  
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-64
    eEISI CIUS Referemce: BR-IT-290

    EeISI Rule 
     (ITA): La lunghezza degli elementi deve essere compresa fra 4 e 15 caratteri  
     (ENG): BT minimum length shall be 4 maximum lenght shall be 15 chars

    CEN:
    /BG-20/BT-92
    /BG-21/BT-99

    UBL Syntax:
    BT-92: /Invoice/cac:AllowanceCharge/cbc:Amount    with cbc:ChargeIndicator = 'false'
           /CreditNote/cac:AllowanceCharge/cbc:Amount with cbc:ChargeIndicator = 'false'
    BT-99: /Invoice/cac:AllowanceCharge/cbc:Amount    with cbc:ChargeIndicator = 'true'
           /CreditNote/cac:AllowanceCharge/cbc:Amount with cbc:ChargeIndicator = 'true'

    CII Syntax:
    BT-92: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ActualAmount
            with ChargeIndicator=false (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge)
    BT-99: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ActualAmount
            with ChargeIndicator=true (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge)

    PrezzoUnitario/PrezzoTotale (XML-PA) pattern: [\-]?[0-9]{1,11}\.[0-9]{2,8}
	  NB:
	  In CEN sono amount e quindi al massimo 2 decimali
	  la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali
    
    Error Message:
    -->    
      <assert test=  "matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" 
        id="BR-IT-290" 
        flag="fatal">  [BR-IT-290] BT-92 BT-99 (Document level allowance amount - Document level charge amount) - BT maximum length shall be 15, including two fraction digits.  
      </assert>
    </rule>
    
    <rule context="//ubl:Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:Amount | //cn:CreditNote/cac:CreditNoteLine/cac:AllowanceCharge/cbc:Amount">
      <!--  
    ID: BT-136 - BT-141
    Cardinality:  (opz.) 1..1	- 1..1
    Level:  3 - 3
    Business Term:  Invoice line allowance amount - Invoice line charge amount 	
    Description:  The amount of an allowance, without VAT. - The amount of a charge, without VAT.  
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-80
    eEISI CIUS Referemce: BR-IT-420

    EeISI Rule 
     (ITA): La lunghezza degli elementi deve essere compresa fra 4 e 21 caratteri  
     (ENG): BT minimum length shall be 4 maximum lenght shall be 21 chars

    CEN:
    /BG-25/BG-27/BT-136
    /BG-25/BG-28/BT-141

    UBL Syntax:
    BT-136: /Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:Amount with cbc:ChargeIndicator = 'false'
            /CreditNote/cac:CreditNoteLine/cac:AllowanceCharge/cbc:Amount with cbc:ChargeIndicator = 'false'
    BT-141: /Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:Amount with cbc:ChargeIndicator = 'true'
            /CreditNote/cac:CreditNoteLine/cac:AllowanceCharge/cbc:Amount with cbc:ChargeIndicator = 'true'

    CII Syntax:
    BT-136: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ActualAmount
            with ChargeIndicator=false (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge )
            
    BT-141: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ActualAmount
            with ChargeIndicator=true (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge)

    PrezzoUnitario/PrezzoTotale (XML-PA) pattern: [\-]?[0-9]{1,11}\.[0-9]{2,8}
	  NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali

    Error Message:
    -->
      
      <assert test=  "matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" 
        id="BR-IT-420" 
        flag="fatal">  [BR-IT-420] BT-136 BT-141 (Invoice line allowance amount - Invoice line charge amount) - BT maximum length shall be 15, including two fraction digits.
      </assert>  
    </rule> 
       
    <rule context="cac:BillingReference/cac:InvoiceDocumentReference">
      <!--  
    ID: BT-25	
    Cardinality:  (opz.) 1..1
    Level: 2
    Business Term: Preceding Invoice number -  	
    Description:  The identification of an Invoice that was previously sent by the Seller.  
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-40
    eEISI CIUS Reference: BR-IT-090

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri   
     (ENG): BT maximum lenght shall be 20 chars   

    CEN:
    /BG-3/BT-25
    
    UBL Syntax:
    BT-25: /Invoice/cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID
           /CreditNote/cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID

    CII Syntax:
    BT-25: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceReferencedDocument/ram:IssuerAssignedID

    Error Message:
    -->
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{1,20}$')" 
        id="BR-IT-090" 
        flag="fatal"> [BR-IT-090] BT-25 (Preceding Invoice number)-BT maximum length shall be 20 chars. 
      </assert>     
    </rule>
    
    <rule context="cac:OrderReference">
    <!--  
    ID: BT-13
    Cardinality:  0..1
    Level:  1
    Business Term: 	Purchase order reference
    Description:  An identifier of a referenced purchase order, issued by the Buyer.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-35
    eEISI CIUS Referemce: BR-IT-040

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri   
     (ENG): BT maximum lenght shall be 20 chars

    CEN:
    /BT-13
    
    UBL Syntax:
    BT-13:  /Invoice/cac:OrderReference/cbc:ID
            /CreditNote/cac:OrderReference/cbc:ID
           
    CII Syntax:
    BT-13: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerOrderReferencedDocument/ram:IssuerAssignedID  

    Error Message:
    -->  
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" 
        id="BR-IT-040" 
        flag="fatal"> [BR-IT-040] BT-13 (Purchase order reference) - BT maximum length shall be 20 chars. 
      </assert>
    </rule>
            
    <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address[cac:Country/cbc:IdentificationCode='IT']">
    <!--  
    ID: BT-75 - BT-77 - BT-78	
    Cardinality:  0..1
    Level: 3 - 3 -3
    Business Term: Deliver to address line 1 - Deliver to city - Deliver to post code 	
    Description:  The main address line in an address. - An additional address line in an address that can be used to give further details supplementing the main line. - An additional address line in an address that can be used to give further details supplementing the main line.
    (Previous Rule) eIGOR CIUS Reference: CIUS-CA-12-1 - CIUS-CA-12-2 - CIUS-CA-12-3
    eEISI CIUS Referemce: BR-IT-240-1, BR-IT-240-2, BR-IT-240-3

     EeISI Rule 
     (ITA): Se il valore dell’elemento BT-80 Buyer country code è "IT", gli elementi devono essere obbligatoriamente valorizzati     
     (ENG): If BT-80 = "IT", BTs should be mandatory

      CEN:
      /BG-13/BG-15/BT-75
      /BG-13/BG-15/BT-77
      /BG-13/BG-15/BT-78

      UBL Syntax:
      BT-75: /Invoice/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName
             /CreditNote/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName
      BT-77: /Invoice/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName
             /CreditNote/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName
      BT-78: /Invoice/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone
             /CreditNote/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone
     CII Syntax:
      BT-75: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress/ram:LineOne
      BT-77: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress/ram:CityName
      BT-78: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress/ram:PostcodeCode  

    Error Message:
    -->    
      <assert test=  "cbc:StreetName" 
        id="BR-IT-240-1" 
        flag="fatal"> [BR-IT-240-1] BT-75 (Deliver to address line 1) - Fields are mandatory in Italy. 
      </assert>
      <assert test=  "cbc:CityName" 
        id="BR-IT-240-2" 
        flag="fatal"> [BR-IT-240-2] BT-77 (Deliver to city) - Fields are mandatory in Italy. 
      </assert>
      <assert test=  "matches(normalize-space(cbc:PostalZone),'^[0-9]{5}$')" 
        id="BR-IT-240-3" 
        flag="fatal"> [BR-IT-240-3] BT-78 (Deliver to post code) - Fields are mandatory in Italy. 
      </assert>
      <!--  
    ID: BT-79
    Cardinality:  0..1
    Level:  3
    Business Term:  Deliver to country subdivision
    Description:  The subdivision of a country.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-31 - CIUS-VD-49
    eEISI CIUS Referemce: BR-IT-250

    EeISI Rule 
     (ITA): se l'elemento BT-80 Deliver to country code ha valore "IT", 
            per l'elemento BT-79 Deliver to country subdivision deve essere utilizzato uno dei valori della lista delle province italiane. 
            Altrimenti l'informazione deve essere riportata in allegato   
     (ENG): If BT-80=IT, then BT-79 shall be coded according to Italian province list else save in attachment

     CEN:
     /BG-13/BG-15/BT-79

     UBL Syntax:
     BT-79: /Invoice/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity
            /CreditNote/cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity

     CII Syntax:
     BT-79: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ShipToTradeParty/ram:PostalTradeAddress/ram:CountrySubDivisionName

    Nota: per controllo puntuale su provincie utilizzare:  contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT ',cbc:CountrySubentity )
    Error Message:
    --> 
      <assert test=  "not(exists(cbc:CountrySubentity)) or matches(normalize-space(cbc:CountrySubentity),'^[A-Z]{2}$')" 
        id="BR-IT-250" 
        flag="fatal"> [BR-IT-250] BT-79 (Deliver to country subdivision) - BT maximum length shall be 2 chars and shall be coded according to Italian province list else save in attachment.  
      </assert>      
      
    </rule>  
    
    <rule context="cac:ContractDocumentReference">
      <!--  
    ID: BT-12	
    Cardinality:  0..1
    Level:  1
    Business Term:  Contract reference 	
    Description:  The identification of a contract.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-34
    eEISI CIUS Referemce: BR-IT-030

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri   
     (ENG): BT maximum lenght shall be 20 chars
   
    CEN:
    /BT-12

    UBL Syntax:
    BT-12: /Invoice/cac:ContractDocumentReference/cbc:ID
           /CreditNote/cac:ContractDocumentReference/cbc:ID
    
    CII Syntax:
    BT-12: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:ContractReferencedDocument/ram:IssuerAssignedID

    Note: using matches(cbc:ID,'^\p{IsBasicLatin}{0,20}$ instead matches(cbc:ID,'^\p{IsBasicLatin}{1,20}$ the field is optional

    Error Message:
    -->  
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" 
        id="BR-IT-030" 
        flag="fatal"> [BR-IT-030] BT-12 (Contract reference) - BT maximum length shall be 20 chars. 
      </assert>
    </rule>

    <rule context="cac:DespatchDocumentReference">
      <!--  
    ID: BT-16
    Cardinality:  0..1
    Level: 1
    Business Term:  Despatch advice reference 	
    Description:  An identifier of a referenced despatch advice. 
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-16
    eEISI CIUS Referemce: BR-IT-060

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri   
     (ENG): BT maximum lenght shall be 20 chars

    CEN:
    /BT-16

    UBL Syntax:
     BT-16: /Invoice/cac:DespatchDocumentReference/cbc:ID
            /CreditNote/cac:DespatchDocumentReference/cbc:ID  
    CII Syntax:
    BT-16:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:DespatchAdviceReferencedDocument/ram:IssuerAssignedID

   Note:
   Nelle ipotesi precedenti si univa la data al numero DDT e la regola era del tipo
      <assert test=  "matches(cbc:ID, '(^\c{1,20})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})$')" 
        id="CIUS-VD-15" 
        flag="warning"> [CIUS-VD-15] BT-16 (Despatch advice reference) -BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05) 
     </assert>   

    Error Message:
    -->  
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" 
        id="BR-IT-060" 
        flag="fatal"> [BR-IT-060] BT-16 (Despatch advice reference) - BT maximum length shall be 20 chars. 
      </assert>
    </rule>
    
    <rule context="cac:DocumentReference">    
    <!--  
    ID: BT-128
    Cardinality:  0..1
    Level:  2
    Business Term: 	Invoice line object identifier
    Description:  An identifier for an object on which the invoice line is based, given by the Seller.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-77
    eEISI CIUS Referemce: BR-IT-370

     EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 35 caratteri   
     (ENG): BT maximum lenght shall be 35 chars

    CEN:
    /BG-25/BT-128    
    /BG-25/BT-128/@scheme
 
    UBL Syntax:
    BT-128:   /Invoice/cac:InvoiceLine/cac:DocumentReference/cbc:ID
              /CreditNote/cac:CreditNoteLine/cac:DocumentReference/cbc:ID
    BT-128-1: /Invoice/cac:InvoiceLine/cac:DocumentReference/cbc:ID/@schemeID
              /CreditNote/cac:CreditNoteLine/cac:DocumentReference/cbc:ID/@schemeID
    
    CII Syntax:
    BT-128:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument/ram:IssuerAssignedID
              Use with TypeCode "130" (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument/ram:TypeCode)
    BT-128-1: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:AdditionalReferencedDocument/ram:ReferenceTypeCode

    CodiceTipo/CodiceValore (XML-PA) Pattern: \p{IsBasicLatin}{1,35}
    
    Error Message:
    -->
      <assert test=  "not (cbc:ID) or matches(normalize-space(cbc:ID), '^\p{IsBasicLatin}{1,35}$')" 
        id="BR-IT-370" 
        flag="fatal"> [BR-IT-370] BT-128 (Invoice line object identifier) - BT maximum length shall be 35 chars. 
      </assert>      
    </rule>
    
    <rule context="cac:InvoiceLine | cac:CreditNoteLine ">    
    <!--  
    ID: BT-133
    Cardinality:  0..1
    Level:  2
    Business Term:  Invoice line Buyer accounting reference
    Description:  A textual value that specifies where to book the relevant data into the Buyer's financial accounts.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-79
    eEISI CIUS Referemce: BR-IT-410

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri 
     (ENG): BT maximum lenght shall be 20 chars

    CEN:
    /BG-25/BT-133

    UBL Syntax:
    BT-133: /Invoice/cac:InvoiceLine/cbc:AccountingCost
            /CreditNote/cac:CreditNoteLine/cbc:AccountingCost

    CII Syntax:
    BT-133: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount/ram:ID   

    NumItem (XML-PA)pattern: \p{IsBasicLatin}{1,20}
    -->        
      
    <assert test=  "matches(normalize-space(cbc:AccountingCost),'^\p{IsBasicLatin}{0,20}$')" 
      id="BR-IT-410" 
      flag="fatal"> [BR-IT-410] BT-133 (Invoice line Buyer accounting reference)- BT maximum length shall be 20 chars. 
    </assert>
    </rule>
        
    <rule context="cac:InvoiceLine/cac:Price | cac:CreditNoteLine/cac:Price">
    <!--  
    ID: BT-146
    Cardinality:  1..1
    Level:  3
    Business Term: 	Item net price
    Description:  The price of an item, exclusive of VAT, after subtracting item price discount.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-83 CIUS-VD-95
    eEISI CIUS Referemce: BR-IT-430

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 21 caratteri e l'elemento dovrà avere 8 cifre decimali
            
     (ENG): BT minimum length shall be 4 maximum lenght shall be 21 chars. BT allowed fraction digits shall be 8

    CEN:
    /BG-25/BG-29/BT-146

    UBL Syntax:
    BT-146: /Invoice/cac:InvoiceLine/cac:Price/cbc:PriceAmount
            /CreditNote/cac:CreditNoteLine/cac:Price/cbc:PriceAmount
    
    CII Syntax:
    BT-146: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:ChargeAmount

    PrezzoUnitario (XML-PA) pattern: [\-]?[0-9]{1,11}\.[0-9]{2,8}
	  NB: la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali
    
    Error Message:
    -->
      <assert test=  "matches(normalize-space(cbc:PriceAmount), '^[\-]?[0-9]{1,11}([\.][0-9]{1,8})?$')" 
        id="BR-IT-430" 
        flag="fatal"> [BR-IT-430] BT-146 (Item net price) - BT  maximum lenght shall be 21. BT allowed fraction digits shall be 8 
      </assert>
    </rule> 
    
    <rule context="cac:InvoiceLine/cbc:InvoicedQuantity | cac:CreditNoteLine/cbc:CreditedQuantity">
    <!--  
    ID: BT-129
    Cardinality:  1..1
    Level:  2
    Business Term:  Invoiced quantity
    Description: The quantity of items (goods or services) that is charged in the Invoice line.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-104 - CIUS-VD-105
    eEISI CIUS Reference: BR-IT-380

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 21 caratteri e l'elemento dovrà avere 8 cifre decimali 
     (ENG): BT minimum length shall be 4 maximum lenght shall be 21 chars and BT allowed fraction digits shall be 8

    CEN:
    /BG-25/BT-129

    UBL Syntax:
    BT-129: /Invoice/cac:InvoiceLine/cbc:InvoicedQuantity
            /CreditNote/cac:CreditNoteLine/cbc:CreditedQuantity
    
    CII Syntax:
    BT-129: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeDelivery/ram:BilledQuantity

    Quantita (XML-PA)pattern: [0-9]{1,12}\.[0-9]{2,8}
	  NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali
    
    Error Message:
    -->        
      <assert test=  "matches(normalize-space(.),'^[0-9]{1,12}([\.][0-9]{1,8})?$')" 
        id="BR-IT-380" 
        flag="fatal"> [BR-IT-380] BT-129 (Invoiced quantity) - BT maximum lenght shall be 21 chars and BT allowed fraction digits shall be 8
      </assert>
    </rule>
    
    <rule context="cac:InvoiceLine/cbc:LineExtensionAmount | cac:CreditNoteLine/cbc:LineExtensionAmount">
    <!--  
    ID: BT-131
    Cardinality:  1..1
    Level:  2
    Business Term: Invoice line net amount
    Description: The total amount of the Invoice line.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-106
    eEISI CIUS Referemce: BR-IT-390

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 21 caratteri  
     (ENG): BT minimum length shall be 4 maximum lenght shall be 21 chars

    CEN:
    /BG-25/BT-131

    UBL Syntax:
    BT-131: /Invoice/cac:InvoiceLine/cbc:LineExtensionAmount
            /CreditNote/cac:CreditNoteLine/cbc:LineExtensionAmount

    CII Syntax:
    BT-131: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation/ram:LineTotalAmount

    PrezzoTotale XML-PA pattern: [\-]?[0-9]{1,11}\.[0-9]{2,8}

	  NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali	

    Error Message:  
    -->
      <assert test=  "matches(normalize-space(.),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" 
        id="BR-IT-390" 
        flag="fatal"> [BR-IT-390] BT-131 (Invoice line net amount) - BT maximum length shall be 15, including two fraction digits.
      </assert>    
    </rule>
        
    <rule context="cac:Item/cac:BuyersItemIdentification">
      <!--  
    ID: BT-156
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item Buyer's identifier
    Description:  An identifier, assigned by the Buyer, for the item.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-87
    eEISI CIUS Referemce: BR-IT-450

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 35 caratteri  
     (ENG): BT maximum lenght shall be 35 chars

    CEN:
    /BG-25/BG-31/BT-156

    UBL Syntax:
    BT-156: /Invoice/cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification/cbc:ID
            /CreditNote/cac:CreditNoteLine/cac:Item/cac:BuyersItemIdentification/cbc:ID
    CII Syntax:
    BT-156: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:BuyerAssignedID

    Pattern Descrizione:  \p{IsBasicLatin}{1,35}
 
    NOTA: potrebbe essere sufficiente anche matches(cbc:ID,'^\p{IsBasicLatin}{0,35}$' ) 

    Error Message:
    --> 
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')"
        id="BR-IT-450" 
        flag="fatal"> [BR-IT-450] BT-156 (Item Buyer's identifier) - BT maximum length shall be 35 chars. 
      </assert>    
    </rule>

    <rule context="cac:Item/cac:CommodityClassification">
      <!--  
    ID: BT-158	
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item classification identifier
    Description:  A code for classifying the item by its type or nature.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-89
    eEISI CIUS Referemce: BR-IT-470

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 35 caratteri  
     (ENG): BT maximum lenght shall be 35 chars

    CEN:
    /BG-25/BG-31/BT-158
    /BG-25/BG-31/BT-158/@scheme
    /BG-25/BG-31/BT-158/@version
    
    UBL Syntax:
    BT-158:   /Invoice/cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode
              /CreditNote/cac:CreditNoteLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode   
    BT-158-1: /Invoice/cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode/@listID
              /CreditNote/cac:CreditNoteLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode/@listID
    BT-158-2: /Invoice/cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode/@listVersionID
              /CreditNote/cac:CreditNoteLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode/@listVersionID

    CII Syntax:
    BT-158:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode   
    BT-158-1: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listID
    BT-158-2: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listVersionID

    Descrizione (XML-PA) Pattern:  \p{IsBasicLatin}{1,35}

    NOTA: Dovremmo mettere controllo anche su BT-158-2 anche se la VersionID non dovrebbe raggiungere queste dimensioni/formato 

    Error Message:
    --> 
      <assert test=  "matches(normalize-space(cbc:ItemClassificationCode),'^\p{IsBasicLatin}{0,35}$')"
        id="BR-IT-470" 
        flag="fatal"> [BR-IT-470] BT-158 (Item classification identifier) - BT maximum length shall be 35 chars. 
      </assert>
    </rule>
    
    <rule context="cac:Item/cac:SellersItemIdentification">
      <!--  
    ID: BT-155
    Cardinality:  0..1
    Level:  3
    Business Term: 	Item Seller's identifier
    Description:  An identifier, assigned by the Seller, for the item.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-86
    eEISI CIUS Referemce: BR-IT-440

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 35 caratteri  
     (ENG): BT maximum lenght shall be 35 chars

    CEN:
    /BG-25/BG-31/BT-155

    UBL Syntax:
    BT-155: /Invoice/cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ID
            /CreditNote/cac:CreditNoteLine/cac:Item/cac:SellersItemIdentification/cbc:ID
    
    CII Syntax:
    BT-155: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:SellerAssignedID

    Descrizione (XML-PA)Pattern:  \p{IsBasicLatin}{1,35}
    NOTA: potrebbe essere sufficiente anche matches(cbc:ID,'^\p{IsBasicLatin}{0,35}$' ) 

    Error Message:
    --> 
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')"
        id="BR-IT-440" 
        flag="fatal"> [BR-IT-440] BT-155 (Item Seller's identifier) - BT maximum length shall be 35 chars. 
      </assert>
    </rule>
    
    <rule context="cac:Item/cac:StandardItemIdentification">
      <!--  
    ID: BT-157
    Cardinality:  0..1	
    Business Term: 	Item standard identifier
    Description:  An item identifier based on a registered scheme.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-88
    eEISI CIUS Referemce: BR-IT-460

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 35 caratteri  
     (ENG): BT maximum lenght shall be 35 chars

    CEN:
    /BG-25/BG-31/BT-157
    /BG-25/BG-31/BT-157/@scheme

    UBL Syntax:
    BT-157:   /Invoice/cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ID
              /CreditNote/cac:CreditNoteLine/cac:Item/cac:StandardItemIdentification/cbc:ID
    BT-157-1: /Invoice/cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ID/@schemeID
              /CreditNote/cac:CreditNoteLine/cac:Item/cac:StandardItemIdentification/cbc:ID/@schemeID

    CII Syntax:
    BT-157:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:GlobalID
    BT-157-1: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedTradeProduct/ram:GlobalID/@schemeID


    NOTA: essendo SchemeID al massimo di 4 char non serve controllo per BT-157-1)   
    Descrizione (XML-PA) Pattern:  \p{IsBasicLatin}{1,35}

    NOTA: potrebbe essere sufficiente anche matches(cbc:ID,'^\p{IsBasicLatin}{0,35}$' ) 
    Error Message:
    --> 
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,35}$')"
        id="BR-IT-460" 
        flag="fatal"> [BR-IT-460] BT-157 (Item standard identifier) - BT maximum length shall be 35 chars. 
      </assert>
    </rule>
        
    <rule context="cac:LegalMonetaryTotal">
    <!--  
    ID: BT-112
    Cardinality:  1..1
    Level:  2
    Business Term: 	Invoice total amount with VAT
    Description:  The total amount of the Invoice with VAT. 
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-62
    eEISI CIUS Referemce: BR-IT-300

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 15 caratteri  
     (ENG): minimum length shall be 4 maximum lenght shall be 15 chars
    
    CEN:
    /BG-22/BT-112

    UBL Syntax:
    BT-112: /Invoice/cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount
            /CreditNote/cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount
    
    CII Syntax:
    BT-112: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:GrandTotalAmount

    ImportoTotaleDocumento (XML-PA)pattern: [\-]?[0-9]{1,11}\.[0-9]{2}

	  NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali
    Error Message:
    -->
      <assert test=  "matches(normalize-space(cbc:TaxInclusiveAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" 
        id="BR-IT-300" 
        flag="fatal"> [BR-IT-300] BT-112 (Invoice total amount with VAT) - BT maximum length shall be 15, including two fraction digits. 
      </assert>
      
    <!--  
    ID: BT-115	
    Cardinality:  1..1	
    Business Term: 	Amount due for payment
    Description:  The outstanding amount that is requested to be paid.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-63
    eEISI CIUS Referemce: BR-IT-320

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 15 caratteri  
     (ENG): minimum length shall be 4 maximum lenght shall be 15 chars

    CEN:
    /BG-22/BT-115

    UBL Syntax:
    BT-115: /Invoice/cac:LegalMonetaryTotal/cbc:PayableAmount
            /CreditNote/cac:LegalMonetaryTotal/cbc:PayableAmount

    CII Syntax:
    BT-115: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:DuePayableAmount

    ImportoPagamento (XML-PA) pattern: [\-]?[0-9]{1,11}\.[0-9]{2}

    Restriction: BT minimum length shall be 4 maximum lenght shall be 15 chars   
	NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali
    -->
      <assert test=  "matches(normalize-space(cbc:PayableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" 
        id="BR-IT-320" 
        flag="fatal"> [BR-IT-320] BT-115 (Amount due for payment) - BT maximum length shall be 15, including two fraction digits. 
      </assert>

    <!--  
    ID: BT-114	
    Cardinality:  0..1
    Level:  2
    Business Term: 	Rounding amount
    Description:  The amount to be added to the invoice total to round the amount to be paid.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-65
    eEISI CIUS Referemce: BR-IT-310

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 15 caratteri  
     (ENG): minimum length shall be 4 maximum lenght shall be 15 chars

    CEN:
    /BG-22/BT-114

    UBL Syntax:
    BT-114: /Invoice/cac:LegalMonetaryTotal/cbc:PayableRoundingAmount
            /CreditNote/cac:LegalMonetaryTotal/cbc:PayableRoundingAmount
    CII Syntax:
    BT-114: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:RoundingAmount

    Arrotondamento (XML-PA) pattern: [\-]?[0-9]{1,11}\.[0-9]{2}

    Restriction: BT minimum length shall be 4 maximum lenght shall be 15 chars   
	NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali
    -->
      <assert test=  "not(exists(cbc:PayableRoundingAmount)) or (matches(normalize-space(cbc:PayableRoundingAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$'))" 
        id="BR-IT-310" 
        flag="fatal"> [BR-IT-310] BT-114 (Rounding amount) - BT maximum length shall be 15, including 2 fraction digit. 
      </assert>
    </rule>
    
    <rule context="cac:OrderLineReference">
      <!--  
    ID: BT-132
    Cardinality:  0..1
    Level:  2
    Business Term: 	Referenced purchase order line reference
    Description:  An identifier for a referenced line within a purchase order, issued by the Buyer.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-96
    eEISI CIUS Referemce: BR-IT-400

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri 
     (ENG): BT maximum lenght shall be 20 chars

    CEN:
    /BG-25/BT-132

    UBL Syntax:
    BT-132: /Invoice/cac:InvoiceLine/cac:OrderLineReference/cbc:LineID
            /CreditNote/cac:CreditNoteLine/cac:OrderLineReference/cbc:LineID

    CII Syntax:
    BT-132: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeAgreement/ram:BuyerOrderReferencedDocument/ram:LineID

    NumItem (XML-PA) pattern: \p{IsBasicLatin}{1,20}

    Error Message:
    -->
      <assert test=  "matches(normalize-space(cbc:LineID),'^\p{IsBasicLatin}{0,20}$')" 
        id="BR-IT-400" 
        flag="fatal"> [BR-IT-400] BT-132 (Referenced purchase order line reference) - BT maximum length shall be 20 chars. 
      </assert>   
    </rule>

    <rule context="cac:OriginatorDocumentReference">
      <!--  
    ID: BT-17
    Cardinality:  0..1
    Level:  1
    Business Term: 	Tender or lot reference
    Description:  The identification of the call for tender or lot the invoice relates to.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-37
    eEISI CIUS Referemce: BR-IT-070

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 15 caratteri   
     (ENG): BT maximum lenght shall be 15 chars

      CEN:
      /BT-17

      UBL Syntax:
      BT-17: /Invoice/cac:OriginatorDocumentReference/cbc:ID
             /CreditNote/cac:OriginatorDocumentReference/cbc:ID 

      CII Syntax:
      BT-17: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:IssuerAssignedID
            Use for "Tender or lot reference" with TypeCode "50"
    
    Error Message:
    -->
      <assert test=  " matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{1,15}$')" 
        id="BR-IT-070" 
        flag="fatal"> [BR-IT-070] BT-17 (Tender or lot reference) -BT maximum length shall be 15 chars. 
      </assert>
    </rule>
        
    <rule context="cac:PaymentMeans">   
    <!--  
    ID: BG-16	
    Cardinality:  0..1	
    Level 2
    Business Term:  PAYMENT INSTRUCTIONS	
    Description:  A group of business terms providing information about the payment.
    (Previous Rule) eIGOR CIUS Reference: CIUS-CA-103
    eEISI CIUS Referemce: BR-IT-260

     EeISI Rule 
     (ITA): Il gruppo di elementi BG-16 Payment instructions deve essere obbligatorio    
     (ENG): BG-16 Payment instructions shall be mandatory
    
    BG-16: /Invoice/cac:PaymentMeans
           /CreditNote/cac:PaymentMeans

     Restriction:  Fields are mandatory in XMLPA. Mapped BG-16 Payment instructions shall be mandatory 
    -->       
      <assert test=  "." 
        id="BR-IT-260" 
        flag="fatal"> [BR-IT-260] BG-16 Payment instructions - BG-16 shall be mandatory 
      </assert>
    </rule>
    
    <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount">
      <!--  
    ID: BT-84	
    Cardinality:  1..1	
    Level: 3
    Business Term:  Payment account identifier	
    Description:  A unique identifier of the financial payment account, at a payment service provider, to which payment should be made.
    (Previous Rule) eIGOR CIUS Reference: CIUS-BT-84
    eEISI CIUS Referemce: BR-IT-270

     EeISI Rule 
     (ITA): L'identificativo del pagamento BT-84 Payment account identifier deve essere un codice IBAN   
     (ENG): BT-84 Payment account identifier shall be an IBAN code

    CEN:
    /BG-16/BG-17/BT-84

     UBL Syntax:
     BT-84: /Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID
            /CreditNote/cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID

     CII Syntax:
     BT-84: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount/ram:IBANID

    Error Message:
    -->     
      <assert test=  "matches(normalize-space(cbc:ID), '(^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})$')" 
        id="BR-IT-270" 
        flag="fatal"> [BR-IT-270] BT-84 (Payment account identifier) shall be an IBAN code according the pattern [a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30}) . 
      </assert>
    </rule>

    <rule context="cac:ProjectReference">
    <!--  
    ID: BT-11
    Cardinality:  0..1
    Level:  1
    Business Term: Project reference	
    Description: The identification of the project the invoice refers to 
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-33
    eEISI CIUS Referemce: BR-IT-020

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 15 caratteri   
     (ENG): BT maximum lenght shall be 15 chars

    CEN:
    /BT-11
    
    UBL Syntax:
    BT-11: /Invoice/cac:ProjectReference/cbc:ID
           /CreditNote/cac:AdditionalDocumentReference/cbc:ID with cbc:DocumentTypeCode = 50
    CII Syntax:
    BT-11: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject/ram:ID 
                  + Use "Project reference" as default value for Name.    

    Note: In UBL are required 2 different Conntext

    Error Message:
    -->  
      <assert test=  " matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,15}$')" 
        id="BR-IT-020-1" 
        flag="fatal"> [BR-IT-020] BT-11 (Project reference) - BT maximum length shall be 15 chars. 
      </assert>    
    </rule>

    <rule context="CreditNote/cac:AdditionalDocumentReference/cbc:ID[normalize-space(cbc:DocumentTypeCode) = '50']">
      <!--  
    ID: BT-11
    Cardinality:  0..1
    Level:  1
    Business Term: Project reference	
    Description: The identification of the project the invoice refers to 
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-33
    eEISI CIUS Referemce: BR-IT-020

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 15 caratteri   
     (ENG): BT maximum lenght shall be 15 chars

    CEN:
    /BT-11
    
    UBL Syntax:
    BT-11: /Invoice/cac:ProjectReference/cbc:ID
                 /CreditNote/cac:AdditionalDocumentReference/cbc:ID with cbc:DocumentTypeCode = 50
    CII Syntax:
    BT-11: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject/ram:ID 
                  + Use "Project reference" as default value for Name.    

    Note: In UBL are required 2 different Conntext

    Error Message:
    -->
      <assert test=  " matches(normalize-space(.),'^\p{IsBasicLatin}{0,15}$')" 
        id="BR-IT-020-2" 
        flag="fatal"> [BR-IT-020-1] BT-11 (Project reference) - BT maximum length shall be 15 chars. 
      </assert>  
    </rule>

    <rule context="cac:ReceiptDocumentReference">
      <!--  
    ID: BT-15
    Cardinality:  0..1
    Level: 1
    Business Term: 	Receiving advice reference
    Description:  An identifier of a referenced receiving advice.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-36
    eEISI CIUS Referemce: BR-IT-050

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 20 caratteri   
     (ENG): BT maximum lenght shall be 20 chars

    CEN:
    /BT-15

    UBL Syntax:
    BT-15: /Invoice/cac:ReceiptDocumentReference/cbc:ID
           /CreditNote/cac:ReceiptDocumentReference/cbc:ID

    CII Syntax:
    BT-15: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery/ram:ReceivingAdviceReferencedDocument/ram:IssuerAssignedID

    Error Message:
    -->  
      <assert test=  "matches(normalize-space(cbc:ID),'^\p{IsBasicLatin}{0,20}$')" 
        id="BR-IT-050" 
        flag="fatal"> [BR-IT-050] BT-15 (Receiving advice reference) - BT maximum length shall be 20 chars. 
      </assert>
    </rule>   
    
    <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch">
  <!--  
  ID: BT-86
  Cardinality:  0..1
  Level: 3 
  Business Term: 	Payment service provider identifier
  Description:  An identifier for the payment service provider where a payment account is located.
  (Previous Rule) eIGOR CIUS Reference: CIUS-VD-59
  eEISI CIUS Referemce: BR-IT-280

  EeISI Rule 
   (ITA): La lunghezza dell'elemento deve essere compresa fra 8 e 11 caratteri (BIC)
   (ENG): BT minimum length shall be 8 maximum lenght shall be 11 chars (BIC)

    CEN:
    /BG-16/BG-17/BT-86
    
    UBL Syntax:
    BT-86: /Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:ID
           /CreditNote/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:ID

    CII Syntax:
    BT-86: rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeeSpecifiedCreditorFinancialInstitution/ram:BICID

    Error Message:
    -->
      <assert test=  "not(cbc:ID) or matches(normalize-space(cbc:ID),'^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3}){0,1}$')" 
        id="BR-IT-280" 
        flag="fatal"> [BR-IT-280] BT-86 (Payment service provider identifier) - BT shall contain a SWIFT/BIC (bank identifier code) according to structure defined in ISO 9362 (minimum length shall be 8- maximum length shall be 11 chars). 
      </assert>
    </rule>
       
    <rule context="cac:TaxCategory/cbc:ID" >
      <!--  
      ID: BT-95 BT-102 BT-118 BT-151
      Cardinality:  1..1 1..1 1..1 1..1
      Level: 2 2 2 3
      Business Term: Document level allowance VAT category code - Document level charge VAT category code -  VAT category code - Invoiced item VAT category code	
      Description: A coded identification of what VAT category applies to the document level allowance.
                   A coded identification of what VAT category applies to the document level charge.
                   Coded identification of a VAT category.
                   The VAT category code for the invoiced item.
      eEISI CIUS Reference: BR-IT-350
  
       EeISI Rule 
       (ITA): I valori accettati sono esclusivamente AE L M E S G  K 
       (ENG): for BT-118 only values AE L M E S G  K shall be allowed

        CEN:
        /BG-20/BT-95
        /BG-21/BT-102
        /BG-23/BT-118
        /BG-25/BG-30/BT-151
        
        UBL Syntax:
        BT-95:   /Invoice/cac:AllowanceCharge/cac:TaxCategory/cbc:ID          with cbc:ChargeIndicator = 'false' & with cac:TaxScheme/cbc:ID = “VAT”
                 /CreditNote/cac:AllowanceCharge/cac:TaxCategory/cbc:ID       with cbc:ChargeIndicator = 'false'
        BT-102:  /Invoice/cac:AllowanceCharge/cac:TaxCategory/cbc:ID          with cbc:ChargeIndicator = 'true' 	
                 /CreditNote/cac:AllowanceCharge/cac:TaxCategory/cbc:ID       with cbc:ChargeIndicator = 'true'
        BT-118:  /Invoice/cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:ID 	                                    with cac:TaxScheme/cbc:ID = "VAT"
                 /CreditNote/cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:ID                                    with cac:TaxScheme/cbc:ID = "VAT" 
        BT-151: /Invoice/cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:ID
                /CreditNote/cac:CreditNoteLine/cac:Item/cac:ClassifiedTaxCategory/cbc:ID

        CII Syntax:
        BT-95&BT-102:   /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:CategoryTradeTax/ram:CategoryCode
                        with /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:CategoryTradeTax/ram:TypeCode Fixed value "VAT"
        BT-118:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:CategoryCode
                 with /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:TypeCode Fixed value "VAT"
        BT-151:  /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ApplicableTradeTax/ram:CategoryCode
                 with /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:ApplicableTradeTax/ram:TypeCode Fixed value "VAT"

    Error Message:
    -->  
      
      <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE L M E S G K ',concat(' ',normalize-space(.),' ') ) ) )"
        id="BR-IT-350" 
        flag="fatal"> [BR-IT-350] VAT category codes - only values E (Exempt from VAT), K	(Intra-community supply), AE (Reverse charge), G (Export outside the EU) ,S (Standard rated), L (IGIC), M (IPSI) shall be allowed . 
      </assert>
    </rule>
    
    <rule context="cac:Item/cac:ClassifiedTaxCategory/cbc:ID">
      <!--  
      ID: BT-151
      Cardinality:  1..1	
      Level: 3
      Business Term: Invoiced item VAT category code	
      Description:  The VAT category code for the invoiced item.
      eEISI CIUS Reference: BR-IT-350-1 (Vedi regola BR-IT-350)
  
       EeISI Rule 
       (ITA): I valori accettati sono esclusivamente AE L M E S G  K  
       (ENG): for BT-118 only values AE L M E S G  K shall be allowed
       
        BT-151: /Invoice/cac:InvoiceLine/cac:Item/cac:ClassifiedTaxCategory/cbc:ID
                /CreditNote/cac:CreditNoteLine/cac:Item/cac:ClassifiedTaxCategory/cbc:ID

      NOTA: Questa parte di controllo non dovrebbe in realtà servire dato che c'è la regola CEN che devono poi essere riportati nei BreakDown e quindi nel BT-118
      -->
      <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE L M E S G K ',concat(' ',normalize-space(.),' ') ) ) )"
        id="BR-IT-350-1" 
        flag="fatal"> [BR-IT-350] VAT category codes - only values E (Exempt from VAT), K	(Intra-community supply), AE (Reverse charge), G (Export outside the EU) ,S (Standard rated), L (IGIC), M (IPSI) shall be allowed . 
      </assert> 
    </rule>
    
    <rule context="cac:TaxRepresentativeParty/cac:PartyTaxScheme">
      <!--  
    ID: BT-63
    Cardinality:  1..1
    Level: 2
    Business Term:  Seller tax representative VAT identifier
    Description:  The VAT identifier of the Seller's tax representative party.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-42
    eEISI CIUS Reference: BR-IT-230

    EeISI Rule 
     (ITA): La lunghezza dell'elemento non può superare i 30 caratteri  
     (ENG): BT maximum lenght shall be 30 chars

     CEN:
     /BG-11/BT-63
     
     UBL Syntax:
     BT-63: /Invoice/cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID
            /CreditNote/cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID

     CII Syntax:
     BT-63: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration/ram:ID
            with @schemeID="VA"

    Error Message:
    -->
      <assert test=  "string-length(normalize-space(cbc:CompanyID)) &lt;= 30"         
        id="BR-IT-230" 
        flag="fatal"> [BR-IT-230] BT-31 (Seller VAT identifier) - BT maximum length shall be 30 chars. 
      </assert> 
    </rule>
    
    <rule context="cac:TaxTotal/cac:TaxSubtotal">
    <!--  
    ID: BT-116
    Cardinality:  1..1
    Level:  2
    Business Term:  VAT category taxable amount
    Description:  Sum of all taxable amounts subject to a specific VAT category code and VAT category rate (if the VAT category rate is applicable).
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-66
    eEISI CIUS Referemce: BR-IT-330

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 15 caratteri  
     (ENG): minimum length shall be 4 maximum lenght shall be 15 chars

    CEN:
    /BG-23/BT-116

    UBL Syntax:
    BT-116: /Invoice/cac:TaxTotal/cac:TaxSubtotal/cbc:TaxableAmount
            /CreditNote/cac:TaxTotal/cac:TaxSubtotal/cbc:TaxableAmount

    CII Syntax:
    BT-116: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:BasisAmount

    ImponibileImporto (XML-PA) pattern: [\-]?[0-9]{1,11}\.[0-9]{2}
    NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali

    Error Message:
    -->        
      <assert test=  "matches(normalize-space(cbc:TaxableAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?')" 
        id="BR-IT-330" 
        flag="fatal"> [BR-IT-330] BT-116 (VAT category taxable amount) - BT maximum length shall be 15, including two fraction digits. 
      </assert>
      
    <!--  
    ID: BT-117
    Cardinality:  11..1
    Level:  2
    Business Term:  VAT category tax amount
    Description:  The total VAT amount for a given VAT category.
    (Previous Rule) eIGOR CIUS Reference: CIUS-VD-67
    eEISI CIUS Referemce: BR-IT-340

    EeISI Rule 
     (ITA): La lunghezza dell'elemento deve essere compresa fra 4 e 15 caratteri  
     (ENG): minimum length shall be 4 maximum lenght shall be 15 chars

    CEN:
    /BG-23/BT-117

    UBL Syntax:
    BT-117: /Invoice/cac:TaxTotal/cac:TaxSubtotal/cbc:TaxAmount
            /CreditNote/cac:TaxTotal/cac:TaxSubtotal/cbc:TaxAmount

    CII Syntax:
    BT-117: /rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:CalculatedAmount

    Imposta (XML-PA) pattern: [\-]?[0-9]{1,11}\.[0-9]{2}  
	NB la lunghezza minima è garantita dal mapper che aggiunge .00 nel caso non siano presenti decimali
    -->   
      <assert test=  "matches(normalize-space(cbc:TaxAmount),'^[\-]?[0-9]{1,11}([\.][0-9]{1,2})?$')" 
        id="BR-IT-340" 
        flag="fatal"> [BR-IT-340] BT-117 (VAT category tax amount) - BT maximum length shall be 15, including two fraction digits. 
      </assert>
    </rule>
      
</pattern> 

</schema>
