package it.infocert.eigor.api.conversion.converter;


import java.util.function.Predicate;

abstract class FilterByValue<E,V> implements Predicate<E> {

    protected final V value;

    FilterByValue(V value) {
        this.value = value;
    }

}
