<pattern abstract="true" id="syntax" xmlns="http://purl.oclc.org/dsdl/schematron">
   <rule context="$CIUS-VD-57_CONTEXT">
       <assert test="$CIUS-VD-57" id="CIUS-VD-57" flag="fatal"> [CIUS-VD-57] BT-84 (Payment account identifier) -BT minimum length shall be 15, maximum length shall be 34 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-58_CONTEXT">
       <assert test="$CIUS-VD-58" id="CIUS-VD-58" flag="fatal"> [CIUS-VD-58] BT-85 (Payment account name) -BT maximum length shall be 200 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-59_CONTEXT">
       <assert test="$CIUS-VD-59" id="CIUS-VD-59" flag="fatal"> [CIUS-VD-59] BT-86 (Payment service provider identifier) -BT minimum length shall be 8 maximum length shall be 11 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-60_CONTEXT">
       <assert test="$CIUS-VD-60" id="CIUS-VD-60" flag="fatal"> [CIUS-VD-60] BT-97, BT-98 (Document level allowance reason
Document level allowance reason code)-BTs maximum length shall be 1000 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-61_CONTEXT">
       <assert test="$CIUS-VD-61" id="CIUS-VD-61" flag="fatal"> [CIUS-VD-61] BT-104, BT-105 (Document level charge reason
Document level charge reason code)-BTs maximum length shall be 1000 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-62_CONTEXT">
       <assert test="$CIUS-VD-62" id="CIUS-VD-62" flag="fatal"> [CIUS-VD-62] BT-112 (Invoice total amount with VAT) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-63_CONTEXT">
       <assert test="$CIUS-VD-63" id="CIUS-VD-63" flag="fatal"> [CIUS-VD-63] BT-115 (Amount due for payment) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-64_CONTEXT">
       <assert test="$CIUS-VD-64" id="CIUS-VD-64" flag="fatal"> [CIUS-VD-64] BT-92, BT-99 (Document level allowance amount
Document level charge amount) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-65_CONTEXT">
       <assert test="$CIUS-VD-65" id="CIUS-VD-65" flag="fatal"> [CIUS-VD-65] BT-114 (Rounding amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-66_CONTEXT">
       <assert test="$CIUS-VD-66" id="CIUS-VD-66" flag="fatal"> [CIUS-VD-66] BT-116 (VAT category taxable amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-67_CONTEXT">
       <assert test="$CIUS-VD-67" id="CIUS-VD-67" flag="fatal"> [CIUS-VD-67] BT-117 (VAT category tax amount) -BT minimum length shall be 4 maximum length shall be 15 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-68_CONTEXT">
       <assert test="$CIUS-VD-68" id="CIUS-VD-68" flag="fatal"> [CIUS-VD-68] BT-120 (VAT exemption reason text) -BT maximum length shall be 100 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-69_CONTEXT">
       <assert test="$CIUS-VD-69" id="CIUS-VD-69" flag="fatal"> [CIUS-VD-69] BT-122, BT-125-2 (Supporting document reference
Attached document Filename) - BT maximum length shall be 60 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-70_CONTEXT">
       <assert test="$CIUS-VD-70" id="CIUS-VD-70" flag="fatal"> [CIUS-VD-70] BT-123 (Supporting document description) -BT maximum length shall be 100 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-72_CONTEXT">
       <assert test="$CIUS-VD-72" id="CIUS-VD-72" flag="fatal"> [CIUS-VD-72] BT-125-1 (Attached document Mime code) -BT maximum length shall be 10 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-74_CONTEXT">
       <assert test="$CIUS-VD-74" id="CIUS-VD-74" flag="fatal"> [CIUS-VD-74] BT-126 (Invoice line identifier) -BT maximum length shall be 4 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-75_CONTEXT">
       <assert test="$CIUS-VD-75" id="CIUS-VD-75" flag="fatal"> [CIUS-VD-75] BT-127 (Invoice line note) -BT maximum length shall be 60 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-76_CONTEXT">
       <assert test="$CIUS-VD-76" id="CIUS-VD-76" flag="fatal"> [CIUS-VD-76] BT-128-1 (Invoice line object identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-77_CONTEXT">
       <assert test="$CIUS-VD-77" id="CIUS-VD-77" flag="fatal"> [CIUS-VD-77] BT-128 (Invoice line object identifier) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-78_CONTEXT">
       <assert test="$CIUS-VD-78-1" id="CIUS-VD-78-1" flag="fatal"> [CIUS-VD-78-1] BT-130 (Invoiced quantity unit of measure) -BT maximum length shall be 10 chars. 
        </assert>
       <assert test="$CIUS-VD-78-2" id="CIUS-VD-78-2" flag="fatal"> [CIUS-VD-78-2] BT-149 (Item price base quantity) -BT maximum length shall be 10 chars. 
        </assert>
       <assert test="$CIUS-VD-78-3" id="CIUS-VD-78-3" flag="fatal"> [CIUS-VD-78-3] BT-150 (Item price base quantity unit of measure code) -BT maximum length shall be 10 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-79_CONTEXT">
       <assert test="$CIUS-VD-79" id="CIUS-VD-79" flag="fatal"> [CIUS-VD-79] BT-133 (Invoice line Buyer accounting reference)-BT maximum length shall be 20 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-80_CONTEXT">
       <assert test="$CIUS-VD-80" id="CIUS-VD-80" flag="fatal"> [CIUS-VD-80] BT-136, BT-141 (Invoice line allowance amount
Invoice line charge amount)-BT minimum length shall be 4, maximum length shall be 21 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-81_CONTEXT">
       <assert test="$CIUS-VD-81-1" id="CIUS-VD-81-1" flag="fatal"> [CIUS-VD-81-1] BT-139 (Invoice line allowance reason)-BT maximum length shall be 1000 chars. 
        </assert>
       <assert test="$CIUS-VD-81-2" id="CIUS-VD-81-2" flag="fatal"> [CIUS-VD-81-2] BT-140 (Invoice line allowance reason code)-BT maximum length shall be 1000 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-82_CONTEXT">
       <assert test="$CIUS-VD-82-1" id="CIUS-VD-82-1" flag="fatal"> [CIUS-VD-82-1] BT-144 (Invoice line charge reason)-BT maximum length shall be 1000 chars. 
        </assert>
       <assert test="$CIUS-VD-82-2" id="CIUS-VD-82-2" flag="fatal"> [CIUS-VD-82-2] BT-145 (Invoice line charge reason code)-BT maximum length shall be 1000 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-83_CONTEXT">
       <assert test="$CIUS-VD-83" id="CIUS-VD-83" flag="fatal"> [CIUS-VD-83] BT-146 (Item net price) -BT minimum length shall be 4 maximum length shall be 21 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-85_CONTEXT">
       <assert test="$CIUS-VD-85-1" id="CIUS-VD-85-1" flag="fatal"> [CIUS-VD-85-1] BT-153 (Item name) -BT maximum length shall be 1000 chars. 
        </assert>
       <assert test="$CIUS-VD-85-2" id="CIUS-VD-85-2" flag="fatal"> [CIUS-VD-85-2] BT-154 (Item description) -BT maximum length shall be 1000 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-86_CONTEXT">
       <assert test="$CIUS-VD-86" id="CIUS-VD-86" flag="fatal"> [CIUS-VD-86] BT-155 (Item Seller's identifier) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-87_CONTEXT">
       <assert test="$CIUS-VD-87" id="CIUS-VD-87" flag="fatal"> [CIUS-VD-87] BT-156 (Item Buyer's identifier) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-88_CONTEXT">
       <assert test="$CIUS-VD-88" id="CIUS-VD-88" flag="fatal"> [CIUS-VD-88] BT-157 (Item standard identifier) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-89_CONTEXT">
       <assert test="$CIUS-VD-89" id="CIUS-VD-89" flag="fatal"> [CIUS-VD-89] BT-158 (Item classification identifier) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-90_CONTEXT">
       <assert test="$CIUS-VD-90" id="CIUS-VD-90" flag="fatal"> [CIUS-VD-90] BT-157-1 (Item standard identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-91_CONTEXT">
       <assert test="$CIUS-VD-91-1" id="CIUS-VD-91-1" flag="fatal"> [CIUS-VD-91-1] BT-158-1 (Item classification identifier identification scheme identifier) -BT maximum length shall be 35 chars. 
        </assert>
       <assert test="$CIUS-VD-91-2" id="CIUS-VD-91-2" flag="fatal"> [CIUS-VD-91-2] BT-158-2 (Scheme version identifer) -BT maximum length shall be 35 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-92_CONTEXT">
       <assert test="$CIUS-VD-92" id="CIUS-VD-92" flag="fatal"> [CIUS-VD-92] BT-159 (Item country of origin) -BT maximum length shall be 60 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-93_CONTEXT">
       <assert test="$CIUS-VD-93" id="CIUS-VD-93" flag="fatal"> [CIUS-VD-93] BT-160 (Item attribute name) -BT maximum length shall be 10 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-94_CONTEXT">
       <assert test="$CIUS-VD-94" id="CIUS-VD-94" flag="fatal"> [CIUS-VD-94] BT-161 (Item attribute value) -BT maximum length shall be 60 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-95_CONTEXT">
       <assert test="$CIUS-VD-95" id="CIUS-VD-95" flag="fatal"> [CIUS-VD-95] BT-146 (Item net price) -BT allowed fraction digits shall be 8. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-96_CONTEXT">
       <assert test="$CIUS-VD-96" id="CIUS-VD-96" flag="fatal"> [CIUS-VD-96] BT-132 (Referenced purchase order line reference) -BT maximum length shall be 20 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-97_CONTEXT">
       <assert test="$CIUS-VD-97-1" id="CIUS-VD-97-1" flag="fatal"> [CIUS-VD-97-1] BT-49, BT-49-1 (Buyer electronic address
Buyer electronic address identification scheme identifier) -If BT-49-1= PEC schema then BT-49 minimum length shall be 7 maximum length shall be 256 
        </assert>
       <assert test="$CIUS-VD-97-2" id="CIUS-VD-97-2" flag="fatal"> [CIUS-VD-97-2] BT-49, BT-49-1 (Buyer electronic address
Buyer electronic address identification scheme identifier) -Indice IPA schema then BT-49 maximum length shall be 6 chars 
        </assert>
       <assert test="$CIUS-VD-97-3" id="CIUS-VD-97-3" flag="fatal"> [CIUS-VD-97-3] BT-49, BT-49-1 (Buyer electronic address
Buyer electronic address identification scheme identifier) -CodiceUfficio schema then BT-49 maximum length shall be 7 chars. 
        </assert>
    </rule>
   <rule context="$CIUS-VD-99_CONTEXT">
       <assert test="$CIUS-VD-99" id="CIUS-VD-99" flag="fatal"> [CIUS-VD-99] BT-32 (Seller tax registration identifier) -In case the seller is Italian this field must contain the codification of RegimeFiscale 
        </assert>
    </rule>
</pattern>