package com.athaydes.pathtrie;

import java.util.function.Function;

@FunctionalInterface
public interface Splitter extends Function<String, Iterable<String>> {
}
