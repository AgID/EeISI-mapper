<pattern abstract="true" id="syntax" xmlns="http://purl.oclc.org/dsdl/schematron">
    <rule context="$CIUS-BT-2_CONTEXT">
        <assert test="$CIUS-BT-2" id="CIUS-BT-2" flag="fatal">
            [CIUS-BT-2] BT-49 shall contain a legal mail address (PEC) or IndicePA/CodiceUfficio (see the
            Italian business rules).
        </assert>
    </rule>


    <rule context="$CIUS-BT-98_CONTEXT">
        <assert test="$CIUS-BT-98"
                flag="fatal">
            [CIUS-BT-98]-BT is a conditional field and shall not be used by a foreign seller as it is not
            possible to map into XMLPA. CEN business rules are not broken. In case the seller is Italian this
            field shall contain the codification of RegimeFiscale (1.2.1.8).
        </assert>
    </rule>


    <rule context="$CIUS-BT-84_CONTEXT">
        <assert test="$CIUS-BT-84" id="CIUS-BT-84" flag="fatal">
            [CIUS-BT-84]-The payment account identifier shall be an IBAN code.
        </assert>
    </rule>


    <rule context="$CIUS-CA-9_CONTEXT">
        <assert test="$CIUS-CA-9-1" id="CIUS-CA-9-1" flag="fatal">
            [CIUS-CA-9-1]-Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax
            representative).
        </assert>
        <assert test="$CIUS-CA-9-2" id="CIUS-CA-9-2" flag="fatal">
            [CIUS-CA-9-2]-Mandatory in Italy (seller). BT-31 should be mandatory or copied from BT-63 (tax
            representative).
        </assert>
    </rule>


    <rule context="$CIUS-CA-10_CONTEXT">
        <assert test="$CIUS-CA-10-1"
                flag="fatal">
            [CIUS-CA-10-1]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
        <assert test="$CIUS-CA-10-2" id="CIUS-CA-10-2" flag="fatal">
            [CIUS-CA-10-2]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
        <assert test="$CIUS-CA-10-3" id="CIUS-CA-10-3" flag="fatal">
            [CIUS-CA-10-3]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
    </rule>


    <rule context="$CIUS-CA-11_CONTEXT">
        <assert test="$CIUS-CA-11-1" id="CIUS-CA-11-1" flag="fatal">
            [CIUS-CA-11-1]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
        <assert test="$CIUS-CA-11-2" id="CIUS-CA-11-2" flag="fatal">
            [CIUS-CA-11-2]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
        <assert test="$CIUS-CA-11-3" id="CIUS-CA-11-3" flag="fatal">
            [CIUS-CA-11-3]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
    </rule>


    <rule context="$CIUS-CA-12_CONTEXT">
        <assert test="$CIUS-CA-12-1" id="CIUS-CA-12-1" flag="fatal">
            [CIUS-CA-12-1]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
        <assert test="$CIUS-CA-12-2" id="CIUS-CA-12-2" flag="fatal">
            [CIUS-CA-12-2]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
        <assert
                test="$CIUS-CA-12-3" flag="fatal">
            [CIUS-CA-12-3]-Fields are mandatory in Italy. Mapped BTs should be mandatory.
        </assert>
    </rule>


    <rule context="$CIUS-CA-71_CONTEXT">
        <assert test="$CIUS-CA-71" id="CIUS-CA-71" flag="fatal">
            [CIUS-CA-71]-If BT-124 is empty then the BT-125 should be mandatory as the mapped field is mandatory
            in Italy.
        </assert>
    </rule>


    <rule context="$CIUS-SD-73_CONTEXT">
        <assert test="$CIUS-SD-73" id="CIUS-SD-73" flag="fatal">
            [CIUS-SD-73]-The BT value should be numeric.
        </assert>
    </rule>


    <rule context="$CIUS-CI-13_CONTEXT">
        <assert test="$CIUS-CI-13" id="CIUS-CI-13" flag="fatal">
            [CIUS-CI-13]-VAT accounting currency code should be € for invoices from EU to IT in accordance with
            2006/112/CE art. 9.
        </assert>
    </rule>


    <rule context="$CIUS-BR-14_CONTEXT">
        <assert test="$CIUS-BR-14" id="CIUS-BR-14" flag="fatal">
            [CIUS-BR-14]-1.4.1.1 is not mandatory in Italy (buyer) but VAT number or Fiscal code should be
            indicated.
        </assert>
    </rule>


    <rule context="$CIUS-VD-15_CONTEXT">
        <assert test="$CIUS-VD-15" id="CIUS-VD-15" flag="fatal">
            [CIUS-VD-15]-BT will be structured as unique ID containing the despatch date as well (e.g.
            123456789_2017-03-05).
        </assert>
    </rule>


    <rule context="$CIUS-VD-16_CONTEXT">
        <assert test="$CIUS-VD-16" id="CIUS-VD-16" flag="fatal">
            [CIUS-VD-16]-BT maximum length shall be 30 chars (20 digit + YYYY-MM-DD).
        </assert>
    </rule>


    <rule context="$CIUS-VD-17_CONTEXT">
        <assert test="$CIUS-VD-17" id="CIUS-VD-17" flag="fatal">
            [CIUS-VD-17]-BT maximum length shall be 80 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-18_CONTEXT">
        <assert test="$CIUS-VD-18" id="CIUS-VD-18" flag="fatal">
            [CIUS-VD-18]-BT maximum length shall be 80 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-19_CONTEXT">
        <assert test="$CIUS-VD-19" id="CIUS-VD-19" flag="fatal">
            [CIUS-VD-19]-BT maximum length shall be 80 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-20_CONTEXT">
        <assert test="$CIUS-VD-20" id="CIUS-VD-20" flag="fatal">
            [CIUS-VD-20]-The sum of BTs maximum length shall be 180 chars (including separator).
        </assert>
    </rule>


    <rule context="$CIUS-VD-21_CONTEXT">
        <assert test="$CIUS-VD-21" id="CIUS-VD-21" flag="fatal">
            [CIUS-VD-21]-The sum of BTs maximum length shall be 180 chars (including separator).
        </assert>
    </rule>


    <rule context="$CIUS-VD-22_CONTEXT">
        <assert test="$CIUS-VD-22" id="CIUS-VD-22" flag="fatal">
            [CIUS-VD-22]-The sum of BTs maximum length shall be 180 chars (including separator).
        </assert>
    </rule>


    <rule context="$CIUS-VD-23_CONTEXT">
        <assert test="$CIUS-VD-23" id="CIUS-VD-23" flag="fatal">
            [CIUS-VD-23]-BT maximum length shall be 60 characters.
        </assert>
    </rule>


    <rule context="$CIUS-VD-24_CONTEXT">
        <assert test="$CIUS-VD-24" id="CIUS-VD-24" flag="fatal">
            [CIUS-VD-24]-BT maximum length shall be 60 characters.
        </assert>
    </rule>


    <rule context="$CIUS-VD-25_CONTEXT">
        <assert test="$CIUS-VD-25" id="CIUS-VD-25" flag="fatal">
            [CIUS-VD-25]-BT maximum length shall be 60 characters.
        </assert>
    </rule>


    <rule context="$CIUS-VD-26_CONTEXT">
        <assert test="$CIUS-VD-26-1" id="CIUS-VD-26-1" flag="fatal">
            [CIUS-VD-26-1]-BT maximum length shall be 15 chars.
        </assert>
        <assert test="$CIUS-VD-26-2" id="CIUS-VD-26-2" flag="fatal">
            [CIUS-VD-26-2]-BT maximum length, if country code =IT then it should be numeric and maximum length
            5.
        </assert>
    </rule>


    <rule context="$CIUS-VD-27_CONTEXT">
        <assert test="$CIUS-VD-27-1" id="CIUS-VD-27-1" flag="fatal">
            [CIUS-VD-27-1]-BT maximum length shall be 15 chars.
        </assert>
        <assert test="$CIUS-VD-27-2" id="CIUS-VD-27-2" flag="fatal">
            [CIUS-VD-27-2]-BT maximum length, if country code =IT then it should be numeric and maximum length
            5.
        </assert>
    </rule>


    <rule context="$CIUS-VD-28_CONTEXT">
        <assert test="$CIUS-VD-28-1" id="CIUS-VD-28-1" flag="fatal">
            [CIUS-VD-28-1]-BT maximum length shall be 15 chars.
        </assert>
        <assert test="$CIUS-VD-28-2" id="CIUS-VD-28-2" flag="fatal">
            [CIUS-VD-28-2]-BT maximum length, if country code =IT then it should be numeric and maximum length
            5.
        </assert>
    </rule>


    <rule context="$CIUS-VD-29_CONTEXT">
        <assert test="$CIUS-VD-29" id="CIUS-VD-29" flag="fatal">
            [CIUS-VD-29]-BT maximum length shall be 2 chars only used if country code=IT else the BT is not
            used.
        </assert>
    </rule>


    <rule context="$CIUS-VD-30_CONTEXT">
        <assert test="$CIUS-VD-30" id="CIUS-VD-30" flag="fatal">
            [CIUS-VD-30]-BT maximum length shall be 2 chars only used if country code=IT else the BT is not
            used.
        </assert>
    </rule>


    <rule context="$CIUS-VD-31_CONTEXT">
        <assert test="$CIUS-VD-31" id="CIUS-VD-31" flag="fatal">
            [CIUS-VD-31]-BT maximum length shall be 2 chars only used if country code=IT else the BT is not
            used.
        </assert>
    </rule>


    <rule context="$CIUS-VD-32_CONTEXT">
        <assert test="$CIUS-VD-32" id="CIUS-VD-32" flag="fatal">
            [CIUS-VD-32]-BT maximum length shall be 20 digits.
        </assert>
    </rule>


    <rule context="$CIUS-VD-33_CONTEXT">
        <assert test="$CIUS-VD-33" id="CIUS-VD-33" flag="fatal">
            [CIUS-VD-33]-BT maximum length shall be 15 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-34_CONTEXT">
        <assert test="$CIUS-VD-34" id="CIUS-VD-34" flag="fatal">
            [CIUS-VD-34]-BT maximum length shall be 20 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-35_CONTEXT">
        <assert test="$CIUS-VD-35" id="CIUS-VD-35" flag="fatal">
            [CIUS-VD-35]-BT maximum length shall be 20 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-36_CONTEXT">
        <assert test="$CIUS-VD-36" id="CIUS-VD-36" flag="fatal">
            [CIUS-VD-36]-BT maximum length shall be 20 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-37_CONTEXT">
        <assert test="$CIUS-VD-37" id="CIUS-VD-37" flag="fatal">
            [CIUS-VD-37]-BT maximum length shall be 15 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-38_CONTEXT">
        <assert test="$CIUS-VD-38" id="CIUS-VD-38" flag="fatal">
            [CIUS-VD-38]-BT maximum length shall be 20 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-39_CONTEXT">
        <assert test="$CIUS-VD-39" id="CIUS-VD-39" flag="fatal">
            [CIUS-VD-39]-The sum of BTs maximum length shall be 200 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-40_CONTEXT">
        <assert test="$CIUS-VD-40" id="CIUS-VD-40" flag="fatal">
            [CIUS-VD-40]-BT maximum length shall be 20 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-41_CONTEXT">
        <assert test="$CIUS-VD-41" id="CIUS-VD-41" flag="fatal">
            [CIUS-VD-41]-BT maximum length shall be 30 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-42_CONTEXT">
        <assert test="$CIUS-VD-42" id="CIUS-VD-42" flag="fatal">
            [CIUS-VD-42]-BT maximum length shall be 30 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-43_CONTEXT">
        <assert test="$CIUS-VD-43" id="CIUS-VD-43" flag="fatal">
            [CIUS-VD-43]-BT maximum length shall be 30 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-44_CONTEXT">
        <assert test="$CIUS-VD-44" id="CIUS-VD-44" flag="fatal">
            [CIUS-VD-44]-BT maximum length shall be 200 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-45_CONTEXT">
        <assert test="$CIUS-VD-45" id="CIUS-VD-45" flag="fatal">
            [CIUS-VD-45]-BT minimum length shall be 5 maximum length shall be 12 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-46_CONTEXT">
        <assert test="$CIUS-VD-46" id="CIUS-VD-46" flag="fatal">
            [CIUS-VD-46]-BT minimum length shall be 7 maximum length shall be 256 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-47_CONTEXT">
        <assert test="$CIUS-VD-47" id="CIUS-VD-47" flag="fatal">
            [CIUS-VD-47]-If country code=IT it should be coded according to Italian province list.
        </assert>
    </rule>


    <rule context="$CIUS-VD-48_CONTEXT">
        <assert test="$CIUS-VD-48" id="CIUS-VD-48" flag="fatal">
            [CIUS-VD-48]-If country code=IT it should be coded according to Italian province list.
        </assert>
    </rule>


    <rule context="$CIUS-VD-49_CONTEXT">
        <assert test="$CIUS-VD-49" id="CIUS-VD-49" flag="fatal">
            [CIUS-VD-49]-If country code=IT it should be coded according to Italian province list.
        </assert>
    </rule>


    <rule context="$CIUS-VD-50_CONTEXT">
        <assert test="$CIUS-VD-50" id="CIUS-VD-50" flag="fatal">
            [CIUS-VD-50]-BT maximum length shall be 200 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-51_CONTEXT">
        <assert test="$CIUS-VD-51" id="CIUS-VD-51" flag="fatal">
            [CIUS-VD-51]-BT maximum length shall be 200 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-53_CONTEXT">
        <assert test="$CIUS-VD-53" id="CIUS-VD-53" flag="fatal">
            [CIUS-VD-53]-If BT-48 is empty then one of the buyer identifiers (0..n) should be the FiscalCode in
            BT-46. BT-46-1 shall contain the scheme..
        </assert>
    </rule>


    <rule context="$CIUS-VD-55_CONTEXT">
        <assert test="$CIUS-VD-55" id="CIUS-VD-55" flag="fatal">
            [CIUS-VD-55]-BT maximum length shall be 200 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-56_CONTEXT">
        <assert test="$CIUS-VD-56" id="CIUS-VD-56" flag="fatal">
            [CIUS-VD-56]-BT maximum length shall be 60 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-57_CONTEXT">
        <assert test="$CIUS-VD-57" id="CIUS-VD-57" flag="fatal">
            [CIUS-VD-57]-BT minimum length shall be 15, maximum length shall be 34 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-58_CONTEXT">
        <assert test="$CIUS-VD-58" id="CIUS-VD-58" flag="fatal">
            [CIUS-VD-58]-BT maximum length shall be 200 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-59_CONTEXT">
        <assert test="$CIUS-VD-59" id="CIUS-VD-59" flag="fatal">
            [CIUS-VD-59]-BT minimum length shall be 8 maximum length shall be 11 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-60_CONTEXT">
        <assert test="$CIUS-VD-60" id="CIUS-VD-60" flag="fatal">
            [CIUS-VD-60]-BTs maximum length shall be 1000 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-61_CONTEXT">
        <assert test="$CIUS-VD-61" id="CIUS-VD-61" flag="fatal">
            [CIUS-VD-61]-BTs maximum length shall be 1000 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-62_CONTEXT">
        <assert test="$CIUS-VD-62"
                flag="fatal">
            [CIUS-VD-62]-BT minimum length shall be 4 maximum length shall be 15 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-63_CONTEXT">
        <assert test="$CIUS-VD-63" id="CIUS-VD-63" flag="fatal">
            [CIUS-VD-63]-BT minimum length shall be 4 maximum length shall be 15 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-64_CONTEXT">
        <assert test="$CIUS-VD-64" id="CIUS-VD-64" flag="fatal">
            [CIUS-VD-64]-BT minimum length shall be 4 maximum length shall be 21 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-65_CONTEXT">
        <assert test="$CIUS-VD-65" id="CIUS-VD-65" flag="fatal">
            [CIUS-VD-65]-BT minimum length shall be 4 maximum length shall be 15 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-66_CONTEXT">
        <assert test="$CIUS-VD-66" id="CIUS-VD-66" flag="fatal">
            [CIUS-VD-66]-BT minimum length shall be 4 maximum length shall be 15 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-67_CONTEXT">
        <assert test="$CIUS-VD-67" id="CIUS-VD-67" flag="fatal">
            [CIUS-VD-67]-BT minimum length shall be 4 maximum length shall be 15 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-68_CONTEXT">
        <assert test="$CIUS-VD-68" id="CIUS-VD-68" flag="fatal">
            [CIUS-VD-68]-BT maximum length shall be 100 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-69_CONTEXT">
        <assert test="$CIUS-VD-69"
                flag="fatal">
            [CIUS-VD-69]-BT maximum length shall be 60 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-70_CONTEXT">
        <assert test="$CIUS-VD-70" id="CIUS-VD-70" flag="fatal">
            [CIUS-VD-70]-BT maximum length shall be 100 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-72_CONTEXT">
        <assert test="$CIUS-VD-72" id="CIUS-VD-72" flag="fatal">
            [CIUS-VD-72]-BT maximum length shall be 10 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-74_CONTEXT">
        <assert test="$CIUS-VD-74" id="CIUS-VD-74" flag="fatal">
            [CIUS-VD-74]-BT maximum length shall be 4 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-75_CONTEXT">
        <assert test="$CIUS-VD-75" id="CIUS-VD-75" flag="fatal">
            [CIUS-VD-75]-BT maximum length shall be 60 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-76_CONTEXT">
        <assert test="$CIUS-VD-76" id="CIUS-VD-76" flag="fatal">
            [CIUS-VD-76]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-77_CONTEXT">
        <assert test="$CIUS-VD-77" id="CIUS-VD-77" flag="fatal">
            [CIUS-VD-77]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-78_CONTEXT">
        <assert test="$CIUS-VD-78-1" id="CIUS-VD-78-1" flag="fatal">
            [CIUS-VD-78-1]-BT maximum length shall be 10 chars.
        </assert>
        <assert test="$CIUS-VD-78-2" id="CIUS-VD-78-2" flag="fatal">
            [CIUS-VD-78-2]-BT maximum length shall be 10 chars.
        </assert>
        <assert test="$CIUS-VD-78-3" id="CIUS-VD-78-3" flag="fatal">
            [CIUS-VD-78-3]-BT maximum length shall be 10 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-79_CONTEXT">
        <assert test="$CIUS-VD-79" id="CIUS-VD-79" flag="fatal">
            [CIUS-VD-79]-BT maximum length shall be 20 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-80_CONTEXT">
        <assert test="$CIUS-VD-80" id="CIUS-VD-80" flag="fatal">
            [CIUS-VD-80]-BT minimum length shall be 4, maximum length shall be 21 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-81_CONTEXT">
        <assert test="$CIUS-VD-81-1" id="CIUS-VD-81-1" flag="fatal">
            [CIUS-VD-81-1]-BT maximum length shall be 1000 chars.
        </assert>
        <assert test="$CIUS-VD-81-2" id="CIUS-VD-81-2" flag="fatal">
            [CIUS-VD-81-2]-BT maximum length shall be 1000 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-82_CONTEXT">
        <assert test="$CIUS-VD-82-1" id="CIUS-VD-82-1" flag="fatal">
            [CIUS-VD-82-1]-BT maximum length shall be 1000 chars.
        </assert>
        <assert test="$CIUS-VD-82-2" id="CIUS-VD-82-2" flag="fatal">
            [CIUS-VD-82-2]-BT maximum length shall be 1000 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-83_CONTEXT">
        <assert test="$CIUS-VD-83" id="CIUS-VD-83" flag="fatal">
            [CIUS-VD-83]-BT minimum length shall be 4 maximum length shall be 21 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-85_CONTEXT">
        <assert test="$CIUS-VD-85-1" id="CIUS-VD-85-1" flag="fatal">
            [CIUS-VD-85-1]-BT maximum length shall be 1000 chars.
        </assert>
        <assert test="$CIUS-VD-85-2" id="CIUS-VD-85-2" flag="fatal">
            [CIUS-VD-85-2]-BT maximum length shall be 1000 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-86_CONTEXT">
        <assert test="$CIUS-VD-86" id="CIUS-VD-86" flag="fatal">
            [CIUS-VD-86]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-87_CONTEXT">
        <assert test="$CIUS-VD-87" id="CIUS-VD-87" flag="fatal">
            [CIUS-VD-87]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-88_CONTEXT">
        <assert test="$CIUS-VD-88" id="CIUS-VD-88" flag="fatal">
            [CIUS-VD-88]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-89_CONTEXT">
        <assert test="$CIUS-VD-89" id="CIUS-VD-89" flag="fatal">
            [CIUS-VD-89]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-90_CONTEXT">
        <assert test="$CIUS-VD-90" id="CIUS-VD-90" flag="fatal">
            [CIUS-VD-90]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-91_CONTEXT">
        <assert test="$CIUS-VD-91-1" id="CIUS-VD-91-1" flag="fatal">
            [CIUS-VD-91-1]-BT maximum length shall be 35 chars.
        </assert>
        <assert test="$CIUS-VD-91-2" id="CIUS-VD-91-2" flag="fatal">
            [CIUS-VD-91-2]-BT maximum length shall be 35 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-92_CONTEXT">
        <assert test="$CIUS-VD-92" id="CIUS-VD-92" flag="fatal">
            [CIUS-VD-92]-BT maximum length shall be 60 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-93_CONTEXT">
        <assert test="$CIUS-VD-93" id="CIUS-VD-93" flag="fatal">
            [CIUS-VD-93]-BT maximum length shall be 10 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-94_CONTEXT">
        <assert test="$CIUS-VD-94" id="CIUS-VD-94" flag="fatal">
            [CIUS-VD-94]-BT maximum length shall be 60 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-95_CONTEXT">
        <assert test="$CIUS-VD-95" id="CIUS-VD-95" flag="fatal">
            [CIUS-VD-95]-BT allowed fraction digits shall be 8.
        </assert>
    </rule>


    <rule context="$CIUS-VD-96_CONTEXT">
        <assert test="$CIUS-VD-96" id="CIUS-VD-96" flag="fatal">
            [CIUS-VD-96]-BT maximum length shall be 20 chars.
        </assert>
    </rule>


    <rule context="$CIUS-VD-97_CONTEXT">
        <assert test="$CIUS-VD-97-1"
                flag="fatal">
            [CIUS-VD-97-1]-If BT-49-1= PEC schema then BT-49 minimum length shall be 7 maximum length shall be
            256
            chars
        </assert>
        <assert test="$CIUS-VD-97-2"
                flag="fatal">
            [CIUS-VD-97-2]-Indice IPA schema then BT-49 maximum length shall be 6 chars
        </assert>
        <assert test="$CIUS-VD-97-3"
                flag="fatal">
            [CIUS-VD-97-3]-CodiceUfficio schema then BT-49 maximum length shall be 7 chars".
        </assert>
    </rule>


    <rule context="$CIUS-VD-99_CONTEXT">
        <assert test="$CIUS-VD-99" id="CIUS-VD-99" flag="fatal">
            [CIUS-VD-99]-In case the seller is Italian this field must contain the codification of RegimeFiscale
            (1.2.1.8 from RF01 to RF19)
        </assert>
    </rule>
</pattern>