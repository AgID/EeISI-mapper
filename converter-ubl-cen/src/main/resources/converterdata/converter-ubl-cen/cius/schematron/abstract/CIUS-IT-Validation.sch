<pattern abstract="true" id="syntax" xmlns="http://purl.oclc.org/dsdl/schematron">
   <rule context="$Invoice_Root">
  <assert test="$CIUS-CA-9" id="CIUS-CA-9" flag="fatal"> [CIUS-CA-9] BT-31 (Seller VAT identifier) -Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax representative). 
        </assert>
 
  <assert test="$CIUS-VD-32" id="CIUS-VD-32" flag="fatal"> [CIUS-VD-32] BT-1 (Invoice number) -BT maximum length shall be 20 digits. 
        </assert>
  <assert test="$CIUS-VD-39" id="CIUS-VD-39" flag="fatal"> [CIUS-VD-39] BT-21, BT-22 (Invoice note subject code
Invoice note) -The sum of BTs maximum length shall be 200 chars. 
        </assert>
  <assert test="$CIUS-CI-13" id="CIUS-CI-13" flag="fatal"> [CIUS-CI-13] BT-6 (VAT accounting currency code)  should be € for invoices from EU to IT in accordance with 2006/112/CE art. 9. 
        </assert>
   </rule>
   <rule context="$Accounting_Customer_PartyIdentification">
  <assert test="$CIUS-VD-100-1" id="CIUS-VD-100-1" flag="fatal"> [CIUS-VD-100-1] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -BT46-1=IT:CF then BT-46 minimum lenght 11 and maximum lenght shall be 16 
        </assert>
  <assert test="$CIUS-VD-100-2" id="CIUS-VD-100-2" flag="fatal"> [CIUS-VD-100-2] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:EORI then BT-46 minimum lenght 13 and maximum lenght shall be 17 
        </assert>
  <assert test="$CIUS-VD-100-3" id="CIUS-VD-100-3" flag="fatal"> [CIUS-VD-100-3] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-46-1=IT:VAT then BT-46 maximum length 30 (the first two chars indicates country code). 
        </assert>
   </rule>
   <rule context="$Accounting_Supplier_PartyIdentification">
  <assert test="$CIUS-VD-101-1" id="CIUS-VD-101-1" flag="fatal"> [CIUS-VD-101-1] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -BT29-1=IT:CF then BT-29 minimum lenght 11 and maximum lenght shall be 16. 
        </assert>
  <assert test="$CIUS-VD-101-2" id="CIUS-VD-101-2" flag="fatal"> [CIUS-VD-101-2] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -If BT-29-1=IT:EORI then BT-29 minimum lenght 13 and maximum lenght shall be 17 . 
        </assert>
  <assert test="$CIUS-VD-101-3" id="CIUS-VD-101-3" flag="fatal"> [CIUS-VD-101-3] BT-29, BT-29-1 (Seller identifier - Seller identifier identification scheme identifier) -If BT-29-1=IT:VAT then BT-29 maximum length 30 (the first two chars indicates country code). 
        </assert>
   </rule>
   <rule context="$Accounting_Supplier_PartyLegalEntity">
  <assert test="$CIUS-VD-102-1" id="CIUS-VD-102-1" flag="fatal"> [CIUS-VD-102-1] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration identifier identification scheme identifier) -If BT-30-1=IT:REA then BT-30 minimum lenght 3 and maximum lenght shall be 22 (first two chars indicate the italian province code). 
        </assert>
  <assert test="$CIUS-VD-102-2" id="CIUS-VD-102-2" flag="fatal"> [CIUS-VD-102-2] BT-30, BT-30-1 (Seller legal registration identifier - Seller legal registration identifier identification scheme identifier) -If BT-30-1=IT:ALBO then BT-30 maximum length 60 . 
        </assert>
   </rule>
   <rule context="$Accounting_Customer_Party">
  <assert test="$CIUS-BR-14" id="CIUS-BR-14" flag="fatal"> [CIUS-BR-14] BT-48
