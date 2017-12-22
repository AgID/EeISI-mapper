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
                .enableForce()
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
        Stream.of(testInvoices).filter(new Filter<File>() {
            @Override
            public boolean apply(File file) {
                return file.getName().startsWith("ubl");
            }
        }).forEach(new Consumer<File>() {
            @Override
            public void consume(File invoice) {
                try {
                    final ConversionResult<byte[]> result = api.convert("ubl", "fatturapa", new FileInputStream(invoice));
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
                    final ConversionResult<byte[]> result = api.convert("ubl", "fatturapa", new FileInputStream(invoice));
                    File[] tempFiles = tmp.getRoot().listFiles();
                    tempFiles = tempFiles != null ? tempFiles : new File[]{};
                    final File invoiceCen = findInvoiceCen(tempFiles, null);
                    assertNotNull(invoiceCen);
                    final FileInputStream cenIs = new FileInputStream(invoiceCen);

                    final ConversionResult<byte[]> results = api.convert("csvcen", "fatturapa", cenIs);

                    File[] tempFiles2 = tmp.getRoot().listFiles();
                    tempFiles2 = tempFiles2 != null ? tempFiles2 : new File[]{};
                    final ArrayList<File> files = Lists.newArrayList();
                    navigate(tempFiles2, files);
                    final List<File> invoiceCens = Stream.create(files).filter(new Filter<File>() {
                        @Override
                        public boolean apply(File file) {
                            return file.getName().contains("invoice-cen");
                        }
                    }).toList();
                    final List<File> sources = Stream.create(files).filter(new Filter<File>() {
                        @Override
                        public boolean apply(File file) {
                            return file.getName().contains("invoice-source");
                        }
                    }).toList();

                    for (File cen : invoiceCens) {
                        for (File source : sources) {
                            try {
                                final String cenContent = getFileContent(new FileInputStream(cen), "UTF-8");
                                final String sourceContent = getFileContent(new FileInputStream(source), "UTF-8");
                                final boolean equals = Objects.equals(cenContent, sourceContent);
                                final boolean parentEquals = Objects.equals(cen.getParent(), source.getParent());
                                System.out.println(cen.getParent());
                                System.out.println(source.getParent());
                                if (equals) {
                                    System.out.println(parentEquals);
                                    System.out.println(cen.getAbsolutePath());
                                    System.out.println(cenContent);
                                    System.out.println(source.getAbsolutePath());
                                    System.out.println(sourceContent);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    fail();
                }

            }
        });
    }

    public static String getFileContent(
            FileInputStream fis,
            String          encoding ) throws IOException
    {
        try( BufferedReader br =
                     new BufferedReader( new InputStreamReader(fis, encoding )))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            return sb.toString();
        }
    }

    private File findInvoiceCen(final File[] files, final List<File> filesFound) {
        final List<File> _t = filesFound != null ? filesFound : new ArrayList<File>();
        for (File tempFile : files) {
            if (tempFile.isDirectory()) {
                findInvoiceCen(tempFile.listFiles(), _t);
            } else {
                final String name = tempFile.getName();
                if (name.contains("invoice-cen")) {
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
                if (name.contains("invoice-cen") || name.contains("invoice-source")) {
                    found.add(tempFile);
                }
            }
        }
    }


    @Test
    public void shouldConvertACSVDumpToCSVCenAndUbl() throws Exception {

    }
}
