package com.athaydes.pathtrie.functions;

import com.athaydes.pathtrie.ParameterizedElement;
import java.util.Iterator;

@FunctionalInterface
public interface Fun2<T> extends Fun<T> {

    T apply(String param1, String param2);

    @Override
    default int parameterCount() {
        return 2;
    }

    @Override
    default T applyParam(ParameterizedElement<T> parameterizedElement) {
        Iterator<String> paramNames = parameterizedElement.getParameterNames().iterator();
        String name1 = paramNames.next();
        String name2 = paramNames.next();
        return apply(parameterizedElement.param(name1), parameterizedElement.param(name2));
    }
}
