package com.infocert.eigor.api;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import com.amoerie.jstreams.functions.Filter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class ITGuaranteedInvoiceExamples {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private List<File> testInvoices;
    private EigorApi api;

    @Before
    public void setUp() throws Exception {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final URL resource = classLoader.getResource("working-examples");
        assert resource != null;

        final File examplesDirectory = new File(resource.getFile());
        if (examplesDirectory.isDirectory()) {
            final File[] files = Preconditions.checkNotNull(examplesDirectory.listFiles(), "No files found in resources/working-examples");
            this.testInvoices = Stream.of(files).filter(new Filter<File>() {
                @Override
                public boolean apply(File file) {
                    return file.getName().endsWith(".xml");
                }
            }).toList();

        }

        final File outputFolderFile = tmp.newFolder();
        if (!outputFolderFile.exists()) outputFolderFile.mkdirs();


        api = new EigorApiBuilder()
                .enableAutoCopy()
                .withOutputFolder(outputFolderFile)
                .build();
    }

    @Test
    public void shouldConvertFattpaExampleWithoutErrors() throws Exception {
        Stream.of(testInvoices).filter(new Filter<File>() {
            @Override
            public boolean apply(File file) {
                return file.getName().startsWith("fattpa");
            }
        }).forEach(new Consumer<File>() {
            @Override
            public void consume(File invoice) {
                try {
                    final ConversionResult<byte[]> result = api.convert("fatturapa", "ubl", new FileInputStream(invoice));
                    assertFalse(result.hasIssues());
                    assertTrue(result.isSuccessful());
                    assertTrue(result.hasResult());
                } catch (FileNotFoundException e) {
                    fail();
                }
            }
        });


    }

    @Test
    public void shouldConvertUblExampleWithoutErrors() throws Exception {

    }
}
