# Getting Started

## Get The Code

Obtain a copy of the _eEisi_ source code. You can obtain it from [the eEISI GitHub](https://github.com/2017-IT-IA-0150/EeISI)
or from [Infocert](https://www.infocert.it/).



## Compile The Code

> Pls note that Eeisi has to be built from a cloned repository, if you try to compile it from a zip distribution, 
it won't build.

Open a shell, go to the source root folder and compile the project.

    $ cd <PROJECT-HOME>
    $ mvn clean install -Prelease

Maven will compile and install the _eEisi_ libraries in your local Maven repository.

## Prepare A Maven Project For An eEisi Client
  
You can now use _eEisi_ the same way you would do with any other Maven project.
Create a new Maven project with this _pom.xml_:

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>test</groupId>
        <artifactId>test</artifactId>
        <version>1.0-SNAPSHOT</version>
    
        <dependencies>
            <dependency>
                <groupId>it.infocert.eigor</groupId>
                <artifactId>eigor-api</artifactId>
                <version>4.0.1-SNAPSHOT</version>
            </dependency>
            <dependency>
                <groupId>ch.qos.logback</groupId>
                <artifactId>logback-classic</artifactId>
                <version>1.2.3</version>
            </dependency>
        </dependencies>
        
    </project>
    
`eigor-api` is the dependnecy that will give you the entry point to the eEisi API. 
`logback-classic` is a _SLF4J_ logging implementation that _eEisi_ can use to log.
    
    
    
## Write a eEisi Client

In the `src/main` create a `Test.java` file with a `main` method.
The first step is to create an `EigorApiBuilder` that provides instances of the `EigorApi`
that can be used to convert between invoices.


    import com.infocert.eigor.api.EigorApi;
    import com.infocert.eigor.api.EigorApiBuilder;
    
    EigorApi api = new EigorApiBuilder()
        .build(); 

We can now use the `api` to convert an Italian _FatturaPA_ invoice in an _UBL_ invoice.
The usage is pretty strightforward, just call the `convert()` method passing 

* the name of the format of the invoice that should be converted.
* the name of the format the source invoice should be converted into.
* an `InputStream` providing the content of the invoice.


    import it.infocert.eigor.api.ConversionResult;
    import it.infocert.eigor.api.IConversionIssue;

    File sourceInvoice = new File("C:\\Users\\esche\\workspace\\repo\\infocert\\eeisi\\eigor-api\\src\\test\\resources\\issues\\issue-245-fattpa.xml");
    ConversionResult<byte[]> convert = api.convert(
        "fatturapa",
        "ubl",
        new FileInputStream(sourceInvoice)
    );
    
Finally, you can just access the `COnversionResult` object to have a list of issues
and the final converted invoice:

    List<IConversionIssue> issues = convert.getIssues();
    for (IConversionIssue issue : issues) {
        System.out.println(issue);
    }
    System.out.println( new String( convert.getResult() ) );    
    
## Configure eEisi Logging

Just write in the `src/main/resources` a valid _Logback_ configuration file in order to set up the logging as you wish.
If you're in doubt you can use this template:

    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="error">
            <appender-ref ref="STDOUT" />
        </root>
        <logger name="it.infocert" level="warn" />
    </configuration>
 
## Launch the Program

Launch the program you just wrote. If all is well, you should see something similar in your console:


    17:19:10.741 [main] DEBUG it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader - Skipping loading Eigor configuration from classpath resource '/eigor-test.properties' that does not exist.
    17:19:10.752 [main] DEBUG it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader - Successfully loaded Eigor configuration from classpath resource '/eigor.properties'
    17:19:10.911 [main] INFO com.infocert.eigor.api.EigorApiBuilder - Eigor
    maven-version: 4.0.1-SNAPSHOT
    git-branch: develop
    git-revision: df700f909204e336760af138c22f21555c8b9db7
    git-timestamp: 2019-01-29T13:40:29+0100
    
    UBL_OUT.SCH_VALIDATION.INVALID - Schematron failed assert '[BR-E-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Exempt from VAT” shall contain exactly one VATBReakdown (BG-23) with the VAT category code (BT-118) equal to "Exempt from VAT".' on XML element at '/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]'. it.infocert.eigor.api.EigorException: Schematron failed assert '[BR-E-01]-An Invoice that contains an Invoice line (BG-25), a Document level allowance (BG-20) or a Document level charge (BG-21) where the VAT category code (BT-151, BT-95 or BT-102) is “Exempt from VAT” shall contain exactly one VATBReakdown (BG-23) with the VAT category code (BT-118) equal to "Exempt from VAT".' on XML element at '/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]'. Fatal: true
    UBL_OUT.SCH_VALIDATION.INVALID - Schematron failed assert '[BR-S-08]-For each different value of VAT category rate (BT-119) where the VAT category code (BT-118) is "Standard rated", the VAT category taxable amount (BT-116) in a VATBReakdown (BG-23) shall equal the sum of Invoice line net amounts (BT-131) plus the sum of document level charge amounts (BT-99) minus the sum of document level allowance amounts (BT-92) where the VAT category code (BT-151, BT-102, BT-95) is “Standard rated” and the VAT rate (BT-152, BT-103, BT-96) equals the VAT category rate (BT-119).' on XML element at '/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]/*:TaxTotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]/*:TaxSubtotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]/*:TaxCategory[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]'. it.infocert.eigor.api.EigorException: Schematron failed assert '[BR-S-08]-For each different value of VAT category rate (BT-119) where the VAT category code (BT-118) is "Standard rated", the VAT category taxable amount (BT-116) in a VATBReakdown (BG-23) shall equal the sum of Invoice line net amounts (BT-131) plus the sum of document level charge amounts (BT-99) minus the sum of document level allowance amounts (BT-92) where the VAT category code (BT-151, BT-102, BT-95) is “Standard rated” and the VAT rate (BT-152, BT-103, BT-96) equals the VAT category rate (BT-119).' on XML element at '/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]/*:TaxTotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]/*:TaxSubtotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]/*:TaxCategory[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]'. Fatal: true
    UBL_OUT.SCH_VALIDATION.INVALID - Schematron failed assert '[BR-Z-08]-In a VATBReakdown (BG-23) where VAT category code (BT-118) is "Zero rated" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amount (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Zero rated".' on XML element at '/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]/*:TaxTotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]/*:TaxSubtotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][2]/*:TaxCategory[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]'. it.infocert.eigor.api.EigorException: Schematron failed assert '[BR-Z-08]-In a VATBReakdown (BG-23) where VAT category code (BT-118) is "Zero rated" the VAT category taxable amount (BT-116) shall equal the sum of Invoice line net amount (BT-131) minus the sum of Document level allowance amounts (BT-92) plus the sum of Document level charge amounts (BT-99) where the VAT category codes (BT-151, BT-95, BT-102) are “Zero rated".' on XML element at '/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]/*:TaxTotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]/*:TaxSubtotal[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][2]/*:TaxCategory[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]'. Fatal: true
    
    <?xml version="1.0" encoding="UTF-8"?>
    <Invoice xmlns="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDataTypes-2" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnqualifiedDataTypes-2">
      <cbc:CustomizationID>urn:cen.eu:en16931:2017</cbc:CustomizationID>
      <cbc:ID>123</cbc:ID>
      ...
      <cac:InvoicePeriod>
        <cbc:StartDate>2017-01-18</cbc:StartDate>
        <cbc:EndDate>2017-01-18</cbc:EndDate>
        <cbc:DescriptionCode>3</cbc:DescriptionCode>
      </cac:InvoicePeriod>
      ...      
      <cac:AccountingSupplierParty>
        <cac:Party>
      ...
        </cac:Party>
      </cac:AccountingSupplierParty>
      ...        
        <cac:Price>
          <cbc:PriceAmount currencyID="EUR">1.00</cbc:PriceAmount>
          <cbc:BaseQuantity unitCode="C62">1.00</cbc:BaseQuantity>
        </cac:Price>
      </cac:InvoiceLine>
    </Invoice>

Where you can identify: 
* the log, written out according to the Logback configuration file.
* the issues discovered by _eEisi_ during conversion, in this case problems related to the validity of the produced
invoice related to the UBL schematron.
* the output invoice as it was produced by _eEisi_.

## Documentation

To build the project documentation, issue this command:

    $ mvn site site:stage
    
Then point your browser to the target directory where the Maven generated doc is placed. 

## Conclusion

This represent a minimal set up, you can built upon it to build your system based on _eEisi_.
