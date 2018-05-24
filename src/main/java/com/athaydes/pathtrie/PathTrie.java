package com.athaydes.pathtrie;

import java.util.Optional;

/**
 * PathTrie is an immutable data structure to store information under paths.
 * <p>
 * It is based on the well known Trie data structure.
 *
 * @param <E> type of elements stored in this path tree
 */
public interface PathTrie<E> {

    /**
     * @param <T> type of elements
     * @return the empty, immutable PathTrie instance
     */
    static <T> PathTrie<T> empty() {
        //noinspection unchecked
        return (PathTrie<T>) EmptyTrie.INSTANCE;
    }

    /**
     * Create a new {@link PathTrie} builder.
     * <p>
     * A default splitter is used that splits paths on the {@code '/'} character.
     *
     * @param <T> type of elements
     * @return builder
     */
    static <T> PathTrieBuilder<T> newBuilder() {
        return new PathTrieBuilder<>();
    }

    /**
     * Create a new {@link PathTrie} builder.
     *
     * @param <T> type of elements
     * @return builder
     */
    static <T> PathTrieBuilder<T> newBuilder(Splitter splitter) {
        return new PathTrieBuilder<>(splitter);
    }

    /**
     * Get an element from this PathTrie.
     *
     * @param path under which the element should be located
     * @return the element, if found, or empty if not found
     */
    Optional<E> get(String path);

    /**
     * Get an element from this PathTrie.
     *
     * @param path parameterized path under which the element should be located
     * @return the element as well as the resolved path parameters, if found, or empty if not found
     */
    Optional<ParameterizedElement<E>> getParameterized(String path);

    /**
     * Get the sub-PathTrie located under the given path.
     *
     * @param path under which the sub-PathTrie is located
     * @return the sub-PathTrie, if found, or empty if not found
     */
    Optional<PathTrie<E>> getChild(String path);

}

final class EmptyTrie implements PathTrie<Object> {

    static final EmptyTrie INSTANCE = new EmptyTrie();

    private EmptyTrie() {
    }

    @Override
    public Optional get(String path) {
        return Optional.empty();
    }

    @Override
    public Optional<ParameterizedElement<Object>> getParameterized(String path) {
        return Optional.empty();
    }

    @Override
    public Optional<PathTrie<Object>> getChild(String path) {
        return Optional.empty();
    }

}
