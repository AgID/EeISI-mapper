Eigor Run Guide
=================

_Eigor_ comes in two flavours.
* _Eigor API_ allows you to integrate the _Eigor_ features in your project.
* _Eigor CLI_ is a stand alone command line application that allows you to run conversions from the console.

### Eigor API

The _Eigor API_ is quite easy to use, as shown in the following snippet.

```java
    // 1. Construct an instance of EigorAPI using the related builder.
    // The API obtained is thread safe and can be then used to convert multiple invoices.
    EigorApi api = new EigorApiBuilder()
            .withOutputFolder(outputFolderFile)
            .build();
    
    // 2. Load the invoice to be converted as a stream
    InputStream invoiceAsStream = new ByteArrayInputStream("<invoice>data</invoice>".getBytes());
    
    // 3. Execute the conversion specifying the source format, the target format and the invoice to be transformed.
    ConversionResult<byte[]> outcome = api
            .convert(
                    "ubl",
                    "fatturapa",
                    invoiceAsStream);
    
    // 4. You have now multiple ways to query the outcome object.
    
    // ...check if the convertion finished with issues.
    boolean hasErrors = outcome.hasIssues();
    
    // ...get the complete list of occurred issues.
    List<IConversionIssue> issues = outcome.getIssues();
    
    // ...whether a converted invoice is available.
    outcome.hasResult();
    
    // ...and in this case you can obtain the produced XML.
    byte[] result = outcome.getResult();
```

### Eigor CLI

After you successfully built _Eigor_, you will find the zipped version of _Eigor CLI_ in `eigor-cli/target/eigor.zip`.

Unzip the file and run one of the following scripts, according to your OS:   

*Windows CMD*
```
eigor.bat
```

*Windows Powershell*
```powershell
.\eigor.bat
```

*Unix (MacOS, Linux, BSD...)*
```bash
./eigor.sh
```

The application will show you a brief help explaining how to proceed to convert an invoice.


Invoices that you can use as example can be found in the `examples` folder. 

Cconfiguration files are stored in  `./conf`.
 
Log files are stored in `./logs`. 

A `./reports` folder will be automatically created as a potential output results.

