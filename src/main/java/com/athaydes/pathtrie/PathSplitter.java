package com.athaydes.pathtrie;

import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Function that splits paths given as Strings into the path constituents.
 * <p>
 * It also provides a parameter prefix that is used to determine which parts of a path are parameters.
 */
@FunctionalInterface
public interface PathSplitter extends Function<String, Iterable<String>> {

    /**
     * @return the prefix of parameterized paths. Must not be null or the empty String.
     */
    default String parameterizedParameterPrefix() {
        return ":";
    }

    /**
     * @return a builder of {@link PathSplitter}
     */
    static PathSplitterBuilder newBuilder() {
        return new PathSplitterBuilder();
    }

    /**
     * Builder of {@link PathSplitter} instances.
     */
    class PathSplitterBuilder {

        private String splitter = "/";
        private String parameterPrefix = ":";

        PathSplitterBuilder splitOn(String splitter) {
            this.splitter = splitter;
            return this;
        }

        public PathSplitterBuilder withParameterPrefix(String parameterPrefix) {
            this.parameterPrefix = parameterPrefix;
            return this;
        }

        public PathSplitter build() {
            return new PathSplitter() {
                final String prefix = parameterPrefix;
                final Pattern splitPattern = Pattern.compile(Pattern.quote(splitter));

                @Override
                public Iterable<String> apply(String s) {
                    return Arrays.asList(splitPattern.split(s));
                }

                @Override
                public String parameterizedParameterPrefix() {
                    return prefix;
                }
            };
        }
    }

}
