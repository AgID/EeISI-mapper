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

import org.codehaus.plexus.util.DirectoryScanner;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.io.OutputStream;


/**
 * The type Schematron to XSLT file updater.
 */
class SchematronXSLTFileUpdater {
    private final File xsltDirectory;
    private final File schematronDirectory;
    private final String schematronPattern;
    private final String xsltPattern;
    private final String xsltExtension;

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
        xsltPattern = "*.xslt";
        xsltExtension = ".xslt";

        if (this.xsltDirectory.exists() && !this.xsltDirectory.isDirectory())
            throw new RuntimeException("The specified XSLT directory " + xsltDirectory + " is not a directory!");
        if (!this.xsltDirectory.exists() && !this.xsltDirectory.mkdirs())
            throw new RuntimeException("Failed to create the XSLT directory " + xsltDirectory);
    }

    /**
     * Check for updated schematron.
     *
     * @return TRUE if any sch file has a newer timestamp than any XSLT file
     */
    boolean checkForUpdatedSchematron() {
        long xsltLastModifiedTimestamp = 0;
        long schLastModifiedTimestamp = 0;


        // Find highest (most recent) last modified timestamp file in XSLT directory
        final String[] xsltFilenames = getXSLTFileList();
        if (xsltFilenames != null) {
            xsltLastModifiedTimestamp = getLatestModifiedTimestamp(xsltFilenames, xsltDirectory);
        }

        // Find highest (most recent) last modified timestamp file in Schematron directory
        final String[] schFilenames = getSchematronFileList();
        if (schFilenames != null) {
            schLastModifiedTimestamp = getLatestModifiedTimestamp(schFilenames, schematronDirectory);
        }

        // if there is a more recent timestamped Schematron file, regeneration of XSLT is needed.
        return schLastModifiedTimestamp > xsltLastModifiedTimestamp;
    }


    private String[] getXSLTFileList() {
        return getDirectoryFileList(xsltDirectory, xsltPattern);
    }

    private String[] getSchematronFileList() {
        return getDirectoryFileList(schematronDirectory, schematronPattern);
    }

    private String[] getDirectoryFileList(File baseDirectory, String pattern) {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(baseDirectory);
        scanner.setIncludes(new String[]{pattern});
        scanner.setCaseSensitive(true);
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    private long getLatestModifiedTimestamp(String[] fileList, File directory) {
        long highestLastModified = 0;
        for (final String fileName : fileList) {
            final File aFile = new File(directory, fileName);
            long lastModified = aFile.lastModified();
            if (lastModified > highestLastModified) {
                highestLastModified = lastModified;
            }
        }
        return highestLastModified;
    }


    /**
     * Start generation of XSLT from Sch files.
     */
    void updateXSLTfromSch() {


        // Find all Schematron files
        final String[] schFilenames = getSchematronFileList();
        if (schFilenames != null) {
            // TODO MAYBE use a separate thread for each file
            for (final String schFilename : schFilenames) {
                final File schFile = new File(schematronDirectory, schFilename);

                // Create XSLT file name (outputdir + localpath + extension)
                final File xsltFile = new File(xsltDirectory, FilenameHelper.getWithoutExtension(schFilename) + xsltExtension);

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

                        final OutputStream xsltOS = FileHelper.getOutputStream(xsltFile);
                        if (xsltOS == null)
                            throw new IllegalStateException("Failed to open output stream for file " +
                                    xsltFile.getAbsolutePath());
                        XMLWriter.writeToStream(xsltProvider.getXSLTDocument(), xsltOS, xmlWriterSettings);
                    } else {
                        throw new RuntimeException("Failed to convert '" + schFile.getPath() +
                                "': the Schematron resource is invalid");
                    }
                } catch (final Exception ex) {
                    throw new RuntimeException("Failed to convert '" + schFile.getPath() + "' to XSLT file '" +
                            xsltFile.getPath() + "'", ex);
                }
            }
        }
    }
}
