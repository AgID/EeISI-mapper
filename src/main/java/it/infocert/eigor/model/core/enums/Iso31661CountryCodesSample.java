package it.infocert.eigor.model.core.enums;

public enum Iso31661CountryCodesSample {

    //1: EnglishName,
    //2: Alpha2Code
    //3: Alpha3Code,
    //4: NumericCode,
    AF("Afghanistan","AF","AFG",4);

    private final String countryNameInEnglish;
    private final String iso2charCode;
    private final String iso3charCode;
    private final int index;

    Iso31661CountryCodesSample(String countryNameInEnglish, String iso2charCode, String iso3charCode, int index) {
        this.countryNameInEnglish = countryNameInEnglish;
        this.iso2charCode = iso2charCode;
        this.iso3charCode = iso3charCode;
        this.index = index;
    }
}
