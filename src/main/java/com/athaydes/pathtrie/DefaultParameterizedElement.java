package com.athaydes.pathtrie;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

final class DefaultParameterizedElement<E> implements ParameterizedElement<E> {

    private final Box<E> element;
    private final Map<String, String> parameters;

    DefaultParameterizedElement(Box<E> element, Map<String, String> parameters) {
        this.element = element;
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    @Override
    public E getElement() {
        return element.use(b -> b.element, f -> f.fun.applyParam(this));
    }

    @Override
    public Set<String> getParameterNames() {
        return parameters.keySet();
    }

    @Override
    public String param(String parameterName) {
        String value = parameters.get(parameterName);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value;
    }

}
