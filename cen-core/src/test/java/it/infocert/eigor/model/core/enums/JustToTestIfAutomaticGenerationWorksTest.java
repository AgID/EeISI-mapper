package it.infocert.eigor.model.core.enums;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JustToTestIfAutomaticGenerationWorksTest {

    @Test
    public void justUseTheEnums() {

        assertThat(Iso31661CountryCodes.CF.getIso2charCode()).isEqualTo("CF");
        assertThat(Iso31661CountryCodes.US.getIso2charCode()).isEqualTo("US");

        assertThat(Untdid1001InvoiceTypeCode.Code1.getCode()).isEqualTo(1);
        assertThat(Untdid1001InvoiceTypeCode.Code998.getCode()).isEqualTo(998);

        assertThat(Untdid4451InvoiceNoteSubjectCode.AAA.getShortDescription()).isEqualTo("Goods description");
        assertThat(Untdid4451InvoiceNoteSubjectCode.ZZZ.getShortDescription()).isEqualTo("Mutually defined");

    }

}
