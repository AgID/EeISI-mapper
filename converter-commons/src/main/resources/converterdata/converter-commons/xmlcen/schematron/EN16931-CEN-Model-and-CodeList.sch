<?xml version="1.0" encoding="UTF-8"?>
<!-- 
            Core semantic model Schematron - CEN Semantic  
            Created by EeISI - European eInvoicing Standard in Italy Project
            Latest update: 2019-FEB-25 
            Release: 1.0.4 DRAFT
  -->
<!--
Modifiche 2019-01-12:
BR-S-01: modificato controllo su count se = 1 a >=1 (da = 1 a &gt; 0)
BR-S-07: fix su BT errato
BR-S-08: rivista completamente in quanto non considerava il vate rate
BR-CO-04 => spostata su diverso context
BR-VATCODE-05 => spostate TUTTE in un diverso contesto

Modifiche 2019-01-25
BR-S-01 to BR-S-10 update to include B Vat Category Code
Modifica 2019-02-25
BR-61 changed BT-84 in BG-17/BT-84 



-->

<schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:u="utils"
  schemaVersion="iso" queryBinding="xslt2">

  <!--
    Business Rules from CEN Schematron (Model UBL)

Implemented (order from EN16931-model.sch:
    BR-52
    BR-CO-25
    BR-63
    BR-11
    BR-51    
    BR-57
    BR-31
    BR-32    
    BR-33
    BR-CO-05 => always true()
    BR-CO-21
    BR-36
    BR-37
    BR-38
    BR-CO-06 => always true()    
    BR-CO-22
    BR-12
    BR-13
    BR-14
    BR-15
    BR-CO-10
    BR-CO-11    
    BR-CO-12
    BR-CO-13
    BR-CO-16
    BR-53
    BR-AE-01
    BR-AE-02
    BR-AE-03
    BR-AE-04
    BR-CO-03
    BR-CO-15
    BR-CO-18    
    BR-E-01
    BR-E-02
    BR-E-03
    BR-E-04    
    BR-G-01
    BR-G-02
    BR-G-03
    BR-G-04
    BR-IC-01
    BR-IC-02
    BR-IC-03
    BR-IC-04
    BR-IC-11
    BR-IC-12
    BR-IG-01    
    BR-IG-02
    BR-IG-03
    BR-IG-04
    BR-IP-01
    BR-IP-02
    BR-IP-03
    BR-IP-04
    BR-O-01
    BR-O-02
    BR-O-03
    BR-O-04
    BR-O-11
    BR-O-12
    BR-O-13
    BR-O-14
    BR-S-01
    BR-S-02
    BR-S-03
    BR-S-04
    BR-Z-01
    BR-Z-02
    BR-Z-03
    BR-Z-04
    BR-27
    BR-28
    BR-CO-04
    BR-42
    BR-CO-07 -> always true()
    BR-CO-23
    BR-44
    BR-CO-08 -> always true()
    BR-CO-24
    BR-30
    BR-CO-20
    BR-29
    BR-CO-19
    BR-CL-08
    BR-17
    BR-61
    BR-CO-26
    BR-CO-14
    BR-CO-09
    BR-AE-08
    BR-AE-09
    BR-AE-10
    BR-E-08
    BR-E-09
    BR-E-10
    BR-G-08
    BR-G-09
    BR-G-10
    BR-IC-08
    BR-IC-09
    BR-IC-10
    BR-IG-08
    BR-IG-09
    BR-IG-10
    BR-IP-08
    BR-IP-09
    BR-IP-10
    BR-O-08
    BR-O-09
    BR-O-10
    BR-S-08
    BR-S-09
    BR-S-10
    BR-Z-08
    BR-Z-09
    BR-Z-10
    BR-AE-06
    BR-E-06
    BR-G-06
    BR-IC-06
    BR-IG-06
    BR-IP-06
    BR-O-06
    BR-S-06
    BR-Z-06
    BR-AE-07
    BR-E-07
    BR-G-07
    BR-IC-07
    BR-IG-07
    BR-IP-07
    BR-O-07
    BR-S-07
    BR-Z-07
    BR-AE-05
    BR-E-05
    BR-G-05
    BR-IC-05
    BR-IG-05
    BR-IP-05
    BR-O-05
    BR-S-05
    BR-Z-05
    
    

To Verify:
- perchè mettere le rules sempre true() => per riservare la codifica per futura implementazione - RFU
- Che differenza esiste fra  BR-CO-21 e BR-33 ???
- Che differenza esiste fra  BR-CO-22 e BR-38 ???
- Regole BR-AE-02 in CEN UBL non mi sembra congruente - non capisco perchè no distingue con gli schema...
          (
            exists(//cac:ClassifiedTaxCategory[normalize-space(cbc:ID) = 'AE']) 
            and   >>>BT-31 (?or 32) o BT-63
              (
                  exists(//cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) 
                  or exists(//cac:TaxRepresentativeParty/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID = 'VAT']/cbc:CompanyID)
              ) 
              and     >>>BT-48 o BT-47
              (
                  exists(//cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID = 'VAT']/cbc:CompanyID) 
                  or exists(//cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID)
              )
          ) 
          
          or not(exists(//cac:ClassifiedTaxCategory[normalize-space(cbc:ID) = 'AE']))"/>
- BR-27 e BR-28 potrebbero essere imposti nello schema - da valutare
- Che differenza esiste fra BR-42 e CR-CO-23 ???
- Che differenza esiste fra BR-44 e CR-CO-24
- Rivedere messaggio originale CEN
      dovrebbe essere
              id="BR-44">[BR-44]-Each Invoice line charge shall have an Invoice line charge reason (BT-144) or an invoice line charge reason code (BT-145). 
      invece di
          <assert test="$BR-44" flag="fatal" id="BR-44">[BR-44]-Each Invoice line charge shall have an Invoice line charge reason or an invoice line allowance reason code. </assert>
- BR-17
    non si usa neanche normalize-spaces() nè gli scheme
    ??? qualcosa non torna ???
- BR-CO-09 - perchè non c'è normalize-space nella rule CEN?
        
Managed by Semantic CEN XSD
    BR-DEC-01 
    BR-DEC-02
    BR-DEC-05
    BR-DEC-06
    BR-DEC-09
    BR-DEC-10
    BR-DEC-11
    BR-DEC-12
    BR-DEC-14
    BR-DEC-16
    BR-DEC-17
    BR-DEC-18
    BR-02
    BR-03
    BR-04
    BR-05
    BR-06
    BR-07
    BR-08
    BR-10
    BR-16
    BR-DEC-13
    BR-DEC-15
    BR-21
    BR-22
    BR-23
    BR-24
    BR-25
    BR-26
    BR-DEC-23
    BR-41
    BR-DEC-24
    BR-DEC-25
    BR-43
    BR-DEC-27
    BR-DEC-28
    BR-54
    BR-65
    BR-64
    BR-50
    BR-49
    BR-55
    BR-62
    BR-09
    BR-18
    BR-19
    BR-20
    BR-56
    BR-DEC-19
    BR-DEC-20

To Be implemented

  -->    
  <phase id="Model">
    <active pattern="CENModel"/>
  </phase>
  <phase id="CodeList">
    <active pattern="CENCodeList"/>
  </phase>
  
  <pattern id="CENModel">

    <rule context="//SEMANTIC-INVOICE">    
      <assert test="not (BT-6) or BG-22/BT-111" 
        flag="fatal" 
        id="BR-53">[BR-53]-If the VAT accounting currency code (BT-6) is present, then the Invoice total VAT amount in accounting currency (BT-111) shall be provided.
      </assert>
      <assert test="
        (((BG-25/BG-30/BT-151[normalize-space(.) = 'AE']) or (BG-20/BT-95[normalize-space(.) = 'AE']) or (BG-21/BT-102[normalize-space(.) = 'AE'])) and count(BG-23/BT-118[normalize-space(.) = 'AE'])=1)
        or 
          count(BG-23/BT-118[normalize-space(.) = 'AE'])=0" 
        flag="fatal" 
        id="BR-AE-01">[BR-AE-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Reverse charge” shall contain in the VATBReakdown (BG-23) exactly one VAT category code (BT-118) equal with "VAT reverse charge".
      </assert>
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'AE'] 
        and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63)
        and (BG-7/BT-48 or BG-7/BT-47))
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'AE'])" 
        flag="fatal" 
        id="BR-AE-02">[BR-AE-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “Reverse charge” shall contain the Seller VAT Identifier (BT-31), the Seller Tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63) and the Buyer VAT identifier (BT-48) and/or the Buyer legal registration identifier (BT-47).
      </assert>
      
      <assert test="(BG-20/BT-95[normalize-space(.) = 'AE'] 
        and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63)
        and (BG-7/BT-48 or BG-7/BT-47))
        or not (BG-20/BT-95[normalize-space(.) = 'AE'])" 
        flag="fatal" 
        id="BR-AE-03">[BR-AE-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Reverse charge” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63) and the Buyer VAT identifier (BT-48) and/or the Buyer legal registration identifier (BT-47).
      </assert>

      <assert test="(BG-21/BT-102[normalize-space(.) = 'AE'] 
        and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63)
        and (BG-7/BT-48 or BG-7/BT-47))
        or not (BG-21/BT-102[normalize-space(.) = 'AE'])" 
        flag="fatal" 
        id="BR-AE-04">[BR-AE-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “Reverse charge” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63) and the Buyer VAT identifier (BT-48) and/or the Buyer legal registration identifier (BT-47).
      </assert>

      <assert test="(BT-7 and not(BT-8))
        or (BT-8 and not(BT-7))
        or (not(BT-7) and not(BT-8))" 
       flag="fatal" 
       id="BR-CO-03">[BR-CO-03]-Value added tax point date (BT-7) and Value added tax point date code (BT-8) are mutually exclusive.
      </assert>
      
      <!--
        every $Currency in cbc:DocumentCurrencyCode satisfies 
        cac:LegalMonetaryTotal/xs:decimal(cbc:TaxInclusiveAmount) = 
        round( 
        (cac:LegalMonetaryTotal/xs:decimal(cbc:TaxExclusiveAmount) 
        + cac:TaxTotal/xs:decimal(cbc:TaxAmount[@currencyID=$Currency])) * 10 * 10) div 100"/>
      -->      
      <assert test="BG-22/xs:decimal(BT-112)=round((BG-22/xs:decimal(BT-109) + BG-22/xs:decimal(BT-110))*10*10)div 100" 
        flag="fatal" 
        id="BR-CO-15">[BR-CO-15]-Invoice total amount with VAT (BT-112) = Invoice total amount without VAT (BT-109) + Invoice total VAT amount (BT-110).
      </assert>

      <assert test="BG-23" 
        flag="fatal" 
        id="BR-CO-18">[BR-CO-18]-An Invoice shall at least have one VATBReakdown group (BG-23).
      </assert>

      <!-- Exempt from VAT (E) -->
      <assert test="
        (((BG-25/BG-30/BT-151[normalize-space(.) = 'E']) or (BG-20/BT-95[normalize-space(.) = 'E']) or (BG-21/BT-102[normalize-space(.) = 'E'])) and count(BG-23/BT-118[normalize-space(.) = 'E'])=1)
        or 
        count(BG-23/BT-118[normalize-space(.) = 'E'])=0" 
        flag="fatal" 
        id="BR-E-01">[BR-E-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Exempt from VAT” shall contain exactly one VATBReakdown (BG-23) with the VAT category code (BT-118) equal to "Exempt from VAT".
      </assert>
 
      <!--  Capire perchè non richiede nulla del Buyer   -->
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'E'] and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'E'])" 
        flag="fatal" 
        id="BR-E-02">[BR-E-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “Exempt from VAT” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>

      <assert test="(BG-20/BT-95[normalize-space(.) = 'E'] 
        and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63)
        )
        or not (BG-20/BT-95[normalize-space(.) = 'E'])" 
        flag="fatal" 
        id="BR-E-03">[BR-E-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Exempt from VAT” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>

      <assert test="(BG-21/BT-102[normalize-space(.) = 'E'] 
        and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63)
        )
        or not (BG-21/BT-102[normalize-space(.) = 'E'])" 
        flag="fatal" 
        id="BR-E-04">[BR-E-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “Exempt from VAT” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      
      <!-- Export outside the EU (G) -->
      <assert test="
        (((BG-25/BG-30/BT-151[normalize-space(.) = 'G']) or (BG-20/BT-95[normalize-space(.) = 'G']) or (BG-21/BT-102[normalize-space(.) = 'G'])) and count(BG-23/BT-118[normalize-space(.) = 'G'])=1)
        or 
        count(BG-23/BT-118[normalize-space(.) = 'G'])=0" 
        flag="fatal" 
        id="BR-G-01">[BR-G-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Export outside the EU” shall contain in the VATBReakdown (BG-23) exactly one VAT category code (BT-118) equal with "Export outside the EU".
      </assert>
      
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'G'] and (BG-4/BT-31 or BG-11/BT-63) )
                    or not (BG-25/BG-30/BT-151[normalize-space(.) = 'G'])" 
        flag="fatal" 
        id="BR-G-02">[BR-G-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “Export outside the EU” shall contain the Seller VAT Identifier (BT-31) or the Seller tax representative VAT identifier (BT-63).
      </assert>
      
      <assert test="(BG-20/BT-95[normalize-space(.) = 'G'] and (BG-4/BT-31 or BG-11/BT-63))
                    or not (BG-20/BT-95[normalize-space(.) = 'G'])" 
        flag="fatal" 
        id="BR-G-03">[BR-G-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Export outside the EU” shall contain the Seller VAT Identifier (BT-31) or the Seller tax representative VAT identifier (BT-63).
      </assert>
      
        <assert test="(BG-21/BT-102[normalize-space(.) = 'G'] 
          and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63)
          )
          or not (BG-21/BT-102[normalize-space(.) = 'G'])"         
          flag="fatal" 
        id="BR-G-04">[BR-G-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “Export outside the EU” shall contain the Seller VAT Identifier (BT-31) or the Seller tax representative VAT identifier (BT-63).
      </assert>

        <!-- Intra-community supply (K) -->
      <assert test="
        (((BG-25/BG-30/BT-151[normalize-space(.) = 'K']) or (BG-20/BT-95[normalize-space(.) = 'K']) or (BG-21/BT-102[normalize-space(.) = 'K'])) and count(BG-23/BT-118[normalize-space(.) = 'K'])=1)
        or count(BG-23/BT-118[normalize-space(.) = 'K'])=0" 
        flag="fatal" 
          id="BR-IC-01">[BR-IC-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Intra-community supply” shall contain in the VATBReakdown (BG-23) exactly one VAT category code (BT-118) equal with "Intra-community supply".
      </assert>
        
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'K'] and (BG-4/BT-31 or BG-11/BT-63 and BG-7/BT-48 ) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'K'])" 
        flag="fatal" 
        id="BR-IC-02">[BR-IC-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “Intra-community supply” shall contain the Seller VAT Identifier (BT-31) or the Seller tax representative VAT identifier (BT-63) and the Buyer VAT identifier (BT-48).
      </assert>
      
      <assert test="(BG-20/BT-95[normalize-space(.) = 'K'] and (BG-4/BT-31 or BG-4/BT-32 and BG-7/BT-48))
        or not (BG-20/BT-95[normalize-space(.) = 'K'])" 
        flag="fatal" 
        id="BR-IC-03">[BR-IC-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Intra-community supply” shall contain the Seller VAT Identifier (BT-31) or the Seller tax representative VAT identifier (BT-63) and the Buyer VAT identifier (BT-48).
      </assert>

      <assert test="(BG-21/BT-102[normalize-space(.) = 'K'] and (BG-4/BT-31 or BG-4/BT-32 and BG-7/BT-48))
        or not (BG-21/BT-102[normalize-space(.) = 'K'])"          
        flag="fatal" 
        id="BR-IC-04">[BR-IC-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “Intra-community supply” shall contain the Seller VAT Identifier (BT-31) or the Seller tax representative VAT identifier (BT-63) and the Buyer VAT identifier (BT-48).
      </assert>

      <assert test="(BG-23/normalize-space(BT-118)='K' and (string-length(normalize-space(BG-13/BT-72)) &gt;=1 or BG-13/BG-14))
        or BG-23/normalize-space(BT-118)!='K'" 
        flag="fatal" 
        id="BR-IC-11">[BR-IC-11]-In an Invoice with a VATBReakdown (BG-23) where the VAT category code (BT-118) is "Intra-community supply" the Actual delivery date (BT-72) or the Invoicing period (BG-14) shall not be blank.
      </assert>

      <assert test="(BG-23/normalize-space(BT-118)='K' and (string-length(normalize-space(BG-13/BG-15/BT-80)) &gt;=1 ))
        or BG-23/normalize-space(BT-118)!='K'" 
        flag="fatal" 
        id="BR-IC-12">[BR-IC-12]-In an Invoice with a VATBReakdown (BG-23) where the VAT category code (BT-118) is "Intra-community supply" the Deliver to country code (BT-80) shall not be blank.
      </assert>
      
      <!-- IGIC (L) -->
      <assert test="(((BG-25/BG-30/BT-151[normalize-space(.) = 'L']) or (BG-20/BT-95[normalize-space(.) = 'L']) or (BG-21/BT-102[normalize-space(.) = 'L'])) and count(BG-23/BT-118[normalize-space(.) = 'L'])=1)
        or count(BG-23/BT-118[normalize-space(.) = 'L'])=0" 
        flag="fatal" 
        id="BR-IG-01">[BR-IG-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “IGIC” shall contain in the VATBReakdown (BG-23) at least one VAT category code (BT-118) equal with "IGIC".
      </assert>
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'L'] and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63 ) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'L'])" 
        flag="fatal" 
        id="BR-IG-02">[BR-IG-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “IGIC” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <assert test="(BG-20/BT-95[normalize-space(.) = 'L'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-20/BT-95[normalize-space(.) = 'L'])" 
        flag="fatal" 
        id="BR-IG-03">[BR-IG-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “IGIC” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <assert test="(BG-21/BT-102[normalize-space(.) = 'L'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-21/BT-102[normalize-space(.) = 'L'])"          
        flag="fatal" 
        id="BR-IG-04">[BR-IG-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “IGIC” shall contain the Seller VAT Identifier (BT-31), the Seller Tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      
      <!-- IPSI (M) -->
      <assert test="(((BG-25/BG-30/BT-151[normalize-space(.) = 'M']) or (BG-20/BT-95[normalize-space(.) = 'M']) or (BG-21/BT-102[normalize-space(.) = 'M'])) and count(BG-23/BT-118[normalize-space(.) = 'M'])=1)
        or count(BG-23/BT-118[normalize-space(.) = 'M'])=0" 
        flag="fatal" 
        id="BR-IP-01">[BR-IP-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “IPSI” shall contain in the VATBReakdown (BG-23) at least one VAT category code (BT-118) equal with "IPSI".
      </assert>
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'M'] and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63 ) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'M'])" 
        flag="fatal" 
        id="BR-IP-02">[BR-IP-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “IPSI” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <assert test="(BG-20/BT-95[normalize-space(.) = 'M'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-20/BT-95[normalize-space(.) = 'M'])" 
        flag="fatal" 
        id="BR-IP-03">[BR-IP-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “IPSI” shall contain the Seller VAT Identifier (BT-31), the Seller Tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <assert test="(BG-21/BT-102[normalize-space(.) = 'M'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-21/BT-102[normalize-space(.) = 'M'])"
        flag="fatal" 
        id="BR-IP-04">[BR-IP-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “IPSI” shall contain the Seller VAT Identifier (BT-31), the Seller Tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      
      <!-- Not subject to VAT (O) -->     
      <assert test="(((BG-25/BG-30/BT-151[normalize-space(.) = 'O']) or (BG-20/BT-95[normalize-space(.) = 'O']) or (BG-21/BT-102[normalize-space(.) = 'O'])) and count(BG-23/BT-118[normalize-space(.) = 'O'])=1)
        or count(BG-23/BT-118[normalize-space(.) = 'O'])=0" 
        flag="fatal" 
        id="BR-O-01">[BR-O-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Not subject to VAT” shall contain exactly one VATBReakdown group (BG-23) with the VAT category code (BT-118) equal to "Not subject to VAT".
      </assert>
      <!-- Rivedere bene se i "non devono comparire" sono tutti in and ...     -->
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'M'] and (not(BG-4/BT-31) and not(BG-11/BT-63) and not(BG-7/BT-46) ) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'M'])" 
        flag="fatal" 
        id="BR-O-02">[BR-O-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “Not subject to VAT” shall not contain the Seller VAT identifier (BT-31), the Seller tax representative VAT identifier (BT-63) or the Buyer VAT identifier (BT-46).
      </assert>
      <assert test="(BG-20/BT-95[normalize-space(.) = 'O'] and (not(BG-4/BT-31) and not(BG-11/BT-63) and not(BG-7/BT-46) ) )
        or not (BG-20/BT-95[normalize-space(.) = 'O'])" 
        flag="fatal" 
        id="BR-O-03">[BR-O-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Not subject to VAT” shall not contain the Seller VAT identifier (BT-31), the Seller tax representative VAT identifier (BT-63) or the Buyer VAT identifier (BT-48).
      </assert>
      <assert test="(BG-21/BT-102[normalize-space(.) = 'O'] and (not(BG-4/BT-31) and not(BG-11/BT-63) and not(BG-7/BT-46) ) )
        or not (BG-21/BT-102[normalize-space(.) = 'O'])"
        flag="fatal" 
        id="BR-O-04">[BR-O-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “Not subject to VAT” shall not contain the Seller VAT identifier (BT-31), the Seller tax representative VAT identifier (BT-63) or the Buyer VAT identifier (BT-48).
      </assert>     
      <assert test="BT-118[normalize-space(.) = 'O'] and count(BG-23/BT-118[normalize-space(.) = 'O'])=1
        or count(BG-23/BT-118[normalize-space(.) = 'O'])=0" 
        flag="fatal" 
        id="BR-O-11">[BR-O-11]-An Invoice that contains a VATBReakdown group (BG-23) with a VAT category code (BT-118) "Not subject to VAT" shall not contain other VATBReakdown groups (BG-23).
      </assert>
      <assert test="BT-118[normalize-space(.) = 'O'] and count(BG-25/BG-30/BT-151[normalize-space(.) = 'O'])=1
        or count(BG-23/BT-118[normalize-space(.) = 'O'])=0" 
        flag="fatal" 
        id="BR-O-12">[BR-O-12]-An Invoice that contains a VATBReakdown group (BG-23) with a VAT category code (BT-118) "Not subject to VAT" shall not contain an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is not "Not subject to VAT".
      </assert>
      <assert test="BT-118[normalize-space(.) = 'O'] and count(BG-20/BT-95[normalize-space(.) = 'O'])=1
        or count(BG-23/BT-118[normalize-space(.) = 'O'])=0" 
        flag="fatal" 
        id="BR-O-13">[BR-O-13]-An Invoice that contains a VATBReakdown group (BG-23) with a VAT category code (BT-118) "Not subject to VAT" shall not contain Document level allowances (BG-20) where Document level allowance VAT category code (BT-95) is not "Not subject to VAT".
      </assert>
      <assert test="BT-118[normalize-space(.) = 'O'] and count(BG-21/BT-102[normalize-space(.) = 'O'])=1
        or count(BG-23/BT-118[normalize-space(.) = 'O'])=0" 
        flag="fatal" 
        id="BR-O-14">[BR-O-14]-An Invoice that contains a VATBReakdown group (BG-23) with a VAT category code (BT-118) "Not subject to VAT" shall not contain Document level charges (BG-21) where Document level charge VAT category code (BT-102) is not "Not subject to VAT".
      </assert>
      
      <!-- Standard rated (S) -->
      
    <!--      
        <assert test="(((BG-25/BG-30/BT-151[normalize-space(.) = 'S']) or (BG-20/BT-95[normalize-space(.) = 'S']) or (BG-21/BT-102[normalize-space(.) = 'S'])) and count(BG-23/BT-118[normalize-space(.) = 'S'])&gt; 0)
        or count(BG-23/BT-118[normalize-space(.) = 'S'])=0" 
        flag="fatal" 
        id="BR-S-01">[BR-S-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Standard rated” shall contain in the VATBReakdown (BG-23) at least one VAT category code (BT-118) equal with "Standard rated".
      </assert>
      -->

      <assert test="
        every $vatcode in (BG-25/BG-30/BT-151[normalize-space(.) = ('S','B')] | BG-20/BT-95[normalize-space(.) = ('S','B')] | BG-21/BT-102[normalize-space(.) = ('S','B')])
        satisfies (count(BG-23/BT-118[normalize-space(.)=$vatcode]) &gt; 0) ">
        ">
        flag="fatal"
        id="BR-S-01">
        [BR-S-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Standard rated” shall contain in the VATBReakdown (BG-23) at least one VAT category code (BT-118) equal with "Standard rated".
      </assert> 
      <!--
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'S'] and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63 ) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'S'])" 
        flag="fatal" 
        id="BR-S-02">[BR-S-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “Standard rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      -->
      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'S' or 'B'] and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63 ) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'S' or 'B'])" 
        flag="fatal" 
        id="BR-S-02">[BR-S-02]-An Invoice that contains an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is “Standard rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <!--
      <assert test="(BG-20/BT-95[normalize-space(.) = 'S'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-20/BT-95[normalize-space(.) = 'S'])" 
        flag="fatal" 
        id="BR-S-03">[BR-S-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Standard rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      -->
      
      <assert test="(BG-20/BT-95[normalize-space(.) = 'S' or 'B'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-20/BT-95[normalize-space(.) = 'S' or 'B'])" 
        flag="fatal" 
        id="BR-S-03">[BR-S-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Standard rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <!--    
      <assert test="(BG-21/BT-102[normalize-space(.) = 'S'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-21/BT-102[normalize-space(.) = 'S'])"
        flag="fatal" 
        id="BR-S-04">[BR-S-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “Standard rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      -->
      <assert test="(BG-21/BT-102[normalize-space(.) = 'S' or 'B'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-21/BT-102[normalize-space(.) = 'S' or 'B'])"
        flag="fatal" 
        id="BR-S-04">[BR-S-04]-An Invoice that contains a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is “Standard rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      
      <!-- Zero rated ()Z -->
      <assert test="(((BG-25/BG-30/BT-151[normalize-space(.) = 'Z']) or (BG-20/BT-95[normalize-space(.) = 'Z']) or (BG-21/BT-102[normalize-space(.) = 'Z'])) and count(BG-23/BT-118[normalize-space(.) = 'Z'])=1)
        or count(BG-23/BT-118[normalize-space(.) = 'Z'])=0" 
        flag="fatal" 
        id="BR-Z-01">[BR-Z-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Zero rated” shall contain in the VATBReakdown (BG-23) exactly one VAT category code (BT-118) equal with "Zero rated".
      </assert>

      <assert test="(BG-25/BG-30/BT-151[normalize-space(.) = 'Z'] and (BG-4/BT-31 or BG-4/BT-32 or BG-11/BT-63 ) )
        or not (BG-25/BG-30/BT-151[normalize-space(.) = 'Z'])" 
        flag="fatal" 
        id="BR-Z-02">[BR-Z-02]-An Invoice that contains an Invoice line where the Invoiced item VAT category code (BT-151) is “Zero rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <assert test="(BG-20/BT-95[normalize-space(.) = 'Z'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-20/BT-95[normalize-space(.) = 'Z'])" 
        flag="fatal" 
        id="BR-Z-03">[BR-Z-03]-An Invoice that contains a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is “Zero rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      <assert test="(BG-21/BT-102[normalize-space(.) = 'Z'] and (BG-4/BT-31 or BG-4/BT-32 and BG-11/BT-63))
        or not (BG-21/BT-102[normalize-space(.) = 'Z'])"
        flag="fatal" 
        id="BR-Z-04">[BR-Z-04]-An Invoice that contains a Document level charge where the Document level charge VAT category code (BT-102) is “Zero rated” shall contain the Seller VAT Identifier (BT-31), the Seller tax registration identifier (BT-32) and/or the Seller tax representative VAT identifier (BT-63).
      </assert>
      
      <!--
      perchè non c'è normalize-space nella rule CEN?
      <param name="BR-CO-09" value="( contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW',substring(cbc:CompanyID,1,2) ) )"/>
      
      Si applica a BT-31 BT-63 e BT-48
      -->
      <assert test="
        contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW',substring(normalize-space(BG-4/BT-31),1,2) )
        and contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW',substring(normalize-space(BG-11/BT-63),1,2) )
        and contains( 'AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH EL ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW',substring(normalize-space(BG-7/BT-48),1,2) )
        " 
        flag="fatal" 
        id="BR-CO-09">[BR-CO-09]-The Seller VAT identifier (BT-31), the Seller tax representative VAT identifier (BT-63) and the Buyer VAT identifier (BT-48) shall have a prefix in accordance with ISO code ISO 3166-1 alpha-2 by which the country of issue may be identified. Nevertheless, Greece may use the prefix ‘EL’.
      </assert>      
      
    </rule>
    <rule context="BG-1/BT-21">
      <!--  
        <param name="BR-CL-08" value="(contains(.,'#') and ( ( contains(' AAA AAB AAC AAD AAE AAF AAG AAI AAJ AAK AAL AAM AAN AAO AAP AAQ AAR AAS AAT AAU AAV AAW AAX AAY AAZ ABA ABB ABC ABD ABE ABF ABG ABH ABI ABJ ABK ABL ABM ABN ABO ABP ABQ ABR ABS ABT ABU ABV ABW ABX ABZ ACA ACB ACC ACD ACE ACF ACG ACH ACI ACJ ACK ACL ACM ACN ACO ACP ACQ ACR ACS ACT ACU ACV ACW ACX ACY ACZ ADA ADB ADC ADD ADE ADF ADG ADH ADI ADJ ADK ADL ADM ADN ADO ADP ADQ ADR ADS ADT ADU ADV ADW ADX ADY ADZ AEA AEB AEC AED AEE AEF AEG AEH AEI AEJ AEK AEL AEM AEN AEO AEP AEQ AER AES AET AEU AEV AEW AEX AEY AEZ AFA AFB AFC AFD AFE AFF AFG AFH AFI AFJ AFK AFL AFM AFN AFO AFP AFQ AFR AFS AFT AFU AFV AFW AFX AFY AFZ AGA AGB AGC AGD AGE AGF AGG AGH AGI AGJ AGK AGL AGM AGN AGO AGP AGQ AGR AGS AGT AGU AGV AGW AGX AGY AGZ AHA AHB AHC AHD AHE AHF AHG AHH AHI AHJ AHK AHL AHM AHN AHO AHP AHQ AHR AHS AHT AHU AHV AHW AHX AHY AHZ AIA AIB AIC AID AIE AIF AIG AIH AII AIJ AIK AIL AIM AIN AIO AIP AIQ AIR AIS AIT AIU AIV AIW AIX AIY AIZ AJA AJB ALC ALD ALE ALF ALG ALH ALI ALJ ALK ALL ALM ALN ALO ALP ALQ ARR ARS AUT AUU AUV AUW AUX AUY AUZ AVA AVB AVC AVD AVE AVF BAG BAH BAI BAJ BAK BAL BAM BAN BAO BAP BAQ BLC BLD BLE BLF BLG BLH BLI BLJ BLK BLL BLM BLN BLO BLP BLQ BLR BLS BLT BLU BLV BLW BLX BLY BLZ BMA BMB BMC BMD BME CCI CEX CHG CIP CLP CLR COI CUR CUS DAR DCL DEL DIN DOC DUT EUR FBC GBL GEN GS7 HAN HAZ ICN IIN IMI IND INS INV IRP ITR ITS LAN LIN LOI MCO MDH MKS ORI OSI PAC PAI PAY PKG PKT PMD PMT PRD PRF PRI PUR QIN QQD QUT RAH REG RET REV RQR SAF SIC SIN SLR SPA SPG SPH SPP SPT SRN SSR SUR TCA TDT TRA TRR TXD WHI ZZZ ',substring-before(substring-after(.,'#'),'#') ) ) )) or not(contains(.,'#'))"/>
      -->
      <assert test="(contains(.,'#') and ( ( contains(' AAA AAB AAC AAD AAE AAF AAG AAI AAJ AAK AAL AAM AAN AAO AAP AAQ AAR AAS AAT AAU AAV AAW AAX AAY AAZ ABA ABB ABC ABD ABE ABF ABG ABH ABI ABJ ABK ABL ABM ABN ABO ABP ABQ ABR ABS ABT ABU ABV ABW ABX ABZ ACA ACB ACC ACD ACE ACF ACG ACH ACI ACJ ACK ACL ACM ACN ACO ACP ACQ ACR ACS ACT ACU ACV ACW ACX ACY ACZ ADA ADB ADC ADD ADE ADF ADG ADH ADI ADJ ADK ADL ADM ADN ADO ADP ADQ ADR ADS ADT ADU ADV ADW ADX ADY ADZ AEA AEB AEC AED AEE AEF AEG AEH AEI AEJ AEK AEL AEM AEN AEO AEP AEQ AER AES AET AEU AEV AEW AEX AEY AEZ AFA AFB AFC AFD AFE AFF AFG AFH AFI AFJ AFK AFL AFM AFN AFO AFP AFQ AFR AFS AFT AFU AFV AFW AFX AFY AFZ AGA AGB AGC AGD AGE AGF AGG AGH AGI AGJ AGK AGL AGM AGN AGO AGP AGQ AGR AGS AGT AGU AGV AGW AGX AGY AGZ AHA AHB AHC AHD AHE AHF AHG AHH AHI AHJ AHK AHL AHM AHN AHO AHP AHQ AHR AHS AHT AHU AHV AHW AHX AHY AHZ AIA AIB AIC AID AIE AIF AIG AIH AII AIJ AIK AIL AIM AIN AIO AIP AIQ AIR AIS AIT AIU AIV AIW AIX AIY AIZ AJA AJB ALC ALD ALE ALF ALG ALH ALI ALJ ALK ALL ALM ALN ALO ALP ALQ ARR ARS AUT AUU AUV AUW AUX AUY AUZ AVA AVB AVC AVD AVE AVF BAG BAH BAI BAJ BAK BAL BAM BAN BAO BAP BAQ BLC BLD BLE BLF BLG BLH BLI BLJ BLK BLL BLM BLN BLO BLP BLQ BLR BLS BLT BLU BLV BLW BLX BLY BLZ BMA BMB BMC BMD BME CCI CEX CHG CIP CLP CLR COI CUR CUS DAR DCL DEL DIN DOC DUT EUR FBC GBL GEN GS7 HAN HAZ ICN IIN IMI IND INS INV IRP ITR ITS LAN LIN LOI MCO MDH MKS ORI OSI PAC PAI PAY PKG PKT PMD PMT PRD PRF PRI PUR QIN QQD QUT RAH REG RET REV RQR SAF SIC SIN SLR SPA SPG SPH SPP SPT SRN SSR SUR TCA TDT TRA TRR TXD WHI ZZZ ',substring-before(substring-after(.,'#'),'#') ) ) )) or not(contains(.,'#'))" 
        flag="fatal" 
        id="BR-CL-08">[BR-CL-08]-Invoiced note subject code SHOULD be coded using UNCL4451
      </assert>
    </rule>   
    <rule context="BG-4">
      <assert test="BT-29 or BT-30 or BT-31" 
        flag="fatal" 
        id="BR-CO-26">[BR-CO-26]-In order for the buyer to automatically identify a supplier, the Seller identifier (BT-29), the Seller legal registration identifier (BT-30) and/or the Seller VAT identifier (BT-31) shall be present.
      </assert>
    </rule>
    <rule context="BG-8">
      <assert test="BT-55" 
        flag="fatal" 
        id="BR-11">[BR-11]-The Buyer postal address shall contain a Buyer country code (BT-55).</assert>
    </rule>
    <rule context="BG-10">
      <!--   
      <param name="BR-17" 
      value="exists(cac:PartyName/cbc:Name) and (not(cac:PartyName/cbc:Name = ../cac:AccountingSupplierParty/cac:Party/cac:PartyName/cbc:Name) 
      and not(cac:PartyIdentification/cbc:ID = ../cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID) 
      and not(cac:PartyLegalEntity/cbc:RegistrationName = ../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))"/>
    BT-59 => BT-28 Seller trading name
    BT-60 (/Invoice/cac:PayeeParty/cac:PartyIdentification/cbc:ID) =>

    <rule context="cac:PayeeParty">
        <assert test="exists(cac:PartyName/cbc:Name) 
        and (not(cac:PartyName/cbc:Name = ../cac:AccountingSupplierParty/cac:Party/cac:PartyName/cbc:Name) 
        and not(cac:PartyIdentification/cbc:ID = ../cac:AccountingSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID) 
        and not(cac:PartyLegalEntity/cbc:RegistrationName = ../cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName))"
                flag="fatal"
                id="BR-17">[BR-17]-The Payee name (BT-59) shall be provided in the Invoice, if the Payee (BG-10) is different from the Seller (BG-4)</assert>
    </rule>

    !!! cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName non esiste per cui non scatta mai!
    non si usa neanche normalize-spaces() nè gli scheme 
     PERCHE' POSSO AVERE PIU' BT-29 e con normalize andrebbe in errore!!!!!
    -->     
    <!--   
      Regola con normalize-space che non funziona se si hanno più BT-29
        <assert test="BT-59 
        and not (normalize-space(BT-59)= normalize-space(../BG-4/BT-28))
        and not (normalize-space(BT-60)= normalize-space(../BG-4/BT-29))
        and not (normalize-space(BT-60/@scheme)= normalize-space(../BG-4/BT-29/@scheme))
        " 
        flag="fatal" id="BR-17">[BR-17]-The Payee name (BT-59) shall be provided in the Invoice, if the Payee (BG-10) is different from the Seller (BG-4)
      </assert>
      -->
      <assert test="BT-59 
        and not ((BT-59)= (../BG-4/BT-28))
        and not ((BT-60)= (../BG-4/BT-29))
        and not ((BT-60/@scheme)= (../BG-4/BT-29/@scheme))
        " 
        flag="fatal" id="BR-17">[BR-17]-The Payee name (BT-59) shall be provided in the Invoice, if the Payee (BG-10) is different from the Seller (BG-4)
      </assert>      
      
    </rule>
    <rule context="BG-13/BG-14">
      <assert test="(BT-73 and BT-74 and BT-74 &gt;= BT-73) or not(BT-73) or not(BT-74)" 
        flag="fatal" 
        id="BR-29">[BR-29]-If both Invoicing period start date (BT-73) and Invoicing period end date (BT-74) are given then the Invoicing period end date (BT-74) shall be later or equal to the Invoicing period start date (BT-73).
      </assert>
      <assert test="BT-73 or BT-74" 
        flag="fatal" 
        id="BR-CO-19">[BR-CO-19]-If Invoicing period (BG-14) is used, the Invoicing period start date (BT-73) or the Invoicing period end date (BT-74) shall be filled, or both.
      </assert>
    </rule>   
    <rule context="BG-15">
      <assert test="BT-80" 
        flag="fatal" 
        id="BR-57">[BR-57]-Each Deliver to address (BG-15) shall contain a Deliver to country code (BT-80).
      </assert>
    </rule>

    <rule context="BG-16">
      <!--      
        <param name="BR-61" value="(exists(cac:PayeeFinancialAccount/cbc:ID) and ((normalize-space(cbc:PaymentMeansCode) = '30') or (normalize-space(cbc:PaymentMeansCode) = '58') )) or ((normalize-space(cbc:PaymentMeansCode) != '30') and (normalize-space(cbc:PaymentMeansCode) != '58'))"/>
      -->     
      <assert test="(BG-17/BT-84 and (normalize-space(BT-81)='30' or normalize-space(BT-81)='58')) 
        or (normalize-space(BT-81)!='30' and normalize-space(BT-81)!='58')  " 
        flag="fatal" 
        id="BR-61">[BR-61]-If the Payment means type code (BT-81) means SEPA credit transfer, Local credit transfer or Non-SEPA international credit transfer, the Payment account identifier (BT-84) shall be present.
      </assert>
    </rule>    
    
    <rule context="BG-18">
      <assert test="string-length(BT-87)&gt;=4 and string-length(BT-87)&lt;=6" 
        flag="fatal" 
        id="BR-51">[BR-51]-The last 4 to 6 digits of the Payment card primary account number (BT-87) shall be present if Payment card information (BG-18) is provided in the Invoice.
      </assert>
    </rule>
    <rule context="BG-20">
      <assert test="BT-92" 
        flag="fatal" 
        id="BR-31">[BR-31]-Each Document level allowance (BG-20) shall have a Document level allowance amount (BT-92).
      </assert>
      <assert test="BT-95" 
        flag="fatal" 
        id="BR-32">[BR-32]-Each Document level allowance (BG-20) shall have a Document level allowance VAT category code (BT-95).
      </assert>
      <!--  Che differenza esiste fra  BR-CO-21 e BR-33 ??? -->
      <assert test="BT-97 or BT-98" 
        flag="fatal" 
        id="BR-33">[BR-33]-Each Document level allowance (BG-20) shall have a Document level allowance reason (BT-97) or a Document level allowance reason code (BT-98).
      </assert>
      <!-- Capire che senso ha questa rules al momento -->
      <assert test="true()" 
        flag="fatal" 
        id="BR-CO-05">[BR-CO-05]-Document level allowance reason code (BT-98) and Document level allowance reason (BT-97) shall indicate the same type of allowance.
      </assert>
      <!--  Che differenza esiste fra  BR-CO-21 e BR-33 ??? -->
      <assert test="BT-97 or BT-98" 
        flag="fatal" 
        id="BR-CO-21">[BR-CO-21]-Each Document level allowance (BG-20) shall contain a Document level allowance reason (BT-97) or a Document level allowance reason code (BT-98), or both.
      </assert>
      
      <!--Reverse charge (AE)-->
      <assert test=".[normalize-space(BT-95) = 'AE']/BT-96=0 or not(.[normalize-space(BT-95) = 'AE'])" 
        flag="fatal" 
        id="BR-AE-06">[BR-AE-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "Reverse charge" the Document level allowance VAT rate (BT-96) shall be 0 (zero).
      </assert>
      <!--Exempt from VAT (E)-->      
      <assert test=".[normalize-space(BT-95) = 'E']/BT-96=0 or not(.[normalize-space(BT-95) = 'E'])" 
        flag="fatal" 
        id="BR-E-06">[BR-E-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "Exempt from VAT", the Document level allowance VAT rate (BT-96) shall be 0 (zero).
      </assert>
      <!-- Export outside the EU (G) -->      
      <assert test=".[normalize-space(BT-95) = 'G']/BT-96=0 or not(.[normalize-space(BT-95) = 'G'])" 
        flag="fatal" 
        id="BR-G-06">[BR-G-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "Export outside the EU" the Document level allowance VAT rate (BT-96) shall be 0 (zero).
      </assert>
      <!--Intra-community supply (K) -->      
      <assert test=".[normalize-space(BT-95) = 'K']/BT-96=0 or not(.[normalize-space(BT-95) = 'K'])" 
        flag="fatal" 
        id="BR-IC-06">[BR-IC-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "Intra-community supply" the Document level allowance VAT rate (BT-96) shall be 0 (zero).
      </assert>
      <!--IGIC (L)-->      
      <assert test=".[normalize-space(BT-95) = 'L']/BT-96 &gt;= 0 or not(.[normalize-space(BT-95) = 'L'])" 
        flag="fatal" 
        id="BR-IG-06">[BR-IG-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "IGIC" the Document level allowance VAT rate (BT-96) shall be 0 (zero) or greater than zero.
      </assert>
      <!--IPSI (M)--> 
      <assert test=".[normalize-space(BT-95) = 'M']/BT-96 &gt;= 0 or not(.[normalize-space(BT-95) = 'M'])" 
        flag="fatal" 
        id="BR-IP-06">[BR-IP-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "IPSI" the Document level allowance VAT rate (BT-96) shall be 0 (zero) or greater than zero.
      </assert>
      <!--Not subject to VAT (O) -->
      <assert test="not(.[normalize-space(BT-95) = 'O']/BT-96) or not(.[normalize-space(BT-95) = 'O'])" 
        flag="fatal" 
        id="BR-O-06">[BR-O-06]-A Document level allowance (BG-20) where VAT category code (BT-95) is "Not subject to VAT" shall not contain a Document level allowance VAT rate (BT-96).
      </assert>
      <!--Standard rated (S) -->      
      <!--<assert test=".[normalize-space(BT-95) = 'S']/BT-96 &gt; 0 or not(.[normalize-space(BT-95) = 'S'])" 
        flag="fatal" 
        id="BR-S-06">[BR-S-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "Standard rated" the Document level allowance VAT rate (BT-96) shall be greater than zero.
      </assert>
      -->
      <assert test=".[normalize-space(BT-95) = ('S','B')]/BT-96 &gt; 0 or not(.[normalize-space(BT-95) = ('S','B')])" 
        flag="fatal" 
        id="BR-S-06">[BR-S-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "Standard rated" the Document level allowance VAT rate (BT-96) shall be greater than zero.
      </assert>
      
      <!--Zero rated (Z) -->
      <assert test=".[normalize-space(BT-95) = 'Z']/BT-96=0 or not(.[normalize-space(BT-95) = 'Z'])" 
        flag="fatal" 
        id="BR-Z-06">[BR-Z-06]-In a Document level allowance (BG-20) where the Document level allowance VAT category code (BT-95) is "Zero rated" the Document level allowance VAT rate (BT-96) shall be 0 (zero).
      </assert>
      
    </rule>
    <rule context="BG-21">
      <assert test="BT-99" 
       flag="fatal" 
       id="BR-36">[BR-36]-Each Document level charge (BG-21) shall have a Document level charge amount (BT-99).
      </assert>
      <assert test="BT-102" 
        flag="fatal" 
        id="BR-37">[BR-37]-Each Document level charge (BG-21) shall have a Document level charge VAT category code (BT-102).
      </assert>
      <!--  Che differenza esiste fra  BR-CO-22 e BR-38 ??? -->
      <assert test="BT-104 or BT-105" 
       flag="fatal" 
       id="BR-38">[BR-38]-Each Document level charge (BG-21) shall have a Document level charge reason (BT-104) or a Document level charge reason code (BT-105).    
      </assert>
      <assert test="true()" 
       flag="fatal" 
       id="BR-CO-06">[BR-CO-06]-Document level charge reason code (BT-105) and Document level charge reason (BT-104) shall indicate the same type of charge.
      </assert>
      <!--  Che differenza esiste fra  BR-CO-22 e BR-38 ???-->
      <assert test="BT-104 or BT-105" 
       flag="fatal"
       id="BR-CO-22">[BR-CO-22]-Each Document level charge (BG-21) shall contain a Document level charge reason (BT-104) or a Document level charge reason code (BT-105), or both.
      </assert>

      <!--Reverse charge (AE)-->
      <assert test=".[normalize-space(BT-102) = 'AE']/BT-103=0 or not(.[normalize-space(BT-95) = 'AE'])"
        flag="fatal" 
        id="BR-AE-07">[BR-AE-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "Reverse charge" the Document level charge VAT rate (BT-103) shall be 0 (zero).
      </assert>
      <!--Exempt from VAT (E)-->
      <assert test=".[normalize-space(BT-102) = 'E']/BT-103=0 or not(.[normalize-space(BT-95) = 'E'])"
        flag="fatal" 
        id="BR-E-07">[BR-E-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "Exempt from VAT", the Document level charge VAT rate (BT-103) shall be 0 (zero).
      </assert>
      <!-- Export outside the EU (G) -->
      <assert test=".[normalize-space(BT-102) = 'G']/BT-103=0 or not(.[normalize-space(BT-95) = 'G'])"
        flag="fatal" 
        id="BR-G-07">[BR-G-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "Export outside the EU" the Document level charge VAT rate (BT-103) shall be 0 (zero).
      </assert>
      <!--Intra-community supply (K) -->       
      <assert test=".[normalize-space(BT-102) = 'K']/BT-103=0 or not(.[normalize-space(BT-95) = 'K'])"
        flag="fatal" 
        id="BR-IC-07">[BR-IC-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "Intra-community supply" the Document level charge VAT rate (BT-103) shall be 0 (zero).
      </assert>
      <!--IGIC (L)-->
      <assert test=".[normalize-space(BT-102) = 'L']/BT-103 &gt;= 0 or not(.[normalize-space(BT-95) = 'L'])"
        flag="fatal" 
        id="BR-IG-07">[BR-IG-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "IGIC" the Document level charge VAT rate (BT-103) shall be 0 (zero) or greater than zero.
      </assert>
      <!--IPSI (M)-->
      <assert test=".[normalize-space(BT-102) = 'M']/BT-103 &gt;= 0 or not(.[normalize-space(BT-95) = 'M'])"
        flag="fatal" 
        id="BR-IP-07">[BR-IP-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "IPSI" the Document level charge VAT rate (BT-103) shall be 0 (zero) or greater than zero.
      </assert>
      <!--Not subject to VAT (O) -->
      <assert test="not(.[normalize-space(BT-102) = 'O']/BT-103) or not(.[normalize-space(BT-95) = 'O'])"
        flag="fatal" 
        id="BR-O-07">[BR-O-07]-A Document level charge (BG-21) where the VAT category code (BT-102) is "Not subject to VAT" shall not contain a Document level charge VAT rate (BT-103).
      </assert>
      <!--Standard rated (S) -->
      <!--<assert test=".[normalize-space(BT-102) = 'S']/BT-103 &gt; 0 or not(.[normalize-space(BT-102) = 'S'])"
        flag="fatal" 
        id="BR-S-07">[BR-S-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "Standard rated" the Document level charge VAT rate (BT-103) shall be greater than zero.
      </assert>
      -->
      <assert test=".[normalize-space(BT-102) = ('S' , 'B')]/BT-103 &gt; 0 or not(.[normalize-space(BT-102) = ('S' , 'B')])"
        flag="fatal" 
        id="BR-S-07">[BR-S-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "Standard rated" the Document level charge VAT rate (BT-103) shall be greater than zero.
      </assert>
      
      <!--Zero rated (Z) -->
      <assert test=".[normalize-space(BT-102) = 'Z']/BT-103 = 0 or not(.[normalize-space(BT-95) = 'Z'])"
        flag="fatal" 
        id="BR-Z-07">[BR-Z-07]-In a Document level charge (BG-21) where the Document level charge VAT category code (BT-102) is "Zero rated" the Document level charge VAT rate (BT-103) shall be 0 (zero).
      </assert>

    </rule>
    <rule context="BG-22">
      <assert test="BT-106" 
        flag="fatal" 
        id="BR-12">[BR-12]-An Invoice shall have the Sum of Invoice line net amount (BT-106).
      </assert>
      <assert test="BT-109" 
       flag="fatal" 
       id="BR-13">[BR-13]-An Invoice shall have the Invoice total amount without VAT (BT-109).
      </assert>
      <assert test="BT-112" 
       flag="fatal" 
       id="BR-14">[BR-14]-An Invoice shall have the Invoice total amount with VAT (BT-112).
      </assert>
      <assert test="BT-115" 
        flag="fatal" 
        id="BR-15">[BR-15]-An Invoice shall have the Amount due for payment (BT-115).
      </assert>

      <assert test="(xs:decimal(BT-106) = (round(sum(../BG-25/xs:decimal(BT-131)) * 10 * 10) div 100))" 
        flag="fatal" 
        id="BR-CO-10">[BR-CO-10]-Sum of Invoice line net amount (BT-106) = Σ Invoice line net amount (BT-131).
      </assert>
      <assert test="xs:decimal(BT-107) = (round(sum(xs:decimal(BT-92)) * 10 * 10) div 100) or not(BT-92)"
        flag="fatal" 
        id="BR-CO-11">[BR-CO-11]-Sum of allowances on document level (BT-107) = Σ Document level allowance amount (BT-92).
      </assert>
      <assert test="xs:decimal(BT-108) =(round(sum(../BG-21/xs:decimal(BT-99)) * 10 * 10) div 100) or not(BT-108)" 
        flag="fatal" 
        id="BR-CO-12">[BR-CO-12]-Sum of charges on document level (BT-108) = Σ Document level charge amount (BT-99).
      </assert>

      <!--  
        ((cbc:ChargeTotalAmount) and (cbc:AllowanceTotalAmount) and (xs:decimal(cbc:TaxExclusiveAmount) 
        = round((xs:decimal(cbc:LineExtensionAmount) + xs:decimal(cbc:ChargeTotalAmount) - xs:decimal(cbc:AllowanceTotalAmount)) * 10 * 10) div 100 ))  

        or (not(cbc:ChargeTotalAmount) and (cbc:AllowanceTotalAmount) and (xs:decimal(cbc:TaxExclusiveAmount) 
        = round((xs:decimal(cbc:LineExtensionAmount) - xs:decimal(cbc:AllowanceTotalAmount)) * 10 * 10 ) div 100)) 
        
        or ((cbc:ChargeTotalAmount) and not(cbc:AllowanceTotalAmount) and (xs:decimal(cbc:TaxExclusiveAmount) 
        = round((xs:decimal(cbc:LineExtensionAmount) + xs:decimal(cbc:ChargeTotalAmount)) * 10 * 10 ) div 100)) 
        
        or (not(cbc:ChargeTotalAmount) and not(cbc:AllowanceTotalAmount) and (xs:decimal(cbc:TaxExclusiveAmount) 
        = xs:decimal(cbc:LineExtensionAmount)))
      
      ..................
      nel context
      cbc:TaxExclusiveAmount = BT-109
      cbc:LineExtensionAmount = BT-106 => Σ Invoice line net amount (BT-131)
      
      
        ((BT-108) and (BT-107) and (xs:decimal(BT-109) 
        = round((xs:decimal(BT-106) + xs:decimal(BT-108) - xs:decimal(BT-107)) * 10 * 10) div 100 ))  

        or (not(BT-108) and (BT-107) and (xs:decimal(BT-109) 
        = round((xs:decimal(BT-106) - xs:decimal(BT-107)) * 10 * 10 ) div 100)) 
        
        or ((BT-108) and not(BT-107) and (xs:decimal(BT-109) 
        = round((xs:decimal(BT-106) + xs:decimal(BT-108)) * 10 * 10 ) div 100)) 
        
        or (not(BT-108) and not(BT-107) and (xs:decimal(BT-109) 
        = xs:decimal(BT-106)))

      -->
      <assert test="
        ((BT-108) and (BT-107) and (xs:decimal(BT-109) 
        = round((xs:decimal(BT-106) + xs:decimal(BT-108) - xs:decimal(BT-107)) * 10 * 10) div 100 ))  
        
        or (not(BT-108) and (BT-107) and (xs:decimal(BT-109) 
        = round((xs:decimal(BT-106) - xs:decimal(BT-107)) * 10 * 10 ) div 100)) 
        
        or ((BT-108) and not(BT-107) and (xs:decimal(BT-109) 
        = round((xs:decimal(BT-106) + xs:decimal(BT-108)) * 10 * 10 ) div 100)) 
        
        or (not(BT-108) and not(BT-107) and (xs:decimal(BT-109) 
        = xs:decimal(BT-106)))" 
       flag="fatal" 
       id="BR-CO-13">[BR-CO-13]-Invoice total amount without VAT (BT-109) = Σ Invoice line net amount (BT-131) - Sum of allowances on document level (BT-107) + Sum of charges on document level (BT-108).
      </assert>
      <!--
        cbc:PrepaidAmount=BT-113
        cbc:PayableRoundingAmount=BT-114
        cbc:PayableAmount=BT-115
        cbc:TaxInclusiveAmount=BT-112

        (xs:decimal(cbc:PrepaidAmount) and not(xs:decimal(cbc:PayableRoundingAmount)) and (xs:decimal(cbc:PayableAmount) 
        = (round((xs:decimal(cbc:TaxInclusiveAmount) - xs:decimal(cbc:PrepaidAmount)) * 10 * 10) div 100))) 
        or (not(xs:decimal(cbc:PrepaidAmount)) and not(xs:decimal(cbc:PayableRoundingAmount)) and xs:decimal(cbc:PayableAmount) 
        = xs:decimal(cbc:TaxInclusiveAmount)) 
        or (xs:decimal(cbc:PrepaidAmount) and xs:decimal(cbc:PayableRoundingAmount) and ((round((xs:decimal(cbc:PayableAmount) - xs:decimal(cbc:PayableRoundingAmount)) * 10 * 10) div 100) 
        = (round((xs:decimal(cbc:TaxInclusiveAmount) - xs:decimal(cbc:PrepaidAmount)) * 10 * 10) div 100))) 
        or (not(xs:decimal(cbc:PrepaidAmount)) and xs:decimal(cbc:PayableRoundingAmount) and ((round((xs:decimal(cbc:PayableAmount) - xs:decimal(cbc:PayableRoundingAmount)) * 10 * 10) div 100) 
        = xs:decimal(cbc:TaxInclusiveAmount)))
        
        ..................
        
        (xs:decimal(BT-113) and not(xs:decimal(BT-114)) and (xs:decimal(BT-115) 
        = (round((xs:decimal(BT-112) - xs:decimal(BT-113)) * 10 * 10) div 100))) 
        or (not(xs:decimal(BT-113)) and not(xs:decimal(BT-114)) and xs:decimal(BT-115) 
        = xs:decimal(BT-112)) 
        or (xs:decimal(BT-113) and xs:decimal(BT-114) and ((round((xs:decimal(BT-115) - xs:decimal(BT-114)) * 10 * 10) div 100) 
        = (round((xs:decimal(BT-112) - xs:decimal(BT-113)) * 10 * 10) div 100))) 
        or (not(xs:decimal(BT-113)) and xs:decimal(BT-114) and ((round((xs:decimal(BT-115) - xs:decimal(BT-114)) * 10 * 10) div 100) 
        = xs:decimal(BT-112))) 
      -->      
      <assert test="(xs:decimal(BT-113) and not(xs:decimal(BT-114)) and (xs:decimal(BT-115) 
        = (round((xs:decimal(BT-112) - xs:decimal(BT-113)) * 10 * 10) div 100))) 
        or (not(xs:decimal(BT-113)) and not(xs:decimal(BT-114)) and xs:decimal(BT-115) 
        = xs:decimal(BT-112)) 
        or (xs:decimal(BT-113) and xs:decimal(BT-114) and ((round((xs:decimal(BT-115) - xs:decimal(BT-114)) * 10 * 10) div 100) 
        = (round((xs:decimal(BT-112) - xs:decimal(BT-113)) * 10 * 10) div 100))) 
        or (not(xs:decimal(BT-113)) and xs:decimal(BT-114) and ((round((xs:decimal(BT-115) - xs:decimal(BT-114)) * 10 * 10) div 100) 
        = xs:decimal(BT-112)))" 
       flag="fatal" 
       id="BR-CO-16">[BR-CO-16]-Amount due for payment (BT-115) = Invoice total amount with VAT (BT-112) -Paid amount (BT-113) +Rounding amount (BT-114).
      </assert>
 
      <assert test="(xs:decimal(BT-110) = round(sum(../BG-23/xs:decimal(BT-117))*10*10)div 100)" 
        flag="fatal" 
        id="BR-CO-14">[BR-CO-14]-Invoice total VAT amount (BT-110) = Σ VAT category tax amount (BT-117).
      </assert>

    </rule>
    <rule context="BG-23">
      <assert test="BT-119 or normalize-space(BT-118)='O'" 
        flag="fatal" 
        id="BR-48">[BR-48]-Each VATBReakdown (BG-23) shall have a VAT category rate (BT-119), except if the Invoice is not subject to VAT.
      </assert>
      <assert test="(BT-119=0 and BT-117=0) or 
        ( BT-119!=0 and (xs:decimal(BT-117) = round (xs:decimal(BT-116)*(xs:decimal(BT-119) div 100) *10*10) div 100) )
        or (not (BT-119 and BT-117=0))" 
        flag="fatal" 
        id="BR-CO-17">[BR-CO-17]-VAT category tax amount (BT-117) = VAT category taxable amount (BT-116) x (VAT category rate (BT-119) / 100), rounded to two decimals.
      </assert>
      <assert test=".[normalize-space(BT-118) = 'AE']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'AE']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'AE'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'AE']))
        or normalize-space(BT-118) != 'AE'" 
        flag="fatal" 
        id="BR-AE-08">[BR-AE-08]-In a VATBReakdown (BG-23) where the VAT category code (BT-118) is "Reverse charge" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amounts (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Reverse charge".
      </assert>
      <assert test=".[normalize-space(BT-118) = 'AE']/BT-117=0 or normalize-space(BT-118) != 'AE'" 
        flag="fatal" 
        id="BR-AE-09">[BR-AE-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where the VAT category code (BT-118) is “Reverse charge” shall be 0 (zero).
      </assert>     
      <assert test=".[normalize-space(BT-118) = 'AE']/BT-120 or .[normalize-space(BT-118) = 'AE']/BT-121 or normalize-space(BT-118) != 'AE'" 
        flag="fatal" 
        id="BR-AE-10">[BR-AE-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) "Reverse charge" shall have a VAT exemption reason code (BT-121), meaning "Reverse charge" or the VAT exemption reason text (BT-120) "Reverse charge" (or the equivalent standard text in another language).
      </assert>
      <!--Exempt from VAT (E)-->
      <assert test=".[normalize-space(BT-118) = 'E']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'E']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'E'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'E']))
        or normalize-space(BT-118) != 'E'"        
        flag="fatal" 
        id="BR-E-08">[BR-E-08]-In a VATBReakdown (BG-23) where the VAT category code (BT-118) is "Exempt from VAT" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amounts (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Exempt from VAT".
      </assert>
      <assert test=".[normalize-space(BT-118) = 'E']/BT-117=0 or normalize-space(BT-118) != 'E'" 
        flag="fatal" 
        id="BR-E-09">[BR-E-09]-The VAT category tax amount (BT-117) In a VATBReakdown (BG-23) where the VAT category code (BT-118) equals "Exempt from VAT" shall equal 0 (zero).
      </assert>
      <assert test=".[normalize-space(BT-118) = 'E']/BT-120 or .[normalize-space(BT-118) = 'E']/BT-121 or normalize-space(BT-118) != 'E'" 
        flag="fatal" 
        id="BR-E-10">[BR-E-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) "Exempt from VAT" shall have a VAT exemption reason code (BT-121) or a VAT exemption reason text (BT-120).
      </assert>
      <!-- Export outside the EU (G) -->
      <assert test=".[normalize-space(BT-118) = 'G']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'G']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'G'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'G']))
        or normalize-space(BT-118) != 'G'"
        flag="fatal" 
        id="BR-G-08">[BR-G-08]-In a VATBReakdown (BG-23) where the VAT category code (BT-118) is "Export outside the EU" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amounts (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Export outside the EU".
      </assert>
      <assert test=".[normalize-space(BT-118) = 'G']/BT-117=0 or normalize-space(BT-118) != 'G'" 
        flag="fatal" 
        id="BR-G-09">[BR-G-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where the VAT category code (BT-118) is “Export outside the EU” shall be 0 (zero).
      </assert>
      <assert test=".[normalize-space(BT-118) = 'G']/BT-120 or .[normalize-space(BT-118) = 'G']/BT-121 or normalize-space(BT-118) != 'G'" 
        flag="fatal" 
        id="BR-G-10">[BR-G-10]-A VATBReakdown (BG-23) with the VAT Category code (BT-118) "Export outside the EU" shall have a VAT exemption reason code (BT-121), meaning "Export outside the EU" or the VAT exemption reason text (BT-120) "Export outside the EU" (or the equivalent standard text in another language).
      </assert>
      <!--Intra-community supply (K) -->
      <assert test=".[normalize-space(BT-118) = 'K']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'K']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'K'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'K']))
        or normalize-space(BT-118) != 'K'"        
        flag="fatal" 
        id="BR-IC-08">[BR-IC-08]-In a VATBReakdown (BG-23) where the VAT category code (BT-118) is "Intra-community supply" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amounts (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Intra-community supply".
      </assert>
      <assert test=".[normalize-space(BT-118) = 'K']/BT-117=0 or normalize-space(BT-118) != 'K'" 
        flag="fatal" 
        id="BR-IC-09">[BR-IC-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where the VAT category code (BT-118) is “Intra-community supply” shall be 0 (zero).
      </assert>
      <assert test=".[normalize-space(BT-118) = 'K']/BT-120 or .[normalize-space(BT-118) = 'K']/BT-121 or normalize-space(BT-118) != 'K'" 
        flag="fatal" 
        id="BR-IC-10">[BR-IC-10]-A VATBReakdown (BG-23) with the VAT Category code (BT-118) "Intra-community supply" shall have a VAT exemption reason code (BT-121), meaning "Intra-community supply" or the VAT exemption reason text (BT-120) "Intra-community supply" (or the equivalent standard text in another language).
      </assert>
      <!--IGIC (L)-->
      <assert test=".[normalize-space(BT-118) = 'L']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'L']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'L'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'L']))
        or normalize-space(BT-118) != 'L'"        
        flag="fatal" 
        id="BR-IG-08">[BR-IG-08]-For each different value of VAT category rate (BT-119) where the VAT category code (BT-118) is "IGIC", the VAT category taxable amount (BT-116) in a VATBReakdown (BG-23) shall equal the sum of Invoice line net amounts (BT-131) plus the sum of document level charge amounts (BT-99) minus the sum of document level allowance amounts (BT-92) where the VAT category code (BT-151, BT-102, BT-95) is “IGIC” and the VAT rate (BT-152, BT-103, BT-96) equals the VAT category rate (BT-119).
      </assert>
      <!--    
        <param name="BR-IG-09" value="xs:decimal(../cbc:TaxAmount) = round((xs:decimal(../cbc:TaxableAmount) * (xs:decimal(cbc:Percent) div 100)) * 10 * 10) div 100 "/>
      -->
      <assert test=".[normalize-space(BT-118) = 'L']/xs:decimal(BT-117) = round( (.[normalize-space(BT-118) = 'L'])/xs:decimal(BT-116) * ( .[normalize-space(BT-118) = 'L']/xs:decimal(BT-119)div 100) * 10 * 10  ) div 100
        or normalize-space(BT-118) != 'L'" 
        flag="fatal" 
        id="BR-IG-09">[BR-IG-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where VAT category code (BT-118) is "IGIC" shall equal the VAT category taxable amount (BT-116) multiplied by the VAT category rate (BT-119).
      </assert>
      <assert test="(not(.[normalize-space(BT-118) = 'L']/BT-120) and not( .[normalize-space(BT-118) = 'L']/BT-121)) or normalize-space(BT-118) != 'L'" 
        flag="fatal" 
        id="BR-IG-10">[BR-IG-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) "IGIC" shall not have a VAT exemption reason code (BT-121) or VAT exemption reason text (BT-120).
      </assert>
      <!--IPSI (M)-->
      <assert test=".[normalize-space(BT-118) = 'M']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'M']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'M'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'M']))
        or normalize-space(BT-118) != 'M'"        
        flag="fatal" 
        id="BR-IP-08">[BR-IP-08]-For each different value of VAT category rate (BT-119) where the VAT category code (BT-118) is "IPSI", the VAT category taxable amount (BT-116) in a VATBReakdown (BG-23) shall equal the sum of Invoice line net amounts (BT-131) plus the sum of document level charge amounts (BT-99) minus the sum of document level allowance amounts (BT-92) where the VAT category code (BT-151, BT-102, BT-95) is “IPSI” and the VAT rate (BT-152, BT-103, BT-96) equals the VAT category rate (BT-119).
      </assert>
      <assert test=".[normalize-space(BT-118) = 'M']/xs:decimal(BT-117) = round( (.[normalize-space(BT-118) = 'M'])/xs:decimal(BT-116) * ( .[normalize-space(BT-118) = 'M']/xs:decimal(BT-119)div 100) * 10 * 10  ) div 100
        or normalize-space(BT-118) != 'M'" 
        flag="fatal" 
        id="BR-IP-09">[BR-IP-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where VAT category code (BT-118) is "IPSI" shall equal the VAT category taxable amount (BT-116) multiplied by the VAT category rate (BT-119).
      </assert>
      <assert test="(not(.[normalize-space(BT-118) = 'M']/BT-120) and not( .[normalize-space(BT-118) = 'M']/BT-121)) or normalize-space(BT-118) != 'M'" 
        flag="fatal" 
        id="BR-IP-10">[BR-IP-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) "IPSI" shall not have a VAT exemption reason code (BT-121) or VAT exemption reason text (BT-120).
      </assert>
      <!--Not subject to VAT (O) -->
      <assert test=".[normalize-space(BT-118) = 'O']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'O']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'O'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'O']))
        or normalize-space(BT-118) != 'O'"        
        flag="fatal" 
        id="BR-O-08">[BR-O-08]-In a VATBReakdown (BG-23) where the VAT category code (BT-118) is " Not subject to VAT" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amounts (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Not subject to VAT".
      </assert>
      <assert test=".[normalize-space(BT-118) = 'O']/BT-117=0 or normalize-space(BT-118) != 'O'" 
        flag="fatal" 
        id="BR-O-09">[BR-O-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where the VAT category code (BT-118) is “Not subject to VAT” shall be 0 (zero).
      </assert>
      <assert test=".[normalize-space(BT-118) = 'O']/BT-120 or .[normalize-space(BT-118) = 'O']/BT-121 or normalize-space(BT-118) != 'O'" 
        flag="fatal" 
        id="BR-O-10">[BR-O-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) " Not subject to VAT" shall have a VAT exemption reason code (BT-121), meaning " Not subject to VAT" or a VAT exemption reason text (BT-120) " Not subject to VAT" (or the equivalent standard text in another language).
      </assert>
      <!--Standard rated (S) -->
      
      <!--
        //// MODIFICATA perchè non controllava i diversi rate
        <assert test=".[normalize-space(BT-118) = 'S']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'S']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'S'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'S']))
        or normalize-space(BT-118) != 'S'"        
        flag="fatal" 
        id="BR-S-08">[BR-S-08]-For each different value of VAT category rate (BT-119) where the VAT category code (BT-118) is "Standard rated", the VAT category taxable amount (BT-116) in a VATBReakdown (BG-23) shall equal the sum of Invoice line net amounts (BT-131) plus the sum of document level charge amounts (BT-99) minus the sum of document level allowance amounts (BT-92) where the VAT category code (BT-151, BT-102, BT-95) is “Standard rated” and the VAT rate (BT-152, BT-103, BT-96) equals the VAT category rate (BT-119).
      </assert>-->
      
      <!--      
        <assert test="
        every $rate in BT-119[normalize-space(../BT-118)='S'] satisfies 
        (
        .[normalize-space(BT-118) = 'S']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'S'][../BG-30/BT-152=xs:decimal($rate)]))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'S'][../BT-96=xs:decimal($rate)])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'S'][../BT-103=xs:decimal($rate)]))
        or normalize-space(BT-118) != 'S'
        )"        
        flag="fatal" 
        id="BR-S-08">[BR-S-08]-For each different value of VAT category rate (BT-119) where the VAT category code (BT-118) is "Standard rated", the VAT category taxable amount (BT-116) in a VATBReakdown (BG-23) shall equal the sum of Invoice line net amounts (BT-131) plus the sum of document level charge amounts (BT-99) minus the sum of document level allowance amounts (BT-92) where the VAT category code (BT-151, BT-102, BT-95) is “Standard rated” and the VAT rate (BT-152, BT-103, BT-96) equals the VAT category rate (BT-119).
      </assert>
      -->
      <assert test="
        every $rate in BT-119[normalize-space(../BT-118)=('S','B')], 
        $vatcode in BT-118[normalize-space(.)=('S','B')] satisfies 
        (
        .[normalize-space(BT-118) = $vatcode]/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = $vatcode][../BG-30/BT-152=xs:decimal($rate)]))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = $vatcode][../BT-96=xs:decimal($rate)])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = $vatcode][../BT-103=xs:decimal($rate)]))
        or normalize-space(BT-118) != $vatcode
        )"        
        flag="fatal" 
        id="BR-S-08">[BR-S-08]-For each different value of VAT category rate (BT-119) where the VAT category code (BT-118) is "Standard rated", the VAT category taxable amount (BT-116) in a VATBReakdown (BG-23) shall equal the sum of Invoice line net amounts (BT-131) plus the sum of document level charge amounts (BT-99) minus the sum of document level allowance amounts (BT-92) where the VAT category code (BT-151, BT-102, BT-95) is “Standard rated” and the VAT rate (BT-152, BT-103, BT-96) equals the VAT category rate (BT-119).
      </assert>
      
      <!--<assert test=".[normalize-space(BT-118) = 'S']/xs:decimal(BT-117) = 
        round( (.[normalize-space(BT-118) = 'S']/xs:decimal(BT-116)) * ( .[normalize-space(BT-118) = 'S']/xs:decimal(BT-119)div 100) * 10 * 10  ) div 100
        or normalize-space(BT-118) != 'S'" 
        flag="fatal" 
        id="BR-S-09">[BR-S-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where VAT category code (BT-118) is "Standard rated" shall equal the VAT category taxable amount (BT-116) multiplied by the VAT category rate (BT-119).
      </assert>
      -->
      <assert test=".[normalize-space(BT-118) = ('S', 'B')]/xs:decimal(BT-117) = 
        round( (.[normalize-space(BT-118) = 'S']/xs:decimal(BT-116)) * ( .[normalize-space(BT-118) = 'S']/xs:decimal(BT-119)div 100) * 10 * 10  ) div 100
        or normalize-space(BT-118) != ('S', 'B')" 
        flag="fatal" 
        id="BR-S-09">[BR-S-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where VAT category code (BT-118) is "Standard rated" shall equal the VAT category taxable amount (BT-116) multiplied by the VAT category rate (BT-119).
      </assert>
      <!--
      <assert test="(not(.[normalize-space(BT-118) = 'S']/BT-120) and not( .[normalize-space(BT-118) = 'S']/BT-121)) or normalize-space(BT-118) != 'S'" 
        flag="fatal" 
        id="BR-S-10">[BR-S-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) "Standard rate" shall not have a VAT exemption reason code (BT-121) or VAT exemption reason text (BT-120).
      </assert>
      -->
      <assert test="(not(.[normalize-space(BT-118) = ('S','B')]/BT-120) and not( .[normalize-space(BT-118) = ('S' , 'B')]/BT-121)) or not(normalize-space(BT-118) = ('S' , 'B'))" 
        flag="fatal" 
        id="BR-S-10">[BR-S-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) "Standard rate" shall not have a VAT exemption reason code (BT-121) or VAT exemption reason text (BT-120).
      </assert>
      <!--Zero rated (Z) -->
      <assert test=".[normalize-space(BT-118) = 'Z']/xs:decimal(BT-116)=
        sum(../BG-25/xs:decimal(BT-131[normalize-space(../BG-30/BT-151) = 'Z']))
        - sum(../BG-20/xs:decimal(BT-92[normalize-space(../BT-95) = 'Z'])) 
        + sum(../BG-21/xs:decimal(BT-99[normalize-space(../BT-102) = 'Z']))
        or normalize-space(BT-118) != 'Z'"        
        flag="fatal" 
        id="BR-Z-08">[BR-Z-08]-In a VATBReakdown (BG-23) where VAT category code (BT-118) is "Zero rated" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amount (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Zero rated".
      </assert>
      <assert test=".[normalize-space(BT-118) = 'Z']/BT-117=0 or normalize-space(BT-118) != 'Z'" 
        flag="fatal" 
        id="BR-Z-09">[BR-Z-09]-The VAT category tax amount (BT-117) in a VATBReakdown (BG-23) where VAT category code (BT-118) is "Zero rated" shall equal 0 (zero).
      </assert>
      <assert test="(not(.[normalize-space(BT-118) = 'Z']/BT-120) and not( .[normalize-space(BT-118) = 'Z']/BT-121)) or normalize-space(BT-118) != 'Z'" 
        flag="fatal" 
        id="BR-Z-10">[BR-Z-10]-A VATBReakdown (BG-23) with VAT Category code (BT-118) "Zero rated" shall not have a VAT exemption reason code (BT-121) or VAT exemption reason text (BT-120).
      </assert>      
            
    </rule>   
    <rule context="BG-24">
      <assert test="BT-122" 
        flag="fatal" 
        id="BR-52">[BR-52]-Each Additional supporting document (BG-24) shall contain a Supporting document reference (BT-122).    
      </assert>
    </rule>	     
    <rule context="BG-25">

    <!-- BR-27 e BR-28 potrebbero essere imposti nello schema - da valutare    -->
    <assert test="BG-29/BT-146 &gt;= 0" 
      flag="fatal" 
      id="BR-27">[BR-27]-The Item net price (BT-146) shall NOT be negative.
    </assert>
    <assert test="not(BT-148) or BG-29/BT-148 &gt;= 0" 
      flag="fatal" 
      id="BR-28">[BR-28]-The Item gross price (BT-148) shall NOT be negative.
    </assert>
      
    </rule>  
      
      
      
    <rule context="BG-25/BG-30">    
<!--    <assert test="BG-30/BT-151" 
     flag="fatal" 
     id="BR-CO-04">[BR-CO-04]-Each Invoice line (BG-25) shall be categorized with an Invoiced item VAT category code (BT-151).
    </assert> -->    
      <assert test="BT-151" 
        flag="fatal" 
        id="BR-CO-04">[BR-CO-04]-Each Invoice line (BG-25) shall be categorized with an Invoiced item VAT category code (BT-151).
      </assert> 
      <!--Reverse charge (AE)-->
      <assert test=".[normalize-space(BT-151) = 'AE']/BT-152=0 or normalize-space(BT-151) != 'AE'" 
        flag="fatal" 
        id="BR-AE-05">[BR-AE-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "Reverse charge" the Invoiced item VAT rate (BT-152) shall be 0 (zero).</assert>
      <!--Exempt from VAT (E)-->
      <assert test=".[normalize-space(BT-151) = 'E']/BT-152=0 or normalize-space(BT-151) != 'E'" 
        flag="fatal" 
        id="BR-E-05">[BR-E-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "Exempt from VAT", the Invoiced item VAT rate (BT-152) shall be 0 (zero).
      </assert>
      <!-- Export outside the EU (G) -->      
      <assert test=".[normalize-space(BT-151) = 'G']/BT-152=0 or normalize-space(BT-151) != 'G'" 
        flag="fatal" 
        id="BR-G-05">[BR-G-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "Export outside the EU" the Invoiced item VAT rate (BT-152) shall be 0 (zero).
      </assert>
      <!--Intra-community supply (K) -->      
      <assert test=".[normalize-space(BT-151) = 'K']/BT-152=0 or normalize-space(BT-151) != 'K'" 
        flag="fatal" 
        id="BR-IC-05">[BR-IC-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "Intracommunity supply" the Invoiced item VAT rate (BT-152) shall be 0 (zero).
      </assert>
      <!--IGIC (L)-->      
      <assert test=".[normalize-space(BT-151) = 'L']/BT-152 &gt;= 0 or normalize-space(BT-151) != 'L'" 
        flag="fatal" 
        id="BR-IG-05">[BR-IG-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "IGIC" the invoiced item VAT rate (BT-152) shall be 0 (zero) or greater than zero.
      </assert>
      <!--IPSI (M)-->      
      <assert test=".[normalize-space(BT-151) = 'M']/BT-152 &gt;= 0 or normalize-space(BT-151) != 'M'" 
        flag="fatal" 
        id="BR-IP-05">[BR-IP-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "IPSI" the Invoiced item VAT rate (BT-152) shall be 0 (zero) or greater than zero.
      </assert>
      <!--Not subject to VAT (O) -->      
      <assert test="not(.[normalize-space(BT-151) = 'O']/BT-152) or normalize-space(BT-151) != 'O'" 
        flag="fatal" 
        id="BR-O-05">[BR-O-05]-An Invoice line (BG-25) where the VAT category code (BT-151) is "Not subject to VAT" shall not contain an Invoiced item VAT rate (BT-152).
      </assert>
      <!--Standard rated (S) -->      
      <!--<assert test=".[normalize-space(BT-151) = 'S']/BT-152 &gt; 0  or normalize-space(BT-151) != 'S'" 
        flag="fatal" 
        id="BR-S-05">[BR-S-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "Standard rated" the Invoiced item VAT rate (BT-152) shall be greater than zero.
      </assert>
      -->
      <assert test=".[normalize-space(BT-151) = ('S' , 'B')]/BT-152 &gt; 0  or not(normalize-space(BT-151) = ('S' , 'B'))" 
        flag="fatal" 
        id="BR-S-05">[BR-S-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "Standard rated" the Invoiced item VAT rate (BT-152) shall be greater than zero.
      </assert>
      <!--Zero rated (Z) -->      
      <assert test=".[normalize-space(BT-151) = 'Z']/BT-152 = 0 or normalize-space(BT-151) != 'Z'" 
        flag="fatal" 
        id="BR-Z-05">[BR-Z-05]-In an Invoice line (BG-25) where the Invoiced item VAT category code (BT-151) is "Zero rated" the Invoiced item VAT rate (BT-152) shall be 0 (zero).
      </assert>
      
    </rule>        
    <rule context="BG-25/BG-27">
      <assert test="BT-139 or BT-140" 
        flag="fatal" 
        id="BR-42">[BR-42]-Each Invoice line allowance (BG-27) shall have an Invoice line allowance reason (BT-139) or an Invoice line allowance reason code (BT-140).
      </assert>
      <assert test="true()" 
        flag="fatal" 
        id="BR-CO-07">[BR-CO-07]-Invoice line allowance reason code (BT-140) and Invoice line allowance reason (BT-139) shall indicate the same type of allowance reason.</assert>
      <assert test="BT-139 or BT-140" 
        flag="fatal" 
        id="BR-CO-23">[BR-CO-23]-Each Invoice line allowance (BG-27) shall contain an Invoice line allowance reason (BT-139) or an Invoice line allowance reason code (BT-140), or both.
      </assert>   
    </rule>
    <rule context="BG-25/BG-28">
      <!--  
      Rivedere messaggio originale CEN
      dovrebbe essere
              id="BR-44">[BR-44]-Each Invoice line charge shall have an Invoice line charge reason (BT-144) or an invoice line charge reason code (BT-145). 
      invece di
          <assert test="$BR-44" flag="fatal" id="BR-44">[BR-44]-Each Invoice line charge shall have an Invoice line charge reason or an invoice line allowance reason code. </assert>
      -->
      <assert test="BT-144 or BT-145" 
        flag="fatal" 
        id="BR-44">[BR-44]-Each Invoice line charge shall have an Invoice line charge reason (BT-144) or an invoice line charge reason code (BT-145). 
      </assert>
      <assert test="true()" 
        flag="fatal" 
        id="BR-CO-08">[BR-CO-08]-Invoice line charge reason code (BT-145) and Invoice line charge reason (BT144) shall indicate the same type of charge reason.</assert>
      <assert test="BT-144 or BT-145" 
        flag="fatal" 
        id="BR-CO-24">[BR-CO-24]-Each Invoice line charge (BG-28) shall contain an Invoice line charge reason (BT-144) or an Invoice line charge reason code (BT-145), or both.
      </assert>
    </rule>
    <rule context="BG-25/BG-26">
      <assert test="(BT-135 and BT-134 and BT-135 &gt;= BT-134) or not (BT-134) or not(BT-135)" 
        flag="fatal" 
        id="BR-30">[BR-30]-If both Invoice line period start date (BT-134) and Invoice line period end date (BT-135) are given then the Invoice line period end date (BT-135) shall be later or equal to the Invoice line period start date (BT-134).
      </assert>
      <assert test="BT-134 or BT-135" 
        flag="fatal" 
        id="BR-CO-20">[BR-CO-20]-If Invoice line period (BG-26) is used, the Invoice line period start date (BT-134) or the Invoice line period end date (BT-135) shall be filled, or both.
      </assert>
    </rule>
    <rule context="BT-115">
      <assert test=". &lt;= 0 or ( ../../BT-9 or ../../BT-20)" 
        flag="fatal" 
        id="BR-CO-25">[BR-CO-25]-In case the Amount due for payment (BT-115) is positive, either the Payment due date (BT-9) or the Payment terms (BT-20) shall be present.
      </assert>
    </rule>
    <rule context="BT-49">
      <assert test="./@scheme" 
        flag="fatal" 
        id="BR-63">[BR-63]-The Buyer electronic address (BT-49) shall have a Scheme identifier.
      </assert>
    </rule>

  </pattern>

  <pattern id="CENCodeList">
    <!-- 
      Perchè mette l'attributo flag nel tag Rule ...?
      
    -->

    <rule context="BT-3">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' 80 81 82 83 84 130 202 203 204 211 261 262 295 296 308 325 326 380 381 383 384 385 386 387 388 389 390 393 394 395 396 420 456 457 458 527 532 575 623 633 751 780 935  ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-01" 
        flag="fatal">[BR-CL-01]-The document type code MUST be coded by the invoice and credit note related code lists of UNTDID 1001.
      </assert>      
    </rule>
    
    <!--  
      IN CEN non serve - si valorizza solo BT-5
      
      <rule context="cbc:Amount | cbc:BaseAmount | cbc:PriceAmount | cbc:TaxAmount | cbc:TaxableAmount | cbc:LineExtensionAmount | cbc:TaxExclusiveAmount | cbc:TaxInclusiveAmount | cbc:AllowanceTotalAmount | cbc:ChargeTotalAmount | cbc:PrepaidAmount | cbc:PayableRoundingAmount | cbc:PayableAmount" flag="fatal">
      <assert 
        test="((not(contains(normalize-space(@currencyID), ' ')) and contains(' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUC CUP CVE CZK DJF DKK DOP DZD EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SLL SOS SRD SSP STD SVC SYP SZL THB TJS TMT TND TOP TRY TTD TWD TZS UAH UGX USD USN UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XOF XPD XPF XPT XSU XTS XUA XXX YER ZAR ZMW ZWL ', concat(' ', normalize-space(@currencyID), ' '))))" 
        id="BR-CL-03"
        flag="fatal">[BR-CL-03]-currencyID MUST be coded using ISO code list 4217 alpha-3</assert>
    </rule>-->
    
    <rule context="BT-5" flag="fatal">
      <assert 
        test="((not(contains(normalize-space(.), ' ')) and contains(' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUC CUP CVE CZK DJF DKK DOP DZD EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SLL SOS SRD SSP STD SVC SYP SZL THB TJS TMT TND TOP TRY TTD TWD TZS UAH UGX USD USN UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XOF XPD XPF XPT XSU XTS XUA XXX YER ZAR ZMW ZWL ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-04" 
        flag="fatal">[BR-CL-04]-Invoice currency code MUST be coded using ISO code list 4217 alpha-3</assert>
    </rule>
    
    <rule context="BT-6" flag="fatal">
      <assert 
        test="((not(contains(normalize-space(.), ' ')) and contains(' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUC CUP CVE CZK DJF DKK DOP DZD EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SLL SOS SRD SSP STD SVC SYP SZL THB TJS TMT TND TOP TRY TTD TWD TZS UAH UGX USD USN UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XOF XPD XPF XPT XSU XTS XUA XXX YER ZAR ZMW ZWL ', concat(' ', normalize-space(.), ' '))))"  
        id="BR-CL-05" 
        flag="fatal">[BR-CL-05]-Tax currency code MUST be coded using ISO code list 4217 alpha-3</assert>
    </rule>
    
    <rule context="BT-8" flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' 3 35 432 ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-06"
        flag="fatal">[BR-CL-06]-Value added tax point date code MUST be coded using a restriction of UNTDID 2005 iterm.
      </assert>
    </rule>    
    
    <rule context="BT-18-1 | BT-128-1" flag="fatal">
      <assert
        test="((not(contains(normalize-space(@schemeID), ' ')) and contains(' AAA AAB AAC AAD AAE AAF AAG AAH AAI AAJ AAK AAL AAM AAN AAO AAP AAQ AAR AAS AAT AAU AAV AAW AAX AAY AAZ ABA ABB ABC ABD ABE ABF ABG ABH ABI ABJ ABK ABL ABM ABN ABO ABP ABQ ABR ABS ABT ABU ABV ABW ABX ABY ABZ AC ACA ACB ACC ACD ACE ACF ACG ACH ACI ACJ ACK ACL ACN ACO ACP ACQ ACR ACT ACU ACV ACW ACX ACY ACZ ADA ADB ADC ADD ADE ADF ADG ADI ADJ ADK ADL ADM ADN ADO ADP ADQ ADT ADU ADV ADW ADX ADY ADZ AE AEA AEB AEC AED AEE AEF AEG AEH AEI AEJ AEK AEL AEM AEN AEO AEP AEQ AER AES AET AEU AEV AEW AEX AEY AEZ AF AFA AFB AFC AFD AFE AFF AFG AFH AFI AFJ AFK AFL AFM AFN AFO AFP AFQ AFR AFS AFT AFU AFV AFW AFX AFY AFZ AGA AGB AGC AGD AGE AGF AGG AGH AGI AGJ AGK AGL AGM AGN AGO AGP AGQ AGR AGS AGT AGU AGV AGW AGX AGY AGZ AHA AHB AHC AHD AHE AHF AHG AHH AHI AHJ AHK AHL AHM AHN AHO AHP AHQ AHR AHS AHT AHU AHV AHX AHY AHZ AIA AIB AIC AID AIE AIF AIG AIH AII AIJ AIK AIL AIM AIN AIO AIP AIQ AIR AIS AIT AIU AIV AIW AIX AIY AIZ AJA AJB AJC AJD AJE AJF AJG AJH AJI AJJ AJK AJL AJM AJN AJO AJP AJQ AJR AJS AJT AJU AJV AJW AJX AJY AJZ AKA AKB AKC AKD AKE AKF AKG AKH AKI AKJ AKK AKL AKM AKN AKO AKP AKQ AKR AKS AKT AKU AKV AKW AKX AKY AKZ ALA ALB ALC ALD ALE ALF ALG ALH ALI ALJ ALK ALL ALM ALN ALO ALP ALQ ALR ALS ALT ALU ALV ALW ALX ALY ALZ AMA AMB AMC AMD AME AMF AMG AMH AMI AMJ AMK AML AMM AMN AMO AMP AMQ AMR AMS AMT AMU AMV AMW AMX AMY AMZ ANA ANB ANC AND ANE ANF ANG ANH ANI ANJ ANK ANL ANM ANN ANO ANP ANQ ANR ANS ANT ANU ANV ANW ANX ANY AOA AOD AOE AOF AOG AOH AOI AOJ AOK AOL AOM AON AOO AOP AOQ AOR AOS AOT AOU AOV AOW AOX AOY AOZ AP APA APB APC APD APE APF APG APH API APJ APK APL APM APN APO APP APQ APR APS APT APU APV APW APX APY APZ AQA AQB AQC AQD AQE AQF AQG AQH AQI AQJ AQK AQL AQM AQN AQO AQP AQQ AQR AQS AQT AQU AQV AQW AQX AQY AQZ ARA ARB ARC ARD ARE ARF ARG ARH ARI ARJ ARK ARL ARM ARN ARO ARP ARQ ARR ARS ART ARU ARV ARW ARX ARY ARZ ASA ASB ASC ASD ASE ASF ASG ASH ASI ASJ ASK ASL ASM ASN ASO ASP ASQ ASR ASS AST ASU ASV ASW ASX ASY ASZ ATA ATB ATC ATD ATE ATF ATG ATH ATI ATJ ATK ATL ATM ATN ATO ATP ATQ ATR ATS ATT ATU ATV ATW ATX ATY ATZ AU AUA AUB AUC AUD AUE AUF AUG AUH AUI AUJ AUK AUL AUM AUN AUO AUP AUQ AUR AUS AUT AUU AUV AUW AUX AUY AUZ AV AVA AVB AVC AVD AVE AVF AVG AVH AVI AVJ AVK AVL AVM AVN AVO AVP AVQ AVR AVS AVT AVU AVV AVW AVX AVY AVZ AWA AWB AWC AWD AWE AWF AWG AWH AWI AWJ AWK AWL AWM AWN AWO AWP AWQ AWR AWS AWT AWU AWV AWW AWX AWY AWZ AXA AXB AXC AXD AXE AXF AXG AXH AXI AXJ AXK AXL AXM AXN AXO AXP AXQ AXR BA BC BD BE BH BM BN BO BR BT BW CAS CAT CAU CAV CAW CAX CAY CAZ CBA CBB CD CEC CED CFE CFF CFO CG CH CK CKN CM CMR CN CNO COF CP CR CRN CS CST CT CU CV CW CZ DA DAN DB DI DL DM DQ DR EA EB ED EE EI EN EQ ER ERN ET EX FC FF FI FLW FN FO FS FT FV FX GA GC GD GDN GN HS HWB IA IB ICA ICE ICO II IL INB INN INO IP IS IT IV JB JE LA LAN LAR LB LC LI LO LRC LS MA MB MF MG MH MR MRN MS MSS MWB NA NF OH OI ON OP OR PB PC PD PE PF PI PK PL POR PP PQ PR PS PW PY RA RC RCN RE REN RF RR RT SA SB SD SE SEA SF SH SI SM SN SP SQ SRN SS STA SW SZ TB TCR TE TF TI TIN TL TN TP UAR UC UCN UN UO URI VA VC VGR VM VN VON VOR VP VR VS VT VV WE WM WN WR WS WY XA XC XP ZZZ ', concat(' ', normalize-space(@schemeID), ' '))))" 
        id="BR-CL-07"
        flag="fatal">[BR-CL-07]-Object identifier identification scheme identifier MUST be coded using a restriction of UNTDID 1153.</assert>
    </rule>    
    <!--
    cac:PartyIdentification/cbc:ID/@schemeID
    - BT-29-1
    - BT-46-1
    - BT-60-1

    in UBL su stessa sintassi si ha anche il BT-90 con SEPA e quindi regola più completa
    -->
    <rule context="BT-29-1 | BT-46-1 | BT-60-1" flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025 0026 0027 0028 0029 0030 0031 0032 0033 0034 0035 0036 0037 0038 0039 0040 0041 0042 0043 0044 0045 0046 0047 0048 0049 0050 0051 0052 0053 0054 0055 0056 0057 0058 0059 0060 0061 0062 0063 0064 0065 0066 0067 0068 0069 0070 0071 0072 0073 0074 0075 0076 0077 0078 0079 0080 0081 0082 0083 0084 0085 0086 0087 0088 0089 0090 0091 0092 0093 0094 0095 0096 0097 0098 0099 0100 0101 0102 0103 0104 0105 0106 0107 0108 0109 0110 0111 0112 0113 0114 0115 0116 0117 0118 0119 0120 0121 0122 0123 0124 0125 0126 0127 0128 0129 0130 0131 0132 0133 0134 0135 0136 0137 0138 0139 0140 0141 0142 0143 0144 0145 0146 0147 0148 0149 0150 0151 0152 0153 0154 0155 0156 0157 0158 0159 0160 0161 0162 0163 0164 0165 0166 0167 0168 0169 0170 0171 0172 0173 0174 0175 0176 0177 0178 0179 0180 0183 0184 0190 0191 0192 0193', concat(' ', normalize-space(.), ' ')))) " 
        id="BR-CL-10"
        flag="fatal">[BR-CL-10]-Any identifier identification scheme identifier MUST be coded using one of the ISO 6523 ICD list.</assert>
    </rule>
    <!--
      cac:PartyLegalEntity/cbc:CompanyID/@schemeID
    
      BT-30-1
      BT-47-1
      BT-61-1     
    -->
    <rule context="BT-30-1 | BT-47-1 | BT-61-1 " flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025 0026 0027 0028 0029 0030 0031 0032 0033 0034 0035 0036 0037 0038 0039 0040 0041 0042 0043 0044 0045 0046 0047 0048 0049 0050 0051 0052 0053 0054 0055 0056 0057 0058 0059 0060 0061 0062 0063 0064 0065 0066 0067 0068 0069 0070 0071 0072 0073 0074 0075 0076 0077 0078 0079 0080 0081 0082 0083 0084 0085 0086 0087 0088 0089 0090 0091 0092 0093 0094 0095 0096 0097 0098 0099 0100 0101 0102 0103 0104 0105 0106 0107 0108 0109 0110 0111 0112 0113 0114 0115 0116 0117 0118 0119 0120 0121 0122 0123 0124 0125 0126 0127 0128 0129 0130 0131 0132 0133 0134 0135 0136 0137 0138 0139 0140 0141 0142 0143 0144 0145 0146 0147 0148 0149 0150 0151 0152 0153 0154 0155 0156 0157 0158 0159 0160 0161 0162 0163 0164 0165 0166 0167 0168 0169 0170 0171 0172 0173 0174 0175 0176 0177 0178 0179 0180 0183 0184 0190 0191 0192 0193', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-11"
        flag="fatal">[BR-CL-11]-Any registration identifier identification scheme identifier MUST be coded using one of the ISO 6523 ICD list.</assert>
    </rule>

    <rule context="BT-158-1" flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' AA AB AC AD AE AF AG AH AI AJ AK AL AM AN AO AP AQ AR AS AT AU AV AW AX AY AZ BA BB BC BD BE BF BG BH BI BJ BK BL BM BN BO BP BQ BR BS BT BU BV BW BX BY BZ CC CG CL CR CV DR DW EC EF EN FS GB GN GS HS IB IN IS IT IZ MA MF MN MP NB ON PD PL PO PV QS RC RN RU RY SA SG SK SN SRS SRT SRU SRV SRW SRX SRY SRZ SS SSA SSB SSC SSD SSE SSF SSG SSH SSI SSJ SSK SSL SSM SSN SSO SSP SSQ SSR SSS SST SSU SSV SSW SSX SSY SSZ ST STA STB STC STD STE STF STG STH STI STJ STK STL STM STN STO STP STQ STR STS STT STU STV STW STX STY STZ SUA SUB SUC SUD SUE SUF SUG SUH SUI SUJ SUK SUL SUM TG TSN TSO TSP UA UP VN VP VS VX ZZZ ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-13"
        flag="fatal">[BR-CL-13]-Item classification identifier identification scheme identifier MUST be
        coded using one of the UNTDID 7143 list.</assert>
    </rule>
    
    <!--
      cac:Country/cbc:IdentificationCode
      BT-40
      BT-55
      BT-69
      BT-80
    -->
    <rule context="BT-40 | BT-55 | BT-69 | BT-80" flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-14"
        flag="fatal">[BR-CL-14]-Country codes in an invoice MUST be coded using ISO code list 3166-1</assert>
    </rule>

    <rule context="BT-159" flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-15"
        flag="fatal">[BR-CL-15]-Country codes in an invoice MUST be coded using ISO code list 3166-1</assert>
    </rule>

    <rule context="BT-81" flag="fatal">
      <assert
        test="( ( not(contains(normalize-space(.),' ')) and contains( ' 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63 64 65 66 67 68 70 74 75 76 77 78 91 92 93 94 95 96 97 ZZZ ',concat(' ',normalize-space(.),' ') ) ) )" 
        id="BR-CL-16" 
        flag="fatal">[BR-CL-16]-Payment means in an invoice MUST be coded using UNCL4461 code list</assert>
    </rule>

    <!--
      cac:TaxCategory/cbc:ID
      BT-95
      BT-102
      BT-118
      
      cac:ClassifiedTaxCategory/cbc:ID
      BT-151
 
      Questa rule viene inclusa nella BR-17
   <rule context="cac:ClassifiedTaxCategory/cbc:ID" flag="fatal">
    <assert
      test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE L M E S Z G O K ',concat(' ',normalize-space(.),' ') ) ) )" 
      id="BR-CL-18" 
      flag="fatal">[BR-CL-18]-Invoice tax categories MUST be coded using UNCL5305 code list</assert>
  </rule>
 
    -->
    <rule context="BT-95 | BT-102 | BT-118 | BT-151" flag="fatal">
      <assert
        test="( ( not(contains(normalize-space(.),' ')) and contains( ' AE L M E S Z G O K ',concat(' ',normalize-space(.),' ') ) ) )" 
        id="BR-CL-17" 
        flag="fatal">[BR-CL-17]-Invoice tax categories MUST be coded using UNCL5305 code list</assert>
    </rule>
    
    <!--
    cac:AllowanceCharge/cbc:AllowanceChargeReasonCode cbc:ChargeIndicator = 'false'
    BT-98
    BT-140   
    -->
    <rule
      context="BT-98 | BT-140"
      flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90 91 92 93 94 95 96 97 98 99 100 101 102 103 104 ZZZ ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-19"
        flag="fatal">[BR-CL-19]-Coded allowance reasons MUST belong to the UNCL 5189 code list</assert>
    </rule>

    <!--
    cac:AllowanceCharge/cbc:AllowanceChargeReasonCode cbc:ChargeIndicator = 'true'
    BT-105
    BT-145    
    -->
    <rule
      context="BT-105 | BT-145"
      flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' AA AAA AAC AAD AAE AAF AAH AAI AAS AAT AAV AAY AAZ ABA ABB ABC ABD ABF ABK ABL ABN ABR ABS ABT ABU ACF ACG ACH ACI ACJ ACK ACL ACM ACS ADC ADE ADJ ADK ADL ADM ADN ADO ADP ADQ ADR ADT ADW ADY ADZ AEA AEB AEC AED AEF AEH AEI AEJ AEK AEL AEM AEN AEO AEP AES AET AEU AEV AEW AEX AEY AEZ AJ AU CA CAB CAD CAE CAF CAI CAJ CAK CAL CAM CAN CAO CAP CAQ CAR CAS CAT CAU CAV CAW CD CG CS CT DAB DAD DL EG EP ER FAA FAB FAC FC FH FI GAA HAA HD HH IAA IAB ID IF IR IS KO L1 LA LAA LAB LF MAE MI ML NAA OA PA PAA PC PL RAB RAC RAD RAF RE RF RH RV SA SAA SAD SAE SAI SG SH SM SU TAB TAC TT TV V1 V2 WH XAA YY ZZZ ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-20"
        flag="fatal">[BR-CL-20]-Coded charge reasons MUST belong to the UNCL 7161 code list</assert>
    </rule>

    <rule context="BT-157-1" flag="fatal">
      <assert      
        test="((not(contains(normalize-space(.), ' ')) and contains(' 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025 0026 0027 0028 0029 0030 0031 0032 0033 0034 0035 0036 0037 0038 0039 0040 0041 0042 0043 0044 0045 0046 0047 0048 0049 0050 0051 0052 0053 0054 0055 0056 0057 0058 0059 0060 0061 0062 0063 0064 0065 0066 0067 0068 0069 0070 0071 0072 0073 0074 0075 0076 0077 0078 0079 0080 0081 0082 0083 0084 0085 0086 0087 0088 0089 0090 0091 0092 0093 0094 0095 0096 0097 0098 0099 0100 0101 0102 0103 0104 0105 0106 0107 0108 0109 0110 0111 0112 0113 0114 0115 0116 0117 0118 0119 0120 0121 0122 0123 0124 0125 0126 0127 0128 0129 0130 0131 0132 0133 0134 0135 0136 0137 0138 0139 0140 0141 0142 0143 0144 0145 0146 0147 0148 0149 0150 0151 0152 0153 0154 0155 0156 0157 0158 0159 0160 0161 0162 0163 0164 0165 0166 0167 0168 0169 0170 0171 0172 0173 0174 0175 0176 0177 0178 0179 0180 0183 0184 0190 0191 0192 0193', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-21"
        flag="fatal">[BR-CL-21]-Item standard identifier scheme identifier MUST belong to the ISO 6523 ICD code list</assert>
    </rule>

    <rule context="BT-130 | BT-150" flag="fatal">
      <assert
        test="((not(contains(normalize-space(.), ' ')) and contains(' 10 11 13 14 15 20 21 22 23 24 25 27 28 33 34 35 37 38 40 41 56 57 58 59 60 61 64 66 74 76 77 78 80 81 84 85 87 89 91 1I 2A 2B 2C 2G 2H 2I 2J 2K 2L 2M 2N 2P 2Q 2R 2U 2X 2Y 2Z 3B 3C 4C 4G 4H 4K 4L 4M 4N 4O 4P 4Q 4R 4T 4U 4W 4X 5A 5B 5E 5J A1 A10 A11 A12 A13 A14 A15 A16 A17 A18 A19 A2 A20 A21 A22 A23 A24 A25 A26 A27 A28 A29 A3 A30 A31 A32 A33 A34 A35 A36 A37 A38 A39 A4 A40 A41 A42 A43 A44 A45 A47 A48 A49 A5 A50 A51 A52 A53 A54 A55 A56 A57 A58 A59 A6 A60 A61 A62 A63 A64 A65 A66 A67 A68 A69 A7 A70 A71 A73 A74 A75 A76 A77 A78 A79 A8 A80 A81 A82 A83 A84 A85 A86 A87 A88 A89 A9 A90 A91 A93 A94 A95 A96 A97 A98 A99 AA AB ACR ACT AD AE AH AI AK AL AMH AMP ANN APZ AQ ARE AS ASM ASU ATM ATT AY AZ B1 B10 B11 B12 B13 B14 B15 B16 B17 B18 B19 B20 B21 B22 B23 B24 B25 B26 B27 B28 B29 B3 B30 B31 B32 B33 B34 B35 B36 B37 B38 B39 B4 B40 B41 B42 B43 B44 B45 B46 B47 B48 B49 B50 B51 B52 B53 B54 B55 B56 B57 B58 B59 B60 B61 B62 B63 B64 B65 B66 B67 B68 B69 B7 B70 B71 B72 B73 B74 B75 B76 B77 B78 B79 B8 B80 B81 B82 B83 B84 B85 B86 B87 B88 B89 B90 B91 B92 B93 B94 B95 B96 B97 B98 B99 BAR BB BFT BHP BIL BLD BLL BP BQL BTU BUA BUI C0 C10 C11 C12 C13 C14 C15 C16 C17 C18 C19 C20 C21 C22 C23 C24 C25 C26 C27 C28 C29 C3 C30 C31 C32 C33 C34 C35 C36 C37 C38 C39 C40 C41 C42 C43 C44 C45 C46 C47 C48 C49 C50 C51 C52 C53 C54 C55 C56 C57 C58 C59 C60 C61 C62 C63 C64 C65 C66 C67 C68 C69 C7 C70 C71 C72 C73 C74 C75 C76 C78 C79 C8 C80 C81 C82 C83 C84 C85 C86 C87 C88 C89 C9 C90 C91 C92 C93 C94 C95 C96 C97 C99 CCT CDL CEL CEN CG CGM CKG CLF CLT CMK CMQ CMT CNP CNT COU CTG CTM CTN CUR CWA CWI D03 D04 D1 D10 D11 D12 D13 D15 D16 D17 D18 D19 D2 D20 D21 D22 D23 D24 D25 D26 D27 D29 D30 D31 D32 D33 D34 D35 D36 D37 D38 D39 D41 D42 D43 D44 D45 D46 D47 D48 D49 D5 D50 D51 D52 D53 D54 D55 D56 D57 D58 D59 D6 D60 D61 D62 D63 D65 D68 D69 D70 D71 D72 D73 D74 D75 D76 D77 D78 D80 D81 D82 D83 D85 D86 D87 D88 D89 D9 D91 D93 D94 D95 DAA DAD DAY DB DD DEC DG DJ DLT DMA DMK DMO DMQ DMT DN DPC DPR DPT DRA DRI DRL DT DTN DU DWT DX DZN DZP E01 E07 E08 E09 E10 E11 E12 E14 E15 E16 E17 E18 E19 E20 E21 E22 E23 E25 E27 E28 E30 E31 E32 E33 E34 E35 E36 E37 E38 E39 E4 E40 E41 E42 E43 E44 E45 E46 E47 E48 E49 E50 E51 E52 E53 E54 E55 E56 E57 E58 E59 E60 E61 E62 E63 E64 E65 E66 E67 E68 E69 E70 E71 E72 E73 E74 E75 E76 E77 E78 E79 E80 E81 E82 E83 E84 E85 E86 E87 E88 E89 E90 E91 E92 E93 E94 E95 E96 E97 E98 E99 EA EB EQ F01 F02 F03 F04 F05 F06 F07 F08 F10 F11 F12 F13 F14 F15 F16 F17 F18 F19 F20 F21 F22 F23 F24 F25 F26 F27 F28 F29 F30 F31 F32 F33 F34 F35 F36 F37 F38 F39 F40 F41 F42 F43 F44 F45 F46 F47 F48 F49 F50 F51 F52 F53 F54 F55 F56 F57 F58 F59 F60 F61 F62 F63 F64 F65 F66 F67 F68 F69 F70 F71 F72 F73 F74 F75 F76 F77 F78 F79 F80 F81 F82 F83 F84 F85 F86 F87 F88 F89 F90 F91 F92 F93 F94 F95 F96 F97 F98 F99 FAH FAR FBM FC FF FH FIT FL FOT FP FR FS FTK FTQ G01 G04 G05 G06 G08 G09 G10 G11 G12 G13 G14 G15 G16 G17 G18 G19 G2 G20 G21 G23 G24 G25 G26 G27 G28 G29 G3 G30 G31 G32 G33 G34 G35 G36 G37 G38 G39 G40 G41 G42 G43 G44 G45 G46 G47 G48 G49 G50 G51 G52 G53 G54 G55 G56 G57 G58 G59 G60 G61 G62 G63 G64 G65 G66 G67 G68 G69 G70 G71 G72 G73 G74 G75 G76 G77 G78 G79 G80 G81 G82 G83 G84 G85 G86 G87 G88 G89 G90 G91 G92 G93 G94 G95 G96 G97 G98 G99 GB GBQ GDW GE GF GFI GGR GIA GIC GII GIP GJ GL GLD GLI GLL GM GO GP GQ GRM GRN GRO GRT GT GV GWH H03 H04 H05 H06 H07 H08 H09 H10 H11 H12 H13 H14 H15 H16 H18 H19 H20 H21 H22 H23 H24 H25 H26 H27 H28 H29 H30 H31 H32 H33 H34 H35 H36 H37 H38 H39 H40 H41 H42 H43 H44 H45 H46 H47 H48 H49 H50 H51 H52 H53 H54 H55 H56 H57 H58 H59 H60 H61 H62 H63 H64 H65 H66 H67 H68 H69 H70 H71 H72 H73 H74 H75 H76 H77 H78 H79 H80 H81 H82 H83 H84 H85 H87 H88 H89 H90 H91 H92 H93 H94 H95 H96 H98 H99 HA HAR HBA HBX HC HDW HEA HGM HH HIU HJ HKM HLT HM HMQ HMT HN HP HPA HTZ HUR IA IE INH INK INQ ISD IU IV J10 J12 J13 J14 J15 J16 J17 J18 J19 J2 J20 J21 J22 J23 J24 J25 J26 J27 J28 J29 J30 J31 J32 J33 J34 J35 J36 J38 J39 J40 J41 J42 J43 J44 J45 J46 J47 J48 J49 J50 J51 J52 J53 J54 J55 J56 J57 J58 J59 J60 J61 J62 J63 J64 J65 J66 J67 J68 J69 J70 J71 J72 J73 J74 J75 J76 J78 J79 J81 J82 J83 J84 J85 J87 J89 J90 J91 J92 J93 J94 J95 J96 J97 J98 J99 JE JK JM JNT JOU JPS JWL K1 K10 K11 K12 K13 K14 K15 K16 K17 K18 K19 K2 K20 K21 K22 K23 K24 K25 K26 K27 K28 K3 K30 K31 K32 K33 K34 K35 K36 K37 K38 K39 K40 K41 K42 K43 K45 K46 K47 K48 K49 K5 K50 K51 K52 K53 K54 K55 K58 K59 K6 K60 K61 K62 K63 K64 K65 K66 K67 K68 K69 K70 K71 K73 K74 K75 K76 K77 K78 K79 K80 K81 K82 K83 K84 K85 K86 K87 K88 K89 K90 K91 K92 K93 K94 K95 K96 K97 K98 K99 KA KAT KB KBA KCC KDW KEL KGM KGS KHY KHZ KI KIC KIP KJ KJO KL KLK KLX KMA KMH KMK KMQ KMT KNI KNS KNT KO KPA KPH KPO KPP KR KSD KSH KT KTN KUR KVA KVR KVT KW KWH KWO KWT KX L10 L11 L12 L13 L14 L15 L16 L17 L18 L19 L2 L20 L21 L23 L24 L25 L26 L27 L28 L29 L30 L31 L32 L33 L34 L35 L36 L37 L38 L39 L40 L41 L42 L43 L44 L45 L46 L47 L48 L49 L50 L51 L52 L53 L54 L55 L56 L57 L58 L59 L60 L63 L64 L65 L66 L67 L68 L69 L70 L71 L72 L73 L74 L75 L76 L77 L78 L79 L80 L81 L82 L83 L84 L85 L86 L87 L88 L89 L90 L91 L92 L93 L94 L95 L96 L98 L99 LA LAC LBR LBT LD LEF LF LH LK LM LN LO LP LPA LR LS LTN LTR LUB LUM LUX LY M1 M10 M11 M12 M13 M14 M15 M16 M17 M18 M19 M20 M21 M22 M23 M24 M25 M26 M27 M29 M30 M31 M32 M33 M34 M35 M36 M37 M38 M39 M4 M40 M41 M42 M43 M44 M45 M46 M47 M48 M49 M5 M50 M51 M52 M53 M55 M56 M57 M58 M59 M60 M61 M62 M63 M64 M65 M66 M67 M68 M69 M7 M70 M71 M72 M73 M74 M75 M76 M77 M78 M79 M80 M81 M82 M83 M84 M85 M86 M87 M88 M89 M9 M90 M91 M92 M93 M94 M95 M96 M97 M98 M99 MAH MAL MAM MAR MAW MBE MBF MBR MC MCU MD MGM MHZ MIK MIL MIN MIO MIU MLD MLT MMK MMQ MMT MND MON MPA MQH MQS MSK MTK MTQ MTR MTS MVA MWH N1 N10 N11 N12 N13 N14 N15 N16 N17 N18 N19 N20 N21 N22 N23 N24 N25 N26 N27 N28 N29 N3 N30 N31 N32 N33 N34 N35 N36 N37 N38 N39 N40 N41 N42 N43 N44 N45 N46 N47 N48 N49 N50 N51 N52 N53 N54 N55 N56 N57 N58 N59 N60 N61 N62 N63 N64 N65 N66 N67 N68 N69 N70 N71 N72 N73 N74 N75 N76 N77 N78 N79 N80 N81 N82 N83 N84 N85 N86 N87 N88 N89 N90 N91 N92 N93 N94 N95 N96 N97 N98 N99 NA NAR NCL NEW NF NIL NIU NL NMI NMP NPR NPT NQ NR NT NTT NU NX OA ODE OHM ON ONZ OT OZ OZA OZI P1 P10 P11 P12 P13 P14 P15 P16 P17 P18 P19 P2 P20 P21 P22 P23 P24 P25 P26 P27 P28 P29 P30 P31 P32 P33 P34 P35 P36 P37 P38 P39 P40 P41 P42 P43 P44 P45 P46 P47 P48 P49 P5 P50 P51 P52 P53 P54 P55 P56 P57 P58 P59 P60 P61 P62 P63 P64 P65 P66 P67 P68 P69 P70 P71 P72 P73 P74 P75 P76 P77 P78 P79 P80 P81 P82 P83 P84 P85 P86 P87 P88 P89 P90 P91 P92 P93 P94 P95 P96 P97 P98 P99 PAL PD PFL PGL PI PLA PO PQ PR PS PT PTD PTI PTL Q10 Q11 Q12 Q13 Q14 Q15 Q16 Q17 Q18 Q19 Q20 Q21 Q22 Q23 Q24 Q25 Q26 Q27 Q28 Q3 QA QAN QB QR QT QTD QTI QTL QTR R1 R9 RH RM ROM RP RPM RPS RT S3 S4 SAN SCO SCR SEC SET SG SHT SIE SMI SQ SQR SR STC STI STK STL STN STW SW SX SYR T0 T3 TAH TAN TI TIC TIP TKM TMS TNE TP TPR TQD TRL TST TTS U1 U2 UA UB UC VA VLT VP W2 WA WB WCD WE WEB WEE WG WHR WM WSD WTT WW X1 YDK YDQ YRD Z11 ZP ZZ X43 X44 X1A X1B X1D X1F X1G X1W X2C X3A X3H X4A X4B X4C X4D X4F X4G X4H X5H X5L X5M X6H X6P X7A X7B X8A X8B X8C XAA XAB XAC XAD XAE XAF XAG XAH XAI XAJ XAL XAM XAP XAT XAV XB4 XBA XBB XBC XBD XBE XBF XBG XBH XBI XBJ XBK XBL XBM XBN XBO XBP XBQ XBR XBS XBT XBU XBV XBW XBX XBY XBZ XCA XCB XCC XCD XCE XCF XCG XCH XCI XCJ XCK XCL XCM XCN XCO XCP XCQ XCR XCS XCT XCU XCV XCW XCX XCY XCZ XDA XDB XDC XDG XDH XDI XDJ XDK XDL XDM XDN XDP XDR XDS XDT XDU XDV XDW XDX XDY XEC XED XEE XEF XEG XEH XEI XEN XFB XFC XFD XFE XFI XFL XFO XFP XFR XFT XFW XFX XGB XGI XGL XGR XGU XGY XGZ XHA XHB XHC XHG XHN XHR XIA XIB XIC XID XIE XIF XIG XIH XIK XIL XIN XIZ XJB XJC XJG XJR XJT XJY XKG XKI XLE XLG XLT XLU XLV XLZ XMA XMB XMC XME XMR XMS XMT XMW XMX XNA XNE XNF XNG XNS XNT XNU XNV XOA XOB XOC XOD XOE XOF XOK XOT XOU XP2 XPA XPB XPC XPD XPE XPF XPG XPH XPI XPJ XPK XPL XPN XPO XPP XPR XPT XPU XPV XPX XPY XPZ XQA XQB XQC XQD XQF XQG XQH XQJ XQK XQL XQM XQN XQP XQQ XQR XQS XRD XRG XRJ XRK XRL XRO XRT XRZ XSA XSB XSC XSD XSE XSH XSI XSK XSL XSM XSO XSP XSS XST XSU XSV XSW XSY XSZ XT1 XTB XTC XTD XTE XTG XTI XTK XTL XTN XTO XTR XTS XTT XTU XTV XTW XTY XTZ XUC XUN XVA XVG XVI XVK XVL XVN XVO XVP XVQ XVR XVS XVY XWA XWB XWC XWD XWF XWG XWH XWJ XWK XWL XWM XWN XWP XWQ XWR XWS XWT XWU XWV XWW XWX XWY XWZ XXA XXB XXC XXD XXF XXG XXH XXJ XXK XYA XYB XYC XYD XYF XYG XYH XYJ XYK XYL XYM XYN XYP XYQ XYR XYS XYT XYV XYW XYX XYY XYZ XZA XZB XZC XZD XZF XZG XZH XZJ XZK XZL XZM XZN XZP XZQ XZR XZS XZT XZU XZV XZW XZX XZY XZZ ', concat(' ', normalize-space(.), ' '))))" 
        id="BR-CL-23"
        flag="fatal">[BR-CL-23]-Unit code MUST be coded according to the UN/ECE Recommendation 20 with
        Rec 21 extension</assert>
    </rule>

    <rule context="BT-125-1" flag="fatal">
      <assert
        test="((. = 'application/pdf' or . = 'image/png' or . = 'image/jpeg' or . = 'text/csv' or . = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' or . = 'application/vnd.oasis.opendocument.spreadsheet'))" 
        id="BR-CL-24"
        flag="fatal">[BR-CL-24]-For Mime code in attribute use MIMEMediaType.</assert>
    </rule>

  </pattern>

</schema>
