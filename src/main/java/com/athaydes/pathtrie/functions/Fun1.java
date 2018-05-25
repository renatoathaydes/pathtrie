package com.athaydes.pathtrie.functions;

import com.athaydes.pathtrie.ParameterizedElement;
import java.util.Iterator;

@FunctionalInterface
public interface Fun1<T> extends Fun<T> {

    T apply(String parameter);

    @Override
    default int parameterCount() {
        return 1;
    }

    @Override
    default T applyParam(ParameterizedElement<T> parameterizedElement) {
        Iterator<String> paramNames = parameterizedElement.getParameterNames().iterator();
        String name1 = paramNames.next();
        return apply(parameterizedElement.param(name1));
    }

}