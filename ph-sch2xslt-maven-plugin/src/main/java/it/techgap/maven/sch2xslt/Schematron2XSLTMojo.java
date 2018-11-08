/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.techgap.maven.sch2xslt;

import com.helger.commons.error.IResourceError;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.xml.CXML;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;
import com.helger.commons.xml.serialize.write.XMLWriter;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;
import com.helger.commons.xml.transform.AbstractTransformErrorListener;
import com.helger.schematron.svrl.CSVRL;
import com.helger.schematron.xslt.ISchematronXSLTBasedProvider;
import com.helger.schematron.xslt.SCHTransformerCustomizer;
import com.helger.schematron.xslt.SchematronResourceSCHCache;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.slf4j.impl.StaticLoggerBinder;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import javax.xml.transform.ErrorListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Converts one or more Schematron schema files into XSLT scripts.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 * @goal convert
 * @phase generate-resources
 */
public final class Schematron2XSLTMojo extends AbstractMojo {

    public final class PluginErrorListener extends AbstractTransformErrorListener {
        @Override
        protected void internalLog(@Nonnull final IResourceError aResError) {
            if (aResError.isError())
                getLog().error(aResError.getAsString(Locale.US), aResError.getLinkedException());
            else
                getLog().warn(aResError.getAsString(Locale.US), aResError.getLinkedException());
        }
    }

    /**
     * The Maven Project.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    @SuppressFBWarnings({"NP_UNWRITTEN_FIELD", "UWF_UNWRITTEN_FIELD"})
    private MavenProject project;

    /**
     * The directory where the Schematron files reside.
     *
     * @parameter property="schematronDirectory"
     * default="${basedir}/src/main/schematron"
     */
    private File schematronDirectory;

    /**
     * A pattern for the Schematron files. Can contain Ant-style wildcards and
     * double wildcards. All files that match the pattern will be converted. Files
     * in the schematronDirectory and its subdirectories will be considered.
     *
     * @parameter property="schematronPattern" default-value="**\/*.sch"
     */
    private String schematronPattern;

    /**
     * The directory where the XSLT files will be saved.
     *
     * @required
     * @parameter property="xsltDirectory" default="${basedir}/src/main/xslt"
     */
    private File xsltDirectory;

    /**
     * The file extension of the created XSLT files.
     *
     * @parameter property="xsltExtension" default-value=".xslt"
     */
    private String xsltExtension;

    /**
     * Overwrite existing Schematron files without notice? If this is set to
     * <code>true</code> than existing XSLT files are overwritten.
     *
     * @parameter property="overwrite" default-value="false"
     */
    private boolean overwriteWithoutQuestion = false;

    /**
     * Define the phase to be used for XSLT creation. By default the
     * <code>defaultPhase</code> attribute of the Schematron file is used.
     *
     * @parameter property="phaseName"
     */
    private String phaseName;

    /**
     * Define the language code for the XSLT creation. Default is English.
     * Supported language codes are: cs, de, en, fr, nl.
     *
     * @parameter property="languageCode"
     */
    private String languageCode;

    /**
     * Update the XSLT if the Schematron changes? If set to <code>false</code>
     * they will not be updated even if modified.
     *
     * @parameter property="updateOnSchematronChanges" default-value="false"
     */
    private boolean updateOnSchematronChanges = false;

    public void setUpdateOnSchematronChanges(final boolean updateOnSchematronChanges) {
        this.updateOnSchematronChanges = updateOnSchematronChanges;
        getLog().debug("Updating Schematron if modified");
    }

    public void setSchematronDirectory(final File aDir) {
        schematronDirectory = aDir;
        if (!schematronDirectory.isAbsolute())
            schematronDirectory = new File(project.getBasedir(), aDir.getPath());
        getLog().debug("Searching Schematron files in the directory '" + schematronDirectory + "'");
    }

    public void setSchematronPattern(final String sPattern) {
        schematronPattern = sPattern;
        getLog().debug("Setting Schematron pattern to '" + sPattern + "'");
    }

    public void setXsltDirectory(final File aDir) {
        xsltDirectory = aDir;
        if (!xsltDirectory.isAbsolute())
            xsltDirectory = new File(project.getBasedir(), aDir.getPath());
        getLog().debug("Writing XSLT files into directory '" + xsltDirectory + "'");
    }

