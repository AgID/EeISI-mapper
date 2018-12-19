package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.model.core.datatypes.Binary;
import org.codehaus.plexus.util.Base64;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public class Base64StringToBinaryConverter implements TypeConverter<String, Binary> {

    public static TypeConverter<String, Binary> newConverter(){
        return new Base64StringToBinaryConverter();
    }

    private Base64StringToBinaryConverter() {
    }

    @Override public Binary convert(String contentInBase64) {
        byte[] bytes = Base64.decodeBase64(contentInBase64.getBytes());
        MimeType mt;
        try {
            mt = new MimeType("application", "data");
        } catch (MimeTypeParseException e) {
            throw new RuntimeException(e);
        }
        return new Binary(bytes, mt, "filename");
    }

    @Override public Class<Binary> getTargetClass() {
        return Binary.class;
    }

    @Override public Class<String> getSourceClass() {
        return String.class;
    }

}
