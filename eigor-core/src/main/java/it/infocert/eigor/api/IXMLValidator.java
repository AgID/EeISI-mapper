package it.infocert.eigor.api;

import java.util.List;

public interface IXMLValidator {

    List<IConversionIssue> validate(byte[] xml);
}
