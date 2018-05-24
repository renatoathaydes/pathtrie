package com.athaydes.pathtrie;

import java.util.Optional;

class EmptyTrie implements PathTrieReader<Object> {

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
    public Optional<PathTrieReader<Object>> getChild(String path) {
        return Optional.empty();
    }

}

public interface PathTrieReader<E> {

    static <T> PathTrieReader<T> empty() {
        //noinspection unchecked
        return (PathTrieReader<T>) EmptyTrie.INSTANCE;
    }

    static <T> PathTrieBuilder<T> newBuilder() {
        return new PathTrieBuilder<>();
    }

    static <T> PathTrieBuilder<T> newBuilder(Splitter splitter) {
        return new PathTrieBuilder<>(splitter);
    }

    Optional<E> get(String path);

    Optional<ParameterizedElement<E>> getParameterized(String path);

    Optional<PathTrieReader<E>> getChild(String path);

}
