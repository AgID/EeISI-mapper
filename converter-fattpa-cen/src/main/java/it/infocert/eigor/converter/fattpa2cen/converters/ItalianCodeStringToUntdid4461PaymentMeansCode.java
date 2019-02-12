package it.infocert.eigor.converter.fattpa2cen.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

import static it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode.*;

public class ItalianCodeStringToUntdid4461PaymentMeansCode implements TypeConverter<String,Untdid4461PaymentMeansCode> {

    ItalianCodeStringToUntdid4461PaymentMeansCode() {
    	
    }

    @Override
    public Untdid4461PaymentMeansCode convert(String stringCode) {
        switch (stringCode) {
        
        	case "MP01":
        		return Untdid4461PaymentMeansCode.Code10;
        	case "MP02":
                return Untdid4461PaymentMeansCode.Code20;
        	case "MP03":
                return Untdid4461PaymentMeansCode.Code23;
        	case "MP04":
                return Untdid4461PaymentMeansCode.Code9;    
        	case "MP06":
                return Untdid4461PaymentMeansCode.Code60;
            case "MP07":
                return Untdid4461PaymentMeansCode.Code49;
            case "MP08":
                return Untdid4461PaymentMeansCode.Code48;                
            case "MP09":
                return Untdid4461PaymentMeansCode.Code46;
            case "MP10":
                return Untdid4461PaymentMeansCode.Code46;
            case "MP11":
                return Untdid4461PaymentMeansCode.Code46;                
            case "MP12":
                return Untdid4461PaymentMeansCode.Code70;
            case "MP13":
                return Untdid4461PaymentMeansCode.Code70;
            case "MP15":
                return Untdid4461PaymentMeansCode.Code15;
            case "MP16":
                return Untdid4461PaymentMeansCode.Code49;
            case "MP17":
                return Untdid4461PaymentMeansCode.Code42;                
            case "MP18":
                return Untdid4461PaymentMeansCode.Code50;
            case "MP19":
                return Untdid4461PaymentMeansCode.Code51;
            case "MP20":
                return Untdid4461PaymentMeansCode.Code51;
            case "MP21":
                return Untdid4461PaymentMeansCode.Code51;            
            case "MP22":
                return Untdid4461PaymentMeansCode.Code97;
            case "MP05":
                return Untdid4461PaymentMeansCode.Code30;
            case "MP14":
                return CodeZZZ;                
            default:
                return Code1;
        }
    }
    
    @Override
    public Class<Untdid4461PaymentMeansCode> getTargetClass() {
        return Untdid4461PaymentMeansCode.class;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    public static TypeConverter<String,Untdid4461PaymentMeansCode> newConverter() {
        return new ItalianCodeStringToUntdid4461PaymentMeansCode();
    }
}
