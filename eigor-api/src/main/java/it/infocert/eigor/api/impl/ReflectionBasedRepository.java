package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.model.core.rules.Rule;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Repository based on <a href="https://github.com/ronmamo/reflections">Reflections</a>.
 */
public class ReflectionBasedRepository implements RuleRepository {

    private Set<Rule> rules = null;
    private final Reflections reflections;

    public ReflectionBasedRepository(Reflections reflections) {
        this.reflections = reflections;
    }

    @Override
    public List<Rule> rules() {
        if (rules == null) {
            this.rules = findImplementation(Rule.class);
        }
        return new ArrayList<>(rules);
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> findImplementation(Class<T> classToFind) {
        Set<T> myRules = new HashSet<>();
        Set<Class<? extends T>> subClasses = reflections.getSubTypesOf(classToFind);

        for (Class<? extends T> subClass : subClasses) {
            try {
                Constructor constrWithReflectionsAndRegistry = null;
                for (Constructor c : subClass.getConstructors()) {
                    if (c.getParameterTypes().length == 1) {
                        Class aClass = c.getParameterTypes()[0];
                        if (Reflections.class.equals(aClass)) {
                            constrWithReflectionsAndRegistry = c;
                        }
                    }
                }
                if (constrWithReflectionsAndRegistry == null) {
                    myRules.add(subClass.newInstance());
                } else {
                    myRules.add((T) constrWithReflectionsAndRegistry.newInstance(reflections));
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("An error occurred instantiating class '" + subClass.getName() + "' as subclass of '" + classToFind.getName() + "'.", e);
            }
        }

        return myRules;
    }

}
