package it.infocert.eigor.model.core.enums;

public enum Untdid4451InvoiceNoteSubjectCodeSample {

    AAA(false,"Goods description", "[7002] Plain language description of the nature of the for banking, Customs, statistical or transport purposes, avoiding unnecessary detail (Generic term).");

    private boolean hasPlusSign;
    private String shortDescritpion;
    private String longDescription;

    Untdid4451InvoiceNoteSubjectCodeSample(boolean hasPlusSign, String shortDescritpion, String longDescription) {
        this.hasPlusSign = hasPlusSign;
        this.shortDescritpion = shortDescritpion;
        this.longDescription = longDescription;
    }

    public String toDetailedString() {
        return String.format("%s%s|%s|%s", (this.hasPlusSign ? "+":""), super.toString(), shortDescritpion, longDescription);
    }

    @Override
    public String toString() {
        return super.toString() + " " + shortDescritpion;
    }
}
