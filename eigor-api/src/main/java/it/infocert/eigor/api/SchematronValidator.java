package it.infocert.eigor.api;

import com.google.common.base.Preconditions;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.xslt.SchematronResourceSCH;
import com.helger.schematron.xslt.SchematronResourceXSLT;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchematronValidator implements IXMLValidator {

    private ISchematronResource schematronResource;

    public SchematronValidator(File schemaFile, boolean isXSLT) {

        Preconditions.checkArgument(schemaFile != null, "Provide a Schematron file.");
        Preconditions.checkArgument(schemaFile.exists(), "Schematron file '%s' (resolved to absolute path '%s') does not exist.", schemaFile.getPath(), schemaFile.getAbsolutePath());

        if (isXSLT) {
            // we check if relative path ../schematron contains newer .sch files
            SchematronXSLTFileUpdater xsltFileUpdater = new SchematronXSLTFileUpdater(
                    schemaFile.getParent(),
                    schemaFile.getAbsoluteFile().getParentFile().getParent() + "/schematron");

            if (xsltFileUpdater.checkForUpdatedSchematron()) {
                xsltFileUpdater.updateXSLTfromSch();
            }
            schematronResource = SchematronResourceXSLT.fromFile(schemaFile);
        } else {
            schematronResource = SchematronResourceSCH.fromFile(schemaFile);
        }
        if (!schematronResource.isValidSchematron())
            throw new IllegalArgumentException(String.format("Invalid %s Schematron file '%s' (resolved to absolute path '%s').", isXSLT?"XSLT":"SCH", schemaFile, schemaFile.getAbsolutePath()));
    }

    @Override
    public List<ConversionIssue> validate(byte[] xml) {
        List<ConversionIssue> errors = new ArrayList<>();
        SchematronOutputType schematronOutput = null;

        try {
            StreamSource source = new StreamSource(new ByteArrayInputStream(xml));
            schematronOutput = schematronResource.applySchematronValidationToSVRL(source);
        } catch (Exception e) {
            errors.add(ConversionIssue.newWarning(e));
            return errors;
        }


        List<Object> firedRuleAndFailedAssert = new ArrayList<>();

        if(schematronOutput!=null) {
            try {
                firedRuleAndFailedAssert.addAll(schematronOutput.getActivePatternAndFiredRuleAndFailedAssert());
            } catch (Exception e) {
                errors.add(ConversionIssue.newError(e));
                return errors;
            }
        }

        for (Object obj : firedRuleAndFailedAssert) {
            if (obj instanceof FailedAssert) {
                FailedAssert failedAssert = (FailedAssert) obj;

                Exception cause = new Exception(
                        failedAssert.getLocation() + " failed test: " + failedAssert.getTest()
                );

                String ruleDescriptionFromSchematron = failedAssert.getText().trim().replaceAll("\\n", " ").replaceAll(" {2,}", " ");
                String offendingElement = failedAssert.getLocation().trim();
                Exception error = new Exception(
                        String.format("Schematron failed assert '%s' on XML element at '%s'.", ruleDescriptionFromSchematron, offendingElement), cause);

                if (failedAssert.getFlag().equals("fatal")) {
                    errors.add(ConversionIssue.newError(error));
                } else {
                    errors.add(ConversionIssue.newWarning(error));
                }
            }
        }
        return errors;
    }
}
