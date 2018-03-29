package it.infocert.eigor.api.conversion.converter;

import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;

public class CountryNameToIso31661CountryCodeConverter extends FilteringEnumConversion<String, Iso31661CountryCodes> {

    public static TypeConverter<String, Iso31661CountryCodes> newConverter(){
        return new CountryNameToIso31661CountryCodeConverter();
    }

    private CountryNameToIso31661CountryCodeConverter() {
        super(Iso31661CountryCodes.class);
    }

    @Override protected Filter<Iso31661CountryCodes> buildFilter(final String value) {
        return new Filter<Iso31661CountryCodes>() {
            @Override public boolean apply(Iso31661CountryCodes iso) {
                return iso.getCountryNameInEnglish().equalsIgnoreCase(value);
            }};
    }

    @Override
    public Class<Iso31661CountryCodes> getTargetClass() {
        return Iso31661CountryCodes.class;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

}
