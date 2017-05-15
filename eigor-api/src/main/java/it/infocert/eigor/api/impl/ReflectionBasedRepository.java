package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.rules.Rule;
import org.reflections.Reflections;

import java.util.*;

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

    @Override public FromCenConversion findConversionFromCen(String format) {
        if (fromCenConversions == null) {
            this.fromCenConversions = findImplementation(FromCenConversion.class);
        }
        return fromCenConversions.stream().filter(c -> c.support(format)).findFirst().orElse(null);
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

    @Override public ToCenConversion findConversionToCen(String sourceFormat) {
        if (toCENConverters == null) {
            this.toCENConverters = findImplementation(ToCenConversion.class);
        }
        return toCENConverters.stream().filter(c -> c.support(sourceFormat)).findFirst().orElse(null);
    }

    private <T> Set<T> findImplementation(Class<T> classToFind) {
        Set<T> myRules = new HashSet<>();
        Set<Class<? extends T>> ruleClasses = reflections.getSubTypesOf(classToFind);
        ruleClasses.forEach(ruleClass -> {
            try {
                myRules.add(ruleClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("An error occurred instantiating class '" + ruleClass.getName() + "' as subclass of '" + classToFind.getName() + "'.", e);
            }
        });
        return myRules;
    }

}
