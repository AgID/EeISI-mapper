package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;

public class CountryNameToIso31661CountryCodeConverter extends FilteringEnumConversion<String, Iso31661CountryCodes> {

    public CountryNameToIso31661CountryCodeConverter() {
        super(Iso31661CountryCodes.class);
    }

    @Override protected Filter<Iso31661CountryCodes> buildFilter(final String value) {
        return new Filter<Iso31661CountryCodes>() {
            @Override public boolean apply(Iso31661CountryCodes iso) {
                return iso.getCountryNameInEnglish().equalsIgnoreCase(value);
            }};
    }

}
