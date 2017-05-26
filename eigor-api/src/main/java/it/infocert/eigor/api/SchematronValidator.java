package it.infocert.eigor.api;

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
        if (isXSLT) {
            // we check if relative path ../schematron contains newer .sch files
            SchematronXSLTFileUpdater xsltFileUpdater = new SchematronXSLTFileUpdater(
                    schemaFile.getParent(),
                    schemaFile.getAbsoluteFile().getParentFile().getParent() + "/schematron"            );

            if (xsltFileUpdater.checkForUpdatedSchematron()) {
                xsltFileUpdater.updateXSLTfromSch();
            }
            schematronResource = SchematronResourceXSLT.fromFile(schemaFile);
        } else {
            schematronResource = SchematronResourceSCH.fromFile(schemaFile);
        }
        if (!schematronResource.isValidSchematron())
            throw new IllegalArgumentException("Invalid Schematron!");
    }

    @Override
    public List<Exception> validate(byte[] xml) {
        List<Exception> errors = new ArrayList<>();
        SchematronOutputType schematronOutput = null;

        try {
            schematronOutput = schematronResource.applySchematronValidationToSVRL(new StreamSource(new ByteArrayInputStream(xml)));
        } catch (Exception e) {
            errors.add(e);
            return errors;
        }

        List<Object> firedRuleAndFailedAssert = schematronOutput.getActivePatternAndFiredRuleAndFailedAssert();
        for (Object obj : firedRuleAndFailedAssert) {
            if (obj instanceof FailedAssert) {
                FailedAssert failedAssert = (FailedAssert) obj;
                Exception cause = new Exception(failedAssert.getLocation() + " failed test: " + failedAssert.getTest());
                Exception error = new Exception("Schematron failed assert:" + failedAssert.getText(), cause);
                errors.add(error);
            }
        }
        return errors;
    }
}
