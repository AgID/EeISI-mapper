package it.infocert.eigor.model.core.enums;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JustToTestIfAutomaticGenerationWorksTest {

    @Test
    public void justUseTheEnums() {

        assertThat(Iso31661CountryCodes.CF.toString()).isEqualTo("CF");
        assertThat(Iso31661CountryCodes.US.toString()).isEqualTo("US");

        assertThat(Untdid1001InvoiceTypeCode.Code1.toString()).isEqualTo("Code1 Certificate of analysis");
        assertThat(Untdid1001InvoiceTypeCode.Code998.toString()).isEqualTo("Code998 Previous Customs document/message");

        assertThat(Untdid4451InvoiceNoteSubjectCode.AAA.toString()).isEqualTo("AAA Goods description");
        assertThat(Untdid4451InvoiceNoteSubjectCode.ZZZ.toString()).isEqualTo("ZZZ Mutually defined");

    }

}
