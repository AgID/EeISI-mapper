<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
        xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
        xmlns:UBL="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" queryBinding="xslt2">
    <title>EN16931 UBL CIUS</title>
    <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
    <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
    <ns prefix="ext" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"/>
    <ns prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"/>
    <ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
    <ns prefix="xs" uri="http://www.w3.org/2001/XMLSchema"/>

    <phase id="EN_16931_CIUS">

        <pattern name="CIUS-BT-2">
            <rule context="cac:AccountingCustomerParty/cac:Party">
                <assert test="exists(cbc:EndpointID)" flag="fatal">
                    [CIUS-BT-2]-BT-49 shall contain a legal mail address (PEC) or IndicePA/CodiceUfficio (see the
                    Italian business rules).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-BT-98">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme">
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CompanyID)"
                        flag="fatal">
                    [CIUS-BT-98]-BT is a conditional field and shall not be used by a foreign seller as it is not
                    possible to map into XMLPA. CEN business rules are not broken. In case the seller is Italian this
                    field shall contain the codification of RegimeFiscale (1.2.1.8).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-BT-84">
            <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount">
                <assert test="matches(cbc:ID, '([A-Z,0-9]{15,34})')" flag="fatal">
                    [CIUS-BT-84]-The payment account identifier shall be an IBAN code.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-CA-9">
            <rule context="//ubl:Invoice">
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID)"
                        flag="fatal">
                    [CIUS-CA-9]-Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax
                    representative).
                </assert>
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cac:TaxRepresentativeParty/cac:PartyTaxScheme/cbc:CompanyID)"
                        flag="fatal">
                    [CIUS-CA-9]-Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax
                    representative).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-CA-10">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)"
                        flag="fatal">
                    [CIUS-CA-10]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)"
                        flag="fatal">
                    [CIUS-CA-10]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)"
                        flag="fatal">
                    [CIUS-CA-10]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-CA-11">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:StreetName)"
                        flag="fatal">
                    [CIUS-CA-11]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:CityName)"
                        flag="fatal">
                    [CIUS-CA-11]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or exists(cbc:PostalZone)"
                        flag="fatal">
                    [CIUS-CA-11]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-CA-12">
            <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:StreetName))"
                        flag="fatal">
                    [CIUS-CA-12]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:CityName))"
                        flag="fatal">
                    [CIUS-CA-12]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or (not(exists(//ubl:Invoice/cac:ReceiptDocumentReference/cbc:ID)) or exists(cbc:PostalZone))"
                        flag="fatal">
                    [CIUS-CA-12]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-CA-71">
            <rule context="//ubl:Invoice">
                <assert test="exists(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:URI) or exists(cac:AdditionalDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject)"
                        flag="fatal">
                    [CIUS-CA-71]-If BT-124 is empty then the BT-125 should be mandatory as the mapped field is mandatory
                    in Italy.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-SD-73">
            <rule context="cac:InvoiceLine">
                <assert test="number(cbc:ID) &gt; 0" flag="fatal">
                    [CIUS-SD-73]-The BT value should be numeric.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-CI-13">
            <rule context="//ubl:Invoice">
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or matches(cbc:TaxCurrencyCode, 'EUR')"
                        flag="fatal">
                    [CIUS-CI-13]-VAT accounting currency code should be € for invoices from EU to IT in accordance with
                    2006/112/CE art. 9.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-BR-14">
            <rule context="//ubl:Invoice/cac:AccountingCustomerParty/cac:Party">
                <assert test="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )"
                        flag="fatal">
                    [CIUS-BR-14]-1.4.1.1 is not mandatory in Italy (buyer) but VAT number or Fiscal code should be
                    indicated.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-15">
            <rule context="cac:DespatchDocumentReference">
                <assert test="matches(cbc:ID, '/([0-9]{1,9})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})/')" flag="fatal">
                    [CIUS-VD-15]-BT will be structured as unique ID containing the despatch date as well (e.g.
                    123456789_2017-03-05).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-16">
            <rule context="cac:DespatchDocumentReference">
                <assert test="string-length(cbc:ID) &lt;= 30" flag="fatal">
                    [CIUS-VD-16]-BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-17">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity">
                <assert test="string-length(cbc:RegistrationName) &lt;= 80" flag="fatal">
                    [CIUS-VD-17]-BT maximum length shall be 80 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-18">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity">
                <assert test="string-length(cbc:RegistrationName) &lt;= 80" flag="fatal">
                    [CIUS-VD-18]-BT maximum length shall be 80 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-19">
            <rule context="cac:TaxRepresentativeParty/cac:PartyName">
                <assert test="string-length(cbc:Name) &lt;= 80" flag="fatal">
                    [CIUS-VD-19]-BT maximum length shall be 80 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-20">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
                <assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180"
                        flag="fatal">
                    [CIUS-VD-20]-The sum of BTs maximum length shall be 180 chars (including separator).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-21">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
                <assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180"
                        flag="fatal">
                    [CIUS-VD-21]-The sum of BTs maximum length shall be 180 chars (including separator).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-22">
            <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
                <assert test="(string-length(cbc:StreetName) + string-length(cbc:AdditionalStreetName) + string-length(cac:AddressLine/cbc:Line)) &lt;= 180"
                        flag="fatal">
                    [CIUS-VD-22]-The sum of BTs maximum length shall be 180 chars (including separator).
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-23">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
                <assert test="string-length(cbc:CityName) &lt;= 60" flag="fatal">
                    [CIUS-VD-23]-BT maximum length shall be 60 characters.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-24">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
                <assert test="string-length(cbc:CityName) &lt;= 60" flag="fatal">
                    [CIUS-VD-24]-BT maximum length shall be 60 characters.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-25">
            <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
                <assert test="string-length(cbc:CityName) &lt;= 60" flag="fatal">
                    [CIUS-VD-25]-BT maximum length shall be 60 characters.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-26">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
                <assert test="string-length(cbc:PostalZone) &lt;= 15" flag="fatal">
                    [CIUS-VD-26]-BT maximum length shall be 15 chars.
                </assert>
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"
                        flag="fatal">
                    [CIUS-VD-26]-BT maximum length, if country code =IT then it should be numeric and maximum length 5.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-27">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
                <assert test="string-length(cbc:PostalZone) &lt;= 15" flag="fatal">
                    [CIUS-VD-27]-BT maximum length shall be 15 chars.
                </assert>
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"
                        flag="fatal">
                    [CIUS-VD-27]-BT maximum length, if country code =IT then it should be numeric and maximum length 5.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-28">
            <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
                <assert test="string-length(cbc:PostalZone) &lt;= 15" flag="fatal">
                    [CIUS-VD-28]-BT maximum length shall be 15 chars.
                </assert>
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or ( string-length(cbc:PostalZone) &lt;= 5 and number(cbc:PostalZone) &gt; 0 )"
                        flag="fatal">
                    [CIUS-VD-28]-BT maximum length, if country code =IT then it should be numeric and maximum length 5.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-29">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2"
                        flag="fatal">
                    [CIUS-VD-29]-BT maximum length shall be 2 chars only used if country code=IT else the BT is not
                    used.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-30">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2"
                        flag="fatal">
                    [CIUS-VD-30]-BT maximum length shall be 2 chars only used if country code=IT else the BT is not
                    used.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-31">
            <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or string-length(cbc:CountrySubentity) = 2"
                        flag="fatal">
                    [CIUS-VD-31]-BT maximum length shall be 2 chars only used if country code=IT else the BT is not
                    used.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-32">
            <rule context="//ubl:Invoice">
                <assert test="string-length(cbc:ID) &lt;= 20" flag="fatal">
                    [CIUS-VD-32]-BT maximum length shall be 20 digits.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-33">
            <rule context="cac:ProjectReference">
                <assert test="string-length(cbc:ID) &lt;= 15" flag="fatal">
                    [CIUS-VD-33]-BT maximum length shall be 15 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-34">
            <rule context="cac:ContractDocumentReference">
                <assert test="string-length(cbc:ID) &lt;= 20" flag="fatal">
                    [CIUS-VD-34]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-35">
            <rule context="cac:OrderReference">
                <assert test="string-length(cbc:ID) &lt;= 20" flag="fatal">
                    [CIUS-VD-35]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-36">
            <rule context="cac:ReceiptDocumentReference">
                <assert test="string-length(cbc:ID) &lt;= 20" flag="fatal">
                    [CIUS-VD-36]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-37">
            <rule context="cac:OriginatorDocumentReference">
                <assert test="string-length(cbc:ID) &lt;= 15" flag="fatal">
                    [CIUS-VD-37]-BT maximum length shall be 15 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-38">
            <rule context="cac:InvoiceLine">
                <assert test="string-length(cbc:AccountingCost) &lt;= 20" flag="fatal">
                    [CIUS-VD-38]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-39">
            <rule context="//ubl:Invoice">
                <assert test="string-length(cbc:Note) &lt;= 200" flag="fatal">
                    [CIUS-VD-39]-The sum of BTs maximum length shall be 200 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-40">
            <rule context="cac:BillingReference/cac:InvoiceDocumentReference">
                <assert test="string-length(cbc:ID) &lt;= 20" flag="fatal">
                    [CIUS-VD-40]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-41">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme">
                <assert test="string-length(cbc:CompanyID) &lt;= 30" flag="fatal">
                    [CIUS-VD-41]-BT maximum length shall be 30 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-42">
            <rule context="cac:TaxRepresentativeParty/cac:PartyTaxScheme">
                <assert test="string-length(cbc:CompanyID) &lt;= 30" flag="fatal">
                    [CIUS-VD-42]-BT maximum length shall be 30 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-43">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme">
                <assert test="string-length(cbc:CompanyID) &lt;= 30" flag="fatal">
                    [CIUS-VD-43]-BT maximum length shall be 30 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-44">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact">
                <assert test="string-length(cbc:Name) &lt;= 200" flag="fatal">
                    [CIUS-VD-44]-BT maximum length shall be 200 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-45">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact">
                <assert test="string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) &gt;= 5"
                        flag="fatal">
                    [CIUS-VD-45]-BT minimum length shall be 5 maximum length shall be 12 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-46">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact">
                <assert test="string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) &gt;= 7"
                        flag="fatal">
                    [CIUS-VD-46]-BT minimum length shall be 7 maximum length shall be 256 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-47">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )"
                        flag="fatal">
                    [CIUS-VD-47]-If country code=IT it should be coded according to Italian province list.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-48">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )"
                        flag="fatal">
                    [CIUS-VD-48]-If country code=IT it should be coded according to Italian province list.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-49">
            <rule context="cac:Delivery/cac:DeliveryLocation/cac:Address">
                <assert test="not(cac:Country/cbc:IdentificationCode = 'IT') or contains( ' AG AL AN AO AR AP AT AV BA BT BL BN BG BI BO BZ BS BR CA CL CB CI CE CT CZ CH CO CS CR KR CN EN FM FE FI FG FC FR GE GO GR IM IS SP AQ LT LE LC LI LO LU MC MN MS MT VS ME MI MO MB NA NO NU OG OT OR PD PA PR PV PG PU PE PC PI PT PN PZ PO RG RA RC RE RI RN RM RO SA SS SV SI SO SR TA TE TR TP TN TV TS TO UD VA VE VB VC VR VV VI VT',concat(' ',normalize-space(.),' ') )"
                        flag="fatal">
                    [CIUS-VD-49]-If country code=IT it should be coded according to Italian province list.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-50">
            <rule context="cac:PayeeParty/cac:PartyName">
                <assert test="string-length(cbc:Name) &lt;= 200" flag="fatal">
                    [CIUS-VD-50]-BT maximum length shall be 200 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-51">
            <rule context="cac:AccountingCustomerParty/cac:Party/cac:Contact">
                <assert test="string-length(cbc:Name) &lt;= 200" flag="fatal">
                    [CIUS-VD-51]-BT maximum length shall be 200 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-53">
            <rule context="cac:AccountingCustomerParty/cac:Party">
                <assert test="exists(cac:PartyTaxScheme/cbc:CompanyID) or ( exists(cac:PartyIdentification/cbc:ID) and exists(cac:PartyIdentification/cbc:ID/@schemeID) )"
                        flag="fatal">
                    [CIUS-BR-14]-If BT-48 is empty then one of the buyer identifiers (0..n) should be the FiscalCode in
                    BT-46. BT-46-1 shall contain the scheme..
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-55">
            <rule context="cac:PaymentMeans">
                <assert test="string-length(cbc:InstructionNote) &lt;= 200" flag="fatal">
                    [CIUS-VD-55]-BT maximum length shall be 200 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-56">
            <rule context="cac:PaymentMeans">
                <assert test="string-length(cbc:PaymentID) &lt;= 60" flag="fatal">
                    [CIUS-VD-56]-BT maximum length shall be 60 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-57">
            <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount">
                <assert test="string-length(cbc:ID) &lt;= 34 and string-length(cbc:ID) &gt;= 15" flag="fatal">
                    [CIUS-VD-57]-BT minimum length shall be 15, maximum length shall be 34 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-58">
            <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount">
                <assert test="string-length(cbc:Name) &lt;= 200" flag="fatal">
                    [CIUS-VD-58]-BT maximum length shall be 200 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-59">
            <rule context="cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch">
                <assert test="string-length(cac:ID) &lt;= 11 and string-length(cac:ID) &gt;= 8" flag="fatal">
                    [CIUS-VD-59]-BT minimum length shall be 8 maximum length shall be 11 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-60">
            <rule context="cac:AllowanceCharge">
                <assert test="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000"
                        flag="fatal">
                    [CIUS-VD-60]-BTs maximum length shall be 1000 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-61">
            <rule context="cac:AllowanceCharge">
                <assert test="(string-length(cbc:AllowanceChargeReason) + string-length(cbc:AllowanceChargeReasonCode)) &lt;= 1000"
                        flag="fatal">
                    [CIUS-VD-61]-BTs maximum length shall be 1000 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-62">
            <rule context="cac:LegalMonetaryTotal">
                <assert test="string-length(cbc:TaxInclusiveAmount) &lt;= 15 and string-length(cbc:TaxInclusiveAmount) &gt;= 4"
                        flag="fatal">
                    [CIUS-VD-62]-BT minimum length shall be 4 maximum length shall be 15 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-63">
            <rule context="cac:LegalMonetaryTotal">
                <assert test="string-length(cbc:PayableAmount) &lt;= 15 and string-length(cbc:PayableAmount) &gt;= 4"
                        flag="fatal">
                    [CIUS-VD-63]-BT minimum length shall be 4 maximum length shall be 15 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-64">
            <rule context="cac:AllowanceCharge">
                <assert test="string-length(cbc:Amount) &lt;= 21 and string-length(cbc:Amount) &gt;= 4" flag="fatal">
                    [CIUS-VD-64]-BT minimum length shall be 4 maximum length shall be 21 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-65">
            <rule context="cac:LegalMonetaryTotal">
                <assert test="not(exists(cbc:PayableRoundingAmount)) or (string-length(cbc:PayableRoundingAmount) &lt;= 15 and string-length(cbc:PayableRoundingAmount) &gt;= 4)"
                        flag="fatal">
                    [CIUS-VD-65]-BT minimum length shall be 4 maximum length shall be 15 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-66">
            <rule context="cac:TaxTotal/cac:TaxSubtotal">
                <assert test="string-length(cbc:TaxableAmount) &lt;= 15 and string-length(cbc:TaxableAmount) &gt;= 4"
                        flag="fatal">
                    [CIUS-VD-66]-BT minimum length shall be 4 maximum length shall be 15 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-67">
            <rule context="cac:TaxTotal/cac:TaxSubtotal">
                <assert test="string-length(cbc:TaxAmount) &lt;= 15 and string-length(cbc:TaxAmount) &gt;= 4"
                        flag="fatal">
                    [CIUS-VD-67]-BT minimum length shall be 4 maximum length shall be 15 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-68">
            <rule context="cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory">
                <assert test="string-length(cbc:TaxExemptionReason) &lt;= 100" flag="fatal">
                    [CIUS-VD-68]-BT maximum length shall be 100 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-69">
            <rule context="cac:AdditionalDocumentReference">
                <assert test="(string-length(cbc:ID) + string-length(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename)) &lt;= 60"
                        flag="fatal">
                    [CIUS-VD-69]-BT maximum length shall be 60 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-70">
            <rule context="cac:AdditionalDocumentReference">
                <assert test="string-length(cbc:DocumentType) &lt;= 100" flag="fatal">
                    [CIUS-VD-70]-BT maximum length shall be 100 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-72">
            <rule context="cac:AdditionalDocumentReference/cac:Attachment">
                <assert test="string-length(cbc:EmbeddedDocumentBinaryObject) &lt;= 10" flag="fatal">
                    [CIUS-VD-72]-BT maximum length shall be 10 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-74">
            <rule context="cac:InvoiceLine">
                <assert test="string-length(cbc:ID) &lt;= 4" flag="fatal">
                    [CIUS-VD-74]-BT maximum length shall be 4 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-75">
            <rule context="cac:InvoiceLine">
                <assert test="string-length(cbc:Note) &lt;= 60" flag="fatal">
                    [CIUS-VD-75]-BT maximum length shall be 60 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-76">
            <rule context="cac:InvoiceLine/cac:DocumentReference">
                <assert test="string-length(cbc:ID/@schemeID) &lt;= 35" flag="fatal">
                    [CIUS-VD-76]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-77">
            <rule context="cac:InvoiceLine/cac:DocumentReference">
                <assert test="string-length(cbc:ID) &lt;= 35" flag="fatal">
                    [CIUS-VD-77]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-78">
            <rule context="cac:InvoiceLine">
                <assert test="string-length(cbc:InvoicedQuantity/@unitCode) &lt;= 10" flag="fatal">
                    [CIUS-VD-78]-BT maximum length shall be 10 chars.
                </assert>
                <assert test="string-length(cac:Price/cbc:BaseQuantity) &lt;= 10" flag="fatal">
                    [CIUS-VD-78]-BT maximum length shall be 10 chars.
                </assert>
                <assert test="string-length(cac:Price/cbc:BaseQuantity/@unitCode) &lt;= 10" flag="fatal">
                    [CIUS-VD-78]-BT maximum length shall be 10 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-79">
            <rule context="cac:InvoiceLine">
                <assert test="string-length(cbc:AccountingCost) &lt;= 20" flag="fatal">
                    [CIUS-VD-79]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-80">
            <rule context="cac:InvoiceLine/cac:AllowanceCharge">
                <assert test="string-length(cbc:Amount) &gt;= 4 and string-length(cbc:Amount) &lt;= 21" flag="fatal">
                    [CIUS-VD-80]-BT minimum length shall be 4, maximum length shall be 21 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-81">
            <rule context="cac:InvoiceLine/cac:AllowanceCharge">
                <assert test="string-length(cbc:AllowanceChargeReason) &lt;= 1000" flag="fatal">
                    [CIUS-VD-81]-BT maximum length shall be 1000 chars.
                </assert>
                <assert test="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000" flag="fatal">
                    [CIUS-VD-81]-BT maximum length shall be 1000 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-82">
            <rule context="cac:InvoiceLine/cac:AllowanceCharge">
                <assert test="string-length(cbc:AllowanceChargeReason) &lt;= 1000" flag="fatal">
                    [CIUS-VD-82]-BT maximum length shall be 1000 chars.
                </assert>
                <assert test="string-length(cbc:AllowanceChargeReasonCode) &lt;= 1000" flag="fatal">
                    [CIUS-VD-82]-BT maximum length shall be 1000 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-83">
            <rule context="cac:InvoiceLine/cac:Price">
                <assert test="string-length(cbc:PriceAmount) &lt;= 21 and string-length(cbc:PriceAmount) &gt;= 4"
                        flag="fatal">
                    [CIUS-VD-83]-BT minimum length shall be 4 maximum length shall be 21 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-85">
            <rule context="cac:InvoiceLine/cac:Item">
                <assert test="string-length(cbc:Name) &lt;= 1000" flag="fatal">
                    [CIUS-VD-85]-BT maximum length shall be 1000 chars.
                </assert>
                <assert test="string-length(cbc:Description) &lt;= 1000" flag="fatal">
                    [CIUS-VD-85]-BT maximum length shall be 1000 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-86">
            <rule context="cac:InvoiceLine/cac:Item/cac:SellersItemIdentification">
                <assert test="string-length(cbc:ID) &lt;= 35" flag="fatal">
                    [CIUS-VD-86]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-87">
            <rule context="cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification">
                <assert test="string-length(cbc:ID) &lt;= 35" flag="fatal">
                    [CIUS-VD-87]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-88">
            <rule context="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification">
                <assert test="string-length(cbc:ID) &lt;= 35" flag="fatal">
                    [CIUS-VD-88]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-89">
            <rule context="cac:InvoiceLine/cac:Item/cac:CommodityClassification">
                <assert test="string-length(cbc:ItemClassificationCode) &lt;= 35" flag="fatal">
                    [CIUS-VD-89]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-90">
            <rule context="cac:InvoiceLine/cac:Item/cac:StandardItemIdentification">
                <assert test="string-length(cbc:ID/@schemeID) &lt;= 35" flag="fatal">
                    [CIUS-VD-90]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-91">
            <rule context="cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:ItemClassificationCode">
                <assert test="string-length(@listVersionID) &lt;= 35" flag="fatal">
                    [CIUS-VD-91]-BT maximum length shall be 35 chars.
                </assert>
                <assert test="string-length(@listID) &lt;= 35" flag="fatal">
                    [CIUS-VD-91]-BT maximum length shall be 35 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-92">
            <rule context="cac:InvoiceLine/cac:Item/cac:OriginCountry">
                <assert test="string-length(cbc:IdentificationCode) &lt;= 60" flag="fatal">
                    [CIUS-VD-92]-BT maximum length shall be 60 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-93">
            <rule context="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty">
                <assert test="string-length(cbc:Name) &lt;= 10" flag="fatal">
                    [CIUS-VD-93]-BT maximum length shall be 10 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-94">
            <rule context="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty">
                <assert test="string-length(cbc:Value) &lt;= 60" flag="fatal">
                    [CIUS-VD-94]-BT maximum length shall be 60 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-94">
            <rule context="cac:InvoiceLine/cac:Item/cac:AdditionalItemProperty">
                <assert test="string-length(cbc:Value) &lt;= 60" flag="fatal">
                    [CIUS-VD-94]-BT maximum length shall be 60 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-95">
            <rule context="cac:InvoiceLine/cac:Price">
                <assert test="matches(cbc:PriceAmount, '^[0-9]+(\.[0-9]{0,8})*$')" flag="fatal">
                    [CIUS-VD-95]-BT allowed fraction digits shall be 8.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-96">
            <rule context="cac:InvoiceLine/cac:OrderLineReference">
                <assert test="string-length(cbc:LineID) &lt;= 20" flag="fatal">
                    [CIUS-VD-96]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-97">
            <rule context="cac:AccountingCustomerParty/cac:Party">
                <assert test="not(cbc:EndpointID/@schemeID = ' PEC') or ( string-length(cbc:EndpointID) &gt;= 7  and string-length(cbc:EndpointID) &lt;= 256 )"
                        flag="fatal">
                    [CIUS-VD-97]-If BT-49-1= PEC schema then BT-49 minimum length shall be 7 maximum length shall be 256
                    chars
                </assert>
                <assert test="not(cbc:EndpointID/@schemeID = ' IPA') or ( string-length(cbc:EndpointID) &lt;= 6 )"
                        flag="fatal">
                    [CIUS-VD-97]-Indice IPA schema then BT-49 maximum length shall be 6 chars
                </assert>
                <assert test="not(cbc:EndpointID/@schemeID = ' CodiceUfficio') or ( string-length(cbc:EndpointID) &lt;= 7 )"
                        flag="fatal">
                    [CIUS-VD-97]-CodiceUfficio schema then BT-49 maximum length shall be 7 chars".
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-99">
            <rule context="//ubl:Invoice/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID">
                <assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or ( ( ( not(contains(normalize-space(.),' ')) and contains( ' RF01 RF02 RF03 RF04 RF05 RF06 RF07 RF08 RF09 RF10 RF11 RF12 RF13 RF14 RF15 RF16 RF17 RF18 RF19',concat(' ',normalize-space(.),' ') ) ) ) )"
                        flag="fatal">
                    [CIUS-VD-99]-In case the seller is Italian this field must contain the codification of RegimeFiscale
                    (1.2.1.8 from RF01 to RF19)
                </assert>
            </rule>
        </pattern>

    </phase>
</schema>
