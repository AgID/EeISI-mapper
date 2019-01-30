package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.model.core.datatypes.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringToIdentifierConverter implements TypeConverter<String,Identifier> {

    private StringToIdentifierConverter() {
    }

    @Override
    public Identifier convert(String stringCode) {
        final String[] splitBlank = stringCode.split(" ");
        String scheme;
        String value;
        if (splitBlank.length > 1) {
            scheme = splitBlank[0];
            value = splitBlank[1];
        } else if(!stringCode.contains(":")){
            scheme = null;
            value = stringCode;
        } else {
            final String regex = "(\\w*:\\w*):(.*)";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(stringCode);
            if (matcher.matches()) {
                scheme = matcher.group(1);
                value = matcher.group(2);
            } else {
                throw new IllegalArgumentException(String.format("String code %s doesn't match regex '%s'", stringCode, regex));
            }
        }
        return new Identifier(scheme, value);
    }

    @Override
    public Class<Identifier> getTargetClass() {
        return Identifier.class;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    public static TypeConverter newConverter() {
        return new StringToIdentifierConverter();
    }
}
