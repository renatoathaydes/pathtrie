package com.athaydes.pathtrie;

import java.util.Set;

public interface ParameterizedElement<E> {
    E getElement();

    Set<String> getParameterNames();

    String param(String parameterName);
}
