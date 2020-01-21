# EeISI

## Introduction
This source code represents one of the EeISI proejct results: the mapper. EeISI European eInvoicing Standard in Italy (action n. 2017-IT-IA-0150) is a EU project funded by the European Commission through the Connecting Europe Facility (CEF) program. See https://www.agid.gov.it/en/platforms/electronic-invoicing/cef-eeisi-project

The EeISI mapper translates from the formats required by EN 16931 (OASIS UBL and UN/CEFACT CII) into the national FatturaPA format and viceversa.

Quick
[Getting started](src/site/markdown/getting-started.md)

## Release notes

Version number | Release date | Notable changes
-------------- | ------------ | ---------------
version 4.5.0 | 2019-12-30 | Latest release for EeISI project (end date 2019-12-31); minor bugfix on CEN to peppol modules
version 4.4.3 | 2019-07-23 | Latest version integrated into SDI; CEN schematron update version 1.2.3 release 2019-07-05 (UBL, CII) (new EAS and VATEX); Update to substitute 9921 with 0201 (source code, example, CIUS-IT schematron); Update UBL to CEN and CEN to UBL for MultiplierFactorNumeric (now considered as percentage and not as number between 0 and 1)
version 4.4.0 | 2019-05-28 | CEN schematron update version 1.2.1 release 2019-05-14 (UBL, CII), Update mapping Natura-VAT exemption reason code according to new VATEX list published on 2019-03-15; Added XXE vulnerability handler
version 4.3.3 | 2019-04-30 | Minor bugfix on CEN to peppol credit node module
version 4.3.2 | 2019-04-15 | CEN schematron update (UBL, CII); Peppol CIUS schematron update 
version 4.3.1 | 2019-04-11 | Bugfix on CEN to CII module; Filename change requrest from AdE to "not-mapped-values.txt"
version 4.3.0 | 2019-04-04 | Automatic selection for UBL invoice and UBL credit note translation module, Logback-test.xml for all modules; Minor bugfix
version 4.2.2 | 2019-03-27 | External folder with sch and xsd is now optional; API for force and intermediate-validation; Documentation update .md; eigor-commons deprecated; Updated CIUS-IT BR-IT-380 (quantity signum enabled); Minor bugfix
version 4.2.1 | 2019-03-21| Minor bugfix
version 4.2.0 | 2019-03-19| Alignment between API and CLI functionalities, CLI module refactoring; XSD integration without the need of external folder; CIUS-IT schematron update (VAT category code, no rules on domestic); CEN schematron update (UBL, CII); Peppol CIUS schematron update; Minor bugfix
version 4.1.2 | 2019-02-28 | Added intermediate CEN semantic validation with verbose flag; Manually modified CEN CII schematron from VAT to VA; Minor bugfix
version 4.1.1 | 2019-02-08 | Added module cen-peppolcn
version 4.1.0 | 2019-01-31 | Compared to the eIGOR mapper the following additional functionalities have been added: Migration from Java 7 to Java 8; Update sch2xslt libraries ; New entry points to separate validation from mapping; New entry points to separate validation steps (syntax, semantic, cius); New entry point for validation with external sch or xsd; Intermediate mapping in semantic format XMLCEN;  Semantic metamodel validation (xsd); Semantic validation schematron (CEN and CIUS-IT); Schematron CEN update (from https://github.com/CenPC434/validation); Schematron CIUS-IT update (from D.2.1 Italian_CIUS_2B_Still_Not_Approved_By_AgIDBozza20181128con evidenze.xlsx); Syntax binding updated according to CEN-TC434_N0219_Corrigendum_for_Electronic_invoicing_-Part.pdf (August 2018); Modified mapping BT-32 to CodiceFiscale instead of RegimeFiscale (MR-29, MR-39, MAPR-CM-26, MAPR-IM-23); Mapping between VAT category Code and Natura according to EN16931-Matrix Rel.1.2.0_last version eIGOR.xlsx   (waiting for consolidation of VAT Category Code - Natura - RegimeFiscale - VAT exemption reason code and RegimeFiscale code list update)   Temporary mapping of RegimeFiscale to default value RF01; New mapper from XMLPAvsPeppolBIS3 for Infocamere scenario (according to Mapping rules CENvsPeppolBIS3 EN16931-Matrix Rel.2.1.2.xlsx); Domestic invoice mapping for Seller and Buyer identifiers (according to EN16931-Matrix Rel.2.1.2.xlsx); Domestic invoice mapping for Bollo, Ritenuta, CassaPrevidenziale, Split (according to EN16931-Matrix Rel.1.2.0_last version eIGOR.xlsx); Modified mapping for CII related to BT-8 and UNTDID 2475 instead of UNTDID 2005; Modified mapping for BT-31, BT-48, BT-63 in CII where schemeID is VA instead of VAT; New mapping for BT-49 with EAS code list (published December 2018) using "9921" for IT:IPA; Full mapping for code list UNTDID1001 BT-3; Full mapping for code list UNTDID4461  BT-81; Modified mapping rule MAPR-CM-27 for BT-81; New mapping rule BT-128, BT-128-1 from UBL/CIIvsCEN; New mapping rules for BT-20, BT-41, BT-56 CENvsXMLPA; New mapping rules for length truncate mechanism (MAPR-TR-124, MAPR-TR-125, MAPR-TR-126, MAPR-TR-127, MAPR-TR-128, MAPR-TR-129, MAPR-TR-130, MAPR-TR-131, MAPR-TR-132, MAPR-TR-133, MAPR-TR-134, MAPR-TR-135, MAPR-TR-136, MAPR-TR-137, MAPR-TR-138, MAPR-TR-139, MAPR-TR-140, MAPR-TR-141, MAPR-TR-142, MAPR-TR-143, MAPR-TR-144, MAPR-TR-145, MAPR-TR-146); New mapping rules for UBL MultiplierFactorNumeric (MAPR-UBL-123, MR-68); New mapping rule for DATE UBL with timezone (MAPR-DA-122); New mapping rule for BT-23 and BT-24 (generic UBL and CII with urn:cen.eu:en16931:2017) ; New mapping rule for BT-23 and BT-24 from CENvsPeppolBIS3 (BT-23=” urn:fdc:peppol.eu:2017:poacc:billing:01:1.0” BT-24=”urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0”); Error codes list updated; Documentation and readme.md update; Added licence EUPL. 
First EeISI release | 2019-01-31| Notes on the first EeISI mapper release: Waiting for final decision on domestic mapping (Bollo, ritenuta, cassa previdenziale, split payment, AIC farmaco) and related detailed specifications; Waiting for update of CIUS-IT according to on-hold decisions (domestic invoices, ICD, vat category code mapping); Waiting for A-deviation process for split payment;  Waiting for VATEX code list from CEF; Waiting for ICD registration IT:PEC, IT:CODDEST for B2B process; Waiting for finalization on VAT Category Code - Natura - RegimeFiscale - VAT exemption reason code mapping; Waiting for corrigenda on CEN schematron (CII VA instead of VAT, CII code list 5189 instead of 4465); Waiting for corrigenda on appropriate use of MultiplierFactorNumeric  by PeppolBIS3; Waiting for corrigenda on syntax binding mismatch cardinalities UBL and CII.

## Build

Java 8, Maven and a working Internet connection are needed.
Build with:

    mvn install -Prelease

## Documentation

Would you like to read the documentation?
Just generate it from the code with:

    mvn site site:stage
    
Then point your browser to `target/staging/index.html`.

