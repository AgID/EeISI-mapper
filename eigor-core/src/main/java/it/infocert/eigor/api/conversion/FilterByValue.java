package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.functions.Filter;

abstract class FilterByValue<E,V> implements Filter<E> {

    protected final V value;

    FilterByValue(V value) {
        this.value = value;
    }

}