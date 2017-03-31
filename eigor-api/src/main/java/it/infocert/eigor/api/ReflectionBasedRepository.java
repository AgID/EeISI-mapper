package it.infocert.eigor.api;

import it.infocert.eigor.model.core.rules.Rule;
import org.reflections.Reflections;

import java.util.*;

/**
 * Repository based on <a href="https://github.com/ronmamo/reflections">Reflections</a>.
 */
public class ReflectionBasedRepository implements CenRuleRepository, FromCenConversionRepository, ToCenConversionRepository {

    private Set<Rule> rules = null;
    private Set<FromCENConverter> fromCENConverters = null;
    private Set<ToCENConversion> toCENConverters = null;
    private Reflections reflections = null;

    @Override public List<Rule> rules() {
        if (rules == null) {
            this.rules = findImplementation(Rule.class);
        }
        return new ArrayList<>(rules);
    }

    @Override public FromCENConverter findConversionFromCen(String format) {
        if (fromCENConverters == null) {
            this.fromCENConverters = findImplementation(FromCENConverter.class);
        }
        return fromCENConverters.stream().filter(c -> c.support(format)).findFirst().orElse(null);
    }

    @Override public ToCENConversion findConversionToCen(String sourceFormat) {
        if (toCENConverters == null) {
            this.toCENConverters = findImplementation(ToCENConversion.class);
        }
        return toCENConverters.stream().filter(c -> c.support(sourceFormat)).findFirst().orElse(null);
    }

    private Reflections lazyReflections() {
        return reflections != null ? reflections : new Reflections("it.infocert");
    }

    private <T> Set<T> findImplementation(Class<T> classToFind) {
        Set<T> myRules = new HashSet<>();
        Reflections reflections = lazyReflections();
        Set<Class<? extends T>> ruleClasses = reflections.getSubTypesOf(classToFind);
        ruleClasses.forEach(ruleClass -> {
            try {
                myRules.add(ruleClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return myRules;
    }

}
