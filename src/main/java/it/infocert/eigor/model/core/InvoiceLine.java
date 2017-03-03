package it.infocert.eigor.model.core;

public class InvoiceLine {

    // 1..1
    private String lineIdentifier;

    // 0..n
    private String lineNote;

    // 1..1
    private int invoicedQuantity;

    public String getLineIdentifier() {
        return lineIdentifier;
    }

    public void setLineIdentifier(String lineIdentifier) {
        this.lineIdentifier = lineIdentifier;
    }

    public String getLineNote() {
        return lineNote;
    }

    public void setLineNote(String lineNote) {
        this.lineNote = lineNote;
    }

    public int getInvoicedQuantity() {
        return invoicedQuantity;
    }

    public void setInvoicedQuantity(int invoicedQuantity) {
        this.invoicedQuantity = invoicedQuantity;
    }
}
