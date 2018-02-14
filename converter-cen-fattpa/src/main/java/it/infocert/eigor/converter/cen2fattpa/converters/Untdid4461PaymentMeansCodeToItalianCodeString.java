package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.ToStringTypeConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.converter.cen2fattpa.models.ModalitaPagamentoType;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

public class Untdid4461PaymentMeansCodeToItalianCodeString extends ToStringTypeConverter<Untdid4461PaymentMeansCode>{

    Untdid4461PaymentMeansCodeToItalianCodeString() {
    }

    public static TypeConverter<Untdid4461PaymentMeansCode, String> newConverter() {
        return new Untdid4461PaymentMeansCodeToItalianCodeString();
    }

    @Override
    public String convert(Untdid4461PaymentMeansCode paymentMeansCode) throws ConversionFailedException {

        checkNotNull(paymentMeansCode);

        switch (paymentMeansCode) {
            case Code30:
                return ModalitaPagamentoType.MP_05.value();
            case Code20:
            case Code92:
                return ModalitaPagamentoType.MP_02.value();
            case Code10:
                return ModalitaPagamentoType.MP_01.value();
            case Code21:
            case Code22:
            case Code23:
            case Code91:
                return ModalitaPagamentoType.MP_03.value();
            case Code60:
                return ModalitaPagamentoType.MP_06.value();
            case Code70:
                return ModalitaPagamentoType.MP_12.value();
            case Code15:
                return ModalitaPagamentoType.MP_15.value();
            case Code50:
                return ModalitaPagamentoType.MP_18.value();
            case Code1:
                return ModalitaPagamentoType.MP_22.value();
            default:
                return "";
        }
    }

    @Override
    public Class<Untdid4461PaymentMeansCode> getSourceClass() {
        return Untdid4461PaymentMeansCode.class;
    }


}