BT-46, BT-46-1 (Buyer VAT identifier - Buyer identifier - Buyer identifier identification scheme identifier) -1.4.1.1 is not mandatory in Italy (buyer) but VAT number or Fiscal code should be indicated. 
        </assert>
  <assert test="$CIUS-CA-2" id="CIUS-CA-2" flag="fatal"> [CIUS-CA-2] BT-49
BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) shall contain a legal mail address (PEC) or IndicePA/CodiceDestinatario (see the Italian business rules). BT-49-1=IT:PEC or IT:IPA or IT:CODDEST 
        </assert>
  <assert test="$CIUS-VD-53" id="CIUS-VD-53" flag="fatal"> [CIUS-VD-53] BT-46, BT-46-1 (Buyer identifier - Buyer identifier identification scheme identifier) -If BT-48 is empty then one of the buyer identifiers (0..n) should be the FiscalCode in BT-46. BT-46-1 shall contain the scheme. 
        </assert>
  <assert test="$CIUS-VD-97-1" id="CIUS-VD-97-1" flag="fatal"> [CIUS-VD-97-1-1] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) -If BT-49-1=IT:PEC schema then BT-49 minimum length shall be 7 maximum length shall be 256 
        </assert>
  <assert test="$CIUS-VD-97-2" id="CIUS-VD-97-2" flag="fatal"> [CIUS-VD-97-1-2] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier) =IT:IPA schema then BT-49 maximum length shall be 6 chars 
        </assert>
  <assert test="$CIUS-VD-97-3" id="CIUS-VD-97-3" flag="fatal"> [CIUS-VD-97-1-3] BT-49, BT-49-1 (Buyer electronic address - Buyer electronic address identification scheme identifier)=IT:CODDEST schema then BT-49 maximum length shall be 7 chars. 
        </assert>
   </rule>
   <rule context="$Accounting_Customer_Party_Contact">
  <assert test="$CIUS-VD-51" id="CIUS-VD-51" flag="fatal"> [CIUS-VD-51] BT-56 (Buyer contact point) -BT maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="$Accounting_Customer_PartyLegalEntity">
  <assert test="$CIUS-VD-18" id="CIUS-VD-18" flag="fatal"> [CIUS-VD-18] BT-44 (Buyer name) -BT maximum length shall be 80 chars. 
        </assert>
   </rule>
   <rule context="$Accounting_Customer_PartyTaxScheme">
  <assert test="$CIUS-VD-43" id="CIUS-VD-43" flag="fatal"> [CIUS-VD-43] BT-48 (Buyer VAT identifier) -BT maximum length shall be 30 chars. 
        </assert>
   </rule>
   <rule context="$Accounting_Customer_PostalAddress">
  <assert test="$CIUS-CA-11-1" id="CIUS-CA-11-1" flag="fatal"> [CIUS-CA-11-1-1] BT-50 (Buyer address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-CA-11-2" id="CIUS-CA-11-2" flag="fatal"> [CIUS-CA-11-1-2] BT-52 (Buyer city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-CA-11-3" id="CIUS-CA-11-3" flag="fatal"> [CIUS-CA-11-1-3] BT-53 (Buyer post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-VD-21" id="CIUS-VD-21" flag="fatal"> [CIUS-VD-21] BT-50, BT-51, BT-163 (Buyer address line 1 - Buyer address line 2 - Buyer address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
        </assert>
  <assert test="$CIUS-VD-24" id="CIUS-VD-24" flag="fatal"> [CIUS-VD-24] BT-52 (Buyer city) -BT maximum length shall be 60 characters. 
        </assert>
  <assert test="$CIUS-VD-27-1" id="CIUS-VD-27-1" flag="fatal"> [CIUS-VD-27-1-1] BT-53 (Buyer post code) -BT maximum length shall be 15 chars  if country-code not =IT and 5 chars if country-code=IT. 
        </assert>
  <assert test="$CIUS-VD-27-2" id="CIUS-VD-27-2" flag="fatal"> [CIUS-VD-27-1-2] BT-53 (Buyer post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </assert>
  <assert test="$CIUS-VD-30" id="CIUS-VD-30" flag="fatal"> [CIUS-VD-30] BT-54 (Buyer country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </assert>
  <assert test="$CIUS-VD-48" id="CIUS-VD-48" flag="fatal"> [CIUS-VD-48] BT-54 (Buyer country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </assert>
   </rule>
   <rule context="$Accounting_Supplier_Party_Contact">
  <assert test="$CIUS-VD-44" id="CIUS-VD-44" flag="fatal"> [CIUS-VD-44] BT-41 (Seller contact point)  -BT maximum length shall be 200 chars. 
        </assert>
  <assert test="$CIUS-VD-45" id="CIUS-VD-45" flag="fatal"> [CIUS-VD-45] BT-42 (Seller contact telephone number) -BT minimum length shall be 5 maximum length shall be 12 chars. 
        </assert>
  <assert test="$CIUS-VD-46" id="CIUS-VD-46" flag="fatal"> [CIUS-VD-46] BT-43 (Seller contact email address) -BT minimum length shall be 7 maximum length shall be 256 chars. 
        </assert>
   </rule>
   <rule context="$Accounting_Supplier_PartyLegalEntity">
  <assert test="$CIUS-VD-17" id="CIUS-VD-17" flag="fatal"> [CIUS-VD-17] BT-27 (Seller name) -BT maximum length shall be 80 chars. 
        </assert>
   </rule>
   <rule context="$Accounting_Supplier_TaxScheme">
  <assert test="$CIUS-BT-98-1" id="CIUS-BT-98-1" flag="fatal"> [CIUS-BT-98-1] BT-32 (Seller tax registration identifier)  is a conditional field and shall not be used by a foreign seller as it is not possible to map into XMLPA. 
        </assert>
  <assert test="$CIUS-VD-99" id="CIUS-VD-99" flag="fatal"> [CIUS-VD-99] BT-32 (Seller tax registration identifier) -In case the seller is Italian this field must contain the codification of RegimeFiscale 
        </assert>
  <assert test="$CIUS-VD-41" id="CIUS-VD-41" flag="fatal"> [CIUS-VD-41] BT-31 (Seller VAT identifier) -BT maximum length shall be 30 chars. 
        </assert>
   </rule>
   <rule context="$Accounting_Supplier">
  <assert test="$CIUS-BT-98-2" id="CIUS-BT-98-2" flag="fatal"> [CIUS-BT-98-2] BT-32 (Seller tax registration identifier). In case the seller is Italian this field shall contain the codification of RegimeFiscale (1.2.1.8) 
        </assert>
   </rule>
   <rule context="$Accounting_Supplier_Party_PostalAddress">
  <assert test="$CIUS-CA-10-1" id="CIUS-CA-10-1" flag="fatal"> [CIUS-CA-10-1] BT-35 (Seller address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-CA-10-2" id="CIUS-CA-10-2" flag="fatal"> [CIUS-CA-10-2] BT-37 (Seller city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-CA-10-3" id="CIUS-CA-10-3" flag="fatal"> [CIUS-CA-10-3] BT-38 (Seller post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-VD-20" id="CIUS-VD-20" flag="fatal"> [CIUS-VD-20] BT-35, BT-36, BT-162 (Seller address line 1 - Seller address line 2 - Seller address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
        </assert>
  <assert test="$CIUS-VD-23" id="CIUS-VD-23" flag="fatal"> [CIUS-VD-23] BT-37 (Seller city) -BT maximum length shall be 60 characters. 
        </assert>
  <assert test="$CIUS-VD-26-1" id="CIUS-VD-26-1" flag="fatal"> [CIUS-VD-26-1-1] BT-38 (Seller post code) - BT maximum length shall be 15 chars if country-code not =IT and 5 chars if country-code=IT. 
        </assert>
  <assert test="$CIUS-VD-26-2" id="CIUS-VD-26-2" flag="fatal"> [CIUS-VD-26-1-2] BT-38 (Seller post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </assert>
  <assert test="$CIUS-VD-29" id="CIUS-VD-29" flag="fatal"> [CIUS-VD-29] BT-39 (Seller country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </assert>
  <assert test="$CIUS-VD-47" id="CIUS-VD-47" flag="fatal"> [CIUS-VD-47] BT-39 (Seller country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </assert>
   </rule>
   <rule context="$Additional_Document_Reference">
  <assert test="$CIUS-CA-71" id="CIUS-CA-71" flag="fatal"> [CIUS-CA-71] BT-125 (Attached document) -If BT-122 not empty then BT-124 or BT-125 should be mandatory as the mapped field is mandatory in Italy. 
        </assert>
  <assert test="$CIUS-VD-69" id="CIUS-VD-69" flag="fatal"> [CIUS-VD-69] BT-122, BT-125-2 (Supporting document reference - Attached document Filename) - BT maximum length shall be 60 chars. 
        </assert>
  <assert test="$CIUS-VD-70" id="CIUS-VD-70" flag="fatal"> [CIUS-VD-70] BT-123 (Supporting document description) -BT maximum length shall be 100 chars. 
        </assert>
   </rule>
   <rule context="$Additional_Document_Reference_Attachment">
  <assert test="$CIUS-VD-72" id="CIUS-VD-72" flag="fatal"> [CIUS-VD-72] BT-125-1 (Attached document Mime code) -BT maximum length shall be 10 chars. 
        </assert>
   </rule>
   <rule context="$AllowanceCharge">
  <assert test="$CIUS-VD-60" id="CIUS-VD-60" flag="fatal"> [CIUS-VD-60] BT-97, BT-98 (Document level allowance reason - Document level allowance reason code)-BTs maximum length shall be 1000 chars. 
        </assert>
  <assert test="$CIUS-VD-61" id="CIUS-VD-61" flag="fatal"> [CIUS-VD-61] BT-104, BT-105 (Document level charge reason - Document level charge reason code)-BTs maximum length shall be 1000 chars. 
        </assert>
  <assert test="$CIUS-VD-64" id="CIUS-VD-64" flag="fatal"> [CIUS-VD-64] BT-92, BT-99 (Document level allowance amount - Document level charge amount) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </assert>
   </rule>
   <rule context="$BillingReference_InvoiceDocumentReference">
  <assert test="$CIUS-VD-40" id="CIUS-VD-40" flag="fatal"> [CIUS-VD-40] BT-25 (Preceding Invoice number)-BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="$Contract_DocumentReference">
  <assert test="$CIUS-VD-34" id="CIUS-VD-34" flag="fatal"> [CIUS-VD-34] BT-12 (Contract reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="$Delivery_DeliveryLocation_Address">
  <assert test="$CIUS-CA-12-1" id="CIUS-CA-12-1" flag="fatal"> [CIUS-CA-12-1-1] BT-75 (Deliver to address line 1) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-CA-12-2" id="CIUS-CA-12-2" flag="fatal"> [CIUS-CA-12-1-2] BT-77 (Deliver to city) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-CA-12-3" id="CIUS-CA-12-3" flag="fatal"> [CIUS-CA-12-1-3] BT-78 (Deliver to post code) - Fields are mandatory in Italy. Mapped BTs should be mandatory. 
        </assert>
  <assert test="$CIUS-VD-22" id="CIUS-VD-22" flag="fatal"> [CIUS-VD-22] BT-75, BT-76, BT-165 (Deliver to address line 1 - Deliver to address line 2 - Deliver to address line 3) -The sum of BTs maximum length shall be 60 chars (including separator). 
        </assert>
  <assert test="$CIUS-VD-25" id="CIUS-VD-25" flag="fatal"> [CIUS-VD-25] BT-77 (Deliver to city) -BT maximum length shall be 60 characters. 
        </assert>
  <assert test="$CIUS-VD-28-1" id="CIUS-VD-28-1" flag="fatal"> [CIUS-VD-28-1-1] BT-78 (Deliver to post code) -BT maximum length shall be 15 chars if country-code not =IT and 5 chars if country-code=IT. 
        </assert>
  <assert test="$CIUS-VD-28-2" id="CIUS-VD-28-2" flag="fatal"> [CIUS-VD-28-1-2] BT-78 (Deliver to post code) -BT maximum length, if country code =IT then it should be numeric and maximum length 5. 
        </assert>
  <assert test="$CIUS-VD-31" id="CIUS-VD-31" flag="fatal"> [CIUS-VD-31] BT-79 (Deliver to country subdivision) -BT maximum length shall be 2 chars only used if country code=IT else the BT is not used. 
        </assert>
  <assert test="$CIUS-VD-49" id="CIUS-VD-49" flag="fatal"> [CIUS-VD-49] BT-79 (Deliver to country subdivision) -If country code=IT it should be coded according to Italian province list. 
        </assert>
   </rule>
   <rule context="$Despatch_DocumentReference">
  <assert test="$CIUS-VD-15" id="CIUS-VD-15" flag="fatal"> [CIUS-VD-15] BT-16 (Despatch advice reference) -BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05) 
        </assert>
  <assert test="$CIUS-VD-16" id="CIUS-VD-16" flag="fatal"> [CIUS-VD-16] BT-16 (Despatch advice reference) -BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD). 
        </assert>
   </rule>
   <rule context="$InvoiceLine">
  <assert test="$CIUS-SD-73" id="CIUS-SD-73" flag="fatal"> [CIUS-SD-73] BT-126 (Invoice line identifier) -The BT value should be numeric. 
        </assert>
  <assert test="$CIUS-VD-38" id="CIUS-VD-38" flag="fatal"> [CIUS-VD-38] BT-19 (Buyer accounting reference) -BT maximum length shall be 20 chars. 
        </assert>
  <assert test="$CIUS-VD-74" id="CIUS-VD-74" flag="fatal"> [CIUS-VD-74] BT-126 (Invoice line identifier) -BT maximum length shall be 4 chars. 
        </assert>
  <assert test="$CIUS-VD-75" id="CIUS-VD-75" flag="fatal"> [CIUS-VD-75] BT-127 (Invoice line note) -BT maximum length shall be 60 chars. 
        </assert>
  <assert test="$CIUS-VD-78-1" id="CIUS-VD-78-1" flag="fatal"> [CIUS-VD-78-1-1] BT-130 (Invoiced quantity unit of measure) -BT maximum length shall be 10 chars. 
        </assert>
  <assert test="$CIUS-VD-78-2" id="CIUS-VD-78-2" flag="fatal"> [CIUS-VD-78-1-2] BT-149 (Item price base quantity) -BT maximum length shall be 10 chars. 
        </assert>
  <assert test="$CIUS-VD-78-3" id="CIUS-VD-78-3" flag="fatal"> [CIUS-VD-78-1-3] BT-150 (Item price base quantity unit of measure code) -BT maximum length shall be 10 chars. 
        </assert>
  <assert test="$CIUS-VD-79" id="CIUS-VD-79" flag="fatal"> [CIUS-VD-79] BT-133 (Invoice line Buyer accounting reference)-BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_AllowanceCharge">
  <assert test="$CIUS-VD-80" id="CIUS-VD-80" flag="fatal"> [CIUS-VD-80] BT-136, BT-141 (Invoice line allowance amount - Invoice line charge amount)-BT minimum length shall be 4, maximum length shall be 21 chars. 
        </assert>
  <assert test="$CIUS-VD-81-1" id="CIUS-VD-81-1" flag="fatal"> [CIUS-VD-81-1-1] BT-139 (Invoice line allowance reason)-BT maximum length shall be 1000 chars. 
        </assert>
  <assert test="$CIUS-VD-81-2" id="CIUS-VD-81-2" flag="fatal"> [CIUS-VD-81-1-2] BT-140 (Invoice line allowance reason code)-BT maximum length shall be 1000 chars. 
        </assert>
  <assert test="$CIUS-VD-82-1" id="CIUS-VD-82-1" flag="fatal"> [CIUS-VD-82-1-1] BT-144 (Invoice line charge reason)-BT maximum length shall be 1000 chars. 
        </assert>
  <assert test="$CIUS-VD-82-2" id="CIUS-VD-82-2" flag="fatal"> [CIUS-VD-82-1-2] BT-145 (Invoice line charge reason code)-BT maximum length shall be 1000 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_DocumentReference">
  <assert test="$CIUS-VD-76" id="CIUS-VD-76" flag="fatal"> [CIUS-VD-76] BT-128-1 (Invoice line object identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
  <assert test="$CIUS-VD-77" id="CIUS-VD-77" flag="fatal"> [CIUS-VD-77] BT-128 (Invoice line object identifier) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item">
  <assert test="$CIUS-VD-85-1" id="CIUS-VD-85-1" flag="fatal"> [CIUS-VD-85-1-1] BT-153 (Item name) -BT maximum length shall be 1000 chars. 
        </assert>
  <assert test="$CIUS-VD-85-2" id="CIUS-VD-85-2" flag="fatal"> [CIUS-VD-85-1-2] BT-154 (Item description) -BT maximum length shall be 1000 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item_AdditionalItemProperty">
  <assert test="$CIUS-VD-93" id="CIUS-VD-93" flag="fatal"> [CIUS-VD-93] BT-160 (Item attribute name) -BT maximum length shall be 10 chars. 
        </assert>
  <assert test="$CIUS-VD-94" id="CIUS-VD-94" flag="fatal"> [CIUS-VD-94] BT-161 (Item attribute value) -BT maximum length shall be 60 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item_BuyersItemIdentification">
  <assert test="$CIUS-VD-87" id="CIUS-VD-87" flag="fatal"> [CIUS-VD-87] BT-156 (Item Buyer's identifier) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item_CommodityClassification">
  <assert test="$CIUS-VD-89" id="CIUS-VD-89" flag="fatal"> [CIUS-VD-89] BT-158 (Item classification identifier) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item_CommodityClassification_ItemClassificationCode">
  <assert test="$CIUS-VD-91-1" id="CIUS-VD-91-1" flag="fatal"> [CIUS-VD-91-1-1] BT-158-1 (Item classification identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
  <assert test="$CIUS-VD-91-2" id="CIUS-VD-91-2" flag="fatal"> [CIUS-VD-91-1-2] BT-158-2 (Scheme version identifer) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item_OriginCountry">
  <assert test="$CIUS-VD-92" id="CIUS-VD-92" flag="fatal"> [CIUS-VD-92] BT-159 (Item country of origin) -BT maximum length shall be 60 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item_SellersItemIdentification">
  <assert test="$CIUS-VD-86" id="CIUS-VD-86" flag="fatal"> [CIUS-VD-86] BT-155 (Item Seller's identifier) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Item_StandardItemIdentification">
  <assert test="$CIUS-VD-88" id="CIUS-VD-88" flag="fatal"> [CIUS-VD-88] BT-157 (Item standard identifier) -BT maximum length shall be 35 chars. 
        </assert>
  <assert test="$CIUS-VD-90" id="CIUS-VD-90" flag="fatal"> [CIUS-VD-90] BT-157-1 (Item standard identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_OrderLineReference">
  <assert test="$CIUS-VD-96" id="CIUS-VD-96" flag="fatal"> [CIUS-VD-96] BT-132 (Referenced purchase order line reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="$InvoiceLine_Price">
  <assert test="$CIUS-VD-83" id="CIUS-VD-83" flag="fatal"> [CIUS-VD-83] BT-146 (Item net price) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </assert>
  <assert test="$CIUS-VD-95" id="CIUS-VD-95" flag="fatal"> [CIUS-VD-95] BT-146 (Item net price) -BT allowed fraction digits shall be 8. 
        </assert>
   </rule>
   <rule context="$LegalMonetaryTotal">
  <assert test="$CIUS-VD-62" id="CIUS-VD-62" flag="fatal"> [CIUS-VD-62] BT-112 (Invoice total amount with VAT) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
  <assert test="$CIUS-VD-63" id="CIUS-VD-63" flag="fatal"> [CIUS-VD-63] BT-115 (Amount due for payment) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
  <assert test="$CIUS-VD-65" id="CIUS-VD-65" flag="fatal"> [CIUS-VD-65] BT-114 (Rounding amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
   </rule>
   <rule context="$OrderReference">
  <assert test="$CIUS-VD-35" id="CIUS-VD-35" flag="fatal"> [CIUS-VD-35] BT-13 (Purchase order reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="$OriginatorDocumentReference">
  <assert test="$CIUS-VD-37" id="CIUS-VD-37" flag="fatal"> [CIUS-VD-37] BT-17 (Tender or lot reference) -BT maximum length shall be 15 chars. 
        </assert>
   </rule>
   <rule context="$Payee_PartyName">
  <assert test="$CIUS-VD-50" id="CIUS-VD-50" flag="fatal"> [CIUS-VD-50] BT-59 (Payee name) -BT maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="$PaymentMeans">
  <assert test="$CIUS-VD-55" id="CIUS-VD-55" flag="fatal"> [CIUS-VD-55] BT-82 (Payment means text) -BT maximum length shall be 200 chars. 
        </assert>
  <assert test="$CIUS-VD-56" id="CIUS-VD-56" flag="fatal"> [CIUS-VD-56] BT-83 (Remittance information) -BT maximum length shall be 60 chars. 
        </assert>
   </rule>
   <rule context="$PaymentMeans_PayeeFinancialAccount">
  <assert test="$CIUS-BT-84" id="CIUS-BT-84" flag="fatal"> [CIUS-BT-84] BT-84 (Payment account identifier)  shall be an IBAN code. 
        </assert>
  <assert test="$CIUS-VD-57" id="CIUS-VD-57" flag="fatal"> [CIUS-VD-57] BT-84 (Payment account identifier) -BT minimum length shall be 15, maximum length shall be 34 chars. 
        </assert>
  <assert test="$CIUS-VD-58" id="CIUS-VD-58" flag="fatal"> [CIUS-VD-58] BT-85 (Payment account name) -BT maximum length shall be 200 chars. 
        </assert>
   </rule>
   <rule context="$PaymentMeans_PayeeFinancialAccount_FinancialInstitutionBranch">
  <assert test="$CIUS-VD-59" id="CIUS-VD-59" flag="fatal"> [CIUS-VD-59] BT-86 (Payment service provider identifier) -BT minimum length shall be 8 maximum length shall be 11 chars. 
        </assert>
   </rule>
   <rule context="$ProjectReference">
  <assert test="$CIUS-VD-33" id="CIUS-VD-33" flag="fatal"> [CIUS-VD-33] BT-11 (Project reference) -BT maximum length shall be 15 chars. 
        </assert>
   </rule>
   <rule context="$ReceiptDocumentReference">
  <assert test="$CIUS-VD-36" id="CIUS-VD-36" flag="fatal"> [CIUS-VD-36] BT-15 (Receiving advice reference) -BT maximum length shall be 20 chars. 
        </assert>
   </rule>
   <rule context="$TaxRepresentative_PartyName">
  <assert test="$CIUS-VD-19" id="CIUS-VD-19" flag="fatal"> [CIUS-VD-19] BT-62 (Seller tax representative name) -BT maximum length shall be 80 chars. 
        </assert>
   </rule>
   <rule context="$TaxRepresentative_PartyTaxScheme">
  <assert test="$CIUS-VD-42" id="CIUS-VD-42" flag="fatal"> [CIUS-VD-42] BT-63 (Seller tax representative VAT identifier) -BT maximum length shall be 30 chars. 
        </assert>
   </rule>
   <rule context="$TaxTotal_TaxSubtotal">
  <assert test="$CIUS-VD-66" id="CIUS-VD-66" flag="fatal"> [CIUS-VD-66] BT-116 (VAT category taxable amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
  <assert test="$CIUS-VD-67" id="CIUS-VD-67" flag="fatal"> [CIUS-VD-67] BT-117 (VAT category tax amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
   </rule>
   <rule context="$TaxTotal_TaxSubtotal_TaxCategory">
  <assert test="$CIUS-VD-68" id="CIUS-VD-68" flag="fatal"> [CIUS-VD-68] BT-120 (VAT exemption reason text) -BT maximum length shall be 100 chars. 
        </assert>
   </rule>
</pattern>