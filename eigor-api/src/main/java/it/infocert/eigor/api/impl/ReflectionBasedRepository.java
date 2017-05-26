package it.infocert.eigor.api.impl;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.rules.Rule;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;

/**
 * Repository based on <a href="https://github.com/ronmamo/reflections">Reflections</a>.
 */
public class ReflectionBasedRepository implements RuleRepository, FromCenConversionRepository, ToCenConversionRepository {

    private Set<Rule> rules = null;
    private Set<FromCenConversion> fromCenConversions = null;
    private Set<ToCenConversion> toCENConverters = null;
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

    @Override public ToCenConversion findConversionToCen(final String sourceFormat) {
        if (toCENConverters == null) {
            this.toCENConverters = findImplementation(ToCenConversion.class);
        }

        Filter<ToCenConversion> f = new Filter<ToCenConversion>() {
            @Override public boolean apply(ToCenConversion c) {
                return c.support(sourceFormat);
            }
        };
        return Stream.create(toCENConverters).filter(f).first();

    }

    private <T> Set<T> findImplementation(Class<T> classToFind) {
        Set<T> myRules = new HashSet<>();
        Set<Class<? extends T>> ruleClasses = reflections.getSubTypesOf(classToFind);

        for (Class<? extends T> ruleClass : ruleClasses) {
            try {
                Constructor constrWithReflections = null;
                for (Constructor c: ruleClass.getConstructors()) {
                    if (c.getTypeParameters().length  == 1 &&
                            Reflections.class.equals(c.getParameterTypes()[0])) {
                        constrWithReflections = c;
                    }
                }
                if (constrWithReflections == null) {
                    myRules.add(ruleClass.newInstance());
                } else {
                    myRules.add( (T)constrWithReflections.newInstance(reflections));
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("An error occurred instantiating class '" + ruleClass.getName() + "' as subclass of '" + classToFind.getName() + "'.", e);
            }
        }

        return myRules;
    }

}
