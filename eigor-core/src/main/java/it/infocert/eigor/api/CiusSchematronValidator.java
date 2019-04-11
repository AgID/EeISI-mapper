package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.org.springframework.core.io.Resource;

public class CiusSchematronValidator extends SchematronValidator {


    public CiusSchematronValidator(Resource schemaResource, boolean isXSLT, boolean xsltFileUpdate, ErrorCode.Location callingLocation) {
        super(schemaResource, isXSLT, xsltFileUpdate, callingLocation);
        setDefaultAction(ErrorCode.Action.CIUS_SCH_VALIDATION);
    }
}
