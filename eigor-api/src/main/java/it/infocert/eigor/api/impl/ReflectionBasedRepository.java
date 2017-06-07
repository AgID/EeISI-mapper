package it.infocert.eigor.api.impl;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.rules.Rule;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Repository based on <a href="https://github.com/ronmamo/reflections">Reflections</a>.
 */
public class ReflectionBasedRepository implements RuleRepository, FromCenConversionRepository, ToCenConversionRepository {

    private Set<Rule> rules = null;
    private Set<FromCenConversion> fromCenConversions = null;
    private Set<Abstract2CenConverter> toCENConverters = null;
    private final Reflections reflections;

    public ReflectionBasedRepository(Reflections reflections) {
        this.reflections = reflections;
    }

    @Override public List<Rule> rules() {
        if (rules == null) {
            this.rules = findImplementation(Rule.class);
        }
        return new ArrayList<>(rules);
    }

    @Override public FromCenConversion findConversionFromCen(final String format) {
        if (fromCenConversions == null) {
            this.fromCenConversions = findImplementation(FromCenConversion.class);
        }

        Filter<FromCenConversion> filter = new Filter<FromCenConversion>() {
            @Override public boolean apply(FromCenConversion c) {
                return c.support(format);
            }
        };

        return Stream.create(fromCenConversions).filter(filter).first();

    }

    @Override
    public Set<String> supportedFormats() {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (FromCenConversion fromCenConversion : fromCenConversions) {
            result.add( fromCenConversion.getSupportedFormats() );
        }
        return result;
    }

    @Override
    public Set<String> supportedToCenFormats() {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        if(toCENConverters!=null) {
            for (ToCenConversion conversion : toCENConverters) {
                result.addAll(conversion.getSupportedFormats());
            }
        }
        return result;
    }

    @Override public Abstract2CenConverter findConversionToCen(final String sourceFormat) {
        if (toCENConverters == null) {
            this.toCENConverters = findImplementation(Abstract2CenConverter.class);
        }

        Filter<Abstract2CenConverter> f = new Filter<Abstract2CenConverter>() {
            @Override public boolean apply(Abstract2CenConverter c) {
                return c.support(sourceFormat);
            }
        };
        return Stream.create(toCENConverters).filter(f).first();

    }

    private <T> Set<T> findImplementation(Class<T> classToFind) {
        Set<T> myRules = new HashSet<>();
        Set<Class<? extends T>> subClasses = reflections.getSubTypesOf(classToFind);

        for (Class<? extends T> subClass : subClasses) {
            try {
                Constructor constrWithReflections = null;
                for (Constructor c: subClass.getConstructors()) {
                    if (c.getParameterTypes().length  == 1) {
                        Class aClass = c.getParameterTypes()[0];
                        if (Reflections.class.equals(aClass)) {
                            constrWithReflections = c;
                        }
                    }
                }
                if (constrWithReflections == null) {
                    myRules.add(subClass.newInstance());
                } else {
                    myRules.add( (T)constrWithReflections.newInstance(reflections));
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("An error occurred instantiating class '" + subClass.getName() + "' as subclass of '" + classToFind.getName() + "'.", e);
            }
        }

        return myRules;
    }

}
