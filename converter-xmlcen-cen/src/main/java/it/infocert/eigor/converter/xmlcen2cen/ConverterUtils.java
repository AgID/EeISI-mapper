package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BTBG;
import org.codehaus.plexus.util.StringUtils;
import org.jdom2.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public final class ConverterUtils {

    private ConverterUtils() {};

    public static BiFunction<String, Element, Optional<BTBG>> getBt = (s, el) -> {
        try {
            final String scheme = el.getAttribute("scheme").getValue();
            final Class<? extends BTBG> btBgByName = new InvoiceUtils(new JavaReflections()).getBtBgByName(s);
            Constructor<? extends BTBG> constructor = btBgByName.getConstructor(Identifier.class);
            Identifier id;
            if(Objects.nonNull(el.getAttribute("scheme")) && StringUtils.isNotEmpty(el.getAttribute("scheme").getValue())) {
                if(Objects.nonNull(el.getAttribute("version")) && StringUtils.isNotEmpty(el.getAttribute("version").getValue())) {
                    final String version = el.getAttribute("version").getValue();
                    id = new Identifier(scheme, version, el.getText());
                } else {
                    id = new Identifier(scheme, el.getText());
                }
            } else {
                id = new Identifier(el.getText());
            }
            return Optional.of(constructor.newInstance(id));
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return Optional.empty();
        }
    };
}
