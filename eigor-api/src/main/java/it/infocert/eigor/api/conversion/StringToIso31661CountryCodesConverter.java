package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;

import java.util.Arrays;

public class StringToIso31661CountryCodesConverter implements TypeConverter<String, Iso31661CountryCodes> {

    @Override
    public Iso31661CountryCodes convert(String s) {

        try {
            return Iso31661CountryCodes.valueOf(s);
        } catch (IllegalArgumentException e) {

        }

        return Arrays.stream(Iso31661CountryCodes.values())
                .filter( iso -> iso.getCountryNameInEnglish().equalsIgnoreCase(s))
                .findFirst().orElseThrow(IllegalArgumentException::new);

    }

}
