package it.infocert.eigor.api.utils;

import com.google.common.base.Preconditions;
import org.reflections.Reflections;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReflectionsReflections implements IReflections  {

    private final Reflections reflections;

    public ReflectionsReflections(Reflections reflections) {
        this.reflections = checkNotNull( reflections );
    }

    public ReflectionsReflections(String packageName) {
        this.reflections = new Reflections(packageName);
    }

    @Override
    public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
        return reflections.getSubTypesOf(type);
    }
}
