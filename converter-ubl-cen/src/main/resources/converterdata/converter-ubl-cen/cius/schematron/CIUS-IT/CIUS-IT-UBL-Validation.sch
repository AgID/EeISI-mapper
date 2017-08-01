<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="syntax" id="CIUS-IT">
<param name="CIUS-BT-2_CONTEXT" value="cac:AccountingCustomerParty/cac:Party"/>
<param name="CIUS-BT-2" value="exists(cbc:EndpointID)"/>
<param name="CIUS-BT-98_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme"/>
<param name="CIUS-BT-98" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CompanyID)" /> <!--and exists(cbc:CompanyID)"/> -->
<param name="CIUS-BT-84_CONTEXT" value="cac:PaymentMeans/cac:PayeeFinancialAccount"/>
<param name="CIUS-BT-84" value="matches(cbc:ID, '([A-Z,0-9]{15,34})')" flag="fatal"/>
<param name="CIUS-CA-9_CONTEXT" value= "//ubl:Invoice"/>
<param name="CIUS-CA-9-1" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID)"/>
<param name="CIUS-CA-9-2" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID)"/>
<param name="CIUS-CA-10_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-CA-10-1" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)"/>
<param name="CIUS-CA-10-2" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)"/>
<param name="CIUS-CA-10-3" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)"/>
<param name="CIUS-CA-11_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-CA-11-1" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)"/>
<param name="CIUS-CA-11-2" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)"/>
<param name="CIUS-CA-11-3" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)"/>
<param name="CIUS-CA-12_CONTEXT" value="cac:Delivery/cac:DeliveryLocation/cac:Address"/>
<param name="CIUS-CA-12-1" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:StreetName))"/>
<param name="CIUS-CA-12-2" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:CityName))"/>
<param name="CIUS-CA-12-3" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:PostalZone))"/>
<param name="CIUS-CA-71_CONTEXT" value="cac:AdditionalDocumentReference"/>
<param name="CIUS-CA-71" value="exists(cac:Attachment/cac:ExternalReference/cbc:URI) or exists(cac:Attachment/cbc:EmbeddedDocumentBinaryObject)"/>
<param name="CIUS-SD-73_CONTEXT" value="cac:InvoiceLine"/>
<param name="CIUS-SD-73" value="number(cbc:ID) &gt; 0"/>
<param name="CIUS-CI-13_CONTEXT" value="//ubl:Invoice"/>
<param name="CIUS-CI-13" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or matches(cbc:TaxCurrencyCode, 'EUR')"/>
<param name="CIUS-BR-14_CONTEXT" value="//ubl:Invoice/cac:AccountingCustomerParty/cac:Party"/>
<param name="CIUS-BR-14" value="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )"/>
<param name="CIUS-VD-15_CONTEXT" value="cac:DespatchDocumentReference"/>
<param name="CIUS-VD-15" value="matches(cbc:ID, '/([0-9]{1,9})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})/')"/>
<param name="CIUS-VD-16_CONTEXT" value="cac:DespatchDocumentReference"/>
<param name="CIUS-VD-16" value="string-length(cbc:ID) &lt;= 30"/>
<param name="CIUS-VD-17_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity"/>
<param name="CIUS-VD-17" value="string-length(cbc:RegistrationName) &lt;= 80"/>
<param name="CIUS-VD-18_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity"/>
<param name="CIUS-VD-18" value="string-length(cbc:RegistrationName) &lt;= 80"/>
<param name="CIUS-VD-19_CONTEXT" value="cac:TaxRepresentativeParty/cac:PartyName"/>
<param name="CIUS-VD-19" value="string-length(cbc:Name) &lt;= 80"/>
<param name="CIUS-VD-20_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-20" value="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180"/>
<param name="CIUS-VD-21_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-21" value="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180"/>
<param name="CIUS-VD-22_CONTEXT" value="cac:Delivery/cac:DeliveryLocation/cac:Address"/>
<param name="CIUS-VD-22" value="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180"/>
<param name="CIUS-VD-23_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-23" value="string-length(cbc:CityName) &lt;= 60"/>
<param name="CIUS-VD-24_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-24" value="string-length(cbc:CityName) &lt;= 60"/>
<param name="CIUS-VD-25_CONTEXT" value="cac:Delivery/cac:DeliveryLocation/cac:Address"/>
<param name="CIUS-VD-25" value="string-length(cbc:CityName) &lt;= 60"/>
<param name="CIUS-VD-26_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-26-1" value="string-length(cbc:PostalZone) &lt;= 15"/>
<param name="CIUS-VD-26-2" value="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"/>
<param name="CIUS-VD-27_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-27-1" value="string-length(cbc:PostalZone) &lt;= 15"/>
<param name="CIUS-VD-27-2" value="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"/>
<param name="CIUS-VD-28_CONTEXT" value="cac:Delivery/cac:DeliveryLocation/cac:Address"/>
<param name="CIUS-VD-28-1" value="string-length(cbc:PostalZone) &lt;= 15"/>
<param name="CIUS-VD-28-2" value="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"/>
<param name="CIUS-VD-29_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-29" value="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2"/>
<param name="CIUS-VD-30_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-30" value="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2"/>
<param name="CIUS-VD-31_CONTEXT" value="cac:Delivery/cac:DeliveryLocation/cac:Address"/>
<param name="CIUS-VD-31" value="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2"/>
<param name="CIUS-VD-32_CONTEXT" value="//ubl:Invoice"/>
<param name="CIUS-VD-32" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-33_CONTEXT" value="cac:ProjectReference"/>
<param name="CIUS-VD-33" value="string-length(cbc:ID) &lt;= 15"/>
<param name="CIUS-VD-34_CONTEXT" value="cac:ContractDocumentReference"/>
<param name="CIUS-VD-34" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-35_CONTEXT" value="cac:OrderReference"/>
<param name="CIUS-VD-35" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-36_CONTEXT" value="cac:ReceiptDocumentReference"/>
<param name="CIUS-VD-36" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-37_CONTEXT" value="cac:OriginatorDocumentReference"/>
<param name="CIUS-VD-37" value="string-length(cbc:ID) &lt;= 15"/>
<param name="CIUS-VD-38_CONTEXT" value="cac:InvoiceLine"/>
<param name="CIUS-VD-38" value="string-length(cbc:AccountingCost) &lt;= 20"/>
<param name="CIUS-VD-39_CONTEXT" value="//ubl:Invoice"/>
<param name="CIUS-VD-39" value="string-length(cbc:Note) &lt;= 200"/>
<param name="CIUS-VD-40_CONTEXT" value="cac:BillingReference/cac:InvoiceDocumentReference"/>
<param name="CIUS-VD-40" value="string-length(cbc:ID) &lt;= 20"/>
<param name="CIUS-VD-41_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme"/>
<param name="CIUS-VD-41" value="string-length(cbc:CompanyID) &lt;= 30"/>
<param name="CIUS-VD-42_CONTEXT" value="cac:TaxRepresentativeParty/cac:PartyTaxScheme"/>
<param name="CIUS-VD-42" value="string-length(cbc:CompanyID) &lt;= 30"/>
<param name="CIUS-VD-43_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme"/>
<param name="CIUS-VD-43" value="string-length(cbc:CompanyID) &lt;= 30"/>
<param name="CIUS-VD-44_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:Contact"/>
<param name="CIUS-VD-44" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-45_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:Contact"/>
<param name="CIUS-VD-45" value="string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) &gt;= 5"/>
<param name="CIUS-VD-46_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:Contact"/>
<param name="CIUS-VD-46" value="string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) &gt;= 7"/>
<param name="CIUS-VD-47_CONTEXT" value="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-47" value="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )"/>
<param name="CIUS-VD-48_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
<param name="CIUS-VD-48" value="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )"/>
<param name="CIUS-VD-49_CONTEXT" value="cac:Delivery/cac:DeliveryLocation/cac:Address"/>
<param name="CIUS-VD-49" value="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )"/>
<param name="CIUS-VD-50_CONTEXT" value="cac:PayeeParty/cac:PartyName"/>
<param name="CIUS-VD-50" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-51_CONTEXT" value="cac:AccountingCustomerParty/cac:Party/cac:Contact"/>
<param name="CIUS-VD-51" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-53_CONTEXT" value="cac:AccountingCustomerParty/cac:Party"/>
<param name="CIUS-VD-53" value="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )"/>
<param name="CIUS-VD-55_CONTEXT" value="cac:PaymentMeans"/>
<param name="CIUS-VD-55" value="string-length(cbc:InstructionNote) &lt;= 200"/>
<param name="CIUS-VD-56_CONTEXT" value="cac:PaymentMeans"/>
<param name="CIUS-VD-56" value="string-length(cbc:PaymentID) &lt;= 60"/>
<param name="CIUS-VD-57_CONTEXT" value="cac:PaymentMeans/cac:PayeeFinancialAccount"/>
<param name="CIUS-VD-57" value="string-length(cbc:ID) &lt;= 34 and string-length(cbc:ID) &gt;= 15"/>
<param name="CIUS-VD-58_CONTEXT" value="cac:PaymentMeans/cac:PayeeFinancialAccount"/>
<param name="CIUS-VD-58" value="string-length(cbc:Name) &lt;= 200"/>
<param name="CIUS-VD-59_CONTEXT" value="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch"/>
<param name="CIUS-VD-59" value="string-length(cbc:ID) &lt;= 11 and string-length(cbc:ID) &gt;= 8"/>
<param name="CIUS-VD-60_CONTEXT" value="cac:AllowanceCharge"/>
<param name="CIUS-VD-60" value="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000"/>
<param name="CIUS-VD-61_CONTEXT" value="cac:AllowanceCharge"/>
<param name="CIUS-VD-61" value="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000"/>
<param name="CIUS-VD-62_CONTEXT" value="cac:LegalMonetaryTotal"/>
<param name="CIUS-VD-62" value="string-length(cbc:TaxInclusiveAmount) &lt;= 15 and string-length(cbc:TaxInclusiveAmount) &gt;= 4"/>
<param name="CIUS-VD-63_CONTEXT" value="cac:LegalMonetaryTotal"/>
<param name="CIUS-VD-63" value="string-length(cbc:PayableAmount) &lt;= 15 and string-length(cbc:PayableAmount) &gt;= 4"/>
<param name="CIUS-VD-64_CONTEXT" value="cac:AllowanceCharge"/>
<param name="CIUS-VD-64" value="string-length(cbc:Amount) &lt;= 21 and string-length(cbc:Amount) &gt;= 4"/>
<param name="CIUS-VD-65_CONTEXT" value="cac:LegalMonetaryTotal"/>
<param name="CIUS-VD-65" value="not(exists(cbc:PayableRoundingAmount)) or (string-length(cbc:PayableRoundingAmount) &lt;= 15 and string-length(cbc:PayableRoundingAmount) &gt;= 4)"/>
<param name="CIUS-VD-66_CONTEXT" value="cac:TaxTotal/cac:TaxSubtotal"/>
<param name="CIUS-VD-66" value="string-length(cbc:TaxableAmount) &lt;= 15 and string-length(cbc:TaxableAmount) &gt;= 4"/>
<param name="CIUS-VD-67_CONTEXT" value="cac:TaxTotal/cac:TaxSubtotal"/>
<param name="CIUS-VD-67" value="string-length(cbc:TaxAmount) &lt;= 15 and string-length(cbc:TaxAmount) &gt;= 4"/>
<param name="CIUS-VD-68_CONTEXT" value="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory"/>
<param name="CIUS-VD-68" value="string-length(cbc:TaxExemptionReason) &lt;= 100"/>
<param name="CIUS-VD-69_CONTEXT" value="cac:AdditionalDocumentReference"/>
<param name="CIUS-VD-69" value="(string-length(cbc:ID) + string-length(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename)) &lt;= 60"/>
<param name="CIUS-VD-70_CONTEXT" value="cac:AdditionalDocumentReference"/>
<param name="CIUS-VD-70" value="string-length(cbc:DocumentType) &lt;= 100"/>
<param name="CIUS-VD-72_CONTEXT" value="cac:AdditionalDocumentReference/cac:Attachment"/>
<param name="CIUS-VD-72" value="string-length(cbc:EmbeddedDocumentBinaryObject) &lt;= 10"/>
<param name="CIUS-VD-74_CONTEXT" value="cac:InvoiceLine"/>
<param name="CIUS-VD-74" value="string-length(cbc:ID) &lt;= 4"/>
<param name="CIUS-VD-75_CONTEXT" value="cac:InvoiceLine"/>
<param name="CIUS-VD-75" value="string-length(cbc:Note) &lt;= 60"/>
<param name="CIUS-VD-76_CONTEXT" value="cac:InvoiceLine/cac:DocumentReference"/>
<param name="CIUS-VD-76" value="string-length(cbc:ID/@schemeID) &lt;= 35"/>
<param name="CIUS-VD-77_CONTEXT" value="cac:InvoiceLine/cac:DocumentReference"/>
<param name="CIUS-VD-77" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-78_CONTEXT" value="cac:InvoiceLine"/>
<param name="CIUS-VD-78-1" value="string-length(cbc:InvoicedQuantity/@unitCode) &lt;= 10"/>
<param name="CIUS-VD-78-2" value="string-length(cac:Price/cbc:BaseQuantity) &lt;= 10"/>
<param name="CIUS-VD-78-3" value="string-length(cac:Price/cbc:BaseQuantity/@unitCode) &lt;= 10"/>
<param name="CIUS-VD-79_CONTEXT" value="cac:InvoiceLine"/>
<param name="CIUS-VD-79" value="string-length(cbc:AccountingCost) &lt;= 20"/>
<param name="CIUS-VD-80_CONTEXT" value="cac:InvoiceLine/cac:AllowanceCharge"/>
<param name="CIUS-VD-80" value="string-length(cbc:Amount) &gt;= 4 and string-length(cbc:Amount) &lt;= 21"/>
<param name="CIUS-VD-81_CONTEXT" value="cac:InvoiceLine/cac:AllowanceCharge"/>
<param name="CIUS-VD-81-1" value="string-length(cbc:AllowanceChargeReason) &lt;= 1000"/>
<param name="CIUS-VD-81-2" value="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000"/>
<param name="CIUS-VD-82_CONTEXT" value="cac:InvoiceLine/cac:AllowanceCharge"/>
<param name="CIUS-VD-82-1" value="string-length(cbc:AllowanceChargeReason) &lt;= 1000"/>
<param name="CIUS-VD-82-2" value="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000"/>
<param name="CIUS-VD-83_CONTEXT" value="cac:InvoiceLine/cac:Price"/>
<param name="CIUS-VD-83" value="string-length(cbc:PriceAmount) &lt;= 21 and string-length(cbc:PriceAmount) &gt;= 4"/>
<param name="CIUS-VD-85_CONTEXT" value="cac:InvoiceLine/cac:Item"/>
<param name="CIUS-VD-85-1" value="string-length(cbc:Name) &lt;= 1000"/>
<param name="CIUS-VD-85-2" value="string-length(cbc:Description) &lt;= 1000"/>
<param name="CIUS-VD-86_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:SellersItemIdentification"/>
<param name="CIUS-VD-86" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-87_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification"/>
<param name="CIUS-VD-87" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-88_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification"/>
<param name="CIUS-VD-88" value="string-length(cbc:ID) &lt;= 35"/>
<param name="CIUS-VD-89_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:CommodityClassification"/>
<param name="CIUS-VD-89" value="string-length(cbc:ItemClassificationCode) &lt;= 35"/>
<param name="CIUS-VD-90_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification"/>
<param name="CIUS-VD-90" value="string-length(cbc:ID/@schemeID) &lt;= 35"/>
<param name="CIUS-VD-91_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode"/>
<param name="CIUS-VD-91-1" value="string-length(@listVersionID) &lt;= 35"/>
<param name="CIUS-VD-91-2" value="string-length(@listID) &lt;= 35"/>
<param name="CIUS-VD-92_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:OriginCountry"/>
<param name="CIUS-VD-92" value="string-length(cbc:IdentificationCode) &lt;= 60" />
<param name="CIUS-VD-93_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty"/>
<param name="CIUS-VD-93" value="string-length(cbc:Name) &lt;= 10" />
<param name="CIUS-VD-94_CONTEXT" value="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty"/>
<param name="CIUS-VD-94" value="string-length(cbc:Value) &lt;= 60"/>
<param name="CIUS-VD-95_CONTEXT" value="cac:InvoiceLine/cac:Price"/>
<param name="CIUS-VD-95" value="matches(cbc:PriceAmount, '^[0-9]+(\.[0-9]{0,8})*$')"/>
<param name="CIUS-VD-96_CONTEXT" value="cac:InvoiceLine/cac:OrderLineReference"/>
<param name="CIUS-VD-96" value="string-length(cbc:LineID) &lt;= 20"/>
<param name="CIUS-VD-97_CONTEXT" value="cac:AccountingCustomerParty/cac:Party"/>
<param name="CIUS-VD-97-1" value="not(cbc:EndpointID/@schemeID = ' PEC') or ( string-length(cbc:EndpointID) &gt;= 7  and string-length(cbc:EndpointID) &lt;= 256 )"/>
<param name="CIUS-VD-97-2" value="not(cbc:EndpointID/@schemeID = ' IPA') or ( string-length(cbc:EndpointID) &lt;= 6 )"/>
<param name="CIUS-VD-97-3" value="not(cbc:EndpointID/@schemeID = ' CodiceUfficio') or ( string-length(cbc:EndpointID) &lt;= 7 )"/>
<param name="CIUS-VD-99_CONTEXT" value="//ubl:Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
<param name="CIUS-VD-99" value="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or ( ( ( not(contains(normalize-space(.),' ')) and contains( ' RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19',concat(' ',normalize-space(.),' ') ) ) ) )"/>
</pattern>