    public void setXsltExtension(final String sExt) {
        xsltExtension = sExt;
        getLog().debug("Setting XSLT file extension to '" + sExt + "'");
    }

    public void setOverwriteWithoutQuestion(final boolean bOverwrite) {
        overwriteWithoutQuestion = bOverwrite;
        if (overwriteWithoutQuestion)
            getLog().debug("Overwriting XSLT files without notice");
        else
            getLog().debug("Ignoring existing Schematron files");
    }

    public void setPhaseName(final String sPhaseName) {
        phaseName = sPhaseName;
        if (phaseName == null)
            getLog().debug("Using default phase");
        else
            getLog().debug("Using the phase '" + phaseName + "'");
    }

    public void setLanguageCode(final String sLanguageCode) {
        languageCode = sLanguageCode;
        if (languageCode == null)
            getLog().debug("Using default language code");
        else
            getLog().debug("Using the language code '" + languageCode + "'");
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        StaticLoggerBinder.getSingleton().setMavenLog(getLog());
        if (schematronDirectory == null)
            throw new MojoExecutionException("No Schematron directory specified!");
        if (schematronDirectory.exists() && !schematronDirectory.isDirectory())
            throw new MojoExecutionException("The specified Schematron directory " +
                    schematronDirectory +
                    " is not a directory!");
        if (schematronPattern == null || schematronPattern.isEmpty()) {
            throw new MojoExecutionException("No Schematron pattern specified!");
        }
        if (xsltDirectory == null)
            throw new MojoExecutionException("No XSLT directory specified!");
        if (xsltDirectory.exists() && !xsltDirectory.isDirectory())
            throw new MojoExecutionException("The specified XSLT directory " + xsltDirectory + " is not a directory!");
        if (xsltExtension == null || xsltExtension.length() == 0 || !xsltExtension.startsWith("."))
            throw new MojoExecutionException("The XSLT extension '" + xsltExtension + "' is invalid!");

        if (!xsltDirectory.exists() && !xsltDirectory.mkdirs())
            throw new MojoExecutionException("Failed to create the XSLT directory " + xsltDirectory);


        // for all Schematron files that match the pattern
        final DirectoryScanner aScanner = new DirectoryScanner();
        aScanner.setBasedir(schematronDirectory);
        aScanner.setIncludes(new String[]{schematronPattern});
        aScanner.setCaseSensitive(true);
        aScanner.scan();
        final String[] aFilenames = aScanner.getIncludedFiles();
        if (aFilenames != null) {
            for (final String sFilename : aFilenames) {
                final File aFile = new File(schematronDirectory, sFilename);

                // 1. build XSLT file name (outputdir + localpath with new extension)
                final File aXSLTFile = new File(xsltDirectory, FilenameHelper.getWithoutExtension(sFilename) + xsltExtension);

                // 2. The Schematron resource
                final IReadableResource aSchematronResource = new FileSystemResource(aFile);

                boolean compileXSLT = false;
                // 3. Check if the XSLT file already exists
                if (aXSLTFile.exists()) {
                    if (overwriteWithoutQuestion) {
                        getLog().info("XSLT file '" + aXSLTFile.getPath() + "' already exists and will be overwritten!");
                        compileXSLT = true;
                    } else if (updateOnSchematronChanges) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS dd-MM-yyyy");
                        Date aFileDate = new Date(aFile.getAbsoluteFile().lastModified());
                        Date aXSLTFileDate = new Date(aXSLTFile.getAbsoluteFile().lastModified());
                        if (FileHelper.isFileNewer(aFile, aXSLTFile)) {
                            String diffText = dateDifferenceAsText(aXSLTFileDate, aFileDate);
                            getLog().info("Schematron file '" +
                                    aFile.getPath() +
                                    "' [" + sdf.format(aFileDate) + "] is " +
                                    diffText + " newer than XSLT file '" +
                                    aXSLTFile.getPath() +
                                    "' [" + sdf.format(aXSLTFileDate) + "] and will overwrite it!");
                            compileXSLT = true;
                        } else {
                            String diffText = dateDifferenceAsText(aFileDate, aXSLTFileDate);
                            getLog().info("Schematron file '" +
                                    aFile.getPath() +
                                    "' [" + sdf.format(aFileDate) + "] is " +
                                    diffText + " older than XSLT file '" +
                                    aXSLTFile.getPath() +
                                    "' [" + sdf.format(aXSLTFileDate) + "] and will be skipped!");
                        }
                    } else {
                        getLog().info("Skipping XSLT file '" + aXSLTFile.getPath() + "' because it already exists!");
                    }
                } else {
                    getLog().info("XSLT file '" + aXSLTFile.getPath() + "' does not exist and will be created!");
                    compileXSLT = true;
                }

                if (compileXSLT) {
                    // 3.2 Create the directory, if necessary
                    final File aXsltFileDirectory = aXSLTFile.getParentFile();
                    if (aXsltFileDirectory != null && !aXsltFileDirectory.exists()) {
                        getLog().debug("Creating directory '" + aXsltFileDirectory.getPath() + "'");
                        if (!aXsltFileDirectory.mkdirs()) {
                            final String message = "Failed to convert '" +
                                    aFile.getPath() +
                                    "' because directory '" +
                                    aXsltFileDirectory.getPath() +
                                    "' could not be created";
                            getLog().error(message);
                            throw new MojoFailureException(message);
                        }
                    }
                    // 3.3 Okay, write the XSLT file
                    try {
                        // Custom error listener to log to the Mojo logger
                        final ErrorListener aMojoErrorListener = new PluginErrorListener();

                        // Custom error listener
                        // No custom URI resolver
                        // Specified phase - default = null
                        // Specified language code - default = null
                        final ISchematronXSLTBasedProvider aXsltProvider = SchematronResourceSCHCache.createSchematronXSLTProvider(aSchematronResource,
                                new SCHTransformerCustomizer().setErrorListener(aMojoErrorListener)
                                        .setPhase(phaseName)
                                        .setLanguageCode(languageCode));
                        if (aXsltProvider != null) {
                            // Add namespace prefixes
                            final MapBasedNamespaceContext aNSContext = new MapBasedNamespaceContext();
                            aNSContext.addMapping("svrl", CSVRL.SVRL_NAMESPACE_URI);
                            final String sNSPrefix = CXML.XML_ATTR_XMLNS + ":";

                            NamedNodeMap attributesMap = aXsltProvider.getXSLTDocument().getDocumentElement().getAttributes();
                            for (int i = 0; i < attributesMap.getLength(); i++) {
                                Node item = attributesMap.item(i);
                                String nodeName = item.getNodeName();
                                String nodeValue = item.getNodeValue();

                                if (nodeName.startsWith(sNSPrefix))
                                    aNSContext.addMapping(nodeName.substring(sNSPrefix.length()), nodeValue);
                            }

                            final XMLWriterSettings xmlWriterSettings = new XMLWriterSettings();
                            xmlWriterSettings.setNamespaceContext(aNSContext);
                            xmlWriterSettings.setPutNamespaceContextPrefixesInRoot(true);

                            // Write the resulting XSLT file to disk
                            XMLWriter.writeToStream(aXsltProvider.getXSLTDocument(), FileHelper.getOutputStream(aXSLTFile), xmlWriterSettings);
                        } else {
                            final String message = "Failed to convert '" + aFile.getPath() + "': the Schematron resource is invalid";
                            getLog().error(message);
                            throw new MojoFailureException(message);
                        }
                    } catch (final MojoFailureException up) {
                        throw up;
                    } catch (final Exception ex) {
                        final String message = "Failed to convert '" +
                                aFile.getPath() +
                                "' to XSLT file '" +
                                aXSLTFile.getPath() +
                                "'";
                        getLog().error(message, ex);
                        throw new MojoFailureException(message, ex);
                    }
                }
            }
        }
    }

    private List<File> getXSLTFileList() {
        return getDirectoryFileList(xsltDirectory, "xslt");
    }

    private List<File> getSchematronFileList() {
        return getDirectoryFileList(schematronDirectory, "sch");
    }

    private List<File> getDirectoryFileList(File baseDirectory, String format) {
        return (List<File>) FileUtils.listFiles(baseDirectory, new String[]{format}, true);
    }

    private String dateDifferenceAsText(Date d1, Date d2) {
        StringBuilder sb = new StringBuilder();
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays > 0) sb.append(diffDays).append("D ");
        if (diffHours > 0) sb.append(diffHours).append("H ");
        if (diffMinutes > 0) sb.append(diffMinutes).append("m ");
        sb.append(diffSeconds).append("s");

        return sb.toString();
    }
}
