package it.infocert.eigor.api;

import com.google.common.base.Preconditions;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.xslt.SchematronResourceSCH;
import com.helger.schematron.xslt.SchematronResourceXSLT;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.schematron.FixedSchematronResource;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchematronValidator implements IXMLValidator {

    private final ErrorCode.Location callingLocation;
    private ISchematronResource schematronResource;
    private static final Logger log = LoggerFactory.getLogger(SchematronValidator.class);

    public SchematronValidator(File schemaFile, boolean isXSLT, ErrorCode.Location callingLocation) {
        this.callingLocation = callingLocation;

        long delta = System.currentTimeMillis();
        try {
            Preconditions.checkArgument(schemaFile != null, "Provide a Schematron file.");
            Preconditions.checkArgument(schemaFile.exists(), "Schematron file '%s' (resolved to absolute path '%s') does not exist.", schemaFile.getPath(), schemaFile.getAbsolutePath());

            if (isXSLT) {
                // we check if relative path ../schematron contains newer .sch files
                SchematronXSLTFileUpdater xsltFileUpdater = new SchematronXSLTFileUpdater(
                        schemaFile.getParent(),
                        schemaFile.getAbsoluteFile().getParentFile().getParent() + "/schematron");

                if (xsltFileUpdater.isSchNewerThanXslt()) {
                    xsltFileUpdater.updateXSLTfromSch();
                }
                schematronResource = new FixedSchematronResource(SchematronResourceXSLT.fromFile(schemaFile));
            } else {
                schematronResource = new FixedSchematronResource(SchematronResourceSCH.fromFile(schemaFile));
            }
            if (!schematronResource.isValidSchematron())
                throw new IllegalArgumentException(String.format("Invalid %s Schematron file '%s' (resolved to absolute path '%s').", isXSLT ? "XSLT" : "SCH", schemaFile, schemaFile.getAbsolutePath()));
        } finally {
            delta = System.currentTimeMillis() - delta;
            log.info(MarkerFactory.getMarker("PERFORMANCE"), "Loaded '{}' in {}ms.", schemaFile.getAbsolutePath(), delta);
        }

    }

    @Override
    public List<IConversionIssue> validate(byte[] xml) {
        List<IConversionIssue> errors = new ArrayList<>();
        SchematronOutputType schematronOutput;

        try {
            StreamSource source = new StreamSource(new ByteArrayInputStream(xml));
            schematronOutput = schematronResource.applySchematronValidationToSVRL(source);
        } catch (Exception e) {
            errors.add(ConversionIssue.newWarning(e, "Error during Schematron Validation.",
                    callingLocation,
                    ErrorCode.Action.SCH_VALIDATION,
                    ErrorCode.Error.INVALID,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));
            return errors;
        }


        List<Object> firedRuleAndFailedAssert = new ArrayList<>();

        if (schematronOutput != null) {
            try {
                List<Object> asserts = schematronOutput.getActivePatternAndFiredRuleAndFailedAssert();
                firedRuleAndFailedAssert.addAll(asserts);
                log.trace(asserts.toString());
            } catch (Exception e) {
                errors.add(ConversionIssue.newWarning(e, "Error during Schematron result registration.",
                        callingLocation,
                        ErrorCode.Action.SCH_VALIDATION,
                        ErrorCode.Error.INVALID,
                        Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));
                return errors;
            }
        } else {
            final String message = "Schematron parsing failed. File: " + schematronResource.getID();
            log.error(message);
            errors.add(ConversionIssue.newError(new EigorRuntimeException(message, callingLocation, ErrorCode.Action.SCH_VALIDATION, ErrorCode.Error.INVALID, Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, schematronResource.getID()))));
        }


        for (Object obj : firedRuleAndFailedAssert) {
            if (obj instanceof FailedAssert) {
                FailedAssert failedAssert = (FailedAssert) obj;

                Exception cause = new Exception(
                        failedAssert.getLocation() + " failed test: " + failedAssert.getTest()
                );

                String ruleDescriptionFromSchematron = failedAssert.getText().trim().replaceAll("\\n", " ").replaceAll(" {2,}", " ");
                String offendingElement = failedAssert.getLocation().trim();
                EigorException error = new EigorException(
                        ErrorMessage.builder()
                        .message(String.format("Schematron failed assert '%s' on XML element at '%s'.",
                                ruleDescriptionFromSchematron,
                                offendingElement)
                        )
                        .location(callingLocation)
                        .action(ErrorCode.Action.SCH_VALIDATION)
                        .error(ErrorCode.Error.INVALID)
                        .build(),
                        cause
                );

                if ("fatal".equals(failedAssert.getFlag())) {
                    errors.add(ConversionIssue.newError(error));
                } else {
                    errors.add(ConversionIssue.newWarning(error));
                }
            }
        }
        return errors;
    }
}
