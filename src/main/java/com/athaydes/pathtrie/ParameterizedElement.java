package com.athaydes.pathtrie;

import java.util.Set;

/**
 * A container for an element which is located under a parameterized path.
 *
 * @param <E> type of element
 */
public interface ParameterizedElement<E> {
    /**
     * @return the element
     */
    E getElement();

    /**
     * @return the name of all available parameters
     */
    Set<String> getParameterNames();

    /**
     * Resolve the value of the parameter with the given name.
     *
     * @param parameterName name of the path parameter
     * @return value of the parameter
     * @throws java.util.NoSuchElementException if the parameter is not found
     */
    String param(String parameterName);
}
