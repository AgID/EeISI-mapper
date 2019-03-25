# Customize EeISI

Advanced users can customize EeISI changing the schematron and the custom mapping that are used by the tool.

By default, EeISI uses the default versions for this artifacts that are shipped with 
the tool itslef.

However, in some scenarios, is reasonable that one would like to be able to change a schematron,
or a custom mapping, for instance to quickly support a new version of a schematron without waiting
for a new EeISI release, or just to experiment with new mappings and rules. 

## Prepare The Validation Repository

_eEISI_ uses several artifact to validate the invoices it handles XSD schemas, and schematron files.
So, before start using _eEISI_ one should obtain all this artifacts.

Luckily, each version of _eEISI_ provides with the latest version of all the validation artifact for each supported format.

Unzip the `eigor-configurations/target/eigor-configurations-<VERSION>.zip` or the equivalent
`eigor-configurations/target/eigor-configurations-<VERSION>.tar.gz` file wherever you prefer.

You should obtain a structure like this:


    ├───converter-cen-cii
    │   └───mappings
    ├───converter-cen-fattpa
    │   └───mappings
    ├───converter-cen-ubl
    │   └───mappings
    ├───converter-cen-ublcn
    │   └───mappings
    ├───converter-cen-xmlcen
    │   └───mappings
    ├───converter-cii-cen
    │   └───mappings
    ├───converter-commons
    │   ├───cii
    │   │   ├───cius
    │   │   │   ├───schematron
    │   │   │   └───schematron-xslt
    │   │   ├───schematron
    │   │   │   ├───abstract
    │   │   │   ├───CII
    │   │   │   └───codelist
    │   │   └───schematron-xslt
    │   ├───ubl
    │   │   ├───cius
    │   │   │   ├───schematron
    │   │   │   └───schematron-xslt
    │   │   ├───schematron
    │   │   │   ├───abstract
    │   │   │   ├───codelist
    │   │   │   └───UBL
    │   │   └───schematron-xslt
    │   └───ublcn
    │       ├───xsd
    │       └───xsdstatic
    │           └───imported
    ├───converter-fattpa-cen
    │   └───mappings
    ├───converter-ubl-cen
    │   └───mappings
    ├───converter-ublcn-cen
    │   └───mappings
    └───converter-xmlcen-cen
        └───mappings

It's pretty easy to identify that for each supported conversion there is a subfolder that contains 
the related validation and mapping artifact.

## Configure eEisi
    
Before launching the application, you should provide an `eigor.properties` file 
that define some key information.

If you are working with a Maven module, just create an empty `eigor.properties` file in your `src/main/resources` fodler. 
This is enough to have _eEisi_ to read that file, because it is read from the classpath. 

