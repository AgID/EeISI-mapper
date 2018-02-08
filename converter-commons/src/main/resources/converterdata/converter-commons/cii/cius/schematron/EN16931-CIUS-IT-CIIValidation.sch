<?xml version="1.0" encoding="UTF-8"?>

<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns:qdt="urn:un:unece:uncefact:data:standard:QualifiedDataType:100"
        xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
        xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"
        queryBinding="xslt2">
    <title>EN16931 CII CIUS</title>
    <ns prefix="rsm" uri="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"/>
    <ns prefix="xsd" uri="http://www.w3.org/2001/XMLSchema"/>
    <ns prefix="qdt" uri="urn:un:unece:uncefact:data:standard:QualifiedDataType:100"/>
    <ns prefix="ram" uri="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"/>
    <ns prefix="udt" uri="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"/>

    <phase id="EN_16931_CIUS">
        <active pattern="CIUS-IT"/>
    </phase>
    <!-- Abstract CEN BII patterns -->
    <!-- ========================= -->
    <include href="abstract/CIUS-IT-Validation.sch"/>
    <!-- Data Binding parameters -->
    <!-- ======================= -->
    <include href="CIUS-IT/CIUS-IT-CII-Validation.sch"/>
    <!-- Code Lists Binding rules -->
    <!-- ======================== -->






</schema>
