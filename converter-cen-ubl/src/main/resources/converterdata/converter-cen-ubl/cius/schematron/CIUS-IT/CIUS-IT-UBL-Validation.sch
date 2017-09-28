<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="syntax" id="CIUS-IT">
<param name="TaxTotal_TaxSubtotal_TaxCategory" value="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory"/>
<param name="TaxTotal_TaxSubtotal" value="cac:TaxTotal/cac:TaxSubtotal"/>
<param name="TaxRepresentative_PartyTaxScheme" value="cac:TaxRepresentativeParty/cac:PartyTaxScheme"/>
<param name="TaxRepresentative_PartyName" value="cac:TaxRepresentativeParty/cac:PartyName"/>
<param name="ReceiptDocumentReference" value="cac:ReceiptDocumentReference"/>
<param name="ProjectReference" value="cac:ProjectReference"/>
<param name="PaymentMeans_PayeeFinancialAccount_FinancialInstitutionBranch" value="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch"/>
<param name="PaymentMeans_PayeeFinancialAccount" value="cac:PaymentMeans/cac:PayeeFinancialAccount"/>
<param name="PaymentMeans" value="cac:PaymentMeans"/>
<param name="Payee_PartyName" value="cac:PayeeParty/cac:PartyName"/>
<param name="OriginatorDocumentReference" value="cac:OriginatorDocumentReference"/>
<param name="OrderReference" value="cac:OrderReference"/>
<param name="LegalMonetaryTotal" value="cac:LegalMonetaryTotal"/>
<param name="InvoiceLine_Price" value="cac:InvoiceLine/cac:Price"/>
<param name="InvoiceLine_OrderLineReference" value="cac:InvoiceLine/cac:OrderLineReference"/>
<param name="InvoiceLine_Item_StandardItemIdentification" value="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification"/>
<param name="InvoiceLine_Item_SellersItemIdentification" value="cac:InvoiceLine/cac:Item/cac:SellersItemIdentification"/>
<param name="InvoiceLine_Item_OriginCountry" value="cac:InvoiceLine/cac:Item/cac:OriginCountry"/>
<param name="InvoiceLine_Item_CommodityClassification_ItemClassificationCode" value="cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode"/>
<param name="InvoiceLine_Item_CommodityClassification" value="cac:InvoiceLine/cac:Item/cac:CommodityClassification"/>
<param name="InvoiceLine_Item_BuyersItemIdentification" value="cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification"/>
<param name="InvoiceLine_Item_AdditionalItemProperty" value="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty"/>
<param name="InvoiceLine_Item" value="cac:InvoiceLine/cac:Item"/>
<param name="InvoiceLine_DocumentReference" value="cac:InvoiceLine/cac:DocumentReference"/>
<param name="InvoiceLine_AllowanceCharge" value="cac:InvoiceLine/cac:AllowanceCharge"/>
<param name="InvoiceLine" value="cac:InvoiceLine"/>
<param name="Invoice_Root" value= "//ubl:Invoice"/>
<param name="Despatch_DocumentReference" value="cac:DespatchDocumentReference"/>
<param name="Delivery_DeliveryLocation_Address" value="cac:Delivery/cac:DeliveryLocation/cac:Address"/>
<param name="Contract_DocumentReference" value="cac:ContractDocumentReference"/>
<param name="CIUS-VD-99" value="not(../cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (cac:TaxScheme/cbc:ID='VAT') or contains( 'RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19',cbc:CompanyID)"/>
<param name="CIUS-VD-97-3" value="not(cbc:EndpointID/@schemeID = 'IT:CODDEST') or ( string-length(cbc:EndpointID) = 7 )"/>
<param name="CIUS-VD-97-2" value="not(cbc:EndpointID/@schemeID = 'IT:IPA') or ( string-length(cbc:EndpointID) = 6 )"/>
<param name="CIUS-VD-97-1" value="not(cbc:EndpointID/@schemeID = 'IT:PEC') or ( string-length(cbc:EndpointID) &gt;= 7  and string-length(cbc:EndpointID) &lt;= 256 )"/>
<param name="CIUS-VD-96" value="string-length(cbc:LineID) &lt;= 20"/>
<param name="CIUS-VD-95" value="matches(cbc:PriceAmount, '^[0-9]+(\.[0-9]{0,8})*$')"/>
<param name="CIUS-VD-94" value="string-length(cbc:Value) &lt;= 60"/>
<param name="CIUS-VD-93" value="string-length(cbc:Name) &lt;= 10" />
<param name="CIUS-VD-92" value="string-length(cbc:IdentificationCode) &lt;= 60" />
<param name="CIUS-VD-91-2" value="string-length(@listID) &lt;= 35"/>
<param name="CIUS-VD-91-1" value="string-length(@listVersionID) &lt;= 35"/>
<param name="CIUS-VD-90" value="string-length(cbc:ID/@schemeID) &lt;= 35"/>
<param name="CIUS-VD-89" value="string-length(cbc:ItemClassificationCode) &lt;= 35"/>
<param name="CIUS-VD-88" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-87" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-86" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-85-2" value="string-length(cbc:Description) &lt;= 1000"/>
<param name="CIUS-VD-85-1" value="string-length(cbc:Name) &lt;= 1000"/>
<param name="CIUS-VD-83" value="string-length(cbc:PriceAmount) &lt;= 21 and string-length(cbc:PriceAmount) &gt;= 4"/>
<param name="CIUS-VD-82-2" value="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000"/>
<param name="CIUS-VD-82-1" value="string-length(cbc:AllowanceChargeReason) &lt;= 1000"/>
<param name="CIUS-VD-81-2" value="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000"/>
<param name="CIUS-VD-81-1" value="string-length(cbc:AllowanceChargeReason) &lt;= 1000"/>
<param name="CIUS-VD-80" value="string-length(cbc:Amount) &gt;= 4 and string-length(cbc:Amount) &lt;= 21"/>
<param name="CIUS-VD-79" value="string-length(cbc:AccountingCost) &lt;= 20"/>
<param name="CIUS-VD-78-3" value="string-length(cac:Price/cbc:BaseQuantity/@unitCode) &lt;= 10"/>
<param name="CIUS-VD-78-2" value="string-length(cac:Price/cbc:BaseQuantity) &lt;= 10"/>
<param name="CIUS-VD-78-1" value="string-length(cbc:InvoicedQuantity/@unitCode) &lt;= 10"/>
<param name="CIUS-VD-77" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-76" value="string-length(cbc:ID/@schemeID) &lt;= 35"/>
<param name="CIUS-VD-75" value="string-length(cbc:Note) &lt;= 60"/>
<param name="CIUS-VD-74" value="string-length(cbc:ID) &lt;= 4"/>
<param name="CIUS-VD-72" value="string-length(EmbeddedDocumentBinaryObject/@mimeCode) &lt;= 10"/>
<param name="CIUS-VD-70" value="string-length(cbc:DocumentType) &lt;= 100"/>
<param name="CIUS-VD-69" value="(string-length(cbc:ID) + string-length(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename)) &lt;= 60"/>
<param name="CIUS-VD-68" value="string-length(cbc:TaxExemptionReason) &lt;= 100"/>
<param name="CIUS-VD-67" value="string-length(cbc:TaxAmount) &lt;= 15 and string-length(cbc:TaxAmount) &gt;= 4"/>
<param name="CIUS-VD-66" value="string-length(cbc:TaxableAmount) &lt;= 15 and string-length(cbc:TaxableAmount) &gt;= 4"/>
<param name="CIUS-VD-65" value="not(exists(cbc:PayableRoundingAmount)) or (string-length(cbc:PayableRoundingAmount) &lt;= 15 and string-length(cbc:PayableRoundingAmount) &gt;= 4)"/>
<param name="CIUS-VD-64" value="string-length(cbc:Amount) &lt;= 21 and string-length(cbc:Amount) &gt;= 4"/>
<param name="CIUS-VD-63" value="string-length(cbc:PayableAmount) &lt;= 15 and string-length(cbc:PayableAmount) &gt;= 4"/>
<param name="CIUS-VD-62" value="string-length(cbc:TaxInclusiveAmount) &lt;= 15 and string-length(cbc:TaxInclusiveAmount) &gt;= 4"/>
<param name="CIUS-VD-61" value="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000"/>
<param name="CIUS-VD-60" value="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000"/>
<param name="CIUS-VD-59" value="string-length(cbc:ID) &lt;= 11 and string-length(cbc:ID) &gt;= 8"/>
<param name="CIUS-VD-58" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-57" value="string-length(cbc:ID) &lt;= 34 and string-length(cbc:ID) &gt;= 15"/>
<param name="CIUS-VD-56" value="string-length(cbc:PaymentID) &lt;= 60"/>
<param name="CIUS-VD-55" value="string-length(cbc:InstructionNote) &lt;= 200"/>
<param name="CIUS-VD-53" value="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )"/>
<param name="CIUS-VD-51" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-50" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-49" value="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )"/>
<param name="CIUS-VD-48" value="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',cbc:CountrySubentity )"/>
<param name="CIUS-VD-47" value="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',cbc:CountrySubentity )"/>
<param name="CIUS-VD-46" value="not(exists(cbc:ElectronicMail)) or (string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) &gt;= 7)"/>
<param name="CIUS-VD-45" value="not(exists(cbc:Telephone)) or (string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) &gt;= 5)"/>
<param name="CIUS-VD-44" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-43" value="string-length(cbc:CompanyID) &lt;= 30"/>
<param name="CIUS-VD-42" value="string-length(cbc:CompanyID) &lt;= 30"/>
<param name="CIUS-VD-41" value="string-length(cbc:CompanyID) &lt;= 30"/>
<param name="CIUS-VD-40" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-39" value="string-length(cbc:Note) &lt;= 200"/>
<param name="CIUS-VD-38" value="string-length(cbc:AccountingCost) &lt;= 20"/>
<param name="CIUS-VD-37" value="string-length(cbc:ID) &lt;= 15"/>
<param name="CIUS-VD-36" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-35" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-34" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-33" value="string-length(cbc:ID) &lt;= 15"/>
<param name="CIUS-VD-32" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-31" value="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2"/>
<param name="CIUS-VD-30" value="not(cac:Country/cbc:IdentificationCode = 'IT') or not(exists(cbc:CountrySubentity)) or string-length(cbc:CountrySubentity) = 2"/>
<param name="CIUS-VD-29" value="not(cac:Country/cbc:IdentificationCode = 'IT') or not(exists(cbc:CountrySubentity)) or string-length(cbc:CountrySubentity) = 2"/>
<param name="CIUS-VD-28-2" value="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"/>
<param name="CIUS-VD-28-1" value="string-length(cbc:PostalZone) &lt;= 15"/>
<param name="CIUS-VD-27-2" value="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"/>
<param name="CIUS-VD-27-1" value="string-length(cbc:PostalZone) &lt;= 15"/>
<param name="CIUS-VD-26-2" value="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"/>
<param name="CIUS-VD-26-1" value="string-length(cbc:PostalZone) &lt;= 15"/>
<param name="CIUS-VD-25" value="string-length(cbc:CityName) &lt;= 60"/>
<param name="CIUS-VD-24" value="string-length(cbc:CityName) &lt;= 60"/>
<param name="CIUS-VD-23" value="string-length(cbc:CityName) &lt;= 60"/>
<param name="CIUS-VD-22" value="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 60"/>
<param name="CIUS-VD-21" value="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 60"/>
<param name="CIUS-VD-20" value="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 60"/>
<param name="CIUS-VD-19" value="string-length(cbc:Name) &lt;= 80"/>
<param name="CIUS-VD-18" value="string-length(cbc:RegistrationName) &lt;= 80"/>
<param name="CIUS-VD-17" value="string-length(cbc:RegistrationName) &lt;= 80"/>
<param name="CIUS-VD-16" value="string-length(cbc:ID) &lt;= 31"/>
<param name="CIUS-VD-15" value="matches(cbc:ID, '([0-9]{1,20})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})')"/>
<param name="CIUS-VD-102-2" value="not (cbc:ID/@schemeID = 'IT:ALBO')  or (string-length(cbc:CompanyID) &lt;=30)"/>
<param name="CIUS-VD-102-1" value="not (cbc:ID/@schemeID = 'IT:REA')  or ( (string-length(cbc:CompanyID) &gt;= 3) and (string-length(cbc:CompanyID) &lt;=22) and( contains( 'AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',substring(cbc:CompanyID,1,2) )))"/>
<param name="CIUS-VD-101-3" value="not (cbc:ID/@schemeID = 'IT:VAT')  or ( (string-length(cbc:ID) &lt;= 30) and ( contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW',substring(cbc:ID,1,2) ) ))"/>
<param name="CIUS-VD-101-2" value="not (cbc:ID/@schemeID = 'IT:EORI')  or ( (string-length(cbc:ID) &gt;= 13) and (string-length(cbc:ID) &lt;=17))"/>
<param name="CIUS-VD-101-1" value="not (cbc:ID/@schemeID = 'IT:CF')  or ( (string-length(cbc:ID) &gt;= 11) and (string-length(cbc:ID) &lt;=16))"/>
<param name="CIUS-VD-100-3" value="not (cbc:ID/@schemeID = 'IT:VAT')  or ( (string-length(cbc:ID) &lt;= 30) and (contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW',substring(cbc:ID,1,2) ) ))"/>
<param name="CIUS-VD-100-2" value="not (cbc:ID/@schemeID = 'IT:EORI')  or ( (string-length(cbc:ID) &gt;= 13) and (string-length(cbc:ID) &lt;=17))"/>
<param name="CIUS-VD-100-1" value="not (cbc:ID/@schemeID = 'IT:CF')  or ( (string-length(cbc:ID) &gt;= 11) and (string-length(cbc:ID) &lt;=16))"/>
<param name="CIUS-SD-73" value="number(cbc:ID) &gt; 0 and number(cbc:ID) &lt;=9999"/>
<param name="CIUS-CI-13" value="not(exists(cbc:TaxCurrencyCode)) or matches(cbc:TaxCurrencyCode, 'EUR')"/>
<param name="CIUS-CA-9" value=" exists(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) or exists(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID)"/>
<param name="CIUS-CA-71" value="exists(cac:Attachment/cac:ExternalReference/cbc:URI) or exists(cac:Attachment/cbc:EmbeddedDocumentBinaryObject)"/>
<param name="CIUS-CA-2" value="exists(cbc:EndpointID) and (cbc:EndpointID/@schemeID = 'IT:CODDEST' or cbc:EndpointID/@schemeID = 'IT:PEC' or cbc:EndpointID/@schemeID = 'IT:IPA' )"/>
<param name="CIUS-CA-12-3" value="exists(cbc:PostalZone)"/>
<param name="CIUS-CA-12-2" value="exists(cbc:CityName)"/>
<param name="CIUS-CA-12-1" value="exists(cbc:StreetName)"/>
<param name="CIUS-CA-11-3" value="exists(cbc:PostalZone)"/>
<param name="CIUS-CA-11-2" value="exists(cbc:CityName)"/>
<param name="CIUS-CA-11-1" value="exists(cbc:StreetName)"/>
<param name="CIUS-CA-10-3" value="exists(cbc:PostalZone)"/>
<param name="CIUS-CA-10-2" value="exists(cbc:CityName)"/>
<param name="CIUS-CA-10-1" value="exists(cbc:StreetName)"/>
<param name="CIUS-BT-98-2" value="not(cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or count(cac:PartyTaxScheme/cac:TaxScheme[not(cbc:ID='VAT')]) &gt;=1"/>
<param name="CIUS-BT-98-1" value="(cac:TaxScheme/cbc:ID='VAT') or ((../cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') and (exists(cbc:CompanyID)))"/>
<param name="CIUS-BT-84" value="matches(cbc:ID, '([a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{11,30})')"/>
<param name="CIUS-BR-14" value="exists(cac:PartyTaxScheme/cbc:CompanyID) or exists(cac:PartyIdentification/cbc:ID/@schemeID = 'IT:CF') or exists(cac:PartyIdentification/cbc:ID/@schemeID = 'IT:PIVA')"/>
<param name="BillingReference_InvoiceDocumentReference" value="cac:BillingReference/cac:InvoiceDocumentReference"/>
<param name="AllowanceCharge" value="cac:AllowanceCharge"/>
<param name="Additional_Document_Reference_Attachment" value="cac:AdditionalDocumentReference/cac:Attachment"/>
<param name="Additional_Document_Reference" value="cac:AdditionalDocumentReference"/>
<param name="Accounting_Supplier_TaxScheme" value="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme"/>
<param name="Accounting_Supplier_PartyLegalEntity" value="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity"/>
<param name="Accounting_Supplier_PartyLegalEntity" value="/cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity"/>
<param name="Accounting_Supplier_PartyIdentification" value="/cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification"/>
<param name="Accounting_Supplier_Party_PostalAddress" value="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
<param name="Accounting_Supplier_Party_Contact" value="cac:AccountingSupplierParty/cac:Party/cac:Contact"/>
<param name="Accounting_Supplier" value="cac:AccountingSupplierParty/cac:Party"/>
<param name="Accounting_Customer_PostalAddress" value="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
<param name="Accounting_Customer_PartyTaxScheme" value="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme"/>
<param name="Accounting_Customer_PartyLegalEntity" value="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity"/>
<param name="Accounting_Customer_PartyIdentification" value="/cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification"/>
<param name="Accounting_Customer_Party_Contact" value="cac:AccountingCustomerParty/cac:Party/cac:Contact"/>
<param name="Accounting_Customer_Party" value="cac:AccountingCustomerParty/cac:Party"/>
</pattern>