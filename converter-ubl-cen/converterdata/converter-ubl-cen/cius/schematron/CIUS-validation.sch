<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
        xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
        xmlns:UBL="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" queryBinding="xslt2">
    <title>EN16931 CIUS</title>
    <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
    <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
    <ns prefix="ext" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"/>
    <ns prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"/>
    <ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
    <ns prefix="xs" uri="http://www.w3.org/2001/XMLSchema"/>

    <phase id="EN_16931_CIUS">

        <!--missing some here-->

        <pattern name="CIUS-SD-73">
            <rule context="cac:InvoiceLine">
                <assert test="number(cbc:ID) &gt; 0" flag="fatal">
                    [CIUS-SD-73]-The BT value should be numeric.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-CI-13">
            <rule context="/Invoice">
                <assert test="matches(cbc:TaxCurrencyCode, 'EUR')" flag="fatal">
                    [CIUS-CI-13]-VAT accounting currency code should be € for invoices from EU to IT in accordance with  2006/112/CE art. 9.
                </assert>
            </rule>
        </pattern>

        <!--missing one here-->

        <pattern name="CIUS-VD-15">
            <rule context="cac:DespatchDocumentReference">
                <assert test="matches(cbc:ID, '/([0-9]{1,9})+_+([0-9]{4})-([0-9]{2})-([0-9]{2})/')" flag="fatal">
                    [CIUS-VD-15]-BT will be structured as unique ID containing the despatch date as well (e.g. 123456789_2017-03-05).
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

        <!--missing some here-->

        <pattern name="CIUS-VD-32">
            <rule context="/Invoice">
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
            <rule context="/Invoice">
                <assert test="string-length(cbc:AccountingCost) &lt;= 20" flag="fatal">
                    [CIUS-VD-38]-BT maximum length shall be 20 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-39">
            <rule context="/Invoice">
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
                <assert test="string-length(cbc:Telephone) &lt;= 12 and string-length(cbc:Telephone) &gt;= 5" flag="fatal">
                    [CIUS-VD-45]-BT minimum length shall be 5 maximum length shall be 12 chars.
                </assert>
            </rule>
        </pattern>

        <pattern name="CIUS-VD-46">
            <rule context="cac:AccountingSupplierParty/cac:Party/cac:Contact">
                <assert test="string-length(cbc:ElectronicMail) &lt;= 256 and string-length(cbc:ElectronicMail) &gt;= 7" flag="fatal">
                    [CIUS-VD-46]-BT minimum length shall be 7 maximum length shall be 256 chars.
                </assert>
            </rule>
        </pattern>

        <!--missing some here-->

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

        <!--missing one here-->

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
                    [CIUS-VD-55]-BT maximum length shall be 60 chars.
                </assert>
            </rule>
        </pattern>

    </phase>


</schema>