Write the following content in the `eigor.properties` file:

    eigor.workdir=${prop.java.io.tmpdir}eigor
    eigor.validation-home=C:/tmp/eeisi/converterdata
            
    eigor.converter.cen-cii.mapping.one-to-one=classpath:converterdata/converter-cen-cii/mappings/one_to_one.properties
    eigor.converter.cen-cii.mapping.many-to-one=classpath:converterdata/converter-cen-cii/mappings/many_to_one.properties
    eigor.converter.cen-cii.mapping.one-to-many=classpath:converterdata/converter-cen-cii/mappings/one_to_many.properties
    eigor.converter.cen-cii.mapping.custom=classpath:converterdata/converter-cen-cii/mappings/custom.conf
    eigor.converter.cen-cii.schematron=file://${eigor.validation-home}/converter-commons/cii/schematron-xslt/EN16931-CII-validation.xslt
    eigor.converter.cen-cii.schematron.auto-update-xslt=false
    eigor.converter.cen-cii.cius=file://${eigor.validation-home}/converter-commons/cii/cius/schematron-xslt/EN16931-CIUS-IT-CIIValidation.xslt
    eigor.converter.cen-cii.cius.auto-update-xslt=false
    eigor.converter.cen-cii.guideline-context=urn:cen.eu:en16931:2017
    
    
    eigor.converter.cii-cen.mapping.one-to-one=classpath:converterdata/converter-cii-cen/mappings/one_to_one.properties
    eigor.converter.cii-cen.mapping.many-to-one=classpath:converterdata/converter-cii-cen/mappings/many_to_one.properties
    eigor.converter.cii-cen.mapping.one-to-many=classpath:converterdata/converter-cii-cen/mappings/one_to_many.properties
    eigor.converter.cii-cen.mapping.custom=classpath:converterdata/converter-cii-cen/mappings/custom.conf
    eigor.converter.cii-cen.schematron=file://${eigor.validation-home}/converter-commons/cii/schematron-xslt/EN16931-CII-validation.xslt
    eigor.converter.cii-cen.schematron.auto-update-xslt=false
    eigor.converter.cii-cen.cius=file://${eigor.validation-home}/converter-commons/cii/cius/schematron-xslt/EN16931-CIUS-IT-CIIValidation.xslt
    eigor.converter.cii-cen.cius.auto-update-xslt=false
    
    eigor.converter.ubl-cen.cius=file://${eigor.validation-home}/converter-commons/ubl/cius/schematron-xslt/EN16931-CIUS-IT-UBLValidation.xslt
    eigor.converter.ubl-cen.cius.auto-update-xslt=false
    eigor.converter.ubl-cen.schematron=file://${eigor.validation-home}/converter-commons/ubl/schematron-xslt/EN16931-UBL-validation.xslt
    eigor.converter.ubl-cen.schematron.auto-update-xslt=false
    eigor.converter.ubl-cen.mapping.one-to-one=classpath:converterdata/converter-ubl-cen/mappings/one_to_one.properties
    eigor.converter.ubl-cen.mapping.many-to-one=classpath:converterdata/converter-ubl-cen/mappings/many_to_one.properties
    eigor.converter.ubl-cen.mapping.one-to-many=classpath:converterdata/converter-ubl-cen/mappings/one_to_many.properties
    eigor.converter.ubl-cen.mapping.custom=classpath:converterdata/converter-ubl-cen/mappings/custom.conf
    
    eigor.converter.ublcn-cen.cius=file://${eigor.validation-home}/converter-commons/ubl/cius/schematron-xslt/EN16931-CIUS-IT-UBLValidation.xslt
    eigor.converter.ublcn-cen.cius.auto-update-xslt=false
    eigor.converter.ublcn-cen.schematron=file://${eigor.validation-home}/converter-commons/ubl/schematron-xslt/EN16931-UBL-validation.xslt
    eigor.converter.ublcn-cen.schematron.auto-update-xslt=false
    eigor.converter.ublcn-cen.mapping.one-to-one=classpath:converterdata/converter-ublcn-cen/mappings/one_to_one.properties
    eigor.converter.ublcn-cen.mapping.many-to-one=classpath:converterdata/converter-ublcn-cen/mappings/many_to_one.properties
    eigor.converter.ublcn-cen.mapping.one-to-many=classpath:converterdata/converter-ublcn-cen/mappings/one_to_many.properties
    eigor.converter.ublcn-cen.mapping.custom=classpath:converterdata/converter-ublcn-cen/mappings/custom.conf
            
    eigor.converter.cen-fatturapa.mapping.one-to-one=classpath:converterdata/converter-cen-fattpa/mappings/one_to_one.properties
    eigor.converter.cen-fatturapa.mapping.many-to-one=classpath:converterdata/converter-cen-fattpa/mappings/many_to_one.properties
    eigor.converter.cen-fatturapa.mapping.one-to-many=classpath:converterdata/converter-cen-fattpa/mappings/one_to_many.properties
    eigor.converter.cen-fatturapa.mapping.custom=classpath:converterdata/converter-cen-fattpa/mappings/custom.conf
    
    eigor.converter.cen-fatturapa.field-lengths=file://${eigor.validation-home}/converter-cen-fattpa/mappings/field_lengths.properties
            
    eigor.converter.fatturapa-cen.mapping.one-to-one=classpath:converterdata/converter-fattpa-cen/mappings/one_to_one.properties
    eigor.converter.fatturapa-cen.mapping.many-to-one=classpath:converterdata/converter-fattpa-cen/mappings/many_to_one.properties
    eigor.converter.fatturapa-cen.mapping.one-to-many=classpath:converterdata/converter-fattpa-cen/mappings/one_to_many.properties
    eigor.converter.fatturapa-cen.mapping.custom=classpath:converterdata/converter-fattpa-cen/mappings/custom.conf
            
    eigor.converter.cen-ubl.mapping.one-to-one=classpath:converterdata/converter-cen-ubl/mappings/one_to_one.properties
    eigor.converter.cen-ubl.mapping.many-to-one=classpath:converterdata/converter-cen-ubl/mappings/many_to_one.properties
    eigor.converter.cen-ubl.mapping.one-to-many=classpath:converterdata/converter-cen-ubl/mappings/one_to_many.properties
    eigor.converter.cen-ubl.mapping.custom=classpath:converterdata/converter-cen-ubl/mappings/custom.conf
    eigor.converter.cen-ubl.schematron=file://${eigor.validation-home}/converter-commons/ubl/schematron-xslt/EN16931-UBL-validation.xslt
    eigor.converter.cen-ubl.schematron.auto-update-xslt=false
    eigor.converter.cen-ubl.customization-id=urn:cen.eu:en16931:2017
            
    eigor.converter.cen-ublcn.mapping.one-to-one=classpath:converterdata/converter-cen-ublcn/mappings/one_to_one.properties
    eigor.converter.cen-ublcn.mapping.many-to-one=classpath:converterdata/converter-cen-ublcn/mappings/many_to_one.properties
    eigor.converter.cen-ublcn.mapping.one-to-many=classpath:converterdata/converter-cen-ublcn/mappings/one_to_many.properties
    eigor.converter.cen-ublcn.mapping.custom=classpath:converterdata/converter-cen-ublcn/mappings/custom.conf
    eigor.converter.cen-ublcn.schematron=file://${eigor.validation-home}/converter-commons/ubl/schematron-xslt/EN16931-UBL-validation.xslt
    eigor.converter.cen-ublcn.schematron.auto-update-xslt=false
            
    eigor.converter.xmlcen-cen.mapping.one-to-one=classpath:converterdata/converter-xmlcen-cen/mappings/one_to_one.properties
    eigor.converter.xmlcen-cen.mapping.many-to-one=classpath:converterdata/converter-xmlcen-cen/mappings/many_to_one.properties
    eigor.converter.xmlcen-cen.mapping.one-to-many=classpath:converterdata/converter-xmlcen-cen/mappings/one_to_many.properties
    eigor.converter.xmlcen-cen.mapping.custom=classpath:converterdata/converter-xmlcen-cen/mappings/custom.conf
 
You mainly uses this file to set up some key info.


* `eigor.workdir` is a mandatory property, it should refer to a folder in your filesystem where eEisi can work some temporary files used during the conversions.
* `eigor.validation-home` is not mandatory, if you look to the config closely, you'll note that it is just used as a placeholder in the definitions of other properties.

__Be sure that `eigor.validation-home` refers to the home of the validation repository you created before.__


> If you have knowledge of the _Spring Framework_ you'll note that several properties
refers to resources in your filesystem using the same syntax of _Spring_'s _ResourceLoader_
to seamlessly access resources seamlessly regardless of their location.
If you need you can have further info in the official _SPring Framework_ [documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#resources-resourceloader>).
