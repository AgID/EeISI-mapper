package it.infocert.eigor.converter.sdk;

public interface Converter {

    ConverterType getConverterType();
    String getInputFormat();
}

/*import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Converter {

    ConverterType type();
    String inboundFormat();

}*/
