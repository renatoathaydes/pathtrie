package com.athaydes.pathtrie.functions;

import com.athaydes.pathtrie.ParameterizedElement;
import java.util.function.Supplier;

@FunctionalInterface
public interface Fun0<T> extends Fun<T>, Supplier<T> {

    T apply();

    @Override
    default T get() {
        return apply();
    }

    @Override
    default int parameterCount() {
        return 0;
    }

    @Override
    default T applyParam(ParameterizedElement<T> parameterizedElement) {
        return apply();
    }

}