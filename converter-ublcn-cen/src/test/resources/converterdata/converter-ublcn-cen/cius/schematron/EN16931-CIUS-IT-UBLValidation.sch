<?xml version="1.0" encoding="UTF-8"?>
<!-- 

            UBL syntax binding to the CIUS ITALIA  
            Created by eIGOR Project
            Timestamp: 2017-09-11 12:00:00 +0100
     -->

<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        xmlns:cn="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"
        xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
        xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
        xmlns:UBL="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xs="http://www.w3.org/2001/XMLSchema"
        queryBinding="xslt2">
    <title>EN16931 UBL CIUS IT</title>
    <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
    <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
    <ns prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"/>
    <ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
    <ns prefix="xs" uri="http://www.w3.org/2001/XMLSchema"/>

    <phase id="EN_16931_CIUS_IT">
        <active pattern="CIUS-IT"/>
    </phase>
    <!-- Abstract CEN BII patterns -->
    <!-- ========================= -->
    <include href="abstract/CIUS-IT-Validation.sch"/>
    <!-- Data Binding parameters -->
    <!-- ======================= -->
    <include href="CIUS-IT/CIUS-IT-UBL-Validation.sch"/>
    <!-- Code Lists Binding rules -->
    <!-- ======================== -->
    <!-- Currently not required -->
    






</schema>
