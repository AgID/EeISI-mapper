package it.infocert.eigor.api.utils;

import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;

import java.util.Set;

public class ReflectionsReflections implements IReflections  {

    private final Reflections reflections;

    public ReflectionsReflections() {
        this.reflections = new Reflections(BTBG.class.getPackage().getName());
    }

    @Override public Set<Class<? extends BTBG>> getSubTypesOfBtBg() {
        return reflections.getSubTypesOf(BTBG.class);
    }
}
