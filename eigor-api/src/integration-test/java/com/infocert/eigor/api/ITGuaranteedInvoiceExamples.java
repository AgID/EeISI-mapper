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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.*;

public class ITGuaranteedInvoiceExamples {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private List<File> testInvoices;
    private EigorApi api;

    @Before
    public void setUp() throws Exception {

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final URL resource = classLoader.getResource("working-examples" );
        assert resource != null;

        final File examplesDirectory = new File(resource.getFile());
        if (examplesDirectory.isDirectory()) {
            final File[] files = Preconditions.checkNotNull(examplesDirectory.listFiles(), "No files found in resources/working-examples" );
            this.testInvoices = Stream.of(files).filter(new Filter<File>() {
                @Override
                public boolean apply(File file) {
                    return file.getName().endsWith(".xml" );
                }
            }).toList();

        }

        final File outputFolderFile = tmp.newFolder();
        if (!outputFolderFile.exists()) outputFolderFile.mkdirs();


        api = new EigorApiBuilder()
                .enableAutoCopy()
                .withOutputFolder(outputFolderFile)
                .enableForce()
                .build();
    }

    @Test
    public void shouldConvertFattpaExampleWithoutErrors() throws Exception {
        Stream.of(testInvoices).filter(new Filter<File>() {
            @Override
            public boolean apply(File file) {
                return file.getName().startsWith("fattpa" );
            }
        }).forEach(new Consumer<File>() {
            @Override
            public void consume(File invoice) {
                try {
                    final ConversionResult<byte[]> result = api.convert("fatturapa" , "ubl" , new FileInputStream(invoice));
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
        Stream.of(testInvoices).filter(new Filter<File>() {
            @Override
            public boolean apply(File file) {
                return file.getName().startsWith("ubl" );
            }
        }).forEach(new Consumer<File>() {
            @Override
            public void consume(File invoice) {
                try {
                    final ConversionResult<byte[]> result = api.convert("ubl" , "fatturapa" , new FileInputStream(invoice));
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
    public void shouldConvertACSVDumpToCSVCenAndFatturaPA() throws Exception {
        Stream.of(testInvoices).filter(new Filter<File>() {
            @Override
            public boolean apply(File file) {
                return file.getName().startsWith("ubl");
            }
        }).forEach(new Consumer<File>() {
            @Override
            public void consume(File invoice) {
                try {
                    final ConversionResult<byte[]> _ = api.convert("ubl" , "fatturapa" , new FileInputStream(invoice));
                    File[] tempFiles = tmp.getRoot().listFiles();
                    tempFiles = tempFiles != null ? tempFiles : new File[]{};
                    final File invoiceCen = findInvoiceCen(tempFiles, null);
                    assertNotNull(invoiceCen);
                    final FileInputStream cenIs = new FileInputStream(invoiceCen);
                    final ConversionResult<byte[]> result = api.convert("csvcen" , "fatturapa" , cenIs);

                    assertTrue(result.isSuccessful());
                    assertFalse(result.hasIssues());
                } catch (FileNotFoundException e) {
                    fail();
                }

            }
        });
    }

    public static String getFileContent(
            File file,
            String encoding) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        } finally {
            fis.close();
        }
    }

    private File findInvoiceCen(final File[] files, final List<File> filesFound) {
        final List<File> _t = filesFound != null ? filesFound : new ArrayList<File>();
        for (File tempFile : files) {
            if (tempFile.isDirectory()) {
                findInvoiceCen(tempFile.listFiles(), _t);
            } else {
                final String name = tempFile.getName();
                if (name.contains("invoice-cen" )) {
                    _t.add(tempFile);
                }
            }
        }

        return _t.isEmpty() ? null : _t.get(0);
    }

    private void navigate(final File[] files, final List<File> found) {
        for (File tempFile : files) {
            if (tempFile.isDirectory()) {
                navigate(tempFile.listFiles(), found);
            } else {
                final String name = tempFile.getName();
                if (name.contains("invoice-cen" ) || name.contains("invoice-source" )) {
                    found.add(tempFile);
                }
            }
        }
    }


    @Test
    public void shouldConvertACSVDumpToCSVCenAndUbl() throws Exception {

    }
}
