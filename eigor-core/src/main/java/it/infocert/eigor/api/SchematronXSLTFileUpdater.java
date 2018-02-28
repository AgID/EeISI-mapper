package it.infocert.eigor.api;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.xml.CXML;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;
import com.helger.commons.xml.serialize.write.XMLWriter;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;
import com.helger.schematron.svrl.CSVRL;
import com.helger.schematron.xslt.ISchematronXSLTBasedProvider;
import com.helger.schematron.xslt.SCHTransformerCustomizer;
import com.helger.schematron.xslt.SchematronResourceSCHCache;

import it.infocert.eigor.api.errors.ErrorCode;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * The type Schematron to XSLT file updater.
 */
class SchematronXSLTFileUpdater {
    private final static Logger log = LoggerFactory.getLogger(SchematronXSLTFileUpdater.class);

    private final File xsltDirectory;
    private final File schematronDirectory;
    private final String schematronPattern;
    private final String xsltPattern;
    private final String xsltExtension;
    private final String schematronExtension;

    /**
     * Instantiates a new Schematron XSLT file updater.
     *
     * @param xsltDirectory       the xslt directory
     * @param schematronDirectory the schematron directory
     */
    SchematronXSLTFileUpdater(String xsltDirectory, String schematronDirectory) {
        this.xsltDirectory = new File(xsltDirectory);
        this.schematronDirectory = new File(schematronDirectory);
        schematronPattern = "*.sch";
        schematronExtension = "sch";
        xsltPattern = "xslt";
        xsltExtension = ".xslt";

        if (this.xsltDirectory.exists() && !this.xsltDirectory.isDirectory())
            throw new RuntimeException("The specified XSLT directory " + xsltDirectory + " is not a directory!");
        if (!this.xsltDirectory.exists() && !this.xsltDirectory.mkdirs())
            throw new RuntimeException("Failed to create the XSLT directory " + xsltDirectory);
    }

    private String[] getFirstLevelSchematronFileList() {
        return getFirstDirectoryFileList(schematronDirectory, schematronPattern);
    }


    private List<File> getDirectoryFileList(File baseDirectory, String format) {
        return (List<File>) FileUtils.listFiles(baseDirectory, new String[]{format}, true);
    }

    private String[] getFirstDirectoryFileList(File baseDirectory, String pattern) {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(baseDirectory);
        scanner.setIncludes(new String[]{pattern});
        scanner.setCaseSensitive(true);
        scanner.scan();

        return scanner.getIncludedFiles();
    }

    /**
     * Attempt regeneration of XSLT from Sch files.
     *
     * @return number of XSLT files updated
     */
    int updateXSLTfromSch() {
        int count = 0;

        // Find all Schematron files
        final String[] schFilenames = getFirstLevelSchematronFileList();
        if (schFilenames != null) {
            for (final String schFilename : schFilenames) {
                boolean updateXSLT = false;

                final File schFile = new File(schematronDirectory, schFilename);

                // Create XSLT file name (outputdir + localpath + extension)
                final File xsltFile = new File(xsltDirectory, FilenameHelper.getWithoutExtension(schFilename) + xsltExtension);

                if (!xsltFile.exists()) {
                    log.info("XSLT file '" + xsltFile.getPath() + "' does not exist and will be created!");
                    updateXSLT = true;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS dd-MM-yyyy");
                    Date schFileDate = new Date(schFile.getAbsoluteFile().lastModified());
                    Date xsltFileDate = new Date(xsltFile.getAbsoluteFile().lastModified());
                    if (FileHelper.isFileNewer(schFile, xsltFile)) {
                        String diffText = dateDifferenceAsText(xsltFileDate, schFileDate);
                        log.info("Schematron file '" +
                                schFile.getPath() +
                                "' [" + sdf.format(schFileDate) + "] is " +
                                diffText + " newer than XSLT file '" +
                                xsltFile.getPath() +
                                "' [" + sdf.format(xsltFileDate) + "] and will overwrite it!");
                        updateXSLT = true;
                    } else {
                        String diffText = dateDifferenceAsText(schFileDate, xsltFileDate);
                        log.info("Schematron file '" +
                                schFile.getPath() +
                                "' [" + sdf.format(schFileDate) + "] is " +
                                diffText + " older than XSLT file '" +
                                xsltFile.getPath() +
                                "' [" + sdf.format(xsltFileDate) + "] and will be skipped!");
                    }
                }

                if (updateXSLT) {
                    final IReadableResource aSchematronResource = new FileSystemResource(schFile);

                    // Create the directory if necessary
                    final File xsltFileDirectory = xsltFile.getParentFile();
                    if (xsltFileDirectory != null && !xsltFileDirectory.exists()) {
                        if (!xsltFileDirectory.mkdirs()) {
                            throw new RuntimeException("Failed to convert '" + schFile.getPath() + "' because directory '" +
                                    xsltFileDirectory.getPath() + "' could not be created");
                        }
                    }

                    // Write the XSLT file
                    try {

                        final ISchematronXSLTBasedProvider xsltProvider = SchematronResourceSCHCache.createSchematronXSLTProvider(aSchematronResource,
                                new SCHTransformerCustomizer());
                        if (xsltProvider != null) {
                            // Write the resulting XSLT file to disk
                            final MapBasedNamespaceContext aNSContext = new MapBasedNamespaceContext()
                                    .addMapping("svrl", CSVRL.SVRL_NAMESPACE_URI);
                            // Add all namespaces from XSLT document root
                            final String sNSPrefix = CXML.XML_ATTR_XMLNS + ":";

                            Element documentElement = xsltProvider.getXSLTDocument().getDocumentElement();
                            NamedNodeMap attributes = documentElement.getAttributes();
                            for (int i = 0; i < attributes.getLength(); i++) {
                                Attr item = (Attr) attributes.item(i);
                                String sAttrName = item.getName();
                                String sAttrValue = item.getValue();
                                if (sAttrName.startsWith(sNSPrefix)) {
                                    aNSContext.addMapping(sAttrName.substring(sNSPrefix.length()),
                                            sAttrValue);
                                }
                            }

                            final XMLWriterSettings xmlWriterSettings = new XMLWriterSettings();
                            xmlWriterSettings.setNamespaceContext(aNSContext).setPutNamespaceContextPrefixesInRoot(true);

                            try (final OutputStream xsltOS = FileHelper.getOutputStream(xsltFile)) {
                                if (xsltOS == null)
                                    throw new IllegalStateException("Failed to open output stream for file " +
                                            xsltFile.getAbsolutePath());
                                XMLWriter.writeToStream(xsltProvider.getXSLTDocument(), xsltOS, xmlWriterSettings);
                            }
                        } else {
                            throw new RuntimeException("Failed to convert '" + schFile.getPath() +
                                    "': the Schematron resource is invalid");
                        }
                    } catch (final Exception ex) {
                        throw new RuntimeException("Failed to convert '" + schFile.getPath() + "' to XSLT file '" +
                                xsltFile.getPath() + "'", ex);
                    }
                    count++;
                }
            }
        }
        return count;
    }

    private String dateDifferenceAsText(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays > 0) return (diffDays + " days");
        if (diffHours > 0) return (diffHours + " hours");
        if (diffMinutes > 0) return (diffMinutes + " minutes");
        return (diffSeconds + " seconds");
    }
}
