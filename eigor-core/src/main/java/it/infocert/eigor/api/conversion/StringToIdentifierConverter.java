package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.datatypes.Identifier;

public class StringToIdentifierConverter implements TypeConverter<String,Identifier> {
    @Override
    public Identifier convert(String stringCode) {
        String scheme = stringCode.split(" ")[0];
        String value = stringCode.split(" ")[1];
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
}
