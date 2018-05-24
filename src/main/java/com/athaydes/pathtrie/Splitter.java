package com.athaydes.pathtrie;

import java.util.function.Function;

/**
 * Function that splits paths given as Strings into the path constituents.
 */
@FunctionalInterface
public interface Splitter extends Function<String, Iterable<String>> {
}
