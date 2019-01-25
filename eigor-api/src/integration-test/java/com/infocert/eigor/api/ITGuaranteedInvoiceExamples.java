package com.infocert.eigor.api;


import com.google.common.base.Preconditions;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.*;

public class ITGuaranteedInvoiceExamples {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    static List<File> testInvoices = new ArrayList<>();
    EigorApi api;

    @BeforeClass
    public static void setUpTestInvoices() throws IOException {

        final ClassLoader classLoader = ITGuaranteedInvoiceExamples.class.getClassLoader();
        final URL resource = classLoader.getResource("working-examples" );
        assert resource != null;

        final File examplesDirectory = new File(resource.getFile());
        if (examplesDirectory.isDirectory()) {
            final File[] files = Preconditions.checkNotNull(examplesDirectory.listFiles(), "No files found in resources/working-examples" );
            testInvoices = Arrays.stream(files).filter(file -> file.getName().endsWith(".xml" )).collect(toList());
        }

    }

    @Before
    public void setUpAPI() throws Exception {

        assumeWorkdirExists();

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
        testInvoices.stream().filter(file -> file.getName().startsWith("fattpa" )).forEach(invoice -> {
            try {
                final ConversionResult<byte[]> result = api.convert("fatturapa" , "ubl" , new FileInputStream(invoice));

                String original = FileUtils.readFileToString(invoice, "UTF-8");
                String converted = new String( result.getResult() );

                assertFalse("original\n\n" + original + "\n\nconverted\n\n" + converted + "\n\nissues\n\n" + result.getIssues().toString().replaceAll(",", "\n"), result.hasIssues());
                assertTrue(result.isSuccessful());
                assertTrue(result.hasResult());
            } catch (Exception e) {
                fail(e.toString());
            }
        });


    }

    @Test
    public void shouldConvertUblExampleWithoutErrors() throws Exception {
        testInvoices.stream().filter(file -> file.getName().startsWith("ubl" )).forEach(new Consumer<File>() {
            @Override
            public void accept(File invoice) {
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

        List<File> ublFiles = testInvoices.stream()
                .filter(file -> file.getName().startsWith("ubl"))
                .collect(toList());


        for (File invoice : ublFiles) {
            try {
                final ConversionResult<byte[]> unused = api.convert("ubl" , "fatturapa" , new FileInputStream(invoice));

                File[] tempFiles = tmp.getRoot().listFiles();
                tempFiles = tempFiles != null ? tempFiles : new File[]{};

                final File invoiceCen = findInvoiceCen(tempFiles, null);

                String message = "Not found in " + tempFiles;
                assertNotNull(message, invoiceCen);

                final FileInputStream cenIs = new FileInputStream(invoiceCen);
                final ConversionResult<byte[]> result = api.convert("csvcen" , "fatturapa" , cenIs);

                assertTrue(result.isSuccessful());
                assertFalse(result.hasIssues());
            } catch (FileNotFoundException e) {
                fail();
            }
        }

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

    /** Recursively find files which name contains 'invoice-cen'. */
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

    private static void assumeWorkdirExists() {
        File workdir = new File(DefaultEigorConfigurationLoader.configuration().getMandatoryString("eigor.workdir"));
        Assume.assumeTrue(String.format("Workdir at '%s' not available here.", workdir.getAbsolutePath()), workdir.exists() && workdir.isDirectory());
    }

}
