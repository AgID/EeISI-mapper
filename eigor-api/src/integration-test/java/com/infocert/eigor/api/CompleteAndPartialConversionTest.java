
package com.infocert.eigor.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.infocert.eigor.api.ConversionUtil.keepErrorsNotWarnings;
import static org.junit.Assume.assumeFalse;

@RunWith(Parameterized.class)
public class CompleteAndPartialConversionTest extends AbstractIssueTest  {

    @Parameters(name = "{index}: {0} {1} to {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "/minimum_and_full_examples/Test_EeISI_310_CENminimumcontent.xml", "xmlcen", "xmlcen", false },
                { "/minimum_and_full_examples/Test_EeISI_310_CENminimumcontent.xml", "xmlcen", "cii", false },
                { "/minimum_and_full_examples/Test_EeISI_310_CENminimumcontent.xml", "xmlcen", "ubl", false },
                { "/minimum_and_full_examples/Test_EeISI_310_CENminimumcontent.xml", "xmlcen", "peppolbis", true }, // wait check from Sara
                { "/minimum_and_full_examples/Test_EeISI_310_CENminimumcontent.xml", "xmlcen", "fatturapa", false },

                { "/minimum_and_full_examples/Test_EeISI_300_CENfullmodel.xml", "xmlcen", "xmlcen", false },
                { "/minimum_and_full_examples/Test_EeISI_300_CENfullmodel.xml", "xmlcen", "cii", true }, // wait check from Sara
                { "/minimum_and_full_examples/Test_EeISI_300_CENfullmodel.xml", "xmlcen", "ubl", false },
                { "/minimum_and_full_examples/Test_EeISI_300_CENfullmodel.xml", "xmlcen", "peppolbis", true }, // wait check from Sara
                { "/minimum_and_full_examples/Test_EeISI_300_CENfullmodel.xml", "xmlcen", "fatturapa", false },

                { "/minimum_and_full_examples/Test_EeISI_320_CENITfullmodel.xml", "xmlcen", "xmlcen", false },
                { "/minimum_and_full_examples/Test_EeISI_320_CENITfullmodel.xml", "xmlcen", "cii", true }, // wait check from Sara
                { "/minimum_and_full_examples/Test_EeISI_320_CENITfullmodel.xml", "xmlcen", "ubl", false },
                { "/minimum_and_full_examples/Test_EeISI_320_CENITfullmodel.xml", "xmlcen", "peppolbis", true }, // wait check from Sara
                { "/minimum_and_full_examples/Test_EeISI_320_CENITfullmodel.xml", "xmlcen", "fatturapa", false }
        });
    }

    @Parameter(0)
    public String inputInvoicePath;

    @Parameter(1)
    public String inputFormat;

    @Parameter(2)
    public String outputFormat;

    @Parameter(3)
    public boolean skip;

    @Test
    public void performConversion() {

        assumeFalse(skip);

        this.conversion.assertConversionWithoutErrors(
                inputInvoicePath,
                inputFormat, outputFormat, keepErrorsNotWarnings());

    }


}
