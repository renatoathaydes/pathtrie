package com.athaydes.pathtrie.functions;

import com.athaydes.pathtrie.ParameterizedElement;
import java.util.Iterator;

public interface Fun4<T> extends Fun<T> {

    T apply(String param1, String param2, String param3, String param4);

    @Override
    default int parameterCount() {
        return 4;
    }

    @Override
    default T applyParam(ParameterizedElement<T> parameterizedElement) {
        Iterator<String> paramNames = parameterizedElement.getParameterNames().iterator();
        String name1 = paramNames.next();
        String name2 = paramNames.next();
        String name3 = paramNames.next();
        String name4 = paramNames.next();
        return apply(parameterizedElement.param(name1),
                parameterizedElement.param(name2),
                parameterizedElement.param(name3),
                parameterizedElement.param(name4));
    }
}
