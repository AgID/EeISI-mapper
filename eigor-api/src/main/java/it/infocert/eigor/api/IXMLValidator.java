package it.infocert.eigor.api;

import java.util.List;

public interface IXMLValidator {

    List<Exception> validate(byte[] xml);
}
