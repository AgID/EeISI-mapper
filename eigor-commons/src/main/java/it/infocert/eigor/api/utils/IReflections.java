package it.infocert.eigor.api.utils;

import java.util.Set;

public interface IReflections {

    <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type);
}
