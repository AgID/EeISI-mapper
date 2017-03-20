package it.infocert.eigor.model.core.enums;

public enum TemplateSampleUntdid1001InvoiceTypeCode {

    // 1: Sign
    // 2: ID
    // 3: ShortDescription
    // 4: LongDescription
    Code1("X",1, "desc", "desc");

    private String sign;
    private int code;
    private String shortDescritpion;
    private String longDescription;

    TemplateSampleUntdid1001InvoiceTypeCode(String sign, int code, String shortDescritpion, String longDescription) {
        this.sign = sign;
        this.code = code;
        this.shortDescritpion = shortDescritpion;
        this.longDescription = longDescription;
    }

    @Override
    public String toString() {
        return super.toString() + " " + shortDescritpion;
    }

}
