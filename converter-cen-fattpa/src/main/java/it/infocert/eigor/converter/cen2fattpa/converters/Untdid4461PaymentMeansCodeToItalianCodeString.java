package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.ToStringTypeConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
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
        
        	case Code2:
        		return ModalitaPagamentoType.MP_19.value();        
        	case Code3:
        		return ModalitaPagamentoType.MP_19.value();
            case Code4:
                return ModalitaPagamentoType.MP_19.value();
        	case Code5:
        		return ModalitaPagamentoType.MP_19.value();        
        	case Code6:
        		return ModalitaPagamentoType.MP_19.value();
            case Code7:
                return ModalitaPagamentoType.MP_19.value();
        	case Code8:
        		return ModalitaPagamentoType.MP_12.value();        
        	case Code9:
        		return ModalitaPagamentoType.MP_05.value();
            case Code11:
                return ModalitaPagamentoType.MP_19.value();                
        	case Code12:
        		return ModalitaPagamentoType.MP_19.value();        
        	case Code13:
        		return ModalitaPagamentoType.MP_19.value();
            case Code14:
                return ModalitaPagamentoType.MP_19.value();
        	case Code16:
        		return ModalitaPagamentoType.MP_05.value();
            case Code17:
                return ModalitaPagamentoType.MP_01.value();
        	case Code18:
        		return ModalitaPagamentoType.MP_01.value();        
        	case Code19:
        		return ModalitaPagamentoType.MP_01.value();
            case Code20:
            	return ModalitaPagamentoType.MP_02.value();        
        	case Code21:
        		return ModalitaPagamentoType.MP_03.value();        
        	case Code22:
        		return ModalitaPagamentoType.MP_02.value();                		
        	case Code23:
        		return ModalitaPagamentoType.MP_03.value();
            case Code24:
                return ModalitaPagamentoType.MP_13.value();
        	case Code25:
        		return ModalitaPagamentoType.MP_02.value();        
        	case Code26:
        		return ModalitaPagamentoType.MP_02.value();
            case Code27:
                return ModalitaPagamentoType.MP_01.value();
        	case Code28:
        		return ModalitaPagamentoType.MP_01.value();        
        	case Code29:
        		return ModalitaPagamentoType.MP_01.value();
        	case Code30:
        		return ModalitaPagamentoType.MP_05.value();             		
        	case Code31:
        		return ModalitaPagamentoType.MP_01.value();       
         	case Code32:
        		return ModalitaPagamentoType.MP_01.value();        
        	case Code33:
        		return ModalitaPagamentoType.MP_01.value();
            case Code34:
                return ModalitaPagamentoType.MP_01.value();
        	case Code35:
        		return ModalitaPagamentoType.MP_01.value();        
        	case Code36:
        		return ModalitaPagamentoType.MP_01.value();
            case Code37:
                return ModalitaPagamentoType.MP_01.value();
        	case Code38:
        		return ModalitaPagamentoType.MP_01.value();        
        	case Code39:
        		return ModalitaPagamentoType.MP_01.value();
        	case Code40:
        		return ModalitaPagamentoType.MP_01.value();        
        	case Code41:
        		return ModalitaPagamentoType.MP_01.value();           
        	case Code42:
        		return ModalitaPagamentoType.MP_17.value();        
        	case Code43:
        		return ModalitaPagamentoType.MP_01.value();
            case Code44:
                return ModalitaPagamentoType.MP_12.value();
        	case Code45:
        		return ModalitaPagamentoType.MP_05.value();        
        	case Code46:
        		return ModalitaPagamentoType.MP_19.value();
            case Code47:
                return ModalitaPagamentoType.MP_19.value();
        	case Code48:
        		return ModalitaPagamentoType.MP_08.value();        
        	case Code49:
        		return ModalitaPagamentoType.MP_01.value(); 
        	case Code51:
        		return ModalitaPagamentoType.MP_05.value();
           	case Code52:
        		return ModalitaPagamentoType.MP_01.value();        
        	case Code53:
        		return ModalitaPagamentoType.MP_01.value();
            case Code54:
                return ModalitaPagamentoType.MP_08.value();
        	case Code55:
        		return ModalitaPagamentoType.MP_08.value();        
        	case Code56:
        		return ModalitaPagamentoType.MP_05.value();
            case Code57:
                return ModalitaPagamentoType.MP_01.value();
        	case Code58:
        		return ModalitaPagamentoType.MP_19.value();        
        	case Code59:
        		return ModalitaPagamentoType.MP_19.value();
        	case Code61:
        		return ModalitaPagamentoType.MP_06.value();               		
          	case Code62:
        		return ModalitaPagamentoType.MP_06.value();        
        	case Code63:
        		return ModalitaPagamentoType.MP_06.value();
            case Code64:
                return ModalitaPagamentoType.MP_06.value();
        	case Code65:
        		return ModalitaPagamentoType.MP_06.value();        
        	case Code66:
        		return ModalitaPagamentoType.MP_06.value();
            case Code67:
                return ModalitaPagamentoType.MP_06.value();
        	case Code68:
        		return ModalitaPagamentoType.MP_05.value();        
            case Code74:
                return ModalitaPagamentoType.MP_13.value();
        	case Code75:
        		return ModalitaPagamentoType.MP_13.value();        
        	case Code76:
        		return ModalitaPagamentoType.MP_13.value();
            case Code77:
                return ModalitaPagamentoType.MP_13.value();
        	case Code78:
        		return ModalitaPagamentoType.MP_13.value();
            case Code91:
                return ModalitaPagamentoType.MP_03.value();      		
            case Code92:
                return ModalitaPagamentoType.MP_02.value();        		
        	case Code93:
        		return ModalitaPagamentoType.MP_05.value();
            case Code94:
                return ModalitaPagamentoType.MP_05.value();
        	case Code95:
        		return ModalitaPagamentoType.MP_05.value();        
        	case Code96:
        		return ModalitaPagamentoType.MP_01.value();
            case Code97:
                return ModalitaPagamentoType.MP_22.value();       		
            case Code10:
                return ModalitaPagamentoType.MP_01.value();
            case Code60:
                return ModalitaPagamentoType.MP_06.value();
            case Code70:
                return ModalitaPagamentoType.MP_12.value();
            case Code15:
                return ModalitaPagamentoType.MP_05.value();
            case Code50:
                return ModalitaPagamentoType.MP_18.value();
            case Code1:
                return ModalitaPagamentoType.MP_01.value();
            default:
                return ModalitaPagamentoType.MP_01.value();
        }
    }

    @Override
    public Class<Untdid4461PaymentMeansCode> getSourceClass() {
        return Untdid4461PaymentMeansCode.class;
    }


}
