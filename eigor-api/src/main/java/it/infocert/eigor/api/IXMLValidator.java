package it.infocert.eigor.api;

import java.util.List;

public interface IXMLValidator {

    List<ConversionIssue> validate(byte[] xml);
}
