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

final class ConverterUtils {

    private static final String SCHEME = "scheme";
    private static final String VERSION = "version";

    private ConverterUtils() {};

    static BiFunction<String, Element, Optional<BTBG>> getBt = (s, el) -> {
        try {
            final Class<? extends BTBG> btBgByName = new InvoiceUtils(new JavaReflections()).getBtBgByName(s);
            Constructor<? extends BTBG> constructor = btBgByName.getConstructor(Identifier.class);
            Identifier id;
            if(Objects.nonNull(el.getAttribute(SCHEME)) && StringUtils.isNotEmpty(el.getAttribute(SCHEME).getValue())) {
                final String schemeValue = el.getAttribute(SCHEME).getValue();
                if(Objects.nonNull(el.getAttribute(VERSION)) && StringUtils.isNotEmpty(el.getAttribute(VERSION).getValue())) {
                    final String versionValue = el.getAttribute(VERSION).getValue();
                    id = new Identifier(schemeValue, versionValue, el.getText());
                } else {
                    id = new Identifier(schemeValue, el.getText());
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