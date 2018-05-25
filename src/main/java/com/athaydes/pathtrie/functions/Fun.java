package com.athaydes.pathtrie.functions;

import com.athaydes.pathtrie.ParameterizedElement;

public interface Fun<T> {

    T applyParam(ParameterizedElement<T> parameterizedElement);

    int parameterCount();

}
